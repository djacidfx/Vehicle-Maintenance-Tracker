package com.demo.carservicetracker2.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.database.models.ReminderModel;
import com.demo.carservicetracker2.databinding.ItemReminderBinding;
import com.demo.carservicetracker2.model.ServiceDocumentModel;
import com.demo.carservicetracker2.utils.AppConstants;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> implements Filterable {
    Context context;
    List<ReminderModel> filterList;
    ReminderClick reminderClick;
    List<ReminderModel> reminderModelList;

    
    public interface ReminderClick {
        void OnReminderClick(int position);
    }

    public ReminderAdapter(Context context, List<ReminderModel> reminderModelList, ReminderClick reminderClick) {
        this.context = context;
        this.reminderModelList = reminderModelList;
        this.reminderClick = reminderClick;
        this.filterList = reminderModelList;
    }

    public List<ReminderModel> getFilterList() {
        return this.filterList;
    }

    @Override 
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false));
    }

    @Override 
    public void onBindViewHolder(ViewHolder holder, int position) {
        ServiceDocumentModel serviceDocumentModel;
        ReminderModel reminderModel = this.filterList.get(position);
        if (reminderModel.getReminderType() == 1) {
            serviceDocumentModel = AppConstants.GetAllStaticService().get(AppConstants.GetAllStaticService().indexOf(new ServiceDocumentModel(reminderModel.getSelectedServiceDocId())));
        } else {
            serviceDocumentModel = AppConstants.GetAllStaticDocument().get(AppConstants.GetAllStaticDocument().indexOf(new ServiceDocumentModel(reminderModel.getSelectedServiceDocId())));
        }
        Glide.with(this.context).load(this.context.getDrawable(AppConstants.GetDrawable(serviceDocumentModel.getIconName()))).into(holder.binding.reminderImage);
        if (!TextUtils.isEmpty(reminderModel.getRemainderText())) {
            holder.binding.txtReminderName.setVisibility(View.VISIBLE);
            holder.binding.txtReminderName.setText(reminderModel.getRemainderText());
        } else {
            holder.binding.txtReminderName.setVisibility(View.GONE);
        }
        holder.binding.txtDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(reminderModel.getDate())));
        Date GetDate = AppConstants.GetDate(reminderModel.getDateOfCreation());
        Date GetDate2 = AppConstants.GetDate(reminderModel.getDate());
        Date GetDate3 = AppConstants.GetDate(System.currentTimeMillis());
        if (GetDate3.before(GetDate)) {
            holder.binding.txtPercentage.setText("0%");
            holder.binding.txtPercentage.setTextColor(Color.parseColor(AppConstants.GetProgressColor(0)));
            holder.binding.progressBar.setProgress(0);
            holder.binding.progressBar.setIndicatorColor(Color.parseColor(AppConstants.GetProgressColor(0)));
        } else if (GetDate2.before(GetDate3) || GetDate2.before(GetDate)) {
            holder.binding.txtPercentage.setText("100%");
            holder.binding.txtPercentage.setTextColor(Color.parseColor(AppConstants.GetProgressColor(100)));
            holder.binding.progressBar.setProgress(100);
            holder.binding.progressBar.setIndicatorColor(Color.parseColor(AppConstants.GetProgressColor(100)));
        } else {
            int GetDaysDiff = (int) ((((float) AppConstants.GetDaysDiff(reminderModel.getDateOfCreation(), System.currentTimeMillis())) * 100.0f) / ((float) AppConstants.GetDaysDiff(reminderModel.getDateOfCreation(), reminderModel.getDate())));
            TextView textView = holder.binding.txtPercentage;
            textView.setText("" + GetDaysDiff + "%");
            holder.binding.txtPercentage.setTextColor(Color.parseColor(AppConstants.GetProgressColor(GetDaysDiff)));
            holder.binding.progressBar.setProgress(GetDaysDiff);
            holder.binding.progressBar.setIndicatorColor(Color.parseColor(AppConstants.GetProgressColor(GetDaysDiff)));
        }
    }

    public void setReminderModelList(List<ReminderModel> reminderModelList) {
        this.filterList = reminderModelList;
        this.reminderModelList = reminderModelList;
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
                    ReminderAdapter reminderAdapter = ReminderAdapter.this;
                    reminderAdapter.filterList = reminderAdapter.reminderModelList;
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (ReminderModel reminderModel : ReminderAdapter.this.reminderModelList) {
                        if (reminderModel != null && reminderModel.getRemainderText() != null && trim != null && reminderModel.getRemainderText().toLowerCase().contains(trim.toLowerCase())) {
                            arrayList.add(reminderModel);
                        }
                    }
                    ReminderAdapter.this.filterList = arrayList;
                }
                Filter.FilterResults filterResults = new Filter.FilterResults();
                filterResults.values = ReminderAdapter.this.filterList;
                return filterResults;
            }

            @Override 
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                ReminderAdapter.this.notifyDataSetChanged();
            }
        };
    }

    
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemReminderBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            ItemReminderBinding itemReminderBinding = (ItemReminderBinding) DataBindingUtil.bind(itemView);
            this.binding = itemReminderBinding;
            itemReminderBinding.reminderMain.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view) {
                    ReminderAdapter.this.reminderClick.OnReminderClick(ViewHolder.this.getAdapterPosition());
                }
            });
        }
    }
}
