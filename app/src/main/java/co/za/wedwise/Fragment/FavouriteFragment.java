package co.za.wedwise.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import co.za.wedwise.Item.GridItem;
import co.za.wedwise.Models.PropertyModels;
import co.za.wedwise.R;
import co.za.wedwise.Utils.DatabaseHelper;


public class FavouriteFragment extends Fragment {

    View getView;
    Context context;
    ArrayList<PropertyModels> listItem;
    public RecyclerView recyclerView;
    GridItem adapter;
    DatabaseHelper databaseHelper;
    RelativeLayout notFound, progress;
    CardView filterandsort;
    Toolbar toolbar;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getView = inflater.inflate(R.layout.fragment_recycle, container, false);
        context = getContext();
        toolbar = getView.findViewById(R.id.toolbar);
        listItem = new ArrayList<>();
        databaseHelper = new DatabaseHelper(getActivity());
        recyclerView = getView.findViewById(R.id.recycle);
        filterandsort = getView.findViewById(R.id.rlfilter);
        notFound = getView.findViewById(R.id.notfound);
        progress = getView.findViewById(R.id.progress);
        toolbar.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        filterandsort.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));


        return getView;
    }

    @Override
    public void onResume() {
        super.onResume();
        listItem = databaseHelper.getFavourite();
        displayData();
    }

    private void displayData() {
        adapter = new GridItem(getActivity(), listItem, this, false);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            notFound.setVisibility(View.VISIBLE);
        } else {
            notFound.setVisibility(View.GONE);
        }

    }


}
