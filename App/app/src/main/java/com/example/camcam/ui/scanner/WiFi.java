package com.example.camcam.ui.scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;

public class WiFi {

    Context appContext;
    WifiManager wifiManager;

    public interface WiFiReciever {
        public void handleSuccess(List<ScanResult> list);

        public void handleFailure();
    }

    public WiFiReciever reciever;


    public WiFi(Context appContext, WiFiReciever reciever) {
        this.wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        this.reciever = reciever;

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        appContext.registerReceiver(wifiScanReceiver, intentFilter);
    }

    public void scan() {
        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            scanFailure();
        }
    }

    public void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();

        this.reciever.handleSuccess(results);
    }

    public void scanFailure() {
        List<ScanResult> results = wifiManager.getScanResults();


        this.reciever.handleFailure();
    }



}