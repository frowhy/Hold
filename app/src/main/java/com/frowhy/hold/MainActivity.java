package com.frowhy.hold;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ezy.assist.compat.SettingsCompat;

public class MainActivity extends AppCompatActivity {

    private Intent sPopButtonService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sPopButtonService = new Intent(this, PopButtonService.class);

        Button mBtnOpenPopWindow = (Button) findViewById(R.id.btn_open_pop_window);
        mBtnOpenPopWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SettingsCompat.canDrawOverlays(MainActivity.this)) {
                    if (!SettingsCompat.setDrawOverlays(MainActivity.this, true)) {
                        SettingsCompat.manageDrawOverlays(MainActivity.this);
                    } else {
                        openPopWindow();
                    }
                } else {
                    openPopWindow();
                }
            }
        });
    }

    private void openPopWindow() {
        startService(sPopButtonService);
    }
}
