package com.sourcebits.footin;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity
{

    private Boolean mFlag = true;
    private Handler mHandler;
    private Runnable mRunnableWaitingThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splashactivity);

        mRunnableWaitingThread = new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    if (mFlag) {
                        Intent intent = new Intent(getBaseContext(),LocationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();

                }
            }
        };

        mHandler = new Handler();
        mHandler.postDelayed(mRunnableWaitingThread,3000 );


    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mHandler.removeCallbacks(mRunnableWaitingThread);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mHandler.postDelayed(mRunnableWaitingThread , 3000);
    }

}
