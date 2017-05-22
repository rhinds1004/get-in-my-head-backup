/*
 * Copyright (c) 2017.  $author
 */

package tcss450.uw.edu.getinmyhead;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class SyncUserItems extends IntentService {

    private static final String ACTION_DOWNLOAD = "tcss450.uw.edu.getinmyhead.action.ACTION_DOWNLOAD";
    private static final String ACTION_UPLOAD = "tcss450.uw.edu.getinmyhead.action.ACTION_DOWNLOAD";
    public static final String USER_EMAIL = "Email";
    private LibraryDataSource datasource;


    public SyncUserItems() {
        super("SyncUserItems");
    }

    /**
     * Starts this service to perform action DOWNLOAD with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionDownload(Context context, String param1) {
        Log.i("SyncUserItems: ", "Inside startActionDownload()");
        Intent intent = new Intent(context, SyncUserItems.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(USER_EMAIL, param1);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action UPLOAD with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionUpload(Context context, String param1) {
        Intent intent = new Intent(context, SyncUserItems.class);
        intent.putExtra(USER_EMAIL, param1);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("SyncUserItems: ", "Inside onHandleIntent()");
        if (intent != null) {
            Log.i("SyncUserItems: ", "Intent is not null");
            final String action = intent.getAction();
            Log.i("SyncUserItems: ", "The action is "+ action);
            if (ACTION_DOWNLOAD.equals(action)) {
                Log.i("SyncUserItems: ", "Action_Download is equal to the action.");
                final String param1 = intent.getStringExtra(USER_EMAIL);
                handleActionDownload(param1);
            } else if (ACTION_UPLOAD.equals(action)) {
                final String param1 = intent.getStringExtra(USER_EMAIL);
                handleActionUpload(param1);
            }
        }
    }

    /**
     * Handle action Download in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDownload(String param1) {
        Log.i("SyncUserItems: ", "Success! Inside handleActionDownload"+param1);
        String result = "";
        HttpURLConnection urlConnection = null;
        URL urlObject = null;
        try {
            Log.i("SyncUserItems: ", "inside Try");
            urlObject = new URL(("http://cssgate.insttech.washington.edu/~_450bteam14/library.php?useritems=me@uw.edu"));
            urlConnection = (HttpURLConnection) urlObject.openConnection();
            Log.i("SyncUserItems: ", "After URL Connection");
            InputStream content = urlConnection.getInputStream();
            Log.i("SyncUserItems: ", "After input stream");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            Log.i("SyncUserItems: ", "After buffer read.");
            String s = "";
            while ((s = buffer.readLine()) != null) {
                 result += s;
            }
            JSONArray arr = new JSONArray(result);
            datasource = new LibraryDataSource(this);
            datasource.open();
            datasource.upgrade();
            for(int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                datasource.createLibItem(obj.getString("title"), 5);
            }

        } catch (MalformedURLException e) {
            Log.i("SyncUserItems: ", "Malformed URL Exception " + e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("SyncUserItems: ", "IOException " + e);
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpload(String param1) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
