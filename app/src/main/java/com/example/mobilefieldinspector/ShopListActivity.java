package com.example.mobilefieldinspector;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilefieldinspector.database.ShopEntity;
import com.example.mobilefieldinspector.ui.ShopsAdapter;
import com.example.mobilefieldinspector.viewmodel.ShopListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopListActivity extends AppCompatActivity {
    private List<ShopEntity> shopsData = new ArrayList<>();
    private ShopsAdapter mAdapter;
    private ShopListViewModel mViewModel;

    @BindView(R.id.recycler_view_shops)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Shops");
        setContentView(R.layout.activity_shop_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        initRecyclerView();
        initViewModel();

        FloatingActionButton fab = findViewById(R.id.fab_add_shop);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopListActivity.this, ShopDetailActivity.class));
            }
        });
    }

    private void initViewModel() {
        //Create an observer
        final Observer<List<ShopEntity>> shopObserver = new Observer<List<ShopEntity>>() {
            @Override
            public void onChanged(List<ShopEntity> shopEntities) {
                shopsData.clear();
                shopsData.addAll(shopEntities);
                if (mAdapter == null) {
                    mAdapter = new ShopsAdapter(shopsData, ShopListActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
                }

            }
        };

        mViewModel = ViewModelProviders.of(this)
                .get(ShopListViewModel.class);

        //Subscribe to the observer
        mViewModel.mShops.observe(this, shopObserver);

    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true); //each item will appear as the same size in the list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ShopsAdapter(shopsData, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_sample_data) {
            addSampleData();
            return true;
        } else if (id == R.id.action_delete_all) {
            deleteAllData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAllData() {
        mViewModel.deleteAllData();
    }

    private void addSampleData() {
        mViewModel.addSampleData();
    }
}