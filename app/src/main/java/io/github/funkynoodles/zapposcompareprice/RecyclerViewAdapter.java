package io.github.funkynoodles.zapposcompareprice;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.databinding.tool.DataBindingBuilder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.provider.SyncStateContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
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
import java.util.ArrayList;

import io.github.funkynoodles.zapposcompareprice.databinding.CardViewBinding;

/**
 * Created by Louis on 9/4/2016.
 * Adapter for Recycler View
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private SearchQuery searchQuery;
    private CardViewBinding binding;

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
        protected SearchResult searchResult;

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
        binding = CardViewBinding.bind(v);

        //bindings.add(binding);
        //System.out.println(bindings.size());
        // Bind search result data to card view
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i){
        final SearchResult searchResult = searchQuery.getResult(i);

        // Use Android-Universal-Image-Loader to load image
        MainActivity.imageLoader.displayImage(searchResult.getThumbnailImageUrl(), viewHolder.thumbnailView);

        String brand = MainActivity.getContext().getResources().getString(R.string.brand_colon) + searchResult.getBrandName();
        String name = MainActivity.getContext().getResources().getString(R.string.name_colon) + searchResult.getProductName();

        // Assign attributes
        viewHolder.brandNameText.setText(brand);
        viewHolder.productNameText.setText(name);
        viewHolder.priceText.setText(searchResult.getPrice());
        viewHolder.searchResult = searchResult;

        binding.setSearchResult(searchResult);
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
}
