package pl.edu.agh.music;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import pl.edu.agh.music.filestorage.StorageProperties;
import pl.edu.agh.music.filestorage.StorageService;
import pl.edu.agh.music.model.MP3Repo;
import pl.edu.agh.music.model.PlaylistRepo;
import pl.edu.agh.music.model.UserRepo;

import java.util.Arrays;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class MusicApplication {

    public static String DEFAULT_ICON_URL = "http://uncyclopedia.wikia.com/wiki/Question_mark";

    public static void main(String[] args) {
        SpringApplication.run(MusicApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

        };
    }


    @Bean
    CommandLineRunner init(StorageService storageService, UserRepo userRepo, MP3Repo mp3Repo, PlaylistRepo playlistRepo) {
        return (args) -> {
//            userRepo.deleteAll();
//            mp3Repo.deleteAll();
//            playlistRepo.deleteAll();
//            storageService.deleteAll();
//            storageService.init();
        };
    }

}
