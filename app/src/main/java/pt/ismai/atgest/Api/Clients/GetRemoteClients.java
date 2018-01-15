package pt.ismai.atgest.Api.Clients;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import org.json.JSONArray;

import pt.ismai.atgest.Api.APIUtils;
import pt.ismai.atgest.Api.Send;
import pt.ismai.atgest.DB.dataBase;

public final class GetRemoteClients extends AsyncTask<Void, Void, String> {
    protected String params;
    protected dataBase db;
    protected Button buttonAction;
    protected ProgressBar progressBar;
    protected SyncClients tskSyncClients;

    @Override
    protected void onPreExecute(){
        if(progressBar != null && buttonAction != null) {
            progressBar.setVisibility(View.VISIBLE);
            buttonAction.setVisibility(View.INVISIBLE);
        }

        params = "?api_token=" + db.getUserApiToken();
    }

    @Override
    protected String doInBackground(Void... url){
        return Send.get("http", APIUtils.ApiURL,80,"api/client" + params);
    }

    @Override
    protected void onProgressUpdate(Void... percentComplete){
        //
    }

    @Override
    protected void onPostExecute(String s){
        try {
            JSONArray array = new JSONArray(s);
            if(array.length() > 0) {
                tskSyncClients = new SyncClients(db, buttonAction, progressBar, array);
                tskSyncClients.execute();
            }
        }catch(Exception e) {
            Log.e("Exceção Post Execute: ", e.getMessage());
        }
    }

    public GetRemoteClients(dataBase _db, Button b, ProgressBar pb){
        db = _db;
        buttonAction = b;
        progressBar = pb;
    }
}