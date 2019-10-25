
package com.kingleoners.infosinema;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFilmActivity extends AppCompatActivity {

    TextView tvName, tvOverview, tvVoteAverage, tvFirstAirDate;
    TextView langName, langOverview, langVoteAverage, langFirstAirDate;
    ImageView imageView;

    private RecyclerView recyclerView;
    private TrailerAdapter adapter;
    private List<Trailer> trailerList;

    private DatabaseHelper databaseHelper;
    private Film film;
    private final AppCompatActivity activity = DetailFilmActivity.this;

    private SQLiteDatabase database;
    String thumbnail, filmName, overview, date;
    Double vote;
    int film_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_film);

        initCollapsingToolbar();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        imageView = (ImageView)findViewById(R.id.thumbnail);
        tvName = (TextView)findViewById(R.id.name);
        tvOverview = (TextView)findViewById(R.id.overview);
        tvVoteAverage = (TextView)findViewById(R.id.vote_average);
        tvFirstAirDate = (TextView)findViewById(R.id.first_air_date);

        Intent intent = getIntent();
        if (intent.hasExtra("films")){

            film = getIntent().getParcelableExtra("films");

            thumbnail = film.getPosterPath();
            filmName = film.getOriginalName();
            overview = film.getOverview();
            vote = film.getVoteAverage();
            date = film.getFirstAirDate();
            film_id = film.getId();

            String poster = "https://image.tmdb.org/t/p/w500" + thumbnail;

            /*String thumbnail = "https://image.tmdb.org/t/p/w500" + getIntent().getExtras().getString("poster_path");
            String name = getIntent().getExtras().getString("original_name");
            String overview = getIntent().getExtras().getString("overview");
            String vote = getIntent().getExtras().getString("vote_average");
            String date = getIntent().getExtras().getString("first_air_date");*/

            Glide.with(this)
                    .load(poster)
                    .placeholder(R.drawable.loading)
                    .into(imageView);

            tvName.setText(filmName);
            tvOverview.setText(overview);
            tvFirstAirDate.setText(date);

            ((CollapsingToolbarLayout)findViewById(R.id.collapsingtoolbar)).setTitle(filmName);

        } else {
            Toast.makeText(this, "No API Data", Toast.LENGTH_SHORT).show();
        }

        MaterialFavoriteButton materialFavoriteButton = (MaterialFavoriteButton)findViewById(R.id.favorite_button);
        /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        materialFavoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {

                        if (favorite){
                            SharedPreferences.Editor editor = getSharedPreferences("com.kingleoners.infosinema.DetailFilmActivity", MODE_PRIVATE).edit();
                            editor.putBoolean("Favorite ditambahkan", true);
                            editor.commit();
                            saveFavorite();
                            Snackbar.make(buttonView, "Ditambahkan ke Favorite ",
                                    Snackbar.LENGTH_SHORT).show();
                        } else {

                            int film_id = getIntent().getExtras().getInt("id");
                            databaseHelper = new DatabaseHelper(DetailFilmActivity.this);
                            databaseHelper.deleteFavorite(film_id);

                            SharedPreferences.Editor editor = getSharedPreferences("com.kingleoners.infosinema.DetailFilmActivity", MODE_PRIVATE).edit();
                            editor.putBoolean("Favorite dihapus", true);
                            Snackbar.make(buttonView,"Dihapus dari Favorite ",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
        );*/

        if (Exists(filmName)){
            materialFavoriteButton.setFavorite(true);
            materialFavoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {

                    if (favorite == true){
                        saveFavorite();
                        Snackbar.make(buttonView, "Ditambahkan ke Favorite", Snackbar.LENGTH_SHORT).show();
                    } else {
                        databaseHelper = new DatabaseHelper(DetailFilmActivity.this);
                        databaseHelper.deleteFavorite(film_id);
                        Snackbar.make(buttonView, "Dihapus dari Favorite" , Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            materialFavoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {

                    if (favorite == true){
                        saveFavorite();
                        Snackbar.make(buttonView, "Ditambahkan ke Favorite", Snackbar.LENGTH_SHORT).show();
                    } else {
                        int film_id = getIntent().getExtras().getInt("id");
                        databaseHelper = new DatabaseHelper(DetailFilmActivity.this);
                        databaseHelper.deleteFavorite(film_id);
                        Snackbar.make(buttonView, "Dihapus dari Favorite", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }

        initViews();

        langName = (TextView)findViewById(R.id.title);
        langOverview = (TextView)findViewById(R.id.overview_synopsis);
        langVoteAverage = (TextView)findViewById(R.id.vote);
        langFirstAirDate = (TextView)findViewById(R.id.air_date);

        updateView((String) Paper.book().read("language"));
    }

    private void updateView(String language) {

        Context context = LocaleHelper.setLocale(this, language);
        Resources resources = context.getResources();

        langName.setText(resources.getString(R.string.name));
        langOverview.setText(resources.getString(R.string.overview_synopsis));
        langVoteAverage.setText(resources.getString(R.string.vote_average));
        langFirstAirDate.setText(resources.getString(R.string.first_air_date));
    }

    public boolean Exists(String searchItem){

        String[] projection = {
                FavoriteContract.FavoriteEntry._ID,
                FavoriteContract.FavoriteEntry.COLUMN_FILM,
                FavoriteContract.FavoriteEntry.COLUMN_NAME,
                FavoriteContract.FavoriteEntry.COLUMN_VOTEAVERAGE,
                FavoriteContract.FavoriteEntry.COLUMN_POSTERPATH,
                FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW
        };

        String selection = FavoriteContract.FavoriteEntry.COLUMN_NAME + " =?";
        String[] selectionArgs = { searchItem };
        String limit = "1";

        Cursor cursor = database.query(FavoriteContract.FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    private void initCollapsingToolbar() {

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsingtoolbar);
        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

                if (scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + i == 0){
                    collapsingToolbarLayout.setTitle(getString(R.string.details));
                    isShow = true;
                } else if (isShow){
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void initViews(){

        trailerList = new ArrayList<>();
        adapter = new TrailerAdapter(this, trailerList);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewTrailer);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        loadJSON();
    }

    private void loadJSON() {

        try {

            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getApplicationContext(), "Mohon peroleh API Key dari themoviedb.org", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service service = Client.getRetrofit().create(Service.class);
            Call<TrailerResponse> call = service.getFilmTrailer(film_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {

                    List<Trailer> trailers = response.body().getResults();
                    recyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), trailers));
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {

                    Log.d("Error ", t.getMessage());
                    Toast.makeText(DetailFilmActivity.this, "Error fetching trailer data", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            Log.d("Error ", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void saveFavorite(){

        databaseHelper = new DatabaseHelper(activity);
        film = new Film();

        /*int film_id = getIntent().getExtras().getInt("id");
        String vote = getIntent().getExtras().getString("vote_average");
        String poster = getIntent().getExtras().getString("poster_path");

        Glide.with(this)
                .load(poster)
                .placeholder(R.drawable.loading)
                .into(imageView);*/

        Double rate = film.getVoteAverage();

        film.setId(film_id);
        film.setOriginalName(filmName);
        film.setPosterPath(thumbnail);
        film.setVoteAverage(vote);
        film.setOverview(overview);
        film.setFirstAirDate(date);

        databaseHelper.addFavorite(film);
    }
}
