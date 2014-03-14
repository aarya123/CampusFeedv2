package com.purdue.CampusFeed;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/*
 * 
 * generates rows for the browse events fragment
 * 
 */
public class RowGenerator_ArrayAdapter extends ArrayAdapter<Event> {
  private final Context context;
  
  //testing
public static  ArrayList<Event> events=new ArrayList<Event>();
  //end test

  //constructor
  public RowGenerator_ArrayAdapter(Context context, ArrayList<Event> events) {
    super(context, R.layout.browseevents_rowlayout, events);
    this.context = context;
    this.events = events;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.browseevents_rowlayout, parent, false);
    try{
    //set the required text and image
    ImageView eventImage = (ImageView) rowView.findViewById(R.id.event_image);
    TextView eventName = (TextView) rowView.findViewById(R.id.event_name);
    TextView eventTime = (TextView) rowView.findViewById(R.id.event_time);
    TextView eventDescription = (TextView) rowView.findViewById(R.id.event_location);
    
    
    eventName.setText(events.get(position).eventName);
   // eventTime.setText(values[position]);
    eventDescription.setText(events.get(position).eventDescription);
    eventImage.setImageResource(R.drawable.purdue_symbol);
    }
    catch(Exception e)
    {
    e.printStackTrace();
        Log.d("mayankerror",e.toString());
    }
    return rowView;
  }
} 