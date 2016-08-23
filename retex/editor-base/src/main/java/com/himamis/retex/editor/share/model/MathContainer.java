/* MathContainer.java
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

import java.util.ArrayList;

import com.himamis.retex.editor.share.model.inspect.Inspecting;
import com.himamis.retex.editor.share.model.traverse.Traversing;

/**
 * This class represents abstract model element.
 *
 * @author Bea Petrovicova
 */
abstract public class MathContainer extends MathComponent {

    protected ArrayList<MathComponent> arguments = null;

    MathContainer(int size) {
        if (size > 0) {
            ensureArguments(size);
        }
    }

    protected void ensureArguments(int size) {
        if (arguments == null) {
            arguments = new ArrayList<MathComponent>(size);
        } else {
            arguments.ensureCapacity(size);
        }
        while (arguments.size() < size) {
            arguments.add(null);
        }
    }

    /**
     * Returns i'th argument.
     */
    public MathComponent getArgument(int i) {
		return (arguments != null && arguments.size() != 0 && i >= 0
				? arguments.get(i) : null);
    }

    /**
     * Sets i'th argument.
     */
    public void setArgument(int i, MathComponent argument) {
        if (arguments == null) {
            arguments = new ArrayList<MathComponent>(i + 1);
        }
        if (argument != null) {
            argument.setParent(this);
        }
        arguments.set(i, argument);
    }

	public void removeArgument(int i) {
		if (arguments == null) {
			arguments = new ArrayList<MathComponent>(i + 1);
		}
		if (arguments.get(i) != null) {
			arguments.get(i).setParent(null);
		}
		arguments.remove(i);
	}

    public void addArgument(MathComponent argument) {
        if (arguments == null) {
            arguments = new ArrayList<MathComponent>(1);
        }
        if (argument != null) {
            argument.setParent(this);
        }
        arguments.add(argument);
    }

    public void addArgument(int index, MathComponent argument) {
        if (arguments == null) {
            arguments = new ArrayList<MathComponent>(index + 1);
        }
        if (argument != null) {
            argument.setParent(this);
        }
        arguments.add(index, argument);
    }

    /**
     * Returns number of arguments.
     */
    public int size() {
        return arguments != null ? arguments.size() : 0;
    }

    /**
     * Get index of first argument.
     */
    public int first() {
        // strange but correct
        return next(-1);
    }

    /**
     * Get index of last argument.
     */
    public int last() {
        return prev(arguments != null ? arguments.size() : 0);
    }

    /**
     * Is there a next argument?
     */
    public boolean hasNext(int current) {
        for (int i = current + 1; i < (arguments != null ? arguments.size() : 0); i++) {
            if (getArgument(i) instanceof MathContainer) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get index of next argument.
     */
    public int next(int current) {
        for (int i = current + 1; i < (arguments != null ? arguments.size() : 0); i++) {
            if (getArgument(i) instanceof MathContainer) {
                return i;
            }
        }
        throw new ArrayIndexOutOfBoundsException(
                "Index out of array bounds.");
    }

    /**
     * Is there previous argument?
     */
    public boolean hasPrev(int current) {
        for (int i = current - 1; i >= 0; i--) {
            if (getArgument(i) instanceof MathContainer) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get index of previous argument.
     */
    public int prev(int current) {
        for (int i = current - 1; i >= 0; i--) {
            if (getArgument(i) instanceof MathContainer) {
                return i;
            }
        }
        throw new ArrayIndexOutOfBoundsException(
                "Index out of array bounds.");
    }

    /**
     * Are there any arguments?
     */
    public boolean hasChildren() {
        for (int i = 0; i < (arguments != null ? arguments.size() : 0); i++) {
            if (getArgument(i) instanceof MathContainer) {
                return true;
            }
        }
        return false;
    }

    public int getInsertIndex() {
        return 0;
    }

    public int getInitialIndex() {
        return 0;
    }

    public MathComponent traverse(Traversing traversing) {
        MathComponent component = traversing.process(this);
        if (component != this) {
            return component;
        }
        for (int i = 0; i < size(); i++) {
            MathComponent argument = getArgument(i);
            setArgument(i, argument.traverse(traversing));
        }
        return this;
    }

    @Override
    public boolean inspect(Inspecting inspecting) {
        if (inspecting.check(this)) {
            return true;
        }
        for (int i = 0; i < size(); i++) {
            MathComponent argument = getArgument(i);
            if (inspecting.check(argument)) {
                return true;
            }
        }
        return false;
    }

    public abstract MathContainer copy();

	public int indexOf(MathComponent argument) {
		return arguments.indexOf(argument);
	}

	public void delArgument(int i) {
		arguments.remove(i);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName());
		sb.append('[');
		for (int i = 0; i < size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(getArgument(i));
		}
		sb.append(']');
		return sb.toString();
	}

}
