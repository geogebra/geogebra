package org.geogebra.common.media;

/**
 * Pack URL errors
 * 
 * @author laszlo
 *
 */
public enum MebisError {
	/** no error */
	NONE,

	/** no Mebis URL at all */
	BASE_MISMATCH,

	/** no "doc" param */
	DOC,

	/** no "id" or "identifier" param */
	ID,

	/** no "type" param when should be */
	TYPE
}