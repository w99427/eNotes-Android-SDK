package io.enotes.sdk.repository.db.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.ethereum.util.ByteUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigInteger;

import io.enotes.sdk.utils.CardUtils;


@Entity(tableName = "card")
public class Card {
    /**
     * Tx signed times = 0
     */
    public static final int STATUS_SAFE = 0;
    /**
     * Tx signed times > 0
     */
    public static final int STATUS_UNSAFE = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_SAFE, STATUS_UNSAFE})
    @interface Status {
    }

    @PrimaryKey(autoGenerate = true)
    private long id;

    @Embedded
    private Cert cert;
    private long createTime;
    private long updateTime;
    private String currencyPubKey;//uncompressed
    private String address;
    private BigInteger balance;
    private int status;
    private String txId;

    @Ignore
    private org.bitcoinj.core.ECKey bitCoinECKey;
    @Ignore
    private org.ethereum.crypto.ECKey ethECKey;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Cert getCert() {
        return cert;
    }

    public void setCert(Cert cert) {
        this.cert = cert;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getCurrencyPubKey() {
        return currencyPubKey;
    }

    public void setCurrencyPubKey(String currencyPubKey) {
        this.currencyPubKey = currencyPubKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigInteger getBalance() {
        return balance;
    }

    public void setBalance(BigInteger balance) {
        this.balance = balance;
    }

    @Status
    public int getStatus() {
        return status;
    }

    public void setStatus(@Status int status) {
        this.status = status;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public org.bitcoinj.core.ECKey getBitCoinECKey() {
        if (bitCoinECKey == null && currencyPubKey != null) {
            try {
                bitCoinECKey = org.bitcoinj.core.ECKey.fromPublicOnly(ByteUtil.hexStringToBytes(currencyPubKey));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return bitCoinECKey;
    }


    public org.ethereum.crypto.ECKey getEthECKey() {
        if (ethECKey == null && currencyPubKey != null) {
            try {
                ethECKey = org.ethereum.crypto.ECKey.fromPublicOnly(ByteUtil.hexStringToBytes(currencyPubKey));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return ethECKey;
    }

    @Nullable
    public String getEthTxAddress() {
        getEthECKey();
        if (ethECKey == null) {
            return null;
        }
        return CardUtils.getEthEncodeAddress(ByteUtil.toHexString(ethECKey.getAddress()));
    }

    @Nullable
    public String getBitcoinMainAddress() {
        getBitCoinECKey();
        if (bitCoinECKey == null) {
            return null;
        }
        return bitCoinECKey.toAddress(MainNetParams.get()).toBase58();
    }

    @Nullable
    public String getBitcoinTest3Address() {
        getBitCoinECKey();
        if (bitCoinECKey == null) {
            return null;
        }
        return bitCoinECKey.toAddress(TestNet3Params.get()).toBase58();
    }
}
