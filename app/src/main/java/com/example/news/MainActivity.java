package com.example.news;

import androidx.room.Room;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.room.Room;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import android.widget.Spinner;

import android.widget.ArrayAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private NewsViewModel newsViewModel;
    private Spinner categorySpinner; // Для выбора категории
    private EditText searchEditText;
    private AppDatabase appDatabase; // Объявление переменной только один раз

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация базы данных Room
        appDatabase = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "bookmarks_database"
        ).build();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Инициализация адаптера
        newsAdapter = new NewsAdapter(new ArrayList<>());
        recyclerView.setAdapter(newsAdapter);

        // Обработка кликов на кнопку закладок
        newsAdapter.setOnBookmarkClickListener(news -> {
            new Thread(() -> {
                Bookmark bookmark = new Bookmark();
                bookmark.setTitle(news.getTitle());
                bookmark.setDescription(news.getDescription());
                bookmark.setUrlToImage(news.getUrlToImage());
                bookmark.setUrl(news.getUrl()); // Устанавливаем URL статьи
                appDatabase.bookmarkDao().insertBookmark(bookmark);
            }).start();
        });

        newsAdapter.setOnItemClickListener(news -> {
            Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
            intent.putExtra("article_url", news.getUrl()); // Передаем URL статьи
            startActivity(intent);
        });



        // Инициализация ViewModel
        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);

        // Наблюдение за обновлением списка новостей
        newsViewModel.getNewsList().observe(this, newsList -> {
            newsAdapter.setNewsList(newsList);
        });

        // Инициализация Spinner для выбора категории
        categorySpinner = findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.news_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Получаем выбранную категорию
                String selectedCategory = parentView.getItemAtPosition(position).toString();
                newsViewModel.setCategory(selectedCategory); // Запрашиваем новости для выбранной категории
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Запрашиваем новости без категории
                newsViewModel.setCategory(""); // Пустая категория для получения всех новостей
            }
        });

        // Инициализация строки поиска
        searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Обновляем поисковый запрос в ViewModel
                newsViewModel.setSearchQuery(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Запрашиваем новости без категории (по умолчанию)
        newsViewModel.fetchNews("2f24442f98e446da819a1d3912475ff1", "");
    }
    public void onViewBookmarksClick(View view) {
        Intent intent = new Intent(this, BookmarksActivity.class);
        startActivity(intent);
    }

}
