package com.example.netflex.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netflex.APIServices.ApiClient;
import com.example.netflex.APIServices.CountryAPIService;
import com.example.netflex.APIServices.FilmAPIService;
import com.example.netflex.APIServices.GenreAPIService;
import com.example.netflex.APIServices.SerieAPIService;
import com.example.netflex.R;
import com.example.netflex.adapter.FilmAdapter;
import com.example.netflex.adapter.SerieAdapter;
import com.example.netflex.model.Country;
import com.example.netflex.model.Film;
import com.example.netflex.model.Genre;
import com.example.netflex.model.Serie;
import com.example.netflex.responseAPI.FilmResponse;
import com.example.netflex.responseAPI.GenreResponse;
import com.example.netflex.responseAPI.SerieResponse;
import com.example.netflex.utils.SharedPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerTrending;
    private RecyclerView recyclerReleases;
    private List<Country> countries = new ArrayList<>();
    private List<Genre> genres = new ArrayList<>();
    private String keyword;


    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferencesManager sharedPrefs = new SharedPreferencesManager(this);
        if (!sharedPrefs.isLoggedIn()) {
            // Nếu chưa đăng nhập thì chuyển sang LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear stack
            startActivity(intent);
            return; // không chạy tiếp
        }
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        setupBottomNavigation();

        RecyclerView recyclerOnlyOn = findViewById(R.id.recyclerOnlyOn);
        RecyclerView recyclerPopular = findViewById(R.id.recyclerPopular);
        recyclerTrending = findViewById(R.id.recyclerTrending);
        recyclerReleases = findViewById(R.id.recyclerReleases);

        //Fetch danh sách Film
        fetchFilteredFilms(null, null, null, null);
        fetchLatestFilms();

        // Code cho phần Lọc
        findViewById(R.id.btnFilter).setOnClickListener(v -> showFilterDialog());

        // Gọi API lấy danh sách Serie
        SerieAPIService serieAPIService = ApiClient.getRetrofit().create(SerieAPIService.class);
        serieAPIService.getSeries(1).enqueue(new Callback<SerieResponse>() {
            @Override
            public void onResponse(Call<SerieResponse> call, Response<SerieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Serie> series = response.body().items;
                    Log.d("SERIE_API", "Series count: " + series.size());

                    setupSerieRecyclerView(recyclerPopular, series);
                    setupSerieRecyclerView(recyclerOnlyOn, series);
                } else {
                    Log.e("SERIE_API", "Response failed or body is null");
                }
            }

            @Override
            public void onFailure(Call<SerieResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch series", t);
            }
        });

        SearchView searchView = findViewById(R.id.searchView);

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(Color.WHITE);
            searchEditText.setHintTextColor(Color.GRAY);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(HomeActivity.this, FilteredResultActivity.class);
                intent.putExtra("keyword", query);
                intent.putExtra("genreId", (String) null);
                intent.putExtra("countryId", (String) null);
                intent.putExtra("year", -1);
                startActivity(intent);

                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                keyword = newText;
                return false;
            }
        });

    }

    // Fetch Filter Film
    private void fetchFilteredFilms(UUID genreId, UUID countryId, Integer year, String keyword) {
        FilmAPIService apiService = ApiClient.getRetrofit().create(FilmAPIService.class);
        int page = 1;

        Integer yearParam = year != null ? year.intValue() : null;

        Call<FilmResponse> call = apiService.getFilms(page, genreId, countryId, yearParam, keyword);
        call.enqueue(new Callback<FilmResponse>() {
            @Override
            public void onResponse(Call<FilmResponse> call, Response<FilmResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Film> films = response.body().items;
                    Log.d("FILM_API", "Filtered films count: " + films.size());

                    setupFilmRecyclerView(recyclerTrending, films);
                } else {
                    Log.e("FILM_API", "Response code: " + response.code());

                    FilmResponse body = response.body();
                    if (body == null || body.items == null) {
                        Log.e("FILM_API", "Body or items null");
                    }

                }
            }

            @Override
            public void onFailure(Call<FilmResponse> call, Throwable t) {
                Log.e("FILM_API", "Film API failed", t);
            }
        });
    }

    private void fetchLatestFilms() {
        FilmAPIService apiService = ApiClient.getRetrofit().create(FilmAPIService.class);
        int page = 1;

        Call<FilmResponse> call = apiService.getLatestFilms(page);
        call.enqueue(new Callback<FilmResponse>() {
            @Override
            public void onResponse(Call<FilmResponse> call, Response<FilmResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Film> latestFilms = response.body().items;

                    setupFilmRecyclerView(recyclerReleases, latestFilms);
                    Log.d("FILM_API", "Latest films count: " + latestFilms.size());
                } else {
                    Log.e("FILM_API", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<FilmResponse> call, Throwable t) {
                Log.e("FILM_API", "Latest API failed", t);
            }
        });
    }

    // Hiển thị danh sách Film
    private void setupFilmRecyclerView(RecyclerView recyclerView, List<Film> films) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new FilmAdapter(films));
    }


    // Hiển thị danh sách Serie
    private void setupSerieRecyclerView(RecyclerView recyclerView, List<Serie> series) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new SerieAdapter(this, series));
    }

    // Lọc Film
    private void showFilterDialog() {
        fetchGenresAndCountries(() -> {
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_filter, null);

            ChipGroup chipGroupGenre = view.findViewById(R.id.chipGroupGenre);
            Spinner spinnerCountry = view.findViewById(R.id.spinnerCountry);
            Spinner spinnerYear = view.findViewById(R.id.spinnerYear);

            Button btnFilm = view.findViewById(R.id.btnFilm);
            Button btnSeries = view.findViewById(R.id.btnSeries);

            AtomicReference<Boolean> isFilmSelected = new AtomicReference<>(true);

            // Màu mặc định và màu khi chọn
            int selectedColor = Color.parseColor("#3399FF");
            int defaultColor = Color.parseColor("#444444");

            btnFilm.setBackgroundTintList(ColorStateList.valueOf(selectedColor));

            btnFilm.setOnClickListener(v -> {
                btnFilm.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
                btnSeries.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
                isFilmSelected.set(true);
            });

            btnSeries.setOnClickListener(v -> {
                btnFilm.setBackgroundTintList(ColorStateList.valueOf(defaultColor));
                btnSeries.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
                isFilmSelected.set(false);
            });

            // Tạo danh sách năm
            List<Integer> years = new ArrayList<>();
            years.add(-1); // -1 tượng trưng cho "All Years"
            for (int y = 2025; y >= 1990; y--) {
                years.add(y);
            }

            ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<Integer>(
                    this,
                    android.R.layout.simple_spinner_item,
                    years
            ) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text = (TextView) view;
                    if (getItem(position) != null && getItem(position) == -1) {
                        text.setText("All Years");
                    } else {
                        text.setText(String.valueOf(getItem(position)));
                    }
                    text.setTextColor(Color.WHITE);
                    text.setTextSize(18);
                    return view;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView text = (TextView) view;
                    if (getItem(position) != null && getItem(position) == -1) {
                        text.setText("All Years");
                    } else {
                        text.setText(String.valueOf(getItem(position)));
                    }
                    text.setTextColor(Color.WHITE);
                    text.setTextSize(20);
                    return view;
                }
            };
            spinnerYear.setAdapter(yearAdapter);
            spinnerYear.setSelection(0);

            // Country adapter
            Country allCountry = new Country();
            allCountry.id = null;
            allCountry.name = "All Countries";

            List<Country> countryListWithAll = new ArrayList<>();
            countryListWithAll.add(allCountry);
            countryListWithAll.addAll(countries);

            ArrayAdapter<Country> countryAdapter = new ArrayAdapter<Country>(
                    this,
                    android.R.layout.simple_spinner_item,
                    countryListWithAll
            ) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text = (TextView) view;
                    Country item = getItem(position);
                    text.setText(item != null ? item.name : "All Countries");
                    text.setTextColor(Color.WHITE);
                    text.setTextSize(18);
                    return view;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView text = (TextView) view;
                    Country item = getItem(position);
                    text.setText(item != null ? item.name : "All Countries");
                    text.setTextColor(Color.WHITE);
                    text.setTextSize(20);
                    return view;
                }
            };
            spinnerCountry.setAdapter(countryAdapter);
            spinnerCountry.setSelection(0);

            // Tạo các Chip động cho Genre
            if (genres != null) {
                chipGroupGenre.removeAllViews();
                for (Genre genre : genres) {
                    Chip chip = new Chip(this);
                    chip.setText(genre.name);
                    chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#BBBBBB")));
                    chip.setChipStrokeWidth(3f);
                    chip.setTextColor(Color.WHITE);
                    chip.setTextSize(18);
                    chip.setHeight(150);
                    chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#444444")));
                    chip.setClickable(true);
                    chip.setCheckable(true);

                    chip.setShapeAppearanceModel(
                            chip.getShapeAppearanceModel().toBuilder()
                                    .setAllCornerSizes(50f)
                                    .build()
                    );

                    // Khi được chọn => đổi màu nền
                    chip.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                        if (isChecked) {
                            chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#3399FF"))); // xanh nước biển nhạt
                        } else {
                            chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#444444"))); // màu gốc
                        }
                    });

                    chipGroupGenre.addView(chip);
                }
            }

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();

            // OPTIONAL: Chiếm 90% chiều cao màn hình nếu muốn
            View parent = (View) view.getParent();
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
            behavior.setPeekHeight((int)(getResources().getDisplayMetrics().heightPixels * 0.8), true);

            // Gán nút Apply
            view.findViewById(R.id.btnApply).setOnClickListener(v -> {
                Integer selectedYear = (Integer) spinnerYear.getSelectedItem();
                Country selectedCountry = (Country) spinnerCountry.getSelectedItem();

                // Lấy Chip Genre được chọn
                int selectedChipId = chipGroupGenre.getCheckedChipId();
                Genre selectedGenre = null;
                if (selectedChipId != -1) {
                    Chip selectedChip = chipGroupGenre.findViewById(selectedChipId);
                    selectedGenre = genres.stream()
                            .filter(g -> g.name.equals(selectedChip.getText().toString()))
                            .findFirst().orElse(null);
                }

                // Tạo Intent để mở trang mới
                Intent intent = new Intent(HomeActivity.this, FilteredResultActivity.class);
                if (isFilmSelected.get()) {
                    intent.putExtra("type", "film");
                } else {
                    intent.putExtra("type", "serie");
                }
                intent.putExtra("genreId", selectedGenre != null ? selectedGenre.id.toString() : null);
                intent.putExtra("countryId", selectedCountry != null ? selectedCountry.id != null ? selectedCountry.id.toString() : null : null);
                intent.putExtra("year", selectedYear != null ? selectedYear : -1);
                intent.putExtra("keyword", keyword);

                startActivity(intent);
            });


            // Nút Reset
            view.findViewById(R.id.btnReset).setOnClickListener(v -> {
                chipGroupGenre.clearCheck();
                spinnerYear.setSelection(0);
                spinnerCountry.setSelection(0);
            });
        });
    }


    private void fetchGenresAndCountries(Runnable onComplete) {
        GenreAPIService genreAPI = ApiClient.getRetrofit().create(GenreAPIService.class);
        CountryAPIService countryAPI = ApiClient.getRetrofit().create(CountryAPIService.class);

        genreAPI.getGenres().enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    genres = response.body().genres;
                } else {
                    Log.e("API_ERROR", "Genres response failed");
                }

                // Tiếp tục gọi Country
                countryAPI.getCountries().enqueue(new Callback<List<Country>>() {
                    @Override
                    public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            countries = response.body();
                        } else {
                            Log.e("API_ERROR", "Countries response failed");
                        }
                        // Luôn gọi onComplete để hiển thị dialog
                        runOnUiThread(onComplete);
                    }

                    @Override
                    public void onFailure(Call<List<Country>> call, Throwable t) {
                        Log.e("API_ERROR", "Failed to fetch countries", t);
                        runOnUiThread(onComplete); // vẫn gọi dialog
                    }
                });
            }

            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch genres", t);
                // Nếu genre lỗi thì vẫn gọi country để lấy tiếp
                countryAPI.getCountries().enqueue(new Callback<List<Country>>() {
                    @Override
                    public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            countries = response.body();
                        }
                        runOnUiThread(onComplete);
                    }

                    @Override
                    public void onFailure(Call<List<Country>> call, Throwable t) {
                        Log.e("API_ERROR", "Failed to fetch countries", t);
                        runOnUiThread(onComplete);
                    }
                });
            }
        });
    }


    // Điều hướng thanh bottom
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                return true;
            } else if (itemId == R.id.menu_explore) {
                Intent intent = new Intent(HomeActivity.this, FilteredResultActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.menu_new) {
                // TODO: Mở New & Hot
                return true;
            } else if (itemId == R.id.menu_History) {
                // TODO: Mở History
                Intent intent = new Intent(HomeActivity.this, WatchHistoryActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_settings) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
//        } else if (itemId == R.id.menu_profile) {
//                Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
//                startActivity(intent);
//                return true;
//            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.menu_home);
    }
}
