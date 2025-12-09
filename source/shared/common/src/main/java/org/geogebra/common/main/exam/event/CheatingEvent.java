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

package org.geogebra.common.main.exam.event;

import java.util.Date;

/**
 * This class represents a cheating event.
 * A cheating event consists of a cheating action and the time of this action.
 */
public final class CheatingEvent {

    private final CheatingAction action;
    private final Date date;

    /**
     * @param action action
     * @param date time
     */
    CheatingEvent(CheatingAction action, Date date) {
        this.action = action;
        this.date = date;
    }

    public CheatingAction getAction() {
        return action;
    }

    public long getTime() {
        return date.getTime();
    }

    public Date getDate() {
        return date;
    }
}
