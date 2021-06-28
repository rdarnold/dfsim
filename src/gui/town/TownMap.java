package dfsim;

import java.util.List;
import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import javafx.beans.property.*;

import javafx.scene.canvas.*;

import dfsim.gui.*;

// So this is meant to be the overland map.
public class TownMap extends DfSquareMap {

    public enum TileType {
        Blank,
        Grass,
        Field,
        Water,
        Sand,
        Tree,
        Road,
        Swamp,
        StoneWall,
        WoodWall,
        Door
    }

    public ArrayList<TownMapTile> tilesArray;
    public TownMapTile[][] tiles;

    public TownMapAvatar avatar;
    public ArrayList<TownMapPersonEntity> people;

    public boolean isInitialized() { return tilesArray != null; }

    // We can have different types of towns, and whether we are a capital or not.
    // Potentially this should go on the town class and this map class should be purely
    // map stuff, called by the town stuff.
    protected boolean capital = false;
    public boolean isCapital() { return capital; }
    public void setIsCapital(boolean cap) { capital = cap; }
    
    int population;
    Constants.TownSize townSize;

    // Basically, compared to a "normal" town of 50x50 squares, which is 
    // what the generator was written for, how big is this particular town.
    // 1.5 means it's 75x50, etc.
    double getSizeRatio() {
        double base = 50 * 50;
        double cur = numXTiles * numYTiles;
        return (cur / base);
    }

    public TownMap(int pop, boolean generate) { 
        super();
        population = pop;
        townSize = Constants.TownSize.sizeForPopulation(population);
        if (generate == true) {
            regen();
        }
    }

    public void clear() {
        xOffset.set(0);
        yOffset.set(0);
        removeFromPane();

        avatar = null;
        if (tiles != null) {
            for (int y = 0; y < numYTiles; y++) {
                for (int x = 0; x < numXTiles; x++) {
                    tiles[x][y] = null;
                }
            }
        }
        tiles = null;
        if (tilesArray != null) {
            tilesArray.clear();
            tilesArray = null;
        }
        if (people != null) {
            people.clear();
            people = null;
        }
    }

    public void regen() {
        clear();
        tilesArray = new ArrayList<TownMapTile>();
        people = new ArrayList<TownMapPersonEntity>();

        // Num X and Y tiles based on size of town.
        // I should vary up the vertical and horizontal sizes.
        // Could be interesting if it were like the population of the
        // town / 100 or something.  Some towns would be SO huge then,
        // but that could be really cool too.
        switch (townSize) {
            case SMALL:
                numXTiles = Utils.number(20, 35);
                numYTiles = Utils.number(20, 35);
                break;
            case MEDIUM:
                numXTiles = Utils.number(40, 60);
                numYTiles = Utils.number(40, 60);
                break;
            case LARGE:
                numXTiles = Utils.number(70, 100);
                numYTiles = Utils.number(70, 100);
                break;
            case HUGE:
                numXTiles = Utils.number(110, 150);
                numYTiles = Utils.number(110, 150);
                break;
        }
        Utils.log("X " + numXTiles + ", Y " + numYTiles);
        tiles = new TownMapTile[numXTiles][numYTiles];

        // And create it
        TownMapTile tile;
        for (int y = 0; y < numYTiles; y++) {
            for (int x = 0; x < numXTiles; x++) {
                tile = new TownMapTile(this);
                tile.translateXProperty().bind(xOffset);
                tile.translateYProperty().bind(yOffset);
                tiles[x][y] = tile;
                tile.mapX = x;
                tile.mapY = y;
                tilesArray.add(tile);
            }
        }
        // Set the base class pointer to the tile array
        super.squareTiles = tiles;
        
        updateDirections();
        randomizeTerrain();
        updateColors();

        avatar = new TownMapAvatar(this);
        tilesArray.get(0).attach(avatar);

        //if (pane != null) {
        //    addToPane(pane);
        //}
        moveTilesToCorrectPositions();
    }

    public void start() {
        start(Constants.Dir.fromInt(Utils.number(0, 3)));
    }

    public void start(Constants.Dir dir) {
        avatar.setPerson(Data.personList.get(0));

        // Stick us in the middle
        avatar.unrestrictedMoveTo(DfSim.townMapScreen.wid/2, DfSim.townMapScreen.hgt/2);

        // Depending on the direction we entered, find an acceptable
        // entrance tile.
        TownMapTile tile = getStartTileForDir(dir);
        tile.attach(avatar);
        centerOnEntity(avatar, false);
    }
    
    public void updateOneFrame() {
        for (TownMapPersonEntity pers : people) {
            // See what happens with this mon.
            if (pers.decWait(Utils.number(500/60, 2000/60)) == true) {
                moveTownPerson(pers);
            }
        }
    }

    public void moveTownPerson(TownMapPersonEntity pers) {
        if (pers.canMove() == false)
            return;
        TownMapTile tile = pers.getTile();
        Constants.Dir dir = tile.getRandomValidDir();
        TownMapTile moveTile = (TownMapTile)tile.getAdjacentTileInDir(dir);
        while (pers.canMoveToTile(moveTile) == false) {
            dir = tile.getRandomValidDir();
            moveTile = (TownMapTile)tile.getAdjacentTileInDir(dir);
        }
        if (moveTile != null) {
            pers.moveTo(moveTile, dir, true);
        }
        /*TownMapTile moveTile = tile.getRandomValidAdjacentTile();
        while (pers.canMoveToTile(moveTile) == false) {
            moveTile = tile.getRandomValidAdjacentTile();
        }
        if (moveTile != null) {
            pers.moveTo(moveTile, true);
        }*/
    }

    public TownMapTile getAt(int x, int y) {
        if (x < 0 || y < 0 || x >= numXTiles || y >= numYTiles)
            return null;
        return tiles[x][y];
    }

    public boolean validStartTile(TownMapTile tile) {
        if (tile.blockMove() == true)
            return false;
        return true;
    }

    public TownMapTile getStartTileForDir(int num) {
        return getStartTileForDir(Constants.Dir.fromInt(num));
    }

    public TownMapTile getStartTileForDir(Constants.Dir dir) {
        int tileX = 0;
        int tileY = 0;
        boolean swap = false;
        int dist = 1;
        TownMapTile tile  = null;
        switch (dir) {
            case NORTH:
                // Start in the middle along the north edge 
                tileX = numXTiles / 2;
                tileY = 0;
                break;
            case EAST:
                tileX = numXTiles - 1;
                tileY = numYTiles / 2;
                break;
            case WEST:
                tileX = 0;
                tileY = numYTiles / 2;
                break;
            case SOUTH:
                tileX = numXTiles / 2;
                tileY = numYTiles - 1;
                break;
        }
        
        tile = getAt(tileX, tileY);
        switch (dir) {
            case NORTH:
            case SOUTH:
                // Move outwards looking for an appropriate tile.
                while (validStartTile(tile) == false) {
                    if (swap == false) {
                        tileX += dist;
                        dist++;
                        swap = true;
                    } 
                    else {
                        tileX -= dist;
                        dist++;
                        swap = false;
                    }

                    if (tileX < 0 || tileX >= numXTiles)
                        break;

                    tile = getAt(tileX, tileY);
                }
                return tile;
            case EAST:
            case WEST:
                while (validStartTile(tile) == false) {
                    if (swap == false) {
                        tileY += dist;
                        dist++;
                        swap = true;
                    } 
                    else {
                        tileY -= dist;
                        dist++;
                        swap = false;
                    }

                    if (tileY < 0 || tileY >= numYTiles)
                        break;

                    tile = getAt(tileX, tileY);
                }
                return tile;
        }

        Utils.log("TownMap getStartTileForDir: No start " + dir + " tile found!  Using random tile.");
        return getRandomValidMovableTile();
    }

    public void updateColors() {
        for (TownMapTile tile : tilesArray) {
            tile.updateColor();
        }
    }

    public void moveTilesToCorrectPositions() {
        int xShift = 2;
        int yShift = 2;

        // Add and move them
        for (int y = 0; y < numYTiles; y++) {
            for (int x = 0; x < numXTiles; x++) {
                TownMapTile tile = getAt(x, y);
                double xPos = tile.mapX * (tile.getWidth());
                double yPos = tile.mapY * (tile.getWidth());
                xPos += xShift;
                yPos += yShift;
                tile.moveTo(xPos, yPos);
            }
        }
        for (TownMapPersonEntity pers : people) {
            pers.moveTo(pers.getTile(), Constants.Dir.SOUTH);
        }
    }
    
    /*public void addToPane(Pane thePane) {
        pane = thePane;

        // Add and move the
        for (TownMapTile tile : tilesArray) {
            tile.addToPane(pane);
        }
        avatar.addToPane(pane);
        for (TownMapPersonEntity pers : people) {
            pers.addToPane(pane);
        }
    }*/

    public void removeFromPane() {
        if (pane == null)
            return;
        if (avatar != null) {
            avatar.removeFromPane(pane);
        }
        if (tilesArray != null) {
            for (TownMapTile tile : tilesArray) {
                tile.removeFromPane(pane);
            }
        }
        if (people != null) {
            for (TownMapPersonEntity pers : people) {
                pers.removeFromPane(pane);
            }
        }
    }

    @Override
    protected void updateVisible() {
        if (avatar == null) {
            for (TownMapTile tile : tilesArray) {
                tile.setVisible(true);
            }
            return;
        }

        // Only nodes within a certain distance of the main dude are visible.
        for (TownMapTile tile : tilesArray) {
            if (getDistance(tile, avatar.getTile()) < 40)
                tile.setVisible(true);
            else
                tile.setVisible(false);
        }
    }
    
    @Override
    protected void onMoveFinished() {
        super.onMoveFinished();
        TownMapTile tile = avatar.getTile();
        if (tile.getType() == TileType.Door) {
            // Enter a building
        }
    }

    @Override
    public void moveAvatarToTile(DfSquareMapEntity ent, DfSquareTile moveTile, Constants.Dir dir) {
        if (moveTile == null) {
            DfSim.showLandMapScreen();
            return;
        }
        if (moveTile.canBeMovedTo(ent) == false) {
            return;
        }
        super.moveAvatarToTile(ent, moveTile, dir);
    }

    public void moveAvatarByInput(Constants.Dir dir) {
        super.moveAvatarByInput(avatar, dir);
    }

    // Interact with whatever you are facing.
    private void interact() {
        TownMapTile tile = (TownMapTile)avatar.getTileFacing();
        // If it's a treasure or contains someone, interact.
        TownMapEntity ent = tile.getContains();
        if (ent != null) {
            if (ent.getType() == DfSquareMapEntity.EntityType.Person) {
                TownMapPersonEntity pent = (TownMapPersonEntity)ent;
                // Set facing; it'll always be the revdir of the char facing
                pent.setFacing(Constants.Dir.revDir(avatar.getFacing()));

                Person pers = pent.getPerson();
                Utils.log("Interacted with " + pers.toStringNPC());
                
                // I can start to play with my random dialogue system here.  This needs
                // to be moved into some dialogue processor class.
                if (pers.getHasBeenMet() == false) {
                    if (pers.getAffection() < 250) {
                        DfSim.sim.showDialogue(pers, "Ew, go away."); 
                    }
                    else if (pers.getAffection() < 500) {
                        DfSim.sim.showDialogue(pers, "Hello, nice to meet you.  I'm " + pers.getName() + "."); 
                    }
                    else if (pers.getAffection() < 750) {
                        DfSim.sim.showDialogue(pers, "Oh, it's great to finally meet you!  My name is " + pers.getName() + "."); 
                    }
                    else {
                        DfSim.sim.showDialogue(pers, "Wow, I've heard so much about you!  You're a hero! Oh, gosh, my name is " + pers.getName() + "."); 
                    }
                }
                else {
                    if (pers.getAffection() < 250) {
                        DfSim.sim.showDialogue(pers, "Ew, you're the last person I wanted to see again."); 
                    }
                    else if (pers.getAffection() < 500) {
                        DfSim.sim.showDialogue(pers, "Hello.  Good to see you again."); 
                    }
                    else if (pers.getAffection() < 750) {
                        DfSim.sim.showDialogue(pers, "It's so great to see you again!"); 
                    }
                    else {
                        DfSim.sim.showDialogue(pers, "I'd drop my panties for you in a second!"); 
                    }
                }
                pers.setHasBeenMet(true);

                // From the back, perhaps some options on right click could be:
                // Talk
                // Give
                // Fight
                // Duel
                // Sap (fail and affection goes down, kick out of house)
                // Steal (fail and affection goes down, kick out of house)

                // But once you are "seen" by someone in a house you stay as
                // seen until you leave, you can't sneak / steal / sap anymore.

                // Invisible spell means you can still do all those things.
                // Fly spell lets you move over houses and walls and such.  You can
                // hit space to enter houses and interact with people.
                // Teleport you can just click where you wanna go and you teleport
                // there.
            }
        }
    }

    public void onRightArrow() {
        moveAvatarByInput(Constants.Dir.EAST);
    }
    public void onLeftArrow() {
        moveAvatarByInput(Constants.Dir.WEST);
    }
    public void onUpArrow() {
        moveAvatarByInput(Constants.Dir.NORTH);
    }
    public void onDownArrow() {
        moveAvatarByInput(Constants.Dir.SOUTH);
    }

    // WASD calls the above functions instead.
    /*public void onD() {
        moveAvatarByInput(Constants.Dir.EAST);
    }
    public void onA() {
        moveAvatarByInput(Constants.Dir.WEST);
    }
    public void onW() {
        moveAvatarByInput(Constants.Dir.NORTH);
    }
    public void onS() {
        moveAvatarByInput(Constants.Dir.SOUTH);
    }*/
    
    public void onF() {
        interact();
    }

    public void onSpace() {
        interact();
    }

    public void onLeftClickAvatar(TownMapAvatar ent) {

    }

    public void onRightClickAvatar(TownMapAvatar ent) {

    }

    public void onLeftClickPerson(TownMapPersonEntity ent) {

    }

    public void onRightClickPerson(TownMapPersonEntity ent) {

    }

    public void onLeftClickTile(TownMapTile tile) {
        //avatar.unrestrictedMoveTo(tile);
        tile.attach(avatar);
        centerOnEntity(avatar);
    }

    public void onRightClickTile(TownMapTile tile) {

    }

    public void onMouseEnterAvatar(TownMapAvatar ent) {
        String str = ent.printInfo();
        DfSim.townMapScreen.updateInfoText(str);
    }

    public void onMouseEnterPerson(TownMapPersonEntity ent) {
        String str = ent.printInfo();
        DfSim.townMapScreen.updateInfoText(str);
    }
    
    public void onMouseEnterTile(TownMapTile tile) {
        //String str = tile.getType().toString();
        DfSim.townMapScreen.updateInfoText("");
    }
    
    public int countTilesOfType(TileType countType) {
        int num = 0;
        for (TownMapTile tile : tilesArray) {
            if (tile.getType() == countType) {
                num++;
            }
        }
        return num;
    }

    // How much land do we have?
    public int countGrass() {
        return countTilesOfType(TileType.Grass);
    }

    public double grassRatio() {
        double ratio = (double)countGrass();
        return (ratio / (numXTiles * numYTiles));
    }
    
    public boolean hasAtLeastOneTileOfType(TileType type) {
        return (getFirstTileOfType(type) != null);
    }

    public TownMapTile getFirstTileOfType(TileType type) {
        if (tilesArray == null)
            return null;

        for (TownMapTile tile : tilesArray) {
            if (tile.getType() == type) {
                return tile;
            }
        }
        return null;
    }

    public TownMapTile getRandomTile() {
        return getAt(Utils.number(0, numXTiles-1), Utils.number(0, numYTiles-1));
    }

    public TownMapTile getRandomWaterTile() {
        TownMapTile tile = getRandomTile();
        while (tile.getType() != TileType.Water) {
             tile = getRandomTile();
        }
        return tile;
    }
    
    public TownMapTile getRandomLandTile() {
        TownMapTile tile = getRandomTile();
        while (tile.getType() != TileType.Grass) {
             tile = getRandomTile();
        }
        return tile;
    }

    // One way to make this more efficient if we needed to, is to keep
    // ArrayLists of pointers to each of the types of tiles, and use those
    // to just choose random tiles in those.  More memory and maintenance though
    // especially if tiles change.  And these are only used when a dungeon is
    // first generated so it probably doesnt't matter too much.
    public TownMapTile getRandomTileOfType(TileType type) {
        int loopCheck = 0;

        TownMapTile tile = getRandomTile();
        while (tile.getType() != type) {
             tile = getRandomTile();
             // Let's hope we know we have a tile of this type
             // when we start, but just in case somehow we screw up and don't,
             // make sure this doesn't run forever.
             loopCheck++;
             if (loopCheck > 9999) {
                 Utils.log("Loopcheck passed 9999 loops in TownMapTile getRandomTileOfType");
                 return getFirstTileOfType(type);
             }
        }
        return tile;
    }
    
    public TownMapTile getRandomValidMovableTile() {
        TownMapTile tile = getRandomTile();
        while (tile.blockMove() == true) {
             tile = getRandomTile();
        }
        return tile;
    }
    
    // Update the north / southeast / etc directions for all tiles
    private void updateDirections() {
        for (TownMapTile tile : tilesArray) {
            tile.north = getAt(tile.mapX, tile.mapY-1);
            tile.east  = getAt(tile.mapX+1, tile.mapY);
            tile.west  = getAt(tile.mapX-1, tile.mapY);
            tile.south = getAt(tile.mapX, tile.mapY+1);
        }
    }

    // For any function that uses the marked boolean on the tile, clear it out.
    // Kinda hokey but works.
    public void clearMarks() {
        for (TownMapTile tile : tilesArray) {
            tile.clearMark();
        }
    }

    // Reset any marked tile to previous.  Perhaps we were building some kind of
    // thing in the map and it turned out not to work so we want to revert and retry.
    public void revertMarkedTiles() {
        for (TownMapTile tile : tilesArray) {
            if (tile.isMarked() == true) {
                tile.revertType(); // Revert to previous type.
                tile.clearMark();
            }
        }
    }

    public TileType getRandomWallTypeForTown() {
        TileType wallType = TileType.WoodWall;
        switch (townSize) {
            case HUGE: 
                wallType = TileType.StoneWall;
                break;
            case LARGE: 
                if (Utils.pass(25) == true)
                    wallType = TileType.WoodWall;
                else
                    wallType = TileType.StoneWall;
                break;
            case MEDIUM: 
                if (Utils.pass(50) == true)
                    wallType = TileType.WoodWall;
                else
                    wallType = TileType.StoneWall;
                break;
            case SMALL: 
                wallType = TileType.WoodWall;
                break;
        }
        return wallType;
    }

    public boolean buildHouseFromSeed(TownMapTile seed, int min, int max) {
        clearMarks();
        TileType wallType = getRandomWallTypeForTown();

        int len = Utils.number(min, max);
        int wid = Utils.number(min, max);

        // Randomly choose how far right and left from seed.
        int left = seed.mapX - Utils.number(0, len-1);
        int top = seed.mapY - Utils.number(0, wid-1);

        // If anything is off the map, redo it.  We don't really want
        // partial towns with inaccessible buildings off the map, I think
        // that will be kinda dumb and if tht were the case we should just
        // expand the map to allow the player to go there - I mean why not -
        // it's a massive randomly generated map anyway.
        if (left < 0 || top < 0 || 
            left + len >= numXTiles || top + wid >= numYTiles - 1) {
            return false;
        }

        // Maintain a list of the potential door options as we build.
        ArrayList<TownMapTile> doorOptions = new ArrayList();

        // Then just go through those coordinates and make them all
        // part of the house as long as they exist (they might be
        // off the map)
        for (int x = 0; x < len; x++) {
            for (int y = 0; y < wid; y++) {
                TownMapTile tile = getAt(left + x, top + y);
                // Actually if a tile is null, redo this entire thing.
                if (tile == null)  {
                    revertMarkedTiles();
                    return false;
                }

                // If this tile is a road, don't gen.  We really should
                // check our territory beforehand to make sure it's a valid
                // spot rather than reverting.
                if (tile.getType() == TileType.Road) {
                    revertMarkedTiles();
                    return false;
                }

                // If the tile right above this one is a door, don't gen
                // this because it'll block the door.
                if (tile.n() != null && tile.n().getType() == TileType.Door) {
                    revertMarkedTiles();
                    return false;
                }

                tile.mark();
                tile.setType(wallType);
                if (x >= 1 && x < len-1 && y == wid-1) {
                    doorOptions.add(tile);
                }
            }
        }

        // Now assign a door to one of the bottom tiles if they
        // are on the map.
        if (doorOptions.size() > 0) {
            int num = Utils.number(0, doorOptions.size()-1);
            TownMapTile door = doorOptions.get(num);
            // If thing south of the door is a building, redo this because it'll be blocked.
            if (door.s() == null || door.s().blockMove() == true) {
                revertMarkedTiles();
                return false;
            }
            door.mark();
            door.setType(TileType.Door);
        }

        doorOptions.clear();
        return true;
    }

    public void generateHouses() {
        int loopCheck = 0;

        // Choose some points at random and generate houses
        // as rectangles depending on how big the town is.
        int num = Utils.numberWithRatio(5, 10, getSizeRatio());
        int min = 3;
        int max = 6;

        while (num >= 0) {
            if (buildHouseFromSeed(getRandomLandTile(), min, max) == true) {
                num--;
            }
        }
        
        // For some cities we may want to ensure that they have more than
        // a particular ratio of land to house, like in larger cities and
        // capitals where much of the area is developed.

        // And some towns are just these massive complexes of houses.  Like
        // we could put a small town with a really low ratio and then it becomes
        // one gigantic house, basically, with all these different entrances
        // and exits and stuff, like one huge thriving community.

        double ratio = 1.0;
        switch (townSize) {
            case HUGE: 
                ratio = Utils.random(0.4, 0.5);
                break;
            case LARGE: 
                ratio = Utils.random(0.5, 0.6);
                break;
            case MEDIUM: 
                ratio = Utils.random(0.7, 0.9);
                break;
            case SMALL: 
                // There should be a chance at a "supercomplex" which is like
                // just a massive house of stuff.
                ratio = Utils.random(0.7, 1.0);
                break;
        }
        Utils.log(ratio);

        while (grassRatio() > ratio) {
            buildHouseFromSeed(getRandomLandTile(), min, max);

            loopCheck++;
            if (loopCheck > 999) {
                // Ok we're done, that means we are too crowded.
                Utils.log("TownMap: generateHouses: loopCheck passed 999");
                break;
            }
        }

    }

    public void generateMansion(int left, int right, int top, int bottom) {
        // It can be a mansion or a castle, we pass in the limits if any
        // exist.

        TownMapTile tile;
        TileType wallType = getRandomWallTypeForTown();

        // If no limits, we just create it anywhere.
        for (int x = left; x < right; x++) {
            for (int y = top; y < bottom; y++) {
                tile = getAt(x, y);
                tile.setType(wallType);
            }
        }

        // Now put a door in the middle
        int mid = (left + right) / 2;
        tile = getAt(mid-1, bottom-1);
        tile.setType(TileType.Door);
        tile = getAt(mid, bottom-1);
        tile.setType(TileType.Door);
    }

    public void generateMainRoadWithMansion() {
        // We do both the main road and the mansion in this one,
        // it can be a castle too if the town is big enough.

        // This should depend on the size of the town actually.
        int roadWid = Utils.numberWithRatio(3, 4, getSizeRatio());

        int left = numXTiles / 2 - roadWid / 2;
        int right = left + roadWid;
        int bottom = numYTiles; // Go from bottom to top always

        // Figure out how much space we have for the mansion at the top
        int manWid = Utils.numberWithRatio(8, 12, getSizeRatio());
        int manHgt = Utils.numberWithRatio(6, 10, getSizeRatio());

        int fromTop = Utils.number(1, 6); // 1-6 spaces from the top

        int top = fromTop + manHgt;

        for (int x = left; x < right; x++) {
            for (int y = top; y < bottom; y++) {
                TownMapTile tile = getAt(x, y);
                tile.setType(TileType.Road);
            }
        }

        generateMansion(numXTiles / 2 - manWid / 2,
                        numXTiles / 2 + manWid / 2,
                        fromTop, top);
    }

    public void generateRoads() {
        // Try to connect doors through shortest paths basically.

        // If we are a big city, we might start with the roads and then build
        // the houses around them.  Or we can do the reverse if it's a town that
        // built up over many years rather than something well-planned.  Maybe
        // all towns and villages and cities have a chance to either be planned
        // or unplanned, planned has some nicer straight roads with houses built
        // along them, and unplanned just does houses all over at random with
        // roads connecting wherever.
        
        // So if we do this first, create some sort of interesting grid of
        // roads.  Then we can generate houses along the roads and it will
        // look fairly orderly.

        // Let's give a bunch of different options here for types of road
        // grids.  First, the "large road up the middle" concept with a main
        // street and then branching side streets.

        // So, pick the middle of the map and generate a 4-6 width road 
        // that goes right up the center, at the top of which will be some
        // kind of large or huge building - the castle or town manor or
        // whatever.
        generateMainRoadWithMansion();
    }
    
    public boolean canBeTree(TownMapTile tile) {
        if (tile == null)
            return false;
        if (tile.getType() != TileType.Grass)
            return false;
        // Can't block a door.
        if (tile.n() != null && tile.n().getType() == TileType.Door) {
            return false;
        }
        return true;
    }

    public boolean hasAtLeastOneAdjacentCanBeTreeTile(TownMapTile from) {
        if (from.n() != null && canBeTree(from.n()) == true)
            return true;
        if (from.e() != null && canBeTree(from.e()) == true)
            return true;
        if (from.w() != null && canBeTree(from.w()) == true)
            return true;
        if (from.s() != null && canBeTree(from.s()) == true)
            return true;
        return false;
    }

    public TownMapTile pickNextTree(TownMapTile from) {
        int loopCheck = 0;
        TownMapTile tile = null;

        if (hasAtLeastOneAdjacentCanBeTreeTile(from) == false)
            return null;

        while (tile == null || canBeTree(tile) == false) {
            int dir = Utils.number(0, 3);
            tile = from.getAdjacentTileForNumber(dir);
            loopCheck++;
            // This one shouldnt ever happen since we already checked.
            if (loopCheck > 200) {
                Utils.log("Loopcheck 200+ in pickNextTree");
                return null;
            }
        }
        return tile;
    }

    public boolean buildTreePatchFromSeed(TownMapTile seed, int dist, int numTrees) {
        int loopCheck = 0;
        int curNum = 1;
        
        // Have to start as a grass tile, we can't generate trees over
        // other stuff right now.
        if (canBeTree(seed) == false) {
            return false;
        }


        ArrayList<TownMapTile> trees = new  ArrayList<TownMapTile>();
        seed.setType(TileType.Tree);
        trees.add(seed);

        // Now, fan out randomly somehow to create interesting
        // structures of trees ...   This is different than the overland
        // map trees because these are potentially cultivated by townspeople
        // I guess.  

        // So I guess what I'll do is, choose a random number of trees first,
        // then meander in a various direction until we get to a certain distance
        // away, and then randomly fill in spots in the forest that are connected
        // to the trees based on propability, like the closer you are to a tree
        // the higher the probability of a tree being produced, until we're done.
        // Then close up anything that has three or 4 sides of trees.

        // So, meander.
        TownMapTile prevTile = seed;
        TownMapTile tile = pickNextTree(prevTile);
        while (tile != null && getDistance(seed, tile) < dist && curNum <= numTrees) {
            tile.setType(TileType.Tree);
            trees.add(tile);
            prevTile = seed;
            tile = pickNextTree(prevTile);
            curNum++;
        }

        // If we already have enough, just stop.
        if (curNum >= numTrees) {
            trees.clear();
            return true;
        }

        // Otherwise we made our path, now fill out the rest.
        while (curNum < numTrees) {
            // Pick a random tree in our array, see if it has space for another tree,
            // if so, do it.
            prevTile = trees.get(Utils.number(0, trees.size()-1));
            tile = pickNextTree(prevTile);
            if (tile != null) {
                tile.setType(TileType.Tree);
                trees.add(tile);
                curNum++;
            }
            loopCheck++;
            if (loopCheck > 200) {
                Utils.log("Loopcheck 200+ in buildTreePatchFromSeed");
                break;
            }
        }

        // Probably need some kind of post-check to make sure you can actually
        // walk all the way west to east and north to south now, it may not
        // be possible to access all buildings now.  That might be fine -
        // could be cool to like allow cutting down of trees - but at least you
        // should be able to walk across the village I think.
        
        trees.clear();
        return true;
    }

    public void generateTrees() {
        // Generate some patches of trees, unlike the landmap, these trees
        // don't generally appear as a single tree, they're in patches.  They
        // should not generate over buildings, roads, or block doors, etc.

        // Num of patches is based on an average town of 2500 blocks.
        int num = Utils.numberWithRatio(5, 10, getSizeRatio());
        int dist = Utils.number(3, 10);
        int numTrees = Utils.number(3, 50);
        while (num >= 0) {
            if (buildTreePatchFromSeed(getRandomLandTile(), dist, numTrees) == true) {
                num--;
            }
        }
    }

    public void generatePonds() {

    }

    public void generateTreasure() {

    }

    public boolean townPersonOnThisTile(TownMapTile tile) {
        for (TownMapPersonEntity pers : people) {
            if (pers.getTile() == tile) {
                return true;
            }
        }
        return false;
    }

    public void placeOneTownPerson(Person person) {
        TownMapPersonEntity pers = new TownMapPersonEntity(this);
        pers.setPerson(person);
        pers.translateXProperty().bind(xOffset);
        pers.translateYProperty().bind(yOffset);
        people.add(pers);
        
        TownMapTile tile = getRandomValidMovableTile();
        while (townPersonOnThisTile(tile) == true) {
            tile = getRandomValidMovableTile();
        }
        pers.moveTo(tile, Constants.Dir.SOUTH);
    }

    public void generatePeople() {
        // People can have neat traits like stats and classes.
        // And names and personalities.

        // Ok, let's try it!
        int num = Utils.numberWithRatio(5, 10, getSizeRatio());
        for (int i = 0; i < num; i++) {
            Person person = Person.generateRandomTownPerson();
            Utils.log(person.toStringNPC());
            placeOneTownPerson(person);
        }
    }

    public void generateOrderedTown() {
        
        // An ordered town starts with roads, then does houses around the roads.
        generateRoads();

        // Houses next, around the roads
        generateHouses();

        // Then put some tree patches in avoiding the
        // roads and houses.
        generateTrees();

        // Put in some kind of water bodies, small ponds or whatnot potentially,
        // maybe not all towns have them.
        generatePonds();
    }

    public void generateDisorderedTown() {
        // Start with houses
        generateHouses();

        // Then put some roads between the houses.
        generateRoads();

        // Then put some tree patches in avoiding the
        // roads and houses.
        generateTrees();

        // Put in some kind of water bodies, small ponds or whatnot potentially,
        // maybe not all towns have them.
        generatePonds();
    }

    public void randomizeTerrain() {
        // First, does it have a main road with a mansion/castle?  The bigger
        // cities always do, smaller ones don't always.
        int chance = 100;
        if (townSize == Constants.TownSize.MEDIUM) {
            chance = 50;
        }
        else if (townSize == Constants.TownSize.SMALL) {
            chance = 50;
        }

        if (Utils.pass(chance) == true) {
            generateMainRoadWithMansion();
        }

        if (Utils.pass(50) == true) {
            generateOrderedTown();
        }
        else {
            //generateDisorderedTown();
            generateOrderedTown();
        }

        // Now, every new town should have treasure, which
        // potentially respawns as the town expands or just over
        // time as well.  So randomly place some chests, would be nice
        // to have them like, somehow behind some kind of puzzles or something
        // to make it more interesting, but I guess at first just place them,
        // and the contents should scale according to the size of the town,
        // so larger towns can have better stuff in some of their chests.
        // This is fine, players can create new heroes and stuff and should
        // constantly be needing new good stuff anyway.  It should cost
        // some money to recruit new heroes, and also certain monsters can
        // be turned into heroes such as Dark Elves, and maybe all monsters
        // actually, using some special ability or item, which costs maybe
        // half as much as recruiting a new hero, and the stats and abilities
        // of the hero depend on the stats of the original monster.

        // And actually maybe the heroes like, show up in town but once you
        // recruit them they're gone and have to regen, and you can't choose
        // what type of hero, like Heroes of Might and Magic III, you have to
        // pick one of the ones at the town, and they're generated randomly.

        generateTreasure();

        // Finally, place some people around the town.
        generatePeople();
    }
    
    @Override
    public void onLeftClick(double x, double y) {
        TownMapTile tile = (TownMapTile)getTileForClick(x, y);
        if (tile != null) {
            onLeftClickTile(tile);
        }
    }

    @Override
    public void onRightClick(double x, double y) {
        TownMapTile tile = (TownMapTile)getTileForClick(x, y);
        if (tile != null) {
            onRightClickTile(tile);
        }
    }

    @Override
    public void onLeftPressed(double x, double y) { }

    @Override
    public void onRightPressed(double x, double y) { }
    
    @Override
    public void onLeftDragged(double x, double y) { }

    @Override
    public void onRightDragged(double x, double y) { }
    
    @Override
    public void onMouseMove(double x, double y) { }
    
    @Override
    public void draw(GraphicsContext gc) {
        // Draw all the visible tiles (tile knows if it's visible or not)
        for (TownMapTile tile : tilesArray) {
            tile.draw(gc);
        }
        
        // Draw the mobiles
        for (TownMapPersonEntity p : people) {
            p.draw(gc);
        }

        // Draw the avatar
        avatar.draw(gc);
    }
}