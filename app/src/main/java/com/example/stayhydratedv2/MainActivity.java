package com.example.stayhydratedv2;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //private BleScanner mBleScanner;
    int level = 1;
    int xp = 0;
    int xpCap = 100;
    int drankTimes = 0;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 5000;
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txtLevel = findViewById(R.id.txtLevel);
        TextView txtXp = findViewById(R.id.txtXP);
        TextView txtDrank = findViewById(R.id.txtDrankTimes);
        TextView txtTimer = findViewById(R.id.txtDrinkTimer);

        Button drankButton = findViewById(R.id.btnDrank);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        txtLevel.setText("Level: " + level);
        txtXp.setText("XP: " + xp);

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished; // Update the time left
                txtTimer.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                txtTimer.setText("Drink water!");

                String channelId = "drink";
                String channelName = "drinkWater";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "drink")
                        .setSmallIcon(R.drawable.water_drop)
                        .setContentTitle("Time to drink water!")
                        .setContentText("You have completed the timer. It's time to drink water.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        };

        drankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationManager.cancel(NOTIFICATION_ID);
                xp += 10;
                drankTimes += 1;
                if (xp == xpCap) {
                    level += 1;
                    xpCap += 50;
                    xp = 0;
                }
                txtLevel.setText("Level: " + level);
                txtXp.setText("XP: " + xp);
                txtDrank.setText("Drank " + drankTimes + " times!");

                // Reset the timer
                countDownTimer.cancel();
                timeLeftInMillis = 30000;
                countDownTimer.start();
            }
        });


        /*mBleScanner = new BleScanner(this);
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
        });*/
    }

}