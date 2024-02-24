package com.demo.carservicetracker2.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.demo.carservicetracker2.AdAdmob;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.databinding.ActivityAddCarBinding;
import com.demo.carservicetracker2.databinding.BottomsheetCaptureImageBinding;
import com.demo.carservicetracker2.databinding.DialogDeleteBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.demo.carservicetracker2.adapter.ImageAdapter;
import com.demo.carservicetracker2.database.AppDatabase;
import com.demo.carservicetracker2.database.models.CarModel;
import com.demo.carservicetracker2.database.models.DocumentModel;
import com.demo.carservicetracker2.database.models.FuelModel;
import com.demo.carservicetracker2.database.models.ImageModel;
import com.demo.carservicetracker2.database.models.ServiceModel;
import com.demo.carservicetracker2.utils.AppConstants;
import com.demo.carservicetracker2.utils.BetterActivityResult;
import com.demo.carservicetracker2.utils.ImageCompressionAsyncTask;
import com.demo.carservicetracker2.utils.ImageListener;
import com.theartofdev.edmodo.cropper.CropImage;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import net.lingala.zip4j.util.InternalZipConstants;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class AddCarActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    String CarId;
    String ImageName;
    AppDatabase appDatabase;
    ActivityAddCarBinding binding;
    CarModel carModel;
    ImageAdapter imageAdapter;
    String imageUri;
    String mCurrentPhotoPath;
    String selectedImageName;
    Uri selectedUri;
    boolean isForUpdate = false;
    long purchaseDate = 0;
    boolean isFromMainImage = false;
    List<ImageModel> imageModelList = new ArrayList();
    boolean isSelected = false;
    boolean isMainImageChange = false;
    boolean isDelete = false;
    CompositeDisposable disposable = new CompositeDisposable();
    public final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = (ActivityAddCarBinding) DataBindingUtil.setContentView(this, R.layout.activity_add_car);
        this.appDatabase = AppDatabase.getAppDatabase(this);
        Init();
        setTextCount();
        Clicks();


        AdAdmob adAdmob = new AdAdmob( this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        adAdmob.FullscreenAd(this);




    }

    private void setTextCount() {
        this.binding.edtCarName.addTextChangedListener(new TextWatcher() { 
            @Override 
            public void afterTextChanged(Editable editable) {
            }

            @Override 
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override 
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                AddCarActivity.this.binding.edtCarName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
                AddCarActivity.this.binding.txtCarNameCnt.setText(String.valueOf(AddCarActivity.this.binding.edtCarName.length()));
            }
        });
        this.binding.edtCarLicensePlate.addTextChangedListener(new TextWatcher() { 
            @Override 
            public void afterTextChanged(Editable editable) {
            }

            @Override 
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override 
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                AddCarActivity.this.binding.edtCarLicensePlate.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                AddCarActivity.this.binding.txtCarPlateCnt.setText(String.valueOf(AddCarActivity.this.binding.edtCarLicensePlate.length()));
            }
        });
        this.binding.edtComments.addTextChangedListener(new TextWatcher() { 
            @Override 
            public void afterTextChanged(Editable editable) {
            }

            @Override 
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override 
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                AddCarActivity.this.binding.edtComments.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1000)});
                AddCarActivity.this.binding.txtCommentsCnt.setText(String.valueOf(AddCarActivity.this.binding.edtComments.length()));
            }
        });
    }

    private void Init() {
        boolean booleanExtra = getIntent().getBooleanExtra("isForUpdate", false);
        this.isForUpdate = booleanExtra;
        if (booleanExtra) {
            CarModel carModel = (CarModel) getIntent().getParcelableExtra("carModel");
            this.carModel = carModel;
            if (!carModel.getCarId().equals(AppConstants.DEFAULT_CAR_ID)) {
                this.binding.btnDelete.setVisibility(View.VISIBLE);
            } else {
                this.binding.btnDelete.setVisibility(View.GONE);
            }
            this.CarId = this.carModel.getCarId();
            if (!TextUtils.isEmpty(this.carModel.getCarImageName())) {
                RequestManager with = Glide.with((FragmentActivity) this);
                with.load(AppConstants.getMediaDir(this) + InternalZipConstants.ZIP_FILE_SEPARATOR + this.carModel.getCarImageName()).into(this.binding.carImage);
                this.binding.cancelImage.setVisibility(View.VISIBLE);
                this.binding.AddImage.setVisibility(View.GONE);
                this.selectedUri = Uri.fromFile(new File(AppConstants.getMediaDir(this) + InternalZipConstants.ZIP_FILE_SEPARATOR + this.carModel.getCarImageName()));
                this.selectedImageName = this.carModel.getCarImageName();
            }
            this.purchaseDate = this.carModel.getPurchaseDate();
            this.binding.edtCarName.setText(this.carModel.getCarName());
            if (this.carModel.getPurchaseDate() > 0) {
                this.binding.txtCarDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(this.carModel.getPurchaseDate())));
            }
            this.binding.edtCarLicensePlate.setText(this.carModel.getLicensePlate());
            EditText editText = this.binding.edtOilMileage;
            editText.setText("" + this.carModel.getOilChangeMileage());
            EditText editText2 = this.binding.edtOilMonth;
            editText2.setText("" + this.carModel.getOilIntervalDate());
            EditText editText3 = this.binding.edtServiceMileage;
            editText3.setText("" + this.carModel.getServiceChangeMileage());
            EditText editText4 = this.binding.edtServiceMonth;
            editText4.setText("" + this.carModel.getServiceIntervalDate());
            this.binding.edtComments.setText(this.carModel.getComments());
            setNestedScroll();
        } else {
            this.binding.btnDelete.setVisibility(View.GONE);
            this.CarId = AppConstants.getUniqueId();
            this.binding.edtOilMileage.setText("15000");
            this.binding.edtOilMonth.setText("12");
            this.binding.edtServiceMileage.setText("15000");
            this.binding.edtServiceMonth.setText("12");
        }
        int length = this.binding.edtCarName.length();
        TextView textView = this.binding.txtCarNameCnt;
        textView.setText("" + length);
        int length2 = this.binding.edtCarLicensePlate.length();
        TextView textView2 = this.binding.txtCarPlateCnt;
        textView2.setText("" + length2);
        int length3 = this.binding.edtComments.length();
        TextView textView3 = this.binding.txtCommentsCnt;
        textView3.setText("" + length3);
        LoadImageList();
    }

    private void setNestedScroll() {
        if (this.carModel.getCarId().equals(AppConstants.DEFAULT_CAR_ID) || !this.isForUpdate) {
            return;
        }
        this.binding.scroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() { 
            @Override 
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    AddCarActivity.this.binding.btnDelete.setVisibility(View.GONE);
                }
                if (scrollY < oldScrollY) {
                    AddCarActivity.this.binding.btnDelete.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void LoadImageList() {
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception  {
                return AddCarActivity.this.m74x3c30f596();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                try {
                    AddCarActivity.this.m75x4234c0f5((Boolean) obj);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }));
    }

    
    
    public  Boolean m74x3c30f596() throws Exception {
        if (this.isForUpdate) {
            this.imageModelList = this.appDatabase.imageDao().GetAllImageOfCar(this.CarId);
        }
        return false;
    }

    
    
    public  void m75x4234c0f5(Boolean bool) throws Exception {
        setImageAdapter();
    }

    private void setImageAdapter() {
        this.imageAdapter = new ImageAdapter(this, this.imageModelList, new ImageAdapter.ImageRemoveListener() { 
            @Override 
            public void OnRemoveImage(int position) {
                if (AddCarActivity.this.imageModelList.get(position).isNew()) {
                    AddCarActivity.this.imageModelList.remove(position);
                } else {
                    AddCarActivity.this.imageModelList.get(position).setDeleted(true);
                }
                AddCarActivity.this.imageAdapter.notifyDataSetChanged();
            }
        }, new AnonymousClass6());
        this.binding.rvImages.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.binding.rvImages.setAdapter(this.imageAdapter);
    }

    
    
    
    public class AnonymousClass6 implements ImageAdapter.OnItemClick {
        AnonymousClass6() {
        }

        @Override 
        public void OnItemClick(int position) {
            Intent intent = new Intent(AddCarActivity.this, ViewImageActivity.class);
            intent.putParcelableArrayListExtra("imageList", (ArrayList) AddCarActivity.this.imageModelList);
            intent.putExtra("position", position);
            AddCarActivity.this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
                @Override 
                public final void onActivityResult(Object obj) {
                    AddCarActivity.AnonymousClass6.this.m81xc12acec5((ActivityResult) obj);
                }
            });
        }

        
        
        public  void m81xc12acec5(ActivityResult activityResult) {
            Intent data = activityResult.getData();
            if (data == null || !data.getBooleanExtra("isChange", false)) {
                return;
            }
            AddCarActivity.this.imageModelList.clear();
            AddCarActivity.this.imageModelList = data.getParcelableArrayListExtra("images");
            AddCarActivity.this.imageAdapter.setList(AddCarActivity.this.imageModelList);
        }
    }

    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (intent.resolveActivity(getPackageManager()) != null) {
            File file = null;
            try {
                file = createImageFile();
                this.mCurrentPhotoPath = file.getAbsolutePath();
            } catch (Exception unused) {
                Log.e("Main", "IOException");
            }
            if (file != null) {
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
                intent.putExtra("output", FileProvider.getUriForFile(this, getPackageName()+".provider", file));
                this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() {
                    @Override
                    public void onActivityResult(Object obj) {
                        AddCarActivity.this.m78xec8df91e((ActivityResult) obj);
                    }
                });
            }
        }
    }

    
    
    public  void m78xec8df91e(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1) {
            CropImage.activity(Uri.fromFile(new File(this.mCurrentPhotoPath))).start(this);
        }
    }

    private File createImageFile() {
        return new File(new File(AppConstants.getTempDirectory(this)), "IMG_" + System.currentTimeMillis() + ".jpg");
    }

    private void insertCar() {
        if (!TextUtils.isEmpty(this.binding.edtCarName.getText()) && !TextUtils.isEmpty(this.binding.edtOilMileage.getText()) && !TextUtils.isEmpty(this.binding.edtOilMonth.getText()) && !TextUtils.isEmpty(this.binding.edtServiceMileage.getText()) && !TextUtils.isEmpty(this.binding.edtServiceMonth.getText())) {
            final String obj = this.binding.edtCarName.getText().toString();
            final String obj2 = this.binding.edtCarLicensePlate.getText().toString();
            final long parseLong = Long.parseLong(this.binding.edtOilMileage.getText().toString());
            final int parseInt = Integer.parseInt(this.binding.edtOilMonth.getText().toString());
            final long parseLong2 = Long.parseLong(this.binding.edtServiceMileage.getText().toString());
            final int parseInt2 = Integer.parseInt(this.binding.edtServiceMonth.getText().toString());
            final String obj3 = this.binding.edtComments.getText().toString();
            if (AddCarActivity.this.selectedUri != null) {
                if (AddCarActivity.this.isForUpdate) {
                    AddCarActivity.this.carModel.setCarName(obj);
                    AddCarActivity.this.carModel.setCarImageName(AddCarActivity.this.selectedImageName);
                    AddCarActivity.this.carModel.setPurchaseDate(AddCarActivity.this.purchaseDate);
                    AddCarActivity.this.carModel.setLicensePlate(obj2);
                    AddCarActivity.this.carModel.setOilChangeMileage(parseLong);
                    AddCarActivity.this.carModel.setOilIntervalDate(parseInt);
                    AddCarActivity.this.carModel.setServiceChangeMileage(parseLong2);
                    AddCarActivity.this.carModel.setServiceIntervalDate(parseInt2);
                    AddCarActivity.this.carModel.setComments(obj3);
                    AddCarActivity.this.appDatabase.carDao().UpdateCar(AddCarActivity.this.carModel);
                    AddCarActivity.this.UpdateImage();
                    Intent intent = AddCarActivity.this.getIntent();
                    intent.putExtra("CarModel", AddCarActivity.this.carModel);
                    intent.putExtra("isSelected", AddCarActivity.this.isSelected);
                    intent.putExtra("isChange", true);
                    AddCarActivity.this.setResult(-1, intent);
                    AddCarActivity.this.finish();
                    return;
                }
                CarModel carModel = new CarModel(AddCarActivity.this.CarId, AddCarActivity.this.selectedImageName, obj, AddCarActivity.this.purchaseDate, obj2, parseLong, parseInt, parseLong2, parseInt2, obj3, AppConstants.GetOnlyDateInMillis(System.currentTimeMillis()));
                AddCarActivity.this.appDatabase.carDao().InsertCar(carModel);
                AddCarActivity.this.InsertImage();
                Intent intent2 = AddCarActivity.this.getIntent();
                intent2.putExtra("CarModel", carModel);
                intent2.putExtra("isSelected", AddCarActivity.this.isSelected);
                AddCarActivity.this.setResult(-1, intent2);
                AddCarActivity.this.finish();
            } else if (AddCarActivity.this.isForUpdate) {
                AddCarActivity.this.carModel.setCarImageName("");
                AddCarActivity.this.carModel.setCarName(obj);
                AddCarActivity.this.carModel.setPurchaseDate(AddCarActivity.this.purchaseDate);
                AddCarActivity.this.carModel.setLicensePlate(obj2);
                AddCarActivity.this.carModel.setOilChangeMileage(parseLong);
                AddCarActivity.this.carModel.setOilIntervalDate(parseInt);
                AddCarActivity.this.carModel.setServiceChangeMileage(parseLong2);
                AddCarActivity.this.carModel.setServiceIntervalDate(parseInt2);
                AddCarActivity.this.carModel.setComments(obj3);
                AddCarActivity.this.appDatabase.carDao().UpdateCar(AddCarActivity.this.carModel);
                AddCarActivity.this.UpdateImage();
                Intent intent3 = AddCarActivity.this.getIntent();
                intent3.putExtra("CarModel", AddCarActivity.this.carModel);
                intent3.putExtra("isSelected", AddCarActivity.this.isSelected);
                intent3.putExtra("isChange", true);
                AddCarActivity.this.setResult(-1, intent3);
                AddCarActivity.this.finish();
            } else {
                CarModel carModel2 = new CarModel(AddCarActivity.this.CarId, "", obj, AddCarActivity.this.purchaseDate, obj2, parseLong, parseInt, parseLong2, parseInt2, obj3, AppConstants.GetOnlyDateInMillis(System.currentTimeMillis()));
                AddCarActivity.this.appDatabase.carDao().InsertCar(carModel2);
                AddCarActivity.this.InsertImage();
                Intent intent4 = AddCarActivity.this.getIntent();
                intent4.putExtra("CarModel", carModel2);
                intent4.putExtra("isSelected", AddCarActivity.this.isSelected);
                AddCarActivity.this.setResult(-1, intent4);
                AddCarActivity.this.finish();
            }
            return;
        }
        if (TextUtils.isEmpty(this.binding.edtCarName.getText())) {
            this.binding.edtCarName.setError("Empty car name");
        }
        if (TextUtils.isEmpty(this.binding.edtOilMileage.getText())) {
            VisibleIntervals();
            this.binding.edtOilMileage.setError("Interval is empty");
        }
        if (TextUtils.isEmpty(this.binding.edtOilMonth.getText())) {
            VisibleIntervals();
            this.binding.edtOilMonth.setError("Interval is empty");
        }
        if (TextUtils.isEmpty(this.binding.edtServiceMileage.getText())) {
            VisibleIntervals();
            this.binding.edtServiceMileage.setError("Interval is empty");
        }
        if (TextUtils.isEmpty(this.binding.edtServiceMonth.getText())) {
            VisibleIntervals();
            this.binding.edtServiceMonth.setError("Interval is empty");
        }
    }

    private void VisibleIntervals() {
        if (this.binding.llOil.getVisibility() == View.GONE && this.binding.llService.getVisibility() == View.GONE) {
            this.binding.llOil.setVisibility(View.VISIBLE);
            this.binding.llService.setVisibility(View.VISIBLE);
            this.binding.downUpImage.setImageResource(R.drawable.up_arrow);
        }
    }

    
    public void UpdateImage() {
        this.binding.progressBar.setVisibility(View.VISIBLE);
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception   {
                return AddCarActivity.this.m76xeb6f3394();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception   {
                AddCarActivity.this.m77xf172fef3((Boolean) obj);
            }
        }));
    }

    
    
    public  Boolean m76xeb6f3394() throws Exception {
        for (int i = 0; i < this.imageModelList.size(); i++) {
            if (this.imageModelList.get(i).isNew()) {
                this.appDatabase.imageDao().InsertImage(new ImageModel(AppConstants.getUniqueId(), this.carModel.getCarId(), this.imageModelList.get(i).getImageName()));
            } else if (this.imageModelList.get(i).isDeleted()) {
                this.appDatabase.imageDao().DeleteImage(this.imageModelList.get(i));
                AppConstants.deleteImage(this, getResources().getString(R.string.app_name) + InternalZipConstants.ZIP_FILE_SEPARATOR + this.imageModelList.get(i).getImageName());
            }
        }
        for (int size = this.imageModelList.size() - 1; size >= 0; size--) {
            if (this.imageModelList.get(size).isDeleted()) {
                this.imageModelList.remove(size);
            }
        }
        return false;
    }

    
    
    public  void m77xf172fef3(Boolean bool) throws Exception {
        this.binding.progressBar.setVisibility(View.GONE);
    }

    
    public void InsertImage() {
        if (this.imageModelList.size() > 0) {
            this.binding.progressBar.setVisibility(View.VISIBLE);
            this.disposable.add(Observable.fromCallable(new Callable() { 
                @Override 
                public final Object call() throws Exception   {
                    return AddCarActivity.this.m72xd7851842();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
                @Override 
                public final void accept(Object obj) throws Exception   {
                    AddCarActivity.this.m73xdd88e3a1((Boolean) obj);
                }
            }));
        }
    }

    
    
    public  Boolean m72xd7851842() throws Exception {
        for (int i = 0; i < this.imageModelList.size(); i++) {
            this.appDatabase.imageDao().InsertImage(new ImageModel(AppConstants.getUniqueId(), this.CarId, this.imageModelList.get(i).getImageName()));
        }
        return false;
    }

    
    
    public  void m73xdd88e3a1(Boolean bool) throws Exception {
        this.binding.progressBar.setVisibility(View.GONE);
    }

    private void Clicks() {
        this.binding.back.setOnClickListener(this);
        this.binding.btnSave.setOnClickListener(this);
        this.binding.mainImageCard.setOnClickListener(this);
        this.binding.AddImage.setOnClickListener(this);
        this.binding.cancelImage.setOnClickListener(this);
        this.binding.txtCarDate.setOnClickListener(this);
        this.binding.calendar.setOnClickListener(this);
        this.binding.btnSelectImage.setOnClickListener(this);
        this.binding.btnDelete.setOnClickListener(this);
        this.binding.llOpenInterval.setOnClickListener(this);
    }

    @Override 
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.AddImage :
            case R.id.mainImageCard :
                openCarPickerBottom();
                return;
            case R.id.back :
                onBackPressed();
                return;
            case R.id.btnDelete :
                DeleteCar();
                return;
            case R.id.btnSave :
                insertCar();
                return;
            case R.id.btnSelectImage :
                openBottomSheet();
                return;
            case R.id.calendar :
            case R.id.txtCarDate :
                final Calendar calendar = Calendar.getInstance();
                if (this.isForUpdate) {
                    long j = this.purchaseDate;
                    if (j > 0) {
                        calendar.setTimeInMillis(j);
                        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() { 
                            @Override 
                            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                                calendar.set(1, year);
                                calendar.set(2, month);
                                calendar.set(5, date);
                                AddCarActivity.this.purchaseDate = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                                AddCarActivity.this.binding.txtCarDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(AddCarActivity.this.purchaseDate)));
                            }
                        }, calendar.get(1), calendar.get(2), calendar.get(5)).show();
                        return;
                    }
                }
                calendar.setTimeInMillis(System.currentTimeMillis());
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() { 
                    @Override 
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        calendar.set(1, year);
                        calendar.set(2, month);
                        calendar.set(5, date);
                        AddCarActivity.this.purchaseDate = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                        AddCarActivity.this.binding.txtCarDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(AddCarActivity.this.purchaseDate)));
                    }
                }, calendar.get(1), calendar.get(2), calendar.get(5)).show();
                return;
            case R.id.cancelImage :
                this.selectedUri = null;
                if (!TextUtils.isEmpty(this.selectedImageName)) {
                    new File(AppConstants.getMediaDir(this) + InternalZipConstants.ZIP_FILE_SEPARATOR + this.selectedImageName).delete();
                    this.selectedImageName = "";
                }
                this.binding.cancelImage.setVisibility(View.GONE);
                this.binding.AddImage.setVisibility(View.VISIBLE);
                this.binding.carImage.setVisibility(View.GONE);
                return;
            case R.id.llOpenInterval :
                if (this.binding.llOil.getVisibility() == View.VISIBLE && this.binding.llService.getVisibility() == View.VISIBLE) {
                    this.binding.llOil.setVisibility(View.GONE);
                    this.binding.llService.setVisibility(View.GONE);
                    this.binding.downUpImage.setImageResource(R.drawable.down_arrow);
                    return;
                }
                this.binding.llOil.setVisibility(View.VISIBLE);
                this.binding.llService.setVisibility(View.VISIBLE);
                this.binding.downUpImage.setImageResource(R.drawable.up_arrow);
                return;
            default:
                return;
        }
    }

    private void DeleteCar() {
        DialogDeleteBinding dialogDeleteBinding = (DialogDeleteBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_delete, null, false);
        final Dialog dialog = new Dialog(this, R.style.dialogTheme);
        dialog.setContentView(dialogDeleteBinding.getRoot());
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(-1, -2);
            dialog.getWindow().setGravity(17);
            dialog.getWindow().setBackgroundDrawableResource(17170445);
        }
        dialog.show();
        dialogDeleteBinding.btnDelete.setOnClickListener(new AnonymousClass9(dialog));
        dialogDeleteBinding.btnCancel.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    
    
    
    public class AnonymousClass9 implements View.OnClickListener {
        final  Dialog val$dialog;

        AnonymousClass9(final Dialog val$dialog) {
            this.val$dialog = val$dialog;
        }

        @Override 
        public void onClick(View v) {
            AddCarActivity.this.binding.progressBar.setVisibility(View.VISIBLE);
            CompositeDisposable compositeDisposable = AddCarActivity.this.disposable;
            final Dialog dialog = this.val$dialog;
            compositeDisposable.add(Observable.fromCallable(new Callable() { 
                @Override 
                public final Object call() throws Exception   {
                    return AddCarActivity.AnonymousClass9.this.m82xac4f3af5(dialog);
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
                @Override 
                public final void accept(Object obj) throws Exception   {
                    AddCarActivity.AnonymousClass9.this.m83x408daa94((Boolean) obj);
                }
            }));
        }

        public  Boolean m82xac4f3af5(Dialog dialog) throws Exception {
            List<ServiceModel> GetAllServiceByCarId = AddCarActivity.this.appDatabase.serviceDao().GetAllServiceByCarId(AddCarActivity.this.carModel.getCarId());
            for (int i = 0; i < GetAllServiceByCarId.size(); i++) {
                List<ImageModel> GetAllImageOfCar = AddCarActivity.this.appDatabase.imageDao().GetAllImageOfCar(GetAllServiceByCarId.get(i).getServiceId());
                for (int i2 = 0; i2 < GetAllImageOfCar.size(); i2++) {
                    AddCarActivity addCarActivity = AddCarActivity.this;
                    AppConstants.deleteImage(addCarActivity, AddCarActivity.this.getResources().getString(R.string.app_name) + InternalZipConstants.ZIP_FILE_SEPARATOR + GetAllImageOfCar.get(i2).getImageName());
                    AddCarActivity.this.appDatabase.imageDao().DeleteImage(GetAllImageOfCar.get(i2));
                }
            }
            AddCarActivity.this.appDatabase.serviceDao().DeleteAllServiceByCarId(AddCarActivity.this.carModel.getCarId());
            List<DocumentModel> GetAllDocumentByCarId = AddCarActivity.this.appDatabase.documentDao().GetAllDocumentByCarId(AddCarActivity.this.carModel.getCarId());
            for (int i3 = 0; i3 < GetAllDocumentByCarId.size(); i3++) {
                List<ImageModel> GetAllImageOfCar2 = AddCarActivity.this.appDatabase.imageDao().GetAllImageOfCar(GetAllDocumentByCarId.get(i3).getDocumentId());
                for (int i4 = 0; i4 < GetAllImageOfCar2.size(); i4++) {
                    AddCarActivity addCarActivity2 = AddCarActivity.this;
                    AppConstants.deleteImage(addCarActivity2, AddCarActivity.this.getResources().getString(R.string.app_name) + InternalZipConstants.ZIP_FILE_SEPARATOR + GetAllImageOfCar2.get(i4).getImageName());
                    AddCarActivity.this.appDatabase.imageDao().DeleteImage(GetAllImageOfCar2.get(i4));
                }
            }
            AddCarActivity.this.appDatabase.documentDao().DeleteAllDocumentByCarId(AddCarActivity.this.carModel.getCarId());
            AddCarActivity.this.appDatabase.reminderDao().DeleteAllReminderByCarId(AddCarActivity.this.carModel.getCarId());
            List<FuelModel> GetAllFuelByCarId = AddCarActivity.this.appDatabase.fuelDao().GetAllFuelByCarId(AddCarActivity.this.carModel.getCarId());
            for (int i5 = 0; i5 < GetAllFuelByCarId.size(); i5++) {
                List<ImageModel> GetAllImageOfCar3 = AddCarActivity.this.appDatabase.imageDao().GetAllImageOfCar(GetAllFuelByCarId.get(i5).getFuelId());
                for (int i6 = 0; i6 < GetAllImageOfCar3.size(); i6++) {
                    AddCarActivity addCarActivity3 = AddCarActivity.this;
                    AppConstants.deleteImage(addCarActivity3, AddCarActivity.this.getResources().getString(R.string.app_name) + InternalZipConstants.ZIP_FILE_SEPARATOR + GetAllImageOfCar3.get(i6).getImageName());
                    AddCarActivity.this.appDatabase.imageDao().DeleteImage(GetAllImageOfCar3.get(i6));
                }
            }
            AddCarActivity.this.appDatabase.fuelDao().DeleteAllFuelByCarId(AddCarActivity.this.carModel.getCarId());
            if (!TextUtils.isEmpty(AddCarActivity.this.carModel.getCarImageName())) {
                AddCarActivity addCarActivity4 = AddCarActivity.this;
                AppConstants.deleteImage(addCarActivity4, AddCarActivity.this.getResources().getString(R.string.app_name) + InternalZipConstants.ZIP_FILE_SEPARATOR + AddCarActivity.this.carModel.getCarImageName());
            }
            List<ImageModel> GetAllImageOfCar4 = AddCarActivity.this.appDatabase.imageDao().GetAllImageOfCar(AddCarActivity.this.carModel.getCarId());
            for (int i7 = 0; i7 < GetAllImageOfCar4.size(); i7++) {
                AddCarActivity addCarActivity5 = AddCarActivity.this;
                AppConstants.deleteImage(addCarActivity5, AddCarActivity.this.getResources().getString(R.string.app_name) + InternalZipConstants.ZIP_FILE_SEPARATOR + GetAllImageOfCar4.get(i7).getImageName());
                AddCarActivity.this.appDatabase.imageDao().DeleteImage(GetAllImageOfCar4.get(i7));
            }
            AddCarActivity.this.appDatabase.carDao().DeleteCar(AddCarActivity.this.carModel);
            AddCarActivity.this.isDelete = true;
            dialog.dismiss();
            return false;
        }

        
        
        public  void m83x408daa94(Boolean bool) throws Exception {
            AddCarActivity.this.binding.progressBar.setVisibility(View.GONE);
            Intent intent = AddCarActivity.this.getIntent();
            intent.putExtra("isDelete", AddCarActivity.this.isDelete);
            AddCarActivity.this.setResult(-1, intent);
            AddCarActivity.this.finish();
        }
    }

    private void openBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        BottomsheetCaptureImageBinding inflate = BottomsheetCaptureImageBinding.inflate(LayoutInflater.from(this), null, false);
        bottomSheetDialog.setContentView(inflate.getRoot());
        bottomSheetDialog.show();
        inflate.llCamera.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                AddCarActivity.this.checkCameraPermission();
            }
        });
        inflate.llGallery.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                AddCarActivity.this.selectImage();
            }
        });
    }

    private void openCarPickerBottom() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        BottomsheetCaptureImageBinding inflate = BottomsheetCaptureImageBinding.inflate(LayoutInflater.from(this), null, false);
        bottomSheetDialog.setContentView(inflate.getRoot());
        bottomSheetDialog.show();
        inflate.llCamera.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                AddCarActivity.this.isFromMainImage = true;
                AddCarActivity.this.checkCameraPermission();
            }
        });
        inflate.llGallery.setOnClickListener(new AnonymousClass14(bottomSheetDialog));
    }

    
    
    
    public class AnonymousClass14 implements View.OnClickListener {
        final  BottomSheetDialog val$bottomSheetDialog;

        AnonymousClass14(final BottomSheetDialog val$bottomSheetDialog) {
            this.val$bottomSheetDialog = val$bottomSheetDialog;
        }

        @Override 
        public void onClick(View v) {
            this.val$bottomSheetDialog.dismiss();
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.setType("image/*");
            AddCarActivity.this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
                @Override 
                public final void onActivityResult(Object obj) {
                    AddCarActivity.AnonymousClass14.this.m80xdd9822e7((ActivityResult) obj);
                }
            });
        }

        
        
        public  void m80xdd9822e7(ActivityResult activityResult) {
            if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
                return;
            }
            Intent data = activityResult.getData();
            AddCarActivity.this.selectedUri = data.getData();
            if (AddCarActivity.this.selectedUri != null) {
                AddCarActivity addCarActivity = AddCarActivity.this;
                new ImageCompressionAsyncTask(addCarActivity, addCarActivity.selectedUri, new ImageListener() {
                    @Override
                    public void onImageReady(String filename) {
                        if (!TextUtils.isEmpty(AddCarActivity.this.selectedImageName)) {
                            AddCarActivity addCarActivity2 = AddCarActivity.this;
                            AppConstants.deleteImage(addCarActivity2, AddCarActivity.this.getResources().getString(R.string.app_name) + InternalZipConstants.ZIP_FILE_SEPARATOR + AddCarActivity.this.selectedImageName);
                        }
                        AddCarActivity.this.selectedImageName = filename;
                        AddCarActivity.this.isMainImageChange = true;
                        AddCarActivity.this.binding.carImage.setVisibility(View.VISIBLE);
                        AddCarActivity.this.binding.cancelImage.setVisibility(View.VISIBLE);
                        AddCarActivity.this.binding.AddImage.setVisibility(View.GONE);
                        AddCarActivity.this.binding.carImage.setImageURI(AddCarActivity.this.selectedUri);
                    }

                    @Override
                    public void onImageError(Throwable error) {

                    }
                });
                return;
            }
            Toast.makeText(AddCarActivity.this, "Can't Retrieve Selected Image", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
        intent.setAction("android.intent.action.GET_CONTENT");
        this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
            @Override 
            public final void onActivityResult(Object obj) {
                AddCarActivity.this.m79x8eb0a063((ActivityResult) obj);
            }
        });
    }

    
    
    public  void m79x8eb0a063(ActivityResult activityResult) {
        final Intent data = activityResult.getData();
        if (data != null) {
            if (data.getClipData() != null) {
                int itemCount = data.getClipData().getItemCount();
                for (int i = 0; i < itemCount; i++) {
                    int finalI = i;
                    new ImageCompressionAsyncTask(this, data.getClipData().getItemAt(i).getUri(), new ImageListener() { 
                        @Override 
                        public void onImageReady(String filename) {
                            AddCarActivity.this.ImageName = filename;
                            AddCarActivity.this.imageUri = data.getClipData().getItemAt(finalI).getUri().toString();
                            AddCarActivity.this.imageModelList.add(new ImageModel(AppConstants.getUniqueId(), AddCarActivity.this.CarId, AddCarActivity.this.ImageName, true, AddCarActivity.this.imageUri));
                            AddCarActivity.this.imageAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onImageError(Throwable error) {

                        }
                    });
                }
                return;
            }
            final Uri data2 = data.getData();
            new ImageCompressionAsyncTask(this, data2, new ImageListener() { 
                @Override 
                public void onImageReady(String filename) {
                    AddCarActivity.this.ImageName = filename;
                    AddCarActivity.this.imageUri = data2.toString();
                    AddCarActivity.this.imageModelList.add(new ImageModel(AppConstants.getUniqueId(), AddCarActivity.this.CarId, AddCarActivity.this.ImageName, true, AddCarActivity.this.imageUri));
                    AddCarActivity.this.imageAdapter.notifyDataSetChanged();
                }

                @Override
                public void onImageError(Throwable error) {

                }
            });
        }
    }

    
    public void checkCameraPermission() {
        if (isHasPermissions(this, "android.permission.CAMERA")) {
            openCamera();
        } else {
            requestPermissions(this, getString(R.string.request_camera), 1, "android.permission.CAMERA");
        }
    }

    private boolean isHasPermissions(Context context, String... permission) {
        return EasyPermissions.hasPermissions(context, permission);
    }

    private void requestPermissions(Context context, String rationale, int requestCode, String... permission) {
        EasyPermissions.requestPermissions((Activity) context, rationale, requestCode, permission);
    }

    @Override 
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == 1) {
            openCamera();
        }
    }

    @Override 
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override 
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    
    @Override 
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 203) {
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            if (resultCode == -1) {
                final Uri uri = activityResult.getUri();
                if (this.isFromMainImage) {
                    new ImageCompressionAsyncTask(this, uri, new ImageListener() { 
                        @Override 
                        public void onImageReady(String filename) {
                            if (!TextUtils.isEmpty(AddCarActivity.this.selectedImageName)) {
                                AddCarActivity addCarActivity = AddCarActivity.this;
                                AppConstants.deleteImage(addCarActivity, AddCarActivity.this.getResources().getString(R.string.app_name) + InternalZipConstants.ZIP_FILE_SEPARATOR + AddCarActivity.this.selectedImageName);
                            }
                            AddCarActivity.this.selectedImageName = filename;
                            AddCarActivity.this.selectedUri = uri;
                            AddCarActivity.this.isMainImageChange = true;
                            AddCarActivity.this.binding.carImage.setVisibility(View.VISIBLE);
                            AddCarActivity.this.binding.cancelImage.setVisibility(View.VISIBLE);
                            AddCarActivity.this.binding.AddImage.setVisibility(View.GONE);
                            AddCarActivity.this.binding.carImage.setImageURI(AddCarActivity.this.selectedUri);
                            AddCarActivity.this.isFromMainImage = false;
                        }

                        @Override
                        public void onImageError(Throwable error) {

                        }
                    });
                } else {
                    new ImageCompressionAsyncTask(this, uri, new ImageListener() { 
                        @Override 
                        public void onImageReady(String filename) {
                            AddCarActivity.this.ImageName = filename;
                            AddCarActivity.this.imageUri = uri.toString();
                            AddCarActivity.this.imageModelList.add(new ImageModel(AppConstants.getUniqueId(), AddCarActivity.this.CarId, AddCarActivity.this.ImageName, true, AddCarActivity.this.imageUri));
                            AddCarActivity.this.imageAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onImageError(Throwable error) {

                        }
                    });
                }
            }
        }
    }
}
