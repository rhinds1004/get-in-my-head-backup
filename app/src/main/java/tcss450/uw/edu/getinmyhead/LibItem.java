package tcss450.uw.edu.getinmyhead;

/**
 * Created by hinds on 5/1/2017.
 * Used this tutorial as base. http://www.vogella.com/tutorials/AndroidSQLite/article.html
 */

/**
 * Holds the information about an item in the users library
 * @author Robert Hinds
 * @version 1.0
 */
public class LibItem {
    private long id;
    private String title;
    private int lastSetting;
    private String mItemText;


    public long getId() {
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLastSetting() {
        return lastSetting;
    }

    public String getItemText(){
        return this.mItemText;
    }
    public void setItemText(String itemText){
         this.mItemText = itemText;
    }

    public void setLastSetting(int lastSetting) {
        this.lastSetting = lastSetting;
    }

    //Used by ArrayAdapter in the ListView
    @Override
    public String toString(){
        return title;
    }
}
