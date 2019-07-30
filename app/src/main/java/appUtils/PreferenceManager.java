package appUtils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by cognoscis on 9/3/18.
 */

public class PreferenceManager {

    private static final String PREF_NAME = "cloudwalker_launcher";



    private static final String USER_NAME = "userName";
    private static final String USER_EMAIL = "userEmail";
    private static final String GOOGLE_ID = "googleId";
    private static final String TV_INFO = "tvInfo";
    private static final String IS_GOOGLE_SIGN_IN = "isGoogleSignIn";
    private static final String IS_CLOUDWALKER_SIGN_IN = "isCloudwalkerSignIn";
    private static final String ROUTING_KEY = "routingKey";
    private static final String PROFILE_IMAGE_URL = "profileImageUrl";
    private static final String PROFILE = "profile";
    private static final String PROFILES = "profiles";
    private static final String DOB = "dob";
    private static final String LANGUAGE = "language";
    private static final String TYPE = "type";
    private static final String GENRE = "genre";
    private static final String MOBILE_NUMBER = "mobileNumber";
    private static final String GENDER = "gender";
    private static final String RABBIT_SERVICE_ON = "rabbitServiceOn";
    private static final String NSD_SERVICE_ON = "nsdServiceOn";
    private static final String LINKED_NSD_DEVICES = "linkedNsdDevices";





    private static final String GOOGLE_SIGNIN_STATUS = "googleSignInStatus";
    private static final String CW_INTERMIDIATE_STATUS = "cwIntermideateStatus";
    private static final String CW_PREFRENCE_STATUS = "cwPreferenceStatus";



    private static final String NSD_HOST_ADDRESS = "nsdHostAddress";
    private static final String NSD_PORT = "nsdPort";



    private static final String CURRENT_NSD_SERVICE_CONNECTED = "currentNsdServiceConnected";





    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    /**
     * Private mode of shared preferences.
     */
    private int PRIVATE_MODE = 0;

    /**
     * Instantiates a new Preference manager.
     *
     * @param ctx the context of the application
     */
    public PreferenceManager(Context ctx) {
        mPreferences = ctx.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPreferences.edit();
    }

    public String getUserName() {
        return mPreferences.getString(USER_NAME, null);
    }

    public void setUserName(String userName) {
        mEditor.putString(USER_NAME, userName);
        mEditor.commit();
    }

    public void setCurrentNsdServiceConnected(String emac){
        mEditor.putString(CURRENT_NSD_SERVICE_CONNECTED, emac);
        mEditor.commit();
    }

    public String getCurrentNsdServiceConnected(){
        return mPreferences.getString(CURRENT_NSD_SERVICE_CONNECTED, "");
    }

    public Set<String> getLinkedNsdDevices()
    {
        return mPreferences.getStringSet(LINKED_NSD_DEVICES, null);
    }

    public void setLinkedNsdDevices(Set<String> linkedDevices)
    {
        mEditor.putStringSet(LINKED_NSD_DEVICES, linkedDevices);
        mEditor.commit();
    }


    public String getProfile() {
        return mPreferences.getString(PROFILE, null);
    }

    public void setProfile(String profile) {
        mEditor.putString(PROFILE, profile);
        mEditor.commit();
    }


    public void setGoogleSigninStatus(Boolean status) {
        mEditor.putBoolean(GOOGLE_SIGNIN_STATUS, status);
        mEditor.commit();
    }


    public Boolean getGoogleSignInStatus(){
        return mPreferences.getBoolean(GOOGLE_SIGNIN_STATUS, false);
    }



    public void setCwIntermidiateStatus(Boolean status) {
        mEditor.putBoolean(CW_INTERMIDIATE_STATUS, status);
        mEditor.commit();
    }


    public Boolean getCwIntermidiateStatus(){
        return mPreferences.getBoolean(CW_INTERMIDIATE_STATUS, false);
    }



    public void setCwPrefrenceStatus(Boolean status) {
        mEditor.putBoolean(CW_PREFRENCE_STATUS, status);
        mEditor.commit();
    }


    public Boolean getCwPrefrenceStatus(){
        return mPreferences.getBoolean(CW_PREFRENCE_STATUS, false);
    }


    public String getUserEmail() {
        return mPreferences.getString(USER_EMAIL, null);
    }

    public void setUserEmail(String userEmail) {
        mEditor.putString(USER_EMAIL, userEmail);
        mEditor.commit();
    }

    public String getProfileImageUrl() {
        return mPreferences.getString(PROFILE_IMAGE_URL, null);
    }

    public void setProfileImageUrl(String profileImageUrl) {
        mEditor.putString(PROFILE_IMAGE_URL, profileImageUrl);
        mEditor.commit();
    }

    public String getGoogleId() {
        return mPreferences.getString(GOOGLE_ID, "");
    }

    public void setGoogleId(String googleId) {
        mEditor.putString(GOOGLE_ID, googleId);
        mEditor.apply();
        mEditor.commit();
    }

    public void setTvInfo(String tvInfo){
        mEditor.putString(TV_INFO, tvInfo);
        mEditor.commit();
    }

    public void setNsdHostAddress(String hostAddress) {
        mEditor.putString(NSD_HOST_ADDRESS, hostAddress);
        mEditor.commit();
    }

    public String getNsdHostAddress(){
         return mPreferences.getString(NSD_HOST_ADDRESS, "");
    }

    public void setNsdPort(Integer port) {
        mEditor.putInt(NSD_PORT, port);
        mEditor.commit();
    }

    public int getNsdPort(){
        return mPreferences.getInt(NSD_PORT, 0);
    }

    public String getTvInfo(){
        return mPreferences.getString(TV_INFO, null);
    }


    public void setDob(String dob){
        mEditor.putString(DOB, dob);
        mEditor.commit();
    }

    public String getDob(){
        return mPreferences.getString(DOB, null);
    }


    public void setGender(String gender){
        mEditor.putString(GENDER, gender);
        mEditor.commit();
    }

    public String getGender(){
        return mPreferences.getString(GENDER, null);
    }


    public void setLanguage(String preference){
        mEditor.putString(LANGUAGE, preference);
        mEditor.commit();
    }

    public String getLanguage(){
        return mPreferences.getString(LANGUAGE, null);
    }


    public void setType(String type){
        mEditor.putString(TYPE, type);
        mEditor.commit();
    }

    public String getType(){
        return mPreferences.getString(TYPE, null);
    }



    public void setMobileNumber(String mobileNumber){
        mEditor.putString(MOBILE_NUMBER, mobileNumber);
        mEditor.commit();
    }

    public String getMobileNumber(){
        return mPreferences.getString(MOBILE_NUMBER, null);
    }


    public void setGenre(String genre){
        mEditor.putString(GENRE, genre);
        mEditor.commit();
    }

    public String getGenre(){
        return mPreferences.getString(GENRE, null);
    }


    public void setIsGoogleSignIn(Boolean isGoogleSignIn){
        mEditor.putBoolean(IS_GOOGLE_SIGN_IN, isGoogleSignIn);
        mEditor.commit();
    }

    public Boolean isGoogleSignIn(){
        return mPreferences.getBoolean(IS_GOOGLE_SIGN_IN, false);
    }

    public void setRabbitServiceOn(Boolean rabbitServiceOn){
        mEditor.putBoolean(RABBIT_SERVICE_ON, rabbitServiceOn);
        mEditor.commit();
    }

    public Boolean getRabbitServiceOn(){
        return mPreferences.getBoolean(RABBIT_SERVICE_ON, false);
    }

    public void setNsdServiceOn(Boolean nsdServiceOn){
        mEditor.putBoolean(NSD_SERVICE_ON, nsdServiceOn);
        mEditor.commit();
    }

    public Boolean getNsdServiceOn(){
        return mPreferences.getBoolean(NSD_SERVICE_ON, false);
    }

    public void setIsCloudwalkerSignIn(Boolean isCloudwalkerSignIn){
        mEditor.putBoolean(IS_CLOUDWALKER_SIGN_IN, isCloudwalkerSignIn);
        mEditor.commit();
    }

    public Boolean isCloudwalkerSigIn(){
        return mPreferences.getBoolean(IS_CLOUDWALKER_SIGN_IN, false);
    }

    public void setRoutingKey(Set<String> routingKeySet){
        mEditor.putStringSet(ROUTING_KEY, routingKeySet);
        mEditor.commit();
    }

    public Set<String> getRoutingKey(){
        return mPreferences.getStringSet(ROUTING_KEY, null);
    }

    public void setProfiles(Set<String> profile){
        mEditor.putStringSet(PROFILES, profile);
        mEditor.commit();
    }

    public Set<String> getProfiles(){
        return mPreferences.getStringSet(PROFILES, null);
    }

}
