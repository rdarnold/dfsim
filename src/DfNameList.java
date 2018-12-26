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

public class DfNameList extends ArrayList<String> {
    
    public DfNameList() { 
        super();
    }
    
    public String name;

    public void load(String fileName) {
        InputStream stream = DfNameList.class.getResourceAsStream(fileName);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                // the .trim() removes any spaces at the end
                add(line.trim());
                //Utils.log(line);
                //sb.append(line);
                //sb.append(System.lineSeparator());
                line = br.readLine();
            }
            //String everything = sb.toString();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

    // So this will either grab one of the names from its list,
    // or it can generate a new name if we want it to.
    public String getRandomName() {
        return (get(Utils.number(0, size()-1)));
    }
}