package dfsim;

import java.util.List;
import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import javafx.beans.property.*;

import javafx.scene.canvas.*;

import dfsim.gui.*;

// So this is meant to be the overland map.
public class DunMap extends DfSquareMap {

    public ArrayList<DunMapTile> tilesArray;
    public DunMapTile[][] tiles;

    public DunMapAvatar avatar;
    public ArrayList<DunMapMon> mons;

    public boolean isInitialized() { return tilesArray != null; }

    public DunMapTile exit;

    // How many rooms, I guess, or just a random
    // measure for how big the dun should be.
    int size = 0;

    public enum TileType {
        Blank,
        Water,
        Floor,
        Wall,
        Stairs,
        Chest,
        EmptyChest,
        Exit
    }

    public DunMap(int theSize, boolean generate) { 
        super();
        size = theSize;
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
        if (mons != null) {
            mons.clear();
            mons = null;
        }
    }

    public void regen() {
        clear();
        tilesArray = new ArrayList<DunMapTile>();
        mons = new ArrayList<DunMapMon>();
        tiles = new DunMapTile[numXTiles][numYTiles];

        // And create it
        DunMapTile tile;
        for (int y = 0; y < numYTiles; y++) {
            for (int x = 0; x < numXTiles; x++) {
                tile = new DunMapTile(this);
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

        avatar = new DunMapAvatar(this);
        tilesArray.get(0).attach(avatar);

        if (pane != null) {
            addToPane(pane);
        }
        moveTilesToCorrectPositions();
    }


    public void start() {
        start(Constants.Dir.fromInt(Utils.number(0, 3)));
    }

    public void start(Constants.Dir dir) {
        avatar.setPerson(Data.personList.get(0));
        // Basically, put the avatar in the middle of the screen,
        // center the exit on the avatar.
        if (exit == null) {
            // Something is wrong
            Utils.log("DunMap start: null exit!  Setting exit randomly.");
            // Give it a random tile; this could be weird but at least it
            // will work better than starting off in a blank space.
            exit = getFirstTileOfType(TileType.Floor);
        }
        avatar.unrestrictedMoveTo(DfSim.dunMapScreen.wid/2, DfSim.dunMapScreen.hgt/2);
        exit.attach(avatar);
        centerOnEntity(avatar, false);
    }

    public void updateOneFrame() {
        for (DunMapMon mon : mons) {
            // See what happens with this mon.
            if (mon.decWait(Utils.number(500/60, 2000/60)) == true) {
                moveMon(mon);
            }
        }
    }

    public void generateEncounter(DunMapMon mon) {
        if (mon == null) {
            return;
        }
        // From the mon, generate a mon list.  Maybe this is semi-random?
        // or maybe it's predetermined?
        ArrayList<DfMon> mons = new ArrayList<DfMon>();

        mons.add(Data.getDfMonByName("Padfoot"));
        mons.add(Data.getDfMonByName("Forest Imp"));
        mons.add(Data.getDfMonByName("Dark Wolf"));

        // Make an army of stag beetles
        for (int i = 0; i < Utils.number(3, 6); i++) {
            mons.add(Data.getDfMonByName("Stag Beetle"));
        }

        DfSim.sim.startEncounter(Data.personList, mons);
        mons.clear();
        mons = null;
    }

    public void moveMon(DunMapMon mon) {
        if (mon.canMove() == false)
            return;
        DunMapTile tile = mon.getTile();
        Constants.Dir dir = tile.getRandomValidDir();
        DunMapTile moveTile = (DunMapTile)tile.getAdjacentTileInDir(dir);
        // If adjacent tile has player, it becomes a fight.
        if (avatarOnThisTile(moveTile) == true) {
            generateEncounter(mon);
        }
        else {
            while (mon.canMoveToTile(moveTile) == false) {
                dir = tile.getRandomValidDir();
                moveTile = (DunMapTile)tile.getAdjacentTileInDir(dir);
            }
            if (moveTile != null) {
                mon.moveTo(moveTile, dir, true);
            }
        /*DunMapTile moveTile = tile.getRandomValidAdjacentTile();
            while (mon.canMoveToTile(moveTile) == false) {
                moveTile = tile.getRandomValidAdjacentTile();
            }
            if (moveTile != null) {
                mon.moveTo(moveTile, true);
            }*/
        }
    }

    public DunMapTile getAt(int x, int y) {
        if (x < 0 || y < 0 || x >= numXTiles || y >= numYTiles)
            return null;
        return tiles[x][y];
    }

    public void updateColors() {
        for (DunMapTile tile : tilesArray) {
            tile.updateColor();
        }
    }

    public void moveTilesToCorrectPositions() {
        int xShift = 2;
        int yShift = 2;

        // Add and move them
        for (int y = 0; y < numYTiles; y++) {
            for (int x = 0; x < numXTiles; x++) {
                DunMapTile tile = getAt(x, y);
                //tile.addToPane(pane);
                double xPos = tile.mapX * (tile.getWidth());
                double yPos = tile.mapY * (tile.getWidth());
                xPos += xShift;
                yPos += yShift;
                tile.moveTo(xPos, yPos);
            }
        }
        for (DunMapMon mon : mons) {
            mon.moveTo(mon.getTile(), Constants.Dir.SOUTH);
        }
    }
    
    public void addToPane(Pane thePane) {
        pane = thePane;

        // Add and move the
        for (DunMapTile tile : tilesArray) {
            tile.addToPane(pane);
        }
        avatar.addToPane(pane);
        for (DunMapMon mon : mons) {
            mon.addToPane(pane);
        }
    }

    public void removeFromPane() {
        if (pane == null)
            return;
        if (avatar != null) {
            avatar.removeFromPane(pane);
        }
        if (tilesArray != null) {
            for (DunMapTile tile : tilesArray) {
                tile.removeFromPane(pane);
            }
        }
        if (mons != null) {
            for (DunMapMon mon : mons) {
                mon.removeFromPane(pane);
            }
        }
    }
    
    /*public void addToPane(Pane thePane) {
        pane = thePane;
        int xShift = 2;
        int yShift = 2;

        // Add and move them
        for (int y = 0; y < numYTiles; y++) {
            for (int x = 0; x < numXTiles; x++) {
                DunMapTile tile = getAt(x, y);
                tile.addToPane(pane);

                double xPos = tile.mapX * (tile.getWidth());
                double yPos = tile.mapY * (tile.getWidth());
                xPos += xShift;
                yPos += yShift;
                tile.moveTo(xPos, yPos);
            }
        }
        avatar.addToPane(pane);

        for (DunMapMon mon : mons) {
            mon.moveTo(mon.getTile());
            mon.addToPane(pane);
        }
    }*/

    @Override
    protected void updateVisible() {
        if (avatar == null) {
            for (DunMapTile tile : tilesArray) {
                tile.setVisible(true);
            }
            return;
        }

        // Only nodes within a certain distance of the main dude are visible.
        for (DunMapTile tile : tilesArray) {
            if (getDistance(tile, avatar.getTile()) < 35)
                tile.setVisible(true);
            else
                tile.setVisible(false);
        }
    }
    
    @Override
    protected void onMoveFinished() {
        super.onMoveFinished();
        DunMapTile tile = avatar.getTile();
        if (tile.getType() == TileType.Exit) {
            DfSim.showLandMapScreen();
        }
        else {
            DunMapMon mon = monOnThisTile(tile);
            if (mon != null) {
                generateEncounter(mon);
            }
        }
    }

    /*public void moveAvatarToTile(DunMapTile moveTile) {
        if (moveTile != null && avatar != null &&
            moveTile.canBeMovedTo(avatar) == true) {
            moveTile.attach(avatar);
        }
        centerOnAvatar();

        // Now, was it a town or a dun?
        // Wish I could have it update the screen before entering
        // the new screen so you could see the avatar step on top of
        // the tile.
        //if (moveTile.getType() == TileType.Exit) {
          //  DfSim.showLandMapScreen();
       // }
    }
    
    public void centerOnAvatar() {
        centerOnAvatar(true);
    }

    public void centerOnAvatar(boolean playAnimation) {
        updateVisible();
        if (avatar == null) {
            return;
        }

        centerOnTile(avatar.getTile(), (int)avatar.getX(), (int)avatar.getY(), 
            DunMapMon.posOffset, playAnimation);
    }*/

    /*public void centerOnAvatar() {
        updateVisible();
        if (avatar == null) {
            return;
        }

        DunMapTile tile = avatar.getTile();
        if (tile == null) {
            return;
        }
            
        playMoveAnimation(
            -1 * ((int)tile.getX() - (int)avatar.getX() + DunMapMon.posOffset),
            -1 * ((int)tile.getY() - (int)avatar.getY() + DunMapMon.posOffset)
        );

        //xOffset.set(-1 * ((int)tile.getX() - (int)avatar.getX() + DunMapMon.posOffset)); // - tile.defSize/2));
        //yOffset.set(-1 * ((int)tile.getY() - (int)avatar.getY() + DunMapMon.posOffset)); // - tile.defSize/2));
    }*/

    private boolean interactWithChest(DunMapTile tile) {
        if (tile.getType() == TileType.Chest) {
            // Find something.
            if (Utils.pass() == true) {
                int amt = Utils.number(10, 100);
                Utils.log("Interacted with a chest, got .. " + amt + " gold");
                Data.money += amt;
            }
            else {
                DfEqItem item = Data.generateRandomEqItem();
                Utils.log("Interacted with a chest, got .. " + item.getName());
                Data.personList.addToInv(item);
            }
            tile.setType(TileType.EmptyChest);
            updateColors();
            return true;
        }

        return false;
    }
    
    // Interact with whatever you are facing.
    private void interact() {
        // First see if we are on something we can interact
        // with.
        DunMapTile tile = (DunMapTile)avatar.getTile();
        if (interactWithChest(tile) == true)
            return;
        
        tile = (DunMapTile)avatar.getTileFacing();
        // If it's a treasure or contains someone, interact.
        DunMapEntity ent = tile.getContains();
        if (ent != null) {
            if (ent.getType() == DfSquareMapEntity.EntityType.Mon) {
                DunMapMon mon = (DunMapMon)ent;
                Utils.log("Interacted with a mon");
                generateEncounter(mon);
                return;
            }
        }
        else {
            interactWithChest(tile);
        }
    }

    public void onF() {
        interact();
    }

    public void onSpace() {
        interact();
    }

    public void moveAvatarByInput(Constants.Dir dir) {
        DunMapTile moveTile = (DunMapTile)avatar.getTile().getAdjacentTileInDir(dir);

        if (moveTile != null) {
            // If adjacent tile has mon, it becomes a fight.
            DunMapMon mon = monOnThisTile(moveTile);
            if (mon != null) {
                generateEncounter(mon);
                return;
            }
        }
        
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
    
    public void onMouseEnterAvatar(DunMapAvatar ent) {

    }

    public void onLeftClickAvatar(DunMapAvatar ent) {

    }

    public void onRightClickAvatar(DunMapAvatar ent) {

    }

    public void onMouseEnterMon(DunMapMon ent) {

    }

    public void onLeftClickMon(DunMapMon ent) {

    }

    public void onRightClickMon(DunMapMon ent) {

    }
    
    public void onMouseEnterTile(DunMapTile tile) {

    }

    public void onLeftClickTile(DunMapTile tile) {
        //avatar.unrestrictedMoveTo(tile);
        tile.attach(avatar);
        centerOnEntity(avatar);
    }

    public void onRightClickTile(DunMapTile tile) {

    }

    public boolean hasAtLeastOneTileOfType(TileType type) {
        return (getFirstTileOfType(type) != null);
    }

    public DunMapTile getFirstTileOfType(TileType type) {
        if (tilesArray == null)
            return null;

        for (DunMapTile tile : tilesArray) {
            if (tile.getType() == type) {
                return tile;
            }
        }
        return null;
    }

    public DunMapTile getRandomTile() {
        return getAt(Utils.number(0, numXTiles-1), Utils.number(0, numYTiles-1));
    }

    public DunMapTile getRandomWaterTile() {
        DunMapTile tile = getRandomTile();
        while (tile.getType() != TileType.Water) {
             tile = getRandomTile();
        }
        return tile;
    }
    
    public DunMapTile getRandomLandTile() {
        DunMapTile tile = getRandomTile();
        while (tile.getType() == TileType.Water) {
             tile = getRandomTile();
        }
        return tile;
    }

    // One way to make this more efficient if we needed to, is to keep
    // ArrayLists of pointers to each of the types of tiles, and use those
    // to just choose random tiles in those.  More memory and maintenance though
    // especially if tiles change.  And these are only used when a dungeon is
    // first generated so it probably doesnt't matter too much.
    public DunMapTile getRandomTileOfType(TileType type) {
        int loopCheck = 0;

        DunMapTile tile = getRandomTile();
        while (tile.getType() != type) {
             tile = getRandomTile();
             // Let's hope we know we have a tile of this type
             // when we start, but just in case somehow we screw up and don't,
             // make sure this doesn't run forever.
             loopCheck++;
             if (loopCheck > 9999) {
                 Utils.log("Loopcheck passed 9999 loops in getRandomTileOfType");
                 return getFirstTileOfType(type);
             }
        }
        return tile;
    }
    
    // Update the north / southeast / etc directions for all tiles
    private void updateDirections() {
        for (DunMapTile tile : tilesArray) {
            tile.north = getAt(tile.mapX, tile.mapY-1);
            tile.east  = getAt(tile.mapX+1, tile.mapY);
            tile.west  = getAt(tile.mapX-1, tile.mapY);
            tile.south = getAt(tile.mapX, tile.mapY+1);
        }
    }

    public int getDistance(DunMapTile tile, DunMapTile tile2) {
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
    
    // For any function that uses the marked boolean on the tile, clear it out.
    // Kinda hokey but works.
    public void clearMarks() {
        for (DunMapTile tile : tilesArray) {
            tile.clearMark();
        }
    }

    // Reset any marked tile to previous.  Perhaps we were building some kind of
    // thing in the map and it turned out not to work so we want to revert and retry.
    public void revertMarkedTiles() {
        for (DunMapTile tile : tilesArray) {
            if (tile.isMarked() == true) {
                tile.revertType(); // Revert to previous type.
                tile.clearMark();
            }
        }
    }

    public boolean buildRoomFromSeed(DunMapTile seed, int min, int max, ArrayList<DunMapTile> curRoom) {
        //clearMarks();

        int len = Utils.number(min, max);
        int wid = Utils.number(min, max);

        // Randomly choose how far right and left from seed.
        int left = seed.mapX - Utils.number(0, len-1);
        int top = seed.mapY - Utils.number(0, wid-1);

        // If anything is off the map, redo it.  Really we should just
        // expand the map to accomodate it.  The map in a dun shouldn't
        // be a square or rectangle necessarily.
        if (left < 0 || top < 0 || 
            left + len >= numXTiles || top + wid >= numYTiles - 1) {
            return false;
        }

        // Then just go through those coordinates and make them all
        // part of the room as long as they exist (they might be
        // off the map)
        curRoom.clear();
        for (int x = 0; x < len; x++) {
            for (int y = 0; y < wid; y++) {
                DunMapTile tile = getAt(left + x, top + y);
                // Actually if a tile is null, redo this entire thing.
                if (tile == null)  {
                    //revertMarkedTiles();
                    return false;
                }

               // tile.mark();
                // Actually don't mark it as floor yet, first we just
                // create the room array with it, then if we are satisfied
                // we can set to floor at that point.
                //tile.setType(TileType.Floor);
                curRoom.add(tile);
            }
        }

        return true;
    }

    public DunMapTile findNextStep(DunMapTile from, DunMapTile to) {
        // Based on their coordinates, what's the next step?
        return from.getAdjacentTileForNumber(findNextStepDir(from, to));
    }
    
    public ArrayList<DunMapTile> getRandomPath(DunMapTile tile1, DunMapTile tile2) {
        ArrayList<DunMapTile> path = new ArrayList<DunMapTile>();
        // Meander totally at random until we hit tile2 from tile1.  Doesn't
        // matter what we hit in the middle.

        // This is ass, it has to be a single line path, like it can't be clustered
        // tiles.

        // Or at the least it has to like, have a higher chance at going in the direction
        // of the room as not.
        DunMapTile lastTile = tile1;
        DunMapTile next = tile1;
        while (next.equals(tile2) == false) {
            next = lastTile.getRandomValidAdjacentTile();
            path.add(next);
            lastTile = next;
        }
        
        return path;
    }

    public ArrayList<DunMapTile> getShortestPath(DunMapTile tile1, DunMapTile tile2) {
        ArrayList<DunMapTile> path = new ArrayList<DunMapTile>();
        
        // Ok so in this case we literally go directly from one tile to the other
        // in the shortest possible route.
        DunMapTile lastTile = tile1;
        DunMapTile next = tile1;
        while (next.equals(tile2) == false) {
            next = findNextStep(lastTile, tile2);
            path.add(next);
            lastTile = next;
        }
        
        return path;
    }

    public void makeShortestPath(DunMapTile tile1, DunMapTile tile2) {
        // We could theoretically just draw random shit until the two link up,
        // but that might get a little nutty.  Or we could do like, four
        // random paths between them, and then pick the shortest of those 4.
        // That way we'd get a lot of randomness but we'd narrow down the
        // end result so it wouldn't usually be insane.

        // Did not do that but I like that above idea for something.
            
        ArrayList<DunMapTile> shortestPath = getShortestPath(tile1, tile2);
        
        if (shortestPath == null || shortestPath.size() <= 0) {
            Utils.log("makePath: no path found!");
            //path.clear();
            if (shortestPath != null)
                shortestPath.clear();
            return;
        }

        //Utils.log("shortestPath: " + shortestPath.size());
        for (DunMapTile tile : shortestPath) {
            //tile.setType(TileType.Water);
            // Could consider setting these paths to a different
            // type just for fun graphically.
            tile.setType(TileType.Floor);
        }
        //path.clear();
        shortestPath.clear();
    }

    public boolean protoRoomIsConnected(ArrayList<DunMapTile> room) {
        // So it is assumed that this room was not set to floor tiles yet,
        // it's just been "chosen" for conversion to a room.
        // If it has any floor tiles in it, it's already connected to the rest
        // of the dun.
        for (DunMapTile tile : room) {
            if (tile.getType() == TileType.Floor) {
                return true;
            }
        }

        // So easy way to start this is, check the entire room and if any part
        // of the room is touching a tile that is outside of the room and is
        // a floor tile we are good.  Actually we only need to go around the
        // edge of the room but we don't do that right now.
        for (DunMapTile tile : room) {
            if (tile.n() != null && tile.n().getType() == TileType.Floor) {
                //if (room.contains(tile.north) == false) {
                    return true;
                //}
            }
            if (tile.s() != null && tile.s().getType() == TileType.Floor) {
                //if (room.contains(tile.south) == false) {
                    return true;
                //}
            }
            if (tile.e() != null && tile.e().getType() == TileType.Floor) {
                //if (room.contains(tile.east) == false) {
                    return true;
                //}
            }
            if (tile.w() != null && tile.w().getType() == TileType.Floor) {
                //if (room.contains(tile.west) == false) {
                    return true;
                //}
            }
        }
        return false;
    }

    // So now, hook this one room into the rest of the dun which is already
    // assumed to be hooked in since we hook as we go.
    // this is a PROTO ROOM which means it is NOT set to Floor tiles yet,
    // it is just an array of the tiles that we are going to set to floor.
    public void connectProtoRoom(ArrayList<DunMapTile> room) {
        if (protoRoomIsConnected(room) == true)
            return;

        // We now know the room is completely isolated.  It does not overlap
        // other floor area nor does it border any other floor area.  It is
        // an "island" 

        // Not connected, so find the closest spot to connect in, and connect.
        // What we will do is pick a random spot inside the room, just for
        // fun, and then pick the closest spot outside the room that connects.
        DunMapTile from = room.get(Utils.number(0, room.size()-1));

        // Get the first floor tile a iterate from there.
        DunMapTile closest = getFirstTileOfType(TileType.Floor);

        // Closest can be null like if this is the first room.
        if (closest == null)
            return;

        int dist = getDistance(from, closest);

        // We know room doesn't have any floor tiles yet because if it did
        // it would already be connected.  Therefore we can just look for the
        // closest floor tile.
        for (DunMapTile tile : tilesArray) {
            if (tile.getType() != TileType.Floor)
                continue;
            if (getDistance(from, tile) < dist) {
                dist = getDistance(from, tile);
                closest = tile;
            }
        }

        if (closest == null) {
            // Should be impossible
            Utils.log("connectProtoRoom: closest is null");
            return;
        }

        makeShortestPath(from, closest);
    }

    public void generateRooms() {
        // Choose seeds for rooms, we can just totally randomize this although
        // it could potentially depend on the type of dungeon.
        int num = Utils.number(3, 50);
        int min = 3;
        int max = 10;
        //ArrayList<DunMapTile> lastRoom = new ArrayList<DunMapTile>();
        ArrayList<DunMapTile> protoRoom = new ArrayList<DunMapTile>();
        while (num >= 0) {
            if (buildRoomFromSeed(getRandomLandTile(), min, max, protoRoom) == true) {
                num--;

                // Now connect them; in the future, I shouldn't automatically
                // do this, players should have some instances where they have to
                // figure out how to access parts of the duns in the best way
                // using various tools and such.
                //if (lastRoom.size() > 0) {
                  //  connectRooms(curRoom, lastRoom);
                //}
                //lastRoom.clear();
                //lastRoom.addAll(curRoom);

                // Now link it in with the rest of the dun
                connectProtoRoom(protoRoom);

                // Now, make it "part of the dungeon" by making it all
                // floor tiles.
                for (DunMapTile tile : protoRoom) {
                    tile.setType(TileType.Floor);
                }
            }
        }
        //lastRoom.clear();
        protoRoom.clear();
    }

    public boolean placeTreas() {
        DunMapTile tile = getRandomTileOfType(TileType.Floor);
        if (tile == null) {
            // No we did not succeed BUT we ran out of space for treasure!
            // So we still need to count down as if we placed treasure,
            // otherwise we will run forever.
            return true;
        }
        while (tile.isSingleFile()) {
            tile = getRandomTileOfType(TileType.Floor);
        }

        tile.setType(TileType.Chest);
        return true;
    }

    public void generateTreas() {
        // Generate treasure I guess totally at random, although theoretically
        // a bigger dungeon should get more of it, but maybe it's just not
        // like that at all.
        int num = Utils.number(1, 10);
        while (num >= 0) {
            if (placeTreas() == true) {
                num--;
            }
        }
    }

    /*public void generateHalls() {
        // Ok now how do we connect these things...

        // How do we define and locate a "room" - and then
        // how do we check to make sure that room is accessible
        // from all other rooms.  Do we always just connect the
        // next generated room to the last one?  That will be
        // probably fine because they are all randomly located,
        // so that will create lots of nifty stuff.
    }*/

    public int countTilesOfType(TileType type) {
        int num = 0;
        for (DunMapTile tile : tilesArray) {
            if (tile.getType() == type) {
                num++;
            }
        }
        return num;
    }

    public int countFloorTiles() {
        return countTilesOfType(TileType.Floor);
    }

    public DunMapMon monOnThisTile(DunMapTile tile) {
        for (DunMapMon mon : mons) {
            if (mon.getTile() == tile) {
                return mon;
            }
        }
        return null;
    }

    public boolean avatarOnThisTile(DunMapTile tile) {
        if (avatar == null) {
            return false;
        }
        return (avatar.getTile() == tile);
    }
 
    public void placeOneMon() {
        DunMapMon mon = new DunMapMon(this);
        mon.translateXProperty().bind(xOffset);
        mon.translateYProperty().bind(yOffset);
        mons.add(mon);
        
        DunMapTile tile = getRandomTileOfType(TileType.Floor);
        while (monOnThisTile(tile) != null) {
            tile = getRandomTileOfType(TileType.Floor);
        }
        mon.moveTo(tile, Constants.Dir.SOUTH);
    }

    public void generateMons() {
        // Throw in various mons based
        // on the size.
        int numTiles = countFloorTiles();

        int min = numTiles / 100;
        int max = numTiles / 50;
        int num = Utils.number(min, max);
        while (num >= 0) {
            placeOneMon();
            num--;
        }
    }

    public void placeEntrance() {
        // Throw up the entrance/exit somewhere.
        DunMapTile tile = null;
        while (tile == null) {
            tile = getRandomTileOfType(TileType.Floor);
            if (tile == null) {
                // Boned!
                Utils.log("placeEntrance:  No available tiles!");
                return;
            }

            if (tile.isSingleFile())
                tile = null;
        }

        tile.setType(TileType.Exit);
        exit = tile;
    }

    public void randomizeTerrain() {
        // How should I build this, maybe by rooms and hallways?
        // Certainly it depends what type of dun this is.

        // These are nice because long straight hallways of
        // one space are actually fine.  Then rooms can
        // offshoot from those, with stairs occasionally
        // branching off as well depending on the layout.
        // I can specify like a width and height and then a number
        // of tiles and it generates based on that.  And maybe a
        // rough shape as well.

        // Then could there perhaps be interesting features of
        // duns?  Or are they just halls and rooms
        // with different types of mons running around?

        // Duns are different - ruins have a more open layout,
        // with the "blank" actually as floor and the edges
        // of the rooms as "wall" kind of like a town. 
        // Caves and mines are different, with blank spaces 
        // as inaccessible.
        generateRooms();
        generateTreas();
        //generateHalls();
        placeEntrance();
        generateMons();

        // Also it should be possible to get different kinds of
        // things, like a really long dun with a single windy hall,
        // or a smaller dun packed with closer rooms.  Otherwise this
        // may get boring quickly.

        // Also maybe some use a different paradigm, like a cave might
        // use the tree spawning paradigm to create space, and then
        // hook through it with the river or mountain-spawning paradigm
        // for realistic water or passages, and maybe you really can't
        // get through it all naturally, maybe sometimes there are tools
        // or ways to get through walls that you need to figure out and
        // perhaps even return later.  I could even check those rooms
        // and give them better stuff.  Bridges across rivers, drills or
        // bombs to get through rock, axes for trees, arrows to pause
        // enemies (although they get mad after and run faster).  Or is that
        // too complex and overkill for this sim?
    }
    
    @Override
    public void onLeftClick(double x, double y) {
        DunMapTile tile = (DunMapTile)getTileForClick(x, y);
        if (tile != null) {
            onLeftClickTile(tile);
        }
    }

    @Override
    public void onRightClick(double x, double y) { 
        DunMapTile tile = (DunMapTile)getTileForClick(x, y);
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
        for (DunMapTile tile : tilesArray) {
            tile.draw(gc);
        }
        
        // Draw the mobiles
        for (DunMapMon mon : mons) {
            mon.draw(gc);
        }

        // Draw the avatar
        avatar.draw(gc);
    }
}