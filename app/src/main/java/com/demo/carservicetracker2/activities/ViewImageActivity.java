package com.demo.carservicetracker2.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.databinding.ActivityViewImageBinding;
import com.demo.carservicetracker2.adapter.FilterPagerAdapter;
import com.demo.carservicetracker2.database.models.ImageModel;
import com.demo.carservicetracker2.utils.AppConstants;
import java.io.File;
import java.util.ArrayList;
import net.lingala.zip4j.util.InternalZipConstants;
import org.apache.commons.lang3.StringUtils;


public class ViewImageActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityViewImageBinding binding;
    ArrayList<ImageModel> imageList = new ArrayList<>();
    boolean isDelete = false;
    FilterPagerAdapter pagerAdapter;
    int position;

    
    public void checkImagePosition() {
    }

    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = (ActivityViewImageBinding) DataBindingUtil.setContentView(this, R.layout.activity_view_image);
        Init();
    }

    private void Init() {
        this.imageList = getIntent().getParcelableArrayListExtra("imageList");
        this.position = getIntent().getIntExtra("position", 0);
        this.binding.shareImage.setVisibility(View.GONE);
        setToolbar();
        setOnClicks();
        initMethod();
        setAdapter();
    }

    private void initMethod() {
        TextView textView = this.binding.txtSwipeSize;
        textView.setText((this.position + 1) + StringUtils.SPACE);
        TextView textView2 = this.binding.txtSize;
        textView2.setText("of " + this.imageList.size());
    }

    public void setToolbar() {
        this.binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() { 
            @Override 
            public void onClick(View view) {
                ViewImageActivity.this.onBackPressed();
            }
        });
    }

    private void isAvailable() {
        if (this.imageList.size() > 0) {
            this.binding.llCard.setVisibility(View.VISIBLE);
            this.binding.llNoData.setVisibility(View.GONE);
            return;
        }
        this.binding.llCard.setVisibility(View.GONE);
        this.binding.llNoData.setVisibility(View.VISIBLE);
    }

    protected void setAdapter() {
        this.pagerAdapter = new FilterPagerAdapter(this, this.imageList);
        this.binding.viewPager.setAdapter(this.pagerAdapter);
        this.binding.viewPager.setCurrentItem(this.position);
        isAvailable();
        setAdapterCount();
    }

    public void setAdapterCount() {
        this.binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { 
            @Override 
            public void onPageScrollStateChanged(int state) {
            }

            @Override 
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override 
            public void onPageSelected(int positi) {
                ViewImageActivity.this.position = positi;
                TextView textView = ViewImageActivity.this.binding.txtSwipeSize;
                textView.setText((ViewImageActivity.this.position + 1) + StringUtils.SPACE);
                ViewImageActivity.this.checkImagePosition();
            }
        });
    }

    protected void setOnClicks() {
        this.binding.shareImage.setOnClickListener(this);
    }

    @Override 
    public void onClick(View view) {
        if (view.getId() != R.id.shareImage) {
            return;
        }
        Uri uriForFile = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(AppConstants.getDatabasePath(this) + InternalZipConstants.ZIP_FILE_SEPARATOR + getResources().getString(R.string.app_name) + InternalZipConstants.ZIP_FILE_SEPARATOR + this.imageList.get(this.position).getImageName()));
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("android.intent.extra.STREAM", uriForFile);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
