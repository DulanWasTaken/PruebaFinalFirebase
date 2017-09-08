package es.udc.tfg.pruebafinalfirebase.multipickcontact;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import es.udc.tfg.pruebafinalfirebase.Core.MainActivity;
import es.udc.tfg.pruebafinalfirebase.R;
import es.udc.tfg.pruebafinalfirebase.Utils.Utils;

public class MultiPickContactActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private int rc;
    private String TAG = "MultiPickContactActiv";
    public final static int CONTACTS_LOADER_ID = 666;

    private FloatingActionButton fab;
    private ActionBar ab;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<ContactItem> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_pick_contact);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        rc = getIntent().getIntExtra("rc", MainActivity.RC_PHONE_CONTACTS);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Group creation");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(MultiPickContactActivity.this);
                if(rc == MainActivity.RC_PHONE_CONTACTS)
                    et.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(MultiPickContactActivity.this)
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
                                    ContactItem tempContact = new ContactItem("temp",phoneNumber,null);
                                    tempContact.setChecked(true);
                                    adapter.addItemToDataset(tempContact,0);
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
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

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
        contacts = contactsFromCursor(cursor);
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
                 ContactsContract.CommonDataKinds.Phone.NUMBER,
                 ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        } ;
        else if(rc == MainActivity.RC_EMAIL_CONTACTS)
            projection = new String[]{                                  // The columns to return for each row
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Email.ADDRESS,
                    ContactsContract.CommonDataKinds.Email.PHOTO_URI
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
                String name = cursor.getString(cursor.getColumnIndex(rc==MainActivity.RC_PHONE_CONTACTS? ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME: ContactsContract.Contacts.DISPLAY_NAME));
                String data = cursor.getString(cursor.getColumnIndex(rc==MainActivity.RC_PHONE_CONTACTS? ContactsContract.CommonDataKinds.Phone.NUMBER: ContactsContract.CommonDataKinds.Email.ADDRESS));
                String photo = cursor.getString(cursor.getColumnIndex(rc==MainActivity.RC_PHONE_CONTACTS? ContactsContract.CommonDataKinds.Phone.PHOTO_URI: ContactsContract.CommonDataKinds.Email.PHOTO_URI));
                Log.d(TAG,"Name: "+name+"data: "+data+"photo: "+photo);
                ContactItem contact = new ContactItem(name,data,photo);
                if(!contacts.contains(contact))
                    contacts.add(contact);
            } while (cursor.moveToNext());
        }

        return contacts;
    }

    /***************************************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.multi_pick_contact_menu,menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final ArrayList<ContactItem> filteredContacts = new ArrayList<ContactItem>();
                for(ContactItem contact : contacts){
                    if(contact.getName().toLowerCase().contains(newText))
                        filteredContacts.add(contact);
                }
                mRecyclerView.setAdapter(new ContactsRecyclerViewAdapter(filteredContacts));
                mRecyclerView.scrollToPosition(0);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                ///ContactsRecyclerViewAdapter adapter = (ContactsRecyclerViewAdapter) mRecyclerView.getAdapter();
                ArrayList<String> selectedContacts = new ArrayList<>();
                for(ContactItem contact : contacts){
                    if(contact.isChecked()) {
                        selectedContacts.add(rc == MainActivity.RC_EMAIL_CONTACTS ? Utils.generateValidEmail(contact.getData()) : Utils.generateValidPhoneNumber(contact.getData()));
                    }
                }
                if(selectedContacts.isEmpty())
                    Toast.makeText(MultiPickContactActivity.this,"No contact selected",Toast.LENGTH_SHORT).show();
                else{
                    Intent data = new Intent();
                    data.putExtra("selectedContacts", selectedContacts);
                    setResult(RESULT_OK, data);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
