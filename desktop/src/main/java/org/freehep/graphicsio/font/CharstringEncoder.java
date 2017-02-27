// Copyright 2001 freehep
package org.freehep.graphicsio.font;

import java.awt.Shape;
import java.io.IOException;
import java.io.OutputStream;

import org.freehep.graphicsio.QuadToCubicPathConstructor;

/**
 * Encoder to encode "CharStrings" used in PostScript and Type 1 Fonts.
 * 
 * @author Simon Fischer
 * @version $Id: CharstringEncoder.java,v 1.4 2009-08-17 21:44:45 murkle Exp $
 */
public class CharstringEncoder extends QuadToCubicPathConstructor {

	private static final int LAST_POINT = 0;

	private static final int HORIZONTAL = 1;

	private static final int VERTICAL = 2;

	private static final int BOTH = 3;

	private OutputStream out;

	private int currentX, currentY;

	public CharstringEncoder(OutputStream out) {
		this.out = out;
		currentX = currentY = 0;
	}

	private int writeNumber(double v) throws IOException {
		int round = (int) Math.round(v);
		writeNumber(round);
		return round;
	}

	private void writeNumber(int v) throws IOException {
		if ((v >= -107) && (v <= 107)) {
			out.write(v + 139);
		} else if ((v >= 108) && (v <= 1131)) {
			int highByte = (v - 108) / 256;
			out.write(highByte + 247);
			out.write(v - 108 - 256 * highByte);
		} else if ((v >= -1131) && (v <= -108)) {
			int highByte = (v + 108) / 256;
			out.write(-highByte + 251);
			out.write(-(v + 108 - 256 * highByte));
		} else {
			out.write(255);
			// copied from DataOutputStream correct? '>>>'?
			out.write((v >>> 24) & 0xFF);
			out.write((v >>> 16) & 0xFF);
			out.write((v >>> 8) & 0xFF);
			out.write((v >>> 0) & 0xFF);
		}
	}

	protected void writeCommand(int com) throws IOException {
		if (com >= 31) {
			throw new IOException("Charstring command out of range: " + com);
		}
		out.write(com);
	}

	protected void writeExtCommand(int com) throws IOException {
		out.write(12);
		out.write(com);
	}

	// -------------------- PATH CONSTRUCTION --------------------

	private void writePoint(double x, double y) throws IOException {
		currentX += writeNumber(x - currentX);
		currentY += writeNumber(y - currentY);
	}

	private void writeX(double x) throws IOException {
		currentX += writeNumber(x - currentX);
	}

	private void writeY(double y) throws IOException {
		currentY += writeNumber(y - currentY);
	}

	// -------------------- start/end --------------------

	public void startChar(double sidebearing, double width) throws IOException {
		currentX = writeNumber(sidebearing);
		writeNumber(width);
		writeCommand(13);
	}

	public void endchar() throws IOException {
		writeCommand(14);
	}

	// -------------------- path construction --------------------

	private int to(double x, double y) throws IOException {
		// writePoint(x, y);
		// return BOTH;

		int rx = (int) Math.round(x);
		int ry = (int) Math.round(y);

		if (rx == currentX) {
			if (ry == currentY) {
				return LAST_POINT;
			}
			writeY(y);
			return VERTICAL;
		} else if (ry == currentY) {
			writeX(x);
			return HORIZONTAL;
		} else {
			writePoint(x, y);
			return BOTH;
		}
	}

	@Override
	public void move(double x, double y) throws IOException {
		switch (to(x, y)) {
		case BOTH:
			writeCommand(21);
			break;
		case HORIZONTAL:
			writeCommand(22);
			break;
		case VERTICAL:
			writeCommand(4);
			break;
		case LAST_POINT:
			break;
		}
		super.move(x, y);
	}

	@Override
	public void line(double x, double y) throws IOException {
		switch (to(x, y)) {
		case BOTH:
			writeCommand(5);
			break;
		case HORIZONTAL:
			writeCommand(6);
			break;
		case VERTICAL:
			writeCommand(7);
			break;
		case LAST_POINT:
			break;
		}
		super.line(x, y);
	}

	@Override
	public void cubic(double x1, double y1, double x2, double y2, double x3,
			double y3) throws IOException {
		writePoint(x1, y1);
		writePoint(x2, y2);
		writePoint(x3, y3);
		writeCommand(8);
		super.cubic(x1, y1, x2, y2, x3, y3);
	}

	@Override
	public void closePath(double x0, double y0) throws IOException {
		writeCommand(9);
		super.closePath(x0, y0);
	}

	public void drawPath(Shape s) throws IOException {
		addPath(s);
	}

}
