package io.github.funkynoodles.zapposcompareprice;

/**
 * Created by Louis on 9/4/2016.
 * Class for storing each individual result (product)
 */
public class SearchResult {
    private String brandName;
    private String thumbnailImageUrl;
    private String productId;
    private String originalPrice;
    private int styleId;
    private int colorId;
    private String price;
    private String percentOff;
    private String productUrl;
    private String productName;

    public String getBrandName(){
        return this.brandName;
    }

    public String getThumbnailImageUrl(){
        return this.thumbnailImageUrl;
    }

    public String getPrice(){
        return this.price;
    }

    public String getProductName(){
        return this.productName;
    }
    public String getProductUrl(){
        return this.productUrl;
    }

    public String getProductId(){
        return productId;
    }
}
