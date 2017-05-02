package tcss450.uw.edu.getinmyhead;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hinds on 5/1/2017.
 */

public class UserOpenHelper extends SQLiteOpenHelper{

    public static final String TABLE_USER = "users";  //table name
    public static final String COLUMN_USER_ID = "user_id";
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

    /** Creates SQLite database
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    /** method will simply delete all existing data and re-create the table.
     *
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
