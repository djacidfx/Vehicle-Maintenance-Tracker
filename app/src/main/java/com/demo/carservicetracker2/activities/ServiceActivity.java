package com.demo.carservicetracker2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.databinding.ActivityServiceBinding;
import com.demo.carservicetracker2.adapter.ServiceAdapter;
import com.demo.carservicetracker2.database.AppDatabase;
import com.demo.carservicetracker2.database.models.CarModel;
import com.demo.carservicetracker2.database.models.ServiceModel;
import com.demo.carservicetracker2.model.ServiceDocumentModel;
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


public class ServiceActivity extends AppCompatActivity implements View.OnClickListener {
    String CarId;
    ServiceAdapter adapter;
    MenuItem addCar;
    AppDatabase appDatabase;
    ActivityServiceBinding binding;
    MenuItem search;
    boolean isChange = false;
    List<ServiceModel> serviceModelList = new ArrayList();
    ArrayList<ServiceModel> InsertedList = new ArrayList<>();
    ArrayList<ServiceModel> DeletedList = new ArrayList<>();
    ArrayList<CarModel> InsertedCarList = new ArrayList<>();
    CompositeDisposable disposable = new CompositeDisposable();
    public final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityServiceBinding activityServiceBinding = (ActivityServiceBinding) DataBindingUtil.setContentView(this, R.layout.activity_service);
        this.binding = activityServiceBinding;
        this.appDatabase = AppDatabase.getAppDatabase(this);
        this.CarId = getIntent().getStringExtra("carModelId");
        setToolbar();
        LoadServiceData();
        Clicks();
    }

    private void setToolbar() {
        setSupportActionBar(this.binding.toolbar.toolbar);
        getSupportActionBar().setTitle("");
        this.binding.toolbar.title.setText("Services");
        this.binding.toolbar.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_arrow));
    }

    private void LoadServiceData() {
        this.binding.progressBar.setVisibility(View.VISIBLE);
        this.binding.llNoData.setVisibility(View.GONE);
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception {
                return ServiceActivity.this.m165xffcfab16();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                ServiceActivity.this.m166xba454b97((Boolean) obj);
            }
        }));
    }

    
    
    public  Boolean m165xffcfab16() throws Exception {
        this.serviceModelList = this.appDatabase.serviceDao().GetAllServiceByCarId(this.CarId);
        return false;
    }

    
    
    public  void m166xba454b97(Boolean bool) throws Exception {
        this.binding.progressBar.setVisibility(View.GONE);
        setAdapter();
        CheckNoData();
        setNestedScroll();
        SortServiceDateDesc();
    }

    private void CheckNoData() {
        if (this.serviceModelList.size() > 0) {
            this.binding.llNoData.setVisibility(View.GONE);
            this.binding.recycle.setVisibility(View.VISIBLE);
            return;
        }
        this.binding.llNoData.setVisibility(View.VISIBLE);
        this.binding.recycle.setVisibility(View.GONE);
    }

    
    
    
    public class AnonymousClass1 implements ServiceAdapter.ServiceClick {
        AnonymousClass1() {
        }

        @Override 
        public void OnServiceClick(int position) {
            Intent intent = new Intent(ServiceActivity.this, AddServiceActivity.class);
            intent.putExtra("isForUpdate", true);
            intent.putExtra("ServiceModel", ServiceActivity.this.adapter.getFilterList().get(position));
            ServiceActivity.this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
                @Override 
                public final void onActivityResult(Object obj) {
                    ServiceActivity.AnonymousClass1.this.m169x94c9fd6((ActivityResult) obj);
                }
            });
        }

        
        
        public  void m169x94c9fd6(ActivityResult activityResult) {
            if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
                return;
            }
            Intent data = activityResult.getData();
            ServiceModel serviceModel = (ServiceModel) data.getParcelableExtra("ServiceModel");
            boolean booleanExtra = data.getBooleanExtra("isDelete", false);
            int indexOf = ServiceActivity.this.serviceModelList.indexOf(serviceModel);
            if (booleanExtra) {
                if (ServiceActivity.this.InsertedList.contains(serviceModel)) {
                    ServiceActivity.this.InsertedList.remove(serviceModel);
                } else {
                    ServiceActivity.this.DeletedList.add(serviceModel);
                }
                ServiceActivity.this.serviceModelList.remove(indexOf);
                int indexOf2 = ServiceActivity.this.adapter.getFilterList().indexOf(serviceModel);
                if (indexOf2 != -1) {
                    ServiceActivity.this.adapter.getFilterList().remove(indexOf2);
                }
            } else {
                ServiceActivity.this.isChange = true;
                ServiceActivity.this.serviceModelList.set(indexOf, serviceModel);
                ServiceActivity.this.adapter.getFilterList().set(ServiceActivity.this.adapter.getFilterList().indexOf(serviceModel), serviceModel);
                ServiceActivity.this.SortServiceDateDesc();
            }
            ServiceActivity.this.adapter.notifyDataSetChanged();
        }
    }

    private void setAdapter() {
        this.adapter = new ServiceAdapter(this, this.serviceModelList, new AnonymousClass1());
        this.binding.recycle.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        this.binding.recycle.setAdapter(this.adapter);
    }

    private void setNestedScroll() {
        this.binding.recycle.addOnScrollListener(new RecyclerView.OnScrollListener() { 
            @Override 
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    ServiceActivity.this.binding.addService.hide();
                } else {
                    ServiceActivity.this.binding.addService.show();
                }
            }
        });
    }

    @Override 
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        this.search = menu.findItem(R.id.search);
        this.addCar = menu.findItem(R.id.add_new_car);
        return super.onCreateOptionsMenu(menu);
    }

    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            final SearchView searchView = (SearchView) item.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { 
                @Override 
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override 
                public boolean onQueryTextChange(String newText) {
                    ServiceActivity.this.adapter.getFilter().filter(newText);
                    return false;
                }
            });
            ((ImageView) searchView.findViewById(R.id.search_close_btn)).setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View v) {
                    searchView.onActionViewCollapsed();
                    ServiceActivity.this.SortServiceDateDesc();
                }
            });
            return true;
        } else if (item.getItemId() == R.id.add_new_car) {
            this.activityLauncher.launch(new Intent(this, AddCarActivity.class), new BetterActivityResult.OnActivityResult() { 
                @Override 
                public final void onActivityResult(Object obj) {
                    ServiceActivity.this.m168x7cd217ec((ActivityResult) obj);
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

    
    
    public  void m168x7cd217ec(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.InsertedCarList.add((CarModel) activityResult.getData().getParcelableExtra("CarModel"));
    }

    private void Clicks() {
        this.binding.addService.setOnClickListener(this);
    }

    @Override 
    public void onClick(View view) {
        if (view.getId() != R.id.add_service) {
            return;
        }
        Intent intent = new Intent(this, AddServiceActivity.class);
        intent.putExtra("CarId", this.CarId);
        this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
            @Override 
            public final void onActivityResult(Object obj) {
                ServiceActivity.this.m167xb5547769((ActivityResult) obj);
            }
        });
    }

    
    
    public  void m167xb5547769(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        Intent data = activityResult.getData();
        ServiceModel serviceModel = (ServiceModel) data.getParcelableExtra("ServiceModel");
        ServiceDocumentModel serviceDocumentModel = (ServiceDocumentModel) data.getParcelableExtra("ServiceDocModel");
        this.serviceModelList.add(serviceModel);
        this.InsertedList.add(serviceModel);
        SortServiceDateDesc();
        CheckNoData();
    }



    
    

    public void SortServiceDateDesc() {
        Collections.sort(this.adapter.getFilterList(), new Comparator<ServiceModel>() { 
            @Override 
            public int compare(ServiceModel o1, ServiceModel o2) {
                return Long.valueOf(o2.getDate()).compareTo(Long.valueOf(o1.getDate()));
            }
        });
        this.adapter.notifyDataSetChanged();
    }

    @Override 
    public void onBackPressed() {
        if (ServiceActivity.this.isChange || ServiceActivity.this.InsertedList.size() > 0 || ServiceActivity.this.InsertedCarList.size() > 0 || ServiceActivity.this.DeletedList.size() > 0) {
            Intent intent = ServiceActivity.this.getIntent();
            intent.putExtra("isChange", ServiceActivity.this.isChange);
            intent.putParcelableArrayListExtra("InsertedServiceList", ServiceActivity.this.InsertedList);
            intent.putParcelableArrayListExtra("DeletedServiceList", ServiceActivity.this.DeletedList);
            intent.putParcelableArrayListExtra("InsertedCarList", ServiceActivity.this.InsertedCarList);
            ServiceActivity.this.setResult(-1, intent);
        }
        ServiceActivity.this.finish();
    }
}
