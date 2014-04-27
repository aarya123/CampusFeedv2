package com.purdue.CampusFeed.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.purdue.CampusFeed.API.Api;
import com.purdue.CampusFeed.API.Event;
import com.purdue.CampusFeed.R;

/**
 * Created by Sean on 2/26/14.
 */

public class EventPageFragment extends Fragment implements OnClickListener {
    public Event myEvent;
    //private final GestureDetector gestureDetector;
    Button declineButton, maybeButton, goingButton;

    public void setEvent(Event e) {
        this.myEvent = e;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View fragmentView = inflater.inflate(R.layout.eventpage, container, false);
        Button inviteButton = (Button) fragmentView.findViewById(R.id.inviteButton);
        goingButton = (Button) fragmentView.findViewById(R.id.goingButton);
        maybeButton = (Button) fragmentView.findViewById(R.id.maybeButton);
        declineButton = (Button) fragmentView.findViewById(R.id.declineButton);
        inviteButton.setOnClickListener(this);
        goingButton.setOnClickListener(this);
        maybeButton.setOnClickListener(this);
        declineButton.setOnClickListener(this);
        return fragmentView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myEvent.incrementViewCount();
        Button editButton = (Button) getActivity().findViewById(R.id.editButton);

        if (myEvent.isAdmin()) {
            final Api api = Api.getInstance(getActivity());
            new AsyncTask<Void, Void, String[]>() {

                protected String[] doInBackground(Void... params) {
                    return api.getEventAttendees(myEvent.getId());
                }

                public void onPostExecute(String[] result) {
                    TextView eventAtt = (TextView) getActivity().findViewById(R.id.event_attendees);
                    String names = " ";
                    for (String name : result) {
                        names += name + ", ";
                    }
                    names = names.substring(0, names.length() - 2);
                    eventAtt.append(names);
                }

            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            editButton.setVisibility(View.INVISIBLE);
            TextView eventAtt = (TextView) getActivity().findViewById(R.id.event_attendees);
            eventAtt.setVisibility(View.INVISIBLE);
        }
        //  EditText descriptionText = (EditText)getActivity().findViewById(R.id.eventdescription_eventpage);
        TextView name = (TextView) getActivity().findViewById(R.id.eventpage_name);
        TextView dateAndTime = (TextView) getActivity().findViewById(R.id.dateAndTime);
        TextView loc = (TextView) getActivity().findViewById(R.id.eventpage_location);
        TextView desc = (TextView) getActivity().findViewById(R.id.event_page_info);
        TextView creator = (TextView) getActivity().findViewById(R.id.event_creator);
        TextView eventTags = (TextView) getActivity().findViewById(R.id.eventTags);
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.imageView);
        if (myEvent.hasTag("Recreation"))
            imageView.setImageResource(R.drawable.recreation_event_image);
        else if (myEvent.hasTag("Charity"))
            imageView.setImageResource(R.drawable.charity_event_image);
        else if (myEvent.hasTag("Social"))
            imageView.setImageResource(R.drawable.social_event_image);
        else if (myEvent.hasTag("Education"))
            imageView.setImageResource(R.drawable.education_event_image);
        else if (myEvent.hasTag("University Event"))
            imageView.setImageResource(R.drawable.university_event_image);
        else
            imageView.setImageResource(R.drawable.stock_event_image);
        setRSVPStatus();
        name.setText(myEvent.getEventName());
        dateAndTime.setText(myEvent.getDatetime());
        loc.setText(myEvent.getEventLocation());
        desc.setText(myEvent.getEventDescription());
        if (myEvent.getCreator().getName().equals("Scraper Scraper")) {
            creator.setText("Hosted by Purdue");
        } else if (myEvent.getCreator().getName().equals(" ")) {
            creator.setText("Hosted by Purdue");
        } else {
            creator.setText("Hosted by " + myEvent.getCreator().getName());
        }


        String tags = "Event Tags: ";
        for (int i = 0; i < myEvent.getCategories().length; i++) {
            tags += myEvent.getCategories()[i] + ", ";
        }
        tags = tags.substring(0, tags.length() - 2);
        eventTags.setText(tags);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEventFragment frag = new CreateEventFragment();
                Bundle args = new Bundle();
                args.putParcelable("event", myEvent);
                //args.putSerializable("event", myEvent);
                frag.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.basic_contentframe, frag).commit();
            }
        });

        //contentFrame.setOnTouchListener(swipeListener);
    }

    @Override
    public void onClick(View v) {
        goingButton = (Button) this.getView().findViewById(R.id.goingButton);
        maybeButton = (Button) this.getView().findViewById(R.id.maybeButton);
        declineButton = (Button) this.getView().findViewById(R.id.declineButton);
        final Api api = Api.getInstance(getActivity());
        switch (v.getId()) {
            case R.id.inviteButton:
                Intent intent = new Intent(this.getActivity(), ContactsListActivity.class);
                intent.putExtra(getString(R.string.EVENT), myEvent);
                startActivity(intent);
                break;
            case R.id.goingButton:
                //goingButton.setSelected(true);
                goingButton.setTypeface(null, Typeface.BOLD);
                maybeButton.setTypeface(null, Typeface.NORMAL);
                declineButton.setTypeface(null, Typeface.NORMAL);

                new AsyncTask<Void, Void, String>() {

                    protected String doInBackground(Void... params) {
                        myEvent.setRSVP(0);
                        api.rsvp(myEvent.getId(), 0);
                        return "";
                    }

                    public void onPostExecute(String result) {
                        Toast.makeText(getActivity(), "RSVP(going) received, enjoy!", Toast.LENGTH_SHORT);
                    }

                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case R.id.maybeButton:
                maybeButton.setTypeface(null, Typeface.BOLD);
                goingButton.setTypeface(null, Typeface.NORMAL);
                declineButton.setTypeface(null, Typeface.NORMAL);

                new AsyncTask<Void, Void, String>() {

                    protected String doInBackground(Void... params) {
                        myEvent.setRSVP(1);
                        api.rsvp(myEvent.getId(), 1);
                        return "";
                    }

                    public void onPostExecute(String result) {
                        Toast.makeText(getActivity(), "RSVP(maybe) received!", Toast.LENGTH_SHORT);
                    }

                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case R.id.declineButton:
                declineButton.setTypeface(null, Typeface.BOLD);
                goingButton.setTypeface(null, Typeface.NORMAL);
                maybeButton.setTypeface(null, Typeface.NORMAL);

                new AsyncTask<Void, Void, String>() {

                    protected String doInBackground(Void... params) {
                        myEvent.setRSVP(2);
                        api.rsvp(myEvent.getId(), 2);
                        return "";
                    }

                    public void onPostExecute(String result) {
                        Toast.makeText(getActivity(), "RSVP(decline) received!", Toast.LENGTH_SHORT);
                    }

                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
        }
    }

    private void setRSVPStatus() {
        final Api api = Api.getInstance(getActivity());
        new AsyncTask<Integer, Void, Integer>() {
            protected Integer doInBackground(Integer... integers) {
                return api.getEvent(myEvent.getId()).getRSVP();
            }

            protected void onPostExecute(Integer status) {
                if (status == -1) {
                    return;
                } else if (status == 0) {
                    goingButton.setTypeface(null, Typeface.BOLD);
                    maybeButton.setTypeface(null, Typeface.NORMAL);
                    declineButton.setTypeface(null, Typeface.NORMAL);
                } else if (status == 1) {
                    maybeButton.setTypeface(null, Typeface.BOLD);
                    goingButton.setTypeface(null, Typeface.NORMAL);
                    declineButton.setTypeface(null, Typeface.NORMAL);
                } else if (status == 2) {
                    declineButton.setTypeface(null, Typeface.BOLD);
                    goingButton.setTypeface(null, Typeface.NORMAL);
                    maybeButton.setTypeface(null, Typeface.NORMAL);
                }
            }
        }.execute();
    }
}