package pl.edu.agh.music.model;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.music.filestorage.StorageFileNotFoundException;
import pl.edu.agh.music.filestorage.StorageService;


@RestController
public class MP3UploadController {

    private final StorageService storageService;
    private final MP3Repo mp3Repo;
    private final PlaylistRepo playlistRepo;
    private final UserRepo userRepo;
    private final List<String> possibleExts = Arrays.asList(".wma", ".mp3");

    @Autowired
    public MP3UploadController(StorageService storageService, MP3Repo mp3Repo, PlaylistRepo playlistRepo, UserRepo userRepo) {
        this.storageService = storageService;
        this.mp3Repo = mp3Repo;
        this.playlistRepo = playlistRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/files")
    public List<String> listUploadedFilesRest(){
        return storageService
                        .loadAll()
                        .map(Path::toString)
                        .collect(Collectors.toList());
    }

    @GetMapping("/userfiles/{userId}")
    public List<String> listUserUploadedFiles(@PathVariable("userId") String userId){
        return storageService
                .loadAllFromDir(userId)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    @GetMapping("/mp3s")
    public List<MP3FileFeatures> getAllMp3s(){
        return mp3Repo.findAll();
    }

    @GetMapping("/mp3s/{musicId}")
    public MP3FileFeatures getMp3ById(@PathVariable("musicId") String musicId){
        return mp3Repo.findById(musicId).get();
    }


    @GetMapping("/userMp3s/{userId}")
    public List<MP3FileFeatures> getAllUsersMp3Features(@PathVariable("userId") String userId){
        return playlistRepo.findPlaylistsByPlaylistNameAndUserId("global", userId).get(0)
                .getMp3FileFeaturesList().stream().map(s -> mp3Repo.findById(s).get()).collect(Collectors.toList());
    }

    @GetMapping("/userMp3s/{userId}/{musicId}")
    public MP3FileFeatures getUserMp3ById(@PathVariable("userId") String userId, @PathVariable("musicId") String musicId){
        if(playlistRepo.findPlaylistsByPlaylistNameAndUserId("global", userId).get(0).getMp3FileFeaturesList().contains(musicId))
            return mp3Repo.findById(musicId).get();
        return null;
    }


    @PutMapping("/userMp3s/{userId}/{musicId}")
    public MP3FileFeatures updateUserMp3ById(@PathVariable("userId") String userId, @PathVariable("musicId") String musicId, @RequestBody MP3FileFeatures updater){
        if(playlistRepo.findPlaylistsByPlaylistNameAndUserId("global", userId).get(0).getMp3FileFeaturesList().contains(musicId)){
            MP3FileFeatures mp3FileFeatures = mp3Repo.findById(musicId).get();
            mp3FileFeatures.setIconAddress(updater.getIconAddress());
            mp3FileFeatures.setNativeText(updater.getNativeText());
            mp3FileFeatures.setTranslationText(updater.getTranslationText());
            mp3FileFeatures.setHowLongListened(updater.getHowLongListened());
            if(!updater.getFileName().equals(mp3FileFeatures.getFileName())){
                try {
                    storageService.renameUserMusicFile(userId, mp3FileFeatures.getFileName(), updater.getFileName());
                    mp3FileFeatures.setFileName(updater.getFileName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return mp3Repo.save(mp3FileFeatures);
        }
        return null;
    }


    @DeleteMapping("/userMp3s/removeMp3Global/{userId}/{musicId}")
    public void removeMp3FromUserAccount(@PathVariable("userId") String userId, @PathVariable("musicId") String musicId){
        MP3FileFeatures mp3FileFeatures = mp3Repo.findById(musicId).get();
        mp3FileFeatures.getPlaylists().forEach(playlistId ->
        {
            Playlist playlist = playlistRepo.findById(playlistId).get();
            playlist.getMp3FileFeaturesList().remove(musicId);
            playlistRepo.save(playlist);
        });
        storageService.deleteUserFile(userId, mp3FileFeatures.getFileName());
        mp3Repo.delete(mp3FileFeatures);
    }

    @PutMapping("/userMp3s/addTag/{userId}/{musicId}/{tagName}")
    public MP3FileFeatures addTagToMp3ById(@PathVariable("userId") String userId, @PathVariable("musicId") String musicId, @PathVariable("tagName") String tagName){
        if(playlistRepo.findPlaylistsByPlaylistNameAndUserId("global", userId).get(0).getMp3FileFeaturesList().contains(musicId)){
            MP3FileFeatures mp3FileFeatures = mp3Repo.findById(musicId).get();
            if(!mp3FileFeatures.getTags().contains(tagName) && !tagName.isEmpty()) mp3FileFeatures.getTags().add(tagName);
            return mp3Repo.save(mp3FileFeatures);
        }
        return null;
    }

    @GetMapping("/download/{userid}/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable("userid") String userId, @PathVariable("filename") String filename) {
        Resource file = storageService.loadAsResource(userId + "//" + filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/up-mp3/{userId}")
    public MP3FileFeatures handleFileUploadRest(@PathVariable("userId") String userId, @RequestParam("file") MultipartFile file){

        MP3FileFeatures mp3FileFeatures = new MP3FileFeatures();
        if(!possibleExts.contains(getExtension(file.getOriginalFilename())) || !userRepo.existsById(userId)){
            return null;
        }

        storageService.storeInDir(userId + "//", file);

        mp3FileFeatures.setFileName(file.getOriginalFilename());
        MP3FileFeatures savedMp3FileFeatures = mp3Repo.save(mp3FileFeatures);

        Playlist playlist = playlistRepo.findPlaylistsByPlaylistNameAndUserId("global", userId).get(0);
        playlist.getMp3FileFeaturesList().add(savedMp3FileFeatures.getMp3Id());
        playlistRepo.save(playlist);

        savedMp3FileFeatures.getPlaylists().add(playlist.getId());
        return mp3Repo.save(savedMp3FileFeatures);

    }

    @PutMapping("/userMp3s/addBandInfo/{userId}/{musicId}")
    public MP3FileFeatures updateArtistsInfo(@PathVariable("userId") String userId, @PathVariable("musicId") String musicId, @RequestBody ArtistsInfo updater){
        if(playlistRepo.findPlaylistsByPlaylistNameAndUserId("global", userId).get(0).getMp3FileFeaturesList().contains(musicId)){
            MP3FileFeatures mp3FileFeatures = mp3Repo.findById(musicId).get();
            if(updater.getBandName() != null) mp3FileFeatures.getArtistsInfo().setBandName(updater.getBandName());
            if(updater.getArtistsNames() != null){
                updater.getArtistsNames().forEach(artistName -> mp3FileFeatures.getArtistsInfo().addArtistName(artistName));
            }
            if(updater.getBandAliases() != null){
                updater.getBandAliases().forEach(bandAlias -> mp3FileFeatures.getArtistsInfo().getBandAliases().add(bandAlias));
            }
            if(updater.getEventsInfo() != null){
                updater.getEventsInfo().forEach(eventsInfo -> mp3FileFeatures.getArtistsInfo().getEventsInfo().add(eventsInfo));
            }
            if(updater.getToursInfo() != null){
                updater.getToursInfo().forEach(tourInfo -> mp3FileFeatures.getArtistsInfo().getToursInfo().add(tourInfo));
            }
            return mp3Repo.save(mp3FileFeatures);
        }
        return null;
    }

    private String getExtension(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i);
        }
        return extension.toLowerCase();
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }



}