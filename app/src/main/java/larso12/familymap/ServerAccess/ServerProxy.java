package larso12.familymap.ServerAccess;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;


import larso12.familymap.Model.Cache;
import services.event.EventRequest;
import services.event.EventResponse;
import services.person.PersonRequest;
import services.person.PersonResponse;
import services.register_and_login.LoginRequest;
import services.register_and_login.LoginResponse;
import services.register_and_login.RegisterRequest;
import services.register_and_login.RegisterResponse;

public class ServerProxy {
    private String host;
    private String port;
    private String urlString;

    public ServerProxy(String host, String port) {
        this.host = host;
        this.port = port;

        urlString = "HTTP://" + host + ":" + port;
    }

    public RegisterResponse registerUser(RegisterRequest registerRequest) throws IOException {
        URL url = new URL(urlString + "/user/register");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.addRequestProperty("Accept", "text/html");

        connection.connect();

        try (OutputStream requestBody = connection.getOutputStream();) {
            String json = ObjectToJson.Encode(registerRequest);
            requestBody.write(json.getBytes(Charset.defaultCharset()));
        }
        RegisterResponse registerResponse;
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            String response = readString(responseBody);
            Gson gson = new Gson();
            registerResponse = gson.fromJson(response, RegisterResponse.class);
        } else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            InputStream responseBody = connection.getErrorStream();
            String response = readString(responseBody);
            Gson gson = new Gson();
            registerResponse = gson.fromJson(response, RegisterResponse.class);
        } else {
            registerResponse = new RegisterResponse("HTTP SERVER ERROR");
        }
        return registerResponse;

    }

    public LoginResponse loginUser(LoginRequest loginRequest) throws IOException {
        URL url = new URL(urlString + "/user/login");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        connection.addRequestProperty("Accept", "text/html");

        connection.connect();

        try (OutputStream requestBody = connection.getOutputStream();) {
            String json = ObjectToJson.Encode(loginRequest);
            requestBody.write(json.getBytes(Charset.defaultCharset()));
        }
        LoginResponse loginResponse;
        int responseCode = connection.getResponseCode();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            String response = readString(responseBody);
            Gson gson = new Gson();
            loginResponse = gson.fromJson(response, LoginResponse.class);
        } else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            InputStream responseBody = connection.getErrorStream();
            String response = readString(responseBody);
            Gson gson = new Gson();
            loginResponse = gson.fromJson(response, LoginResponse.class);
        } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            InputStream responseBody = connection.getErrorStream();
            String response = readString(responseBody);
            Gson gson = new Gson();
            loginResponse = gson.fromJson(response, LoginResponse.class);
        } else {
            //SERVER ERROR
            return new LoginResponse("HTTP SERVER ERROR");
        }
        return loginResponse;
    }

    public EventResponse getEvents(EventRequest eventRequest) throws IOException {
        URL url = new URL(urlString + "/event");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.setDoOutput(false);

        Cache cache = Cache.getInstance();

        connection.addRequestProperty("Accept", "text/html");
        connection.addRequestProperty("Authorization", cache.getAuthToken());

        connection.connect();
        /*try (OutputStream requestBody = connection.getOutputStream();) {
            String json = ObjectToJson.Encode(eventRequest);
            requestBody.write(json.getBytes(Charset.defaultCharset()));
        }*/
        EventResponse eventResponse;
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            String response = readString(responseBody);
            Gson gson = new Gson();
            eventResponse = gson.fromJson(response, EventResponse.class);
            return eventResponse;
        } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            InputStream responseBody = connection.getErrorStream();
            String response = readString(responseBody);
            Gson gson = new Gson();
            eventResponse = gson.fromJson(response, EventResponse.class);
            return eventResponse;
        } else {
            //SERVER ERROR
            return new EventResponse("HTTP SERVER ERROR");
        }
    }

    public PersonResponse getPersons(PersonRequest personRequest) throws IOException {
        URL url = new URL(urlString + "/person");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        connection.setDoOutput(false);

        Cache cache = Cache.getInstance();

        connection.addRequestProperty("Accept", "text/html");
        connection.addRequestProperty("Authorization", cache.getAuthToken());

        connection.connect();
        PersonResponse personResponse;
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            String response = readString(responseBody);
            Gson gson = new Gson();
            personResponse = gson.fromJson(response, PersonResponse.class);
            return personResponse;
        } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            InputStream responseBody = connection.getErrorStream();
            String response = readString(responseBody);
            Gson gson = new Gson();
            personResponse = gson.fromJson(response, PersonResponse.class);
            return personResponse;
        } else {
            //SERVER ERROR
            return new PersonResponse("HTTP SERVER ERROR");
        }
    }

    private String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    /**
     * Writes a string to an output stream
     */
    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(sw);
        bw.write(str);
        bw.flush();
    }
}
