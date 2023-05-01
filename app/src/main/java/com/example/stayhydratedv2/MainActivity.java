package com.example.stayhydratedv2;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BleScanner mBleScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mBleScanner = new BleScanner(this);
        mBleScanner.scanLeDevice(true, new BleScanner.BleScanCallback() {
            @Override
            public void onScanStarted() {
                Log.d(TAG, "Scan started");
            }

            @SuppressLint("MissingPermission")
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();
                Log.d(TAG, "Found device: " + device.getName() + " (" + device.getAddress() + ")");
                mBleScanner.scanLeDevice(false, this);
                mBleScanner.connectToDevice(device, new BleScanner.BleConnectCallback() {
                    @Override
                    public Context getContext() {
                        return MainActivity.this;
                    }

                    @Override
                    public void onConnecting() {
                        Log.d(TAG, "Connecting to device...");
                    }


                    @Override
                    public void onConnected(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                        Log.d(TAG, "Connected to device");
                    }

                    @Override
                    public void onDataReceived(BluetoothGattCharacteristic characteristic) {
                        byte[] data = characteristic.getValue();
                        // process data
                    }

                    @Override
                    public void onConnectFailed() {
                        Log.d(TAG, "Failed to connect to device");
                    }
                });
            }

            @Override
            public void onScanStopped() {
                Log.d(TAG, "Scan stopped");
            }

            @SuppressLint("MissingPermission")
            public void onBatchScanResults(List<ScanResult> results) {
                for (ScanResult result : results) {
                    Log.d(TAG, "Found device: " + result.getDevice().getName() + " (" + result.getDevice().getAddress() + ")");
                }
            }

            public void onScanFailed(int errorCode) {
                Log.d(TAG, "Scan failed with error code: " + errorCode);
            }
        });
    }

}