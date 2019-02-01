package io.enotes.sdk.core;

public class EntSignature {
    private String r;
    private String s;

    public EntSignature(String r, String s) {
        this.r = r;
        this.s = s;
    }

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }
}
