package bsa.java.concurrency.image;

import bsa.java.concurrency.fs.FileSystem;
import bsa.java.concurrency.fs.FileService;
import bsa.java.concurrency.image.dto.SearchResultDTO;
import bsa.java.concurrency.image.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author professorik
 * @created 28/06/2021 - 11:35
 * @project concurrency
 */
@Service
public class ImageService {
    private ImageRepository imageRepository;
    private FileSystem fileSystem;

    @Autowired
    public ImageService(ImageRepository imageRepository, FileSystem fileSystem) {
        this.imageRepository = imageRepository;
        this.fileSystem = fileSystem;
    }

    public byte[] getById(UUID imageId) {
        String url = imageRepository.findOneById(imageId).getUrl();
        try {
            return Files.readAllBytes(Paths.get(url));
        } catch (IOException e) {
            return null;
        }
    }

    public CompletableFuture<Void> saveImages(MultipartFile[] files) {
        var promises = Arrays.stream(files)
                .parallel()
                .map(file -> {
                    var future = fileSystem.saveImage(file);
                    var hash = DHasher.calculateHash(FileService.getBytes(file));
                    future.thenAccept(result -> imageRepository.save(new Image(UUID.randomUUID(), result, hash)));
                    return future;
                }).toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(promises);
    }

    public List<SearchResultDTO> searchFile(MultipartFile file, double threshold) {
        var hash = DHasher.calculateHash(FileService.getBytes(file));
        var responseFromDb = imageRepository.getSearchResult(hash, threshold);

        if(responseFromDb.size() == 0) {
            CompletableFuture.supplyAsync(() -> {
                var future = fileSystem.saveImage(file);
                future.thenAccept(result -> imageRepository.save(new Image(UUID.randomUUID(), result, hash)));
                return future;
            });
        }
        return responseFromDb;
    }

    public void purgeFiles() {
        fileSystem.deleteAllFiles();
        imageRepository.deleteAll();
    }

    public void deleteById(UUID imageId) {
        fileSystem.deleteFileById(imageId);
        imageRepository.deleteById(imageId);
    }
}
