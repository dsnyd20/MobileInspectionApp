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

import com.example.mobilefieldinspector.database.RailcarEntity;
import com.example.mobilefieldinspector.ui.RailcarsAdapter;
import com.example.mobilefieldinspector.viewmodel.RailcarListViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RailcarListActivity extends AppCompatActivity {
    private List<RailcarEntity> railcarsData = new ArrayList<>();
    private RailcarsAdapter mAdapter;
    private RailcarListViewModel mViewModel;

    @BindView(R.id.recycler_view_railcars)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Railcars");
        setContentView(R.layout.activity_railcar_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        initRecyclerView();
        initViewModel();


        FloatingActionButton fab = findViewById(R.id.fab_add_railcar);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(RailcarListActivity.this, RailcarDetailActivity.class));
                }
            });


    }

    private void initViewModel() {
        //Create an observer
        final Observer<List<RailcarEntity>> railcarObserver = new Observer<List<RailcarEntity>>() {
            @Override
            public void onChanged(List<RailcarEntity> railcarEntities) {
                railcarsData.clear();
                railcarsData.addAll(railcarEntities);
                if (mAdapter == null) {
                    mAdapter = new RailcarsAdapter(railcarsData, RailcarListActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
                }

            }
        };

        mViewModel = ViewModelProviders.of(this)
                .get(RailcarListViewModel.class);

        //Subscribe to the observer
        mViewModel.mRailcars.observe(this, railcarObserver);

    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true); //each item will appear as the same size in the list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new RailcarsAdapter(railcarsData, this);
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