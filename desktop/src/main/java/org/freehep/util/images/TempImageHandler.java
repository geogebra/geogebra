package org.freehep.util.images;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class TempImageHandler {

    private static Image brokenImage;
    private static Cursor brokenCursor;
    private static String[] imageExtensions = { ".gif", ".png" };
    private static Hashtable imageCache = new Hashtable();
    private static Hashtable cursorCache = new Hashtable();
    private static Hashtable iconCache = new Hashtable();
    
    // only static methods
    protected TempImageHandler() {
    }

    static {
        Toolkit toolkit = Toolkit.getDefaultToolkit(); 
        
        byte[] brokenImageBytes = getImageBytes("BrokenImage.gif",
                                                TempImageHandler.class);
        if (brokenImageBytes != null) {
            brokenImage = toolkit.createImage(brokenImageBytes);
        }
        
        if (brokenImage == null) {
            throw new RuntimeException("Could not load BrokenIcon");
        }
        
        byte[] brokenCursorBytes =  getImageBytes("BrokenCursor.gif", 
                                                  TempImageHandler.class);
        if (brokenCursorBytes != null) {
            Image brokenCursorImage = toolkit.createImage(brokenCursorBytes);
            if (brokenCursorImage != null) {
                brokenCursor = toolkit.createCustomCursor(brokenCursorImage, 
                                                          new Point(0,0), 
                                                          "BrokenCursor");
            }
        }
        
        if (brokenCursor == null) {
            throw new RuntimeException("Could not load BrokenCursor");
        }
    }
    
    public static Image getImage(String name, Class clazz) {
        Image image = (Image)imageCache.get(name);
        if (image == null) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            int i = 0;
            while ((image == null) && (i < imageExtensions.length)) {
                byte[] imageBytes = getImageBytes(name+imageExtensions[i], 
                                                  clazz);
                if (imageBytes != null) {
                    image = toolkit.createImage(imageBytes);
                }
                i++;
            }
            if (image != null) {
                imageCache.put(name, image);
            } else {
                image = brokenImage;
            }
        }
        return image;
    }

    public static Cursor getCursor(String name, Class clazz) {
        return getCursor(name, clazz, 0, 0);
    }    
   
    public static Cursor getCursor(String name, Class clazz, int x, int y) {
        Cursor cursor = (Cursor)cursorCache.get(name);

        if (cursor == null) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image image = getImage(name, clazz);

            // Make this into an icon image, so that we don't have to deal
            // with image observers.
            ImageIcon icon = new ImageIcon(image);
            int iconWidth = icon.getIconWidth();
            int iconHeight = icon.getIconHeight();

            // Get the best size for the cursor.
            Dimension bestSize = 
                toolkit.getBestCursorSize(icon.getIconWidth(),
                                          icon.getIconHeight());
            int cursorWidth = bestSize.width;
            int cursorHeight = bestSize.height;

            if (cursorWidth<iconWidth || cursorHeight<iconHeight) {

                // One of the dimensions is smaller, so just scale the image.
                image = image.getScaledInstance(cursorWidth,cursorHeight,
                                                Image.SCALE_DEFAULT);

            } else if (iconWidth!=cursorWidth || iconHeight!=cursorHeight) {

                // Both dimensions are larger.  Make a larger image and draw
                // the image into the upper, left-hand corner. 
                BufferedImage bimage = 
                    new BufferedImage(cursorWidth,cursorHeight,
                                      BufferedImage.TYPE_INT_ARGB);

                // Draw into the image to create the new cursor.
                Graphics2D g2d = (Graphics2D) bimage.getGraphics();
                Color trans = new Color(0,0,0,0);
                g2d.setColor(trans);
                g2d.fillRect(0,0,cursorWidth,cursorHeight);
                g2d.drawImage(icon.getImage(),0,0,null);

                // Set the image to the newly created one.
                image = bimage;
            }

            if (image == brokenImage) {
                cursor = brokenCursor;
            } else if (image != null) {
                cursor = toolkit.createCustomCursor(image, new Point(x, y), 
                                                    ""+name);
                if (cursor != null) cursorCache.put(name, cursor);
            }
        }
        return cursor;
    }

    public static Icon getIcon(String name, Class clazz) {
        Icon icon = (Icon)iconCache.get(name);
        if (icon == null) {
            Image image = getImage(name, clazz);
            if (image != null) {
                icon = new ImageIcon(image, name);
            }
            if ((icon != null) && (image != brokenImage)) 
                iconCache.put(name, icon);
        }
        return icon;
    }    


    /**
     * Read a string of byes from a file.  */
    private static byte[] getImageBytes(String name, Class clazz) {
        try {

            // Try first to get the data as a stream.
            InputStream stream = clazz.getResourceAsStream(name);
            if (stream == null) return null;

            ByteArrayOutputStream ostream = new ByteArrayOutputStream();

            // Create a temporary buffer.
            int maxBytes = 10000;
            byte[] buffer = new byte[maxBytes];

            // Read the full file into the ByteArrayOutputStream.
            int bytesRead = stream.read(buffer);
            while (bytesRead>0) {
                ostream.write(buffer,0,bytesRead);
                bytesRead = stream.read(buffer);
            }
            stream.close();

            // Convert to a byte array and return it.
            return ostream.toByteArray();
        } catch (IOException x) {

            return null;
        }
    }
}
