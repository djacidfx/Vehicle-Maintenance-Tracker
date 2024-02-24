package com.demo.carservicetracker2.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.database.models.ImageModel;
import com.demo.carservicetracker2.databinding.ItemPagerImgBinding;
import com.demo.carservicetracker2.utils.AppConstants;
import java.util.ArrayList;
import net.lingala.zip4j.util.InternalZipConstants;


public class FilterPagerAdapter extends PagerAdapter {
    Context context;
    ArrayList<ImageModel> list;

    @Override 
    public int getItemPosition(Object object) {
        return -2;
    }

    public FilterPagerAdapter(Context context, ArrayList<ImageModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override 
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override 
    public int getCount() {
        return this.list.size();
    }

    @Override 
    public Object instantiateItem(ViewGroup container, int position) {
        ItemPagerImgBinding itemPagerImgBinding = (ItemPagerImgBinding) DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.item_pager_img, container, false);
        if (this.list.get(position).isNew()) {
            Glide.with(this.context).load(Uri.parse(this.list.get(position).getUri())).into(itemPagerImgBinding.imgs);
        } else {
            RequestManager with = Glide.with(this.context);
            with.load(AppConstants.getDatabasePath(this.context) + InternalZipConstants.ZIP_FILE_SEPARATOR + this.context.getResources().getString(R.string.app_name) + InternalZipConstants.ZIP_FILE_SEPARATOR + this.list.get(position).getImageName()).into(itemPagerImgBinding.imgs);
        }
        container.addView(itemPagerImgBinding.getRoot());
        itemPagerImgBinding.executePendingBindings();
        return itemPagerImgBinding.getRoot();
    }

    @Override 
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }
}
