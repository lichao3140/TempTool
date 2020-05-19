package com.example.temptool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.common.pos.api.util.PosUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends SerialPortActivity implements RadioGroup.OnCheckedChangeListener{

    public Context mContext;

    private Intent intentService;
    private UIThread uithread;

    private EditText mReception;
    private RadioGroup rg_select_type;
    private int select_type = 1;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case Const.UPDATE_UI://更新UI
                    if (Const.SERIAL_IS_RECEIVE) { // 有人查询
                        if (select_type == 1) {
                            // 查询温度
                            sendTempMessage(Const.SEND_QUERY_TEMPERATURE);
                        } else if (select_type == 2) {
                            // 查询版本号
                            sendTempMessage(Const.GET_SEND_VERSION);
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        //接收到串口信息
        byte[] bRec = new byte[size];
        for (int i = 0; i < size; i++) {
            bRec[i] = buffer[i];
        }
        StringBuilder sMsg = new StringBuilder();
        sMsg.append(SerialUtil.ByteArrToHex(bRec));
        //Log.i("lichao", "sMsg:" + sMsg);


        if (sMsg.toString().replace(" ", "").length() == 18) {
            String temp = sMsg.toString().replace(" ", "");
            if (temp.substring(0, 2).equals("08") && temp.substring(2, 4).equals("00")) {
                String TO = temp.substring(6, 8) + temp.substring(4, 6);
                String TA = temp.substring(10, 12) + temp.substring(8, 10);

                double to = Integer.parseInt(TO, 16) / 100.00;
                double ta = Integer.parseInt(TA, 16) / 100.00;

                Log.i("lichao","to=" + to + " ta=" + ta);
                runOnUiThread(() -> {
                    // 显示模块温度
                    mReception.append("--->物体温度=" + to  + "℃--->环境温度=" + ta + "℃\n");
                });
            } else if (temp.substring(0, 2).equals("A1") && temp.substring(2, 4).equals("00")) {
                // 版本号数据
                String str_v6 = temp.substring(10, 12);
                String str_v5 = temp.substring(8, 10);
                String str_v4 = temp.substring(6, 8);
                String str_v3 = temp.substring(4, 6);
                String temperature_version = Integer.parseInt(str_v6, 16) + "." +
                        Integer.parseInt(str_v5, 16) + "." +
                        Integer.parseInt(str_v4, 16) + "." +
                        Integer.parseInt(str_v3, 16);

                Log.i("lichao","version=" + temperature_version);
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 显示接收数据
                    mReception.append("--->接收数据=" + sMsg + "\n");
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        initView();

        intentService = new Intent(mContext, ProximityService.class);
        startService(intentService);
    }

    private void initView() {
        mReception = findViewById(R.id.et_show_temperature);
        rg_select_type = findViewById(R.id.rg_select_type);
        rg_select_type.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (uithread == null) {
            uithread = new UIThread();
            uithread.start();
        }
    }

    /**
     * 信息发送-收发模式
     * @param message
     */
    private void sendTempMessage(String message) {
        PosUtil.setRs485Status(1, 5); // 发送模式
        sendMessageDelay(5);
        replayMsg(message);
    }

    /**
     * 机器延迟发送信息
     * @param time
     */
    private void sendMessageDelay(int time) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                PosUtil.setRs485Status(0, 10); //接收模式
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, time);//time毫秒后执行TimeTask的run方法
    }

    /**
     * 485串口信息发送
     * @param send_str
     */
    private void replayMsg(String send_str) {
        byte[] bOutArray = SerialUtil.toByteArray(send_str);
        try {
            mOutputStream.write(bOutArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_select_temp:
                select_type = 1;
                break;
            case R.id.rb_select_version:
                select_type = 2;
                break;
        }
    }

    /**
     * 更新UI标志线程
     */
    private class UIThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = Const.UPDATE_UI;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
