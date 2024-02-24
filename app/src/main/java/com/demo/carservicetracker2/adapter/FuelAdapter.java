package com.demo.carservicetracker2.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.carservicetracker2.database.models.FuelModel;
import com.demo.carservicetracker2.databinding.ItemFuelBinding;
import com.github.mikephil.charting.utils.Utils;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.utils.AppConstants;
import java.util.ArrayList;
import java.util.List;
import net.lingala.zip4j.util.InternalZipConstants;
import org.apache.commons.lang3.StringUtils;


public class FuelAdapter extends RecyclerView.Adapter<FuelAdapter.ViewHolder> implements Filterable {
    Context context;
    List<FuelModel> filterList;
    FuelClick fuelClick;
    List<FuelModel> fuelModelList;

    
    public interface FuelClick {
        void OnFuelClick(int position);
    }

    public FuelAdapter(Context context, List<FuelModel> fuelModelList, FuelClick fuelClick) {
        this.context = context;
        this.fuelModelList = fuelModelList;
        this.fuelClick = fuelClick;
        this.filterList = fuelModelList;
    }

    public List<FuelModel> getFilterList() {
        return this.filterList;
    }

    public void setFuelModelList(List<FuelModel> fuelModelList) {
        this.filterList = fuelModelList;
        this.fuelModelList = fuelModelList;
    }

    @Override 
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fuel, parent, false));
    }

    @Override 
    public void onBindViewHolder(ViewHolder holder, int position) {
        FuelModel fuelModel = this.filterList.get(position);
        if (!TextUtils.isEmpty(fuelModel.getFuelType())) {
            holder.binding.txtFuelType.setText(fuelModel.getFuelType());
        } else {
            holder.binding.txtFuelType.setText("Fuel");
        }
        holder.binding.txtDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(fuelModel.getDate())));
        if (fuelModel.getMileage() > 0) {
            holder.binding.llMileage.setVisibility(View.VISIBLE);
            TextView textView = holder.binding.txtMileage;
            textView.setText("" + fuelModel.getMileage());
            holder.binding.txtMileageUnit.setText(AppConstants.GetDistanceUnit());
        } else {
            holder.binding.llMileage.setVisibility(View.GONE);
        }
        if (fuelModel.getTotalCost() > Utils.DOUBLE_EPSILON || fuelModel.getPricePerLiter() > Utils.DOUBLE_EPSILON || fuelModel.getFuelVolume() > Utils.DOUBLE_EPSILON) {
            holder.binding.llCost.setVisibility(View.VISIBLE);
            if (fuelModel.getTotalCost() > Utils.DOUBLE_EPSILON) {
                holder.binding.txtTotal.setVisibility(View.VISIBLE);
                TextView textView2 = holder.binding.txtTotal;
                textView2.setText(AppConstants.getNumberFormat(Double.valueOf(fuelModel.getTotalCost())) + StringUtils.SPACE + AppConstants.GetCurrency());
            } else {
                holder.binding.txtTotal.setVisibility(View.GONE);
            }
            if (fuelModel.getPricePerLiter() > Utils.DOUBLE_EPSILON) {
                holder.binding.llPricePerLiter.setVisibility(View.VISIBLE);
                TextView textView3 = holder.binding.txtPricePerLiter;
                textView3.setText(AppConstants.getNumberFormat(Double.valueOf(fuelModel.getPricePerLiter())) + StringUtils.SPACE + AppConstants.GetCurrency() + InternalZipConstants.ZIP_FILE_SEPARATOR + AppConstants.GetVolumeUnit());
            } else {
                holder.binding.llPricePerLiter.setVisibility(View.GONE);
            }
            if (fuelModel.getFuelVolume() > Utils.DOUBLE_EPSILON) {
                holder.binding.llVolume.setVisibility(View.VISIBLE);
                TextView textView4 = holder.binding.txtVolume;
                textView4.setText(AppConstants.getNumberFormat(Double.valueOf(fuelModel.getFuelVolume())) + AppConstants.GetVolumeUnit());
                return;
            }
            holder.binding.llVolume.setVisibility(View.GONE);
            return;
        }
        holder.binding.llCost.setVisibility(View.GONE);
    }

    @Override 
    public int getItemCount() {
        return this.filterList.size();
    }

    @Override 
    public Filter getFilter() {
        return new Filter() { 
            @Override 
            protected Filter.FilterResults performFiltering(CharSequence charSequence) {
                String trim = charSequence.toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    FuelAdapter fuelAdapter = FuelAdapter.this;
                    fuelAdapter.filterList = fuelAdapter.fuelModelList;
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (FuelModel fuelModel : FuelAdapter.this.fuelModelList) {
                        if (fuelModel != null && fuelModel.getFuelType() != null && trim != null && fuelModel.getFuelType().toLowerCase().contains(trim.toLowerCase())) {
                            arrayList.add(fuelModel);
                        }
                    }
                    FuelAdapter.this.filterList = arrayList;
                }
                Filter.FilterResults filterResults = new Filter.FilterResults();
                filterResults.values = FuelAdapter.this.filterList;
                return filterResults;
            }

            @Override 
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                FuelAdapter.this.notifyDataSetChanged();
            }
        };
    }

    
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemFuelBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            ItemFuelBinding itemFuelBinding = (ItemFuelBinding) DataBindingUtil.bind(itemView);
            this.binding = itemFuelBinding;
            itemFuelBinding.mainFuel.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view) {
                    FuelAdapter.this.fuelClick.OnFuelClick(ViewHolder.this.getAdapterPosition());
                }
            });
        }
    }
}
