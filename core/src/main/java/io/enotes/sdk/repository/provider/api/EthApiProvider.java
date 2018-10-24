package io.enotes.sdk.repository.provider.api;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.text.TextUtils;

import java.math.BigInteger;
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
import io.enotes.sdk.repository.api.entity.EntCallEntity;
import io.enotes.sdk.repository.api.entity.EntConfirmedEntity;
import io.enotes.sdk.repository.api.entity.EntGasEntity;
import io.enotes.sdk.repository.api.entity.EntGasPriceEntity;
import io.enotes.sdk.repository.api.entity.EntNonceEntity;
import io.enotes.sdk.repository.api.entity.EntSendTxEntity;
import io.enotes.sdk.repository.api.entity.request.EntBalanceListRequest;
import io.enotes.sdk.repository.api.entity.request.EntConfirmedListRequest;
import io.enotes.sdk.repository.api.entity.request.EntSendTxListRequest;
import io.enotes.sdk.repository.api.entity.request.eth.infura.EthRequestForInfura;
import io.enotes.sdk.repository.base.Resource;

import static io.enotes.sdk.repository.provider.ApiProvider.C_BLOCKCHAIN_ETHER;

/**
 * EthApiProvider
 */
public class EthApiProvider extends BaseApiProvider {
    private ApiService apiService;
    private ApiService transactionThirdService;
    private static Map<Integer, String> firstNetWork = new HashMap<>();
    private static Map<Integer, String> secondNetWork = new HashMap<>();
    public static Map<Integer, String> eNotesNetWork = new HashMap<>();

    static {
        firstNetWork.put(Constant.Network.ETH_MAINNET, "mainnet");
        firstNetWork.put(Constant.Network.ETH_ROPSTEN, "ropsten");
        firstNetWork.put(Constant.Network.ETH_RINKEBY, "rinkeby");
        firstNetWork.put(Constant.Network.ETH_KOVAN, "kovan");

        secondNetWork.put(Constant.Network.ETH_MAINNET, "api");
        secondNetWork.put(Constant.Network.ETH_ROPSTEN, "api-ropsten");
        secondNetWork.put(Constant.Network.ETH_RINKEBY, "api-rinkeby");
        secondNetWork.put(Constant.Network.ETH_KOVAN, "kovan");

        eNotesNetWork.put(Constant.Network.ETH_MAINNET, "mainnet");
        eNotesNetWork.put(Constant.Network.ETH_ROPSTEN, "ropsten");
        eNotesNetWork.put(Constant.Network.ETH_RINKEBY, "rinkeby");
        eNotesNetWork.put(Constant.Network.ETH_KOVAN, "kovan");
    }

    public EthApiProvider(ApiService apiService, ApiService transactionThirdService) {
        super();
        this.apiService = apiService;
        this.transactionThirdService = transactionThirdService;
    }

    /**
     * getEthBalance
     *
     * @param network
     * @param address
     * @return
     */
    public LiveData<Resource<EntBalanceEntity>> getEthBalance(int network, String address) {
        List<EntBalanceListRequest> listRequests = new ArrayList<>();
        EntBalanceListRequest request = new EntBalanceListRequest();
        listRequests.add(request);
        request.setBlockchain(C_BLOCKCHAIN_ETHER);
        request.setNetwork(eNotesNetWork.get(network));
        request.setAddress(address);
        return addLiveDataSource(getEthBalanceBy1st(network, address), getEthBalanceBy2nd(network, address), addSourceForEsList(apiService.getBalanceListByES(listRequests), Constant.BlockChain.ETHEREUM));
    }

    /**
     * isConfirmedTxForEth
     *
     * @param network
     * @param txId
     * @return
     */
    public LiveData<Resource<EntConfirmedEntity>> isConfirmedTxForEth(int network, String txId) {
        List<EntConfirmedListRequest> listRequests = new ArrayList<>();
        EntConfirmedListRequest request = new EntConfirmedListRequest();
        listRequests.add(request);
        request.setBlockchain(C_BLOCKCHAIN_ETHER);
        request.setNetwork(eNotesNetWork.get(network));
        request.setTxid(txId);
        return addLiveDataSource(isConfirmedTxForEthBy1st(network, txId), isConfirmedTxForEthBy2nd(network, txId), addSourceForEsList(apiService.getConfirmedListByES(listRequests), Constant.BlockChain.ETHEREUM));
    }

    /**
     * get eth GasPrice
     * need select 2nd network, because of more recommend gasPrice
     *
     * @param network
     * @return
     */
    public LiveData<Resource<EntGasPriceEntity>> getEthGasPrice(int network) {
        return addLiveDataSource(getGasPriceForEthByThird(), getEthGasPriceBy1st(network), getEthGasPriceBy2nd(network), addSourceForES(apiService.getGasPriceByES(eNotesNetWork.get(network))));
    }

    /**
     * get eth nonce
     *
     * @param network
     * @param address
     * @return
     */
    public LiveData<Resource<EntNonceEntity>> getEthNonce(int network, String address) {
        return addLiveDataSource(getEthNonceBy1st(network, address), getEthNonceBy2nd(network, address), addSourceForES(apiService.getNonceByES(eNotesNetWork.get(network), address)));
    }

    /**
     * get eth GasLimit
     *
     * @param network
     * @param toAddress
     * @param value
     * @param gasPrice
     * @return
     */
    public LiveData<Resource<EntGasEntity>> estimateGas(int network, String from, String toAddress, String value, String gasPrice, String data) {
        if (!TextUtils.isEmpty(data)) value = "0";
        return addLiveDataSource(estimateGasBy1st(network, from, toAddress, value, gasPrice, data), estimateGasBy2nd(network, from, toAddress, value, gasPrice, data), addSourceForES(apiService.getEstimateGasByES(eNotesNetWork.get(network), toAddress, from, "0x" + new BigInteger(value).toString(16), data)));
    }

    /**
     * sendEthTx
     *
     * @param hex
     * @return
     */
    public LiveData<Resource<EntSendTxEntity>> sendEthTx(int network, String hex) {
        List<EntSendTxListRequest> listRequests = new ArrayList<>();
        EntSendTxListRequest request = new EntSendTxListRequest();
        listRequests.add(request);
        request.setRawtx("0x" + hex);
        request.setBlockchain(C_BLOCKCHAIN_ETHER);
        request.setNetwork(eNotesNetWork.get(network));
        return addLiveDataSource(sendEthTxBy1st(network, hex), sendEthTxBy2nd(network, hex), addSourceForEsList(apiService.sendRawTransactionByES(listRequests), Constant.BlockChain.ETHEREUM));
    }

    /**
     * call abi
     *
     * @param toAddress
     * @param datta
     * @param testCard  test card need call kovan network,release card call mainnet
     * @return
     */
    public LiveData<Resource<EntCallEntity>> callEth(String toAddress, String datta, boolean testCard) {
        return addLiveDataSource(callEthBy1st(firstNetWork.get(testCard ? Constant.Network.ETH_KOVAN : Constant.Network.ETH_MAINNET), toAddress, datta), callEthBy2nd(secondNetWork.get(testCard ? Constant.Network.ETH_KOVAN : Constant.Network.ETH_MAINNET), toAddress, datta), addSourceForES(apiService.callByES(eNotesNetWork.get(testCard ? Constant.Network.ETH_KOVAN : Constant.Network.ETH_MAINNET), toAddress, datta)));
    }

    /**
     * call abi
     *
     * @param network
     * @param toAddress
     * @param datta
     * @return
     */
    public LiveData<Resource<EntCallEntity>> callEth(int network, String toAddress, String datta) {
        return addLiveDataSource(callEthBy1st(firstNetWork.get(network), toAddress, datta), callEthBy2nd(secondNetWork.get(network), toAddress, datta), addSourceForES(apiService.callByES(eNotesNetWork.get(network), toAddress, datta)));
    }

    /**
     * getEthBalance by first network
     *
     * @param network
     * @param address
     * @return
     */
    private LiveData<Resource<EntBalanceEntity>> getEthBalanceBy1st(int network, String address) {
        MediatorLiveData<Resource<EntBalanceEntity>> mediatorLiveData = new MediatorLiveData<>();
        EthRequestForInfura request = new EthRequestForInfura();
        request.parseParams(address, "latest");
        request.setMethod(EthRequestForInfura.GET_BALANCE);
        mediatorLiveData.addSource(transactionThirdService.getBalanceForEthByInfura(firstNetWork.get(network), request, get1stRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                if (!checkEthError(mediatorLiveData, api.body, true)) {
                    EntBalanceEntity entBalanceEntity = api.body.parseToENotesEntity();
                    entBalanceEntity.setAddress(address);
                    entBalanceEntity.setCoinType(Constant.BlockChain.ETHEREUM);
                    mediatorLiveData.postValue(Resource.success(entBalanceEntity));
                }
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));

        return mediatorLiveData;
    }

    /**
     * getEthBalance by second network
     *
     * @param network
     * @param address
     * @return
     */
    private LiveData<Resource<EntBalanceEntity>> getEthBalanceBy2nd(int network, String address) {
        MediatorLiveData<Resource<EntBalanceEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getBalanceForEthByEtherScan(secondNetWork.get(network), address, get2ndRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                EntBalanceEntity entBalanceEntity = api.body.parseToENotesEntity();
                entBalanceEntity.setAddress(address);
                entBalanceEntity.setCoinType(Constant.BlockChain.ETHEREUM);
                mediatorLiveData.postValue(Resource.success(entBalanceEntity));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * getEthGasPrice by first network
     *
     * @param network
     * @return
     */
    private LiveData<Resource<EntGasPriceEntity>> getEthGasPriceBy1st(int network) {
        MediatorLiveData<Resource<EntGasPriceEntity>> mediatorLiveData = new MediatorLiveData<>();
        EthRequestForInfura request = new EthRequestForInfura();
        request.setMethod(EthRequestForInfura.GAS_PRICE);
        mediatorLiveData.addSource(transactionThirdService.getGasPriceForEthByInfura(firstNetWork.get(network), request, get1stRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                if (!checkEthError(mediatorLiveData, api.body, true)) {
                    mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
                }
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));

        return mediatorLiveData;
    }

    /**
     * getEthGasPrice by second network
     *
     * @param network
     * @return
     */
    private LiveData<Resource<EntGasPriceEntity>> getEthGasPriceBy2nd(int network) {
        MediatorLiveData<Resource<EntGasPriceEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getGasPriceForEthByEtherScan(secondNetWork.get(network), get2ndRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                if (!checkEthError(mediatorLiveData, api.body, true)) {
                    mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
                }
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));

        return mediatorLiveData;
    }

    /**
     * getEthGasPrice by third network
     *
     * @return
     */
    private LiveData<Resource<EntGasPriceEntity>> getGasPriceForEthByThird() {
        MediatorLiveData<Resource<EntGasPriceEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getGasPriceForEthByEtherChain(), (api -> {
            if (api.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * getEthNonce by first network
     *
     * @param network
     * @param address
     * @return
     */
    private LiveData<Resource<EntNonceEntity>> getEthNonceBy1st(int network, String address) {
        MediatorLiveData<Resource<EntNonceEntity>> mediatorLiveData = new MediatorLiveData<>();
        EthRequestForInfura request = new EthRequestForInfura();
        request.setMethod(EthRequestForInfura.GET_NONCE);
        request.parseParams(address, "latest");
        mediatorLiveData.addSource(transactionThirdService.getNonceForEthByInfura(firstNetWork.get(network), request, get1stRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                if (!checkEthError(mediatorLiveData, api.body, true)) {
                    mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
                }
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));

        return mediatorLiveData;
    }

    /**
     * getEthNonce by second network
     *
     * @param network
     * @param address
     * @return
     */
    private LiveData<Resource<EntNonceEntity>> getEthNonceBy2nd(int network, String address) {
        MediatorLiveData<Resource<EntNonceEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getNonceForEthByEtherScan(secondNetWork.get(network), address, get2ndRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * estimateGas by first network
     *
     * @param network
     * @param toAddress
     * @param value
     * @param gasPrice
     * @return
     */
    private LiveData<Resource<EntGasEntity>> estimateGasBy1st(int network, String from, String toAddress, String value, String gasPrice, String data) {
        MediatorLiveData<Resource<EntGasEntity>> mediatorLiveData = new MediatorLiveData<>();
        EthRequestForInfura request = new EthRequestForInfura();
        request.setMethod(EthRequestForInfura.ESTIMATE_GAS);
        Map<String, String> paramsMap = new HashMap<>();
        if (!toAddress.startsWith("0x")) toAddress = "0x" + toAddress;
        paramsMap.put("from", from);
        paramsMap.put("to", toAddress);
        paramsMap.put("gasPrice", "0x" + new BigInteger(gasPrice).toString(16));
        paramsMap.put("value", "0x" + new BigInteger(value).toString(16));
        if (!TextUtils.isEmpty(data))
            paramsMap.put("data", "0x" + data);
        request.parseParams(paramsMap);
        mediatorLiveData.addSource(transactionThirdService.estimateGasForEthByInfura(firstNetWork.get(network), request, get1stRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                if (!checkEthError(mediatorLiveData, api.body, true)) {
                    mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
                }
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));

        return mediatorLiveData;
    }

    /**
     * estimateGas by second network
     *
     * @param network
     * @param toAddress
     * @param value
     * @param gasPrice
     * @return
     */
    private LiveData<Resource<EntGasEntity>> estimateGasBy2nd(int network, String from, String toAddress, String value, String gasPrice, String data) {
        MediatorLiveData<Resource<EntGasEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.getGasLimitForEthByEtherScan(secondNetWork.get(network), toAddress, from, value, gasPrice, data, get2ndRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * sendEthTx by first network
     *
     * @param network
     * @param hex
     * @return
     */
    private LiveData<Resource<EntSendTxEntity>> sendEthTxBy1st(int network, String hex) {
        MediatorLiveData<Resource<EntSendTxEntity>> mediatorLiveData = new MediatorLiveData<>();
        EthRequestForInfura request = new EthRequestForInfura();
        request.setMethod(EthRequestForInfura.SEND_RAW_TRANSACTION);
        request.parseParams("0x" + hex);
        mediatorLiveData.addSource(transactionThirdService.sendRawTransactionForEthByInfura(firstNetWork.get(network), request, get1stRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                if (!checkEthError(mediatorLiveData, api.body, true)) {
                    mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
                }
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }


    /**
     * sendEthTx by second network
     *
     * @param network
     * @param hex
     * @return
     */
    private LiveData<Resource<EntSendTxEntity>> sendEthTxBy2nd(int network, String hex) {
        MediatorLiveData<Resource<EntSendTxEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.sendRawTransactionForEthByEtherScan(secondNetWork.get(network), hex, get2ndRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                if (!checkEthError(mediatorLiveData, api.body, true)) {
                    EntSendTxEntity entSendTxEntity = new EntSendTxEntity();
                    entSendTxEntity.setTxid(api.body.getResult());
                    mediatorLiveData.postValue(Resource.success(entSendTxEntity));
                }
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * isConfirmedTxForEth by first network
     *
     * @param network
     * @param txId
     * @return
     */
    private LiveData<Resource<EntConfirmedEntity>> isConfirmedTxForEthBy1st(int network, String txId) {
        MediatorLiveData<Resource<EntConfirmedEntity>> mediatorLiveData = new MediatorLiveData<>();
        EthRequestForInfura request = new EthRequestForInfura();
        request.setMethod(EthRequestForInfura.TRANSACTION_RECEIPT);
        request.parseParams(txId);
        mediatorLiveData.addSource(transactionThirdService.isConfirmedTxForEthByInfura(firstNetWork.get(network), request, get1stRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * isConfirmedTxForEth by second network
     *
     * @param network
     * @param txId
     * @return
     */
    private LiveData<Resource<EntConfirmedEntity>> isConfirmedTxForEthBy2nd(int network, String txId) {
        MediatorLiveData<Resource<EntConfirmedEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.isConfirmedTxForEthByEtherScan(secondNetWork.get(network), txId, get2ndRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(api.body.parseToENotesEntity()));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * callEthBy2nd by first network
     *
     * @param toAddress
     * @param data
     * @return
     */
    private LiveData<Resource<EntCallEntity>> callEthBy1st(String network, String toAddress, String data) {
        MediatorLiveData<Resource<EntCallEntity>> mediatorLiveData = new MediatorLiveData<>();
        EthRequestForInfura request = new EthRequestForInfura();
        request.setMethod(EthRequestForInfura.CALL);
        Map<String, String> paramsMap = new HashMap<>();
        if (!toAddress.startsWith("0x")) toAddress = "0x" + toAddress;
        paramsMap.put("to", toAddress);
        if (!data.startsWith("0x")) data = "0x" + data;
        paramsMap.put("data", data);
        request.parseParams(paramsMap, "latest");
        mediatorLiveData.addSource(transactionThirdService.callForEthByInfura(network, request, get2ndRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(api.body));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    /**
     * callEthBy2nd by second network
     *
     * @param toAddress
     * @param data
     * @return
     */
    private LiveData<Resource<EntCallEntity>> callEthBy2nd(String network, String toAddress, String data) {
        MediatorLiveData<Resource<EntCallEntity>> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(transactionThirdService.callForEthByEtherScan(network, toAddress, data, get2ndRandomApiKey()), (api -> {
            if (api.isSuccessful()) {
                mediatorLiveData.postValue(Resource.success(api.body));
            } else {
                mediatorLiveData.postValue(Resource.error(ErrorCode.NET_ERROR, api.errorMessage));
            }
        }));
        return mediatorLiveData;
    }

    private String get1stRandomApiKey() {
        String[] apiKeys = {"1a3b1601f246404b9578a0d1be70e6f3", "4913275e9b8f45fb8c8e5870c7c91bf7", "11bf57adbfc943228151de57fc441b8b", "7ddb525a0d2a48ee86fcb6776a50104f", "939218ad17d849ef8ed82fec270d41c1"};
        if (RPCApiManager.networkConfig != null && RPCApiManager.networkConfig.infuraKeys != null && RPCApiManager.networkConfig.infuraKeys.length > 1) {
            String[] keys = RPCApiManager.networkConfig.infuraKeys;
            return keys[new Random().nextInt(100) % keys.length];
        } else {
            return apiKeys[new Random().nextInt(100) % 5];
        }
    }

    private String get2ndRandomApiKey() {
        String[] apiKeys = {"JVSWRAFRJZ5I5ANGHS3SVHTP1FRFP67A4J", "X9QCCGJS9PN331TSRE368Y6EG5EAZ3PN8M", "GVZVPSQHD6AY15MIJXJFRY4AW82AT7Z1UG"};
        if (RPCApiManager.networkConfig != null && RPCApiManager.networkConfig.etherscanKeys != null && RPCApiManager.networkConfig.etherscanKeys.length > 1) {
            String[] keys = RPCApiManager.networkConfig.etherscanKeys;
            return keys[new Random().nextInt(100) % keys.length];
        } else {
            return apiKeys[new Random().nextInt(100) % 3];
        }
    }
}
