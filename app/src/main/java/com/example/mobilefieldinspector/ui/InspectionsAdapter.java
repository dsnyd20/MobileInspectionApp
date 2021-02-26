package com.example.mobilefieldinspector.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilefieldinspector.InspectionDetailActivity;
import com.example.mobilefieldinspector.R;
import com.example.mobilefieldinspector.database.InspectionEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mobilefieldinspector.utilities.Constants.INSPECTION_ID_KEY;

public class InspectionsAdapter extends RecyclerView.Adapter<InspectionsAdapter.ViewHolder> implements Filterable {
    private final List<InspectionEntity> mInspections;
    private final Context mContext;
    private List<InspectionEntity> mInspectionsAll;

    public InspectionsAdapter(List<InspectionEntity> mInspections, Context mContext) {
        this.mInspections = mInspections;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public InspectionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.inspection_list_item, parent, false);
        mInspectionsAll = new ArrayList<>(mInspections);
        return new InspectionsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InspectionsAdapter.ViewHolder holder, int position) {
        final InspectionEntity inspection = mInspections.get(position);
        holder.mInspectionName.setText(inspection.getInspectionName());
        holder.mInspectedDate.setText(inspection.getInspectionDate());

        holder.mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, InspectionDetailActivity.class);
                intent.putExtra(INSPECTION_ID_KEY, inspection.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mInspections.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<InspectionEntity> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0 || constraint.toString().isEmpty()) {
                filteredList.addAll(mInspectionsAll);
            } else {
                for (InspectionEntity item: mInspectionsAll) {
                    if (item.toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mInspections.clear();
            mInspections.addAll((Collection<? extends InspectionEntity>) results.values);
            notifyDataSetChanged();
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.inspection_name)
        TextView mInspectionName;
        @BindView(R.id.inspection_date)
        TextView mInspectedDate;
        @BindView(R.id.fab_edit_inspection)
        FloatingActionButton mFab;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}

