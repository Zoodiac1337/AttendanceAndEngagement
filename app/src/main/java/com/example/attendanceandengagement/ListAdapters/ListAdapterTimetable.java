package com.example.attendanceandengagement.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.attendanceandengagement.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ListAdapterTimetable extends BaseAdapter {
    Context context;
    private final String [] title;
    private final Long [] beginVal;
    private final Long [] endVal;
    private final Long [] eventID;

    public ListAdapterTimetable(Context context, String [] titles, Long [] beginVals, Long [] endVals, Long[] eventIDs){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.title = titles;
        this.beginVal = beginVals;
        this.endVal = endVals;
        this.eventID = eventIDs;
    }

    @Override
    public int getCount() {
        return title.length;
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
            convertView = inflater.inflate(R.layout.single_list_item_timetable, parent, false);

            viewHolder.day_text = (TextView) convertView.findViewById(R.id.day_text);
            viewHolder.day_number = (TextView) convertView.findViewById(R.id.day_number);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.event = (LinearLayout) convertView.findViewById(R.id.event);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Calendar begin = Calendar.getInstance();
        begin.setTimeInMillis(beginVal[position]);
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(endVal[position]);

        DateFormat time = new SimpleDateFormat("HH:mm");
        DateFormat day = new SimpleDateFormat("dd.MM");
        DateFormat dayOfWeek = new SimpleDateFormat("EEE");

        viewHolder.day_text.setText(dayOfWeek.format(begin.getTime()));
        viewHolder.day_number.setText(day.format(begin.getTime()));
        viewHolder.time.setText(time.format(begin.getTime())+"-"+time.format(end.getTime()));
        viewHolder.title.setText(title[position]);

        if (position!=0) {
            Calendar previousBegin = Calendar.getInstance();
            previousBegin.setTimeInMillis(beginVal[position - 1]);

            if (day.format(begin.getTime()).equals(day.format(previousBegin.getTime()))){
                viewHolder.day_text.setText("");
                viewHolder.day_number.setText("");
            }
            else viewHolder.event.setPadding(0,20,0,0);
        }
        else viewHolder.event.setPadding(0,20,0,0);

        return result;
    }

    private static class ViewHolder {

        TextView day_text;
        TextView day_number;
        TextView time;
        TextView title;
        LinearLayout event;
    }
}