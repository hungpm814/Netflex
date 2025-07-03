package com.example.netflex;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.FilmAPIService;
import com.example.netflex.model.Actor;
import com.example.netflex.model.Film;
import com.example.netflex.viewModels.FilmDetailViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilmDetailActivity extends AppCompatActivity {

    ImageView poster;
    TextView title, textAbout, textActor, textYear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_detail);

        String filmId = getIntent().getStringExtra("film_id");
        FilmAPIService apiService = ApiClient.getRetrofit().create(FilmAPIService.class);
        Call<FilmDetailViewModel> call =  apiService.getFilm(filmId);

        // Find views by ids.
        setupViews();

        if (filmId != null) {
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<FilmDetailViewModel> call, Response<FilmDetailViewModel> response) {
                    if (response.isSuccessful()){
                        Film film = response.body().film;
                        List<Actor> actors = response.body().actors;

                        // Set image to poster.
                        Picasso.get()
                                .load(film.poster)
                                .into(poster);

                        title.setText(film.getTitle());
                        textAbout.setText(film.getAbout());

                        String textsForTextActor = "";
                        StringBuilder sb = new StringBuilder();

                        if (actors.size() > 0){
                            for (Actor a : actors){
                                sb.append(a.getName() + ", ");
                            }
                            textsForTextActor = sb.toString();
                        }

                        else{
                            textsForTextActor = getString(R.string.no_actor_info);
                        }

                        textActor.setText("Actors: " + textsForTextActor);
                        textYear.setText("Year: " + film.getProductionYear());
                    }
                }

                @Override
                public void onFailure(Call<FilmDetailViewModel> call, Throwable t) {

                }
            });

        } else {
            Toast.makeText(this, "Film ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViews(){
        poster = findViewById(R.id.imagePoster);
        title = findViewById(R.id.title);
        textAbout = findViewById(R.id.textAbout);
        textActor = findViewById(R.id.textActor);
        textYear = findViewById(R.id.textYear);
    }
}