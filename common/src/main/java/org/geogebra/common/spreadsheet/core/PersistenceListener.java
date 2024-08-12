package org.geogebra.common.spreadsheet.core;

// TODO find a better name
public interface PersistenceListener {
	// TODO document what implementers are supposed to do here
	void persist(SpreadsheetDimensions spreadsheetSettings);
}
