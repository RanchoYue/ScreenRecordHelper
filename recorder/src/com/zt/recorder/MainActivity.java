package com.zt.recorder;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static com.zt.recorder.ScreenRecordHelper.RECORD_REQUEST_CODE;


public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.stop_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ScreenRecordHelper.getInstance().stopRecord();
            }
        });
        findViewById(R.id.start_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ScreenRecordHelper.getInstance().startRecord();
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
            ScreenRecordHelper.getInstance().onActivityResult(requestCode, resultCode, data);
        }
    }

}
