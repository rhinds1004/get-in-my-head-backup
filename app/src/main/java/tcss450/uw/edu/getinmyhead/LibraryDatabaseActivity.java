package tcss450.uw.edu.getinmyhead;
/**
 * Created by Robert Hinds on 5/1/2017.
 * Used this tutorial as base. http://www.vogella.com/tutorials/AndroidSQLite/article.html
 */

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
                return true;
            }
        });
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