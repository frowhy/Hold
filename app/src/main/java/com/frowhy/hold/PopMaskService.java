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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import static android.os.PowerManager.SCREEN_BRIGHT_WAKE_LOCK;
import static com.frowhy.hold.PpwButtonService.gFabContent;

public class PopMaskService extends Service {

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

    private void initView() {
        mReceiver = new HomeKeyEventBroadCastReceiver();
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        PowerManager powerManager = (PowerManager) this.getSystemService(Service.POWER_SERVICE);
        //noinspection deprecation
        mWakeLock = powerManager.newWakeLock(SCREEN_BRIGHT_WAKE_LOCK, "My Lock");
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
        static final String SYSTEM_REASON = "reason";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (reason != null && reason.equals("homekey")) {
                    stopSelf();
                    gFabContent.setButtonColor(getResources().getColor(R.color.colorAccent));
                    Toast.makeText(context, "取消保持", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}