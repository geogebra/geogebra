package org.geogebra.desktop.gui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.awt.GGraphics2DD;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;

public class MyImageD implements MyImage {

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
		try {
			stream = new ByteArrayInputStream(svgStr.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
		}

		SVGUniverse universe = SVGCache.getSVGUniverse();
		URI uri;
		try {
			uri = universe.loadSVG(stream, name);
			diagram = universe.getDiagram(uri);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getMD5() {

		if (img == null) {
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("MD5");
				byte[] md5hash = new byte[32];
				md.update(svg.toString().getBytes());
				md5hash = md.digest();
				return StringUtil.convertToHex(md5hash);
			} catch (NoSuchAlgorithmException e) {
				App.error("MD5 Error");
				return "svg" + UUID.randomUUID();
			}
		}

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (img == null) {
				App.error("image==null");
			} else {
				ImageIO.write((BufferedImage) img, "png", baos);
				byte[] fileData = baos.toByteArray();

				MessageDigest md;
				md = MessageDigest.getInstance("MD5");
				byte[] md5hash = new byte[32];
				md.update(fileData, 0, fileData.length);
				md5hash = md.digest();
				return StringUtil.convertToHex(md5hash);
			}
		} catch (Exception e) {
			//
		}

		App.error("MD5 Error");
		return "image" + UUID.randomUUID();

	}

	public void load(File imageFile) throws IOException {

		if (imageFile.getName().toLowerCase(Locale.US).endsWith(".svg")) {

			svg = new StringBuilder((int) imageFile.length());
			BufferedReader reader = new BufferedReader(
					new FileReader(imageFile));
			for (String line = reader.readLine(); line != null; line = reader
					.readLine()) {
				svg.append(line);
				svg.append('\n');
			}

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

	public boolean isSVG() {
		return diagram != null;
	}

	public int getHeight() {
		if (img != null) {
			return img.getHeight(null);
		}

		if (diagram != null) {
			return (int) (diagram.getHeight() + 0.5);
		}

		return 1;
	}

	public int getWidth() {
		if (img != null) {
			return img.getWidth(null);
		}

		if (diagram != null) {
			return (int) (diagram.getWidth() + 0.5);
		}

		return 1;
	}

	public void drawSubimage(int startX, int startY, int imgWidth,
			int imgHeight, GGraphics2D g, int posX, int posY) {
		GGraphics2DD.getAwtGraphics(g).drawImage( 
				((BufferedImage) img).getSubimage(startX, startY, imgWidth,
						imgHeight),null,posX,posY);
	}

	public GGraphics2D createGraphics() {
		return new GGraphics2DD((Graphics2D) img.getGraphics());
	}

	public SVGDiagram getDiagram() {
		return diagram;
	}

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
				App.error("drawing svg failed");
			}
		}

	}

}
