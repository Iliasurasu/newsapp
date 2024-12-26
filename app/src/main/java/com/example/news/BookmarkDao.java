package com.example.news;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface BookmarkDao {

    @Insert
    void insertBookmark(Bookmark bookmark);  // Вставка новой закладки

    @Delete
    void delete(Bookmark bookmark);  // Удаление закладки

    @Query("SELECT * FROM bookmarks")
    List<Bookmark> getAllBookmarks();  // Получение всех закладок
}
