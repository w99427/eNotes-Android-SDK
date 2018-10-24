package io.enotes.examples;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.enotes.examples.test.TestBluetoothActivity;
import io.enotes.sdk.constant.Status;
import io.enotes.sdk.core.CardManager;
import io.enotes.sdk.core.RPCApiManager;
import io.enotes.sdk.repository.card.Reader;
import io.enotes.sdk.repository.db.entity.Card;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BluetoothTest extends BaseTest{
    private static final String TAG = "BluetoothTest";
    @Rule
    public ActivityTestRule<TestBluetoothActivity> mActivity = new ActivityTestRule<>(TestBluetoothActivity.class);
    private TestBluetoothActivity activity;

    @Before
    public void setup() {
        activity = mActivity.getActivity();
    }

    @Test
    public void testStartBluetoothScan() {
        Object lock = new Object();
        ThreadLock threadLock = new ThreadLock(lock);
        CardManager cardManager = activity.getCardManager();
        assertNotNull(cardManager);
        cardManager.startBluetoothScan((resource -> {
            threadLock.assertResource(resource);
            if(resource.status == Status.SUCCESS) {
                assertTrue(resource.data instanceof Reader);
                Log.i(TAG, "bluetooth devices name = " +  resource.data.getDeviceInfo().getName());
                threadLock.notifyLock();
            }
        }));
        threadLock.waitAndRelease(4);
    }

    @Test
    public void testConnectBluetooth() {
        Object lock = new Object();
        ThreadLock threadLock = new ThreadLock(lock);
        CardManager cardManager = activity.getCardManager();
        assertNotNull(cardManager);
        cardManager.startBluetoothScan((resource -> {
            threadLock.assertResource(resource);
            if(resource.status == Status.SUCCESS) {
                assertTrue(resource.data instanceof Reader);
                Log.i(TAG, "bluetooth devices name = " + resource.data.getDeviceInfo().getName());
                cardManager.connectBluetooth(((Reader) resource.data).getDeviceInfo());
            }
        }));
        cardManager.setReadCardCallback((resource -> {
            threadLock.assertResource(resource);
            if (resource.status == Status.SUCCESS) {
                assertTrue(resource.data instanceof Card);
                Log.i(TAG, "card cert = " +  resource.data.getCert().toString());
                threadLock.notifyLock();
            }
        }));
        threadLock.waitAndRelease(10);
    }

    @Test
    public void testBtcRawTransaction() {
        Object lock = new Object();
        ThreadLock threadLock = new ThreadLock(lock);
        CardManager cardManager = activity.getCardManager();
        assertNotNull(cardManager);
        RPCApiManager rpcApiManager = activity.getRpcApiManager();
        assertNotNull(rpcApiManager);
        cardManager.startBluetoothScan((resource -> {
            threadLock.assertResource(resource);
            if(resource.status == Status.SUCCESS) {
                assertTrue(resource.data instanceof Reader);
                Log.i(TAG, "bluetooth devices name = " +  resource.data.getDeviceInfo().getName());
                cardManager.connectBluetooth( resource.data.getDeviceInfo());
            }
        }));
        cardManager.setReadCardCallback((resource -> {
            if (resource.status == Status.SUCCESS) {
                assertTrue(resource.data instanceof Card);
                Log.i(TAG, "card cert = " +  resource.data.getCert().toString());
                getBtcRawTransaction(threadLock,  resource.data, cardManager, rpcApiManager);
            }
        }));
        threadLock.waitAndRelease(15);
    }

    @Test
    public void testEthRawTransaction() {
        Object lock = new Object();
        ThreadLock threadLock = new ThreadLock(lock);
        CardManager cardManager = activity.getCardManager();
        assertNotNull(cardManager);
        RPCApiManager rpcApiManager = activity.getRpcApiManager();
        assertNotNull(rpcApiManager);
        cardManager.startBluetoothScan((resource -> {
            threadLock.assertResource(resource);
            if(resource.status == Status.SUCCESS) {
                assertTrue(resource.data instanceof Reader);
                Log.i(TAG, "bluetooth devices name = " +  resource.data.getDeviceInfo().getName());
                cardManager.connectBluetooth( resource.data.getDeviceInfo());
            }
        }));
        cardManager.setReadCardCallback((resource -> {
            if (resource.status == Status.SUCCESS) {
                assertTrue(resource.data instanceof Card);
                Log.i(TAG, "card cert = " + resource.data.getCert().toString());
                getEthRawTransaction(threadLock,  resource.data, cardManager, rpcApiManager);
            }
        }));
        threadLock.waitAndRelease(15);
    }
}
