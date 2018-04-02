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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.edu.agh.music.filestorage.StorageFileNotFoundException;
import pl.edu.agh.music.filestorage.StorageService;


@Controller
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

    @GetMapping("files")
    @ResponseBody
    public List<String> listUploadedFilesRest(){
        return
                storageService
                        .loadAll()
                        .map(Path::toString)
                        .collect(Collectors.toList());
    }

    @GetMapping("userfiles/{userId}")
    @ResponseBody
    public List<String> listUserUploadedFiles(@PathVariable("userId") String userId){
        return storageService
                .loadAllFromDir(userId)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files",
                storageService
                        .loadAll()
                        .map(Path::toString)
                        .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }


    private String getExtension(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i);
        }
        return extension.toLowerCase();
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("filestorage") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        if(!possibleExts.contains(getExtension(file.getOriginalFilename()))){
            return "Fuck u";
        }
        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/";
    }

    @PostMapping("/up-mp3/{userId}")
    @ResponseBody
    public MP3FileFeatures handleFileUploadRest(@PathVariable("userId") String userId, @RequestParam("file") MultipartFile file){

        MP3FileFeatures mp3FileFeatures = new MP3FileFeatures();
        if(!possibleExts.contains(getExtension(file.getOriginalFilename())) || !userRepo.existsById(userId)){
            mp3FileFeatures.setFileName("Fuck u");
            return mp3FileFeatures;
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

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }



}