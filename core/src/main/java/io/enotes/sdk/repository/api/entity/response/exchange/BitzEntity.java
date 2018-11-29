package io.enotes.sdk.repository.api.entity.response.exchange;

import io.enotes.sdk.constant.Constant;
import io.enotes.sdk.repository.api.entity.BaseENotesEntity;
import io.enotes.sdk.repository.api.entity.BaseThirdEntity;
import io.enotes.sdk.repository.api.entity.EntExchangeRateEntity;
import io.enotes.sdk.repository.provider.api.ExchangeRateApiProvider;

public class BitzEntity implements BaseThirdEntity {
    private int status;
    private String msg;
    private Data data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public EntExchangeRateEntity parseToENotesEntity() {
        EntExchangeRateEntity rateEntity = new EntExchangeRateEntity();
        rateEntity.setExchange("bitz");
        rateEntity.setDigiccy(data.btc == null ? Constant.CardType.ETH : Constant.CardType.BTC);
        Symbol symbol = data.btc == null ? data.eth : data.btc;
        EntExchangeRateEntity.Data exData = new EntExchangeRateEntity.Data();
        exData.setBtc(symbol.btc);
        exData.setEth(symbol.eth);
        exData.setUsd(symbol.usd);
        exData.setEur(symbol.eur);
        exData.setCny(symbol.cny);
        exData.setJpy(symbol.jpy);
        rateEntity.setData(exData);
        return rateEntity;
    }

    public static class Data {
        private Symbol btc;
        private Symbol eth;

        public Symbol getBtc() {
            return btc;
        }

        public void setBtc(Symbol btc) {
            this.btc = btc;
        }

        public Symbol getEth() {
            return eth;
        }

        public void setEth(Symbol eth) {
            this.eth = eth;
        }
    }

    public static class Symbol {
        private String btc;
        private String eth;
        private String usd;
        private String cny;
        private String eur;
        private String jpy;

        public String getBtc() {
            return btc;
        }

        public void setBtc(String btc) {
            this.btc = btc;
        }

        public String getEth() {
            return eth;
        }

        public void setEth(String eth) {
            this.eth = eth;
        }

        public String getUsd() {
            return usd;
        }

        public void setUsd(String usd) {
            this.usd = usd;
        }

        public String getCny() {
            return cny;
        }

        public void setCny(String cny) {
            this.cny = cny;
        }

        public String getEur() {
            return eur;
        }

        public void setEur(String eur) {
            this.eur = eur;
        }

        public String getJpy() {
            return jpy;
        }

        public void setJpy(String jpy) {
            this.jpy = jpy;
        }
    }
}
