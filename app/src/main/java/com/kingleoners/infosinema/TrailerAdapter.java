package com.kingleoners.infosinema;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerFilmViewHolder> {

    private Context context;
    private List<Trailer> trailerList;

    public TrailerAdapter(Context context, List<Trailer> trailerList) {
        this.context = context;
        this.trailerList = trailerList;
    }

    @NonNull
    @Override
    public TrailerFilmViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trailer_card, viewGroup, false);
        return new TrailerFilmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerFilmViewHolder trailerFilmViewHolder, int i) {

        trailerFilmViewHolder.name.setText(trailerList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class TrailerFilmViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView thumbnail;

        public TrailerFilmViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.name);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        Trailer clickedDataItem = trailerList.get(position);
                        String videoId = trailerList.get(position).getKey();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("TV_ID", videoId);
                        context.startActivity(intent);
                        Toast.makeText(v.getContext(), "Kamu memilih " + clickedDataItem, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
