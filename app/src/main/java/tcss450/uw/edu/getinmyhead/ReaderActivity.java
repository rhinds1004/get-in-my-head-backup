/*
 * Copyright (c) 2017.  $author
 */

package tcss450.uw.edu.getinmyhead;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    public void onFragmentInteraction(Uri uri) {

    }
}