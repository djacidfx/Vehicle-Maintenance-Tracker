package com.demo.carservicetracker2.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.carservicetracker2.AdAdmob;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.databinding.ActivitySettingsBinding;
import com.demo.carservicetracker2.adapter.CurrencyNameAdapter;
import com.demo.carservicetracker2.model.CurrencyModel;
import com.demo.carservicetracker2.utils.AppConstants;
import com.demo.carservicetracker2.utils.AppPref;
import com.demo.carservicetracker2.utils.Currency;

import java.util.List;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    MenuItem addCar;
    ActivitySettingsBinding binding;
    boolean isChange = false;
    Point p;
    MenuItem search;

    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = (ActivitySettingsBinding) DataBindingUtil.setContentView(this, R.layout.activity_settings);
        setToolbar();
        Init();
        Clicks();


        AdAdmob adAdmob = new AdAdmob( this);
        adAdmob.BannerAd((RelativeLayout) findViewById(R.id.banner), this);
        adAdmob.FullscreenAd(this);


    }

    private void setToolbar() {
        setSupportActionBar(this.binding.toolbar);
        getSupportActionBar().setTitle("");
        this.binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_arrow));






    }

    private void Init() {
        this.binding.txtDistanceUnit.setText(AppConstants.GetFullDistanceUnit());
        this.binding.txtVolumeUnit.setText(AppConstants.GetFullVolumeUnit());
        this.binding.txtCurrency.setText(AppPref.getCurrencyName());
        if (AppPref.IsShowFuel()) {
            this.binding.fuelShowImg.setImageDrawable(getResources().getDrawable(R.drawable.on));
        } else {
            this.binding.fuelShowImg.setImageDrawable(getResources().getDrawable(R.drawable.off));
        }
    }

    @Override 
    public void onWindowFocusChanged(boolean hasFocus) {
        int[] iArr = new int[2];
        this.binding.llCurrency.getLocationOnScreen(iArr);
        Point point = new Point();
        this.p = point;
        point.x = iArr[0];
        this.p.y = iArr[1];
    }

    private void showStatusPopup(final Activity context, Point p) {
        View inflate = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.car_list_layout, (ViewGroup) null);
        final PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setContentView(inflate);
        popupWindow.setWidth(-2);
        popupWindow.setHeight(-2);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(inflate, 0, p.x - 20, p.y + 50);
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.carRecycle);
        final List<CurrencyModel> CurrencyList = Currency.CurrencyList();
        CurrencyNameAdapter currencyNameAdapter = new CurrencyNameAdapter(context, CurrencyList, new CurrencyNameAdapter.CurrencyClick() { 
            @Override 
            public void OnCurrencyClick(int position) {
                CurrencyModel currencyModel = (CurrencyModel) CurrencyList.get(position);
                SettingsActivity.this.binding.txtCurrency.setText(currencyModel.getCurrencyName());
                AppPref.setCurrency(currencyModel.getCurrencySymbol());
                AppPref.setCurrencyName(currencyModel.getCurrencyName());
                popupWindow.dismiss();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(currencyNameAdapter);
    }

    @Override 
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        this.search = menu.findItem(R.id.search);
        MenuItem findItem = menu.findItem(R.id.add_new_car);
        this.addCar = findItem;
        findItem.setVisible(false);
        this.search.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            onBackPressed();
            return true;
        }
        return true;
    }

    private void Clicks() {
        this.binding.llDistancePopup.setOnClickListener(this);
        this.binding.llVolumePopup.setOnClickListener(this);
        this.binding.fuelShowImg.setOnClickListener(this);
        this.binding.llDistance.setOnClickListener(this);
        this.binding.llVolume.setOnClickListener(this);
        this.binding.llCurrency.setOnClickListener(this);
    }

    @Override 
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fuelShowImg :
                this.isChange = true;
                if (AppPref.IsShowFuel()) {
                    this.binding.fuelShowImg.setImageDrawable(getResources().getDrawable(R.drawable.off));
                    AppPref.setIsShowFuel(false);
                    return;
                }
                this.binding.fuelShowImg.setImageDrawable(getResources().getDrawable(R.drawable.on));
                AppPref.setIsShowFuel(true);
                return;
            case R.id.llCurrency :
                this.isChange = true;
                Point point = this.p;
                if (point != null) {
                    showStatusPopup(this, point);
                    return;
                }
                return;
            case R.id.llDistance :
            case R.id.llDistancePopup :
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.getMenuInflater().inflate(R.menu.distance_unit, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { 
                    @Override 
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        SettingsActivity.this.isChange = true;
                        SettingsActivity.this.binding.txtDistanceUnit.setText(menuItem.getTitle().toString());
                        AppPref.setDistanceUnit(menuItem.getTitle().toString());
                        return true;
                    }
                });
                popupMenu.show();
                return;
            case R.id.llVolume :
            case R.id.llVolumePopup :
                PopupMenu popupMenu2 = new PopupMenu(this, view);
                popupMenu2.getMenuInflater().inflate(R.menu.volume_unit, popupMenu2.getMenu());
                popupMenu2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { 
                    @Override 
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        SettingsActivity.this.isChange = true;
                        SettingsActivity.this.binding.txtVolumeUnit.setText(menuItem.getTitle().toString());
                        AppPref.setVolumeUnit(menuItem.getTitle().toString());
                        return true;
                    }
                });
                popupMenu2.show();
                return;
            default:
                return;
        }
    }



    @Override 
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("isChange", this.isChange);
        setResult(-1, intent);
        finish();
    }
}
