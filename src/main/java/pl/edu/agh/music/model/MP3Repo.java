package pl.edu.agh.music.model;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface MP3Repo extends MongoRepository<MP3FileFeatures, String> {}
