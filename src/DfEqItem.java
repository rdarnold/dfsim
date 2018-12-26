package dfsim;

import java.util.ArrayList;

import dfsim.*;

// Doesnt have much, just to give all the eqs a common base.
public class DfEqItem {

    DfEqItem() {

    }

    public String name = "";
    public double cost;
    public double atk;
    public double acc;
    public double litdmg;
    public double firdmg;
    public double icedmg;
    public double magdmg;
    public double heal;
    public double def;
    public double str;
    public double agi;
    public double dex;
    public double sta;
    public double kno;
    public double mag;
    public double luk;

    public String getName() { return name; }
    public double getCost() { return cost; }
    public double getAtk() { return atk; }
    public double getAcc() { return acc; }
    public double getLitDmg() { return litdmg; }
    public double getFirDmg() { return firdmg; }
    public double getIceDmg() { return icedmg; }
    public double getMagDmg() { return magdmg; }
    public double getHeal() { return heal; }
    public double getDef() { return def; }
    public double getStr() { return str; }
    public double getAgi() { return agi; }
    public double getDex() { return dex; }
    public double getSta() { return sta; }
    public double getKno() { return kno; }
    public double getMag() { return mag; }
    public double getLuk() { return luk; }

    public void copyTo(DfEqItem item) {
        item.name = this.name;
        item.cost = this.cost;
        item.atk = this.atk;
        item.acc = this.acc;
        item.litdmg = this.litdmg;
        item.firdmg = this.firdmg;
        item.icedmg = this.icedmg;
        item.magdmg = this.magdmg;
        item.heal = this.heal;
        item.def = this.def;
        item.str = this.str;
        item.agi = this.agi;
        item.dex = this.dex;
        item.sta = this.sta;
        item.kno = this.kno;
        item.luk = this.luk;
    }
    
    public DfEqItem dupe() {
        DfEqItem item = new DfEqItem();
        copyTo(item);
        return item;
    }
}