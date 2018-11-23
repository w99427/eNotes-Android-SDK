package io.enotes.sdk.repository.api.entity;

public class EntExchangeRateEntity extends BaseENotesEntity {
    private String exchange;
    private String digiccy;
    private Data data;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getDigiccy() {
        return digiccy;
    }

    public void setDigiccy(String digiccy) {
        this.digiccy = digiccy;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String btc;
        private String eth;
        private String usd;
        private String eur;
        private String cny;
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

        public String getEur() {
            return eur;
        }

        public void setEur(String eur) {
            this.eur = eur;
        }

        public String getCny() {
            return cny;
        }

        public void setCny(String cny) {
            this.cny = cny;
        }

        public String getJpy() {
            return jpy;
        }

        public void setJpy(String jpy) {
            this.jpy = jpy;
        }
    }

    @Override
    public String toString() {
        return "exchange = " + exchange + "\ndigiccy = " + digiccy + "\nrate = "+ " # btc : " + data.btc+ " # eth : " + data.eth   + " # usd : " + data.usd + " # eur : " + data.eur + " # cny : " + data.cny + " # jpy : " + data.jpy;
    }
}