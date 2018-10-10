package io.enotes.sdk.core;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import io.enotes.sdk.core.interfaces.RPCApiInterface;
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

    public RPCApiManager(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        RPCApiViewModel rpcApiViewModel = ViewModelProviders.of(fragmentActivity).get(RPCApiViewModel.class);
        apiProvider = rpcApiViewModel.getApiProvider();
    }

    public RPCApiManager(Fragment fragment) {
        this.fragmentActivity = fragment.getActivity();
        RPCApiViewModel rpcApiViewModel = ViewModelProviders.of(fragmentActivity).get(RPCApiViewModel.class);
        apiProvider = rpcApiViewModel.getApiProvider();
    }

    @Override
    public void getBalance(String blockchain, int network, String address, @NonNull Callback callback) {
        apiProvider.getBalance(blockchain, network, address).observe(fragmentActivity, (resource -> {
            callback.onBack(resource);
        }));
    }

    @Override
    public void getTransactionReceipt(String blockchain, int network, String txId, @NonNull Callback callback) {
        apiProvider.getTransactionReceipt(blockchain, network, txId).observe(fragmentActivity, (resource -> {
            callback.onBack(resource);
        }));
    }

    @Override
    public void sendRawTransaction(String blockchain, int network, String hexString, @NonNull Callback callback) {
        apiProvider.sendRawTransaction(blockchain, network, hexString).observe(fragmentActivity, (resource -> {
            callback.onBack(resource);
        }));
    }

    @Override
    public void estimateFee(int network, @NonNull Callback callback) {
        apiProvider.estimateFee(network).observe(fragmentActivity, (resource -> {
            callback.onBack(resource);
        }));
    }

    @Override
    public void getUnSpend(int network, String address, @NonNull Callback callback) {
        apiProvider.getUnSpend(network, address).observe(fragmentActivity, (resource -> {
            callback.onBack(resource);
        }));
    }

    @Override
    public void estimateGas(int network, String from, String to, String value, String gasPrice, String data, @NonNull Callback callback) {
        apiProvider.estimateGas(network, from, to, value, gasPrice, data).observe(fragmentActivity, (resource -> {
            callback.onBack(resource);
        }));
    }

    @Override
    public void getGasPrice(int network, @NonNull Callback callback) {
        apiProvider.getGasPrice(network).observe(fragmentActivity, (resource -> {
            callback.onBack(resource);
        }));
    }

    @Override
    public void getNonce(int network, String address, @NonNull Callback callback) {
        apiProvider.getNonce(network, address).observe(fragmentActivity, (resource -> {
            callback.onBack(resource);
        }));
    }

    @Override
    public void call(int network, String address, String data, @NonNull Callback callback) {
        apiProvider.call(network, address, data).observe(fragmentActivity, (resource -> {
            callback.onBack(resource);
        }));
    }


    public static class NetworkConfig {
        public String[] etherscanKeys;
        public String[] infuraKeys;
        public String[] blockchypherKeys;
    }
}
