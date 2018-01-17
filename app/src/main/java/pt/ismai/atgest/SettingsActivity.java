package pt.ismai.atgest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import pt.ismai.atgest.DB.dataBase;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    protected Intent intent;
    protected Toolbar toolbar;
    protected dataBase db;
    protected TextView sideBarUsername, sideBarEmail;
    protected ImageView sideBarThumb;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setNavigationHeader();

        intent = null;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Definições");
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
        navigationView.setCheckedItem(R.id.nav_settings);
        navigationView.setNavigationItemSelectedListener(this);
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

        if (id == R.id.nav_ats) {
            intent = new Intent(this, MainActivity.class);
            toolbar.setTitle("Assistências Técnicas");
        } else if (id == R.id.nav_gps) {
            intent = new Intent(this, GpsActivity.class);
            toolbar.setTitle("Colaboradores");
        } else if (id == R.id.nav_clientes) {
            intent = new Intent(this, ClientesActivity.class);
            toolbar.setTitle("Clientes");
        } else if (id == R.id.nav_sync) {
            intent = new Intent(this, SyncActivity.class);
            toolbar.setTitle("Sincronizar");
        } else if (id == R.id.nav_logout) {
            db.ClearUserData();
            showLogin();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}