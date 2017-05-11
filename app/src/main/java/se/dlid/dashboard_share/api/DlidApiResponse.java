package se.dlid.dashboard_share.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;

import se.dlid.dashboard_share.BuildConfig;
import se.dlid.dashboard_share.Constants;
import se.dlid.dashboard_share.JSendResponse;

/**
 * Created by yz8885 on 2017-05-09.
 */

public class DlidApiResponse {

    public enum JSendStatus {
        Success,
        Error,
        Fail
    }

    private JSendStatus mStatus;
    private String mMessage;
    private JSONObject mData;

    private DlidApiResponse(String rawResponse, JSendStatus status, JSONObject data) {
        mStatus = status;
        mData = data;
        mRawResponse = rawResponse;
    }

    private DlidApiResponse(String rawResponse, JSendStatus status, String message, JSONObject data) {
        mStatus = status;
        mData = data;
        mMessage = message;
        mRawResponse = rawResponse;
    }

    private DlidApiResponse(String rawResponse, JSendStatus status, String message) {
        mStatus = status;
        mMessage = message;
        mRawResponse = rawResponse;
    }

    private DlidApiResponse(String rawResponse, JSendStatus status) {
        mStatus = status;
        mRawResponse = rawResponse;
    }

     @Override
    public String toString() {
        return mRawResponse;
    }

    private String mRawResponse;

    public JSendStatus getStatus() {return mStatus;}
    public String getMessage() {return mMessage;}
    public JSONObject getData() {return mData;}

    public static DlidApiResponse Parse(String jsonResponseString) {

        JSONObject jObject = null ;
        String jSendStatus = "";
        String jSendMessage = "";
        JSONObject jSendData = null;

        try {
            jObject = new JSONObject(jsonResponseString);

            if (jObject.has("status")) {
                jSendStatus = jObject.getString("status");
            }
            if (jObject.has("message")) {
                jSendMessage = jObject.getString("message");
            }
            if (jObject.has("data")) {
                jSendData = jObject.getJSONObject("data");
            }
        } catch (JSONException e) {
            Log.e(Constants.LOG, "Error parsing JSON response " + jsonResponseString);
            Log.e(Constants.LOG, e.getMessage() + " " + e.getStackTrace());
            return new DlidApiResponse(jsonResponseString, JSendStatus.Error, "Service did not return valid JSON Data");
        }

        if (BuildConfig.DEBUG) {
            Log.i(Constants.LOG, "JSendResponse received with " + jSendStatus );
        }

        if (jSendStatus.compareTo("success") == 0) {
            return new DlidApiResponse(jsonResponseString, JSendStatus.Success, jSendMessage, jSendData);
        } else if (jSendStatus.compareTo("fail") == 0) {
            return new DlidApiResponse(jsonResponseString, JSendStatus.Fail, jSendMessage, jSendData);
        } else if (jSendStatus.compareTo("error") == 0) {
            return new DlidApiResponse(jsonResponseString, JSendStatus.Error, jSendMessage, jSendData);
        } else {
            return new DlidApiResponse(jsonResponseString, JSendStatus.Error, "Unknown jSend Status from service: " + jSendStatus);
        }

    }

}
