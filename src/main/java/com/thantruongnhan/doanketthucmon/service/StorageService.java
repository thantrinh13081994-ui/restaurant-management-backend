package com.thantruongnhan.doanketthucmon.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String saveImage(MultipartFile file);
}
