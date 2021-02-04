package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.util.MyMath;

import com.himamis.retex.renderer.share.platform.graphics.RenderingHints;

/**
 * Class for drawing labels of 3D elements
 * 
 * @author mathieu
 *
 */
public class DrawLabel3D {

	/** text of the label */
	protected String text;
	/** font of the label */
	protected GFont font;
	protected GFont fontOriginal;
	/** text wants serif */
	private boolean serif;
	/** color of the label */
	private Coords backgroundColor;
	private Coords color;
	/** origin of the label (left-bottom corner) */
	protected Coords origin;
	/** x, y, z offset */
	private float xOffset;
	private float yOffset;
	private float zOffset;
	protected float xOffset2;
	protected float yOffset2;
	/** says if there's an anchor to do */
	private boolean anchor;
	/** says if the label is visible */
	private boolean isVisible;

	/** width and height of the text */
	protected int height;
	protected int width;
	/** width and height of the texture */
	private int height2;
	private int width2;

	/** index of the texture used for this label */
	private int textureIndex = -1;

	/** current view where this label is drawn */
	protected EuclidianView3D view;

	/** says it wait for reset */
	private boolean waitForReset;

	private Runnable callBack = null;

	protected double drawX;
	protected double drawY;
	protected double drawZ;

	private Coords vScreen = new Coords(3);

	private float[] labelOrigin = new float[3];

	private int pickingX;
	private int pickingY;
	private int pickingW;
	private int pickingH;

	/** temp graphics used for calculate bounds */
	protected GGraphics2D tempGraphics;

	protected Drawable3D drawable;
	private int textIndex = -1;
	private int pickingIndex = -1;
	protected int backgroundIndex = -1;
	protected boolean hasIndex = false;

    private CoordMatrix4x4 positionMatrix;

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
		if (view.drawsLabels()) {
			tempGraphics = AwtFactory.getPrototype().newBufferedImage(1, 1, 1).createGraphics();
		}
	}

	/**
	 * update the label
	 * 
	 * @param text0
	 *            text
	 * @param font0
	 *            font
	 * @param fgColor
	 *            color
	 * @param v
	 *            coordinates
	 * @param xOffset0
	 *            abs offset in x
	 * @param yOffset0
	 *            abs offset in y
	 * @param zOffset0
	 * 	 *        abs offset in z
	 */
	public void update(String text0, GFont font0, GColor fgColor, Coords v,
			float xOffset0, float yOffset0, float zOffset0) {

		if (view.drawsLabels()) {
			update(text0, font0, null, fgColor, v, xOffset0, yOffset0, zOffset0);
		}
	}

	/**
	 * 
	 * @return font scale (used for image export)
	 */
	protected double getFontScale() {
		return view.getFontScale();
	}

	/**
	 * update the label
	 * 
	 * @param text0
	 *            text
	 * @param font0
	 *            font
	 * @param bgColor
	 *            background color
	 * @param fgColor
	 *            foreground color
	 * @param v
	 *            coordinates
	 * @param xOffset0
	 *            abs offset in x
	 * @param yOffset0
	 *            abs offset in y
	 * @param zOffset0
	 * 	          abs offset in y
	 */
	public void update(String text0, GFont font0, GColor bgColor,
			GColor fgColor, Coords v, float xOffset0, float yOffset0, float zOffset0) {

		this.origin = v;
		if (text0.length() == 0) {
			return;
		}

		this.color = new Coords((double) fgColor.getRed() / 255,
				(double) fgColor.getGreen() / 255, (double) fgColor.getBlue() / 255,
				1);

		if (bgColor != null) {
			this.backgroundColor = new Coords(
					(double) bgColor.getRed() / 255,
					(double) bgColor.getGreen() / 255,
					(double) bgColor.getBlue() / 255, 1);
		} else {
			this.backgroundColor = null;
		}

		if (view.isGrayScaled()) {
			this.color.convertToGrayScale();
		}

		setIsVisible(true);

		if (waitForReset || !text0.equals(this.text)
				|| !font0.equals(this.fontOriginal)) {
			this.text = text0;
			fontOriginal = font0;
			int style = fontOriginal.getStyle();
			int size = fontOriginal.getSize();
			font = fontOriginal.deriveFont(style,
					(float) (size * getFontScale()));

			tempGraphics.setFont(font);

			serif = true;
			GeoElement geo = drawable.getGeoElement();
			if (geo instanceof TextProperties) {
				serif = ((TextProperties) geo).isSerifFont();
			}

			GRectangle rectangle = getBounds();

			int xMin = (int) rectangle.getMinX() - 1;
			int xMax = (int) rectangle.getMaxX() + 1;
			int yMin = (int) rectangle.getMinY() - 1;
			int yMax = (int) rectangle.getMaxY() + 1;

			// Application.debug(text+"\nxMin="+xMin+", xMax="+xMax+",
			// advance="+textLayout.getAdvance());

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

		this.xOffset = xOffset0; // + xOffset2;
		this.yOffset = yOffset0; // + yOffset2;
		this.zOffset = zOffset0;
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

		GAffineTransform gt = AwtFactory.getPrototype().newAffineTransform();
		gt.scale(1, -1d);
		gt.translate(-xOffset2, yOffset2); // put the baseline on the label
											// anchor
		g2d.transform(gt);

		g2d.setColor(GColor.BLACK);
		g2d.setFont(font);

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		return g2d;
	}

	/**
	 * @return buffered image
	 */
	protected GBufferedImage createBufferedImage() {
		return view.getRenderer().createBufferedImage(this);
	}

	protected GRectangle getBounds() {
		
		GRectangle rectangle = EuclidianStatic.drawMultiLineText(
				view.getApplication(), text, 0, 0, tempGraphics, false, font,
				AwtFactory.getPrototype().newRectangle(), null);
		if (text.contains("_")) { // text contains subscript
			hasIndex = true;
			GPoint p = EuclidianStatic.drawIndexedString(view.getApplication(),
					tempGraphics, text, 0, 0, false);
			rectangle.setRect(rectangle.getMinX(), rectangle.getMinY(),
					rectangle.getWidth(), rectangle.getHeight() + p.y);
		} else {
			hasIndex = false;
		}

		return rectangle;
	}

	private static boolean isLatex(String text) {
		return text.length() > 1 && text.charAt(0) == '$' && text.endsWith("$");
	}

	/**
	 * @return buffered image with label drawn in it
	 */
	protected GBufferedImage draw() {

		GBufferedImage bimg;
		GGraphics2D g2d;

		if (isLatex(text)) {
			GeoElement geo = drawable.getGeoElement();

			// make sure LaTeX labels
			// don't go off bottom of screen
			int offsetY = 10 + view.getFontSize();

			height += offsetY;
			bimg = createBufferedImage();
			g2d = createGraphics2D(bimg);

			App app = view.getApplication();
			// GDimension dim =
			app.getDrawEquation().drawEquation(geo.getKernel().getApplication(),
					geo, g2d, 0, -offsetY, text.substring(1, text.length() - 1),
					g2d.getFont(), serif, g2d.getColor(), g2d.getBackground(),
					true, false, getCallBack());
			return bimg;
		}

		bimg = createBufferedImage();
		g2d = createGraphics2D(bimg);
		g2d.setFont(font);

		if (hasIndex) {
			EuclidianStatic.drawIndexedString(view.getApplication(), g2d, text,
					0, 0, false);
		} else {
			g2d.drawString(text, 0, 0);
		}

		return bimg;
	}

	/**
	 * 
	 * @return callback (for JLM)
	 */
	protected Runnable getCallBack() {
		if (callBack == null) {
			callBack = new DrawLaTeXCallBack(this);
		}

		return callBack;
	}

	protected class DrawLaTeXCallBack implements Runnable {

		private DrawLabel3D label;

		public DrawLaTeXCallBack(DrawLabel3D label) {
			this.label = label;
		}

		@Override
		public void run() {
			label.drawable.setLabelWaitForReset();
			view.repaintView();
		}

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
	 *            anchor
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

	/**
	 * 
	 * @return z position (in screen coords) where the label is drawn
	 */
	public double getDrawZ() {
		return drawZ;
	}

	/**
	 * update draw position
	 */
	public void updateDrawPosition() {

		if (origin == null) {
			return;
		}

		vScreen.setMul(view.getToScreenMatrix(), origin);

		origin.get3ForGL(labelOrigin);
		labelOrigin[0] *= view.getXscale();
		labelOrigin[1] *= view.getYscale();
		labelOrigin[2] *= view.getZscale();

		if (!view.isXREnabled() || !anchor) {
			drawX = (int) (vScreen.getX() + xOffset);
			if (anchor && xOffset < 0) {
				drawX -= width / getFontScale();
			} else {
				drawX += xOffset2 / getFontScale();
			}

			drawY = (int) (vScreen.getY() + yOffset);
			if (anchor && yOffset < 0) {
				drawY -= height / getFontScale();
			} else {
				drawY += yOffset2 / getFontScale();
			}

			drawZ = (int) (vScreen.getZ() + zOffset);
		}
	}

	/**
	 * 
	 * update position for axes labels (x/y/z)
	 * 
	 * @param xOffset1
	 *            x offset
	 * @param yOffset1
	 *            y offset
	 * @param zOffset1
	 *            z offset
	 * @param tickSize
	 *            tick size
	 */
	public void updateDrawPositionAxes(float xOffset1, float yOffset1,
			float zOffset1, int tickSize) {
		this.xOffset = xOffset1;
		this.yOffset = yOffset1;
		this.zOffset = zOffset1;
	    updateDrawPositionAxes(tickSize);
    }

	/**
	 * update position for axes numbers
	 * 
	 * @param tickSize
	 *            tick sizes
	 */
	private void updateDrawPositionAxes(int tickSize) {
		drawX = (int) (vScreen.getX());
		drawY = (int) (vScreen.getY());
		drawZ = (int) (vScreen.getZ());

		double radius = (MyMath.length(pickingW, pickingH) / 2) / getFontScale();
		drawX += radius * xOffset;
		drawY += radius * yOffset;
		drawZ += radius * zOffset;

		double f = 1.5;
		drawX += f * tickSize * xOffset;
		drawY += f * tickSize * yOffset;
		drawZ += f * tickSize * zOffset;
	}

	/**
	 * 
	 * @param x
	 *            mouse x position
	 * @param y
	 *            mouse y position
	 * @return true if mouse hits the label
	 */
	public boolean hit(double x, double y) {
		if (backgroundColor != null) {
			return drawX <= x && drawX + width >= x && drawY <= y
					&& drawY + height >= y;
		}

		return drawX + pickingX <= x && drawX + pickingX + pickingW >= x
				&& drawY + pickingY <= y && drawY + pickingY + pickingH >= y;
	}

	/**
	 * 
	 * @param o
	 *            mouse origin
	 * @param direction
	 *            mouse direction
	 * @return true if mouse hits the label
	 */
	public boolean hit(Coords o, Coords direction) {
		double x = o.getX()
				+ (drawZ - o.getZ()) * direction.getX() / direction.getZ();
		double y = o.getY()
				+ (drawZ - o.getZ()) * direction.getY() / direction.getZ();
		return hit(x, y);
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

		if (!isVisible) {
			return;
		}

		if (textureIndex == -1) {
			return;
		}

		if (view.isXRDrawing()) {
		    if (positionMatrix == null) {
                positionMatrix = new CoordMatrix4x4();
            }
            positionMatrix.set(renderer.getUndoRotationMatrixAR());
            Coords origin = positionMatrix.getOrigin();
            origin.setX(drawX);
            origin.setY(drawY);
            origin.setZ(drawZ);
            renderer.getRendererImpl().setMatrixView(positionMatrix);
        }

		renderer.getRendererImpl().setLabelOrigin(labelOrigin);

		if (forPicking) {
			// renderer.getGeometryManager().rectangle(drawX + pickingX, drawY +
			// pickingY, drawZ, pickingW, pickingH);
			if (backgroundColor != null) {
				renderer.getGeometryManager().draw(backgroundIndex);
			} else {
				renderer.getGeometryManager().draw(pickingIndex);
			}
		} else {

			// draw background
			if (backgroundColor != null) {
				renderer.setColor(backgroundColor);
				renderer.getRendererImpl().disableTextures();
				// renderer.getGeometryManager().rectangle(drawX, drawY, drawZ,
				// width, height);
				renderer.getGeometryManager().draw(backgroundIndex);

			}

			// draw text
			drawText(renderer);

		}
	}

	/**
	 * draw at (x,y,z)
	 * 
	 * @param renderer
	 *            renderer
	 */
	protected void drawText(Renderer renderer) {
		// draw text
		renderer.setColor(color);
		renderer.getRendererImpl().enableTextures();
		renderer.getRendererImpl().setLayer(Renderer.LAYER_FOR_TEXTS);
		renderer.getTextures().setTextureLinear(textureIndex);
		renderer.getGeometryManager().drawLabel(textIndex);
		renderer.getRendererImpl().setLayer(Renderer.LAYER_DEFAULT);

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
	 *            label visibility
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

	/**
	 * @param scale
	 *            scale
	 */
	public void scaleRenderingDimensions(float scale) {
		width2 *= scale;
		height2 *= scale;
		pickingX *= scale;
		pickingY *= scale;
		pickingW *= scale;
		pickingH *= scale;
	}

	/**
	 * @return whether the drawable can be picked
	 */
	public boolean isPickable() {
		return drawable.hasPickableLable();
	}

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
        if (view.isXRDrawing()) {
			if (!view.isXREnabled() || !anchor) {
				textIndex = drawRectangle(renderer, 0, 0, 0,
						width2 / getFontScale(), height2 / getFontScale(), textIndex);
			} else {
				double w = width2 / getFontScale();
				double h = height2 / getFontScale();
				textIndex = drawRectangle(renderer, -(pickingX + pickingW / 2d) / getFontScale(),
						-(pickingY + pickingH / 2d) / getFontScale(), 0,
						w, h, textIndex);
			}
        } else {
            textIndex = drawRectangle(renderer, drawX, drawY, drawZ, width2 / getFontScale(),
                     height2 / getFontScale(), textIndex);
        }
		renderer.getGeometryManager().remove(old);

		old = pickingIndex;
		pickingIndex = drawRectangle(renderer,
				drawX + pickingX / getFontScale(),
				drawY + pickingY / getFontScale(), drawZ,
				pickingW / getFontScale(), pickingH / getFontScale(),
				pickingIndex);
		renderer.getGeometryManager().remove(old);

		old = backgroundIndex;
		if (view.isXRDrawing()) {
			backgroundIndex = drawRectangle(renderer, 0, 0, 0,
					width / getFontScale(),
					height / getFontScale(), backgroundIndex);
		} else {
			backgroundIndex = drawRectangle(renderer, drawX, drawY, drawZ,
					width / getFontScale(), height / getFontScale(),
					backgroundIndex);
		}
		renderer.getGeometryManager().remove(old);

		// Log.debug("textIndex: "+textIndex+", pickingIndex: "+pickingIndex+",
		// backgroundIndex: "+backgroundIndex);
	}

	private static final int drawRectangle(Renderer renderer, double x,
			double y, double z, double w, double h, int index) {
		return renderer.getGeometryManager().rectangle(x, y, z, w, h, index);
	}

	/**
	 * remove from GPU memory
	 */
	public void removeFromGL() {
		Manager manager = view.getRenderer().getGeometryManager();
		manager.remove(textIndex);
		manager.remove(pickingIndex);
		manager.remove(backgroundIndex);
		view.getRenderer().getTextures().removeTexture(textureIndex);
	}

}
