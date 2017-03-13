package com.frowhy.hold;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import static com.frowhy.hold.PpwButtonService.gFabContent;

public class PpwMaskService extends Service {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private View mWindowView;
    private HomeKeyEventBroadCastReceiver mReceiver;
    private PowerManager.WakeLock mWakeLock = null;

    @Override
    public void onCreate() {
        super.onCreate();
        initWindowParams();
        initView();
        addWindowView2Window();
    }

    private void initWindowParams() {
        mWindowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    @SuppressLint("InflateParams")
    private void initView() {
        mReceiver = new HomeKeyEventBroadCastReceiver();
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        PowerManager powerManager = (PowerManager) this.getSystemService(Service.POWER_SERVICE);
        //noinspection deprecation
        mWakeLock = powerManager.newWakeLock(android.os.PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Lock");
        mWakeLock.acquire();
        mWindowView = LayoutInflater.from(getApplication()).inflate(R.layout.ppw_mask, null);
    }

    private void addWindowView2Window() {
        mWindowManager.addView(mWindowView, mWindowParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
        unregisterReceiver(mReceiver);
        if (mWindowView != null) {
            mWindowManager.removeView(mWindowView);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                stopSelf();
                gFabContent.setAlpha(1);
                gFabContent.setButtonColor(getResources().getColor(R.color.colorAccent));
            }
        }
    }
}