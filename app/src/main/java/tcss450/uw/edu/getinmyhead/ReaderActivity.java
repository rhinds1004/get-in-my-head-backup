/*
 * Copyright (c) 2017.  $author
 */

package tcss450.uw.edu.getinmyhead;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * container for the ReadTextFragment.
 * @author Robert Hinds
 * @version 5/22/2016
 */
public class ReaderActivity extends AppCompatActivity implements ReadTextFragment.OnFragmentInteractionListener{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        String mItemText =  getIntent().getExtras().getString(getString(R.string.key_item_text));
        int mLastSet = getIntent().getExtras().getInt(getString(R.string.key_last_setting));
        ReadTextFragment readTextFragment = new ReadTextFragment();
        Bundle args = new Bundle();
        args.putString(getString(R.string.key_item_text), mItemText); // passing values of the libItem selected to readTextFragment
        args.putInt(getString(R.string.key_last_setting), mLastSet);
        readTextFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, readTextFragment)
                .commit();
/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_feedback:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:GiMHTeam@gmail.com")); // only email apps should handle this
                // intent.putExtra(Intent.EXTRA_EMAIL, address[0]);
                intent.putExtra(Intent.EXTRA_SUBJECT, "User Feedback!");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}