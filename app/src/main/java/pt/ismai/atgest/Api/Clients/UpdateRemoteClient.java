package pt.ismai.atgest.Api.Clients;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import pt.ismai.atgest.Api.APIUtils;
import pt.ismai.atgest.Api.Send;
import pt.ismai.atgest.DB.dataBase;

import static pt.ismai.atgest.Api.APIUtils.buildJsonParams;
import static pt.ismai.atgest.Api.APIUtils.getPostDataString;


public final class UpdateRemoteClient extends AsyncTask<Void, Void, String> {
    protected dataBase db;
    protected JSONObject client;
    protected int clientId = 0;
    protected ArrayList<String[]> params;
    protected String strParams;

    @Override
    protected void onPreExecute(){
        JSONObject postParams = buildJsonParams(params);
        try {
            clientId = client.getInt("id");
            params = new ArrayList<String[]>();
            params.add(new String[]{"api_token", db.getUserApiToken()});
            params.add(new String[]{"name", client.getString("name")});
            params.add(new String[]{"postal_code", client.getString("postal_code")});
            params.add(new String[]{"address", client.getString("address")});
            params.add(new String[]{"location", client.getString("location")});
            params.add(new String[]{"obs", client.getString("obs")});
            params.add(new String[]{"updated_at", client.getString("updated_at")});
            strParams = getPostDataString(postParams);
        }catch(Exception e) {
            Log.e("Exceção Pre Execute: ", e.getMessage());
        }
    }

    @Override
    protected String doInBackground(Void... url){
        return Send.post("http", APIUtils.ApiURL, 80, "api/client/" + clientId, strParams);
    }

    @Override
    protected void onProgressUpdate(Void... percentComplete){
        //
    }

    @Override
    protected void onPostExecute(String result){
        try {
            JSONObject object = (JSONObject) new JSONTokener(result).nextValue();
            if(!object.has("id")) {
                Log.e("Error: ", object.getString("error"));
            }
        }catch(Exception e) {
            Log.e("Returned string: ", result);
        }
    }

    public UpdateRemoteClient(dataBase _db, JSONObject _client){
        db = _db;
        client = _client;
    }
}
