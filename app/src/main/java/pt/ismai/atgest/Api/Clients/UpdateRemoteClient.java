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
    protected String strParams;

    @Override
    protected void onPreExecute(){
        try {
            clientId = client.getInt("id");
            client.put("api_token", db.getUserApiToken());
            strParams = getPostDataString(client);
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
