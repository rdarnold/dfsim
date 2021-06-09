
package dfsim;

import java.util.*;
import java.util.concurrent.*;
import javafx.geometry.*;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import java.io.*; 
import java.nio.file.*;
import java.net.*; // To get the mac address
import java.security.*;

import javax.json.*;
import javax.json.stream.*;


// GUI stuff
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.text.*;
import javafx.scene.control.*;

// This is essentially like a static class in C#
public final class Utils {
    public static Random rand;

    private Utils() { // private constructor
    }

    public static void init() {
        rand = new Random();
    }

    // Integer between min and max, inclusive of both.
    // Like old style C.
    public static int number(int min, int max) {
        return (min + rand.nextInt(((max + 1) - min)));
    }

    // This seems to be a better way, especially for concurrent programs
    // which this is not, but good for future reference.
    public static double random(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    // So this allows us to specify a standard min / max but then a changing
    // ratio that determines the result is multiplied by.
    public static int numberWithRatio(int min, int max, double ratio) {
        int newMin = (int)((double)min * ratio);
        int newMax = (int)((double)max * ratio);
        return number(newMin, newMax);
    }

    public static boolean pass(double percentChance) {
        return pass((int)percentChance);
    }

    // Pass without a number is a 50/50 chance, I use that a lot
    public static boolean pass() {
        return (pass(50));
    }

    // Pass in a 1-100% chance to pass, and we roll to see if we
    // pass that chance.  Just an easy way to do percentile rolls
    public static boolean pass(int percentChance) {
        if (percentChance < 1) {
            return false;
        }
        else if (percentChance >= 100) {
            return true;
        }
        
        return (number(1, 100) <= percentChance);
    }
 
    // This is a pretty cool function, you pass in any class type and two generic
    // objects, then use that class type to compare the two and see if they are
    // the same object.  Handy when we only have a base class pointer to what is 
    // actually a superclassed object, like trying to see if a particular Node
    // is a certain CheckBox control.
    // Turns out not to be necessary because you can just use the .equals.  I was
    // so excited too.
    /*public static boolean genericEquals(Class<?> cls, Object obj1, Object obj2) {
        if (obj1 == null || obj2 == null)
            return false;

        if ((cls.isInstance(obj1) == false) ||
            (cls.isInstance(obj2) == false)) {
            return false;
        }
        return (obj1.equals(obj2));
    }*/
    // Utility just to do some null checking.
    public static boolean equals(Object obj1, Object obj2) {
        if (obj1 == null || obj2 == null)
            return false;
        return (obj1.equals(obj2));
    }

    private static long logNum = 0; // Keep track of all the logs we have logged.
    public static void log() {
        log("");
    }

    public static void log(String str) {
        System.out.println("[" + logNum  + "] LOG: " + str);
        if (logNum >= Long.MAX_VALUE - 1) {
            // Unlikely that this will ever happen but who knows.
            logNum = 0;
        }
        logNum++;
    }

    public static void log(int num) {
        log("" + num);
    }

    public static void log(double num) {
        log("" + num);
    }

    public static void err() {
        System.out.println("ERROR");
    }

    public static void err(String str) {
        System.out.println("ERR: " + str);
    }

    public static void err(int num) {
        err("" + num);
    }

    public static void err(double num) {
        err("" + num);
    }
    
    public static int clampColor(int num) {
        return clamp(num, 0, 255);
    }
    
    public static int clamp(int num, int min, int max) {
        if (num < min) return min;
        if (num > max) return max;
        return num;
    }

    public static double clamp(double num, double min, double max) {
        if (num < min) return min;
        if (num > max) return max;
        return num;
    }

    public static double getAngleDegrees(double x1, double y1, double x2, double y2) {
        return (Utils.normalizeAngle(Math.toDegrees(getAngleRadians(x1, y1, x2, y2))));
    }

    // in radians
    public static double getAngleRadians(double x1, double y1, double x2, double y2) {
        return (Math.atan2(y2 - y1, x2 - x1));
    }

    public static double getAngleRadians(double xSpeed, double ySpeed) {
        return (Math.atan2(ySpeed, xSpeed));
    }
    public static double getAngleDegrees(double xSpeed, double ySpeed) {
        return (Utils.normalizeAngle(Math.toDegrees(getAngleRadians(xSpeed, ySpeed))));
    }

    public static double normalizeAngle(double angleDegrees) {
        double deg = angleDegrees;
        // More efficient to do this through division but w/e,
        // I'll never be more than 360 off anyway so same diff
        while (deg < 0)     { deg += 360; }
        while (deg >= 360)  { deg -= 360; }
        return deg;
    }

    public static boolean rectanglesOverlap(double left1, double top1, double wid1, double hgt1,
                                            double left2, double top2, double wid2, double hgt2) {
        // If left of 1 is further right than right of 2
        if (left1 > left2 + wid2) { return false; }

        // If top of 1 is further down than bottom of 2
        if (top1 > top2 + hgt2) { return false; }

        // If right of 1 is further left than left of 2
        if (left1 + wid1 < left2) { return false; }

        // If bottom of 1 is further up than top of 2
        if (top1 + hgt1 < top2) { return false; }

        // Otherwise, we are overlapping.
        return true;
    }

    public static boolean buttonsOverlap(Button btn1, Button btn2) {
        double wid1 = btn1.getWidth();
        double hgt1 = btn1.getHeight();
        double wid2 = btn2.getWidth();
        double hgt2 = btn2.getHeight();
        // First lets turn their coordinates into scene coordinates.
   	    Point2D point1 = btn1.localToScene(btn1.getLayoutBounds().getMinX(),
                                           btn1.getLayoutBounds().getMinY());

   	    Point2D point2 = btn2.localToScene(btn2.getLayoutBounds().getMinX(),
                                           btn2.getLayoutBounds().getMinY());
        return rectanglesOverlap(point1.getX(), point1.getY(), wid1, hgt1, 
                                 point2.getX(), point2.getY(), wid2, hgt2);
    }

    public static double calculateOverlappingArea(Button btn1, Button btn2) {
        double wid1 = btn1.getWidth();
        double hgt1 = btn1.getHeight();
        double wid2 = btn2.getWidth();
        double hgt2 = btn2.getHeight();
        // First lets turn their coordinates into scene coordinates.
   	    Point2D point1 = btn1.localToScene(btn1.getLayoutBounds().getMinX(),
                                           btn1.getLayoutBounds().getMinY());

   	    Point2D point2 = btn2.localToScene(btn2.getLayoutBounds().getMinX(),
                                           btn2.getLayoutBounds().getMinY());

        Rectangle a = new Rectangle(point1.getX(), point1.getY(), wid1, hgt1);
        Rectangle b = new Rectangle(point2.getX(), point2.getY(), wid2, hgt2);

        Shape intersect = Shape.intersect(a, b);
        if (intersect.getBoundsInLocal().getWidth() != -1) {
            // Simply multiply width by height
            return (intersect.getBoundsInLocal().getWidth() * intersect.getBoundsInLocal().getHeight());
        }
        return 0;
    }

    // Add a vertical space region to a vbox
    public static void addVerticalSpace(VBox vbox, int amount) {
        Region space = new Region();
        space.setMinHeight(amount);
        vbox.getChildren().add(space);
    }

    // Methods that help us parse ints and doubles and allow them to interchange.
    public static int tryParseInt(String value) {  
        return tryParseInt(value, true);
    }

    private static int tryParseInt(String value, boolean tryDoubleToo) {  
        try {  
            int i = Integer.parseInt(value);  
            return i;  
        } catch (NumberFormatException e) {  
            if (tryDoubleToo == true) {
                return (int)tryParseDouble(value, false);
            }
            return 0;  
        }  
    }

    public static double tryParseDouble(String value) {  
        return tryParseDouble(value, true);
    }

    private static double tryParseDouble(String value, boolean tryIntToo) {   
        try {  
            double d = Double.parseDouble(value);  
            return d;  
        } catch (NumberFormatException e) {  
            // Ok so try as an int now.
            if (tryIntToo == true) {
                return (double)tryParseInt(value, false);
            }
            return 0;  
        }  
    }
    
    public static Tooltip createTooltip(String str) {
        Tooltip toolTip = new Tooltip(str);
        toolTip.setMaxWidth(400);
        toolTip.setWrapText(true);
        return toolTip;
    }

    public static void addToolTip(Button btn, String str) {
        Tooltip toolTip = createTooltip(str);
        btn.setTooltip(toolTip);
    }

    public static void addToolTip(Label label, String str) {
        Tooltip toolTip = createTooltip(str);
        label.setTooltip(toolTip);
    }

    public static TitledPane addTitledPane(Accordion addTo, VBox vbox, String titleText, String fromClassName) {
        TitledPane titledPane = new TitledPane();
        titledPane.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        titledPane.setText(titleText);
        //tp.lookup(".title").setStyle("-fx-font-weight: bold;");
        titledPane.setContent(vbox);
        addTo.getPanes().add(titledPane);
        // Set our class info on the user data so we can recall it later when we
        // want to record clicks on this pane.
        titledPane.setUserData(fromClassName);
        titledPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                TitledPane pane = (TitledPane)event.getSource();
            }
        });
        return titledPane;
    }

    public static double calcDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
    }

    // From x1, y1, as the origin point, rotate x2, y2
    public static Point2D calcRotatedPoint(double originX, double originY, double x2, double y2, double angleDegrees) {
        double hyp = calcDistance(originX, originY, x2, y2);
        double angleRadians = Math.toRadians(angleDegrees);

        double rotX = Math.cos(angleRadians) * hyp;
        double rotY = Math.sin(angleRadians) * hyp;

        Point2D point = new Point2D(originX + rotX, originY + rotY);
        return point;
    }


    // Attempt to get the MAC address if we are allowed to.
    public static String getMacAddress() {
        return (String)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    InetAddress address = InetAddress.getLocalHost();
                    NetworkInterface nwi = NetworkInterface.getByInetAddress(address);
                    byte mac[] = nwi.getHardwareAddress();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
                    }
                    return sb.toString();
                }
                catch (Exception e) {
                    System.out.println(e);
                    return "";
                }
            }
        });
    }

    // Not that helpful since it's just internal addresses.
    public static String getIpAddresses() {
        return (String)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    Enumeration e = NetworkInterface.getNetworkInterfaces();
                    while(e.hasMoreElements())
                    {
                        NetworkInterface n = (NetworkInterface) e.nextElement();
                        Enumeration ee = n.getInetAddresses();
                        while (ee.hasMoreElements())
                        {
                            InetAddress i = (InetAddress) ee.nextElement();
                            System.out.println(i.getHostAddress());
                        }
                    }
                    return "";
                }
                catch (Exception e) {
                    System.out.println(e);
                    return "";
                }
            }
        });
    }

    public static String getFileExtensionFromName(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    // String newStr = replaceFileExtensionWith(oldStr, "jpg");
    public static String replaceFileExtensionWith(String inputStr, String newExtension) {
        if (inputStr == null || inputStr == "" || inputStr.contains(".") == false) {
            return null;
        }
        return (removeFileExtension(inputStr) + "." + newExtension);
    }
    
    public static String removeFileExtension(String s) {
        String separator = System.getProperty("file.separator");
        String filename;

        // Remove the path upto the filename.
        int lastSeparatorIndex = s.lastIndexOf(separator);
        if (lastSeparatorIndex == -1) {
            filename = s;
        } else {
            filename = s.substring(lastSeparatorIndex + 1);
        }

        // Remove the extension.
        int extensionIndex = filename.lastIndexOf(".");
        if (extensionIndex == -1)
            return filename;

        return filename.substring(0, extensionIndex);
    }

    public static boolean stringStartsWith(String str, String strStartsWith) {
        if (str == null || strStartsWith == null || str.equals("") == true || strStartsWith.equals("") == true) {
            return false;
        }
        if (str.length() >= strStartsWith.length() && str.substring(0, strStartsWith.length()).equals(strStartsWith)) {
            return true;
        }
        return false;
    }
    
    public static String readFile(String fileName) {
        return readFile(fileName, true);
    }

    public static String readFile(String fileName, boolean printLog) {
        try {
            // Man, Java sucks here.  We can't check Files.exists using the same
            // path as we use to read the file !!  we have to actually strip off
            // the first slash or it will give false negative...
            //String existsPath = fileName.substring(1, fileName.length());

            // Strange, that is not the case in dfsim...
            String existsPath = fileName;
            if (Files.exists(Paths.get(existsPath)) == false) {
                if (printLog == true) {
                    Utils.log("readFile: " + existsPath + " not found");
                }
                return null;
            }
            //BufferedReader reader = new BufferedReader(new FileReader (file));
            // This way works both inside and outside of a jar file.  Using a File as
            // above does not.
            if (printLog == true) {
                Utils.log("readFile: " + fileName);
            }
            
            // Strange, need to do FileInputStream to make it work in dfsim...
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName)));//Utils.class.getResourceAsStream(fileName)));

            String         line = null;
            StringBuilder  stringBuilder = new StringBuilder();
            String         ls = System.getProperty("line.separator");

            try {
                while((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append(ls);
                }

                return stringBuilder.toString();
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            Utils.err("Failed to read file " + fileName);
            err();
            e.printStackTrace();
            return null;
        }
    }
    
    // If we're done parsing in an array, we may want to move past the array
    // end so that we can get to the next thing.
    public static void moveToEndOfJsonArray(JsonParser parser) {
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            if (event == JsonParser.Event.END_ARRAY) {
                return;
            }
	    }
    }
    
    // This is so stupid, I cant believe these methods arent built into the parser,
    // the java JsonParser class SUCKS.
    public static String getNextJsonValueAsString(JsonParser parser ) {
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            if (event == JsonParser.Event.VALUE_STRING ||
                event == JsonParser.Event.VALUE_NUMBER) {
                return parser.getString();
            }
	    }
        return null;
    }

    public static double getNextJsonValueAsDouble(JsonParser parser) {
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            if (event == JsonParser.Event.VALUE_STRING ||
                event == JsonParser.Event.VALUE_NUMBER) {
                return Utils.tryParseDouble(parser.getString());
            }
	    }
        return 0;
    }

    public static int getNextJsonValueAsInt(JsonParser parser) {
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            if (event == JsonParser.Event.VALUE_STRING ||
                event == JsonParser.Event.VALUE_NUMBER) {
                return Utils.tryParseInt(parser.getString());
            }
	    }
        return 0;
    }
}