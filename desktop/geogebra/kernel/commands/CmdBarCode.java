package geogebra.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.TextValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.euclidian.EuclidianViewD;
import geogebra.main.AppD;
import geogebra.util.BarcodeFactory;

import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Locale;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * ToolImage
 */
public class CmdBarCode extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdBarCode(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 0:
			// decode barcode from active Graphics View
			BufferedImage image;

			EuclidianViewD ev = (EuclidianViewD) app.getActiveEuclidianView();
			if (ev.getSelectedWidth() > 600 || ev.getSelectedHeight() > 600) {
				// if it's too big, get scaled image
				image = ((AppD) app).getExportImage(600, 600);
				App.debug("600x600");
			} else {
				// otherwise get image at 1:1
				image = ev.getExportImage(1.0);
				App.debug("1.0");
			}

			/*
			r=Math.random();
			Application.debug(r);
			 File outputfile = new File("c:\\saved2"+r+".png"); try {
			 ImageIO.write(image, "png", outputfile); } catch (IOException e2)
			 { // TODO Auto-generated catch block
				 e2.printStackTrace(); }
			 */
			 

			return decode(image, c);

		case 1:

			if (!arg[0].isGeoText() && !arg[0].isNumberValue()) {
				if (!arg[0].isGeoImage()) {
					throw argErr(app, c.getName(), arg[0]);
				}

				image = geogebra.awt.GBufferedImageD
						.getAwtBufferedImage(((GeoImage) arg[0]).getFillImage());

				return decode(image, c);
			}

			// GeoText, GeoNumeric: fall through

		case 2:
		case 3:
		case 4:
		case 5:
			// defaults
			ErrorCorrectionLevel errorLevel = ErrorCorrectionLevel.H;
			BarcodeFormat barcodeFormat = BarcodeFormat.QR_CODE;
			int width = 100;
			int height = 100;
			String text;

			if (arg[0].isTextValue() && arg[0].isDefined()) {
				text = ((TextValue) arg[0]).toValueString(StringTemplate.defaultTemplate);
			} else if (arg[0].isNumberValue() && arg[0].isDefined()) {
				text = (Math.round(((NumberValue) arg[0]).getDouble())) + "";
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

			int i = 1;

			if (i < arg.length && !arg[i].isDefined()) {
				throw argErr(app, c.getName(), arg[i]);
			}

			boolean checksumNeeded = false;
			String formatText = null;
			if (i < arg.length && arg[i].isTextValue()) {
				TextValue format = (TextValue) arg[i];
				try {
					formatText = format.getText().toValueString(StringTemplate.defaultTemplate)
							.toUpperCase(Locale.US);
					barcodeFormat = BarcodeFormat.valueOf(formatText);
					checksumNeeded = formatText.startsWith("EAN")
							|| formatText.startsWith("UPC");
				} catch (Exception e) {
					// default already set
					// barcodeFormat = BarcodeFormat.QR_CODE;
				}
				i++;
			}

			if (i < arg.length && !arg[i].isDefined()) {
				throw argErr(app, c.getName(), arg[i]);
			}

			if (i < arg.length && arg[i].isTextValue()) {
				TextValue error = (TextValue) arg[i];
				String errorStr = error.getText().toValueString(StringTemplate.defaultTemplate)
						.toUpperCase(Locale.US);
				if (errorStr.length() > 0) {
					switch (errorStr.charAt(0)) {
					case 'L':
						errorLevel = ErrorCorrectionLevel.L;
						break;
					case 'M':
						errorLevel = ErrorCorrectionLevel.M;
						break;
					case 'Q':
						errorLevel = ErrorCorrectionLevel.Q;
						break;
					default:
						// errorLevel = ErrorCorrectionLevel.H;
					}
				}
				i++;
			}

			if (i < arg.length && !arg[i].isDefined()) {
				throw argErr(app, c.getName(), arg[i]);
			}

			if (i < arg.length && arg[i].isNumberValue()) {
				width = (int) ((NumberValue) arg[i]).getDouble();
				i++;
			}

			if (i < arg.length && !arg[i].isDefined()) {
				throw argErr(app, c.getName(), arg[i]);
			}

			if (i < arg.length && arg[i].isNumberValue()) {
				height = (int) ((NumberValue) arg[i]).getDouble();
				i++;
			}

			if (i != n) {
				throw argErr(app, c.getName(), arg[i]);
			}

			boolean allDigits = true;
			for (int j = 0; j < text.length(); j++) {
				if (text.charAt(j) < '0' || text.charAt(j) > '9') {
					allDigits = false;
					break;
				}
			}

			// try to guess correct format
			// if this fails, QR_CODE used
			if (formatText == null && allDigits) {
				// UPC_E not supported in zxing 1.7
				// if (text.length() <= 5) {
				// formatText = "UPC_E";
				// barcodeFormat = BarcodeFormat.valueOf(formatText);
				// checksumNeeded = true;
				// } else
				if (text.length() <= 8) {
					formatText = "EAN_8";
					barcodeFormat = BarcodeFormat.valueOf(formatText);
					checksumNeeded = true;
				} else if (text.length() <= 12) {
					formatText = "UPC_A";
					barcodeFormat = BarcodeFormat.valueOf(formatText);
					checksumNeeded = true;
				} else if (text.length() <= 13) {
					formatText = "EAN_13";
					barcodeFormat = BarcodeFormat.valueOf(formatText);
					checksumNeeded = true;
				}
			}

			MultiFormatWriter writer = new MultiFormatWriter();
			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.ERROR_CORRECTION, errorLevel);
			BitMatrix matrix;
			try {

				// add checksums for EAN and UPC barcodes (otherwise barcodes
				// won't decode)
				if (checksumNeeded) {
					if (formatText.equals("EAN_8")) {
						text = (text + "00000000").substring(0, 8);
						text = BarcodeFactory.addStandardUPCEANChecksum(text);
					} else if (formatText.equals("EAN_13")) {
						text = (text + "0000000000000").substring(0, 13);
						text = BarcodeFactory.addStandardUPCEANChecksum(text);
						// UPC_E not supported in zxing 1.7
						// } else if (formatText.equals("UPC_E")) {
						// text = (text+"00000").substring(0,5);
						// text =
						// BarcodeFactory.addStandardUPCEANChecksum(text);
					} else if (formatText.equals("UPC_A")) {
						// text = (text+"000000000000").substring(0,12);
						// 11 or 12 digits OK, less not OK
						if (text.length() < 11) {
							text = (text + "000000000000").substring(0, 12);
						}
						if (text.length() > 12) {
							text = text.substring(0, 12);
						}
						text = BarcodeFactory.addStandardUPCEANChecksum(text);
					}
				}

				matrix = writer.encode(text, barcodeFormat, width, height,
						hints);
				image = MatrixToImageWriter.toBufferedImage(matrix);
			} catch (Exception e1) {
				e1.printStackTrace();
				// some errors are OK
				// BarCode["12345","EAN_13"]
				// java.lang.IllegalArgumentException: Requested contents should
				// be 13 digits long, but got 5
				// at
				// com.google.zxing.oned.EAN13Writer.encode(EAN13Writer.java:50)

				// some are not too helpful
				// BarCode["123456789123a","EAN_13"]
				// java.lang.NumberFormatException: For input string: "a"
				// at
				// java.lang.NumberFormatException.forInputString(NumberFormatException.java:48)
				// at java.lang.Integer.parseInt(Integer.java:447)
				// at java.lang.Integer.parseInt(Integer.java:497)
				// at
				// com.google.zxing.oned.EAN13Writer.encode(EAN13Writer.java:73)

				app.showError(e1.getLocalizedMessage());
				GeoElement[] ret = {};
				return ret;
			}

			String fileName = ((AppD) app).createImage(image, "barcode"
					+ text + ".png");

			GeoImage geoImage = new GeoImage(app.getKernel().getConstruction());
			geoImage.setImageFileName(fileName);
			geoImage.setTooltipMode(GeoElement.TOOLTIP_OFF);

			boolean oldState = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			GeoPoint corner = new GeoPoint(cons, null, 0, 0, 1);
			cons.setSuppressLabelCreation(oldState);
			try {
				geoImage.setStartPoint(corner);
			} catch (CircularDefinitionException e) {
				// don't mind about this error
			}
			geoImage.setLabel(null);

			GeoElement[] ret2 = { geoImage };
			return ret2;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/*
	 * http://www.morovia.com/education/utility/upc-ean.asp
	 * http://code.google.com/p/zxing/wiki/DeveloperNotes
	 */
	private GeoElement[] decode(BufferedImage image, Command c) {

		Result result;

		try {
			//
			Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
			hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
			hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
			Reader reader = new MultiFormatReader();
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			result = reader.decode(bitmap, hints);
		} catch (Exception e) {
			e.printStackTrace();
			result = null;

		}

		// check if return geo exists, create it if not
		GeoElement retGeo = cons.getKernel().lookupLabel(c.getLabel());
		if (retGeo == null || !retGeo.isGeoText()) {
			retGeo = new GeoText(cons);
			retGeo.setLabel(c.getLabel());

		}

		if (result != null) {
			((GeoText) retGeo).setTextString(result.getText());
		} else {
			retGeo.setUndefined();
		}

		GeoElement[] ret = { retGeo };
		return ret;
	}
}
