package com.example.exploresagradafamilia;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.exploresagradafamilia.RecyclerView.SightplaceAdapter;
import com.example.exploresagradafamilia.ViewModel.ListSightplaceViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    public static final String FRAGMENT_LIST = "LIST_FRAGMENT_TAG";
    private static final String LOG = "List Slider";
    private MainActivity activity;
    private SightplaceAdapter adapter;
    ViewPager2 sightplaceViewPager;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sightplaceViewPager = activity.findViewById(R.id.placesViewPager);
        List<Sightplace> sightplaceList = new ArrayList<>();
        adapter = new SightplaceAdapter(activity);

        sightplaceViewPager.setAdapter(adapter);

        ListSightplaceViewModel model = new ViewModelProvider(activity).get(ListSightplaceViewModel.class);
        //when the list of the items changed, the adapter gets the new list.
        model.getItems().observe(activity, new Observer<List<Sightplace>>() {
            @Override
            public void onChanged(List<Sightplace> sightplaces) {
                Log.d("LIST", "ADAPTER UPDATED");
                adapter.setData(sightplaces);
            }
        });

        sightplaceViewPager.setClipToPadding(false);
        sightplaceViewPager.setClipChildren(false);
        sightplaceViewPager.setOffscreenPageLimit(3);
        sightplaceViewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(25));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.95f + r * 0.05f);
            }
        });

        sightplaceViewPager.setPageTransformer(compositePageTransformer);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sightlist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            selectElement(args.getInt("ID"));
        }
    }

    void updateList() {
        Log.d(LOG, "updateList()");
        adapter.updateList();
    }

    /**
     * @param id Minor id
     */
    void selectElement(int id) {
        sightplaceViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                sightplaceViewPager.setCurrentItem(adapter.getIndexOfMinorID(id));
            }
        }, 100);
    }
}
