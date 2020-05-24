package com.example.exploresagradafamilia.RecyclerView;

import android.app.Activity;
import android.media.Image;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exploresagradafamilia.R;
import com.example.exploresagradafamilia.Sightplace;
import com.example.exploresagradafamilia.Utility;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.internal.Util;

public class SightplaceAdapter extends RecyclerView.Adapter<SightplaceAdapter.SigthplaceViewHolder> {

    private static final String LOG = "Sightplace Adapter";
    private List<Sightplace> sightplaceList = new ArrayList<>();
    private Activity activity;

    public SightplaceAdapter(Activity activity) {
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
        holder.setSightplaceData(sightplaceList.get(position));
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

        SigthplaceViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            textDescription = itemView.findViewById(R.id.description);
            textTitle = itemView.findViewById(R.id.title);
            textStarRating = itemView.findViewById(R.id.textStarRating);
            textLocation = itemView.findViewById(R.id.textLocation);
            disableCardView = itemView.findViewById(R.id.viewDisableLayout);
            lock = itemView.findViewById(R.id.imageLock);
        }

        void setSightplaceData(Sightplace sightplace) {
            //to add url
            //Picasso.get().load("https://www.publicdomainpictures.net/pictures/300000/velka/empty-white-room.jpg").into(image);

            image.setImageBitmap(Utility.getImageBitmap(sightplace.getImage()));
            textTitle.setText(sightplace.getTitle());
            textLocation.setText(sightplace.getLocation());
            textStarRating.setText(String.valueOf(sightplace.getRating()));
            textDescription.setText(sightplace.getDescription());
            textDescription.setMovementMethod(new ScrollingMovementMethod());

            if (!sightplace.getArchived()) {
                disableCardView.setVisibility(View.VISIBLE);
                lock.setVisibility(View.VISIBLE);
            } else {
                disableCardView.setVisibility(View.GONE);
                lock.setVisibility(View.GONE);
            }
        }
    }
}
