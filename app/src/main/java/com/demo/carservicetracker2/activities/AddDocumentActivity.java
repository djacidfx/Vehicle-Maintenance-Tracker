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
import com.demo.carservicetracker2.databinding.ActivityAddDocumentBinding;
import com.demo.carservicetracker2.databinding.BottomsheetCaptureImageBinding;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.adapter.ImageAdapter;
import com.demo.carservicetracker2.database.AppDatabase;
import com.demo.carservicetracker2.database.models.DocumentModel;
import com.demo.carservicetracker2.database.models.ImageModel;
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

public class AddDocumentActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    String DocumentId;
    String ImageName;
    AppDatabase appDatabase;
    ActivityAddDocumentBinding binding;
    String carModelId;
    DocumentModel documentModel;
    ImageAdapter imageAdapter;
    String imageUri;
    String mCurrentPhotoPath;
    ServiceDocumentModel selectedDocumentModel;
    long documentDate = 0;
    boolean isForUpdate = false;
    List<ImageModel> imageModelList = new ArrayList();
    CompositeDisposable disposable = new CompositeDisposable();
    public final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = (ActivityAddDocumentBinding) DataBindingUtil.setContentView(this, R.layout.activity_add_document);
        Init();
        setToolbar();
        setTextCount();
        Clicks();
        this.binding.edtTotalCost.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(10, 2)});


        AdAdmob adAdmob = new AdAdmob( this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        adAdmob.FullscreenAd(this);



    }

    private void setToolbar() {
        if (this.isForUpdate) {
            this.binding.txtTitle.setText("Edit Document");
        } else {
            this.binding.txtTitle.setText("Add Document");
        }
    }

    private void Init() {
        this.appDatabase = AppDatabase.getAppDatabase(this);
        this.binding.txtMileageUnit.setText(AppConstants.GetDistanceUnit());
        this.binding.txtCurrency.setText(AppConstants.GetCurrency());
        boolean booleanExtra = getIntent().getBooleanExtra("isForUpdate", false);
        this.isForUpdate = booleanExtra;
        if (booleanExtra) {
            this.binding.btnDelete.setVisibility(View.VISIBLE);


             documentModel = (DocumentModel) getIntent().getParcelableExtra("DocumentModel");

            Log.e("MYTAG", "ErrorNo: Init:" +documentModel);
            this.carModelId = documentModel.getCarId();
            this.DocumentId = this.documentModel.getDocumentId();
            this.selectedDocumentModel = AppConstants.GetAllStaticDocument().get(AppConstants.GetAllStaticDocument().indexOf(new ServiceDocumentModel(this.documentModel.getSelectedDocId())));
            this.binding.documentImage.setImageResource(AppConstants.GetDrawable(this.selectedDocumentModel.getIconName()));
            this.documentDate = this.documentModel.getDate();
            this.binding.edtDocumentName.setText(this.documentModel.getDocName());
            int length = this.binding.edtDocumentName.length();
            TextView textView = this.binding.txtDocumentNameCnt;
            textView.setText("" + length);
            if (this.documentModel.getMileage() > 0) {
                EditText editText = this.binding.edtMileage;
                editText.setText("" + this.documentModel.getMileage());
            }
            if (this.documentModel.getTotalCoast() > Utils.DOUBLE_EPSILON) {
                EditText editText2 = this.binding.edtTotalCost;
                editText2.setText("" + this.documentModel.getTotalCoast());
            }
            this.binding.edtComments.setText(this.documentModel.getComments());
            int length2 = this.binding.edtComments.length();
            TextView textView2 = this.binding.txtCommentsCnt;
            textView2.setText("" + length2);
            setNestedScroll();
        } else {
            this.binding.btnDelete.setVisibility(View.GONE);
            this.carModelId = getIntent().getStringExtra("CarId");
            this.DocumentId = AppConstants.getUniqueId();
            this.selectedDocumentModel = AppConstants.GetAllStaticDocument().get(AppConstants.GetAllStaticDocument().size() - 1);
            this.binding.documentImage.setImageResource(AppConstants.GetDrawable(this.selectedDocumentModel.getIconName()));
            this.binding.edtDocumentName.setText(this.selectedDocumentModel.getName());
            this.binding.txtDocumentNameCnt.setText(String.valueOf(this.binding.edtDocumentName.length()));
            this.documentDate = AppConstants.GetOnlyDateInMillis(System.currentTimeMillis());
        }
        this.binding.edtDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(this.documentDate)));
        LoadImageList();
    }

    private void setNestedScroll() {
        this.binding.scroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() { 
            @Override 
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    AddDocumentActivity.this.binding.btnDelete.setVisibility(View.GONE);
                }
                if (scrollY < oldScrollY) {
                    AddDocumentActivity.this.binding.btnDelete.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setTextCount() {
        this.binding.edtDocumentName.addTextChangedListener(new TextWatcher() { 
            @Override 
            public void afterTextChanged(Editable editable) {
            }

            @Override 
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override 
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                AddDocumentActivity.this.binding.edtDocumentName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(90)});
                AddDocumentActivity.this.binding.txtDocumentNameCnt.setText(String.valueOf(AddDocumentActivity.this.binding.edtDocumentName.length()));
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
                AddDocumentActivity.this.binding.edtComments.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1000)});
                AddDocumentActivity.this.binding.txtCommentsCnt.setText(String.valueOf(AddDocumentActivity.this.binding.edtComments.length()));
            }
        });
    }

    private void LoadImageList() {
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception {
                try {
                    return AddDocumentActivity.this.m86x66b54e97();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                AddDocumentActivity.this.m87xb474c698((Boolean) obj);
            }
        }));
    }

    
    
    public  Boolean m86x66b54e97() throws Exception {
        if (this.isForUpdate) {
            this.imageModelList = this.appDatabase.imageDao().GetAllImageOfCar(this.DocumentId);
        }
        return false;
    }

    
    
    public  void m87xb474c698(Boolean bool) throws Exception {
        setImageAdapter();
    }

    private void setImageAdapter() {
        this.imageAdapter = new ImageAdapter(this, this.imageModelList, new ImageAdapter.ImageRemoveListener() { 
            @Override 
            public void OnRemoveImage(int position) {
                if (AddDocumentActivity.this.imageModelList.get(position).isNew()) {
                    AddDocumentActivity.this.imageModelList.remove(position);
                } else {
                    AddDocumentActivity.this.imageModelList.get(position).setDeleted(true);
                }
                AddDocumentActivity.this.imageAdapter.notifyDataSetChanged();
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
            Intent intent = new Intent(AddDocumentActivity.this, ViewImageActivity.class);
            intent.putParcelableArrayListExtra("imageList", (ArrayList) AddDocumentActivity.this.imageModelList);
            intent.putExtra("position", position);
            AddDocumentActivity.this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
                @Override 
                public final void onActivityResult(Object obj) {
                    AddDocumentActivity.AnonymousClass5.this.m95xf5f4f16b((ActivityResult) obj);
                }
            });
        }
        
        
        public  void m95xf5f4f16b(ActivityResult activityResult) {
            Intent data = activityResult.getData();
            if (data == null || !data.getBooleanExtra("isChange", false)) {
                return;
            }
            AddDocumentActivity.this.imageModelList.clear();
            AddDocumentActivity.this.imageModelList = data.getParcelableArrayListExtra("images");
            AddDocumentActivity.this.imageAdapter.setList(AddDocumentActivity.this.imageModelList);
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
                AddDocumentActivity.this.checkCameraPermission();
            }
        });
        inflate.llGallery.setOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                AddDocumentActivity.this.selectImage();
            }
        });
    }

    private void UpdateImage() {
        this.binding.progressBar.setVisibility(View.VISIBLE);
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public Object call(){
                try {
                    return AddDocumentActivity.this.m88x984a758();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj)  {
                AddDocumentActivity.this.m89x57441f59((Boolean) obj);
            }
        }));
    }

    
    
    public  Boolean m88x984a758(){
        for (int i = 0; i < this.imageModelList.size(); i++) {
            if (this.imageModelList.get(i).isNew()) {
                this.appDatabase.imageDao().InsertImage(new ImageModel(AppConstants.getUniqueId(), this.DocumentId, this.imageModelList.get(i).getImageName()));
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

    
    
    public  void m89x57441f59(Boolean bool)  {
        this.binding.progressBar.setVisibility(View.GONE);
    }

    private void InsertImage() {
        if (this.imageModelList.size() > 0) {
            this.binding.progressBar.setVisibility(View.VISIBLE);
            this.disposable.add(Observable.fromCallable(new Callable() { 
                @Override 
                public final Object call() throws Exception {
                    try {
                        return AddDocumentActivity.this.m84x9c687f6a();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
                @Override 
                public final void accept(Object obj) throws Exception {
                    AddDocumentActivity.this.m85xea27f76b((Boolean) obj);
                }
            }));
        }
    }

    
    
    public  Boolean m84x9c687f6a() {
        for (int i = 0; i < this.imageModelList.size(); i++) {
            this.appDatabase.imageDao().InsertImage(new ImageModel(AppConstants.getUniqueId(), this.DocumentId, this.imageModelList.get(i).getImageName()));
        }
        return false;
    }

    
    
    public  void m85xea27f76b(Boolean bool) throws Exception {
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
                        AddDocumentActivity.this.m91xa6132013((ActivityResult) obj);
                    }
                });
            }
        }
    }

    
    
    public  void m91xa6132013(ActivityResult activityResult) {
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
                AddDocumentActivity.this.m92x41415bea((ActivityResult) obj);
            }
        });
    }

    public void m92x41415bea(ActivityResult activityResult) {
        final Intent data = activityResult.getData();
        if (data != null) {
            if (data.getClipData() != null) {
                int itemCount = data.getClipData().getItemCount();
                for (int i = 0; i < itemCount; i++) {
                    int finalI = i;

                    new ImageCompressionAsyncTask(this, data.getClipData().getItemAt(i).getUri(), new ImageListener(){
                        @Override
                        public void onImageReady(String filename) {
                            AddDocumentActivity.this.ImageName = filename;
                            AddDocumentActivity.this.imageUri = data.getClipData().getItemAt(finalI).getUri().toString();
                            AddDocumentActivity.this.imageModelList.add(new ImageModel(AppConstants.getUniqueId(), AddDocumentActivity.this.DocumentId, AddDocumentActivity.this.ImageName, true, AddDocumentActivity.this.imageUri));
                            AddDocumentActivity.this.imageAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onImageError(Throwable error) {
                        }
                    });
                }
                return;
            }
            final Uri data2 = data.getData();
            new ImageCompressionAsyncTask(this, data2, new ImageListener(){
                @Override
                public void onImageReady(String filename) {
                    AddDocumentActivity.this.ImageName = filename;
                    AddDocumentActivity.this.imageUri = data2.toString();
                    AddDocumentActivity.this.imageModelList.add(new ImageModel(AppConstants.getUniqueId(), AddDocumentActivity.this.DocumentId, AddDocumentActivity.this.ImageName, true, AddDocumentActivity.this.imageUri));
                    AddDocumentActivity.this.imageAdapter.notifyDataSetChanged();
                }
                @Override
                public void onImageError(Throwable error) {
                }
            });
        }
    }

    
    
    
    
    public void InsertDocument() {
        double parseDouble = 0;
        String docName = this.binding.edtDocumentName.getText().toString();
        String obj2 = this.binding.edtMileage.getText().toString();
        String obj3 = this.binding.edtTotalCost.getText().toString();
        String edtComments = this.binding.edtComments.getText().toString();
        long parseLong = !obj2.isEmpty() ? Long.parseLong(obj2) : 0L;
        if (!obj3.isEmpty()) {
            try {
                parseDouble = Double.parseDouble(obj3);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (this.isForUpdate) {
                Log.e("MYTAG", "ErrorNo: 3432432:"+this.selectedDocumentModel.getId());
                Log.e("MYTAG", "ErrorNo: 243234:"+ this.documentModel);

                documentModel.setSelectedDocId(this.selectedDocumentModel.getId());
                documentModel.setDocName(docName);
                documentModel.setMileage(parseLong);
                documentModel.setDate(this.documentDate);
                documentModel.setTotalCoast(parseDouble);
                documentModel.setComments(edtComments);

                appDatabase.documentDao().UpdateDocument(documentModel);
                UpdateImage();
                Intent intent = getIntent();
                intent.putExtra("DocumentModel",documentModel);
                setResult(-1, intent);
                finish();
                return;
            }
            DocumentModel documentModel = new DocumentModel(this.DocumentId, this.selectedDocumentModel.getId(), docName, parseLong, this.documentDate, parseDouble, edtComments, this.carModelId);
            this.appDatabase.documentDao().InsertDocument(documentModel);
            InsertImage();
            Intent intent2 = getIntent();
            intent2.putExtra("DocumentModel", documentModel);
            intent2.putExtra("ServiceDocModel", this.selectedDocumentModel);
            setResult(-1, intent2);
            finish();
            return;
        }



    }

    private void Clicks() {
        this.binding.back.setOnClickListener(this);
        this.binding.btnSave.setOnClickListener(this);
        this.binding.mainImageCard.setOnClickListener(this);
        this.binding.edtDate.setOnClickListener(this);
        this.binding.calendar.setOnClickListener(this);
        this.binding.btnSelect.setOnClickListener(this);
        this.binding.btnDelete.setOnClickListener(this);
    }

    @Override 
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back :
                onBackPressed();
                return;
            case R.id.btnDelete :
                openDeleteDialog();
                return;
            case R.id.btnSave :
                AddDocumentActivity.this.InsertDocument();
                return;
            case R.id.btnSelect :
                openPictureBottomSheet();
                return;
            case R.id.calendar :
            case R.id.edtDate :
                final Calendar calendar = Calendar.getInstance();
                if (this.isForUpdate) {
                    long j = this.documentDate;
                    if (j > 0) {
                        calendar.setTimeInMillis(j);
                        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() { 
                            @Override 
                            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                                calendar.set(1, year);
                                calendar.set(2, month);
                                calendar.set(5, date);
                                AddDocumentActivity.this.documentDate = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                                AddDocumentActivity.this.binding.edtDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(AddDocumentActivity.this.documentDate)));
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
                        AddDocumentActivity.this.documentDate = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                        AddDocumentActivity.this.binding.edtDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(AddDocumentActivity.this.documentDate)));
                    }
                }, calendar.get(1), calendar.get(2), calendar.get(5)).show();
                return;
            case R.id.mainImageCard :
                this.activityLauncher.launch(new Intent(this, AllSelectDocumentActivity.class), new BetterActivityResult.OnActivityResult() { 
                    @Override 
                    public final void onActivityResult(Object obj) {
                        AddDocumentActivity.this.m90xb5a2a415((ActivityResult) obj);
                    }
                });
                return;
            default:
                return;
        }
    }

    
    
    public  void m90xb5a2a415(ActivityResult activityResult) {
        ServiceDocumentModel serviceDocumentModel;
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null || (serviceDocumentModel = (ServiceDocumentModel) activityResult.getData().getParcelableExtra("SelectedServiceModel")) == null) {
            return;
        }
        this.selectedDocumentModel = serviceDocumentModel;
        this.binding.documentImage.setImageDrawable(getResources().getDrawable(AppConstants.GetDrawable(this.selectedDocumentModel.getIconName())));
        this.binding.edtDocumentName.setText(this.selectedDocumentModel.getName());
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
            AddDocumentActivity.this.binding.progressBar.setVisibility(View.VISIBLE);
            CompositeDisposable compositeDisposable = AddDocumentActivity.this.disposable;
            Observable observeOn = Observable.fromCallable(new Callable() { 
                @Override 
                public final Object call() throws Exception {
                    try {
                        return AnonymousClass12.this.m93x36451218();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            final Dialog dialog = this.val$dialog;
            compositeDisposable.add(observeOn.subscribe(new Consumer() { 
                @Override 
                public final void accept(Object obj) throws Exception {
                    AddDocumentActivity.AnonymousClass12.this.m94xd2b30e77(dialog, (Boolean) obj);
                }
            }));
        }

        
        
        public  Boolean m93x36451218(){
            List<ImageModel> GetAllImageOfCar = AddDocumentActivity.this.appDatabase.imageDao().GetAllImageOfCar(AddDocumentActivity.this.documentModel.getDocumentId());
            for (int i = 0; i < GetAllImageOfCar.size(); i++) {
                AddDocumentActivity addDocumentActivity = AddDocumentActivity.this;
                AppConstants.deleteImage(addDocumentActivity,AddDocumentActivity.this.getResources().getString(R.string.app_name) +
                        InternalZipConstants.ZIP_FILE_SEPARATOR +
                        GetAllImageOfCar.get(i).getImageName());
                AddDocumentActivity.this.appDatabase.imageDao().DeleteImage(GetAllImageOfCar.get(i));
            }
            AddDocumentActivity.this.appDatabase.documentDao().DeleteDocument(AddDocumentActivity.this.documentModel);
            return false;
        }

        
        
        public  void m94xd2b30e77(Dialog dialog, Boolean bool) throws Exception {
            AddDocumentActivity.this.binding.progressBar.setVisibility(View.GONE);
            dialog.dismiss();
            Intent intent = AddDocumentActivity.this.getIntent();
            intent.putExtra("isDelete", true);
            AddDocumentActivity.this.setResult(-1, intent);
            AddDocumentActivity.this.finish();
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
                        AddDocumentActivity.this.ImageName = filename;
                        AddDocumentActivity.this.imageUri = uri.toString();
                        AddDocumentActivity.this.imageModelList.add(new ImageModel(AppConstants.getUniqueId(), AddDocumentActivity.this.DocumentId, AddDocumentActivity.this.ImageName, true, AddDocumentActivity.this.imageUri));
                        AddDocumentActivity.this.imageAdapter.notifyDataSetChanged();

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override 
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode != 1) {
            return;
        }
        openCamera();
    }

    @Override 
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
