package io.enotes.examples;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.enotes.sdk.constant.Status;
import io.enotes.sdk.core.CardManager;
import io.enotes.sdk.core.RPCApiManager;
import io.enotes.sdk.repository.api.entity.EntBalanceEntity;
import io.enotes.sdk.repository.api.entity.EntFeesEntity;
import io.enotes.sdk.repository.api.entity.EntGasEntity;
import io.enotes.sdk.repository.api.entity.EntGasPriceEntity;
import io.enotes.sdk.repository.api.entity.EntNonceEntity;
import io.enotes.sdk.repository.api.entity.EntUtxoEntity;
import io.enotes.sdk.repository.db.entity.Card;
import static org.junit.Assert.*;
public class BaseTest {
    private static final String TAG = "BaseTest";
    protected void getBtcRawTransaction(ThreadLock threadLock, Card card, CardManager cardManager, RPCApiManager rpcApiManager){
        rpcApiManager.getUnSpend(card.getCert().getNetWork(),card.getAddress(),(resource -> {
            assertTrue(resource.status == Status.SUCCESS);
            assertTrue(resource.data instanceof List);
            List<EntUtxoEntity> utxoEntities=(List<EntUtxoEntity>)resource.data;
            assertTrue(utxoEntities.size()>0);
            List<EntUtxoEntity> confirmedList = new ArrayList<>();
            for(EntUtxoEntity entity:utxoEntities){
                if(entity.isComfirmed()){
                    confirmedList.add(entity);
                }
            }
            assertTrue(confirmedList.size()>0);
            rpcApiManager.estimateFee(card.getCert().getNetWork(),(feeResource -> {
                assertTrue(feeResource.status == Status.SUCCESS);
                assertTrue(feeResource.data instanceof EntFeesEntity);
                EntFeesEntity feesEntity=(EntFeesEntity)feeResource.data;
                cardManager.getBtcRawTransaction(card,feesEntity.getFast(),card.getAddress(),confirmedList,(resource1 -> {
                    threadLock.assertResource(resource1);
                    if(resource1.status == Status.SUCCESS){
                        String hex = (String)resource1.data;
                        Log.i(TAG,"btc raw transaction hex = "+hex);
                        threadLock.notifyLock();
                    }
                }));
            }));
        }));
    }

    protected void getEthRawTransaction(ThreadLock threadLock, Card card, CardManager cardManager, RPCApiManager rpcApiManager){
        rpcApiManager.getBalance(card.getCert().getBlockChain(),card.getCert().getNetWork(),card.getAddress(),(resource -> {
            assertTrue(resource.status == Status.SUCCESS);
            assertTrue(resource.data instanceof EntBalanceEntity);
            EntBalanceEntity balanceEntity = (EntBalanceEntity)resource.data;
            rpcApiManager.getNonce(card.getCert().getNetWork(),card.getAddress(),(nonceResource->{
                assertTrue(nonceResource.status == Status.SUCCESS);
                assertTrue(nonceResource.data instanceof EntNonceEntity);
                EntNonceEntity nonceEntity=(EntNonceEntity)nonceResource.data;
                rpcApiManager.getGasPrice(card.getCert().getNetWork(),(gasPriceResource->{
                    assertTrue(gasPriceResource.status == Status.SUCCESS);
                    assertTrue(gasPriceResource.data instanceof EntGasPriceEntity);
                    EntGasPriceEntity gasPriceEntity=(EntGasPriceEntity)gasPriceResource.data;
                    rpcApiManager.estimateGas(card.getCert().getNetWork(),card.getAddress(),card.getAddress(),balanceEntity.getBalance(),gasPriceEntity.getFast(),null,(gasResource->{
                        assertTrue(gasResource.status == Status.SUCCESS);
                        assertTrue(gasResource.data instanceof EntGasEntity);
                        EntGasEntity gasEntity=(EntGasEntity)gasResource.data;
                        cardManager.getEthRawTransaction(card,nonceEntity.getNonce(),gasEntity.getGas(),gasPriceEntity.getFast(),card.getAddress(),balanceEntity.getBalance(),null,(txResource->{
                            threadLock.assertResource(txResource);
                            if(txResource.status == Status.SUCCESS){
                                String hex = (String)txResource.data;
                                Log.i(TAG,"eth raw transaction hex = "+hex);
                                threadLock.notifyLock();
                            }
                        }));
                    }));
                }));
            }));
        }));
    }
}
