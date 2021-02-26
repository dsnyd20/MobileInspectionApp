package com.example.mobilefieldinspector;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.mobilefieldinspector.database.PhotoEntity;
import com.example.mobilefieldinspector.viewmodel.PhotoDetailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.example.mobilefieldinspector.utilities.Constants.EDITING_KEY;
import static com.example.mobilefieldinspector.utilities.Constants.IMAGE_PATH;
import static com.example.mobilefieldinspector.utilities.Constants.INSPECTION_ID_KEY;
import static com.example.mobilefieldinspector.utilities.Constants.PHOTO_ID_KEY;

public class PhotoDetailActivity extends AppCompatActivity {
    private PhotoDetailViewModel mViewModel;
    private ImageView profileImageView;
    private TextView mNote;
    private String currentPhotoPath = "";
    private boolean mNewPhoto, mEditing;
    private int currentInspectionId;

    public static final int SELECT_PHOTO = 1;
    public static final int CAPTURE_PHOTO = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        profileImageView = findViewById(R.id.img_photo);
        mNote = findViewById(R.id.fieldNoteText);

        if(savedInstanceState != null) {
            mEditing = savedInstanceState.getBoolean(EDITING_KEY);
            currentPhotoPath = savedInstanceState.getString(IMAGE_PATH);
        }
        profileImageView.setImageURI(Uri.parse(currentPhotoPath));

        FloatingActionButton fabSavePhoto = findViewById(R.id.fab_save_photo);
        fabSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndReturn();
            }
        });

        FloatingActionButton fabCapPhoto = findViewById(R.id.btn_pick_image);
        fabCapPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(PhotoDetailActivity.this)
                        .title("Capture or Select Photo")
                        .items(R.array.uploadImages)
                        .itemsIds(R.array.itemIds)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                switch (position){
                                    case 0:
                                        dispatchTakePictureIntent();
                                        break;
                                    case 1:
                                        Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                        photoPickerIntent.setType("image/*");
                                        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                                        break;
                                }
                            }
                        }).show();
            }
        });

        //Make sure we have permission to take photos and save
        if(ContextCompat.checkSelfPermission(PhotoDetailActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            profileImageView.setEnabled(false);
            ActivityCompat.requestPermissions(PhotoDetailActivity.this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            profileImageView.setEnabled(true);
        }

        initViewModel();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EDITING_KEY, true);
        outState.putString(IMAGE_PATH, currentPhotoPath);
        super.onSaveInstanceState(outState);
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this)
                .get(PhotoDetailViewModel.class);

        mViewModel.mLivePhoto.observe(this, new Observer<PhotoEntity>() {
            @Override
            public void onChanged(PhotoEntity photoEntity) {
                profileImageView.setImageURI(Uri.parse(currentPhotoPath));
                if (photoEntity != null && !mEditing) {
                    mNote.setText(photoEntity.getPhotoNote());
                    currentPhotoPath = photoEntity.getImageUri();
                    profileImageView.setImageURI(Uri.parse(currentPhotoPath));
                    currentInspectionId = photoEntity.getInspectionId();
                }
            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras.getInt(PHOTO_ID_KEY) == 0) {
            setTitle("New Photo");
            mNewPhoto = true;
            currentInspectionId = extras.getInt(INSPECTION_ID_KEY);

        } else {
            setTitle("Edit Photo");
            int photoId = extras.getInt(PHOTO_ID_KEY);
            mViewModel.loadData(photoId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PHOTO) {
            if(resultCode == RESULT_OK) {
                profileImageView.setImageURI(data.getData());
                currentPhotoPath = data.getData().toString();
            }
        } else if(requestCode == CAPTURE_PHOTO){
           if(resultCode == RESULT_OK){
               Log.d("TAG", currentPhotoPath);
               profileImageView.setImageURI(Uri.parse(currentPhotoPath));
           }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        Log.d("TAG", "createImageFile: This sets the current image path");
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    takePictureIntent.setClipData(ClipData.newRawUri("", photoURI));
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                startActivityForResult(takePictureIntent, CAPTURE_PHOTO);
                Log.d("TAG", "dispatchTakePictureIntent: " + photoURI.toString());
                currentPhotoPath = photoURI.toString();
            }
        }
    }

    private void saveAndReturn() {
        int success = mViewModel.savePhoto(currentPhotoPath, currentInspectionId, mNote.getText().toString());
        if (success == 1) {
            Toast toast = Toast.makeText(getApplicationContext(), "Save Successful!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Save Unsuccessful! Please do not leave the photo or note blank.", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if (!mNewPhoto) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.delete_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.action_delete) {
            mViewModel.deletePhoto();
            Toast toast = Toast.makeText(getApplicationContext(), "Delete Successful!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
        return true;
    }
}