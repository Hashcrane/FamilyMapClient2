package larso12.familymap.UserIterface;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import static larso12.familymap.ClientUtilities.concatEvent;
import static larso12.familymap.ClientUtilities.concatName;
import static larso12.familymap.ClientUtilities.sortByYear;

import java.util.ArrayList;
import java.util.Map;

import larso12.familymap.Model.Cache;
import larso12.familymap.R;
import larso12.familymap.Model.SettingsCache;
import models.Event;
import models.Person;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment";
    static final float WIDTH_START = 15;

    private enum LineType {SPOUSE_LINE, FAMILY_LINE, LIFE_STORY_LINE}

    public MapFragment() {
        // Required empty public constructor
    }

    private GoogleMap map;
    private View view;
    private boolean fromEventsAct;
    private Event selectedEvent;
    private ArrayList<Polyline> polylines = new ArrayList<>();

    private GoogleMap.OnMarkerClickListener  markerClickListener;

    {
        markerClickListener = new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Cache cache = Cache.getInstance();
                removePolyLines();
                cache.setMarker(marker);

                updateMarkerInfo(marker, null);
                Event event = (Event) marker.getTag();
                centerMap(event);

                SettingsCache sCache = SettingsCache.getSettingsCache();
                if (sCache.isShowLifeLines()) {

                    drawLifeStoryLines(createRelatedEventList(cache.getFilteredPersons().get(event.getPersonID())));
                }
                if (sCache.isShowSpouseLines()) {
                    drawSpouseLines(event);
                }
                if (sCache.isShowFamilyTreeLines()) {
                    drawFamilyTreeLinesStart(event);
                }
                return true;
            }
        };
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            fromEventsAct = bundle.getBoolean("fromEventsAct");
            if (fromEventsAct){
                Cache cache = Cache.getInstance();
                selectedEvent = cache.getCurrentDisplayedEvents().get(bundle.getString("eventID"));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
//

        view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (savedInstanceState == null) {
            ImageView imageView = view.findViewById(R.id.eventMarker);
            imageView.setImageResource(R.drawable.male_person_48dp);
            imageView.setVisibility(view.INVISIBLE);

            TextView textView = view.findViewById(R.id.mapTextName);
            textView.setText("");
            textView = view.findViewById(R.id.mapTextEvent);
            textView.setText("");
        } else {

        }
        if (Cache.getInstance().getMarker() == null) {
            TextView textView = view.findViewById(R.id.mapTextName);
            textView.setText(R.string.mapFragMessage);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_map_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_button:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivity(intent);
                return true;
            case R.id.filter_button:
                Intent intent1 = new Intent(getActivity(), FilterActivity.class);
                getActivity().startActivity(intent1);
                return true;
            case R.id.search_button:
                Intent intent2 = new Intent(getActivity(), SearchActivity.class);
                getActivity().startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        startMap();
    }

    void startMap() {
        zoomMap(1);
        setMapType();
        addMarkers();
        setMarkerListener();

        if (fromEventsAct) {
            centerMap(selectedEvent);
            updateMarkerInfo(Cache.getInstance().getMarker(), selectedEvent);
        }
        Marker marker = Cache.getInstance().getMarker();
        if (marker != null) {
            markerClickListener.onMarkerClick(marker);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    void setMapType() {
        SettingsCache sCache = SettingsCache.getSettingsCache();
        int mapType;
        switch (sCache.getCurrentMapType()) {
            case "Normal":
                mapType = GoogleMap.MAP_TYPE_NORMAL;
                break;
            case "Hybrid":
                mapType = GoogleMap.MAP_TYPE_HYBRID;
                break;
            case "Satellite":
                mapType = GoogleMap.MAP_TYPE_SATELLITE;
                break;
            case "Terrain":
                mapType = GoogleMap.MAP_TYPE_TERRAIN;
                break;
            case "None":
                mapType = GoogleMap.MAP_TYPE_NONE;
                break;
            default:
                mapType = GoogleMap.MAP_TYPE_SATELLITE;
        }
        map.setMapType(mapType);
    }

    void zoomMap(float amount) {
        CameraUpdate update = CameraUpdateFactory.zoomTo(amount);
        map.moveCamera(update);
    }

    void centerMap(Event event) {
        CameraUpdate update = CameraUpdateFactory.newLatLng(extractLatLng(event));
        map.moveCamera(update);
    }

    void addMarkers() {
        Cache cache = Cache.getInstance();

        for (Map.Entry<String, Event> entry: cache.getCurrentDisplayedEvents().entrySet()) {
            if (cache.getBirthEvents().containsValue(entry.getValue())){
                addSingleMarker(entry.getValue(), "birth");
            } else if (cache.getDeathEvents().containsValue(entry.getValue())){
                addSingleMarker(entry.getValue(), "death");
            } else if (cache.getMarriageEvents().containsValue(entry.getValue())){
                addSingleMarker(entry.getValue(), "marriage");
            } else {
                addSingleMarker(entry.getValue(), "other");
            }
        }
    }

    void addSingleMarker(Event event, String eventListType) {
        float color;
        switch (eventListType) {
            case "birth":
                color = BitmapDescriptorFactory.HUE_YELLOW;
                break;
            case "death":
                color = BitmapDescriptorFactory.HUE_GREEN;
                break;
            case "marriage":
                color = BitmapDescriptorFactory.HUE_RED;
                break;
            case "other":
                color = BitmapDescriptorFactory.HUE_BLUE;
                break;
            default:
                color = BitmapDescriptorFactory.HUE_RED;
        }

        MarkerOptions options = new MarkerOptions().position(extractLatLng(event))
                .icon(BitmapDescriptorFactory.defaultMarker(color));
        Marker marker = map.addMarker(options);
        marker.setTag(event);
    }

    void setMarkerListener() {
        map.setOnMarkerClickListener(markerClickListener);
    }

    void removePolyLines() {
        for (Polyline polyline: polylines) {
            polyline.remove();
        }
        polylines.clear();
    }

    void drawSpouseLines(Event event) {
        Cache cache = Cache.getInstance();
        Person person = cache.getFilteredPersons().get(event.getPersonID());
        if (person.getSpouse() == null) return;
        ArrayList<Event> spouseEvents = cache.getPersonIDToEvents().get(person.getSpouse());
        spouseEvents = sortByYear(spouseEvents);

        for (Event selectEvent : spouseEvents) {
            if (cache.getCurrentDisplayedEvents().containsKey(selectEvent.getEventID())) {
                drawLine(event, selectEvent, LineType.SPOUSE_LINE, WIDTH_START);
                break;
            }
        }
    }

    void drawLifeStoryLines(ArrayList<Event> events) {
        if (events == null) {
            Toast.makeText(getContext(), R.string.drawLinesErrorMessage, Toast.LENGTH_LONG).show();
        } else if (events.isEmpty()) {
            Log.i(TAG, "No Event Lines to draw");
        } else {
            int count = 0;
            events = sortByYear(events);

            while (count < events.size()) {
                for (int i = 0; i < events.size() - 1; ++i) {
                    if (count >= events.size()) break;
                    drawLine(events.get(i), events.get(i + 1), LineType.LIFE_STORY_LINE, WIDTH_START);
                    ++count;
                }
            }
        }
    }

    void drawFamilyTreeLinesStart(Event event) {
        drawFamilyTreeLinesRecurse(event, LineType.FAMILY_LINE, WIDTH_START);

    }

     void drawFamilyTreeLinesRecurse(Event event, LineType lineType, Float lineThickness) {
         Cache cache = Cache.getInstance();
         Person person = cache.getFilteredPersons().get(event.getPersonID());

         ArrayList<Event> nextEvents = new ArrayList<>();
         ArrayList<Event> motherEvents;
         if (person.getMother() != null) {
             motherEvents = cache.getPersonIDToEvents().get(person.getMother());
             motherEvents = sortByYear(motherEvents);

             for (Event selectEvent : motherEvents) {
                 if (cache.getCurrentDisplayedEvents().containsKey(selectEvent.getEventID())) {
                     drawLine(event, selectEvent, lineType, lineThickness);
                     nextEvents.add(selectEvent);
                     break;
                 }
             }
         }

         ArrayList<Event> fatherEvents;
         if (person.getFather() != null) {
             fatherEvents = cache.getPersonIDToEvents().get(person.getFather());
             fatherEvents = sortByYear(fatherEvents);

             for (Event selectEvent : fatherEvents) {
                 if (cache.getCurrentDisplayedEvents().containsKey(selectEvent.getEventID())) {
                     drawLine(event, selectEvent, lineType, lineThickness);
                     nextEvents.add(selectEvent);
                     break;
                 }
             }
         }
         for (Event next: nextEvents){
             drawFamilyTreeLinesRecurse(next, lineType, lineThickness - 2f);
         }
     }

    void updateMarkerInfo(Marker marker, Event event) {
        if (event == null) {
            event = (Event) marker.getTag();
            centerMap(event);
        }
        final Event passedEvent = event;
        Cache cache = Cache.getInstance();

        TextView name = view.findViewById(R.id.mapTextName);
        name.setText(concatName(cache.getFilteredPersons().get(event.getPersonID())));

        TextView eventView = view.findViewById(R.id.mapTextEvent);
        eventView.setText(concatEvent(event));

        ImageView imageView = view.findViewById(R.id.eventMarker);
        if (cache.getFilteredPersons().containsKey(event.getPersonID())) {
            if (cache.getFilteredPersons().get(event.getPersonID()).getGender()
                    .toLowerCase().equals("m")) {
                imageView.setImageResource(R.drawable.male_person_48dp);
            } else {
                imageView.setImageResource(R.drawable.female_person_48dp);
            }
        } else {
            Log.e(TAG, "Missing Person found for Event in setMarkerListener()");
        }
        imageView.setVisibility(View.VISIBLE);
        View mapDetailView = view.findViewById(R.id.mapDetailView);
        mapDetailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PersonActivity.class);
                intent.putExtra("personID", passedEvent.getPersonID());
                startActivity(intent);
            }
        });
    }



    private void drawLine(Event point1, Event point2, LineType lineType, Float lineThickness) {
        PolylineOptions options;
        switch (lineType) {
            case FAMILY_LINE:
                options = new PolylineOptions().add(extractLatLng(point1), extractLatLng(point2))
                        .color(getColor(lineType)).width(lineThickness);
                break;
            case SPOUSE_LINE:
                options = new PolylineOptions().add(extractLatLng(point1), extractLatLng(point2))
                        .color(getColor(lineType)).width(WIDTH_START);
                break;
            case LIFE_STORY_LINE:
                options = new PolylineOptions().add(extractLatLng(point1), extractLatLng(point2))
                        .color(getColor(lineType)).width(WIDTH_START);
                break;
            default:
                options = new PolylineOptions().add(extractLatLng(point1), extractLatLng(point2))
                        .color(getColor(lineType)).width(WIDTH_START);
                break;
        }
        Polyline polyline = map.addPolyline(options);
        polylines.add(polyline);
    }

    LatLng extractLatLng(Event event) {
        return new LatLng(Double.valueOf(event.getLatitude()),
                Double.valueOf(event.getLongitude()));
    }

    private ArrayList<Event> createRelatedEventList(Person person) {
        Cache cache = Cache.getInstance();
        if (cache.getPersonIDToEvents().containsKey(person.getID())) {
            return cache.getPersonIDToEvents().get(person.getID());
        } else {
            //Error
            Log.e(TAG, "Event Displayed on map is not found in filtered list");
            return null;
        }
    }

    private int getColor(LineType lineType) {
        SettingsCache sCache = SettingsCache.getSettingsCache();
        String color;
        switch (lineType) {
            case SPOUSE_LINE:
                color = sCache.getColorSpouseLines();
                break;
            case FAMILY_LINE:
                color = sCache.getColorFamilyTreeLines();
                break;
            case LIFE_STORY_LINE:
                color = sCache.getColorLifeLines();
                break;
            default:
                color = "Green";
        }

        switch (color) {
            case "Green":
                return Color.GREEN;
            case "Blue":
                return Color.BLUE;
            case "Red":
                return Color.RED;
            case "Yellow":
                return Color.YELLOW;
            default:
                return Color.GREEN;

        }
    }
}
