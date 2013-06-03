package geogebra.common.kernel.algos;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.font.GTextLayout;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoCanvasImage;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoNumeric;

import java.text.DecimalFormat;

/**
 * draw density for 2-variables function
 * 
 * @author Giuliano Bellucci 05/04/2013
 * 
 */
public class AlgoDensityPlot extends AlgoElement {

	private GeoCanvasImage outputImage;
	private GeoFunctionNVar function;

	private double minX;
	private double maxX;
	private double minY;
	private double maxY;

	private int offset = 30;
	private int imageSize = 280;

	private int gridPixel = 14;
	private int[] colors;
	private int i;
	private int j;
	private GColor color;
	private double incX;
	private double incY;
	private double[] vals = new double[2];
	private GGraphics2D g;
	private FunctionNVar f;
	private DecimalFormat df;
	private GTextLayout t;
	private GFont font = app.getFontCanDisplay("-999").deriveFont(GFont.PLAIN,
			8);
	private double scaleX;
	private double scaleY;
	private int grade;
	private EuclidianViewInterfaceCommon view;
	private boolean fixed;
	private int imagePlusOffset;
	private double value;
	private boolean prevGrid;

	/**
	 * @param c 
	 *           Construction
	 * @param function 
	 *           2-variables function
	 * 	
	 */
	public AlgoDensityPlot(final Construction c, final GeoFunctionNVar function) {
		this(c, function, -2, 2, -2, 2, false);
	}

	/**
	 * @param cons
	 *            Construction
	 * @param geoFunctionNVar
	 *            2-variables function
	 * @param lowX
	 * @param highX
	 * @param lowY
	 * @param highY
	 * @param fixed
	 *            true for fixed scale, false for scale handled by euclidean
	 *            view
	 */
	public AlgoDensityPlot(Construction cons, GeoFunctionNVar geoFunctionNVar,
			double lowX, double highX, double lowY, double highY, boolean fixed) {
		super(cons);
		grade = 1;
		// for web image, area and resolution are a quarter of desktop
		if (app.isHTML5Applet()) {
			grade = 2;
			offset = 25;
		}
		function = geoFunctionNVar;
		f = function.getFunction();
		view = app.getActiveEuclidianView();
		this.fixed = fixed;
		if (fixed) {
			minX = lowX;
			minY = lowY;
			maxX = highX;
			maxY = highY;
		}
		scaleX = maxX - minX;
		scaleY = maxY - minY;
		imageSize /= grade;
		gridPixel /= grade;
		outputImage = new GeoCanvasImage(cons, imageSize + 2 * offset,
				imageSize + 2 * offset);
		g = outputImage.getGraphics();
		g.setFont(font);
		g.setColor(GColor.white);
		g.fillRect(0, 0, imageSize + 2 * offset, offset);
		df = new DecimalFormat("0.##");
		imagePlusOffset = imageSize + offset;
		outputImage.setAbsoluteScreenLocActive(true);
		outputImage.setAbsoluteScreenLoc(view.getViewWidth() / 2
				- (imageSize + 2 * offset) / 2, view.getViewHeight() / 2
				+ (imageSize + 2 * offset) / 2);
		setInputOutput();
		deleteAxes();
		if (fixed) {
			compute();
		}
		update();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[6];
		input[0] = function;
		input[1] = new GeoNumeric(cons, minX);
		input[2] = new GeoNumeric(cons, maxX);
		input[3] = new GeoNumeric(cons, minY);
		input[4] = new GeoNumeric(cons, maxY);
		input[5] = new GeoNumeric(cons, grade);

		super.setOutputLength(1);
		super.setOutput(0, outputImage);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public void compute() {
		incX = scaleX / imageSize * grade;
		incY = scaleY / imageSize * grade;
		for (j = offset, vals[1] = maxY; j < imagePlusOffset; vals[1] -= incY, j += grade) {
			for (i = offset, vals[0] = minX; i < imagePlusOffset; vals[0] += incX, i += grade) {
				value = f.evaluate(vals);
				colors = rgbColor(value);
				color = AwtFactory.prototype.newColor(colors[0], colors[1],
						colors[1]);
				g.setColor(color);
				g.fillRect(i, j, grade, grade);
			}
		}
	}

	private void drawGrid() {
		g.setColor(GColor.lightGray);
		for (i = offset; i <= imagePlusOffset; i += gridPixel * 5) {
			g.drawLine(i, offset, i, imagePlusOffset);
		}
		for (i = offset; i <= imagePlusOffset; i += gridPixel * 5) {
			g.drawLine(offset, i, imagePlusOffset, i);
		}
	}

	private void drawAxes() {
		double xx = minX;
		double yy = maxY;
		g.setColor(GColor.gray);
		for (i = offset; i <= imagePlusOffset; i += gridPixel * 5) {
			g.drawLine(i, imagePlusOffset, i, imagePlusOffset + 2);
			g.drawLine(offset - 2, i, offset, i);
		}
		g.setColor(GColor.black);
		for (i = offset; i <= imagePlusOffset; i += gridPixel * 5) {
			t = geogebra.common.factories.AwtFactory.prototype.newTextLayout(
					df.format(xx), font, g.getFontRenderContext());
			g.drawString(df.format(xx), i - t.getAdvance() / 2, imageSize + 2
					* offset - offset / 3);
			g.drawString(df.format(yy), 1, i + 4);
			yy -= incY * gridPixel * 5 / grade;
			xx += incX * gridPixel * 5 / grade;
		}
	}

	/**
	 * @return GeoCanvasImage of function
	 */
	public GeoCanvasImage getResult() {
		return outputImage;
	}

	@Override
	public GetCommand getClassName() {
		return Commands.DensityPlot;
	}

	@Override
	public void update() {
		if (!fixed) {
			if (minX != view.getXmin() || minY != view.getYmin()
					|| maxX != view.getXmax() || maxY != view.getYmax()) {
				minX = view.getXmin();
				minY = view.getYmin();
				maxX = view.getXmax();
				maxY = view.getYmax();
				scaleX = maxX - minX;
				scaleY = maxY - minY;
				compute();
			}
		}
		deleteAxes();
		if (view.getShowAxis(EuclidianViewInterfaceCommon.AXIS_X)
				|| view.getShowAxis(EuclidianViewInterfaceCommon.AXIS_Y)) {
			drawAxes();
		}
		showGrid();
	}

	private void deleteAxes() {
		g.setColor(GColor.white);
		g.fillRect(0, imagePlusOffset, imagePlusOffset + 2 * offset, offset);
		g.fillRect(0, offset, offset, imagePlusOffset);
		g.fillRect(0, 0, imagePlusOffset + 2 * offset, offset);
		g.fillRect(imagePlusOffset, offset, offset,imageSize);
	}

	private void showGrid() {
		if (view.getShowGrid()) {
			drawGrid();
			prevGrid = true;
		} else {
			if (view.getShowGrid() != prevGrid && prevGrid) {
				prevGrid = false;
				compute();
			}
		}
	}

	/*
	 * This code is based on Zoltan Kovacs's <kovacsz@nyf.hu> idea. For details
	 * see Teaching Math. and Comp. Sci. *2*, pp. 321-331.
	 */

	private static int col(double x1) {
		int code;
		code = (int) (x1 * 256);
		if (code > 255) {
			return 255;
		}
		return (code);
	}

	private static int[] hlsToRgb(double h, double l, double s) {
		int[] rgb = new int[2];
		double m2;
		if (l < 0.5) {
			m2 = l * (1 + s);
		} else {
			m2 = l + s - l * s;
		}
		if (h == 180) {
			rgb[1] = col(m2);
			rgb[0] = col(2.0 * l - m2);
		} else {
			rgb[1] = col(2.0 * l - m2);
			rgb[0] = col(m2);
		}
		return rgb;
	}

	private static int[] rgbColor(double zre) {
		double xx;
		xx = 1.0 - 2.0 * Math.atan(Math.sqrt(zre * zre)) / Math.PI;
		double x1 = xx <= 0.5 ? 2 * xx : 2 - 2 * xx;
		double arg = zre < 0 ? 180 : 0;
		return hlsToRgb(arg, xx, x1);
	}
}
