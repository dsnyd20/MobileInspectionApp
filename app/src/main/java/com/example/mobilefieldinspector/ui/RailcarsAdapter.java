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

import com.example.mobilefieldinspector.R;
import com.example.mobilefieldinspector.RailcarDetailActivity;
import com.example.mobilefieldinspector.database.RailcarEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mobilefieldinspector.utilities.Constants.RAILCAR_ID_KEY;

public class RailcarsAdapter extends RecyclerView.Adapter<RailcarsAdapter.ViewHolder> implements Filterable {
    private List<RailcarEntity> mRailcars;
    private List<RailcarEntity> mRailcarsAll;
    private final Context mContext;



    public RailcarsAdapter(List<RailcarEntity> mRailcars, Context mContext) {
        this.mRailcars = mRailcars;
        this.mContext = mContext;
        //this.mRailcarsAll = new ArrayList<>(mRailcars);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.railcar_list_item, parent, false);
        mRailcarsAll = new ArrayList<>(mRailcars);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RailcarEntity railcar = mRailcars.get(position);
        holder.mRunningNumber.setText(railcar.getRunningNumber());
        holder.mBuiltDate.setText(railcar.getBuiltDate());

        holder.mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RailcarDetailActivity.class);
                intent.putExtra(RAILCAR_ID_KEY, railcar.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRailcars.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<RailcarEntity> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0 || constraint.toString().isEmpty()) {
                filteredList.addAll(mRailcarsAll);
            } else {
                for (RailcarEntity item: mRailcarsAll) {
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
            mRailcars.clear();
            mRailcars.addAll((Collection<? extends RailcarEntity>) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.railcar_running_number)
        TextView mRunningNumber;
        @BindView(R.id.railcar_built_date)
        TextView mBuiltDate;
        @BindView(R.id.fab_edit_railcar)
        FloatingActionButton mFab;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}

