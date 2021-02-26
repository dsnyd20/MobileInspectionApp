package com.example.mobilefieldinspector.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilefieldinspector.PhotoDetailActivity;
import com.example.mobilefieldinspector.R;
import com.example.mobilefieldinspector.database.PhotoEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mobilefieldinspector.utilities.Constants.FALSE;
import static com.example.mobilefieldinspector.utilities.Constants.NEW_NOTE_FLAG;
import static com.example.mobilefieldinspector.utilities.Constants.PHOTO_ID_KEY;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private final List<PhotoEntity> mPhotos;
    private final Context mContext;

    public PhotoAdapter(List<PhotoEntity> mPhotos, Context mContext) {
        this.mPhotos = mPhotos;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.photo_list_item, parent, false);
        return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.ViewHolder holder, int position) {
        final PhotoEntity photo = mPhotos.get(position);
        holder.mTextView.setText(photo.getPhotoNote());
        holder.mImageView.setImageURI(Uri.parse(photo.getImageUri()));
        holder.mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhotoDetailActivity.class);
                intent.putExtra(NEW_NOTE_FLAG, FALSE);
                intent.putExtra(PHOTO_ID_KEY, photo.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rv_note)
        TextView mTextView;
        @BindView(R.id.fab_edit_note)
        FloatingActionButton mFab;
        @BindView(R.id.rv_photo)
        ImageView mImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}