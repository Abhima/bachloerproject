package com.example.rteav1;

public class Config {

    public static final String BASEURL = "http://192.168.1.101:80/rtea/";
   // public static final String BASEURL = "http://monikashrestha.net23.net:80/rtea/";
    public static final String LOGIN_URL = BASEURL + "loginUser.php";
    public static final String REGISTER_URL = BASEURL + "registerUser.php";
    public static final String CHECK_USER_IN_DB_URL = BASEURL + "checkUser.php";
    public static final String TRIGGER_URL = BASEURL + "victim.php";

    //Key for Sharedpreferences
    //This would be the name of the shared preference .xml file
    public static final String SHARED_PREF_FILENAME = "rteaSharedPreference";

    //This would be used to store the email of the current logged in user.
    public static final String SP_mobilenumber = "sp_mobilenumber";
    public static final String SP_fullname = "sp_fullname";
    public static final String SP_eyecolor = "sp_eyecolor";
    public static final String SP_haircolor = "sp_haircolor";
    public static final String SP_height = "sp_height";
    public static final String SP_age = "sp_age";
    public static final String SP_gender = "sp_gender";
    public static final String SP_image = "sp_image";
    public static final String SP_email = "sp_email";

    //This stores the boolean in sharedpreference to track user
    //is logged in or not.
    public static final String LOGGEDIN_SHARED_PREF = "isLoggedIn";

    public static final String SP_PinCode = "sp_pin";

    public static final String SP_SettingChanged = "sp_setting";

}
