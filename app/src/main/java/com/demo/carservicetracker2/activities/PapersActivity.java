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
import com.demo.carservicetracker2.databinding.ActivityPapersBinding;
import com.demo.carservicetracker2.adapter.DocumentAdapter;
import com.demo.carservicetracker2.database.AppDatabase;
import com.demo.carservicetracker2.database.models.CarModel;
import com.demo.carservicetracker2.database.models.DocumentModel;
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


public class PapersActivity extends AppCompatActivity implements View.OnClickListener {
    String CarId;
    DocumentAdapter adapter;
    MenuItem addCar;
    AppDatabase appDatabase;
    ActivityPapersBinding binding;
    MenuItem search;
    boolean isChange = false;
    List<DocumentModel> documentModelList = new ArrayList();
    ArrayList<CarModel> InsertedCarList = new ArrayList<>();
    CompositeDisposable disposable = new CompositeDisposable();
    public final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPapersBinding activityPapersBinding = (ActivityPapersBinding) DataBindingUtil.setContentView(this, R.layout.activity_papers);
        this.binding = activityPapersBinding;
        this.appDatabase = AppDatabase.getAppDatabase(this);
        this.CarId = getIntent().getStringExtra("carModelId");
        setToolbar();
        LoadDocumentData();
        Clicks();




        AdAdmob adAdmob = new AdAdmob( this);
        adAdmob.FullscreenAd(this);
    }

    private void setToolbar() {
        setSupportActionBar(this.binding.toolbar.toolbar);
        getSupportActionBar().setTitle("");
        this.binding.toolbar.title.setText("Papers");
        this.binding.toolbar.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_arrow));
    }

    private void LoadDocumentData() {
        this.binding.progressBar.setVisibility(View.VISIBLE);
        this.binding.llNoData.setVisibility(View.GONE);
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception {
                return PapersActivity.this.m151xecc2ed18();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                PapersActivity.this.m152xf2c6b877((Boolean) obj);
            }
        }));
    }

    
    
    public  Boolean m151xecc2ed18() throws Exception {
        this.documentModelList = this.appDatabase.documentDao().GetAllDocumentByCarId(this.CarId);
        return false;
    }

    
    
    public  void m152xf2c6b877(Boolean bool) throws Exception {
        this.binding.progressBar.setVisibility(View.GONE);
        setAdapter();
        CheckNoData();
        setNestedScroll();
        SortPaperDateDesc();
    }

    
    
    
    public class AnonymousClass1 implements DocumentAdapter.DocumentClick {
        AnonymousClass1() {
        }

        @Override 
        public void OnDocumentClick(int position) {
            Intent intent = new Intent(PapersActivity.this, AddDocumentActivity.class);
            intent.putExtra("isForUpdate", true);
            intent.putExtra("DocumentModel", PapersActivity.this.adapter.getFilterList().get(position));
            PapersActivity.this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
                @Override 
                public final void onActivityResult(Object obj) {
                    PapersActivity.AnonymousClass1.this.m155x39eaf2bc((ActivityResult) obj);
                }
            });
        }

        
        
        public  void m155x39eaf2bc(ActivityResult activityResult) {
            if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
                return;
            }
            Intent data = activityResult.getData();
            DocumentModel documentModel = (DocumentModel) data.getParcelableExtra("DocumentModel");
            boolean booleanExtra = data.getBooleanExtra("isDelete", false);
            int indexOf = PapersActivity.this.documentModelList.indexOf(documentModel);
            if (booleanExtra) {
                PapersActivity.this.documentModelList.remove(indexOf);
                int indexOf2 = PapersActivity.this.adapter.getFilterList().indexOf(documentModel);
                if (indexOf2 != -1) {
                    PapersActivity.this.adapter.getFilterList().remove(indexOf2);
                }
            } else {
                PapersActivity.this.isChange = true;
                PapersActivity.this.documentModelList.set(indexOf, documentModel);
                int indexOf3 = PapersActivity.this.adapter.getFilterList().indexOf(documentModel);
                if (indexOf3 != 1) {
                    PapersActivity.this.adapter.getFilterList().set(indexOf3, documentModel);
                }
                PapersActivity.this.SortPaperDateDesc();
            }
            PapersActivity.this.adapter.notifyDataSetChanged();
            PapersActivity.this.CheckNoData();
        }
    }

    private void setAdapter() {
        this.adapter = new DocumentAdapter(this, this.documentModelList, new AnonymousClass1());
        this.binding.recycle.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        this.binding.recycle.setAdapter(this.adapter);
    }

    private void setNestedScroll() {
        this.binding.recycle.addOnScrollListener(new RecyclerView.OnScrollListener() { 
            @Override 
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    PapersActivity.this.binding.addPaper.hide();
                } else {
                    PapersActivity.this.binding.addPaper.show();
                }
            }
        });
    }

    
    public void CheckNoData() {
        if (this.documentModelList.size() > 0) {
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
                    PapersActivity.this.adapter.getFilter().filter(newText);
                    return false;
                }
            });
            ((ImageView) searchView.findViewById(R.id.search_close_btn)).setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View v) {
                    searchView.onActionViewCollapsed();
                    PapersActivity.this.SortPaperDateDesc();
                }
            });
            return true;
        } else if (item.getItemId() == R.id.add_new_car) {
            this.activityLauncher.launch(new Intent(this, AddCarActivity.class), new BetterActivityResult.OnActivityResult() { 
                @Override 
                public final void onActivityResult(Object obj) {
                    PapersActivity.this.m154x879da08e((ActivityResult) obj);
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

    
    
    public  void m154x879da08e(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        this.InsertedCarList.add((CarModel) activityResult.getData().getParcelableExtra("CarModel"));
    }

    private void Clicks() {
        this.binding.addPaper.setOnClickListener(this);
    }

    @Override 
    public void onClick(View view) {
        if (view.getId() != R.id.add_paper) {
            return;
        }
        Intent intent = new Intent(this, AddDocumentActivity.class);
        intent.putExtra("CarId", this.CarId);
        this.activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult() { 
            @Override 
            public final void onActivityResult(Object obj) {
                PapersActivity.this.m153xd3c2dd71((ActivityResult) obj);
            }
        });
    }

    
    
    public  void m153xd3c2dd71(ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1 || activityResult.getData() == null) {
            return;
        }
        Intent data = activityResult.getData();
        DocumentModel documentModel = (DocumentModel) data.getParcelableExtra("DocumentModel");
        ServiceDocumentModel serviceDocumentModel = (ServiceDocumentModel) data.getParcelableExtra("ServiceDocModel");

        this.documentModelList.add(documentModel);
        SortPaperDateDesc();
        CheckNoData();
    }

    public void SortPaperDateDesc() {
        Collections.sort(this.adapter.getFilterList(), new Comparator<DocumentModel>() { 
            @Override 
            public int compare(DocumentModel o1, DocumentModel o2) {
                return Long.valueOf(o2.getDate()).compareTo(Long.valueOf(o1.getDate()));
            }
        });
        this.adapter.notifyDataSetChanged();
    }

    @Override 
    public void onBackPressed() {
        if (PapersActivity.this.InsertedCarList.size() > 0) {
            Intent intent = PapersActivity.this.getIntent();
            intent.putParcelableArrayListExtra("InsertedCarList", PapersActivity.this.InsertedCarList);
            PapersActivity.this.setResult(-1, intent);
        }
        PapersActivity.this.finish();
    }
}
