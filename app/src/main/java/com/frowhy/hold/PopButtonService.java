package com.frowhy.hold;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.scalified.fab.FloatingActionButton;

public class PopButtonService extends Service {

    public static FloatingActionButton mFabOnce;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private View mWindowView;
    private int mStartX;
    private int mStartY;
    private int mEndX;
    private int mEndY;
    private boolean mIsTouch;

    @Override
    public void onCreate() {
        super.onCreate();
        initWindowParams();
        initView();
        addWindowView2Window();
        initClick();
    }

    private void initWindowParams() {
        mWindowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    private void initView() {
        mWindowView = LayoutInflater.from(getApplication()).inflate(R.layout.pop_button, null);
        mFabOnce = (FloatingActionButton) mWindowView.findViewById(R.id.fab_once);
    }

    private void addWindowView2Window() {
        mWindowManager.addView(mWindowView, mWindowParams);
    }

    private void initClick() {
        mFabOnce.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartX = (int) motionEvent.getRawX();
                        mStartY = (int) motionEvent.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        mEndX = (int) motionEvent.getRawX();
                        mEndY = (int) motionEvent.getRawY();
                        if (needIntercept()) {
                            mIsTouch = true;
                            mWindowParams.x = (int) motionEvent.getRawX() - mWindowView.getMeasuredWidth() / 2;
                            mWindowParams.y = (int) motionEvent.getRawY() - mWindowView.getMeasuredHeight() / 2;
                            mWindowManager.updateViewLayout(mWindowView, mWindowParams);
                            return true;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (needIntercept()) {
                            mIsTouch = false;
                            return true;
                        }
                        break;

                    default:
                        break;
                }
                return false;
            }
        });

        mFabOnce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sPopMaskService = new Intent(getApplicationContext(), PopMaskService.class);
                startService(sPopMaskService);
                Toast.makeText(PopButtonService.this, "保持,取消请按Home键", Toast.LENGTH_SHORT).show();
                mFabOnce.setButtonColor(getResources().getColor(R.color.colorAccent));
            }
        });

        mFabOnce.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mIsTouch) {
                    Toast.makeText(PopButtonService.this, "单击保持", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private boolean needIntercept() {
        return Math.abs(mStartX - mEndX) > 2 || Math.abs(mStartY - mEndY) > 2;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWindowView != null) {
            mWindowManager.removeView(mWindowView);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}