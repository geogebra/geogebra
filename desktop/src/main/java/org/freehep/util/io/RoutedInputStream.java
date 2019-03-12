// Copyright 2002, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.geogebra.common.util.Charsets;

/**
 * The RoutedInputStream allows the user to add a listener for a certain
 * delimited portion of the main inputstream. This portion is marked by a start
 * and end marker. The end marker can be null, in which case the portion runs
 * from the start marker to the end of the main inputstream. The listener is
 * informed via a route (partial inputstream) when input is available. The new
 * routed inputstream (route) is supposed to be read to the end or closed, after
 * which the main inputstream should be read again. Closing a route will not
 * close the original stream, but will discard any bytes up to and including the
 * end marker. Returning from a route without reading until the end of the route
 * means that the remaining bytes are still available on the main stream.
 * Multiple routes can be added, as long as they have different start sequences.
 * Start sequences such as "StartA, StartB, StartEmpty" are allowed, but "Start,
 * StartOther" are not since they overlap. Start and End markers can be the
 * same.
 * 
 * IMPORTANT: inherits from InputStream rather than FilterInputStream so that
 * the correct read(byte[], int, int) method is used.
 * 
 * @author Mark Donszelmann
 * @version $Id: RoutedInputStream.java,v 1.3 2008-05-04 12:21:54 murkle Exp $
 */
public class RoutedInputStream extends InputStream {

	private InputStream in;

	private Map routes, listeners;

	private byte[] buffer;

	private int sob, eob;

	private int index;

	private int state;

	private byte[] start;

	private static final int UNROUTED = 0; // the main stream is returned

	private static final int ROUTEFOUND = 1; // found a route, but need to
												// still return buffer

	private static final int ROUTEINFORM = 2; // buffer returned, need to
												// inform routelistener

	private static final int ROUTED = 3; // the main stream is now routed to
											// the route

	private static final int CLOSING = 4; // the underlying stream is closed,
											// but buffer need to be emptied

	private static final int CLOSED = 5; // the main stream is closed

	/**
	 * Creates a RoutedInputStream from the underlying stream.
	 * 
	 * @param input
	 *            stream to read
	 */
	public RoutedInputStream(InputStream input) {
		super();
		in = input;
		routes = new HashMap();
		listeners = new HashMap();
		// bufferlength has to be more than 1
		buffer = new byte[20];
		sob = -1;
		eob = 0;
		index = 0;
		state = UNROUTED;
	}

	/**
	 * Returns the next byte on this stream, however if a start marker is found,
	 * the associated route listener is called, which should take over reading
	 * the stream. In this case this method will, after the route is finished
	 * reading and closed, return the first byte after the end marker. This of
	 * course unless that byte is part of the next start marker.
	 */
	@Override
	public int read() throws IOException {

		int result;

		NEWSTATE: while (true) {
			switch (state) {
			default:
			case UNROUTED:
				// fill the buffer with one or more bytes
				int b = -1;
				while (sob != eob) {
					if (sob < 0) {
						sob = 0;
					}

					// read a byte from the underlying stream
					b = in.read();
					if (b < 0) {
						// underlying stream closed
						state = CLOSING;
						continue NEWSTATE;
					}

					// try to find a start marker
					buffer[eob] = (byte) b;
					eob = (eob + 1) % buffer.length;

					// search for a route
					for (Iterator i = routes.keySet().iterator(); i
							.hasNext();) {
						start = (byte[]) i.next();
						index = (eob + buffer.length - start.length)
								% buffer.length;
						if (equals(start, buffer, index)) {
							state = ROUTEFOUND;
							continue NEWSTATE;
						}
					}

				} // while

				// always return what drops from the buffer
				// the buffer is one byte longer than the longest start marker,
				// so even
				// if we find that marker we can still return the byte just in
				// front of it.
				result = buffer[sob];
				sob = (sob + 1) % buffer.length;
				return result;

			case ROUTEFOUND:
				// found a start marker, we still need to return all bytes
				// before
				// the marker; i.e. from sob to index
				if (sob == index) {
					state = ROUTEINFORM;
					continue NEWSTATE;
				}
				result = buffer[sob];
				sob = (sob + 1) % buffer.length;
				return result;

			case ROUTEINFORM:
				// we inform the routelistener to start reading
				state = ROUTED;
				Route route = new Route(start, (byte[]) routes.get(start));
				// next call will generate callbacks to this read method in
				// state ROUTED.
				((RouteListener) listeners.get(start)).routeFound(route);

				// route listener finished
				state = UNROUTED;
				if (sob == eob) {
					// we restart buffering if the buffer was empty
					sob = -1;
					eob = 0;
					continue NEWSTATE;
				}
				// FIXME: we need an UNROUTING here which just returns the
				// buffer, but does not refill it, in case the reads would
				// block...
				// we return a byte from the buffer and
				// let the next call take care of rebuffering, otherwise we may
				// block
				result = buffer[sob];
				sob = (sob + 1) % buffer.length;
				return result;

			case ROUTED:
				// calls end up here when the Route is reading. We should
				// return the start marker (in buffer) followed by newly read
				// bytes.
				if (sob == eob) {
					result = in.read();
					if (result < 0) {
						state = CLOSED;
						continue NEWSTATE;
					}
				} else {
					result = buffer[sob];
					sob = (sob + 1) % buffer.length;
				}
				return result;

			case CLOSING:
				// the underlying stream is closed, no more markers can be found
				// thus the rest of the buffer is returned
				if (sob == eob) {
					state = CLOSED;
					continue NEWSTATE;
				}
				result = buffer[sob];
				sob = (sob + 1) % buffer.length;
				return result;

			case CLOSED:
				// all streams are closed
				return -1;
			} // switch
		} // while
	}

	/**
	 * Adds a route for given start and end string. The strings are converted
	 * according to the default encoding to start and end markers (byte[]).
	 * 
	 * @param start
	 *            start marker
	 * @param end
	 *            end marker
	 * @param listener
	 *            listener to inform about the route
	 */
	public void addRoute(String start, String end, RouteListener listener) {
		addRoute(start.getBytes(Charsets.getUtf8()),
				(end == null) ? null : end.getBytes(Charsets.getUtf8()),
				listener);
	}

	/**
	 * Adds a route for given start and end marker.
	 * 
	 * If the end marker is null, the route is indefinite, and can be read until
	 * the main stream ends.
	 * 
	 * If the start and end marker are equal, the route can be read for exactly
	 * their length.
	 * 
	 * @param start
	 *            start marker
	 * @param end
	 *            end marker
	 * @param listener
	 *            listener to inform about the route
	 */
	public void addRoute(byte[] start, byte[] end, RouteListener listener) {
		for (Iterator i = routes.keySet().iterator(); i.hasNext();) {
			String key = new String((byte[]) i.next(), Charsets.getUtf8());
			String name = new String(start, Charsets.getUtf8());
			if (key.startsWith(name) || name.startsWith(key)) {
				throw new IllegalArgumentException("Route '" + name
						+ "' cannot be added since it overlaps with '" + key
						+ "'.");
			}
		}

		routes.put(start, end);
		listeners.put(start, listener);
		// we make the buffer one longer than the longest start marker, so that
		// read() can always return a byte before a marker.
		if (start.length > buffer.length - 1) {
			byte[] tmp = new byte[start.length + 1];
			System.arraycopy(buffer, 0, tmp, 0, buffer.length);
			buffer = tmp;
		}
	}

	/**
	 * Checks if cmp is equal to buf (with start at index) for the length of
	 * cmp.
	 * 
	 * @param cmp
	 *            buffer1 to compare
	 * @param buf
	 *            buffer2 to compare
	 * @param index
	 *            start index to start compare
	 * @return true if cmp == buf for length of cmp.
	 */
	private static boolean equals(byte[] cmp, byte[] buf, int index) {
		for (int i = cmp.length - 1; i > 0; i--) {
			int j = (index + buf.length + i) % buf.length;
			if (buf[j] != cmp[i]) {
				return false;
			}
		}

		return buf[(index + buf.length) % buf.length] == cmp[0];
	}

	/**
	 * Route which can be read up to and including the end marker.
	 * 
	 * When you close the route, all bytes including the end marker will be
	 * read/discarded before returning.
	 * 
	 * If you just discard the Route, the underlying stream will still return
	 * you all or part of the bytes of this route.
	 * 
	 * If the end marker is set to null, the stream can be read until the
	 * underlying stream ends.
	 */
	public class Route extends InputStream {

		private byte[] start, end;

		private byte[] buffer;

		private int index;

		private boolean closed;

		/**
		 * Creates a route with given start and end marker.
		 * 
		 * @param start
		 *            start marker
		 * @param end
		 *            end marker
		 */
		public Route(byte[] start, byte[] end) {
			this.start = start;
			this.end = end;
			if (end != null) {
				buffer = new byte[end.length];
			}
			index = 0;
			closed = false;
		}

		/**
		 * Returns bytes of this specific route, starting with the start marker,
		 * followed by any bytes up to and including the end marker.
		 * 
		 * If the end marker is null, the route is indefinite.
		 */
		@Override
		public int read() throws IOException {
			if (closed) {
				return -1;
			}

			int b = RoutedInputStream.this.read();
			if (b < 0) {
				closed = true;
				return b;
			}

			if (end == null) {
				return b;
			}

			buffer[index] = (byte) b;
			index = (index + 1) % buffer.length;

			closed = RoutedInputStream.equals(end, buffer, index);

			return b;
		}

		/**
		 * Closes the stream, and discards any bytes up to and including the end
		 * marker.
		 */
		@Override
		public void close() throws IOException {
			while (read() >= 0) {
			}
			;
			closed = true;
		}

		/**
		 * Returns start marker.
		 * 
		 * @return start marker
		 */
		public byte[] getStart() {
			return start;
		}

		/**
		 * Returns end marker.
		 * 
		 * @return end marker
		 */
		public byte[] getEnd() {
			return end;
		}
	}
}
