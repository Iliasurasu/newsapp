package com.example.news;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import java.io.InputStream;
import java.util.List;

public class BookmarkAdapter extends ArrayAdapter<Bookmark> {

    private OnItemClickListener itemClickListener;
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Bookmark bookmark);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Bookmark bookmark);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    // Конструктор адаптера
    public BookmarkAdapter(Context context, List<Bookmark> bookmarks) {
        super(context, 0, bookmarks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Создание нового представления, если оно пустое
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bookmark_item, parent, false);
        }

        // Получаем закладку для текущей позиции
        Bookmark bookmark = getItem(position);

        // Находим элементы UI
        TextView title = convertView.findViewById(R.id.bookmark_title);
        TextView description = convertView.findViewById(R.id.bookmark_description);
        ImageView imageView = convertView.findViewById(R.id.bookmark_image);
        Button deleteButton = convertView.findViewById(R.id.delete_button);

        // Устанавливаем данные в элементы UI
        title.setText(bookmark.getTitle());
        description.setText(bookmark.getDescription());

        String urlToImage = bookmark.getUrlToImage();
        if (urlToImage != null && !urlToImage.isEmpty()) {
            new DownloadImageTask(imageView).execute(urlToImage);
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_background);
        }

// Обработка клика по кнопке удаления
        deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(bookmark);
            }
        });

        // Обработка клика по элементу
        convertView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(bookmark);
            }

        });

        return convertView;
    }

    // AsyncTask для загрузки изображения в фоновом потоке
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new java.net.URL(imageUrl).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }
    // Метод для обновления списка закладок
    public void setBookmarks(List<Bookmark> bookmarks) {
        clear(); // Очищаем старые данные
        addAll(bookmarks); // Добавляем новые данные
        notifyDataSetChanged(); // Уведомляем об изменении данных
    }

}
