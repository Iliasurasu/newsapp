package com.example.news;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class NewsViewModel extends ViewModel {

    private MutableLiveData<List<News>> newsList = new MutableLiveData<>();
    private MutableLiveData<List<News>> allNewsList = new MutableLiveData<>();
    private MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private MutableLiveData<String> category = new MutableLiveData<>(); // Новая переменная для категории

    public LiveData<List<News>> getNewsList() {
        return newsList;
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public LiveData<String> getCategory() {
        return category;
    }

    // Запрос новостей по категории
    public void fetchNews(String apiKey, String category) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewsApi newsApi = retrofit.create(NewsApi.class);

        // Если категория не указана, используем "general" для получения всех новостей
        Call<NewsResponse> call;
        if (category == null || category.isEmpty() || category.equals("all")) {
            call = newsApi.getTopHeadlines("us", "general", apiKey); // Новости по умолчанию, категория "general"
        } else {
            call = newsApi.getTopHeadlines("us", category, apiKey); // Запрос новостей по выбранной категории
        }

        // Асинхронный запрос
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful()) {
                    List<News> news = response.body().getArticles();

                    // Сортируем новости по названию
                    news.sort((news1, news2) -> news1.getTitle().compareTo(news2.getTitle()));

                    // Сохраняем все новости в отдельный список
                    allNewsList.setValue(news);

                    // Устанавливаем новости в текущий список (по умолчанию все)
                    newsList.setValue(news);
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // Обновление поискового запроса
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
        filterNews(query);
    }

    // Установка категории
    public void setCategory(String category) {
        this.category.setValue(category);
        fetchNews("2f24442f98e446da819a1d3912475ff1", category); // Перезагружаем новости по новой категории
    }

    // Фильтрация новостей по запросу
    private void filterNews(String query) {
        if (query == null || query.isEmpty()) {
            // Если строка поиска пуста, показываем все новости
            newsList.setValue(allNewsList.getValue());
        } else {
            // Фильтруем новости по запросу
            List<News> filteredNews = new ArrayList<>();
            for (News news : allNewsList.getValue()) {
                if (news.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredNews.add(news);
                }
            }
            newsList.setValue(filteredNews);
        }
    }
}
