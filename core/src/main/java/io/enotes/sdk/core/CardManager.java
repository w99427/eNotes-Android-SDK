package io.enotes.sdk.core;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import org.bitcoinj.core.Transaction;
import org.ethereum.util.ByteUtil;

import java.math.BigInteger;
import java.util.List;

import io.enotes.sdk.constant.ErrorCode;
import io.enotes.sdk.constant.Status;
import io.enotes.sdk.core.interfaces.CardInterface;
import io.enotes.sdk.repository.api.entity.EntUtxoEntity;
import io.enotes.sdk.repository.base.Resource;
import io.enotes.sdk.repository.card.Command;
import io.enotes.sdk.repository.card.CommandException;
import io.enotes.sdk.repository.card.Reader;
import io.enotes.sdk.repository.card.TLVBox;
import io.enotes.sdk.repository.db.entity.Card;
import io.enotes.sdk.repository.provider.CardProvider;
import io.enotes.sdk.utils.BtcRawTransaction;
import io.enotes.sdk.utils.EthRawTransaction;
import io.enotes.sdk.utils.Utils;
import io.enotes.sdk.viewmodel.CardViewModel;

public class CardManager implements CardInterface {
    private CardProvider cardProvider;
    private @NonNull
    FragmentActivity fragmentActivity;
    private Callback readCardCallback;
    private Card connectedCard;

    public CardManager(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        CardViewModel cardViewModel = ViewModelProviders.of(fragmentActivity).get(CardViewModel.class);
        cardProvider = cardViewModel.getCardProvider();
        initCallback();
    }

    public CardManager(Fragment fragment) {
        this.fragmentActivity = fragment.getActivity();
        CardViewModel cardViewModel = ViewModelProviders.of(fragment).get(CardViewModel.class);
        cardProvider = cardViewModel.getCardProvider();
        initCallback();
    }

    private void initCallback() {
        if (cardProvider != null) {
            cardProvider.getCard().observe(fragmentActivity, (resource -> {
                if (resource.status == Status.SUCCESS) {
                    connectedCard = resource.data;
                }
                if (readCardCallback != null) {
                    readCardCallback.onBack(resource);
                }
            }));
        }
    }

    @Override
    public void selectAid(String aid) {
        cardProvider.setTargetAID(aid);
    }

    @Override
    public void startBluetoothScan(@NonNull Callback<Reader> callback) {
        cardProvider.startScan();
        cardProvider.getReader().observe(fragmentActivity, (resource -> {
            callback.onBack(resource);
        }));
    }

    @Override
    public void stopBluetoothScan(Callback callback) {
        cardProvider.destroyScanner();
    }

    @Override
    public void connectBluetooth(BluetoothDevice bluetoothDevice) {
        cardProvider.parseAndConnect(new Reader().setDeviceInfo(bluetoothDevice));
    }

    @Override
    public void disconnectBluetooth() {
        cardProvider.disconnectCard();
    }

    @Override
    public void enableNfcReader(Activity activity) {
        cardProvider.enterForeground(activity);
    }

    @Override
    public void disableNfcReader(Activity activity) {
        cardProvider.enterBackground(activity);
    }

    @Override
    public void setReadCardCallback(Callback<Card> cardCallback) {
        readCardCallback = cardCallback;
    }

    @Override
    public boolean isConnected() {
        return cardProvider.isConnected();
    }

    @Override
    public boolean isPresent() {
        return cardProvider.isPresent();
    }

    @Override
    public void getBtcRawTransaction(Card card, String fees, String toAddress, List<EntUtxoEntity> unSpends, @NonNull Callback<String> callback) {
        if (!cardProvider.isPresent() || connectedCard == null || !connectedCard.getCurrencyPubKey().equals(card.getCurrencyPubKey())) {
            callback.onBack(Resource.error(ErrorCode.NOT_FIND_RIGHT_CARD, "not find right card when withdraw"));
            return;
        }
        BtcRawTransaction btcRawTransaction = new BtcRawTransaction();
        Transaction transaction = btcRawTransaction.createRawTransaction(card, cardProvider, Long.valueOf(fees), toAddress, 0, "", unSpends);
        try {
            btcRawTransaction.signTransactionInputs(transaction);
            callback.onBack(Resource.success(ByteUtil.toHexString(transaction.bitcoinSerialize())));
        } catch (CommandException e) {
            e.printStackTrace();
            callback.onBack(Resource.error(e.getCode(), e.getMessage()));
        }
    }

    @Override
    public void getEthRawTransaction(Card card, String nonce, String estimateGas, String gasPrice, String toAddress, String value, byte[] data, Callback<String> callback) {
        if (!cardProvider.isPresent() || connectedCard == null || !connectedCard.getCurrencyPubKey().equals(card.getCurrencyPubKey())) {
            callback.onBack(Resource.error(ErrorCode.NOT_FIND_RIGHT_CARD, "not find right card when withdraw"));
            return;
        }
        EthRawTransaction ethRawTransaction = new EthRawTransaction();
        try {
            BigInteger toValue = new BigInteger(value).subtract((new BigInteger(gasPrice).multiply(new BigInteger(estimateGas))));
            String rawTransaction = ethRawTransaction.getRawTransaction(card, cardProvider, ByteUtil.bigIntegerToBytes(new BigInteger(nonce)), ByteUtil.bigIntegerToBytes(new BigInteger(gasPrice)), ByteUtil.bigIntegerToBytes(new BigInteger(estimateGas)), ByteUtil.hexStringToBytes(toAddress), ByteUtil.bigIntegerToBytes(toValue), data);
            callback.onBack(Resource.success(rawTransaction));
        } catch (CommandException e) {
            e.printStackTrace();
            callback.onBack(Resource.error(e.getCode(), e.getMessage()));
        }
    }

    @Override
    public String transmitApdu(@NonNull Command command) throws CommandException {
        return cardProvider.transceive(command);
    }
}
