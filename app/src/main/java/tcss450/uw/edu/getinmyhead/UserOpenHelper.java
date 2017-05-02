package tcss450.uw.edu.getinmyhead;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hinds on 5/1/2017.
 */


/** database tables should use the identifier _id for the primary key of the table.
 * Several Android functions rely on this standard.
 */

public class UserOpenHelper extends SQLiteOpenHelper{

    public static final String TABLE_USER = "users";  //table name
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USER_NAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    private static final String DATABASE_NAME = "userinfo.db";
    private static final int DATABASE_VERSION = 1;

    /* DataBase creating sql statement */
    private static final String DATABASE_CREATE ="create table " + TABLE_USER + "( " +
            COLUMN_USER_ID + " integer primary key autoincrement, " +
            COLUMN_USER_NAME + " text not null," +
            COLUMN_USER_NAME + " text not null," +
            COLUMN_PASSWORD + " text not null" +
            " );";

    //constructor
    public UserOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** Creates SQLite database  if the database is accessed but not yet created.
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    /** method will simply delete all existing data and re-create the table.
     *  Is called, if the database version is increased in application code. This method allows
     *  an update  of an existing database schema or to drop the existing database and recreate it
     *  via the onCreate() method.
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(UserOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }
}
