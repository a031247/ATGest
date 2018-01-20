package pt.ismai.atgest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import pt.ismai.atgest.DB.dataBase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener, WeekView.ScrollListener {
    protected Intent intent;
    protected Toolbar toolbar;
    protected dataBase db;
    protected TextView sideBarUsername, sideBarEmail;
    protected ImageView sideBarThumb;
    protected WeekView mWeekView;
    protected Calendar currentDateTime;
    protected SimpleDateFormat sdfMe;

    protected void onStart(){
        super.onStart();
        db = new dataBase(this).open();
        drawUserInfo();
    }

    @Override
    protected void onStop(){
        super.onStop();
        db.close();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mWeekView.invalidate();
        mWeekView.goToToday();
        mWeekView.goToHour(8.0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavigationHeader();

        currentDateTime = Calendar.getInstance();

        intent = null;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        sdfMe = new SimpleDateFormat("MMM", Locale.getDefault());
        toolbar.setTitle("ATs - " + sdfMe.format(currentDateTime.getTime()).toUpperCase() + " / " + currentDateTime.get(Calendar.YEAR));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                if(intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_ats);
        navigationView.setNavigationItemSelectedListener(this);

        mWeekView = (WeekView) findViewById(R.id.weekView);
        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEventLongPressListener(this);
        mWeekView.setEmptyViewLongPressListener(this);
        mWeekView.setScrollListener(this);
        setupDateTimeInterpreter(true);
    }

    private void translateMonth(int month){

    }

    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("E", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                String weekdayShort = ""+weekday.charAt(0) + weekday.charAt(1) + weekday.charAt(2);
                SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());
                return weekdayShort.toUpperCase() + " " + dayFormat.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return ""+hour;
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Intent x = new Intent(getApplicationContext(), EditAtActivity.class);
        String id = String.valueOf(event.getId());
        x.putExtra("id", id);
        startActivity(x);
        //Toast.makeText(this, "Clicked " + event.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        int year,month,day,hour;
        year = time.get(Calendar.YEAR);
        month = time.get(Calendar.MONTH) + 1;
        day = time.get(Calendar.DAY_OF_MONTH);
        hour = time.get(Calendar.HOUR_OF_DAY);

        Intent x = new Intent(getApplicationContext(), NewAtActivity.class);
        String[] sTime = new String[4];
        sTime[0] = "" + year;
        sTime[1] = month < 10 ? "0" + month : "" + month;
        sTime[2] = day < 10 ? "0" + day : "" + day;
        sTime[3] = hour < 10 ? "0" + hour : "" + hour;

        x.putExtra("time", sTime);
        startActivity(x);
        //Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
    }

    public WeekView getWeekView() {
        return mWeekView;
    }

    public void setNavigationHeader(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        navigationView.addHeaderView(header);
        sideBarUsername = (TextView) header.findViewById(R.id.sideBarUsername);
        sideBarEmail = (TextView) header.findViewById(R.id.sideBarEmail);
        sideBarThumb = (ImageView) header.findViewById(R.id.sideBarThumb);
    }

    public void drawUserInfo(){
        JSONObject userInfo = db.getUserInfo();

        if(userInfo != null){
            try{
                sideBarUsername.setText(userInfo.getString("name"));
                sideBarEmail.setText(userInfo.getString("email"));
                byte[] imageBytes = Base64.decode(db.getUserThumb(), Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                sideBarThumb.setImageBitmap(bmp);
            }catch(Exception e){
                Log.e("drawUserInfo", e.getMessage());
            }
        }else{
            showLogin();
        }
    }

    private void showLogin(){
        Intent x = new Intent(this, LoginActivity.class);
        startActivity(x);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            minimizeApp();
        }
    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gps) {
            intent = new Intent(this, GpsActivity.class);
            toolbar.setTitle("Colaboradores");
        } else if (id == R.id.nav_clientes) {
            intent = new Intent(this, ClientesActivity.class);
            toolbar.setTitle("Clientes");
        } else if (id == R.id.nav_sync) {
            intent = new Intent(this, SyncActivity.class);
            toolbar.setTitle("Sincronizar");
        } else if (id == R.id.nav_settings) {
            intent = new Intent(this, SettingsActivity.class);
            toolbar.setTitle("Definições");
        } else if (id == R.id.nav_logout) {
            db.ClearUserData();
            showLogin();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        ArrayList<JSONObject> ats = db.getAtsByMonthYear(newYear, newMonth);
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        SimpleDateFormat sdfD = new SimpleDateFormat("dd");
        SimpleDateFormat sdfH = new SimpleDateFormat("HH");
        SimpleDateFormat sdfM = new SimpleDateFormat("mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar startTime;
        Calendar endTime;
        WeekViewEvent event;

        if(ats.size() > 0) {
            try{
                for (JSONObject at : ats) {
                    startTime = Calendar.getInstance();
                    endTime = Calendar.getInstance();
                    startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sdfD.format(sdf.parse(at.getString("datestart")))));
                    startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sdfH.format(sdf.parse(at.getString("datestart")))));
                    startTime.set(Calendar.MINUTE, Integer.parseInt(sdfM.format(sdf.parse(at.getString("datestart")))));
                    startTime.set(Calendar.MONTH, newMonth-1);
                    startTime.set(Calendar.YEAR, newYear);
                    endTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(sdfD.format(sdf.parse(at.getString("datefinish")))));
                    endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sdfH.format(sdf.parse(at.getString("datefinish")))));
                    endTime.set(Calendar.MINUTE, Integer.parseInt(sdfM.format(sdf.parse(at.getString("datefinish")))));

                    endTime.set(Calendar.MONTH, newMonth-1);
                    event = new WeekViewEvent(at.getInt("id"), at.getString("name"), startTime, endTime);
                    event.setColor(getResources().getColor(R.color.event_color_02));
                    events.add(event);
                }
            }catch(Exception e) {
                Log.e("calendar", e.getMessage());
            }
        }

        return events;
    }

    public void onFirstVisibleDayChanged(Calendar newDay, Calendar oldDay){
        if(currentDateTime.get(Calendar.MONTH) != newDay.get(Calendar.MONTH)) {
            toolbar.setTitle("ATs - " + sdfMe.format(newDay.getTime()).toUpperCase() + " / " + newDay.get(Calendar.YEAR));
            currentDateTime.set(Calendar.MONTH, newDay.get(Calendar.MONTH));
        }
    }
}
