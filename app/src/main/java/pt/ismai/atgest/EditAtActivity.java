package pt.ismai.atgest;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.ismai.atgest.DB.dataBase;

public class EditAtActivity extends AppCompatActivity {
    protected ListView lvClients;
    protected SearchView svClients;
    protected Button buttonSaveEditAt, buttonChangeClient;
    protected dataBase db;
    protected ArrayList<Spanned> clients;
    protected ArrayAdapter<Spanned> adapter;
    protected EditText etObs;
    protected TextView tvInicio, tvFim, tvClient;
    protected static TextView tvAux;
    protected static int clicked;
    protected static int atId;
    protected static String[] timeStart, timeEnd;
    protected int clientSelected;
    protected Activity activity;

    protected void onStart(){
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
        setContentView(R.layout.activity_edit_at);
        tvClient = (TextView) findViewById(R.id.tvClient);
        lvClients = (ListView) findViewById(R.id.lvClients);
        svClients = (SearchView) findViewById(R.id.svClients);
        etObs = (EditText) findViewById(R.id.etObs);
        buttonSaveEditAt = (Button) findViewById(R.id.buttonSaveEditAt);
        buttonChangeClient = (Button) findViewById(R.id.buttonChangeClient);
        tvAux = (TextView) findViewById(R.id.tvAux);
        tvInicio = (TextView) findViewById(R.id.tvInicio);
        tvFim = (TextView) findViewById(R.id.tvFim);
        activity = this;

        Intent intent = getIntent();
        atId = Integer.parseInt(intent.getStringExtra("id"));

        db = new dataBase(this).open();

        getAtFromDb(atId);


        clients = db.getClientsIdName();
        adapter = new ArrayAdapter<Spanned>(this,android.R.layout.simple_list_item_1, clients);
        lvClients.setAdapter(adapter);
        svClients.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText){
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        lvClients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String client = adapter.getItem(i).toString();
                Pattern p = Pattern.compile("\\|\\s(\\d*)\\s\\|");
                Matcher m = p.matcher(client);
                m.find();
                clientSelected = Integer.parseInt(m.group(1));
            }
        });

        buttonSaveEditAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDate = sdf.format(Calendar.getInstance().getTime());
                    JSONObject userInfo = db.getUserInfo();
                    JSONObject at = new JSONObject();
                    at.put("id", atId);
                    at.put("clientid", clientSelected);
                    at.put("datestart", tvInicio.getText().toString() + ":00");
                    at.put("datefinish", tvFim.getText().toString() + ":00");
                    at.put("syncronized", 0);
                    at.put("obs", etObs.getText().toString());
                    at.put("updated_at", currentDate);
                    db.updateAt(at);
                    Intent x = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(x);
                    activity.finish();
                }catch(Exception e){
                    Log.e("new at", e.getMessage());
                }
            }
        });

        buttonChangeClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.clientInfo).setVisibility(View.GONE);
                svClients.setVisibility(View.VISIBLE);
                lvClients.setVisibility(View.VISIBLE);
            }
        });

        tvInicio.setText(timeStart[0] + "-" + timeStart[1] + "-" + timeStart[2] + " " + timeStart[3] + ":" + timeStart[4]);
        tvFim.setText(timeEnd[0] + "-" + timeEnd[1] + "-" + timeEnd[2] + " " + timeEnd[3] + ":" + timeEnd[4]);

        tvInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = v.getId();
                showTimePickerDialog(v);
                showDatePickerDialog(v);
            }
        });

        tvFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = v.getId();
                showTimePickerDialog(v);
                showDatePickerDialog(v);
            }
        });

        tvAux.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                TextView field = (TextView) findViewById(clicked);
                field.setText(editable);
            }
        });
    }

    public void getAtFromDb(int id){
        JSONObject at, clientInfo;
        Date dbDateStart, dbDateEnd;
        timeStart = new String[5];
        timeEnd = new String[5];
        SimpleDateFormat sdfY = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfM = new SimpleDateFormat("MM");
        SimpleDateFormat sdfD = new SimpleDateFormat("dd");
        SimpleDateFormat sdfH = new SimpleDateFormat("HH");
        SimpleDateFormat sdfm = new SimpleDateFormat("mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            at = db.getAt(id);
            clientSelected = at.getInt("clientid");
            dbDateStart = sdf.parse(at.getString("datestart"));
            dbDateEnd = sdf.parse(at.getString("datefinish"));
            timeStart[0] = sdfY.format(dbDateStart);
            timeStart[1] = sdfM.format(dbDateStart);
            timeStart[2] = sdfD.format(dbDateStart);
            timeStart[3] = sdfH.format(dbDateStart);
            timeStart[4] = sdfm.format(dbDateStart);
            timeEnd[0] = sdfY.format(dbDateEnd);
            timeEnd[1] = sdfM.format(dbDateEnd);
            timeEnd[2] = sdfD.format(dbDateEnd);
            timeEnd[3] = sdfH.format(dbDateEnd);
            timeEnd[4] = sdfm.format(dbDateEnd);
            etObs.setText(at.getString("obs"));

            if(clientSelected > 0) {
                clientInfo = db.getClient(clientSelected);
                tvClient.setText(clientInfo.getString("name"));
            }
        }catch(Exception e){
            Log.e("getAtFromDb", e.getMessage());
        }
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int hour, minute;
            String time[] = new String[5];
            if(clicked == R.id.tvInicio)
                System.arraycopy(timeStart, 0, time, 0, timeStart.length);
            else
                System.arraycopy(timeEnd, 0, time, 0, timeEnd.length);

            hour = Integer.parseInt(time[3]);
            minute = Integer.parseInt(time[4]);

            return new TimePickerDialog(getActivity(), this, hour, minute, true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String sHourOfDay = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
            String sMinute = minute < 10 ? "0" + minute : "" + minute;
            tvAux.setText(tvAux.getText() + " " + sHourOfDay + ":"	+ sMinute);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year, month, day;
            String time[] = new String[5];
            if(clicked == R.id.tvInicio)
                System.arraycopy(timeStart, 0, time, 0, timeStart.length);
            else
                System.arraycopy(timeEnd, 0, time, 0, timeEnd.length);

            year = Integer.parseInt(time[0]);
            month = Integer.parseInt(time[1]);
            day = Integer.parseInt(time[2]);

            return new DatePickerDialog(getActivity(), this, year, month - 1, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month++;
            String sMonth = month < 10 ? "0" + month : "" + month;
            String sDay = day < 10 ? "0" + day : "" + day;
            tvAux.setText(year + "-" + sMonth + "-" + sDay);
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new EditAtActivity.TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new EditAtActivity.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}
