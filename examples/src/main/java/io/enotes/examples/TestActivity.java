package io.enotes.examples;

import android.annotation.TargetApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.enotes.examples.common.runtimepermission.PermissionsManager;
import io.enotes.examples.common.runtimepermission.PermissionsResultAction;
import io.enotes.sdk.core.CardManager;

public class TestActivity extends AppCompatActivity {
    private CardManager cardManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        cardManager = new CardManager(this);
    }
    /**
     * request all needed permission
     */
    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onDenied(String permission) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    public CardManager getCardManager() {
        return cardManager;
    }
}
