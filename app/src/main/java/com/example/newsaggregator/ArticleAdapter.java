package com.example.newsaggregator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleHolder> {
    private final MainActivity mainActivity;
    private final List<Article> articleList;

    public ArticleAdapter(MainActivity mainActivity, List<Article> articleList) {
        this.mainActivity = mainActivity;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public ArticleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_article_layout,parent,false));
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ArticleHolder holder, int position) {
        Article article = articleList.get(position);

        if(!article.checkIfNull(article.getTitle())){
            holder.articleTitle.setText(article.getTitle());
            holder.articleTitle.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(article.getUrl()));
                mainActivity.startActivity(intent);
            });
        }else{
            holder.articleTitle.setVisibility(View.INVISIBLE);
        }
        holder.articleDate.setText(article.getPublishedDate());

        if(!article.checkIfNull(article.getAuthor())){
            holder.articleAuthor.setText(article.getAuthor());
        }
        else{
            holder.articleAuthor.setVisibility(View.INVISIBLE);
        }

        if(!article.checkIfNull((article.getUrl()))){
            Picasso picasso = Picasso.with(mainActivity);
            picasso.setLoggingEnabled(true);
            picasso.load(article.getUrlToImage()).resize(1080,650).error(R.drawable.brokenimage).placeholder(R.drawable.loading).into(holder.articleImage);
            holder.articleImage.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(article.getUrl()));
                mainActivity.startActivity(intent);
            });
        }
        else{
            holder.articleImage.setImageResource(R.drawable.noimage);
        }

        if(!article.checkIfNull(article.getDescription())){
            holder.articleDescription.setText(article.getDescription());
        }else{
            holder.articleDescription.setVisibility(View.INVISIBLE);
        }


        holder.articleCount.setText(String.format("%d of %d", (position + 1), articleList.size()));
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }
}
