package com.demo.carservicetracker2.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.database.models.ImageModel;
import com.demo.carservicetracker2.databinding.ItemImageBinding;
import com.demo.carservicetracker2.utils.AppConstants;
import java.util.List;
import net.lingala.zip4j.util.InternalZipConstants;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.DataHolder> {
    Context context;
    List<ImageModel> imageList;
    ImageRemoveListener imageRemoveListener;
    LayoutInflater inflater;
    OnItemClick onItemClick;

    
    public interface ImageRemoveListener {
        void OnRemoveImage(int position);
    }

    
    public interface OnItemClick {
        void OnItemClick(int position);
    }

    public ImageAdapter(Context context, List<ImageModel> imageList, ImageRemoveListener imageRemoveListener, OnItemClick onItemClick) {
        this.context = context;
        this.imageList = imageList;
        this.imageRemoveListener = imageRemoveListener;
        this.onItemClick = onItemClick;
        this.inflater = LayoutInflater.from(context);
    }

    @Override 
    public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DataHolder(this.inflater.inflate(R.layout.item_image, parent, false));
    }

    public void setList(List<ImageModel> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    @Override 
    public void onBindViewHolder(DataHolder holder, int position) {

        if (this.imageList.get(position).isNew()) {
            Glide.with(this.context).load(Uri.parse(this.imageList.get(position).getUri())).into(holder.binding.ivLogo);
        } else {
            RequestManager with = Glide.with(this.context);

            with.load(AppConstants.getMediaDir(this.context)
                            + InternalZipConstants.ZIP_FILE_SEPARATOR +
                            this.imageList.get(position).getImageName()).
                    into(holder.binding.ivLogo);
        }
        if (this.imageList.get(position).isDeleted()) {
            holder.binding.llMain.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParams = holder.binding.llMain.getLayoutParams();
            layoutParams.width = 0;
            layoutParams.height = 0;
            holder.binding.llMain.setLayoutParams(layoutParams);
            return;
        }
        holder.binding.llMain.setVisibility(View.VISIBLE);
    }

    @Override 
    public int getItemCount() {
        return this.imageList.size();
    }

    public int getIndexOfModel(ImageModel imageMaster) {
        return this.imageList.indexOf(imageMaster);
    }

    
    
    public class DataHolder extends RecyclerView.ViewHolder {
        ItemImageBinding binding;

        public DataHolder(View itemView) {
            super(itemView);
            ItemImageBinding itemImageBinding = (ItemImageBinding) DataBindingUtil.bind(itemView);
            this.binding = itemImageBinding;
            itemImageBinding.ivDelete.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view) {
                    ImageAdapter.this.imageRemoveListener.OnRemoveImage(DataHolder.this.getAdapterPosition());
                }
            });
            this.binding.rlLogo.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view) {
                    ImageAdapter.this.onItemClick.OnItemClick(DataHolder.this.getAdapterPosition());
                }
            });
        }
    }
}
