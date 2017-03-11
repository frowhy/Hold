package com.frowhy.hold;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import static android.os.PowerManager.SCREEN_BRIGHT_WAKE_LOCK;
import static com.frowhy.hold.PopButtonService.mFabOnce;

public class PopMaskService extends Service {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private View mWindowView;
    private HomeKeyEventBroadCastReceiver receiver;
    private PowerManager.WakeLock wakeLock = null;

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
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    private void initView() {
        receiver = new HomeKeyEventBroadCastReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        PowerManager powerManager = (PowerManager) this.getSystemService(Service.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(SCREEN_BRIGHT_WAKE_LOCK, "My Lock");
        wakeLock.acquire();
        mWindowView = LayoutInflater.from(getApplication()).inflate(R.layout.pop_mask, null);
    }

    private void addWindowView2Window() {
        mWindowManager.addView(mWindowView, mWindowParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
        unregisterReceiver(receiver);
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
        static final String SYSTEM_REASON = "reason";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (reason != null && reason.equals("homekey")) {
                    stopSelf();
                    mFabOnce.setButtonColor(getResources().getColor(R.color.colorPrimary));
                    Toast.makeText(context, "取消保持", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}