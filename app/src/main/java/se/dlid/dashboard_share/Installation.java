package se.dlid.dashboard_share;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.lang.reflect.Field;

/**
 * Created by David Lidström on 2017-04-27.
 */



class Installation {
    private static String sID = null;
    private static String sApiToken;
    private static final String INSTALLATION = "APP_INSTALLATION";
    private static final String API_TOKEN = "APP_DLID_TOKEN";

    /** Returns the consumer friendly device name */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }



    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    public synchronized static String id(Context context) {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    public synchronized static String apiToken(Context context) {
        if (sApiToken == null) {
            File token = new File(context.getFilesDir(), API_TOKEN);
            try {
                if (token.exists()) {
                    sApiToken = readInstallationFile(token);
                    JSONObject jo = new JSONObject(sApiToken);
                    sApiToken = jo.getString("token");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sApiToken;
    }

    public synchronized static void removeToken(Context context){
        File tokenFile = new File(context.getFilesDir(), API_TOKEN);

        if (tokenFile.exists()) {
            sApiToken = null;
            tokenFile.delete();
        }
    }

    public synchronized static void writeApiToken(Context context, String token, String email, List<String> permissions) throws IOException {
        File tokenFile = new File(context.getFilesDir(), API_TOKEN);
        sApiToken = null;
        FileOutputStream out = new FileOutputStream(tokenFile);

        JSONObject object = new JSONObject();
        try {
            object.put("token", token);
            object.put("email", email);
            object.put("permissions", permissions.toArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String id = UUID.randomUUID().toString();
        out.write(object.toString().getBytes());
        out.close();
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }
}