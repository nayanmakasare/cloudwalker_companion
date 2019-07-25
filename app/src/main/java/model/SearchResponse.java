package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class SearchResponse {

    public SearchResponse() {
    }


    @SerializedName("rows")
    @Expose
    private Rows rows;

    public SearchResponse(Rows rows) {
        super();
        this.rows = rows;
    }

    public Rows getRows() {
        return rows;
    }

    public void setRows(Rows rows) {
        this.rows = rows;
    }


}
