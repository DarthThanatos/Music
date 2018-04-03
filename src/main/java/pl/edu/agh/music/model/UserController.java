package pl.edu.agh.music.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.music.filestorage.StorageService;

import java.util.List;

@RestController
public class UserController {

    private final UserRepo userRepo;
    private final StorageService storageService;
    private final PlaylistRepo playlistRepo;
    private final MP3Repo mp3Repo;

    @Autowired
    public UserController(UserRepo userRepo, StorageService storageService, PlaylistRepo playlistRepo, MP3Repo mp3Repo) {
        this.userRepo = userRepo;
        this.storageService = storageService;
        this.playlistRepo = playlistRepo;
        this.mp3Repo = mp3Repo;
    }


    @GetMapping("/users")
    public List<User> getAll() {
        return userRepo.findAll();
    }

    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable String userId){
        return userRepo.findById(userId).get();
    }

    @PostMapping("/users")
    public User create(@RequestBody User user) {
        User savedUser = userRepo.save(user);
        storageService.createUserDir(savedUser.getId());

        Playlist playlist = new Playlist("global");
        playlist.setUserId(savedUser.getId());
        Playlist savedGlobalPlaylist = playlistRepo.save(playlist);

        savedUser.getPlaylists().add(savedGlobalPlaylist.getId());
        savedUser = userRepo.save(savedUser);
        return savedUser;
    }

    @DeleteMapping("/users/{id}")
    public void delete(@PathVariable String id) {
        User user = userRepo.findById(id).get();

        user.getPlaylists().forEach(playlistId -> {
            playlistRepo.findById(playlistId).get().getMp3FileFeaturesList().forEach(
                    musicId -> {
                        mp3Repo.findById(musicId).ifPresent(mp3Repo::delete);
                    });
            playlistRepo.deleteById(playlistId);
        });
        storageService.deleteUserDir(user.getId());
        userRepo.delete(user);
    }

    @PutMapping("/users/{id}")
    public User update(@PathVariable("id") String id, @RequestBody User user){
        User userToUpdate = userRepo.findById(id).get();
        userToUpdate.setPasswd(user.getPasswd());
        userToUpdate.setUserName(user.getUserName());
        return userRepo.save(userToUpdate);
    }


}
