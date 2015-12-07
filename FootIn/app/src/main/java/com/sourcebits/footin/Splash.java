package com.sourcebits.footin;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends Activity
{

    Boolean flag = true;
    Handler handler;
    Runnable thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash);

        thread = new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    if (flag) {
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

        handler = new Handler();
        handler.postDelayed(thread,5000 );


		/*Thread thread = new Thread() {
			public void run() {
				try {
					if (flag) {
						sleep(3000);
						Intent intent = new Intent(getBaseContext(),
								ActivityActivity.class);
						startActivity(intent);
						finish();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();*/



    }

    @Override
    protected void onPause()
    {
        super.onPause();
        flag = false;
        handler.removeCallbacks(thread);
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        flag =true;
        handler.postDelayed(thread, 1000);
    }

}
