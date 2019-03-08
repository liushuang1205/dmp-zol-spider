package com.sndo.dmp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * 
 * @author yangqi   
 * @date 2015-10-9 上午11:04:14    
 * zlib辅助库
 */
public class Zlib {

    public static byte[] compress(byte[] input) {
        Deflater compresser = new Deflater();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            compresser.reset();
            compresser.setInput(input);
            compresser.finish();

            byte[] buffer = new byte[4096];
            while (!compresser.finished()) {
                int count = compresser.deflate(buffer);
                byteArrayOutputStream.write(buffer, 0, count);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            compresser.end();
        }
    }

    public static byte[] decompress(byte[] input) {
        Inflater decompresser = new Inflater();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            decompresser.reset();
            decompresser.setInput(input);

            byte[] buffer = new byte[4096];
            while (!decompresser.finished()) {
                int count = decompresser.inflate(buffer);
                byteArrayOutputStream.write(buffer, 0, count);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (decompresser != null) {
                decompresser.end();
            }
        }
    }

}
