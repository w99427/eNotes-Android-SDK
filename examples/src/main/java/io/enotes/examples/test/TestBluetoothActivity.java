package io.enotes.examples.test;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import io.enotes.examples.R;
import io.enotes.examples.TestActivity;
import io.enotes.examples.common.runtimepermission.PermissionsManager;
import io.enotes.examples.common.runtimepermission.PermissionsResultAction;
import io.enotes.sdk.core.CardManager;
import io.enotes.sdk.core.RPCApiManager;

public class TestBluetoothActivity extends AppCompatActivity {
    private CardManager cardManager;
    private RPCApiManager rpcApiManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.CENTER);
        textView.setText("test bluetooth");
        textView.setTextSize(40);
        setContentView(textView);
        requestPermissions();
        cardManager = new CardManager(this);
        rpcApiManager = new RPCApiManager(this);
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
    public RPCApiManager getRpcApiManager() {
        return rpcApiManager;
    }
}
