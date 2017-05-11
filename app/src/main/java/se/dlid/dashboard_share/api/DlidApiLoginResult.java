package se.dlid.dashboard_share.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import se.dlid.dashboard_share.BuildConfig;
import se.dlid.dashboard_share.Constants;

/**
 * Created by yz8885 on 2017-05-09.
 */

public class DlidApiLoginResult {

    Boolean _loggedIn = false;
    String _message;
    Exception _exception;
    String _token;
    List<String> _permissions;
    String emailAddress;

    public DlidApiLoginResult(String email, DlidApiResponse response) {
        this.emailAddress = email;

        if (response.getStatus() == DlidApiResponse.JSendStatus.Success) {
            JSONObject dataObject = response.getData();
            if (dataObject != null && dataObject.has("device-token")) {
                if (dataObject.has("permissions")) {
                    List<String> permissions = new ArrayList<String>();
                    JSONArray parr = null;
                    try {
                        parr = dataObject.getJSONArray("permissions");
                        for (int i = 0; i < parr.length(); i++) {
                            permissions.add(parr.getString(i).trim());
                        }
                    } catch (JSONException e) {

                    }
                    setPermissions(permissions);
                    if (dataObject.has("device-token")) {
                        try {
                            setToken(dataObject.getString("device-token"));
                            setLoggedIn(true);
                        } catch (JSONException ex) {
                                setMessage("Could not read token from service call");
                        }
                    } else {
                        setMessage("No token was returned from service call");
                    }
                    setMessage(response.getMessage());

                    if (BuildConfig.DEBUG) {
                        Log.i(Constants.LOG, "Permissions: " + getPermissions().toString());
                        Log.i(Constants.LOG, "Token: " + getToken());
                    }
                }
            }
        } else if (response.getStatus() == DlidApiResponse.JSendStatus.Fail) {
            Log.e(Constants.LOG, "Login failed " + response.getMessage() );
            setMessage(response.getMessage());
        } else if (response.getStatus() == DlidApiResponse.JSendStatus.Error) {
            Log.e(Constants.LOG, "Error during login " + response.getMessage() );
            setMessage(response.getMessage());
        }
    }

    public void setMessage(String message) {
        _message = message;
    }

    public String getMessage() {
        return _message;
    }

    public String getEmail() {
        return emailAddress;
    }

    public List<String> getPermissions() {
        return _permissions;
    }

    public void setPermissions(List<String> permissions) {
        _permissions = permissions;
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

    public void setToken(String token) {
        _token = token;
    }

    public String getToken() {
        return _token;
    }


}
