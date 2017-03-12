package com.frowhy.hold;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.scalified.fab.FloatingActionButton;

@SuppressWarnings("ALL")
public class PpwButtonService extends Service {

    public static FloatingActionButton gFabContent;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private View mWindowView;
    private int mStartX, mStartY, mEndX, mEndY;
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
        mWindowView = LayoutInflater.from(getApplication()).inflate(R.layout.ppw_button, null);
        gFabContent = (FloatingActionButton) mWindowView.findViewById(R.id.fab_content);
    }

    private void addWindowView2Window() {
        mWindowManager.addView(mWindowView, mWindowParams);
    }

    private void initClick() {
        gFabContent.setOnTouchListener(new View.OnTouchListener() {
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

        gFabContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sPopMaskService = new Intent(getApplicationContext(), PopMaskService.class);
                startService(sPopMaskService);
                Toast.makeText(PpwButtonService.this, "保持,取消请按Home键", Toast.LENGTH_SHORT).show();
                gFabContent.setButtonColor(getResources().getColor(R.color.colorPrimary));
            }
        });

        gFabContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mIsTouch) {
                    Toast.makeText(PpwButtonService.this, "单击保持", Toast.LENGTH_SHORT).show();
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