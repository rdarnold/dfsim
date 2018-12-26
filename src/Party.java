package dfsim;

import java.util.ArrayList;

public class Party extends ArrayList<Person> {
    public ArrayList<DfEqItem> inv;
    
    private Person leader = null;
    public Person getLeader() { return leader; }
    public void setLeader(Person newLeader) { leader = newLeader; }
    public void setLeader() {
        leader = get(0);
    }

    Party() {
        inv = new ArrayList<DfEqItem>();
    }

    public void addToInv(DfEqItem item) {
        inv.add(item);
    }

}