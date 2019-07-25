package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TvInfo  implements Parcelable {

    public static final Creator<TvInfo> CREATOR = new Creator<TvInfo>() {
        @Override
        public TvInfo createFromParcel(Parcel in) {
            return new TvInfo(in);
        }

        @Override
        public TvInfo[] newArray(int size) {
            return new TvInfo[size];
        }
    };

    private Integer _id;
    @SerializedName("emac")
    @Expose
    private String emac;
    @SerializedName("boardName")
    @Expose
    private String boardName;
    @SerializedName("panelName")
    @Expose
    private String panelName;

    public TvInfo(String emac, String boardName, String panelName) {
        this.emac = emac;
        this.boardName = boardName;
        this.panelName = panelName;
    }

    protected TvInfo(Parcel in) {
        emac = in.readString();
        boardName = in.readString();
        panelName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(emac);
        dest.writeString(boardName);
        dest.writeString(panelName);
    }



    public void set_id(int _id) {
        this._id = _id;
    }

    public Integer get_id() {
        return _id;
    }

    public String getEmac() {
        return emac;
    }

    public void setEmac(String emac) {
        this.emac = emac;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getPanelName() {
        return panelName;
    }

    public void setPanelName(String panelName) {
        this.panelName = panelName;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
