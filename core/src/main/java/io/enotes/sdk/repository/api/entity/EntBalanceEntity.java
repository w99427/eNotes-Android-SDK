package io.enotes.sdk.repository.api.entity;


import io.enotes.sdk.utils.Utils;

public class EntBalanceEntity extends BaseENotesEntity {
    private String address;
    private String balance;
    private String unit;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBalance() {
        return Utils.hexToBigIntString(balance);
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getIntBalance() {
        return Utils.hexToBigIntString(balance);
    }
}

