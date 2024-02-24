package com.demo.carservicetracker2.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.databinding.ItemServiceDocumentBinding;
import com.demo.carservicetracker2.model.ServiceDocumentModel;
import com.demo.carservicetracker2.utils.AppConstants;
import java.util.ArrayList;
import java.util.List;


public class ServiceDocumentAdapter extends RecyclerView.Adapter<ServiceDocumentAdapter.ViewHolder> implements Filterable {
    Context context;
    List<ServiceDocumentModel> filterList;
    boolean isReminder;
    ServiceClick serviceClick;
    List<ServiceDocumentModel> serviceDocumentModelList;

    
    public interface ServiceClick {
        void onServiceClick(int position);
    }

    public ServiceDocumentAdapter(Context context, List<ServiceDocumentModel> serviceDocumentModelList, boolean isReminder, ServiceClick serviceClick) {
        this.context = context;
        this.serviceDocumentModelList = serviceDocumentModelList;
        this.isReminder = isReminder;
        this.serviceClick = serviceClick;
        this.filterList = serviceDocumentModelList;
    }

    public List<ServiceDocumentModel> getFilterList() {
        return this.filterList;
    }

    public void setServiceModelList(List<ServiceDocumentModel> serviceDocumentModelList) {
        this.filterList = serviceDocumentModelList;
        this.serviceDocumentModelList = serviceDocumentModelList;
    }

    @Override 
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_document, parent, false));
    }

    @Override 
    public void onBindViewHolder(ViewHolder holder, int position) {
        ServiceDocumentModel serviceDocumentModel = this.filterList.get(position);
        if (this.isReminder) {
            if (serviceDocumentModel.isShowInReminder()) {
                holder.binding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
                holder.binding.img.setImageResource(AppConstants.GetDrawable(serviceDocumentModel.getIconName()));
                holder.binding.txtTitle.setText(serviceDocumentModel.getName());
                return;
            }
            holder.binding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(-2, 0));
            return;
        }
        holder.binding.img.setImageResource(AppConstants.GetDrawable(serviceDocumentModel.getIconName()));
        holder.binding.txtTitle.setText(serviceDocumentModel.getName());
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
                    ServiceDocumentAdapter serviceDocumentAdapter = ServiceDocumentAdapter.this;
                    serviceDocumentAdapter.filterList = serviceDocumentAdapter.serviceDocumentModelList;
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (ServiceDocumentModel serviceDocumentModel : ServiceDocumentAdapter.this.serviceDocumentModelList) {
                        if (serviceDocumentModel != null && serviceDocumentModel.getName() != null && trim != null && serviceDocumentModel.getName().toLowerCase().contains(trim.toLowerCase())) {
                            arrayList.add(serviceDocumentModel);
                        }
                    }
                    ServiceDocumentAdapter.this.filterList = arrayList;
                }
                Filter.FilterResults filterResults = new Filter.FilterResults();
                filterResults.values = ServiceDocumentAdapter.this.filterList;
                return filterResults;
            }

            @Override 
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                ServiceDocumentAdapter.this.notifyDataSetChanged();
            }
        };
    }

    
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemServiceDocumentBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            ItemServiceDocumentBinding itemServiceDocumentBinding = (ItemServiceDocumentBinding) DataBindingUtil.bind(itemView);
            this.binding = itemServiceDocumentBinding;
            itemServiceDocumentBinding.mainLL.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view) {
                    ServiceDocumentAdapter.this.serviceClick.onServiceClick(ViewHolder.this.getAdapterPosition());
                }
            });
        }
    }
}
