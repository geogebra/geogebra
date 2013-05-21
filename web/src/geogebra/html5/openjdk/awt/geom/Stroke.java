/*
 * @(#)Stroke.java	1.22 03/12/19
 *
 * Copyright (c) 1997, 2006, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package geogebra.html5.openjdk.awt.geom;

import geogebra.common.awt.GGraphics2D;

/**
 * The <code>BasicStroke</code> class defines a basic set of rendering
 * attributes for the outlines of graphics primitives, which are rendered with a
 * {@link GGraphics2D} object that has its Stroke attribute set to this
 * <code>BasicStroke</code>. The rendering attributes defined by
 * <code>BasicStroke</code> describe the shape of the mark made by a pen drawn
 * along the outline of a {@link Shape} and the decorations applied at the ends
 * and joins of path segments of the <code>Shape</code>. These rendering
 * attributes include:
 * <dl compact>
 * <dt><i>width</i>
 * <dd>The pen width, measured perpendicularly to the pen trajectory.
 * <dt><i>end caps</i>
 * <dd>The decoration applied to the ends of unclosed subpaths and dash
 * segments. Subpaths that start and end on the same point are still considered
 * unclosed if they do not have a CLOSE segment. See
 * {@link java.awt.geom.PathIterator#SEG_CLOSE SEG_CLOSE} for more information
 * on the CLOSE segment. The three different decorations are: {@link #CAP_BUTT},
 * {@link #CAP_ROUND}, and {@link #CAP_SQUARE}.
 * <dt><i>line joins</i>
 * <dd>The decoration applied at the intersection of two path segments and at
 * the intersection of the endpoints of a subpath that is closed using
 * {@link java.awt.geom.PathIterator#SEG_CLOSE SEG_CLOSE}. The three different
 * decorations are: {@link #JOIN_BEVEL}, {@link #JOIN_MITER}, and
 * {@link #JOIN_ROUND}.
 * <dt><i>miter limit</i>
 * <dd>The limit to trim a line join that has a JOIN_MITER decoration. A line
 * join is trimmed when the ratio of miter length to stroke width is greater
 * than the miterlimit value. The miter length is the diagonal length of the
 * miter, which is the distance between the inside corner and the outside corner
 * of the intersection. The smaller the angle formed by two line segments, the
 * longer the miter length and the sharper the angle of intersection. The
 * default miterlimit value of 10.0f causes all angles less than 11 degrees to
 * be trimmed. Trimming miters converts the decoration of the line join to
 * bevel.
 * <dt><i>dash attributes</i>
 * <dd>The definition of how to make a dash pattern by alternating between
 * opaque and transparent sections.
 * </dl>
 * All attributes that specify measurements and distances controlling the shape
 * of the returned outline are measured in the same coordinate system as the
 * original unstroked <code>Shape</code> argument. When a
 * <code>Graphics2D</code> object uses a <code>Stroke</code> object to redefine
 * a path during the execution of one of its <code>draw</code> methods, the
 * geometry is supplied in its original form before the <code>Graphics2D</code>
 * transform attribute is applied. Therefore, attributes such as the pen width
 * are interpreted in the user space coordinate system of the
 * <code>Graphics2D</code> object and are subject to the scaling and shearing
 * effects of the user-space-to-device-space transform in that particular
 * <code>Graphics2D</code>. For example, the width of a rendered shape's outline
 * is determined not only by the width attribute of this
 * <code>BasicStroke</code>, but also by the transform attribute of the
 * <code>Graphics2D</code> object. Consider this code: <blockquote><tt>
 *      // sets the Graphics2D object's Tranform attribute
 * 	g2d.scale(10, 10);
 *      // sets the Graphics2D object's Stroke attribute
 *      g2d.setStroke(new BasicStroke(1.5f));
 * </tt></blockquote> Assuming there are no other scaling transforms added to
 * the <code>Graphics2D</code> object, the resulting line will be approximately
 * 15 pixels wide. As the example code demonstrates, a floating-point line
 * offers better precision, especially when large transforms are used with a
 * <code>Graphics2D</code> object. When a line is diagonal, the exact width
 * depends on how the rendering pipeline chooses which pixels to fill as it
 * traces the theoretical widened outline. The choice of which pixels to turn on
 * is affected by the antialiasing attribute because the antialiasing rendering
 * pipeline can choose to color partially-covered pixels.
 * <p>
 * For more information on the user space coordinate system and the rendering
 * process, see the <code>Graphics2D</code> class comments.
 * 
 * @see GGraphics2D
 * @version 1.40, 12/19/03
 * @author Jim Graham
 */
public class Stroke {

	/**
	 * Interface to be implemented by enumerated CSS values.
	 */
	public interface HasCssName {

		/**
		 * Gets the CSS name associated with this value.
		 */
		String getCssName();
	}

	public static enum LineJoin implements HasCssName {
		/**
		 * Joins path segments by extending their outside edges until they meet.
		 */
		MITER {
			public String getCssName() {
				return "miter";
			}
		},
		/**
		 * Joins path segments by rounding off the corner at a radius of half
		 * the line width.
		 */
		ROUND {
			public String getCssName() {
				return "round";
			}
		},
		/**
		 * Joins path segments by connecting the outer corners of their wide
		 * outlines with a straight segment.
		 */
		BEVEL {
			public String getCssName() {
				return "bevel";
			}
		}
	};

	public static enum LineCap implements HasCssName {
		/**
		 * Ends unclosed subpaths and dash segments with no added decoration.
		 */
		BUTT {
			public String getCssName() {
				return "butt";
			}
		},
		/**
		 * Ends unclosed subpaths and dash segments with a round decoration that
		 * has a radius equal to half of the width of the pen.
		 */
		ROUND {
			public String getCssName() {
				return "round";
			}
		},
		/**
		 * Ends unclosed subpaths and dash segments with a square projection
		 * that extends beyond the end of the segment to a distance equal to
		 * half of the line width.
		 */
		SQUARE {
			public String getCssName() {
				return "square";
			}
		}
	};

	protected double width;
	protected LineJoin join;
	protected LineCap cap;
	protected double miterlimit;

	/**
	 * Constructs a new <code>Stroke</code> with the specified attributes.
	 * 
	 * @param width
	 *            the width of this <code>Stroke</code>. The width must be
	 *            greater than or equal to 0.0. If width is set to 0.0, the
	 *            stroke is rendered as the thinnest possible line for the
	 *            target device and the antialias hint setting.
	 * @param cap
	 *            the decoration of the ends of a <code>Stroke</code>
	 * @param join
	 *            the decoration applied where path segments meet
	 * @param miterlimit
	 *            the limit to trim the miter join. The miterlimit must be
	 *            greater than or equal to 1.0.
	 * @throws IllegalArgumentException
	 *             if <code>width</code> is negative
	 * @throws IllegalArgumentException
	 *             if <code>miterlimit</code> is less than 1 and
	 *             <code>join</code> is {@link LineJoin#MITER}
	 */
	public Stroke(double width, LineCap cap, LineJoin join, double miterlimit) {
		if (width < 0.0) {
			throw new IllegalArgumentException("negative width");
		}
		if (join == LineJoin.MITER) {
			if (miterlimit < 1.0) {
				throw new IllegalArgumentException("miter limit < 1");
			}
		}
		this.width = width;
		this.cap = cap;
		this.join = join;
		this.miterlimit = miterlimit;
	}

	/**
	 * Constructs a solid <code>Stroke</code> with the specified attributes. The
	 * <code>miterlimit</code> parameter is unnecessary in cases where the
	 * default is allowable or the line joins are not specified as JOIN_MITER.
	 * 
	 * @param width
	 *            the width of the <code>BasicStroke</code>
	 * @param cap
	 *            the decoration of the ends of a <code>BasicStroke</code>
	 * @param join
	 *            the decoration applied where path segments meet
	 * @throws IllegalArgumentException
	 *             if <code>width</code> is negative
	 */
	public Stroke(double width, LineCap cap, LineJoin join) {
		this(width, cap, join, 10.0);
	}

	/**
	 * Constructs a solid <code>Stroke</code> with the specified line width and
	 * with default values for the cap and join styles.
	 * 
	 * @param width
	 *            the width of the <code>BasicStroke</code>
	 * @throws IllegalArgumentException
	 *             if <code>width</code> is negative
	 */
	public Stroke(float width) {
		this(width, LineCap.SQUARE, LineJoin.MITER, 10.0);
	}

	/**
	 * Constructs a new <code>BasicStroke</code> with defaults for all
	 * attributes. The default attributes are a solid line of width
	 * <code>1.0</code>, {@link LineCap#SQUARE}, {@link LineJoin#MITER}, a miter
	 * limit of <code>10.0</code>.
	 */
	public Stroke() {
		this(1.0, LineCap.SQUARE, LineJoin.MITER, 10.0);
	}

	/**
	 * Returns the line width.
	 * 
	 * @return the line width of this <code>Stroke</code>.
	 */
	public double getLineWidth() {
		return width;
	}

	/**
	 * Returns the end cap style.
	 * 
	 * @return the end cap style of this <code>Stroke</code> as one of the
	 *         static {@link LineCap} values that define possible end cap
	 *         styles.
	 */
	public LineCap getEndCap() {
		return cap;
	}

	/**
	 * Returns the line join style.
	 * 
	 * @return the line join style of the <code>Stroke</code> as one of the
	 *         static {@link LineJoin} values that define possible line join
	 *         styles.
	 */
	public LineJoin getLineJoin() {
		return join;
	}

	/**
	 * Returns the limit of miter joins.
	 * 
	 * @return the limit of miter joins of the <code>Stroke</code>.
	 */
	public double getMiterLimit() {
		return miterlimit;
	}

	/**
	 * Returns true if this BasicStroke represents the same stroking operation
	 * as the given argument.
	 */
	/**
	 * Tests if a specified object is equal to this <code>Stroke</code> by first
	 * testing if it is a <code>BasicStroke</code> and then comparing its width,
	 * join, cap and miterlimit with those of this <code>Stroke</code>.
	 * 
	 * @param obj
	 *            the specified object to compare to this <code>Stroke</code>
	 * @return <code>true</code> if the width, join, cap and miter limit are the
	 *         same for both objects; <code>false</code> otherwise.
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof Stroke)) {
			return false;
		}

		Stroke bs = (Stroke) obj;
		if (width != bs.width) {
			return false;
		}

		if (join != bs.join) {
			return false;
		}

		if (cap != bs.cap) {
			return false;
		}

		if (miterlimit != bs.miterlimit) {
			return false;
		}

		return true;
	}
}
