package geogebra.common.awt;

import java.util.Map;

public abstract class GGraphics2D {
	
	/**
	 * Draws a 3-D highlighted outline of the specified rectangle.
	 * The edges of the rectangle are highlighted so that they
	 * appear to be beveled and lit from the upper left corner.
	 * <p>
	 * The colors used for the highlighting effect are determined
	 * based on the current color.
	 * The resulting rectangle covers an area that is
	 * <code>width&nbsp;+&nbsp;1</code> pixels wide
	 * by <code>height&nbsp;+&nbsp;1</code> pixels tall.  This method
	 * uses the current <code>Color</code> exclusively and ignores
	 * the current <code>Paint</code>.
	 * @param x the x coordinate of the rectangle to be drawn.
	 * @param y the y coordinate of the rectangle to be drawn.
	 * @param width the width of the rectangle to be drawn.
	 * @param height the height of the rectangle to be drawn.
	 * @param raised a boolean that determines whether the rectangle
	 *                      appears to be raised above the surface
	 *                      or sunk into the surface.
	 * @see         java.awt.Graphics#fill3DRect
	 */
	public abstract void draw3DRect(int x, int y, int width, int height,
			boolean raised);

	/**
	 * Paints a 3-D highlighted rectangle filled with the current color.
	 * The edges of the rectangle are highlighted so that it appears
	 * as if the edges were beveled and lit from the upper left corner.
	 * The colors used for the highlighting effect and for filling are
	 * determined from the current <code>Color</code>.  This method uses
	 * the current <code>Color</code> exclusively and ignores the current
	 * <code>Paint</code>.
	 * @param x the x coordinate of the rectangle to be filled.
	 * @param y the y coordinate of the rectangle to be filled.
	 * @param       width the width of the rectangle to be filled.
	 * @param       height the height of the rectangle to be filled.
	 * @param       raised a boolean value that determines whether the
	 *                      rectangle appears to be raised above the surface
	 *                      or etched into the surface.
	 * @see         java.awt.Graphics#draw3DRect
	 */
	public abstract void fill3DRect(int x, int y, int width, int height,
			boolean raised);

	/**
	 * Strokes the outline of a <code>Shape</code> using the settings of the
	 * current <code>Graphics2D</code> context.  The rendering attributes
	 * applied include the <code>Clip</code>, <code>Transform</code>,
	 * <code>Paint</code>, <code>Composite</code> and
	 * <code>Stroke</code> attributes.
	 * @param s the <code>Shape</code> to be rendered
	 * @see #setStroke
	 * @see #setPaint
	 * @see java.awt.Graphics#setColor
	 * @see #transform
	 * @see #setTransform
	 * @see #clip
	 * @see #setClip
	 * @see #setComposite
	 */
	public abstract void draw(GShape s);

	/**
	 * Renders an image, applying a transform from image space into user space
	 * before drawing.
	 * The transformation from user space into device space is done with
	 * the current <code>Transform</code> in the <code>Graphics2D</code>.
	 * The specified transformation is applied to the image before the
	 * transform attribute in the <code>Graphics2D</code> context is applied.
	 * The rendering attributes applied include the <code>Clip</code>,
	 * <code>Transform</code>, and <code>Composite</code> attributes.
	 * Note that no rendering is done if the specified transform is
	 * noninvertible.
	 * @param img the specified image to be rendered.
	 *            This method does nothing if <code>img</code> is null.
	 * @param xform the transformation from image space into user space
	 * @param obs the {@link GImageObserver}
	 * to be notified as more of the <code>Image</code>
	 * is converted
	 * @return <code>true</code> if the <code>Image</code> is
	 * fully loaded and completely rendered, or if it's null;
	 * <code>false</code> if the <code>Image</code> is still being loaded.
	 * @see #transform
	 * @see #setTransform
	 * @see #setComposite
	 * @see #clip
	 * @see #setClip
	 */
	public abstract boolean drawImage(GImage img, GAffineTransform xform,
			GImageObserver obs);
	/**
	 * Renders a <code>BufferedImage</code> that is
	 * filtered with a
	 * {@link GBufferedImageOp}.
	 * The rendering attributes applied include the <code>Clip</code>,
	 * <code>Transform</code>
	 * and <code>Composite</code> attributes.  This is equivalent to:
	 * <pre>
	 * img1 = op.filter(img, null);
	 * drawImage(img1, new AffineTransform(1f,0f,0f,1f,x,y), null);
	 * </pre>
	 * @param op the filter to be applied to the image before rendering
	 * @param img the specified <code>BufferedImage</code> to be rendered.
	 *            This method does nothing if <code>img</code> is null.
	 * @param x the x coordinate of the location in user space where
	 * the upper left corner of the image is rendered
	 * @param y the y coordinate of the location in user space where
	 * the upper left corner of the image is rendered
	 *
	 * @see #transform
	 * @see #setTransform
	 * @see #setComposite
	 * @see #clip
	 * @see #setClip
	 */
	public abstract void drawImage(GBufferedImage img, GBufferedImageOp op,
			int x, int y);
	
	
	public abstract void drawImage(GBufferedImage img, int x, int y);

	/**
	 * Renders a {@link GRenderedImage},
	 * applying a transform from image
	 * space into user space before drawing.
	 * The transformation from user space into device space is done with
	 * the current <code>Transform</code> in the <code>Graphics2D</code>.
	 * The specified transformation is applied to the image before the
	 * transform attribute in the <code>Graphics2D</code> context is applied.
	 * The rendering attributes applied include the <code>Clip</code>,
	 * <code>Transform</code>, and <code>Composite</code> attributes. Note
	 * that no rendering is done if the specified transform is
	 * noninvertible.
	 * @param img the image to be rendered. This method does
	 *            nothing if <code>img</code> is null.
	 * @param xform the transformation from image space into user space
	 * @see #transform
	 * @see #setTransform
	 * @see #setComposite
	 * @see #clip
	 * @see #setClip
	 */
	public abstract void drawRenderedImage(GRenderedImage img,
			GAffineTransform xform);

	/**
	 * Renders a
	 * {@link GRenderableImage},
	 * applying a transform from image space into user space before drawing.
	 * The transformation from user space into device space is done with
	 * the current <code>Transform</code> in the <code>Graphics2D</code>.
	 * The specified transformation is applied to the image before the
	 * transform attribute in the <code>Graphics2D</code> context is applied.
	 * The rendering attributes applied include the <code>Clip</code>,
	 * <code>Transform</code>, and <code>Composite</code> attributes. Note
	 * that no rendering is done if the specified transform is
	 * noninvertible.
	 *<p>
	 * Rendering hints set on the <code>Graphics2D</code> object might
	 * be used in rendering the <code>RenderableImage</code>.
	 * If explicit control is required over specific hints recognized by a
	 * specific <code>RenderableImage</code>, or if knowledge of which hints
	 * are used is required, then a <code>RenderedImage</code> should be
	 * obtained directly from the <code>RenderableImage</code>
	 * and rendered using
	 *{@link #drawRenderedImage(GRenderedImage, GAffineTransform) drawRenderedImage}.
	 * @param img the image to be rendered. This method does
	 *            nothing if <code>img</code> is null.
	 * @param xform the transformation from image space into user space
	 * @see #transform
	 * @see #setTransform
	 * @see #setComposite
	 * @see #clip
	 * @see #setClip
	 * @see #drawRenderedImage
	 */
	public abstract void drawRenderableImage(GRenderableImage img,
			GAffineTransform xform);

	/**
	 * Renders the text of the specified <code>String</code>, using the
	 * current text attribute state in the <code>Graphics2D</code> context.
	 * The baseline of the
	 * first character is at position (<i>x</i>,&nbsp;<i>y</i>) in
	 * the User Space.
	 * The rendering attributes applied include the <code>Clip</code>,
	 * <code>Transform</code>, <code>Paint</code>, <code>Font</code> and
	 * <code>Composite</code> attributes.  For characters in script
	 * systems such as Hebrew and Arabic, the glyphs can be rendered from
	 * right to left, in which case the coordinate supplied is the
	 * location of the leftmost character on the baseline.
	 * @param str the string to be rendered
	 * @param x the x coordinate of the location where the
	 * <code>String</code> should be rendered
	 * @param y the y coordinate of the location where the
	 * <code>String</code> should be rendered
	 * @throws NullPointerException if <code>str</code> is
	 *         <code>null</code>
	 * @see         java.awt.Graphics#drawBytes
	 * @see         java.awt.Graphics#drawChars
	 * @since       JDK1.0
	 */
	public abstract void drawString(String str, int x, int y);

	/**
	 * Renders the text specified by the specified <code>String</code>,
	 * using the current text attribute state in the <code>Graphics2D</code> context.
	 * The baseline of the first character is at position
	 * (<i>x</i>,&nbsp;<i>y</i>) in the User Space.
	 * The rendering attributes applied include the <code>Clip</code>,
	 * <code>Transform</code>, <code>Paint</code>, <code>Font</code> and
	 * <code>Composite</code> attributes. For characters in script systems
	 * such as Hebrew and Arabic, the glyphs can be rendered from right to
	 * left, in which case the coordinate supplied is the location of the
	 * leftmost character on the baseline.
	 * @param str the <code>String</code> to be rendered
	 * @param x the x coordinate of the location where the
	 * <code>String</code> should be rendered
	 * @param y the y coordinate of the location where the
	 * <code>String</code> should be rendered
	 * @throws NullPointerException if <code>str</code> is
	 *         <code>null</code>
	 * @see #setPaint
	 * @see java.awt.Graphics#setColor
	 * @see java.awt.Graphics#setFont
	 * @see #setTransform
	 * @see #setComposite
	 * @see #setClip
	 */
	public abstract void drawString(String str, float x, float y);

	/**
	 * Renders the text of the specified iterator applying its attributes
	 * in accordance with the specification of the {@link java.awt.font.TextAttribute} class.
	 * <p>
	 * The baseline of the first character is at position
	 * (<i>x</i>,&nbsp;<i>y</i>) in User Space.
	 * For characters in script systems such as Hebrew and Arabic,
	 * the glyphs can be rendered from right to left, in which case the
	 * coordinate supplied is the location of the leftmost character
	 * on the baseline.
	 * @param iterator the iterator whose text is to be rendered
	 * @param x the x coordinate where the iterator's text is to be
	 * rendered
	 * @param y the y coordinate where the iterator's text is to be
	 * rendered
	 * @throws NullPointerException if <code>iterator</code> is
	 *         <code>null</code>
	 * @see #setPaint
	 * @see java.awt.Graphics#setColor
	 * @see #setTransform
	 * @see #setComposite
	 * @see #setClip
	 */
	public abstract void drawString(GAttributedCharacterIterator iterator,
			int x, int y);

	/**
	 * Renders the text of the specified iterator applying its attributes
	 * in accordance with the specification of the {@link java.awt.font.TextAttribute} class.
	 * <p>
	 * The baseline of the first character is at position
	 * (<i>x</i>,&nbsp;<i>y</i>) in User Space.
	 * For characters in script systems such as Hebrew and Arabic,
	 * the glyphs can be rendered from right to left, in which case the
	 * coordinate supplied is the location of the leftmost character
	 * on the baseline.
	 * @param iterator the iterator whose text is to be rendered
	 * @param x the x coordinate where the iterator's text is to be
	 * rendered
	 * @param y the y coordinate where the iterator's text is to be
	 * rendered
	 * @throws NullPointerException if <code>iterator</code> is
	 *         <code>null</code>
	 * @see #setPaint
	 * @see java.awt.Graphics#setColor
	 * @see #setTransform
	 * @see #setComposite
	 * @see #setClip
	 */
	public abstract void drawString(GAttributedCharacterIterator iterator,
			float x, float y);

	/**
	 * Renders the text of the specified
	 * {@link GGlyphVector} using
	 * the <code>Graphics2D</code> context's rendering attributes.
	 * The rendering attributes applied include the <code>Clip</code>,
	 * <code>Transform</code>, <code>Paint</code>, and
	 * <code>Composite</code> attributes.  The <code>GlyphVector</code>
	 * specifies individual glyphs from a {@link GFont}.
	 * The <code>GlyphVector</code> can also contain the glyph positions.
	 * This is the fastest way to render a set of characters to the
	 * screen.
	 * @param g the <code>GlyphVector</code> to be rendered
	 * @param x the x position in User Space where the glyphs should
	 * be rendered
	 * @param y the y position in User Space where the glyphs should
	 * be rendered
	 * @throws NullPointerException if <code>g</code> is <code>null</code>.
	 *
	 * @see java.awt.Font#createGlyphVector
	 * @see java.awt.font.GlyphVector
	 * @see #setPaint
	 * @see java.awt.Graphics#setColor
	 * @see #setTransform
	 * @see #setComposite
	 * @see #setClip
	 */
	public abstract void drawGlyphVector(GGlyphVector g, float x, float y);

	/**
	 * Fills the interior of a <code>Shape</code> using the settings of the
	 * <code>Graphics2D</code> context. The rendering attributes applied
	 * include the <code>Clip</code>, <code>Transform</code>,
	 * <code>Paint</code>, and <code>Composite</code>.
	 * @param s the <code>Shape</code> to be filled
	 * @see #setPaint
	 * @see java.awt.Graphics#setColor
	 * @see #transform
	 * @see #setTransform
	 * @see #setComposite
	 * @see #clip
	 * @see #setClip
	 */
	public abstract void fill(GShape s);

	/*//* Currently, this is not needed but the comment is left here in case it were needed
	 * Checks whether or not the specified <code>Shape</code> intersects
	 * the specified {@link Rectangle}, which is in device
	 * space. If <code>onStroke</code> is false, this method checks
	 * whether or not the interior of the specified <code>Shape</code>
	 * intersects the specified <code>Rectangle</code>.  If
	 * <code>onStroke</code> is <code>true</code>, this method checks
	 * whether or not the <code>Stroke</code> of the specified
	 * <code>Shape</code> outline intersects the specified
	 * <code>Rectangle</code>.
	 * The rendering attributes taken into account include the
	 * <code>Clip</code>, <code>Transform</code>, and <code>Stroke</code>
	 * attributes.
	 * @param rect the area in device space to check for a hit
	 * @param s the <code>Shape</code> to check for a hit
	 * @param onStroke flag used to choose between testing the
	 * stroked or the filled shape.  If the flag is <code>true</code>, the
	 * <code>Stroke</code> oultine is tested.  If the flag is
	 * <code>false</code>, the filled <code>Shape</code> is tested.
	 * @return <code>true</code> if there is a hit; <code>false</code>
	 * otherwise.
	 * @see #setStroke
	 * @see #fill
	 * @see #draw
	 * @see #transform
	 * @see #setTransform
	 * @see #clip
	 * @see #setClip
	 */
	//public abstract boolean hit(Rectangle rect, Shape s, boolean onStroke);

	/**
	 * Returns the device configuration associated with this
	 * <code>Graphics2D</code>.
	 * @return the device configuration of this <code>Graphics2D</code>.
	 */
	public abstract GGraphicsConfiguration getDeviceConfiguration();

	/**
	 * Sets the <code>Composite</code> for the <code>Graphics2D</code> context.
	 * The <code>Composite</code> is used in all drawing methods such as
	 * <code>drawImage</code>, <code>drawString</code>, <code>draw</code>,
	 * and <code>fill</code>.  It specifies how new pixels are to be combined
	 * with the existing pixels on the graphics device during the rendering
	 * process.
	 * <p>If this <code>Graphics2D</code> context is drawing to a
	 * <code>Component</code> on the display screen and the
	 * <code>Composite</code> is a custom object rather than an
	 * instance of the <code>AlphaComposite</code> class, and if
	 * there is a security manager, its <code>checkPermission</code>
	 * method is called with an <code>AWTPermission("readDisplayPixels")</code>
	 * permission.
	 * @param comp the <code>Composite</code> object to be used for rendering
	 * @throws SecurityException
	 *         if a custom <code>Composite</code> object is being
	 *         used to render to the screen and a security manager
	 *         is set and its <code>checkPermission</code> method
	 *         does not allow the operation.
	 * @see java.awt.Graphics#setXORMode
	 * @see java.awt.Graphics#setPaintMode
	 * @see #getComposite
	 * @see java.awt.AlphaComposite
	 * @see SecurityManager#checkPermission
	 * @see java.awt.AWTPermission
	 */
	public abstract void setComposite(GComposite comp);

	/**
	 * Sets the <code>Paint</code> attribute for the
	 * <code>Graphics2D</code> context.  Calling this method
	 * with a <code>null</code> <code>Paint</code> object does
	 * not have any effect on the current <code>Paint</code> attribute
	 * of this <code>Graphics2D</code>.
	 * @param paint the <code>Paint</code> object to be used to generate
	 * color during the rendering process, or <code>null</code>
	 * @see java.awt.Graphics#setColor
	 * @see #getPaint
	 * @see java.awt.GradientPaint
	 * @see java.awt.TexturePaint
	 */
	public abstract void setPaint(GPaint paint);

	/**
	 * Sets the <code>Stroke</code> for the <code>Graphics2D</code> context.
	 * @param s the <code>Stroke</code> object to be used to stroke a
	 * <code>Shape</code> during the rendering process
	 * @see GBasicStroke
	 * @see #getStroke
	 */
	public abstract void setStroke(GBasicStroke s);

	/**
	 * Sets the value of a single preference for the rendering algorithms.
	 * Hint categories include controls for rendering quality and overall
	 * time/quality trade-off in the rendering process.  Refer to the
	 * <code>RenderingHints</code> class for definitions of some common
	 * keys and values.
	 * @param hintKey the key of the hint to be set.
	 * @param hintValue the value indicating preferences for the specified
	 * hint category.
	 * @see #getRenderingHint(GKey)
	 * @see GRenderingHints
	 */
	public abstract void setRenderingHint(GKey hintKey, Object hintValue);

	/**
	 * Returns the value of a single preference for the rendering algorithms.
	 * Hint categories include controls for rendering quality and overall
	 * time/quality trade-off in the rendering process.  Refer to the
	 * <code>RenderingHints</code> class for definitions of some common
	 * keys and values.
	 * @param hintKey the key corresponding to the hint to get.
	 * @return an object representing the value for the specified hint key.
	 * Some of the keys and their associated values are defined in the
	 * <code>RenderingHints</code> class.
	 * @see GRenderingHints
	 * @see #setRenderingHint(GKey, Object)
	 */
	public abstract Object getRenderingHint(GKey hintKey);

	/**
	 * Replaces the values of all preferences for the rendering
	 * algorithms with the specified <code>hints</code>.
	 * The existing values for all rendering hints are discarded and
	 * the new set of known hints and values are initialized from the
	 * specified {@link Map} object.
	 * Hint categories include controls for rendering quality and
	 * overall time/quality trade-off in the rendering process.
	 * Refer to the <code>RenderingHints</code> class for definitions of
	 * some common keys and values.
	 * @param hints the rendering hints to be set
	 * @see #getRenderingHints
	 * @see GRenderingHints
	 */
	public abstract void setRenderingHints(Map<?, ?> hints);

	/**
	 * Sets the values of an arbitrary number of preferences for the
	 * rendering algorithms.
	 * Only values for the rendering hints that are present in the
	 * specified <code>Map</code> object are modified.
	 * All other preferences not present in the specified
	 * object are left unmodified.
	 * Hint categories include controls for rendering quality and
	 * overall time/quality trade-off in the rendering process.
	 * Refer to the <code>RenderingHints</code> class for definitions of
	 * some common keys and values.
	 * @param hints the rendering hints to be set
	 * @see GRenderingHints
	 */
	public abstract void addRenderingHints(Map<?, ?> hints);

	/**
	 * Gets the preferences for the rendering algorithms.  Hint categories
	 * include controls for rendering quality and overall time/quality
	 * trade-off in the rendering process.
	 * Returns all of the hint key/value pairs that were ever specified in
	 * one operation.  Refer to the
	 * <code>RenderingHints</code> class for definitions of some common
	 * keys and values.
	 * @return a reference to an instance of <code>RenderingHints</code>
	 * that contains the current preferences.
	 * @see GRenderingHints
	 * @see #setRenderingHints(Map)
	 */
	public abstract GRenderingHints getRenderingHints();

	/**
	 * Translates the origin of the <code>Graphics2D</code> context to the
	 * point (<i>x</i>,&nbsp;<i>y</i>) in the current coordinate system.
	 * Modifies the <code>Graphics2D</code> context so that its new origin
	 * corresponds to the point (<i>x</i>,&nbsp;<i>y</i>) in the
	 * <code>Graphics2D</code> context's former coordinate system.  All
	 * coordinates used in subsequent rendering operations on this graphics
	 * context are relative to this new origin.
	 * @param  x the specified x coordinate
	 * @param  y the specified y coordinate
	 * @since   JDK1.0
	 */
	public abstract void translate(int x, int y);

	/**
	 * Concatenates the current
	 * <code>Graphics2D</code> <code>Transform</code>
	 * with a translation transform.
	 * Subsequent rendering is translated by the specified
	 * distance relative to the previous position.
	 * This is equivalent to calling transform(T), where T is an
	 * <code>AffineTransform</code> represented by the following matrix:
	 * <pre>
	 *          [   1    0    tx  ]
	 *          [   0    1    ty  ]
	 *          [   0    0    1   ]
	 * </pre>
	 * @param tx the distance to translate along the x-axis
	 * @param ty the distance to translate along the y-axis
	 */
	public abstract void translate(double tx, double ty);

	/**
	 * Concatenates the current <code>Graphics2D</code>
	 * <code>Transform</code> with a rotation transform.
	 * Subsequent rendering is rotated by the specified radians relative
	 * to the previous origin.
	 * This is equivalent to calling <code>transform(R)</code>, where R is an
	 * <code>AffineTransform</code> represented by the following matrix:
	 * <pre>
	 *          [   cos(theta)    -sin(theta)    0   ]
	 *          [   sin(theta)     cos(theta)    0   ]
	 *          [       0              0         1   ]
	 * </pre>
	 * Rotating with a positive angle theta rotates points on the positive
	 * x axis toward the positive y axis.
	 * @param theta the angle of rotation in radians
	 */
	public abstract void rotate(double theta);

	/**
	 * Concatenates the current <code>Graphics2D</code>
	 * <code>Transform</code> with a translated rotation
	 * transform.  Subsequent rendering is transformed by a transform
	 * which is constructed by translating to the specified location,
	 * rotating by the specified radians, and translating back by the same
	 * amount as the original translation.  This is equivalent to the
	 * following sequence of calls:
	 * <pre>
	 *          translate(x, y);
	 *          rotate(theta);
	 *          translate(-x, -y);
	 * </pre>
	 * Rotating with a positive angle theta rotates points on the positive
	 * x axis toward the positive y axis.
	 * @param theta the angle of rotation in radians
	 * @param x the x coordinate of the origin of the rotation
	 * @param y the y coordinate of the origin of the rotation
	 */
	public abstract void rotate(double theta, double x, double y);

	/**
	 * Concatenates the current <code>Graphics2D</code>
	 * <code>Transform</code> with a scaling transformation
	 * Subsequent rendering is resized according to the specified scaling
	 * factors relative to the previous scaling.
	 * This is equivalent to calling <code>transform(S)</code>, where S is an
	 * <code>AffineTransform</code> represented by the following matrix:
	 * <pre>
	 *          [   sx   0    0   ]
	 *          [   0    sy   0   ]
	 *          [   0    0    1   ]
	 * </pre>
	 * @param sx the amount by which X coordinates in subsequent
	 * rendering operations are multiplied relative to previous
	 * rendering operations.
	 * @param sy the amount by which Y coordinates in subsequent
	 * rendering operations are multiplied relative to previous
	 * rendering operations.
	 */
	public abstract void scale(double sx, double sy);

	/**
	 * Concatenates the current <code>Graphics2D</code>
	 * <code>Transform</code> with a shearing transform.
	 * Subsequent renderings are sheared by the specified
	 * multiplier relative to the previous position.
	 * This is equivalent to calling <code>transform(SH)</code>, where SH
	 * is an <code>AffineTransform</code> represented by the following
	 * matrix:
	 * <pre>
	 *          [   1   shx   0   ]
	 *          [  shy   1    0   ]
	 *          [   0    0    1   ]
	 * </pre>
	 * @param shx the multiplier by which coordinates are shifted in
	 * the positive X axis direction as a function of their Y coordinate
	 * @param shy the multiplier by which coordinates are shifted in
	 * the positive Y axis direction as a function of their X coordinate
	 */
	public abstract void shear(double shx, double shy);

	/**
	 * Composes an <code>AffineTransform</code> object with the
	 * <code>Transform</code> in this <code>Graphics2D</code> according
	 * to the rule last-specified-first-applied.  If the current
	 * <code>Transform</code> is Cx, the result of composition
	 * with Tx is a new <code>Transform</code> Cx'.  Cx' becomes the
	 * current <code>Transform</code> for this <code>Graphics2D</code>.
	 * Transforming a point p by the updated <code>Transform</code> Cx' is
	 * equivalent to first transforming p by Tx and then transforming
	 * the result by the original <code>Transform</code> Cx.  In other
	 * words, Cx'(p) = Cx(Tx(p)).  A copy of the Tx is made, if necessary,
	 * so further modifications to Tx do not affect rendering.
	 * @param Tx the <code>AffineTransform</code> object to be composed with
	 * the current <code>Transform</code>
	 * @see #setTransform
	 * @see GAffineTransform
	 */
	public abstract void transform(GAffineTransform Tx);

	/**
	 * Overwrites the Transform in the <code>Graphics2D</code> context.
	 * WARNING: This method should <b>never</b> be used to apply a new
	 * coordinate transform on top of an existing transform because the
	 * <code>Graphics2D</code> might already have a transform that is
	 * needed for other purposes, such as rendering Swing
	 * components or applying a scaling transformation to adjust for the
	 * resolution of a printer.
	 * <p>To add a coordinate transform, use the
	 * <code>transform</code>, <code>rotate</code>, <code>scale</code>,
	 * or <code>shear</code> methods.  The <code>setTransform</code>
	 * method is intended only for restoring the original
	 * <code>Graphics2D</code> transform after rendering, as shown in this
	 * example:
	 * <pre><blockquote>
	 * // Get the current transform
	 * AffineTransform saveAT = g2.getTransform();
	 * // Perform transformation
	 * g2d.transform(...);
	 * // Render
	 * g2d.draw(...);
	 * // Restore original transform
	 * g2d.setTransform(saveAT);
	 * </blockquote></pre>
	 *
	 * @param Tx the <code>AffineTransform</code> that was retrieved
	 *           from the <code>getTransform</code> method
	 * @see #transform
	 * @see #getTransform
	 * @see GAffineTransform
	 */
	public abstract void setTransform(GAffineTransform Tx);

	/**
	 * Returns a copy of the current <code>Transform</code> in the
	 * <code>Graphics2D</code> context.
	 * @return the current <code>AffineTransform</code> in the
	 *             <code>Graphics2D</code> context.
	 * @see #transform
	 * @see #setTransform
	 */
	public abstract GAffineTransform getTransform();

	/**
	 * Returns the current <code>Paint</code> of the
	 * <code>Graphics2D</code> context.
	 * @return the current <code>Graphics2D</code> <code>Paint</code>,
	 * which defines a color or pattern.
	 * @see #setPaint
	 * @see java.awt.Graphics#setColor
	 */
	public abstract GPaint getPaint();

	/**
	 * Returns the current <code>Composite</code> in the
	 * <code>Graphics2D</code> context.
	 * @return the current <code>Graphics2D</code> <code>Composite</code>,
	 *              which defines a compositing style.
	 * @see #setComposite
	 */
	public abstract GComposite getComposite();

	/**
	 * Sets the background color for the <code>Graphics2D</code> context.
	 * The background color is used for clearing a region.
	 * When a <code>Graphics2D</code> is constructed for a
	 * <code>Component</code>, the background color is
	 * inherited from the <code>Component</code>. Setting the background color
	 * in the <code>Graphics2D</code> context only affects the subsequent
	 * <code>clearRect</code> calls and not the background color of the
	 * <code>Component</code>.  To change the background
	 * of the <code>Component</code>, use appropriate methods of
	 * the <code>Component</code>.
	 * @param color the background color that isused in
	 * subsequent calls to <code>clearRect</code>
	 * @see #getBackground
	 * @see java.awt.Graphics#clearRect
	 */
	public abstract void setBackground(GColor color);

	/**
	 * Returns the background color used for clearing a region.
	 * @return the current <code>Graphics2D</code> <code>Color</code>,
	 * which defines the background color.
	 * @see #setBackground
	 */
	public abstract GColor getBackground();

	/**
	 * Returns the current <code>Stroke</code> in the
	 * <code>Graphics2D</code> context.
	 * @return the current <code>Graphics2D</code> <code>Stroke</code>,
	 *                 which defines the line style.
	 * @see #setStroke
	 */
	public abstract GBasicStroke getStroke();

	/**
	 * Intersects the current <code>Clip</code> with the interior of the
	 * specified <code>Shape</code> and sets the <code>Clip</code> to the
	 * resulting intersection.  The specified <code>Shape</code> is
	 * transformed with the current <code>Graphics2D</code>
	 * <code>Transform</code> before being intersected with the current
	 * <code>Clip</code>.  This method is used to make the current
	 * <code>Clip</code> smaller.
	 * To make the <code>Clip</code> larger, use <code>setClip</code>.
	 * The <i>user clip</i> modified by this method is independent of the
	 * clipping associated with device bounds and visibility.  If no clip has
	 * previously been set, or if the clip has been cleared using
	 * {@link java.awt.Graphics#setClip(java.awt.Shape) setClip} with a <code>null</code>
	 * argument, the specified <code>Shape</code> becomes the new
	 * user clip.
	 * @param s the <code>Shape</code> to be intersected with the current
	 *          <code>Clip</code>.  If <code>s</code> is <code>null</code>,
	 *          this method clears the current <code>Clip</code>.
	 */
	public abstract void clip(geogebra.common.awt.GShape s);

	/**
	 * Get the rendering context of the <code>Font</code> within this
	 * <code>Graphics2D</code> context.
	 * The {@link GFontRenderContext}
	 * encapsulates application hints such as anti-aliasing and
	 * fractional metrics, as well as target device specific information
	 * such as dots-per-inch.  This information should be provided by the
	 * application when using objects that perform typographical
	 * formatting, such as <code>Font</code> and
	 * <code>TextLayout</code>.  This information should also be provided
	 * by applications that perform their own layout and need accurate
	 * measurements of various characteristics of glyphs such as advance
	 * and line height when various rendering hints have been applied to
	 * the text rendering.
	 *
	 * @return a reference to an instance of FontRenderContext.
	 * @see java.awt.font.FontRenderContext
	 * @see java.awt.Font#createGlyphVector
	 * @see java.awt.font.TextLayout
	 * @since     1.2
	 */

	public abstract GFontRenderContext getFontRenderContext();
	public abstract GColor getColor();
	public abstract GFont getFont();
	public abstract void setFont(GFont font);
	
	public abstract void setColor(GColor selColor);
	public abstract void fillRect(int i, int j, int k, int l);
	public abstract void drawLine(int x1, int y1, int x2, int y2);
	public abstract void setClip(GShape shape);
	public abstract GShape getClip();

	public abstract void drawRect(int i, int j, int k, int l);

	public abstract void setClip(int xAxisStart, int i, int width, int yAxisEnd);

	public abstract void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

	public abstract void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

	public abstract void drawImage(GImage img, int x, int y);
}
