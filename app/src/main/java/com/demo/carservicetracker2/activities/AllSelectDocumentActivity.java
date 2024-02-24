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
import com.demo.carservicetracker2.databinding.ActivityAllSelectDocumentBinding;
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


public class AllSelectDocumentActivity extends AppCompatActivity {
    ServiceDocumentAdapter adapter;
    MenuItem addCar;
    ActivityAllSelectDocumentBinding binding;
    MenuItem search;
    CompositeDisposable disposable = new CompositeDisposable();
    List<ServiceDocumentModel> documentList = new ArrayList();
    public final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);


    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAllSelectDocumentBinding activityAllSelectDocumentBinding = (ActivityAllSelectDocumentBinding) DataBindingUtil.setContentView(this, R.layout.activity_all_select_document);
        this.binding = activityAllSelectDocumentBinding;

        setToolbar();
        LoadServiceData();



        AdAdmob adAdmob = new AdAdmob( this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        adAdmob.FullscreenAd(this);
    }

    private void setToolbar() {
        setSupportActionBar(this.binding.toolbar.toolbar);
        getSupportActionBar().setTitle("");
        this.binding.toolbar.title.setText("All Select Document");
        this.binding.toolbar.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_arrow));





    }

    private void LoadServiceData() {
        this.binding.progressBar.setVisibility(View.VISIBLE);
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception {
                return AllSelectDocumentActivity.this.m120x62bc2119();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                AllSelectDocumentActivity.this.m121x45e7d45a((Boolean) obj);
            }
        }));
    }



    public  Boolean m120x62bc2119() throws Exception {
        this.documentList = AppConstants.GetAllStaticDocument();
        return false;
    }



    public  void m121x45e7d45a(Boolean bool) throws Exception {
        this.binding.progressBar.setVisibility(View.GONE);
        setAdapter();
    }

    private void setAdapter() {
        this.adapter = new ServiceDocumentAdapter(this, this.documentList, false, new ServiceDocumentAdapter.ServiceClick() { 
            @Override 
            public void onServiceClick(int position) {
                Intent intent = AllSelectDocumentActivity.this.getIntent();
                intent.putExtra("SelectedServiceModel", AllSelectDocumentActivity.this.adapter.getFilterList().get(position));
                AllSelectDocumentActivity.this.setResult(-1, intent);
                AllSelectDocumentActivity.this.finish();
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
                    AllSelectDocumentActivity.this.adapter.getFilter().filter(newText);
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
