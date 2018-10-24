package io.enotes.sdk.core.interfaces;

import android.support.annotation.NonNull;

import java.util.List;

import io.enotes.sdk.core.Callback;
import io.enotes.sdk.repository.api.entity.EntBalanceEntity;
import io.enotes.sdk.repository.api.entity.EntCallEntity;
import io.enotes.sdk.repository.api.entity.EntConfirmedEntity;
import io.enotes.sdk.repository.api.entity.EntFeesEntity;
import io.enotes.sdk.repository.api.entity.EntGasEntity;
import io.enotes.sdk.repository.api.entity.EntGasPriceEntity;
import io.enotes.sdk.repository.api.entity.EntNonceEntity;
import io.enotes.sdk.repository.api.entity.EntSendTxEntity;
import io.enotes.sdk.repository.api.entity.EntUtxoEntity;

public interface RPCApiInterface {

    ///////////////////////Universal////////////////////////////////

    /**
     * Returns the balance of the account of given address
     *
     * @param blockchain
     * @param network
     * @param address
     * @param callback
     */
    void getBalance(String blockchain, int network, String address, @NonNull Callback<EntBalanceEntity> callback);

    /**
     * Returns the receipt of a transaction by transaction hash
     *
     * @param blockchain
     * @param network
     * @param txId
     * @param callback
     */
    void getTransactionReceipt(String blockchain, int network, String txId, @NonNull Callback<EntConfirmedEntity> callback);

    /**
     * Creates new message call transaction or a contract creation for signed transactions
     *
     * @param blockchain
     * @param network
     * @param hexString
     * @param callback
     */
    void sendRawTransaction(String blockchain, int network, String hexString, @NonNull Callback<EntSendTxEntity> callback);

    ///////////////////////BTC////////////////////////////////

    /**
     * Return current Bitcoin transaction fee predictions
     *
     * @param network
     * @param callback
     */
    void estimateFee(int network, @NonNull Callback<EntFeesEntity> callback);

    /**
     * Returns data contained in all unspend outputs
     *
     * @param network
     * @param address
     * @param callback
     */
    void getUnSpend(int network, String address, @NonNull Callback<List<EntUtxoEntity>> callback);

    ///////////////////////ETH////////////////////////////////

    /**
     * Generates and returns an estimate of how much gas is necessary to allow the transaction to complete
     *
     * @param network
     * @param from
     * @param to
     * @param value
     * @param gasPrice
     * @param data
     * @param callback
     */
    void estimateGas(int network, String from, String to, String value, String gasPrice, String data, @NonNull Callback<EntGasEntity> callback);

    /**
     * Returns the number of hashes per second that the node is mining with
     *
     * @param network
     * @param callback
     */
    void getGasPrice(int network, @NonNull Callback<EntGasPriceEntity> callback);

    /**
     * Returns the number of transactions sent from an address
     *
     * @param network
     * @param address
     * @param callback
     */
    void getNonce(int network, String address, @NonNull Callback<EntNonceEntity> callback);

    /**
     * Executes a new message call immediately without creating a transaction on the block chain
     *
     * @param network
     * @param address
     * @param data
     * @param callback
     */
    void call(int network, String address, String data, @NonNull Callback<EntCallEntity> callback);
}
