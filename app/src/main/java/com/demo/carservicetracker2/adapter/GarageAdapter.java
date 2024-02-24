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
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.database.models.CarModel;
import com.demo.carservicetracker2.databinding.ItemCarBinding;
import com.demo.carservicetracker2.utils.AppConstants;
import java.util.ArrayList;
import java.util.List;
import net.lingala.zip4j.util.InternalZipConstants;


public class GarageAdapter extends RecyclerView.Adapter<GarageAdapter.ViewHolder> implements Filterable {
    List<CarModel> carModelList;
    CarModelSingleListener carModelSingleListener;
    Context context;
    List<CarModel> filterList;

    
    public interface CarModelSingleListener {
        void CarModelClick(int position);
    }

    public List<CarModel> getFilterList() {
        return this.filterList;
    }

    public GarageAdapter(Context context, List<CarModel> carModelList, CarModelSingleListener carModelSingleListener) {
        this.context = context;
        this.carModelList = carModelList;
        this.carModelSingleListener = carModelSingleListener;
        this.filterList = carModelList;
    }

    @Override 
    public Filter getFilter() {
        return new Filter() { 
            @Override 
            protected Filter.FilterResults performFiltering(CharSequence charSequence) {
                String trim = charSequence.toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    GarageAdapter garageAdapter = GarageAdapter.this;
                    garageAdapter.filterList = garageAdapter.carModelList;
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (CarModel carModel : GarageAdapter.this.carModelList) {
                        if (carModel != null && carModel.getCarName() != null && trim != null && carModel.getCarName().toLowerCase().contains(trim.toLowerCase())) {
                            arrayList.add(carModel);
                        }
                    }
                    GarageAdapter.this.filterList = arrayList;
                }
                Filter.FilterResults filterResults = new Filter.FilterResults();
                filterResults.values = GarageAdapter.this.filterList;
                return filterResults;
            }

            @Override 
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                GarageAdapter.this.notifyDataSetChanged();
            }
        };
    }

    @Override 
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false));
    }

    @Override 
    public void onBindViewHolder(ViewHolder holder, int position) {
        CarModel carModel = this.filterList.get(position);
        holder.binding.txtCarName.setText(carModel.getCarName());
        if (carModel.getPurchaseDate() > 0) {
            holder.binding.llDate.setVisibility(View.VISIBLE);
            holder.binding.txtDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(carModel.getPurchaseDate())));
        } else {
            holder.binding.llDate.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(carModel.getCarImageName())) {
            holder.binding.carImage.setVisibility(View.GONE);
            holder.binding.defaultCar.setVisibility(View.VISIBLE);
        } else {
            holder.binding.carImage.setVisibility(View.VISIBLE);
            holder.binding.defaultCar.setVisibility(View.GONE);
            RequestManager with = Glide.with(this.context);
            with.load(AppConstants.getMediaDir(this.context) + InternalZipConstants.ZIP_FILE_SEPARATOR + carModel.getCarImageName()).into(holder.binding.carImage);
        }
        if (!TextUtils.isEmpty(carModel.getComments())) {
            holder.binding.txtComments.setVisibility(View.VISIBLE);
            holder.binding.txtComments.setText(carModel.getComments());
            return;
        }
        holder.binding.txtComments.setVisibility(View.GONE);
    }

    @Override 
    public int getItemCount() {
        return this.filterList.size();
    }

    
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCarBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            ItemCarBinding itemCarBinding = (ItemCarBinding) DataBindingUtil.bind(itemView);
            this.binding = itemCarBinding;
            itemCarBinding.carMain.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view) {
                    GarageAdapter.this.carModelSingleListener.CarModelClick(ViewHolder.this.getAdapterPosition());
                }
            });
        }
    }
}
