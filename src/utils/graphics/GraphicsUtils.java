
package dfsim;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import java.util.*;

// GUI stuff
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.event.*;
import javafx.scene.text.*;

import javafx.scene.effect.*;
import javafx.scene.paint.*;

import dfsim.gui.*;

// This is essentially like a static class in C#
public final class GraphicsUtils {

    private GraphicsUtils() { // private constructor
    }

    // You use this and call node.setEffect(GraphicsUtils.createBorderGlow(Color.BLUE));
    public static DropShadow createBorderGlow(Color color) {
        int depth = 70;
        DropShadow borderGlow = new DropShadow();
        borderGlow.setOffsetY(0f);
        borderGlow.setOffsetX(0f);
        borderGlow.setColor(color);
        borderGlow.setWidth(depth);
        borderGlow.setHeight(depth);
        return borderGlow;
    }

    // Check if any female or male portraits are available
    public static boolean femalePortraitsAvailable() {
        for (Portrait p : Data.femalePortraits) {
            if (p.inUse() == false) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean malePortraitsAvailable() {
        for (Portrait p : Data.malePortraits) {
            if (p.inUse() == false) {
                return true;
            }
        }
        return false;
    }

    public static Image rotateImage(Image img, int angleDegrees) {
        ImageView imageView = new ImageView(img);
        imageView.setRotate(angleDegrees);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return imageView.snapshot(params, null);
    }

    private static final int TOLERANCE_THRESHOLD = 0xFF;
    
    // Within tolerance threshold, make transColor transparent
    // transColor is a hex type, like 0x00FFFFFF;
    private static Image makeColorTransparent(Image inputImage, int transColor) {
        int W = (int) inputImage.getWidth();
        int H = (int) inputImage.getHeight();
        WritableImage outputImage = new WritableImage(W, H);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                int argb = reader.getArgb(x, y);

                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                if (r >= TOLERANCE_THRESHOLD 
                        && g >= TOLERANCE_THRESHOLD 
                        && b >= TOLERANCE_THRESHOLD) {
                    //argb &= 0x00FFFFFF;
                    argb &= transColor;
                }

                writer.setArgb(x, y, argb);
            }
        }

        return outputImage;
    }

    // Should I make a custom sprite arraylist class that does this instead of having it here?
    public static GameSprite getSpriteForKey(ArrayList<GameSprite> spriteList, String key) {
        for (GameSprite spr : spriteList) {
            if (spr.getSpriteKey().equals(key)) {
                return spr;
            }
        }
        return null;
    }

    // Wish I could just use the above function somehow... must be a way.
    public static SpriteAnimation getAnimationSpriteForKey(ArrayList<SpriteAnimation> spriteList, String key) {
        for (SpriteAnimation spr : spriteList) {
            if (spr.getSpriteKey().equals(key)) {
                return spr;
            }
        }
        return null;
    }

    public static Rectangle getTransparentBounds(Image image) {
        return getTransparentBounds(image, 0, 0, image.getWidth(), image.getHeight());
    }

    // We may want to get the "transparent" bounding box of
    // an image, like what are the bounds beyond which every
    // pixel is transparent.
    // We can pass a "sub-rect" to start in the image, from startX, startY, etc.
    public static Rectangle getTransparentBounds(Image image, double startX, double startY, double width, double height) {
        PixelReader reader = image.getPixelReader();

        int totalWidth = (int)startX + (int)width - 1;
        int totalHeight = (int)startY + (int)height - 1;

        int firstNonEmptyX = (int)startX;
        int firstNonEmptyY = (int)startY;
        int lastNonEmptyX = totalWidth;
        int lastNonEmptyY = totalHeight;
        
        outer: for (; firstNonEmptyX < totalWidth; firstNonEmptyX++) {
            for (int y = 0; y < totalHeight; y++) {
                // stop, if most significant byte (alpha channel) is != 0
                if ((reader.getArgb(firstNonEmptyX, y) & 0xFF000000) != 0) {
                    break outer;
                }
            }
        }

        outer: for (; lastNonEmptyX > firstNonEmptyX; lastNonEmptyX--) {
            for (int y = 0; y < totalHeight; y++) {
                if ((reader.getArgb(lastNonEmptyX, y) & 0xFF000000) != 0) {
                    break outer;
                }
            }
        }

        outer: for (; firstNonEmptyY < totalHeight; firstNonEmptyY++) {
            // use info for columns to reduce the amount of pixels that need checking
            for (int x = firstNonEmptyX; x <= lastNonEmptyX; x++) {
                if ((reader.getArgb(x, firstNonEmptyY) & 0xFF000000) != 0) {
                    break outer;
                }
            }
        }

        outer: for (; lastNonEmptyY > firstNonEmptyY; lastNonEmptyY--) {
            for (int x = firstNonEmptyX; x <= lastNonEmptyX; x++) {
                if ((reader.getArgb(x, lastNonEmptyY) & 0xFF000000) != 0) {
                    break outer;
                }
            }
        }
        
        Rectangle rect = new Rectangle(firstNonEmptyX, firstNonEmptyY, lastNonEmptyX - firstNonEmptyX, lastNonEmptyY - firstNonEmptyY);
        return rect;
    }
}