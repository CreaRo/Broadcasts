package rish.crearo.gcmtester.Utils;

/**
 * Created by rish on 30/9/15.
 */
public class Constants {

    public static String API_KEY = "";
    public static String SENDER_ID = "235204757885";

    public static final String PREF_SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String PREF_REGISTRATION_COMPLETE = "registrationComplete";

    public static final String BASE_URL = "http://192.168.150.1:8080";
    public static final String BASE_URL_REGISTER = "http://192.168.150.1:8080/register";
    public static final String BASE_URL_NEW_BROADCAST = "http://192.168.150.1:8080/broadcasts/create";
    public static final String BASE_URL_NEW_GROUP = "http://192.168.150.1:8080/groups/create";
    public static final String BASE_URL_GROUPS = "http://192.168.150.1:8080/groups/all";
    public static final String BASE_URL_GROUPS_BROADCASTABLE = "http://192.168.150.1:8080/groups/adminof/"; // use with <username> in front

    public static final String TAG_GROUPS = "results";

    public static final String BC_TITLE = "b_title";
    public static final String BC_CONTENT = "b_content";
    public static final String BC_SENDER = "b_sender";
    public static final String BC_FOR_GROUP = "b_for_group";
    public static final String BC_DATE_POST = "b_date_post";
    public static final String BC_DATE_EVENT = "b_date_event";
    public static final String BC_LOCATION = "b_location";

    public static final String LOCALBR_REFRESH_ADAPTERS = "LOCALBR_REFRESH_ADAPTERS";

}