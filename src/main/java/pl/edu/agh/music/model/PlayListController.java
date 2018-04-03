package pl.edu.agh.music.model;


import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PlayListController {

    private final PlaylistRepo playlistRepo;
    private final MP3Repo mp3Repo;
    private final UserRepo userRepo;

    public PlayListController(PlaylistRepo playlistRepo, MP3Repo mp3Repo, UserRepo userRepo) {
        this.playlistRepo = playlistRepo;
        this.mp3Repo = mp3Repo;
        this.userRepo = userRepo;
    }

    @GetMapping("/playlists")
    public List<Playlist> getAllPlaylists(){
        return playlistRepo.findAll();
    }

    @GetMapping("/playlists/{playlistId}")
    public Playlist getPlayListWithId(@PathVariable("playlistId") String playlistId){
        return playlistRepo.findById(playlistId).get();
    }

    @DeleteMapping("/playlists/{playlistId}")
    public void deletePlaylist(@PathVariable("playlistId") String playlistId){
        Playlist playlist = playlistRepo.findById(playlistId).get();
        if(playlist.getPlaylistName().equals("global")) return;
        playlist.getMp3FileFeaturesList().forEach(
                musicId -> {
                    mp3Repo.findById(musicId).ifPresent(
                            mp3FileFeatures -> {
                                mp3FileFeatures.getPlaylists().remove(playlistId);
                                mp3Repo.save(mp3FileFeatures);
                            }

                    );
                });
        User user = userRepo.findById(playlist.getUserId()).get();
        user.getPlaylists().remove(playlist.getId());
        userRepo.save(user);

        playlistRepo.deleteById(playlistId);
    }

    @DeleteMapping("/playlists/removeMp3NotGlobal/{playlistId}/{musicId}")
    public void removeMp3FromPlaylist(@PathVariable("playlistId") String playlistId, @PathVariable("musicId") String musicId){
        Playlist playlist = playlistRepo.findById(playlistId).get();
        if(playlist.getPlaylistName().equals("global")) return;
        MP3FileFeatures mp3FileFeatures = mp3Repo.findById(musicId).get();

        mp3FileFeatures.getPlaylists().remove(playlistId);
        mp3Repo.save(mp3FileFeatures);

        playlist.getMp3FileFeaturesList().remove(musicId);
        playlistRepo.save(playlist);
    }


    @GetMapping("playlists/user/{userId}")
    public List<Playlist> getUserPlaylists(@PathVariable("userId") String userId){
        return playlistRepo.findPlaylistsByUserId(userId);
    }

    @GetMapping("/playlists/mp3s/{playlistId}")
    public List<MP3FileFeatures> getMp3sOf(@PathVariable("playlistId") String playlistId){
        return playlistRepo.findById(playlistId).get().getMp3FileFeaturesList().stream().map(s -> mp3Repo.findById(s).get()).collect(Collectors.toList());
    }

    @PostMapping("/playlists/new/{userId}/{playlistName}")
    public Playlist newPlaylist(@PathVariable("userId") String userId, @PathVariable("playlistName") String playlistName){
        if(playlistName.equals("global")) return null;
        User user = userRepo.findById(userId).get();
        Playlist playlist = new Playlist(playlistName);
        playlist.setUserId(user.getId());
        Playlist savedPlaylist = playlistRepo.save(playlist);
        user.getPlaylists().add(savedPlaylist.getId());
        userRepo.save(user);
        return savedPlaylist;

    }

    @PutMapping("/playlists/addMusic/{userId}/{playlistId}/{musicId}")
    public Playlist addMusicToPlaylist(@PathVariable("userId") String userId, @PathVariable("playlistId") String playlistId, @PathVariable("musicId") String musicId){
        if(playlistRepo.findById(playlistId).get().getPlaylistName().equals("global")) return null;
        if(playlistRepo.findPlaylistsByPlaylistNameAndUserId("global", userId).get(0).getMp3FileFeaturesList().contains(musicId)) {

            MP3FileFeatures mp3FileFeatures = mp3Repo.findById(musicId).get();
            mp3FileFeatures.getPlaylists().add(playlistId);
            mp3Repo.save(mp3FileFeatures);

            Playlist playlist = playlistRepo.findById(playlistId).get();
            playlist.getMp3FileFeaturesList().add(musicId);
            return playlistRepo.save(playlist);
        }
        return null;
    }



    @PutMapping("/playlists/{userId}/{playlistId}")
    public Playlist updatePlaylistWithId(@PathVariable("userId") String userId, @PathVariable("playlistId") String playlistId, @RequestBody Playlist updater){
        if(userRepo.findById(userId).get().getPlaylists().contains(playlistId)){
            Playlist playlist = playlistRepo.findById(playlistId).get();
            playlist.setIconAddress(updater.getIconAddress());
            if(!playlist.getPlaylistName().equals("global"))
                playlist.setPlaylistName(updater.getPlaylistName());
            return playlistRepo.save(playlist);
        }
        return null;
    }

}
