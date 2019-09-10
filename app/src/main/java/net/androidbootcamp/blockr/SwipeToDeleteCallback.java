package net.androidbootcamp.blockr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {


    private WordListAdapter mAdapter;
    private Context mContext;
    //Constructor for class
    public SwipeToDeleteCallback(WordListAdapter adapter,Context context) {
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
        mContext =context;

    }
    //Does nothing
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }
    //Deletes from database when swiped
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mAdapter.deleteItem(position);
        DatabaseHelper dbHelper = new DatabaseHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
         int delete = (int) viewHolder.itemView.getTag();
         String deleteString = Integer.toString(delete);
        db.delete("white_list_table","contactID=?",new String[] { String.valueOf(deleteString) });
    }
}
