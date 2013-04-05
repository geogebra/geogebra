package geogebra.common.kernel.algos;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.font.GTextLayout;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoBoolean;
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
	private double y;
	private double x;
	private GColor color;
	private double incX;
	private double incY;
	private double[] vals = new double[2];
	private GGraphics2D g;
	private FunctionNVar f;
	private DecimalFormat df;
	private boolean hasGrid;
	private boolean hasAxes;
	private GTextLayout t;
	private GFont font = app.getFontCanDisplay("-999").deriveFont(GFont.PLAIN,
			8);
	private double scaleX;
	private double scaleY;
	private double zeroY=0;
	private double zeroX=0;
	private int grade;

	public AlgoDensityPlot(Construction c, GeoFunctionNVar function) {
		this(c, function, -2, 2, -2, 2,1);

	}

	public AlgoDensityPlot(Construction cons, GeoFunctionNVar geoFunctionNVar,
			double lowX, double highX, double lowY, double highY) {
		this(cons, geoFunctionNVar, lowX, highX, lowY, highY,1);
	}

	public AlgoDensityPlot(Construction cons, GeoFunctionNVar geoFunctionNVar,
			double lowX, double highX, double lowY, double highY, int grade) {
		this(cons,geoFunctionNVar,lowX,highX,lowY,highY,grade,0,0,true,false);
	}

	public AlgoDensityPlot(Construction cons, GeoFunctionNVar geoFunctionNVar,
			double lowX, double highX, double lowY, double highY, int grade,
			double zeroX, double zeroY,
			boolean hasAxes, boolean hasGrid) {
		super(cons);
		this.function = geoFunctionNVar;
		f = function.getFunction();
		minX = lowX;
		minY = lowY;
		maxX = highX;
		maxY = highY;
		scaleX = maxX - minX;
		scaleY = maxY - minY;
		this.zeroX=zeroX;
		this.zeroY=zeroY;
		imageSize /= grade;
		gridPixel /= grade;
		if (grade == 2) {
			offset = 25;
		}
		outputImage = new GeoCanvasImage(cons, imageSize + 2 * offset,
				imageSize + 2 * offset);
		g = outputImage.getGraphics();
		g.setFont(font);
		df = new DecimalFormat("0.##");
		this.grade = grade;
		this.hasAxes=hasAxes;
		this.hasGrid=hasGrid;
		setInputOutput();
	}
	@Override
	protected void setInputOutput() {		
		input = new GeoElement[10];
		input[0] = function;
		input[1] = new GeoNumeric(cons,minX);
		input[2] = new GeoNumeric(cons,maxX);
		input[3] = new GeoNumeric(cons,minY);
		input[4] = new GeoNumeric(cons,maxY);
		input[5] = new GeoNumeric(cons,grade);
		input[6] = new GeoNumeric(cons,zeroX);
		input[7] = new GeoNumeric(cons,zeroY);
		input[8] = new GeoBoolean(cons,hasAxes);
		input[9] = new GeoBoolean(cons,hasGrid);
		
		super.setOutputLength(1);
		super.setOutput(0, outputImage);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public void compute() {
		incX = scaleX / imageSize;
		incY = scaleY / imageSize;
		g.setColor(GColor.white);
		g.fillRect(0, 0, imageSize + 2 * offset, imageSize + 2 * offset);
		if (hasAxes) {
			drawAxes();
		}
		for (j = offset, y = maxY; j < imageSize + offset; y -= incY * grade, j += grade) {
			for (i = offset, x = minX; i < imageSize + offset; x += incX
					* grade, i += grade) {
				vals[0] = x;
				vals[1] = y;
				double value = f.evaluate(vals);
				colors = rgbColor(value);
				color = AwtFactory.prototype.newColor(colors[0], colors[1],
						colors[1]);
				g.setColor(color);
				g.fillRect(i, j, grade,  grade);
			}
		}
		if (hasGrid) {
			drawGrid();
		}
		setInputOutput();
	}

	private void drawGrid() {
		g.setColor(GColor.gray);
		for (i = offset; i <= imageSize + offset; i += gridPixel) {
			g.drawLine(i, offset, i, imageSize + offset);
		}
		for (i = offset; i <= imageSize + offset; i += gridPixel) {
			g.drawLine(offset, i, imageSize + offset, i);
		}
	}

	private void drawAxes() {
		double xx = minX;
		double yy = maxY;
		g.setColor(GColor.gray);
		for (i = offset; i <= imageSize + offset; i += gridPixel * 5) {
			g.drawLine(offset - 2, i, offset, i);
			g.drawLine(i, imageSize + offset, i, imageSize + offset + 2);
		}
		g.setColor(GColor.black);
		for (i = offset; i <= imageSize + offset; i += gridPixel * 5) {
			g.drawString(df.format(yy), 1, i + 4);
			t = geogebra.common.factories.AwtFactory.prototype.newTextLayout(
					df.format(xx), font, g.getFontRenderContext());
			g.drawString(df.format(xx), i - t.getAdvance() / 2, imageSize + 2
					* offset - offset / 3);
			xx += incX * gridPixel * 5;
			yy -= incY * gridPixel * 5;
		}
	}

	public GeoCanvasImage getResult() {
		return outputImage;
	}

	@Override
	public GetCommand getClassName() {
		return Commands.DensityPlot;
	}

	
	// getter ad setter 
	
	public double getScaleX() {
		return scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	public void setZeroY(double value) {		
		minY = -(scaleY / 2 + value);
		maxY = scaleY / 2 - value;
		zeroY=value;
	}

	public void setZeroX(double value) {
		minX = -(scaleX / 2 + value);
		maxX = scaleX / 2 - value;
		zeroX=value;
	}

	public void setScaleX(double scale) {
		this.scaleX = scale;
		setZeroX(zeroX);
	}

	public void setScaleY(double scale) {
		this.scaleY = scale;
		setZeroY(zeroY);
	}

	public double getZeroX() {
		return zeroX;
	}

	public double getZeroY() {
		return zeroY;
	}

	public void setGrid(boolean selected) {
		hasGrid = selected;
	}

	public void setAxes(boolean selected) {
		hasAxes = selected;
	}

	public boolean hasGrid() {
		return hasGrid;
	}

	public boolean hasAxes() {
		return hasAxes;
	}
	
	/*
	 * This code is based on Zoltan Kovacs's <kovacsz@nyf.hu> idea. For details
	 * see Teaching Math. and Comp. Sci. *2*, pp. 321-331.
	 */

	private int col(double x) {
		int code;
		code = (int) (x * 256);
		if (code > 255) {
			return 255;
		} else {
			return (code);
		}
	}

	private int[] hlsToRgb(double h, double l, double s) {
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

	public int[] rgbColor(double zre) {
		double xx;
		xx = 1.0 - 2.0 * Math.atan(Math.sqrt(zre * zre)) / Math.PI;
		double x = xx <= 0.5 ? 2 * xx : 2 - 2 * xx;
		double arg = zre < 0 ? 180 : 0;
		return hlsToRgb(arg, xx, x);
	}
	
}
