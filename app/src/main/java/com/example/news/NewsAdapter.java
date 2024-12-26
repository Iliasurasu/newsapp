package com.example.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.List;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<News> newsList;
    private OnBookmarkClickListener bookmarkClickListener;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(News news);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnBookmarkClickListener {
        void onBookmarkClick(News news);
    }

    public void setOnBookmarkClickListener(OnBookmarkClickListener listener) {
        this.bookmarkClickListener = listener;
    }

    // Конструктор адаптера
    public NewsAdapter(List<News> newsList) {
        this.newsList = newsList;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        News news = newsList.get(position);

        holder.title.setText(news.getTitle());
        holder.description.setText(news.getDescription());

        String urlToImage = news.getUrlToImage();
        if (urlToImage != null && !urlToImage.isEmpty()) {
            new DownloadImageTask(holder.image).execute(urlToImage);
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_background);
        }

        // Обработка клика по элементу списка
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(news);
            }
        });

        // Обработка клика по кнопке закладки
        holder.bookmarkButton.setOnClickListener(v -> {
            if (bookmarkClickListener != null) {
                bookmarkClickListener.onBookmarkClick(news);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    // Обновляем данные в адаптере
    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    // ViewHolder для элементов списка
    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView image;
        Button bookmarkButton;

        public NewsViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.news_title);
            description = itemView.findViewById(R.id.news_description);
            image = itemView.findViewById(R.id.news_image);
            bookmarkButton = itemView.findViewById(R.id.bookmark_button);
        }
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
}
