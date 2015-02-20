package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GPoint;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GRenderingHints;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;

/**
 * Class for drawing labels of 3D elements
 * 
 * @author matthieu
 *
 */
public class DrawLabel3D {

	/** text of the label */
	protected String text;
	/** font of the label */
	protected GFont font;
	/** color of the label */
	private Coords backgroundColor, color;
	/** origin of the label (left-bottom corner) */
	protected Coords origin;
	/** x and y offset */
	private float xOffset, yOffset;
	private float xOffset2, yOffset2;
	/** says if there's an anchor to do */
	private boolean anchor;
	/** says if the label is visible */
	private boolean isVisible;

	/** width and height of the text */
	protected int height, width;
	/** width and height of the texture */
	private int height2, width2;

	/** index of the texture used for this label */
	private int textureIndex = -1;

	/** current view where this label is drawn */
	protected EuclidianView3D view;

	/** says it wait for reset */
	private boolean waitForReset;

	/** temp graphics used for calculate bounds */
	protected GGraphics2D tempGraphics = geogebra.common.factories.AwtFactory.prototype
			.newBufferedImage(1, 1, GBufferedImage.TYPE_INT_ARGB)
			.createGraphics();

	private Drawable3D drawable;

	/**
	 * common constructor
	 * 
	 * @param view
	 *            3D view
	 * @param drawable
	 *            drawable linked to this label
	 */
	public DrawLabel3D(EuclidianView3D view, Drawable3D drawable) {
		this.view = view;
		this.drawable = drawable;
	}

	/**
	 * update the label
	 * 
	 * @param text
	 * @param fontsize
	 * @param color
	 * @param v
	 * @param xOffset
	 * @param yOffset
	 */
	public void update(String text, GFont font, GColor color, Coords v,
			float xOffset, float yOffset) {

		update(text, font, null, color, v, xOffset, yOffset);
	}

	/**
	 * update the label
	 * 
	 * @param text
	 * @param fontsize
	 * @param color
	 * @param v
	 * @param xOffset
	 * @param yOffset
	 */
	public void update(String text, GFont font, GColor backgroundColor,
			GColor color, Coords v, float xOffset, float yOffset) {

		this.origin = v;

		if (text.length() == 0)
			return;

		if (isLatex(text)) { // TODO remove this as soon as latex is gwt
								// compatible
			setIsVisible(false);
			return;
		}

		this.color = new Coords((double) color.getRed() / 255,
				(double) color.getGreen() / 255,
				(double) color.getBlue() / 255, 1);

		if (backgroundColor != null)
			this.backgroundColor = new Coords(
					(double) backgroundColor.getRed() / 255,
					(double) backgroundColor.getGreen() / 255,
					(double) backgroundColor.getBlue() / 255, 1);
		else
			this.backgroundColor = null;

		if (view.isGrayScaled())
			this.color.convertToGrayScale();

		setIsVisible(true);

		if (waitForReset || !text.equals(this.text) || !font.equals(this.font)) {

			this.text = text;
			this.font = font;

			tempGraphics.setFont(font);

			GRectangle rectangle = getBounds();

			int xMin = (int) rectangle.getMinX() - 1;
			int xMax = (int) rectangle.getMaxX() + 1;
			int yMin = (int) rectangle.getMinY() - 1;
			int yMax = (int) rectangle.getMaxY() + 1;

			// Application.debug(text+"\nxMin="+xMin+", xMax="+xMax+", advance="+textLayout.getAdvance());

			width = xMax - xMin;
			height = yMax - yMin;
			xOffset2 = xMin;
			yOffset2 = -yMax;

			// creates the buffered image
			GBufferedImage bimg = draw();

			// creates the texture
			view.getRenderer().createAlphaTexture(this, bimg);

			waitForReset = false;
			// Application.debug("textureIndex = "+textureIndex);
		}

		this.xOffset = xOffset;// + xOffset2;
		this.yOffset = yOffset;// + yOffset2;

		// update draw position
		updateDrawPosition();

	}

	/**
	 * create graphics2D instance from buffered image
	 * 
	 * @param bimg
	 *            buffered image
	 * @return graphics2D
	 */
	protected GGraphics2D createGraphics2D(GBufferedImage bimg) {
		GGraphics2D g2d = bimg.createGraphics();

		GAffineTransform gt = geogebra.common.factories.AwtFactory.prototype
				.newAffineTransform();
		gt.scale(1, -1d);
		gt.translate(-xOffset2, yOffset2); // put the baseline on the label
											// anchor
		g2d.transform(gt);

		g2d.setColor(GColor.BLACK);
		g2d.setFont(font);
		g2d.setRenderingHint(GRenderingHints.KEY_ANTIALIASING,
				GRenderingHints.VALUE_ANTIALIAS_ON);

		return g2d;
	}

	/**
	 * @return buffered image
	 */
	protected GBufferedImage createBufferedImage() {
		return view.getRenderer().createBufferedImage(this);
	}

	protected boolean hasIndex = false;

	protected GRectangle getBounds() {
		GRectangle rectangle = EuclidianStatic.drawMultiLineText(
				view.getApplication(), text, 0, 0, tempGraphics, false,
				tempGraphics.getFont());
		if (text.contains("_")) { // text contains subscript
			hasIndex = true;
			geogebra.common.awt.GPoint p = EuclidianStatic.drawIndexedString(
					view.getApplication(), tempGraphics, text, 0, 0, false,
					false);
			rectangle.setRect(rectangle.getMinX(), rectangle.getMinY(),
					rectangle.getWidth(), rectangle.getHeight() + p.y);
		} else {
			hasIndex = false;
		}

		return rectangle;
	}

	private static boolean isLatex(String text) {
		return (text.charAt(0) == '$') && text.endsWith("$");
	}

	/**
	 * @return buffered image with label drawn in it
	 */
	protected GBufferedImage draw() {

		GBufferedImage bimg;
		GGraphics2D g2d;

		if (isLatex(text) && text.length() > 1) {
			boolean serif = true; // nice "x"s
			GeoElement geo = drawable.getGeoElement();
			if (geo.isGeoText())
				serif = ((GeoText) geo).isSerifFont();
			int offsetY = 10 + view.getFontSize(); // make sure LaTeX labels
													// don't go
			// off bottom of screen

			height += offsetY;
			bimg = createBufferedImage();
			g2d = createGraphics2D(bimg);

			App app = view.getApplication();
			// GDimension dim =
			app.getDrawEquation().drawEquation(
					geo.getKernel().getApplication(), geo, g2d, 0, -offsetY,
					text.substring(1, text.length() - 1), g2d.getFont(), serif,
					g2d.getColor(), g2d.getBackground(), true, false);
			// App.debug(height+","+dim.getHeight());
			/*
			 * labelRectangle.setBounds(xLabel, yLabel - offsetY,
			 * dim.getWidth(), dim.getHeight());
			 */
			return bimg;
		}

		bimg = createBufferedImage();
		g2d = createGraphics2D(bimg);

		if (hasIndex) {
			EuclidianStatic.drawIndexedString(view.getApplication(), g2d, text,
					0, 0, false, false);
		} else {
			g2d.drawString(text, 0, 0);
		}

		return bimg;
	}

	/**
	 * set the label to be reset
	 */
	public void setWaitForReset() {
		waitForReset = true;

		textIndex = -1;
		pickingIndex = -1;
		backgroundIndex = -1;
	}

	/**
	 * 
	 * @return true if this wait for reset
	 */
	public boolean waitForReset() {
		return waitForReset;
	}

	/**
	 * sets the anchor
	 * 
	 * @param flag
	 */
	public void setAnchor(boolean flag) {
		anchor = flag;
	}

	/**
	 * draws the label
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void draw(Renderer renderer) {

		draw(renderer, false);
	}

	protected int drawX, drawY, drawZ;

	/**
	 * 
	 * @return z position (in screen coords) where the label is drawn
	 */
	public int getDrawZ() {
		return drawZ;
	}

	/**
	 * update draw position
	 */
	public void updateDrawPosition() {

		if (origin == null) {
			return;
		}

		Coords v = view.getToScreenMatrix().mul(origin);
		drawX = (int) (v.getX() + xOffset);
		if (anchor && xOffset < 0) {
			drawX -= width;
		} else {
			drawX += xOffset2;
		}

		drawY = (int) (v.getY() + yOffset);
		if (anchor && yOffset < 0) {
			drawY -= height;
		} else {
			drawY += yOffset2;
		}

		drawZ = (int) v.getZ();

	}

	/**
	 * 
	 * @param pos
	 *            mouse position
	 * @return true if pos hits the label
	 */
	public boolean hit(GPoint pos) {
		if (backgroundColor != null) {
			return drawX <= pos.x
					&& drawX + width >= pos.x
					&& drawY <= pos.y
					&& drawY + height >= pos.y;
		}

		return drawX + pickingX <= pos.x
				&& drawX + pickingX + pickingW >= pos.x
				&& drawY + pickingY <= pos.y
				&& drawY + pickingY + pickingH >= pos.y;
	}

	/**
	 * draws the label
	 * 
	 * @param renderer
	 *            renderer
	 * @param forPicking
	 *            says if it's for picking
	 */
	public void draw(Renderer renderer, boolean forPicking) {

		if (!isVisible)
			return;

		if (textureIndex == -1)
			return;

		renderer.setLabelOrigin(origin);

		if (forPicking) {
			// renderer.getGeometryManager().rectangle(drawX + pickingX, drawY +
			// pickingY, drawZ, pickingW, pickingH);
			if (backgroundColor != null) {
				renderer.getGeometryManager().draw(backgroundIndex);
			}else{
				renderer.getGeometryManager().draw(pickingIndex);
			}
		} else {

			// draw background
			if (backgroundColor != null) {
				renderer.setColor(backgroundColor);
				renderer.disableTextures();
				// renderer.getGeometryManager().rectangle(drawX, drawY, drawZ,
				// width, height);
				renderer.getGeometryManager().draw(backgroundIndex);

			}

			// draw text
			draw(renderer, drawX, drawY, drawZ);

		}
	}

	/**
	 * draw at (x,y,z)
	 * 
	 * @param renderer
	 *            renderer
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param z
	 *            z
	 */
	protected void draw(Renderer renderer, int x, int y, int z) {
		// draw text
		renderer.setColor(color);
		renderer.enableTextures();
		// renderer.getTextures().setTextureNearest(textureIndex);
		renderer.getTextures().setTextureLinear(textureIndex);
		// renderer.getGeometryManager().rectangle(x, y, z, width2, height2);
		renderer.getGeometryManager().drawLabel(textIndex);

	}

	/**
	 * set texture index
	 * 
	 * @param i
	 *            index
	 */
	public void setTextureIndex(int i) {
		textureIndex = i;
	}

	/**
	 * @return texture indexl
	 * 
	 */
	public int getTextureIndex() {
		return textureIndex;
	}

	/**
	 * sets the visibility of the label
	 * 
	 * @param flag
	 */
	public void setIsVisible(boolean flag) {
		isVisible = flag;
	}

	/**
	 * 
	 * @return label width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 
	 * @return label height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 
	 * @return label width for texture (power of 2)
	 */
	public int getWidthPowerOfTwo() {
		return width2;
	}

	/**
	 * 
	 * @return label height for texture (power of 2)
	 */
	public int getHeightPowerOfTwo() {
		return height2;
	}

	/**
	 * set dimension for picking
	 * 
	 * @param x
	 *            bottom-left x position
	 * @param y
	 *            bottom-left y position
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	public void setPickingDimension(int x, int y, int w, int h) {
		pickingX = x;
		pickingY = y;
		pickingW = w;
		pickingH = h;
	}

	/**
	 * set power of 2 width and height
	 * 
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	public void setDimensionPowerOfTwo(int w, int h) {
		width2 = w;
		height2 = h;
	}

	private int pickingX, pickingY, pickingW, pickingH;

	public boolean isPickable() {
		return drawable.hasPickableLable();
	}

	private int textIndex = -1;
	private int pickingIndex = -1;
	protected int backgroundIndex = -1;

	/**
	 * update label for view (update screen position)
	 * 
	 * @param renderer
	 *            GL renderer
	 */
	public void updatePosition(Renderer renderer) {

		updateDrawPosition();

		if (origin == null) {
			renderer.getGeometryManager().remove(textIndex);
			textIndex = -1;
			renderer.getGeometryManager().remove(pickingIndex);
			pickingIndex = -1;
			renderer.getGeometryManager().remove(backgroundIndex);
			backgroundIndex = -1;
			return;
		}

		int old = textIndex;
		textIndex = renderer.getGeometryManager().rectangle(drawX, drawY,
				drawZ, width2, height2, textIndex);
		renderer.getGeometryManager().remove(old);

		old = pickingIndex;
		pickingIndex = renderer.getGeometryManager().rectangle(
				drawX + pickingX, drawY + pickingY, drawZ, pickingW, pickingH,
				pickingIndex);
		renderer.getGeometryManager().remove(old);

		old = backgroundIndex;
		backgroundIndex = renderer.getGeometryManager().rectangle(drawX, drawY,
				drawZ, width, height, backgroundIndex);
		renderer.getGeometryManager().remove(old);

		// App.debug("textIndex: "+textIndex+", pickingIndex: "+pickingIndex+", backgroundIndex: "+backgroundIndex);
	}

}
