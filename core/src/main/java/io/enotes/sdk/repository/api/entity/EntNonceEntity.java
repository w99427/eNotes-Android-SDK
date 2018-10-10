package io.enotes.sdk.repository.api.entity;

public class EntNonceEntity extends BaseENotesEntity {
    private String nonce;

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
