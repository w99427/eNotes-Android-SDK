package io.enotes.sdk.repository.provider.api;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.enotes.sdk.constant.Constant;
import io.enotes.sdk.constant.ErrorCode;
import io.enotes.sdk.repository.api.ApiService;
import io.enotes.sdk.repository.api.entity.EntBalanceEntity;
import io.enotes.sdk.repository.api.entity.EntConfirmedEntity;
import io.enotes.sdk.repository.api.entity.EntFeesEntity;
import io.enotes.sdk.repository.api.entity.EntSendTxEntity;
import io.enotes.sdk.repository.api.entity.EntTransactionEntity;
import io.enotes.sdk.repository.api.entity.EntUtxoEntity;
import io.enotes.sdk.repository.api.entity.request.xrp.XRPBalanceParams;
import io.enotes.sdk.repository.api.entity.request.xrp.XRPRequest;
import io.enotes.sdk.repository.api.entity.request.xrp.XRPSendRawTxParams;
import io.enotes.sdk.repository.api.entity.request.xrp.XRPTransactionListParams;
import io.enotes.sdk.repository.api.entity.request.xrp.XRPTxParams;
import io.enotes.sdk.repository.api.entity.response.bch.blockdozer.BchTransactionListForBlockdozer;
import io.enotes.sdk.repository.api.entity.response.bch.blockdozer.BchUtxoForBlockdozer;
import io.enotes.sdk.repository.api.entity.response.xrp.XRPBalance;
import io.enotes.sdk.repository.api.entity.response.xrp.XRPFee;
import io.enotes.sdk.repository.api.entity.response.xrp.XRPTransactionList;
import io.enotes.sdk.repository.base.Resource;
import io.enotes.sdk.utils.Utils;

/**
 * BchApiProvider
 */
public class XrpApiProvider extends BaseApiProvider {
    private ApiService transactionThirdService;
    private static Map<Integer, String> firstNetWork = new HashMap<>();
    private static Map<Integer, String> secondNetWork = new HashMap<>();

    static {
        firstNetWork.put(Constant.Network.BTC_MAINNET, "s2.ripple.com");
        firstNetWork.put(Constant.Network.BTC_TESTNET, "s.altnet.rippletest.net");

        secondNetWork.put(Constant.Network.BTC_MAINNET, "");
        secondNetWork.put(Constant.Network.BTC_TESTNET, "test-");

    }

    public XrpApiProvider(Context context, ApiService transactionThirdService) {
        super(context);
        this.transactionThirdService = transactionThirdService;
    }


    /**
     * getBitBalance
     */
    public LiveData<Resource<EntBalanceEntity>> getXrpBalance(int network, String address) {
        return addLiveDataSourceNoENotes(getXrpBalanceBy1st(network, address));
    }


    /**
     * bitcoin transaction confirmed
     */
    public LiveData<Resource<EntConfirmedEntity>> isConfirmedTxForXrp(int network, String txId) {
        return addLiveDataSourceNoENotes(isConfirmedTxForXrpBy1st(network, txId));
    }


    /**
     * getBitFees
     * because of enotes server result is not completion,so need to request third
     */
    public LiveData<Resource<EntFeesEntity>> getXrpFees(int network) {
        return addLiveDataSourceNoENotes(getXrpFeesBy1st(network));
    }

    /**
     * sendBitTx
     */
    public LiveData<Resource<EntSendTxEntity>> sendXrpTx(int network, String hex) {
        return addLiveDataSourceNoENotes(sendXrpTxBy1st(network, hex), sendBtcTxBy2nd(network, hex));
    }


    public LiveData<Resource<List<EntTransactionEntity>>> getTransactionList(int network, String address) {
        return addLiveDataSourceNoENotes(getTransactionList1st(network, address), getTransactionList2nd(network, address));
    }

    private LiveData<Resource<List<EntTransactionEntity>>> getTransactionList1st(int network, String address) {
        XRPTransactionListParams params = new XRPTransactionListParams();
        params.setAccount(address);
        params.setBinary(false);
        params.setForward(false);
        params.setLedger_index_max(-1);
        params.setLedger_index_min(-1);
        params.setLimit(50);
        XRPRequest<XRPTransactionListParams> request = XRPRequest.getXRPRequest("account_tx", params);

        MediatorLiveData<Resource<List<EntTransactionEntity>>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getTransactionListForRipple(firstNetWork.get(network), request), (resource -> {
            if (resource.isSuccessful()) {
                List<EntTransactionEntity> list = new ArrayList<>();
                List<XRPTransactionList.Transaction> txs = resource.body.getResult().getTransactions();
                if (txs != null) {
                    for (XRPTransactionList.Transaction tx : txs) {
                        EntTransactionEntity entTransactionEntity = new EntTransactionEntity();
                        entTransactionEntity.setConfirmations(tx.isValidated()?6:0);
                        entTransactionEntity.setTime(tx.getTx().getDate() + "");
                        entTransactionEntity.setTxId(tx.getTx().getHash());
                        entTransactionEntity.setSent(tx.getTx().getAccount().equals(address));
                        entTransactionEntity.setAmount(tx.getTx().getAmount());
                        list.add(entTransactionEntity);
                    }
                }
                mediatorLiveData.postValue(Resource.success(list));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, resource.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    private LiveData<Resource<List<EntTransactionEntity>>> getTransactionList2nd(int network, String address) {
        MediatorLiveData<Resource<List<EntTransactionEntity>>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getTransactionListForBchByBitpay(secondNetWork.get(network), address), (resource -> {
            if (resource.isSuccessful()) {
                List<EntTransactionEntity> list = new ArrayList<>();
                List<BchTransactionListForBlockdozer.Tx> txs = resource.body.getTxs();
                if (txs != null) {
                    for (BchTransactionListForBlockdozer.Tx tx : txs) {
                        EntTransactionEntity entTransactionEntity = new EntTransactionEntity();
                        entTransactionEntity.setConfirmations(tx.getConfirmations());
                        entTransactionEntity.setTime(tx.getTime() + "");
                        entTransactionEntity.setTxId(tx.getTxid());
                        for (BchTransactionListForBlockdozer.Input input : tx.getVin()) {
                            if (input.getAddr().equals(address)) {
                                entTransactionEntity.setSent(true);
                                break;
                            }
                        }
                        entTransactionEntity.setAmount(!entTransactionEntity.isSent() ? tx.getValueOut() : tx.getValueIn());
                        entTransactionEntity.setAmount(new BigDecimal(entTransactionEntity.getAmount()).multiply(new BigDecimal("100000000")).toBigInteger().toString());
                        list.add(entTransactionEntity);
                    }
                }
                mediatorLiveData.postValue(Resource.success(list));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, resource.errorMessage));
            }
        }));
        return mediatorLiveData;
    }


    /**
     * getBitBalance By first network
     */
    private LiveData<Resource<EntBalanceEntity>> getXrpBalanceBy1st(int network, String address) {
        XRPBalanceParams xrpBalanceParams = new XRPBalanceParams();
        xrpBalanceParams.setAccount(address);
        xrpBalanceParams.setStrict(true);
        xrpBalanceParams.setLedger_index("current");
        xrpBalanceParams.setQueue(true);
        XRPRequest<XRPBalanceParams> request = XRPRequest.getXRPRequest("account_info", xrpBalanceParams);
        MediatorLiveData<Resource<EntBalanceEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getBalanceForRipple(firstNetWork.get(network), request), (api -> {
            if (api.isSuccessful()) {
                EntBalanceEntity entBalanceEntity = new EntBalanceEntity();
                XRPBalance balance = api.body;
                entBalanceEntity.setAddress(address);
                entBalanceEntity.setCoinType(Constant.BlockChain.RIPPLE);
                if(balance.getResult().getAccount_data() == null){
                    entBalanceEntity.setBalance(Utils.intToHexString("0"));
                    entBalanceEntity.setSequence(1);
                }else{
                    entBalanceEntity.setBalance(Utils.intToHexString(balance.getResult().getAccount_data().getBalance()));
                    entBalanceEntity.setSequence(balance.getResult().getAccount_data().getSequence());
                }
                mediatorLiveData.postValue(Resource.success(entBalanceEntity));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * getBitBalance By second network
     */
    private LiveData<Resource<EntBalanceEntity>> getBtcBalanceBy2nd(int network, String address) {
        MediatorLiveData<Resource<EntBalanceEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getBalanceForBchByBitpay(secondNetWork.get(network), address), (api -> {
            if (api.isSuccessful()) {
                EntBalanceEntity entBalanceEntity = api.body.parseToENotesEntity();
                entBalanceEntity.setAddress(address);
                entBalanceEntity.setCoinType(Constant.BlockChain.BITCOIN_CASH);
                mediatorLiveData.postValue(Resource.success(entBalanceEntity));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * getBitFees by first network
     */
    private LiveData<Resource<EntFeesEntity>> getXrpFeesBy1st(int network) {
        XRPRequest<Object> request = XRPRequest.getXRPRequest("fee", null);
        MediatorLiveData<Resource<EntFeesEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getFeesForRipple(firstNetWork.get(network), request), (api -> {
            if (api.isSuccessful()) {
                XRPFee.Drop drops = api.body.getResult().getDrops();
                EntFeesEntity feesEntity = new EntFeesEntity();
                feesEntity.setLow(drops.getMinimum_fee());
                feesEntity.setFast(drops.getBase_fee());
                feesEntity.setFastest(drops.getMedian_fee());
                mediatorLiveData.postValue(Resource.success(feesEntity));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }


    /**
     * getBitFees by second network
     */
    private LiveData<Resource<EntFeesEntity>> getBtcFeesBy2nd(int network) {
        MediatorLiveData<Resource<EntFeesEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getFeesForBchByBitpay(secondNetWork.get(network)), (api -> {
            if (api.isSuccessful()) {
                if (api.body.size() > 0) {
                    Map.Entry<String, String> next = api.body.entrySet().iterator().next();
                    String fee = api.body.get(next.getKey());
                    EntFeesEntity entFeeEntity = new EntFeesEntity();
                    entFeeEntity.setFast(new BigDecimal(fee).multiply(new BigDecimal("100000000")).intValue() + "");
                    mediatorLiveData.postValue(Resource.success(entFeeEntity));
                } else {
                    mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
                }
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * isConfirmedTxForBitCoin by first network
     */
    private LiveData<Resource<EntConfirmedEntity>> isConfirmedTxForXrpBy1st(int network, String txId) {
        XRPTxParams params = new XRPTxParams();
        params.setTransaction(txId);
        params.setBinary(false);

        XRPRequest<XRPTxParams> request = XRPRequest.getXRPRequest("tx", params);
        MediatorLiveData<Resource<EntConfirmedEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.isConfirmedForRipple(firstNetWork.get(network), request), (api -> {
            if (api.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * isConfirmedTxForBitCoin by second network
     */
    private LiveData<Resource<EntConfirmedEntity>> isConfirmedTxForBitCoinBy2nd(int network, String txId) {
        MediatorLiveData<Resource<EntConfirmedEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.isConfirmedTxForBchByBitpay(secondNetWork.get(network), txId), (api -> {
            if (api.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * sendBitTx by first network
     */
    private LiveData<Resource<EntSendTxEntity>> sendXrpTxBy1st(int network, String hex) {
        XRPSendRawTxParams params = new XRPSendRawTxParams();
        params.setTx_blob(hex);
        XRPRequest<XRPSendRawTxParams> request = XRPRequest.getXRPRequest("submit", params);
        MediatorLiveData<Resource<EntSendTxEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.sendRawTransactionForRipple(firstNetWork.get(network), request), (api -> {
            if (api.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * sendBitTx by second network
     */
    private LiveData<Resource<EntSendTxEntity>> sendBtcTxBy2nd(int network, String hex) {
        MediatorLiveData<Resource<EntSendTxEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.sendRawTransactionForBchByBitpay(secondNetWork.get(network), hex), (api -> {
            if (api.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

}
