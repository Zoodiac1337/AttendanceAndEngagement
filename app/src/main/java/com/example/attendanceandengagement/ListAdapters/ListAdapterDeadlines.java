package com.example.attendanceandengagement.ListAdapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.attendanceandengagement.R;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ListAdapterDeadlines extends BaseAdapter {
    Context context;
    private final String [] name;
    private final Date[] date;
    private final String [] description;

    public ListAdapterDeadlines(Context context, String [] names, Date [] dates, String [] descriptions){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.name = names;
        this.date = dates;
        this.description = descriptions;
    }

    @Override
    public int getCount() {
        return name.length;
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
            convertView = inflater.inflate(R.layout.single_list_item_deadlines, parent, false);
            viewHolder.daysNumber = (TextView) convertView.findViewById(R.id.daysNumber);
            viewHolder.courseworkName = (TextView) convertView.findViewById(R.id.courseworkName);
            viewHolder.deadline = (TextView) convertView.findViewById(R.id.deadline);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        Date date1 =  new GregorianCalendar(2022, Calendar.OCTOBER, 18).getTime();
        long timeDiff = Math.abs(date[position].getTime() - date1.getTime());
        long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);

        viewHolder.daysNumber.setText(daysDiff+"");
        if (daysDiff<=10)
            viewHolder.daysNumber.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B21616")));
        else if (daysDiff<=20)
            viewHolder.daysNumber.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A45050")));
        else if (daysDiff<=40)
            viewHolder.daysNumber.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A49750")));
        else if (daysDiff<=60)
            viewHolder.daysNumber.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A2A450")));
        else if (daysDiff<=80)
            viewHolder.daysNumber.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6BA450")));
        else if (daysDiff<=100)
            viewHolder.daysNumber.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#52A450")));
        else
            viewHolder.daysNumber.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#197739")));


        viewHolder.courseworkName.setText(name[position]);
        viewHolder.deadline.setText(description[position]);

        return convertView;
    }

    private static class ViewHolder {

        TextView daysNumber;
        TextView courseworkName;
        TextView deadline;
    }
}