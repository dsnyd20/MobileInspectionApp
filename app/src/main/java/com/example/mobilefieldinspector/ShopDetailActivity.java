package com.example.mobilefieldinspector;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilefieldinspector.database.InspectionEntity;
import com.example.mobilefieldinspector.database.PhotoEntity;
import com.example.mobilefieldinspector.database.ShopEntity;
import com.example.mobilefieldinspector.ui.InspectionsAdapter;
import com.example.mobilefieldinspector.ui.PhotoAdapter;
import com.example.mobilefieldinspector.viewmodel.ShopDetailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.example.mobilefieldinspector.utilities.Constants.EDITING_KEY;
import static com.example.mobilefieldinspector.utilities.Constants.SHOP_ID_KEY;

public class ShopDetailActivity extends AppCompatActivity {
    private ShopDetailViewModel mViewModel;
    private TextView mShopName;
    private TextView mShopAddress;
    private TextView mShopPhone;
    private boolean mNewShop, mEditing;
    private RecyclerView mRecyclerView;
    private InspectionsAdapter mAdapterInspections;
    private List<InspectionEntity> inspectionsData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        mShopName = findViewById(R.id.fieldShopName);
        mShopAddress = findViewById(R.id.fieldShopAddress);
        mShopPhone = findViewById(R.id.fieldShopPhone);
        mRecyclerView = findViewById(R.id.recycler_view_railcar_inspections);

        FloatingActionButton fabSaveShop = findViewById(R.id.fab_save_shop);
        fabSaveShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndReturn();
            }
        });

        if(savedInstanceState != null) {
            mEditing = savedInstanceState.getBoolean(EDITING_KEY);
        }

        initRecyclerView();
        initViewModel();
    }

    private void saveAndReturn() {
        int success = mViewModel.saveShop(mShopName.getText().toString(),mShopAddress.getText().toString(), mShopPhone.getText().toString());
        if (success == 1) {
            Toast toast = Toast.makeText(getApplicationContext(), "Save Successful!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Save Unsuccessful! Please do not leave the shop name, address, or phone number blank.", Toast.LENGTH_LONG);
            toast.show();
        }

    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this)
                .get(ShopDetailViewModel.class);

        mViewModel.mLiveShop.observe(this, new Observer<ShopEntity>() {
            @Override
            public void onChanged(ShopEntity shopEntity) {
                if(shopEntity != null && !mEditing) {
                    mShopName.setText(shopEntity.getShopName());
                    mShopAddress.setText(shopEntity.getShopAddress());
                    mShopPhone.setText(shopEntity.getShopPhone());
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setTitle("New Shop");
            mNewShop = true;
        } else {
            setTitle("Edit Shop");
            int shopId = extras.getInt(SHOP_ID_KEY);
            mViewModel.loadData(shopId);
        }


        //Create an observer
        //Create an observer
        final Observer<List<InspectionEntity>> inspectionObserver = new Observer<List<InspectionEntity>>() {
            @Override
            public void onChanged(List<InspectionEntity> inspectionEntities) {
                inspectionsData.clear();
                inspectionsData.addAll(inspectionEntities);
                if (mAdapterInspections == null) {
                    mAdapterInspections = new InspectionsAdapter(inspectionsData, ShopDetailActivity.this);
                    mRecyclerView.setAdapter(mAdapterInspections);
                } else {
                    mAdapterInspections.notifyDataSetChanged();
                }

            }
        };

        //Subscribe to the observer
        mViewModel.mInspections.observe(this, inspectionObserver);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EDITING_KEY, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if (!mNewShop) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.delete_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.action_delete) {
            if (mViewModel.shopUsage > 0) {
                Toast toast = Toast.makeText(getApplicationContext(), "Delete Unsuccessful! Please make sure there are no inspections assigned to this shop.", Toast.LENGTH_LONG);
                toast.show();
                return false;
            }
            mViewModel.deleteShop();
            Toast toast = Toast.makeText(getApplicationContext(), "Delete Successful!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
        return true;
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true); //each item will appear as the same size in the list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapterInspections = new InspectionsAdapter(inspectionsData, this);
        mRecyclerView.setAdapter(mAdapterInspections);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }
}