package edu.vedoque.seguridadbase.service.impl;

import edu.vedoque.seguridadbase.service.FileProcessingService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileProcessingServiceImpl implements FileProcessingService {

    // 1. Usamos Path en lugar de String.
    // "uploads" buscará la carpeta en la raíz de tu proyecto (junto al pom.xml)
    private final Path rootLocation = Paths.get("uploads");

    @Override
    public List<String> fileList() {
        File dir = rootLocation.toFile();
        File[] files = dir.listFiles();
        return files != null ? Arrays.stream(files).map(File::getName).collect(Collectors.toList()) : null;
    }

    @Override
    public String uploadFile(MultipartFile multipartFile, String fileName) {
        try {
            // 2. IMPORTANTE: Crea la carpeta si no existe.
            // Esto evita el error si tu compañero descarga el proyecto limpio.
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            // 3. IMPORTANTE: .resolve() pone la barra '/' automáticamente.
            // uploads + perro.jpg -> uploads/perro.jpg
            Path destinationFile = rootLocation.resolve(fileName);

            // Copiamos el archivo
            Files.copy(multipartFile.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            return "CREATED";

        } catch (IOException e) {
            e.printStackTrace();
            return "FAILED";
        }
    }

    @Override
    public Resource downloadFile(String fileName) {
        try {
            Path file = rootLocation.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}