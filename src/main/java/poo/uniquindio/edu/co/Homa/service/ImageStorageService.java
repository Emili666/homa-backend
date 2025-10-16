package poo.uniquindio.edu.co.homa.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    UploadResult subirImagen(MultipartFile archivo);

    List<UploadResult> subirImagenes(List<MultipartFile> archivos);

    void eliminarImagen(String publicId);

    record UploadResult(String url, String publicId) {
    }
}
