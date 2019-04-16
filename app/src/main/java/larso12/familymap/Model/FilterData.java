package larso12.familymap.Model;

import java.util.Objects;

public class FilterData {
    public String getEventType() {
        return eventType;
    }

    private final String eventType;
    private boolean show;

    public FilterData(String eventType){
        this.eventType = eventType;
        show = true;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterData)) return false;
        FilterData that = (FilterData) o;
        return show == that.show &&
                Objects.equals(eventType, that.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, show);
    }
}
