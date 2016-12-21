package es.udc.tfg.pruebafinalfirebase;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class multiPickContactActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private int rc;
    public final static int CONTACTS_LOADER_ID = 666;

    private FloatingActionButton fab;
    private ActionBar ab;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_pick_contact);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        rc = getIntent().getIntExtra("rc",MainActivity.RC_PHONE_CONTACTS);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Group creation");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(multiPickContactActivity.this);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(multiPickContactActivity.this)
                        .setTitle(rc==MainActivity.RC_PHONE_CONTACTS? "Phone number":"Email")
                        .setMessage(rc==MainActivity.RC_PHONE_CONTACTS? "Enter a phone number":"Enter an email")
                        .setView(et)
                        .setCancelable(true)
                        .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String phoneNumber = et.getText().toString();
                                if (!phoneNumber.equals("")){
                                    ContactsRecyclerViewAdapter adapter = (ContactsRecyclerViewAdapter) mRecyclerView.getAdapter();
                                    adapter.addItemToDataset(new ContactItem("temp",phoneNumber),0);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        getSupportLoaderManager().initLoader(CONTACTS_LOADER_ID, null,this);
    }

/************************** LOADER *******************************/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.

        if (id == CONTACTS_LOADER_ID) {
            return contactsLoader();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //The framework will take care of closing the
        // old cursor once we return.
        ArrayList<ContactItem> contacts = contactsFromCursor(cursor);
        mRecyclerView.setAdapter(new ContactsRecyclerViewAdapter(contacts));
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
    }

    private  Loader<Cursor> contactsLoader() {
        Uri contactsUri = rc==MainActivity.RC_PHONE_CONTACTS? ContactsContract.CommonDataKinds.Phone.CONTENT_URI:ContactsContract.CommonDataKinds.Email.CONTENT_URI; // The content URI of the phone contacts

        String[] projection = null;
        if (rc==MainActivity.RC_PHONE_CONTACTS)
         projection = new String[]{                                  // The columns to return for each row
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        } ;
        else if(rc == MainActivity.RC_EMAIL_CONTACTS)
            projection = new String[]{                                  // The columns to return for each row
                    ContactsContract.CommonDataKinds.Email.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Email.ADDRESS
            } ;

        String selection = null;                                 //Selection criteria
        String[] selectionArgs = {};                             //Selection criteria
        String sortOrder = rc == MainActivity.RC_PHONE_CONTACTS? ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC": ContactsContract.CommonDataKinds.Email.DISPLAY_NAME+" ASC";                                 //The sort order for the returned rows

        return new CursorLoader(
                getApplicationContext(),
                contactsUri,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    private ArrayList<ContactItem> contactsFromCursor(Cursor cursor) {
        ArrayList<ContactItem> contacts = new ArrayList<ContactItem>();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                String name = cursor.getString(cursor.getColumnIndex(rc==MainActivity.RC_PHONE_CONTACTS? ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME: ContactsContract.CommonDataKinds.Email.DISPLAY_NAME));
                String data = cursor.getString(cursor.getColumnIndex(rc==MainActivity.RC_PHONE_CONTACTS? ContactsContract.CommonDataKinds.Phone.NUMBER: ContactsContract.CommonDataKinds.Email.ADDRESS));
                contacts.add(new ContactItem(name,data));
            } while (cursor.moveToNext());
        }

        return contacts;
    }

    /***************************************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.multi_pick_contact_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                ContactsRecyclerViewAdapter adapter = (ContactsRecyclerViewAdapter) mRecyclerView.getAdapter();
                ArrayList<String> selectedContacts = new ArrayList<>();
                for(ContactItem contact : adapter.getmDataset()){
                    selectedContacts.add(rc==MainActivity.RC_EMAIL_CONTACTS? Utils.generateValidEmail(contact.getData()):contact.getData());
                }
                Intent data = new Intent();
                data.putExtra("selectedContacts", selectedContacts);
                setResult(RESULT_OK, data);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
