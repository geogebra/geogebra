package org.geogebra.common.kernel;

import javax.annotation.CheckForNull;

public final class EquationForm {

	public enum Linear {
		/** implicit equation a x + b y = c */
		IMPLICIT(Linear.CONST_IMPLICIT),
		/** explicit equation y = m x + b */
		EXPLICIT(Linear.CONST_EXPLICIT),
		/** parametric equation */
		PARAMETRIC(Linear.CONST_PARAMETRIC),
		/** TODO APPS-5867 document equation form */
		IMPLICIT_NON_CANONICAL(Linear.CONST_IMPLICIT_NON_CANONICAL),
		/** general form a x + b y + c = 0 (GGB-1212) */
		GENERAL(Linear.CONST_GENERAL),
		/** user input form */
		USER(Linear.CONST_USER);

		// These constants are provided for use in case statements.
		// (values originally defined in GeoLine).
		public static final int CONST_IMPLICIT = 0;
		public static final int CONST_EXPLICIT = 1;
		public static final int CONST_PARAMETRIC = 2;
		public static final int CONST_IMPLICIT_NON_CANONICAL = 3;
		public static final int CONST_GENERAL = 4;
		public static final int CONST_USER = 5;

		@CheckForNull
		public static Linear valueOf(int rawValue) {
			switch (rawValue) {
			case CONST_IMPLICIT:
				return IMPLICIT;
			case CONST_EXPLICIT:
				return EXPLICIT;
			case CONST_PARAMETRIC:
				return PARAMETRIC;
			case CONST_IMPLICIT_NON_CANONICAL:
				return IMPLICIT_NON_CANONICAL;
			case CONST_GENERAL:
				return GENERAL;
			case CONST_USER:
				return USER;
			default:
				return null;
			}
		}

		public final int rawValue;

		private Linear(int rawValue) {
			this.rawValue = rawValue;
		}
	}

	public enum Quadric {
		/** ax^2+bxy+cy^2+dx+ey+f=0 */
		IMPLICIT(Quadric.CONST_IMPLICIT),
		/** y=ax^2+bx+c */
		EXPLICIT(Quadric.CONST_EXPLICIT),
		/** (x-m)^2/a^2+(y-n)^2/b^2=1 */
		SPECIFIC(Quadric.CONST_SPECIFIC),
		/** X=(1,1)+(sin(t),cos(t)) */
		PARAMETRIC(Quadric.CONST_PARAMETRIC),
		/** user input form */
		USER(Quadric.CONST_USER),
		/** vertex form */
		VERTEX(Quadric.CONST_VERTEX),
		/** conic form */
		CONICFORM(Quadric.CONST_CONICFORM);

		// These constants are provided for use in case statements.
		// (values originally defined in GeoConicND).

		public static final int CONST_IMPLICIT = 0;
		public static final int CONST_EXPLICIT = 1;
		public static final int CONST_SPECIFIC = 2;
		public static final int CONST_PARAMETRIC = 3;
		public static final int CONST_USER = 4;
		public static final int CONST_VERTEX = 5;
		public static final int CONST_CONICFORM = 6;

		@CheckForNull
		public static Quadric valueOf(int rawValue) {
			switch (rawValue) {
			case CONST_IMPLICIT:
				return IMPLICIT;
			case CONST_EXPLICIT:
				return EXPLICIT;
			case CONST_SPECIFIC:
				return SPECIFIC;
			case CONST_PARAMETRIC:
				return PARAMETRIC;
			case CONST_USER:
				return USER;
			case CONST_VERTEX:
				return VERTEX;
			case CONST_CONICFORM:
				return CONICFORM;
			default:
				return null;
			}
		}

		public final int rawValue;

		private Quadric(int rawValue) {
			this.rawValue = rawValue;
		}
	}

	public enum Other {
		USER
	}
}
