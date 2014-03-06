package com.purdue.CampusFeed;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

/*Test the RowGenerator_ArrayAdapter class
 *  change the following line in the android manifest if you want to run this:
 *  
 *  <activity
            android:name="com.purdue.CampusFeed.Test_RG_ArrayAdapter"
            
     The app will then launch this activity first instead of MainActivity
 */
public class Test_RG_ArrayAdapter extends ListActivity {
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
        "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
        "Linux", "OS/2" };
    RowGenerator_ArrayAdapter adapter = new RowGenerator_ArrayAdapter(this, values);
    setListAdapter(adapter);
  }
  
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    String item = (String) getListAdapter().getItem(position);
    Toast.makeText(this, item + " selected", Toast.LENGTH_SHORT).show();
  }

} 
