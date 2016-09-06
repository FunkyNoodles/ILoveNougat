package io.github.funkynoodles.zapposcompareprice;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;

import io.github.funkynoodles.zapposcompareprice.databinding.CardViewBinding;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView noSearchText;
    private TextView startBySearchText;

    private SearchQuery searchQuery;

    public static ImageLoader imageLoader;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // From splash screen
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);

        // Setups
        noSearchText = (TextView)findViewById(R.id.noSearchResult);
        startBySearchText = (TextView)findViewById(R.id.startBySearch);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                String apiSite = "https://api.zappos.com/Search?term=";
                String zapposKey = "&key=b743e26728e16b81da139182bb2094357c31d331";
                String url = apiSite + query + zapposKey;

                // Clear the area and then execute URL read in another thread
                noSearchText.setVisibility(TextView.INVISIBLE);
                startBySearchText.setVisibility(TextView.INVISIBLE);
                if (adapter != null){
                    adapter.clearData();
                }
                new ReadURLZappos().execute(url);

                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String query){
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // AsyncTask for reading URL from zappos.com in another thread
    public class ReadURLZappos extends AsyncTask<String, Void, SearchQuery> {
        protected SearchQuery doInBackground(String... url) {
            HttpResponse res;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            String resString;
            try {
                // Get response from the api site
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                resString = EntityUtils.toString(res.getEntity(), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            // Use Gson from Google to read JSON format
            Gson gson = new Gson();
            searchQuery = gson.fromJson(resString, SearchQuery.class);

            return searchQuery;
        }
        protected void onPostExecute(SearchQuery result){
            // If no results from the search, then display no results and return
            if (result.getCurrentResultCount() < 1){
                noSearchText.setVisibility(TextView.VISIBLE);
                return;
            }
            adapter = new RecyclerViewAdapter(searchQuery);
            recyclerView.setAdapter(adapter);
        }
    }

    public static Context getContext(){
        return context;
    }



    public void cardClicked(View view){
        CardViewBinding binding = DataBindingUtil.getBinding(view);
    // Start another activity to display clicked product
        Intent intent = new Intent(MainActivity.getContext(), ItemActivity.class);
        intent.putExtra(Constants.SEARCH_TERM, searchQuery.getOriginalTerm());
        intent.putExtra(Constants.SEARCH_RESULT, binding.getSearchResult());
        MainActivity.getContext().startActivity(intent);
    }
}
