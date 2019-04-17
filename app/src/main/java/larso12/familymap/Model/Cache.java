package larso12.familymap.Model;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import models.Event;
import models.Person;
import models.User;

public class Cache {
    public Map<String, ArrayList<Event>> getOtherEvents() {
        return otherEvents;
    }

    public Map<String, Event> getBirthEvents() {
        return birthEvents;
    }

    public Map<String, Event> getDeathEvents() {
        return deathEvents;
    }

    public Map<String, Event> getMarriageEvents() {
        return marriageEvents;
    }

    public Map<String, Event> getCurrentDisplayedEvents() {
        return currentDisplayedEvents;
    }

    private static Cache cache;


    /**
     * Singleton that handles local data and sorts it for access
     */
    private Cache() {
        allEvents = new ArrayList<>();
        allPersons = new ArrayList<>();
        birthEvents = new HashMap<>();
        deathEvents = new HashMap<>();
        marriageEvents = new HashMap<>();
        currentDisplayedEvents = new HashMap<>();
        personTree = new Tree();
        filteredPersons = new HashMap<>();
        otherEvents = new HashMap<>();
        personIDToEvents = new HashMap<>();
        personIDToPerson = new HashMap<>();
        allEventTypes = new TreeMap<>();
        setEventTypes = new HashSet<>();
    }

    public static Cache getInstance() {
        if (cache == null) {
            cache = new Cache();
        }
        return cache;
    }

    private Marker marker;
    private Tree personTree;
    private User currentUser;
    private Person currentUserPerson;
    private String authToken;

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    private String serverHost;
    private String serverPort;

    public String getCurrentUserPersonID() {
        return currentUserPersonID;
    }

    public void setCurrentUserPersonID(String currentUserPersonID) {
        this.currentUserPersonID = currentUserPersonID;
    }

    private String currentUserPersonID;

    private ArrayList<Person> allPersons;
    private ArrayList<Event> allEvents;
    /**
     * personID key to person's birth event
     */
    private Map<String, Event> birthEvents;
    /**
     * personID key to person's death event
     */
    private Map<String, Event> deathEvents;

    /**
     * personID key to person's marriage event
     */
    private Map<String, Event> marriageEvents;

    /**
     * personID key to all person's events
     */
    private Map<String, ArrayList<Event>> personIDToEvents;

    /**
     * indexed by type of events
     */
    private Map<String, ArrayList<Event>> otherEvents;

    /**
     * Map of EventID to Event Object
     */
    private Map<String, Event> currentDisplayedEvents;


    /**
     * map person id to person object
     */
    private Map<String, Person> filteredPersons;


    public Map<String, ArrayList<Event>> getPersonIDToEvents() {
        return personIDToEvents;
    }

    /**
     * holds all persons in database
     */
    private Map<String, Person> personIDToPerson;

    public Map<String, FilterData> getAllEventTypes() {
        return allEventTypes;
    }

    /**
     * set of event types. Marriage events are simplified to just marriage
     * This becomes the list that the recycler view in the filter activity uses
     */
    private Map<String, FilterData> allEventTypes;

    private Set<String> setEventTypes;

    /**
     * to be called when resyncing or logging in
     */
    public void clearAll() {
        marker = null;
        filteredPersons.clear();
        allPersons.clear();
        allEvents.clear();
        birthEvents.clear();
        deathEvents.clear();
        marriageEvents.clear();
        currentDisplayedEvents.clear();
        otherEvents.clear();
        personIDToEvents.clear();
        personTree = null;
        personTree = new Tree();
        personIDToPerson.clear();
        setEventTypes.clear();
        allEventTypes.clear();
    }

    /**
     * for use when syncing data in the cache, not for setting filters
     */
    public void sortAll() {
//FIXME
        sortAllFilteredEvents();
        setCurrentSearchablePersons();
        setCurrentDisplayedEvents();
    }

    public Map<String, Person> getFilteredPersons() {
        return filteredPersons;
    }

    public void setPersonObject() {
        for (Person person : allPersons) {
            if (currentUserPersonID.equals(person.getID())) {
                currentUserPerson = person;
                return;
            }
        }
    }


    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Person getCurrentUserPerson() {
        return currentUserPerson;
    }

    public void setCurrentUserPerson(Person currentUserPerson) {
        this.currentUserPerson = currentUserPerson;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public ArrayList<Person> getAllPersons() {
        return allPersons;
    }

    public void setAllPersons(ArrayList<Person> allPersons) {
        this.allPersons = allPersons;
        if (!allPersons.isEmpty()) {
            sortPersons();
        }
        for (Person person : allPersons) {
            personIDToPerson.put(person.getID(), person);
        }
    }

    public ArrayList<Event> getAllEvents() {
        return allEvents;
    }

    public void setAllEvents(ArrayList<Event> allEvents) {
        this.allEvents = allEvents;
        sortEvents();
    }

    private void sortEvents() {
        if (setEventTypes.add("father's side")) allEventTypes.put("father's side", new FilterData("father's side"));
        if (setEventTypes.add("mother's side")) allEventTypes.put("mother's side", new FilterData("mother's side"));
        if (setEventTypes.add("male events")) allEventTypes.put("male events", new FilterData("male events"));
        if (setEventTypes.add("female events")) allEventTypes.put("female events", new FilterData("female events"));
        if (setEventTypes.add("birth events")) allEventTypes.put("birth events", new FilterData("birth events"));
        if (setEventTypes.add("death events")) allEventTypes.put("death events", new FilterData("death events"));
        if (setEventTypes.add("marriage events")) allEventTypes.put("marriage events", new FilterData("marriage events"));

        for (Event event : allEvents) {
            switch (event.getEventType().toLowerCase()) {
                case "birth":
                    birthEvents.put(event.getPersonID(), event);
                    break;
                case "death":
                    deathEvents.put(event.getPersonID(), event);
                    break;
                default:
                    if (event.getEventType().toLowerCase().contains("marriage")) {
                        marriageEvents.put(event.getPersonID(), event);
                    } else {
                        if (setEventTypes.add(event.getEventType().toLowerCase())) allEventTypes.put(event.getEventType().toLowerCase(), new FilterData(event.getEventType()));
                        if (otherEvents.containsKey(event.getEventType().toLowerCase())) {
                            otherEvents.get(event.getEventType().toLowerCase()).add(event);
                        } else {
                            ArrayList<Event> newEventType = new ArrayList<>();
                            newEventType.add(event);
                            otherEvents.put(event.getEventType().toLowerCase(), newEventType);
                        }
                    }

                    break;
            }
        }
    }

    /**
     * wrapper method for sorting maps into personIDToEvents
     */
    private void sortAllFilteredEvents() {
        personIDToEvents.clear();
        sortFilteredEvents(birthEvents);
        sortFilteredEvents(deathEvents);
        sortFilteredEvents(marriageEvents);

        for (Map.Entry<String, ArrayList<Event>> eventList : otherEvents.entrySet()) {
            for (Event event : eventList.getValue()) {
                if (!personIDToEvents.containsKey(event.getPersonID())) {
                    ArrayList<Event> events = new ArrayList<>();
                    events.add(event);
                    personIDToEvents.put(event.getPersonID(), events);
                } else {
                    personIDToEvents.get(event.getPersonID()).add(event);
                }
            }
        }
    }

    /**
     * populates the PersonIDToEvents Map so a list of events are searchable by person id
     *
     * @param map
     */
    private void sortFilteredEvents(Map<String, Event> map) {
        for (Map.Entry<String, Event> entry : map.entrySet()) {
            if (!personIDToEvents.containsKey(entry.getValue().getPersonID())) {
                ArrayList<Event> events = new ArrayList<>();
                events.add(entry.getValue());
                personIDToEvents.put(entry.getValue().getPersonID(), events);
            } else {
                personIDToEvents.get(entry.getValue().getPersonID()).add(entry.getValue());
            }
        }
    }

    /**
     * Fills out the personTree until every node has been added,
     * currently may iterate over full list multiple times
     */
    private void sortPersons() {
        while (personTree.numPersons != allPersons.size()) {
            for (Person person : allPersons) {
                if (person.getID().equals(currentUserPersonID)) {
                    personTree.setRootUser(person);
                } else {
                    personTree.addNode(person);
                }
            }
        }
    }

    /**
     * creates a list of IDs to aid in searching for correct events
     * filtered persons map is created
     */
    private void setCurrentSearchablePersons() {
        filteredPersons.clear();
        SettingsCache sCache = SettingsCache.getSettingsCache();
        if (sCache.isShowFatherSide() && sCache.isShowMotherSide() &&
                sCache.isShowMaleEvents() && sCache.isShowFemaleEvents()) {
            for (Person person : allPersons) {
                //grab all persons
                filteredPersons.put(person.getID(), person);
            }
        } else if (sCache.isShowFatherSide() && sCache.isShowMotherSide() &&
                !sCache.isShowMaleEvents() && sCache.isShowFemaleEvents()) {
            //grab all female persons
            for (Person person : allPersons) {
                //grab all persons
                if (person.getGender().toLowerCase().equals("f"))
                    filteredPersons.put(person.getID(), person);
            }

        } else if (sCache.isShowFatherSide() && sCache.isShowMotherSide() &&
                sCache.isShowMaleEvents() && !sCache.isShowFemaleEvents()) {
            //grab all male persons
            for (Person person : allPersons) {
                //grab all persons
                if (person.getGender().toLowerCase().equals("m"))
                    filteredPersons.put(person.getID(), person);
            }

        } else if (!sCache.isShowFatherSide() && sCache.isShowMotherSide() &&
                sCache.isShowMaleEvents() && sCache.isShowFemaleEvents()) {
            //grab all persons on mother side
            for (Person person : personTree.getMotherSide()) {
                filteredPersons.put(person.getID(), person);
            }

        } else if (sCache.isShowFatherSide() && !sCache.isShowMotherSide() &&
                sCache.isShowMaleEvents() && sCache.isShowFemaleEvents()) {
            //grab all persons on father side
            for (Person person : personTree.getFatherSide()) {
                filteredPersons.put(person.getID(), person);
            }

        } else if (!sCache.isShowFatherSide() && sCache.isShowMotherSide() &&
                !sCache.isShowMaleEvents() && sCache.isShowFemaleEvents()) {
            //grab females on mother side
            for (Person person : personTree.getMotherSide()) {
                if (person.getGender().toLowerCase().equals("f")) {
                    filteredPersons.put(person.getID(), person);
                }
            }

        } else if (!sCache.isShowFatherSide() && sCache.isShowMotherSide() &&
                sCache.isShowMaleEvents() && !sCache.isShowFemaleEvents()) {
            //grab males on mother side
            for (Person person : personTree.getMotherSide()) {
                if (person.getGender().toLowerCase().equals("m")) {
                    filteredPersons.put(person.getID(), person);
                }
            }

        } else if (sCache.isShowFatherSide() && !sCache.isShowMotherSide() &&
                sCache.isShowMaleEvents() && !sCache.isShowFemaleEvents()) {
            //grab males on father side
            for (Person person : personTree.getFatherSide()) {
                if (person.getGender().toLowerCase().equals("m")) {
                    filteredPersons.put(person.getID(), person);
                }
            }

        } else if (sCache.isShowFatherSide() && !sCache.isShowMotherSide() &&
                !sCache.isShowMaleEvents() && sCache.isShowFemaleEvents()) {
            //grab females on father side
            for (Person person : personTree.getFatherSide()) {
                if (person.getGender().toLowerCase().equals("f")) {
                    filteredPersons.put(person.getID(), person);
                }
            }

        } else {
            //grab nothing
        }
    }

    /**
     * uses filtered persons list to display events according to settings activity
     * and filter activity
     * When called, filtered persons will only contain persons to be displayed: male, female, which
     * side of the family
     */
    public void setCurrentDisplayedEvents() {
        currentDisplayedEvents.clear();
        SettingsCache sCache = SettingsCache.getSettingsCache();
        if (sCache.isShowBirthEvents()) {
            for (Map.Entry<String, Person> entry : filteredPersons.entrySet()) {
                if (birthEvents.containsKey(entry.getKey())) {
                    currentDisplayedEvents.put(birthEvents.get(entry.getKey()).getEventID(),
                            birthEvents.get(entry.getKey()));
                }
            }
        }
        if (sCache.isShowDeathEvents()) {
            for (Map.Entry<String, Person> entry : filteredPersons.entrySet()) {
                if (deathEvents.containsKey(entry.getKey())) {
                    currentDisplayedEvents.put(deathEvents.get(entry.getKey()).getEventID(),
                            deathEvents.get(entry.getKey()));
                }
            }
        }
        if (sCache.isShowMarriageEvents()) {
            for (Map.Entry<String, Person> entry : filteredPersons.entrySet()) {
                if (marriageEvents.containsKey(entry.getKey())) {
                    currentDisplayedEvents.put(marriageEvents.get(entry.getKey()).getEventID(),
                            marriageEvents.get(entry.getKey()));
                }
            }
        }

        for (Map.Entry<String, ArrayList<Event>> entry: otherEvents.entrySet()) {
            if (allEventTypes.get(entry.getKey()).isShow()) {
                for (Event event: entry.getValue()) {
                    if (!filteredPersons.isEmpty()) {
                        if (filteredPersons.containsKey(event.getPersonID())) {
                            currentDisplayedEvents.put(event.getEventID(), event);
                        }
                    }
                }
            }
        }
    }

    public ArrayList<Person> getPersonActRelatedPersonList(Person person) {
        ArrayList<Person> persons = new ArrayList<>();
        if (person.getFather() != null) {
            persons.add(personIDToPerson.get(person.getFather()));
        }
        if (person.getMother() != null) {
            persons.add(personIDToPerson.get(person.getMother()));
        }
        if (person.getSpouse() != null) {
            persons.add(personIDToPerson.get(person.getSpouse()));
        }
        for (Map.Entry<String, Person> entry : personIDToPerson.entrySet()) {
            if (entry.getValue().getMother() != null && entry.getValue().getFather() != null) {
                if (entry.getValue().getMother().equals(person.getID()) ||
                        entry.getValue().getFather().equals(person.getID())) {
                    persons.add(entry.getValue());
                    break;
                }
            }
        }
        return persons;
    }

    /**
     * tree to store all persons for easy sorting and access
     */
    private class Tree {
        Node rootUser;
        int numPersons;

        Tree() {
            rootUser = new Node();
            numPersons = 0;
        }

        /**
         * @return list of all persons on mother side of tree
         */
        ArrayList<Person> getMotherSide() {
            return getAllFromNewRoot(rootUser.nMom);
        }

        /**
         * @return list of all persons on father side of tree
         */
        ArrayList<Person> getFatherSide() {
            return getAllFromNewRoot(rootUser.nDad);
        }


        /**
         * Makes a list of all persons that are parents or greater than a designated child
         *
         * @param newRoot child to get parents and ancestors of
         * @return list of all ancestors from node
         */
        ArrayList<Person> getAllFromNewRoot(Node newRoot) {
            ArrayList<Node> list = new ArrayList<>();
            list.add(newRoot.nDad);
            list.add(newRoot.nMom);
            ArrayList<Person> finalPersons = new ArrayList<>();

            while (!list.isEmpty()) {
                if (list.get(0).nDad != null) {
                    list.add(list.get(0).nDad);
                }
                if (list.get(0).nMom != null) {
                    list.add(list.get(0).nMom);
                }
                finalPersons.add(list.get(0).currentPerson);
                list.remove(0);
            }
            return finalPersons;
        }

        void setRootUser(Person person) {
            rootUser.currentPerson = person;
            rootUser.dad = person.getFather();
            rootUser.mom = person.getMother();
            numPersons = 1;
        }

        boolean addNode(Person person) {
            if (rootUser == null) return false;

            if (rootUser.nMom == null) {
                if (person.getID().equals(rootUser.mom)) {
                    Node n = new Node(person);
                    rootUser.nMom = n;
                    ++numPersons;
                    return true;
                }
            }
            if (rootUser.nDad == null) {
                if (person.getID().equals(rootUser.dad)) {
                    Node n = new Node(person);
                    rootUser.nDad = n;
                    ++numPersons;
                    return true;
                }
            }
            if (addNode(person, rootUser.nMom)) return true;

            else return addNode(person, rootUser.nDad);

        }

        boolean addNode(Person person, Node currentNode) {

            if (currentNode == null) return false;

            if (currentNode.mom != null && currentNode.nMom == null) {
                if (person.getID().equals(currentNode.mom)) {
                    Node n = new Node(person);
                    currentNode.nMom = n;
                    ++numPersons;
                    return true;
                }
            } if (currentNode.dad != null && currentNode.nDad == null) {
                if (person.getID().equals(currentNode.dad)) {
                    Node n = new Node(person);
                    currentNode.nDad = n;
                    ++numPersons;
                    return true;
                }
            }
            if (addNode(person, currentNode.nMom)) return true;

            else return addNode(person, currentNode.nDad);
        }

        private class Node {
            String mom;
            Node nMom;
            String dad;
            Node nDad;
            Person currentPerson;

            Node() {}

            Node(Person person) {
                currentPerson = person;
                dad = person.getFather();
                mom = person.getMother();
            }
        }
    }
}