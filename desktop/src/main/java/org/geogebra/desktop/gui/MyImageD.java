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
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.CheckForNull;
import javax.imageio.ImageIO;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.MyImage;
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
import com.kitfox.svg.xml.StyleAttribute;

public class MyImageD implements MyImageJre {

	private final Image img;
	private SVGDiagram diagram;
	private URI uri;
	// SVG as XML
	private final StringBuilder svg;
	private MyImageD tinted;
	private @CheckForNull GColor color;

	/**
	 * @param img bitmap image
	 */
	public MyImageD(Image img) {
		this.img = img;
		this.svg = null;
	}

	/**
	 * Load SVG from String
	 * 
	 * @param svgStr SVG content
	 * @param name name
	 */
	public MyImageD(String svgStr, String name) {
		svg = new StringBuilder(svgStr.length());
		svg.append(svgStr);
		img = null;
		InputStream stream = new ByteArrayInputStream(svgStr.getBytes(Charsets.getUtf8()));

		SVGUniverse universe = SVGCache.getSVGUniverse();
		try {
			uri = universe.loadSVG(stream, name);
			diagram = universe.getDiagram(uri);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private MyImageD(StringBuilder svgStr, SVGDiagram diagram, URI uri) {
		this.svg = svgStr;
		this.diagram = diagram;
		this.img = null;
		this.uri = uri;
	}

	/**
	 * @return MD5 hash of the content
	 */
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

	/**
	 * Loads internal image as SVG
	 * @param filename internal path (/org/geogebra/...)
	 * @return SVG image
	 */
	public static MyImageD loadAsSvg(String filename) {
		return loadAsSvg(MyImageD.class.getResourceAsStream(filename),
				MyImageD.class.getResource(filename));
	}

	private static MyImageD loadAsSvg(InputStream in, URL url) {
		StringBuilder svg = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(in, Charsets.getUtf8()))) {
			for (String line = reader
					.readLine(); line != null; line = reader.readLine()) {
				svg.append(line);
				svg.append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		svg = new StringBuilder(ImageManager.fixSVG(svg.toString()));
		SVGUniverse universe = SVGCache.getSVGUniverse();
		URI uri = universe.loadSVG(url);
		SVGDiagram diagram = universe.getDiagram(uri);
		return new MyImageD(svg, diagram, uri);
	}

	/**
	 * @param imageFile image to load
	 * @throws IOException when I/O problem occurs
	 * @return SVG image
	 */
	public static MyImageD load(File imageFile) throws IOException {
		if (StringUtil.toLowerCaseUS(imageFile.getName()).endsWith(".svg")) {
			return loadAsSvg(Files.newInputStream(imageFile.toPath()), imageFile.toURI().toURL());
		} else {
			return new MyImageD(ImageIO.read(imageFile));
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

	/**
	 * @param file file
	 * @param fileName filename
	 * @return image
	 * @throws IOException when I/O problem occurs
	 */
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

	@Override
	public MyImage tint(GColor color, Runnable onLoad) {
		if (svg == null) {
			return this;
		}
		if (tinted == null || !Objects.equals(tinted.color, color)) {
			if (tinted != null) {
				SVGCache.getSVGUniverse().removeDocument(tinted.uri);
			}
			tinted = createTinted(color);
			tinted.color = color;
		}
		return tinted;
	}

	private MyImageD createTinted(GColor color) {
		SVGUniverse universe = SVGCache.getSVGUniverse();
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(svg.toString()
					.getBytes(Charsets.getUtf8()));
			URI uri = universe.loadSVG(stream, UUID.randomUUID() + "-tint.svg");
			SVGDiagram diagram = universe.getDiagram(uri);
			StyleAttribute fill = diagram.getRoot().getPresAbsolute("fill");
			fill.setStringValue(color.toString());
			return new MyImageD(svg, diagram, uri);
		} catch (Exception e) {
			Log.debug(e.getMessage());
		}
		return this;
	}

}
