package net.androidbootcamp.blockr;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WhiteListAdd extends AppCompatActivity {
    private ArrayList<Map<String, String>> contactList;
    private SimpleAdapter mAdapter;
    private AutoCompleteTextView nameContact;
    private EditText id;
    private Button submit;
    DatabaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_list_add);
        contactList = new ArrayList<Map<String, String>>();
        getContactList();
        id = findViewById(R.id.primaryKey);
        nameContact =  findViewById(R.id.contact_entry);
        nameContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            //When user clicks something in adapter view it fills the textview and invisible edittext
            public void onItemClick(AdapterView<?> av, View arg1, int index,
                                    long arg3) {
                Map<String, String> map = (Map<String, String>) av.getItemAtPosition(index);

                String name  = map.get("Name");
                //String number = map.get("Phone");
                String idText = map.get("ID");
                //nameContact.setText(""+name+" <"+number+">");
                nameContact.setText(name);
                id.setText(idText);



            }



        });
        //makes adapter with arraylist information using index of array list
        mAdapter = new SimpleAdapter(this, contactList, R.layout.custcontview,
                new String[] { "Name", "Phone", "Type" }, new int[] {
                R.id.contactName, R.id.contactNumber, R.id.contactType});
        nameContact.setAdapter(mAdapter);
        db = new DatabaseHelper(this);
        db.getWritableDatabase();
        submit = findViewById(R.id.add_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isContact(WhiteListAdd.this,nameContact.getText().toString())&& android.text.TextUtils.isDigitsOnly(id.getText())) {
                    db.addData(Integer.parseInt(id.getText().toString()));
                    Intent intent = new Intent(WhiteListAdd.this, MainActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(WhiteListAdd.this, "Not a Contact ", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
    //Adds data to Arraylist from contacts database, uses cursor to step through and pull information
    public void getContactList() {
        contactList.clear();
        Cursor people = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (people.moveToNext()) {
            String contactName = people.getString(people
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = people.getString(people
                    .getColumnIndex(ContactsContract.Contacts._ID));
            String hasPhone = people
                    .getString(people
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            //makes sure contact has phone number
            if ((Integer.parseInt(hasPhone) > 0)){
                // You now have the number so now query it like this
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,
                        null, null);
                //Moves through cursor until no data left
                while (phones.moveToNext()){
                    //store numbers and display a dialog letting the user select which.
                    String phoneNumber = phones.getString(
                            phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String numberType = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.TYPE));
                    Map<String, String> NamePhoneType = new HashMap<String, String>();
                    NamePhoneType.put("Name", contactName);
                    NamePhoneType.put("Phone", phoneNumber);
                    NamePhoneType.put("ID", contactId);
                    //code stands for each type
                    if(numberType.equals("0"))
                        NamePhoneType.put("Type", "Work");
                    else
                    if(numberType.equals("1"))
                        NamePhoneType.put("Type", "Home");
                    else if(numberType.equals("2"))
                        NamePhoneType.put("Type",  "Mobile");
                    else
                        NamePhoneType.put("Type", "Other");
                    //Then add this map to the list.
                    contactList.add(NamePhoneType);
                }
                phones.close();
            }
        }
        people.close();
    }
    //Method to test if incoming number is a contact
    private boolean isContact(Context context, String contactName) {

        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + contactName +"%'";
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor contactCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);

        boolean inContacts =false;
        try{
            //movetofirst lets you know if incomingnumber is stores in contacts database
            if(contactCur!=null &&contactCur.moveToFirst()) {
                inContacts=true;
            }


        }finally{
            if(contactCur!=null){
                contactCur.close();
            }
        }
        return inContacts;

    }


}