package org.geogebra.common.util.lang.subtags;

import java.util.Collections;
import java.util.List;

/** Record from the Language Subtag Registry */
public class Record {

	public final List<Field> fields;

	/**
	 * Constructs a record.
	 * @param fields a list of fields
	 */
	public Record(List<Field> fields) {
		this.fields = Collections.unmodifiableList(fields);
	}
}
