package com.demo.carservicetracker2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.databinding.ItemFuelNameBinding;
import com.demo.carservicetracker2.model.FuelNameModel;
import java.util.List;


public class FuelNameAdapter extends RecyclerView.Adapter<FuelNameAdapter.ViewHolder> {
    Context context;
    FuelClick fuelClick;
    List<FuelNameModel> fuelNameModelList;

    
    public interface FuelClick {
        void OnFuelClick(int position);
    }

    public FuelNameAdapter(Context context, List<FuelNameModel> fuelNameModelList, FuelClick fuelClick) {
        this.context = context;
        this.fuelNameModelList = fuelNameModelList;
        this.fuelClick = fuelClick;
    }

    @Override 
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fuel_name, parent, false));
    }

    @Override 
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.txtFuelName.setText(this.fuelNameModelList.get(position).getName());
    }

    @Override 
    public int getItemCount() {
        return this.fuelNameModelList.size();
    }

    
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemFuelNameBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            ItemFuelNameBinding itemFuelNameBinding = (ItemFuelNameBinding) DataBindingUtil.bind(itemView);
            this.binding = itemFuelNameBinding;
            itemFuelNameBinding.rlMain.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view) {
                    FuelNameAdapter.this.fuelClick.OnFuelClick(ViewHolder.this.getAdapterPosition());
                }
            });
        }
    }
}
