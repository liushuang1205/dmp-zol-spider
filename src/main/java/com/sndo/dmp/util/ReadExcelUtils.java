package com.sndo.dmp.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReadExcelUtils {

    /**
     * @Description: 处理Xlsx文件
     * @param @param path
     * @return List<List<String>>  返回类型
     */
    public static List<List<String>> readXlsx(String path) throws Exception {
        InputStream is = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        List<List<String>> result = new ArrayList<>();
        // 循环每一页，并处理当前循环页
        for (Sheet xssfSheet : xssfWorkbook) {
            if (xssfSheet == null) {
                continue;
            }
            // 处理当前页，循环读取每一行
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                Row xssfRow = xssfSheet.getRow(rowNum);
                int minColIx = xssfRow.getFirstCellNum();
                int maxColIx = xssfRow.getLastCellNum();
                List<String> rowList = new ArrayList<>();
                for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                    Cell cell = xssfRow.getCell(colIx);
                    if (cell == null) {
                        continue;
                    }
                    rowList.add(cell.toString());
                }
                result.add(rowList);
            }
        }
        return result;
    }

    public static void main(String[] args){
        ReadExcelUtils excel = new ReadExcelUtils();
        try {
            List<List<String>> list =  excel.readXlsx("C:\\Users\\liushuang\\Desktop\\游戏资料.xlsx");
            System.out.println(list.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
