package com.example.mobilefieldinspector;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilefieldinspector.database.InspectionEntity;
import com.example.mobilefieldinspector.ui.InspectionsAdapter;
import com.example.mobilefieldinspector.viewmodel.InspectionListViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InspectionListActivity extends AppCompatActivity {
    private List<InspectionEntity> inspectionsData = new ArrayList<>();
    private InspectionsAdapter mAdapter;
    private InspectionListViewModel mViewModel;

    @BindView(R.id.recycler_view_inpsections)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Inspections");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        initRecyclerView();
        initViewModel();

    }

    private void initViewModel() {
        //Create an observer
        final Observer<List<InspectionEntity>> inspectionObserver = new Observer<List<InspectionEntity>>() {
            @Override
            public void onChanged(List<InspectionEntity> inspectionEntities) {
                inspectionsData.clear();
                inspectionsData.addAll(inspectionEntities);
                if (mAdapter == null) {
                    mAdapter = new InspectionsAdapter(inspectionEntities, InspectionListActivity.this);
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
                }

            }
        };

        mViewModel = ViewModelProviders.of(this)
                .get(InspectionListViewModel.class);

        //Subscribe to the observer
        mViewModel.mInspections.observe(this, inspectionObserver);

    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true); //each item will appear as the same size in the list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new InspectionsAdapter(inspectionsData, this);
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