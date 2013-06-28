package geogebra.common.kernel.algos;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.font.GTextLayout;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoCanvasImage;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoTransferFunction;
import geogebra.common.util.Unicode;

import java.util.List;

/**
 * Algo class for Nyquist and Bode diagram
 * 
 * @author Giuliano
 * 
 */
public class AlgoTransferFunction extends AlgoElement {

	private GeoFunction function;
	private GeoTransferFunction gcf;
	private GeoCanvasImage outputImage;
	private int offset = 30;
	private GGraphics2D g;
	private EuclidianViewInterfaceCommon view;
	private boolean nyquist;
	private double maxPhase;
	private double minPhase;
	private int maxDecibel;
	private int zeroMagnitude;
	private int zeroPhase;
	private int imageSize;
	private int stepXForOmega;
	private int stepY;
	private int lastDecibel;
	private int stepX;
	private int omegaStart;
	private GeoList num;
	private GeoList den;

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param num
	 *            list of coefficients of numerator
	 * @param den
	 *            list of coefficients of denominator
	 * @param omegaStart
	 *            value of omega for the interval [-omega;omega]
	 * 
	 * @param step
	 *            step for calculus of function
	 */
	public AlgoTransferFunction(Construction c, String label, GeoList num,
			GeoList den, int omegaStart, double step) {
		super(c);
		this.nyquist = true;
		gcf = new GeoTransferFunction(c, label, num, den, omegaStart, step);
		this.function = gcf.getGeoFunction();
		view = kernel.getApplication().getActiveEuclidianView();
		this.omegaStart = omegaStart;
		this.num = num;
		this.den = den;
		compute();
		setInputOutput();
	}

	/**
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param num
	 *            list of coefficients of numerator
	 * @param den
	 *            list of coefficients of denominator
	 * @param startBode
	 *            start value of omega
	 * @param endBode
	 * 			  end value of omega
	 */
	public AlgoTransferFunction(Construction c, String label, GeoList num,
			GeoList den, int startBode, int endBode) {
		super(c);
		this.nyquist = false;
		gcf = new GeoTransferFunction(c, label, num, den, startBode, endBode);
		this.function = gcf.getGeoFunction();
		view = kernel.getApplication().getActiveEuclidianView();
		this.num = num;
		this.den = den;
		compute();
		setInputOutput();
	}

	@Override
	protected void setInputOutput() {

		super.setOutputLength(1);
		if (nyquist) {
			input = new GeoElement[3];
			input[0] = num;
			input[1] = den;
			input[2] = new GeoNumeric(cons, omegaStart);
			super.setOutput(0, gcf);
		} else {
			input = new GeoElement[4];
			input[0] = num;
			input[1] = den;
			input[2] = new GeoNumeric(cons, gcf.getStartBode());
			input[3] = new GeoNumeric(cons, gcf.getEndBode());
			super.setOutput(0, outputImage);
		}
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return GeoFunction if Nyquist, GeoCanvasImage if Bode
	 */
	public GeoElement getResult() {
		return getOutput(0);
	}

	@Override
	public void compute() {
		gcf.evaluate();
		if (!nyquist) {
			outputImage = new GeoCanvasImage(cons, 460, 460);
			outputImage.setAbsoluteScreenLocActive(true);
			outputImage.setAbsoluteScreenLoc(view.getViewWidth() / 2
					- outputImage.getWidth() / 2, view.getViewHeight() / 2
					+ outputImage.getHeight() / 2);
			imageSize = outputImage.getHeight() - offset - offset;
			g = outputImage.getGraphics();
			g.setColor(GColor.white);
			g.fillRect(0, 0, outputImage.getWidth(), outputImage.getHeight());
			drawBode();
		}
	}

	private void drawBode() {
		List<Coords> coords = gcf.getCoordsList();
		calcExtremePhase(coords);
		calcExtremeMagnitude(coords);
		calcExtremeForDraw();
		drawGrid();
		drawHAxis();
		drawMagnitude(coords);
		drawPhase(coords);
		drawCaption();
	}

	private void drawCaption() {
		GFont font = kernel.getApplication().getFontCanDisplay("-999").deriveFont(GFont.PLAIN, 12);
		g.setFont(font);
		GTextLayout t = geogebra.common.factories.AwtFactory.prototype
				.newTextLayout("Bode Diagram", font, g.getFontRenderContext());
		g.drawString("Bode Diagram", imageSize / 2 + offset - t.getAdvance()
				/ 2, 15 + t.getDescent());
		t = geogebra.common.factories.AwtFactory.prototype.newTextLayout(
				Unicode.omega + "", font, g.getFontRenderContext());
		g.drawString(Unicode.omega + "",
				imageSize / 2 + offset - t.getAdvance() / 2,
				outputImage.getHeight() - 5);
		g.rotate(1.57, imageSize + offset + 10, imageSize / 6);
		g.drawString("Magnitude (dB)", imageSize + offset + 2, imageSize / 6);
		g.rotate(-1.57, imageSize + offset + 10, imageSize / 6);
		g.rotate(-1.57, offset - 10, imageSize / 2 + offset + imageSize / 3);
		g.drawString("Phase (deg)", offset - 10, imageSize / 2 + offset
				+ imageSize / 3);
	}

	private void calcExtremePhase(List<Coords> list) {
		double y;
		double maxP = Double.NEGATIVE_INFINITY;
		double minP = Double.POSITIVE_INFINITY;
		for (int i = 0; i < list.size(); i++) {
			y = Math.atan2(list.get(i).getY(), list.get(i).getX()) * 180
					/ Math.PI;
			if (y > maxP) {
				maxP = y;
			}
			if (y < minP) {
				minP = y;
			}
		}
		maxPhase = (int) Math.round(maxP);
		minPhase = (int) Math.floor(minP);
	}

	private void calcExtremeMagnitude(List<Coords> list) {
		double y = 0;
		double maxM = Double.NEGATIVE_INFINITY;
		for (int i = list.size() - 1; i > -1; i--) {
			y = Math.sqrt(list.get(i).getY() * list.get(i).getY()
					+ list.get(i).getX() * list.get(i).getX());
			if (y > maxM) {
				maxM = y;
			}
		}
		int size = list.size() - 1;
		y = Math.sqrt(list.get(size).getY() * list.get(size).getY()
				+ list.get(size).getX() * list.get(size).getX());
		maxDecibel = (int) Math.round(20 * Math.log10(maxM));
		lastDecibel = (int) Math.floor(20 * Math.log10(y));
		if (maxDecibel == lastDecibel) {
			double minM = Double.POSITIVE_INFINITY;
			for (int i = list.size() - 1; i > -1; i--) {
				y = Math.sqrt(list.get(i).getY() * list.get(i).getY()
						+ list.get(i).getX() * list.get(i).getX());
				if (y < minM) {
					minM = y;
				}
			}
			lastDecibel = (int) Math.floor(20 * Math.log10(minM));
		}
	}

	private void calcExtremeForDraw() {
		GFont font = kernel.getApplication().getFontCanDisplay("-999").deriveFont(GFont.PLAIN, 10);
		g.setFont(font);
		do {
			maxDecibel++;
		} while (maxDecibel % 20 != 0);

		while (lastDecibel % 20 != 0) {
			lastDecibel--;
		}

		do {
			maxPhase++;
		} while (maxPhase % 45 != 0);

		while (minPhase % 45 != 0) {
			minPhase--;
		}
	}

	private void drawHAxis() {
		GFont font = kernel.getApplication().getFontCanDisplay("-999").deriveFont(GFont.PLAIN, 7);
		int x = offset;
		g.setColor(GColor.black);
		g.setFont(font);
		for (int i = gcf.getStartBode(); i <= gcf.getEndBode(); i++) {
			g.drawString("" + i, x + 3, imageSize + offset + 10);
			x += stepXForOmega;
		}
		x = offset;
		font = kernel.getApplication().getFontCanDisplay("-999").deriveFont(GFont.PLAIN, 9);
		g.setFont(font);
		for (int i = gcf.getStartBode(); i <= gcf.getEndBode(); i++) {
			g.drawString("10", x - 6, imageSize + offset + 17);
			x += stepXForOmega;
		}

		g.setStroke(AwtFactory.prototype.newBasicStroke(1.5f));
		g.drawLine(offset - 3, imageSize / 2 + offset, imageSize + offset,
				imageSize / 2 + offset);
	}

	private void drawMagnitude(List<Coords> coords) {
		double x = offset + imageSize;
		double sc = stepX / 20f;
		double step = 1.0 * imageSize / coords.size();
		GeneralPathClipped gp = new GeneralPathClipped(view);
		double y;
		boolean flag = false;
		for (int i = coords.size() - 1; i > -1; i--) {
			y = Math.sqrt(coords.get(i).getY() * coords.get(i).getY()
					+ coords.get(i).getX() * coords.get(i).getX());
			y = 20 * Math.log10(y);
			if (zeroMagnitude - y * sc < offset + imageSize / 2) {
				gp.lineTo(x, zeroMagnitude - y * sc);
				flag = true;
			} else {
				if (flag) {
					flag = false;
					gp.lineTo(x, offset + imageSize / 2);
				}
			}
			x -= step;
		}
		g.draw(gp);
	}

	private void drawPhase(List<Coords> coords) {
		double x = offset + imageSize;
		double sc = stepY / 45f;
		double step = 1.0 * imageSize / coords.size();
		GeneralPathClipped gp = new GeneralPathClipped(view);
		double y;
		for (int i = coords.size() - 1; i > -1; i--) {
			y = Math.atan2(coords.get(i).getY(), coords.get(i).getX()) * 180
					/ Math.PI;
			gp.lineTo(x, zeroPhase - y * sc);
			x -= step;
		}
		g.draw(gp);
	}

	private void drawGrid() {
		int x = drawVerticalGrid();

		g.drawLine(x, offset, x, imageSize + offset);

		drawMagnitudeGrid();

		drawMagnitudeLabels();

		drawPhaseGrid();

		drawPhaseLabels();

	}

	private void drawMagnitudeLabels() {
		g.setColor(GColor.black);
		for (int i = offset; i <= imageSize / 2 + offset; i += stepX) {
			if (maxDecibel == 0) {
				zeroMagnitude = i;
			}
			g.drawString("" + maxDecibel, 2, i + 5);
			maxDecibel -= 20;
		}
	}

	private void drawMagnitudeGrid() {
		g.setColor(GColor.lightGray);
		stepX = Math.abs((maxDecibel - lastDecibel)) / 20;
		stepX = imageSize / 2 / stepX;
		for (int i = offset; i <= imageSize / 2 + offset; i += stepX) {
			g.drawLine(offset - 3, i, imageSize + offset + 3, i);
		}

	}

	private int drawVerticalGrid() {
		g.setColor(GColor.lightGray);
		int x = offset;
		stepXForOmega = gcf.getEndBode() - gcf.getStartBode();
		stepXForOmega = imageSize / stepXForOmega;
		for (int i = gcf.getStartBode(); i < gcf.getEndBode(); i++) {
			g.drawLine(x, offset, x, imageSize + offset);
			for (int j = 2; j < 10; j++) {
				g.drawLine((int) (x + Math.log10(j) * stepXForOmega), offset,
						(int) (x + Math.log10(j) * stepXForOmega), imageSize
								+ offset);
			}
			x += stepXForOmega;
		}
		return x;
	}

	private void drawPhaseGrid() {
		g.setColor(GColor.lightGray);
		stepY = (int) (Math.abs((maxPhase - minPhase)) / 90 + 1);
		stepY = imageSize / 4 / stepY;
		for (int i = offset + imageSize / 2; i <= imageSize + offset; i += stepY) {
			g.drawLine(offset - 3, i, imageSize + offset + 3, i);
		}
	}

	private void drawPhaseLabels() {
		g.setColor(GColor.black);
		for (int i = offset + imageSize / 2; i <= imageSize + offset; i += stepY) {
			if (maxPhase == 0) {
				zeroPhase = i;
			}
			g.drawString("" + (int) maxPhase, imageSize + offset + 4, i + 5);
			maxPhase -= 45;
		}
	}

	@Override
	public GetCommand getClassName() {
		if (nyquist) {
			return Commands.Nyquist;
		}
		return Commands.Bode;
	}

}
