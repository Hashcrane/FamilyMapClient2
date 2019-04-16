package larso12.familymap;

import java.util.ArrayList;
import java.util.Collections;

import models.Event;
import models.Person;

public class ClientUtilities {
    public static String concatName(Person person) {
        return person.getF_name() + " " + person.getL_name();
    }

    public static String concatEvent(Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append(event.getEventType()).append(": ").append(event.getCity()).append(", ");
        sb.append(event.getCountry()).append(" ").append("(").append(event.getYear()).append(")");
        return sb.toString();
    }

    public static ArrayList<Event> sortByYear(ArrayList<Event> events) {
        ArrayList<Event> sorted = new ArrayList<>();
        ArrayList<Integer> years = new ArrayList<>();
        for (Event event: events) {
            years.add(event.getYear());
        }
        Collections.sort(years);
        int count = 0;
        while (sorted.size() < events.size()) {
            for (Event event : events) {
                if (count >= years.size()) break;
                if (event.getYear() == years.get(count)) {
                    sorted.add(event);
                    ++count;
                }
            }
        }
        return sorted;
    }
}
