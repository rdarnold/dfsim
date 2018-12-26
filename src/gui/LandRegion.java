package dfsim.gui;

import java.util.ArrayList;

import dfsim.*;
import dfsim.gui.*;

// A section of land with an associated level and perhaps other attributes
public class LandRegion extends ArrayList<LandMapTile> {
    
    private static int currentId = 0;

    private int m_nId = currentId;
    private int m_nLevel = 0;

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