package com.example.temptool;

import android.app.Application;
import android.content.SharedPreferences;
import android.serialport.SerialPort;
import android.serialport.SerialPortFinder;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

public class MyApplication extends Application {

    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;

    public SerialPort getSerialPort() throws IOException {
        if (mSerialPort == null) {
            String packageName = getPackageName();
            SharedPreferences sp = getSharedPreferences(packageName + "_preferences", MODE_PRIVATE);
            String path = sp.getString("DEVICE", "/dev/ttyS1");
            int baudrate = Integer.decode(sp.getString("BAUDRATE", "115200"));

            /* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

            // 默认8N1(8数据位、无校验位、1停止位)
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
        }

        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
}
