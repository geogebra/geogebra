/* MathComponent.java
 * =========================================================================
 * This file is part of the Mirai Math TN - http://mirai.sourceforge.net
 *
 * Copyright (C) 2008-2009 Bea Petrovicova
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 */

package com.himamis.retex.editor.share.model;

import java.io.Serializable;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.meta.Tag;
import com.himamis.retex.editor.share.model.inspect.Inspectable;
import com.himamis.retex.editor.share.model.traverse.Traversable;

/**
 * This class represents abstract model element.
 *
 * @author Bea Petrovicova
 */
abstract public class MathComponent implements Traversable, Inspectable, Serializable {

    /**
	 * MathComponent needs to implement serializable for Parcel (Android project)
	 */
	private static final long serialVersionUID = 1L;

    @Weak
	private MathContainer parent;

    /**
	 * Gets parent of this component.
	 * 
	 * @return parent of this component.
	 */
    public MathContainer getParent() {
        return parent;
    }

    /**
	 * Sets parent of this component.
	 * 
	 * @param container
	 *            parent
	 */
    void setParent(MathContainer container) {
        this.parent = container;
    }

    /**
	 * Gets index of this component within its parent component.
	 * 
	 * @return index within parent
	 */
    public int getParentIndex() {
        if (parent == null) {
            return 0;
        }
        int index = parent.indexOf(this);
        if (index >= 0) {
            return index;
        }

        throw new RuntimeException("Parent reference is not set correctly");
    }

    /**
     * Creates a deep copy of the object.
     *
     * @return copy
     */
    abstract public MathComponent copy();

	/**
	 * @return this wrapped in MathSequence TODO unused?
	 */
	public MathComponent wrap() {
		MathSequence seq = new MathSequence();
		seq.addArgument(this);
		return seq;
	}

	/**
	 * @param tag
	 *            container tag
	 * @return whether this is a container with given tag
	 */
	public boolean hasTag(Tag tag) {
		return false;
	}

}
