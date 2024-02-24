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
import com.demo.carservicetracker2.databinding.ActivityAllSelectReminderBinding;
import com.demo.carservicetracker2.adapter.ServiceDocumentAdapter;
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


public class AllSelectReminderActivity extends AppCompatActivity {
    ServiceDocumentAdapter adapter;
    MenuItem addCar;
    ActivityAllSelectReminderBinding binding;
    MenuItem search;
    List<ServiceDocumentModel> serviceDocList = new ArrayList();
    CompositeDisposable disposable = new CompositeDisposable();
    public final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAllSelectReminderBinding activityAllSelectReminderBinding = (ActivityAllSelectReminderBinding) DataBindingUtil.setContentView(this, R.layout.activity_all_select_reminder);
        this.binding = activityAllSelectReminderBinding;

        setToolbar();
        setServiceDocData();

        AdAdmob adAdmob = new AdAdmob( this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
    }

    private void setToolbar() {
        setSupportActionBar(this.binding.toolbar.toolbar);
        getSupportActionBar().setTitle("");
        this.binding.toolbar.title.setText("All Select Reminder");
        this.binding.toolbar.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_arrow));
    }

    private void setServiceDocData() {
        this.binding.progressBar.setVisibility(View.VISIBLE);
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception {
                return AllSelectReminderActivity.this.m122x332c5d26();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                AllSelectReminderActivity.this.m123x16581067((Boolean) obj);
            }
        }));
    }

    
    
    public  Boolean m122x332c5d26() throws Exception {
        this.serviceDocList.addAll(AppConstants.GetAllStaticService());
        this.serviceDocList.addAll(AppConstants.GetAllStaticDocument());
        return false;
    }

    
    
    public  void m123x16581067(Boolean bool) throws Exception {
        this.binding.progressBar.setVisibility(View.GONE);
        setAdapter();
    }

    private void setAdapter() {
        this.adapter = new ServiceDocumentAdapter(this, this.serviceDocList, true, new ServiceDocumentAdapter.ServiceClick() { 
            @Override 
            public void onServiceClick(int position) {
                Intent intent = AllSelectReminderActivity.this.getIntent();
                intent.putExtra("SelectedServiceModel", AllSelectReminderActivity.this.adapter.getFilterList().get(position));
                AllSelectReminderActivity.this.setResult(-1, intent);
                AllSelectReminderActivity.this.finish();
            }
        });
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
                    AllSelectReminderActivity.this.adapter.getFilter().filter(newText);
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
