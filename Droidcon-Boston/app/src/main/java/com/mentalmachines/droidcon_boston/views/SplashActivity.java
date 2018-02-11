package com.mentalmachines.droidcon_boston.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import com.mentalmachines.droidcon_boston.R;

public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Make Fullscreen
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.activity_splash);

    Handler handler = new Handler();
    handler.postDelayed(() -> {
      startActivity(new Intent(SplashActivity.this, MainActivity.class));
      overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
      finish();
    }, 1200);
  }
}
