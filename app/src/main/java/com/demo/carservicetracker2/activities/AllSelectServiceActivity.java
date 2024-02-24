package com.demo.carservicetracker2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.carservicetracker2.AdAdmob;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.databinding.ActivityAllSelectServiceBinding;
import com.demo.carservicetracker2.adapter.ServiceDocumentAdapter;
import com.demo.carservicetracker2.database.models.ServiceModel;
import com.demo.carservicetracker2.model.ServiceDocumentModel;
import com.demo.carservicetracker2.utils.AppConstants;
import com.demo.carservicetracker2.utils.BetterActivityResult;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class AllSelectServiceActivity extends AppCompatActivity {
    ServiceDocumentAdapter adapter;
    MenuItem addCar;
    ActivityAllSelectServiceBinding binding;
    String carId;
    MenuItem search;
    List<ServiceDocumentModel> serviceList = new ArrayList();
    CompositeDisposable disposable = new CompositeDisposable();
    public final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAllSelectServiceBinding activityAllSelectServiceBinding = (ActivityAllSelectServiceBinding) DataBindingUtil.setContentView(this, R.layout.activity_all_select_service);
        this.binding = activityAllSelectServiceBinding;
        this.carId = getIntent().getStringExtra("carId");
        setToolbar();
        setServiceData();


        AdAdmob adAdmob = new AdAdmob( this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        adAdmob.FullscreenAd(this);

    }

    private void setToolbar() {
        setSupportActionBar(this.binding.toolbar.toolbar);
        getSupportActionBar().setTitle("");
        this.binding.toolbar.title.setText("All Select Service");
        this.binding.toolbar.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_arrow));
    }

    private void setServiceData() {
        this.binding.progressBar.setVisibility(View.VISIBLE);
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception {
                return AllSelectServiceActivity.this.m124xdd387cf7();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                AllSelectServiceActivity.this.m125x1618dd96((Boolean) obj);
            }
        }));
    }

    
    
    public  Boolean m124xdd387cf7() throws Exception {
        this.serviceList = AppConstants.GetAllStaticService();
        return false;
    }

    
    
    public  void m125x1618dd96(Boolean bool) throws Exception {
        this.binding.progressBar.setVisibility(View.GONE);
        setAdapter();
    }

    
    
    
    public class AnonymousClass1 implements ServiceDocumentAdapter.ServiceClick {
        AnonymousClass1() {
        }

        @Override 
        public void onServiceClick(int position) {
            ServiceDocumentModel serviceDocumentModel = AllSelectServiceActivity.this.adapter.getFilterList().get(position);
            if (AllSelectServiceActivity.this.carId != null) {
                Intent intent = new Intent(AllSelectServiceActivity.this, AddServiceActivity.class);
                intent.putExtra("CarId", AllSelectServiceActivity.this.carId);
                intent.putExtra("ServiceDocModel", serviceDocumentModel);
                AllSelectServiceActivity.this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
                    @Override 
                    public final void onActivityResult(Object obj) {
                        AllSelectServiceActivity.AnonymousClass1.this.m126xad43ae4f((ActivityResult) obj);
                    }
                });
                return;
            }
            Intent intent2 = AllSelectServiceActivity.this.getIntent();
            intent2.putExtra("SelectedServiceModel", serviceDocumentModel);
            AllSelectServiceActivity.this.setResult(-1, intent2);
            AllSelectServiceActivity.this.finish();
        }

        
        
        public  void m126xad43ae4f(ActivityResult activityResult) {
            if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
                return;
            }
            Intent data = activityResult.getData();
            Intent intent = AllSelectServiceActivity.this.getIntent();
            intent.putExtra("ServiceModel", (ServiceModel) data.getParcelableExtra("ServiceModel"));
            intent.putExtra("ServiceDocModel", (ServiceDocumentModel) data.getParcelableExtra("ServiceDocModel"));
            AllSelectServiceActivity.this.setResult(-1, intent);
            AllSelectServiceActivity.this.finish();
        }
    }

    private void setAdapter() {
        this.adapter = new ServiceDocumentAdapter(this, this.serviceList, false, new AnonymousClass1());
        this.binding.recycle.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        this.binding.recycle.setAdapter(this.adapter);
    }

    @Override 
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        this.search = menu.findItem(R.id.search);
        MenuItem findItem = menu.findItem(R.id.add_new_car);
        this.addCar = findItem;
        findItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            ((SearchView) item.getActionView()).setOnQueryTextListener(new SearchView.OnQueryTextListener() { 
                @Override 
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override 
                public boolean onQueryTextChange(String newText) {
                    AllSelectServiceActivity.this.adapter.getFilter().filter(newText);
                    return false;
                }
            });
            return true;
        } else if (item.getItemId() == 16908332) {
            onBackPressed();
            return true;
        } else {
            return true;
        }
    }
}
