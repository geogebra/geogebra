/**
 * 
 */
package org.apache.commons.collections15;

/**
 * Thrown by the nextElement method of an Enumeration to indicate that there are
 * no more elements in the enumeration.
 * 
 * @author dave.trudes
 * 
 */
public class NoSuchElementException extends RuntimeException {

	private static final long serialVersionUID = 2008212869190964200L;

	/**
	 * Constructs a <code>NoSuchElementException</code> with <code>null</code>
	 * as its error message string.
	 */
	public NoSuchElementException() {
		super();
	}

	/**
	 * Constructs a <code>NoSuchElementException</code>, saving a reference to
	 * the error message string s for later retrieval by the
	 * <code>getMessage</code> method.
	 * 
	 * @param s
	 */
	public NoSuchElementException(String s) {
		super(s);
	}
}
