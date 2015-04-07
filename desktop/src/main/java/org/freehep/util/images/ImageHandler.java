package org.freehep.util.images;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * A convenience class for handling images.
 * Maintains a cache of images suitable for small icons which may be reused
 * often.
 *
 * @author Tony Johnson
 * @author Cal Loomis
 * @version $Id: ImageHandler.java,v 1.3 2008-05-04 12:23:21 murkle Exp $
 */

public class ImageHandler
{
    public static final Image brokenImage;
    public static final Cursor brokenCursor;
    public static final Icon brokenIcon;
    private static String[] imageExtensions = { ".png", ".gif", ".jpeg", ".jpg" };
    private static Hashtable imageCache = new Hashtable();
    private static Hashtable cursorCache = new Hashtable();
    private static Hashtable iconCache = new Hashtable();

    // only static methods
    protected ImageHandler()
    {
    }

    static
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        brokenImage = toolkit.getImage(ImageHandler.class.getResource("BrokenImage.gif"));
        if (brokenImage == null)
        {
            throw new RuntimeException("Could not load BrokenIcon .. this looks bad!");
        }
        brokenIcon = new ImageIcon(brokenImage,"BrokenIcon");

        Image brokenCursorImage = toolkit.getImage(ImageHandler.class.getResource("BrokenCursor.gif"));
        if (GraphicsEnvironment.isHeadless()) {
            // if run with headless option no custom cursors are available
            brokenCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        } else if (brokenCursorImage != null) {
           brokenCursor = toolkit.createCustomCursor(brokenCursorImage, new Point(0,0), "BrokenCursor");
        } else {
           brokenCursor = null;
        }

        if (brokenCursor == null)
        {
            throw new RuntimeException("Could not load BrokenCursor .. this looks bad!");
        }
    }

    /**
     * Load an image from a URL. Images are cached for efficiency. If the image cannot be found
     * will return a brokenImage. No exceptions are thrown.
     * @param url The url of the image to load.
     * @return The image
     */
    public static Image getImage(URL url)
    {
        Image image = null;
        if (url!=null) {
            image = (Image) imageCache.get(url);
        } else {
            image = brokenImage;
        }

        if (image==null && url!=null)
        {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            // Note we dont use toolkit.createImage(url) since it returns an image
            // even if the specified URL does not exist
            try
            {
                InputStream in = url.openStream();
                try
                {
                    int length = in.available();
                    if (length == 0) length = 1000;
                    byte[] bytes = new byte[length];
                    for (int offset=0;;)
                    {
                        int rc = in.read(bytes,offset,length-offset);
                        if (rc<0) break;
                        if (rc>0) offset += rc;
                        else
                        {
                            length += 1000;
                            byte[] newBytes = new byte[length];
                            System.arraycopy(bytes,0,newBytes,0,offset);
                            bytes = newBytes;
                        }
                    }
                    image = toolkit.createImage(bytes);
                }
                finally
                {
                    in.close();
                }
            }
            catch (IOException x)
            {
                image = brokenImage;
            }

            // Note we SHOULD include brokenImage in the cache, otherwise repeated requests for
            // the same non-existant url will be very slow
            if (url!=null) imageCache.put(url, image);
        }
        return image;
    }

    /**
     * Create the best cursor by  reading from a URL, with hotspot (0,0).
     */
    public static Cursor getBestCursor(String name, Class clazz, int width, int height) {
        return getBestCursor(name, clazz, width, height, 0, 0);
    }
    
    /**
     * Create the best cursor by  reading from a URL, with hotspot (x, y).
     * The best width and height are inserted into the URL in the spots with %width
     * and %height. Example:
     *
     * getBestCursor("/org/freehep/swing/images/MyIcon%wx%h.png", 32 32);
     *
     * @param name URL of the cursor
     * @param clazz Class to be used for getting resource
     * @param width suggested width
     * @param height suggested height
     * @param x hotspot
     * @param y hotspot
     * @return The cursor, the default cursor if custom cursors are not supported
     *         or brokenCursor if the cursor cannot be found
     */
    public static Cursor getBestCursor(String name, Class clazz, int width, int height, int x, int y) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getBestCursorSize(width, height);
        if ((dimension.width == 0) && (dimension.height == 0)) return Cursor.getDefaultCursor();
        name = name.replaceAll("%w", Integer.toString(dimension.width));
        name = name.replaceAll("%h", Integer.toString(dimension.height));
        return getCursor(name, clazz, x, y);
    }

    /**
     * Create a cursor by reading from a URL, with hotspot (0,0).
     * @param url The URL to read
     * @return The cursor, or brokenCursor if the cursor cannot be found
     */
    public static Cursor getCursor(URL url) {
        return getCursor(url, 0, 0);
    }

    /**
     * Create a cursor by reading from a URL
     * @param url The URL to read
     * @param x x position of hotspot
     * @param y y position of hotspot
     * @return The cursor, or brokenCursor if the cursor cannot be found
     */
    public static Cursor getCursor(URL url, int x, int y)
    {
        Cursor cursor = (url!=null) ? (Cursor) cursorCache.get(url) : null;
        if (cursor == null)
        {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image image = getImage(url);
            if (image == brokenImage) cursor = brokenCursor;
            else cursor = toolkit.createCustomCursor(image, new Point(x, y), url.getFile());
            if (url != null) cursorCache.put(url, cursor);
        }
        return cursor;
    }

    /**
     * Create an Icon by reading from a URL
     * @param url The URL to read
     * @return The icon, or brokenIcon if it cannot be loaded
     */
    public static Icon getIcon(URL url)
    {
        Icon icon = (url!=null) ? (Icon) iconCache.get(url) : null;
        if (icon == null)
        {
            Image image = getImage(url);
            if (image == brokenImage) icon = brokenIcon;
            else icon = new ImageIcon(image,url.getFile());
            if (url != null) iconCache.put(url, icon);
        }
        return icon;
    }
    /**
     * Convenience routine, equivalent to getImage(clazz.getResource(name));
     * As an additional convenience, if the name does not contain an extension (eg .gif)
     * then a list of common extensions will be tried.
     * @param name The relative address of the image to load
     * @param clazz The class from which the address is based
     * @return The image, or brokenImage if no image is found.
     */
    public static Image getImage(String name, Class clazz)
    {
        if (name.indexOf('.') >= 0) return getImage(clazz.getResource(name));
        for (int i=0; i<imageExtensions.length; i++)
        {
            URL url = clazz.getResource(name+imageExtensions[i]);
            if (url != null) return getImage(url);
        }
        return brokenImage;
    }
    
    /**
     * Convenience routine, equivalent to getCursor(clazz.getResource(name));
     * As an additional convenience, if the name does not contain an extension (eg .gif)
     * then a list of common extensions will be tried.
     * @param name The relative address of the cursor to load
     * @param clazz The class from which the address is based
     * @return The cursor, or brokenCursor if no cursor is found.
     */
    public static Cursor getCursor(String name, Class clazz) {
        return getCursor(name, clazz, 0, 0);
    }
    
    /**
     * Convenience routine, equivalent to getCursor(clazz.getResource(name));
     * As an additional convenience, if the name does not contain an extension (eg .gif)
     * then a list of common extensions will be tried.
     * @param name The relative address of the cursor to load
     * @param clazz The class from which the address is based
     * @param x hotspot
     * @param y hotspot
     * @return The cursor, or brokenCursor if no cursor is found.
     */
    public static Cursor getCursor(String name, Class clazz, int x, int y)
    {
        if (name.indexOf('.') >= 0) return getCursor(clazz.getResource(name), x, y);

        for (int i=0; i<imageExtensions.length; i++)
        {
            URL url = clazz.getResource(name+imageExtensions[i]);
            if (url != null) return getCursor(url, x, y);
        }
        return brokenCursor;
    }

    /**
     * Convenience routine, equivalent to getIcon(clazz.getResource(name));
     * As an additional convenience, if the name does not contain an extension (eg .gif)
     * then a list of common extensions will be tried.
     * @param name The relative address of the icon to load
     * @param clazz The class from which the address is based
     * @return The icon, or brokenIcon if no icon is found.
     */
    public static Icon getIcon(String name, Class clazz)
    {
        if (name.indexOf('.') >= 0) return getIcon(clazz.getResource(name));

        for (int i=0; i<imageExtensions.length; i++)
        {
            URL url = clazz.getResource(name+imageExtensions[i]);
            if (url != null) return getIcon(url);
        }
        return brokenIcon;
    }
}
