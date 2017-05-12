package tcss450.uw.edu.getinmyhead;
/**
 * Created by Robert Hinds on 5/1/2017.
 * Used this tutorial as base. http://www.vogella.com/tutorials/AndroidSQLite/article.html
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Helper class to abstract the creation and upgrading of SQL tables.
 * Database tables should use the identifier _id for the primary key of the table.
 * Several Android functions rely on this standard.
 * @author Robert Hinds
 * @version 1.0
 */

public class LibraryOpenHelper extends SQLiteOpenHelper{

    public static final String TABLE_LIBRARY = "library";  //table name
    public static final String COLUMN_LIB_ITEM_ID = "_id";
    public static final String COLUMN_LIB_ITEM_NAME = "item_name";
    public static final String COLUMN_LIB_ITEM_LAST_SETTING = "last_setting";

    private static final String DATABASE_NAME = "LibraryOfText.db";
    private static final int DATABASE_VERSION = 1;

    /* DataBase creating sql statement */
    private static final String DATABASE_CREATE ="create table " + TABLE_LIBRARY + "( " +
            COLUMN_LIB_ITEM_ID + " INTEGER primary key autoincrement, " +
            COLUMN_LIB_ITEM_NAME + " TEXT not null," +
            COLUMN_LIB_ITEM_LAST_SETTING + " INTEGER" +
            " );";

    //constructor
    public LibraryOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates SQLite database if the database is accessed but not yet created.
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DATABASE_CREATE);
    }

    /**
     *  Method deletes all existing data and re-creates the table.
     *  Is called, if the database version is increased in application code. This method allows
     *  an update  of an existing database schema or to drop the existing database and recreate it
     *  via the onCreate() method.
     * @param db         SQL database to be updated
     * @param oldVersion previous database version
     * @param newVersion new database version
     *
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LibraryOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIBRARY);
        onCreate(db);
    }
}
