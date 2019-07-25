package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Rows {

    @SerializedName("rowHeader")
    @Expose
    private String rowHeader;
    @SerializedName("rowItems")
    @Expose
    private ArrayList<MovieTile> rowItems;

    public Rows() {
    }

    /**
     * @param rowItems
     * @param rowHeader
     */
    public Rows(String rowHeader, ArrayList<MovieTile> rowItems) {
        super();
        this.rowHeader = rowHeader;
        this.rowItems = rowItems;
    }

    public String getRowHeader() {
        return rowHeader;
    }

    public void setRowHeader(String rowHeader) {
        this.rowHeader = rowHeader;
    }

    public ArrayList<MovieTile> getRowItems() {
        return rowItems;
    }

    public void setRowItems(ArrayList<MovieTile> rowItems) {
        this.rowItems = rowItems;
    }


}
