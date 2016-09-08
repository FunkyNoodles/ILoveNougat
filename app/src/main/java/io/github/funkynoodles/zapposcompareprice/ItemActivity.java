package io.github.funkynoodles.zapposcompareprice;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;

public class ItemActivity extends AppCompatActivity {

    private TextView brandText;
    private TextView priceText;
    private TextView productIdText;
    private TextView originalPriceText;
    private TextView styleIdText;
    private TextView colorIdText;
    private TextView percentOffText;
    private TextView productUrlText;
    private TextView productNameText;
    private ImageView imageView;

    private SearchResult searchResult;
    private String searchTerm;

    public static Handler mHandler;
    public static Handler notFoundHandler;

    private static Context context;

    private String connectedDeviceName = null;
    private ArrayAdapter<String> arrayAdapter;

    private BluetoothAdapter btAdapter = null;
    private BluetoothShareService shareService = null;

    SearchQuery pmSearchQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        this.context = this;

        Intent intent = getIntent();

        searchResult = (SearchResult)intent.getSerializableExtra(Constants.SEARCH_RESULT);
        searchTerm = (String)intent.getSerializableExtra(Constants.SEARCH_TERM);

        imageView = (ImageView)findViewById(R.id.detailThumbnail);
        MainActivity.imageLoader.displayImage(searchResult.getThumbnailImageUrl(), imageView);

        brandText = (TextView)findViewById(R.id.detailBrandName);
        String brand = getResources().getString(R.string.brand_colon) + searchResult.getBrandName();
        brandText.setText(brand);

        priceText = (TextView)findViewById(R.id.detailPrice);
        String price = getResources().getString(R.string.price_colon) + searchResult.getPrice();
        priceText.setText(price);

        productIdText = (TextView)findViewById(R.id.detailProductId);
        String productId = getResources().getString(R.string.product_id_colon) + searchResult.getProductId();
        productIdText.setText(productId);

        originalPriceText = (TextView)findViewById(R.id.detailOriginalPrice);
        String originalPrice = getResources().getString(R.string.original_price_colon) + searchResult.getOriginalPrice();
        originalPriceText.setText(originalPrice);

        styleIdText = (TextView)findViewById(R.id.detailStyleId);
        String styleId = getResources().getString(R.string.style_id_colon) + searchResult.getStyleId();
        styleIdText.setText(styleId);

        colorIdText = (TextView)findViewById(R.id.detailColorId);
        String colorId = getResources().getString(R.string.color_id_colon) + searchResult.getColorId();
        colorIdText.setText(colorId);

        percentOffText = (TextView)findViewById(R.id.detailPercentOff);
        String percentOff = getResources().getString(R.string.percent_off_colon) + searchResult.getPercentOff();
        percentOffText.setText(percentOff);

        productUrlText = (TextView)findViewById(R.id.detailProductUrl);
        String productUrl = getResources().getString(R.string.product_url_colon) + searchResult.getProductUrl();
        productUrlText.setText(productUrl);

        productNameText = (TextView)findViewById(R.id.detailProductName);
        String productName = getResources().getString(R.string.name_colon) + searchResult.getProductName();
        productNameText.setText(productName);

        mHandler = new FoundHandler(Looper.getMainLooper());
        notFoundHandler = new DidNotFindHandler(Looper.getMainLooper());
    }

    public static Context getContext(){
        return context;
    }

    public void shareButtonPressed(View v){
    }


    public void doneButtonPressed(View v){
        finish();
    }

    public void compareButtonPressed(View v){
        String apiSite = "https://api.6pm.com/Search?term=";
        String pmKey = "&key=524f01b7e2906210f7bb61dcbe1bfea26eb722eb";
        String url = apiSite + searchTerm + pmKey;

        new ReadURL6pm().execute(url, searchResult.getProductId(), searchResult.getPrice());

    }
    // AsyncTask for reading URL from 6pm.com in another thread
    // Pass in: URL, productId, price
    public class ReadURL6pm extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... args) {
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
                        Message m = ItemActivity.mHandler.obtainMessage();
                        m.obj = price + "&" + price6pm;
                        m.sendToTarget();
                        return null;
                    }
                }
            }
            Message m1 = ItemActivity.notFoundHandler.obtainMessage();
            m1.sendToTarget();

            return null;
        }
    }

    public static class FoundHandler extends Handler {
        public FoundHandler(Looper l){
            super(l);
        }
        @Override
        public void handleMessage(Message message){
            String str = (String)message.obj;
            int index = str.indexOf("&");
            String price = str.substring(0, index);
            String price6pm = str.substring(index + 1);
            String m = "Found the same product with a lower price on 6pm.com\nZappos.com price: "+
                    price + "\n6pm.com price: " + price6pm;
            AlertDialog.Builder builder = new AlertDialog.Builder(ItemActivity.getContext());
            builder.setTitle("Match with lower price found");
            builder.setMessage(m);
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public static class DidNotFindHandler extends Handler {
        public DidNotFindHandler(Looper l){
            super(l);
        }
        @Override
        public void handleMessage(Message message){
            AlertDialog.Builder builder = new AlertDialog.Builder(ItemActivity.getContext());
            builder.setTitle("Nothing Found");
            builder.setMessage("6pm.com either did not have this product or is not cheaper");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
