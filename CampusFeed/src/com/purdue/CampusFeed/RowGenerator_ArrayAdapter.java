package com.purdue.CampusFeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * 
 * generates rows for the browse events fragment
 * 
 */
public class RowGenerator_ArrayAdapter extends ArrayAdapter<String> {
  private final Context context;
  
  //testing
  private final String[] values;
  //end test

  //constructor
  public RowGenerator_ArrayAdapter(Context context, String[] values) {
    super(context, R.layout.browseevents_rowlayout, values);
    this.context = context;
    this.values = values;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.browseevents_rowlayout, parent, false);
    
    //set the required text and image
    ImageView eventImage = (ImageView) rowView.findViewById(R.id.event_image);
    /*TextView eventName = (TextView) rowView.findViewById(R.id.event_name);
    TextView eventTime = (TextView) rowView.findViewById(R.id.event_time);
    TextView eventDescription = (TextView) rowView.findViewById(R.id.event_location);
    
    
    eventName.setText(values[position]);
    eventTime.setText(values[position]);
    eventDescription.setText(values[position]);*/
    eventImage.setImageResource(R.drawable.purdue_symbol);
 
    return rowView;
  }
} 