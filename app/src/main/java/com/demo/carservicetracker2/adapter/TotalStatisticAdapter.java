package com.demo.carservicetracker2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.databinding.ItemStatisticBinding;
import com.demo.carservicetracker2.model.MonthlyReportModel;
import com.demo.carservicetracker2.utils.AppConstants;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


public class TotalStatisticAdapter extends RecyclerView.Adapter<TotalStatisticAdapter.ViewHolder> {
    ArrayList<Integer> colors;
    Context context;
    List<MonthlyReportModel> reportDataModelList;

    public TotalStatisticAdapter(Context context, List<MonthlyReportModel> reportDataModelList, ArrayList<Integer> colors) {
        this.context = context;
        this.reportDataModelList = reportDataModelList;
        this.colors = colors;
    }

    @Override 
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistic, parent, false));
    }

    @Override 
    public void onBindViewHolder(ViewHolder holder, int position) {
        MonthlyReportModel monthlyReportModel = this.reportDataModelList.get(position);
        holder.binding.monthBgCard.setCardBackgroundColor(this.colors.get(position).intValue());
        holder.binding.txtTitle.setText(monthlyReportModel.getMonthName());
        TextView textView = holder.binding.txtAmt;
        textView.setText(AppConstants.getNumberFormat(Double.valueOf(monthlyReportModel.getTotalAmt())) + StringUtils.SPACE + AppConstants.GetCurrency());
    }

    @Override 
    public int getItemCount() {
        return this.reportDataModelList.size();
    }

    
    public class ViewHolder extends RecyclerView.ViewHolder {
        com.demo.carservicetracker2.databinding.ItemStatisticBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            this.binding = (ItemStatisticBinding) DataBindingUtil.bind(itemView);
        }
    }
}
