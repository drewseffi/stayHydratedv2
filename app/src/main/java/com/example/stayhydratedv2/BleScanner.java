package com.example.stayhydratedv2;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.List;
import java.util.UUID;

public class BleScanner {
    private static final String TAG = "BleScanner";
    private static final long SCAN_PERIOD = 10000; // scan for 10 seconds
    private static final UUID SERVICE_UUID = UUID.fromString("01b1c65c-d88a-11ed-afa1-0242ac120002"); // Heart Rate Service UUID
    private static final UUID CHAR_UUID = UUID.fromString("0c00f0b4-d88b-11ed-afa1-0242ac120002"); // Heart Rate Measurement Characteristic UUID

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler = new Handler();

    public BleScanner(Context context) {
        BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public void scanLeDevice(final boolean enable, final BleScanCallback callback) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan(callback);
                }
            }, SCAN_PERIOD);

            startScan(callback);
        } else {
            stopScan(callback);
        }
    }

    @SuppressLint("MissingPermission")
    private void startScan(BleScanCallback callback) {
        mScanning = true;
        mBluetoothAdapter.getBluetoothLeScanner().startScan((ScanCallback) callback);
        callback.onScanStarted();
    }

    @SuppressLint("MissingPermission")
    private void stopScan(BleScanCallback callback) {
        mScanning = false;
        mBluetoothAdapter.getBluetoothLeScanner().stopScan((ScanCallback) callback);
        callback.onScanStopped();
    }

    public void connectToDevice(BluetoothDevice device, BleConnectCallback callback) {
        BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    callback.onConnectFailed();
                }
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    BluetoothGattService service = gatt.getService(SERVICE_UUID);
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHAR_UUID);
                    gatt.readCharacteristic(characteristic);
                    callback.onConnected(gatt, characteristic);
                } else {
                    callback.onConnectFailed();
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    callback.onDataReceived(characteristic);
                }
            }
        };

        @SuppressLint("MissingPermission") BluetoothGatt gatt = device.connectGatt(callback.getContext(), false, gattCallback);
    }

    public interface BleScanCallback {
        void onScanStarted();
        void onScanStopped();
    }


    public interface BleConnectCallback {
        Context getContext();
        void onConnecting();
        void onConnected(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
        void onDataReceived(BluetoothGattCharacteristic characteristic);
        void onConnectFailed();
    }
}

