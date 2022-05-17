package com.qiao.picturedepot.util;

import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FileUtil {
    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        FileCopyUtils.copy(inputStream, outputStream);
    }

    public static void save(byte[] data, OutputStream outputStream) throws IOException {
        FileCopyUtils.copy(data, outputStream);
    }

    public static String getNameSuffix(String filename){
        if(filename == null) return null;
        int p = filename.lastIndexOf('.');
        return filename.substring(p+1);
    }

    public static Set<String> pictureFormats = new HashSet<>();
    static{
        String[] formats = {"png", "jpg", "bmp", "webp", "ico", "gif", "tif", "tga"};
        pictureFormats.addAll(Arrays.asList(formats));
    }

    public static boolean isPictureFile(String suffix){
        if(suffix == null) return false;
        return pictureFormats.contains(suffix.toLowerCase());
    }
}
