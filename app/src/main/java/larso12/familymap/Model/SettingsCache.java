package larso12.familymap.Model;

import java.util.HashSet;
import java.util.Set;

public class SettingsCache {
    private static SettingsCache cache;

    private SettingsCache() {
        eventTypes = new HashSet<>();
        colorLifeLines = "Green";
        showLifeLines = true;
        colorSpouseLines = "Blue";
        showSpouseLines = true;
        colorFamilyTreeLines = "Red";
        showFamilyTreeLines = true;
        currentMapType = "Satellite";
        showMaleEvents = true;
        showFemaleEvents = true;
        showMotherSide = true;
        showFatherSide = true;
        showMarriageEvents = true;
        showDeathEvents = true;
        showBirthEvents = true;
    }

    public static SettingsCache getSettingsCache() {
        if (cache == null) {
            cache = new SettingsCache();
        }
        return cache;
    }

    //********From Settings Activity

    private String colorLifeLines;
    private boolean showLifeLines;

    private String colorSpouseLines;
    private boolean showSpouseLines;

    private String colorFamilyTreeLines;
    private boolean showFamilyTreeLines;

    private String currentMapType;



    //***********From Filter Activity

    private boolean showMaleEvents;
    private boolean showFemaleEvents;
    private boolean showMotherSide;
    private boolean showFatherSide;
    private boolean showMarriageEvents;
    private boolean showDeathEvents;
    private boolean showBirthEvents;

    private Set<String> eventTypes;


    public void restoreDefaults() {
        eventTypes.clear();
        colorLifeLines = "Green";
        showLifeLines = true;
        colorSpouseLines = "Blue";
        showSpouseLines = true;
        colorFamilyTreeLines = "Red";
        showFamilyTreeLines = true;
        currentMapType = "Satellite";
        showMaleEvents = true;
        showFemaleEvents = true;
        showMotherSide = true;
        showFatherSide = true;
        showMarriageEvents = true;
        showDeathEvents = true;
        showBirthEvents = true;
    }

    public static SettingsCache getCache() {
        return cache;
    }

    public static void setCache(SettingsCache cache) {
        SettingsCache.cache = cache;
    }

    public String getColorLifeLines() {
        return colorLifeLines;
    }

    public void setColorLifeLines(String colorLifeLines) {
        this.colorLifeLines = colorLifeLines;
    }

    public boolean isShowLifeLines() {
        return showLifeLines;
    }

    public void setShowLifeLines(boolean showLifeLines) {
        this.showLifeLines = showLifeLines;
    }

    public String getColorSpouseLines() {
        return colorSpouseLines;
    }

    public void setColorSpouseLines(String colorSpouseLines) {
        this.colorSpouseLines = colorSpouseLines;
    }

    public boolean isShowSpouseLines() {
        return showSpouseLines;
    }

    public void setShowSpouseLines(boolean showSpouseLines) {
        this.showSpouseLines = showSpouseLines;
    }

    public String getColorFamilyTreeLines() {
        return colorFamilyTreeLines;
    }

    public void setColorFamilyTreeLines(String colorFamilyTreeLines) {
        this.colorFamilyTreeLines = colorFamilyTreeLines;
    }

    public boolean isShowFamilyTreeLines() {
        return showFamilyTreeLines;
    }

    public void setShowFamilyTreeLines(boolean showFamilyTreeLines) {
        this.showFamilyTreeLines = showFamilyTreeLines;
    }

    public String getCurrentMapType() {
        return currentMapType;
    }

    public void setCurrentMapType(String currentMapType) {
        this.currentMapType = currentMapType;
    }

    public boolean isShowMaleEvents() {
        return showMaleEvents;
    }

    public void setShowMaleEvents(boolean showMaleEvents) {
        this.showMaleEvents = showMaleEvents;
    }

    public boolean isShowFemaleEvents() {
        return showFemaleEvents;
    }

    public void setShowFemaleEvents(boolean showFemaleEvents) {
        this.showFemaleEvents = showFemaleEvents;
    }

    public boolean isShowMotherSide() {
        return showMotherSide;
    }

    public void setShowMotherSide(boolean showMotherSide) {
        this.showMotherSide = showMotherSide;
    }

    public boolean isShowFatherSide() {
        return showFatherSide;
    }

    public void setShowFatherSide(boolean showFatherSide) {
        this.showFatherSide = showFatherSide;
    }

    public boolean isShowMarriageEvents() {
        return showMarriageEvents;
    }

    public void setShowMarriageEvents(boolean showMarriageEvents) {
        this.showMarriageEvents = showMarriageEvents;
    }

    public boolean isShowDeathEvents() {
        return showDeathEvents;
    }

    public void setShowDeathEvents(boolean showDeathEvents) {
        this.showDeathEvents = showDeathEvents;
    }

    public boolean isShowBirthEvents() {
        return showBirthEvents;
    }

    public void setShowBirthEvents(boolean showBirthEvents) {
        this.showBirthEvents = showBirthEvents;
    }

    public Set<String> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes(Set<String> eventTypes) {
        this.eventTypes = eventTypes;
    }
}
