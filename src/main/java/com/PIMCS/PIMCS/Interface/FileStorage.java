package com.PIMCS.PIMCS.Interface;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorage {

    String save(MultipartFile multipartFile) throws Exception;
    byte[] read(String fileName) throws IOException;

}
