/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.geogebra.common.jre.gui.MyImageJre;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Util;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.MyImageD;
import org.geogebra.desktop.main.ScaledIcon;

import com.himamis.retex.renderer.desktop.graphics.Base64;

/**
 * An ImageManager provides methods for loading images and icons for a JFrame.
 * To save memory every image and icon will be loaded only once.
 * 
 * @author Markus Hohenwarter
 */
public class ImageManagerD extends ImageManager {

	private final Hashtable<String, ImageIcon> iconTable = new Hashtable<>();
	private final Hashtable<String, MyImageD> internalImageTable = new Hashtable<>();
	private static final Hashtable<String, MyImageD> externalImageTable = new Hashtable<>();

	private final Hashtable<String, ImageResourceD> fillableImgs = new Hashtable<>();

	private final Toolkit toolKit;
	private MediaTracker tracker;

	private int maxIconSize = 64; // DEFAULT_ICON_SIZE;
	private double pixelRatio = 1;

	/**
	 * Creates a new ImageManager for the given JFrame.
	 */
	public ImageManagerD(Component comp) {
		toolKit = Toolkit.getDefaultToolkit();
		updatePixelRatio(comp.getGraphicsConfiguration());
		tracker = new MediaTracker(comp);
	}

	public ImageManagerD() {
		toolKit = Toolkit.getDefaultToolkit();
	}

	/**
	 * Gets the icon specified by fileName.
	 * 
	 * @return icon for fileName or null
	 */
	public ImageIcon getImageIcon(ImageResourceD fileName) {
		return getImageIcon(fileName, null);
	}

	/**
	 * 
	 * @param fileName
	 *            icon filename
	 * @param borderColor
	 *            if borderColor == null no border is added
	 * @return icon
	 */
	public ImageIcon getImageIcon(ImageResourceD fileName, Color borderColor) {
		return getImageIcon(fileName, borderColor, null);
	}

	/**
	 * @param fileName filename
	 * @param borderColor border color
	 * @param background background color
	 * @return image with border  and background
	 */
	public ImageIcon getImageIcon(ImageResourceD fileName, Color borderColor,
			Color background) {
		ImageIcon icon = iconTable.get(fileName.getFilename());
		if (icon == null) {
			// load the icon
			Image im = getImageResourceGeoGebra(fileName);
			if (im != null) {
				icon = new ImageIcon(addBorder(im, borderColor, background));
				iconTable.put(fileName.getFilename(), icon);
			}
		}
		return icon;
	}

	/**
	 * draw a line around the image
	 * @return image with border
	 */
	public static Image addBorder(Image im, Color borderColor,
			Color background) {
		if (borderColor == null) {
			return im;
		}

		BufferedImage bim = toBufferedImage(im);
		Graphics g = bim.getGraphics();
		if (background != null) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, bim.getWidth() - 1, bim.getHeight() - 1);
			g.drawImage(im, 0, 0, null);
		}
		g.setColor(borderColor);
		g.drawRect(0, 0, bim.getWidth() - 1, bim.getHeight() - 1);
		return bim;
	}

	/**
	 * Gets the image specified by fileName.
	 * 
	 * @return image for fileName or null
	 */
	public MyImageD getInternalImage(ImageResourceD fileName) {
		MyImageD img = null;
		MyImageD ob = internalImageTable.get(fileName.getFilename());
		if (ob != null) {
			// image already loaded
			img = ob;
		} else {
			// load the image from disk
			Image imgNative = getImageResourceGeoGebra(fileName);
			if (imgNative != null) {
				img = new MyImageD(imgNative);
				internalImageTable.put(fileName.getFilename(), img);
			}
		}
		return img;
	}

	/**
	 * Adds an external image files and changes ".gif" extensions to ".png".
	 * @param fileName0 filename
	 * @param img image
	 */
	public void addExternalImage(String fileName0, MyImageJre img) {
		Log.error("adding " + fileName0);
		if (fileName0 != null && img != null) {
			String fileName = fileName0;
			// GIF saved as PNG in .ggb files so need to change extension
			FileExtensions ext = StringUtil.getFileExtension(fileName);
			if (!ext.isAllowedImage()) {
				fileName = StringUtil.changeFileExtension(fileName,
						FileExtensions.PNG);
			}

			fileName = fileName.replace(".GIF", ".png");
			Log.debug("storing " + fileName + " " + img.isSVG());
			externalImageTable.put(fileName, (MyImageD) img);
		}
	}

	/**
	 * Lookup image by path, normalize extension
	 * @param fileName0 file path
	 * @return image
	 */
	public static MyImageD getExternalImage(String fileName0) {
		String fileName = fileName0;
		// GIF saved as PNG in .ggb files so need to change extension
		FileExtensions ext = StringUtil.getFileExtension(fileName);
		if (!ext.isAllowedImage()) {
			fileName = StringUtil.changeFileExtension(fileName,
					FileExtensions.PNG);
		}
		return externalImageTable.get(fileName);
	}

	/**
	 * get image for icons and other automatically add "/org/geogebra/desktop"
	 * prefix
	 * 
	 * @param name
	 *            name of the image (without "/org/geogebra/desktop" prefix)
	 * @return the image
	 */
	public Image getImageResourceGeoGebra(ImageResourceD name) {
		Image img = getImageResource(name);
		if (img == null) {
			img = getImageResource(
					"/org/geogebra/desktop" + name.getFilename());
		}

		if (img == null) {
			Log.error("Image " + name.getFilename() + " not found");
		}

		return img;
	}

	/**
	 * @param name resource identifier
	 * @return image for given resource
	 */
	public Image getImageResource(ImageResourceD name) {
		return getImageResource(name.getFilename());
	}

	/**
	 * return image from the full path name
	 * 
	 * @param name image path
	 * @return image from the full path name
	 */
	protected Image getImageResource(String name) {
		String path = name;
		if (!name.startsWith("/org")) {
			path = "/org/geogebra/desktop" + path;
		}
		Image img = null;

		try {
			java.net.URL url = ImageManagerD.class.getResource(path);
			if (url != null) {
				img = toolKit.getImage(url);
				tracker.addImage(img, 0);
				try {
					tracker.waitForAll();
				} catch (InterruptedException e) {
					Log.debug("Interrupted while loading Image: " + path);
				}
				tracker.removeImage(img);
			}
		} catch (Exception e) {
			Log.debug(e.toString());
		}

		return img;
	}

	/**
	 * @param image base image
	 * @return a buffered image with the contents of an image
	 */
	public static BufferedImage toBufferedImage(Image image) {
		// Determine if the image has transparent pixels; for this method's
		// implementation, see e661 Determining If an Image Has Transparent
		// Pixels
		boolean hasAlpha = hasAlpha(image);

		if (hasAlpha) {
			return toBufferedImage(image, Transparency.BITMASK);
		}
		return toBufferedImage(image, Transparency.OPAQUE);

	}

	/**
	 * @param image0 base image
	 * @param transparency see java.awt.Transparency
	 * @return buffered image
	 */
	public static BufferedImage toBufferedImage(Image image0,
			int transparency) {
		if (image0 instanceof BufferedImage) {
			return (BufferedImage) image0;
		}

		// This code ensures that all the pixels in the image are loaded
		Image image = new ImageIcon(image0).getImage();

		// Create a buffered image with a format that's compatible with the
		// screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		try {

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null),
					image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}

		if (bimage == null) {
			// Create a buffered image using the default color model
			int type;
			if (transparency == Transparency.OPAQUE) {
				type = BufferedImage.TYPE_INT_RGB;
			} else {
				type = BufferedImage.TYPE_INT_ARGB;
			}

			bimage = new BufferedImage(image.getWidth(null),
					image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	/**
	 * @param image image
	 * @return true if the specified image has transparent pixels
	 */
	public static boolean hasAlpha(Image image) {
		// If buffered image, the color model is readily available
		if (image instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage) image;
			return bimage.getColorModel().hasAlpha();
		}

		// Use a pixel grabber to retrieve the image's color model;
		// grabbing a single pixel is usually sufficient
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			// ignore
		}

		// Get the image's color model
		ColorModel cm = pg.getColorModel();

		if (cm == null) {
			return false;
		}

		return cm.hasAlpha();
	}

	/**
	 * @param icon base icon
	 * @param width new width
	 * @param height new height
	 * @return scaled icon
	 */
	public static ImageIcon getScaledIcon(ImageIcon icon, int width,
			int height) {
		if (icon.getIconWidth() == width && icon.getIconHeight() == height) {
			return icon;
		}
		Image scaledImage = getScaledImage(icon.getImage(), width, height);
		return new ImageIcon(scaledImage);
	}

	/**
	 * @param img base image
	 * @param width new width
	 * @param height new height
	 * @return scaled image
	 */
	public static Image getScaledImage(Image img, int width, int height) {
		return img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}

	/**
	 * @param res resource
	 * @param app application
	 * @return full path (md5 folder + filename)
	 */
	public String createImage(ImageResourceD res, App app) {
		Image im = getImageResource(res);
		BufferedImage image = ImageManagerD.toBufferedImage(im);
		return createImage(new MyImageD(image),
				"tool.png", app);
	}

	/**
	 * @param modeText mode name
	 * @return mode icon
	 */
	public ImageResourceD getToolImageResource(String modeText) {
		String filename = "mode_" + StringUtil.toLowerCaseUS(modeText) + ".png";
		String path = getToolbarIconPath() + filename;
		return new ImageResourceDImpl(path);
	}

	/**
	 * @return path to folder with toolbar icons for current font size
	 */
	public String getToolbarIconPath() {
		if (getMaxIconSize() <= 32 && pixelRatio <= 1) {
			return "/org/geogebra/common/icons_toolbar/p32/";
		}
		return "/org/geogebra/common/icons_toolbar/p64/";
	}

	/**
	 * Set maximum image size
	 * @param points size in points
	 */
	public void setMaxIconSizeAsPt(int points) {
		setMaxIconSize(Math.max(32, points * 2));
	}

	/**
	 * Sets the maximum pixel size (width and height) of all icons in the user
	 * interface. Larger icons are scaled down.
	 * 
	 * @param pixel
	 *            max icon size between 16 and 32 pixels
	 */
	public void setMaxIconSize(int pixel) {
		maxIconSize = Math.min(64, Math.max(16, pixel));
	}

	public int getMaxIconSize() {
		return maxIconSize;
	}

	public int getMaxScaledIconSize() {
		return (int) (maxIconSize * pixelRatio);
	}

	/**
	 * @param image image
	 * @param imageFileName filename
	 * @param app application
	 * @return full path (md5 folder + filename)
	 */
	public String createImage(MyImageD image, String imageFileName, App app) {
		String fileName = imageFileName;
		try {
			String zip_directory = image.getMD5();

			String fn = fileName;
			int index = fileName.lastIndexOf(File.separator);
			if (index != -1) {
				fn = fn.substring(index + 1); // filename without
			}
			// path
			fn = Util.processFilename(fn);

			// filename will be of form
			// "a04c62e6a065b47476607ac815d022cc/filename.ext"
			fileName = zip_directory + "/" + fn;
			// make sure this filename is not taken yet
			MyImageD oldImg = ImageManagerD.getExternalImage(fileName);
			if (oldImg != null) {
				// image with this name exists already
				if ((oldImg.getWidth() == image.getWidth())
						&& (oldImg.getHeight() == image.getHeight())) {
					// same size and filename => we consider the images as equal
					return fileName;
				}
				// same name but different size: change filename
				// this bit of code should now be
				// redundant as it
				// is near impossible for the filename to be the same unless
				// the files are the same
				int n = 0;
				do {
					n++;
					int pos = fileName.lastIndexOf('.');
					String firstPart = pos > 0 ? fileName.substring(0, pos)
							: "";
					String extension = fileName.substring(pos);
					fileName = firstPart + n + extension;
				} while (ImageManagerD.getExternalImage(fileName) != null);
			}

			addExternalImage(fileName, image);

			return fileName;
		} catch (Exception e) {
			app.setDefaultCursor();
			Log.debug(e);
			app.showError(Errors.LoadFileFailed);
			return null;
		} catch (java.lang.OutOfMemoryError t) {
			Log.debug("Out of memory");
			System.gc();
			app.setDefaultCursor();
			app.showError(Errors.LoadFileFailed);
			return null;
		}
	}

	@Override
	public void addExternalImage(String filename0, String urlBase64) {
		if (urlBase64.startsWith(StringUtil.pngMarker)) {
			String pngStr = StringUtil.removePngMarker(urlBase64);

			BufferedImage image = null;
			byte[] imageByte = Base64.decode(pngStr);

			try (ByteArrayInputStream bis = new ByteArrayInputStream(imageByte)) {
				image = ImageIO.read(bis);
			} catch (IOException e) {
				Log.debug(e);
			}

			if (image != null) {
				MyImageD img = new MyImageD(image);
				addExternalImage(filename0, img);
			}
		} else if (urlBase64.startsWith("<svg")
				|| urlBase64.startsWith("<?xml")) {

			MyImageD img = new MyImageD(urlBase64);

			addExternalImage(filename0, img);

		} else {
			Log.debug(urlBase64.substring(0, 10) + " not supported");
		}

	}

	@Override
	public void setImageForFillable(Kernel kernel, GeoText geo, GeoElement fillable) {
		if (fillableImgs.isEmpty()) {
			fillImages();
		}
		String imgName = geo.getTextStringSafe();
		String fileName = fillableImgs.get(imgName) != null
				? fillableImgs.get(imgName).getFilename() : "";
		if (!fileName.isEmpty()) {
			fillable.setImageFileName(fileName);
			fillable.setAlphaValue(1.0f);
			fillable.updateVisualStyleRepaint(GProperty.HATCHING);
		}
	}

	private void fillImages() {
		fillableImgs.put("pause", GuiResourcesD.FILLING_PAUSE);
		fillableImgs.put("play", GuiResourcesD.FILLING_PLAY);
		fillableImgs.put("stop", GuiResourcesD.FILLING_STOP);
		fillableImgs.put("replay", GuiResourcesD.FILLING_REPLAY);
		fillableImgs.put("skip_next", GuiResourcesD.FILLING_SKIP_NEXT);
		fillableImgs.put("skip_previous", GuiResourcesD.FILLING_SKIP_PREVIOUS);
		fillableImgs.put("loop", GuiResourcesD.FILLING_LOOP);
		fillableImgs.put("zoom_in", GuiResourcesD.FILLING_ZOOM_IN);
		fillableImgs.put("zoom_out", GuiResourcesD.FILLING_ZOOM_OUT);
		fillableImgs.put("close", GuiResourcesD.FILLING_CLOSE);
		fillableImgs.put("arrow_up", GuiResourcesD.FILLING_ARROW_UP);
		fillableImgs.put("arrow_down", GuiResourcesD.FILLING_ARROW_DOWN);
		fillableImgs.put("arrow_back", GuiResourcesD.FILLING_ARROW_BACK);
		fillableImgs.put("arrow_forward", GuiResourcesD.FILLING_ARROW_FORWARD);
		fillableImgs.put("fast_forward", GuiResourcesD.FILLING_FAST_FORWARD);
		fillableImgs.put("fast_rewind", GuiResourcesD.FILLING_FAST_REWIND);
		fillableImgs.put("zoom_to_fit", GuiResourcesD.FILLING_ZOOM_TO_FIT);
		fillableImgs.put("center_view", GuiResourcesD.FILLING_CENTER_VIEW);
		fillableImgs.put("help", GuiResourcesD.FILLING_HELP);
		fillableImgs.put("settings", GuiResourcesD.FILLING_SETTINGS);
		fillableImgs.put("undo", GuiResourcesD.UNDO);
		fillableImgs.put("redo", GuiResourcesD.REDO);
		fillableImgs.put("remove", GuiResourcesD.REMOVE);
		fillableImgs.put("add", GuiResourcesD.ADD);
		fillableImgs.put("check_mark", GuiResourcesD.CHECK_MARK);
	}

	public double getPixelRatio() {
		return pixelRatio;
	}

	/**
	 * @param graphicsConfiguration graphics configuration
	 * @return whether pixel ratio changed
	 */
	public boolean updatePixelRatio(GraphicsConfiguration graphicsConfiguration) {
		if (graphicsConfiguration == null) {
			return false; // don't log NPE in tests
		}
		try {
			double oldRatio = pixelRatio;
			pixelRatio = graphicsConfiguration.getDefaultTransform().getScaleX();
			if (pixelRatio != oldRatio) {
				iconTable.clear();
				return true;
			}
		} catch (RuntimeException ex) {
			Log.debug(ex);
		}
		return false;
	}

	/**
	 * @param icon unscaled icon
	 * @param maxSize maximum size in pixels (assuming width == height)
	 * @return icon respecting pixel ratio
	 */
	public ScaledIcon getResponsiveScaledIcon(ImageIcon icon, int maxSize) {
		int maxScaledSize = (int) (maxSize * getPixelRatio());
		return new ScaledIcon(ImageManagerD.getScaledIcon(icon,
				Math.min(icon.getIconWidth(), maxScaledSize),
				Math.min(icon.getIconHeight(), maxScaledSize)),

				getPixelRatio());
	}
}
