package com.demo.carservicetracker2.activities;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.demo.carservicetracker2.AdAdmob;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.database.AppDatabase;
import com.demo.carservicetracker2.database.models.CarModel;
import com.demo.carservicetracker2.databinding.ActivityDisclouserBinding;
import com.demo.carservicetracker2.utils.AppConstants;
import com.demo.carservicetracker2.utils.AppPref;
public class DisclouserActivity extends AppCompatActivity implements View.OnClickListener {
    AppDatabase appDatabase;
    ActivityDisclouserBinding binding;

    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = (ActivityDisclouserBinding) DataBindingUtil.setContentView(this, R.layout.activity_disclouser);
        this.appDatabase = AppDatabase.getAppDatabase(this);
        Clicks();



    }

    private void Clicks() {
        this.binding.userAgreement.setOnClickListener(this);
        this.binding.privacyPolicy.setOnClickListener(this);
        this.binding.termsServices.setOnClickListener(this);
        this.binding.acceptContinue.setOnClickListener(this);
    }

    @Override 
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.acceptContinue :
                AppPref.setIsTermsAccept(true);
                goToMainScreen();
                return;
            case R.id.privacyPolicy :
                AppConstants.openUrl(this, AppConstants.PRIVACY_POLICY_URL);
                return;
            case R.id.termsServices :
                AppConstants.openUrl(this, AppConstants.TERMS_OF_SERVICE_URL);
                return;
            case R.id.userAgreement :
                agreeAndContinueDialog();
                return;
            default:
                return;
        }
    }

    public void agreeAndContinueDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(AppConstants.DISCLOSURE_DIALOG_DESC);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() { 
            @Override 
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.show();
    }

    private void goToMainScreen() {
        try {
            if (AppPref.IsTermsAccept()) {
                this.appDatabase.carDao().InsertCar(new CarModel(AppConstants.DEFAULT_CAR_ID, "", "My Car", 0L, "", 15000L, 12, 15000L, 12, "", AppConstants.GetOnlyDateInMillis(System.currentTimeMillis())));
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MYTAG", "ErrorNo: goToMainScreen:" +e);
        }
    }
}
