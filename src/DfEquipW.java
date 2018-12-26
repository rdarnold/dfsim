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

public class DfEquipW extends DfEqItem {
    
    public static final DfEquipWVal[] vals = DfEquipWVal.values();

    public enum DfEquipWVal {
        Name,
        Cost,
        Atk,
        Acc,
        Litdmg,
        Firdmg,
        Icedmg,
        Magdmg,
        Heal,
        Str,
        Agi,
        Dex,
        Sta,
        Kno,
        Mag,
        Luk
    }
    
    public DfEquipW() { }

    public DfEquipW dupe() {
        DfEquipW item = new DfEquipW();
        copyTo(item);
        return item;
    }

    public void copyTo(DfEquipW item) {
        super.copyTo(item);
    }

    public String toString() {
        String strRetv = name + ", ";

        if (cost > 0) strRetv += "cost: " + cost + " ";
        if (atk > 0) strRetv += "atk: " + atk + " ";
        if (acc > 0) strRetv += "acc: " + acc + " ";
        if (litdmg > 0) strRetv += "litdmg: " + litdmg + " ";
        if (firdmg > 0) strRetv += "firdmg: " + firdmg + " ";
        if (icedmg > 0) strRetv += "icedmg: " + icedmg + " ";
        if (magdmg > 0) strRetv += "magdmg: " + magdmg + " ";
        if (heal > 0) strRetv += "heal: " + heal + " ";
        if (str > 0) strRetv += "str: " + str + " ";
        if (agi > 0) strRetv += "agi: " + agi + " ";
        if (dex > 0) strRetv += "dex: " + dex + " ";
        if (sta > 0) strRetv += "sta: " + sta + " ";
        if (kno > 0) strRetv += "kno: " + kno + " ";
        if (mag > 0) strRetv += "mag: " + mag + " ";
        if (luk > 0) strRetv += "luk: " + luk + " ";

        return strRetv;
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
            int r = ExcelUtils.getRowForString("Weapon", wb, sheet);
            r++; // And move down one row so we are at the actual data

            // Do not restart r, we are right where we wanna be.
            for (; r <= numRows; r++) {
                row = sheet.getRow(r);
                if (row == null) 
                    continue;
                DfEquipW item = new DfEquipW();
                Data.dfEquipWList.add(item);
                for (int c = 1; c < numCols; c++) {
                    cell = row.getCell(c);
                    if (ExcelUtils.isCellEmpty(cell)) {
                        if (c == 1) { 
                            // No name, so no mon, remove it and move on
                            Data.dfEquipWList.remove(item);
                            break;
                        }
                        continue;
                    }
                    DfEquipW.DfEquipWVal enumValue = DfEquipW.vals[c-1];
                    switch (enumValue) {
                        case Name:
                            item.name = cell.getStringCellValue();
                            break;
                        case Cost:
                            item.cost = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Atk:
                            item.atk = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Acc:
                            item.acc = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Litdmg:
                            item.litdmg = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Firdmg:
                            item.firdmg = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Icedmg:
                            item.icedmg = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Magdmg:
                            item.magdmg = ExcelUtils.getNumberForCell(cell);
                            break;
                        case Heal:
                            item.heal = ExcelUtils.getNumberForCell(cell);
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