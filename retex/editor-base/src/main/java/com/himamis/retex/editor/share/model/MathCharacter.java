/* MathCharacter.java
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

import com.himamis.retex.editor.share.meta.MetaCharacter;
import com.himamis.retex.editor.share.model.inspect.Inspecting;
import com.himamis.retex.editor.share.model.traverse.Traversing;

/**
 * Character. This class is part of model.
 * <p/>
 * x^2
 * x_j
 *
 * @author Bea Petrovicova
 */
public class MathCharacter extends MathComponent {

    private MetaCharacter meta;

    /**
     * Use MathFormula.newCharacter(...)
     */
    public MathCharacter(MetaCharacter meta) {
        this.meta = meta;
    }

    /**
     * Gets parent of this component.
     */
	@Override
	public MathSequence getParent() {
        return (MathSequence) super.getParent();
    }

	@Override
	public MathCharacter copy() {
        return new MathCharacter(meta);
    }

    public String getName() {
        return meta.getName();
    }

    public String getTexName() {
        return meta.getTexName();
    }

    public char getUnicode() {
        return meta.getUnicode();
    }

    /**
     * Is Character.
     */
    public boolean isCharacter() {
        return meta.getType() == MetaCharacter.CHARACTER;
    }

    /**
     * Is Operator.
     */
    public boolean isOperator() {
        return meta.getType() == MetaCharacter.OPERATOR;
    }

    /**
     * Is Symbol.
     */
    public boolean isSymbol() {
        return meta.getType() == MetaCharacter.SYMBOL;
    }

	@Override
	public MathComponent traverse(Traversing traversing) {
        return traversing.process(this);
    }

    @Override
    public boolean inspect(Inspecting inspecting) {
        return inspecting.check(this);
    }

	@Override
	public String toString() {
		return meta.getUnicode() + "";
	}

	public boolean isSeparator() {
		return meta.getUnicode() == ',' || meta.getUnicode() == ';'
				|| meta.getUnicode() == ':';
	}
}
