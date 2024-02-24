package com.demo.carservicetracker2.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.databinding.ActivitySplashBinding;
import com.demo.carservicetracker2.database.AppDatabase;
import com.demo.carservicetracker2.utils.AppPref;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;
    Context context;
    SplashActivity splash_activity;
    AppDatabase appDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = (ActivitySplashBinding) DataBindingUtil.setContentView(this, R.layout.activity_splash);
        this.appDatabase = AppDatabase.getAppDatabase(this);
        this.context = this;
        this.splash_activity = this;
        if (Build.VERSION.SDK_INT >= 33) {
            launcher.launch(require_permistion[0]);
        }else {
            new Handler(Looper.getMainLooper()).postDelayed(new runnable(), 500L);
        }
    }

    public class runnable implements Runnable {
        @Override
        public void run() {
            GoToMainScreen();
        }
    }

    public void GoToMainScreen() {
        if (!AppPref.IsTermsAccept()) {
            startActivity(new Intent(this, DisclouserActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        finish();
    }

    
















    private static final String[] require_permistion = new String[]{
            Manifest.permission.READ_MEDIA_IMAGES
    };

    ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            new Handler(Looper.getMainLooper()).postDelayed(new runnable(), 500L);
        } else {
            loanch();
            Toast.makeText(this, "Please allow permission !", Toast.LENGTH_SHORT).show();
        }
    });

    private void loanch() {
        launcher.launch(require_permistion[0]);
    }

}
