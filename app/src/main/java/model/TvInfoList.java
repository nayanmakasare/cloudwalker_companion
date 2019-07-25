package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TvInfoList {

    @SerializedName("googleId")
    @Expose
    private String googleId;
    @SerializedName("linkedDevices")
    @Expose
    private List<TvInfo> linkedDevices = null;

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public List<TvInfo> getLinkedDevices() {
        return linkedDevices;
    }

    public void setLinkedDevices(List<TvInfo> linkedDevices) {
        this.linkedDevices = linkedDevices;
    }

}
