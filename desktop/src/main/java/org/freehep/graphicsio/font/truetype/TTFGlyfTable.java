// Copyright 2001-2006, FreeHEP.
package org.freehep.graphicsio.font.truetype;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.IOException;

/**
 * GLYPH Table.
 * 
 * @author Simon Fischer
 * @version $Id: TTFGlyfTable.java,v 1.5 2009-08-17 21:44:45 murkle Exp $
 */
public class TTFGlyfTable extends TTFVersionTable {

	/**
	 * If this variable is set to false then the glyphs will not be read until
	 * they are retrieved with <tt>getGlyph(int)</tt>.
	 */
	private static final boolean READ_GLYPHS = false;

	public abstract class Glyph {

		public int xMin, yMin, xMax, yMax;

		public abstract String getType();

		public abstract GeneralPath getShape();

		public void read() throws IOException {
			xMin = ttf.readFWord();
			yMin = ttf.readFWord();
			xMax = ttf.readFWord();
			yMax = ttf.readFWord();
		}

		public Rectangle getBBox() {
			return new Rectangle(xMin, yMin, xMax - xMin, yMax - yMin);
		}

		@Override
		public String toString() {
			return "[" + getType() + "] (" + xMin + "," + yMin + "):(" + xMax
					+ "," + yMax + ")";
		}

		public String toDetailedString() {
			return toString();
		}
	}

	// --------------------------------------------------------------------------------

	public class SimpleGlyph extends Glyph {

		private static final int ON_CURVE = 0;

		private static final int X_SHORT = 1;

		private static final int Y_SHORT = 2;

		private static final int REPEAT_FLAG = 3;

		private static final int X_SAME = 4;

		private static final int Y_SAME = 5;

		private static final int X_POSITIVE = 4;

		private static final int Y_POSITIVE = 5;

		public int numberOfContours;

		public int[] endPtsOfContours;

		public int[] instructions;

		public int[] flags;

		public int[] xCoordinates, yCoordinates;

		public boolean[] onCurve;

		public GeneralPath shape;

		public SimpleGlyph(int numberOfContours) {
			this.numberOfContours = numberOfContours;
			this.endPtsOfContours = new int[numberOfContours];
		}

		@Override
		public String getType() {
			return "Simple Glyph";
		}

		@Override
		public void read() throws IOException {
			super.read();

			for (int i = 0; i < endPtsOfContours.length; i++) {
				endPtsOfContours[i] = ttf.readUShort();
			}

			instructions = new int[ttf.readUShort()];
			for (int i = 0; i < instructions.length; i++) {
				instructions[i] = ttf.readByte();
			}

			int numberOfPoints = endPtsOfContours[endPtsOfContours.length - 1]
					+ 1;
			flags = new int[numberOfPoints];
			xCoordinates = new int[numberOfPoints];
			yCoordinates = new int[numberOfPoints];
			onCurve = new boolean[numberOfPoints];
			int repeatCount = 0;
			int repeatFlag = 0;
			for (int i = 0; i < numberOfPoints; i++) {
				if (repeatCount > 0) {
					flags[i] = repeatFlag;
					repeatCount--;
				} else {
					flags[i] = ttf.readRawByte();
					if (TTFInput.flagBit(flags[i], REPEAT_FLAG)) {
						repeatCount = ttf.readByte();
						repeatFlag = flags[i];
					}
				}
				TTFInput.checkZeroBit(flags[i], 6, "flags");
				TTFInput.checkZeroBit(flags[i], 7, "flags");
				onCurve[i] = TTFInput.flagBit(flags[i], ON_CURVE);
			}

			int last = 0;
			for (int i = 0; i < numberOfPoints; i++) {
				if (TTFInput.flagBit(flags[i], X_SHORT)) {
					if (TTFInput.flagBit(flags[i], X_POSITIVE)) {
						last = xCoordinates[i] = last + ttf.readByte();
					} else {
						last = xCoordinates[i] = last - ttf.readByte();
					}
				} else {
					if (TTFInput.flagBit(flags[i], X_SAME)) {
						last = xCoordinates[i] = last;
					} else {
						last = xCoordinates[i] = last + ttf.readShort();
					}
				}
			}

			last = 0;
			for (int i = 0; i < numberOfPoints; i++) {
				if (TTFInput.flagBit(flags[i], Y_SHORT)) {
					if (TTFInput.flagBit(flags[i], Y_POSITIVE)) {
						last = yCoordinates[i] = last + ttf.readByte();
					} else {
						last = yCoordinates[i] = last - ttf.readByte();
					}
				} else {
					if (TTFInput.flagBit(flags[i], Y_SAME)) {
						last = yCoordinates[i] = last;
					} else {
						last = yCoordinates[i] = last + ttf.readShort();
					}
				}
			}
		}

		@Override
		public String toString() {
			String str = super.toString() + ", " + numberOfContours
					+ " contours, endPts={";
			for (int i = 0; i < numberOfContours; i++) {
				str += (i == 0 ? "" : ",") + endPtsOfContours[i];
			}
			str += "}, " + instructions.length + " instructions";
			return str;
		}

		@Override
		public String toDetailedString() {
			String str = toString() + "\n  instructions = {";
			for (int i = 0; i < instructions.length; i++) {
				str += Integer.toHexString(instructions[i]) + " ";
			}
			return str + "}";
		}

		@Override
		public GeneralPath getShape() {
			if (shape != null) {
				return shape;
			}

			shape = new GeneralPath(GeneralPath.WIND_NON_ZERO);
			int p = 0;
			for (int i = 0; i < endPtsOfContours.length; i++) {
				int startIndex = p++;
				shape.moveTo(xCoordinates[startIndex],
						yCoordinates[startIndex]);
				boolean lastOnCurve = true;
				while (p <= endPtsOfContours[i]) {

					if (onCurve[p]) {
						if (lastOnCurve) {
							shape.lineTo(xCoordinates[p], yCoordinates[p]);
						} else {
							shape.quadTo(xCoordinates[p - 1],
									yCoordinates[p - 1], xCoordinates[p],
									yCoordinates[p]);
						}
						lastOnCurve = true;
					} else {
						if (!lastOnCurve) {
							int x1 = xCoordinates[p - 1];
							int y1 = yCoordinates[p - 1];
							int x2 = (int) ((x1 + xCoordinates[p]) / 2.0);
							int y2 = (int) ((y1 + yCoordinates[p]) / 2.0);
							shape.quadTo(x1, y1, x2, y2);
						}
						lastOnCurve = false;
					}
					p++;
				}
				if (!onCurve[p - 1]) {
					shape.quadTo(xCoordinates[p - 1], yCoordinates[p - 1],
							xCoordinates[startIndex], yCoordinates[startIndex]);
				} else if ((xCoordinates[p - 1] != xCoordinates[startIndex])
						|| (yCoordinates[p - 1] != yCoordinates[startIndex])) {
					shape.closePath();
				}
			}
			return shape;
		}
	}

	// --------------------------------------------------------------------------------

	public class CompositeGlyph extends Glyph {

		private static final int ARGS_WORDS = 0;

		private static final int ARGS_XY = 1;

		private static final int SCALE = 3;

		private static final int XY_SCALE = 6;

		private static final int TWO_BY_TWO = 7;

		private static final int MORE_COMPONENTS = 5;

		private GeneralPath shape;

		private int noComponents;

		@Override
		public String getType() {
			return "Composite Glyph";
		}

		@Override
		public GeneralPath getShape() {
			return shape;
		}

		@Override
		public void read() throws IOException {
			super.read();
			shape = new GeneralPath();

			noComponents = 0;
			boolean more = true;
			while (more) {
				noComponents++;
				ttf.readUShortFlags();
				more = ttf.flagBit(MORE_COMPONENTS);
				int glyphIndex = ttf.readUShort();
				int arg1, arg2;
				if (ttf.flagBit(ARGS_WORDS)) {
					arg1 = ttf.readShort();
					arg2 = ttf.readShort();
				} else {
					arg1 = ttf.readChar();
					arg2 = ttf.readChar();
				}
				AffineTransform t = new AffineTransform();
				if (ttf.flagBit(ARGS_XY)) {
					t.translate(arg1, arg2);
				} else {
					System.err.println(
							"TTFGlyfTable: ARGS_ARE_POINTS not implemented.");
				}

				if (ttf.flagBit(SCALE)) {
					double scale = ttf.readF2Dot14();
					t.scale(scale, scale);
				} else if (ttf.flagBit(XY_SCALE)) {
					double scaleX = ttf.readF2Dot14();
					double scaleY = ttf.readF2Dot14();
					t.scale(scaleX, scaleY);
				} else if (ttf.flagBit(TWO_BY_TWO)) {
					System.err.println(
							"TTFGlyfTable: WE_HAVE_A_TWO_BY_TWO not implemented.");
				}

				GeneralPath appendGlyph = (GeneralPath) getGlyph(glyphIndex)
						.getShape().clone();
				appendGlyph.transform(t);
				shape.append(appendGlyph, false);
			}
		}

		@Override
		public String toString() {
			return super.toString() + ", " + noComponents + " components";
		}

	}

	// --------------------------------------------------------------------------------

	public Glyph[] glyphs;

	private long[] offsets;

	@Override
	public String getTag() {
		return "glyf";
	}

	@Override
	public void readTable() throws IOException {
		glyphs = new Glyph[((TTFMaxPTable) getTable("maxp")).numGlyphs];
		offsets = ((TTFLocaTable) getTable("loca")).offset;

		if (READ_GLYPHS) {
			for (int i = 0; i < glyphs.length; i++) {
				if ((i > 0) && (offsets[i - 1] == offsets[i])) {
					glyphs[i] = glyphs[i - 1];
				} else {
					try {
						getGlyph(i);
					} catch (IOException e) {
						System.err.println("While reading glyph #" + i
								+ " (offset " + offsets[i] + "):");
						e.printStackTrace();
					}
				}
			}
		}

	}

	public Glyph getGlyph(int i) throws IOException {
		if (glyphs[i] != null) {
			return glyphs[i];
		}
		ttf.pushPos();
		ttf.seek(offsets[i]);
		int numberOfContours = ttf.readShort();
		if (numberOfContours >= 0) {
			glyphs[i] = new SimpleGlyph(numberOfContours);
		} else {
			glyphs[i] = new CompositeGlyph();
		}
		glyphs[i].read();
		// System.out.println(i+": "+offsets[i]+"-"+ttf.getPointer());
		ttf.popPos();
		return glyphs[i];
	}

	@Override
	public String toString() {
		String str = super.toString();
		for (int i = 0; i < glyphs.length; i++) {
			str += "\n  #" + i + ": " + glyphs[i];
		}
		return str;
	}
}
