package tcss450.uw.edu.getinmyhead;

import android.app.ListActivity;
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

public class LibraryDatabaseActivity extends ListActivity {
    private LibraryDataSource datasource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_database);

        datasource = new LibraryDataSource(this);
        datasource.open();

        List<LibItem> values = datasource.getAllLibItems();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        ArrayAdapter<LibItem> adapter = new ArrayAdapter<LibItem>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);

        ListView listView = getListView();
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //TODO add logic so it opens the file when the listitem is clicked on.

                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Will be called via the onClick attribute
    // of the buttons in main.xml
    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<LibItem> adapter = (ArrayAdapter<LibItem>) getListAdapter();
        LibItem libItem = null;
        switch (view.getId()) {
            case R.id.add_libitem:

                String[] comments = new String[] { "Cool", "Very nice", "Hate it" };
                int nextInt = new Random().nextInt(3);
                // save the new comment to the database
                libItem = datasource.createLibItem(comments[nextInt], nextInt);
                adapter.add(libItem);
                break;
            case R.id.delete_libitem:
                if (getListAdapter().getCount() > 0) {
                    libItem = (LibItem) getListAdapter().getItem(0); // this is determing what is getting deleted.
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