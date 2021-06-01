package dfsim.gui;

import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import dfsim.*;

// So this is basically just an HexMapEntity list, but it has
// interesting properties like how it's laid out and such.
public class HexMap extends ArrayList<HexMapTile> {
    
    int numX = 0;
    int numY = 0;

    public HexMap() {
        // So create entities right next to each other
        // for the size of the map.
        int size = HexMapEntity.defSize;
        numX = (int)((DfSim.width / size)*0.70);
        numY = (int)((DfSim.hexMapScreen.topAreaHeight / size)*2.2);

        for (int y = 0; y < numY; y++) {
            for (int x = 0; x < numX; x++) {
                HexMapTile ent = new HexMapTile();
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
            HexMapTile hent = this.get(i);
            
            // North is always same x but y-2
            hent.north = getAt(hent.hexMapX, hent.hexMapY-2);
            hent.south = getAt(hent.hexMapX, hent.hexMapY+2);

            if (hent.hexMapY % 2 == 0) {
                // Northwest is actually same x but y-1
                hent.northwest = getAt(hent.hexMapX, hent.hexMapY-1);
                hent.northeast = getAt(hent.hexMapX+1, hent.hexMapY-1);

                // Southwest is actually same x but y+1
                hent.southwest = getAt(hent.hexMapX, hent.hexMapY+1);
                hent.southeast = getAt(hent.hexMapX+1, hent.hexMapY+1);
            }
            else {
                // Northwest is actually same x but y-1
                hent.northwest = getAt(hent.hexMapX-1, hent.hexMapY-1);
                hent.northeast = getAt(hent.hexMapX, hent.hexMapY-1);

                // Southwest is actually same x but y+1
                hent.southwest = getAt(hent.hexMapX-1, hent.hexMapY+1);
                hent.southeast = getAt(hent.hexMapX, hent.hexMapY+1);
            }
        }
    }

    public HexMapTile getAt(int x, int y) {
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hent = this.get(i);
            if (hent.hexMapX == x && hent.hexMapY == y) {
                return hent;
            }
        }
        return null;
    }

    public void addToPane(Pane pane) {
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
    }

    int comps = 0;
    public void setCanMoveToRecursive(HexMapTile from, int numMoves) {
        if (numMoves < 0)
            return;
        
        if (from == null)// || from.canMoveTo == true)
            return;

        if (from.traversable(false) == false)
            return;

        from.setCanMoveTo(true);

        int moves = numMoves - from.moveCost();

        // Now go through each one
        setCanMoveToRecursive(from.northwest, moves);
        setCanMoveToRecursive(from.north, moves);
        setCanMoveToRecursive(from.northeast, moves);
        setCanMoveToRecursive(from.southwest, moves);
        setCanMoveToRecursive(from.south, moves);
        setCanMoveToRecursive(from.southeast, moves);
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
 
    public void setCanMoveTo(HexMapTile from, int numMoves) {
        // So, starting at the "from" tile, go numMoves in every
        // direction.
        setCanMoveToRecursive(from, numMoves);
        from.setCanMoveTo(false); // can't move into the space you're already in

        updateColors();
    }

    public HexMapTile getNextCurMovPathTile() {
        int min = 999999;
        HexMapTile tile = null;
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hex = this.get(i);
            if (hex.getCurMovPath() == false)
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
            if (hex.getCurMovPath() == false)
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
            HexMapTile hent = this.get(i);
            if (hent.getCurMovStep() > from) {
                hent.setCurMovPath(false);
                hent.setCurMovStep(0);
            }
        }
        updateColors();
    }

    public void clearCanMoveTo() {
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hent = this.get(i);
            hent.setCanMoveTo(false);
            hent.setCurMovPath(false);
            hent.setCurMovStep(0);
        }
        updateColors();
    }

    public void updateColors() {
        for (int i = 0; i < this.size(); i++) {
            HexMapTile hent = this.get(i);
            hent.updateColor();
        }
    }

    public HexMapTile getRandomHex() {
        return getAt(Utils.number(0, numX-1), Utils.number(0, numY-1));
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
}