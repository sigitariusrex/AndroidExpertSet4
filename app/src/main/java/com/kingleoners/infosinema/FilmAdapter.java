package com.kingleoners.infosinema;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmViewHolder> {

    private Context context;
    private List<Film> filmList;

    public FilmAdapter(Context context, List<Film> filmList) {
        this.context = context;
        this.filmList = filmList;
    }

    @NonNull
    @Override
    public FilmAdapter.FilmViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.film_card, viewGroup, false);

        return new FilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmAdapter.FilmViewHolder filmViewHolder, int i) {

        filmViewHolder.name.setText(filmList.get(i).getOriginalName());
        String rate = Double.toString(filmList.get(i).getVoteAverage());
        filmViewHolder.vote_average.setText(rate);

        String poster = "https://image.tmdb.org/t/p/w500" + filmList.get(i).getPosterPath();

        Glide.with(context).load(poster)
                .placeholder(R.drawable.loading)
                .into(filmViewHolder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return filmList.size();
    }

    public class FilmViewHolder extends RecyclerView.ViewHolder {

        public TextView name, vote_average;
        public ImageView thumbnail;

        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.name);
            vote_average = (TextView)itemView.findViewById(R.id.vote_average);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        Film clickedDataItem = filmList.get(position);
                        Intent intent = new Intent(context, DetailFilmActivity.class);
                        intent.putExtra("films", clickedDataItem);
                        /*intent.putExtra("original_name", filmList.get(position).getOriginalName());
                        intent.putExtra("poster_path", filmList.get(position).getPosterPath());
                        intent.putExtra("overview", filmList.get(position).getOverview());
                        intent.putExtra("vote_average", Double.toString(filmList.get(position).getVoteAverage()));
                        intent.putExtra("first_air_date", filmList.get(position).getFirstAirDate());
                        intent.putExtra("id",filmList.get(position).getId());*/
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        Toast.makeText(v.getContext(), "Kamu memilih " + clickedDataItem.getOriginalName(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
