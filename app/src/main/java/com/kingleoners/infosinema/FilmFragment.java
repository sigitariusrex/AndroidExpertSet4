package com.kingleoners.infosinema;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FilmFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    View view;

    private RecyclerView recyclerView;
    private FilmAdapter adapter;
    private List<Film> filmList;
    ProgressDialog pd;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String LOG_TAG = FilmAdapter.class.getName();

    private DatabaseHelper databaseHelper;

    public FilmFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_film, container, false);

        pd = new ProgressDialog(getContext());
        pd.setMessage("Mohon tunggu...");
        pd.setCancelable(false);
        pd.show();

        recyclerView = (RecyclerView)view.findViewById(R.id.rv_films);

        filmList = new ArrayList<>();
        adapter = new FilmAdapter(getContext(), filmList);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        databaseHelper = new DatabaseHelper(getActivity());

        checkSortOrder();

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.main_content);
        swipeRefreshLayout.setRefreshing(true);
        /* swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark); */
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*pd = new ProgressDialog(getContext());
                pd.setMessage("Mohon tunggu...");
                pd.setCancelable(false);
                pd.show();*/

                recyclerView = (RecyclerView)view.findViewById(R.id.rv_films);

                filmList = new ArrayList<>();
                adapter = new FilmAdapter(getContext(), filmList);

                if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
                }

                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Content Refreshed", Toast.LENGTH_SHORT).show();

                checkSortOrder();
            }
        });

        return view;
    }

    private void initViews2(){

        pd.dismiss();

        recyclerView = (RecyclerView)view.findViewById(R.id.rv_films);

        filmList = new ArrayList<>();
        adapter = new FilmAdapter(getContext(), filmList);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        databaseHelper = new DatabaseHelper(getActivity());

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.main_content);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                recyclerView = (RecyclerView)view.findViewById(R.id.rv_films);

                filmList = new ArrayList<>();
                adapter = new FilmAdapter(getContext(), filmList);

                if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
                }

                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Content Refreshed", Toast.LENGTH_SHORT).show();

                checkSortOrder();

            }
        });

        getAllFavorite();
    }

    private void LoadJSON() {

        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getContext(), "Mohon peroleh API Key dari themovieddb.org", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                return;
            }

            Client client = new Client();
            Service service = Client.getRetrofit().create(Service.class);
            Call<FilmResponse> call = service.getPopularFilm(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<FilmResponse>() {
                @Override
                public void onResponse(Call<FilmResponse> call, Response<FilmResponse> response) {

                    List<Film> films = response.body().getResults();
                    recyclerView.setAdapter(new FilmAdapter(getContext(), films));
                    recyclerView.smoothScrollToPosition(0);

                    if (swipeRefreshLayout.isRefreshing()){
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    pd.dismiss();
                }

                @Override
                public void onFailure(Call<FilmResponse> call, Throwable t) {

                    Log.d("Error", t.getMessage());
                    Toast.makeText(getContext(), "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void LoadJSON1() {

        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getContext(), "Mohon peroleh API Key dari themovieddb.org", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                return;
            }

            Client client = new Client();
            Service service = Client.getRetrofit().create(Service.class);
            Call<FilmResponse> call = service.getTopRateFilm(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<FilmResponse>() {
                @Override
                public void onResponse(Call<FilmResponse> call, Response<FilmResponse> response) {

                    List<Film> films = response.body().getResults();
                    recyclerView.setAdapter(new FilmAdapter(getContext(), films));
                    recyclerView.smoothScrollToPosition(0);

                    if (swipeRefreshLayout.isRefreshing()){
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    pd.dismiss();
                }

                @Override
                public void onFailure(Call<FilmResponse> call, Throwable t) {

                    Log.d("Error", t.getMessage());
                    Toast.makeText(getContext(), "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Log.d(LOG_TAG,"Preferences updated");
        checkSortOrder();
    }

    private void checkSortOrder() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortOrder = preferences.getString(this.getString(R.string.sort_order_key),
        this.getString(R.string.most_popular));

        if (sortOrder.equals(this.getString(R.string.most_popular))){
            Log.d(LOG_TAG,"Urutkan paling populer");
            LoadJSON();
        } else if (sortOrder.equals(this.getString(R.string.favorite))){
            Log.d(LOG_TAG, "Urutkan untuk favorite");
            initViews2();
        } else {
            Log.d(LOG_TAG,"Urutkan nilai terbaik");
            LoadJSON1();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (filmList.isEmpty()){
            checkSortOrder();
        } else {
            checkSortOrder();
        }
    }

    private void getAllFavorite() {

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params){

                filmList.clear();
                filmList.addAll(databaseHelper.getAllFavorite());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid){
                super.onPostExecute(aVoid);

                adapter.notifyDataSetChanged();
            }
        } .execute();

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

    }

}
