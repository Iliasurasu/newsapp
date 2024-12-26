package com.example.news;

import android.os.Bundle;
import android.widget.ListView;
import android.util.Log;
import androidx.room.Room;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class BookmarksActivity extends AppCompatActivity {
    private ListView listView;
    private BookmarkAdapter bookmarkAdapter;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        // Инициализация базы данных
        appDatabase = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "bookmarks_database"
        ).build();


        listView = findViewById(R.id.bookmarks_list_view);

        // Создаем адаптер для отображения закладок
        bookmarkAdapter = new BookmarkAdapter(this, new ArrayList<>());
        listView.setAdapter(bookmarkAdapter);

        new Thread(() -> {
            // Получаем все закладки
            List<Bookmark> bookmarks = appDatabase.bookmarkDao().getAllBookmarks();

            // Обновляем список на UI
            runOnUiThread(() -> {
                bookmarkAdapter.setBookmarks(bookmarks); // Обновляем список
            });
        }).start();
        bookmarkAdapter.setOnItemClickListener(bookmark -> {
            String url = bookmark.getUrl(); // Получаем URL из закладки
            if (url != null && !url.isEmpty()) {
                Intent intent = new Intent(BookmarksActivity.this, ArticleActivity.class);
                intent.putExtra("article_url", url); // Передаём URL в Intent
                startActivity(intent);
            } else {
                // Логируем или показываем сообщение, если URL отсутствует
                Log.e("BookmarksActivity", "URL is null or empty");
            }
        });
        bookmarkAdapter.setOnDeleteClickListener(bookmark -> {
            new Thread(() -> {
                appDatabase.bookmarkDao().delete(bookmark); // Удаляем из базы данных
                runOnUiThread(() -> {
                    bookmarkAdapter.remove(bookmark); // Удаляем из адаптера
                    bookmarkAdapter.notifyDataSetChanged();
                });
            }).start();
        });





    }

}
