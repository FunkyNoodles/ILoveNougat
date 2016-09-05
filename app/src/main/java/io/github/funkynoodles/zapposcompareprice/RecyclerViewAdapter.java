package io.github.funkynoodles.zapposcompareprice;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;

/**
 * Created by Louis on 9/4/2016.
 * Adapter for Recycler View
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private SearchQuery searchQuery;

    private SearchQuery pmSearchQuery;

    public RecyclerViewAdapter(SearchQuery searchQuery){
        this.searchQuery = searchQuery;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Shown attributes
        protected ImageView thumbnailView;
        protected TextView brandNameText;
        protected TextView productNameText;
        protected TextView priceText;

        // Hidden attributes
        protected String productId;
        protected String price;

        private CardView cardView;
        public ViewHolder(View v){
            super(v);
            thumbnailView = (ImageView)v.findViewById(R.id.thumbnail);
            brandNameText = (TextView)v.findViewById(R.id.brandName);
            productNameText = (TextView)v.findViewById(R.id.productName);
            priceText = (TextView)v.findViewById(R.id.price);
            cardView = (CardView)v.findViewById(R.id.cardView);
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Inflate a card view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i){
        SearchResult searchResult = searchQuery.getResult(i);

        // Use Android-Universal-Image-Loader to load image
        MainActivity.imageLoader.displayImage(searchResult.getThumbnailImageUrl(), viewHolder.thumbnailView);

        String brand = MainActivity.getContext().getResources().getString(R.string.brand_colon) + searchResult.getBrandName();
        String name = MainActivity.getContext().getResources().getString(R.string.name_colon) + searchResult.getProductName();
        String price = MainActivity.getContext().getResources().getString(R.string.price_colon) + searchResult.getPrice();

        // Assign attributes
        viewHolder.brandNameText.setText(brand);
        viewHolder.productNameText.setText(name);
        viewHolder.priceText.setText(price);
        viewHolder.productId = searchResult.getProductId();
        viewHolder.price = searchResult.getPrice();

        // Card view click listener
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apiSite = "https://api.6pm.com/Search?term=";
                String pmKey = "&key=524f01b7e2906210f7bb61dcbe1bfea26eb722eb";
                String query = searchQuery.getOriginalTerm();
                String url = apiSite + query + pmKey;
                new ReadURL6pm().execute(url, viewHolder.productId, viewHolder.price);
            }
        });
    }

    @Override
    public int getItemCount(){
        return searchQuery.getCurrentResultCount();
    }

    public void clearData(){
        if (searchQuery.getCurrentResultCount() > 0) {
            searchQuery.clear();
            notifyDataSetChanged();
        }
    }

    // AsyncTask for reading URL from 6pm.com in another thread
    public class ReadURL6pm extends AsyncTask<String, Void, SearchQuery> {
        protected SearchQuery doInBackground(String... args) {
            HttpResponse res;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            String resString;
            try {
                // Get response from the api site
                res = httpclient.execute(new HttpGet(URI.create(args[0])));
                resString = EntityUtils.toString(res.getEntity(), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            // Use Gson from Google to read JSON format
            Gson gson = new Gson();
            pmSearchQuery = gson.fromJson(resString, SearchQuery.class);
            for(int i = 0; i < pmSearchQuery.getCurrentResultCount(); i++){
                SearchResult s = pmSearchQuery.getResult(i);
                if (args[1].equals(s.getProductId())){
                    // Found product ID match
                    // Get rid of the dollar sign
                    String price = args[2].substring(1);
                    String price6pm = s.getPrice().substring(1);
                    float priceF = Float.parseFloat(price);
                    float price6pmF = Float.parseFloat(price6pm);

                    if (price6pmF < priceF){
                        // 6pm is cheaper
                        Message m = MainActivity.mHandler.obtainMessage();
                        m.sendToTarget();
                        break;
                    }
                }
            }

            return pmSearchQuery;
        }
        protected void onPostExecute(SearchQuery result){
        }
    }
}
