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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilefieldinspector.R;
import com.example.mobilefieldinspector.ShopDetailActivity;
import com.example.mobilefieldinspector.database.ShopEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mobilefieldinspector.utilities.Constants.SHOP_ID_KEY;

public class ShopsAdapter extends RecyclerView.Adapter<ShopsAdapter.ViewHolder> implements Filterable {
    private final List<ShopEntity> mShops;
    private List<ShopEntity> mShopsAll;
    private final Context mContext;

    public ShopsAdapter(List<ShopEntity> mShops, Context mContext) {
        this.mShops = mShops;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ShopsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.shop_list_item, parent, false);
        mShopsAll = new ArrayList<>(mShops);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopsAdapter.ViewHolder holder, int position) {
        final ShopEntity shop = mShops.get(position);
        holder.mShopName.setText(shop.getShopName());

        holder.mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShopDetailActivity.class);
                intent.putExtra(SHOP_ID_KEY, shop.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mShops.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ShopEntity> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0 || constraint.toString().isEmpty()) {
                filteredList.addAll(mShopsAll);
            } else {
                for (ShopEntity item: mShopsAll) {
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
            mShops.clear();
            mShops.addAll((Collection<? extends ShopEntity>) results.values);
            notifyDataSetChanged();
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.shop_name)
        TextView mShopName;
        @BindView(R.id.fab_edit_shop)
        FloatingActionButton mFab;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}

