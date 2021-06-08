package dfsim;

import java.util.*;

import javax.swing.plaf.synth.Region;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import javafx.beans.property.*;

import javafx.scene.canvas.*;

import dfsim.gui.*;

// So this is meant to be the overland map.
public class LandMap extends DfSquareMap {

    public ArrayList<LandRegion> regions;
    public ArrayList<LandMapTile> tilesArray;
    public LandMapTile[][] tiles;

    public LandMapEntity avatar;

    LandMapTile m_lmtStartingTownTile = null;
    LandRegion m_lrEndRegion = null;
    LandRegion getStartingRegion() { 
        if (m_lmtStartingTownTile == null) 
            return null; 
        return m_lmtStartingTownTile.getRegion(); 
    }

    public enum MapTileType {
        Blank,
        Grass,
        Field,
        Sand,
        Water,
        Hill,
        Mountain,
        Forest,
        Swamp,
        Deadlands,
        Village,
        Town,
        City,
        BigCity,
        Capital,
        Cave,
        Mine,
        Tower,
        Ruin
    }

    // So we will generate it according to those medieval demographics,
    // Basically one capital city, various smaller cities, various towns,
    // other stuff, and scatter that stuff all over the map, and generate
    // random shops for the towns also based on that same demographics,
    // the larger tows with bigger chances to have stuff.

    // Then we can scatter dungeons all over, especially at the edges of
    // mountain ranges, across rivers, and in random places like the middle
    // of deserts, place towers, castles, caves, ruins.

    // Sometimes new dungeons are formed, or enemies from a dungeon you haven't
    // visited in awhile will pop out as a swarm and actually invade some place.

    // Evil leaders will rise up and lead groups of undead or invasions of imps
    // which can actually destroy or damage towns, reducing their size and stuff
    // inside them, or they can be crushed and defeated entirely, depending on the
    // size of their guard forces.

    // You can recruit heroes at some towns, new heroes that you can then grow
    // and use them to defend your towns.

    // Then in some area, the massive evil exists, the dragon king, whom you 
    // are supposed to defeat.

    public LandMap() { init(); }

    private void init() {
        numXTiles = 200;
        numYTiles = 200;
        tiles = new LandMapTile[numXTiles][numYTiles];
        tilesArray = new ArrayList<LandMapTile>();
        regions = new ArrayList<LandRegion>();

        // And create it
        LandMapTile tile;
        for (int y = 0; y < numYTiles; y++) {
            for (int x = 0; x < numXTiles; x++) {
                tile = new LandMapTile(this);
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
        updateGraphics();
        
        avatar = new LandMapEntity(this);
        
        // The avatar should use the same image as the town entity, dungeon entity, etc; all
        // need to point to the same data structure.
        //avatar.setImage(Data.spriteHero1);
    }

    public void start() {
        avatar.setPerson(Data.personList.get(0));
        avatar.unrestrictedMoveTo(DfSim.landMapScreen.wid/2, DfSim.landMapScreen.hgt/2);
        if (m_lmtStartingTownTile == null) {
            m_lmtStartingTownTile = getRandomPopulatedTile();
        }
        m_lmtStartingTownTile.attach(avatar);
        centerOnEntity(avatar, false);
    }

    public LandMapTile getAt(int x, int y) {
        if (x < 0 || y < 0 || x >= numXTiles || y >= numYTiles)
            return null;
        return tiles[x][y];
    }

    public void updateGraphics() {
        for (LandMapTile tile : tilesArray) {
            tile.updateColor();
            tile.updateImage();
        }
    }

    public void addToCanvas(DfCanvas canvas) {
        int xShift = 2;
        int yShift = 2;

        // Add and move them
        for (int y = 0; y < numYTiles; y++) {
            for (int x = 0; x < numXTiles; x++) {
                LandMapTile tile = getAt(x, y);
                double xPos = tile.mapX * (tile.getWidth());
                double yPos = tile.mapY * (tile.getWidth());
                xPos += xShift;
                yPos += yShift;
                tile.moveTo(xPos, yPos);
            }
        }
        
        canvas.setSquareMap(this);
    }

    /*public void addToPane(Pane pane) {
        int xShift = 2;
        int yShift = 2;

        // Add and move them
        for (int y = 0; y < numYTiles; y++) {
            for (int x = 0; x < numXTiles; x++) {
                LandMapTile tile = getAt(x, y);
                tile.addToPane(pane);

                double xPos = tile.mapX * (tile.getWidth());
                double yPos = tile.mapY * (tile.getWidth());
                xPos += xShift;
                yPos += yShift;
                tile.moveTo(xPos, yPos);
            }
        }
        
        avatar.addToPane(pane);
    }*/

    @Override
    protected void updateVisible() {
        if (avatar == null) {
            for (LandMapTile tile : tilesArray) {
                tile.setVisible(true);
            }
            return;
        }

        // Only nodes within a certain distance of the main dude are visible.
        for (LandMapTile tile : tilesArray) {
            // This could potentially lower at night.
            if (getDistance(tile, avatar.getTile()) < 30)
                tile.setVisible(true);
            else
                tile.setVisible(false);
        }
    }

    @Override
    protected void onMoveFinished() {
        super.onMoveFinished();
        LandMapTile tile = avatar.getTile();
        if (tile.isPopulated()) {
            DfSim.showTownMapScreen(tile, Constants.Dir.revDir(avatar.getLastMoveDir()));
        }
        else if (tile.isDun()) {
            DfSim.showDunMapScreen(tile, Constants.Dir.revDir(avatar.getLastMoveDir()));
        }
    }

    /*public void moveAvatarToTile(LandMapTile moveTile, Constants.Dir dir) {
        if (moveTile != null && avatar != null &&
            moveTile.canBeMovedTo(avatar) == true) {
            moveTile.attach(avatar);
        }
        centerOnAvatar();
        avatar.lastMoveDir = dir;*/

        // Now, was it a town or a dun?
        // Wish I could have it update the screen before entering
        // the new screen so you could see the avatar step on top of
        // the tile.
        /*if (moveTile.isPopulated()) {
            DfSim.showTownMapScreen(Constants.Dir.revDir(dir));
        }
        else if (moveTile.isDun()) {
            DfSim.showDunMapScreen();
        }*/
    /*}

    public void centerOnAvatar() {
        centerOnAvatar(true);
    }

    public void centerOnAvatar(boolean playAnimation) {
        updateVisible();
        if (avatar == null) {
            return;
        }

        centerOnTile(avatar.getTile(), (int)avatar.getX(), (int)avatar.getY(), 
            LandMapEntity.posOffset, playAnimation);
    }*/

    public void moveAvatarByInput(Constants.Dir dir) {
        super.moveAvatarByInput(avatar, dir);
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

    public void onMouseEnterTile(LandMapTile tile) {
        //String str = tile.getType().toString();
        DfSim.landMapScreen.updateInfoText("" + tile.getRegionId() + ": " + tile.getLevel());
    }

    public void onLeftClickTile(LandMapTile tile) {
        //avatar.unrestrictedMoveTo(tile);
        tile.attach(avatar);
        centerOnEntity(avatar);
    }

    public void onRightClickTile(LandMapTile tile) {

    }

    public void onMouseEnterAvatar(LandMapEntity av) {

    }

    public void onLeftClickAvatar(LandMapEntity av) {

    }

    public void onRightClickAvatar(LandMapEntity av) {

    }

    public LandMapTile getRandomTile() {
        return getAt(Utils.number(0, numXTiles-1), Utils.number(0, numYTiles-1));
    }

    public LandMapTile getRandomWaterTile() {
        LandMapTile tile = getRandomTile();
        while (tile.type != MapTileType.Water) {
             tile = getRandomTile();
        }
        return tile;
    }
    
    public LandMapTile getRandomLandTile() {
        LandMapTile tile = getRandomTile();
        while (tile.type == MapTileType.Water) {
             tile = getRandomTile();
        }
        return tile;
    }

    public LandMapTile getRandomPopulatedTile() {
        LandMapTile tile = getRandomTile();
        while (tile.isPopulated() == false) {
             tile = getRandomTile();
        }
        return tile;
    }
    
    public LandMapTile getRandomTileWithoutRegion() {
        LandMapTile tile = getRandomTile();
        while (tile.getRegion() != null) {
             tile = getRandomTile();
        }
        return tile;
    }

    // Update the north / southeast / etc directions for all tiles
    private void updateDirections() {
        for (LandMapTile tile : tilesArray) {
            tile.north = getAt(tile.mapX, tile.mapY-1);
            tile.east  = getAt(tile.mapX+1, tile.mapY);
            tile.west  = getAt(tile.mapX-1, tile.mapY);
            tile.south = getAt(tile.mapX, tile.mapY+1);
        }
    }

    public int getDistance(LandMapTile tile, LandMapTile tile2) {
        // Just a subtraction of x,y
        // How far are these two apart just using their coordinates?
        int dist = 0;

        int xdist = Math.abs(tile.mapX - tile2.mapX);
        int ydist = Math.abs(tile.mapY - tile2.mapY);

        if (xdist > ydist) {
            dist = xdist + ((ydist+1)/2);
        }
        else {
            dist = ydist + ((xdist+1)/2);
        }
       
        return dist;
    }

    // How much land do we have?
    public int countLand() {
        int num = 0;
        for (LandMapTile tile : tilesArray) {
            if (tile.type != MapTileType.Water) {
                num++;
            }
        }
        return num;
    }

    public double landToWaterRatio() {
        double land = (double)countLand();
        return (land / (numXTiles * numYTiles));
    }
    
    //int num = 0;
    private void buildLandFromSeed(LandMapTile seed, int size) {
        seed.type = MapTileType.Grass;

        int dist = 0;
        for (LandMapTile tile : tilesArray) {
            if (tile == seed)
                continue;
            dist = getDistance(seed, tile);
            if (dist >= size)
                continue;
            //num++;
            //Utils.log(num);
            double perc = ((double)size - (double)(size - dist)) / (double)size;
            int newPerc = 100 - ((int)(perc*100));
            if (Utils.number(1, 100) <= newPerc) {
                tile.type = MapTileType.Grass;
            }
        }
    }

    private void generateContinents() {
        // Let's grow some land.  Let's kinda really randomize it
        // but we still want a continent kinda thing, I guess.
        // Well a lot of different options could be cool.
        
        // So maybe I'll do something like choose X number of seed
        // points, and from each of those, an "island" of size Y grows,
        // and those islands are meant to overlap, so they ultimately
        // form some kind of interesting continent.
        //ArrayList<LandMapTile> seeds = new ArrayList<LandMapTile>();

        // Large seeds first
        int numSeeds = Utils.number(7, 11);
        int size = Utils.number(25, 50);
        while (numSeeds > 0) {
            size = Utils.number(25, 50);
            buildLandFromSeed(getRandomTile(), size);
            numSeeds--;
        }

        // Then small seeds
        numSeeds = Utils.number(10, 12);
        size = Utils.number(10, 20);
        while (numSeeds > 0) {
            size = Utils.number(10, 20);
            buildLandFromSeed(getRandomTile(), size);
            numSeeds--;
        }


        // Comb out any that are solitary
        boolean found = true;
        while (found == true) {
            found = false;
            for (LandMapTile tile : tilesArray) {
                if (tile.type == MapTileType.Grass) {
                   if (tile.surroundedByType(MapTileType.Water)) {
                       found = true;
                       tile.type = MapTileType.Water;
                   }
                   else if (tile.numberAdjacentTilesOfType(MapTileType.Water) >= 3) {
                       found = true;
                       tile.type = MapTileType.Water;
                   }
                }
                else {
                   if (tile.surroundedByType(MapTileType.Grass)) {
                       found = true;
                       tile.type = MapTileType.Grass;
                   }
                   else if (tile.numberAdjacentTilesOfType(MapTileType.Grass) >= 3) {
                       found = true;
                       tile.type = MapTileType.Grass;
                   }
                }
            }
        }

        // Comb out everything that is bordered on all but 1 side
        // by something that isnt it, this might make terrain a little
        // boring but we will see
        /*found = false;
        while (found == true) {
            found = false;
            for (LandMapTile tile : tilesArray) {
                if (tile.type == MapTileType.Grass) {
                   if (tile.surroundedByType(MapTileType.Water)) {
                       found = true;
                       tile.type = MapTileType.Water;
                   }
                }
                else {
                   if (tile.surroundedByType(MapTileType.Grass)) {
                       found = true;
                       tile.type = MapTileType.Grass;
                   }
                }
            }
        }*/


        //seeds.clear();
    }

    private void buildForestFromSeed(LandMapTile seed, int size) {
        seed.type = MapTileType.Forest;

        int dist = 0;
        for (LandMapTile tile : tilesArray) {
            if (tile == seed)
                continue;
            if (tile.type == MapTileType.Water)
                continue;
            dist = getDistance(seed, tile);
            if (dist >= size)
                continue;
            //num++;
            //Utils.log(num);
            double perc = ((double)size - (double)(size - dist)) / (double)size;
            int newPerc = 100 - ((int)(perc*100));
            if (Utils.number(1, 100) <= newPerc) {
                tile.type = MapTileType.Forest;
            }
        }
    }

    private void generateForests() {
        // Seed some forests, we can use the same method
        // as the hexmap uses to start and maybe tweak later
        // to try to make things more interesting if we want.
        int numSeeds = Utils.number(30, 50);
        int size = Utils.number(3, 50);
        while (numSeeds > 0) {
            size = Utils.number(3, 50);
            buildForestFromSeed(getRandomLandTile(), size);
            numSeeds--;
        }
    }

    private LandMapTile chooseNextMountainTile(LandMapTile prevTile, int dir, int prevDir) { 
        if (prevTile == null)
            return null;
        // Get a new random direction but not the way we came in
        //int dir = Utils.number(0, 3);
        //while (LandMapTile.isRevDir(prevDir, dir) == true) {
          //  dir = Utils.number(0, 3);
       // }

        // Get the tile for the direction we chose.
        LandMapTile tile = prevTile.getAdjacentTileForNumber(dir);

        // Make sure the tile is a mountainable tile, if not, just break
        // out at this point.  That's fine, just increases the randomness.
        if (tile == null || 
            tile.type == MapTileType.Water) {
            return null;
        }
        return tile;
    }

    private void buildMountainFromSeed(LandMapTile seed, int size) {
        seed.type = MapTileType.Mountain;
        int loopCheck = 0;

        // So from the seed, we move size nodes in random directions,
        // never back on ourself, which gives us a string of mountains.
        // From there, we beef out that string of mountains at random
        // points, building it out probablistically or some shit.
        int sizeLeft = size;

        ArrayList<LandMapTile> mtnRange = new ArrayList<LandMapTile>();
        mtnRange.add(seed);

        LandMapTile tile, prevTile = seed;
        int prevDir = -1;
        while (sizeLeft > 0 && prevTile != null) {
            // If somehow we're a lone island in the water although it should
            // not be possible, just break out.
            if (prevTile.surroundedByType(MapTileType.Water) == true) {
                break;
            }

            // If we backed ourselves into a corner, we select a random
            // tile from this mountain range until one works.
            loopCheck = 0;
            while (prevTile.surroundedByType(MapTileType.Mountain) == true) {
                int tileNum = Utils.number(0, mtnRange.size()-1);
                prevTile = mtnRange.get(tileNum);
                loopCheck++;
                if (loopCheck > 200) {
                    Utils.log("loopCheck passed 200 tries in buildMountainFromSeed");
                    prevTile = null;
                    break;
                }
            }

            // Get our next tile.
            int dir = Utils.number(0, 3);
            tile = chooseNextMountainTile(prevTile, dir, prevDir);

            // If it's a mountain, try again.  It should not have to be,
            // because we checked earlier to make sure our tile wasn't surrounded
            // by mountains so there must be some valid direction to go.
            while (tile != null && tile.type == MapTileType.Mountain) {
                dir = Utils.number(0, 3);
                tile = chooseNextMountainTile(prevTile, dir, prevDir);
            }

            if (tile != null) {
                tile.type = MapTileType.Mountain;
                mtnRange.add(tile);
            }
            // Get a new random direction but not the way we came in
            /*int dir = Utils.number(0, 3);
            while (LandMapTile.isRevDir(prevDir, dir) == true) {
                dir = Utils.number(0, 3);
            }

            // Get the tile for the direction we chose.
            tile = lastTile.getAdjacentTileForNumber(dir);

            // Make sure the tile is a mountainable tile, if not, just break
            // out at this point.  That's fine, just increases the randomness.
            if (tile == null || 
                tile.type == MapTileType.Water) {
                break;
            }*/

            // If we've chosen another mountain, that's not what we want,
            // choose something else.
            prevDir = dir;
            prevTile = tile;
            sizeLeft--;
        }
        /*int dist = 0;
        for (LandMapTile tile : tilesArray) {
            if (tile == seed)
                continue;
            if (tile.type == MapTileType.Water)
                continue;
            dist = getDistance(seed, tile);
            if (dist >= size)
                continue;
            //num++;
            //Utils.log(num);
            double perc = ((double)size - (double)(size - dist)) / (double)size;
            int newPerc = 100 - ((int)(perc*100));
            if (Utils.number(1, 100) <= newPerc) {
                tile.type = MapTileType.Mountain;
            }
        }*/

        mtnRange.clear();
    }

    private void generateMountains() {
        // Let's set up some mountain ranges!

        // Mountain ranges will have a direction they tend to expand
        // in, and in that direction they have a higher probability of 
        // expanding.
        
        // Small ranges
        int numSeeds = Utils.number(20, 30);
        int size = Utils.number(3, 30);
        while (numSeeds > 0) {
            size = Utils.number(3, 30);
            buildMountainFromSeed(getRandomLandTile(), size);
            numSeeds--;
        }

        // Large ranges
        numSeeds = Utils.number(15, 20);
        size = Utils.number(50, 100);
        while (numSeeds > 0) {
            size = Utils.number(50, 100);
            buildMountainFromSeed(getRandomLandTile(), size);
            numSeeds--;
        }

        // Monster ranges
        numSeeds = Utils.number(10, 15);
        size = Utils.number(120, 200);
        while (numSeeds > 0) {
            size = Utils.number(120, 200);
            buildMountainFromSeed(getRandomLandTile(), size);
            numSeeds--;
        }
    }

    private void generateHills() {
        // Set up some hills, generally bordering mountains.
    }

    private void generateLakes() {

    }

    private void generateRivers() {

    }

    private void generateEnvironment() {
        // Set up swamps, deserts.

    }

    private void buildFieldFromSeed(LandMapTile seed) {
        // Seed should generally be a town tile.
        int size = 0;
        switch (seed.type) {
            case Village:   size = 2; break;
            case Town:      size = 3; break;
            case City:      size = 4; break;
            case BigCity:   size = 5; break;
            case Capital:   size = 6; break;
            default: return;
        }

        // I may want to reduce the footprint on this by going
        // out just from each seed.
        int dist = 0;
        for (LandMapTile tile : tilesArray) {
            if (tile == seed)
                continue;
            if (tile.type == MapTileType.Water || 
                tile.type == MapTileType.Mountain || 
                tile.isPopulated())
                continue;
            dist = getDistance(seed, tile);
            if (dist >= size)
                continue;
            //num++;
            //Utils.log(num);
            double perc = ((double)size - (double)(size - dist)) / (double)size;
            int newPerc = 100 - ((int)(perc*100));
            newPerc /= 2;
            if (Utils.number(1, 100) <= newPerc) {
                tile.type = MapTileType.Field;
            }
        }
    }

    private void generateFields() {
        for (LandMapTile tile : tilesArray) {
            if (tile.isPopulated()) {
                buildFieldFromSeed(tile);
            }
        }
    }

   /* private int numVillages = 0;
    private int numTowns = 0;
    private int numCities = 0;
    private int numBigCities = 0;*/

    private void placeTileOnValidLand(MapTileType tileType) {
        LandMapTile tile = getRandomLandTile();

        // Really if these are cities and towns they should not overwrite each other,
        // especially the big cities, though that could be interesting I guess, just for
        // added randomness.  But it would suck if the capital or a big city got overwritten
        // by a village or some crap.
        while (tile.surroundedByType(MapTileType.Mountain) || 
               tile.isPopulated()) {
            tile = getRandomLandTile();
        }

        tile.type = tileType;
    }

    private void placeCityTileOnValidLand(int pop) {
        MapTileType tileType = MapTileType.Village;
        Constants.TownSize size = Constants.TownSize.sizeForPopulation(pop);
        switch (size) {
            case SMALL:  tileType = MapTileType.Village;
                break;
            case MEDIUM: tileType = MapTileType.Town;
                break;
            case LARGE:  tileType = MapTileType.City;
                break;
            case HUGE:   tileType = MapTileType.BigCity;
                break;
        }
        placeTileOnValidLand(tileType);
        /*if (pop >= 12000) {
            placeCityTileOnValidLand(MapTileType.BigCity);
        }
        else if (pop >= 8000) {
            placeCityTileOnValidLand(MapTileType.City);
        } 
        else if (pop >= 1000) {
            placeCityTileOnValidLand(MapTileType.Town);
        }
        else {
            placeCityTileOnValidLand(MapTileType.Village);
        }*/
    }

    private void placeCityTileOnValidLand(double pop) {
        placeCityTileOnValidLand((int)pop);
    }

    private void generateCountry() {
        // Generate the population, the major cities,
        // towns, villages, etc.  This will have to be
        // more extensive later when we figure out what
        // should actually be in the towns and such.
        // The towns will need their own generator.

        // Potentially a town or city within x distance from a coast
        // will have a port.  From a port you can take a boat.

        // Village, town, city, bigcity, capital.

        // Determine how many of each type we have.  We will use
        // Demographics Made Easy as the model.

        // We might actually want to do more than one country if
        // there are sufficient large land masses.  For now we will
        // keep it simple and do just one.
        
        // So we set our population first, to 5 million.  Of course
        // in this sim there won't be that many people but it's just
        // a way to determine what's what.
        int pop = Utils.number(5000000, 7000000);
        int miles = 80000; // We are going to claim 80k square miles.

        // People per square mile.  I could adjust this based on the type
        // of terrain too.
        int density = Utils.number(30, 120);

        // Determine population of largest city.  We are going to call that
        // the capital though that doesn't have to be the case.  There should be
        // a chance for some other city to be larger potentially.

        /*  First, determine the population of the largest city in the kingdom. 
        This is equal to (P times M), where P is equal to the square root of 
        the country's population, and M is equal to a random roll of 2d4+10 
        (the average roll is 15).  */

        double p = Math.sqrt(pop);
        double m = 10 + Utils.number(2, 8); 
        double curPop = p * m;
        // Generate biggest city.
        placeTileOnValidLand(MapTileType.Capital);
        pop -= curPop;
        
        Utils.log("Capital: " + curPop);

        /* The second-ranking city will be from 20-80% the size of the largest city. 
        To randomly determine this, roll 2d4 times 10% (the average result is 50%) */
        double perc = Utils.number(20, 80);
        curPop = curPop * (perc/100);
        // Generate second biggest city.
        placeCityTileOnValidLand(curPop);
        pop -= curPop;

        Utils.log("Second: " + curPop);

        /* Each remaining city will be from 10% to 40% smaller than the previous one 
        (2d4 times 5% - the average result is 25%); continue listing cities for as 
        long as the results maintain a city-scaled population (8,000 or more). */
        int numCities = 2;  // We have the two largest already
        while (curPop >= 8000) {
            perc = Utils.number(10, 40);
            curPop -= (curPop * (perc/100));
            Utils.log("City: " + curPop);
            placeCityTileOnValidLand(curPop);
            pop -= curPop;
            numCities++;
        }
        //Utils.log("Cities: " + numCities);

        /* To determine the number of towns, start with the number of cities, and 
        multiply it by a roll of 2d8 (the average result is 9). */
        int numTowns = numCities * Utils.number(2, 4);
        int numVillages = numTowns * Utils.number(2, 4);
        Utils.log("Towns: " + numTowns);
        Utils.log("Villages: " + numVillages);

        while (numTowns >= 0) {
            pop -= Utils.number(1000, 8000);
            placeTileOnValidLand(MapTileType.Town);
            numTowns--;
        }

        // Now whatever is left is all villages.
        //int numVillages = 0;

        // This is far too many for this sim, let's just base it on
        // the number of towns.
        while (numVillages >= 0) {
            pop -= Utils.number(200, 1000);
            placeTileOnValidLand(MapTileType.Village);
            placeTileOnValidLand(MapTileType.Town);
            numVillages--;
        }
        /*while (pop >= 0) {
            numVillages++;
            pop -= Utils.number(200, 1000);
            placeCityTileOnValidLand(MapTileType.Village);
        }*/

        // Now place fields around towns and cities in greater density.
        generateFields();
    }

    private void generateOneDun() {
        LandMapTile tile = getRandomLandTile();

        // Make sure it isnt in water or surrounded by mountains
        // although we could potentially put stuff in mountain ranges
        // if we allow people to get through mountains somehow.
        while (tile.surroundedByType(MapTileType.Mountain)) {
            tile = getRandomLandTile();
        }

        // If it's a mountain or bordered by them, good chance to
        // be a cave or a mine
        if ((tile.type == MapTileType.Mountain) ||
            (tile.numberAdjacentTilesOfType(MapTileType.Mountain) > 0)) {
            if (Utils.number(0, 4) > 1) {
                if (Utils.pass(50) == true)
                    tile.type = MapTileType.Cave;
                else
                    tile.type = MapTileType.Mine;
            }
            else {
                if (Utils.pass(50) == true)
                    tile.type = MapTileType.Tower;
                else
                    tile.type = MapTileType.Ruin;
            }
        }
        else {
            // If not a mountain, 20% chance to be a cave or mine
            if (Utils.pass(20) == true) {
                if (Utils.pass(50) == true)
                    tile.type = MapTileType.Cave;
                else
                    tile.type = MapTileType.Mine;
            }
            else {
                if (Utils.pass(50) == true)
                    tile.type = MapTileType.Tower;
                else
                    tile.type = MapTileType.Ruin;
            }
        }
    }

    private void generateDuns() {
        // Now just throw shit everywhere, caves tend to be
        // on the edges of mountains and other terrain,
        // towers and ruined castles can be anywhere.

        // Caves, mines, towers, ruins.

        // Basically we want to average one dungeon for
        // every 10 x 10 area or 100 squares, or maybe less (more dungeons).
        int numDuns = (numXTiles * numYTiles) / 100;
        Utils.log("Dungeons: " + numDuns);
        while (numDuns > 0) {
            generateOneDun();
            numDuns--;
        }
    }

    public LandRegion getRegionById(int id) {
        for (LandRegion reg : regions) {
            if (reg.getId() == id) {
                return reg;
            }
        }
        return null;
    }

    private void pruneRegions() {
        // If any regions have no tiles left in them, just remove them - this
        // can happen if other regions completely overlap them.
        for (int i = regions.size()-1; i >= 0; i--) {
            LandRegion reg = regions.get(i);
            if (reg.size() <= 0) {
                regions.remove(reg);
            }
        }
    }

    private LandRegion createLandRegion(LandMapTile seed) {
        return createLandRegion(seed, 0);
    }

    private LandRegion createLandRegion(LandMapTile seed, int level) {
        int base = level;
        // Level 0 is a random level
        if (base == 0) {
            base = Utils.number(1, 1000);
        }

        LandRegion reg = new LandRegion(base);
        regions.add(reg);
        reg.addTile(seed);

        // Now fan out and add more to this region.  Pick a random
        // tile in the region, and add a random adjacent tile to this
        // region.
        int num = 0;
        int numTiles = Utils.number(50, 300);
        LandMapTile tile = reg.getRandomTileBeyondEdge();
        while (tile != null && num < numTiles) {
            reg.addTile(tile);
            tile = reg.getRandomTileBeyondEdge();
            num++;
        }

        return reg;
    }

    public LandRegion getHighestLevelRegion() {
        if (regions == null || regions.size() <= 0) {
            Utils.log("Error - no or 0 size regions array in getHighestLevelRegion");
            return null;
        }
        LandRegion highestReg = regions.get(0);
        for (LandRegion reg : regions) {
            if (reg.size() < 10) {
                // If it's too small, don't pick it as the end region.
                continue;
            }
            if (reg.getLevel() > highestReg.getLevel()) {
                highestReg = reg;
            }
        }
        return highestReg;
    }

    // Assign the final area region and surrounding regions
        // I had it crash on this function before, saying array size was 0
    private void assignEndRegion() {
        // The highest level one becomes the master evil region, and we top it out.
        LandRegion reg = getHighestLevelRegion();
        if (reg == null || reg.size() <= 0) {
            Utils.log("Error - no or 0 size LandRegion in assignEndRegion");
            return;
        }

        // But now we slap a region on top of this one just in case it's only like
        // one room or something.
        LandMapTile tile = reg.get(0);
        m_lrEndRegion = createLandRegion(tile, 1000);
        m_lrEndRegion.makeFixedLevel();

        // Then we make all bordering regions almost as hard, if they aren't.
        // Nah, for now I won't do that, it's just the master region, could be
        // bordered by the starting region in fact.
        //if (region != getStartingRegion()
    }

    private int numTilesInRegions() {
        int num = 0;
        for (LandRegion reg : regions) {
            num += reg.size();
        }
        return num;
    }

    private void generateRegions() {

        // Regions should generally stop at mountain ranges, rivers, etc.  But for now
        // they're just random.

        // Now create other regions at varying levels
        // Let's create a random number of regions, based on the number of tiles in
        // the map.
        int numTiles = numXTiles * numYTiles;
        int min = numTiles / 400;
        int max = numTiles / 100;
        int numRegions = Utils.number(min, max); 
        int num = 0;
        while (num < numRegions) {
            createLandRegion(getRandomTile());
            num++;
        }
        
        // Now we should just keep creating regions until all tiles are assigned
        // to one.  That'll get us more variation too.  Basically all regions'
        // sizes should add up to the total number of tiles in theory.  If they
        // don't, then we still have tiles that aren't assigned.  We'll get within
        // 500 tiles of the max, then call that good enough.
        while (numTilesInRegions() + 500 < numTiles) {
            createLandRegion(getRandomTileWithoutRegion());
        }

        // Now actually assign our starting town, this region will be starting level,
        // we overwrite whatever region is there already
        m_lmtStartingTownTile = getRandomPopulatedTile();
        LandRegion startRegion = createLandRegion(m_lmtStartingTownTile, 1);
        startRegion.makeFixedLevel();

        // Now prune any regions that got eaten up completely by others so we don't
        // have regions with no tiles
        pruneRegions();
        Utils.log("Regions: " + regions.size());

        // Assign the master evil region and its surroundings
        assignEndRegion();

        // And now we just comb all the tiles until everything is assigned to a region.
        // Most are already assigned when we created the regions earlier, but we have some
        // orphan tiles because we stopped creating regions when we were within 500 tiles
        // of the max.  When we have created enough regions to house tiles within 500 of
        // the max, we assume we have enough regions then and we don't need to keep creating
        // more for that last bit.  So we go through them now, and assign those orphan
        // tiles based on proximity to existing regions.
        // Basically for each pass, if a tile is not assigned to a region and is
        // adjacent to a region, add it to that region.  And keep going until everything 
        // is assigned.  This creates some kinda stratified effects if used on lots of tiles 
        // but we should't have more than 500 tiles unassigned at this point out of 40,000.
        boolean notFinished = true;
        while (notFinished == true) {
            notFinished = false;
            for (LandMapTile tile : tilesArray) {
                if (tile.getRegion() == null) {
                    notFinished = true;
                    tile.assignToAdjacentRegion();
                }
            }
        }

        // Now we are done creating and pruning regions, so they are fixed (unless we
        // decide to change something during gameplay).  So, set up all the adjacent
        // region lists so we don't have to figure this out every time.
        updateAllAdjacentRegions();

        // Now go through and smooth out region levels appropriately based on a probability
        // function that is based on the level of adjacent regions.  This gives us a higher
        // probability that regions near each other gradually increase in level rather than
        // the player suddenly wandering from a 5th level region into a 650th level region 
        // and getting instantly slanked.  To keep it interesting, we don't touch our level
        // 1 region or our level 1000 (master evil / end region).  Also, we choose some
        // "untouchable" regions at random that do not touch any other region to help with
        // our smoothing process
        //setRandomRegionsToFixedLevel();

        // Now create a path from start to end of appropriately scaled leveling.  So 
        // there is at least one in the game that can be used
        createLevelingPath();
    }

    private void createLevelingPath() {
        ArrayList<LandRegion> path = findPathFrom(getStartingRegion(), m_lrEndRegion, 20);

        if (path == null) {
            Utils.log("createLevelingPath: Level path creation failed");
            Utils.log("Remaking world (NOT IMPLEMENTED YET SO YOU ARE SCREWED!  Region levels will vary wildly)");
            return;
        }

        // So, now that we have path, it's an ordered list, the first element of which is adjacent
        // region to the starting region and the last element is the end (level 1000) region.  First
        // let's just pop off that end region.
        path.remove(path.size()-1);

        // Now divide by the size to find our level scaling difference.
        int levelDiff = 1000 / path.size();

        // Now starting from the first element to the last, add levelDiff to each one's level to create a
        // leveling path from start to end region 
        int level = 1;
        for (LandRegion reg : path) {
            // Make sure this won't be changed by any future manipulation of area levels
            reg.makeFixedLevel();
    
            // Add level diff with a little variation, hopefully it won't stack too much but whatever, the
            // players will figure out a way to manage at higher levels anyway
            level += levelDiff;
            level += Utils.number(-1*(levelDiff/3), levelDiff/3);

            // Can't go beyond 1000
            if (level > 1000) {
                level = 1000;
            }

            reg.setLevel(level);
        }

        // TODO - Now I have to bulldoze the path so that it's actually possible to go between regions.
        // When it's not, like a mountain range or deep water is in the way, i need to either break a path
        // in the mountains, or take the "easy way" and create a dungeon with an exit between the regions.
        // Preferably i create a dungeon but allow players to break their own paths through the mountains
        // or build bridges over water with proper tools.
        //   NOT DONE - create random dungeon with exit that goes out to each region; in such cases,
        //   monster level randomizes between both regions

        // And log the completion
        Utils.log("Leveling path created: includes " + path.size() + " regions");
    }

    private ArrayList<LandRegion> buildOnePath(ArrayList<LandRegion> path, LandRegion current, LandRegion end, int minSteps) {
        if (current == null || end == null) {
            return path;
        }

        ArrayList<LandRegion> adjRegions = current.getAdjacentRegions();
        if (adjRegions == null) {
            return path;
        }

        // Check if end region is in our adjacent regions - if so, we're done, but only
        // if we have enough steps
        if (path.size() >= minSteps) {
            for (LandRegion reg : adjRegions) {
                if (reg.getId() == end.getId()) {
                    path.add(end);
                    return path;
                }
            }
        }

        // Clone it
        ArrayList<LandRegion> shuffledList = (ArrayList<LandRegion>)adjRegions.clone();

        // Shuffle the clone
        Collections.shuffle(shuffledList);

        // Go through shuffled list
        for (LandRegion reg : shuffledList) {
            // If current path already contains it, don't use it
            if (path.contains(reg) == true)
                continue;

            // Don't use it if it's the starting area, obviously
            if (reg.getId() == getStartingRegion().getId() == true)
                continue;

            // Also don't use it if it's the end region; we check for that above
            if (reg.getId() == m_lrEndRegion.getId() == true)
                continue;

            // OK it's a valid new path, use it
            path.add(reg);
            return buildOnePath(path, reg, end, minSteps);
        }

        // Something went wrong so just return what we have.
        return path;
    }

    private ArrayList<LandRegion> findPathFrom(LandRegion start, LandRegion end, int minSteps) {
        int numTries = 0;
        int maxTries = 20000; // We'll try a few thousand times.

        ArrayList<LandRegion> path = null;
        for (numTries = 0; numTries < maxTries; numTries++) {
            // Find a path from start to end region that is at least minSteps long.
            path = buildOnePath(new ArrayList<LandRegion>(), start, end, minSteps);

            // Something went wrong, try again
            if (path == null) {
                continue;
            }
            
            // Didn't make it to the end, try again
            if (path.contains(end) == false){
                path.clear();
                path = null;
                continue;
            }

            // Too short, try again
            if (path.size() < minSteps) {
                path.clear();
                path = null;
                continue;
            }

            // We found a good one!
            break;
        }

        // Should just be null if something went wrong but we'll check other conditions anyway
        if (path == null || path.contains(end) == false || path.size() < minSteps) {
            Utils.log("findPathFrom:  Unable to find " + minSteps + " path from region ID: " + start.getId() + ", Level: " + start.getLevel() + " to " +
                "region ID: " + end.getId() + ", Level: " + end.getLevel());
        }

        return path;
    }

    public void setRandomRegionsToFixedLevel() {
        // Let's do it as a function of how many regions we have.  In a typical scenario
        // the way the game was originally set up, we usually have 600+ regions.  Let's
        // try to make 10% of those fixed.  Then we smooth out all the in betweens.
        int num = regions.size() / 10;
        for (int i = 0; i < num; i++) {
            setRandomRegionToFixedLevel();
        }
    }

    public void setRandomRegionToFixedLevel() {
        // Find a random region in the world that:
        //   1:  Is not already fixed level
        //   2:  Is not adjacent to a fixed-level region

        // And make it fixed level.
        int loopCheck = 0;

        // Set our rules
        boolean notAdjacent = true;
        LandRegion reg = null;
        while (reg == null) {
            loopCheck++;
            if (loopCheck > 200) {
                Utils.log("loopCheck passed 200 tries in setRandomRegionToFixedLevel");
                // It looped too many times, just let it be adjacent
                notAdjacent = false;
            }

            reg = getRandomRegion();
            if (reg.isFixedLevel() == true) {
                continue;
            }
            if (notAdjacent == true) {
                ArrayList<LandRegion> regs = reg.getAdjacentRegions();
                if (regs == null) {
                    continue;
                }
                for (LandRegion adjRegion : regs) {
                    // Found an adjacent fixed level region; abort!
                    if (adjRegion.isFixedLevel() == true) {
                        reg = null;
                        break;
                    }
                }
            }
        }

        if (reg != null) {
            reg.makeFixedLevel();
        }
    }

    public LandRegion getRandomRegion() {
        int index = Utils.number(0, regions.size()-1);
        return regions.get(index);
    }

    // Each region maintains a list of its own adjacent regions for convenience
    // We would need to update this if we ever changed the surface area of a region
    // or removed it
    public void updateAllAdjacentRegions() {
        // Go through every region and set/update all of its adjacent regions
        for (LandRegion reg : regions) {
            reg.updateAdjacentRegions();
        }
    }

    private void setAreaLevels() {
        // Now we go through and set areas to certain levels,
        // including duns.  We should probably start by selecting
        // the master dun and making it a really high level and
        // then extending a high level area around it, and then 
        // pick some other various focus areas for higher levels
        // and range them out from there.  Then at the very end
        // go through and make sure the town where the party starts
        // is at low level so they can survive.  Or when we pick
        // the starting town, just make sure we pick one in a low
        // level area and if such a place doesn't exist then we create
        // one.

        // And the final battles should be really really difficult, like
        // the enemy should have a ton of guys, like 20 or 40, maybe the
        // max player party size is 20 but the enemy gets even double that,
        // and they are top level and it's just really tough.  Or they have
        // a variety of levels like the Disgaea court.


        // Now, we have all the mons that can occur throughout the game.  Now we
        // have to assign some sort of leveled regions to different areas, and for
        // each leveled region, we randomly throw on a certain number of different
        // mons into that region that are of the appropriate level.

        // In this way, we create random, leveled regions throughout the world, 
        // each of which contains specific mons that vary for each different game
        // created.

        // Leveled regions increase or decrease on a probability scale; it's more
        // likely that regions decrease on a gradual scale.  
        
        // First, we randomly create some low level regions to start with.  Then
        // we probabalistically fan out from there, creating other regions as we go.


        // So, now we have a bunch of regions set up.  But we don't know what levels
        // we want them to be.  We probably want some kind of graduating scale,
        // where you don't have super high level regions bordering low level ones,
        // because that can be kind of boring and frustrating.  You could be like 
        // surrounded by uber enemies with no way out.  Something like that could be
        // cool I guess but in general it seems kind of lame.  So we will try to even
        // out the levels a bit so that you can kind of naturally progress from the 
        // beginning to wherever, and areas just around towns should always/often be
        // pretty low level so people can raise new hires and such.

        // I think high level regions are just going to have to be more rare, it does
        // kind of make sense - most places should be relatively safe, but some places
        // are extremely dangerous.  Also levels of NPCs should have probabilities based on
        // the size of cities they spawn in.  So larger cities should spawn more higher
        // level NPCs than small villages.
    }

    private void randomizeTerrain() {
        
        // We need a threshold of land mass since that's where 
        // most of the game actually takes place
        double landMassRequired = 0.65;

        generateContinents();
        while (landToWaterRatio() < landMassRequired) {
            generateContinents();
        }

        generateForests();
        generateMountains();
        generateHills();
        generateLakes();
        generateRivers();
        generateCountry();
        generateEnvironment();
        generateDuns();
        generateRegions();
        setAreaLevels();
    }

    @Override
    public void onLeftClick(double x, double y) {
        LandMapTile tile = (LandMapTile)getTileForClick(x, y);
        if (tile != null) {
            onLeftClickTile(tile);
        }
    }

    @Override
    public void onRightClick(double x, double y) {
        LandMapTile tile = (LandMapTile)getTileForClick(x, y);
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
        for (LandMapTile tile : tilesArray) {
            tile.draw(gc);
        }

        // Draw the avatar
        avatar.draw(gc);
    }
}