package utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class AppUtils
{

    public static String loadFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }


    public static String getWifiMacAddress() {
        try {
            return loadFileAsString("/sys/class/net/wlan0/address")
                    .toUpperCase().substring(0, 17);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static ProfileObject getProfileObjFromResponseObj(SingleProfileResponseObject responseObject) {
//        ProfileObject profileObject = new ProfileObject();
//        profileObject.setName(responseObject.getName());
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        try {
//            Date date = simpleDateFormat.parse(responseObject.getDob());
//            profileObject.setDob(date.getTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        profileObject.setMobileNumber(responseObject.getMobileNumber());
//        profileObject.setGender(responseObject.getGender());
//        profileObject.setGenres(responseObject.getGenres());
//        profileObject.setLanguages(responseObject.getLanguages());
//        profileObject.setContentTypes(responseObject.getContentType());
//        profileObject.setFavourites(responseObject.getFavourites());
//        return profileObject;
//    }

    public static String loadJSONFromAsset(Context context) {
        String json ;
        try {
            InputStream is = context.getAssets().open("profile_config.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}

