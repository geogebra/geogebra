package org.geogebra.desktop.gui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.jre.gui.MyImageJre;
import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.gui.util.JSVGImageBuilder;
import org.geogebra.desktop.gui.util.SVGImage;
import org.geogebra.desktop.main.AppD;

public class MyImageD implements MyImageJre {

	public static final double DELTA = 0;
	private Image img = null;
	// SVG as XML
	private StringBuilder sb;
	private SVGImage svgImage;

	/**
	 * @param img bitmap image
	 */
	public MyImageD(Image img) {
		this.img = img;
		this.sb = null;

	}

	/**
	 * Load SVG from String
	 * @param svgContent SVG content
	 */
	public MyImageD(String svgContent) {
		sb = new StringBuilder(svgContent.length());
		sb.append(svgContent);
		svgImage = JSVGImageBuilder.fromContent(sb.toString());
	}

	private MyImageD(SVGImage svgImage) {
		this.svgImage = svgImage;
	}

	/**
	 * @return MD5 hash of the content
	 */
	public String getMD5() {
		if (img == null) {
			return AppD.md5EncryptStatic(svgImage.toString());
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
	 * @param filename internal pah (/org/geogebra/...)
	 * @return SVG image
	 */
	public static MyImageD loadAsSvg(String filename) {
		return loadAsSvg(MyImageD.class.getResourceAsStream(filename),
				MyImageD.class.getResource(filename));
	}

	private static MyImageD loadAsSvg(InputStream in, URL url) {
		StringBuilder svgSb = new StringBuilder();
		try {
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(in, StandardCharsets.UTF_8))) {
				for (String line = reader
						.readLine(); line != null; line = reader.readLine()) {
					svgSb.append(line);
					svgSb.append('\n');
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		svgSb = new StringBuilder(ImageManager.fixSVG(svgSb.toString()));
		return new MyImageD(svgSb.toString());
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
		return svgImage != null;
	}

	@Override
	public int getHeight() {
		if (img != null) {
			return img.getHeight(null);
		}

		if (svgImage != null) {
			return (int) (svgImage.getHeight() + 0.5);
		}

		return 1;
	}

	@Override
	public int getWidth() {
		if (img != null) {
			return img.getWidth(null);
		}

		if (svgImage != null) {
			return (int) (svgImage.getWidth() + DELTA);
		}

		return 1;
	}

	@Override
	public GGraphics2D createGraphics() {
		return new GGraphics2DD((Graphics2D) img.getGraphics());
	}

	@Override
	public String getSVG() {
		return svgImage.getContent();
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
			return new MyImageD(JSVGImageBuilder.fromFile(file));
		}

		// returns null if the file isn't an image
		BufferedImage bi = ImageIO.read(file);

		if (bi != null) {
			return new MyImageD(bi);
		}
		return null;
	}

	@Override
	public MyImage tintedSVG(GColor color, Runnable onLoad) {
		return new MyImageD(svgImage.tint(color));
	}

	public void render(Graphics2D impl, int x, int y) {
		if (isSVG()) {
			renderSvg(impl, x, y);
		} else {
			impl.drawImage(img, x, y, null);
		}
	}

	private void renderSvg(Graphics2D g, int x, int y) {
		g.translate(x, y);
		svgImage.paint(g);
		g.translate(-x, -y);
	}

	public void render(Graphics2D impl, int sx, int sy, int sw, int sh, int dx, int dy, int dw,
			int dh) {
		if (isSVG()) {
			renderSvg(impl, dx, dy);
		} else {
			impl.drawImage(
					img, dx, dy, dx + dw, dy + dh,
					sx, sy, sx + sw, sy + sh, null);
		}
	}
}
