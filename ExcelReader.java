import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ExcelReader {

    ExcelReader(String path){
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(path);
            Workbook workbook = new XSSFWorkbook(fis);
            int numberOfSheets = workbook.getNumberOfSheets();

            for (int i = 0; i < numberOfSheets; i++){
                Sheet sheet = workbook.getSheetAt(i);
                Iterator rowIterator = sheet.iterator();
            }

        } catch (FileNotFoundException e) {e.printStackTrace();}
          catch (IOException e) {e.printStackTrace();}
    }
}
