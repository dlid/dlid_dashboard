package se.dlid.dashboard_share;

import org.json.JSONObject;

/**
 * Created by yz8885 on 2017-05-08.
 */

public class JSendResponse {

    private JSONObject _data;
    private JSendResponseStatus _status;
    private String _message;

    public JSendResponse(JSendResponseStatus status, String message, JSONObject data) {
        _status = status;
        _data = data;
        _message = message;
    };

    public JSendResponse(JSendResponseStatus status, String message) {
        _status = status;
        _message = message;
    };

    public JSendResponse(JSendResponseStatus status, JSONObject data) {
        _status = status;
        _data  = data;
    };

    public JSONObject getData() {
        return _data;
    }

    public JSendResponseStatus getStatus() {
        return _status;
    }

    public String getMessage() {
        return _message;
    }

    public enum JSendResponseStatus {
        Success,
        Fail,
        Error
    }

}
