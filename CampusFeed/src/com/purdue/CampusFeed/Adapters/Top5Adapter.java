package com.purdue.CampusFeed.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.R;
import com.purdue.CampusFeed.Utils.Utils;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import java.util.ArrayList;

/**
 * User: AnubhawArya
 * Date: 4/22/14
 * Time: 4:02 PM
 */
public class Top5Adapter extends ArrayAdapter<Event> implements StickyListHeadersAdapter {

    public Top5Adapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.browseevents_rowlayout, events);
    }

    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.headerText);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        String headerText = "" + Utils.categories[((int) getHeaderId(position))];
        holder.text.setText(headerText);
        if (headerText.equals("Recreation"))
            holder.text.setBackgroundColor(Color.parseColor("#2ecc71"));
        else if (headerText.equals("Charity"))
            holder.text.setBackgroundColor(Color.parseColor("#3498db"));
        else if (headerText.equals("Social"))
            holder.text.setBackgroundColor(Color.parseColor("#ffa400"));
        else if (headerText.equals("Education"))
            holder.text.setBackgroundColor(Color.parseColor("#9b59b6"));
        else if (headerText.equals("University Event"))
            holder.text.setBackgroundColor(Color.parseColor("#e58974"));
        else
            holder.text.setBackgroundColor(Color.parseColor("#95a5a6"));
        return convertView;
    }

    public long getHeaderId(int position) {
        return (long) Math.floor((int) (((double) position) / 5.0));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
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

    class HeaderViewHolder {
        TextView text;
    }
}
