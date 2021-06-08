package dfsim;

import java.util.EnumSet;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// This is essentially like a static class in C#
public final class Constants {
    private Constants () { // private constructor
    }

    public static void init() {
    }
    
    public static final long ONE_SECOND_IN_NANOSECONDS = 1000000000;
    
    public static final String RES_LOAD_PATH = "/res/";
    public static final String FEMALE_PORTRAIT_PATH = RES_LOAD_PATH + "portraits/female/";
    public static final String MALE_PORTRAIT_PATH = RES_LOAD_PATH + "portraits/male/";
    public static final String GIF_PATH = RES_LOAD_PATH + "gifs/";
    public static final String TILES_PATH = RES_LOAD_PATH + "tiles/";
    public static final String CHAR_SPRITES_PATH = RES_LOAD_PATH + "char_sprites/";

    public static final String FILENAME_CHAR_SPRITE_HERO1 = CHAR_SPRITES_PATH + "hero1.png";

    // Char sprites
    public static final String FEMALE_CHARSPRITE_PATH = CHAR_SPRITES_PATH + "female/";
    public static final String MALE_CHARSPRITE_PATH = CHAR_SPRITES_PATH + "male/";

    // The pipoya defines
    public static final boolean USING_PIPOYA = true;

    public static final String PIPOYA_PATH_MAP = TILES_PATH + "Pipoya RPG World 48x48/";
    public static final String PIPOYA_PATH_TILES = TILES_PATH + "Pipoya RPG World 48x48/[A]_type3/";

    public static final String PIPOYA_FILENAME_MAP = PIPOYA_PATH_MAP + "pipo-map001.png";
    public static final String PIPOYA_FILENAME_GRASS = PIPOYA_PATH_TILES + "pipo-map001_at-kusa.png";
    public static final String PIPOYA_FILENAME_PATH = PIPOYA_PATH_TILES + "pipo-map001_at-miti.png";
    public static final String PIPOYA_FILENAME_FOREST = PIPOYA_PATH_TILES + "pipo-map001_at-mori.png";
    public static final String PIPOYA_FILENAME_DESERT = PIPOYA_PATH_TILES + "pipo-map001_at-sabaku.png";
    public static final String PIPOYA_FILENAME_DIRT = PIPOYA_PATH_TILES + "pipo-map001_at-tuti.png";
    public static final String PIPOYA_FILENAME_SEA = PIPOYA_PATH_TILES + "pipo-map001_at-umi.png";
    public static final String PIPOYA_FILENAME_MTN1 = PIPOYA_PATH_TILES + "pipo-map001_at-yama1.png";
    public static final String PIPOYA_FILENAME_MTN2 = PIPOYA_PATH_TILES + "pipo-map001_at-yama2.png";
    public static final String PIPOYA_FILENAME_MTN3 = PIPOYA_PATH_TILES + "pipo-map001_at-yama3.png";
    // End pipoya defines

    public static final int BUTTON_WIDTH = 120;
    public static final int NODE_SIZE = 125;

    // Draw the areas in a grayscale based on level so we can easily visualize
    // what the "level-scape" looks like
    public static final boolean ENABLE_LEVEL_GRAYSCALE = false;

    // Enable drawing of the actual graphics rather than just colors
    public static final boolean ENABLE_TILE_GRAPHICS = true;

    // Number of pixels per tile.  The engine will automatically scale tile images
    // to this size regardless of what their resolutions are.
    public static final int BASE_TILE_SIZE = 30;

    // How many ms for screen fades?
    public static final int FADE_MS = 500;

    public static enum Dir {
        NORTH(0), EAST(1), WEST(2), SOUTH(3), NUMDIRS(4), NONE(5);

        private int _value;

        Dir(int Value) {
            this._value = Value;
        }

        public int val() {
            return _value;
        }

        private static Dir[] cachedValues = null;
        public static Dir fromInt(int i) {
            if (Dir.cachedValues == null) {
                Dir.cachedValues = Dir.values();
            }
            return Dir.cachedValues[i];
        }

        public static Dir revDir(Constants.Dir dir) {
            switch (dir) {
                case NORTH: return SOUTH;
                case EAST:  return WEST;
                case WEST:  return EAST;
                case SOUTH: return NORTH;
            }
            return null;
        }

        public static Dir getRandomDir() {
            return fromInt(Utils.number(0, 3));
        }
    }
    
    public static enum Ordinal {
        NORTHWEST(0), 
        NORTH(1), 
        NORTHEAST(2), 
        SOUTHEAST(3), 
        SOUTH(4), 
        SOUTHWEST(5), 
        EAST(6), 
        WEST(7), 
        NUMDIRS(8), 
        NONE(9);

        private int _value;

        Ordinal(int Value) {
            this._value = Value;
        }

        public int val() {
            return _value;
        }

        private static Ordinal[] cachedValues = null;
        public static Ordinal fromInt(int i) {
            if (Ordinal.cachedValues == null) {
                Ordinal.cachedValues = Ordinal.values();
            }
            return Ordinal.cachedValues[i];
        }

        public static Ordinal revOrdinal(Constants.Ordinal dir) {
            switch (dir) {
                case NORTH: return SOUTH;
                case EAST:  return WEST;
                case WEST:  return EAST;
                case SOUTH: return NORTH;
                case NORTHEAST: return SOUTHWEST;
                case NORTHWEST: return SOUTHEAST;
                case SOUTHEAST: return NORTHWEST;
                case SOUTHWEST: return NORTHEAST;
            }
            return null;
        }

        public static Ordinal getRandomDir() {
            return fromInt(Utils.number(0, 7));
        }
    }

    public static enum TownSize {
        SMALL(0), MEDIUM(1), LARGE(2), HUGE(3);

        private int _value;

        TownSize(int Value) {
            this._value = Value;
        }

        public int val() {
            return _value;
        }

        private static TownSize[] cachedValues = null;
        public static TownSize fromInt(int i) {
            if (TownSize.cachedValues == null) {
                TownSize.cachedValues = TownSize.values();
            }
            return TownSize.cachedValues[i];
        }

        public static TownSize sizeForPopulation(int pop) {
            if (pop >= 12000) {
                return HUGE;
            }
            else if (pop >= 8000) {
                return LARGE;
            } 
            else if (pop >= 1000) {
                return MEDIUM;
            }
            else {
                return SMALL;
            }
        }
    }

    public static enum Gender {
        None,
        Male,
        Female;

        public static Gender fromStr(String str) {
            String lowerStr = str.toLowerCase();
            if (lowerStr.equals("male")) {
                return Male;
            }
            else if (lowerStr.equals("female")) {
                return Female;
            }
            return None;
        }
    }

    public static enum CharClass {
        Soldier,
        Warrior,
        Hunter,
        Thief,
        Cleric,
        Mage,
        Fighter,
        Wayfarer,
        Assassin,
        Minstrel,

        General,
        Knight,
        Ranger,
        Ninja,
        Pilgrim,
        Wizard,
        Battler,
        Adventurer,
        Slayer,
        Bard,

        Sage,
        Paladin,

        Villager,
        Merchant,
        TownGuard,
        CityGuard,
        Pirate,
        MysteriousOldMan,
        MysteriousOldWoman,
        Prince,
        Princess,
        Lord,
        Lady,
        Shopkeeper,
        Innkeeper,
        Mutant,
        KnightErrant,
        Gifted,
        Reaper,
        Angel,
        Demon,
        Servant,
        Dreamwalker,
        Dreamer,
        Antihero,
        Hero,
        Samurai,
        Ronin,
        Ghost,
        Henchman,
        Mercenary,
        Merman,
        Mermaid,
        Nymph,
        Jester,
        Dancer,
        HalfDragon,
        
        DarkElf;  // Now we start the weird classes
        
        private int _value;
        private static int nextVal = 0;

        CharClass() {
            this._value = ordinal();
        }

        public int val() {
            return _value;
        }

        @Override
        public String toString() {
            switch (this) {
                case TownGuard:
                    return "Town Guard";
                case CityGuard:
                    return "City Guard";
                case MysteriousOldMan:
                    return "Mysterious Old Man";
                case MysteriousOldWoman:
                    return "Mysterious Old Woman";
                case KnightErrant:
                    return "Knight-Errant";
                case HalfDragon:
                    return "Half-Dragon";
                case DarkElf:
                    return "Dark Elf";
                default:
                    return super.toString();
            }
        }

        public static CharClass fromStr(String str) {
            if (CharClass.cachedValues == null) {
                CharClass.cachedValues = CharClass.values();
            }
            for (CharClass chclass : CharClass.cachedValues) {
                if (chclass.toString().equals(str)) {
                    return chclass;
                }
            }
            return Villager;
        }

        public boolean allowedForGender(Gender gender) {
            if (this == MysteriousOldMan ||
                this == Prince ||
                this == Lord ||
                this == Merman) {
                if (gender != Gender.Male) {
                    return false;
                }
            }
            else if (this == MysteriousOldWoman ||
                     this == Princess ||
                     this == Lady ||
                     this == Nymph ||
                     this == Mermaid) {
                if (gender != Gender.Female) {
                    return false;
                }
            }
            return true;
        }

        private static CharClass[] cachedValues = null;
        public static CharClass fromInt(int i) {
            if (CharClass.cachedValues == null) {
                CharClass.cachedValues = CharClass.values();
            }
            return CharClass.cachedValues[i];
        }

        public static CharClass getRandomHireClass() {
            // These are the ones that appear for hire
            int start = Soldier.val();
            int end = Assassin.val();
            return (fromInt(Utils.number(start, end)));
        }

        public static CharClass getRandomTownPersonClass(Gender gender) {
            // Random classes just for villagers.
            if (Utils.pass() == true) {
                return Villager;
            }
            int start = Villager.val();
            int end = HalfDragon.val();
            CharClass cls = fromInt(Utils.number(start, end));
            while (cls.allowedForGender(gender) == false) {
                cls = fromInt(Utils.number(start, end));
            }
            return (cls);
        }
    }

    /*
    Hero types:
    Castle Soldier
    Town Guard
    City Guard
    Wayfarer
    Merchant
    Master
    Black Belt
    Thief
    Ninja
    Samurai
    Pirate
    Knight
    Fighter
    Sage
    Wizard
    Cleric
    Pilgrim
    Ranger


    DW3
    Soldier/Warrior
    Fighter (bare hands)
    Pilgrim/Cleric
    Wizard/Witch/Mage
    Merchant/Dealer
    Goof-Off/Jester

    Sage
    Thief
    */

    /*
      "Townswoman" or "Townsman" or "Villager" which is OK
    // but doesn't get a lot of great moves, but can promote to whatever class
    // you want.  But occasionally you'll get something interesting like
    // Minstrel or Wayfarer or Thief or Mysterious Old Man or Princess
    // or Shopkeeper (yes you can recruit the shopkeepers too), or something
    // like, Mutant, Adventurer, Knight Errant, Gifted, Reaper, Angel (maybe
    // they spawn rarely next to fountains at night and things), Servant,
    // Dreamwalker, Dreamer, Antihero, Hero, Merchant, Ronin, Ghost, and anything else
    // that it seems cool that someone might be.  All kinds of weird, neat
    // fantasy tropes.  Henchman, Mercenary, Mermaid, Half-Dragon, etc.
    */
}