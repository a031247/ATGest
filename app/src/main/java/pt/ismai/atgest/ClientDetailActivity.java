package pt.ismai.atgest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.ismai.atgest.DB.dataBase;

public class ClientDetailActivity extends AppCompatActivity {
    protected Toolbar toolbar;
    protected dataBase db;
    protected String rawInfo;
    protected EditText etAddress, etLocation, etZipCode, etObs;
    protected TextView tvNif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etZipCode = (EditText) findViewById(R.id.etZipCode);
        etObs = (EditText) findViewById(R.id.etObs);
        tvNif = (TextView) findViewById(R.id.tvNif);

        db = new dataBase(this).open();
        Intent intent = getIntent();
        rawInfo = intent.getStringExtra("rawInfo");
        Pattern p = Pattern.compile("\\|\\s(\\d*)\\s\\|");
        Matcher m = p.matcher(rawInfo);
        m.find();

        JSONObject client = db.getClient(Integer.parseInt(m.group(1)));
        try {
            toolbar.setTitle(client.getString("name"));
            etAddress.setText(client.getString("address"));
            etLocation.setText(client.getString("location"));
            etZipCode.setText(client.getString("postal_code"));
            etObs.setText(client.getString("obs"));
            tvNif.setText(client.getString("nif"));
        }catch(Exception e){
            Log.e("client detail ", e.getMessage());
        }
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
