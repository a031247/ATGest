package pt.ismai.atgest.Api.Clients;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pt.ismai.atgest.DB.dataBase;

public final class SyncClients extends AsyncTask<Void, Void, List<JSONObject>> {
    protected dataBase db;
    protected Button buttonAction;
    protected ProgressBar progressBar;
    protected JSONArray array;
    protected List<JSONObject> updateList;
    protected UpdateRemoteClient tskUpdateRemoteClient;

    @Override
    protected void onPreExecute(){
        if(progressBar != null && buttonAction != null) {
            progressBar.setVisibility(View.VISIBLE);
            buttonAction.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected List<JSONObject> doInBackground(Void... url){
        try {
                JSONObject registoRemoto, registoLocal;

                for(int i = 0; i < array.length(); i++){
                    registoRemoto = array.getJSONObject(i);
                    registoLocal = db.getClient(registoRemoto.getInt("id"));
                    if(registoLocal.has("id")){
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date dateRemoto = sdf.parse(registoRemoto.getString("updated_at"));
                        Date dateLocal = sdf.parse(registoLocal.getString("updated_at"));
                        if(dateRemoto.before(dateLocal)){
                            updateList.add(registoLocal);
                            Log.i("Editar cliente remoto:","" + registoRemoto.getInt("id"));
                        }else if(dateLocal.before(dateRemoto)){
                            db.updateClient(registoRemoto);
                            Log.i("Editar cliente local:","" + registoRemoto.getInt("id"));
                        }
                    }else{
                        db.saveClient(registoRemoto);
                        Log.i("Registo Novo:","" + registoRemoto.getInt("id"));
                    }
                }
        }catch(Exception e) {
            Log.e("Exceção Post Execute: ", e.getMessage());
        }
        return updateList;
    }

    @Override
    protected void onProgressUpdate(Void... percentComplete){
        //
    }

    @Override
    protected void onPostExecute(List<JSONObject> updateList){
        if(updateList != null) {
            for (JSONObject registo : updateList) {
                tskUpdateRemoteClient = new UpdateRemoteClient(db, registo);
                tskUpdateRemoteClient.execute();
            }
        }

        if(progressBar != null && buttonAction != null) {
            progressBar.setVisibility(View.INVISIBLE);
            buttonAction.setVisibility(View.VISIBLE);
        }
    }

    public SyncClients(dataBase _db, Button b, ProgressBar pb, JSONArray _array){
        db = _db;
        buttonAction = b;
        progressBar = pb;
        array = _array;
    }
}
