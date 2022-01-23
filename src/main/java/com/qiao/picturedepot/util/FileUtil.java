package com.qiao.picturedepot.util;

import java.io.*;

public class FileUtil {
    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        final int BUFFER_SIZE = 10240;
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

        int len = 0;
        while((len = bufferedInputStream.read(buffer, 0, BUFFER_SIZE)) > 0){
            bufferedOutputStream.write(buffer, 0, len);
        }
    }
}
