package com.sndo.dmp.load;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GetFileName {

    private static String logoPath = "G:\\logo";
    private static String screenshotPath = "G:\\screenshot";

    private static File logoFilePath = new File(logoPath);
    private static File screenshotFilePath = new File(screenshotPath);

    static List<String> logoFile = new ArrayList<>();
    static List<String> screenshotFile = new ArrayList<>();

    public void getFileName(File file){
        if(file.getPath().equals(logoPath)){
            common(file, logoFile);
        } else {
            common(file, screenshotFile);
        }
    }

    private void common(File file, List<String> logoFile) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                logoFile.add(f.getName());
            }
        }
    }

    public void conparTo(List<String> logoFile, List<String> screenshotFile){
        for(int i = 0; i < logoFile.size(); i++){
            System.out.println(logoFile.get(i));
            for(int j = 0; j < screenshotFile.size(); j++){
                if(logoFile.get(i).equals(screenshotFile.get(j))){
                    System.out.println(screenshotFile.get(j));
                    System.out.println();
                }
            }
        }
    }

    public static void main(String[] args){
        GetFileName fileName = new GetFileName();
        fileName.getFileName(logoFilePath);
        fileName.getFileName(screenshotFilePath);
        fileName.conparTo(logoFile,screenshotFile);
    }
}
