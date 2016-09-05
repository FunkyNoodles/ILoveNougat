package io.github.funkynoodles.zapposcompareprice;

import java.util.ArrayList;

/**
 * Created by Louis on 9/4/2016.
 * Data read from the REST request is stored in here
 */
public class SearchQuery {
    private String originalTerm;
    private int currentResultCount;
    private int totalResultCount;
    private String term;
    private ArrayList<SearchResult> results;
    private int statusCode;

    public SearchResult getResult(int i){
        return results.get(i);
    }

    public int getCurrentResultCount(){
        return results.size();
    }

    public void clear(){
        results.clear();
    }

    public String getOriginalTerm(){
        return originalTerm;
    }
}
