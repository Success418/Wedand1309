package co.za.wedwise.Constants;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by za on 10/23/2018.
 */

public class Constants {

    public static String BASEURL = "http://wedwise.co.za/admin";

    public static String CONNECTION = BASEURL + "/api.php?";
    public static String SIGNUP = CONNECTION + "signup";
    public static String REGISTER = CONNECTION + "register";
    public static String LOGIN = CONNECTION + "login";
    public static String SETTINGAPP = CONNECTION + "settingapp";
    public static String EDITPROFILE = CONNECTION + "editprofile";
    public static String USERDATA = CONNECTION + "userdata";
    public static String UPLOADIMAGES = CONNECTION + "uploadImages";
    public static String DELETEIMAGES = CONNECTION + "deleteImages";
    public static String FEATURED = CONNECTION + "featuredproperty";
    public static String CATEGORY = CONNECTION + "category";
    public static String CITY = CONNECTION + "city";
    public static String POPULAR = CONNECTION + "popularproperty";
    public static String ALLPOPULAR = CONNECTION + "allpopularproperty";
    public static String LATEST = CONNECTION + "latestproperty";
    public static String PROPPERTYDETAIL = CONNECTION + "propid=";
    public static String VENDOR_REGISTRATION = CONNECTION + "vendorRegistration";
    public static String LIKE_UNLIKE = CONNECTION + "like";
    public static String BYCAT = CONNECTION + "cid=";
    public static String BYCITY = CONNECTION + "cityid=";
    public static String SEARCH = CONNECTION + "searchtext=";
    public static String FILTERPRICE = CONNECTION + "filterprice=";
    public static String DISTANCE = CONNECTION + "distance&user_lat=";
    public static String ALLPROPERTY = CONNECTION + "allproperty";
    public static String MYPROPERTY = CONNECTION + "myproperty=";
    public static String DELETEPROPERTY = CONNECTION + "deleteproperty=";
    public static String ADDPROPERTY = CONNECTION + "addproperty";
    public static String RATING = CONNECTION + "proprating=";
    public static String FAVOURITE = CONNECTION + "favourite";
    public static String RATING_LIST = CONNECTION + "ratinglist";
    public static String RATING_REPORT = CONNECTION + "reportabuse";
    public static String GET_USER_RATING = CONNECTION + "userrating";

    public static String FILTERCATID = "";
    public static String FILTERCITYID = "";

    public static String pref_name = "pref_name";
    public static String f_name = "f_name";
    public static String l_name = "l_name";
    public static String uid = "uid";
    public static String u_pic = "u_pic";
    public static String Lat = "Lat";
    public static String Lon = "Lon";
    public static String device_token = "device_token";

    public static String versionname = "1.0";

    public static int permission_camera_code = 786;
    public static int permission_write_data = 788;
    public static int permission_Read_data = 789;
    public static int permission_Recording_audio = 790;
    public static int Select_image_from_gallry_code = 3;
    public static int successmsg;

    public static String gif_firstpart = "https://media.giphy.com/media/";
    public static String gif_secondpart = "/100w.gif";

    public static String gif_firstpart_chat = "https://media.giphy.com/media/";
    public static String gif_secondpart_chat = "/200w.gif";

    public static SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);


    public static final String FCM_KEY = "AIzaSyDxyT0Yi8ZHX2amUD9YaqxK2Sa6oO27KfQ";
    public static final LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(-7.216001, 0), // southwest
            new LatLng(0, 107.903316)); // northeast


    public static String latitude = "latitude";
    public static String longitude = "longitude";


}
