package larso12.familymap.UserIterface;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import larso12.familymap.R;

public class MainActivity extends AppCompatActivity {

    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isLoggedIn = false;


        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.MainActFragContainer);

        if (fragment == null) {
            fragment = new LoginFragment();
            fragmentManager.beginTransaction().add(R.id.MainActFragContainer, fragment).commit();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);

    }

    public void startMapFragment()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.MainActFragContainer);

        if (fragment == null) {
            fragment = new MapFragment();
            fragmentManager.beginTransaction().add(R.id.MainActFragContainer, fragment).commit();
        } else {
            fragmentManager.beginTransaction().remove(fragment).commit();
            fragment = new MapFragment();
            fragmentManager.beginTransaction().add(R.id.MainActFragContainer, fragment).commit();
        }
    }
}

