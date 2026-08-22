package poo.uniquindio.edu.co.Homa.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import poo.uniquindio.edu.co.Homa.exception.BusinessException;
import poo.uniquindio.edu.co.Homa.service.ImageStorageService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryImageStorageService implements ImageStorageService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:alojamientos}")
    private String defaultFolder;

    @Override
    public UploadResult subirImagen(MultipartFile archivo) {
        validarArchivo(archivo);

        try {
            Map<String, Object> options = new HashMap<>();
            options.put("folder", defaultFolder);
            options.put("overwrite", false);
            options.put("resource_type", "image");

            Map<?, ?> uploadResult = cloudinary.uploader().upload(archivo.getBytes(), options);

            String url = Objects.toString(uploadResult.get("secure_url"), null);
            String publicId = Objects.toString(uploadResult.get("public_id"), null);

            if (url == null || publicId == null) {
                throw new BusinessException("La respuesta de Cloudinary no contiene los datos esperados");
            }

            return new UploadResult(url, publicId);
        } catch (IOException ex) {
            log.error("Error subiendo imagen a Cloudinary: {}", ex.getMessage());
            throw new BusinessException("No fue posible subir la imagen, inténtalo de nuevo");
        }
    }

    @Override
    public List<UploadResult> subirImagenes(List<MultipartFile> archivos) {
        if (archivos == null || archivos.isEmpty()) {
            throw new BusinessException("Debes adjuntar al menos una imagen");
        }

        List<UploadResult> resultados = new ArrayList<>();
        for (MultipartFile archivo : archivos) {
            resultados.add(subirImagen(archivo));
        }
        return resultados;
    }

    @Override
    public void eliminarImagen(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            throw new BusinessException("El identificador de la imagen es obligatorio");
        }

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException ex) {
            log.error("Error eliminando imagen en Cloudinary: {}", ex.getMessage());
            throw new BusinessException("No fue posible eliminar la imagen, inténtalo de nuevo");
        }
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessException("El archivo de imagen no puede estar vacío");
        }

        if (archivo.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("Cada imagen debe pesar máximo 5MB");
        }
    }
}
