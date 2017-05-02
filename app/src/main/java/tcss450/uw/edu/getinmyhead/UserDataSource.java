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

/** Data Access object (DAO)
 * Maintains database connection and supports adding new comments and fetching all comments.
 */
public class UserDataSource {

    //Database fields
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;
    private String[] allColumns = { UserOpenHelper.COLUMN_USER_ID, UserOpenHelper.COLUMN_USER_NAME,
            UserOpenHelper.COLUMN_PASSWORD};

    /** Creates a new Database
     *
     * @param context
     */
    public UserDataSource(Context context){
        dbHelper = new UserOpenHelper(context);
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

    /** Adds a User to the Database
     *
     * @param myUsername
     * @param myPassword
     * @return
     */
    public User createUser( String myUsername, String myPassword){
        ContentValues values = new ContentValues(); // allows to define key/values. The key represents the table column identifier and the value represents the content for the table record in this column.
        values.put(UserOpenHelper.COLUMN_USER_NAME, myUsername);
        values.put(UserOpenHelper.COLUMN_PASSWORD, myPassword);
        long insertID = database.insert(UserOpenHelper.TABLE_USER, null, values);
        Cursor cursor = database.query(UserOpenHelper.TABLE_USER, allColumns,
                UserOpenHelper.COLUMN_USER_ID + " = " + insertID, null, null, null, null);
        cursor.moveToFirst();
        User newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;
        
    }

    /** Deletes a User from the Database
     *
     * @param user
     */
    public void deleteUser(User user) {
        long id = user.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(UserOpenHelper.TABLE_USER, UserOpenHelper.COLUMN_USER_ID
                + " = " + id, null);
    }

    /** Helper function that sets the data fields for the User object.
     *
     * @param cursor
     * @return User class
     */
    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(0));
        user.setUsername(cursor.getString(1));
        user.setPassword(cursor.getString(2));
        return user;
    }

}
