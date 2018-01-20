package pt.ismai.atgest.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class dataBase {
    private dataBaseHelper dbHelper;
    private SQLiteDatabase database;

    public dataBase(Context context){
        dbHelper = new dataBaseHelper(context.getApplicationContext());
    }

    public dataBase open(){
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    public void ClearUserData(){
        database.delete("user", null, null);
        database.delete("clients", null, null);
        database.delete("ats", null, null);
    }

    public Long saveUserInfo(JSONObject userInfo){
        ContentValues values = new ContentValues();

        try {
            values.put("api_token", userInfo.getString("api_token"));
            values.put("name", userInfo.getString("name"));
            values.put("email", userInfo.getString("email"));
            values.put("thumb", userInfo.getString("thumb"));
        }catch(Exception e){
            Log.e("saveUserInfo", e.getMessage());
        }

        database.delete("user", null, null);
        return database.insert("user", null, values);
    }

    public JSONObject getUserInfo(){
        JSONObject object = null;
        String[] colunas = {"id", "api_token", "name", "email"};
        Cursor cursor = database.query("user", colunas, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            try {
                object = new JSONObject();
                object.put("id", cursor.getInt(0));
                object.put("api_token", cursor.getString(1));
                object.put("name", cursor.getString(2));
                object.put("email", cursor.getString(3));
            }catch(Exception e){
                Log.e("Sql Query User Info: ", e.getMessage());
            }
        }
        cursor.close();
        return object;
    }

    public byte[] getUserThumb(){
        byte[] byteArray = null;
        String[] colunas = {"thumb"};
        Cursor cursor = database.query("user", colunas, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            byteArray = cursor.getBlob(0);
        }
        cursor.close();
        return byteArray;
    }

    public String getUserApiToken(){
        String apiToken = null;
        String[] colunas = {"api_token"};
        Cursor cursor = database.query("user", colunas, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            apiToken = cursor.getString(0);
        }
        cursor.close();
        return apiToken;
    }

    public ArrayList<Spanned> getClientsIdName(){
        ArrayList<Spanned> clients = new ArrayList<Spanned>();
        String[] colunas = {"id", "name"};
        Cursor cursor = database.query("clients", colunas, null, null, null, null, "id DESC");
        if(cursor.moveToFirst()){
            do{
                clients.add(Html.fromHtml(cursor.getString(1) + "<br /><small><font color='gray'>| " + cursor.getString(0) +" |</font></small>", 1));
            } while (cursor.moveToNext());
        }
        return clients;
    }

    public JSONObject getClient(int id){
        JSONObject object = new JSONObject();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM clients WHERE id=?", new String[] { String.valueOf(id) });
        if (cursor.moveToFirst()) {
            try {
                object.put("id", cursor.getInt(0));
                object.put("name", cursor.getString(1));
                object.put("postal_code", cursor.getString(2));
                object.put("address", cursor.getString(3));
                object.put("location", cursor.getString(4));
                object.put("nif", cursor.getString(5));
                object.put("obs", cursor.getString(6));
                object.put("status", cursor.getInt(7));
                object.put("userstamp", cursor.getInt(8));
                object.put("created_at", cursor.getString(9));
                object.put("updated_at", cursor.getString(10));
            }catch(Exception e){
                Log.e("Sql Query Client Info: ", e.getMessage());
            }
        }
        cursor.close();
        return object;
    }

    public long addClient(JSONObject client){
        ContentValues values = new ContentValues();
        try {
            values.put("id", client.getInt("id"));
            values.put("name", client.getString("name"));
            values.put("postal_code", client.getString("postal_code"));
            values.put("address", client.getString("address"));
            values.put("location", client.getString("location"));
            values.put("nif", client.getString("nif"));
            values.put("obs", client.getString("obs"));
            values.put("status", client.getInt("status"));
            values.put("userstamp", client.getString("userstamp"));
            values.put("created_at", client.getString("created_at"));
            values.put("updated_at", client.getString("updated_at"));
        }catch(Exception e){
            Log.e("client.getString() ", e.getMessage());
        }
        return database.insert("clients", null, values);
    }

    public long updateClientSync(JSONObject client){
        ContentValues values = new ContentValues();
        int clientId = 0;
        try {
            clientId = client.getInt("id");
            values.put("name", client.getString("name"));
            values.put("postal_code", client.getString("postal_code"));
            values.put("address", client.getString("address"));
            values.put("location", client.getString("location"));
            values.put("nif", client.getString("nif"));
            values.put("obs", client.getString("obs"));
            values.put("status", client.getInt("status"));
            values.put("userstamp", client.getString("userstamp"));
            values.put("created_at", client.getString("created_at"));
            values.put("updated_at", client.getString("updated_at"));
        }catch(Exception e){
            Log.e("client.getString() ", e.getMessage());
        }
        return database.update("clients", values, "id=" + clientId, null);
    }

    public long updateClient(JSONObject client){
        ContentValues values = new ContentValues();
        int clientId = 0;
        try {
            clientId = client.getInt("id");
            values.put("postal_code", client.getString("postal_code"));
            values.put("address", client.getString("address"));
            values.put("location", client.getString("location"));
            values.put("obs", client.getString("obs"));
            values.put("updated_at", client.getString("updated_at"));
        }catch(Exception e){
            Log.e("client.getString() ", e.getMessage());
        }
        return database.update("clients", values, "id=" + clientId, null);
    }

    public JSONObject getAt(int id){
        JSONObject object = new JSONObject();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM ats WHERE id=?", new String[] { String.valueOf(id) });
        if (cursor.moveToFirst()) {
            try {
                object.put("id", cursor.getInt(0));
                object.put("clientid", cursor.getInt(1));
                object.put("datestart", cursor.getString(2));
                object.put("datefinish", cursor.getString(3));
                object.put("syncronized", cursor.getInt(4));
                object.put("userstamp", cursor.getInt(5));
                object.put("clientsignature", cursor.getString(6));
                object.put("obs", cursor.getString(7));
                object.put("created_at", cursor.getString(8));
                object.put("updated_at", cursor.getString(9));
            }catch(Exception e){
                Log.e("getAt: ", e.getMessage());
            }
        }
        cursor.close();
        return object;
    }

    public ArrayList<JSONObject> getAts(){
        ArrayList<JSONObject> ats = new ArrayList<JSONObject>();
        JSONObject object;
        String[] colunas = {"id", "clientid", "datestart", "datefinish"};
        Cursor cursor = database.query("ats", colunas, null, null, null, null, "id DESC");
        if(cursor.moveToFirst()){
            try {
                do{
                    object = new JSONObject();
                    object.put("id", cursor.getInt(0));
                    object.put("clientid", cursor.getInt(1));
                    object.put("datestart", cursor.getString(2));
                    object.put("datefinish", cursor.getString(3));
                    ats.add(object);
                } while (cursor.moveToNext());
            }catch(Exception e){
                Log.e("getAts: ", e.getMessage());
            }
        }
        cursor.close();
        return ats;
    }

    public long addAt(JSONObject at){
        ContentValues values = new ContentValues();
        try {
            values.put("clientid", at.getInt("clientid"));
            values.put("datestart", at.getString("datestart"));
            values.put("datefinish", at.getString("datefinish"));
            values.put("syncronized", at.getInt("syncronized"));
            values.put("userstamp", at.getInt("userstamp"));
            values.put("clientsignature", at.getString("clientsignature"));
            values.put("obs", at.getString("obs"));
            values.put("created_at", at.getString("created_at"));
            values.put("updated_at", at.getString("updated_at"));
        }catch(Exception e){
            Log.e("addAt", e.getMessage());
        }
        return database.insert("ats", null, values);
    }

    public long updateAt(JSONObject at){
        ContentValues values = new ContentValues();
        int atId = 0;
        try {
            atId = at.getInt("id");
            values.put("clientid", at.getInt("clientid"));
            values.put("datestart", at.getString("datestart"));
            values.put("datefinish", at.getString("datefinish"));
            values.put("syncronized", 0);
            values.put("obs", at.getString("obs"));
            values.put("updated_at", at.getString("updated_at"));
        }catch(Exception e){
            Log.e("updateAt", e.getMessage());
        }
        return database.update("ats", values, "id=" + atId, null);
    }

    public ArrayList<JSONObject> getAtsByMonthYear(int year, int month){
        JSONObject at;
        String clientName;
        Cursor cName;
        ArrayList<JSONObject> ats = new ArrayList<JSONObject>();
        String sYear = String.valueOf(year);
        String sMonth = month < 10 ? "0" + month : String.valueOf(month);

        Cursor cursor = database.rawQuery(
                "SELECT id, clientid, datestart, datefinish FROM ats WHERE strftime('%Y', datestart) = strftime('%Y', '" +
                        sYear + "-" + sMonth +
                        "-01') AND strftime('%m', datestart) = strftime('%m', '" +
                        sYear + "-" + sMonth +
                        "-01')", null);
        if(cursor.moveToFirst()){
            do{
                cName = database.rawQuery(
                        "SELECT name FROM clients WHERE id=?", new String[] { String.valueOf(cursor.getInt(1)) });
                if (cName.moveToFirst())
                    clientName = cName.getString(0);
                else
                    clientName = "(Sem Cliente)";

                cName.close();

                at = new JSONObject();
                try {
                    at.put("id", cursor.getInt(0));
                    at.put("name", clientName);
                    at.put("datestart", cursor.getString(2));
                    at.put("datefinish", cursor.getString(3));
                    ats.add(at);
                }catch(Exception e){
                    Log.e("getAtsByMonthYear: ", e.getMessage());
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return ats;
    }
}
