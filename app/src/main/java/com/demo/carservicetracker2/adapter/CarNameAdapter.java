package com.demo.carservicetracker2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.database.models.CarModel;
import com.demo.carservicetracker2.databinding.ItemCarNameBinding;

import java.util.List;


public class CarNameAdapter extends RecyclerView.Adapter<CarNameAdapter.ViewHolder> {
    CarClick carClick;
    List<CarModel> carModelList;
    Context context;

    
    public interface CarClick {
        void OnCarClick(int position);
    }

    public CarNameAdapter(Context context, List<CarModel> carModelList, CarClick carClick) {
        this.context = context;
        this.carModelList = carModelList;
        this.carClick = carClick;
    }

    @Override 
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car_name, parent, false));
    }

    @Override 
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.txtCarName.setText(this.carModelList.get(position).getCarName());
    }

    @Override 
    public int getItemCount() {
        return this.carModelList.size();
    }

    
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCarNameBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            ItemCarNameBinding itemCarNameBinding = (ItemCarNameBinding) DataBindingUtil.bind(itemView);
            this.binding = itemCarNameBinding;
            itemCarNameBinding.rlMain.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view) {
                    CarNameAdapter.this.carClick.OnCarClick(ViewHolder.this.getAdapterPosition());
                }
            });
        }
    }
}
