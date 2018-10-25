package io.enotes.sdk.core;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.List;
import java.util.Objects;

import io.enotes.sdk.core.interfaces.RPCApiInterface;
import io.enotes.sdk.repository.api.entity.EntBalanceEntity;
import io.enotes.sdk.repository.api.entity.EntCallEntity;
import io.enotes.sdk.repository.api.entity.EntConfirmedEntity;
import io.enotes.sdk.repository.api.entity.EntFeesEntity;
import io.enotes.sdk.repository.api.entity.EntGasEntity;
import io.enotes.sdk.repository.api.entity.EntGasPriceEntity;
import io.enotes.sdk.repository.api.entity.EntNonceEntity;
import io.enotes.sdk.repository.api.entity.EntSendTxEntity;
import io.enotes.sdk.repository.api.entity.EntUtxoEntity;
import io.enotes.sdk.repository.provider.ApiProvider;
import io.enotes.sdk.viewmodel.RPCApiViewModel;

public class RPCApiManager implements RPCApiInterface {
    private ApiProvider apiProvider;
    private @NonNull
    FragmentActivity fragmentActivity;
    public static NetworkConfig networkConfig;

    public static void setNetworkConfig(NetworkConfig config) {
        networkConfig = config;
    }

    public RPCApiManager(@NonNull FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        RPCApiViewModel rpcApiViewModel = ViewModelProviders.of(fragmentActivity).get(RPCApiViewModel.class);
        apiProvider = rpcApiViewModel.getApiProvider();
    }

    public RPCApiManager(@NonNull Fragment fragment) {
        this.fragmentActivity = Objects.requireNonNull(fragment.getActivity());
        RPCApiViewModel rpcApiViewModel = ViewModelProviders.of(fragmentActivity).get(RPCApiViewModel.class);
        apiProvider = rpcApiViewModel.getApiProvider();
    }

    @Override
    public void getBalance(String blockchain, int network, String address, @NonNull Callback<EntBalanceEntity> callback) {
        apiProvider.getBalance(blockchain, network, address).observe(fragmentActivity, (callback::onCallBack));
    }

    @Override
    public void getTransactionReceipt(String blockchain, int network, String txId, @NonNull Callback<EntConfirmedEntity> callback) {
        apiProvider.getTransactionReceipt(blockchain, network, txId).observe(fragmentActivity, callback::onCallBack);
    }

    @Override
    public void sendRawTransaction(String blockchain, int network, String hexString, @NonNull Callback<EntSendTxEntity> callback) {
        apiProvider.sendRawTransaction(blockchain, network, hexString).observe(fragmentActivity, (callback::onCallBack));
    }

    @Override
    public void estimateFee(int network, @NonNull Callback<EntFeesEntity> callback) {
        apiProvider.estimateFee(network).observe(fragmentActivity, (callback::onCallBack));
    }

    @Override
    public void getUnSpend(int network, String address, @NonNull Callback<List<EntUtxoEntity>> callback) {
        apiProvider.getUnSpend(network, address).observe(fragmentActivity, (callback::onCallBack));
    }

    @Override
    public void estimateGas(int network, String from, String to, String value, String gasPrice, String data, @NonNull Callback<EntGasEntity> callback) {
        apiProvider.estimateGas(network, from, to, value, gasPrice, data).observe(fragmentActivity, (callback::onCallBack));
    }

    @Override
    public void getGasPrice(int network, @NonNull Callback<EntGasPriceEntity> callback) {
        apiProvider.getGasPrice(network).observe(fragmentActivity, (callback::onCallBack));
    }

    @Override
    public void getNonce(int network, String address, @NonNull Callback<EntNonceEntity> callback) {
        apiProvider.getNonce(network, address).observe(fragmentActivity, (callback::onCallBack));
    }

    @Override
    public void call(int network, String address, String data, @NonNull Callback<EntCallEntity> callback) {
        apiProvider.call(network, address, data).observe(fragmentActivity, (callback::onCallBack));
    }


    public static class NetworkConfig {
        public String[] etherscanKeys;
        public String[] infuraKeys;
        public String[] blockchypherKeys;
    }
}
