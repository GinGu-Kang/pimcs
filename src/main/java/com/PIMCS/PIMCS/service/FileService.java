package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Interface.FileStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileService implements FileStorage {
    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public String save(MultipartFile multipartFile) throws Exception {
        UUID uuid = UUID.randomUUID();
        String savedName = uuid.toString()+"_"+multipartFile.getOriginalFilename();
        File target = new File(uploadPath, savedName);
        multipartFile.transferTo(target);
        return savedName;
    }

    @Override
    public byte[] read(String fileName) throws IOException {
        File file = new File(uploadPath, fileName);
        InputStream imageStream = new FileInputStream(file);
        byte[] imageByteArray = new byte[0];
        imageByteArray = imageStream.readAllBytes();
        imageStream.close();
        return imageByteArray;
    }
}
