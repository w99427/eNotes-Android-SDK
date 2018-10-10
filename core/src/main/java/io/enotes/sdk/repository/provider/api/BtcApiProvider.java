package io.enotes.sdk.repository.provider.api;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.enotes.sdk.constant.Constant;
import io.enotes.sdk.constant.ErrorCode;
import io.enotes.sdk.core.RPCApiManager;
import io.enotes.sdk.repository.api.ApiService;
import io.enotes.sdk.repository.api.entity.EntBalanceEntity;
import io.enotes.sdk.repository.api.entity.EntConfirmedEntity;
import io.enotes.sdk.repository.api.entity.EntFeesEntity;
import io.enotes.sdk.repository.api.entity.EntSendTxEntity;
import io.enotes.sdk.repository.api.entity.EntUtxoEntity;
import io.enotes.sdk.repository.api.entity.request.EntBalanceListRequest;
import io.enotes.sdk.repository.api.entity.request.EntConfirmedListRequest;
import io.enotes.sdk.repository.api.entity.request.EntSendTxListRequest;
import io.enotes.sdk.repository.api.entity.request.btc.blockcypher.BtcRequestSendRawTransaction;
import io.enotes.sdk.repository.api.entity.response.btc.blockchain.BtcUtxoForBlockChain;
import io.enotes.sdk.repository.api.entity.response.btc.blockcypher.BtcUtxoForBlockCypher;
import io.enotes.sdk.repository.api.entity.response.btc.blockexplorer.BtcUtxoForBlockExplorer;
import io.enotes.sdk.repository.base.Resource;

import static io.enotes.sdk.repository.provider.ApiProvider.C_BLOCKCHAIN_BITCOIN;

/**
 * BtcApiProvider
 * <p>
 * (blockcypher=Random(5 apikeys))
 * (1)Common Api: Random(blockchain,blockcypher,blockexplorer)->Random(第一次随机剩余的两个)->eNotes server
 * (2)SendRawTransaction: Random(blockcypher,blockexplorer)->第一次随机剩余的一个->eNotes server
 * (3)Fee Api: bitcoinfees->blockexplorer->eNotes server
 */
public class BtcApiProvider extends BaseApiProvider {
    private ApiService apiService;
    private ApiService transactionThirdService;
    private static Map<Integer, String> firstNetWork = new HashMap<>();
    private static Map<Integer, String> secondNetWork = new HashMap<>();
    public static Map<Integer, String> eNotesNetWork = new HashMap<>();

    static {
        firstNetWork.put(Constant.Network.BTC_MAINNET, "main");
        firstNetWork.put(Constant.Network.BTC_TESTNET, "test3");

        secondNetWork.put(Constant.Network.BTC_MAINNET, "");
        secondNetWork.put(Constant.Network.BTC_TESTNET, "testnet.");

        eNotesNetWork.put(Constant.Network.BTC_MAINNET, "mainnet");
        eNotesNetWork.put(Constant.Network.BTC_TESTNET, "testnet");
    }

    public BtcApiProvider(ApiService apiService, ApiService transactionThirdService) {
        super();
        this.apiService = apiService;
        this.transactionThirdService = transactionThirdService;
    }

    /**
     * getBitBalance
     *
     * @param network
     * @param address
     * @return
     */
    public LiveData<Resource<EntBalanceEntity>> getBtcBalance(int network, String address) {
        List<EntBalanceListRequest> listRequests = new ArrayList<>();
        EntBalanceListRequest request = new EntBalanceListRequest();
        listRequests.add(request);
        request.setBlockchain(C_BLOCKCHAIN_BITCOIN);
        request.setNetwork(eNotesNetWork.get(network));
        request.setAddress(address);
        return addLiveDataSourceRandom(getBtcBalanceBy1st(network, address), getBtcBalanceBy2nd(network, address), getBtcBalanceBy3rd(network, address), addSourceForEsList(apiService.getBalanceListByES(listRequests), Constant.BlockChain.BITCOIN));
    }

    /**
     * get bitcoin unSpend list
     *
     * @param network
     * @param address
     * @return
     */
    public LiveData<Resource<List<EntUtxoEntity>>> getBtcUnSpend(int network, String address) {
        return addLiveDataSourceRandom(getUtxoListBy1st(network, address), getUtxoListBy2nd(network, address), getUtxoListBy3rd(network, address), getUtxoListByES(network, address));
    }

    /**
     * bitcoin transaction confirmed
     *
     * @param network
     * @param txId
     * @return
     */
    public LiveData<Resource<EntConfirmedEntity>> isConfirmedTxForBitCoin(int network, String txId) {
        List<EntConfirmedListRequest> listRequests = new ArrayList<>();
        EntConfirmedListRequest request = new EntConfirmedListRequest();
        listRequests.add(request);
        request.setBlockchain(C_BLOCKCHAIN_BITCOIN);
        request.setNetwork(eNotesNetWork.get(network));
        request.setTxid(txId);
        return addLiveDataSourceRandom(isConfirmedTxForBitCoinBy1st(network, txId), isConfirmedTxForBitCoinBy2nd(network, txId), addSourceForEsList(apiService.getConfirmedListByES(listRequests), Constant.BlockChain.BITCOIN));
    }


    /**
     * getBitFees
     * because of enotes server result is not completion,so need to request third
     *
     * @param network
     * @return
     */
    public LiveData<Resource<EntFeesEntity>> getBtcFees(int network) {
        if (network == Constant.Network.BTC_TESTNET)
            return addLiveDataSource(getBtcFeesBy1Xst(network), getBtcFeesBy2nd(network), addSourceForES(apiService.getFeeByES(eNotesNetWork.get(network))));
        else
            return addLiveDataSource(getBtcFeesBy1Xst(network), getBtcFeesBy1st(), getBtcFeesBy2nd(network), addSourceForES(apiService.getFeeByES(eNotesNetWork.get(network))));

    }

    /**
     * sendBitTx
     *
     * @param network
     * @param hex
     * @return
     */
    public LiveData<Resource<EntSendTxEntity>> sendBtcTx(int network, String hex) {
        List<EntSendTxListRequest> listRequests = new ArrayList<>();
        EntSendTxListRequest request = new EntSendTxListRequest();
        listRequests.add(request);
        request.setRawtx(hex);
        request.setBlockchain(C_BLOCKCHAIN_BITCOIN);
        request.setNetwork(eNotesNetWork.get(network));
        return addLiveDataSourceRandom(sendBtcTxBy1st(network, hex), sendBtcTxBy2nd(network, hex), addSourceForEsList(apiService.sendRawTransactionByES(listRequests), Constant.BlockChain.BITCOIN));
    }


    /**
     * getBitBalance By first network
     *
     * @param network
     * @param address
     * @return
     */
    private LiveData<Resource<EntBalanceEntity>> getBtcBalanceBy1st(int network, String address) {
        MediatorLiveData<Resource<EntBalanceEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getBalanceForBtcByBlockChain(secondNetWork.get(network), address), (api -> {
            if (api.isSuccessful()) {
                EntBalanceEntity entBalanceEntity = api.body.parseToENotesEntity();
                entBalanceEntity.setAddress(address);
                entBalanceEntity.setCoinType(Constant.BlockChain.BITCOIN);
                mediatorLiveData.postValue(Resource.success(entBalanceEntity));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * getBitBalance By second network
     *
     * @param network
     * @param address
     * @return
     */
    private LiveData<Resource<EntBalanceEntity>> getBtcBalanceBy2nd(int network, String address) {
        MediatorLiveData<Resource<EntBalanceEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getBalanceForBtcByBlockCypher(firstNetWork.get(network), address, get1stRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                EntBalanceEntity entBalanceEntity = api.body.parseToENotesEntity();
                entBalanceEntity.setAddress(address);
                entBalanceEntity.setCoinType(Constant.BlockChain.BITCOIN);
                mediatorLiveData.postValue(Resource.success(entBalanceEntity));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * getBitBalance By second network
     *
     * @param network
     * @param address
     * @return
     */
    private LiveData<Resource<EntBalanceEntity>> getBtcBalanceBy3rd(int network, String address) {
        MediatorLiveData<Resource<EntBalanceEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getBalanceForBtcByBlockExplorer(secondNetWork.get(network), address), (api -> {
            if (api.isSuccessful()) {
                EntBalanceEntity entBalanceEntity = api.body.parseToENotesEntity();
                entBalanceEntity.setAddress(address);
                entBalanceEntity.setCoinType(Constant.BlockChain.BITCOIN);
                mediatorLiveData.postValue(Resource.success(entBalanceEntity));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * getBitFees by first X network
     *
     * @param network
     * @return
     */
    private LiveData<Resource<EntFeesEntity>> getBtcFeesBy1Xst(int network) {
        MediatorLiveData<Resource<EntFeesEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getFeesForBtcByBlockCypher(firstNetWork.get(network)), (api -> {
            if (api.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * getBitFees by first network
     *
     * @return
     */
    private LiveData<Resource<EntFeesEntity>> getBtcFeesBy1st() {
        MediatorLiveData<Resource<EntFeesEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getFeesForBtcByBitcoinFees(), (api -> {
            if (api.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * getBitFees by second network
     *
     * @param network
     * @return
     */
    private LiveData<Resource<EntFeesEntity>> getBtcFeesBy2nd(int network) {
        MediatorLiveData<Resource<EntFeesEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getFeesForBtcByBlockExplorer(secondNetWork.get(network)), (api -> {
            if (api.isSuccessful() && api.body != null) {
                try {
                    if (api.body.size() > 0) {
                        Map.Entry<String, String> next = api.body.entrySet().iterator().next();
                        String fee = api.body.get(next.getKey());
                        EntFeesEntity entFeeEntity = new EntFeesEntity();
                        entFeeEntity.setFast(new BigDecimal(fee).multiply(new BigDecimal("100000000")).intValue() + "");
                        mediatorLiveData.postValue(Resource.success(entFeeEntity));
                    } else {
                        mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
     *
     * @param network
     * @param txId
     * @return
     */
    private LiveData<Resource<EntConfirmedEntity>> isConfirmedTxForBitCoinBy1st(int network, String txId) {
        MediatorLiveData<Resource<EntConfirmedEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.isConfirmedTxForBitCoinByBlockCypher(firstNetWork.get(network), txId, get1stRandomApiKey()), (api -> {
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
     *
     * @param network
     * @param txId
     * @return
     */
    private LiveData<Resource<EntConfirmedEntity>> isConfirmedTxForBitCoinBy2nd(int network, String txId) {
        MediatorLiveData<Resource<EntConfirmedEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.isConfirmedTxForBitCoinByBlockExplorer(secondNetWork.get(network), txId), (api -> {
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
     *
     * @param network
     * @param hex
     * @return
     */
    private LiveData<Resource<EntSendTxEntity>> sendBtcTxBy1st(int network, String hex) {
        MediatorLiveData<Resource<EntSendTxEntity>> mediatorLiveData = new MediatorLiveData<>();
        BtcRequestSendRawTransaction requestSendRawTransaction = new BtcRequestSendRawTransaction();
        requestSendRawTransaction.setTx(hex);
        mediatorLiveData.addSource(transactionThirdService.sendRawTransactionForBitCoinByBlockCypher(firstNetWork.get(network), requestSendRawTransaction, get1stRandomApiKey()), (api -> {
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
     *
     * @param network
     * @param hex
     * @return
     */
    private LiveData<Resource<EntSendTxEntity>> sendBtcTxBy2nd(int network, String hex) {
        MediatorLiveData<Resource<EntSendTxEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.sendRawTransactionForBitCoinByBlockExplorer(secondNetWork.get(network), hex), (api -> {
            if (api.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * getUtxoList by first network
     *
     * @param network
     * @param address
     * @return
     */
    private LiveData<Resource<List<EntUtxoEntity>>> getUtxoListBy1st(int network, String address) {
        MediatorLiveData<Resource<List<EntUtxoEntity>>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getUTXOForBitCoinByBlockChain(secondNetWork.get(network), address), (api -> {
            if (api.isSuccessful()) {
                List<EntUtxoEntity> list = new ArrayList<>();
                if (api.body.getUnspent_outputs() != null) {
                    for (BtcUtxoForBlockChain.UnspentOutputsBean txsBean : api.body.getUnspent_outputs()) {
                        list.add(txsBean.parseToENotesEntity());
                    }
                }
                mediatorLiveData.postValue(Resource.success(list));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * getUtxoList by second network
     *
     * @param network
     * @param address
     * @return
     */
    private LiveData<Resource<List<EntUtxoEntity>>> getUtxoListBy2nd(int network, String address) {
        MediatorLiveData<Resource<List<EntUtxoEntity>>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getUTXOForBitCoinByBlockCypher(firstNetWork.get(network), address, get1stRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                List<EntUtxoEntity> list = new ArrayList<>();
                if (api.body.getTxrefs() != null) {
                    for (BtcUtxoForBlockCypher.BtcUtxo txsBean : api.body.getTxrefs()) {
                        list.add(txsBean.parseToENotesEntity());
                    }
                }
                if (api.body.getUnconfirmed_txrefs() != null) {
                    for (BtcUtxoForBlockCypher.BtcUtxo txsBean : api.body.getUnconfirmed_txrefs()) {
                        list.add(txsBean.parseToENotesEntity());
                    }
                }
                mediatorLiveData.postValue(Resource.success(list));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * getUtxoList by third network
     *
     * @param network
     * @param address
     * @return
     */
    private LiveData<Resource<List<EntUtxoEntity>>> getUtxoListBy3rd(int network, String address) {
        MediatorLiveData<Resource<List<EntUtxoEntity>>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getUTXOForBitCoinByBlockExplorer(secondNetWork.get(network), address), (api -> {
            if (api.isSuccessful()) {
                List<EntUtxoEntity> list = new ArrayList<>();
                if (api.body != null) {
                    for (BtcUtxoForBlockExplorer txsBean : api.body) {
                        list.add(txsBean.parseToENotesEntity());
                    }
                }
                mediatorLiveData.postValue(Resource.success(list));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * getUtxoList by eNotes network
     *
     * @param network
     * @param address
     * @return
     */
    private LiveData<Resource<List<EntUtxoEntity>>> getUtxoListByES(int network, String address) {
        MediatorLiveData<Resource<List<EntUtxoEntity>>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(apiService.getUtxoListByES(eNotesNetWork.get(network), address), (api -> {
            if (api.isSuccessful()) {
                if (api.body.getCode() == 0 && api.body != null && api.body.getData() != null) {
                    List<EntUtxoEntity> entUtxoEntityList = api.body.getData();
                    //remove invalid utxo,remove utxo which positive utxo (txid and index) and negative utxo (pretxid nad index) matches is true
                    if (entUtxoEntityList.size() > 0) {
                        List<EntUtxoEntity> positiveList = new ArrayList<>();
                        List<EntUtxoEntity> negativeList = new ArrayList<>();
                        List<EntUtxoEntity> matchList = new ArrayList<>();
                        for (EntUtxoEntity entity : api.body.getData()) {
                            if (entity.isPositive()) {
                                positiveList.add(entity);
                            } else {
                                negativeList.add(entity);
                            }
                        }
                        for (EntUtxoEntity n : negativeList) {
                            for (EntUtxoEntity p : positiveList) {
                                if (n.getPrevtxid() != null && p.getTxid() != null) {
                                    if (n.getPrevtxid().equals(p.getTxid()) && n.getIndex().equals(p.getIndex())) {
                                        matchList.add(p);
                                    }
                                }
                            }
                        }
                        if (matchList.size() > 0) {
                            positiveList.removeAll(matchList);
                        }
                        mediatorLiveData.postValue(Resource.success(positiveList));
                    }

                } else
                    mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.body.getMessage()));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    private String get1stRandomApiKey() {
        String[] apiKeys = {"e967dba1620441c8ab57d48e88150d87", "ce05502a8ab447db8f3e7dbf10e830cd", "db5dad2ee7d5496184d78a2a0012246a", "21a6d79adca247808a06b6f899a99577", "ab673cc2aeae4a1b81bc6fa38363b2b6"};
        if (RPCApiManager.networkConfig != null && RPCApiManager.networkConfig.blockchypherKeys != null && RPCApiManager.networkConfig.blockchypherKeys.length > 1) {
            String[] keys = RPCApiManager.networkConfig.blockchypherKeys;
            return keys[new Random().nextInt(100) % keys.length];
        } else {
            return apiKeys[new Random().nextInt(100) % 5];
        }
    }
}
