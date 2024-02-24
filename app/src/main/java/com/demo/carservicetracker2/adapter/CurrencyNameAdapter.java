package com.demo.carservicetracker2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.databinding.ItemCarNameBinding;
import com.demo.carservicetracker2.model.CurrencyModel;
import java.util.List;


public class CurrencyNameAdapter extends RecyclerView.Adapter<CurrencyNameAdapter.ViewHolder> {
    Context context;
    CurrencyClick currencyClick;
    List<CurrencyModel> currencyModelList;

    
    public interface CurrencyClick {
        void OnCurrencyClick(int position);
    }

    public CurrencyNameAdapter(Context context, List<CurrencyModel> currencyModelList, CurrencyClick currencyClick) {
        this.context = context;
        this.currencyModelList = currencyModelList;
        this.currencyClick = currencyClick;
    }

    @Override 
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car_name, parent, false));
    }

    @Override 
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.txtCarName.setText(this.currencyModelList.get(position).getCurrencyName());
    }

    @Override 
    public int getItemCount() {
        return this.currencyModelList.size();
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
                    CurrencyNameAdapter.this.currencyClick.OnCurrencyClick(ViewHolder.this.getAdapterPosition());
                }
            });
        }
    }
}
