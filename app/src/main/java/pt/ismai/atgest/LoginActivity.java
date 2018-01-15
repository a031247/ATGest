package pt.ismai.atgest;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import pt.ismai.atgest.Api.Send;
import pt.ismai.atgest.Api.APIUtils;
import pt.ismai.atgest.DB.dataBase;

public class LoginActivity extends Activity {

        protected EditText editTextEmail;
        protected EditText editTextPassword;
        protected Button buttonLogin;
        protected ProgressBar progressBar;
        protected dataBase db;
        protected Activity activity;
        getUserInfoAsync backgroundTask;

        @Override
        protected  void onStart(){
            super.onStart();
            db = new dataBase(this).open();
        }

        @Override
        protected void onStop(){
            super.onStop();
            db.close();
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            editTextEmail = (EditText) findViewById(R.id.editTextEmail);
            editTextPassword = (EditText) findViewById(R.id.editTextPassword);
            buttonLogin = (Button) findViewById(R.id.buttonLogin);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            activity = this;

            buttonLogin.setOnClickListener(new Button.OnClickListener(){
                public void onClick(View v){
                    backgroundTask = new getUserInfoAsync(editTextEmail,editTextPassword, buttonLogin, progressBar, db, activity);
                    backgroundTask.execute();
                }
            });
        }

        @Override
        public void onBackPressed(){}
}

final class getUserInfoAsync extends AsyncTask<Void, Void, String> {
    protected EditText editTextEmail;
    protected EditText editTextPassword;
    protected Button buttonLogin;
    protected ProgressBar progressBar;
    protected dataBase db;
    protected Activity activity;
    protected String email;
    protected String password;

    @Override
    protected void onPreExecute(){
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        buttonLogin.setVisibility(View.INVISIBLE);
    }

    @Override
    protected String doInBackground(Void... url){
        ArrayList<String[]> params = new ArrayList<String[]>();
        params.add(new String[]{"username", email});
        params.add(new String[]{"password", password});
        JSONObject postParams = APIUtils.buildJsonParams(params);
        String strParams = APIUtils.getPostDataString(postParams);

        return Send.post("http","zonaporto.com",80,"api/userLogin",strParams);
    }

    @Override
    protected void onProgressUpdate(Void... percentComplete){
        //
    }

    @Override
    protected void onPostExecute(String s){
        try {
            JSONObject object = (JSONObject) new JSONTokener(s).nextValue();
            if(object.has("api_token")) {
                db.saveUserInfo(object);
                activity.finish();
            }
            else
                Toast.makeText(activity, object.getString("error"), Toast.LENGTH_LONG).show();
        }catch(Exception e) {
            Toast.makeText(activity, s, Toast.LENGTH_LONG).show();
        }

        progressBar.setVisibility(View.INVISIBLE);
        buttonLogin.setVisibility(View.VISIBLE);
    }

    public getUserInfoAsync(EditText e, EditText p, Button b, ProgressBar pb, dataBase _db, Activity a){
        editTextEmail = e;
        editTextPassword = p;
        buttonLogin = b;
        progressBar = pb;
        db = _db;
        activity = a;
    }
}
