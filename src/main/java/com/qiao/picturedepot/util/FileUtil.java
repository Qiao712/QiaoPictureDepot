package com.qiao.picturedepot.util;

import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

public class FileUtil {
    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        FileCopyUtils.copy(inputStream, outputStream);
    }

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        return FileCopyUtils.copyToByteArray(inputStream);
    }

    public static String getNameSuffix(String filename){
        if(filename == null) return null;
        int p = filename.lastIndexOf('.');
        return filename.substring(p+1);
    }

    public static boolean isPictureFile(String pictureFormat){
        if(pictureFormat == null) return false;
        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType("." + pictureFormat);
        return mediaType.isPresent() && mediaType.get().getType().equals("image");
    }

    public static String getContentType(String pictureFormat){
        Optional<MediaType> mediaType = MediaTypeFactory.getMediaType("." + pictureFormat);
        if(mediaType.isPresent()){
            return mediaType.get().toString();
        }else{
            return null;
        }
    }

    public static byte[] md5Digest(byte[] data){
        return DigestUtils.md5Digest(data);
    }
}
