package com.example.newsaggregator;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArticleHolder extends RecyclerView.ViewHolder {
    TextView articleTitle, articleDate, articleAuthor, articleDescription, articleCount;
    ImageView articleImage;

    public ArticleHolder(@NonNull View itemView) {
        super(itemView);
        articleTitle = itemView.findViewById(R.id.articleHeadline);
        articleDate = itemView.findViewById(R.id.articleDate);
        articleAuthor = itemView.findViewById(R.id.articleAuthor);
        articleDescription = itemView.findViewById(R.id.articleDescription);
        articleCount = itemView.findViewById(R.id.articleCount);
        articleImage = itemView.findViewById(R.id.articleImage);
        //Making the description scrollable if the content is bigger than the container
        articleDescription.setMovementMethod(new ScrollingMovementMethod());
    }
}
