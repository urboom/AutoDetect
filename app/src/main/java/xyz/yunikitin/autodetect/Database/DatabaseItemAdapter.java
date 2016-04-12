package xyz.yunikitin.autodetect.Database;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import xyz.yunikitin.autodetect.Database.DatabaseItem;
import xyz.yunikitin.autodetect.R;

/**
 * Adapter to bind a ToDoItem List to a view
 */
public class DatabaseItemAdapter extends ArrayAdapter<DatabaseItem> {

    /**
     * Adapter context
     */
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public DatabaseItemAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }
    /**
     * Returns the view for a specific item on the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final DatabaseItem currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }
        row.setTag(currentItem);
        final TextView textDatabaseField = (TextView) row.findViewById(R.id.textDatabaseField);
        textDatabaseField.setText(currentItem.getNumberPlate());
        return row;
    }
}