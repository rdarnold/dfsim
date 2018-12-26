package dfsim;

import java.util.List;
import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;

import java.io.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class DfEquipC extends DfEqItem {
   
    public static final DfEquipCVal[] vals = DfEquipCVal.values();

    public enum DfEquipCVal {
        Name,
        Cost,
        Str,
        Agi,
        Dex,
        Sta,
        Kno,
        Mag,
        Luk
    }

    public DfEquipC() { 
    }
    
    public DfEquipC dupe() {
        DfEquipC item = new DfEquipC();
        copyTo(item);
        return item;
    }

    public void copyTo(DfEquipC item) {
        super.copyTo(item);
    }

    public static void load(String fileName) {
        try {
            InputStream stream = ExcelUtils.class.getResourceAsStream(fileName);
            Workbook wb = new XSSFWorkbook(stream);
            Sheet sheet = wb.getSheetAt(0);
            Row row;
            Cell cell;

            int numRows = sheet.getPhysicalNumberOfRows();
            int numCols = ExcelUtils.getNumberOfColumns(wb, sheet);

            // So first iterate down to our first actual entry.
            int r = ExcelUtils.getRowForString("Crystal", wb, sheet);
            r++; // And move down one row so we are at the actual data

            //Utils.log("Found it, it's row " + r);
            // Do not restart r, we are right where we wanna be.
            for (; r <= numRows; r++) {
                row = sheet.getRow(r);
                if (row == null) 
                    continue;
                DfEquipC item = new DfEquipC();
                Data.dfEquipCList.add(item);
                for (int c = 1; c < numCols; c++) {
                    cell = row.getCell(c);
                    if (ExcelUtils.isCellEmpty(cell)) {
                        if (c == 1) { 
                            // No name, so no mon, remove it and move on
                            Data.dfEquipCList.remove(item);
                            break;
                        }
                        continue;
                    }
                    DfEquipC.DfEquipCVal enumValue = DfEquipC.vals[c-1];
                    switch (enumValue) {
                        case Name:
                            item.name = cell.getStringCellValue();
                            break;
                        case Cost:
                            item.cost = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Str:
                            item.str = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Agi:
                            item.agi = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Dex:
                            item.dex = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Sta:
                            item.sta = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Kno:
                            item.kno = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Mag:
                            item.mag = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Luk:
                            item.luk = ExcelUtils.getNumberForCell(cell);
                            break;
                    }
                } 
            }

            /*for (DfMon dw : Data.dfMonList) {
                Utils.log(dw.toString());
            }*/
            
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }
}