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

package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.factories.UtilFactory;

/**
 * class for handling rotation speed
 */
public class RotationSpeedHandler {

	/** number of samples for calculating the speed */
	static final private int SAMPLES = 5;
	/** max delay for a valid rotation occurrence */
	static final private double MAX_DELAY = 100;

	private double timeOld;
	private double xOld;
	private double lastDelay;
	private int index;

	private double[] speeds;

	private PointerEventType pointerEventType;

	/**
	 * constructor
	 */
	public RotationSpeedHandler() {
		speeds = new double[SAMPLES];
	}

	/**
	 * start gesture for rotation
	 * 
	 * @param x
	 *            origin value
	 */
	public void setStart(double x, PointerEventType pointerEventType) {
		timeOld = UtilFactory.getPrototype().getMillisecondTime();
		lastDelay = Double.POSITIVE_INFINITY;
		xOld = x;
		index = 0;
		this.pointerEventType = pointerEventType;
	}

	/**
	 * rotation occurred
	 * 
	 * @param x
	 *            new value
	 */
	public void rotationOccurred(double x) {
		double time = UtilFactory.getPrototype().getMillisecondTime();
		lastDelay = time - timeOld;
		timeOld = time;
		if (lastDelay > MAX_DELAY) {
			index = 0;
		} else {
			double dx = x - xOld;
			speeds[index % SAMPLES] = dx / lastDelay;
			index++;
		}
		xOld = x;
	}

	/**
	 * 
	 * @return delay between last two recorded values
	 */
	public double getLastDelay() {
		return lastDelay;
	}

	/**
	 * 
	 * @return current recorded speed
	 */
	public double getSpeed() {
		if (index < SAMPLES) {
			return 0;
		}
		double sum = 0;
		for (double s : speeds) {
			sum += s;
		}
		return sum / SAMPLES;
	}

    /**
     *
     * @return pointer event type for the current (last) rotation
     */
    public PointerEventType getPointerEventType() {
        return pointerEventType;
    }
}
