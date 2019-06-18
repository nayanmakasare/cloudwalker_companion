package model;

public class ProfileInfoList
{
    private String propertyName;

    public int getPropertyImage() {
        return propertyImage;
    }

    public void setPropertyImage(int propertyImage) {
        this.propertyImage = propertyImage;
    }

    private int propertyImage;

    public ProfileInfoList(String propertyName, int propertyImage) {
        this.propertyName = propertyName;
        this.propertyImage = propertyImage;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}
