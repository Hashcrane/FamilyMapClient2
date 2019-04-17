package larso12.familymap.UserIterface;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.IOException;


import larso12.familymap.Model.Cache;
import larso12.familymap.R;
import larso12.familymap.ServerAccess.ServerProxy;
import models.Person;
import models.User;
import services.event.EventRequest;
import services.event.EventResponse;
import services.person.PersonRequest;
import services.person.PersonResponse;
import services.register_and_login.LoginRequest;
import services.register_and_login.LoginResponse;
import services.register_and_login.RegisterRequest;
import services.register_and_login.RegisterResponse;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "LoginFragment";

    private View view;
    private final String STATE_SH = "serverhost";
    private EditText serverHost;
    private boolean SH_ready;
    private final String STATE_SP = "serverport";
    private EditText serverPort;
    private boolean SP_ready;
    private final String STATE_UN = "username";
    private EditText userName;
    private boolean UN_ready;
    private final String STATE_P = "password"; //Maybe don't save this
    private EditText password;
    private boolean P_ready;
    private final String STATE_FN = "fname";
    private EditText fName;
    private boolean FN_ready;
    private final String STATE_LN = "lname";
    private EditText lName;
    private boolean LN_ready;
    private final String STATE_E = "email";
    private EditText email;
    private boolean E_ready;
    private final String STATE_G = "gender";
    private RadioButton male;
    private Button signIn;
    private Button register;
    private boolean isLoggedIn;
    private boolean allowRetrieveData;
    private boolean registerSuccess;
    private boolean loginSuccess;

    private Cache cache = Cache.getInstance();

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        if (savedInstance != null) {
            serverHost.setText(savedInstance.getString(STATE_SH));
            serverPort.setText(savedInstance.getString(STATE_SP));
            userName.setText(savedInstance.getString(STATE_UN));
            //password.setText(savedInstance.getString(STATE_P));
            fName.setText(savedInstance.getString(STATE_FN));
            lName.setText(savedInstance.getString(STATE_LN));
            email.setText(savedInstance.getString(STATE_E));
            male.setChecked(savedInstance.getBoolean(STATE_G));
        }
        isLoggedIn = false;
        allowRetrieveData = false;
        registerSuccess = false;
        loginSuccess = false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_SH, serverHost.getText().toString());
        outState.putString(STATE_SP, serverPort.getText().toString());
        outState.putString(STATE_UN, userName.getText().toString());
        //outState.putString(STATE_P, password.getText().toString());
        outState.putString(STATE_FN, fName.getText().toString());
        outState.putString(STATE_LN, lName.getText().toString());
        outState.putString(STATE_E, email.getText().toString());
        outState.putBoolean(STATE_G, male.isChecked());

    }

    /**
     * Checks to enable sign in button
     *
     * @return true if fields are ready, false if not
     */
    private boolean signInReady() {
        if (SH_ready && SP_ready && UN_ready && P_ready) {
            return true;
        } else return false;
    }

    /**
     * Checks to enable register button
     *
     * @return true if all fields are ready, false if not
     */
    private boolean registerReady() {
        if (SH_ready && SP_ready && UN_ready && P_ready && FN_ready &&
                LN_ready && E_ready) {
            return true;
        } else return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        super.onCreateView(inflater, container, savedInstance);

        view = inflater.inflate(R.layout.fragment_login, container, false);
        getAllFields();
        signIn = view.findViewById(R.id.signInButton);
        register = view.findViewById(R.id.registerButton);
        signIn.setEnabled(false);
        register.setEnabled(false);

        //Following code checks for text in required fields and enables/disables buttons
        serverPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    SP_ready = true;
                } else {
                    SP_ready = false;
                }
                if (signInReady()) {
                    signIn.setEnabled(true);
                } else signIn.setEnabled(false);

                if (registerReady()) {
                    register.setEnabled(true);
                } else register.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        serverHost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    SH_ready = true;
                } else {
                    SH_ready = false;
                }
                if (signInReady()) {
                    signIn.setEnabled(true);
                } else signIn.setEnabled(false);

                if (registerReady()) {
                    register.setEnabled(true);
                } else register.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    UN_ready = true;
                } else {
                    UN_ready = false;
                }
                if (signInReady()) {
                    signIn.setEnabled(true);
                } else signIn.setEnabled(false);

                if (registerReady()) {
                    register.setEnabled(true);
                } else register.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    P_ready = true;
                } else {
                    P_ready = false;
                }
                if (signInReady()) {
                    signIn.setEnabled(true);
                } else signIn.setEnabled(false);

                if (registerReady()) {
                    register.setEnabled(true);
                } else register.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    FN_ready = true;
                } else {
                    FN_ready = false;
                }
                if (signInReady()) {
                    signIn.setEnabled(true);
                } else signIn.setEnabled(false);

                if (registerReady()) {
                    register.setEnabled(true);
                } else register.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        lName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    LN_ready = true;
                } else {
                    LN_ready = false;
                }
                if (signInReady()) {
                    signIn.setEnabled(true);
                } else signIn.setEnabled(false);

                if (registerReady()) {
                    register.setEnabled(true);
                } else register.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    E_ready = true;
                } else {
                    E_ready = false;
                }
                if (signInReady()) {
                    signIn.setEnabled(true);
                } else signIn.setEnabled(false);

                if (registerReady()) {
                    register.setEnabled(true);
                } else register.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        serverHost.setText("10.0.2.2");
        serverPort.setText("8080");
        userName.setText("sheila");
        password.setText("parker");

        signIn.setOnClickListener(this);
        register.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signInButton:
                SignIn();

                break;
            case R.id.registerButton:
                Register();

                break;
        }
    }

    /**
     * Starts Async task to sign in user
     */
    private void SignIn() {
        LoginRequest loginRequest = new LoginRequest(userName.getText().toString(),
                password.getText().toString());
        AsyncLogin asyncLogin = new AsyncLogin();
        asyncLogin.execute(loginRequest);

    }

    /**
     * Starts Async Task to Register a User
     */
    private void Register() {
        String gender;
        if (male.isChecked()) {
            gender = "m";
        } else {
            gender = "f";
        }
        Person person = new Person(userName.getText().toString(), fName.getText().toString(),
                lName.getText().toString(), gender, null, null, null);
        User user = new User(person, password.getText().toString(), email.getText().toString());

        cache.setCurrentUser(user);
        cache.setCurrentUserPerson(person);

        RegisterRequest registerRequest = new RegisterRequest(user);

        AsyncRegister asyncRegister = new AsyncRegister();
        asyncRegister.execute(registerRequest);

    }

    /**
     * Class to implement Async for registration
     */
    private class AsyncRegister extends AsyncTask<RegisterRequest, Void, RegisterResponse> {

        @Override
        protected RegisterResponse doInBackground(RegisterRequest... registerRequests) {
            ServerProxy proxy = new ServerProxy(serverHost.getText().toString(),
                    serverPort.getText().toString());

            RegisterResponse registerResponse = new RegisterResponse("Registration Failed");
            try {
                registerResponse = proxy.registerUser(registerRequests[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return registerResponse;
        }

        @Override
        protected void onPostExecute(RegisterResponse registerResponse) {
            super.onPostExecute(registerResponse);
            if (registerResponse.getMessage() == null) {

                cache.setAuthToken(registerResponse.getToken());
                cache.setServerHost(serverHost.getText().toString());
                cache.setServerPort(serverPort.getText().toString());
                cache.setCurrentUserPersonID(registerResponse.getPersonID());
                allowRetrieveData = true;
                registerSuccess = true;
            } else {
                Toast.makeText(view.getContext(), registerResponse.getMessage(), Toast.LENGTH_LONG).show();
            }
            if (allowRetrieveData) {
                EventRequest eventRequest = new EventRequest(cache.getAuthToken());
                GetEventsTask getEventsTask = new GetEventsTask("register");
                getEventsTask.execute(eventRequest);

                PersonRequest personRequest = new PersonRequest(cache.getAuthToken());
                GetPersonsTask getPersonsTask = new GetPersonsTask("register");
                getPersonsTask.execute(personRequest);
                allowRetrieveData = false;
            }
        }
    }

    /**
     * Class to implement Async for Login of user
     */
    private class AsyncLogin extends AsyncTask<LoginRequest, Void, LoginResponse> {


        @Override
        protected LoginResponse doInBackground(LoginRequest... loginRequests) {

            ServerProxy proxy = new ServerProxy(serverHost.getText().toString(),
                    serverPort.getText().toString());

            LoginResponse loginResponse = new LoginResponse("Empty");
            try {
                loginResponse = proxy.loginUser(loginRequests[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return loginResponse;
        }


        @Override
        protected void onPostExecute(LoginResponse loginResponse) {
            super.onPostExecute(loginResponse);
            if (loginResponse.getMessage() == null) {

                cache.setAuthToken(loginResponse.getToken());
                cache.setCurrentUserPersonID(loginResponse.getPersonID());
                cache.setServerHost(serverHost.getText().toString());
                cache.setServerPort(serverPort.getText().toString());
                allowRetrieveData = true;
                loginSuccess = true;
            } else {
                Toast.makeText(view.getContext(), loginResponse.getMessage(), Toast.LENGTH_LONG).show();
            }
            if (allowRetrieveData) {

                EventRequest eventRequest = new EventRequest(cache.getAuthToken());
                GetEventsTask getEventsTask = new GetEventsTask("login");
                getEventsTask.execute(eventRequest);

                allowRetrieveData = false;
            }
        }
    }

    private void checkDataRetrieved(String calledFrom) {

        if (!cache.getAllPersons().isEmpty() && !cache.getAllEvents().isEmpty()) {
            isLoggedIn = true;
            cache.sortAll();
            StringBuilder sb = new StringBuilder();
            if (loginSuccess) {
                String name = cache.getCurrentUserPerson().getF_name() + " " +
                        cache.getCurrentUserPerson().getL_name();
                sb.append(name);
                sb.append(R.string.loginSuccessMessage);
                String s = sb.toString();
                Toast.makeText(view.getContext(), s, Toast.LENGTH_LONG).show();
            } else if (registerSuccess) {
                String name = cache.getCurrentUserPerson().getF_name() + " " +
                        cache.getCurrentUserPerson().getL_name();
                sb.append(name);
                sb.append(R.string.registerSuccessMessage);
                Toast.makeText(view.getContext(), name, Toast.LENGTH_LONG).show();
                registerSuccess = false;
            }
        } else {
            isLoggedIn = false;
            if (calledFrom.equals("register")) {
                Toast.makeText(view.getContext(), R.string.registerFailedMessage, Toast.LENGTH_LONG).show();
            }
            if (calledFrom.equals("login")) {
                Toast.makeText(view.getContext(), R.string.loginFailedMessage, Toast.LENGTH_LONG).show();

            }
        }
        if (isLoggedIn) {
            //FIXME Go back to MainActivity and
            ((MainActivity)getActivity()).startMapFragment();
        }
    }

    /**
     * Async Task to fill Cache with Events
     */
    private class GetEventsTask extends AsyncTask<EventRequest, Void, EventResponse> {
        private String calledFrom;
        protected GetEventsTask(String calledFrom) {
            this.calledFrom = calledFrom;
        }
        @Override
        protected EventResponse doInBackground(EventRequest... eventRequests) {
            ServerProxy proxy = new ServerProxy(serverHost.getText().toString(),
                    serverPort.getText().toString());
            EventResponse eventResponse = new EventResponse("Empty");
            try {
                eventResponse = proxy.getEvents(eventRequests[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return eventResponse;
        }

        @Override
        protected void onPostExecute(EventResponse eventResponse) {
            super.onPostExecute(eventResponse);
            if (eventResponse.getMessage() != null) {
            } else {
                cache.setAllEvents(eventResponse.getEvents());
            }
            PersonRequest personRequest = new PersonRequest(cache.getAuthToken());
            GetPersonsTask getPersonsTask = new GetPersonsTask(calledFrom);
            getPersonsTask.execute(personRequest);
        }
    }

    /**
     * Async Task to fill Cache with Persons
     */
    private class GetPersonsTask extends AsyncTask<PersonRequest, Void, PersonResponse> {
        private String calledFrom;
        protected GetPersonsTask(String calledFrom) {
            this.calledFrom = calledFrom;
        }
        @Override
        protected void onPostExecute(PersonResponse personResponse) {
            super.onPostExecute(personResponse);
            if (personResponse.getMessage() != null) {
                Log.e("LOGIN", personResponse.getMessage());
            } else {
                cache.setAllPersons(personResponse.getPersons());
                checkDataRetrieved(calledFrom);
            }
        }

        @Override
        protected PersonResponse doInBackground(PersonRequest... personRequests) {
            ServerProxy proxy = new ServerProxy(serverHost.getText().toString(),
                    serverPort.getText().toString());
            PersonResponse personResponse = new PersonResponse("Empty");
            try {
                personResponse = proxy.getPersons(personRequests[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return personResponse;
        }
    }

    /**
     * Loads the EditText fields from View
     */
    private void getAllFields() {
        serverHost = view.findViewById(R.id.ServerHostInput);
        serverPort = view.findViewById(R.id.ServerPortInput);
        userName = view.findViewById(R.id.UserNameInput);
        password = view.findViewById(R.id.PasswordInput);
        fName = view.findViewById(R.id.FirstNameInput);
        lName = view.findViewById(R.id.LastNameInput);
        email = view.findViewById(R.id.EmailInput);
        male = view.findViewById(R.id.maleRadioButton);
    }

}
