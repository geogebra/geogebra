package org.freehep.graphicsio.pdf;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.freehep.graphicsio.ImageGraphics2D;
import org.freehep.graphicsio.raw.RawImageWriteParam;
import org.freehep.util.UserProperties;
import org.freehep.util.io.ASCII85OutputStream;
import org.freehep.util.io.ASCIIHexOutputStream;
import org.freehep.util.io.CountedByteOutputStream;
import org.freehep.util.io.FinishableOutputStream;
import org.freehep.util.io.FlateOutputStream;

/**
 * This class allows you to write/print into a PDFStream. Several methods are
 * available to specify the content of a page, image. This class performs some
 * error checking, while writing the stream.
 * <p>
 * The stream allows to write dictionary entries. The /Length entry is written
 * automatically, referencing an object which will also be written just after
 * the stream is closed and the length is calculated.
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFStream.java,v 1.7 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFStream extends PDFDictionary implements PDFConstants {

	private String name;

	private PDFObject object;

	private boolean dictionaryOpen;

	private OutputStream[] stream;

	private CountedByteOutputStream byteCountStream;

	private String[] encode;

	PDFStream(PDF pdf, PDFByteWriter writer, String name, PDFObject parent,
			String[] encode) throws IOException {
		super(pdf, writer);
		this.name = name;
		object = parent;
		if (object == null) {
			System.err.println(
					"PDFWriter: 'PDFStream' cannot have a null parent");
		}
		// first write the dictionary
		dictionaryOpen = true;
		this.encode = encode;
	}

	/**
	 * Starts the stream, writes out the filters using the preset encoding, and
	 * encodes the stream.
	 */
	private void startStream() throws IOException {
		startStream(encode);
	}

	/**
	 * Starts the stream, writes out the filters using the given encoding, and
	 * encodes the stream.
	 */
	private void startStream(String[] encode) throws IOException {
		if (dictionaryOpen) {
			PDFName[] filters = decodeFilters(encode);
			if (filters != null) {
				entry("Filter", filters);
			}

			super.close();
			dictionaryOpen = false;
			out.printPlain("stream\n");

			byteCountStream = new CountedByteOutputStream(out);
			stream = openFilters(byteCountStream, encode);
		}
	}

	private void write(int b) throws IOException {
		startStream();
		stream[0].write(b);
	}

	private void write(byte[] b) throws IOException {
		for (int i = 0; i < b.length; i++) {
			write(b[i]);
		}
	}

	private static PDFName[] decodeFilters(String[] encode) {
		PDFName[] filters = null;
		if ((encode != null) && (encode.length != 0)) {
			filters = new PDFName[encode.length];
			for (int i = 0; i < filters.length; i++) {
				filters[i] = new PDFName(
						encode[encode.length - i - 1] + "Decode");
			}
		}
		return filters;
	}

	// open new stream using Standard Filters (see table 3.5)
	// stream[0] is the one to write to, the last one is s
	private static OutputStream[] openFilters(OutputStream s,
			String[] filters) {
		OutputStream[] os;
		if ((filters != null) && (filters.length != 0)) {
			os = new OutputStream[filters.length + 1];
			os[os.length - 1] = s;
			for (int i = os.length - 2; i >= 0; i--) {
				if (filters[i].equals("ASCIIHex")) {
					os[i] = new ASCIIHexOutputStream(os[i + 1]);
				} else if (filters[i].equals("ASCII85")) {
					os[i] = new ASCII85OutputStream(os[i + 1]);
				} else if (filters[i].equals("Flate")) {
					os[i] = new FlateOutputStream(os[i + 1]);
				} else if (filters[i].equals("DCT")) {
					os[i] = os[i + 1];
				} else {
					System.err.println(
							"PDFWriter: unknown stream filter: " + filters[i]);
				}
			}
		} else {
			os = new OutputStream[1];
			os[0] = s;
		}
		return os;
	}

	// stream[0] is the first one to finish, the last one is not finished
	private static void closeFilters(OutputStream[] s) throws IOException {
		for (int i = 0; i < s.length - 1; i++) {
			s[i].flush();
			if (s[i] instanceof FinishableOutputStream) {
				((FinishableOutputStream) s[i]).finish();
			}
		}
		s[s.length - 1].flush();
	}

	private void write(String s) throws IOException {
		byte[] b = s.getBytes("ISO-8859-1");
		for (int i = 0; i < b.length; i++) {
			write(b[i]);
		}
	}

	@Override
	void close() throws IOException {
		closeFilters(stream);
		stream = null;
		out.printPlain("\nendstream");
		out.println();
		object.close();
	}

	String getName() {
		return name;
	}

	public int getLength() {
		return byteCountStream.getCount();
	}

	public void print(String s) throws IOException {
		write(s);
	}

	public void println(String s) throws IOException {
		write(s);
		write(EOL);
	}

	public void comment(String comment) throws IOException {
		println("% " + comment);
	}

	// ==========================================================================
	// PDFStream Operators according to Table 4.1
	// ==========================================================================

	//
	// Graphics State operators (see Table 4.7)
	//
	private int gStates = 0;

	public void save() throws IOException {
		println("q");
		gStates++;
	}

	public void restore() throws IOException {
		if (gStates <= 0) {
			System.err.println("PDFStream: unbalanced saves()/restores()");
		}
		gStates--;
		println("Q");
	}

	public void matrix(AffineTransform xform) throws IOException {
		matrix(xform.getScaleX(), xform.getShearY(), xform.getShearX(),
				xform.getScaleY(), xform.getTranslateX(),
				xform.getTranslateY());
	}

	public void matrix(double m00, double m10, double m01, double m11,
			double m02, double m12) throws IOException {
		println(PDFUtil.fixedPrecision(m00) + " " + PDFUtil.fixedPrecision(m10)
				+ " " + PDFUtil.fixedPrecision(m01) + " "
				+ PDFUtil.fixedPrecision(m11) + " "
				+ PDFUtil.fixedPrecision(m02) + " "
				+ PDFUtil.fixedPrecision(m12) + " cm");
	}

	public void width(double width) throws IOException {
		println(PDFUtil.fixedPrecision(width) + " w");
	}

	public void cap(int capStyle) throws IOException {
		println(capStyle + " J");
	}

	public void join(int joinStyle) throws IOException {
		println(joinStyle + " j");
	}

	public void mitterLimit(double limit) throws IOException {
		println(PDFUtil.fixedPrecision(limit) + " M");
	}

	public void dash(int[] dash, double phase) throws IOException {
		print("[");
		for (int i = 0; i < dash.length; i++) {
			print(" " + PDFUtil.fixedPrecision(dash[i]));
		}
		println("] " + PDFUtil.fixedPrecision(phase) + " d");
	}

	public void dash(float[] dash, double phase) throws IOException {
		print("[");
		for (int i = 0; i < dash.length; i++) {
			print(" " + PDFUtil.fixedPrecision(dash[i]));
		}
		println("] " + PDFUtil.fixedPrecision(phase) + " d");
	}

	public void flatness(double flatness) throws IOException {
		println(PDFUtil.fixedPrecision(flatness) + " i");
	}

	public void state(PDFName stateDictionary) throws IOException {
		println(stateDictionary + " gs");
	}

	//
	// Path Construction operators (see Table 4.9)
	//
	public void cubic(double x1, double y1, double x2, double y2, double x3,
			double y3) throws IOException {
		println(PDFUtil.fixedPrecision(x1) + " " + PDFUtil.fixedPrecision(y1)
				+ " " + PDFUtil.fixedPrecision(x2) + " "
				+ PDFUtil.fixedPrecision(y2) + " " + PDFUtil.fixedPrecision(x3)
				+ " " + PDFUtil.fixedPrecision(y3) + " c");
	}

	public void cubicV(double x2, double y2, double x3, double y3)
			throws IOException {
		println(PDFUtil.fixedPrecision(x2) + " " + PDFUtil.fixedPrecision(y2)
				+ " " + PDFUtil.fixedPrecision(x3) + " "
				+ PDFUtil.fixedPrecision(y3) + " v");
	}

	public void cubicY(double x1, double y1, double x3, double y3)
			throws IOException {
		println(PDFUtil.fixedPrecision(x1) + " " + PDFUtil.fixedPrecision(y1)
				+ " " + PDFUtil.fixedPrecision(x3) + " "
				+ PDFUtil.fixedPrecision(y3) + " y");
	}

	public void move(double x, double y) throws IOException {
		println(PDFUtil.fixedPrecision(x) + " " + PDFUtil.fixedPrecision(y)
				+ " m");
	}

	public void line(double x, double y) throws IOException {
		println(PDFUtil.fixedPrecision(x) + " " + PDFUtil.fixedPrecision(y)
				+ " l");
	}

	public void closePath() throws IOException {
		println("h");
	}

	public void rectangle(double x, double y, double width, double height)
			throws IOException {
		println(PDFUtil.fixedPrecision(x) + " " + PDFUtil.fixedPrecision(y)
				+ " " + PDFUtil.fixedPrecision(width) + " "
				+ PDFUtil.fixedPrecision(height) + " re");
	}

	//
	// Path Painting operators (see Table 4.10)
	//
	public void stroke() throws IOException {
		println("S");
	}

	public void closeAndStroke() throws IOException {
		println("s");
	}

	public void fill() throws IOException {
		println("f");
	}

	public void fillEvenOdd() throws IOException {
		println("f*");
	}

	public void fillAndStroke() throws IOException {
		println("B");
	}

	public void fillEvenOddAndStroke() throws IOException {
		println("B*");
	}

	public void closeFillAndStroke() throws IOException {
		println("b");
	}

	public void closeFillEvenOddAndStroke() throws IOException {
		println("b*");
	}

	public void endPath() throws IOException {
		println("n");
	}

	//
	// Clipping Path operators (see Table 4.11)
	//
	public void clip() throws IOException {
		println("W");
	}

	public void clipEvenOdd() throws IOException {
		println("W*");
	}

	//
	// Text Object operators (see Table 5.4)
	//
	private boolean textOpen = false;

	public void beginText() throws IOException {
		if (textOpen) {
			System.err.println("PDFStream: nested beginText() not allowed.");
		}
		println("BT");
		textOpen = true;
	}

	public void endText() throws IOException {
		if (!textOpen) {
			System.err.println(
					"PDFStream: unbalanced use of beginText()/endText().");
		}
		println("ET");
		textOpen = false;
	}

	//
	// Text State operators (see Table 5.2)
	//
	public void charSpace(double charSpace) throws IOException {
		println(PDFUtil.fixedPrecision(charSpace) + " Tc");
	}

	public void wordSpace(double wordSpace) throws IOException {
		println(PDFUtil.fixedPrecision(wordSpace) + " Tw");
	}

	public void scale(double scale) throws IOException {
		println(PDFUtil.fixedPrecision(scale) + " Tz");
	}

	public void leading(double leading) throws IOException {
		println(PDFUtil.fixedPrecision(leading) + " TL");
	}

	private boolean fontWasSet = false;

	public void font(PDFName fontName, double size) throws IOException {
		println(fontName + " " + PDFUtil.fixedPrecision(size) + " Tf");
		fontWasSet = true;
	}

	public void rendering(int mode) throws IOException {
		println(mode + " Tr");
	}

	public void rise(double rise) throws IOException {
		println(PDFUtil.fixedPrecision(rise) + " Ts");
	}

	//
	// Text Positioning operators (see Table 5.5)
	//
	public void text(double x, double y) throws IOException {
		println(PDFUtil.fixedPrecision(x) + " " + PDFUtil.fixedPrecision(y)
				+ " Td");
	}

	public void textLeading(double x, double y) throws IOException {
		println(PDFUtil.fixedPrecision(x) + " " + PDFUtil.fixedPrecision(y)
				+ " TD");
	}

	public void textMatrix(double a, double b, double c, double d, double e,
			double f) throws IOException {
		println(PDFUtil.fixedPrecision(a) + " " + PDFUtil.fixedPrecision(b)
				+ " " + PDFUtil.fixedPrecision(c) + " "
				+ PDFUtil.fixedPrecision(d) + " " + PDFUtil.fixedPrecision(e)
				+ " " + PDFUtil.fixedPrecision(f) + " Tm");
	}

	public void textLine() throws IOException {
		println("T*");
	}

	//
	// Text Showing operators (see Table 5.6)
	//
	public void show(String text) throws IOException {
		if (!fontWasSet) {
			System.err.println(
					"PDFStream: cannot use Text Showing operator before font is set.");
		}
		if (!textOpen) {
			System.err.println(
					"PDFStream: Text Showing operator only allowed inside Text section.");
		}
		println("(" + PDFUtil.escape(text) + ") Tj");
	}

	public void showLine(String text) throws IOException {
		if (!fontWasSet) {
			System.err.println(
					"PDFStream: cannot use Text Showing operator before font is set.");
		}
		if (!textOpen) {
			System.err.println(
					"PDFStream: Text Showing operator only allowed inside Text section.");
		}
		println("(" + PDFUtil.escape(text) + ") '");
	}

	public void showLine(double wordSpace, double charSpace, String text)
			throws IOException {
		if (!fontWasSet) {
			System.err.println(
					"PDFStream: cannot use Text Showing operator before font is set.");
		}
		if (!textOpen) {
			System.err.println(
					"PDFStream: Text Showing operator only allowed inside Text section.");
		}
		println(PDFUtil.fixedPrecision(wordSpace) + " "
				+ PDFUtil.fixedPrecision(charSpace) + " ("
				+ PDFUtil.escape(text) + ") \"");
	}

	public void show(Object[] array) throws IOException {
		print("[");
		for (int i = 0; i < array.length; i++) {
			Object object = array[i];
			if (object instanceof String) {
				print(" (" + PDFUtil.escape(object.toString()) + ")");
			} else if (object instanceof Integer) {
				print(" " + ((Integer) object).intValue());
			} else if (object instanceof Double) {
				print(" " + ((Double) object).doubleValue());
			} else {
				System.err.println(
						"PDFStream: input array of operator TJ may only contain objects of type 'String', 'Integer' or 'Double'");
			}
		}
		println("] TJ");
	}

	//
	// Type 3 Font operators (see Table 5.10)
	//
	public void glyph(double wx, double wy) throws IOException {
		println(PDFUtil.fixedPrecision(wx) + " " + PDFUtil.fixedPrecision(wy)
				+ " d0");
	}

	public void glyph(double wx, double wy, double llx, double lly, double urx,
			double ury) throws IOException {
		println(PDFUtil.fixedPrecision(wx) + " " + PDFUtil.fixedPrecision(wy)
				+ " " + PDFUtil.fixedPrecision(llx) + " "
				+ PDFUtil.fixedPrecision(lly) + " "
				+ PDFUtil.fixedPrecision(urx) + " "
				+ PDFUtil.fixedPrecision(ury) + " d1");
	}

	//
	// Color operators (see Table 4.21)
	//
	public void colorSpace(PDFName colorSpace) throws IOException {
		println(colorSpace + " cs");
	}

	public void colorSpaceStroke(PDFName colorSpace) throws IOException {
		println(colorSpace + " CS");
	}

	public void colorSpace(double[] color) throws IOException {
		for (int i = 0; i < color.length; i++) {
			print(" " + color[i]);
		}
		println(" scn");
	}

	public void colorSpaceStroke(double[] color) throws IOException {
		for (int i = 0; i < color.length; i++) {
			print(" " + color[i]);
		}
		println(" SCN");
	}

	public void colorSpace(double[] color, PDFName name) throws IOException {
		if (color != null) {
			for (int i = 0; i < color.length; i++) {
				print(PDFUtil.fixedPrecision(color[i]) + " ");
			}
		}
		println(name + " scn");
	}

	public void colorSpaceStroke(double[] color, PDFName name)
			throws IOException {
		if (color != null) {
			for (int i = 0; i < color.length; i++) {
				print(PDFUtil.fixedPrecision(color[i]) + " ");
			}
		}
		println(name + " SCN");
	}

	public void colorSpace(double g) throws IOException {
		println(PDFUtil.fixedPrecision(g) + " g");
	}

	public void colorSpaceStroke(double g) throws IOException {
		println(PDFUtil.fixedPrecision(g) + " G");
	}

	public void colorSpace(double r, double g, double b) throws IOException {
		println(PDFUtil.fixedPrecision(r) + " " + PDFUtil.fixedPrecision(g)
				+ " " + PDFUtil.fixedPrecision(b) + " rg");
	}

	public void colorSpaceStroke(double r, double g, double b)
			throws IOException {
		println(PDFUtil.fixedPrecision(r) + " " + PDFUtil.fixedPrecision(g)
				+ " " + PDFUtil.fixedPrecision(b) + " RG");
	}

	public void colorSpace(double c, double m, double y, double k)
			throws IOException {
		println(PDFUtil.fixedPrecision(c) + " " + PDFUtil.fixedPrecision(m)
				+ " " + PDFUtil.fixedPrecision(y) + " "
				+ PDFUtil.fixedPrecision(k) + " k");
	}

	public void colorSpaceStroke(double c, double m, double y, double k)
			throws IOException {
		println(PDFUtil.fixedPrecision(c) + " " + PDFUtil.fixedPrecision(m)
				+ " " + PDFUtil.fixedPrecision(y) + " "
				+ PDFUtil.fixedPrecision(k) + " K");
	}

	//
	// Shading Pattern operator (see Table 4.24)
	//
	public void shade(PDFName name) throws IOException {
		println(name + " sh");
	}

	/**
	 * Image convenience function (see Table 4.35). Ouputs the data of the image
	 * using "DeviceRGB" colorspace, and the requested encodings
	 */
	public void image(RenderedImage image, Color bkg, String[] encode)
			throws IOException {
		byte[] imageBytes = imageToBytes(image, bkg, encode);
		PDFName[] filters = decodeFilters(encode);

		entry("Width", image.getWidth());
		entry("Height", image.getHeight());
		entry("ColorSpace", pdf.name("DeviceRGB"));
		entry("BitsPerComponent", 8);
		entry("Filter", filters);

		startStream(null);
		write(imageBytes);
	}

	public void imageMask(RenderedImage image, String[] encode)
			throws IOException {
		// FIXME hardcoded to A85, Flate
		PDFName[] filters = decodeFilters(new String[] { "Flate", "ASCII85" });
		entry("Width", image.getWidth());
		entry("Height", image.getHeight());
		entry("BitsPerComponent", 8);
		entry("ColorSpace", pdf.name("DeviceGray"));
		entry("Filter", filters);

		startStream(null);
		// FIXME hardcoded to A85, Flate
		ASCII85OutputStream a85 = new ASCII85OutputStream(stream[0]);
		FlateOutputStream imageStream = new FlateOutputStream(a85);
		UserProperties props = new UserProperties();
		props.setProperty(RawImageWriteParam.BACKGROUND, (Color) null);
		props.setProperty(RawImageWriteParam.CODE, "A");
		props.setProperty(RawImageWriteParam.PAD, 1);
		ImageGraphics2D.writeImage(image, "raw", props, imageStream);
		imageStream.finish();
		a85.finish();
	}

	/**
	 * Inline Image convenience function (see Table 4.39 and 4.40). Ouputs the
	 * data of the image using "DeviceRGB" colorspace, and the requested
	 * encoding.
	 */
	public void inlineImage(RenderedImage image, Color bkg, String[] encode)
			throws IOException {
		byte[] imageBytes = imageToBytes(image, bkg, encode);

		println("BI");
		imageInfo("Width", image.getWidth());
		imageInfo("Height", image.getHeight());
		imageInfo("ColorSpace", pdf.name("DeviceRGB"));
		imageInfo("BitsPerComponent", 8);

		PDFName[] filters = decodeFilters(encode);
		imageInfo("Filter", filters);
		print("ID\n");

		write(imageBytes);

		println("\nEI");
	}

	/**
	 * Recursive method which returns the bytes of an image in the given
	 * encoding[0], unless the encoding[0] is null, in which case it returns the
	 * bytes and sets the encoding[0] to the encoding which gives the smallest
	 * image. If the image is transparent, Flate encoding is used by default.
	 */
	private byte[] imageToBytes(RenderedImage image, Color bkg, String[] encode)
			throws IOException {
		if (encode[0] == null) {
			if (image.getColorModel().hasAlpha() && (bkg == null)) {
				encode[0] = "Flate";
				return imageToBytes(image, null, encode);
			}

			// return the smallest
			encode[0] = "Flate";
			byte[] zlibBytes = imageToBytes(image, bkg, encode);
			encode[0] = "DCT";
			byte[] jpgBytes = imageToBytes(image, bkg, encode);

			if (jpgBytes.length < 0.5 * zlibBytes.length) {
				encode[0] = "DCT";
				return jpgBytes;
			}
			encode[0] = "Flate";
			return zlibBytes;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// FIXME hardcoded to A85
		ASCII85OutputStream a85 = new ASCII85OutputStream(baos);
		OutputStream imageStream;
		if (encode[0].equalsIgnoreCase("Flate")) {
			imageStream = new FlateOutputStream(a85);
			UserProperties props = new UserProperties();
			props.setProperty(RawImageWriteParam.BACKGROUND, bkg);
			props.setProperty(RawImageWriteParam.CODE, "RGB");
			props.setProperty(RawImageWriteParam.PAD, 1);
			ImageGraphics2D.writeImage(image, "raw", props, imageStream);
		} else {
			imageStream = a85;
			ImageGraphics2D.writeImage(image, "jpg", new Properties(),
					imageStream);
		}
		imageStream.close();
		a85.close();
		baos.close();
		return baos.toByteArray();
	}

	//
	// In-line Image operators (see Table 4.38)
	//
	private void imageInfo(String key, int number) throws IOException {
		println("/" + key + " " + number);
	}

	private void imageInfo(String key, PDFName name) throws IOException {
		println("/" + key + " " + name);
	}

	private void imageInfo(String key, Object[] array) throws IOException {
		print("/" + key + " [");
		for (int i = 0; i < array.length; i++) {
			print(" " + array[i]);
		}
		println("]");
	}

	/**
	 * Draws the <i>points</i> of the shape using path <i>construction</i>
	 * operators. The path is neither stroked nor filled.
	 * 
	 * @return true if even-odd winding rule should be used, false if non-zero
	 *         winding rule should be used.
	 */
	public boolean drawPath(Shape s) throws IOException {
		PDFPathConstructor path = new PDFPathConstructor(this);
		return path.addPath(s);
	}

	//
	// XObject operators (see Table 4.34)
	//
	public void xObject(PDFName name) throws IOException {
		println(name + " Do");
	}

	//
	// Marked Content operators (see Table 8.5)
	//
	// FIXME: missing all

	//
	// Compatibility operators (see Table 3.19)
	//
	private boolean compatibilityOpen = false;

	public void beginCompatibility() throws IOException {
		if (compatibilityOpen) {
			System.err.println(
					"PDFStream: nested use of Compatibility sections not allowed.");
		}
		println("BX");
		compatibilityOpen = true;
	}

	public void endCompatibility() throws IOException {
		if (!compatibilityOpen) {
			System.err.println(
					"PDFStream: unbalanced use of begin/endCompatibilty().");
		}
		println("EX");
		compatibilityOpen = false;
	}

}
