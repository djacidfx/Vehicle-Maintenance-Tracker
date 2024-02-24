package com.demo.carservicetracker2.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.carservicetracker2.AdAdmob;
import com.demo.carservicetracker2.database.models.FuelModel;
import com.demo.carservicetracker2.databinding.ActivityAddFuelBinding;
import com.demo.carservicetracker2.databinding.BottomsheetCaptureImageBinding;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.adapter.FuelNameAdapter;
import com.demo.carservicetracker2.adapter.ImageAdapter;
import com.demo.carservicetracker2.database.AppDatabase;
import com.demo.carservicetracker2.database.models.ImageModel;
import com.demo.carservicetracker2.model.FuelNameModel;
import com.demo.carservicetracker2.utils.AppConstants;
import com.demo.carservicetracker2.utils.BetterActivityResult;
import com.demo.carservicetracker2.utils.DecimalDigitsInputFilter;
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


public class AddFuelActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    String FuelId;
    String ImageName;
    AppDatabase appDatabase;
    ActivityAddFuelBinding binding;
    String carModelId;
    FuelModel fuelModel;
    ImageAdapter imageAdapter;
    String imageUri;
    String mCurrentPhotoPath;
    Point p;
    long fuelDate = 0;
    boolean isForUpdate = false;
    List<ImageModel> imageModelList = new ArrayList();
    CompositeDisposable disposable = new CompositeDisposable();
    public final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = (ActivityAddFuelBinding) DataBindingUtil.setContentView(this, R.layout.activity_add_fuel);
        Init();
        setToolbar();
        setTextCount();
        Clicks();
        this.binding.edtTotalCost.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(12, 2)});
        this.binding.edtPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2)});
        this.binding.edtVolume.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2)});


        AdAdmob adAdmob = new AdAdmob( this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        adAdmob.FullscreenAd(this);


    }

    private void setToolbar() {
        if (this.isForUpdate) {
            this.binding.txtTitle.setText("Edit Fuel");
        } else {
            this.binding.txtTitle.setText("Add Fuel");
        }
    }

    private void Init() {
        this.appDatabase = AppDatabase.getAppDatabase(this);
        this.binding.txtTotalCurrency.setText(AppConstants.GetCurrency());
        TextView textView = this.binding.txtPriceVolume;
        textView.setText(AppConstants.GetCurrency() + InternalZipConstants.ZIP_FILE_SEPARATOR + AppConstants.GetVolumeUnit());
        this.binding.txtVolumeUnit.setText(AppConstants.GetVolumeUnit());
        this.binding.txtMileageUnit.setText(AppConstants.GetDistanceUnit());
        boolean booleanExtra = getIntent().getBooleanExtra("isForUpdate", false);
        this.isForUpdate = booleanExtra;
        if (booleanExtra) {
            this.binding.btnDelete.setVisibility(View.VISIBLE);
            FuelModel fuelModel = (FuelModel) getIntent().getParcelableExtra("FuelModel");
            this.fuelModel = fuelModel;
            this.carModelId = fuelModel.getCarId();
            this.FuelId = this.fuelModel.getFuelId();
            this.fuelDate = this.fuelModel.getDate();
            this.binding.edtFuel.setText(this.fuelModel.getFuelType());
            int length = this.binding.edtFuel.length();
            TextView textView2 = this.binding.txtFuelCnt;
            textView2.setText("" + length);
            if (this.fuelModel.getTotalCost() > Utils.DOUBLE_EPSILON) {
                EditText editText = this.binding.edtTotalCost;
                editText.setText("" + this.fuelModel.getTotalCost());
            }
            if (this.fuelModel.getPricePerLiter() > Utils.DOUBLE_EPSILON) {
                EditText editText2 = this.binding.edtPrice;
                editText2.setText("" + this.fuelModel.getPricePerLiter());
            }
            if (this.fuelModel.getFuelVolume() > Utils.DOUBLE_EPSILON) {
                EditText editText3 = this.binding.edtVolume;
                editText3.setText("" + this.fuelModel.getFuelVolume());
            }
            if (this.fuelModel.getMileage() > 0) {
                EditText editText4 = this.binding.edtMileage;
                editText4.setText("" + this.fuelModel.getMileage());
            }
            this.binding.edtComment.setText(this.fuelModel.getComment());
            int length2 = this.binding.edtComment.length();
            TextView textView3 = this.binding.txtCommentsCnt;
            textView3.setText("" + length2);
            setNestedScroll();
        } else {
            this.binding.btnDelete.setVisibility(View.GONE);
            this.carModelId = getIntent().getStringExtra("CarId");
            this.FuelId = AppConstants.getUniqueId();
            this.fuelDate = AppConstants.GetOnlyDateInMillis(System.currentTimeMillis());
        }
        this.binding.edtDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(this.fuelDate)));
        LoadImageList();
    }

    private void setNestedScroll() {
        this.binding.scroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() { 
            @Override 
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    AddFuelActivity.this.binding.btnDelete.setVisibility(View.GONE);
                }
                if (scrollY < oldScrollY) {
                    AddFuelActivity.this.binding.btnDelete.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setTextCount() {
        this.binding.edtFuel.addTextChangedListener(new TextWatcher() { 
            @Override 
            public void afterTextChanged(Editable editable) {
            }

            @Override 
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override 
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                AddFuelActivity.this.binding.edtFuel.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                AddFuelActivity.this.binding.txtFuelCnt.setText(String.valueOf(AddFuelActivity.this.binding.edtFuel.length()));
            }
        });
        this.binding.edtComment.addTextChangedListener(new TextWatcher() { 
            @Override 
            public void afterTextChanged(Editable editable) {
            }

            @Override 
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override 
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                AddFuelActivity.this.binding.edtComment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1000)});
                AddFuelActivity.this.binding.txtCommentsCnt.setText(String.valueOf(AddFuelActivity.this.binding.edtComment.length()));
            }
        });
    }

    private void LoadImageList() {
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception {
                try {
                    return AddFuelActivity.this.m98x458682b2();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                AddFuelActivity.this.m99xfffc2333((Boolean) obj);
            }
        }));
    }

    
    
    public  Boolean m98x458682b2() throws Exception {
        if (this.isForUpdate) {
            this.imageModelList = this.appDatabase.imageDao().GetAllImageOfCar(this.FuelId);
        }
        return false;
    }

    
    
    public  void m99xfffc2333(Boolean bool) {
        setImageAdapter();
    }

    private void setImageAdapter() {
        this.imageAdapter = new ImageAdapter(this, this.imageModelList, new ImageAdapter.ImageRemoveListener() { 
            @Override 
            public void OnRemoveImage(int position) {
                if (AddFuelActivity.this.imageModelList.get(position).isNew()) {
                    AddFuelActivity.this.imageModelList.remove(position);
                } else {
                    AddFuelActivity.this.imageModelList.get(position).setDeleted(true);
                }
                AddFuelActivity.this.imageAdapter.notifyDataSetChanged();
            }
        }, new AnonymousClass5());
        this.binding.rvImages.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.binding.rvImages.setAdapter(this.imageAdapter);
    }

    
    
    
    public class AnonymousClass5 implements ImageAdapter.OnItemClick {
        AnonymousClass5() {
        }

        @Override 
        public void OnItemClick(int position) {
            Intent intent = new Intent(AddFuelActivity.this, ViewImageActivity.class);
            intent.putParcelableArrayListExtra("imageList", (ArrayList) AddFuelActivity.this.imageModelList);
            intent.putExtra("position", position);
            AddFuelActivity.this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
                @Override 
                public final void onActivityResult(Object obj) {
                    AddFuelActivity.AnonymousClass5.this.m106xdcac0446((ActivityResult) obj);
                }
            });
        }

        
        
        public  void m106xdcac0446(ActivityResult activityResult) {
            Intent data = activityResult.getData();
            if (data == null || !data.getBooleanExtra("isChange", false)) {
                return;
            }
            AddFuelActivity.this.imageModelList.clear();
            AddFuelActivity.this.imageModelList = data.getParcelableArrayListExtra("images");
            AddFuelActivity.this.imageAdapter.setList(AddFuelActivity.this.imageModelList);
        }
    }

    private void openPictureBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        BottomsheetCaptureImageBinding inflate = BottomsheetCaptureImageBinding.inflate(LayoutInflater.from(this), null, false);
        bottomSheetDialog.setContentView(inflate.getRoot());
        bottomSheetDialog.show();
        inflate.llCamera.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                AddFuelActivity.this.checkCameraPermission();
            }
        });
        inflate.llGallery.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                AddFuelActivity.this.selectImage();
            }
        });
    }

    
    public void UpdateImage() {
        this.binding.progressBar.setVisibility(View.VISIBLE);
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception {
                try {
                    return AddFuelActivity.this.m100xc39a63f3();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                AddFuelActivity.this.m101x7e100474((Boolean) obj);
            }
        }));
    }

    
    
    public  Boolean m100xc39a63f3() {
        for (int i = 0; i < this.imageModelList.size(); i++) {
            if (this.imageModelList.get(i).isNew()) {
                this.appDatabase.imageDao().InsertImage(new ImageModel(AppConstants.getUniqueId(), this.FuelId, this.imageModelList.get(i).getImageName()));
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

    
    
    public  void m101x7e100474(Boolean bool)  {
        this.binding.progressBar.setVisibility(View.GONE);
    }

    
    public void InsertImage() {
        if (this.imageModelList.size() > 0) {
            this.binding.progressBar.setVisibility(View.VISIBLE);
            this.disposable.add(Observable.fromCallable(new Callable() { 
                @Override 
                public final Object call() throws Exception {
                    try {
                        return AddFuelActivity.this.m96x5a411505();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
                @Override 
                public final void accept(Object obj) throws Exception {
                    try {
                        AddFuelActivity.this.m97x14b6b586((Boolean) obj);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }));
        }
    }

    
    
    public  Boolean m96x5a411505()  {
        for (int i = 0; i < this.imageModelList.size(); i++) {
            this.appDatabase.imageDao().InsertImage(new ImageModel(AppConstants.getUniqueId(), this.FuelId, this.imageModelList.get(i).getImageName()));
        }
        return false;
    }

    
    
    public  void m97x14b6b586(Boolean bool) throws Exception {
        this.binding.progressBar.setVisibility(View.GONE);
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
                    public final void onActivityResult(Object obj) {
                        AddFuelActivity.this.m102x8aa0722e((ActivityResult) obj);
                    }
                });
            }
        }
    }

    
    
    public  void m102x8aa0722e(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1) {
            CropImage.activity(Uri.fromFile(new File(this.mCurrentPhotoPath))).start(this);
        }
    }

    private File createImageFile() {
        return new File(new File(AppConstants.getTempDirectory(this)), "IMG_" + System.currentTimeMillis() + ".jpg");
    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
        intent.setAction("android.intent.action.GET_CONTENT");
        this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
            @Override 
            public final void onActivityResult(Object obj) {
                AddFuelActivity.this.m103x42fc3185((ActivityResult) obj);
            }
        });
    }

    
    
    public  void m103x42fc3185(ActivityResult activityResult) {
        final Intent data = activityResult.getData();
        if (data != null) {
            if (data.getClipData() != null) {
                int itemCount = data.getClipData().getItemCount();
                for (int i = 0; i < itemCount; i++) {
                    int finalI = i;
                    new ImageCompressionAsyncTask(this, data.getClipData().getItemAt(i).getUri(), new ImageListener() { 
                        @Override 
                        public void onImageReady(String filename) {
                            AddFuelActivity.this.ImageName = filename;
                            AddFuelActivity.this.imageUri = data.getClipData().getItemAt(finalI).getUri().toString();
                            AddFuelActivity.this.imageModelList.add(new ImageModel(AppConstants.getUniqueId(), AddFuelActivity.this.FuelId, AddFuelActivity.this.ImageName, true, AddFuelActivity.this.imageUri));
                            AddFuelActivity.this.imageAdapter.notifyDataSetChanged();
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
                    AddFuelActivity.this.ImageName = filename;
                    AddFuelActivity.this.imageUri = data2.toString();
                    AddFuelActivity.this.imageModelList.add(new ImageModel(AppConstants.getUniqueId(), AddFuelActivity.this.FuelId, AddFuelActivity.this.ImageName, true, AddFuelActivity.this.imageUri));
                    AddFuelActivity.this.imageAdapter.notifyDataSetChanged();
                }

                @Override
                public void onImageError(Throwable error) {

                }
            });
        }
    }

    private void Clicks() {
        this.binding.back.setOnClickListener(this);
        this.binding.btnSave.setOnClickListener(this);
        this.binding.edtDate.setOnClickListener(this);
        this.binding.calendar.setOnClickListener(this);
        this.binding.btnSelect.setOnClickListener(this);
        this.binding.btnDelete.setOnClickListener(this);
        this.binding.openFuelType.setOnClickListener(this);
        this.binding.edtTotalCost.setOnClickListener(this);
        this.binding.edtFuel.setOnClickListener(this);
        this.binding.edtPrice.setOnClickListener(this);
        this.binding.edtVolume.setOnClickListener(this);
        this.binding.edtMileage.setOnClickListener(this);
        this.binding.edtComment.setOnClickListener(this);
    }

    @Override 
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back :
                onBackPressed();
                return;
            case R.id.btnDelete :
                CheckAndSetFuelCost();
                openDeleteDialog();
                return;
            case R.id.btnSave :
                CheckAndSetFuelCost();
                InsertFuel();
                return;
            case R.id.btnSelect :
                CheckAndSetFuelCost();
                openPictureBottomSheet();
                return;
            case R.id.calendar :
            case R.id.edtDate :
                CheckAndSetFuelCost();
                final Calendar calendar = Calendar.getInstance();
                if (this.isForUpdate) {
                    long j = this.fuelDate;
                    if (j > 0) {
                        calendar.setTimeInMillis(j);
                        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() { 
                            @Override 
                            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                                calendar.set(1, year);
                                calendar.set(2, month);
                                calendar.set(5, date);
                                AddFuelActivity.this.fuelDate = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                                AddFuelActivity.this.binding.edtDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(AddFuelActivity.this.fuelDate)));
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
                        AddFuelActivity.this.fuelDate = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                        AddFuelActivity.this.binding.edtDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(AddFuelActivity.this.fuelDate)));
                    }
                }, calendar.get(1), calendar.get(2), calendar.get(5)).show();
                return;
            case R.id.edtComment :
            case R.id.edtMileage :
            case R.id.edtPrice :
            case R.id.edtTotalCost :
            case R.id.edtVolume :
                CheckAndSetFuelCost();
                return;
            case R.id.edtFuel :
            case R.id.open_fuel_type :
                CheckAndSetFuelCost();
                Point point = this.p;
                if (point != null) {
                    showStatusPopup(this, point);
                }
                setTextCount();
                return;
            default:
                return;
        }
    }

    
    
    
    
    
    
    
    
    
    
    private void CheckAndSetFuelCost() {
        double parseDouble = 0;
        double parseDouble2 = 0;
        double parseDouble3 = 0;
        int i;
        int i2;
        String obj = this.binding.edtTotalCost.getText().toString();
        String obj2 = this.binding.edtPrice.getText().toString();
        String obj3 = this.binding.edtVolume.getText().toString();
        if (!obj.isEmpty()) {
            try {
                parseDouble = Double.parseDouble(obj);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (!obj2.isEmpty()) {
                try {
                    parseDouble2 = Double.parseDouble(obj2);
                } catch (NumberFormatException e2) {
                    e2.printStackTrace();
                }
                if (!obj3.isEmpty()) {
                    try {
                        parseDouble3 = Double.parseDouble(obj3);
                    } catch (NumberFormatException e3) {
                        e3.printStackTrace();
                    }
                    i = (parseDouble2 > Utils.DOUBLE_EPSILON ? 1 : (parseDouble2 == Utils.DOUBLE_EPSILON ? 0 : -1));
                    if (i <= 0 && parseDouble3 > Utils.DOUBLE_EPSILON) {
                        this.binding.edtTotalCost.setText("" + (parseDouble2 * parseDouble3));
                        this.binding.edtPrice.setText("" + parseDouble2);
                        this.binding.edtVolume.setText("" + parseDouble3);
                        return;
                    }
                    i2 = (parseDouble3 > Utils.DOUBLE_EPSILON ? 1 : (parseDouble3 == Utils.DOUBLE_EPSILON ? 0 : -1));
                    if (i2 != 0 && i > 0 && parseDouble == Utils.DOUBLE_EPSILON) {
                        this.binding.edtTotalCost.setText("");
                        this.binding.edtPrice.setText("" + parseDouble2);
                        this.binding.edtVolume.setText("");
                        return;
                    } else if (i2 <= 0 && i == 0 && parseDouble == Utils.DOUBLE_EPSILON) {
                        this.binding.edtTotalCost.setText("");
                        this.binding.edtPrice.setText("");
                        this.binding.edtVolume.setText("" + parseDouble3);
                        return;
                    } else if (i2 != 0 && i == 0 && parseDouble > Utils.DOUBLE_EPSILON) {
                        this.binding.edtTotalCost.setText("" + parseDouble);
                        this.binding.edtPrice.setText("");
                        this.binding.edtVolume.setText("");
                        return;
                    } else if (i2 <= 0 && i == 0 && parseDouble > Utils.DOUBLE_EPSILON) {
                        this.binding.edtTotalCost.setText("" + parseDouble);
                        this.binding.edtPrice.setText("" + (parseDouble / parseDouble3));
                        this.binding.edtVolume.setText("" + parseDouble3);
                        return;
                    } else if (i2 == 0 || i <= 0 || parseDouble <= Utils.DOUBLE_EPSILON) {
                        return;
                    } else {
                        double d = parseDouble / parseDouble2;
                        this.binding.edtTotalCost.setText("" + parseDouble);
                        this.binding.edtPrice.setText("" + parseDouble2);
                        this.binding.edtVolume.setText("" + d);
                        return;
                    }
                }
                parseDouble3 = 0.0d;
                i = (parseDouble2 > Utils.DOUBLE_EPSILON ? 1 : (parseDouble2 == Utils.DOUBLE_EPSILON ? 0 : -1));
                if (i <= 0) {
                }
                i2 = (parseDouble3 > Utils.DOUBLE_EPSILON ? 1 : (parseDouble3 == Utils.DOUBLE_EPSILON ? 0 : -1));
                if (i2 != 0) {
                }
                if (i2 <= 0) {
                }
                if (i2 != 0) {
                }
                if (i2 <= 0) {
                }
                if (i2 == 0) {
                    return;
                }
                return;
            }
            parseDouble2 = 0.0d;
            if (!obj3.isEmpty()) {
            }
            parseDouble3 = 0.0d;
            i = (parseDouble2 > Utils.DOUBLE_EPSILON ? 1 : (parseDouble2 == Utils.DOUBLE_EPSILON ? 0 : -1));
            if (i <= 0) {
            }
            i2 = (parseDouble3 > Utils.DOUBLE_EPSILON ? 1 : (parseDouble3 == Utils.DOUBLE_EPSILON ? 0 : -1));
            if (i2 != 0) {
            }
            if (i2 <= 0) {
            }
            if (i2 != 0) {
            }
            if (i2 <= 0) {
            }
            if (i2 == 0) {
            }
        }
        parseDouble = 0.0d;
        if (!obj2.isEmpty()) {
        }
        parseDouble2 = 0.0d;
        if (!obj3.isEmpty()) {
        }
        parseDouble3 = 0.0d;
        i = (parseDouble2 > Utils.DOUBLE_EPSILON ? 1 : (parseDouble2 == Utils.DOUBLE_EPSILON ? 0 : -1));
        if (i <= 0) {
        }
        i2 = (parseDouble3 > Utils.DOUBLE_EPSILON ? 1 : (parseDouble3 == Utils.DOUBLE_EPSILON ? 0 : -1));
        if (i2 != 0) {
        }
        if (i2 <= 0) {
        }
        if (i2 != 0) {
        }
        if (i2 <= 0) {
        }
        if (i2 == 0) {
        }
    }

    private void InsertFuel() {
        final String edtTotalCost = this.binding.edtTotalCost.getText().toString();
        final String edtFuel = this.binding.edtFuel.getText().toString();
        final String edtPrice = this.binding.edtPrice.getText().toString();
        final String edtVolume = this.binding.edtVolume.getText().toString();
        final String edtMileage = this.binding.edtMileage.getText().toString();
        final String edtComment = this.binding.edtComment.getText().toString();

        if (!TextUtils.isEmpty(edtFuel) || !TextUtils.isEmpty(edtMileage) || !TextUtils.isEmpty(edtTotalCost) || !TextUtils.isEmpty(edtComment) || !TextUtils.isEmpty(edtVolume) || !TextUtils.isEmpty(edtPrice)) {
                    double parseDouble = 0;
                    double parseDouble2 = 0;
                    long parseLong = !edtMileage.isEmpty() ? Long.parseLong(edtMileage) : 0L;
                    boolean isEmpty = edtTotalCost.isEmpty();
                    double d = Utils.DOUBLE_EPSILON;
                    if (!isEmpty) {
                        try {
                            parseDouble = Double.parseDouble(edtTotalCost);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        if (!edtPrice.isEmpty()) {
                            try {
                                parseDouble2 = Double.parseDouble(edtPrice);
                            } catch (NumberFormatException e2) {
                                e2.printStackTrace();
                            }
                            if (!edtVolume.isEmpty()) {
                                try {
                                    d = Double.parseDouble(edtVolume);
                                } catch (NumberFormatException e3) {
                                    e3.printStackTrace();
                                }
                            }
                            double d2 = d;
                            if (AddFuelActivity.this.isForUpdate) {
                                AddFuelActivity.this.fuelModel.setTotalCost(parseDouble);
                                AddFuelActivity.this.fuelModel.setFuelType(edtFuel);
                                AddFuelActivity.this.fuelModel.setPricePerLiter(parseDouble2);
                                AddFuelActivity.this.fuelModel.setFuelVolume(d2);
                                AddFuelActivity.this.fuelModel.setMileage(parseLong);
                                AddFuelActivity.this.fuelModel.setDate(AddFuelActivity.this.fuelDate);
                                AddFuelActivity.this.fuelModel.setComment(edtComment);
                                AddFuelActivity.this.appDatabase.fuelDao().UpdateFuel(AddFuelActivity.this.fuelModel);
                                AddFuelActivity.this.UpdateImage();
                                Intent intent = AddFuelActivity.this.getIntent();
                                intent.putExtra("FuelModel", AddFuelActivity.this.fuelModel);
                                AddFuelActivity.this.setResult(-1, intent);
                                AddFuelActivity.this.finish();
                                return;
                            }
                            FuelModel fuelModel = new FuelModel(AddFuelActivity.this.FuelId, parseDouble, edtFuel, parseDouble2, d2, parseLong, AddFuelActivity.this.fuelDate, edtComment, AddFuelActivity.this.carModelId);
                            AddFuelActivity.this.appDatabase.fuelDao().InsertFuel(fuelModel);
                            AddFuelActivity.this.InsertImage();
                            Intent intent2 = AddFuelActivity.this.getIntent();
                            intent2.putExtra("FuelModel", fuelModel);
                            AddFuelActivity.this.setResult(-1, intent2);
                            AddFuelActivity.this.finish();
                            return;
                        }
                        parseDouble2 = 0.0d;
                        if (!edtVolume.isEmpty()) {
                        }
                        double d22 = d;
                        if (AddFuelActivity.this.isForUpdate) {
                        }
                    }
                    parseDouble = 0.0d;
                    if (!edtPrice.isEmpty()) {
                    }
                    parseDouble2 = 0.0d;
                    if (!edtVolume.isEmpty()) {
                    }
                    double d222 = d;
                    if (AddFuelActivity.this.isForUpdate) {
                    }
                }
            return;
    }

    private void openDeleteDialog() {
        com.demo.carservicetracker2.databinding.DialogDeleteBinding dialogDeleteBinding = (com.demo.carservicetracker2.databinding.DialogDeleteBinding) DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_delete, null, false);
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
        dialogDeleteBinding.btnDelete.setOnClickListener(new AnonymousClass12(dialog));
        dialogDeleteBinding.btnCancel.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    
    
    
    public class AnonymousClass12 implements View.OnClickListener {
        final  Dialog val$dialog;

        AnonymousClass12(final Dialog val$dialog) {
            this.val$dialog = val$dialog;
        }

        @Override 
        public void onClick(View v) {
            AddFuelActivity.this.binding.progressBar.setVisibility(View.VISIBLE);
            CompositeDisposable compositeDisposable = AddFuelActivity.this.disposable;
            Observable observeOn = Observable.fromCallable(new Callable() { 
                @Override 
                public final Object call() throws Exception {
                    try {
                        return AnonymousClass12.this.m104x6c8e991d();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            final Dialog dialog = this.val$dialog;
            compositeDisposable.add(observeOn.subscribe(new Consumer() { 
                @Override 
                public final void accept(Object obj) throws Exception {
                    try {
                        AnonymousClass12.this.m105xeaef9cfc(dialog, (Boolean) obj);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }));
        }

        
        
        public  Boolean m104x6c8e991d() throws Exception {
            List<ImageModel> GetAllImageOfCar = AddFuelActivity.this.appDatabase.imageDao().GetAllImageOfCar(AddFuelActivity.this.fuelModel.getFuelId());
            for (int i = 0; i < GetAllImageOfCar.size(); i++) {
                AddFuelActivity addFuelActivity = AddFuelActivity.this;
                AppConstants.deleteImage(addFuelActivity, AddFuelActivity.this.getResources().getString(R.string.app_name) + InternalZipConstants.ZIP_FILE_SEPARATOR + GetAllImageOfCar.get(i).getImageName());
                AddFuelActivity.this.appDatabase.imageDao().DeleteImage(GetAllImageOfCar.get(i));
            }
            AddFuelActivity.this.appDatabase.fuelDao().DeleteFuel(AddFuelActivity.this.fuelModel);
            return false;
        }

        
        
        public  void m105xeaef9cfc(Dialog dialog, Boolean bool) throws Exception {
            AddFuelActivity.this.binding.progressBar.setVisibility(View.GONE);
            dialog.dismiss();
            Intent intent = AddFuelActivity.this.getIntent();
            intent.putExtra("isDelete", true);
            AddFuelActivity.this.setResult(-1, intent);
            AddFuelActivity.this.finish();
        }
    }

    @Override 
    public void onWindowFocusChanged(boolean hasFocus) {
        int[] iArr = new int[2];
        this.binding.edtFuel.getLocationOnScreen(iArr);
        Point point = new Point();
        this.p = point;
        point.x = iArr[0];
        this.p.y = iArr[1];
    }

    private void showStatusPopup(final Activity context, Point p) {
        View inflate = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fuel_list_layout, (ViewGroup) null);
        final PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setContentView(inflate);
        popupWindow.setAnimationStyle(R.style.popup_window_animation_sms);
        popupWindow.setWidth(-2);
        popupWindow.setHeight(-2);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(inflate, 0, p.x - 20, p.y + 50);
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.fuelRecycle);
        final List<FuelNameModel> GetAllFuelName = AppConstants.GetAllFuelName();
        FuelNameAdapter fuelNameAdapter = new FuelNameAdapter(this, GetAllFuelName, new FuelNameAdapter.FuelClick() { 
            @Override 
            public void OnFuelClick(int position) {
                AddFuelActivity.this.binding.edtFuel.setText(((FuelNameModel) GetAllFuelName.get(position)).getName());
                popupWindow.dismiss();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(fuelNameAdapter);
    }

    
    @Override 
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 203) {
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            if (resultCode == -1) {
                final Uri uri = activityResult.getUri();
                new ImageCompressionAsyncTask(this, uri, new ImageListener() {

                    @Override
                    public void onImageError(Throwable error) {

                    } 
                    @Override 
                    public void onImageReady(String filename) {
                        AddFuelActivity.this.ImageName = filename;
                        AddFuelActivity.this.imageUri = uri.toString();
                        AddFuelActivity.this.imageModelList.add(new ImageModel(AppConstants.getUniqueId(), AddFuelActivity.this.FuelId, AddFuelActivity.this.ImageName, true, AddFuelActivity.this.imageUri));
                        AddFuelActivity.this.imageAdapter.notifyDataSetChanged();
                    }
                });
            }
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
}
