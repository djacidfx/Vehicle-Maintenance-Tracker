package com.demo.carservicetracker2.activities;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.carservicetracker2.databinding.ActivityReportsBinding;
import com.demo.carservicetracker2.model.ReportDataModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.demo.carservicetracker2.R;
import com.kal.rackmonthpicker.MonthType;
import com.kal.rackmonthpicker.RackMonthPicker;
import com.kal.rackmonthpicker.listener.DateMonthDialogListener;
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener;
import com.demo.carservicetracker2.adapter.CarNameAdapter;
import com.demo.carservicetracker2.adapter.TotalMonthStatisticAdapter;
import com.demo.carservicetracker2.adapter.TotalStatisticAdapter;
import com.demo.carservicetracker2.database.AppDatabase;
import com.demo.carservicetracker2.database.models.CarModel;
import com.demo.carservicetracker2.model.MonthlyReportModel;
import com.demo.carservicetracker2.utils.AppConstants;
import com.demo.carservicetracker2.utils.AppPref;
import com.demo.carservicetracker2.utils.BetterActivityResult;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;
public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {
    TotalMonthStatisticAdapter adapter;
    AppDatabase appDatabase;
    ActivityReportsBinding binding;
    String carId;
    Typeface fontFamily;
    CarModel mainCarModel;
    Point p;
    TotalStatisticAdapter statisticAdapter;
    List<CarModel> carModelList = new ArrayList();
    ArrayList<CarModel> InsertedCarList = new ArrayList<>();
    ArrayList<Integer> colors = new ArrayList<>();
    ArrayList<Integer> totalColors = new ArrayList<>();
    List<MonthlyReportModel> reportMonthlyDataModelList = new ArrayList();
    List<MonthlyReportModel> reportTotalDataList = new ArrayList();
    CompositeDisposable disposable = new CompositeDisposable();
    long customDate = 0;
    public final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = (ActivityReportsBinding) DataBindingUtil.setContentView(this, R.layout.activity_reports);
        this.appDatabase = AppDatabase.getAppDatabase(this);
        this.carId = getIntent().getStringExtra("carModelId");
        this.fontFamily = Typeface.createFromAsset(getAssets(), "semibold.ttf");
        this.customDate = System.currentTimeMillis();
        LoadCarData();
        Clicks();
        setMonthlyColors();
        setTotalColors();





    }

    private void setTotalColors() {
        this.totalColors.add(Integer.valueOf(getResources().getColor(R.color.red)));
        this.totalColors.add(Integer.valueOf(getResources().getColor(R.color.green)));
        this.totalColors.add(Integer.valueOf(getResources().getColor(R.color.yellow)));
    }

    private void setMonthlyColors() {
        this.colors.add(Integer.valueOf(getResources().getColor(R.color.jan)));
        this.colors.add(Integer.valueOf(getResources().getColor(R.color.fab)));
        this.colors.add(Integer.valueOf(getResources().getColor(R.color.mar)));
        this.colors.add(Integer.valueOf(getResources().getColor(R.color.apr)));
        this.colors.add(Integer.valueOf(getResources().getColor(R.color.may)));
        this.colors.add(Integer.valueOf(getResources().getColor(R.color.jun)));
        this.colors.add(Integer.valueOf(getResources().getColor(R.color.jul)));
        this.colors.add(Integer.valueOf(getResources().getColor(R.color.aug)));
        this.colors.add(Integer.valueOf(getResources().getColor(R.color.sep)));
        this.colors.add(Integer.valueOf(getResources().getColor(R.color.oct)));
        this.colors.add(Integer.valueOf(getResources().getColor(R.color.nov)));
        this.colors.add(Integer.valueOf(getResources().getColor(R.color.dec)));
    }

    private void LoadCarData() {
        this.disposable.add(Observable.fromCallable(new Callable() { 
            @Override 
            public final Object call() throws Exception {
                return ReportsActivity.this.m163xdb5847ff();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer() { 
            @Override 
            public final void accept(Object obj) throws Exception {
                ReportsActivity.this.m164x95cde880((Boolean) obj);
            }
        }));
    }

    
    
    public  Boolean m163xdb5847ff() throws Exception {
        List<CarModel> GetAllCar = this.appDatabase.carDao().GetAllCar();
        this.carModelList = GetAllCar;
        if (GetAllCar.contains(new CarModel(this.carId))) {
            int indexOf = this.carModelList.indexOf(new CarModel(this.carId));
            this.carModelList.get(indexOf).setSelected(true);
            this.mainCarModel = this.carModelList.get(indexOf);
        } else {
            this.carModelList.get(0).setSelected(true);
            this.mainCarModel = this.carModelList.get(0);
        }
        return false;
    }

    
    
    public  void m164x95cde880(Boolean bool) throws Exception {
        this.binding.txtCarName.setText(this.mainCarModel.getCarName());
        this.binding.txtFilterType.setText(AppPref.getReportDuration());
        this.binding.llCustom.setVisibility(AppPref.getReportDuration().equals("Custom") ? View.VISIBLE : View.GONE);
        this.binding.fuelCard.setVisibility(AppPref.IsShowFuel() ? View.VISIBLE : View.GONE);
        this.binding.txtCustomType.setText(AppPref.getCustomType());
        this.binding.txtDate.setText((AppPref.getCustomType().equals(AppConstants.MONTH) ? AppConstants.MonthFormat : AppConstants.YearFormat).format(Long.valueOf(this.customDate)));
        setMonthlyChartAdapter();
        setTotalChartAdapter();
        setChartData();
    }

    private void setTotalChartAdapter() {
        this.statisticAdapter = new TotalStatisticAdapter(this, this.reportTotalDataList, this.totalColors);
        this.binding.totalRecycle.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        this.binding.totalRecycle.setAdapter(this.statisticAdapter);
    }

    private void setMonthlyChartAdapter() {
        this.adapter = new TotalMonthStatisticAdapter(this, this.reportMonthlyDataModelList, this.colors);
        this.binding.monthlyRecycle.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        this.binding.monthlyRecycle.setAdapter(this.adapter);
    }

    @Override 
    public void onWindowFocusChanged(boolean hasFocus) {
        int[] iArr = new int[2];
        this.binding.rlCarName.getLocationOnScreen(iArr);
        Point point = new Point();
        this.p = point;
        point.x = iArr[0];
        this.p.y = iArr[1];
    }

    private void Clicks() {
        this.binding.back.setOnClickListener(this);
        this.binding.rlCarName.setOnClickListener(this);
        this.binding.llDuration.setOnClickListener(this);
        this.binding.llPicker.setOnClickListener(this);
        this.binding.llCustomType.setOnClickListener(this);
    }

    private void showStatusPopup(final Activity context, Point p) {
        View inflate = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.car_name_list_layout, (ViewGroup) null);
        final PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setContentView(inflate);
        popupWindow.setWidth(-2);
        popupWindow.setHeight(-2);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(inflate, 0, p.x - 20, p.y + 50);
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.carRecycle);
        CarNameAdapter carNameAdapter = new CarNameAdapter(context, this.carModelList, new CarNameAdapter.CarClick() { 
            @Override 
            public void OnCarClick(int position) {
                ReportsActivity.this.mainCarModel.setSelected(false);
                ReportsActivity.this.mainCarModel = ReportsActivity.this.carModelList.get(position);
                ReportsActivity.this.mainCarModel.setSelected(true);
                ReportsActivity.this.binding.txtCarName.setText(ReportsActivity.this.mainCarModel.getCarName());
                popupWindow.dismiss();
                ReportsActivity.this.setChartData();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(carNameAdapter);
    }

    @Override 
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back :
                onBackPressed();
                return;
            case R.id.llCustomType :
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.getMenuInflater().inflate(R.menu.custom_type, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { 
                    @Override 
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.month) {
                            ReportsActivity.this.binding.txtCustomType.setText(AppConstants.MONTH);
                            AppPref.setCustomType(AppConstants.MONTH);
                            ReportsActivity.this.binding.txtDate.setText(AppConstants.MonthFormat.format(Long.valueOf(ReportsActivity.this.customDate)));
                        } else if (menuItem.getItemId() == R.id.year) {
                            ReportsActivity.this.binding.txtCustomType.setText(AppConstants.YEAR);
                            AppPref.setCustomType(AppConstants.YEAR);
                            ReportsActivity.this.binding.txtDate.setText(AppConstants.YearFormat.format(Long.valueOf(ReportsActivity.this.customDate)));
                        }
                        ReportsActivity.this.setChartData();
                        return true;
                    }
                });
                popupMenu.show();
                return;
            case R.id.llDuration :
                PopupMenu popupMenu2 = new PopupMenu(this, view);
                popupMenu2.getMenuInflater().inflate(R.menu.duration_filter, popupMenu2.getMenu());
                popupMenu2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { 
                    @Override 
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.currentYear) {
                            ReportsActivity.this.binding.llCustom.setVisibility(View.GONE);
                            AppPref.setReportDuration(AppConstants.CURRENT_YEAR);
                        } else if (menuItem.getItemId() == R.id.lastYear) {
                            ReportsActivity.this.binding.llCustom.setVisibility(View.GONE);
                            AppPref.setReportDuration(AppConstants.LAST_YEAR);
                        } else if (menuItem.getItemId() == R.id.threeMonths) {
                            ReportsActivity.this.binding.llCustom.setVisibility(View.GONE);
                            AppPref.setReportDuration(AppConstants.LAST_THREE_MONTHS);
                        } else if (menuItem.getItemId() == R.id.sixMonths) {
                            ReportsActivity.this.binding.llCustom.setVisibility(View.GONE);
                            AppPref.setReportDuration(AppConstants.LAST_SIX_MONTHS);
                        } else if (menuItem.getItemId() == R.id.currentMonth) {
                            ReportsActivity.this.binding.llCustom.setVisibility(View.GONE);
                            AppPref.setReportDuration(AppConstants.CURRENT_MONTHS);
                        } else if (menuItem.getItemId() == R.id.lastMonth) {
                            ReportsActivity.this.binding.llCustom.setVisibility(View.GONE);
                            AppPref.setReportDuration(AppConstants.LAST_MONTHS);
                        }









                        ReportsActivity.this.binding.txtFilterType.setText(AppPref.getReportDuration());
                        ReportsActivity.this.setChartData();
                        return true;
                    }
                });
                popupMenu2.show();
                return;
            case R.id.llPicker :
                try {
                     Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    if (AppPref.getCustomType().equals(AppConstants.MONTH)) {
























































                        return;
                    }
                    new RackMonthPicker(this)
                            .setLocale(Locale.ENGLISH)
                            .setMonthType(MonthType.NUMBER)
                            .setPositiveButton(new DateMonthDialogListener() {
                                @Override
                                public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
                                    calendar.set(2, month);
                                    calendar.set(1, year);
                                    calendar.set(5, 1);
                                    calendar.set(11, 0);
                                    calendar.set(12, 0);
                                    calendar.set(13, 0);
                                    calendar.set(14, 0);
                                    ReportsActivity.this.customDate = calendar.getTimeInMillis();
                                    ReportsActivity.this.binding.txtDate.setText(AppConstants.YearFormat.format(Long.valueOf(calendar.getTimeInMillis())));
                                    AppPref.setReportDuration("Custom");
                                    AppPref.setCustomType(AppConstants.YEAR);
                                    ReportsActivity.this.setChartData();
                                }
                            })
                            .setNegativeButton(new OnCancelMonthDialogListener() {
                                @Override
                                public void onCancel(AlertDialog dialog) {
                                    dialog.dismiss();
                                }
                            }).show();









                    return;
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("MYTAG", "ErrorNo: onClick:" + e);
                }

            case R.id.rlCarName :
                Point point = this.p;
                if (point != null) {
                    showStatusPopup(this, point);
                    return;
                }
                return;
            default:
                return;
        }
    }

    
    public void setChartData() {
        setTotalChart();
        setMonthlyChart();
        setDataInChart();
    }

    private void setTotalChart() {
        this.binding.totalPieChart.setUsePercentValues(true);
        this.binding.totalPieChart.getDescription().setEnabled(false);
        this.binding.totalPieChart.setExtraOffsets(5.0f, 10.0f, 5.0f, 5.0f);
        this.binding.totalPieChart.setDragDecelerationFrictionCoef(0.95f);
        this.binding.totalPieChart.setDrawHoleEnabled(true);
        this.binding.totalPieChart.setHoleColor(-1);
        this.binding.totalPieChart.setTransparentCircleColor(-1);
        this.binding.totalPieChart.setTransparentCircleAlpha(110);
        this.binding.totalPieChart.setHoleRadius(58.0f);
        this.binding.totalPieChart.setTransparentCircleRadius(61.0f);
        this.binding.totalPieChart.setDrawCenterText(true);
        this.binding.totalPieChart.setRotationAngle(0.0f);
        this.binding.totalPieChart.setRotationEnabled(true);
        this.binding.totalPieChart.setHighlightPerTapEnabled(true);
        this.binding.totalPieChart.animateY(1400, Easing.EaseInOutQuad);
        this.binding.totalPieChart.getLegend().setEnabled(false);
        this.binding.totalPieChart.setEntryLabelColor(getResources().getColor(R.color.font1));
        this.binding.totalPieChart.setEntryLabelTypeface(this.fontFamily);
        this.binding.totalPieChart.setEntryLabelTextSize(10.0f);
    }

    private void setMonthlyChart() {
        this.binding.pieChart.setUsePercentValues(true);
        this.binding.pieChart.getDescription().setEnabled(false);
        this.binding.pieChart.setExtraOffsets(5.0f, 10.0f, 5.0f, 5.0f);
        this.binding.pieChart.setDragDecelerationFrictionCoef(0.95f);
        this.binding.pieChart.setDrawHoleEnabled(true);
        this.binding.pieChart.setHoleColor(-1);
        this.binding.pieChart.setTransparentCircleColor(-1);
        this.binding.pieChart.setTransparentCircleAlpha(110);
        this.binding.pieChart.setHoleRadius(58.0f);
        this.binding.pieChart.setTransparentCircleRadius(61.0f);
        this.binding.pieChart.setDrawCenterText(true);
        this.binding.pieChart.setRotationAngle(0.0f);
        this.binding.pieChart.setRotationEnabled(true);
        this.binding.pieChart.setHighlightPerTapEnabled(true);
        this.binding.pieChart.animateY(1400, Easing.EaseInOutQuad);
        this.binding.pieChart.getLegend().setEnabled(false);
        this.binding.pieChart.setEntryLabelColor(getResources().getColor(R.color.font1));
        this.binding.pieChart.setEntryLabelTypeface(this.fontFamily);
        this.binding.pieChart.setEntryLabelTextSize(10.0f);
    }

    private void setDataInChart() {
        long GetOnlyDateInMillis;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String reportDuration = AppPref.getReportDuration();
        reportDuration.hashCode();
        char c = 65535;
        switch (reportDuration.hashCode()) {
            case -2025718474:
                if (reportDuration.equals(AppConstants.CURRENT_YEAR)) {
                    c = 0;
                    break;
                }
                break;
            case -1394624493:
                if (reportDuration.equals(AppConstants.LAST_YEAR)) {
                    c = 1;
                    break;
                }
                break;
            case -294458006:
                if (reportDuration.equals(AppConstants.LAST_MONTHS)) {
                    c = 2;
                    break;
                }
                break;
            case 1348823903:
                if (reportDuration.equals(AppConstants.LAST_SIX_MONTHS)) {
                    c = 3;
                    break;
                }
                break;
            case 1616465063:
                if (reportDuration.equals(AppConstants.CURRENT_MONTHS)) {
                    c = 4;
                    break;
                }
                break;
            case 1732445339:
                if (reportDuration.equals(AppConstants.LAST_THREE_MONTHS)) {
                    c = 5;
                    break;
                }
                break;
            case 2029746065:
                if (reportDuration.equals("Custom")) {
                    c = 6;
                    break;
                }
                break;
        }
        long j = 0;
        switch (c) {
            case 0:
                calendar.set(2, 0);
                calendar.set(5, 1);
                j = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                calendar.set(2, 11);
                calendar.set(5, calendar.getActualMaximum(5));
                GetOnlyDateInMillis = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                break;
            case 1:
                calendar.set(1, calendar.get(1) - 1);
                calendar.set(2, 0);
                calendar.set(5, 1);
                j = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                calendar.set(2, 11);
                calendar.set(5, calendar.getActualMaximum(5));
                GetOnlyDateInMillis = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                break;
            case 2:
                calendar.set(2, calendar.get(2) - 1);
                calendar.set(5, 1);
                j = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                calendar.set(5, calendar.getActualMaximum(5));
                GetOnlyDateInMillis = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                break;
            case 3:
                calendar.set(2, calendar.get(2) - 5);
                calendar.set(5, 1);
                j = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                calendar.add(2, 5);
                calendar.set(5, calendar.getActualMaximum(5));
                GetOnlyDateInMillis = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                break;
            case 4:
                calendar.set(5, 1);
                j = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                calendar.set(5, calendar.getActualMaximum(5));
                GetOnlyDateInMillis = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                break;
            case 5:
                calendar.set(2, calendar.get(2) - 2);
                calendar.set(5, 1);
                j = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                calendar.add(2, 2);
                calendar.set(5, calendar.getActualMaximum(5));
                GetOnlyDateInMillis = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                break;
            case 6:
                calendar.setTimeInMillis(this.customDate);
                j = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                calendar.set(5, calendar.getActualMaximum(5));
                GetOnlyDateInMillis = AppConstants.GetOnlyDateInMillis(calendar.getTimeInMillis());
                break;
            default:
                GetOnlyDateInMillis = 0;
                break;
        }
        setTotalChartData(j, GetOnlyDateInMillis);
        setMonthlyChartData(j, GetOnlyDateInMillis);
    }

    private void setTotalChartData(long startDate, long endDate) {
        ReportDataModel GetCarDataBetweenTwoDates = this.appDatabase.carDao().GetCarDataBetweenTwoDates(startDate, endDate, this.mainCarModel.getCarId());
        setTotalChartDescription(GetCarDataBetweenTwoDates);
        if (GetCarDataBetweenTwoDates.getTotalDocumentCost() == 0.0f && GetCarDataBetweenTwoDates.getTotalFuelCost() == 0.0f && GetCarDataBetweenTwoDates.getTotalServiceCost() == 0.0f) {
            this.binding.totalPieChart.clear();
            this.binding.totalPieChart.invalidate();
            this.binding.totalPieChart.setNoDataText("No chart data available.");
            this.binding.llTotalDescription.setVisibility(View.GONE);
            return;
        }
        this.binding.llTotalDescription.setVisibility(View.VISIBLE);
        this.binding.totalPieChart.invalidate();
        this.reportTotalDataList.clear();
        ArrayList arrayList = new ArrayList();
        if (GetCarDataBetweenTwoDates.getTotalDocumentCost() > 0.0f) {
            arrayList.add(new PieEntry(GetCarDataBetweenTwoDates.getTotalDocumentCost(), "Paper"));
            this.reportTotalDataList.add(new MonthlyReportModel("Paper", GetCarDataBetweenTwoDates.getTotalDocumentCost()));
        }
        if (GetCarDataBetweenTwoDates.getTotalServiceCost() > 0.0f) {
            arrayList.add(new PieEntry(GetCarDataBetweenTwoDates.getTotalServiceCost(), "Service"));
            this.reportTotalDataList.add(new MonthlyReportModel("Service", GetCarDataBetweenTwoDates.getTotalServiceCost()));
        }
        if (AppPref.IsShowFuel() && GetCarDataBetweenTwoDates.getTotalFuelCost() > 0.0f) {
            arrayList.add(new PieEntry(GetCarDataBetweenTwoDates.getTotalFuelCost(), "Fuel"));
            this.reportTotalDataList.add(new MonthlyReportModel("Fuel", GetCarDataBetweenTwoDates.getTotalFuelCost()));
        }
        this.statisticAdapter.notifyDataSetChanged();
        PieDataSet pieDataSet = new PieDataSet(arrayList, "");
        pieDataSet.setColors(this.totalColors);
        pieDataSet.setDrawIcons(false);
        pieDataSet.setSliceSpace(3.0f);
        pieDataSet.setIconsOffset(new MPPointF(0.0f, 40.0f));
        pieDataSet.setSelectionShift(5.0f);
        pieDataSet.setValueLinePart1OffsetPercentage(80.0f);
        pieDataSet.setValueLinePart1Length(0.2f);
        pieDataSet.setValueLinePart2Length(0.4f);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(11.0f);
        pieData.setValueTypeface(this.fontFamily);
        pieData.setValueTextColor(-1);
        this.binding.totalPieChart.setData(pieData);
        ((PieData) this.binding.totalPieChart.getData()).notifyDataChanged();
        this.binding.totalPieChart.highlightValues(null);
    }

    private void setTotalChartDescription(ReportDataModel reportDataModel) {
        if (AppPref.IsShowFuel()) {
            this.binding.fuelCard.setVisibility(View.VISIBLE);
            TextView textView = this.binding.txtFuelCost;
            textView.setText("" + AppConstants.getNumberFormat(Double.valueOf(reportDataModel.getTotalFuelCost())) + StringUtils.SPACE + AppConstants.GetCurrency());
        } else {
            this.binding.fuelCard.setVisibility(View.GONE);
        }
        TextView textView2 = this.binding.txtServiceCost;
        textView2.setText(AppConstants.getNumberFormat(Double.valueOf(reportDataModel.getTotalServiceCost())) + StringUtils.SPACE + AppConstants.GetCurrency());
        TextView textView3 = this.binding.txtPaperCost;
        textView3.setText(AppConstants.getNumberFormat(Double.valueOf((double) reportDataModel.getTotalDocumentCost())) + StringUtils.SPACE + AppConstants.GetCurrency());
        TextView textView4 = this.binding.txtTotalCost;
        textView4.setText(AppConstants.getNumberFormat(Double.valueOf((double) reportDataModel.getTotal())) + StringUtils.SPACE + AppConstants.GetCurrency());
    }

    private void setMonthlyChartData(long startDate, long endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(startDate);
        this.binding.pieChart.invalidate();
        ArrayList arrayList = new ArrayList();
        this.reportMonthlyDataModelList.clear();
        double d = 0.0d;
        while (calendar.getTimeInMillis() < endDate) {
            calendar2.set(5, calendar2.getActualMaximum(5));
            ReportDataModel GetCarDataBetweenTwoDates = this.appDatabase.carDao().GetCarDataBetweenTwoDates(calendar.getTimeInMillis(), AppConstants.GetOnlyDateInMillis(calendar2.getTimeInMillis()), this.mainCarModel.getCarId());
            MonthlyReportModel monthlyReportModel = new MonthlyReportModel(AppConstants.GetMonthName(calendar.get(2)), GetCarDataBetweenTwoDates.getTotal());
            if (GetCarDataBetweenTwoDates.getTotal() > 0.0f) {
                this.reportMonthlyDataModelList.add(monthlyReportModel);
                arrayList.add(new PieEntry(GetCarDataBetweenTwoDates.getTotal(), AppConstants.GetMonthName(calendar.get(2))));
            }
            d += GetCarDataBetweenTwoDates.getTotal();
            calendar.add(2, 1);
            calendar2.setTimeInMillis(calendar.getTimeInMillis());
        }
        this.adapter.notifyDataSetChanged();
        if (d > Utils.DOUBLE_EPSILON) {
            this.binding.monthlyRecycle.setVisibility(View.VISIBLE);
            PieDataSet pieDataSet = new PieDataSet(arrayList, "");
            pieDataSet.setColors(this.colors);
            pieDataSet.setDrawIcons(false);
            pieDataSet.setSliceSpace(3.0f);
            pieDataSet.setIconsOffset(new MPPointF(0.0f, 40.0f));
            pieDataSet.setSelectionShift(5.0f);
            pieDataSet.setValueLinePart1OffsetPercentage(80.0f);
            pieDataSet.setValueLinePart1Length(0.2f);
            pieDataSet.setValueLinePart2Length(0.4f);
            pieDataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
            pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            PieData pieData = new PieData(pieDataSet);
            pieData.setValueFormatter(new PercentFormatter());
            pieData.setValueTextSize(11.0f);
            pieData.setValueTextColor(-1);
            pieData.setValueTypeface(this.fontFamily);
            this.binding.pieChart.setData(pieData);
            ((PieData) this.binding.pieChart.getData()).notifyDataChanged();
            this.binding.pieChart.highlightValues(null);
            return;
        }
        this.binding.pieChart.clear();
        this.binding.pieChart.invalidate();
        this.binding.pieChart.setNoDataText("No chart data available.");
        this.binding.monthlyRecycle.setVisibility(View.GONE);
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
