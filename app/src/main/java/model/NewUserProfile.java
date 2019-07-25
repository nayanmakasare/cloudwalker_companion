package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewUserProfile implements Parcelable {

    public static final Creator<NewUserProfile> CREATOR = new Creator<NewUserProfile>() {
        @Override
        public NewUserProfile createFromParcel(Parcel in) {
            return new NewUserProfile(in);
        }

        @Override
        public NewUserProfile[] newArray(int size) {
            return new NewUserProfile[size];
        }
    };

    @SerializedName("cwId")
    @Expose
    private String cwId;
    @SerializedName("userName")
    @Expose
    private String userName;

    public NewUserProfile(String cwId, String userName, String dob, String mobileNumber, String gender, String imageUrl, String email, List<String> genre, List<String> launguage, List<String> contentType, List<TvInfo> linkedDevices) {
        this.cwId = cwId;
        this.userName = userName;
        this.dob = dob;
        this.mobileNumber = mobileNumber;
        this.gender = gender;
        this.imageUrl = imageUrl;
        this.email = email;
        this.genre = genre;
        this.launguage = launguage;
        this.contentType = contentType;
        this.linkedDevices = linkedDevices;
    }

    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("genre")
    @Expose
    private List<String> genre = null;
    @SerializedName("languages")
    @Expose
    private List<String> launguage = null;
    @SerializedName("contentType")
    @Expose
    private List<String> contentType = null;
    @SerializedName("linkedDevices")
    @Expose
    private List<TvInfo> linkedDevices = null;

    public NewUserProfile(){

    }

    protected NewUserProfile(Parcel in) {
        cwId = in.readString();
        userName = in.readString();
        dob = in.readString();
        mobileNumber = in.readString();
        gender = in.readString();
        imageUrl = in.readString();
        email = in.readString();
        genre = in.createStringArrayList();
        launguage = in.createStringArrayList();
        contentType = in.createStringArrayList();
        linkedDevices = in.createTypedArrayList(TvInfo.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cwId);
        dest.writeString(userName);
        dest.writeString(dob);
        dest.writeString(mobileNumber);
        dest.writeString(gender);
        dest.writeString(imageUrl);
        dest.writeString(email);
        dest.writeStringList(genre);
        dest.writeStringList(launguage);
        dest.writeStringList(contentType);
        dest.writeTypedList(linkedDevices);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCwId() {
        return cwId;
    }

    public void setCwId(String cwId) {
        this.cwId = cwId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public List<String> getLaunguage() {
        return launguage;
    }

    public void setLaunguage(List<String> launguage) {
        this.launguage = launguage;
    }

    public List<String> getContentType() {
        return contentType;
    }

    public void setContentType(List<String> contentType) {
        this.contentType = contentType;
    }

    public List<TvInfo> getLinkedDevices() {
        return linkedDevices;
    }

    public void setLinkedDevices(List<TvInfo> linkedDevices) {
        this.linkedDevices = linkedDevices;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}