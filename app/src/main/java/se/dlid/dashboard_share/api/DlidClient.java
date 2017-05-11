package se.dlid.dashboard_share.api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import se.dlid.dashboard_share.BuildConfig;
import se.dlid.dashboard_share.Constants;
import se.dlid.dashboard_share.JSendResponse;

/**
 * Created by yz8885 on 2017-04-28.
 */

public class DlidClient {

    public DlidClient() {
    }

    private String Scramble(String val) {

        String scrambled = "";

        for (int i = 0; i < val.length(); i++) {
            char c = val.charAt(i);
            int cCode = (int)c;
            int sCode = c ^ 123;
            scrambled += (char)sCode;
        }
        return scrambled;
    }


    public DlidApiLoginResult VerifyLogin(String token) {
        return null;
    }

    public void AddLink(String url, String title) {

    }

    public void TestToken() throws Exception {
        try {
            DlidApiRequest req = new DlidApiRequest("user/test");

            if(req.execute()) {
                DlidApiResponse r = req.getResponse();

                String resp = r.toString();

            } else {
                Log.e(Constants.LOG, "An exception occured when attempting login " + req.getException().getMessage());
                throw req.getException();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String token;

    /**
     * Set the API Device Token to be used for requests
     * @param token
     */
    public void setToken(String token) {this.token  = token;}

    /**
     * Get the API Device Token for the requests
     * @return
     */

    public DlidApiLoginResult Login(String user, String password, String deviceId, String deviceName) throws Exception {

        // Build the data to post
        String str = "username=" + Uri.encode(Scramble(user));
        str += "&password=" + Uri.encode(Scramble(password));
        str += "&include=" + Uri.encode(Scramble("permissions"));
        str += "&device_id=" + Uri.encode(Scramble(deviceId));
        str += "&device_name=" + Uri.encode(Scramble(deviceName));
        str += "&app_id=" + Uri.encode(Scramble(Constants.APP_ID));

        // Create and execute login request
        DlidApiRequest req = new DlidApiRequest("user/login", str);

        if (req.execute()) {
            DlidApiResponse re = req.getResponse();
            return new DlidApiLoginResult(user, req.getResponse());
        } else {
            Log.e(Constants.LOG, "An exception occured when attempting login " + req.getException().getMessage());
            throw req.getException();
        }
    }

}

