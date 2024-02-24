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
import com.demo.carservicetracker2.databinding.ActivityGarageBinding;
import com.demo.carservicetracker2.adapter.GarageAdapter;
import com.demo.carservicetracker2.database.AppDatabase;
import com.demo.carservicetracker2.database.models.CarModel;
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


public class GarageActivity extends AppCompatActivity implements View.OnClickListener {
    GarageAdapter adapter;
    MenuItem addCar;
    AppDatabase appDatabase;
    ActivityGarageBinding binding;
    MenuItem search;
    CarModel selectedCar;
    boolean isMultiSelect = false;
    boolean isChange = false;
    List<CarModel> carModelList = new ArrayList();
    ArrayList<CarModel> InsertedCarList = new ArrayList<>();
    CompositeDisposable disposable = new CompositeDisposable();
    public final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityGarageBinding activityGarageBinding = (ActivityGarageBinding) DataBindingUtil.setContentView(this, R.layout.activity_garage);
        this.binding = activityGarageBinding;
        this.appDatabase = AppDatabase.getAppDatabase(this);
        setToolbar();
        LoadCarList();
        Clicks();


        AdAdmob adAdmob = new AdAdmob( this);
        adAdmob.FullscreenAd(this);


    }

    private void setToolbar() {
        setSupportActionBar(this.binding.toolbar.toolbar);
        getSupportActionBar().setTitle("");
        this.binding.toolbar.title.setText("Garage");
        this.binding.toolbar.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_arrow));
    }

    private void LoadCarList() {
        this.binding.progressBar.setVisibility(View.VISIBLE);
        this.binding.llNoData.setVisibility(View.GONE);
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception {
                return GarageActivity.this.m133xc49deab1();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                GarageActivity.this.m134xcaa1b610((Boolean) obj);
            }
        }));
    }

    
    
    public  Boolean m133xc49deab1() throws Exception {
        this.carModelList = this.appDatabase.carDao().GetAllCar();
        return false;
    }

    
    
    public  void m134xcaa1b610(Boolean bool) throws Exception {
        this.binding.progressBar.setVisibility(View.GONE);
        setAdapter();
        setNestedScroll();
    }

    
    
    
    public class AnonymousClass1 implements GarageAdapter.CarModelSingleListener {
        AnonymousClass1() {
        }

        @Override 
        public void CarModelClick(final int position) {
            final CarModel carModel = GarageActivity.this.adapter.getFilterList().get(position);
            Intent intent = new Intent(GarageActivity.this, AddCarActivity.class);
            intent.putExtra("carModel", carModel);
            intent.putExtra("isForUpdate", true);
            GarageActivity.this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
                @Override 
                public final void onActivityResult(Object obj) {
                    GarageActivity.AnonymousClass1.this.m135x2d9d7ed7(carModel, position, (ActivityResult) obj);
                }
            });
        }

        
        
        public  void m135x2d9d7ed7(CarModel carModel, int i, ActivityResult activityResult) {
            if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
                return;
            }
            Intent data = activityResult.getData();
            CarModel carModel2 = (CarModel) data.getParcelableExtra("CarModel");
            GarageActivity.this.isChange = data.getBooleanExtra("isChange", false);
            if (!data.getBooleanExtra("isDelete", false)) {
                if (carModel2 != null) {
                    GarageActivity.this.carModelList.set(GarageActivity.this.carModelList.indexOf(GarageActivity.this.adapter.getFilterList().get(i)), carModel2);
                    GarageActivity.this.adapter.getFilterList().set(GarageActivity.this.adapter.getFilterList().indexOf(carModel2), carModel2);
                    GarageActivity.this.SortDateDesc();
                    return;
                }
                return;
            }
            GarageActivity.this.isChange = true;
            if (GarageActivity.this.InsertedCarList.contains(carModel2)) {
                GarageActivity.this.InsertedCarList.remove(carModel2);
            }
            GarageActivity.this.carModelList.remove(GarageActivity.this.carModelList.indexOf(carModel));
            int indexOf = GarageActivity.this.adapter.getFilterList().indexOf(carModel);
            if (indexOf != -1) {
                GarageActivity.this.adapter.getFilterList().remove(indexOf);
            }
            GarageActivity.this.adapter.notifyDataSetChanged();
        }
    }

    private void setAdapter() {
        this.adapter = new GarageAdapter(this, this.carModelList, new AnonymousClass1());
        this.binding.recycle.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        this.binding.recycle.setAdapter(this.adapter);
    }

    private void setNestedScroll() {
        this.binding.recycle.addOnScrollListener(new RecyclerView.OnScrollListener() { 
            @Override 
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    GarageActivity.this.binding.addCar.hide();
                } else {
                    GarageActivity.this.binding.addCar.show();
                }
            }
        });
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
            final SearchView searchView = (SearchView) item.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { 
                @Override 
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override 
                public boolean onQueryTextChange(String newText) {
                    GarageActivity.this.adapter.getFilter().filter(newText.trim());
                    return false;
                }
            });
            ((ImageView) searchView.findViewById(R.id.search_close_btn)).setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View v) {
                    searchView.onActionViewCollapsed();
                    GarageActivity.this.SortDateDesc();
                }
            });
            return true;
        } else if (item.getItemId() == R.id.add_new_car) {
            AddCar();
            return true;
        } else if (item.getItemId() == 16908332) {
            onBackPressed();
            return true;
        } else {
            return true;
        }
    }

    private void Clicks() {
        this.binding.addCar.setOnClickListener(this);
    }

    @Override 
    public void onClick(View view) {
        if (view.getId() != R.id.add_car) {
            return;
        }
        AddCar();
    }

    private void AddCar() {
        this.activityLauncher.launch(new Intent(this, AddCarActivity.class), new BetterActivityResult.OnActivityResult() { 
            @Override 
            public final void onActivityResult(Object obj) {
                GarageActivity.this.m132x2db6a34e((ActivityResult) obj);
            }
        });
    }

    
    
    public  void m132x2db6a34e(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        Intent data = activityResult.getData();
        CarModel carModel = (CarModel) data.getParcelableExtra("CarModel");
        if (data.getBooleanExtra("isSelected", false)) {
            this.selectedCar = carModel;
        }
        this.carModelList.add(carModel);
        this.InsertedCarList.add(carModel);
        SortDateDesc();
    }

    public void SortDateDesc() {
        Collections.sort(this.adapter.getFilterList(), new Comparator<CarModel>() { 
            @Override 
            public int compare(CarModel o1, CarModel o2) {
                return Long.valueOf(o2.getPurchaseDate()).compareTo(Long.valueOf(o1.getPurchaseDate()));
            }
        });
        this.adapter.notifyDataSetChanged();
    }

    @Override 
    public void onBackPressed() {
        if (this.InsertedCarList.size() > 0 || this.isChange) {
            Intent intent = getIntent();
            intent.putParcelableArrayListExtra("InsertedCarList", this.InsertedCarList);
            intent.putExtra("isChange", this.isChange);
            setResult(-1, intent);
            finish();
            return;
        }
        super.onBackPressed();
    }
}
