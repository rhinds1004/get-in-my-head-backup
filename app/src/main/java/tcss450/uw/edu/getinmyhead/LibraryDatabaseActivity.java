/**
 * Created by Robert Hinds on 5/1/2017.
 * Used this tutorial as base. http://www.vogella.com/tutorials/AndroidSQLite/article.html
 * TCSS 450
 *
 */
package tcss450.uw.edu.getinmyhead;

import android.app.Activity;
import android.app.IntentService;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

/**
 * Allows the user to interact with the local database
 * @author Robert Hinds
 */

//need to have this activity open up a table with user names
public class LibraryDatabaseActivity extends ListActivity {
    private LibraryDataSource datasource;
    private List<LibItem> values;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_database);

        datasource = new LibraryDataSource(this);
        datasource.open();

         values = datasource.getAllLibItems();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        ArrayAdapter<LibItem> adapter = new ArrayAdapter<LibItem>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);

        final ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        //short tap
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //TODO this probably needs to be worked on.
                Intent i = new Intent(LibraryDatabaseActivity.this, ReaderActivity.class);
                i.putExtra(getString(R.string.key_last_setting), values.get(position).getLastSetting());
                i.putExtra(getString(R.string.key_item_text), values.get(position).getItemText());
                startActivity(i);

            }
        });

        //long tap
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO add logic so a dialog window pops up to (delete, edit or something) on long click on listitem.
                Toast.makeText(getApplicationContext(), "Long click",
                        Toast.LENGTH_SHORT).show();
                //Log.i("Long Click: ", datasource.getSingleItem( (int)values.get(position).getId() ).getItemText());

                performFileSearch();
                return true;
            }
        });
    }


    private static final int READ_REQUEST_CODE = 42;
    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("text/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    Log.i("File Picker: ", readTextFromUri(uri));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        inputStream.close();
    reader.close();
        return stringBuilder.toString();
    }



    // Will be called via the onClick attribute
    // of the buttons in main.xml
    //TODO this is a placeholder function.
    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<LibItem> adapter = (ArrayAdapter<LibItem>) getListAdapter();
        LibItem libItem = null;
        switch (view.getId()) {
            case R.id.add_libitem:

                //Intent i = new Intent(this, ListFilesActivity.class);
                //startActivity(i);

                String[] comments = new String[] { "Cool", "Very nice", "Hate it" };
                int nextInt = new Random().nextInt(3);

                // save the new comment to the database
                libItem = datasource.createLibItem(comments[nextInt], 5 , getString(R.string.temp_item_text_string));

                adapter.add(libItem);
                break;
            case R.id.delete_libitem:
                if (getListAdapter().getCount() > 0) {
                    libItem = (LibItem) getListAdapter().getItem(0); // this is determining what is getting deleted.
                    datasource.deleteLibItem(libItem);
                    adapter.remove(libItem);
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

}