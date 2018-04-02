package pl.edu.agh.music.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.music.filestorage.StorageService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepo userRepo;
    private final StorageService storageService;
    private final PlaylistRepo playlistRepo;

    @Autowired
    public UserController(UserRepo userRepo, StorageService storageService, PlaylistRepo playlistRepo) {
        this.userRepo = userRepo;
        this.storageService = storageService;
        this.playlistRepo = playlistRepo;
    }


    @GetMapping
    public List<User> getAll() {
        return userRepo.findAll();
    }

    @PostMapping
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

    @DeleteMapping(value="{id}")
    public void delete(@PathVariable String id) {
        userRepo.findById(id).ifPresent(userRepo::delete);
    }

    @PutMapping("{id}")
    public User update(@PathVariable("id") String id, @RequestBody User user){
        User userToUpdate = userRepo.findById(id).get();
        userToUpdate.setPasswd(user.getPasswd());
        userToUpdate.setUserName(user.getUserName());
        return userRepo.save(userToUpdate);
    }


}
