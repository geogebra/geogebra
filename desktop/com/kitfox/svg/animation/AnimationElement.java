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
 * Created on August 15, 2004, 2:52 AM
 */

package com.kitfox.svg.animation;

import com.kitfox.svg.SVGElement;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public abstract class AnimationElement extends SVGElement {
	protected String attribName;
	// protected String attribType;
	protected int attribType = AT_AUTO;

	public static final int AT_CSS = 0;
	public static final int AT_XML = 1;
	public static final int AT_AUTO = 2; // Check CSS first, then XML

	/**
	 * <a href="http://www.w3.org/TR/smil20/smil-timing.html#adef-fill">More
	 * about the <b>fill</b> attribute</a>
	 */
	public static final int FT_REMOVE = 0;
	public static final int FT_FREEZE = 1;
	public static final int FT_HOLD = 2;
	public static final int FT_TRANSITION = 3;
	public static final int FT_AUTO = 4;
	public static final int FT_DEFAULT = 5;

	/** Additive state of track */
	public static final int AD_REPLACE = 0;
	public static final int AD_SUM = 1;

	int additiveType = AD_REPLACE;

	/** Accumlative state */
	public static final int AC_REPLACE = 0;
	public static final int AC_SUM = 1;

	int accumulateType = AC_REPLACE;

	/** Creates a new instance of AnimateEle */
	public AnimationElement() {
	}

	public static String animationElementToString(int attrValue) {
		switch (attrValue) {
		case AT_CSS:
			return "CSS";
		case AT_XML:
			return "XML";
		case AT_AUTO:
			return "AUTO";
		default:
			throw new RuntimeException("Unknown element type");
		}
	}

}
