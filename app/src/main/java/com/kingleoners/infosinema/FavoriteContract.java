package com.kingleoners.infosinema;

import android.provider.BaseColumns;

public class FavoriteContract {

    public static final class FavoriteEntry implements BaseColumns{

        public static final String TABLE_NAME = "favoriteFilm";
        public static final String COLUMN_FILM = "filmId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTEAVERAGE = "vote_average";
        public static final String COLUMN_POSTERPATH = "poster_path";
        public static final String COLUMN_DATE = "first_air_date";
    }
}
