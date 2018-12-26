package dfsim;

import java.util.ArrayList;
import java.util.EnumSet;


// Basically a holder of static data that should be accessible by everyone.
public final class Data {
    private Data () { // private constructor
    }

    // I'll use a public pool of money for now
    public static int money = 0;

    public static DfNameList nameListM;
    public static DfNameList nameListF;

    public static Party personList; //ArrayList<Person> personList;
    public static ArrayList<DfEqItem> dfEqItemMasterList;
    public static ArrayList<DfEquipA> dfEquipAList;
    public static ArrayList<DfEquipAcc> dfEquipAccList;
    public static ArrayList<DfEquipC> dfEquipCList;
    public static ArrayList<DfEquipH> dfEquipHList;
    public static ArrayList<DfEquipS> dfEquipSList;
    public static ArrayList<DfEquipW> dfEquipWList;
    public static ArrayList<DfItem> dfItemList;
    public static ArrayList<DfMon> dfMonList;

    //public static Values values;

    private static XmlProcessor xmlProcessor = new XmlProcessor();

    public static void load() {
        nameListM = new DfNameList();
        nameListF = new DfNameList();

        nameListM.load(Constants.RES_LOAD_PATH + "names_m.txt");
        nameListF.load(Constants.RES_LOAD_PATH + "names_f.txt");

        //values = new Values();
        personList = new Party();
        dfEqItemMasterList = new ArrayList<DfEqItem>(); // This contains everything
        dfEquipAList = new ArrayList<DfEquipA>();
        dfEquipAccList = new ArrayList<DfEquipAcc>();
        dfEquipCList = new ArrayList<DfEquipC>();
        dfEquipHList = new ArrayList<DfEquipH>();
        dfEquipSList = new ArrayList<DfEquipS>();
        dfEquipWList = new ArrayList<DfEquipW>();
        dfItemList = new ArrayList<DfItem>();
        dfMonList = new ArrayList<DfMon>();
        
        //values = xmlProcessor.loadValues(Constants.RES_LOAD_PATH + "values.xml");
        //ExcelUtils.readExcelFile(Constants.RES_LOAD_PATH + "dfmon.xlsx");
        DfEquipA.load(Constants.RES_LOAD_PATH + "dfequipa.xlsx");
        DfEquipC.load(Constants.RES_LOAD_PATH + "dfequipc.xlsx");
        DfEquipAcc.load(Constants.RES_LOAD_PATH + "dfequipacc.xlsx");
        DfEquipH.load(Constants.RES_LOAD_PATH + "dfequiph.xlsx");
        DfEquipS.load(Constants.RES_LOAD_PATH + "dfequips.xlsx");
        DfEquipW.load(Constants.RES_LOAD_PATH + "dfequipw.xlsx");
        DfItem.load(Constants.RES_LOAD_PATH + "dfitem.xlsx");
        DfMon.load(Constants.RES_LOAD_PATH + "dfmon.xlsx");

        dfEqItemMasterList.addAll(dfEquipAList);
        dfEqItemMasterList.addAll(dfEquipAccList);
        dfEqItemMasterList.addAll(dfEquipCList);
        dfEqItemMasterList.addAll(dfEquipHList);
        dfEqItemMasterList.addAll(dfEquipSList);
        dfEqItemMasterList.addAll(dfEquipWList);
        dfEqItemMasterList.addAll(dfItemList);

        // This comes last as it's going to try to set people up with the right
        // equipment so the equipment must already be loaded.
        xmlProcessor.loadPersonListXML(Constants.RES_LOAD_PATH + "people.xml", personList);
        if (personList.size() <= 0) {
            throw new AssertionError("Person list failed to load.");
        }
        else {
            // For everyone we load from the list, set them as "met" because they are
            // party.
            for (Person person : personList) {
                person.setMet(true);
            }
            personList.setLeader();
        }
    }

    public static Person getPersonByName(String strName) {
        for (Person person : personList) {
            if (person.getName().equals(strName)) {
                return person;
            }
        }
        return null;
    }
    
    public static DfEquipW getDfEquipWByName(String strName) {
        for (DfEquipW item : dfEquipWList) {
            if (item.name.equals(strName)) {
                return item;
            }
        }
        return null;
    }

    public static DfEquipA getDfEquipAByName(String strName) {
        for (DfEquipA item : dfEquipAList) {
            if (item.name.equals(strName)) {
                return item;
            }
        }
        return null;
    }

    public static DfEquipAcc getDfEquipAccByName(String strName) {
        for (DfEquipAcc item : dfEquipAccList) {
            if (item.name.equals(strName)) {
                return item;
            }
        }
        return null;
    }
    
    public static DfEquipC getDfEquipCByName(String strName) {
        for (DfEquipC item : dfEquipCList) {
            if (item.name.equals(strName)) {
                return item;
            }
        }
        return null;
    }
    
    public static DfEquipH getDfEquipHByName(String strName) {
        for (DfEquipH item : dfEquipHList) {
            if (item.name.equals(strName)) {
                return item;
            }
        }
        return null;
    }

    public static DfEquipS getDfEquipSByName(String strName) {
        for (DfEquipS item : dfEquipSList) {
            if (item.name.equals(strName)) {
                return item;
            }
        }
        return null;
    }

    public static DfItem getDfItemByName(String strName) {
        for (DfItem item : dfItemList) {
            if (item.name.equals(strName)) {
                return item;
            }
        }
        return null;
    }

    public static DfMon getDfMonByName(String strName) {
        for (DfMon mon : dfMonList) {
            if (mon.getName().equals(strName)) {
                return mon;
            }
        }
        return null;
    }
    
    public static DfEqItem generateRandomEqItem() {
        // So it could be anything, anything at all.
        int num = Utils.number(0, dfEqItemMasterList.size()-1);
        DfEqItem item = dfEqItemMasterList.get(num);

        // It's a generate, so we are actually allocating memory here.
        return item.dupe();
    }
}