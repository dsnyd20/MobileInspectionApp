package com.example.mobilefieldinspector;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilefieldinspector.database.InspectionEntity;
import com.example.mobilefieldinspector.database.PhotoEntity;
import com.example.mobilefieldinspector.database.ShopEntity;
import com.example.mobilefieldinspector.ui.PhotoAdapter;
import com.example.mobilefieldinspector.utilities.NotificationReceiver;
import com.example.mobilefieldinspector.viewmodel.InspectionDetailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.mobilefieldinspector.utilities.Constants.EDITING_KEY;
import static com.example.mobilefieldinspector.utilities.Constants.INSPECTED_RAILCAR_ID_KEY;
import static com.example.mobilefieldinspector.utilities.Constants.INSPECTED_RAILCAR_RUNNING_NUMBER_KEY;
import static com.example.mobilefieldinspector.utilities.Constants.INSPECTION_ID_KEY;

public class InspectionDetailActivity extends AppCompatActivity {
    private InspectionDetailViewModel mViewModel;
    private TextView mInspectionName;
    private TextView mRunningNumber;
    private Spinner mShopName;
    private TextView mInspectionDate;
    private TextView mNote;
    private RecyclerView mRecyclerView;
    private int railcarId;
    private int mInspectionId;
    private boolean mNewInspection, mEditing;
    DatePickerDialog picker;
    private ArrayAdapter<ShopEntity> adapter;
    private List<ShopEntity> shopsData = new ArrayList<>();
    private List<PhotoEntity> photosData = new ArrayList<>();
    private PhotoAdapter mPhotosAdapter;
    private int spinnerPosition;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_detail);

        mRecyclerView = findViewById(R.id.recycler_view_photos);
        mInspectionName = findViewById(R.id.fieldInspectionName);
        mRunningNumber = findViewById(R.id.fieldInspectedRailcarRunningNumber);
        mInspectionDate = findViewById(R.id.fieldInspectionDate);
        mNote = findViewById(R.id.fieldNoteText);

        FloatingActionButton fabSaveInspection = (FloatingActionButton) findViewById(R.id.fab_save_inspection);
        fabSaveInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndReturn();
            }
        });

        FloatingActionButton fabAddPhoto = findViewById(R.id.fab_add_photo);
        fabAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PhotoDetailActivity.class);
                intent.putExtra(INSPECTION_ID_KEY, mInspectionId);
                startActivity(intent);
            }
        });

        if(savedInstanceState != null) {
            mEditing = savedInstanceState.getBoolean(EDITING_KEY);
        }

        mInspectionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(InspectionDetailActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mInspectionDate.setText(String.format("%02d",year) + "/" + String.format("%02d",monthOfYear+1) + "/" + String.format("%02d",dayOfMonth));
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        initViewModel();
        initRecyclerView();

        if (mNewInspection) {
            fabAddPhoto.setVisibility(View.GONE);
            fabAddPhoto.setEnabled(!mNewInspection);
        }
    }

    private void saveAndReturn() {
        ShopEntity shopselected = (ShopEntity) mShopName.getSelectedItem();
        int success = 0;
        success = mViewModel.saveInspection(railcarId, shopselected.getId(), mInspectionDate.getText().toString(), mNote.getText().toString(), mInspectionName.getText().toString());
        if (success == 1) {
            Toast toast = Toast.makeText(getApplicationContext(), "Save Successful!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        } else if (success == 2) {
            Toast toast = Toast.makeText(getApplicationContext(), "Save Unsuccessful! There is already an inspection for this car on this date at another location.", Toast.LENGTH_LONG);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Save Unsuccessful! Please do not leave any fields blank.", Toast.LENGTH_LONG);
            toast.show();
        }

    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this)
                .get(InspectionDetailViewModel.class);

        mViewModel.mLiveInspection.observe(this, new Observer<InspectionEntity>() {
            @Override
            public void onChanged(InspectionEntity inspectionEntity) {
                if(inspectionEntity != null && !mEditing) {
                    mInspectionName.setText(inspectionEntity.getInspectionName());
                    mRunningNumber.setText(mViewModel.mRailcar.getRunningNumber());
                    mNote.setText(inspectionEntity.getNote());
                    mInspectionDate.setText(inspectionEntity.getInspectionDate());
                    railcarId = inspectionEntity.getRailcarId();

                    if (mViewModel.mShop != null) {
                        spinnerPosition = mViewModel.getPosition(mViewModel.mShop);
                        mShopName.setSelection(spinnerPosition);
                    }
                }
            }
        });

        mViewModel.getAllShops().observe(this, new Observer<List<ShopEntity>>() {
            @Override
            public void onChanged(List<ShopEntity> shopEntities) {
                adapter.addAll(shopEntities);
            }
        });

        //Setup shop spinner
        mShopName = findViewById(R.id.spinnerShopName);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mShopName.setAdapter(adapter);




        Bundle extras = getIntent().getExtras();
        if (extras.getInt(INSPECTION_ID_KEY) == 0) {
            setTitle("New Inspection");
            mNewInspection = true;
            mRunningNumber.setText(extras.getString(INSPECTED_RAILCAR_RUNNING_NUMBER_KEY));
            railcarId = extras.getInt(INSPECTED_RAILCAR_ID_KEY);
        } else {
            setTitle("Edit Inspection");
            int inspectionId = extras.getInt(INSPECTION_ID_KEY);
            mViewModel.loadData(inspectionId);
            mInspectionId = inspectionId;
        }

        //Create an observer
        final Observer<List<PhotoEntity>> photoObserver = new Observer<List<PhotoEntity>>() {
            @Override
            public void onChanged(List<PhotoEntity> photoEntities) {
                photosData.clear();
                photosData.addAll(photoEntities);
                if (mPhotosAdapter == null) {
                    mPhotosAdapter = new PhotoAdapter(photosData, InspectionDetailActivity.this);
                    mRecyclerView.setAdapter(mPhotosAdapter);
                } else {
                    mPhotosAdapter.notifyDataSetChanged();
                }

            }
        };

        //Subscribe to the observer
        mViewModel.mPhotos.observe(this, photoObserver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EDITING_KEY, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if (!mNewInspection) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.inspection_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
         if (item.getItemId() == R.id.action_alert) {
             NotificationReceiver.scheduleAlert(this, mInspectionDate.getText().toString(), mInspectionName.getText().toString(), mInspectionId, "is", "inspection");
         } else if (item.getItemId() == R.id.action_share){

             String text = mViewModel.getInspectionShare();
             Intent sendIntent = new Intent();
             sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
             sendIntent.putExtra(Intent.EXTRA_TEXT, text);
             sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Inspection Report for " + mViewModel.mRailcar.getRunningNumber());
             int photoCount = mPhotosAdapter.getItemCount();
             ArrayList<Uri> imageUris = new ArrayList<Uri>();
             for (int i = 0; i < photoCount; i++) {
                 PhotoEntity currentPhoto =  mViewModel.mPhotos.getValue().get(i);
                 imageUris.add(Uri.parse(currentPhoto.getImageUri()));
                 //Uri fileUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", new File(currentPhoto.getImageUri()));
                 //sendIntent.putExtra(Intent.EXTRA_STREAM,fileUri);
                 text = text + "\n\nPhoto " + Integer.toString(i + 1) + ": " + currentPhoto.getPhotoNote();
             }

             sendIntent.putExtra(Intent.EXTRA_TEXT, text);
             sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,imageUris);
             sendIntent.setType("image/*");
             startActivity(Intent.createChooser(sendIntent, "Share inspection to.."));
        } else if (item.getItemId() == R.id.action_delete) {
             if (mPhotosAdapter.getItemCount() > 0){
                 Toast toast = Toast.makeText(getApplicationContext(), "Delete Unsuccessful! Please make sure there are no photos assigned to this inspection.", Toast.LENGTH_LONG);
                 toast.show();
                 return false;
             }
             mViewModel.deleteInspection();
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

        mPhotosAdapter = new PhotoAdapter(photosData, this);
        mRecyclerView.setAdapter(mPhotosAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }
}