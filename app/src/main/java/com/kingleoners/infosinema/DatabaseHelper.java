package com.kingleoners.infosinema;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favoriteFilm.db";

    private static final int DATABASE_VERSION = 1;

    public static final String LOG_TAG = "FAVORITE";

    SQLiteOpenHelper openHelper;
    SQLiteDatabase db;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open(){
        Log.i(LOG_TAG, "Database Terbuka");
        db = openHelper.getWritableDatabase();
    }

    public void close(){
        Log.i(LOG_TAG, "Database Tertutup");
        openHelper.close();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoriteContract.FavoriteEntry.TABLE_NAME + " (" +
                FavoriteContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteContract.FavoriteEntry.COLUMN_FILM + " INTEGER, " +
                FavoriteContract.FavoriteEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_VOTEAVERAGE + " REAL NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_POSTERPATH + " TEXT NOT NULL, " +
                FavoriteContract.FavoriteEntry.COLUMN_DATE + " TEXT NOT NULL " +
                "); ";
        database.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        database.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavoriteEntry.TABLE_NAME);
        onCreate(database);
    }

    public void addFavorite(Film film){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FavoriteContract.FavoriteEntry.COLUMN_FILM, film.getId());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_NAME, film.getOriginalName());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW, film.getOverview());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_VOTEAVERAGE, film.getVoteAverage());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_POSTERPATH, film.getPosterPath());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_DATE, film.getFirstAirDate());


        db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, values);
        db.close();
    }

    public void deleteFavorite(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FavoriteContract.FavoriteEntry.TABLE_NAME, FavoriteContract.FavoriteEntry.COLUMN_FILM + "=" + id, null);
    }

    public List<Film> getAllFavorite(){
        String[] columns = {
                FavoriteContract.FavoriteEntry._ID,
                FavoriteContract.FavoriteEntry.COLUMN_FILM,
                FavoriteContract.FavoriteEntry.COLUMN_NAME,
                FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW,
                FavoriteContract.FavoriteEntry.COLUMN_VOTEAVERAGE,
                FavoriteContract.FavoriteEntry.COLUMN_POSTERPATH,
                FavoriteContract.FavoriteEntry.COLUMN_DATE
        };

        String sortOrder = FavoriteContract.FavoriteEntry._ID + " ASC";
        List<Film> favoriteList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FavoriteContract.FavoriteEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);

        if (cursor.moveToFirst()){

            do {
                Film film = new Film();
                film.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_FILM))));
                film.setOriginalName(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_NAME)));
                film.setOverview(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW)));
                film.setVoteAverage(Double.parseDouble(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_VOTEAVERAGE))));
                film.setPosterPath(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTERPATH)));
                film.setFirstAirDate(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_DATE)));

                favoriteList.add(film);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return favoriteList;
    }


}
