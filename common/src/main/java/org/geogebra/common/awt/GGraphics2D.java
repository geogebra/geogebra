package org.geogebra.common.awt;

import org.geogebra.common.awt.font.GTextLayout;

public interface GGraphics2D {

	/**
	 * Strokes the outline of a <code>Shape</code> using the settings of the
	 * current <code>Graphics2D</code> context. The rendering attributes applied
	 * include the <code>Clip</code>, <code>Transform</code>, <code>Paint</code>
	 * , <code>Composite</code> and <code>Stroke</code> attributes.
	 * 
	 * @param s
	 *            the <code>Shape</code> to be rendered
	 * @see #setStroke
	 * @see #setPaint
	 * @see #setColor
	 * @see #transform
	 * 
	 * 
	 * @see #setClip
	 * @see #setComposite
	 */
	void draw(GShape s);

	void drawImage(GBufferedImage img, int x, int y);

	void drawImage(MyImage img, int x, int y);

	/**
	 * Renders the text of the specified <code>String</code>, using the current
	 * text attribute state in the <code>Graphics2D</code> context. The baseline
	 * of the first character is at position (<i>x</i>,&nbsp;<i>y</i>) in the
	 * User Space. The rendering attributes applied include the
	 * <code>Clip</code>, <code>Transform</code>, <code>Paint</code>,
	 * <code>Font</code> and <code>Composite</code> attributes. For characters
	 * in script systems such as Hebrew and Arabic, the glyphs can be rendered
	 * from right to left, in which case the coordinate supplied is the location
	 * of the leftmost character on the baseline.
	 * 
	 * @param str
	 *            the string to be rendered
	 * @param x
	 *            the x coordinate of the location where the <code>String</code>
	 *            should be rendered
	 * @param y
	 *            the y coordinate of the location where the <code>String</code>
	 *            should be rendered
	 * @throws NullPointerException
	 *             if <code>str</code> is <code>null</code>
	 * @since JDK1.0
	 */
	void drawString(String str, int x, int y);

	/**
	 * Renders the text specified by the specified <code>String</code>, using
	 * the current text attribute state in the <code>Graphics2D</code> context.
	 * The baseline of the first character is at position (<i>x</i>,&nbsp;
	 * <i>y</i>) in the User Space. The rendering attributes applied include the
	 * <code>Clip</code>, <code>Transform</code>, <code>Paint</code>,
	 * <code>Font</code> and <code>Composite</code> attributes. For characters
	 * in script systems such as Hebrew and Arabic, the glyphs can be rendered
	 * from right to left, in which case the coordinate supplied is the location
	 * of the leftmost character on the baseline.
	 * 
	 * @param str
	 *            the <code>String</code> to be rendered
	 * @param x
	 *            the x coordinate of the location where the <code>String</code>
	 *            should be rendered
	 * @param y
	 *            the y coordinate of the location where the <code>String</code>
	 *            should be rendered
	 * @throws NullPointerException
	 *             if <code>str</code> is <code>null</code>
	 * @see #setPaint
	 * @see #setColor
	 * @see #setFont
	 * 
	 * @see #setComposite
	 * @see #setClip
	 */
	void drawString(String str, double x, double y);

	/**
	 * Fills the interior of a <code>Shape</code> using the settings of the
	 * <code>Graphics2D</code> context. The rendering attributes applied include
	 * the <code>Clip</code>, <code>Transform</code>, <code>Paint</code>, and
	 * <code>Composite</code>.
	 * 
	 * @param s
	 *            the <code>Shape</code> to be filled
	 * @see #setPaint
	 * @see #setColor
	 * @see #transform
	 * 
	 * @see #setComposite
	 * @see #setClip
	 */
	void fill(GShape s);

	/**
	 * Sets the <code>Composite</code> for the <code>Graphics2D</code> context.
	 * The <code>Composite</code> is used in all drawing methods such as
	 * <code>drawImage</code>, <code>drawString</code>, <code>draw</code>, and
	 * <code>fill</code>. It specifies how new pixels are to be combined with
	 * the existing pixels on the graphics device during the rendering process.
	 * <p>
	 * If this <code>Graphics2D</code> context is drawing to a
	 * <code>Component</code> on the display screen and the
	 * <code>Composite</code> is a custom object rather than an instance of the
	 * <code>AlphaComposite</code> class, and if there is a security manager,
	 * its <code>checkPermission</code> method is called with an
	 * <code>AWTPermission("readDisplayPixels")</code> permission.
	 * 
	 * @param comp
	 *            the <code>Composite</code> object to be used for rendering
	 * @throws SecurityException
	 *             if a custom <code>Composite</code> object is being used to
	 *             render to the screen and a security manager is set and its
	 *             <code>checkPermission</code> method does not allow the
	 *             operation.
	 * @see #getComposite
	 * @see GAlphaComposite
	 * @see SecurityManager#checkPermission
	 */
	void setComposite(GComposite comp);

	/**
	 * Sets the <code>Paint</code> attribute for the <code>Graphics2D</code>
	 * context. Calling this method with a <code>null</code> <code>Paint</code>
	 * object does not have any effect on the current <code>Paint</code>
	 * attribute of this <code>Graphics2D</code>.
	 * 
	 * @param paint
	 *            the <code>Paint</code> object to be used to generate color
	 *            during the rendering process, or <code>null</code>
	 * @see #setColor
	 * @see GGradientPaint
	 * @see GTexturePaint
	 */
	void setPaint(GPaint paint);

	/**
	 * Sets the <code>Stroke</code> for the <code>Graphics2D</code> context.
	 * 
	 * @param s
	 *            the <code>Stroke</code> object to be used to stroke a
	 *            <code>Shape</code> during the rendering process
	 * @see GBasicStroke
	 * @see #getStroke
	 */
	void setStroke(GBasicStroke s);

	/**
	 * Sets the value of a single preference for the rendering algorithms. Hint
	 * categories include controls for rendering quality and overall
	 * time/quality trade-off in the rendering process. Refer to the
	 * <code>RenderingHints</code> class for definitions of some common keys and
	 * values.
	 * 
	 * @param hintKey
	 *            the key of the hint to be set.
	 * @param hintValue
	 *            the value indicating preferences for the specified hint
	 *            category.
	 * @see com.himamis.retex.renderer.share.platform.graphics.RenderingHints
	 */
	void setRenderingHint(int hintKey, int hintValue);

	/**
	 * Concatenates the current <code>Graphics2D</code> <code>Transform</code>
	 * with a translation transform. Subsequent rendering is translated by the
	 * specified distance relative to the previous position. This is equivalent
	 * to calling transform(T), where T is an <code>AffineTransform</code>
	 * represented by the following matrix:
	 * 
	 * <pre>
	 *          [   1    0    tx  ]
	 *          [   0    1    ty  ]
	 *          [   0    0    1   ]
	 * </pre>
	 * 
	 * @param tx
	 *            the distance to translate along the x-axis
	 * @param ty
	 *            the distance to translate along the y-axis
	 */
	void translate(double tx, double ty);

	/**
	 * Concatenates the current <code>Graphics2D</code> <code>Transform</code>
	 * with a scaling transformation Subsequent rendering is resized according
	 * to the specified scaling factors relative to the previous scaling. This
	 * is equivalent to calling <code>transform(S)</code>, where S is an
	 * <code>AffineTransform</code> represented by the following matrix:
	 * 
	 * <pre>
	 *          [   sx   0    0   ]
	 *          [   0    sy   0   ]
	 *          [   0    0    1   ]
	 * </pre>
	 * 
	 * @param sx
	 *            the amount by which X coordinates in subsequent rendering
	 *            operations are multiplied relative to previous rendering
	 *            operations.
	 * @param sy
	 *            the amount by which Y coordinates in subsequent rendering
	 *            operations are multiplied relative to previous rendering
	 *            operations.
	 */
	void scale(double sx, double sy);

	/**
	 * Composes an <code>AffineTransform</code> object with the
	 * <code>Transform</code> in this <code>Graphics2D</code> according to the
	 * rule last-specified-first-applied. If the current <code>Transform</code>
	 * is Cx, the result of composition with Tx is a new <code>Transform</code>
	 * Cx'. Cx' becomes the current <code>Transform</code> for this
	 * <code>Graphics2D</code>. Transforming a point p by the updated
	 * <code>Transform</code> Cx' is equivalent to first transforming p by Tx
	 * and then transforming the result by the original <code>Transform</code>
	 * Cx. In other words, Cx'(p) = Cx(Tx(p)). A copy of the Tx is made, if
	 * necessary, so further modifications to Tx do not affect rendering.
	 * 
	 * @param Tx
	 *            the <code>AffineTransform</code> object to be composed with
	 *            the current <code>Transform</code>
	 * 
	 * @see GAffineTransform
	 */
	void transform(GAffineTransform Tx);

	/**
	 * Returns the current <code>Composite</code> in the <code>Graphics2D</code>
	 * context.
	 * 
	 * @return the current <code>Graphics2D</code> <code>Composite</code>, which
	 *         defines a compositing style.
	 * @see #setComposite
	 */
	GComposite getComposite();

	/**
	 * Returns the background color used for clearing a region.
	 * 
	 * @return the current <code>Graphics2D</code> <code>Color</code>, which
	 *         defines the background color.
	 */
	GColor getBackground();

	/**
	 * Returns the current <code>Stroke</code> in the <code>Graphics2D</code>
	 * context.
	 * 
	 * @return the current <code>Graphics2D</code> <code>Stroke</code>, which
	 *         defines the line style.
	 * @see #setStroke
	 */
	GBasicStroke getStroke();

	/**
	 * Intersects the current <code>Clip</code> with the interior of the
	 * specified <code>Shape</code> and sets the <code>Clip</code> to the
	 * resulting intersection. The specified <code>Shape</code> is transformed
	 * with the current <code>Graphics2D</code> <code>Transform</code> before
	 * being intersected with the current <code>Clip</code>. This method is used
	 * to make the current <code>Clip</code> smaller. To make the
	 * <code>Clip</code> larger, use <code>setClip</code>. The <i>user clip</i>
	 * modified by this method is independent of the clipping associated with
	 * device bounds and visibility. If no clip has previously been set, or if
	 * the clip has been cleared using {@link #setClip(GShape) setClip} with a
	 * <code>null</code> argument, the specified <code>Shape</code> becomes the
	 * new user clip.
	 * 
	 * @param s
	 *            the <code>Shape</code> to be intersected with the current
	 *            <code>Clip</code>. If <code>s</code> is <code>null</code>,
	 *            this method clears the current <code>Clip</code>.
	 */
	// public abstract void clip(GShape s);

	/**
	 * Get the rendering context of the <code>Font</code> within this
	 * <code>Graphics2D</code> context. The {@link GFontRenderContext}
	 * encapsulates application hints such as anti-aliasing and fractional
	 * metrics, as well as target device specific information such as
	 * dots-per-inch. This information should be provided by the application
	 * when using objects that perform typographical formatting, such as
	 * <code>Font</code> and <code>TextLayout</code>. This information should
	 * also be provided by applications that perform their own layout and need
	 * accurate measurements of various characteristics of glyphs such as
	 * advance and line height when various rendering hints have been applied to
	 * the text rendering.
	 *
	 * @return a reference to an instance of FontRenderContext.
	 * @see GFontRenderContext
	 * @see GTextLayout
	 * @since 1.2
	 */

	GFontRenderContext getFontRenderContext();

	GColor getColor();

	GFont getFont();

	void setFont(GFont font);

	void setColor(GColor selColor);

	void fillRect(int x, int y, int w, int h);

	void clearRect(int x, int y, int w, int h);

	void drawLine(int x1, int y1, int x2, int y2);

	void setClip(GShape shape);

	void setClip(GShape shape, boolean saveContext);

	void resetClip();

	void drawRect(int x, int y, int width, int height);

	void setClip(int x, int y, int width, int height);

	void setClip(int x, int y, int width, int height,
			boolean saveContext);

	void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight);

	void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight);

	void setAntialiasing();

	void setTransparent();

	Object setInterpolationHint(
			boolean needsInterpolationRenderingHint);

	void resetInterpolationHint(Object oldInterpolationHint);

	void updateCanvasColor();

	void drawStraightLine(double xCrossPix, double d,
			double xCrossPix2, double i);

	/**
	 * Saves the state of the current transformation matrix.
	 */
	void saveTransform();

	/**
	 * Restores the transformation matrix to the state where
	 * <b>saveTransform()</b> was called.
	 */
	void restoreTransform();

	/**
	 * start a new general path we'll add lines etc. to
	 */
	void startGeneralPath();

	/**
	 * add straight line to current general path
	 * 
	 * @param x1
	 *            first point x coordinate
	 * @param y1
	 *            first point y coordinate
	 * @param x2
	 *            second point x coordinate
	 * @param y2
	 *            second point y coordinate
	 */
	void addStraightLineToGeneralPath(double x1, double y1,
			double x2, double y2);

	/**
	 * end current general path and draw it
	 */
	void endAndDrawGeneralPath();

	/**
	 * @param img
	 *            source
	 * @param sx
	 *            source min x
	 * @param sy
	 *            source min y
	 * @param sw
	 *            source width
	 * @param sh
	 *            source height
	 * @param dx
	 *            dest rect min x
	 * @param dy
	 *            dest rect min y
	 */
	void drawImage(MyImage img, int sx, int sy, int sw, int sh, int dx,
			int dy, int dw, int dh);

	void drawImage(MyImage img, int dx, int dy, int dw, int dh);
}
