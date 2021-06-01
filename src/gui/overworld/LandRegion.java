package dfsim.gui;

import java.util.ArrayList;

import dfsim.*;
import dfsim.gui.*;

// A section of land with an associated level and perhaps other attributes
public class LandRegion extends ArrayList<LandMapTile> {
    
    private static int currentId = 0;

    private int m_nId = currentId;
    private int m_nLevel = 0;

    // This is for assigning levels in the beginning - some regions
    // are considered fixed and won't be "smoothed" out during the
    // smoothing process
    private boolean m_bFixedLevel = false;
    public boolean isFixedLevel() { return m_bFixedLevel; }
    public void makeFixedLevel() { m_bFixedLevel = true; }

    private ArrayList<LandRegion> m_alAdjacentRegions;
    public ArrayList<LandRegion> getAdjacentRegions() { return m_alAdjacentRegions; }

    public LandRegion() { 
        init(0);
    }

    public LandRegion(int lev) { 
        init(lev);
    }

    private void init(int lev) {
        m_nId = currentId;
        currentId++;
        setLevel(lev);
    }

    public boolean addTile(LandMapTile tile) {
        if (contains(tile) == true) {
            return false;
        }
        add(tile);
        tile.setRegion(this);
        return true;
    }

    public int getLevel() { return m_nLevel; }
    public int getId() { return m_nId; }
    public void setLevel(int lv) { 
        m_nLevel = lv; 
        for (LandMapTile tile : this) {
            tile.setLevel(lv);
        }
    }

    // Generate a list of all the adjacent regions for easy reference
    public void updateAdjacentRegions() {
        if (m_alAdjacentRegions == null){
            m_alAdjacentRegions = new ArrayList<LandRegion>();
        }
        m_alAdjacentRegions.clear();

        // So just look through at all the tiles, if it's an edge tile,
        // check the region of the adjacent tile and add if it's not added already
        for (LandMapTile tile : this) {
            if (isEdgeTile(tile) == false) {
                continue;
            }

            // Now check all the adjacent regions; if any one of them isn't
            // this region and isn't already on the list, add it.
            checkAddAdjacentRegion(tile.getRegionNorth(), m_alAdjacentRegions);
            checkAddAdjacentRegion(tile.getRegionSouth(), m_alAdjacentRegions);
            checkAddAdjacentRegion(tile.getRegionEast(), m_alAdjacentRegions);
            checkAddAdjacentRegion(tile.getRegionWest(), m_alAdjacentRegions);
        }
    }

    private void checkAddAdjacentRegion(LandRegion reg, ArrayList<LandRegion> adjList) {
        if (reg == null) {
            return;
        }
        int regId = reg.getId();

        // If it's us, we can't add it
        if (regId == this.getId()) {
            return;
        }
        
        // If it's already on the list we can't add it
        if (isAdjacentTo(reg) == true) {
            return;
        }

        adjList.add(reg);
    }

    public boolean isAdjacentTo(int regId) {
        if (m_alAdjacentRegions == null) {
            return false;
        }
        for (LandRegion region : m_alAdjacentRegions) {
            if (region.getId() == regId) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdjacentTo(LandRegion reg) {
        if (reg == null) {
            return false;
        }
        return isAdjacentTo(reg.getId());
    }

    public LandMapTile getRandomTile() {
        if (size() == 0)
            return null;
        return get(Utils.number(0, size()-1));
    }

    public boolean isEdgeTile(LandMapTile tile) {
        if (tile == null) {
            return false;
        }
        if (tile.getNorth() != null && tile.getNorth().getRegionId() != getId())
            return true;
        if (tile.getSouth() != null && tile.getSouth().getRegionId() != getId())
            return true;
        if (tile.getEast() != null && tile.getEast().getRegionId() != getId())
            return true;
        if (tile.getWest() != null && tile.getWest().getRegionId() != getId())
            return true;
        return false;
    }

    public boolean isInThisRegion(LandMapTile tile) {
        if (tile == null) {
            return false;
        }
        return (tile.getRegionId() == getId());
    }

    public LandMapTile getRandomEdgeTile() {
        if (size() == 0)
            return null;
        LandMapTile tile = getRandomTile();

        int loopCheck = 0;
        while (isEdgeTile(tile) == false) {
            tile = getRandomTile();

            // If we have huge regions this might start to happen, otherwise it
            // shouldn't.  If it does, it doesn't really matter, we just end up with
            // potentially a small region, which is fine.
            loopCheck++;
            if (loopCheck > 2000) {
                Utils.log("LoopCheck passed 2000 in getRandomEdgeTile");
                return null;
            }
        }

        return tile;   
    }

    // Get a random tile bordering this region right on the edge.
    public LandMapTile getRandomTileBeyondEdge() {
        LandMapTile tile = getRandomEdgeTile();
        if (tile == null) {
            return null;
        }

        LandMapTile beyond = tile.getTileInRandomDir();
        int loopCheck = 0;
        while (isInThisRegion(beyond) == true) {
            beyond = tile.getTileInRandomDir();

            // If something effed up and we somehow don't have a valid edge,
            // don't just loop forever.  Shouldn't happen but just in case.
            loopCheck++;
            if (loopCheck > 200) {
                Utils.log("LoopCheck passed 200 in getRandomTileBeyondEdge");
                return null;
            }
        }
        return beyond;
    }
}