/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package edu.uci.ics.jung.graph.event;

import java.util.EventListener;

/**
 * An interface for classes that listen for graph events.
 */
public interface GraphEventListener<V, E> extends EventListener {
	/**
	 * Method called by the process generating a graph event to which this
	 * instance is listening. The implementor of this interface is responsible
	 * for deciding what behavior is appropriate.
	 */
	void handleGraphEvent(GraphEvent<V, E> evt);
}
