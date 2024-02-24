package com.demo.carservicetracker2.backupRestore;

import android.app.Activity;
import android.app.Dialog;
import android.widget.TextView;
import com.demo.carservicetracker2.R;
import java.lang.ref.WeakReference;


public class BackupRestoreProgress {
    Activity activity;
    boolean isShowing = false;
    private Dialog progressDialog;
    TextView textView;
    WeakReference<Activity> weakReference;

    public BackupRestoreProgress(Activity activity) {
        this.progressDialog = null;
        this.progressDialog = new Dialog(activity);
        this.activity = activity;
        this.weakReference = new WeakReference<>(activity);
        this.progressDialog.setContentView(R.layout.bacup_restore_progress_dialog);
        this.textView = (TextView) this.progressDialog.findViewById(R.id.message);
        this.progressDialog.setCancelable(false);
    }

    public void showDialog() {
        Dialog dialog = this.progressDialog;
        if (dialog == null || this.isShowing) {
            return;
        }
        dialog.show();
        this.isShowing = true;
    }

    public void dismissDialog() {
        Dialog dialog = this.progressDialog;
        if (dialog == null || !dialog.isShowing() || this.weakReference.get() == null) {
            return;
        }
        this.progressDialog.dismiss();
        this.isShowing = false;
    }

    public void setMessage(String message) {
        this.textView.setText(message);
    }

    public boolean isShowing() {
        return this.isShowing;
    }
}
