package io.enotes.examples;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.enotes.examples.test.TestNfcActivity;
import io.enotes.sdk.constant.Constant;
import io.enotes.sdk.constant.Status;
import io.enotes.sdk.core.CardManager;
import io.enotes.sdk.core.ENotesSDK;
import io.enotes.sdk.core.RPCApiManager;
import io.enotes.sdk.repository.card.CommandException;
import io.enotes.sdk.repository.db.entity.Card;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class NfcTest extends BaseTest {
    private static final String TAG = "NfcTest";
    @Rule
    public ActivityTestRule<TestNfcActivity> activityTestRule = new ActivityTestRule<>(TestNfcActivity.class);
    private TestNfcActivity activity;

    @Before
    public void setup() {
        activity = activityTestRule.getActivity();
        ENotesSDK.config.debugCard=true;
    }

    @Test
    public void testReadCard() {
        Object lock = new Object();
        ThreadLock threadLock = new ThreadLock(lock);
        CardManager cardManager = activity.getCardManager();
        assertNotNull(cardManager);
        cardManager.setReadCardCallback((resource -> {
            threadLock.assertResource(resource);
            if (resource.status == Status.SUCCESS) {
                assertTrue(resource.data instanceof Card);
                Log.i(TAG, "card cert = " + resource.data.getCert().toString());
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
        cardManager.setReadCardCallback((resource -> {
            if (resource.status == Status.SUCCESS) {
                assertTrue(resource.data instanceof Card);
                Log.i(TAG, "card cert = " + resource.data.getCert().toString());
                getBtcRawTransaction(threadLock, resource.data, cardManager, rpcApiManager);
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
        cardManager.setReadCardCallback((resource -> {
            if (resource.status == Status.SUCCESS) {
                assertTrue(resource.data instanceof Card);
                Log.i(TAG, "card cert = " + resource.data.getCert().toString());
                getEthRawTransaction(threadLock, resource.data, cardManager, rpcApiManager);
            }
        }));
        threadLock.waitAndRelease(15);
    }

    @Test
    public void testXrpRawTransaction() {
        Object lock = new Object();
        ThreadLock threadLock = new ThreadLock(lock);
        CardManager cardManager = activity.getCardManager();
        assertNotNull(cardManager);
        cardManager.setReadCardCallback((resource -> {
            if (resource.status == Status.SUCCESS) {
                assertTrue(resource.data instanceof Card);
                resource.data.getCert().setBlockChain(Constant.BlockChain.RIPPLE);
                Log.i(TAG, "card cert = " + resource.data.getCert().toString());
                cardManager.getXrpRawTransaction(resource.data,"rLeQtpFZC4PizpR8y1gEhGEPp4MAz1gzk9","100000",1,"100",(resource1 -> {
                    String hex = resource1.data;
                    Log.i(TAG,"xrp raw transaction hex = "+hex);
                    threadLock.notifyLock();
                }));
            }
        }));
        threadLock.waitAndRelease(15);
    }

    @Test
    public void testFreezeStatus() {
        Object lock = new Object();
        ThreadLock threadLock = new ThreadLock(lock);
        CardManager cardManager = activity.getCardManager();
        assertNotNull(cardManager);
        cardManager.setReadCardCallback((resource -> {
            if (resource.status == Status.SUCCESS) {
                assertTrue(resource.data instanceof Card);
                Log.i(TAG, "card cert = " + resource.data.getCert().toString());
                try {

                    int freezeStatus = cardManager.getFreezeStatus();
                    Log.i(TAG, "freezeStatus = " + freezeStatus);
                    threadLock.notifyLock();
                } catch (CommandException e) {
                    e.printStackTrace();
                    assert false;
                }
            }
        }));

        threadLock.waitAndRelease(15);
    }

    @Test
    public void testFreezeTries() {
        Object lock = new Object();
        ThreadLock threadLock = new ThreadLock(lock);
        CardManager cardManager = activity.getCardManager();
        assertNotNull(cardManager);
        cardManager.setReadCardCallback((resource -> {
            if (resource.status == Status.SUCCESS) {
                assertTrue(resource.data instanceof Card);
                Log.i(TAG, "card cert = " + resource.data.getCert().toString());
                try {

                    int getUnFreezeTries = cardManager.getUnFreezeTries();
                    Log.i(TAG, "getUnFreezeTries = " + getUnFreezeTries);
                    threadLock.notifyLock();
                } catch (CommandException e) {
                    e.printStackTrace();
                    assert false;
                }
            }
        }));

        threadLock.waitAndRelease(15);
    }

    @Test
    public void testFreezeTX() {
        Object lock = new Object();
        ThreadLock threadLock = new ThreadLock(lock);
        CardManager cardManager = activity.getCardManager();
        assertNotNull(cardManager);
        cardManager.setReadCardCallback((resource -> {
            if (resource.status == Status.SUCCESS) {
                assertTrue(resource.data instanceof Card);
                Log.i(TAG, "card cert = " + resource.data.getCert().toString());
                try {

                    boolean flag = cardManager.freezeTransaction("123456");
                    assert flag;
                    Log.i(TAG, "freezeTransaction = " + flag);
                    threadLock.notifyLock();
                } catch (CommandException e) {
                    e.printStackTrace();
                    assert false;
                }
            }
        }));

        threadLock.waitAndRelease(15);
    }

    @Test
    public void testUnFreezeTX() {
        Object lock = new Object();
        ThreadLock threadLock = new ThreadLock(lock);
        CardManager cardManager = activity.getCardManager();
        assertNotNull(cardManager);
        cardManager.setReadCardCallback((resource -> {
            if (resource.status == Status.SUCCESS) {
                assertTrue(resource.data instanceof Card);
                Log.i(TAG, "card cert = " + resource.data.getCert().toString());
                try {

                    boolean flag = cardManager.unFreezeTransaction("123456");
                    assert flag;
                    Log.i(TAG, "unFreezeTransaction = " + flag);
                    threadLock.notifyLock();
                } catch (CommandException e) {
                    e.printStackTrace();
                    assert false;
                }
            }
        }));

        threadLock.waitAndRelease(15);
    }
}
