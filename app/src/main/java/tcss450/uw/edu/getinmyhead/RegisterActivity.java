/*
 * Copyright (c) 2017.  $author
 */

package tcss450.uw.edu.getinmyhead;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Bundle b = getIntent().getExtras();
        this.editTextUserName = (EditText)findViewById(R.id.edittext_user_email);
        this.editTextUserPassword = (EditText)findViewById(R.id.edittext_user_password);
        this.editTextUserName.setText(b.getString(getString(R.string.user_email)));
        this.editTextUserPassword.setText(b.getString(getString(R.string.user_password)));
        this.submitButton = (Button) findViewById(R.id.button_submit_reg);
        this.cancelButton = (Button) findViewById(R.id.button_cancel_reg);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addUser(ADD_USER_URL);
            }
        } );
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }


    /**
     * This method builds a http url string
     *
     * @return http login url on successful string creation
     * @throws buildLoginURLException
     * @author Robert Hinds
     */
    private String buildLoginURL(String URL) throws buildLoginURLException {
        StringBuilder sb = new StringBuilder(URL);

        try {

            sb.append("&email=");
            sb.append(URLEncoder.encode(this.editTextUserName.getText().toString(), "UTF-8"));

            sb.append("&password=");
            sb.append(URLEncoder.encode(this.editTextUserPassword.getText().toString(), "UTF-8"));
            Log.i("login url", sb.toString());
        } catch (Exception e) {
            throw new buildLoginURLException(getString(R.string.error_in_url) + e.getMessage());
        }
        return sb.toString();
    }

    /**
     * Method to add a user to the remote database
     * Checks network connection and displays an error message if a network isn't available.
     * Displays an error message if buildLoginUrl method is unsuccessful.
     *
     * @param url http url string
     * @modified 5/27/2017 Robert Hinds
     *
     */
    private void addUser(String url) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                String loginURL = buildLoginURL(url);
                Log.i("login url", loginURL);
                RegisterActivity.AddUserTask task = new RegisterActivity.AddUserTask();
                task.execute(new String[]{loginURL.toString()});

            } catch (buildLoginURLException e) {
                e.printStackTrace();
                Toast msg = Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG);
                msg.show();
            }
        } else {
            Toast.makeText(this, "No network connection available. Cannot authenticate user",
                    Toast.LENGTH_SHORT) .show();
        }
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
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If successful starts the User's Library Database
         * If not, it displays the exception.
         *
         * @param result
         * @modified 5/27/2017 Robert Hinds
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            String status = "fail";

            try {
                JSONObject jsonObject = new JSONObject(result);
                 status = (String) jsonObject.get("result");

                if (status.equals("success")) {
                   Toast.makeText(getApplicationContext(), "User successfully added!",
                           Toast.LENGTH_LONG).show();

                        Intent i = new Intent(RegisterActivity.this, LibraryDatabaseActivity.class);
                        i.putExtra(getString(R.string.user_email), editTextUserName.getText().toString());
                        i.putExtra(getString(R.string.user_password), editTextUserPassword.getText().toString());
                        startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("error"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

    /**
     * buildLoginURL's exception class
     */
    public class buildLoginURLException extends Exception {

        public buildLoginURLException(String message){
            super(message);
        }

    }


}
