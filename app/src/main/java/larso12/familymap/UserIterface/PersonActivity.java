package larso12.familymap.UserIterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import larso12.familymap.Model.Cache;
import larso12.familymap.R;
import models.Event;
import models.Person;

import static larso12.familymap.ClientUtilities.concatEvent;
import static larso12.familymap.ClientUtilities.concatName;
import static larso12.familymap.ClientUtilities.sortByYear;

public class PersonActivity extends AppCompatActivity {

    private static final String TAG = "PersonActivity";
    private Cache cache = Cache.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.personActivityLabel);

        Intent intent = getIntent();
        String personID = intent.getExtras().getString("personID");

        if (!cache.getFilteredPersons().containsKey(personID)) {
            Log.e(TAG, "Target person should not be displayed");
            throw new IllegalArgumentException("Unverified Person accessed");
        }
        Person person = cache.getFilteredPersons().get(personID);
        TextView fName = findViewById(R.id.FNamePersonInput);
        TextView lName = findViewById(R.id.LNamePersonInput);
        TextView gender = findViewById(R.id.GenderPersonInput);

        fName.setText(person.getF_name());
        lName.setText(person.getL_name());
        if (person.getGender().toLowerCase().equals("m")) {
            gender.setText(R.string.male);
        } else {
            gender.setText(R.string.female);
        }
        ExpandableListView listView = findViewById(R.id.expandListPerson);

        listView.setAdapter(new ExpandListAdapter(cache.getPersonIDToEvents().get(personID),
                cache.getPersonActRelatedPersonList(person), person));

    }

    private class ExpandListAdapter extends BaseExpandableListAdapter {
        private static final int EVENTS_POSITION = 0;
        private static final int PERSONS_POSITION = 1;

        private final ArrayList<Event> events;
        private final ArrayList<Person> persons;

        private final Person TARGET_PERSON;

        ExpandListAdapter(ArrayList<Event> events, ArrayList<Person> persons, Person target) {
            events = sortByYear(events);
            this.events = new ArrayList<>();
            for (int i = 0; i < events.size(); ++i) {
                if (cache.getCurrentDisplayedEvents().containsKey(events.get(i).getEventID())) {
                    this.events.add(events.get(i));
                }
            }
            this.persons = new ArrayList<>();
            for (Person person: persons) {
                if (cache.getFilteredPersons().containsKey(person.getID())) {
                    this.persons.add(person);
                }
            }

            this.TARGET_PERSON = target;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case PERSONS_POSITION:
                    return getString(R.string.personActPersonTitle);
                case EVENTS_POSITION:
                    return getString(R.string.personActEventTitle);
                default:
                    throw new IllegalArgumentException("Unrecognized group position");
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case PERSONS_POSITION:
                    return persons.get(childPosition);
                case EVENTS_POSITION:
                    return events.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position");
            }
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case PERSONS_POSITION:
                    return persons.size();
                case EVENTS_POSITION:
                    return events.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position");
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.expand_list_title, parent, false);
            }
            TextView listTitle = convertView.findViewById(R.id.listGroupTitle);

            switch (groupPosition) {
                case PERSONS_POSITION:
                    listTitle.setText(R.string.personActPersonTitle);
                    break;
                case EVENTS_POSITION:
                    listTitle.setText(R.string.personActEventTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position");
            }

            return convertView;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView;

            switch (groupPosition) {
                case PERSONS_POSITION:
                    itemView = layoutInflater.inflate(R.layout.person_group_view, parent, false);
                    initializeItemView(itemView, childPosition, PERSONS_POSITION);
                    break;
                case EVENTS_POSITION:
                    itemView = layoutInflater.inflate(R.layout.person_group_view, parent, false);
                    initializeItemView(itemView, childPosition, EVENTS_POSITION);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position");
            }
            return itemView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private void initializeItemView(View itemView, final int childPosition, int POSITION) {
            TextView top;
            TextView bottom;
            ImageView image = itemView.findViewById(R.id.personGroupIconView);

            switch (POSITION) {
                case PERSONS_POSITION:
                    top = itemView.findViewById(R.id.personGroupTextTop);
                    top.setText(concatName(persons.get(childPosition)));

                    bottom = itemView.findViewById(R.id.personGroupTextBottom);
                    bottom.setText(getRelation(persons.get(childPosition)));

                    if (persons.get(childPosition).getGender().toLowerCase().equals("m")) {
                        image.setImageResource(R.drawable.male_person_48dp);
                    } else {
                        image.setImageResource(R.drawable.female_person_48dp);
                    }

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
                            intent.putExtra("personID", persons.get(childPosition).getID());
                            startActivity(intent);
                        }
                    });
                    break;
                case EVENTS_POSITION:
                    top = itemView.findViewById(R.id.personGroupTextTop);
                    top.setText(concatEvent(events.get(childPosition)));

                    Cache cache = Cache.getInstance();
                    bottom = itemView.findViewById(R.id.personGroupTextBottom);
                    bottom.setText(concatName(cache.getFilteredPersons().get(events.get(childPosition).getPersonID())));

                    image.setImageResource(R.drawable.birth_event_24dp);

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                            intent.putExtra("eventID", events.get(childPosition).getEventID());
                            startActivity(intent);
                        }
                    });
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position");
            }
        }


        private String getRelation(Person person) {
            if (TARGET_PERSON == null || person == null) {
                Log.e(TAG, "Person object is null");
            }
            if (TARGET_PERSON.getSpouse().equals(person.getID()) || (person.getFather() == null || person.getMother() == null)) {
                return "Spouse";
            } else if (person.getFather().equals(TARGET_PERSON.getID()) || person.getMother().equals(TARGET_PERSON.getID())) {
                return "Child";
            } else if (TARGET_PERSON.getMother().equals(person.getID())) {
                return "Mother";
            } else if (TARGET_PERSON.getFather().equals(person.getID())) {
                return "Father";
            } else {
                throw new IllegalArgumentException("Unrelated person found in list");
            }
        }
    }
}
