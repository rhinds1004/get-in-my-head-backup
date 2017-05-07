package tcss450.uw.edu.getinmyhead;

/**
 * Created by hinds on 5/1/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/** Data Access object (DAO)
 * Maintains database connection and supports adding new comments and fetching all comments.
 */
public class LibraryDataSource {

    //Database fields
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;
    private String[] allColumns = { LibraryOpenHelper.COLUMN_LIB_ITEM_ID, LibraryOpenHelper.COLUMN_LIB_ITEM_NAME,
            LibraryOpenHelper.COLUMN_LIB_ITEM_LAST_SETTING};

    /** Creates a new Database
     *
     * @param context
     */
    public LibraryDataSource(Context context){
        dbHelper = new LibraryOpenHelper(context);
    }

    /** Opens a writable SQL database
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /** Closes Database
     *
     */
    public void close(){
        dbHelper.close();
    }

    /**
     * Adds a LibItem to the Database
     * @param myLibItemTitle
     * @param myLibItemLastSetting
     * @return
     */
    public LibItem createLibItem(String myLibItemTitle, Integer myLibItemLastSetting){
        ContentValues values = new ContentValues(); // allows to define key/values. The key represents the table column identifier and the value represents the content for the table record in this column.
        values.put(LibraryOpenHelper.COLUMN_LIB_ITEM_NAME, myLibItemTitle);
        values.put(LibraryOpenHelper.COLUMN_LIB_ITEM_LAST_SETTING, myLibItemLastSetting);
        long insertID = database.insert(LibraryOpenHelper.TABLE_LIBRARY, null, values);
        Cursor cursor = database.query(LibraryOpenHelper.TABLE_LIBRARY, allColumns,
                LibraryOpenHelper.COLUMN_LIB_ITEM_ID + " = " + insertID, null, null, null, null);
        cursor.moveToFirst();
        LibItem mLibItem = cursorToLibItem(cursor);
        cursor.close();
        return mLibItem;
    }

    /**
     *  Deletes a LibItem from the Database
     * @param mLibItem
     */
    public void deleteLibItem(LibItem mLibItem) {
        long id = mLibItem.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(LibraryOpenHelper.TABLE_LIBRARY, LibraryOpenHelper.COLUMN_LIB_ITEM_ID
                + " = " + id, null);
    }

    public List<LibItem> getAllLibItems(){
        List<LibItem> libitems = new ArrayList<LibItem>();

        Cursor cursor = database.query(LibraryOpenHelper.TABLE_LIBRARY, allColumns, null, null, null,
                null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            LibItem libitem = cursorToLibItem(cursor);
            libitems.add(libitem);
            cursor.moveToNext();
        }
        //make sure to close cursor
        cursor.close();
        return libitems;
    }

    /** Helper function that sets the data fields for the LibItem object.
     *
     * @param cursor
     * @return LibItem class
     */
    private LibItem cursorToLibItem(Cursor cursor) {
        LibItem mLibItem = new LibItem();
        mLibItem.setId(cursor.getLong(0));
        mLibItem.setTitle(cursor.getString(1));
        mLibItem.setLastSetting(Integer.getInteger(cursor.getString(2)));
        return mLibItem;
    }

}
