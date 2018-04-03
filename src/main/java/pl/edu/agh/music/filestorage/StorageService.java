package pl.edu.agh.music.filestorage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();
    void createUserDir(String dir);
    void renameUserMusicFile(String userDir, String oldName, String newName) throws IOException;
    void deleteUserDir(String userDir);
    void deleteUserFile(String userDir, String userFile);

    void storeInDir(String dir, MultipartFile file);
    void store(MultipartFile file);

    Stream<Path> loadAllFromDir(String dir);
    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

}
