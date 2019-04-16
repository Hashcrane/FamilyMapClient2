package larso12.familymap.UserIterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import larso12.familymap.Model.Cache;
import larso12.familymap.R;
import models.Event;

public class EventActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cache cache = Cache.getInstance();
        Intent intent = getIntent();
        Bundle eventBundle = intent.getExtras();
        String selectedEvent = (String) eventBundle.get("eventID");

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.EventActFragContainer);

        if (fragment == null) {
            fragment = new MapFragment();

            Bundle bundle = new Bundle();
            bundle.putBoolean("fromEventAct", true);
            bundle.putString("eventID", selectedEvent);
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().add(R.id.EventActFragContainer, fragment).commit();
        }
    }
}
