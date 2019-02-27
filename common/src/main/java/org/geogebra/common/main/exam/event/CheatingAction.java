package org.geogebra.common.main.exam.event;

import org.geogebra.common.main.Localization;

public enum CheatingAction {

    WINDOW_ENTERED {
        @Override
        public String toString(Localization loc) {
            return loc.getMenu("exam_log_window_entered");
        }
    },

    WINDOWS_LEFT {
        @Override
        public String toString(Localization loc) {
            return loc.getMenu("exam_log_window_left");
        }
    },

    AIRPLANE_MODE_ON {
        @Override
        public String toString(Localization loc) {
            return loc.getMenu("exam_log_airplane_mode_on");
        }
    },

    AIRPLANE_MODE_OFF {
        @Override
        public String toString(Localization loc) {
            return loc.getMenu("exam_log_airplane_mode_off");
        }
    },

    WIFI_ENABLED {
        @Override
        public String toString(Localization loc) {
            return loc.getMenu("exam_log_wifi_enabled");
        }
    },

    WIFI_DISABLED {
        @Override
        public String toString(Localization loc) {
            return loc.getMenu("exam_log_wifi_disabled");
        }
    },

    TASK_LOCKED {
        @Override
        public String toString(Localization loc) {
            return loc.getMenu("exam_log_pin");
        }
    },

    TASK_UNLOCKED {
        @Override
        public String toString(Localization loc) {
            return loc.getMenu("exam_log_unpin");
        }
    },

    BLUETOOTH_ENABLED {
        @Override
        public String toString(Localization loc) {
            return loc.getMenu("exam_log_bluetooth_enabled");
        }
    },

    BLUETOOTH_DISABLED {
        @Override
        public String toString(Localization loc) {
            return loc.getMenu("exam_log_bluetooth_disabled");
        }
    },

    SCREEN_ON {
        @Override
        public String toString(Localization loc) {
            return loc.getMenu("exam_log_screen_on");
        }
    },

    SCREEN_OFF {
        @Override
        public String toString(Localization loc) {
            return loc.getMenu("exam_log_screen_off");
        }
    };

    public abstract String toString(Localization loc);

}
