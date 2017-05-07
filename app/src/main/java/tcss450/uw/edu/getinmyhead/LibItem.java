package tcss450.uw.edu.getinmyhead;

/**
 * Created by hinds on 5/1/2017.
 */

public class LibItem {
    private long id;
    private String title;
    private Integer lastSetting;


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

    public Integer getLastSetting() {
        return lastSetting;
    }

    public void setLastSetting(Integer lastSetting) {
        this.lastSetting = lastSetting;
    }

    //Used by ArrayAdapter in the ListView
    @Override
    public String toString(){
        return title;
    }
}
