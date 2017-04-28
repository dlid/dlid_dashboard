package se.dlid.dashboard_share;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by yz8885 on 2017-04-28.
 */

class DlidApi {

    private Context _context;

    public DlidApi(Context context) {
        _context = context;
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

    public DlidApiLoginResult Login(String user, String password) {

        String resultToDisplay = "";
        DlidApiLoginResult loginResult = new DlidApiLoginResult();

        InputStream in = null;
        try {
            URL url = new URL("https://services.dlid.se/user/login");

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

           // urlConnection.setReadTimeout(60);
          //  urlConnection.setConnectTimeout(60);
            String language = Locale.getDefault().toString();
            if (language.indexOf('_') != -1)
                language = language.substring(0, language.indexOf('_'));

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            urlConnection.setRequestProperty("X-Dlid-Lang", language);

            // Create the data we'd like to post
            String str = "username=" + Uri.encode(Scramble(user));
            str += "&password=" + Uri.encode(Scramble(password));
            str += "&device_id=" + Uri.encode(Installation.id(_context));
            str += "&device_name=" + Uri.encode(Installation.getDeviceName());

            byte[] outputBytes = str.getBytes("UTF-8");

            OutputStream os = urlConnection.getOutputStream();
            os.write(outputBytes);

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                //Log.e("14 - HTTP_OK");

                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    resultToDisplay += line;
                }

                JSONObject jObject = new JSONObject(resultToDisplay);
                String jSendStatus = jObject.getString("status");

                if (jSendStatus.equalsIgnoreCase("success")) {
                    loginResult.setLoggedIn(true);
                } else {
                    String jSendMessage = jObject.getString("message");
                    loginResult.setMessage(jSendMessage);
                }

                System.out.println("status: " + jSendStatus);
            } else {
                //Log.e("14 - False - HTTP_OK");
                resultToDisplay = "";
                loginResult.setMessage("Service returned " + responseCode);
            }

        } catch (Exception e) {
            loginResult.setException(e);
            System.out.println(e.getMessage());
        }
/*
        try {
            resultToDisplay = IOUtils.toString(in, "UTF-8");
            //to [convert][1] byte stream to a string
        }
        catch (IOException e) {
            loginResult.setException(e);
            e.printStackTrace();
        }
*/
        return loginResult;
    }

}

class DlidApiLoginResult {

    Boolean _loggedIn = false;
    String _message;
    Exception _exception;

    public void setMessage(String message) {
        _message = message;
    }

    public String getMessage() {
        return _message;
    }

    public void setException(Exception e) {
        _exception = e;
    }

    public Exception getException() {
        return _exception;
    }


    public void setLoggedIn(Boolean loggedIn) {
        _loggedIn = loggedIn;
    }

    public Boolean getLoggedIn() {
        return _loggedIn;
    }
}