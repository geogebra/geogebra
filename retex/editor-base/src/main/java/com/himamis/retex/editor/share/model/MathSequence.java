/* MathSequence.java
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

public class MathSequence extends MathContainer {

    MathSequence(int i) {
        super(i);
        ensureArguments(i);
    }

    /**
     * Use MathFormula.newSequence(...)
     */
    /*public MathSequence(MathFormula formula, String text) {
        this(text.length());
        for (int i = 0; i < text.length(); i++) {
            setArgument(i, new MathCharacter(formula, formula.getMetaModel().getCharacter("" + text.charAt(i))));
        }
    }*/

    /**
     * Use MathFormula.newSequence(...)
     */
    public MathSequence() {
        super(0);
        ensureArguments(0);
    }

    public void addArgument(MathComponent argument) {
        if (argument != null) {
            argument.setParent(this);
        }
        arguments.add(argument);
    }

    public void addArgument(int i, MathComponent argument) {
        if (argument != null) {
            argument.setParent(this);
        }
        arguments.add(i, argument);
    }

    public void delArgument(int i) {
        arguments.remove(i);
    }

    public MathSequence copy() {
        MathSequence sequence = new MathSequence();
        for (int i = 0; i < arguments.size(); i++) {
            MathComponent component = getArgument(i);
            MathComponent newComponent = component.copy();
            sequence.addArgument(i, newComponent);
        }
        return sequence;
    }

    /**
     * has operator (including power).
     */
    public boolean hasOperator() {
        for (int i = 0; i < size(); i++) {
            if (isOperator(i)) {
                return true;
            } else if (getArgument(i) instanceof MathFunction &&
                    ("^".equals(((MathFunction) getArgument(i)).getName()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is i'th argument script.
     */
    public boolean isScript(int i) {
        return i >= 0 && i < size() && getArgument(i) instanceof MathFunction &&
                ("^".equals(((MathFunction) getArgument(i)).getName()) ||
                        "_".equals(((MathFunction) getArgument(i)).getName()));
    }

    /**
     * Is i'th argument character.
     */
    public boolean isCharacter(int i) {
        return i >= 0 && i < size() &&
                getArgument(i) instanceof MathCharacter &&
                ((MathCharacter) getArgument(i)).isCharacter();
    }

    /**
     * Is i'th argument operator.
     */
    public boolean isOperator(int i) {
        return i >= 0 && i < size() &&
                getArgument(i) instanceof MathCharacter &&
                ((MathCharacter) getArgument(i)).isOperator();
    }

    /**
     * Is i'th argument symbol.
     */
    public boolean isSymbol(int i) {
        return i >= 0 && i < size() &&
                getArgument(i) instanceof MathCharacter &&
                ((MathCharacter) getArgument(i)).isSymbol();
    }

}
