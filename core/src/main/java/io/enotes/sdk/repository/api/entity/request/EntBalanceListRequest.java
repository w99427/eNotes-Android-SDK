package io.enotes.sdk.repository.api.entity.request;


import io.enotes.sdk.repository.api.entity.BaseENotesEntity;

public class EntBalanceListRequest extends BaseENotesEntity {
    private String height;
    private String address;
    private String contract;

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }
}
