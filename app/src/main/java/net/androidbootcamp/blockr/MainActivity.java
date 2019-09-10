package net.androidbootcamp.blockr;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    //Int to describe permssion request channel
    private static final int  REQUEST_PERMISSIONS = 112;

    private final LinkedList<String> mNameList = new LinkedList<>();
    private final LinkedList<String> mPrimaryKeyList = new LinkedList<>();
    private RecyclerView mRecyclerView;
    private WordListAdapter mAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("WhiteList");
        setContentView(R.layout.activity_main);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Change to add activity when clicking Fab button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addContact = new Intent(MainActivity.this, WhiteListAdd.class);
                startActivity(addContact);

            }
        });



        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.getData();
        // Get a handle to the RecyclerView.
        mRecyclerView = findViewById(R.id.recyclerview);
        // Create an adapter and supply the data to be displayed.
        mAdapter = new WordListAdapter(this, cursor);
        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(mAdapter,this));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        //request permissions
        final String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS,
                Manifest.permission.CALL_PHONE};

        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);



        //Get Settings from preferences
        SharedPreferences sharedPref =
                android.support.v7.preference.PreferenceManager
                        .getDefaultSharedPreferences(this);
        Boolean whiteListSwitch = sharedPref.getBoolean
                (SettingsActivity.KEY_PREF_WHITE_LIST_SWITCH, false);
        Boolean contactsSwitch = sharedPref.getBoolean
                (SettingsActivity.KEY_PREF_CONTACT_SWITCH, false);
        Toast.makeText(this, "Contacts: " + contactsSwitch.toString() + "Whitelist: " + whiteListSwitch.toString(),
                Toast.LENGTH_SHORT).show();

        //Shared preferences turn off of on broadcast
        if (!contactsSwitch && !whiteListSwitch) {
            deRegisterForBroadcasts(this);
        } else {
            registerForBroadcasts(this);
        }

    }
    // Turn on broadcast
    public void registerForBroadcasts(Context context) {
        ComponentName component = new ComponentName(MainActivity.this, CallReciever.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                component,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    //Turn off broadcast
    public void deRegisterForBroadcasts(Context context) {
        ComponentName component = new ComponentName(MainActivity.this, CallReciever.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                component,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
