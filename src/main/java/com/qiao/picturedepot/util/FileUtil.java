package com.qiao.picturedepot.util;

import io.lettuce.core.StrAlgoArgs;
import jdk.internal.util.xml.impl.Input;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Optional;

public class FileUtil {
    private FileUtil(){
    }

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

    /**
     * 每个线程两个buffer，用于文件比较
     */
    private final static int bufferSize = 10240;
    private final static ThreadLocal<ByteBuffer> localBuffer1 = new ThreadLocal<>();
    private final static ThreadLocal<ByteBuffer> localBuffer2 = new ThreadLocal<>();

    public static boolean compareFile(File file1, File file2) throws IOException {
        if(file1.equals(file2)) return true;

        ByteBuffer byteBuffer1 = localBuffer1.get();
        if(byteBuffer1 == null){
            byteBuffer1 = ByteBuffer.allocateDirect(bufferSize);
            localBuffer1.set(byteBuffer1);
        }
        ByteBuffer byteBuffer2 = localBuffer2.get();
        if(byteBuffer2 == null){
            byteBuffer2 = ByteBuffer.allocateDirect(bufferSize);
            localBuffer2.set(byteBuffer2);
        }

        //读取并比较文件内容
        try(FileInputStream inputStream1 = new FileInputStream(file1);
            FileInputStream inputStream2 = new FileInputStream(file2)){
            try(FileChannel channel1 = inputStream1.getChannel();
                FileChannel channel2 = inputStream2.getChannel();){
                if(channel1.size() != channel1.size()) return false;

                byteBuffer1.clear();
                byteBuffer2.clear();
                while(channel1.read(byteBuffer1) != -1 && channel2.read(byteBuffer2) != -1){
                    byteBuffer1.flip();
                    byteBuffer2.flip();
                    if(byteBuffer1.compareTo(byteBuffer2) != 0) return false;
                    byteBuffer1.clear();
                    byteBuffer2.clear();
                }
            }
        }

        return true;
    }
}
