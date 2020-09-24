package com.example.exploresagradafamilia.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exploresagradafamilia.R;
import com.example.exploresagradafamilia.Sightplace;
import com.example.exploresagradafamilia.Utility;
import com.flaviofaria.kenburnsview.KenBurnsView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SightplaceAdapter extends RecyclerView.Adapter<SightplaceAdapter.SigthplaceViewHolder> {

    private static final String LOG = "Sightplace Adapter";
    private List<Sightplace> sightplaceList = new ArrayList<>();
    private AppCompatActivity activity;

    public SightplaceAdapter(AppCompatActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public SigthplaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SigthplaceViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.fragment_sightlist_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SigthplaceViewHolder holder, int position) {
        holder.setSightplaceData(activity, sightplaceList.get(position));
    }

    @Override
    public int getItemCount() {
        return sightplaceList.size();
    }

    /**
     * Method called when a new item is added
     *
     * @param newData the new list of items
     */
    public void setData(List<Sightplace> newData) {
        this.sightplaceList.clear();
        this.sightplaceList.addAll(newData);
        //order of ids
        this.sightplaceList.sort((i, i2) -> i.getId() - i2.getId());
        super.notifyDataSetChanged();
    }

    public void updateList() {
        Log.d(LOG, "updateList()");
        notifyDataSetChanged();
    }

    public int getIndexOfMinorID(int minor_id) {
        List<Sightplace> check = sightplaceList.stream().filter(item -> item.getMinor() == minor_id).collect(Collectors.toList());
        if (check.isEmpty()) {
            Log.d("Adapter", "ID Non presente in lista");
            return -1;
        } else {
            return sightplaceList.indexOf(check.get(0));
        }
    }

    static class SigthplaceViewHolder extends RecyclerView.ViewHolder {

        private KenBurnsView image;
        private TextView textLocation, textStarRating, textTitle, textDescription;
        private View disableCardView;
        private ImageView lock;
        private ImageView share;

        SigthplaceViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            textDescription = itemView.findViewById(R.id.description);
            textTitle = itemView.findViewById(R.id.title);
            textStarRating = itemView.findViewById(R.id.textStarRating);
            textLocation = itemView.findViewById(R.id.textLocation);
            disableCardView = itemView.findViewById(R.id.viewDisableLayout);
            lock = itemView.findViewById(R.id.imageLock);
            share = itemView.findViewById(R.id.imageShare);
        }

        void setSightplaceData(AppCompatActivity activity, Sightplace sightplace) {
            image.setImageBitmap(Utility.getImageFromUri(activity, sightplace.getImageUrl()));
            textTitle.setText(sightplace.getTitle());
            textLocation.setText(sightplace.getLocation());
            textStarRating.setText(String.valueOf(sightplace.getRating()));
            textDescription.setText(sightplace.getDescription());
            textDescription.setMovementMethod(new ScrollingMovementMethod());

            //Manage the Blocked - Unblocked Sightplace
            if (!sightplace.getArchived()) {
                disableCardView.setVisibility(View.VISIBLE);
                lock.setVisibility(View.VISIBLE);
            } else {
                disableCardView.setVisibility(View.GONE);
                lock.setVisibility(View.GONE);
            }

            //Manage the sharebutton


            share.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            v.getContext().getString(R.string.location) + sightplace.getLocation() + "\n" +
                                    v.getContext().getString(R.string.title) + sightplace.getTitle() + "\n" +
                                    v.getContext().getString(R.string.description) + sightplace.getDescription()
                    );
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + sightplace.getImageUrl()));
                    sendIntent.setType("image/png");

                    sendIntent.setType("text/plain");
                    if (v.getContext() != null &&
                            sendIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                        v.getContext().startActivity(Intent.createChooser(sendIntent, "Share with"));
                    }
                }
            });
        }

        private Uri pathToShare(Bitmap bitmapImage) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + "Share.png";
            OutputStream out = null;
            File file = new File(path);
            try {
                out = new FileOutputStream(file);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            path = file.getPath();
            Uri bmpUri = Uri.parse("file://" + path);
            return bmpUri;
        }
    }
}
