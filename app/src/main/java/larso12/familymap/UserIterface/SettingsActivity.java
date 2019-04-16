package larso12.familymap.UserIterface;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import larso12.familymap.Model.Cache;
import larso12.familymap.R;
import larso12.familymap.ServerAccess.ServerProxy;
import services.event.EventRequest;
import services.event.EventResponse;
import services.person.PersonRequest;
import services.person.PersonResponse;


/**
 * Contains the settings for changing what events and persons get shown in other activities
 */
public class SettingsActivity extends AppCompatActivity {

    private View view;

    private boolean isLoggedIn;

    private String drawableColors[] = {"Green", "Blue", "Red", "Yellow"};

    private String mapTypes[] = {"Normal", "Hybrid", "Satellite", "Terrain"};

    private Spinner spLifeLines;
    private String colorLifeLines;
    private Switch showLifeLines;
    private ArrayAdapter<String> adapterColorsLifeLines;

    private Spinner spSpouseLines;
    private String colorSpouseLines;
    private Switch showSpouseLines;
    private ArrayAdapter<String> adapterColorsSpouseLines;

    private Spinner spFamilyTreeLines;
    private String colorFamilyTreeLines;
    private Switch showFamilyTreeLines;
    private ArrayAdapter<String> adapterColorsFamilyTree;

    private Spinner spMapType;
    private String currentMapType;
    private ArrayAdapter<String> adapterMapType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.settingsActivityLabel);

        initializeResources();
        TextView logout = view.findViewById(R.id.logoutView);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cache cache = Cache.getInstance();
                cache.clearAll();
                isLoggedIn = false;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("isLoggedIn", isLoggedIn);
                startActivity(intent);

            }
        });

        TextView resync = view.findViewById(R.id.syncDataView);
        resync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cache cache = Cache.getInstance();
                EventRequest eventRequest = new EventRequest(cache.getAuthToken());
                GetEventsTask getEventsTask = new GetEventsTask();
                getEventsTask.execute(eventRequest);

                PersonRequest personRequest = new PersonRequest(cache.getAuthToken());
                GetPersonsTask getPersonsTask = new GetPersonsTask();
                getPersonsTask.execute(personRequest);
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    void initializeResources() {
        spLifeLines = (Spinner) findViewById(R.id.SpinnerLifeStory);
        showLifeLines = (Switch) findViewById(R.id.ShowLifeStory);
        adapterColorsLifeLines = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                drawableColors);
        adapterColorsLifeLines.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLifeLines.setAdapter(adapterColorsLifeLines);
        spLifeLines.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                colorLifeLines = adapterColorsLifeLines.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spFamilyTreeLines = (Spinner) findViewById(R.id.SpinnerFamilyLines);
        showFamilyTreeLines = findViewById(R.id.ShowFamilyLines);
        adapterColorsFamilyTree = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                drawableColors);
        adapterColorsFamilyTree.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFamilyTreeLines.setAdapter(adapterColorsFamilyTree);
        spFamilyTreeLines.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                colorFamilyTreeLines = adapterColorsFamilyTree.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSpouseLines = (Spinner) findViewById(R.id.SpinnerSpouseLines);
        showSpouseLines = findViewById(R.id.ShowSpouseLines);
        adapterColorsSpouseLines = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                drawableColors);
        adapterColorsSpouseLines.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSpouseLines.setAdapter(adapterColorsSpouseLines);
        spSpouseLines.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                colorSpouseLines = adapterColorsSpouseLines.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spMapType = (Spinner) findViewById(R.id.SpinnerMapType);
        adapterMapType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                mapTypes);
        spMapType.setAdapter(adapterMapType);
        spMapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentMapType = adapterMapType.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Async Task to fill Cache with Events
     */
    private class GetEventsTask extends AsyncTask<EventRequest, Void, EventResponse> {

        @Override
        protected EventResponse doInBackground(EventRequest... eventRequests) {
            Cache cache = Cache.getInstance();
            ServerProxy proxy = new ServerProxy(cache.getServerHost(),
                    cache.getServerPort());
            EventResponse eventResponse = new EventResponse("Empty");
            try {
                eventResponse = proxy.getEvents(eventRequests[0]);
            } catch (IOException e) {
                e.printStackTrace();
                //FIXME Log error
            }
            return eventResponse;
        }

        @Override
        protected void onPostExecute(EventResponse eventResponse) {
            super.onPostExecute(eventResponse);
            if (eventResponse.getMessage() != null) {
                //FIXME Log error
            } else {
                Cache cache = Cache.getInstance();
                cache.setAllEvents(eventResponse.getEvents());
            }
        }
    }

    /**
     * Async Task to fill Cache with Persons
     */
    private class GetPersonsTask extends AsyncTask<PersonRequest, Void, PersonResponse> {

        @Override
        protected void onPostExecute(PersonResponse personResponse) {
            super.onPostExecute(personResponse);
            if (personResponse.getMessage() != null) {
                //FIXME LOG ERROR
            } else {
                Cache cache = Cache.getInstance();
                cache.setAllPersons(personResponse.getPersons());
                cache.setPersonObject();
                checkDataRetrieved();
            }
        }

        @Override
        protected PersonResponse doInBackground(PersonRequest... personRequests) {
            Cache cache = Cache.getInstance();
            ServerProxy proxy = new ServerProxy(cache.getServerHost(),
                    cache.getServerPort());
            PersonResponse personResponse = new PersonResponse("Empty");
            try {
                personResponse = proxy.getPersons(personRequests[0]);
            } catch (IOException e) {
                e.printStackTrace();
                //FIXME Log error
            }
            return personResponse;
        }
    }

    private void checkDataRetrieved() {
        Cache cache = Cache.getInstance();
        if (!cache.getAllPersons().isEmpty() && !cache.getAllEvents().isEmpty()) {
            isLoggedIn = true;
            cache.sortAll();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("isLoggedIn", isLoggedIn);
            startActivity(intent);

        } else {
            isLoggedIn = false;
            Toast.makeText(view.getContext(), R.string.resyncFailed, Toast.LENGTH_LONG).show();
        }
    }
}