package net.androidbootcamp.blockr;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.regex.Pattern;


public class WordListAdapter extends
        RecyclerView.Adapter<WordListAdapter.WordViewHolder> {

    private  Cursor mCursor;
    private Context mContext;
    private final LayoutInflater mInflater;

    class WordViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public final TextView wordItemView;
        final WordListAdapter mAdapter;

       //Constructor for view holder
        public WordViewHolder(View itemView, WordListAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.word);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }
        //Not used
        @Override
        public void onClick(View view) {

        }
    }
    //Constructor for word list adapter requires linkedlist
    public WordListAdapter(Context context, Cursor cursor) {
        mInflater = LayoutInflater.from(context);
        mCursor =cursor;
        mContext =context;

    }

    //Creation of ViewHolder
    @Override
    public WordListAdapter.WordViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // Inflate an item view.
        View mItemView = mInflater.inflate(
                R.layout.wordlist_item, parent, false);
        return new WordViewHolder(mItemView, this);
    }

    //Add information to  View
    @Override
    public void onBindViewHolder(WordListAdapter.WordViewHolder holder,
                                 int position) {
        // Retrieve the data for that position.
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        // Add the data to the view holder.
        Uri uri = ContactsContract.Data.CONTENT_URI;
        int contactId = mCursor.getInt(mCursor.getColumnIndex("contactID"));
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String[] selectionArguments = {Integer.toString(contactId)};
        Cursor cursorContact = mContext.getContentResolver().query(uri, projection, selection, selectionArguments, null);

        if (cursorContact != null) {
            while (cursorContact.moveToNext()) {
                String number = cursorContact.getString(0);

                    holder.wordItemView.setText(number);

                holder.itemView.setTag(contactId);
            }
        }
    }
    //return how big arraylist is
    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void addItem(String contact)
    {
       // mWordList.add(contact);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
//         String mRecentlyDeletedItem = mWordList.get(position);
//        int  mRecentlyDeletedItemPosition = position;
//        mWordList.remove(position);
//        notifyItemRemoved(position);

    }
}