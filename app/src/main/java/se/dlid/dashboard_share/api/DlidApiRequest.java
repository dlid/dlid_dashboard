package se.dlid.dashboard_share.api;

import android.app.Service;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import se.dlid.dashboard_share.BuildConfig;
import se.dlid.dashboard_share.Constants;

/**
 * Created by yz8885 on 2017-05-09.
 */

class DlidApiRequest {

    private String mContentType = "";
    private String mToken;
    private String mServicePath;
    private String mPostData;
    private String mRequestMethod = "GET";
    private DlidApiResponse mResponse = null;
    private long executionTimeMs = -1;

    /**
     * Create a GET request toward the specified service
     * @param ServicePath The service path without leading slashes
     */
    public DlidApiRequest(String ServicePath) {
        mServicePath = ServicePath;
    }

    public DlidApiRequest(String ServicePath, String PostData) {

        mServicePath = ServicePath;
        if ( PostData.length() > 0 && (PostData.startsWith("[") || PostData.endsWith("{")) && PostData.endsWith(PostData.substring(0,1))  ) {
            // JSON is it?
            mContentType = "application/json";
        } else {
            mContentType = "application/x-www-form-urlencoded; charset=UTF-8";
        }

        mPostData = PostData;
        mRequestMethod = "POST";

    }

    public boolean execute() {
        try {
            long startTime = System.nanoTime();

            HttpURLConnection connection = createConnection();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                String responseString = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    responseString += line;
                }

                DlidApiResponse response = DlidApiResponse.Parse(responseString);
                mResponse = response;
                executionTimeMs = TimeUnit.NANOSECONDS.toMillis( System.nanoTime() - startTime );

                if (BuildConfig.DEBUG)
                    Log.i(Constants.LOG, this.mServicePath + " took " + executionTimeMs);

                if (mResponse!= null) {
                    return true;
                }
            } else {
                executionTimeMs = TimeUnit.NANOSECONDS.toMillis( System.nanoTime() - startTime );
                return false;
            }
        } catch (IOException e) {
            mException = e;
            return false;
        }
        return false;
    }

    public DlidApiResponse getResponse() {
        return mResponse;
    }

    private Exception mException;
    public Exception getException() {return mException;}


    public void setToken(String token) {mToken = token;}

    private HttpURLConnection createConnection() throws IOException {

        URL url = new URL("https://services.dlid.se/" + mServicePath);
        //url = new URL("http://127.0.0.1:1010/services/" + path);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String language = Locale.getDefault().toString();
        String userAgent = getAppUserAgent();

        String contentType = mContentType;

        if (mRequestMethod == "POST" && !TextUtils.isEmpty(mContentType) ) {
            mContentType= "application/x-www-form-urlencoded; charset=UTF-8";
        }

        if (language.indexOf('_') != -1)
            language = language.substring(0, language.indexOf('_'));

        // urlConnection.setReadTimeout(60);
        // urlConnection.setConnectTimeout(60);

        urlConnection.setRequestMethod(mRequestMethod);
        urlConnection.setRequestProperty("User-Agent", userAgent);
        urlConnection.setRequestProperty("X-Dlid-Lang", language);

        if (!TextUtils.isEmpty(mContentType )) urlConnection.setRequestProperty("Content-Type", mContentType);
        if (!TextUtils.isEmpty(mToken))  urlConnection.setRequestProperty("X-Dlid-Token", mToken);

        // Send form data if provided, and if not a GET request
        if (mRequestMethod != "GET" && !TextUtils.isEmpty(mPostData)) {
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            byte[] outputBytes = mPostData.getBytes("UTF-8");
            OutputStream os = urlConnection.getOutputStream();
            os.write(outputBytes);
        }

        return urlConnection;
    }

    public static String getAppUserAgent() {

        StringBuilder builder = new StringBuilder();
        builder.append("Android").append(Build.VERSION.RELEASE);

        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                builder.append("/").append(fieldName);
                builder.append(" (SDK ").append(fieldValue).append(")");
            }
        }
        return builder.toString();
    }




}
