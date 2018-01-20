package pt.ismai.atgest;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
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
import org.w3c.dom.Text;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.ismai.atgest.DB.dataBase;

public class NewAtActivity extends AppCompatActivity {
    protected ListView lvClients;
    protected SearchView svClients;
    protected Button buttonSaveNewAt;
    protected dataBase db;
    protected ArrayList<Spanned> clients;
    protected ArrayAdapter<Spanned> adapter;
    protected EditText etObs;
    protected TextView tvInicio, tvFim;
    protected static TextView tvAux;
    protected static int clicked;
    protected static String[] time;
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
        setContentView(R.layout.activity_new_at);

        activity = this;

        Intent intent = getIntent();
        time = intent.getStringArrayExtra("time");

        db = new dataBase(this).open();

        lvClients = (ListView) findViewById(R.id.lvClients);

        clients = db.getClientsIdName();
        adapter = new ArrayAdapter<Spanned>(this,android.R.layout.simple_list_item_1, clients);
        lvClients.setAdapter(adapter);

        svClients = (SearchView) findViewById(R.id.svClients);
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

        etObs = (EditText) findViewById(R.id.etObs);
        buttonSaveNewAt = (Button) findViewById(R.id.buttonSaveNewAt);

        buttonSaveNewAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDate = sdf.format(Calendar.getInstance().getTime());
                    JSONObject userInfo = db.getUserInfo();
                    JSONObject at = new JSONObject();
                    at.put("clientid", clientSelected);
                    at.put("datestart", tvInicio.getText().toString() + ":00");
                    at.put("datefinish", tvFim.getText().toString() + ":00");
                    at.put("syncronized", 0);
                    at.put("userstamp", userInfo.getInt("id"));
                    at.put("clientsignature", "");
                    at.put("obs", etObs.getText().toString());
                    at.put("created_at", currentDate);
                    at.put("updated_at", currentDate);
                    db.addAt(at);
                    Intent x = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(x);
                    activity.finish();
                }catch(Exception e){
                    Log.e("new at", e.getMessage());
                }
            }
        });

        Locale locale = new Locale("PT", "PT");
        Locale.setDefault(locale);

        tvAux = (TextView) findViewById(R.id.tvAux);
        tvInicio = (TextView) findViewById(R.id.tvInicio);
        tvFim = (TextView) findViewById(R.id.tvFim);

        tvInicio.setText(time[0] + "-" + time[1] + "-" + time[2] + " " + time[3] + ":00");
        tvFim.setText(time[0] + "-" + time[1] + "-" + time[2] + " " + time[3] + ":30");

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

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int hour = Integer.parseInt(time[3]);
            int minute = clicked == R.id.tvInicio ? 00 : 30;

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
            int year = Integer.parseInt(time[0]);
            int month = Integer.parseInt(time[1]);
            int day = Integer.parseInt(time[2]);
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
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}
