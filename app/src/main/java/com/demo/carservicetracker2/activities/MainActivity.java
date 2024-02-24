package com.demo.carservicetracker2.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.carservicetracker2.backupRestore.BackupRestore;
import com.demo.carservicetracker2.backupRestore.BackupRestoreProgress;
import com.demo.carservicetracker2.backupRestore.OnBackupRestore;
import com.demo.carservicetracker2.backupRestore.RestoreRowModel;
import com.demo.carservicetracker2.databinding.ActivityMainBinding;
import com.demo.carservicetracker2.databinding.BottomsheetBackupRestoreBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.adapter.CarNameAdapter;
import com.demo.carservicetracker2.adapter.HorizontalStaticServiceAdapter;
import com.demo.carservicetracker2.adapter.ServiceAdapter;
import com.demo.carservicetracker2.database.AppDatabase;
import com.demo.carservicetracker2.database.models.CarModel;
import com.demo.carservicetracker2.database.models.ServiceModel;
import com.demo.carservicetracker2.model.ServiceDocumentModel;
import com.demo.carservicetracker2.utils.AppConstants;
import com.demo.carservicetracker2.utils.AppPref;
import com.demo.carservicetracker2.utils.BetterActivityResult;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static Context context;
    ServiceAdapter adapter;
    AppDatabase appDatabase;
    private BackupRestore backupRestore;
    ActivityMainBinding binding;
    CarModel mainCarModel;
    Point p;
    private BackupRestoreProgress progressDialog;
    HorizontalStaticServiceAdapter staticAdapter;
    ActionBarDrawerToggle toggle;
    public final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);
    List<CarModel> carModelList = new ArrayList();
    List<ServiceDocumentModel> staticServiceList = new ArrayList();
    List<ServiceModel> serviceModelList = new ArrayList();
    CompositeDisposable disposable = new CompositeDisposable();
    public static void lambda$onClick$8(ActivityResult activityResult) {
    }



    private int requestCode = 22;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == this.requestCode) {
                if (grantResults.length > 0)
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "not Granted", Toast.LENGTH_SHORT).show();
                    }
            }
        } catch (Exception e) {
            Log.e("TAG", "onRequestPermissionsResult: ");
        }
    }
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.binding = (ActivityMainBinding) DataBindingUtil.setContentView(this, R.layout.activity_main);





            context = this;
            this.appDatabase = AppDatabase.getAppDatabase(this);
            this.toggle = new ActionBarDrawerToggle(this, this.binding.drawer, R.string.nav_open, R.string.nav_close);
            this.binding.drawer.addDrawerListener(this.toggle);
            this.toggle.syncState();
            this.backupRestore = new BackupRestore(this);
            this.progressDialog = new BackupRestoreProgress(this);
            if (AppPref.IsShowFuel()) {
                this.binding.llFuel.setVisibility(View.VISIBLE);
            } else {
                this.binding.llFuel.setVisibility(View.GONE);
            }
            Clicks();
            LoadCarServiceData();

            if(requestPermission(0));{
                Log.e("TAG", "requestPermission: ");
            }
            if (AppPref.IsShowNotification()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (this.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, requestCode);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e("MYTAG", "ErrorNo: onCreate:" + e);
        }
    }
    String[] require_permistion = new String[]{
            Manifest.permission.READ_MEDIA_IMAGES,
    };
    ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            Log.e("TAG", "Granted: ");
            Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("TAG", "Not GRanted: ");
            Toast.makeText(this, "Please allow permission !", Toast.LENGTH_SHORT).show();
        }

    });
    public boolean requestPermission(int i) {
        if (Build.VERSION.SDK_INT > 29) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, require_permistion[i]) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            launcher.launch(require_permistion[i]);
            return false;
        }
        } else {
            if (ContextCompat.checkSelfPermission(this,  require_permistion[i]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, i);
                return false;
            }
            return true;
        }
    }



    private void LoadCarServiceData() {
        this.binding.progressBar.setVisibility(View.VISIBLE);
        this.binding.llNoData.setVisibility(View.GONE);
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception {
                return MainActivity.this.m136xed4639c4();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                MainActivity.this.m137x7a3350e3((Boolean) obj);
            }
        }));
    }

    
    
    public  Boolean m136xed4639c4()  {
        List<CarModel> GetAllCar = this.appDatabase.carDao().GetAllCar();
        this.carModelList = GetAllCar;
        if (GetAllCar.contains(new CarModel(AppPref.getSelectedCarId()))) {
            int indexOf = this.carModelList.indexOf(new CarModel(AppPref.getSelectedCarId()));
            this.carModelList.get(indexOf).setSelected(true);
            this.mainCarModel = this.carModelList.get(indexOf);
        } else {
            this.carModelList.get(0).setSelected(true);
            this.mainCarModel = this.carModelList.get(0);
        }
        this.staticServiceList = AppConstants.GetAllStaticService();
        this.serviceModelList = this.appDatabase.serviceDao().GetAllServiceByCarId(this.mainCarModel.getCarId());
        return false;
    }
    public  void m137x7a3350e3(Boolean bool) throws Exception {
        this.binding.progressBar.setVisibility(View.GONE);
        this.binding.txtCarName.setText(this.mainCarModel.getCarName());
        setStaticAdapter();
        setServiceAdapter();
        CheckNoData();
    }

    
    
    
    public class AnonymousClass3 implements ServiceAdapter.ServiceClick {
        AnonymousClass3() {
        }

        @Override 
        public void OnServiceClick(int position) {
            Intent intent = new Intent(MainActivity.this, AddServiceActivity.class);
            intent.putExtra("isForUpdate", true);
            intent.putExtra("ServiceModel", MainActivity.this.serviceModelList.get(position));
            MainActivity.this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
                @Override 
                public final void onActivityResult(Object obj) {
                    MainActivity.AnonymousClass3.this.m146x4b3fd532((ActivityResult) obj);
                }
            });
        }

        
        
        public  void m146x4b3fd532(ActivityResult activityResult) {
            if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
                return;
            }
            Intent data = activityResult.getData();
            ServiceModel serviceModel = (ServiceModel) data.getParcelableExtra("ServiceModel");
            boolean booleanExtra = data.getBooleanExtra("isDelete", false);
            int indexOf = MainActivity.this.serviceModelList.indexOf(serviceModel);
            if (booleanExtra) {
                MainActivity.this.serviceModelList.remove(indexOf);
                MainActivity.this.adapter.notifyItemRemoved(indexOf);
                MainActivity.this.CheckNoData();
                return;
            }
            MainActivity.this.serviceModelList.set(indexOf, serviceModel);
            MainActivity.this.adapter.notifyItemChanged(indexOf);
            MainActivity.this.SortServiceDateDesc();
        }
    }

    private void setServiceAdapter() {
        this.adapter = new ServiceAdapter(this, this.serviceModelList, new AnonymousClass3());
        this.binding.serviceRecycle.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        this.binding.serviceRecycle.setAdapter(this.adapter);
    }

    
    
    
    public class AnonymousClass4 implements HorizontalStaticServiceAdapter.ServiceClick {
        @Override 
        public void OnServiceClick(int position) {
            Intent intent = new Intent(MainActivity.this, AddServiceActivity.class);
            intent.putExtra("CarId", MainActivity.this.mainCarModel.getCarId());
            intent.putExtra("ServiceDocModel", MainActivity.this.staticServiceList.get(position));
            MainActivity.this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
                @Override 
                public final void onActivityResult(Object obj) {
                    MainActivity.AnonymousClass4.this.onActivityResult((ActivityResult) obj);
                }
            });
        }
        public  void onActivityResult(ActivityResult activityResult) {
            if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
                return;
            }
            Intent data = activityResult.getData();
            ServiceModel serviceModel = (ServiceModel) data.getParcelableExtra("ServiceModel");
            ServiceDocumentModel serviceDocumentModel = (ServiceDocumentModel) data.getParcelableExtra("ServiceDocModel");
            MainActivity.this.serviceModelList.add(serviceModel);
            MainActivity.this.SortServiceDateDesc();
            MainActivity.this.CheckNoData();

        }
    }

    private void setStaticAdapter() {
        this.staticAdapter = new HorizontalStaticServiceAdapter(this, this.staticServiceList, new AnonymousClass4());
        this.binding.staticServiceRecycle.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.binding.staticServiceRecycle.setAdapter(this.staticAdapter);
    }



    
    
    

    public void openCloseDrawer(boolean isOpen) {
        if (isOpen) {
            if (this.binding.drawer.isDrawerOpen(GravityCompat.START)) {
                return;
            }
            this.binding.drawer.openDrawer(GravityCompat.START);
        } else if (this.binding.drawer.isDrawerOpen(GravityCompat.START)) {
            this.binding.drawer.closeDrawers();
        }
    }

    private void Clicks() {
        this.binding.drawerMenu.setOnClickListener(this);
        this.binding.addCarIcon.setOnClickListener(this);
        this.binding.serviceSeeAll.setOnClickListener(this);
        this.binding.rlPaper.setOnClickListener(this);
        this.binding.rlReport.setOnClickListener(this);
        this.binding.rlCarName.setOnClickListener(this);
        this.binding.SelectedCarServiceAll.setOnClickListener(this);
        this.binding.llFuel.setOnClickListener(this);
        this.binding.llSetting.setOnClickListener(this);
        this.binding.llBackupRestore.setOnClickListener(this);
        this.binding.llShare.setOnClickListener(this);
        this.binding.llRateus.setOnClickListener(this);
        this.binding.llPrivacy.setOnClickListener(this);
    }

    @Override 
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.SelectedCarServiceAll :
                Intent intent = new Intent(this, ServiceActivity.class);
                intent.putExtra("carModelId", this.mainCarModel.getCarId());
                this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
                    @Override 
                    public final void onActivityResult(Object obj) {
                        MainActivity.this.m144x9a627ff((ActivityResult) obj);
                    }
                });
                return;
            case R.id.add_car_icon :
                this.activityLauncher.launch(new Intent(this, GarageActivity.class), new BetterActivityResult.OnActivityResult() { 
                    @Override 
                    public final void onActivityResult(Object obj) {
                        MainActivity.this.m139x4904b464((ActivityResult) obj);
                    }
                });
                return;

            case R.id.drawer_menu :
                openCloseDrawer(true);
                return;
            case R.id.llBackupRestore :
                openCloseDrawer(false);
                openBackupRestoreBottomSheet();
                return;
            case R.id.llFuel :
                openCloseDrawer(false);
                Intent intent2 = new Intent(this, FuelActivity.class);
                intent2.putExtra("carModelId", this.mainCarModel.getCarId());
                this.activityLauncher.launch(intent2, new BetterActivityResult.OnActivityResult() { 
                    @Override 
                    public final void onActivityResult(Object obj) {
                        MainActivity.this.m145x2380563d((ActivityResult) obj);
                    }
                });
                return;
            case R.id.llPrivacy :
                openCloseDrawer(false);
                AppConstants.openUrl(this, AppConstants.PRIVACY_POLICY_URL);
                return;
            case R.id.llRateus :
                openCloseDrawer(false);
                AppConstants.showRattingDialog(this);
                return;
            case R.id.llSetting :
                openCloseDrawer(false);
                this.activityLauncher.launch(new Intent(this, SettingsActivity.class), new BetterActivityResult.OnActivityResult() { 
                    @Override 
                    public final void onActivityResult(Object obj) {
                        MainActivity.this.m138x4d1d70e7((ActivityResult) obj);
                    }
                });
                return;
            case R.id.llShare :
                openCloseDrawer(false);
                AppConstants.shareApp(this);
                return;
            case R.id.rlCarName :
                Point point = this.p;
                if (point != null) {
                    showStatusPopup(this, point);
                    return;
                }
                return;
            case R.id.rlPaper :
                Intent intent3 = new Intent(this, PapersActivity.class);
                intent3.putExtra("carModelId", this.mainCarModel.getCarId());
                this.activityLauncher.launch(intent3, new BetterActivityResult.OnActivityResult() {
                    @Override
                    public void onActivityResult(Object obj) {
                        MainActivity.this.m141x62dee2a2((ActivityResult) obj);
                    }
                });
                return;

            case R.id.rlReport :
                Intent intent5 = new Intent(this, ReportsActivity.class);
                intent5.putExtra("carModelId", this.mainCarModel.getCarId());
                this.activityLauncher.launch(intent5, new BetterActivityResult.OnActivityResult() { 
                    @Override 
                    public final void onActivityResult(Object obj) {
                        MainActivity.this.m143x7cb910e0((ActivityResult) obj);
                    }
                });
                return;
            case R.id.serviceSeeAll :
                Intent intent6 = new Intent(this, AllSelectServiceActivity.class);
                intent6.putExtra("carId", this.mainCarModel.getCarId());
                this.activityLauncher.launch(intent6, new BetterActivityResult.OnActivityResult() { 
                    @Override 
                    public final void onActivityResult(Object obj) {
                        MainActivity.this.m140xd5f1cb83((ActivityResult) obj);
                    }
                });
                return;
            default:
                return;
        }
    }

    
    
    public  void m139x4904b464(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        Intent data = activityResult.getData();
        boolean booleanExtra = data.getBooleanExtra("isChange", false);
        ArrayList parcelableArrayListExtra = data.getParcelableArrayListExtra("InsertedCarList");
        if (booleanExtra) {
            this.carModelList.clear();
            this.carModelList.addAll(this.appDatabase.carDao().GetAllCar());
            SortCarDateDesc();
            if (this.carModelList.contains(this.mainCarModel)) {
                this.mainCarModel = this.carModelList.get(this.carModelList.indexOf(this.mainCarModel));
            } else {
                this.carModelList.get(0).setSelected(true);
                this.mainCarModel = this.carModelList.get(0);
                this.serviceModelList.clear();
                this.serviceModelList.addAll(this.appDatabase.serviceDao().GetAllServiceByCarId(this.mainCarModel.getCarId()));
                this.adapter.notifyDataSetChanged();
                CheckNoData();
            }
        } else if (parcelableArrayListExtra.size() > 0) {
            this.carModelList.addAll(parcelableArrayListExtra);
            SortCarDateDesc();
        }
        this.binding.txtCarName.setText(this.mainCarModel.getCarName());
    }

    
    
    public  void m140xd5f1cb83(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        Intent data = activityResult.getData();
        ServiceModel serviceModel = (ServiceModel) data.getParcelableExtra("ServiceModel");
        ServiceDocumentModel serviceDocumentModel = (ServiceDocumentModel) data.getParcelableExtra("ServiceDocModel");
        this.serviceModelList.add(serviceModel);
        CheckNoData();

    }

    
    
    public  void m141x62dee2a2(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        ArrayList parcelableArrayListExtra = activityResult.getData().getParcelableArrayListExtra("InsertedCarList");
        if (parcelableArrayListExtra.size() > 0) {
            this.carModelList.addAll(parcelableArrayListExtra);
        }
    }

    
    
    public  void m142xefcbf9c1(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        ArrayList parcelableArrayListExtra = activityResult.getData().getParcelableArrayListExtra("InsertedCarList");
        if (parcelableArrayListExtra.size() > 0) {
            this.carModelList.addAll(parcelableArrayListExtra);
        }
    }

    
    
    public  void m143x7cb910e0(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.carModelList.addAll(activityResult.getData().getParcelableArrayListExtra("InsertedCarList"));
    }

    
    
    public  void m144x9a627ff(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        Intent data = activityResult.getData();
        boolean booleanExtra = data.getBooleanExtra("isChange", false);
        ArrayList parcelableArrayListExtra = data.getParcelableArrayListExtra("InsertedServiceList");
        ArrayList parcelableArrayListExtra2 = data.getParcelableArrayListExtra("DeletedServiceList");
        ArrayList parcelableArrayListExtra3 = data.getParcelableArrayListExtra("InsertedCarList");
        if (booleanExtra) {
            this.serviceModelList.clear();
            this.serviceModelList.addAll(this.appDatabase.serviceDao().GetAllServiceByCarId(this.mainCarModel.getCarId()));
        } else {
            if (parcelableArrayListExtra.size() > 0) {
                this.serviceModelList.addAll(parcelableArrayListExtra);
            }
            if (parcelableArrayListExtra2.size() > 0) {
                this.serviceModelList.removeAll(parcelableArrayListExtra2);
            }
        }
        if (parcelableArrayListExtra3.size() > 0) {
            this.carModelList.addAll(parcelableArrayListExtra3);
        }
        SortServiceDateDesc();
        CheckNoData();
    }

    
    
    public  void m145x2380563d(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        ArrayList parcelableArrayListExtra = activityResult.getData().getParcelableArrayListExtra("InsertedCarList");
        if (parcelableArrayListExtra.size() > 0) {
            this.carModelList.addAll(parcelableArrayListExtra);
        }
    }

    
    
    public  void m138x4d1d70e7(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1 && activityResult.getData() != null && activityResult.getData().getBooleanExtra("isChange", false)) {
            if (AppPref.IsShowFuel()) {
                this.binding.llFuel.setVisibility(View.VISIBLE);
            } else {
                this.binding.llFuel.setVisibility(View.GONE);
            }
            this.adapter.notifyDataSetChanged();
        }
    }

    
    public void CheckNoData() {
        if (this.serviceModelList.size() > 0) {
            this.binding.llNoData.setVisibility(View.GONE);
            this.binding.serviceRecycle.setVisibility(View.VISIBLE);
            SortServiceDateDesc();
            return;
        }
        this.binding.llNoData.setVisibility(View.VISIBLE);
        this.binding.serviceRecycle.setVisibility(View.GONE);
    }

    private void openBackupRestoreBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        BottomsheetBackupRestoreBinding bottomsheetBackupRestoreBinding = (BottomsheetBackupRestoreBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottomsheet_backup_restore, null, false);
        bottomSheetDialog.setContentView(bottomsheetBackupRestoreBinding.getRoot());
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();
        bottomsheetBackupRestoreBinding.llBackup.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                MainActivity.this.backupData(false);
                bottomSheetDialog.dismiss();
            }
        });
        bottomsheetBackupRestoreBinding.llRestore.setOnClickListener(new AnonymousClass8(bottomSheetDialog));
    }

    
    
    
    public class AnonymousClass8 implements View.OnClickListener {
        final  BottomSheetDialog val$bottomSheetBackUpDialog;

        AnonymousClass8(final BottomSheetDialog val$bottomSheetBackUpDialog) {
            this.val$bottomSheetBackUpDialog = val$bottomSheetBackUpDialog;
        }

        @Override 
        public void onClick(View view) {
            MainActivity.this.activityLauncher.launch(new Intent(MainActivity.this, RestoreDriveList.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), new BetterActivityResult.OnActivityResult() { 
                @Override 
                public final void onActivityResult(Object obj) {
                    MainActivity.AnonymousClass8.this.m150xea1993a((ActivityResult) obj);
                }
            });
            this.val$bottomSheetBackUpDialog.dismiss();
        }

        
        
        public  void m150xea1993a(ActivityResult activityResult) {
            Intent data = activityResult.getData();
            if (data == null || !data.getBooleanExtra("backupScuccess", false)) {
                return;
            }
            MainActivity.this.carModelList.clear();
            MainActivity.this.carModelList.addAll(MainActivity.this.appDatabase.carDao().GetAllCar());
            if (!MainActivity.this.carModelList.contains(MainActivity.this.mainCarModel)) {
                MainActivity.this.carModelList.get(0).setSelected(true);
                MainActivity mainActivity = MainActivity.this;
                mainActivity.mainCarModel = mainActivity.carModelList.get(0);
                MainActivity.this.binding.txtCarName.setText(MainActivity.this.mainCarModel.getCarName());
            }
            MainActivity.this.serviceModelList.clear();
            MainActivity.this.serviceModelList.addAll(MainActivity.this.appDatabase.serviceDao().GetAllServiceByCarId(MainActivity.this.mainCarModel.getCarId()));
            MainActivity.this.adapter.notifyDataSetChanged();
            MainActivity.this.CheckNoData();
        }
    }

    
    public void backupData(boolean isLocal) {

        this.backupRestore.backupRestore(this.progressDialog, isLocal, true, null, false, new OnBackupRestore() {
            @Override 
            public void getList(ArrayList<RestoreRowModel> list) {

            }

            @Override 
            public void onSuccess(boolean isSuccess) {
                if (isSuccess) {
                    MainActivity mainActivity = MainActivity.this;
                    Toast.makeText(mainActivity, mainActivity.getString(R.string.export_successfully), Toast.LENGTH_SHORT).show();
                    return;
                }
                MainActivity mainActivity2 = MainActivity.this;
                Toast.makeText(mainActivity2, mainActivity2.getString(R.string.failed_to_export), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String idToken = account.getIdToken();

                SharedPreferences mySharedPreferences = context.getSharedPreferences(AppConstants.PRIVATE_SHARED_PREFERENCE, 0);
                SharedPreferences.Editor edit = mySharedPreferences.edit();
                edit.putString(AppConstants.ID_TOKAN, idToken);
                edit.apply();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("MYTAG", "ErrorNo: onActivityResult:" + e);

            }
        }

    }

    @Override 
    public void onWindowFocusChanged(boolean hasFocus) {
        int[] iArr = new int[2];
        this.binding.rlCarName.getLocationOnScreen(iArr);
        Point point = new Point();
        this.p = point;
        point.x = iArr[0];
        this.p.y = iArr[1];
    }

    private void showStatusPopup(final Activity context2, Point p) {
        View inflate = ((LayoutInflater) context2.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.car_list_layout, (ViewGroup) null);
        final PopupWindow popupWindow = new PopupWindow(context2);
        popupWindow.setContentView(inflate);
        popupWindow.setAnimationStyle(R.style.popup_window_animation_sms);
        popupWindow.setWidth(-2);
        popupWindow.setHeight(-2);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(inflate, 0, p.x - 20, p.y + 50);
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.carRecycle);
        SortCarDateDesc();
        CarNameAdapter carNameAdapter = new CarNameAdapter(context2, this.carModelList, new CarNameAdapter.CarClick() { 
            @Override 
            public void OnCarClick(int position) {
                MainActivity.this.mainCarModel.setSelected(false);
                MainActivity.this.mainCarModel = MainActivity.this.carModelList.get(position);
                MainActivity.this.mainCarModel.setSelected(true);
                MainActivity.this.binding.txtCarName.setText(MainActivity.this.mainCarModel.getCarName());
                MainActivity.this.serviceModelList.clear();
                MainActivity.this.serviceModelList.addAll(MainActivity.this.appDatabase.serviceDao().GetAllServiceByCarId(MainActivity.this.mainCarModel.getCarId()));
                MainActivity.this.adapter.notifyDataSetChanged();
                MainActivity.this.CheckNoData();
                popupWindow.dismiss();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(carNameAdapter);
    }

    public void SortCarDateDesc() {
        Collections.sort(this.carModelList, new Comparator<CarModel>() { 
            @Override 
            public int compare(CarModel o1, CarModel o2) {
                return Long.valueOf(o2.getPurchaseDate()).compareTo(Long.valueOf(o1.getPurchaseDate()));
            }
        });
    }

    public void SortServiceDateDesc() {
        Collections.sort(this.serviceModelList, new Comparator<ServiceModel>() { 
            @Override 
            public int compare(ServiceModel o1, ServiceModel o2) {
                return Long.valueOf(o2.getDate()).compareTo(Long.valueOf(o1.getDate()));
            }
        });
        this.adapter.notifyDataSetChanged();
    }

    @Override 
    public void onBackPressed() {
        CarModel carModel = this.mainCarModel;
        if (carModel != null) {
            AppPref.setSelectedCarId(carModel.getCarId());
        } else {
            AppPref.setSelectedCarId(AppConstants.DEFAULT_CAR_ID);
        }
        if (this.binding.drawer.isDrawerOpen(GravityCompat.START)) {
            this.binding.drawer.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
