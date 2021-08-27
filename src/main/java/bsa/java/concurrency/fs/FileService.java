package bsa.java.concurrency.fs;

import bsa.java.concurrency.image.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author professorik
 * @created 28/06/2021 - 11:13
 * @project concurrency
 */
@Service
public class FileService implements FileSystem{

    private static final String PATH = "D:\\IdeaProjects\\bsa-java-concurrency-template\\src\\main\\java\\bsa\\java\\concurrency\\imgBase\\";
    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private ImageRepository imageRepository;

    @Autowired
    public FileService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public CompletableFuture<String> saveImage(MultipartFile file) {
        return CompletableFuture.supplyAsync(() -> saveFile(file), threadPool);
    }

    @Override
    public void deleteAllFiles() {
        File directory = new File(FileService.getPath());
        File[] files = directory.listFiles();

        for(File file : files) {
            if(!file.delete()) {
                System.err.println("Impossible to delete: " + file.getAbsolutePath());
            }
        }
    }

    @Override
    public void deleteFileById(UUID id) {
        var image = imageRepository.findOneById(id);
        var file = new File(image.getUrl());
        if(!file.delete()) {
            System.err.println("It can't be possible to delete: " + file.getAbsolutePath() + "/" + image.getUrl());
        }
    }

    private String saveFile(MultipartFile file) {
        String filePath = null;
        Path savePath = Paths.get(getPath());
        try (var out = new BufferedOutputStream(Files.newOutputStream(savePath.resolve(file.getOriginalFilename())))) {
            if(!Files.exists(savePath)) {
                Files.createDirectories(savePath);
            }
            out.write(file.getBytes());
            filePath = getPath(file.getOriginalFilename());
            out.flush();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        System.out.println(filePath);
        return filePath;
    }

    private static String getPath(String... args) {
        StringBuilder builder = new StringBuilder(PATH);
        for (String i : args) {
            builder.append(i);
        }
        return builder.toString();
    }

    public static byte[] getBytes(MultipartFile file) {
        byte[] result = null;
        try {
            result = file.getBytes();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
