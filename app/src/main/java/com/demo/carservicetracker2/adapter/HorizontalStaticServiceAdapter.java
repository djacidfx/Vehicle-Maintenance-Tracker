package com.demo.carservicetracker2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.databinding.ItemHorizontalServiceBinding;
import com.demo.carservicetracker2.model.ServiceDocumentModel;
import com.demo.carservicetracker2.utils.AppConstants;
import java.util.List;


public class HorizontalStaticServiceAdapter extends RecyclerView.Adapter<HorizontalStaticServiceAdapter.ViewHolder> {
    Context context;
    ServiceClick serviceClick;
    List<ServiceDocumentModel> serviceList;

    
    public interface ServiceClick {
        void OnServiceClick(int position);
    }

    public HorizontalStaticServiceAdapter(Context context, List<ServiceDocumentModel> serviceList, ServiceClick serviceClick) {
        this.context = context;
        this.serviceList = serviceList;
        this.serviceClick = serviceClick;
    }

    @Override 
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_service, parent, false));
    }

    @Override 
    public void onBindViewHolder(ViewHolder holder, int position) {
        ServiceDocumentModel serviceDocumentModel = this.serviceList.get(position);
        Glide.with(this.context).load(Integer.valueOf(AppConstants.GetDrawable(serviceDocumentModel.getIconName()))).into(holder.binding.serviceImg);
        holder.binding.txtServiceName.setText(serviceDocumentModel.getName());
    }

    @Override 
    public int getItemCount() {
        return this.serviceList.size();
    }

    
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHorizontalServiceBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            ItemHorizontalServiceBinding itemHorizontalServiceBinding = (ItemHorizontalServiceBinding) DataBindingUtil.bind(itemView);
            this.binding = itemHorizontalServiceBinding;
            itemHorizontalServiceBinding.mainCard.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view) {
                    HorizontalStaticServiceAdapter.this.serviceClick.OnServiceClick(ViewHolder.this.getAdapterPosition());
                }
            });
        }
    }
}
