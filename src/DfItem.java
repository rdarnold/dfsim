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

public class DfItem extends DfEqItem {

    public static final DfItemVal[] vals = DfItemVal.values();
    
    public enum DfItemVal {
        Name,
        Cost,
        Spell,
        Str,
        Agi,
        Dex,
        Sta,
        Kno,
        Mag,
        Luk
    }


    public DfItem() { 
    }

    public DfItem dupe() {
        DfItem item = new DfItem();
        copyTo(item);
        return item;
    }

    public void copyTo(DfItem item) {
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
            int r = ExcelUtils.getRowForString("Item", wb, sheet);
            r++; // And move down one row so we are at the actual data

            // Do not restart r, we are right where we wanna be.
            for (; r <= numRows; r++) {
                row = sheet.getRow(r);
                if (row == null) 
                    continue;
                DfItem item = new DfItem();
                Data.dfItemList.add(item);
                for (int c = 1; c < numCols; c++) {
                    cell = row.getCell(c);
                    if (ExcelUtils.isCellEmpty(cell)) {
                        if (c == 1) { 
                            // No name, so no mon, remove it and move on
                            Data.dfItemList.remove(item);
                            break;
                        }
                        continue;
                    }
                    DfItem.DfItemVal enumValue = DfItem.vals[c-1];
                    switch (enumValue) {
                        case Name:
                            item.name = cell.getStringCellValue();
                            break;
                    }
                    /*if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                        Utils.log(cell.getNumericCellValue()));
                    } 
                    else  {
                        Utils.log(cell.getStringCellValue());
                    }*/
                } 
            }

            /*for (DfEquipW dw : Data.dfEquipWList) {
                Utils.log(dw.toString());
            }*/

        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }
}