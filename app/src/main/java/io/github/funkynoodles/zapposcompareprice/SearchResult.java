package io.github.funkynoodles.zapposcompareprice;

import java.io.Serializable;

/**
 * Created by Louis on 9/4/2016.
 * Class for storing each individual result (product)
 */
public class SearchResult implements Serializable{
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

    public SearchResult(SearchResult s){
        brandName = s.getBrandName();
        thumbnailImageUrl = s.getThumbnailImageUrl();
        productId = s.getProductId();
        originalPrice = s.getOriginalPrice();
        styleId = s.getStyleId();
        colorId = s.getColorId();
        price = s.getPrice();
        percentOff = s.getPercentOff();
        productUrl = s.getProductUrl();
        productName = s.getProductName();
    }

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

    public String getOriginalPrice(){
        return this.originalPrice;
    }

    public int getStyleId(){
        return styleId;
    }

    public int getColorId(){
        return colorId;
    }

    public String getPercentOff(){
        return percentOff;
    }
}
