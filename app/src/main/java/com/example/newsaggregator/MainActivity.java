package com.example.newsaggregator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String NEWSURL ="https://newsapi.org/v2/sources";
    private static final String SPECIFICNEWSSOURCEURL = "https://newsapi.org/v2/top-headlines";
    private final String APIKEY= "0aa0b169e930489da20c6c5ae77de619";
    private static final List<Article> articleList = new ArrayList<>();
    private static final List<Article> resultArticleList = new ArrayList<>();
    private ViewPager2 viewPager;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private static final String TAG = "MainActivity";
    ArticleAdapter articleAdapter;
    private ArrayAdapter<Source> arrayAdapter;
    private static ArrayList<String> menuItems;
    private static final HashSet<String> newsCategory = new HashSet<>();
    private static List<Source> tempSource;
    private static Sources news;
    private Menu menu;
    TextView newsCategoryText,drawerTextView;
    List<Source> newsSourcesList, copyNewsSourcesList;
    String[] colorArray =  new String[]{
            "#f44336", "#e91e63", "#9c27b0", "#673ab7", "#3f51b5", "#2196f3",
            "#00bcd4", "#ff5722","#ffeb3b","#ff9100"
    };
    HashMap<String, String> menuTextcolors ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("News Gateway");
        viewPager = findViewById(R.id.viewPager);
//        ArticleAdapter articlesAdapter = new ArticleAdapter(this, articleList);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.drawerLayout);
        boolean connection = isNetworkConnected();
        if(connection){
            //get news Data
            getNewsData();
        }else {
            Toast.makeText(this, "Please Check you Internet Connection", Toast.LENGTH_SHORT).show();
        }
        drawerList.setOnItemClickListener(
                (parent, view, position, id) -> getSelectedItem(position)
        );

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.openDrawer,R.string.closeDrawer);
        drawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public void getNewsData(){
        RequestQueue queueSource = Volley.newRequestQueue(this);
        Uri.Builder buildURL = Uri.parse(NEWSURL).buildUpon();
        buildURL.appendQueryParameter("apiKey", APIKEY);
        String finalUrl =  buildURL.build().toString();
        Log.d(TAG, "getNewsDate: "+finalUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, finalUrl, null, response -> {
            news = getSources(response);
            setData(news.getNewsSources());
            menuItems = new ArrayList<>();
            newsCategory.add("all");
            tempSource = news.getNewsSources();
            for (Source source: tempSource){
                newsCategory.add(source.getNewsCategory());
            }
            setMenuItems(newsCategory);
            Log.d(TAG, "onResponse: "+news.getRespStatus());
        }, error -> Toast.makeText(MainActivity.this, "error in getting Info", Toast.LENGTH_SHORT).show()){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "News-App");
                return headers;
            }
        };
        queueSource.add(jsonObjectRequest);
    }

    public void setMenuItems(HashSet<String> newsCategory){
        menuTextcolors = setMenuTextColor(newsCategory);
        for(String category: newsCategory){
            if(!category.equals("all")){
                SpannableString s = new SpannableString(category);
                s.setSpan(new ForegroundColorSpan(Color.parseColor(menuTextcolors.get(category))), 0, s.length(), 0);
                this.menu.add(s);
            }else{
                this.menu.add(category);
            }
        }
    }

    public void setData(List<Source> sourceList){
        newsSourcesList = sourceList;
        copyNewsSourcesList = newsSourcesList;
        arrayAdapter = new ArrayAdapter<Source>(this, android.R.layout.simple_list_item_1, this.newsSourcesList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                Source newsSource = newsSourcesList.get(position);
                View view = super.getView(position, convertView, parent);
                newsCategoryText = (TextView) view.findViewById(android.R.id.text1);
                newsCategoryText.setText(newsSource.getSourceName());
                setTitle("News Gateway ("+newsSourcesList.size()+")");
                newsCategoryText.setTextColor(Color.parseColor(menuTextcolors.get(newsSource.getNewsCategory())));
                return view;
            }
        };
        drawerList.setAdapter(arrayAdapter);
        drawerToggle = new ActionBarDrawerToggle( this, drawerLayout,R.string.openDrawer,R.string.closeDrawer);
    }

    public Boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = this.getSystemService(ConnectivityManager.class);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        return network != null && network.isConnectedOrConnecting();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    public Sources getSources(JSONObject newResp){
        List<Source> newsSources = new ArrayList<>();
        String status="";
        try{
            status = newResp.getString("status");
            JSONArray newsSourceJsonArray = newResp.getJSONArray("sources");
            for(int i=0;i<newsSourceJsonArray.length();i++){
                JSONObject subSourceItem = newsSourceJsonArray.getJSONObject(i);
                String id = subSourceItem.getString("id");
                String name = subSourceItem.getString("name");
                String desc = subSourceItem.getString("category");
                newsSources.add(new Source(id, name, desc));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Sources(status, newsSources);
    }

    public HashMap<String, String> setMenuTextColor(HashSet<String> menuCategorySet){
        HashMap<String, String> colorHashmap = new HashMap<>();
        List<String> colorList = new ArrayList<>(Arrays.asList(colorArray));
        int i = 0 ;
        for(String has : menuCategorySet){
            colorHashmap.put(has, colorList.get(i));
            i++;
        }
        return  colorHashmap;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: "+item);
        if(drawerToggle.onOptionsItemSelected(item)){
            Log.d(TAG, "onOptionsItemSelected: "+item);
            return true;
        }
        if(item.getTitle().equals("all")){
            String title = item.getTitle().toString();
            setDrawerItems(newsSourcesList,title);
        }
        else if(item.getTitle() != null){
            String title = item.getTitle().toString();
            List<Source> result = new ArrayList<>();
            for (int i =0 ; i < tempSource.size(); i++){
                if(tempSource.get(i).getNewsCategory().equalsIgnoreCase(title)){
                    result.add(tempSource.get(i));
                }
            }
            setDrawerItems(result,title);
        }
        return super.onOptionsItemSelected(item);
    }
    private void setDrawerItems(List<Source> drawerListItems, String title) {
        copyNewsSourcesList = drawerListItems;
        arrayAdapter = new ArrayAdapter<Source>(this, android.R.layout.simple_list_item_1, drawerListItems){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                Source newsSource = drawerListItems.get(position);
                View view = super.getView(position, convertView, parent);
                drawerTextView = (TextView) view.findViewById(android.R.id.text1);
                drawerTextView.setText(newsSource.getSourceName());
                if(title.equals("all")){
                    setTitle("News Gateway ("+drawerListItems.size()+")");
                    drawerTextView.setTextColor(Color.parseColor(menuTextcolors.get(newsSource.getNewsCategory())));
                } else {
                    setTitle(drawerListItems.get(0).getNewsCategory()+" ("+drawerListItems.size()+")");
                }
                drawerTextView.setTextColor(Color.parseColor(menuTextcolors.get(newsSource.getNewsCategory())));
                return view;
            }
        };
        drawerList.setAdapter(arrayAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getArticles(String sourceName){
        RequestQueue queueArticle = Volley.newRequestQueue(this);
        Uri.Builder buildURL = Uri.parse(SPECIFICNEWSSOURCEURL).buildUpon();
        buildURL.appendQueryParameter("sources", sourceName);
        buildURL.appendQueryParameter("apiKey", APIKEY);
        String finalurl = buildURL.build().toString();
        Log.d(TAG, "getArticles: "+finalurl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, finalurl, null, response -> {
            Toast.makeText(MainActivity.this, "Searching the articles", Toast.LENGTH_SHORT).show();
            try{
                JSONArray articlesJsonArray = response.getJSONArray("articles");
                for(int i=0;i<articlesJsonArray.length();i++) {
                    Article currArticle = new Article();
                    JSONObject jsonObjCurrArticle = articlesJsonArray.getJSONObject(i);
                    currArticle.setAuthor(jsonObjCurrArticle.getString("author"));
                    currArticle.setTitle(jsonObjCurrArticle.getString("title"));
                    currArticle.setDescription(jsonObjCurrArticle.getString("description"));
                    currArticle.setUrl(jsonObjCurrArticle.getString("url"));
                    currArticle.setUrlToImage(jsonObjCurrArticle.getString("urlToImage"));
                    currArticle.setPublishedDate(currArticle.getLocaltIme(jsonObjCurrArticle.getString("publishedAt")));
                    resultArticleList.add(currArticle);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            articleAdapter = new ArticleAdapter(MainActivity.this,resultArticleList);
            viewPager.setAdapter(articleAdapter);
            Log.d(TAG, "onResponse3: "+articleAdapter.getItemCount());
        }, error -> Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show()){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "News-App");
                return headers;
            }
        };
        queueArticle.add(jsonObjectRequest);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getSelectedItem(int position) {
        Log.d(TAG, "selectItem: "+position);
        String title = copyNewsSourcesList.get(position).getSourceName();
        setTitle(title);
        resultArticleList.clear();
        getArticles(copyNewsSourcesList.get(position).getId());
        viewPager.setCurrentItem(position);
        drawerLayout.setBackgroundResource(0);
        viewPager.setBackground(null);
        drawerLayout.closeDrawer(drawerList);

    }
}