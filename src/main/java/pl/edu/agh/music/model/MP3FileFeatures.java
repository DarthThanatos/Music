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
public class MP3FileFeatures {

    @Id
    private String mp3Id;
    private String fileName;
    private String nativeText;
    private String translationText;
    private List<String> attributes;
    private URI iconAddress;

    private List<String> playlists = new ArrayList<>();

    public MP3FileFeatures(){
        try {
            iconAddress = new URI(MusicApplication.DEFAULT_ICON_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public String getMp3Id() {
        return mp3Id;
    }

    public void setMp3Id(String mp3Id) {
        this.mp3Id = mp3Id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getNativeText() {
        return nativeText;
    }

    public void setNativeText(String nativeText) {
        this.nativeText = nativeText;
    }

    public String getTranslationText() {
        return translationText;
    }

    public void setTranslationText(String translationText) {
        this.translationText = translationText;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public URI getIconAddress() {
        return iconAddress;
    }

    public void setIconAddress(URI iconAddress) {
        this.iconAddress = iconAddress;
    }

    public List<String> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<String> playlists) {
        this.playlists = playlists;
    }
}
