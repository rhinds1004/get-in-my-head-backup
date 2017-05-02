package tcss450.uw.edu.getinmyhead;

/**
 * Created by hinds on 5/1/2017.
 */

public class User {
    private long id;
    private String username;
    private String password;


    public long getId() {
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
