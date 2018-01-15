package pt.ismai.atgest.Api;

import android.util.Log;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

public class APIUtils {
    public static final String ApiURL = "zonaporto.com";

    public static JSONObject buildJsonParams (ArrayList<String[]> params){
        JSONObject postParams = new JSONObject();
        try {
            for (String[] registo : params) {
                postParams.put(registo[0], registo[1]);
            }
        }
        catch(Exception e){
            Log.e("Exception: ", e.getMessage());
        }
        return postParams;
    }

    public static String getPostDataString(JSONObject params){
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();

        try {
            while (itr.hasNext()) {
                String key = itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));
            }
        }catch(Exception e){
            Log.e("Exception: ", e.getMessage());
        }
        return result.toString();
    }
}
