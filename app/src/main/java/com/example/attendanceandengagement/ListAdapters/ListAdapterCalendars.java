package com.example.attendanceandengagement.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.attendanceandengagement.R;

public class ListAdapterCalendars extends BaseAdapter {
    Context context;
    private final String [] displayName;
    private final String [] accountName;
    private final String [] ownerName;
    private final Long [] calID;

    public ListAdapterCalendars(Context context, String [] displayNames, String [] accountNames, String [] ownerNames, Long[] calIDs){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.displayName = displayNames;
        this.accountName = accountNames;
        this.ownerName = ownerNames;
        this.calID = calIDs;
    }

    @Override
    public int getCount() {
        return displayName.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {

        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.single_list_item_calendars, parent, false);

            viewHolder.displayName = (TextView) convertView.findViewById(R.id.displayName);
            viewHolder.accountName = (TextView) convertView.findViewById(R.id.accountName);
            viewHolder.ownerName = (TextView) convertView.findViewById(R.id.ownerName);
            viewHolder.calID = (TextView) convertView.findViewById(R.id.calID);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.displayName.setText("Name: "+displayName[position]);
        viewHolder.accountName.setText("Account: "+accountName[position]);
        viewHolder.ownerName.setText("Calendar type: "+ownerName[position].substring(ownerName[position].indexOf('@')+1));
        viewHolder.calID.setText("#"+calID[position]);

        return result;
    }

    private static class ViewHolder {

        TextView displayName;
        TextView accountName;
        TextView ownerName;
        TextView calID;
    }
}