package org.geogebra.desktop.gui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.jre.gui.MyImageJre;
import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.util.Charsets;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.UtilD;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;

public class MyImageD implements MyImageJre {

	private Image img;
	private SVGDiagram diagram;
	// SVG as XML
	private StringBuilder svg;

	public MyImageD(Image img) {
		this.img = img;
	}

	public MyImageD() {
	}

	/**
	 * Load SVG from String
	 * 
	 * @param svgStr
	 * @param name
	 */
	public MyImageD(String svgStr, String name) {
		svg = new StringBuilder(svgStr.length());
		svg.append(svgStr);

		InputStream stream = null;

		stream = new ByteArrayInputStream(svgStr.getBytes(Charsets.getUtf8()));

		SVGUniverse universe = SVGCache.getSVGUniverse();
		URI uri;
		try {
			uri = universe.loadSVG(stream, name);
			diagram = universe.getDiagram(uri);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getMD5() {

		if (img == null) {
			return AppD.md5EncryptStatic(svg.toString());
		}

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			ImageIO.write((BufferedImage) img, "png", baos);
			byte[] fileData = baos.toByteArray();

			MessageDigest md = AppD.getMd5Encrypter();

			md.update(fileData, 0, fileData.length);
			byte[] md5hash = md.digest();
			return StringUtil.convertToHex(md5hash);

		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.error("MD5 Error");
		return "image" + UUID.randomUUID();

	}

	public void load(File imageFile) throws IOException {

		if (StringUtil.toLowerCaseUS(imageFile.getName()).endsWith(".svg")) {

			svg = new StringBuilder((int) imageFile.length());
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(imageFile), Charsets.getUtf8()));
				for (String line = reader
						.readLine(); line != null; line = reader.readLine()) {
					svg.append(line);
					svg.append('\n');
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
			svg = new StringBuilder(ImageManager.fixSVG(svg.toString()));
			URL url = imageFile.toURI().toURL();
			SVGUniverse universe = SVGCache.getSVGUniverse();
			URI uri = universe.loadSVG(url);
			diagram = universe.getDiagram(uri);

		} else {
			img = ImageIO.read(imageFile);
		}
	}

	public Image getImage() {
		return img;
	}

	@Override
	public boolean hasNonNullImplementation() {
		return img != null;
	}

	@Override
	public boolean isSVG() {
		return diagram != null;
	}

	@Override
	public int getHeight() {
		if (img != null) {
			return img.getHeight(null);
		}

		if (diagram != null) {
			return (int) (diagram.getHeight() + 0.5);
		}

		return 1;
	}

	@Override
	public int getWidth() {
		if (img != null) {
			return img.getWidth(null);
		}

		if (diagram != null) {
			return (int) (diagram.getWidth() + 0.5);
		}

		return 1;
	}

	@Override
	public void drawSubimage(int startX, int startY, int imgWidth,
			int imgHeight, GGraphics2D g, int posX, int posY) {
		GGraphics2DD.getAwtGraphics(g).drawImage(((BufferedImage) img)
				.getSubimage(startX, startY, imgWidth, imgHeight), null, posX,
				posY);
	}

	@Override
	public GGraphics2D createGraphics() {
		return new GGraphics2DD((Graphics2D) img.getGraphics());
	}

	public SVGDiagram getDiagram() {
		return diagram;
	}

	@Override
	public String getSVG() {
		return svg.toString();
	}

	public void drawImage(Graphics2D g2, int x, int y, int width, int height) {
		if (img != null) {
			// bitmap
			g2.drawImage(img, x, y, width, height, null);
		} else {
			// SVG
			try {
				diagram.render(g2);
			} catch (Exception e) {
				Log.error("Drawing svg image failed");
			}
		}

	}

	@Override
	public String toLaTeXStringBase64() {
		if (!isSVG() && img instanceof BufferedImage) {
			BufferedImage bi = (BufferedImage) img;

			final ByteArrayOutputStream os = new ByteArrayOutputStream();

			try {
				ImageIO.write(bi, "PNG", os);
				return "\\imagebasesixtyfour{" + getWidth() + "}{" + getHeight()
						+ "}{" + StringUtil.pngMarker
						+ Base64.encodeToString(os.toByteArray(), false) + "}";
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		Log.error("problem converting image to base64");
		return "";

	}

	public static MyImageD fromFile(File file, String fileName)
			throws IOException {
		if (fileName.endsWith(".svg")) {

			FileInputStream is = new FileInputStream(file);
			String svg = UtilD.loadIntoString(is);
			is.close();

			return new MyImageD(svg, fileName);
		}
		// returns null if the file isn't an image
		BufferedImage bi = ImageIO.read(file);

		if (bi != null) {
			return new MyImageD(bi);
		}
		return null;
	}

}
