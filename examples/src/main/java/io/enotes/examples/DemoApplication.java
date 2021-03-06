package io.enotes.examples;

import android.app.Application;

import io.enotes.sdk.core.ENotesSDK;
import io.enotes.sdk.repository.db.entity.Card;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ENotesSDK.config.debugCard=true;
    }

    private Card card;

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
