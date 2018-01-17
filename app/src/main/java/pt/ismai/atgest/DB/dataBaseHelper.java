package pt.ismai.atgest.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dataBaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "atgest.db";
    private static final int VERSION = 4;

    public dataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String tableUser = "CREATE TABLE user(" +
                "id integer primary key autoincrement," +
                " api_token varchar(60)," +
                " name varchar(255)," +
                " email varchar(255)," +
                " thumb blob" +
                "); ";

        String tableClients =
                "CREATE TABLE clients(" +
                        "id integer primary key autoincrement," +
                        " name varchar(255)," +
                        " postal_code varchar(255)," +
                        " address varchar(255)," +
                        " location varchar(255)," +
                        " nif varchar(15)," +
                        " obs text," +
                        " status integer," +
                        " userstamp integer," +
                        " created_at datetime," +
                        " updated_at datetime" +
                        ");";

        db.execSQL(tableUser);
        db.execSQL(tableClients);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS clients");
        onCreate(db);
    }
}
