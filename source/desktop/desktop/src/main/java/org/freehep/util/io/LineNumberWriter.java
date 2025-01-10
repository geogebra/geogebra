// Copyright 2003, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;
import java.io.Writer;
import java.util.EventListener;
import java.util.EventObject;
import java.util.TooManyListenersException;

/**
 * Counts line numbers, based on the first cr-lf, cr or lf it finds. Informs a
 * listener when the linenumber exceeds a threshold.
 * 
 * Listeners can only be informed from the second line only.
 * 
 * @author Mark Donszelmann
 * @version $Id: LineNumberWriter.java,v 1.3 2008-05-04 12:21:42 murkle Exp $
 */
public class LineNumberWriter extends Writer {

	private final static int UNKNOWN = 0;

	private final static int CR = 1;

	private final static int CRLF = 2;

	private final static int LF = 3;

	private final static int LFCR = 4;

	private int lineSeparator = UNKNOWN;

	private Writer out;

	private int lineNo = 0;

	private LineNumberListener listener;

	private int lineNoLimit;

	private int previous = -1;

	/**
	 * Creates a Line Number Writer
	 * 
	 * @param out
	 *            writer to write to
	 */
	public LineNumberWriter(Writer out) {
		this.out = out;
	}

	@Override
	public void write(char cbuf[]) throws IOException {
		write(cbuf, 0, cbuf.length);
	}

	@Override
	public void write(char cbuf[], int off, int len) throws IOException {
		for (int i = 0; i < len; i++) {
			write(cbuf[off + i]);
		}
	}

	@Override
	public void write(String str) throws IOException {
		write(str, 0, str.length());
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		for (int i = 0; i < len; i++) {
			write(str.charAt(off + i));
		}
	}

	@Override
	public void write(int c) throws IOException {
		boolean newLine = false;
		synchronized (lock) {
			out.write(c);

			switch (lineSeparator) {
			default:
			case UNKNOWN:
				switch (previous) {
				case '\r':
					lineNo++;
					lineSeparator = (c == '\n') ? CRLF : CR;
					if (c == '\r') {
						lineNo++;
					}
					break;
				case '\n':
					lineNo++;
					lineSeparator = (c == '\r') ? LFCR : LF;
					if (c == '\n') {
						lineNo++;
					}
					break;
				default:
					break;
				}
				break;
			case CR:
				if (c == '\r') {
					lineNo++;
					newLine = true;
				}
				break;
			case CRLF:
				if ((previous == '\r') && (c == '\n')) {
					lineNo++;
					newLine = true;
				}
				break;
			case LF:
				if (c == '\n') {
					lineNo++;
					newLine = true;
				}
				break;
			case LFCR:
				if ((previous == '\n') && (c == '\r')) {
					lineNo++;
					newLine = true;
				}
				break;
			}
			previous = c;
		}

		if ((listener != null) && newLine && (lineNo >= lineNoLimit)) {
			listener.lineNumberReached(new LineNumberEvent(this, lineNo));
		}
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	/**
	 * Returns the line number that is currently being written.
	 * 
	 * @return current line number
	 */
	public int getLineNumber() {
		return lineNo;
	}

	/**
	 * Set the current line number
	 * 
	 * @param lineNo
	 *            new line number
	 */
	public void setLineNumber(int lineNo) {
		this.lineNo = lineNo;
	}

	/**
	 * Add a LineNumberListener
	 * 
	 * @param listener
	 *            new listener
	 * @param lineNoLimit
	 *            line number for which to generate a LineNumberEvent
	 * @throws TooManyListenersException
	 *             if there is more than one listener
	 */
	public void addLineNumberListener(LineNumberListener listener,
			int lineNoLimit) throws TooManyListenersException {
		if (this.listener != null) {
			throw new TooManyListenersException();
		}
		if (lineNoLimit < 2) {
			throw new IllegalArgumentException(
					"LineNoLimit cannot be less than 2");
		}

		this.listener = listener;
		this.lineNoLimit = lineNoLimit;
	}

	/**
	 * LineNumberListener interface can inform a listener about changes in the
	 * line number, or when a linenumber limit has been reached.
	 * 
	 * @author duns
	 * @version $Id: LineNumberWriter.java,v 1.3 2008-05-04 12:21:42 murkle Exp
	 *          $
	 */
	public static interface LineNumberListener extends EventListener {

		/**
		 * Called when the line number limit has been reached.
		 * 
		 * @param event
		 *            line number event
		 */
		public void lineNumberReached(LineNumberEvent event);
	}

	/**
	 * Event to be used by the LineNumberListener interface.
	 * 
	 * @author duns
	 * @version $Id: LineNumberWriter.java,v 1.3 2008-05-04 12:21:42 murkle Exp
	 *          $
	 */
	public static class LineNumberEvent extends EventObject {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2821724279014031198L;

		private int lineNo;

		/**
		 * Create a LineNumberEvent
		 * 
		 * @param source
		 *            event source
		 * @param lineNo
		 *            current line number
		 */
		public LineNumberEvent(Object source, int lineNo) {
			super(source);
			this.lineNo = lineNo;
		}

		/**
		 * @return current line number
		 */
		public int getLineNumber() {
			return lineNo;
		}

		@Override
		public String toString() {
			return "LineNumberEvent: line=" + lineNo + "; "
					+ getSource().toString();
		}
	}
}
