package com.PIMCS.PIMCS.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;
//@PropertySource("classpath:application.properties")
public class FileUtils {

    public static String uploadFile(MultipartFile multipartFile) throws Exception{
        final String UPLOAD_PATH ="/Users/gamdodo/Documents/java_workspace/media";

        UUID uuid = UUID.randomUUID();
        String savedName = uuid.toString()+"_"+multipartFile.getOriginalFilename();
        File target = new File(UPLOAD_PATH, savedName);
        multipartFile.transferTo(target);
        
        return savedName;
    }
}
