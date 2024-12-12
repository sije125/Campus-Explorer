package com.ibrahim.campusexplorer.fragments;


import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import com.ibrahim.campusexplorer.Buildings;
import com.ibrahim.campusexplorer.DatabaseAccess;
import com.ibrahim.campusexplorer.MapActivity;
import com.ibrahim.campusexplorer.R;

import com.ibrahim.campusexplorer.RecyclerViewAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class BuildingsListFragment extends Fragment
{
    private static List<Buildings> buildings = new ArrayList<>();
    private RecyclerViewAdapter adapter;
    private static RecyclerView rvBuildings;
    private TextView loading;
    private LottieAnimationView lottieAnimationView;
    private SwipeRefreshLayout swipeContainer;
    static private boolean check = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.buildings_list_fragment, container, false);
        //To show the search icon in the toolbar
        setHasOptionsMenu(true);

        //Link the views to the layout id
        loading = view.findViewById(R.id.loading);
        lottieAnimationView = view.findViewById(R.id.loading_anim);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        rvBuildings = view.findViewById(R.id.rvBuildings);


        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                buildings.clear();
                GetAllItemsAsyncTask getAllItemsAsyncTask = new GetAllItemsAsyncTask();
                getAllItemsAsyncTask.execute();
            }
        });


        //check to load the data form the database
        if(!check)
        {
            GetAllItemsAsyncTask getAllItemsAsyncTask = new GetAllItemsAsyncTask();
            getAllItemsAsyncTask.execute();
        }
        else
        {
            loading.setVisibility(View.INVISIBLE);
            lottieAnimationView.setVisibility(View.INVISIBLE);
            rvBuildings.setVisibility(View.VISIBLE);

        }

        // Inflate the layout for this fragment
        //Recycler View
        adapter = new RecyclerViewAdapter(getContext(), buildings);
        rvBuildings.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvBuildings.setAdapter(adapter);

        //Run shared activities animation
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        rvBuildings.setLayoutAnimation(animation);


        return view;
    }

    public void sortAlphabetically()
    {
        buildings.sort(new Comparator<Buildings>()
        {
            @Override
            public int compare(Buildings o1, Buildings o2)
            {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
    private class GetAllItemsAsyncTask extends AsyncTask<Void, Void, List<Document>>
    {
        @Override
        protected List<Document> doInBackground(Void... params) {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getContext());
            buildings.addAll(databaseAccess.getMostItems());
            sortAlphabetically();
            return databaseAccess.getAllItems();
        }

        @Override
        protected void onPostExecute(List<Document> documents)
        {
            adapter = new RecyclerViewAdapter(getContext(), buildings);
            rvBuildings.setAdapter(adapter);
            //Set the loading views to invisible
            loading.setVisibility(View.INVISIBLE);
            lottieAnimationView.setVisibility(View.INVISIBLE);
            //Set the recycle view to visible
            rvBuildings.setVisibility(View.VISIBLE);
            //adapter = new RecyclerViewAdapter(getContext(), buildings);
            //Notify that data was added to the list
            adapter.notifyDataSetChanged();
            //turn check to true for the app not to download the data again
            check = true;
            swipeContainer.setRefreshing(false);
            super.onPostExecute(documents);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        menu.clear();
        inflater.inflate(R.menu.building_menu,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        MenuItem map = menu.findItem(R.id.action_map);
        map.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                    Intent intent = new Intent(getActivity(), MapActivity.class);
                    intent.putExtra("buildings", Parcels.wrap(buildings));
                    startActivity(intent);
                    return false;
            }
        });

    }


}



