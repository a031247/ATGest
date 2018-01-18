package pt.ismai.atgest;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar;

import pt.ismai.atgest.DB.dataBase;

public class ClientDetailActivity extends AppCompatActivity implements TextWatcher{
    protected Toolbar toolbar;
    protected dataBase db;
    protected String rawInfo;
    protected EditText etAddress, etLocation, etZipCode, etObs;
    protected TextView tvCod,tvNif;
    protected FloatingActionButton fab;
    protected String id, address, location, zipCode, obs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvCod = (TextView) findViewById(R.id.tvCod);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etZipCode = (EditText) findViewById(R.id.etZipCode);
        etObs = (EditText) findViewById(R.id.etObs);
        tvNif = (TextView) findViewById(R.id.tvNif);
        fab = (FloatingActionButton)  findViewById(R.id.fabClientDetail);

        db = new dataBase(this).open();
        Intent intent = getIntent();
        rawInfo = intent.getStringExtra("rawInfo");
        Pattern p = Pattern.compile("\\|\\s(\\d*)\\s\\|");
        Matcher m = p.matcher(rawInfo);
        m.find();

        final JSONObject client = db.getClient(Integer.parseInt(m.group(1)));
        try {
            toolbar.setTitle(client.getString("name"));
            tvCod.setText(client.getString("id"));
            etAddress.setText(client.getString("address"));
            etLocation.setText(client.getString("location"));
            etZipCode.setText(client.getString("postal_code"));
            etObs.setText(client.getString("obs"));
            tvNif.setText(client.getString("nif"));
        }catch(Exception e){
            Log.e("client detail ", e.getMessage());
        }
        setSupportActionBar(toolbar);

        etAddress.addTextChangedListener(this);
        etLocation.addTextChangedListener(this);
        etZipCode.addTextChangedListener(this);
        etObs.addTextChangedListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = tvCod.getText().toString();
                address = etAddress.getText().toString();
                location = etLocation.getText().toString();
                zipCode = etZipCode.getText().toString();
                obs = etObs.getText().toString();

                try{
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    JSONObject clientInfo = new JSONObject();
                    clientInfo.put("id", id);
                    clientInfo.put("address", address);
                    clientInfo.put("location", location);
                    clientInfo.put("postal_code", zipCode);
                    clientInfo.put("obs", obs);
                    clientInfo.put("updated_at", sdf.format(Calendar.getInstance().getTime()));
                    if(db.updateClient(clientInfo) > 0){
                        fab.setVisibility(View.INVISIBLE);
                    }
                }catch(Exception e){
                    Log.e("ClientDetail ", e.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        db.close();
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after){}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count){}

    @Override
    public  void afterTextChanged(Editable s){
        fab.setVisibility(View.VISIBLE);
    }
}
