package com.demo.carservicetracker2.backupRestore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.databinding.RestoreItemBinding;

import java.util.ArrayList;


public class RestoreAdapter extends RecyclerView.Adapter<RestoreAdapter.ViewHolder> {
    private ArrayList<RestoreRowModel> arrayList;
    Context context;
    private OnRecyclerItemClick itemClick;

    public RestoreAdapter(Context context, ArrayList<RestoreRowModel> data, OnRecyclerItemClick itemClickListener) {
        this.arrayList = data;
        this.context = context;
        this.itemClick = itemClickListener;
    }

    @Override 
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.restore_item, parent, false));
    }

    @Override 
    public void onBindViewHolder(final ViewHolder holder, int position) {
        RestoreRowModel restoreRowModel = this.arrayList.get(position);
        holder.binding.txtName.setText(restoreRowModel.getTitle());
        holder.binding.txtDate.setText(restoreRowModel.getDateModified());
        holder.binding.txtSize.setText(restoreRowModel.getSize());
    }

    @Override 
    public int getItemCount() {
        return this.arrayList.size();
    }

    
    public class ViewHolder extends RecyclerView.ViewHolder {
        RestoreItemBinding binding;

        ViewHolder(final View itemView) {
            super(itemView);
            this.binding = (RestoreItemBinding) DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View v) {
                    RestoreAdapter.this.itemClick.onClick(ViewHolder.this.getAdapterPosition(), 1);
                }
            });
            this.binding.imgDelete.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View v) {
                    RestoreAdapter.this.itemClick.onClick(ViewHolder.this.getAdapterPosition(), 2);
                }
            });
        }
    }
}
