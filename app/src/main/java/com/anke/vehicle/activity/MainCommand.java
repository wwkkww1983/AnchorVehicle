package com.anke.vehicle.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.anke.vehicle.R;

public class MainCommand extends Activity {
    private Button btStop;
    public static Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anchormain);
        btStop = (Button) findViewById(R.id.btstop);
        btStop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (myHandler != null)
                    myHandler.sendEmptyMessage(1);
            }
        });
    }

}
