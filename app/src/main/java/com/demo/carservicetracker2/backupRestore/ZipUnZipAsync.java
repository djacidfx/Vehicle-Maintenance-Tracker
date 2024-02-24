package com.demo.carservicetracker2.backupRestore;

import android.app.Activity;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.utils.AppConstants;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;


public class ZipUnZipAsync {
    Activity activity;
    BackupRestoreProgress dialog;
    ArrayList<File> fileListForZip;
    String fileToRestore;
    boolean isZip;
    OnBackupRestore onBackupRestore;
    String tempZipFilePath;
    WeakReference<Activity> weakReference;
    String pass = "abc";
    CompositeDisposable disposable = new CompositeDisposable();

    public ZipUnZipAsync(final BackupRestoreProgress dialog, Activity activity, final boolean isZip, final ArrayList<File> fileListForZip, final String fileToRestore, String tempPath, final OnBackupRestore onBackupRestore) {
        this.weakReference = new WeakReference<>(activity);
        this.activity = activity;
        this.isZip = isZip;
        this.dialog = dialog;
        this.tempZipFilePath = tempPath;
        this.fileToRestore = fileToRestore;
        this.fileListForZip = fileListForZip;
        this.onBackupRestore = onBackupRestore;
        dialog.setMessage(isZip ? "Creating Zip..." : "Extracting Zip...");
        dialog.showDialog();
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception {
                return ZipUnZipAsync.this.m181xa675b3fb(isZip, fileListForZip, fileToRestore);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                ZipUnZipAsync.this.m182x3ab4239a(dialog, onBackupRestore, (Boolean) obj);
            }
        }));
    }

    
    
    public  Boolean m181xa675b3fb(boolean z, ArrayList arrayList, String str) throws Exception {
        if (z) {
            return Boolean.valueOf(encryptedZip(arrayList, this.tempZipFilePath));
        }
        return Boolean.valueOf(decryptedZip(str));
    }

    
    
    public  void m182x3ab4239a(BackupRestoreProgress backupRestoreProgress, OnBackupRestore onBackupRestore, Boolean bool) throws Exception {
        try {
            backupRestoreProgress.dismissDialog();
            onBackupRestore.onSuccess(true);
            this.disposable.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean encryptedZip(ArrayList<File> fileListForZip, String destPath) {
        try {
            ZipFile zipFile = new ZipFile(destPath);
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionMethod(8);
            zipParameters.setCompressionLevel(5);
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(99);
            zipParameters.setAesKeyStrength(3);
            zipParameters.setPassword(this.pass);
            for (int i = 0; i < fileListForZip.size(); i++) {
                if (!fileListForZip.get(i).isDirectory()) {
                    zipFile.addFile(fileListForZip.get(i), zipParameters);
                } else if (fileListForZip.get(i).listFiles().length > 0) {
                    zipFile.addFolder(fileListForZip.get(i), zipParameters);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean decryptedZip(String _zipFile) {
        AppConstants.getRootPath(this.activity);
        try {
            ZipFile zipFile = new ZipFile(_zipFile);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(this.pass);
            }
            zipFile.extractAll(AppConstants.getTempDirectory(this.activity));
            File file = new File(AppConstants.getTempDirectory(this.activity) + File.separator + this.activity.getString(R.string.app_name));
            if (file.exists()) {
                FileUtils.copyDirectoryToDirectory(file, new File(AppConstants.getDatabasePath(this.activity)));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private boolean dirChecker(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.exists();
    }
}
