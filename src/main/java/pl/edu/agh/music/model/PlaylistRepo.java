package pl.edu.agh.music.model;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PlaylistRepo extends MongoRepository<Playlist, String> {

    List<Playlist> findPlaylistsByPlaylistNameAndUserId(String playlistName, String userId);
    List<Playlist> findPlaylistsByUserId(String userId);
}
