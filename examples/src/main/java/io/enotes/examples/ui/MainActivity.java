package io.enotes.examples.ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.enotes.examples.DemoApplication;
import io.enotes.examples.R;
import io.enotes.examples.common.runtimepermission.PermissionsManager;
import io.enotes.examples.common.runtimepermission.PermissionsResultAction;
import io.enotes.sdk.constant.Status;
import io.enotes.sdk.core.CardManager;
import io.enotes.sdk.repository.card.Reader;
import io.enotes.sdk.repository.db.entity.Card;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private CardManager cardManager;
    private ListView listView;
    private TextView textView;
    private List<BluetoothDevice> list = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait ...");
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        textView = (TextView) findViewById(R.id.tv);
        cardManager = new CardManager(this);
        cardManager.setReadCardCallback((resource -> {
            Log.i(TAG, "read card status = " + resource.status + "\n message = " + resource.message + "/n errorCode = " + resource.errorCode);
            if (resource.status == Status.SUCCESS) {
                progressDialog.dismiss();
                Card card = resource.data;
                ((DemoApplication) getApplication()).setCard(card);
                startActivity(new Intent(this, CardInfoActivity.class));
            } else if (resource.status == Status.NFC_CONNECTED || resource.status == Status.BLUETOOTH_CONNECTING) {
                progressDialog.show();
            } else if (resource.status == Status.ERROR) {
                Toast.makeText(this,resource.message,Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cardManager.enableNfcReader(this);
        list.clear();
        listView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cardManager.disableNfcReader(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            listView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            progressDialog.show();
            list.clear();
            adapter.notifyDataSetChanged();
            cardManager.startBluetoothScan((resource -> {
                if (resource.status == Status.SUCCESS) {
                    Reader data = resource.data;
                    list.add(data.getDeviceInfo());
                    adapter.notifyDataSetChanged();
                } else if (resource.status == Status.BLUETOOTH_SCAN_FINISH) {
                    progressDialog.dismiss();
                }
            }));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(MainActivity.this);
                convertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 140));
                ((TextView) convertView).setGravity(Gravity.CENTER);
            }
            if (list.size() > position)
                ((TextView) convertView).setText(list.get(position).getName());
            convertView.setOnClickListener((v -> {
                cardManager.connectBluetooth(list.get(position));
            }));
            return convertView;
        }
    };
}
