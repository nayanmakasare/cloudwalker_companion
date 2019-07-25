package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Search {
    @SerializedName("appId")
    @Expose
    private String appId;
    @SerializedName("cde")
    @Expose
    private String cde;

    /**
     * No args constructor for use in serialization
     */
    public Search() {
    }

    /**
     * @param appId
     * @param cde
     */
    public Search(String appId, String cde) {
        super();
        this.appId = appId;
        this.cde = cde;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCde() {
        return cde;
    }

    public void setCde(String cde) {
        this.cde = cde;
    }

}

