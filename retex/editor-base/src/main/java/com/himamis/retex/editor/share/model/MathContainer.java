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

import com.himamis.retex.editor.share.meta.MetaCharacter;
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

	// table to convert a nibble to a hex char.
	private static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	final public static String toHexString(String s) {
		StringBuilder sb = new StringBuilder(s.length() * 6);
		for (int i = 0; i < s.length(); i++) {
			sb.append(toHexString(s.charAt(i)));
		}

		return sb.toString();
	}

	final public static String toHexString(char c) {
		int i = c + 0;

		StringBuilder hexSB = new StringBuilder(8);
		hexSB.append("\\u");
		hexSB.append(hexChar[(i & 0xf000) >>> 12]);
		hexSB.append(hexChar[(i & 0x0f00) >> 8]); // look up low nibble char
		hexSB.append(hexChar[(i & 0xf0) >>> 4]);
		hexSB.append(hexChar[i & 0x0f]); // look up low nibble char
		return hexSB.toString();
	}

	public static void doPrintStacktrace(String message) {
		try {
			// message null check done in caller
			throw new Exception(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * checks previous character to see if it combines with ch
	 * 
	 * @param i
	 * @param comp
	 * @return true if it's been combined (so ch doesn't need adding)
	 */
	protected boolean checkKorean(int i, MathComponent comp) {

		if (i > 0 && arguments.size() > 0 && i - 1 < arguments.size()) {

			MathComponent compLast = arguments.get(i - 1);
			if (!(compLast instanceof MathCharacter)) {
				return false;
			}
			String s = compLast.toString();

			char newChar = comp.toString().charAt(0);

			char lastChar = 0;

			if (s.length() == 1) {
				lastChar = s.charAt(0);
			} else {
				System.err.println("length isn't 1" + s + " " + toHexString(s));
				return false;
			}

			// case 1
			// we already have Jamo lead + vowel as single unicode

			if (Korean.isKoreanLeadPlusVowelChar(lastChar)
					&& Korean.isKoreanTailChar(newChar, true)) {
				String replaceChar = Korean
						.unflattenKorean(Korean.flattenKorean(lastChar + "")
								+ "" + newChar)
						.toString();
				System.err.println("need to replace " + lastChar + " "
						+ toHexString(lastChar) + " with "
						+ replaceChar + " " + toHexString(replaceChar));

				char c = replaceChar.charAt(0);

				MetaCharacter metaChar = new MetaCharacter(c + "", c + "", c, c,
						MetaCharacter.CHARACTER);

				MathCharacter mathChar = (MathCharacter) compLast;
				mathChar.setChar(metaChar);
				return true;
			}

			// case 2
			// we already have just Jamo lead char as single unicode

			if (Korean.isKoreanLeadChar(lastChar, true)
					&& Korean.isKoreanVowelChar(newChar, true)) {
				String replaceChar = Korean
						.unflattenKorean(lastChar + "" + newChar).toString();
				System.err.println("need to replace " + lastChar + " "
						+ toHexString(lastChar) + " with "
						+ replaceChar + " " + toHexString(replaceChar));

				char c = replaceChar.charAt(0);

				MetaCharacter metaChar = new MetaCharacter(c + "", c + "", c, c,
						MetaCharacter.CHARACTER);

				MathCharacter mathChar = (MathCharacter) compLast;
				mathChar.setChar(metaChar);
				return true;

			}

			// case 3
			// character typed twice (instead of pressing <Shift>)
			String merged = Korean
					.mergeDoubleCharacters(
							Korean.flattenKorean(lastChar + "" + newChar));

			System.err.println(lastChar + "" + newChar + " " + merged + " "
					+ merged.length());

			if (merged.length() == 1) {

				char c = merged.charAt(0);

				MetaCharacter metaChar = new MetaCharacter(c + "", c + "", c, c,
						MetaCharacter.CHARACTER);

				// TODO: deal with case of tail chars + compatibility Jamo
				MathCharacter mathChar = (MathCharacter) compLast;
				mathChar.setChar(metaChar);
				return true;

			}
			
			// case 4
			// we have something like 
			// \u3141 \u3163 \u3142 \u315C \u3134
			// which has been grouped as
			// (\u3141 \u3163 \u3142) + \u315C 
			// but when \u3134 is typed it needs to change to 
			// (\u3141 \u3163) + (\u3142 \u315C \u3134)
			// ie "\u3134" needs to change from tail (\u11ab) to lead (\u1102)
			
			String lastCharFlat = Korean.flattenKorean(lastChar+"");
			
			if (lastCharFlat.length() == 3 && Korean.isVowel(newChar)) {
				
				// System.err.println("case 4");
				
				// not needed, useful for debugging
				// newChar = Korean.convertFromCompatibilityJamo(newChar,
				// false);

				char newLastChar = Korean.unflattenKorean(lastCharFlat.substring(0,2)).charAt(0);

				char newNewChar = Korean.unflattenKorean(
						Korean.tailToLead(lastCharFlat.charAt(2)) + ""
								+ newChar)
						.charAt(0);
				// System.err.println(
				// "lastCharFlat.charAt(2) = " + lastCharFlat.charAt(2)
				// + " " + toHexString(lastCharFlat.charAt(2)));
				// System.err.println(
				// "newChar = " + newChar + " " + toHexString(newChar));
				//
				// System.err.println("newLastChar = " + newLastChar + " "
				// + toHexString(newLastChar));
				// System.err.println("newNewChar = " + newNewChar + " "
				// + toHexString(newNewChar) + " "
				// + Korean.flattenKorean(newNewChar + ""));

				MathCharacter mathChar = (MathCharacter) compLast;
				mathChar.setChar(new MetaCharacter(newLastChar + "",
						newLastChar + "", newLastChar, newLastChar,
						MetaCharacter.CHARACTER));

				mathChar = (MathCharacter) comp;
				mathChar.setChar(new MetaCharacter(newNewChar + "",
						newNewChar + "", newNewChar, newNewChar,
						MetaCharacter.CHARACTER));

				
				// make sure comp is still inserted
				return false;
				
				
			}
			
			// case5: a tailed char is doubled
			// entered as two key presses
			// eg \u3131 \u314F \u3142 \u3145 needs to give \uAC12

			if (lastCharFlat.length() == 3 && !Korean.isVowel(newChar)) {

				System.err.println("case 5");

				// not needed, useful for debugging
				newChar = Korean.convertFromCompatibilityJamo(newChar, false);

				char lastChar2 = lastCharFlat.charAt(2);

				// if this is length 1, merge succeeded
				String doubleCheck = Korean
						.mergeDoubleCharacters(lastChar2 + "" + newChar);

				if (doubleCheck.length() == 1) {
					System.err.println("merge check passed");

					newChar = Korean.unflattenKorean(
							lastCharFlat.substring(0, 2) + "" + doubleCheck)
							.charAt(0);

					MathCharacter mathChar = (MathCharacter) compLast;
					mathChar.setChar(
							new MetaCharacter(newChar + "", newChar + "",
									newChar, newChar, MetaCharacter.CHARACTER));

					return true;

				}


			}

		}

		return false;

	}

	// private String toFlatString() {
	// StringBuilder sb = new StringBuilder();
	// Iterator<MathComponent> it = arguments.iterator();
	//
	// while (it.hasNext()) {
	// sb.append(((MathCharacter) it.next()).getUnicode());
	// }
	//
	// return sb.toString();
	// }

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
	 * Returns i-th argument.
	 * 
	 * @param i
	 *            index
	 * @return argument
	 */
    public MathComponent getArgument(int i) {
		return (arguments != null && arguments.size() > i && i >= 0
				? arguments.get(i) : null);
    }

    /**
	 * Sets i-th argument.
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

    public void clearArguments() {
        if (arguments == null) {
            arguments = new ArrayList<MathComponent>();
        }
        for (int i = arguments.size() - 1; i > -1; i--) {
            removeArgument(i);
        }
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

	public boolean addArgument(int index, MathComponent argument) {
        if (arguments == null) {
            arguments = new ArrayList<MathComponent>(index + 1);
        }
        if (argument != null) {
            argument.setParent(this);
        }
        arguments.add(index, argument);

		return true;
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

    @Override
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

    @Override
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
