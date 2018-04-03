package pl.edu.agh.music.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.edu.agh.music.MusicApplication;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Document
public class Playlist {

    @Id
    private String id;
    private String playlistName;
    private URI iconAddress;
    private List<String> mp3FileFeaturesList = new ArrayList<>();
    private String userId;



    public Playlist()  {
        try {
            iconAddress = new URI(MusicApplication.DEFAULT_ICON_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    Playlist(String playlistName){
        this();
        this.playlistName = playlistName;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId(){
        return userId;
    }

    public List<String> getMp3FileFeaturesList() {
        return mp3FileFeaturesList;
    }


    public String getId(){
        return id;
    }

    @Override
    public String toString(){
        return "Playlist " + playlistName + " of " + userId + " id " + id;
    }

    public URI getIconAddress() {
        return iconAddress;
    }

    public void setIconAddress(URI iconAddress) {
        this.iconAddress = iconAddress;
    }

    public void setPlaylistName(String playlistName){
        this.playlistName = playlistName;
    }

    public String getPlaylistName(){
        return playlistName;
    }
}
