package com.PIMCS.PIMCS.config;

import com.PIMCS.PIMCS.Interface.FileStorage;
import com.PIMCS.PIMCS.service.FileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {

    @Bean(name = "fileStorage")
    public FileStorage fileService (){
        return new FileService();
    }

}
