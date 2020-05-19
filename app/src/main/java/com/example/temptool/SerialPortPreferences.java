package com.example.temptool;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.serialport.SerialPortFinder;

/**
 * 串口配置
 */
public class SerialPortPreferences extends PreferenceActivity {

    private MyApplication application;
    private SerialPortFinder mSerialPortFinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (MyApplication) getApplication();
        mSerialPortFinder = application.mSerialPortFinder;

        addPreferencesFromResource(R.xml.serial_port_preferences);

        // 设备类型
        final ListPreference devices = (ListPreference) findPreference("DEVICE");
        String[] entries = mSerialPortFinder.getAllDevices();
        String[] entryValues = mSerialPortFinder.getAllDevicesPath();
        devices.setEntries(entries);
        devices.setEntryValues(entryValues);
        devices.setSummary(devices.getValue());
        devices.setOnPreferenceChangeListener((preference, newValue) -> {
            preference.setSummary((String) newValue);
            return true;
        });

        // 波特率
        final ListPreference baudrates = (ListPreference) findPreference("BAUDRATE");
        baudrates.setSummary(baudrates.getValue());
        baudrates.setOnPreferenceChangeListener((preference, newValue) -> {
            preference.setSummary((String) newValue);
            return true;
        });
    }
}
