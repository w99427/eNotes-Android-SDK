package io.enotes.examples;

import android.util.Log;

import io.enotes.sdk.constant.Status;
import io.enotes.sdk.repository.base.Resource;

import static org.junit.Assert.*;

public class ThreadLock {
    private static final String TAG = "AndroidTest";
    private Object lock;
    private boolean flag;

    public ThreadLock(Object lock) {
        this.lock = lock;
    }

    public void assertResource(Resource resource) {
        Log.i(TAG,"status = "+ resource.status+" @ msg = "+resource.message);
        assertTrue(resource.status != Status.ERROR);
        if(resource.status == Status.SUCCESS) {
            flag = true;
        }
    }

    public void waitAndRelease(long second) {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        new Thread(() -> {
            try {
                Thread.sleep(second*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.notifyAll();
            if (!flag) {
                assertTrue(false);
            }
        }).start();
    }

    public void notifyLock(){
        synchronized (lock){
            lock.notifyAll();
        }
    }
}
