/*
 * Copyright (c) 2017.  $author
 */

package tcss450.uw.edu.getinmyhead;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {
    final String ADD_USER_URL = "http://cssgate.insttech.washington.edu/~hindsr/Android/adduser.php?";

   EditText editTextUserName;
   EditText editTextUserPassword;
    Button submitButton;
    String finalResult = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Bundle b = getIntent().getExtras();
        this.editTextUserName = (EditText)findViewById(R.id.edittext_user_email);
        this.editTextUserPassword = (EditText)findViewById(R.id.edittext_user_password);
        this.editTextUserName.setText(b.getString(getString(R.string.user_email)));
        this.editTextUserPassword.setText(b.getString(getString(R.string.user_password)));
        this.submitButton = (Button) findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String result = addUser(ADD_USER_URL);

                    Intent i = new Intent(RegisterActivity.this, LibraryDatabaseActivity.class);
                    i.putExtra(getString(R.string.user_email), editTextUserName.getText().toString());
                    i.putExtra(getString(R.string.user_password), editTextUserPassword.getText().toString());
                    startActivity(i);

              //  finalResult = "";
            }
        } );
    }


    /**
     * This method builds a http url string
     *
     * @return http login url on successful string creation or error string if unsuccessful
     * @author Robert Hinds
     */
    private String buildLoginURL(String URL) {
        StringBuilder sb = new StringBuilder(URL);

        try {

            sb.append("&email=");
            sb.append(URLEncoder.encode(this.editTextUserName.getText().toString(), "UTF-8"));

            sb.append("&password=");
            sb.append(URLEncoder.encode(this.editTextUserPassword.getText().toString(), "UTF-8"));

            Log.i("login url", sb.toString());

        } catch (Exception e) {
            return getString(R.string.error_in_url) + e.getMessage();
        }
        return sb.toString();
    }

    /**
     * Method to add a user to the remote database
     *
     * @param url http url string
     * @return result of the add user task
     */
    private String addUser(String url) {
        String loginURL = buildLoginURL(url);
        String result = "success";

        Log.i("login url" , loginURL);
            RegisterActivity.AddUserTask task = new RegisterActivity.AddUserTask();
            task.execute(new String[]{loginURL.toString()});

/*                Toast msg = Toast.makeText(getBaseContext(), finalResult, Toast.LENGTH_LONG);
                msg.show();
                result = getString(R.string.error_in_url);
            finalResult = "";*/

        //currently does not check if the the user was successfully added.
        // task.getStatus();
/*        try {
           result = task.get();
        }catch(Exception e){

        }*/
        return result;
    }

    /**
     * Represents an asynchronous add user to the remote database task
     *
     * @author Robert Hinds
     * @version 1.0
     */
    public class AddUserTask extends AsyncTask<String, Void, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to add user, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
           // finalResult = response;
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.

            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");

                if (status.equals("success")) {
                   Toast.makeText(getApplicationContext(), "User successfully added!"
                            , Toast.LENGTH_LONG)
                            .show();
                 //   finalResult = "User successfully added!";
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
               //   finalResult = "Failed to add: " + jsonObject.get("error");
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
           //  finalResult = "Something wrong with the data" + e.getMessage();
            }

        }

    }
}
