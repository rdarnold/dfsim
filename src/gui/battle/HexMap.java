package dfsim.gui;

import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import javafx.scene.canvas.*;

import dfsim.*;

// So this is basically just an HexMapEntity list, but it has
// interesting properties like how it's laid out and such.
public class HexMap extends ArrayList<HexMapTile> {
    
    private HexMapScreen m_HexMapScreen;

    private HexMapEntity m_SelectedHexMapEntity = null;
    public HexMapEntity getSelectedHexMapEntity() { return m_SelectedHexMapEntity; }

    private HexMapTile m_SelectedHexMapTile = null;
    public HexMapTile getSelectedHexMapTile() { return m_SelectedHexMapTile; }
    
    public ArrayList<HexMapEntity> partyEntities;
    public ArrayList<HexMapEntity> monsEntities; 

    // Variable to keep track of whether or not we are animating, so that we can block input.
    // What we do is, anything that is moving or animating pushes itself onto the list, 
    // and then removes itself when it's done.  That way we just need to check if the list
    // is empty to see if we have anything animating.
    private ArrayList<HexMapEntity> animatingList = new ArrayList<HexMapEntity>();
    // These are called by the entities themselves to add themselves as needed.
    public void addAnimatingEntity(HexMapEntity ent) {
        if (animatingList.contains(ent) == false) {
            animatingList.add(ent);
        }  
    }
    public void removeAnimatingEntity(HexMapEntity ent) { animatingList.remove(ent); }
    public boolean isAnimating() { return animatingList.size() > 0; }

    int m_numXTiles = 0;
    int m_numYTiles = 0;

    public HexMap(HexMapScreen screen) {
        // Give us a ref to our parent screen
        m_HexMapScreen = screen;

        partyEntities = new ArrayList<HexMapEntity>();
        monsEntities = new ArrayList<HexMapEntity>();

        // So create entities right next to each other
        // for the size of the map.
        int size = Constants.BASE_HEX_TILE_SIZE;
        m_numXTiles = (int)((DfSim.width / size)*0.68);
        m_numYTiles = (int)((DfSim.hexMapScreen.topAreaHeight / size)*2.4);

        for (int y = 0; y < m_numYTiles; y++) {
            for (int x = 0; x < m_numXTiles; x++) {
                HexMapTile ent = new HexMapTile(this);
                ent.hexMapX = x;
                ent.hexMapY = y;
                add(ent);
                //ent.setText("" + x + "," + y);
            }
        }

        updateDirections();
        randomizeTerrain();
    }

    // Update the north / southeast / etc directions for all tiles
    private void updateDirections() {
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hex = this.get(i);
            
            // North is always same x but y-2
            hex.north = getAt(hex.hexMapX, hex.hexMapY-2);
            hex.south = getAt(hex.hexMapX, hex.hexMapY+2);

            if (hex.hexMapY % 2 == 0) {
                // Northwest is actually same x but y-1
                hex.northwest = getAt(hex.hexMapX, hex.hexMapY-1);
                hex.northeast = getAt(hex.hexMapX+1, hex.hexMapY-1);

                // Southwest is actually same x but y+1
                hex.southwest = getAt(hex.hexMapX, hex.hexMapY+1);
                hex.southeast = getAt(hex.hexMapX+1, hex.hexMapY+1);
            }
            else {
                // Northwest is actually same x but y-1
                hex.northwest = getAt(hex.hexMapX-1, hex.hexMapY-1);
                hex.northeast = getAt(hex.hexMapX, hex.hexMapY-1);

                // Southwest is actually same x but y+1
                hex.southwest = getAt(hex.hexMapX-1, hex.hexMapY+1);
                hex.southeast = getAt(hex.hexMapX, hex.hexMapY+1);
            }
        }
    }

    public HexMapTile getAt(int x, int y) {
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hex = this.get(i);
            if (hex.hexMapX == x && hex.hexMapY == y) {
                return hex;
            }
        }
        return null;
    }

    public void addToCanvas(DfCanvas canvas) {
        int num = 1;
        int xShift = 50;
        int yShift = 20;

        // Add and move them
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hex = this.get(i);
            //hex.addToPane(pane);
            hex.tileNum = num;
            
            if (hex.hexMapY % 2 == 0) {
                double xPos = (hex.hexMapX*2) * (hex.getSize()*.75);
                double yPos = (hex.hexMapY/2) * (hex.getSize()-5);
                xPos += xShift;
                yPos += yShift;
                hex.moveTo(xPos, yPos);
            } else {
                double xPos = ((hex.hexMapX-1)*2) * (hex.getSize()*.75);
                double yPos = ((hex.hexMapY-1)/2) * (hex.getSize()-5);
                xPos += hex.getSize()*.75;
                yPos += ((hex.getSize()-5)/2);
                xPos += xShift;
                yPos += yShift;
                hex.moveTo(xPos, yPos);
            }

           // hex.setText("" + getRowForY(hex)); //.tileNum);
            num++;
        }
        canvas.setHexMap(this);
    }

    /*public void addToPane(Pane pane) {
        int num = 1;
        int xShift = 44;
        int yShift = 20;

        // Add and move them
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hent = this.get(i);
            hent.addToPane(pane);
            hent.tileNum = num;
            
            if (hent.hexMapY % 2 == 0) {
                double xPos = (hent.hexMapX*2) * (hent.getSize()*.75);
                double yPos = (hent.hexMapY/2) * (hent.getSize()-5);
                xPos += xShift;
                yPos += yShift;
                hent.moveTo(xPos, yPos);
            } else {
                double xPos = ((hent.hexMapX-1)*2) * (hent.getSize()*.75);
                double yPos = ((hent.hexMapY-1)/2) * (hent.getSize()-5);
                xPos += hent.getSize()*.75;
                yPos += ((hent.getSize()-5)/2);
                xPos += xShift;
                yPos += yShift;
                hent.moveTo(xPos, yPos);
            }

           // hent.setText("" + getRowForY(hent)); //.tileNum);
            num++;
        }
    }*/

    public void addParty(ArrayList<Person> party) {
        removeAllPartyMembers();
        for (Person person : party) {
            addPartyMemberHexMapEntity(person);
        }
    }

    public void addMons(ArrayList<DfMon> mons) {
        removeAllMons();
        for (DfMon mon : mons) {
            addMonHexMapEntity(mon);
        }
    }

    public void addPartyMemberHexMapEntity(Person person) {
        HexMapEntity ent = new HexMapEntity(this, person);

        int x = m_numXTiles/2;
        int y = m_numYTiles/2;
        
        if (partyEntities.size() > 0) {
            // If we have current party entities, find a random spot
            // to place this person based on where current party is.
            // Pick a random person, then a random direction
            int num = Utils.number(0, partyEntities.size()-1);
            HexMapEntity e = partyEntities.get(num);
            HexMapTile eHex = e.getHex();
            // Should not be null but check just in case...
            if (eHex != null) {
                // False means we do not allow a null tile
                HexMapTile nextHex = eHex.getTileInRandomDir(false);
                int dist = Utils.number(0, 2); // Within 3 spaces
                while (dist > 0) {
                    dist--;
                    nextHex = nextHex.getTileInRandomDir(false);
                    // No going back to our original tile.
                    while (nextHex == eHex) {
                        nextHex = nextHex.getTileInRandomDir(false);
                    }
                }
                // Should never be null...
                if (nextHex != null) {
                    x = nextHex.hexMapX;
                    y = nextHex.hexMapY;
                }
            }
        }
        else {
            // If not, just do it totally at random.
            x = Utils.number(1, m_numXTiles - 1);
            y = Utils.number(1, m_numYTiles - 1);
        }
        HexMapTile hex = getAt(x, y);
        if (hex == null) {
            Utils.log("X: " + x + ", y: " + y);
        }
        addHexMapEntity(hex, ent);

        partyEntities.add(ent);
    }

    public void addMonHexMapEntity(DfMon mon) {
        HexMapEntity ent = new HexMapEntity(this, mon);
        monsEntities.add(ent);
        int x = Utils.number(1, m_numXTiles - 1);
        int y = Utils.number(1, m_numYTiles - 1);
        HexMapTile hex = getAt(x, y);
        addHexMapEntity(hex, ent);
    }

    private boolean addHexMapEntity(HexMapTile hex, HexMapEntity ent ) {
        HexMapTile attachHex = hex;
        int loopCheck = 0;
        
        while (attachHex.setContainsIfEmpty(ent) == false) {
            int x = Utils.number(1, m_numXTiles - 1);
            int y = Utils.number(1, m_numYTiles - 1);
            attachHex = getAt(x, y);
            loopCheck++;
            if (loopCheck > 200) {
                Utils.log("loopCheck passed 200 tries in addHexMapEntity");
                return false;
            }
        }
        if (attachHex != null) {
            ent.moveToTile(attachHex, false);
        }
        ent.setRandomFacing();
        return true;
    }

    public void removeAllPartyMembers() {
        for (int i = partyEntities.size()-1; i >= 0; i--) {
            HexMapEntity ent = partyEntities.get(i);
            removeHexMapEntity(ent);
        }
        partyEntities.clear();
    }

    public void removeAllMons() {
        for (int i = monsEntities.size()-1; i >= 0; i--) {
            HexMapEntity ent = monsEntities.get(i);
            removeHexMapEntity(ent);
        }
        monsEntities.clear();
    }

    public void removeAllEntities() {
        removeAllPartyMembers();
        removeAllMons();
    }

    public void removeHexMapEntity(HexMapEntity ent) {
        // Remove from animation list.
        ent.setAnimating(false);

        //ent.removeFromPane(topArea);
        if (ent.getHex() != null) {
            //ent.getHex().detach();
            ent.getHex().removeContains(ent);
        }
        if (ent == getSelectedHexMapEntity()) {
            deselect();
        }

        monsEntities.remove(ent);
        partyEntities.remove(ent);
    }

    public void resetTurnMovs() {
        for (HexMapEntity ent : partyEntities) {
            ent.setTurnMovDone(false);
        }
        for (HexMapEntity ent : monsEntities) {
            ent.setTurnMovDone(false);
        }
    }

    public void resetTurnAtks() {
        for (HexMapEntity ent : partyEntities) {
            ent.setTurnAtkDone(false);
        }
        for (HexMapEntity ent : monsEntities) {
            ent.setTurnAtkDone(false);
        }
    }

    public void reset() {
        // remove the entities
        removeAllEntities();

        // regenerate the map
        randomizeTerrain();
    }

    public void selectHexMapTile(HexMapTile tile) {
        m_SelectedHexMapTile = tile;
    }
    
    public void onLeftClickHexMapTile(HexMapTile hex) {
        selectHexMapTile(hex);
        if (hex.containsHexMapEntity() == true) {
            onLeftClickHexMapEntity(hex.getContains());
            return;
        }

        if (m_SelectedHexMapEntity != null && hex.containsHexMapEntity() == false) {
            //hex.attach(m_SelectedHexMapEntity);
            m_SelectedHexMapEntity.moveToTile(hex);
            m_SelectedHexMapEntity.setTurnMovDone(true);
            clearCanMoveTo();
        }
    }

    public void onRightClickHexMapTile(HexMapTile hex) {
        if (hex.containsHexMapEntity() == true) {
            onRightClickHexMapEntity(hex.getContains());
            return;
        }

        // Maybe context menu or something
        clearCurMovPath();
        m_SelectedHexMapEntity.resetCurMov();
    }

    /* Algorithm Description:  Hex Pathfinding
    2-Step Strategy:
        - Find the shortest path from start node to every node in the grid (run a BFS) ,
          and mark each node with the "length" (i.e. movement-cost) of that 
          shortest path at that node
        - Walk backwards from our destination node, checking for whichever node
          bordering it has the lowest cost.  Repeat until we hit the start node.
          These nodes are all higlighted as the shortest movement path.
        
    Key Optimizations:
        - Make sure not to repeat searches on nodes that don't need it.  For example,
          keep track of current lowest cost path of all paths, and if any path meets or
          exceeds that during search, immediately abandon.
        - "Try" to go in the "right direction" to start out; the sooner we find the shorter
          paths, the faster the algorithm because then other paths will be abandoned as
          they are too long.
    */
    // Utility for pathfinding
    // Should this become a separate hexmap pathfinding utility class?
    private void clearBfsMarks() {
        for (HexMapTile hex : this) {
            hex.unmark();
            hex.setText("");
        }
    }

    public int sumMoveCost(HexMapEntity ent, ArrayList<HexMapTile> tiles) {
        int sum = 0;
        for (HexMapTile tile : tiles) {
            sum += tile.moveCost(ent);
        }
        return sum;
    }

    // Can move through friends but can't stop on them
    public boolean canMoveThroughTile(HexMapEntity ent, HexMapTile tile) {
        if (tile == null) {
            return false;
        }
        if (tile.traversable(ent) == false) {
            return false;
        }
        if (tile.containsEnemy(ent) == true) {
            return false;
        }
        return true;
    }

    public boolean canStopOnTile(HexMapEntity ent, HexMapTile tile) {
        if (canMoveThroughTile(ent, tile) == false) {
            return false;
        }
        if (tile.containsHexMapEntity() == true) {
            return false;
        }
        return true;
    }

    // Take a guess at the best direction to look in, helps guide the search and keep
    // it more efficient
    public Constants.Ordinal guessStartDir(HexMapEntity ent, HexMapTile goal, HexMapTile from) {
        if (goal.getCenterX() > from.getCenterX())  {
            // Goal is to the east
            if (canMoveThroughTile(ent, from.getSoutheast()) && goal.getCenterY() > from.getCenterY()) {
                return Constants.Ordinal.SOUTHEAST;
            }
            else if (canMoveThroughTile(ent, from.getNortheast())) {
                return Constants.Ordinal.NORTHEAST;
            }
        }
        else if (goal.getCenterX() < from.getCenterX())  {
            // Goal is to the west
            if (canMoveThroughTile(ent, from.getSouthwest()) && goal.getCenterY() > from.getCenterY()) {
                return Constants.Ordinal.SOUTHWEST;
            }
            else if (canMoveThroughTile(ent, from.getNorthwest())) {
                return Constants.Ordinal.NORTHWEST;
            }
        }
        else {
            if (canMoveThroughTile(ent, from.getSouth()) && goal.getCenterY() > from.getCenterY()) {
                return Constants.Ordinal.SOUTH;
            }
            else if (canMoveThroughTile(ent, from.getNorth()))  {
                return Constants.Ordinal.NORTH;
            }
        }
        // We didn't guess anything, just guess at random then.
        return Constants.Ordinal.getRandomDir();
    }

    // Returns true if a valid path was found
    public int findValidPath(HexMapEntity traveler, HexMapTile goal, HexMapTile from) {
        return findNextStep(traveler, goal, from, Constants.Ordinal.NONE, 0, true);
    }
    
    public int findNextStep(HexMapEntity traveler, HexMapTile goal, HexMapTile from) {
        return findNextStep(traveler, goal, from, Constants.Ordinal.NONE, 0, false);
    }

    public int findNextStep(HexMapEntity traveler, HexMapTile goal, HexMapTile from, Constants.Ordinal lastDir, int cost) {
        return findNextStep(traveler, goal, from, lastDir, cost, false);
    }


    // Keep track of some variables 
    int m_nFindNextStepCalls = 0; // Logging only
    int m_nCurrentBestPathCost = -1;  // Super important

    // Setting 'pathOnly' as true is just intended to find a valid path only, it doesn't find shortest path
    // Returns true if a valid path was found
    public int findNextStep(HexMapEntity traveler, HexMapTile goal, HexMapTile from, Constants.Ordinal lastDir, int cost, boolean pathOnly) {
        m_nFindNextStepCalls++;
        if (from == null ) {
            return -1;
        }
        if (from.marked() && (pathOnly == true || (cost >= from.getCost()))) {
            return -1;
        }
        if (from == goal) {
            // If we are just looking for a valid path, we are DONE.
            if (pathOnly == true) {
                return cost;
            }

            // Reached goal, ignore any paths that go over this cost.
            if (m_nCurrentBestPathCost == -1) {
                m_nCurrentBestPathCost = cost;
            }
            else if (cost < m_nCurrentBestPathCost) {
                m_nCurrentBestPathCost = cost;
            }
            return cost;
        }
        if (canMoveThroughTile(traveler, from) == false) {
            return -1;
        }

        // Mark them all with the lowest cost, then what we do is backtrace
        // from the goal tile, check all adjacent ones to find the one with the lowest
        // cost, then from there, check all adjacnet tiles that have not previously been
        // checked, etc.  basically "dequeue" it
        from.mark(cost);
        //from.setText("" + cost);

        // Moving "out of" the tile is what takes movement
        int newCost = cost + from.moveCost(traveler);

        // If current cost exceeds our best case so far, just abandon it.
        if (pathOnly == false && m_nCurrentBestPathCost > -1 && newCost >= m_nCurrentBestPathCost) {
            return -1;
        }
        
        // Take a guess at where to start, and go from there.  This should
        // reduce execution time considerably.
        int lowestCost = -1;
        Constants.Ordinal dir = guessStartDir(traveler, goal, from);
        for (int i = 0; i < 6; i++) {
            if (lastDir != Constants.Ordinal.revOrdinal(dir)) {
                int pathCost = findNextStep(traveler, goal, from.getByOrdinal(dir), dir, newCost, pathOnly);
                if (pathCost >= 0 && pathOnly == true) {
                    // If we are just looking for a path, well we just found one, we can
                    // return now.
                    return cost;
                }
                // Keep checking this each loop in case we updated the cost meanwhile.
                // If current cost exceeds our best case so far, just abandon it.
                if (pathOnly == false && m_nCurrentBestPathCost > -1 && newCost >= m_nCurrentBestPathCost) {
                    return -1;
                }
                // Keep track of our current lowest cost path.
                if (lowestCost == -1 || pathCost < lowestCost) {
                    lowestCost = pathCost;
                }
            }
            dir = Constants.Ordinal.next(dir);
        }
        return lowestCost;
    }

    public int getCostIfLess(HexMapTile tile, int cost) {
        if (tile == null) {
            return -1;
        }
        if (tile.marked() == false || tile.getCost() == -1) {
            return -1;
        }
        if (cost == -1 || tile.getCost() < cost) {
            return tile.getCost();
        }
        return -1;
    }

    //if (goal.getNorthwest()) { if (cost == -1) { cost = from.getNorthwest(); } else { tempCost = cost = from.getNorthwest(); }}

    public HexMapTile findNextBackStep(HexMapTile tile, HexMapTile start) {
        if (tile == start) {
            return null;
        }
        HexMapTile next = null;
        int cost = -1;
        int tempCost = -1;
        if ((tempCost = getCostIfLess(tile.getNorthwest(), cost)) != -1)  { cost = tempCost; next = tile.getNorthwest(); }
        if ((tempCost = getCostIfLess(tile.getNorth(), cost)) != -1)      { cost = tempCost; next = tile.getNorth(); }
        if ((tempCost = getCostIfLess(tile.getNortheast(), cost)) != -1)  { cost = tempCost; next = tile.getNortheast(); }
        if ((tempCost = getCostIfLess(tile.getSouthwest(), cost)) != -1)  { cost = tempCost; next = tile.getSouthwest(); }
        if ((tempCost = getCostIfLess(tile.getSouth(), cost)) != -1)      { cost = tempCost; next = tile.getSouth(); }
        if ((tempCost = getCostIfLess(tile.getSoutheast(), cost)) != -1)  { cost = tempCost; next = tile.getSoutheast(); }
        return next; 
    }

    // Now walk backwards along the way
    public void backTrackFromGoal(HexMapTile goal, HexMapTile start) {
        // Immediately eliminate any costs that are too high
        for (HexMapTile hex : this) {
            if (hex.getCost() > m_nCurrentBestPathCost) {
                hex.unmark();
                //hex.setText("");
            }
        }

        // Now walk back from goal, to find the best match
        HexMapTile tile = goal;
        ArrayList<HexMapTile> list = new ArrayList<HexMapTile>();
        while (tile != null) {
            // In the future only set it on the path if it can be reached by player;
            // if it's out of range, stop there.
            list.add(tile);
            tile = findNextBackStep(tile, start);
        }

        if (list.size() <= 0) {
            return;
        }

        // Now walk backwards thnrough the list of tiles to set up the movement
        // path correctly; right now it's moving backwards.
        int step = 0;
        // size()-2 because we leave out the final tile, which IS literally the tile
        // the entity is standing on.  We don't want to move to the tile the entity
        // is standing on because the entity is already there
        for (int i = list.size()-2; i >= 0; i--) {
            tile = list.get(i);
            tile.setOnCurMovPath(true);
            tile.setCurMovStep(step++);
        }
    }

    public boolean isTileReachable(HexMapTile target, HexMapEntity ent) {
        m_nFindNextStepCalls = 0;
        // If target isn't traversable/enterable, don't even try.
        if (canStopOnTile(ent, target) == false) {
            return false;
        }

        // Now the harder part, is the tile for example part of a multi-tile
        // island in the middle of the water, or on the other side of
        // a river.
        m_nCurrentBestPathCost = -1;
        clearBfsMarks();
        return (findValidPath(ent, target, ent.getHex()) != -1);
    }

    // So if we are "out of movement path" and we already highlighted
    // the path, but then we hover over a square that is outside of that
    // path, rather then leaving it there, we try to find a new path to
    // this current tile
    public void findShortestPathToTile(HexMapTile target, HexMapEntity ent) {
        // Make sure we can reach the tile, otherwise we will burn a lot of
        // CPU cycles failing.
        if (isTileReachable(target, ent) == false) {
            //Utils.log("NOT REACHABLE: " + m_nFindNextStepCalls);
            return;
        }
        
        clearCurMovPath();
        ent.resetCurMov();
        clearBfsMarks();

        // Basically we need to run a bfs with weights to figure out
        // which path is the "shortest"
        // Even if we can't reach it, that's fine, we'll highlight as far as
        // we can along the route.
        m_nFindNextStepCalls = 0;
        m_nCurrentBestPathCost = -1;
        findNextStep(ent, target, ent.getHex());
        //Utils.log("Calls: " + m_nFindNextStepCalls);

        // Now backtrace from goal
        backTrackFromGoal(target, ent.getHex());
    }

    // Only generate a new path if we entered a new tile
    private HexMapTile m_LastTileForPathUsed = null;
    public void onMouseEnterHexMapTile(HexMapTile hex) {
        //Utils.log(hex.getTileNumber());
        if (m_SelectedHexMapEntity != null) {
            if (hex != m_LastTileForPathUsed) {
                m_LastTileForPathUsed = hex;
                findShortestPathToTile(hex, m_SelectedHexMapEntity);
            }
            /*
            if (m_SelectedHexMapEntity.getTurnMovDone() == true)
                return;

            if (hex.containsHexMapEntity(m_SelectedHexMapEntity) == true) {
                clearCurMovPath();
                m_SelectedHexMapEntity.resetCurMov();
            }
            else if (hex.isOnCurMovPath() == true) {
                // Clear everything past this one
                // if we re-hover on one we've already used
                clearCurMovPath(hex.getCurMovStep());
                m_SelectedHexMapEntity.curMov = (int)m_SelectedHexMapEntity.getMov() - hex.getCurMovStep();
            }
            else if (m_SelectedHexMapEntity.curMov > 0) {
                // Check if we are bordering more than one other mov path, we need to have
                // at least one, but if we have more than one we eliminate that and
                // everything past it.
                int pathLen = hex.numberAdjacentCurMovPathTiles(m_SelectedHexMapEntity);
                if (pathLen <= 0) {
                    return;
                }
                if (pathLen > 1) {
                    // So I need to find the adjacent tile to clear past now...
                    // theoretically it should be the highest one
                    //Utils.log("Arsewobbler " +m_SelectedHexMapEntity.curMov);
                    //m_SelectedHexMapEntity.curMov++;
                    //hexMap.clearCurMovPath(m_SelectedHexMapEntity.curMov);
                }
                m_SelectedHexMapEntity.curMov--;
                hex.setOnCurMovPath(true);
                hex.setCurMovStep((int)m_SelectedHexMapEntity.getMov() - m_SelectedHexMapEntity.curMov);
                //Utils.log(hex.getCurMovStep());
            }*/
            hex.updateGraphics();
        }
    }

    // If we mouse over one of our party or a mon, then it should reset the mov
    // path.
    public void onMouseEnterHexMapEntity(HexMapEntity ent) {
        if (m_SelectedHexMapEntity == ent) {
            if (m_SelectedHexMapEntity.curMov < m_SelectedHexMapEntity.getMov()) {
                clearCurMovPath();
                m_SelectedHexMapEntity.resetCurMov();
            }
        }
    }

    public void onLeftClickHexMapEntity(HexMapEntity ent) {
        // For now, every time we select a new HexMapEntity we just reset all
        // the others.  At some point we'll want to keep track of actual
        // turns and use them accordingly.
        if (m_SelectedHexMapEntity != ent) {
            resetTurnMovs();
            resetTurnAtks();
        }
        selectHexMapEntity(ent);
        ent.resetCurMov();
        
        // Show the movable tiles
        // So firstly clear all movable tiles
        clearCanMoveTo();

        if (ent.getTurnMovDone() == true)
            return;

        // Now set the tiles that can be moved to
        if (ent.mon != null) {
            setCanMoveTo(ent, ent.getHex(), ent.mon.getMov());
        }
        else if (ent.partyMember != null) {
            setCanMoveTo(ent, ent.getHex(), ent.partyMember.getMov());
        }
    }

    public void onRightClickHexMapEntity(HexMapEntity ent) {
        // Attack or something
        if (ent == null || m_SelectedHexMapEntity == null)
            return;

        attack(m_SelectedHexMapEntity, ent);
        m_SelectedHexMapEntity.setTurnAtkDone(true);
    }

    public void selectHexMapEntity(HexMapEntity ent) {
        if (m_SelectedHexMapEntity != null) {
            m_SelectedHexMapEntity.setSelected(false);
        }
        m_SelectedHexMapEntity = ent;
        m_SelectedHexMapEntity.setSelected(true);
    }

    public void deselect() {
        clearCanMoveTo();
        if (m_SelectedHexMapEntity != null)
        m_SelectedHexMapEntity.setSelected(false);
        m_SelectedHexMapEntity = null;
    }

    
    ////////////////////////////
    ////////////////////////////
    // START BATTLE FUNCTIONS //
    ////////////////////////////
    ////////////////////////////
    public void kill(HexMapEntity ch, HexMapEntity ent) {
        if (ent.mon != null) {
            m_HexMapScreen.appendText("   ... " + ent.getName()+ " has died!");
           
            // Award stats
            awardStats(ch, ent.mon);

            // And remove from game
            removeHexMapEntity(ent);
        }
        else {
            ent.hp = 0;
            m_HexMapScreen.appendText("   ... " + ent.getName()+ " collapses!");
        }
    }

    public int getStatAward(Person ch, DfMon vict, double perc, double min, double max) {
        if (ch == null || vict == null || (max <= min))
            return 0;
        int roll = Utils.number(1, 100);

        if (roll > (int)(perc*100)) {
            return 0;
        }

        int num = Utils.number((int)min, (int)max);
        // But now reduce it through trophies or whatever...
        return num;
    }

    public void awardStats(HexMapEntity ent, DfMon vict) {
        if (ent == null || vict == null)
            return;
        
        Person ch = ent.partyMember;

        if (ch == null) 
            return;
        
        int str = getStatAward(ch, vict, vict.getStrPerc(), vict.getStrMin(), vict.getStrMax());
        int agi = getStatAward(ch, vict, vict.getAgiPerc(), vict.getAgiMin(), vict.getAgiMax());
        int dex = getStatAward(ch, vict, vict.getDexPerc(), vict.getDexMin(), vict.getDexMax());
        int sta = getStatAward(ch, vict, vict.getStaPerc(), vict.getStaMin(), vict.getStaMax());
        int kno = getStatAward(ch, vict, vict.getKnoPerc(), vict.getKnoMin(), vict.getKnoMax());
        int mag = getStatAward(ch, vict, vict.getMagPerc(), vict.getMagMin(), vict.getMagMax());
        int luk = getStatAward(ch, vict, vict.getLukPerc(), vict.getLukMin(), vict.getLukMax());

        ch.addStr(str);
        ch.addAgi(agi);
        ch.addDex(dex);
        ch.addSta(sta);
        ch.addKno(kno);
        ch.addMag(mag);
        ch.addLuk(luk);
        if (str + agi + dex + sta + kno + mag + luk <= 0) {
            return;
        }
        if (str > 0) m_HexMapScreen.appendText("   ... +" + str + " Str");
        if (agi > 0) m_HexMapScreen.appendText("   ... +" + agi + " Agi");
        if (dex > 0) m_HexMapScreen.appendText("   ... +" + dex + " Dex");
        if (sta > 0) m_HexMapScreen.appendText("   ... +" + sta + " Sta");
        if (kno > 0) m_HexMapScreen.appendText("   ... +" + kno + " Kno");
        if (mag > 0) m_HexMapScreen.appendText("   ... +" + mag + " Mag");
        if (luk > 0) m_HexMapScreen.appendText("   ... +" + luk + " Luk");
        m_HexMapScreen.appendText("   ... is now Level " + ch.getLevel());
    }

    public void doAttackAnimation(HexMapEntity ch, HexMapEntity vict) {
        SpriteAnimation anim = GraphicsUtils.getAnimationSpriteForKey(Data.atkAnimSprites, "sword");
        if (anim != null) {
            anim.animate(m_HexMapScreen.getCanvas(), vict.getCenterX(), vict.getCenterY()); 
        }
    }

    public void attack(HexMapEntity ch, HexMapEntity vict) {
        if (ch.getTurnAtkDone() == true) {
            m_HexMapScreen.appendText(ch.getName() + " ...");
            m_HexMapScreen.appendText("   ... has already attacked!");
            return;
        }

        m_HexMapScreen.appendText(ch.getName() + " attacks...");
        m_HexMapScreen.appendText("   " + vict.getName() + "!");
        
        doAttackAnimation(ch, vict);

        // Now, calculate the hit, damage, etc.
        boolean success = hit(ch, vict);
        if (success == false) {
            m_HexMapScreen.appendText("   ... Miss!");
            return;
        }
            
        int dmg = damage(ch, vict);
        m_HexMapScreen.appendText("   ... Hit for " + dmg + " damage!");
        vict.hp -= dmg;
        if (vict.hp <= 0) {
            kill(ch, vict);
        }
    }
    
    /*
    Min: Hit * (2/3)
    Max: Min + Hit
    Rng: Max – Min
    */
    public double getHitMin(HexMapEntity ch) {
        return (ch.getHit() * 0.67);
    }

    public double getHitMax(HexMapEntity ch) {
        return (getHitMin(ch) + ch.getHit());
    }

    /*public double getHitRng(HexMapEntity ch) {
        return (getHitMax(ch) - getHitMin(ch));
    }*/

    public boolean hit(HexMapEntity ch, HexMapEntity vict) {

        //Formula: (Target Eva – Attacker Min) * (100 / Attacker Rng)
        //(Anything below 10% is 10%, anything above 90% is 90%)
        double evadeChance = 
            (vict.getEva() - getHitMin(ch)) *
            (100F / ch.getHit());
        
        if (evadeChance < 10) { evadeChance = 10; }
        if (evadeChance > 90) { evadeChance = 90; }

        int chance = (int)evadeChance;
        int roll = Utils.number(1, 100);
        if (chance > roll) {
            return false; 
        }
        return (true);
    }

    public int damage(HexMapEntity ch, HexMapEntity vict) {
        int dmg = (int)(ch.getPwr() / 10);
        
        // But now check def
        int absorb = Utils.number(0, (int)(vict.getDef()/10));
        dmg -= absorb;
        if (dmg < 0) { dmg = 0; }
        return dmg;
    }
    //////////////////////////
    //////////////////////////
    // END BATTLE FUNCTIONS //
    //////////////////////////
    //////////////////////////

    int comps = 0;
    public void setCanMoveToRecursive(HexMapEntity traveler, HexMapTile from, int numMoves) {
        if (numMoves < 0)
            return;
        
        if (from == null)// || from.canMoveTo == true)
            return;

        if (from.traversable(traveler) == false)
            return;

        from.setCanMoveToFlag(true);

        int moves = numMoves - from.moveCost(traveler);

        // Now go through each one
        setCanMoveToRecursive(traveler, from.northwest, moves);
        setCanMoveToRecursive(traveler, from.north, moves);
        setCanMoveToRecursive(traveler, from.northeast, moves);
        setCanMoveToRecursive(traveler, from.southwest, moves);
        setCanMoveToRecursive(traveler, from.south, moves);
        setCanMoveToRecursive(traveler, from.southeast, moves);
    }

    public int getColumnForX(HexMapTile tile) {
        int col = (tile.hexMapX * 2) + 1;
        // So what column are we in, there are numX * 2 columns.
        // If it's odd, we know that X deceases by 1 to the left, and
        // stays the same to the right.
        if (tile.hexMapY % 2 != 0) {
            col--;
        }
        return col;
    }

    public int getRowForY(HexMapTile tile) {
        // Easy, this literally is just the hex map Y the way I
        // have it set up.
        return tile.hexMapY;
    }

    public int getXDistance(HexMapTile tile, HexMapTile tile2) {
        int dist = 0;
        // So basically, this tells you moves you'd have to take to
        // get to the same X axis
        dist = Math.abs(getColumnForX(tile) - getColumnForX(tile2));
        return dist;
    }

    public int getYDistance(HexMapTile tile, HexMapTile tile2) {
        int dist = 0;
        dist = Math.abs(getRowForY(tile) - getRowForY(tile2));
        // Technically it's half of that because of the way tiles touch
        dist/=2;
        return dist;
    }

    public int getDistance(HexMapTile tile, HexMapTile tile2) {
        // How far are these two apart just using their coordinates?
        int dist = 0;

        int xdist = getXDistance(tile, tile2);
        int ydist = getYDistance(tile, tile2);

        if (xdist > ydist) {
            dist = xdist + ((ydist+1)/2);
        }
        else {
            dist = ydist + ((xdist+1)/2);
        }
       
        // Yesssssss this works
        return dist;
    }
 
    public void setCanMoveTo(HexMapEntity traveler, HexMapTile from, int numMoves) {
        // So, starting at the "from" tile, go numMoves in every
        // direction.
        setCanMoveToRecursive(traveler, from, numMoves);
        from.setCanMoveToFlag(false); // can't move into the space you're already in

        updateColors();
    }

    public HexMapTile getNextCurMovPathTile() {
        int min = 999999;
        HexMapTile tile = null;
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hex = this.get(i);
            if (hex.isOnCurMovPath() == false)
                continue;
            if (hex.getCurMovStep() < min) {
                tile = hex;
                min = hex.getCurMovStep();
            }
        }
        return tile;
    }

    public int countCurMovPathTiles() {
        int num = 0;
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hex = this.get(i);
            if (hex.isOnCurMovPath() == false)
                continue;
            num++;
        }
        return num;
    }

    public void clearCurMovPath() {
        clearCurMovPath(-1);
    }

    public void clearCurMovPath(int from) {
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hex = this.get(i);
            if (hex.getCurMovStep() > from) {
                hex.setOnCurMovPath(false);
                hex.setCurMovStep(0);
            }
        }
        updateColors();
    }

    public void clearCanMoveTo() {
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hex = this.get(i);
            hex.setCanMoveToFlag(false);
            hex.setOnCurMovPath(false);
            hex.setCurMovStep(0);
        }
        updateColors();
    }

    public void updateColors() {
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hex = this.get(i);
            hex.updateGraphics();      
        }
    }

    public HexMapTile getRandomHex() {
        return getAt(Utils.number(0, m_numXTiles-1), Utils.number(0, m_numYTiles-1));
    }

    public void randomizeTerrain() {
        int num = 0;

        // Create some cool terrain effects.

        // Let's start with all grass but this will depend on the environment.
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hent = this.get(i);
            hent.setType(HexMapTile.HexTileType.Grass);
        }

        // Sometimes there are streets, this should be closer to the city though
        /*if (Utils.number(1, 4) == 1) {
            createStreets();
        }*/

        // There are patches of trees with grass around them
       // if (Utils.number(1, 2) == 1) {
            num = Utils.number(1, 4); 
            for (int i = 0; i < num; i++) {
                createTreePatch();
            }
       // }

        // There can be streams, ponds, or lakes of varying deep and shallow
        // water that vary in width.
       // if (Utils.number(1, 2) == 1) {
            // 50% chance to have water bodies
            num = Utils.number(1, 4);
            for (int i = 0; i < num; i++) {
                //if (Utils.number(1, 2) == 1)
                    createStream();
                //else
                    createPond();
            }

        //}
        
        // There are logs that can be one or more tiles
        //if (Utils.number(1, 2) == 1) {
            num = Utils.number(1, 8); 
            for (int i = 0; i < num; i++) {
                createLog();
            }
        //}

        // There are stones that can be as large as several tiles
        //if (Utils.number(1, 2) == 1) {
            num = Utils.number(1, 6); 
            for (int i = 0; i < num; i++) {
                createStone();
            }
       // }

       createDeepWater();
       
       updateColors();
    }


    /*  Blank,
        hent.setType(HexMapTile.HexTileType.Grass);
        hent.setType(HexMapTile.HexTileType.Field);
        hent.setType(HexMapTile.HexTileType.Sand);
        hent.setType(HexMapTile.HexTileType.Dirt);
        hent.setType(HexMapTile.HexTileType.Tree);
        hent.setType(HexMapTile.HexTileType.Log);
        hent.setType(HexMapTile.HexTileType.Stone);
        hent.setType(HexMapTile.HexTileType.Wall);
        hent.setType(HexMapTile.HexTileType.Street);
        hent.setType(HexMapTile.HexTileType.ShallowWater);
        hent.setType(HexMapTile.HexTileType.DeepWater);
        */
    public void createStreets() {
        int len = Utils.number(4, 50);
        HexMapTile hent = getRandomHex();
        len--;
        while (len > 0) {
            // This shouldn't be a random direction though,
            // it should have to actually flow
            hent = hent.getTileInRandomDir();
            if (hent == null)
                return;
            hent.setType(HexMapTile.HexTileType.Street);
            len--;
        }
    }

    public void createStream() {
        int loopCheck = 0;
        int len = Utils.number(4, 50);
        HexMapTile hent = getRandomHex();
        HexMapTile prev = hent;
        hent.setType(HexMapTile.HexTileType.ShallowWater);
        len--;
        while (len > 0) {
            loopCheck = 0;
            // This shouldn't be a random direction though,
            // it should have to actually flow
            prev = hent;
            hent = prev.getTileInRandomDir();
            if (hent == null)
                return;

            // Keep it only one stream tile touching one other
            while (hent.numberAdjacentTilesOfType(HexMapTile.HexTileType.ShallowWater) >= 2) {
                hent = prev.getTileInRandomDir();
                if (hent == null)
                    return;
                loopCheck++;
                if (loopCheck >= 100)
                    return;
            }
            if (hent == null)
                return;
            
            hent.setType(HexMapTile.HexTileType.ShallowWater);
            len--;
        }
    }

    private HexMapTile getNextPondEdgeTile(ArrayList<HexMapTile> pond) {
        int loopCheck = 0;
        // Go through all tiles in the pond - something has to have
        // an edge because the pond can't be as big as the map - 
        // and grab one at random.

        int tileNum = Utils.number(0, pond.size()-1);
        HexMapTile hex = pond.get(tileNum);

        while (hex.surroundedByType(HexMapTile.HexTileType.ShallowWater) == true) {
            tileNum = Utils.number(0, pond.size()-1);
            hex = pond.get(tileNum);
            // Just check to make sure we dont infinitely loop in case we have
            // ponds everywhere for whatever reason
            loopCheck++;
            if (loopCheck >= 100)
                return null;
        }
        return hex;
    }

    public void createPond() {
        int size = Utils.number(4, 30);
        HexMapTile hent = getRandomHex();
        hent.setType(HexMapTile.HexTileType.ShallowWater);

        ArrayList<HexMapTile> pond = new ArrayList<HexMapTile>();
        pond.add(hent);

        // Ok so basically, the size is the number of hex tiles
        // we use.  So each time, we find a spot in the pond that has
        // an edge that does not have water, then we randomly fill
        // around it.
        size--;
        while (size > 0) {
            size--;
            hent = getNextPondEdgeTile(pond);
            if (hent == null)
                break;

            // So this is the edge of the pond, we know it has at least one
            // direction without water in it, so just randomly select until we find it.
            int tileNum = Utils.number(0, 5);
            HexMapTile hex = hent.getAdjacentTileForNumber(tileNum);
            while (hex == null || (hex.type == HexMapTile.HexTileType.ShallowWater) == true) {
                tileNum = Utils.number(0, 5);
                hex = hent.getAdjacentTileForNumber(tileNum);
            }
            if (hex == null)
                break;
            hex.setType(HexMapTile.HexTileType.ShallowWater);
            pond.add(hex);
        }
        // Free the temp memory
        pond.clear();
    }

    public void createStone() {
        int size = Utils.number(1, 6);
        HexMapTile hent = getRandomHex();
        hent.setType(HexMapTile.HexTileType.Stone);

        // So choose size of things around you
        size--;
        while (size > 0) {
            HexMapTile hex = hent.getTileInRandomDir();
            if (hex == null)
                return;
            hex.setType(HexMapTile.HexTileType.Stone);
            size--;
        }
    }

   /* public void createTreePatchRecursive(HexMapTile from, int num, int origSize) {
        if (num < 0)
            return;
        
        if (from == null)// || from.canMoveTo == true)
            return;

        double perc = ((double)origSize - (double)(origSize - num)) / (double)origSize;

        int newPerc = (int)(perc*100);
        newPerc /= 2;
        if (from.percent < newPerc)
            from.percent = newPerc;

        //Utils.log("Size: " + origSize + ", Num: " + num + ", perc: " + from.percent);

        int numLeft = num - 1;

        // Now go through each one
        createTreePatchRecursive(from.northwest, numLeft, origSize);
        createTreePatchRecursive(from.north, numLeft, origSize);
        createTreePatchRecursive(from.northeast, numLeft, origSize);
        createTreePatchRecursive(from.southwest, numLeft, origSize);
        createTreePatchRecursive(from.south, numLeft, origSize);
        createTreePatchRecursive(from.southeast, numLeft, origSize);
    }*/

    public void createTreePatch() {
        // Basically, lots of trees in the center although they're generally
        // separated by whatever else, and thins out as it gets further
        // from the center.
        // So what we'll do is, take a patch of area based on the size,
        // load it all up into an array, assign probabilities based on the distance
        // from the center, and load trees based on that.

        int size = Utils.number(3, 12);
        HexMapTile start = getRandomHex();
        start.setType(HexMapTile.HexTileType.Tree);
        
        //createTreePatchRecursive(hent, size-1, size);
        /*for (int i = 0; i < this.size(); i++) {
            HexMapTile hex = this.get(i);
            if (Utils.number(1, 100) <= hex.percent) {
                hex.setType(HexMapTile.HexTileType.Tree);
            }
            hex.percent = 0;
        }*/

        // The new way, we can calculate distance, so just iterate
        // through the whole map and check distance from the origin
        // from each hex - this is way less expensive (n) than finding shortest
        // path to everything within size area which has taken hundreds of
        // thousands of comparisons at times for some reason.
        int dist = 0;
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hex = this.get(i);
            if (hex == start) 
                continue;
            dist = getDistance(start, hex);
            if (dist >= size)
                continue;
            double perc = ((double)size - (double)(size - dist)) / (double)size;
            int newPerc = (int)(perc*100);
            newPerc /= 2;
            if (Utils.number(1, 100) <= newPerc) {
                hex.setType(HexMapTile.HexTileType.Tree);
            }
        }
    }

    public void createLog() {
        int loopCheck = 0;

        int size = Utils.number(1, 4);
        HexMapTile hent = getRandomHex();
        HexMapTile prev = null;
        hent.setType(HexMapTile.HexTileType.Log);
        size--;
        while (size > 0) {
            loopCheck = 0;
            prev = hent;
            hent = prev.getTileInRandomDir();
            if (hent == null)
                return;
            while (hent.numberAdjacentTilesOfType(HexMapTile.HexTileType.Log) >= 2) {
                hent = prev.getTileInRandomDir();
                if (hent == null)
                    return;
                loopCheck++;
                if (loopCheck >= 100)
                    return;
            }
            if (hent == null)
                return;
            hent.setType(HexMapTile.HexTileType.Log);
            size--;
        }
    }

    public void createDeepWater() {
        // Comb the map for any shallow water sections and if they're surrounded by
        // water on more than 2 sides give them an increasing chance at being deep water.
        
        double chance = 0;
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hent = this.get(i);
            if (hent.type != HexMapTile.HexTileType.ShallowWater)
                continue;
            int num = hent.numberAdjacentWaterTiles();
            if (num > 2) {
                chance = ((double)num / 6F) * 75;
                if (Utils.number(1, 100) < (int)chance) {
                    hent.setType(HexMapTile.HexTileType.DeepWater);
                }
            }
        }
    }

    // We cannot provide input when things are animating, etc.
    // Any buttons on the associated screen should check this too.
    public boolean inputAllowed() {
        if (DfSim.sim.isShowingDialogue() == true) {
            return false;
        }
        if (isAnimating() == true) {
            return false;
        }
        return true;
    }

    public void onLeftClick(double x, double y) { 
        if (inputAllowed() == false) {
            return;
        }
        HexMapTile tile = getTileForClick(x, y);
        if (tile != null) {
            onLeftClickHexMapTile(tile);
        }
    }

    public void onRightClick(double x, double y) { 
        if (inputAllowed() == false) {
            return;
        }
        HexMapTile tile = getTileForClick(x, y);
        if (tile != null) {
            onRightClickHexMapTile(tile);
        }
    }

    public void onLeftPressed(double x, double y) { 
        if (inputAllowed() == false) {
            return;
        }
    }
    public void onRightPressed(double x, double y) { 
        if (inputAllowed() == false) {
            return;
        }
    }
    public void onLeftDragged(double x, double y) { 
        if (inputAllowed() == false) {
            return;
        }
    }
    public void onRightDragged(double x, double y) {
        if (inputAllowed() == false) {
            return;
        }
    }

    public void onMouseMove(double x, double y) { 
        if (inputAllowed() == false) {
            return;
        }
        HexMapTile tile = getTileForClick(x, y);
        if (tile != null) {
            onMouseEnterHexMapTile(tile);
        }
    }
    private boolean withinClick(double x, double y, MovablePolygon poly) {
        // Thank you JavaFX, you rocked here.
        return (poly.contains(x, y));

        /*int leftX = (int)poly.getCenterX() - (int)poly.getRadius();
        int rightX = (int)poly.getCenterX() + (int)poly.getRadius();
        int topY = (int)poly.getCenterY() - (int)poly.getRadius();
        int bottomY = (int)poly.getCenterY() + (int)poly.getRadius();

        if (x >= leftX && x <= rightX && y >= topY && y <= bottomY) {
            return true;
        }
        return false;*/
    }

    protected HexMapTile getTileForClick(double x, double y) {
        // Just iterate through and see which one is within.
        for (HexMapTile tile : this) {
            if (withinClick(x, y, tile) == true) {
                return tile;
            }
        }
        return null;
    }

    //@Override
    public void draw(GraphicsContext gc) {
        // Draw all the visible tiles (tile knows if it's visible or not)
        for (HexMapTile tile : this) {
            tile.draw(gc);
        }

        if (m_HexMapScreen == null) {
            return;
        }

        if (partyEntities != null) {
            for (HexMapEntity ent : partyEntities) {
                ent.draw(gc);
            }
        }

        if (monsEntities != null) {
            for (HexMapEntity ent : monsEntities) {
                ent.draw(gc);
            }
        }
    }
    
}