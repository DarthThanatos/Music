package pl.edu.agh.music.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;

public class ArtistsInfo {

    private String bandName;
    private List<String> bandAliases;
    private List<String> artistsNames;
    private List<String> eventsInfo;
    private List<String> toursInfo;

    public ArtistsInfo(){
        this.bandName = "Unknown artist";
        bandAliases = new ArrayList<>();
        artistsNames = new ArrayList<>();
        eventsInfo = new ArrayList<>();
        toursInfo = new ArrayList<>();
    }

    public ArtistsInfo(String bandName){
        this();
        this.bandName = bandName;
    }


    public void addAlias(String alias){
        bandAliases.add(alias);
    }

    public void addArtistName(String artistName){
        artistsNames.add(artistName);
    }

    public void addEventInfo(String eventInfo){
        eventsInfo.add(eventInfo);
    }

    public void addTourInfo(String tourInfo){
        toursInfo.add(tourInfo);
    }

    public String getBandName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public List<String> getBandAliases() {
        return bandAliases;
    }

    public void setBandAliases(List<String> bandAliases) {
        this.bandAliases = bandAliases;
    }

    public List<String> getArtistsNames() {
        return artistsNames;
    }

    public void setArtistsNames(List<String> artistsNames) {
        this.artistsNames = artistsNames;
    }

    public List<String> getEventsInfo() {
        return eventsInfo;
    }

    public void setEventsInfo(List<String> eventsInfo) {
        this.eventsInfo = eventsInfo;
    }

    public List<String> getToursInfo() {
        return toursInfo;
    }

    public void setToursInfo(List<String> toursInfo) {
        this.toursInfo = toursInfo;
    }
}
