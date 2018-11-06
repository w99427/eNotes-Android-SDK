package io.enotes.examples.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.enotes.examples.DemoApplication;
import io.enotes.examples.R;
import io.enotes.examples.common.runtimepermission.PermissionsManager;
import io.enotes.examples.common.runtimepermission.PermissionsResultAction;
import io.enotes.examples.common.utils.DialogUtils;
import io.enotes.sdk.constant.Status;
import io.enotes.sdk.core.CardManager;
import io.enotes.sdk.core.RPCApiManager;
import io.enotes.sdk.repository.api.entity.EntFeesEntity;
import io.enotes.sdk.repository.api.entity.EntGasPriceEntity;
import io.enotes.sdk.repository.api.entity.EntNonceEntity;
import io.enotes.sdk.repository.api.entity.EntUtxoEntity;
import io.enotes.sdk.repository.db.entity.Card;
import io.enotes.sdk.utils.CardUtils;
import io.enotes.sdk.utils.Utils;

public class WithdrawActivity extends AppCompatActivity {
    private static final String TAG = "WithdrawActivity";
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_chain)
    TextView tvName;
    @BindView(R.id.tv_balance_t)
    TextView tvBalanceT;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_recommend)
    TextView tvRecommend;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.tv_from_address)
    TextView tvAddress;
    @BindView(R.id.et_fees)
    EditText etFees;
    @BindView(R.id.tv_fees_t)
    TextView tvFeesT;
    @BindView(R.id.tv_gas)
    TextView tvGas;
    @BindView(R.id.tv_gas_price)
    TextView tvGasPrice;
    @BindView(R.id.tv_gas_used)
    TextView tvGasUsed;
    @BindView(R.id.et_gas)
    EditText etGas;
    @BindView(R.id.et_gas_used)
    EditText etGasUsed;
    @BindView(R.id.rl_gas)
    RelativeLayout rlGas;
    @BindView(R.id.rl_gas_used)
    RelativeLayout rlGasUsed;
    private Card card;
    private BigInteger toValue;
    private List<EntUtxoEntity> unspent_outputs = new ArrayList<>();
    private RPCApiManager rpcApiManager;
    private CardManager cardManager;
    private String ethNonce;
    public static final int REQUEST_CODE = 111;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.withdraw);
        setContentView(R.layout.activity_withdraw);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        card = ((DemoApplication) getApplication()).getCard();
        rpcApiManager = new RPCApiManager(this);
        cardManager = new CardManager(this);
        initViews();
        initData();
    }

    private void initViews() {
        if (card != null) {
            ivIcon.setImageResource(CardUtils.isBTC(card.getCert().getBlockChain()) ? R.mipmap.ic_bit : R.mipmap.ic_eth);
            tvName.setText(CardUtils.getChain(card));
            tvAddress.setText(card.getAddress());
            tvNumber.setText(card.getCert().getSerialNumber());
            tvFeesT.setText("satoshi");
            if (CardUtils.isETH(card.getCert().getBlockChain())) {
                tvGas.setVisibility(View.VISIBLE);
                tvGasUsed.setVisibility(View.VISIBLE);
                rlGas.setVisibility(View.VISIBLE);
                rlGasUsed.setVisibility(View.VISIBLE);
                tvGasPrice.setText(R.string.gas_price);
                tvFeesT.setText("wei");
                addGasUsedListener(etFees);
                addGasUsedListener(etGas);
                addAddressListener(etAddress);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cardManager.enableNfcReader(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cardManager.disableNfcReader(this);
    }

    private void initData() {
        //get balance
        rpcApiManager.getBalance(card.getCert().getBlockChain(), card.getCert().getNetWork(), card.getAddress(), (resource -> {
            if (resource.status == Status.SUCCESS) {
                String balance = "";
                if (CardUtils.isBTC(card.getCert().getBlockChain())) {
                    balance = resource.data.getIntBalance();
                    BigDecimal bigDecimal = new BigDecimal(balance);
                    toValue = bigDecimal.toBigInteger();
                    balance = CardUtils.getBitcoinValue(bigDecimal.toBigInteger());

                } else {
                    toValue = new BigInteger(resource.data.getIntBalance());
                    if (TextUtils.isEmpty(card.getCert().getTokenAddress()))
                        balance = CardUtils.getEthValue(new BigInteger(resource.data.getIntBalance()));

                }
                tvBalance.setText(balance);
            }
        }));
        if (CardUtils.isBTC(card.getCert().getBlockChain())) { //for btc
            //get estimate fee
            rpcApiManager.estimateFee(card.getCert().getNetWork(), (resource -> {
                if (resource.status == Status.SUCCESS) {
                    if (TextUtils.isEmpty(etFees.getText())) {

                        if (TextUtils.isEmpty(resource.data.getFastest())) {
                            tvRecommend.setVisibility(View.GONE);
                            etFees.setText(getBitcoinFee(resource.data.getFast()));
                        } else {
                            tvRecommend.setVisibility(View.VISIBLE);
                            etFees.setText(getBitcoinFee(resource.data.getFastest()));
                            String[] arr = getResources().getStringArray(R.array.tx_recommend);
                            tvRecommend.setText(arr[0] + getBitcoinFee(resource.data.getLow()) + "; " + arr[1] + getBitcoinFee(resource.data.getFast()) + "; " + arr[2] + getBitcoinFee(resource.data.getFastest()));
                        }
                    }
                }
            }));

            //get unSpemd
            rpcApiManager.getUnSpend(card.getCert().getNetWork(), card.getAddress(), (resource -> {
                if (resource.status == Status.SUCCESS) {
                    unspent_outputs = resource.data;
                }
            }));
        } else {//for eth
            // get nonce
            rpcApiManager.getNonce(card.getCert().getNetWork(), card.getAddress(), (resource -> {
                if (resource.status == Status.SUCCESS) {
                    EntNonceEntity nonceEntity = resource.data;
                    ethNonce = nonceEntity.getNonce();
                }
            }));
            //get gasPrice
            rpcApiManager.getGasPrice(card.getCert().getNetWork(), (resource -> {
                if (resource.status == Status.SUCCESS) {
                    if (TextUtils.isEmpty(etFees.getText()))
                        etFees.setText(resource.data.getFast());
                    if (TextUtils.isEmpty(resource.data.getFastest())) {
                        tvRecommend.setVisibility(View.GONE);
                    } else {
                        tvRecommend.setVisibility(View.VISIBLE);
                        String[] arr = getResources().getStringArray(R.array.eth_tx_recommend);
                        tvRecommend.setText(arr[0] + resource.data.getLow() + "; " + arr[1] + resource.data.getStander() + ";\n" + arr[2] + resource.data.getFast() + "; " + arr[3] + resource.data.getFastest());
                    }

                }
            }));
        }

    }

    private void addGasUsedListener(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setGasUsedToTextView();
            }
        });
    }

    private void setGasUsedToTextView() {
        if (!TextUtils.isEmpty(etFees.getText().toString()) && !TextUtils.isEmpty(etGas.getText().toString())) {
            try {
                BigInteger multiply = new BigInteger(etFees.getText().toString()).multiply(new BigInteger(etGas.getText().toString()));
                etGasUsed.setText(CardUtils.wei2eth(multiply).toString() + " ETH");
                etGasUsed.setTag(multiply.toString());
            } catch (NumberFormatException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    /**
     * listen address EditText for get gas
     *
     * @param editText
     */
    private void addAddressListener(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(etAddress.getText())) {
                    String address = etAddress.getText().toString();
                    int addressLength = address.length();
                    if (CardUtils.isETH(card.getCert().getBlockChain())) {
                        String toAddress = address;
                        if (!toAddress.startsWith("0x")) {
                            toAddress = "0x" + toAddress;
                        }
                        //eth address is 20 bytes
                        if (toAddress.length() == 42) {
                            etGas.setText("");
                            rpcApiManager.estimateGas(card.getCert().getNetWork(), card.getAddress(), toAddress, toValue == null ? "0" : toValue.toString(), etFees.getText().toString(), null, (resource -> {
                                if (resource.status == Status.SUCCESS) {
                                    BigInteger bigInteger = Utils.hexToBigInteger(resource.data.getGas());
                                    if (!bigInteger.toString().equals("21000")) {
                                        BigDecimal bigDecimal = new BigDecimal(bigInteger);
                                        bigInteger = bigDecimal.toBigInteger();
                                    }
                                    if (TextUtils.isEmpty(etGas.getText()))
                                        etGas.setText(bigInteger.toString());
                                    setGasUsedToTextView();
                                }
                            }));
                        }
                    }
                }
            }
        });
    }

    /**
     * calculate bitcoin fee
     * http://bitcoinfees.com/
     *
     * @param bitFees
     * @return
     */
    private String getBitcoinFee(String bitFees) {
        int changeCount = 0;
        return (148 * unspent_outputs.size() + (changeCount == 0 ? 1 : 2) * 34 + 10) * Long.valueOf(bitFees) / 1000 + "";
    }

    @OnClick(R.id.iv_address_del)
    void clearAddressClick() {
        etAddress.setText("");
    }

    @OnClick(R.id.iv_gas_del)
    void clearGasClick() {
        etGas.setText("");
    }

    @OnClick(R.id.iv_fees_del)
    void clearFeesClick() {
        etFees.setText("");
    }

    @OnClick(R.id.rl_send)
    void withdrawClick() {
        sendTransaction();
    }

    /**
     * send raw transaction
     */
    private void sendTransaction() {
        if (TextUtils.isEmpty(etAddress.getText())) {
            Utils.showToast(this, getString(R.string.please_select_to_address));
            return;
        }
        if (!Utils.isNetworkConnected(this)) {
            Utils.showToast(this, getString(R.string.network_unavailable));
            return;
        }
        if (toValue == null) return;
        String fee;
        String amount;
        if (CardUtils.isBTC(card.getCert().getBlockChain())) {

            if (TextUtils.isEmpty(etFees.getText())) {
                Utils.showToast(this, getString(R.string.please_select_fees));
                return;
            }

            BigInteger fee1 = new BigInteger(etFees.getText().toString());
            fee = CardUtils.formatBalance(CardUtils.satoshi2bitcoin(fee1), 5).toString() + " BTC";
            amount = CardUtils.formatBalance(CardUtils.satoshi2bitcoin(toValue.subtract(fee1)), 5) + " BTC";
        } else {

            if (TextUtils.isEmpty(etFees.getText())) {
                Utils.showToast(this, getString(R.string.please_select_gaslimit));
                return;
            }
            if (TextUtils.isEmpty(etGas.getText().toString())) {
                Utils.showToast(this, getString(R.string.please_select_gas));
                return;
            }

            BigInteger fee1 = new BigInteger(etGasUsed.getTag().toString());
            fee = CardUtils.formatBalance(CardUtils.wei2eth(fee1), 5).toString() + " ETH";

            amount = CardUtils.formatBalance(CardUtils.wei2eth(toValue.subtract(fee1)), 5) + " ETH";

        }

        AlertDialog alertDialog = DialogUtils.showWithdrawDialog(this, etAddress.getText().toString(), amount, fee);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(((view) -> {
            alertDialog.dismiss();
            if (CardUtils.isBTC(card.getCert().getBlockChain())) {
                cardManager.getBtcRawTransaction(card, etFees.getText().toString(), etAddress.getText().toString(), unspent_outputs, (resource -> {
                    if (resource.status == Status.SUCCESS) {
                        rpcApiManager.sendRawTransaction(card.getCert().getBlockChain(), card.getCert().getNetWork(), resource.data, (resource1 -> {
                            if (resource1.status == Status.SUCCESS) {
                                Toast.makeText(WithdrawActivity.this, R.string.send_success, Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (resource1.status == Status.ERROR) {
                                Toast.makeText(WithdrawActivity.this, resource1.message, Toast.LENGTH_SHORT).show();

                            }
                        }));
                    } else if (resource.status == Status.ERROR) {
                        Toast.makeText(WithdrawActivity.this, resource.message, Toast.LENGTH_SHORT).show();

                    }
                }));
            } else {
                cardManager.getEthRawTransaction(card, ethNonce, etGas.getText().toString(), etFees.getText().toString(), etAddress.getText().toString(), toValue.toString(), null, (resource -> {
                    if (resource.status == Status.SUCCESS) {
                        rpcApiManager.sendRawTransaction(card.getCert().getBlockChain(), card.getCert().getNetWork(), resource.data, (resource1 -> {
                            if (resource1.status == Status.SUCCESS) {
                                Toast.makeText(WithdrawActivity.this, R.string.send_success, Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (resource1.status == Status.ERROR) {
                                Toast.makeText(WithdrawActivity.this, resource1.message, Toast.LENGTH_SHORT).show();

                            }


                        }));
                    } else if (resource.status == Status.ERROR) {
                        Toast.makeText(WithdrawActivity.this, resource.message, Toast.LENGTH_SHORT).show();

                    }
                }));
            }

        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_withdraw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_camera) {
            if (PermissionsManager.getInstance().hasPermission(this, Manifest.permission.CAMERA)) {
                startActivityForResult(new Intent(this, ScanQrActivity.class), REQUEST_CODE);
            } else {
                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        startActivityForResult(new Intent(WithdrawActivity.this, ScanQrActivity.class), REQUEST_CODE);
                    }

                    @Override
                    public void onDenied(String permission) {

                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                //get qr code
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String scanResult = bundle.getString(CodeUtils.RESULT_STRING);
                    if (scanResult.contains(":"))
                        scanResult = scanResult.substring(scanResult.indexOf(":") + 1);
                    etAddress.setText(scanResult);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                }
            }
        }
    }
}
