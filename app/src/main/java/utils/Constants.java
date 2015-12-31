package utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bharat on 12/7/15.
 */
public class Constants {


/*    public static final String REG_ID="regid";
    public static final String APP_VERSION="app_version";
    public static final String SENDER_ID="188284807000";

    */
    /**
     * Async client connection timeout time
     *//*
    public static final String EmailId="EmailId";
    public static final int connection_timeout = 40 * 1000;
    public static final String PreferenceData="PreferenceData";
    public static final String UserId ="user_id";*/

    // ************** Login *******************//
    public static String USER_ID;
    public static String ROLE_ID;

    //*************** SignUp *****************//
    public static String ROLE_ID_TO_SEND ;

    //************** Profile *****************//
    public static String FIRSTNAME;
    public static String LASTNAME;
    public static String USERNAME;
    public static String PHONE;
    public static String COMPANY_NAME;
    public static String MC_NO;
    public static String ADDRESS;
    public static String CITY;
    public static String STATE;
    public static String STATE_ID;
    public static String ZIPCODE;
    public static String COUNTRY_ID;
    public static String COUNTRY_NAME;
    public static String FAX;
    public static ArrayList<HashMap<String , String>> countryList = new ArrayList<HashMap<String , String>>();
    public static ArrayList<HashMap<String , String>> stateList = new ArrayList<HashMap<String , String>>();

    public static String NO_INTERNET = "No internet connection.";
    public static int connection_timeout = 40 * 1000;
    public static String BASE_URL = "http://phphosting.osvin.net/JSKT/API/";
    public static String LOGIN_URL = BASE_URL+"SignIn.php";
    public static String SIGNUP_URL = BASE_URL+"SignUp.php";
    public static String GET_PROFILE_URL = BASE_URL+"ViewProfile.php";
    public static String UPDATE_PROFILE_URL = BASE_URL +"UpdateProfile.php";
    public static String COUNTRY_URL = BASE_URL +"CountryCodeList.php";
    public static String STATE_URL = BASE_URL +"StateCodeList.php";
    public static String FORGOT_PASS_URL = BASE_URL +"ForgotPassword.php";
    public static String WORKORDER_LIST_API = BASE_URL +"WorkOrderList.php";
    public static String UPLOAD_POD_IMAGE = BASE_URL +"AddPODImage.php";
    public static String CONFIRM_APPT = BASE_URL +"Confirm_Appt.php";
    public static String CUST_SEARCH_RATE = BASE_URL+"CustomerSearchRates.php";
    public static String POD_IMAGES_LIST = BASE_URL+"ListPodImages.php";

    // **************** work order list appt detail *********************//

    public static String COMPANY;
    public static String TELEPHONE ;
    public static String CONTACT;
    public static String DATE;
    public static String TIME ;
    public static String WORKORDER_ID;
    public static String EMAIL;
    public static String CONFIRM_STATUS;


    //***************** Search rates **********************************//

    public static double FromLat;
    public static double FromLng;
    public static double ToLat;
    public static double ToLng;

    //********************** work order list ************************//

    public static String WORK_ORDER_ID ;

}
