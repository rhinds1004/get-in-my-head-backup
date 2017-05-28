/*
 * Copyright (c) 2017.  Robert Hinds
 */

package tcss450.uw.edu.getinmyhead;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static android.Manifest.permission.READ_CONTACTS;
import static java.lang.Thread.sleep;

/**
 * A login screen that offers login via email/password. Uses Google login activity as base code.
 * Also uses methods by Menaka Abraham.
 *
 * @author Robert Hinds
 * @author Menaka Abraham
 * @version 1.0
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, UserRegisterDialogFragment.NoticeDialogListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    /**
     * Email validation pattern.
     */
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox checkBox;
    public static final String PREFS_NAME = "LOGIN_PREFS";
    private SharedPreferences loginSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();

                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        loginSettings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
   //     loginSettings =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if(loginSettings.getBoolean(getString(R.string.stored_login_info), true) ){
            // Store values at the time of the login attempt.
            mEmailView.setText(loginSettings.getString(getString(R.string.user_email),null));
            mPasswordView.setText(loginSettings.getString(getString(R.string.user_password),null));
        }
        checkBox = (CheckBox) findViewById(R.id.checkBox_store_login_info);

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(this, "No network connection available. Cannot authenticate user",
                    Toast.LENGTH_SHORT) .show();
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            loginSettings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = loginSettings.edit();
            if (checkBox.isChecked()) {


                editor.putBoolean(getString(R.string.stored_login_info), true);
                editor.putString(getString(R.string.user_email), email);
                editor.putString(getString(R.string.user_password), password);

            }else{
                editor.putBoolean(getString(R.string.stored_login_info), false);
            }
            editor.commit();
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);

        }
    }

    private boolean isEmailValid(String email) {
        //: Replace this with your own logic
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        //: Replace this with your own logic
        return password.length() > 5;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        i.putExtra(getString(R.string.user_email), dialog.getArguments().getString(getString(R.string.user_email)));
        i.putExtra(getString(R.string.user_password), dialog.getArguments().getString(getString(R.string.user_password)));
        startActivity(i);
        System.out.println("click");

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     *
     * @author Robert Hinds
     * @version 1.0
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean>  {
      //  private final DialogFragment myFrag = new UserRegisterDialogFragment();
        private final String mEmail;
        private final String mPassword;
        private String errorType = "";
        //location of the remote server
        final String LOGIN_URL = "http://cssgate.insttech.washington.edu/~hindsr/Android/login.php?";
        final String ADD_USER_URL = "http://cssgate.insttech.washington.edu/~hindsr/Android/adduser.php?";

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        /**
         * Checks to if user login attempt is valid by connecting to remote host.
         *
         * @param params
         * @return the result of the login attempt
         * @author Robert Hinds
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;
            errorType = doUserLogin(buildLoginURL(LOGIN_URL));
            if (errorType.contains("success")) {
                result = true;
            } else {
                if (errorType.contains("email")) {
                   // if (addUser(buildLoginURL(ADD_USER_URL)).contains("success")) {
                     //   result = true;
                   // }
                }
            }

            return result;
        }

        /**
         * Method start LibraryDataBaseActivity if param is true. If param is false determines
         * which part of the login attempt failed. Depending on which part of the login attempt
         * failed, the appropriate error string is set and reqestfocus() is set on the view item
         * that failed during the login attempt.
         *
         * @param success the result of the login attempt
         * @author Robert Hinds
         */
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            //if username is correct and password is correct go into library database activity.
            //TODO change to up Main MENU UI
            if (success) {

                Log.i("LoginActivity: ", "Success...before calling startActionDownload");
                SyncUserItems.startActionDownload(LoginActivity.this, this.mEmail);
                Log.i("LoginActivity: ", "Success...after calling startActionDownload");

                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent(LoginActivity.this, LibraryDatabaseActivity.class);
                i.putExtra(getString(R.string.user_email), this.mEmail);
                i.putExtra(getString(R.string.user_password), this.mPassword);
                startActivity(i);
                finish();
            } else {
                if (errorType.contains("email")) {
                    if( errorType.contains("email")){

                        Bundle args = new Bundle();
                        args.putString(getString(R.string.user_email), this.mEmail);
                        args.putString(getString(R.string.user_password), this.mPassword);

                        DialogFragment myFrag = new UserRegisterDialogFragment();
                        myFrag.setArguments(args);
                        myFrag.show(getSupportFragmentManager(), "register");

                    }
                    errorType = getString(R.string.error_incorrect_email);
                    mEmailView.setError(errorType);
                    mEmailView.requestFocus();

                } else if (errorType.contains("password")) {
                    errorType = getString(R.string.error_incorrect_password);
                    mPasswordView.setError(errorType);
                    mPasswordView.requestFocus();

                } else if (errorType.contains("Something")) {
                    errorType = getString(R.string.error_in_url);
                    Toast.makeText(getApplicationContext(), R.string.error_in_url, Toast.LENGTH_LONG).show();

                } else {
                    errorType = getString(R.string.error_unknown);
                    mEmailView.setError(errorType);
                    mEmailView.requestFocus();
                }

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
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
                sb.append(URLEncoder.encode(this.mEmail, "UTF-8"));

                sb.append("&password=");
                sb.append(URLEncoder.encode(this.mPassword, "UTF-8"));

                Log.i("login url", sb.toString());

            } catch (Exception e) {
                return getString(R.string.error_in_url) + e.getMessage();
            }
            return sb.toString();
        }

        /**
         * This method uses http url string to authenticate a user on a remote server.
         *
         * @param urls
         * @return an empty string on successful login and error message string on unsuccessful
         * login
         * @author Robert Hinds
         */
        protected String doUserLogin(String... urls) {
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
                    response = "Unable to verify user, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


    }

    /**
     * Method to add a user to the remote database
     *
     * @param url http url string
     * @return result of the add user task
     */
    private String addUser(String url) {
        AddUserTask task = new AddUserTask();
        task.execute(new String[]{url.toString()});
        String result = "success";
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
    private class AddUserTask extends AsyncTask<String, Void, String> {


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
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            showProgress(false);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "User successfully added!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}