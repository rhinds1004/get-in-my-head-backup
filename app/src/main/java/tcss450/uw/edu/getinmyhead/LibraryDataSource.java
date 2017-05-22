package tcss450.uw.edu.getinmyhead;

/**
 * Created by Robert Hinds on 5/1/2017.
 * Used this tutorial as base. http://www.vogella.com/tutorials/AndroidSQLite/article.html
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** Data Access object (DAO)
 * Maintains database connection and supports adding new LibItem objects and fetching all LibItems
 * currently in the SQL dataBase.
 * @author Robert Hinds
 * @version 1.0
 */
public class LibraryDataSource {

    //Database fields
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;
    private String[] allColumns = { LibraryOpenHelper.COLUMN_LIB_ITEM_ID, LibraryOpenHelper.COLUMN_LIB_ITEM_NAME,
            LibraryOpenHelper.COLUMN_LIB_ITEM_LAST_SETTING, LibraryOpenHelper.COLUMN_LIB_ITEM_TEXT};

    /**
     * Creates a new Database
     * @param context
     */
    public LibraryDataSource(Context context){
        dbHelper = new LibraryOpenHelper(context);
    }

    /**
     * Opens a writable SQL database
     * @throws SQLException
     */
    public void open() throws SQLException {

        database = dbHelper.getWritableDatabase();
       // dbHelper.onCreate(database);
    }

    /**
     * Closes Database
     */
    public void close(){
        dbHelper.close();
    }

    /**
     * Adds a LibItem to the Database
     * @param myLibItemTitle title of the text
     * @param myLibItemLastSetting the last setting used by the user
     * @return newly created LibItem
     * @author Robert Hinds
     */
    public LibItem createLibItem(String myLibItemTitle, Integer myLibItemLastSetting, String myLibItemText){
        ContentValues values = new ContentValues(); // allows to define key/values. The key represents the table column identifier and the value represents the content for the table record in this column.
        values.put(LibraryOpenHelper.COLUMN_LIB_ITEM_NAME, myLibItemTitle);
        values.put(LibraryOpenHelper.COLUMN_LIB_ITEM_LAST_SETTING, myLibItemLastSetting);
        values.put(LibraryOpenHelper.COLUMN_LIB_ITEM_TEXT, myLibItemText);
        long insertID = database.insert(LibraryOpenHelper.TABLE_NAME, null, values);
        Cursor cursor = database.query(LibraryOpenHelper.TABLE_NAME, allColumns,
                LibraryOpenHelper.COLUMN_LIB_ITEM_ID + " = " + insertID, null, null, null, null);
        cursor.moveToFirst();
        LibItem mLibItem = cursorToLibItem(cursor);
        cursor.close();
        return mLibItem;
    }

    /**
     * Adds a LibItem to the Database
     * @return newly created LibItem
     * @author Robert Hinds
     */
    public void createLibItemsFromJsonArray(String myLibItems){
        try {
            JSONArray arr = new JSONArray(myLibItems);
            for(int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                createLibItem("Yay", 5);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a LibItem from the Database
     * @param mLibItem LibItem to be deleted
     * @author Robert Hinds
     */
    public void deleteLibItem(LibItem mLibItem) {
        long id = mLibItem.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(LibraryOpenHelper.TABLE_NAME, LibraryOpenHelper.COLUMN_LIB_ITEM_ID
                + " = " + id, null);
    }

    public List<LibItem> getAllLibItems(){
        List<LibItem> libItems = new ArrayList<LibItem>();

        Cursor cursor = database.query(LibraryOpenHelper.TABLE_NAME, allColumns, null, null, null,
                null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            LibItem libItem = cursorToLibItem(cursor);
            libItems.add(libItem);
            cursor.moveToNext();
        }
        //make sure to close cursor
        cursor.close();
        return libItems;
    }

    /**
     * Helper function that sets the data fields for the LibItem object.
     * @param cursor The element of the SQL query
     * @return LibItem class
     * @author Robert Hinds
     */
    private LibItem cursorToLibItem(Cursor cursor) {
        LibItem mLibItem = new LibItem();
        mLibItem.setId(cursor.getLong(0));
        mLibItem.setTitle(cursor.getString(1));
        mLibItem.setLastSetting(Integer.parseInt(cursor.getString(2)));
        mLibItem.setItemText(cursor.getString(3));
        return mLibItem;
    }

    public void upgrade() {

        dbHelper.onUpgrade(database, 1,1);
    }

}
