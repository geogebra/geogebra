package org.geogebra.common;

import static org.geogebra.common.GeoGebraConstants.*;

public enum SuiteSubApp {
	GRAPHING() {
		@Override
		public String getAppCode() {
			return GRAPHING_APPCODE;
		}
	},
	GEOMETRY() {
		@Override
		public String getAppCode() {
			return GEOMETRY_APPCODE;
		}
	},
	G3D() {
		@Override
		public String getAppCode() {
			return G3D_APPCODE;
		}
	},
	CAS() {
		@Override
		public String getAppCode() {
			return CAS_APPCODE;
		}
	},
	PROBABILITY() {
		@Override
		public String getAppCode() {
			return PROBABILITY_APPCODE;
		}
	},
	SCIENTIFIC() {
		@Override
		public String getAppCode() {
			return SCIENTIFIC_APPCODE;
		}
	};

	public abstract String getAppCode();
}
