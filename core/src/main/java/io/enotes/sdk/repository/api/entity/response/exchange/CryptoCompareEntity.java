package io.enotes.sdk.repository.api.entity.response.exchange;

public class CryptoCompareEntity {
    private String BTC;
    private String ETH;
    private String USD;
    private String CNY;
    private String EUR;
    private String JPY;

    public String getBTC() {
        return BTC;
    }

    public void setBTC(String BTC) {
        this.BTC = BTC;
    }

    public String getETH() {
        return ETH;
    }

    public void setETH(String ETH) {
        this.ETH = ETH;
    }

    public String getUSD() {
        return USD;
    }

    public void setUSD(String USD) {
        this.USD = USD;
    }

    public String getCNY() {
        return CNY;
    }

    public void setCNY(String CNY) {
        this.CNY = CNY;
    }

    public String getEUR() {
        return EUR;
    }

    public void setEUR(String EUR) {
        this.EUR = EUR;
    }

    public String getJPY() {
        return JPY;
    }

    public void setJPY(String JPY) {
        this.JPY = JPY;
    }
}