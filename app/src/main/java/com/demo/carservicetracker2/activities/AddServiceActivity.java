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
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.demo.carservicetracker2.databinding.ActivityAddServiceBinding;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.adapter.ImageAdapter;
import com.demo.carservicetracker2.database.AppDatabase;
import com.demo.carservicetracker2.database.models.ImageModel;
import com.demo.carservicetracker2.database.models.ServiceModel;
import com.demo.carservicetracker2.model.ServiceDocumentModel;
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


public class AddServiceActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    String ImageName;
    String ServiceId;
    AppDatabase appDatabase;
    ActivityAddServiceBinding binding;
    String carModelId;
    ImageAdapter imageAdapter;
    String imageUri;
    String mCurrentPhotoPath;
    ServiceDocumentModel selectedServiceModel;
    ServiceModel serviceModel;
    long serviceDate = 0;
    boolean isForUpdate = false;
    List<ImageModel> imageModelList = new ArrayList();
    CompositeDisposable disposable = new CompositeDisposable();
    public final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = (ActivityAddServiceBinding) DataBindingUtil.setContentView(this, R.layout.activity_add_service);
        Init();
        setToolbar();
        setTextCount();
        Clicks();
        this.binding.edtParts.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(9, 2)});
        this.binding.edtLabour.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(9, 2)});


        AdAdmob adAdmob = new AdAdmob( this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        adAdmob.FullscreenAd(this);



    }

    private void setToolbar() {
        if (this.isForUpdate) {
            this.binding.txtTitle.setText("Edit Service");
        } else {
            this.binding.txtTitle.setText("Add Service");
        }
    }

    private void Init() {
        this.appDatabase = AppDatabase.getAppDatabase(this);
        this.binding.txtMileage.setText(AppConstants.GetDistanceUnit());
        this.binding.txtPartsCurrency.setText(AppConstants.GetCurrency());
        this.binding.txtLabourCurrency.setText(AppConstants.GetCurrency());
        this.binding.txtTotalCurrency.setText(AppConstants.GetCurrency());
        boolean booleanExtra = getIntent().getBooleanExtra("isForUpdate", false);
        this.isForUpdate = booleanExtra;
        if (booleanExtra) {
            this.binding.btnDelete.setVisibility(View.VISIBLE);
            ServiceModel serviceModel = (ServiceModel) getIntent().getParcelableExtra("ServiceModel");
            this.serviceModel = serviceModel;
            this.carModelId = serviceModel.getCarId();
            this.ServiceId = this.serviceModel.getServiceId();
            ServiceDocumentModel serviceDocumentModel = AppConstants.GetAllStaticService().get(AppConstants.GetAllStaticService().indexOf(new ServiceDocumentModel(this.serviceModel.getSelectedServiceID())));
            this.selectedServiceModel = serviceDocumentModel;
            if (isMileage(serviceDocumentModel)) {
                this.binding.llBottom.setVisibility(View.GONE);
            } else {
                this.binding.llBottom.setVisibility(View.VISIBLE);
            }
            this.binding.serviceImage.setImageResource(AppConstants.GetDrawable(this.selectedServiceModel.getIconName()));
            this.serviceDate = this.serviceModel.getDate();
            this.binding.edtServiceName.setText(this.serviceModel.getServiceName());
            int length = this.binding.edtServiceName.length();
            TextView textView = this.binding.txtServiceNameCnt;
            textView.setText("" + length);
            if (this.serviceModel.getMileage() > 0) {
                EditText editText = this.binding.edtMileage;
                editText.setText("" + this.serviceModel.getMileage());
            }
            if (this.serviceModel.getPartsCosts() > Utils.DOUBLE_EPSILON) {
                EditText editText2 = this.binding.edtParts;
                editText2.setText("" + this.serviceModel.getPartsCosts());
            }
            if (this.serviceModel.getLabourCosts() > Utils.DOUBLE_EPSILON) {
                EditText editText3 = this.binding.edtLabour;
                editText3.setText("" + this.serviceModel.getLabourCosts());
            }
            this.binding.edtVendorCodes.setText(this.serviceModel.getVendorCodes());
            int length2 = this.binding.edtVendorCodes.length();
            TextView textView2 = this.binding.txtVendorsCnt;
            textView2.setText("" + length2);
            this.binding.edtComment.setText(this.serviceModel.getComments());
            int length3 = this.binding.edtComment.length();
            TextView textView3 = this.binding.txtCommentsCnt;
            textView3.setText("" + length3);
            SetCostData();
            setNestedScroll();
        } else {
            this.binding.btnDelete.setVisibility(View.GONE);
            this.carModelId = getIntent().getStringExtra("CarId");
            this.ServiceId = AppConstants.getUniqueId();
            if (getIntent().getParcelableExtra("ServiceDocModel") != null) {
                this.selectedServiceModel = (ServiceDocumentModel) getIntent().getParcelableExtra("ServiceDocModel");
            } else {
                this.selectedServiceModel = AppConstants.GetAllStaticService().get(AppConstants.GetAllStaticService().size() - 1);
            }
            this.binding.serviceImage.setImageResource(AppConstants.GetDrawable(this.selectedServiceModel.getIconName()));
            this.binding.edtServiceName.setText(this.selectedServiceModel.getName());
            this.binding.txtServiceNameCnt.setText(String.valueOf(this.binding.edtServiceName.length()));
            this.serviceDate = AppConstants.GetOnlyDateInMillis(System.currentTimeMillis());
        }
        this.binding.txtDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(this.serviceDate)));
        LoadImageList();
    }

    private void setNestedScroll() {
        this.binding.scroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() { 
            @Override 
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    AddServiceActivity.this.binding.btnDelete.setVisibility(View.GONE);
                }
                if (scrollY < oldScrollY) {
                    AddServiceActivity.this.binding.btnDelete.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setTextCount() {
        this.binding.edtServiceName.addTextChangedListener(new TextWatcher() { 
            @Override 
            public void afterTextChanged(Editable editable) {
            }

            @Override 
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override 
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                AddServiceActivity.this.binding.edtServiceName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                AddServiceActivity.this.binding.txtServiceNameCnt.setText(String.valueOf(AddServiceActivity.this.binding.edtServiceName.length()));
            }
        });
        this.binding.edtVendorCodes.addTextChangedListener(new TextWatcher() { 
            @Override 
            public void afterTextChanged(Editable editable) {
            }

            @Override 
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override 
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                AddServiceActivity.this.binding.edtVendorCodes.setFilters(new InputFilter[]{new InputFilter.LengthFilter(300)});
                AddServiceActivity.this.binding.txtVendorsCnt.setText(String.valueOf(AddServiceActivity.this.binding.edtVendorCodes.length()));
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
                AddServiceActivity.this.binding.edtComment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1000)});
                AddServiceActivity.this.binding.txtCommentsCnt.setText(String.valueOf(AddServiceActivity.this.binding.edtComment.length()));
            }
        });
    }

    private void LoadImageList() {
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception {
                return AddServiceActivity.this.m110x19a149f7();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                AddServiceActivity.this.m111x98024dd6((Boolean) obj);
            }
        }));
    }

    
    
    public  Boolean m110x19a149f7() throws Exception {
        if (this.isForUpdate) {
            this.imageModelList = this.appDatabase.imageDao().GetAllImageOfCar(this.ServiceId);
        }
        return false;
    }

    
    
    public  void m111x98024dd6(Boolean bool) throws Exception {
        setImageAdapter();
    }

    private void setImageAdapter() {
        this.imageAdapter = new ImageAdapter(this, this.imageModelList, new ImageAdapter.ImageRemoveListener() { 
            @Override 
            public void OnRemoveImage(int position) {
                if (AddServiceActivity.this.imageModelList.get(position).isNew()) {
                    AddServiceActivity.this.imageModelList.remove(position);
                } else {
                    AddServiceActivity.this.imageModelList.get(position).setDeleted(true);
                }
                AddServiceActivity.this.imageAdapter.notifyDataSetChanged();
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
            Intent intent = new Intent(AddServiceActivity.this, ViewImageActivity.class);
            intent.putParcelableArrayListExtra("imageList", (ArrayList) AddServiceActivity.this.imageModelList);
            intent.putExtra("position", position);
            AddServiceActivity.this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
                @Override 
                public final void onActivityResult(Object obj) {
                    AddServiceActivity.AnonymousClass6.this.m119xc62f3866((ActivityResult) obj);
                }
            });
        }

        
        
        public  void m119xc62f3866(ActivityResult activityResult) {
            Intent data = activityResult.getData();
            if (data == null || !data.getBooleanExtra("isChange", false)) {
                return;
            }
            AddServiceActivity.this.imageModelList.clear();
            AddServiceActivity.this.imageModelList = data.getParcelableArrayListExtra("images");
            AddServiceActivity.this.imageAdapter.setList(AddServiceActivity.this.imageModelList);
        }
    }

    public boolean isMileage(ServiceDocumentModel serviceDocumentModel) {
        return serviceDocumentModel.getId().equals("Service_21");
    }

    private void openPictureBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        com.demo.carservicetracker2.databinding.BottomsheetCaptureImageBinding inflate = com.demo.carservicetracker2.databinding.BottomsheetCaptureImageBinding.inflate(LayoutInflater.from(this), null, false);
        bottomSheetDialog.setContentView(inflate.getRoot());
        bottomSheetDialog.show();
        inflate.llCamera.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                AddServiceActivity.this.checkCameraPermission();
            }
        });
        inflate.llGallery.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                AddServiceActivity.this.selectImage();
            }
        });
    }

    private void UpdateImage() {
        this.binding.progressBar.setVisibility(View.VISIBLE);
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception {
                return AddServiceActivity.this.m112xe5135516();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                AddServiceActivity.this.m113x637458f5((Boolean) obj);
            }
        }));
    }

    
    
    public  Boolean m112xe5135516() throws Exception {
        for (int i = 0; i < this.imageModelList.size(); i++) {
            if (this.imageModelList.get(i).isNew()) {
                this.appDatabase.imageDao().InsertImage(new ImageModel(AppConstants.getUniqueId(), this.ServiceId, this.imageModelList.get(i).getImageName()));
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

    
    
    public  void m113x637458f5(Boolean bool) throws Exception {
        this.binding.progressBar.setVisibility(View.GONE);
    }

    private void InsertImage() {
        if (this.imageModelList.size() > 0) {
            this.binding.progressBar.setVisibility(View.VISIBLE);
            this.disposable.add(Observable.fromCallable(new Callable() { 
                @Override 
                public final Object call() throws Exception {
                    return AddServiceActivity.this.m108x552b32c4();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
                @Override 
                public final void accept(Object obj) throws Exception {
                    AddServiceActivity.this.m109xd38c36a3((Boolean) obj);
                }
            }));
        }
    }

    
    
    public  Boolean m108x552b32c4() throws Exception {
        for (int i = 0; i < this.imageModelList.size(); i++) {
            this.appDatabase.imageDao().InsertImage(new ImageModel(AppConstants.getUniqueId(), this.ServiceId, this.imageModelList.get(i).getImageName()));
        }
        return false;
    }

    
    
    public  void m109xd38c36a3(Boolean bool) throws Exception {
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
                        AddServiceActivity.this.m115x7ec558fb((ActivityResult) obj);
                    }
                });
            }
        }
    }

    
    
    public  void m115x7ec558fb(ActivityResult activityResult) {
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
                AddServiceActivity.this.m116x7b84c644((ActivityResult) obj);
            }
        });
    }

    
    
    public  void m116x7b84c644(ActivityResult activityResult) {
        final Intent data = activityResult.getData();
        if (data != null) {
            if (data.getClipData() != null) {
                int itemCount = data.getClipData().getItemCount();
                for (int i = 0; i < itemCount; i++) {
                    int finalI = i;
                    new ImageCompressionAsyncTask(this, data.getClipData().getItemAt(i).getUri(), new ImageListener() { 
                        @Override 
                        public void onImageReady(String filename) {
                            AddServiceActivity.this.ImageName = filename;
                            AddServiceActivity.this.imageUri = data.getClipData().getItemAt(finalI).getUri().toString();
                            AddServiceActivity.this.imageModelList.add(new ImageModel(AppConstants.getUniqueId(), AddServiceActivity.this.ServiceId, AddServiceActivity.this.ImageName, true, AddServiceActivity.this.imageUri));
                            AddServiceActivity.this.imageAdapter.notifyDataSetChanged();
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
                public void onImageError(Throwable error) {

                } 
                @Override 
                public void onImageReady(String filename) {
                    AddServiceActivity.this.ImageName = filename;
                    AddServiceActivity.this.imageUri = data2.toString();
                    AddServiceActivity.this.imageModelList.add(new ImageModel(AppConstants.getUniqueId(), AddServiceActivity.this.ServiceId, AddServiceActivity.this.ImageName, true, AddServiceActivity.this.imageUri));
                    AddServiceActivity.this.imageAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    
    
    
    
    
    public void InsertService() {
        double parseDouble = 0;
        double parseDouble2 = 0;
        String obj = this.binding.edtServiceName.getText().toString();
        String obj2 = this.binding.edtMileage.getText().toString();
        String obj3 = this.binding.edtParts.getText().toString();
        String obj4 = this.binding.edtLabour.getText().toString();
        String obj5 = this.binding.edtVendorCodes.getText().toString();
        String obj6 = this.binding.edtComment.getText().toString();
        long parseLong = !obj2.isEmpty() ? Long.parseLong(obj2) : 0L;
        if (!obj3.isEmpty()) {
            try {
                parseDouble = Double.parseDouble(obj3);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (!obj4.isEmpty()) {
                try {
                    parseDouble2 = Double.parseDouble(obj4);
                } catch (NumberFormatException e2) {
                    e2.printStackTrace();
                }
                if (this.isForUpdate) {
                    this.serviceModel.setSelectedServiceID(this.selectedServiceModel.getId());
                    this.serviceModel.setServiceName(obj);
                    this.serviceModel.setMileage(parseLong);
                    this.serviceModel.setDate(this.serviceDate);
                    ServiceModel serviceModel = this.serviceModel;
                    if (isMileage(this.selectedServiceModel)) {
                        parseDouble = Utils.DOUBLE_EPSILON;
                    }
                    serviceModel.setPartsCosts(parseDouble);
                    this.serviceModel.setLabourCosts(isMileage(this.selectedServiceModel) ? Utils.DOUBLE_EPSILON : parseDouble2);
                    ServiceModel serviceModel2 = this.serviceModel;
                    if (isMileage(this.selectedServiceModel)) {
                        obj5 = "";
                    }
                    serviceModel2.setVendorCodes(obj5);
                    ServiceModel serviceModel3 = this.serviceModel;
                    if (isMileage(this.selectedServiceModel)) {
                        obj6 = "";
                    }
                    serviceModel3.setComments(obj6);
                    this.appDatabase.serviceDao().UpdateService(this.serviceModel);
                    UpdateImage();
                    Intent intent = getIntent();
                    intent.putExtra("ServiceModel", this.serviceModel);
                    setResult(-1, intent);
                    finish();
                    return;
                }
                String str = this.ServiceId;
                String id = this.selectedServiceModel.getId();
                double d = parseDouble;
                long j = this.serviceDate;
                if (isMileage(this.selectedServiceModel)) {
                    d = Utils.DOUBLE_EPSILON;
                }
                ServiceModel serviceModel4 = new ServiceModel(str, id, obj, parseLong, j, d, isMileage(this.selectedServiceModel) ? Utils.DOUBLE_EPSILON : parseDouble2, isMileage(this.selectedServiceModel) ? "" : obj5, isMileage(this.selectedServiceModel) ? "" : obj6, this.carModelId);
                this.appDatabase.serviceDao().InsertService(serviceModel4);
                InsertImage();
                Intent intent2 = getIntent();
                intent2.putExtra("ServiceModel", serviceModel4);
                intent2.putExtra("ServiceDocModel", this.selectedServiceModel);
                setResult(-1, intent2);
                finish();
                return;
            }
            parseDouble2 = Utils.DOUBLE_EPSILON;
            if (this.isForUpdate) {
            }
        }
        parseDouble = Utils.DOUBLE_EPSILON;
        if (!obj4.isEmpty()) {
        }
        parseDouble2 = Utils.DOUBLE_EPSILON;
        if (this.isForUpdate) {
        }
    }

    private void Clicks() {
        this.binding.back.setOnClickListener(this);
        this.binding.btnSave.setOnClickListener(this);
        this.binding.mainImageCard.setOnClickListener(this);
        this.binding.llDate.setOnClickListener(this);
        this.binding.btnSelect.setOnClickListener(this);
        this.binding.btnDelete.setOnClickListener(this);
        this.binding.edtServiceName.setOnClickListener(this);
        this.binding.edtMileage.setOnClickListener(this);
        this.binding.txtDate.setOnClickListener(this);
        this.binding.edtParts.setOnClickListener(this);
        this.binding.edtLabour.setOnClickListener(this);
        this.binding.txtTotal.setOnClickListener(this);
        this.binding.edtVendorCodes.setOnClickListener(this);
        this.binding.edtComment.setOnClickListener(this);
    }

    @Override 
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back :
                onBackPressed();
                return;
            case R.id.btnDelete :
                SetCostData();
                openDeleteDialog();
                return;
            case R.id.btnSave :
                SetCostData();
                AddServiceActivity.this.InsertService();
                return;
            case R.id.btnSelect :
                SetCostData();
                openPictureBottomSheet();
                return;
            case R.id.edtComment :
            case R.id.edtLabour :
            case R.id.edtMileage :
            case R.id.edtParts :
            case R.id.edtServiceName :
            case R.id.edtVendorCodes :
            case R.id.txtDate :
            case R.id.txtTotal :
                SetCostData();
                return;
            case R.id.llDate :
                SetCostData();
                final Calendar calendar = Calendar.getInstance();
                if (this.isForUpdate) {
                    long j = this.serviceDate;
                    if (j > 0) {
                        calendar.setTimeInMillis(j);
                        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() { 
                            @Override 
                            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                                calendar.set(1, year);
                                calendar.set(2, month);
                                calendar.set(5, date);
                                AddServiceActivity.this.serviceDate = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                                AddServiceActivity.this.binding.txtDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(AddServiceActivity.this.serviceDate)));
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
                        AddServiceActivity.this.serviceDate = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                        AddServiceActivity.this.binding.txtDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(AddServiceActivity.this.serviceDate)));
                    }
                }, calendar.get(1), calendar.get(2), calendar.get(5)).show();
                return;
            case R.id.mainImageCard :
                SetCostData();
                this.activityLauncher.launch(new Intent(this, AllSelectServiceActivity.class), new BetterActivityResult.OnActivityResult() { 
                    @Override 
                    public final void onActivityResult(Object obj) {
                        AddServiceActivity.this.m114xb9144cb9((ActivityResult) obj);
                    }
                });
                return;
            default:
                return;
        }
    }

    
    
    public  void m114xb9144cb9(ActivityResult activityResult) {
        ServiceDocumentModel serviceDocumentModel;
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null || (serviceDocumentModel = (ServiceDocumentModel) activityResult.getData().getParcelableExtra("SelectedServiceModel")) == null) {
            return;
        }
        this.selectedServiceModel = serviceDocumentModel;
        this.binding.serviceImage.setImageDrawable(getResources().getDrawable(AppConstants.GetDrawable(this.selectedServiceModel.getIconName())));
        this.binding.edtServiceName.setText(this.selectedServiceModel.getName());
        if (isMileage(this.selectedServiceModel)) {
            this.binding.llBottom.setVisibility(View.GONE);
        } else {
            this.binding.llBottom.setVisibility(View.VISIBLE);
        }
    }

    public void SetCostData() {




        double edtPartsDouble = 0;
        double edtLabourDouble = 0;
        int i;
        String edtParts = this.binding.edtParts.getText().toString();
        String edtLabour = this.binding.edtLabour.getText().toString();
        if (!edtParts.isEmpty()) {
            try {
                edtPartsDouble = Double.parseDouble(edtParts);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (!edtLabour.isEmpty()) {
                try {
                    edtLabourDouble = Double.parseDouble(edtLabour);
                } catch (NumberFormatException e2) {
                    e2.printStackTrace();
                }
                double d = edtPartsDouble + edtLabourDouble;
                this.binding.txtTotal.setText("" + d);
                return;

















            }
        }









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
        dialogDeleteBinding.btnDelete.setOnClickListener(new AnonymousClass13(dialog));
        dialogDeleteBinding.btnCancel.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    
    
    
    public class AnonymousClass13 implements View.OnClickListener {
        final  Dialog val$dialog;

        AnonymousClass13(final Dialog val$dialog) {
            this.val$dialog = val$dialog;
        }

        @Override 
        public void onClick(View v) {
            AddServiceActivity.this.binding.progressBar.setVisibility(View.VISIBLE);
            CompositeDisposable compositeDisposable = AddServiceActivity.this.disposable;
            Observable observeOn = Observable.fromCallable(new Callable() { 
                @Override 
                public final Object call() throws Exception {
                    return AddServiceActivity.AnonymousClass13.this.m117x340fbe5();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            final Dialog dialog = this.val$dialog;
            compositeDisposable.add(observeOn.subscribe(new Consumer() { 
                @Override 
                public final void accept(Object obj) throws Exception {
                    AddServiceActivity.AnonymousClass13.this.m118xdf0277a6(dialog, (Boolean) obj);
                }
            }));
        }

        
        
        public  Boolean m117x340fbe5() throws Exception {
            List<ImageModel> GetAllImageOfCar = AddServiceActivity.this.appDatabase.imageDao().GetAllImageOfCar(AddServiceActivity.this.serviceModel.getServiceId());
            for (int i = 0; i < GetAllImageOfCar.size(); i++) {
                AddServiceActivity addServiceActivity = AddServiceActivity.this;
                AppConstants.deleteImage(addServiceActivity, AddServiceActivity.this.getResources().getString(R.string.app_name) + InternalZipConstants.ZIP_FILE_SEPARATOR + GetAllImageOfCar.get(i).getImageName());
                AddServiceActivity.this.appDatabase.imageDao().DeleteImage(GetAllImageOfCar.get(i));
            }
            AddServiceActivity.this.appDatabase.serviceDao().DeleteService(AddServiceActivity.this.serviceModel);
            return false;
        }

        
        
        public  void m118xdf0277a6(Dialog dialog, Boolean bool) throws Exception {
            dialog.dismiss();
            Intent intent = AddServiceActivity.this.getIntent();
            intent.putExtra("isDelete", true);
            AddServiceActivity.this.setResult(-1, intent);
            AddServiceActivity.this.finish();
        }
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
                    public void onImageReady(String filename) {
                        AddServiceActivity.this.ImageName = filename;
                        AddServiceActivity.this.imageUri = uri.toString();
                        AddServiceActivity.this.imageModelList.add(new ImageModel(AppConstants.getUniqueId(), AddServiceActivity.this.ServiceId, AddServiceActivity.this.ImageName, true, AddServiceActivity.this.imageUri));
                        AddServiceActivity.this.imageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onImageError(Throwable error) {

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
        if (requestCode != 1) {
            return;
        }
        openCamera();
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
