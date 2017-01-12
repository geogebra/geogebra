// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * Chord TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: Chord.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class Chord extends EMFTag {

	private Rectangle bounds;

	private Point start, end;

	public Chord() {
		super(46, 1);
	}

	public Chord(Rectangle bounds, Point start, Point end) {
		this();
		this.bounds = bounds;
		this.start = start;
		this.end = end;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		Chord tag = new Chord(emf.readRECTL(), emf.readPOINTL(),
				emf.readPOINTL());
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeRECTL(bounds);
		emf.writePOINTL(start);
		emf.writePOINTL(end);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  bounds: " + bounds + "\n"
				+ "  start: " + start + "\n" + "  end: " + end;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public Point getStart() {
		return start;
	}

	public Point getEnd() {
		return end;
	}
}
