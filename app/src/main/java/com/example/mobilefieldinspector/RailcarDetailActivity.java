package com.example.mobilefieldinspector;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilefieldinspector.database.InspectionEntity;
import com.example.mobilefieldinspector.database.RailcarEntity;
import com.example.mobilefieldinspector.ui.InspectionsAdapter;
import com.example.mobilefieldinspector.viewmodel.RailcarDetailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mobilefieldinspector.utilities.Constants.EDITING_KEY;
import static com.example.mobilefieldinspector.utilities.Constants.INSPECTED_RAILCAR_ID_KEY;
import static com.example.mobilefieldinspector.utilities.Constants.INSPECTED_RAILCAR_RUNNING_NUMBER_KEY;
import static com.example.mobilefieldinspector.utilities.Constants.INSPECTION_ID_KEY;
import static com.example.mobilefieldinspector.utilities.Constants.RAILCAR_ID_KEY;

public class RailcarDetailActivity extends AppCompatActivity {
    private RailcarDetailViewModel mViewModel;
    private TextView mRunningNumber;
    private Spinner mCarType;
    private TextView mBuiltDate;
    private boolean mNewRailcar, mEditing;
    private int railcarId;
    DatePickerDialog picker;
    @BindView(R.id.recycler_view_railcar_inspections)
    RecyclerView mRecyclerView;
    private InspectionsAdapter mAdapterInspections;
    private List<InspectionEntity> inspectionsData = new ArrayList<>();

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_railcar_detail);
        mRunningNumber = findViewById(R.id.fieldRailcarRunningNumber);
        mBuiltDate = findViewById(R.id.fieldRailcarBuiltDate);

        FloatingActionButton fabSaveRailcar = (FloatingActionButton) findViewById(R.id.fab_save_railcar);
        fabSaveRailcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndReturn();
            }
        });

        FloatingActionButton fabAddInspection = (FloatingActionButton) findViewById(R.id.fab_add_inspection);
        fabAddInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RailcarDetailActivity.this, InspectionDetailActivity.class);
                intent.putExtra(INSPECTED_RAILCAR_ID_KEY, railcarId);
                intent.putExtra(INSPECTED_RAILCAR_RUNNING_NUMBER_KEY, mRunningNumber.getText().toString());
                intent.putExtra(INSPECTION_ID_KEY, 0);

                startActivity(intent);
            }
        });

        if(savedInstanceState != null) {
            mEditing = savedInstanceState.getBoolean(EDITING_KEY);
        }

        mCarType = (Spinner) findViewById(R.id.spinnerRailcarCarType);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterCarTypeSpinner = ArrayAdapter.createFromResource(this,
                R.array.car_type_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterCarTypeSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mCarType.setAdapter(adapterCarTypeSpinner);

        mBuiltDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(RailcarDetailActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mBuiltDate.setText(String.format("%02d",year) + "/" + String.format("%02d",monthOfYear+1) + "/" + String.format("%02d",dayOfMonth));
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        ButterKnife.bind(this);
        initRecyclerView();

        initViewModel();

        if (mNewRailcar) {
            fabAddInspection.setVisibility(View.GONE);
            fabAddInspection.setEnabled(!mNewRailcar);
        }
    }

    private void saveAndReturn() {
        int success = mViewModel.saveRailcar(mRunningNumber.getText().toString(),mCarType.getSelectedItem().toString(),mBuiltDate.getText().toString());
        if (success == 1) {
            Toast toast = Toast.makeText(getApplicationContext(), "Save Successful!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Save Unsuccessful! Please do not leave the running number or built date blank and make sure the built date is not in the future.", Toast.LENGTH_LONG);
            toast.show();
        }

    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this)
                .get(RailcarDetailViewModel.class);

        mViewModel.mLiveRailcar.observe(this, new Observer<RailcarEntity>() {
            @Override
            public void onChanged(RailcarEntity railcarEntity) {
                if(railcarEntity != null && !mEditing) {
                    mRunningNumber.setText(railcarEntity.getRunningNumber());
                    final List<String> statusSpinnerOptions = new ArrayList<>(Arrays.asList(new String[]{"Tanker", "Freight"}));
                    int spinnerPosition = statusSpinnerOptions.indexOf(railcarEntity.getCarType());
                    mCarType.setSelection(spinnerPosition);
                    mBuiltDate.setText(railcarEntity.getBuiltDate());

                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setTitle("New Railcar");
            mNewRailcar = true;
        } else {
            setTitle("Edit Railcar");
            railcarId = extras.getInt(RAILCAR_ID_KEY);
            mViewModel.loadData(railcarId);
        }

        //Create an observer
        //Create an observer
        final Observer<List<InspectionEntity>> inspectionObserver = new Observer<List<InspectionEntity>>() {
            @Override
            public void onChanged(List<InspectionEntity> inspectionEntities) {
                inspectionsData.clear();
                inspectionsData.addAll(inspectionEntities);
                if (mAdapterInspections == null) {
                    mAdapterInspections = new InspectionsAdapter(inspectionsData, RailcarDetailActivity.this);
                    mRecyclerView.setAdapter(mAdapterInspections);
                } else {
                    mAdapterInspections.notifyDataSetChanged();
                }

            }
        };

        //Subscribe to the observer
        mViewModel.mInspections.observe(this, inspectionObserver);

    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true); //each item will appear as the same size in the list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapterInspections = new InspectionsAdapter(inspectionsData, this);
        mRecyclerView.setAdapter(mAdapterInspections);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EDITING_KEY, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if (!mNewRailcar) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.delete_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.action_delete) {
            if (mAdapterInspections.getItemCount() > 0) {
                Toast toast = Toast.makeText(getApplicationContext(), "Delete Unsuccessful! Please make sure there are no inspections assigned to this railcar.", Toast.LENGTH_LONG);
                toast.show();
                return false;
            }
            mViewModel.deleteRailcar();
            Toast toast = Toast.makeText(getApplicationContext(), "Delete Successful!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
        return true;
    }
}