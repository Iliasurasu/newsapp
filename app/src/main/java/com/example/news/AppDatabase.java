package com.example.news;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Bookmark.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookmarkDao bookmarkDao();  // Метод для получения объекта BookmarkDao
}
