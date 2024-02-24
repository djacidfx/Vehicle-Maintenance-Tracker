package com.demo.carservicetracker2.backupRestore;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.util.Log;

import androidx.sqlite.db.SupportSQLiteDatabase;

import com.demo.carservicetracker2.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.demo.carservicetracker2.database.AppDatabase;
import com.demo.carservicetracker2.utils.AppConstants;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;


public class BackupRestore {

    private Activity activity;
    private Drive driveService;
    File file;
    File fileMetadata;
    java.io.File filePath;
    FileContent mediaContent;
    String METADATA_FILE_TYPE = "application/zip";
    String METADATA_FILE_PARENT = "appDataFolder";
    boolean isSuccessCreate = true;
    OutputStream outputStream = null;
    boolean isSuccessDelete = true;
    CompositeDisposable disposable = new CompositeDisposable();
    FileList fileList = null;

    public BackupRestore(Activity activity) {
        this.activity = activity;
    }

    public static void deleteAllTableData(SupportSQLiteDatabase dbSql) {
        dbSql.execSQL("DELETE FROM CarModel");
        dbSql.execSQL("DELETE FROM dbVersion");
        dbSql.execSQL("DELETE FROM DocumentModel");
        dbSql.execSQL("DELETE FROM FuelModel");
        dbSql.execSQL("DELETE FROM ImageModel");
        dbSql.execSQL("DELETE FROM ReminderModel");
        dbSql.execSQL("DELETE FROM ServiceModel");
    }

    public void backupRestore(BackupRestoreProgress dialog, boolean isLocal, boolean isBackup, String fileToRestore, boolean isMerge, OnBackupRestore onBackupRestore) {
        if (isLocal) {
            localBackUpAndRestore(dialog, isBackup, fileToRestore, isMerge, onBackupRestore);
        } else {
            driveBackupRestore(dialog, isBackup, fileToRestore, isMerge, onBackupRestore);
        }
    }

    private void localBackUpAndRestore(BackupRestoreProgress dialog, boolean isBackup, String fileToRestore, boolean isMerge, OnBackupRestore onBackupRestore) {
        dialog.showDialog();
        try {
            AppConstants.deleteTempFile(this.activity);
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (isBackup) {
            startLocalBackUp(dialog, onBackupRestore);
        } else {
            startLocalRestore(dialog, fileToRestore, isMerge, onBackupRestore);
        }
    }

    private void startLocalBackUp(final BackupRestoreProgress dialog, OnBackupRestore onBackupRestore) {
        String localZipFilePath = AppConstants.getLocalZipFilePath();
        try {
            Activity activity = this.activity;
            new ZipUnZipAsync(dialog, activity, true, getAllFilesForBackup(AppConstants.getRootPath(activity)), "", localZipFilePath, onBackupRestore);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<java.io.File> getAllFilesForBackup(String file) {
        ArrayList<java.io.File> arrayList = new ArrayList<>();
        java.io.File[] listFiles = new java.io.File(AppConstants.getDatabasePath(this.activity)).listFiles();
        if (listFiles != null && listFiles.length > 0) {
            arrayList.addAll(Arrays.asList(listFiles));
        }
        return arrayList;
    }

    private void startLocalRestore(final BackupRestoreProgress dialog, String fileToRestore, final boolean isMerge, final OnBackupRestore onBackupRestore) {
        new ZipUnZipAsync(dialog, this.activity, false, null, fileToRestore, "", new OnBackupRestore() { 


            @Override 
            public void onSuccess(boolean isSuccess) {
                BackupRestore.this.localRestoreAsync(dialog, isMerge, onBackupRestore, isSuccess);
            }

            @Override 
            public void getList(ArrayList<RestoreRowModel> list) {
                onBackupRestore.getList(list);
            }
        });
    }

    public void localRestoreAsync(final BackupRestoreProgress dialog, final boolean isMerge, final OnBackupRestore onBackupRestore, final boolean isSuccess) {
        dialog.setMessage(this.activity.getString(R.string.importing_data));
        dialog.showDialog();
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception {
                return BackupRestore.this.m79xde0636f9(isMerge);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                BackupRestore.lambda$localRestoreAsync$1(dialog, onBackupRestore, isSuccess, (Boolean) obj);
            }
        }));
    }

    
    public  Boolean m79xde0636f9(boolean z) throws Exception {
        localRestore(z);
        return false;
    }

    public static  void lambda$localRestoreAsync$1(BackupRestoreProgress backupRestoreProgress, OnBackupRestore onBackupRestore, boolean z, Boolean bool) throws Exception {
        try {
            backupRestoreProgress.dismissDialog();
            onBackupRestore.onSuccess(z);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void localRestore(boolean isMerge) {
        SupportSQLiteDatabase writableDatabase = AppDatabase.getAppDatabase(this.activity).getOpenHelper().getWritableDatabase();
        if (!isMerge) {
            deleteAllTableData(writableDatabase);
        }
        writableDatabase.execSQL(String.format("ATTACH DATABASE '%s' AS encrypted;", AppConstants.getTempDirectory(this.activity) + java.io.File.separator + AppConstants.APP_DB_NAME));
        replaceAllTableData(isMerge, writableDatabase);
        writableDatabase.execSQL("DETACH DATABASE encrypted");
    }

    private void replaceAllTableData(boolean isMerge, SupportSQLiteDatabase dbSql) {
        replaceAll(dbSql, isMerge, "CarModel", "CarId");
        replaceAll(dbSql, isMerge, "dbVersion", "versionNumber");
        replaceAll(dbSql, isMerge, "DocumentModel", "DocumentId");
        replaceAll(dbSql, isMerge, "FuelModel", "FuelId");
        replaceAll(dbSql, isMerge, "ImageModel", "ImageId");
        replaceAll(dbSql, isMerge, "ReminderModel", "ReminderId");
        replaceAll(dbSql, isMerge, "ServiceModel", "ServiceId");
    }

    private void replaceAll(SupportSQLiteDatabase dbSql, boolean isMerge, String tableName, String primaryKey) {
        if (isMerge) {
            try {
                dbSql.execSQL("insert into " + tableName + " select b.* from encrypted." + tableName + " b left join " + tableName + " c on c." + primaryKey + "=b." + primaryKey + " where c." + primaryKey + " is null ");
                return;
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            dbSql.execSQL("insert into " + tableName + " select * from encrypted." + tableName);
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
    }

    private void driveBackupRestore(BackupRestoreProgress dialog, boolean isBackup, String fileToRestore, boolean isMerge, OnBackupRestore onBackupRestore) {









        

        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this.activity);


        
        if (lastSignedInAccount == null) {
            signIn();
        } else if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this.activity), new Scope("https://www.googleapis.com/auth/drive.appdata"))) {
            GoogleSignIn.requestPermissions(this.activity, AppConstants.REQUEST_CODE_SIGN_IN, GoogleSignIn.getLastSignedInAccount(this.activity), new Scope("https://www.googleapis.com/auth/drive.appdata"));
        } else {
            setCredentials(lastSignedInAccount);
            startDriveOperation(isBackup, fileToRestore, isMerge, dialog, onBackupRestore);

        }
    }

    private void signIn() {
        this.activity.startActivityForResult(buildGoogleSignInClient().getSignInIntent(), AppConstants.REQUEST_CODE_SIGN_IN);
    }

    public void driveBackupList(BackupRestoreProgress dialog, OnBackupRestore onBackupRestore) {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this.activity);
        if (lastSignedInAccount == null) {
            signIn();
            return;
        }
        setCredentials(lastSignedInAccount);
        listFilesFromAppFolder(dialog, onBackupRestore);
    }

    private void listFilesFromAppFolder(final BackupRestoreProgress dialog, final OnBackupRestore onBackupRestore) {
        dialog.setMessage("Fetching backups...");
        dialog.showDialog();
        this.disposable.add(Observable.fromCallable(new Callable() { 

            @Override 
            public final Object call() {
                return BackupRestore.this.m81x1c3895aa();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 

            @Override 
            public final void accept(Object obj) {
                BackupRestore.this.m80xb0770549(dialog, onBackupRestore, (Boolean) obj);
            }
        }));
    }

    
    public  Boolean m81x1c3895aa() {
        try {
            Drive.Files.List list = this.driveService.files().list();
            this.fileList = list.setQ("mimeType ='" + this.METADATA_FILE_TYPE + "'")
                    .setSpaces(this.METADATA_FILE_PARENT)
                    .setFields("files(id, name,size,createdTime,modifiedTime)").execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    
    public  void m80xb0770549(BackupRestoreProgress backupRestoreProgress, OnBackupRestore onBackupRestore, Boolean bool) {
        backupRestoreProgress.dismissDialog();
        onBackupRestore.onSuccess(true);
        onBackupRestore.getList(getBackupList());
    }

    private ArrayList<RestoreRowModel> getBackupList() {
        ArrayList<RestoreRowModel> arrayList = new ArrayList<>();
        if (this.fileList != null) {
            for (int i = 0; i < this.fileList.getFiles().size(); i++) {
                File file = this.fileList.getFiles().get(i);
                String name = file.getName();
                String id = file.getId();
                String format = AppConstants.FILE_DATE_FORMAT.format(Long.valueOf(file.getModifiedTime().getValue()));
                arrayList.add(new RestoreRowModel(name, id, format, (file.getSize().longValue() / 1024) + "KB", file.getModifiedTime().getValue()));
            }
        }
        return arrayList;
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        return GoogleSignIn.getClient(this.activity, new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope("https://www.googleapis.com/auth/drive.appdata"), new Scope[0])
                .requestEmail()
                .requestProfile()
                .build());







    }

    public void startDriveOperation(boolean isBackup, String fileToRestore, boolean isMerge, BackupRestoreProgress dialog, OnBackupRestore onBackupRestore) {
        AppConstants.deleteTempFile(this.activity);
        if (isBackup) {
            startDriveBackup(fileToRestore, dialog, onBackupRestore);
        } else {
            downloadRestore(dialog, fileToRestore, AppConstants.getRemoteZipFilePath(this.activity), isMerge, onBackupRestore);
        }
    }

    public void handleSignInResult(final Intent result, final boolean isBackup, final boolean isList, final String fileToRestore, final BackupRestoreProgress dialog, final OnBackupRestore onBackupRestore) {
        try {
            GoogleSignIn.getSignedInAccountFromIntent(result).addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() { 

                @Override 
                public void onSuccess(GoogleSignInAccount googleAccount) {
                    Log.d("handleSignInResult", "Signed in as " + googleAccount.getEmail());
                    BackupRestore.this.setCredentials(googleAccount);
                    if (!isList) {
                        BackupRestore.this.startDriveOperation(isBackup, fileToRestore, false, dialog, onBackupRestore);
                    } else {
                        BackupRestore.this.driveBackupList(dialog, onBackupRestore);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() { 

                @Override 
                public void onFailure(Exception exception) {
                    Log.e("handleSignInResult", exception.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCredentials(GoogleSignInAccount googleAccount) {
        GoogleAccountCredential usingOAuth2 = GoogleAccountCredential.usingOAuth2(this.activity, Collections.singleton("https://www.googleapis.com/auth/drive.appdata"));
        usingOAuth2.setSelectedAccount(googleAccount.getAccount());
        this.driveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), usingOAuth2).setApplicationName(this.activity.getString(R.string.app_name)).build();
    }

    private void startDriveBackup(final String fileToRestore, final BackupRestoreProgress dialog, final OnBackupRestore onBackupRestore) {
        dialog.showDialog();
        if (fileToRestore == null) {
            final String remoteZipFilePath = AppConstants.getRemoteZipFilePath(this.activity);
            Activity activity = this.activity;
            new ZipUnZipAsync(dialog, activity, true, getAllFilesForBackup(AppConstants.getRootPath(activity)), "", remoteZipFilePath, new OnBackupRestore() { 
                @Override 
                public void getList(ArrayList<RestoreRowModel> list) {
                }

                @Override 
                public void onSuccess(boolean isSuccess) {
                    BackupRestore backupRestore = BackupRestore.this;
                    backupRestore.createFileInAppFolder(dialog, remoteZipFilePath, backupRestore.driveService, onBackupRestore);
                }
            });
            return;
        }
        createFileInAppFolder(dialog, fileToRestore, this.driveService, onBackupRestore);
    }

    public void createFileInAppFolder(final BackupRestoreProgress dialog, final String pathToUpload, final Drive driveService, final OnBackupRestore onBackupRestore) {
        dialog.setMessage("Uploading to drive...");
        dialog.showDialog();
        File file = new File();
        this.fileMetadata = file;
        file.setName(AppConstants.getBackupName());
        this.fileMetadata.setParents(Collections.singletonList(this.METADATA_FILE_PARENT));
        this.filePath = new java.io.File(pathToUpload);
        this.mediaContent = new FileContent(this.METADATA_FILE_TYPE, this.filePath);
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() {
                return BackupRestore.this.m87xd708c8dc(driveService);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 

            @Override 
            public final void accept(Object obj) {
                BackupRestore.this.m86x6b47387b(dialog, onBackupRestore, (Boolean) obj);
            }
        }));
    }

    
    public  Boolean m87xd708c8dc(Drive drive) {
        try {

            Log.e("MYTAG", "ErrorNo: 1:mediaContent" +mediaContent);
            Log.e("MYTAG", "ErrorNo: 2:fileMetadata" +fileMetadata);
            Log.e("MYTAG", "ErrorNo: 3:drive" +drive);

            this.file = drive.files().create(this.fileMetadata, this.mediaContent).setFields("id").execute();
            this.isSuccessCreate = true;
        } catch (IOException e) {
            this.isSuccessCreate = false;
            e.printStackTrace();
        }
        return Boolean.valueOf(this.isSuccessCreate);
    }

    
    public  void m86x6b47387b(BackupRestoreProgress backupRestoreProgress, OnBackupRestore onBackupRestore, Boolean bool) {
        backupRestoreProgress.dismissDialog();
        onBackupRestore.onSuccess(this.isSuccessCreate);
    }

    private void downloadRestore(final BackupRestoreProgress dialog, final String id, final String fileName, final boolean isMerge, final OnBackupRestore onBackupRestore) {
        dialog.showDialog();
        this.disposable.add(Observable.fromCallable(new Callable() {

            @Override 
            public final Object call() {
                return BackupRestore.this.m83xe55a506(fileName, id);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 


            @Override 
            public final void accept(Object obj) {
                BackupRestore.this.m82xa29414a5(dialog, fileName, isMerge, onBackupRestore, (Boolean) obj);
            }
        }));
    }

    
    public  Boolean m83xe55a506(String str, String str2) {
        try {
            this.outputStream = new FileOutputStream(str);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            this.driveService.files().get(str2).executeMediaAndDownloadTo(this.outputStream);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return false;
    }

    
    public  void m82xa29414a5(BackupRestoreProgress backupRestoreProgress, String str, boolean z, OnBackupRestore onBackupRestore, Boolean bool) {
        backupRestoreProgress.dismissDialog();
        if (new java.io.File(str).exists()) {
            startLocalRestore(backupRestoreProgress, str, z, onBackupRestore);
        }
    }

    public void deleteFromDrive(final BackupRestoreProgress dialog, final String fileId, final OnBackupRestore onBackupRestore) {
        dialog.setMessage("Deleting from Drive...");
        dialog.showDialog();
        this.disposable.add(Observable.fromCallable(new Callable() { 


            @Override 
            public final Object call() {
                return BackupRestore.this.m85x885a6cb5(fileId);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 

            @Override 
            public final void accept(Object obj) {
                BackupRestore.this.m84x1c98dc54(dialog, onBackupRestore, (Boolean) obj);
            }
        }));
    }

    public  Boolean m85x885a6cb5(String str) {
        try {
            this.driveService.files().delete(str).execute();
        } catch (IOException e) {
            e.printStackTrace();
            this.isSuccessDelete = false;
        }
        return false;
    }

    public  void m84x1c98dc54(BackupRestoreProgress backupRestoreProgress, OnBackupRestore onBackupRestore, Boolean bool) {
        backupRestoreProgress.dismissDialog();
        onBackupRestore.onSuccess(this.isSuccessDelete);
    }
}
