package com.example.temptool;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import com.common.pos.api.util.PosUtil;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 红外
 */
public class ProximityService extends Service {

    private Timer mTimer = null;
    private TimerTask mTask = null;
    private static final String TAG = "ProximityService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler showHandler = new Handler(Looper.getMainLooper());

    private void show(final String message) {
        showHandler.post(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mTask == null) {
            mTask = new TimerTask() {
                @Override
                public void run() {
                    int ret = PosUtil.getPriximitySensorStatus();
                    Message message = mHandler.obtainMessage(ret);
                    mHandler.sendMessage(message);
                }
            };
        }
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(mTask, 0, 300);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Const.SERIAL_IS_RECEIVE = true;
                    PosUtil.setLedPower(1);
                    break;
                case 0:
                    Const.SERIAL_IS_RECEIVE = false;
                    PosUtil.setLedPower(0);
                    break;
                default:
                    show("error");
                    break;
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mTask = null;
        mTimer = null;
    }
}
