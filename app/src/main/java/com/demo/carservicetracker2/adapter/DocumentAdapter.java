package com.demo.carservicetracker2.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.carservicetracker2.database.models.DocumentModel;
import com.demo.carservicetracker2.databinding.ItemDocumentBinding;
import com.github.mikephil.charting.utils.Utils;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.model.ServiceDocumentModel;
import com.demo.carservicetracker2.utils.AppConstants;
import com.demo.carservicetracker2.utils.AppPref;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> implements Filterable {
    Context context;
    DocumentClick documentClick;
    List<DocumentModel> documentModelList;
    List<DocumentModel> filterList;

    
    public interface DocumentClick {
        void OnDocumentClick(int position);
    }

    public DocumentAdapter(Context context, List<DocumentModel> documentModelList, DocumentClick documentClick) {
        this.context = context;
        this.documentModelList = documentModelList;
        this.documentClick = documentClick;
        this.filterList = documentModelList;
    }

    public List<DocumentModel> getFilterList() {
        return this.filterList;
    }

    public void setDocumentModelList(List<DocumentModel> documentModelList) {
        this.filterList = documentModelList;
        this.documentModelList = documentModelList;
    }

    @Override 
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false));
    }

    @Override 
    public void onBindViewHolder(ViewHolder holder, int position) {
        DocumentModel documentModel = this.filterList.get(position);
        double totalCoast = documentModel.getTotalCoast();
        holder.binding.documentImage.setImageDrawable(this.context.getDrawable(AppConstants.GetDrawable(AppConstants.GetAllStaticDocument().get(AppConstants.GetAllStaticDocument().indexOf(new ServiceDocumentModel(documentModel.getSelectedDocId()))).getIconName())));
        if (!TextUtils.isEmpty(documentModel.getDocName())) {
            holder.binding.txtDocumentName.setVisibility(View.VISIBLE);
            holder.binding.txtDocumentName.setText(documentModel.getDocName());
        } else {
            holder.binding.txtDocumentName.setVisibility(View.GONE);
        }
        holder.binding.txtDate.setText(AppConstants.simpleDateFormat.format(Long.valueOf(documentModel.getDate())));
        if (documentModel.getMileage() > 0) {
            holder.binding.llMileage.setVisibility(View.VISIBLE);
            TextView textView = holder.binding.txtMileage;
            textView.setText("" + documentModel.getMileage());
            holder.binding.txtMileageUnit.setText(AppConstants.GetDistanceUnit());
        } else {
            holder.binding.llMileage.setVisibility(View.GONE);
        }
        if (totalCoast > Utils.DOUBLE_EPSILON) {
            holder.binding.txtCost.setVisibility(View.VISIBLE);
            TextView textView2 = holder.binding.txtCost;
            textView2.setText("" + AppConstants.getNumberFormat(Double.valueOf(totalCoast)) + StringUtils.SPACE + AppPref.getCurrency());
            return;
        }
        holder.binding.txtCost.setVisibility(View.GONE);
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
                    DocumentAdapter documentAdapter = DocumentAdapter.this;
                    documentAdapter.filterList = documentAdapter.documentModelList;
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (DocumentModel documentModel : DocumentAdapter.this.documentModelList) {
                        if (documentModel != null && documentModel.getDocName() != null && trim != null && documentModel.getDocName().toLowerCase().contains(trim.toLowerCase())) {
                            arrayList.add(documentModel);
                        }
                    }
                    DocumentAdapter.this.filterList = arrayList;
                }
                Filter.FilterResults filterResults = new Filter.FilterResults();
                filterResults.values = DocumentAdapter.this.filterList;
                return filterResults;
            }

            @Override 
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                DocumentAdapter.this.notifyDataSetChanged();
            }
        };
    }

    
    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemDocumentBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            ItemDocumentBinding itemDocumentBinding = (ItemDocumentBinding) DataBindingUtil.bind(itemView);
            this.binding = itemDocumentBinding;
            itemDocumentBinding.docMain.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View view) {
                    DocumentAdapter.this.documentClick.OnDocumentClick(ViewHolder.this.getAdapterPosition());
                }
            });
        }
    }
}
