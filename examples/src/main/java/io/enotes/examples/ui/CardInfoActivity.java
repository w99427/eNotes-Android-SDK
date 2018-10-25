package io.enotes.examples.ui;

import android.arch.lifecycle.LifecycleRegistry;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.BigInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.enotes.examples.DemoApplication;
import io.enotes.examples.R;
import io.enotes.examples.common.utils.TransitionDate;
import io.enotes.sdk.constant.Status;
import io.enotes.sdk.core.RPCApiManager;
import io.enotes.sdk.repository.api.entity.EntBalanceEntity;
import io.enotes.sdk.repository.db.entity.Card;
import io.enotes.sdk.utils.CardUtils;
import io.enotes.sdk.utils.Utils;

public class CardInfoActivity extends AppCompatActivity {
    private static final String TAG = "CardInfoActivity";
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.iv_icon_big)
    ImageView ivIconBig;
    @BindView(R.id.tv_chain)
    TextView tvName;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_balance_t)
    TextView tvBalanceT;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_tx_status)
    TextView tvTxStatus;
    @BindView(R.id.iv_status)
    ImageView ivStatus;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;
    @BindView(R.id.tv_issuer)
    TextView tvIssuer;
    @BindView(R.id.tv_record_time)
    TextView tvRecordTime;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_denomination)
    TextView tvDenomination;
    @BindView(R.id.rl_balance)
    RelativeLayout rlBalance;
    private Card card;
    private RPCApiManager rpcApiManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.detail);
        setContentView(R.layout.activity_card_info);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        card = ((DemoApplication) getApplication()).getCard();
        rpcApiManager = new RPCApiManager(this);
        initViews();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getBalance();
    }

    private void initViews() {
        if (card != null) {
            Log.i(TAG, "publicKey=\n" + card.getCurrencyPubKey());
            ivIcon.setImageResource(CardUtils.isBTC(card.getCert().getBlockChain()) ? R.mipmap.ic_bit : R.mipmap.ic_eth);
            tvStatus.setText(card.getStatus() == Card.STATUS_SAFE ? getString(R.string.safe) : getString(R.string.dangerous));
            tvStatus.setTextColor(ContextCompat.getColor(this, card.getStatus() == Card.STATUS_SAFE ? R.color.green : R.color.red));
            tvRecordTime.setText(TransitionDate.DateToStr(card.getCert().getProductionDate()));
            int addressLenght = card.getAddress().length();
            tvAddress.setText(card.getAddress());
            tvAddress.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, addressLenght > 40 ? getResources().getDimension(R.dimen.card_sub1_label) : getResources().getDimension(R.dimen.card_label));
            if (card.getCert() != null) {
                String text = CardUtils.getChain(card);
                tvName.setText(text);
                tvSerialNumber.setText(card.getCert().getSerialNumber());
                tvIssuer.setText(card.getCert().getVendorName());
                if (CardUtils.isBTC(card.getCert().getBlockChain())) {
                    tvDenomination.setText(CardUtils.getBitcoinValue(card.getCert().getCoinDeno()));
                } else {
                    tvDenomination.setText(CardUtils.getEthValue(card.getCert().getCoinDeno()));
                }
            }

            //set swipeRefreshLayout

            tvAddress.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (android.os.Build.VERSION.SDK_INT > 11) {
                        android.content.ClipboardManager c = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        c.setPrimaryClip(ClipData.newPlainText("Label", tvAddress.getText()));

                    } else {
                        android.text.ClipboardManager c = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        c.setText(tvAddress.getText());
                    }
                    Utils.showToast(this, getString(R.string.address_copy_clipboard));
                    return true;
                }
                return false;
            });
        }
    }


    private void getBalance() {
        int observerCount = ((LifecycleRegistry) getLifecycle()).getObserverCount();
        rpcApiManager.getBalance(card.getCert().getBlockChain(), card.getCert().getNetWork(), card.getAddress(), (resource -> {
            Log.i(TAG,"get balance = "+resource.status);
            if (resource.status == Status.SUCCESS) {
                String balance = "";
                if (CardUtils.isBTC(card.getCert().getBlockChain())) {
                    balance = resource.data.getIntBalance();
                    BigDecimal bigDecimal = new BigDecimal(balance);
                    balance = CardUtils.getBitcoinValue(bigDecimal.toBigInteger());
                } else {
                    if (TextUtils.isEmpty(card.getCert().getTokenAddress()))
                        balance = CardUtils.getEthValue(new BigInteger(resource.data.getIntBalance()));
                    else {

                    }
                }
                String[] split = balance.split(" ");
                if (split.length == 2) {
                    tvBalance.setText(split[0]);
                    tvBalanceT.setText(getString(R.string.card_balance) + "(" + split[1] + ")");
                }
            }
        }));
    }

    @OnClick(R.id.rl_withdraw)
    void withdrawClick() {
        startActivity(new Intent(this, WithdrawActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
