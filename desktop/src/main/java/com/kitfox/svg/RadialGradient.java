/*
 * SVG Salamander
 * Copyright (c) 2004, Mark McKay
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 *   - Redistributions of source code must retain the above 
 *     copyright notice, this list of conditions and the following
 *     disclaimer.
 *   - Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * Mark McKay can be contacted at mark@kitfox.com.  Salamander and other
 * projects can be found at http://www.kitfox.com
 *
 * Created on January 26, 2004, 1:55 AM
 */
package com.kitfox.svg;

import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.kitfox.svg.xml.StyleAttribute;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class RadialGradient extends Gradient {
	public static final String TAG_NAME = "radialgradient";

	float cx = 0.5f;
	float cy = 0.5f;
	float fx = 0.5f;
	float fy = 0.5f;
	float r = 0.5f;

	/**
	 * Creates a new instance of RadialGradient
	 */
	public RadialGradient() {
	}

	@Override
	public String getTagName() {
		return TAG_NAME;
	}

	@Override
	protected void build() throws SVGException {
		super.build();

		StyleAttribute sty = new StyleAttribute();

		if (getPres(sty.setName("cx"))) {
			cx = sty.getFloatValueWithUnits();
		}

		if (getPres(sty.setName("cy"))) {
			cy = sty.getFloatValueWithUnits();
		}

		if (getPres(sty.setName("fx"))) {
			fx = sty.getFloatValueWithUnits();
		}

		if (getPres(sty.setName("fy"))) {
			fy = sty.getFloatValueWithUnits();
		}

		if (getPres(sty.setName("r"))) {
			r = sty.getFloatValueWithUnits();
		}
	}

	@Override
	public Paint getPaint(Rectangle2D bounds, AffineTransform xform) {
		com.kitfox.svg.batik.MultipleGradientPaint.CycleMethodEnum method;
		switch (spreadMethod) {
		default:
		case SM_PAD:
			method = com.kitfox.svg.batik.MultipleGradientPaint.NO_CYCLE;
			break;
		case SM_REPEAT:
			method = com.kitfox.svg.batik.MultipleGradientPaint.REPEAT;
			break;
		case SM_REFLECT:
			method = com.kitfox.svg.batik.MultipleGradientPaint.REFLECT;
			break;
		}

		Paint paint;
		Point2D.Float pt1 = new Point2D.Float(cx, cy);
		Point2D.Float pt2 = new Point2D.Float(fx, fy);
		if (gradientUnits == GU_USER_SPACE_ON_USE) {
			paint = new com.kitfox.svg.batik.RadialGradientPaint(pt1, r, pt2,
					getStopFractions(), getStopColors(), method,
					com.kitfox.svg.batik.MultipleGradientPaint.SRGB,
					gradientTransform);
		} else {
			AffineTransform viewXform = new AffineTransform();
			viewXform.translate(bounds.getX(), bounds.getY());
			viewXform.scale(bounds.getWidth(), bounds.getHeight());

			viewXform.concatenate(gradientTransform);

			paint = new com.kitfox.svg.batik.RadialGradientPaint(pt1, r, pt2,
					getStopFractions(), getStopColors(), method,
					com.kitfox.svg.batik.MultipleGradientPaint.SRGB, viewXform);
		}

		return paint;
	}

	/**
	 * Updates all attributes in this diagram associated with a time event. Ie,
	 * all attributes with track information.
	 *
	 * @return - true if this node has changed state as a result of the time
	 *         update
	 */
	@Override
	public boolean updateTime(double curTime) throws SVGException {
		// if (trackManager.getNumTracks() == 0) return false;
		boolean changeState = super.updateTime(curTime);

		// Get current values for parameters
		StyleAttribute sty = new StyleAttribute();
		if (getPres(sty.setName("cx"))) {
			float newVal = sty.getFloatValueWithUnits();
			if (newVal != cx) {
				cx = newVal;
			}
		}

		if (getPres(sty.setName("cy"))) {
			float newVal = sty.getFloatValueWithUnits();
			if (newVal != cy) {
				cy = newVal;
			}
		}

		if (getPres(sty.setName("fx"))) {
			float newVal = sty.getFloatValueWithUnits();
			if (newVal != fx) {
				fx = newVal;
			}
		}

		if (getPres(sty.setName("fy"))) {
			float newVal = sty.getFloatValueWithUnits();
			if (newVal != fy) {
				fy = newVal;
			}
		}

		if (getPres(sty.setName("r"))) {
			float newVal = sty.getFloatValueWithUnits();
			if (newVal != r) {
				r = newVal;
			}
		}

		return changeState;
	}
}
