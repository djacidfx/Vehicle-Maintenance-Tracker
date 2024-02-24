package com.demo.carservicetracker2.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.backupRestore.BackupRestore;
import com.demo.carservicetracker2.backupRestore.BackupRestoreProgress;
import com.demo.carservicetracker2.backupRestore.OnBackupRestore;
import com.demo.carservicetracker2.backupRestore.OnRecyclerItemClick;
import com.demo.carservicetracker2.backupRestore.RestoreAdapter;
import com.demo.carservicetracker2.backupRestore.RestoreListModel;
import com.demo.carservicetracker2.backupRestore.RestoreRowModel;
import com.demo.carservicetracker2.databinding.ActivityRestoreDriveListBinding;
import com.demo.carservicetracker2.databinding.DialogConfRestoreBinding;
import com.demo.carservicetracker2.databinding.DialogDeleteBinding;
import com.demo.carservicetracker2.utils.AppConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class RestoreDriveList extends AppCompatActivity {
    private BackupRestore backupRestore;
    boolean backupSuccess = false;
    ActivityRestoreDriveListBinding binding;
    Context context;
    private boolean isDesc;
    private boolean isResultOK;
    private RestoreListModel model;
    private BackupRestoreProgress progressDialog;

    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = (ActivityRestoreDriveListBinding) DataBindingUtil.setContentView(this, R.layout.activity_restore_drive_list);
        RestoreListModel restoreListModel = new RestoreListModel();
        this.model = restoreListModel;
        this.context = this;
        restoreListModel.setArrayList(new ArrayList<>());
        this.model.setNoDataText("No Backup Found");
        this.model.setNoDataDetail("Please backup your data to restore");
        this.binding.setModel(this.model);
        this.backupRestore = new BackupRestore(this);
        this.progressDialog = new BackupRestoreProgress(this);
        setToolbar();
        setRecycler();
        fillData();
        this.binding.back.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                RestoreDriveList.this.onBackPressed();
            }
        });
    }

    private void setToolbar() {
        this.binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                RestoreDriveList.this.onBackPressed();
            }
        });
        this.binding.sort.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                RestoreDriveList restoreDriveList = RestoreDriveList.this;
                restoreDriveList.isDesc = !restoreDriveList.isDesc;
                RestoreDriveList.this.shortList();
            }
        });
    }

    protected void fillData() {
        getDriveBackupList();
    }

    private void getDriveBackupList() {
        this.backupRestore.driveBackupList(this.progressDialog, new OnBackupRestore() {
            @Override 
            public void onSuccess(boolean isSuccess) {
            }

            @Override 
            public void getList(ArrayList<RestoreRowModel> list) {
                RestoreDriveList.this.model.getArrayList().addAll(list);
                RestoreDriveList.this.notifyAdapter();
            }
        });
    }

    
    public void shortList() {
        Collections.sort(this.model.getArrayList(), new Comparator<RestoreRowModel>() { 
            @Override 
            public int compare(RestoreRowModel o1, RestoreRowModel o2) {
                if (RestoreDriveList.this.isDesc) {
                    return Long.compare(o1.getTimestamp().longValue(), o2.getTimestamp().longValue());
                }
                return Long.compare(o2.getTimestamp().longValue(), o1.getTimestamp().longValue());
            }
        });
        notifyAdapter();
    }

    private void setRecycler() {
        this.binding.recycler.setLayoutManager(new LinearLayoutManager(this.context));
        this.binding.recycler.setAdapter(new RestoreAdapter(this.context, this.model.getArrayList(), new OnRecyclerItemClick() {
            @Override 
            public void onClick(int position, int type) {
                if (type == 2) {
                    RestoreDriveList.this.deleteItem(position);
                } else {
                    RestoreDriveList.this.restoreItem(position);
                }
            }
        }));
    }

    public void deleteItem(final int position) {
        DialogDeleteBinding dialogDeleteBinding = (DialogDeleteBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_delete, null, false);
        final Dialog dialog = new Dialog(this, R.style.dialogTheme);
        dialog.setContentView(dialogDeleteBinding.getRoot());
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(-1, -2);
            dialog.getWindow().setBackgroundDrawableResource(17170445);
        }
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialogDeleteBinding.btnDelete.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                RestoreDriveList.this.deleteFile(position);
                dialog.dismiss();
            }
        });
        dialogDeleteBinding.btnCancel.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    
    public void deleteFile(final int position) {
        this.backupRestore.deleteFromDrive(this.progressDialog, this.model.getArrayList().get(position).getPath(), new OnBackupRestore() { 
            @Override 
            public void getList(ArrayList<RestoreRowModel> list) {
            }

            @Override 
            public void onSuccess(boolean isSuccess) {
                if (isSuccess) {
                    RestoreDriveList.this.model.getArrayList().remove(position);
                    RestoreDriveList.this.binding.recycler.getAdapter().notifyItemRemoved(position);
                    Toast.makeText(RestoreDriveList.this.getApplicationContext(), "File deleted", Toast.LENGTH_SHORT).show();
                    RestoreDriveList.this.notifyAdapter();
                    return;
                }
                Toast.makeText(RestoreDriveList.this.getApplicationContext(), "Unable to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void restoreItem(final int position) {
        DialogConfRestoreBinding dialogConfRestoreBinding = (DialogConfRestoreBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_conf_restore, null, false);
        final Dialog dialog = new Dialog(this, R.style.dialogTheme);
        dialog.setContentView(dialogConfRestoreBinding.getRoot());
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(-1, -2);
            dialog.getWindow().setBackgroundDrawableResource(17170445);
        }
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialogConfRestoreBinding.llMarge.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                RestoreDriveList.this.isResultOK = true;
                RestoreDriveList restoreDriveList = RestoreDriveList.this;
                restoreDriveList.backupData(restoreDriveList.model.getArrayList().get(position).getPath(), true);
                dialog.dismiss();
            }
        });
        dialogConfRestoreBinding.lldelete.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                RestoreDriveList.this.isResultOK = true;
                RestoreDriveList restoreDriveList = RestoreDriveList.this;
                restoreDriveList.backupData(restoreDriveList.model.getArrayList().get(position).getPath(), false);
                dialog.dismiss();
            }
        });
    }

    
    public void backupData(String path, boolean isMerge) {
        this.backupRestore.backupRestore(this.progressDialog, false, false, path, isMerge, new OnBackupRestore() { 
            @Override 
            public void getList(ArrayList<RestoreRowModel> list) {
            }
            @Override 
            public void onSuccess(boolean isSuccess) {
                if (isSuccess) {
                    AppConstants.deleteTempFile(RestoreDriveList.this);
                    RestoreDriveList.this.backupSuccess = true;
                    Toast.makeText(RestoreDriveList.this.getApplicationContext(), "Import Successfully", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(RestoreDriveList.this.getApplicationContext(), "Failed to import", Toast.LENGTH_SHORT).show();
            }
        });
    }

    
    public void notifyAdapter() {
        setViewVisibility();
        if (this.binding.recycler.getAdapter() != null) {
            this.binding.recycler.getAdapter().notifyDataSetChanged();
        }
    }

    private void setViewVisibility() {
        this.binding.linData.setVisibility(this.model.isListData() ? View.VISIBLE : View.GONE);
        this.binding.linNoData.setVisibility(this.model.isListData() ? View.GONE : View.VISIBLE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_SIGN_IN) {
            handleSignIn(data);
        }
    }

    private void handleSignIn(Intent data) {
        this.backupRestore.handleSignInResult(data, true, true, null, this.progressDialog, new OnBackupRestore() { 
            @Override 
            public void onSuccess(boolean isSuccess) {
            }

            @Override 
            public void getList(ArrayList<RestoreRowModel> list) {
                RestoreDriveList.this.model.getArrayList().addAll(list);
                RestoreDriveList.this.notifyAdapter();
            }
        });
    }

    @Override 
    public void onBackPressed() {
        if (this.backupSuccess) {
            Intent intent = getIntent();
            intent.putExtra("backupScuccess", this.backupSuccess);
            setResult(-1, intent);
        }
        finish();
    }
}
