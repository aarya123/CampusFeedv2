package com.purdue.CampusFeed.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.R;

import java.util.ArrayList;

/*
 * 
 * generates rows for the browse events fragment
 * 
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {

    ViewHolder holder;

    public EventArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.browseevents_rowlayout, events);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.browseevents_rowlayout, parent, false);
            holder = new ViewHolder();
            holder.eventImage = (ImageView) convertView.findViewById(R.id.event_image);
            holder.eventName = (TextView) convertView.findViewById(R.id.event_name);
            holder.eventTime = (TextView) convertView.findViewById(R.id.event_time);
            holder.eventDescription = (TextView) convertView.findViewById(R.id.event_location);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Event e = getItem(position);
        holder.eventName.setText(e.getEventName());
        holder.eventTime.setText(e.getDatetime());
        holder.eventDescription.setText(e.getEventDescription());
        holder.eventImage.setImageResource(R.drawable.purdue_symbol);
        return convertView;
    }

    public static class ViewHolder {
        public TextView eventName, eventTime, eventDescription;
        public ImageView eventImage;
    }
} 