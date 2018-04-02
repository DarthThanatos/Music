package pl.edu.agh.music.model;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/playlists")
public class PlayListController {

    private final PlaylistRepo playlistRepo;
    private final MP3Repo mp3Repo;

    public PlayListController(PlaylistRepo playlistRepo, MP3Repo mp3Repo) {
        this.playlistRepo = playlistRepo;
        this.mp3Repo = mp3Repo;
    }

    @GetMapping
    public List<String> getAllPlaylists(){
        return playlistRepo.findAll().stream().map(Playlist::toString).collect(Collectors.toList());
    }

    @GetMapping("mp3s/{playlistId}")
    public List<MP3FileFeatures> getMp3sOf(@PathVariable("playlistId") String playlistId){
        return playlistRepo.findById(playlistId).get().getMp3FileFeaturesList().stream().map(s -> mp3Repo.findById(s).get()).collect(Collectors.toList());
    }
}
