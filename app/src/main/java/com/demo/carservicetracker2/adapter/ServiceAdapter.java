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

import com.demo.carservicetracker2.database.models.ServiceModel;
import com.demo.carservicetracker2.databinding.ItemServiceBinding;
import com.github.mikephil.charting.utils.Utils;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.model.ServiceDocumentModel;
import com.demo.carservicetracker2.utils.AppConstants;
import com.demo.carservicetracker2.utils.AppPref;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> implements Filterable {
    Context context;
    List<ServiceModel> filterList;
    ServiceClick serviceClick;
    List<ServiceModel> serviceModelList;

    
    public interface ServiceClick {
        void OnServiceClick(int position);
    }

    public ServiceAdapter(Context context, List<ServiceModel> serviceModelList, ServiceClick serviceClick) {
        this.context = context;
        this.serviceModelList = serviceModelList;
        this.serviceClick = serviceClick;
        this.filterList = serviceModelList;
    }

    public List<ServiceModel> getFilterList() {
        return this.filterList;
    }

    public void setServiceModelList(List<ServiceModel> serviceModelList) {
        this.filterList = serviceModelList;
        this.serviceModelList = serviceModelList;
    }

    @Override 
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false));
    }

    @Override 
    public void onBindViewHolder(ViewHolder holder, int position) {
        ServiceModel serviceModel = this.filterList.get(position);
        double partsCosts = serviceModel.getPartsCosts();
        double labourCosts = serviceModel.getLabourCosts();
        holder.binding.serviceImage.setImageDrawable(this.context.getDrawable(AppConstants.GetDrawable(AppConstants.GetAllStaticService().get(AppConstants.GetAllStaticService().indexOf(new ServiceDocumentModel(serviceModel.getSelectedServiceID()))).getIconName())));
        if (!TextUtils.isEmpty(serviceModel.getServiceName())) {
            holder.binding.txtServiceName.setVisibility(View.VISIBLE);
            holder.binding.txtServiceName.setText(serviceModel.getServiceName());
        } else {
            holder.binding.txtServiceName.setVisibility(View.GONE);
        }
        if (serviceModel.getDate() > 0) {
            holder.binding.llDate.setVisibility(View.VISIBLE);
            holder.binding.txtDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(serviceModel.getDate())));
        } else {
            holder.binding.llDate.setVisibility(View.GONE);
        }
        if (serviceModel.getMileage() > 0) {
            holder.binding.llMileage.setVisibility(View.VISIBLE);
            TextView textView = holder.binding.txtMileage;
            textView.setText("" + serviceModel.getMileage());
            holder.binding.txtMileageUnit.setText(AppConstants.GetDistanceUnit());
        } else {
            holder.binding.llMileage.setVisibility(View.GONE);
        }
        if (serviceModel.getLabourCosts() > Utils.DOUBLE_EPSILON || serviceModel.getPartsCosts() > Utils.DOUBLE_EPSILON) {
            holder.binding.llCost.setVisibility(View.VISIBLE);
            TextView textView2 = holder.binding.txtTotal;
            textView2.setText(AppConstants.getNumberFormat(Double.valueOf(partsCosts + labourCosts)) + StringUtils.SPACE + AppPref.getCurrency());
            if (serviceModel.getLabourCosts() > Utils.DOUBLE_EPSILON) {
                holder.binding.llLabour.setVisibility(View.VISIBLE);
                TextView textView3 = holder.binding.txtLabour;
                textView3.setText(AppConstants.getNumberFormat(Double.valueOf(serviceModel.getLabourCosts())) + StringUtils.SPACE + AppPref.getCurrency());
            } else {
                holder.binding.llLabour.setVisibility(View.GONE);
            }
            if (serviceModel.getPartsCosts() > Utils.DOUBLE_EPSILON) {
                holder.binding.llParts.setVisibility(View.VISIBLE);
                TextView textView4 = holder.binding.txtParts;
                textView4.setText(AppConstants.getNumberFormat(Double.valueOf(serviceModel.getPartsCosts())) + StringUtils.SPACE + AppPref.getCurrency());
                return;
            }
            holder.binding.llParts.setVisibility(View.GONE);
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
                    ServiceAdapter serviceAdapter = ServiceAdapter.this;
                    serviceAdapter.filterList = serviceAdapter.serviceModelList;
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (ServiceModel serviceModel : ServiceAdapter.this.serviceModelList) {
                        if (serviceModel != null && serviceModel.getServiceName() != null && trim != null && serviceModel.getServiceName().toLowerCase().contains(trim.toLowerCase())) {
                            arrayList.add(serviceModel);
                        }
                    }
                    ServiceAdapter.this.filterList = arrayList;
                }
                Filter.FilterResults filterResults = new Filter.FilterResults();
                filterResults.values = ServiceAdapter.this.filterList;
                return filterResults;
            }

            @Override 
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                ServiceAdapter.this.notifyDataSetChanged();
            }
        };
    }

    
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemServiceBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            ItemServiceBinding itemServiceBinding = (ItemServiceBinding) DataBindingUtil.bind(itemView);
            this.binding = itemServiceBinding;
            itemServiceBinding.carMain.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view) {
                    ServiceAdapter.this.serviceClick.OnServiceClick(ViewHolder.this.getAdapterPosition());
                }
            });
        }
    }
}
