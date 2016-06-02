import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

public class DRTableReader extends DoseResponse {

    DRTable[] table;

    DRTableReader(DRTable[] table){
        this.table = table;
    }

    void fillTable(){
        String path = "./Resources/Dose Response/Information.xlsx";
        FileInputStream fis;
        try{
            int counter = 0;
            fis = new FileInputStream(path);
            Workbook workbook = new XSSFWorkbook(fis);
            int numberOfSheets = workbook.getNumberOfSheets();

            for (int i = 0; i < numberOfSheets; i++){
                Sheet sheet = workbook.getSheetAt(i);
                Iterator rowIterator = sheet.iterator();
                if (!rowIterator.hasNext()) {
                    continue;
                }

                //Skip first two rows.
                rowIterator.next();
                rowIterator.next();

                while (rowIterator.hasNext()){
                    Row row = (Row) rowIterator.next();
                    Iterator cellIterator = row.cellIterator();

                    fillRow(cellIterator, counter);
                    counter++;
                }
            }
        } catch (FileNotFoundException e) {e.printStackTrace();}
          catch (IOException e) {e.printStackTrace();}
    }

    void fillRow(Iterator cellIterator, int counter){
        table[counter].setSet(((Cell) cellIterator.next()).getStringCellValue());

        String check = ((Cell) cellIterator.next()).getStringCellValue();
        if (check.equals("-")) {table[counter].setConcentration(null);}
        else {table[counter].setConcentration(Double.parseDouble(check));}

        table[counter].setPicture(((Cell) cellIterator.next()).getStringCellValue());

        check = ((Cell) cellIterator.next()).getStringCellValue();
        if (check.equals("Normal")) {table[counter].setHeartrate(DRTable.Heartrate.NORMAL);}
        else if (check.equals("No")) {table[counter].setHeartrate(DRTable.Heartrate.NONE);}
        else if (check.equals("Lower")) {table[counter].setHeartrate(DRTable.Heartrate.AFFECTED);}

        check = ((Cell) cellIterator.next()).getStringCellValue();
        if (check.equals("Normal")) {table[counter].setMovement(DRTable.Movement.NORMAL);}
        else if (check.equals("No")) {table[counter].setMovement(DRTable.Movement.NONE);}
        else if (check.equals("Lower")) {table[counter].setMovement(DRTable.Movement.AFFECTED);}

        int effect = 0;
        while (effect < 7) {
            double value = ((Cell) cellIterator.next()).getNumericCellValue();
            if (value == 1.0) {table[counter].getEffect(effect).setStatus(true);}
            else {table[counter].getEffect(effect).setStatus(false);}
            effect++;
        }

        check = ((Cell) cellIterator.next()).getStringCellValue();
        if (check.equals("Ok")) {table[counter].setResult(DRTable.Result.ALIVE);}
        else if (check.equals("Lethal")) {table[counter].setResult(DRTable.Result.DEAD);}
        else if (check.equals("Non-lethal")) {table[counter].setResult(DRTable.Result.AFFECTED);}
    }
}
