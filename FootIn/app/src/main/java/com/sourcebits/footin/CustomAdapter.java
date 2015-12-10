package com.sourcebits.footin;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by sunavar on 08/12/15.
 */
public class CustomAdapter extends CursorAdapter
{
    private Context mContext;
    public CustomAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;
    }

    private    static  class   ViewHolder  {

        TextView   placeName;
    }

    public void bindView(View view, Context context, Cursor cursor) {

        // Get all the values
        // Use it however you need to
        ViewHolder holder  =   (ViewHolder)    view.getTag();

        //TextView PlaceName = (TextView)view.findViewById(R.id.Place_name);
        holder.placeName.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));


    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.single_row_item, parent, false);

        ViewHolder mViewHolder = new ViewHolder();
        mViewHolder.placeName = (TextView)retView.findViewById(R.id.Place_name);

        retView.setTag(mViewHolder);

        return retView;
    }
}
