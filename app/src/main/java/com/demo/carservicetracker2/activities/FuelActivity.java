package com.demo.carservicetracker2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.carservicetracker2.AdAdmob;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.databinding.ActivityFuelBinding;
import com.demo.carservicetracker2.adapter.FuelAdapter;
import com.demo.carservicetracker2.database.AppDatabase;
import com.demo.carservicetracker2.database.models.CarModel;
import com.demo.carservicetracker2.database.models.FuelModel;
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


public class FuelActivity extends AppCompatActivity implements View.OnClickListener {
    String CarId;
    FuelAdapter adapter;
    MenuItem addCar;
    AppDatabase appDatabase;
    ActivityFuelBinding binding;
    MenuItem search;
    boolean isChange = false;
    List<FuelModel> fuelModelList = new ArrayList();
    ArrayList<CarModel> InsertedCarList = new ArrayList<>();
    CompositeDisposable disposable = new CompositeDisposable();
    public final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFuelBinding activityFuelBinding = (ActivityFuelBinding) DataBindingUtil.setContentView(this, R.layout.activity_fuel);
        this.binding = activityFuelBinding;
        this.appDatabase = AppDatabase.getAppDatabase(this);
        this.CarId = getIntent().getStringExtra("carModelId");
        setToolbar();
        LoadFuelData();
        Clicks();


        AdAdmob adAdmob = new AdAdmob( this);
        adAdmob.FullscreenAd(this);
    }

    private void setToolbar() {
        setSupportActionBar(this.binding.toolbar.toolbar);
        getSupportActionBar().setTitle("");
        this.binding.toolbar.title.setText("Fuels");
        this.binding.toolbar.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_arrow));
    }

    private void LoadFuelData() {
        this.binding.progressBar.setVisibility(View.VISIBLE);
        this.binding.llNoData.setVisibility(View.GONE);
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call()  {
                return FuelActivity.this.m127x63a6566c();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                FuelActivity.this.m128xf0936d8b((Boolean) obj);
            }
        }));
    }

    public Boolean m127x63a6566c() {
        this.fuelModelList = this.appDatabase.fuelDao().GetAllFuelByCarId(this.CarId);
        return false;
    }

    
    
    public  void m128xf0936d8b(Boolean bool) throws Exception {
        this.binding.progressBar.setVisibility(View.GONE);
        setAdapter();
        CheckNoData();
        setNestedScroll();
        SortFuelDateDesc();
    }

    private void setNestedScroll() {
        this.binding.recycle.addOnScrollListener(new RecyclerView.OnScrollListener() { 
            @Override 
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    FuelActivity.this.binding.addFuel.hide();
                } else {
                    FuelActivity.this.binding.addFuel.show();
                }
            }
        });
    }

    
    
    
    public class AnonymousClass2 implements FuelAdapter.FuelClick {
        @Override 
        public void OnFuelClick(int position) {
            Intent intent = new Intent(FuelActivity.this, AddFuelActivity.class);
            intent.putExtra("isForUpdate", true);
            intent.putExtra("FuelModel", FuelActivity.this.adapter.getFilterList().get(position));
            FuelActivity.this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
                @Override 
                public final void onActivityResult(Object obj) {
                    FuelActivity.AnonymousClass2.this.m131xba70fb67((ActivityResult) obj);
                }
            });
        }

        
        
        public  void m131xba70fb67(ActivityResult activityResult) {
            if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
                return;
            }
            Intent data = activityResult.getData();
            FuelModel fuelModel = (FuelModel) data.getParcelableExtra("FuelModel");
            boolean booleanExtra = data.getBooleanExtra("isDelete", false);
            int indexOf = FuelActivity.this.fuelModelList.indexOf(fuelModel);
            if (booleanExtra) {
                FuelActivity.this.fuelModelList.remove(indexOf);
                int indexOf2 = FuelActivity.this.adapter.getFilterList().indexOf(fuelModel);
                if (indexOf2 != -1) {
                    FuelActivity.this.adapter.getFilterList().remove(indexOf2);
                }
                FuelActivity.this.CheckNoData();
            } else {
                FuelActivity.this.isChange = true;
                FuelActivity.this.fuelModelList.set(indexOf, fuelModel);
                FuelActivity.this.adapter.getFilterList().set(FuelActivity.this.adapter.getFilterList().indexOf(fuelModel), fuelModel);
                FuelActivity.this.SortFuelDateDesc();
            }
            FuelActivity.this.adapter.notifyDataSetChanged();
        }
    }

    private void setAdapter() {
        this.adapter = new FuelAdapter(this, this.fuelModelList, new AnonymousClass2());
        this.binding.recycle.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        this.binding.recycle.setAdapter(this.adapter);
    }

    
    public void CheckNoData() {
        if (this.fuelModelList.size() > 0) {
            this.binding.llNoData.setVisibility(View.GONE);
            this.binding.recycle.setVisibility(View.VISIBLE);
            return;
        }
        this.binding.llNoData.setVisibility(View.VISIBLE);
        this.binding.recycle.setVisibility(View.GONE);
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
                    FuelActivity.this.adapter.getFilter().filter(newText);
                    return false;
                }
            });
            ((ImageView) searchView.findViewById(R.id.search_close_btn)).setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View v) {
                    searchView.onActionViewCollapsed();
                    FuelActivity.this.SortFuelDateDesc();
                }
            });
            return true;
        } else if (item.getItemId() == R.id.add_new_car) {
            this.activityLauncher.launch(new Intent(this, AddCarActivity.class), new BetterActivityResult.OnActivityResult() { 
                @Override 
                public final void onActivityResult(Object obj) {
                    FuelActivity.this.m130xcf4080dd((ActivityResult) obj);
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

    
    
    public  void m130xcf4080dd(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.InsertedCarList.add((CarModel) activityResult.getData().getParcelableExtra("CarModel"));
    }

    private void Clicks() {
        this.binding.addFuel.setOnClickListener(this);
    }

    @Override 
    public void onClick(View view) {
        if (view.getId() != R.id.add_fuel) {
            return;
        }
        Intent intent = new Intent(this, AddFuelActivity.class);
        intent.putExtra("CarId", this.CarId);
        this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
            @Override 
            public final void onActivityResult(Object obj) {
                FuelActivity.this.m129x48459a80((ActivityResult) obj);
            }
        });
    }

    
    
    public  void m129x48459a80(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.fuelModelList.add((FuelModel) activityResult.getData().getParcelableExtra("FuelModel"));
        this.adapter.notifyDataSetChanged();
        CheckNoData();
    }

    public void SortFuelDateDesc() {
        Collections.sort(this.adapter.getFilterList(), new Comparator<FuelModel>() { 
            @Override 
            public int compare(FuelModel o1, FuelModel o2) {
                return Long.valueOf(o2.getDate()).compareTo(Long.valueOf(o1.getDate()));
            }
        });
        this.adapter.notifyDataSetChanged();
    }

    @Override 
    public void onBackPressed() {
        if (this.InsertedCarList.size() > 0) {
            Intent intent = getIntent();
            intent.putParcelableArrayListExtra("InsertedCarList", this.InsertedCarList);
            setResult(-1, intent);
        }
        finish();
    }
}
