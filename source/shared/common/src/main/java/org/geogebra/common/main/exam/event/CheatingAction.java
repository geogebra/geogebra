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

import org.geogebra.common.main.Localization;

/**
 * Cheating Action
 */
public enum CheatingAction {

    WINDOW_ENTERED {
        @Override
        public String toString(Localization localization) {
            return localization.getMenu("exam_log_window_entered");
        }
    },

    WINDOW_LEFT {
        @Override
        public String toString(Localization localization) {
            return localization.getMenu("exam_log_window_left");
        }
    },

    AIRPLANE_MODE_ON {
        @Override
        public String toString(Localization localization) {
            return localization.getMenu("exam_log_airplane_mode_on");
        }
    },

    AIRPLANE_MODE_OFF {
        @Override
        public String toString(Localization localization) {
            return localization.getMenu("exam_log_airplane_mode_off");
        }
    },

    WIFI_ENABLED {
        @Override
        public String toString(Localization localization) {
            return localization.getMenu("exam_log_wifi_enabled");
        }
    },

    WIFI_DISABLED {
        @Override
        public String toString(Localization localization) {
            return localization.getMenu("exam_log_wifi_disabled");
        }
    },

    TASK_LOCKED {
        @Override
        public String toString(Localization localization) {
            return localization.getMenu("exam_log_pin");
        }
    },

    TASK_UNLOCKED {
        @Override
        public String toString(Localization localization) {
            return localization.getMenu("exam_log_unpin");
        }
    },

    BLUETOOTH_ENABLED {
        @Override
        public String toString(Localization localization) {
            return localization.getMenu("exam_log_bluetooth_enabled");
        }
    },

    BLUETOOTH_DISABLED {
        @Override
        public String toString(Localization localization) {
            return localization.getMenu("exam_log_bluetooth_disabled");
        }
    },

    SCREEN_ON {
        @Override
        public String toString(Localization localization) {
            return localization.getMenu("exam_log_screen_on");
        }
    },

    SCREEN_OFF {
        @Override
        public String toString(Localization localization) {
            return localization.getMenu("exam_log_screen_off");
        }
    };

	/**
	 * @param localization localization
	 * @return The localized name of the action.
	 */
	public abstract String toString(Localization localization);

}
