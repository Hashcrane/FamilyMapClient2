package larso12.familymap.UserIterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.Map;

import larso12.familymap.Model.Cache;
import larso12.familymap.R;
import models.Event;
import models.Person;

import static larso12.familymap.ClientUtilities.concatEvent;
import static larso12.familymap.ClientUtilities.concatName;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private ArrayList<Person> personResults;
    private ArrayList<Event> eventResults;
    private enum dataType  {EVENT, PERSON}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        personResults = new ArrayList<>();
        eventResults = new ArrayList<>();

        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.searchActivityLabel);



        recyclerView = findViewById(R.id.searchRecyclerView);
        searchView = findViewById(R.id.search_bar);

        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                findPeople(query);
                findEvents(query);

                if (recyclerAdapter == null) {
                    recyclerAdapter = new RecyclerAdapter(personResults, eventResults);
                    recyclerView.setAdapter(recyclerAdapter);
                } else {
                    recyclerAdapter.notifyDataSetChanged();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //do nothing
                return false;
            }
        });


    }

    private void findPeople(String query) {
        Cache cache = Cache.getInstance();
        for (Map.Entry<String, Person> entry : cache.getFilteredPersons().entrySet()) {
            if (entry.getValue().getF_name().toLowerCase().contains(query.toLowerCase()) ||
                    entry.getValue().getL_name().toLowerCase().contains(query.toLowerCase())) {
                personResults.add(entry.getValue());
            }
        }
    }

    private void findEvents(String query) {
        Cache cache = Cache.getInstance();
        for (Map.Entry<String, Event> entry : cache.getCurrentDisplayedEvents().entrySet()) {
            if (entry.getValue().getCountry().toLowerCase().contains(query.toLowerCase()) ||
                    entry.getValue().getCity().toLowerCase().contains(query.toLowerCase()) ||
                    entry.getValue().getEventType().toLowerCase().contains(query.toLowerCase()) ||
                    ((Integer) entry.getValue().getYear()).toString().toLowerCase().contains(query.toLowerCase())) {
                eventResults.add(entry.getValue());
            }
        }
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private final ArrayList<EventPersonData> list;

        RecyclerAdapter(ArrayList<Person> persons, ArrayList<Event> events) {
            list = new ArrayList<>();
            for (Person person: persons) {
                EventPersonData data = new EventPersonData(person);
                list.add(data);
            }
            for (Event event: events) {
                EventPersonData data = new EventPersonData(event);
                list.add(data);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            return new MyViewHolder(inflater, viewGroup);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
            EventPersonData data = list.get(i);
            myViewHolder.bindItem(data);
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Event heldEvent;
        private Person heldPerson;

        private final TextView topText;
        private final TextView bottomText;
        private final ImageView imageView;

        MyViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.person_group_view, parent, false));
            itemView.setOnClickListener(this);

            topText = itemView.findViewById(R.id.personGroupTextTop);
            bottomText = itemView.findViewById(R.id.personGroupTextBottom);
            imageView = itemView.findViewById(R.id.personGroupIconView);
        }

        void bindItem(EventPersonData data) {
            switch (data.getType()) {
                case EVENT:
                    heldEvent = data.getEvent();
                    topText.setText(concatEvent(heldEvent));
                    Cache cache = Cache.getInstance();
                    bottomText.setText(concatName(cache.getFilteredPersons().get(heldEvent.getPersonID())));
                    imageView.setImageResource(R.drawable.other_event_24dp);
                    break;
                case PERSON:
                    heldPerson = data.getPerson();
                    topText.setText(concatName(heldPerson));
                    bottomText.setText("");
                    if (heldPerson.getGender().toLowerCase().equals("m")) {
                        imageView.setImageResource(R.drawable.male_person_48dp);
                    } else {
                        imageView.setImageResource(R.drawable.female_person_48dp);
                    }
                    break;
            }
        }


        @Override
        public void onClick(View v) {
            if (heldEvent != null && heldPerson == null) {
                Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                intent.putExtra("eventID", heldEvent.getEventID());
            } else if (heldEvent == null && heldPerson != null) {
                Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
                intent.putExtra("personID", heldPerson.getID());
                startActivity(intent);
            }
        }
    }

    private class EventPersonData {
        private final Event event;
        private final Person person;
        private final dataType type;

        EventPersonData(Event event) {
            this.event = event;
            type = dataType.EVENT;
            this.person = null;
        }

        EventPersonData(Person person) {
            this.person = person;
            type = dataType.PERSON;
            this.event = null;
        }

        public Event getEvent() {
            return event;
        }

        public Person getPerson() {
            return person;
        }

        public dataType getType() {
            return type;
        }
    }
}
