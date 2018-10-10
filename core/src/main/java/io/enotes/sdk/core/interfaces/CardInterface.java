package io.enotes.sdk.core.interfaces;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;

import java.util.List;

import io.enotes.sdk.core.Callback;
import io.enotes.sdk.repository.api.entity.EntUtxoEntity;
import io.enotes.sdk.repository.db.entity.Card;

public interface CardInterface {
    /**
     * start scan bluetooth devices
     *
     * @param callback
     */
    void startBluetoothScan(Callback callback);

    /**
     * stop scan bluetooth devices
     *
     * @param callback
     */
    void stopBluetoothScan(Callback callback);

    /**
     * connect bluetooth device,you can get connect statu from [setReadCardCallback](#setReadCardCallback)
     *
     * @param bluetoothDevice
     */
    void connectBluetooth(BluetoothDevice bluetoothDevice);

    /**
     * disconnect bluetooth device,you can get connect statu from [setReadCardCallback](#setReadCardCallback)
     */
    void disconnectBluetooth();

    /**
     * Limit the NFC controller to reader mode while this Activity is in the foreground ,
     * you can get read status from [setReadCardCallback](#setReadCardCallback),support you set on BaseActivity onResume().
     *
     * @param activity
     */
    void enableNfcReader(Activity activity);

    /**
     * Restore the NFC adapter to normal mode of operation,support you set on BaseActivity onPause().
     *
     * @param activity
     */
    void disableNfcReader(Activity activity);

    /**
     * after read cert and verify sign by bluetooth or nfc , will get card object.
     *
     * @param cardCallback
     */
    void setReadCardCallback(Callback cardCallback);

    /**
     * judge whether bluetooth is connected
     *
     * @return
     */
    boolean isConnected();

    /**
     * judge whether card is clinging to phone or bluetooth
     *
     * @return
     */
    boolean isPresent();

    /**
     * get btc raw transaction
     *
     * @param card
     * @param fees
     * @param toAddress
     * @param unSpends
     * @param callback
     */
    void getBtcRawTransaction(Card card, String fees, String toAddress, List<EntUtxoEntity> unSpends, Callback callback);

    /**
     * get eth raw transaction
     *
     * @param card
     * @param nonce
     * @param estimateGas
     * @param gasPrice
     * @param value
     * @param callback
     */
    void getEthRawTransaction(Card card, String nonce, String estimateGas, String gasPrice, String toAddress, String value, byte[] data, Callback callback);
}
