package net.androidbootcamp.blockr;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.preference.PreferenceManager;
import android.telecom.Call;
import android.telecom.TelecomManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Pattern;

//Class to receive call broadcast
public class CallReciever extends BroadcastReceiver {
    ArrayList<String> mnNumberList;
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref =PreferenceManager.getDefaultSharedPreferences(context);
        Boolean whiteListSwitch = sharedPref.getBoolean
                (SettingsActivity.KEY_PREF_WHITE_LIST_SWITCH, false);
        Boolean contactsSwitch = sharedPref.getBoolean
                (SettingsActivity.KEY_PREF_CONTACT_SWITCH, false);
        //Internal android class
        ITelephony telephonyService;
        try {
            mnNumberList = new ArrayList<>();
            //String for state of phone, such as ringing,idle
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            //String to hold telephone number of incoming call
            String incomingNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            //Only runs if phone state is ringing
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    Method m = tm.getClass().getDeclaredMethod("getITelephony");
                    m.setAccessible(true);
                    telephonyService = (ITelephony) m.invoke(tm);

                    //If not a contact endcall/ block
                    if (!isContact(context, incomingNumber) && contactsSwitch) {
                        telephonyService.endCall();
                        Toast.makeText(context, "Blocking " + incomingNumber, Toast.LENGTH_SHORT).show();

                    }//To DO
                    else if(!isWhiteList(context,incomingNumber) && whiteListSwitch){
                        telephonyService.endCall();
                        Toast.makeText(context, "Blocking " + incomingNumber, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }catch (Exception e) {
            e.printStackTrace();















        }
    }
    //Boolean method to test if incomingnumber is in contacts
    private boolean isContact(Context context,String incommingNumber) {
        //Create a uri for the incoming call number
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(incommingNumber));
        ContentResolver resolver = context.getContentResolver();
        //String array with information about contacts
        String[] phoneNumebers ={ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        //Object to test database of contacts against incomingnumber
        Cursor cursor = resolver.query(uri, phoneNumebers,null,null,null);

        boolean inContacts =false;
        try{
            //movetofirst lets you know if incomingnumber is stores in contacts database
            if(cursor!=null &&cursor.moveToFirst()) {
                inContacts=true;
            }


        }finally{
            if(cursor!=null){
                cursor.close();
            }
        }
        return inContacts;

    }
    //Boolean method to test if incomingnumber is in contacts
    private boolean isWhiteList(Context context,String incommingNumber) {

        try{
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = dbHelper.getData();


            while(cursor.moveToNext()){

                String primaryKey =(cursor.getString(1));

                Uri uri = ContactsContract.Data.CONTENT_URI;
                //Should be Phone.display name but number apparently brings the name and number
                String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER };
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
                String[] selectionArguments = { primaryKey };
                Cursor cursorContact = context.getContentResolver().query(uri, projection, selection, selectionArguments, null);

                if (cursorContact != null) {
                    while (cursorContact.moveToNext()) {
                        String number= cursorContact.getString(0);
                        if (!Pattern.matches("[a-zA-Z]+", number))
                            mnNumberList.add(number);


                    }

                }
            }

        }
        catch (SQLiteException e){
            Log.v("Stuff","Cant Read Database");
        }
        boolean inWhiteList = false;

        for (int i =0;i<mnNumberList.size();i++){
            if (PhoneNumberUtils.compare(incommingNumber, mnNumberList.get(i)))
            {
                inWhiteList = true;
            }


        }
        return inWhiteList;
    }


}