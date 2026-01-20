package edu.vedoque.seguridadbase.controller;

import edu.vedoque.seguridadbase.service.FileProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/file")
public class FileProcessingController {

    @Autowired
    private FileProcessingService fileProcessingService;

    // Ruta para servir las imágenes: /file/download/nombre-imagen.jpg
    @GetMapping("/download/{name}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable(value="name") String fileName){

        Resource file = fileProcessingService.downloadFile(fileName);

        if (file == null || !file.exists()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .body(file);
    }
}