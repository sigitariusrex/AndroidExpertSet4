package com.kingleoners.infosinema;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {

    @GET("tv/popular")
    Call<FilmResponse> getPopularFilm(@Query("api_key") String apiKey);

    @GET("tv/top_rated")
    Call<FilmResponse> getTopRateFilm(@Query("api_key") String apiKey);

    @GET("tv/{tv_id}/videos")
    Call<TrailerResponse> getFilmTrailer(@Path("tv_id") int id, @Query("api_key") String apiKey);
}
