package org.geogebra.common.kernel.barycentric;

/**
 * The heavyweight part of AlgoCubic Based on data from Bernard Gibert
 * 
 * @author Arpad Fekete
 * @author Zbynek Konecny
 *
 */
public class AlgoCubicSwitch {

	private final static double r3 = Math.sqrt(3);

	private double a2, a3, a4, a5, a6, a7, a8, a9, a10, a12, a14, a16, a18, a20;
	private double b2, b3, b4, b5, b6, b7, b8, b9, b10, b12, b14, b16, b18, b20;
	private double c2, c3, c4, c5, c6, c7, c8, c9, c10, c12, c14, c16, c18, c20;
	private double S, cA3, cB3, cC3;

	/**
	 * Returns equation in case cubic is symmetric or antisymmetric
	 */
	public String getEquation(int n, double a, double b, double c) {
		StringBuilder equation = new StringBuilder("0=0");
		for (int p = 0; p < 3; p++) {
			int q = (p + 1) % 3;
			int r = (q + 1) % 3;
			double[] sides = { a, b, c };
			double[] coeff = getCoeff(n, sides[p], sides[q], sides[r]);
			if (coeff.length < 4) {
				return null;
			}
			double[] coeffR = getCoeff(n, sides[q], sides[p], sides[r]);
			char A = "ABC".charAt(p), B = "ABC".charAt(q);
			equation.append("+(" + coeff[0] + ")*" + A + "^3");
			equation.append("+(" + coeff[1] + ")*" + A + "^2*" + B);
			equation.append("+(" + coeffR[2] + ")*" + B + "^2*" + A);
			if (p == 0) {
				equation.append("+(" + coeff[3] + ")*A*B*C");
			}
		}
		return equation.toString();
	}

	private static double[] a(double p, double q, double r) {
		return new double[] { p, q, -q, r };
	}

	private static double[] s(double p, double q, double r) {
		return new double[] { p, q, q, r };
	}

	private double[] getCoeff(int n, double a, double b, double c) {
		// x
		b2 = b * b;
		b3 = b * b2;
		b4 = b2 * b2;
		b5 = b2 * b3;
		b6 = b3 * b3;
		b7 = b3 * b4;
		b8 = b4 * b4;
		b9 = b4 * b5;
		b10 = b6 * b4;
		a2 = a * a;
		a3 = a * a2;
		a4 = a2 * a2;
		a5 = a2 * a3;
		a6 = a3 * a3;
		a7 = a3 * a4;
		a8 = a4 * a4;
		a9 = a4 * a5;
		a10 = a6 * a4;
		c2 = c * c;
		c3 = c * c2;
		c4 = c2 * c2;
		c5 = c2 * c3;
		c6 = c3 * c3;
		c7 = c3 * c4;
		c8 = c4 * c4;
		c9 = c4 * c5;
		c10 = c6 * c4;

		a12 = a6 * a6;
		a14 = a6 * a8;
		a16 = a8 * a8;
		a18 = a10 * a8;
		a20 = a10 * a10;
		b12 = b6 * b6;
		b14 = b6 * b8;
		b16 = b8 * b8;
		b18 = b10 * b8;
		b20 = b10 * b10;
		c12 = c6 * c6;
		c14 = c6 * c8;
		c16 = c8 * c8;
		c18 = c10 * c8;
		c20 = c10 * c10;
		S = Math.sqrt((a + b - c) * (a - b + c) * (-a + b + c) * (a + b + c));

		cA3 = Math.cos(Math.acos((b2 + c2 - a2) / 2 / b / c) / 3);
		cB3 = Math.cos(Math.acos((a2 + c2 - b2) / 2 / a / c) / 3);
		cC3 = Math.cos(Math.acos((b2 + a2 - c2) / 2 / a / b) / 3);

		int absN = Math.abs(n);

		if (absN < 100) {
			return getCoeff1to99(n, a, b, c);
		}
		if (absN < 200) {
			return getCoeff100to199(n, a, b, c);
		}
		if (absN < 300) {
			return getCoeff200to299(n, a, b, c);
		}
		if (absN < 400) {
			return getCoeff300to399(n, a, b, c);
		}
		if (absN < 500) {
			return getCoeff400to499(n, a, b, c);
		}
		if (absN < 550) {
			return getCoeff500to549(n, a, b, c);
		}
		if (absN < 600) {
			return getCoeff550to599(n, a, b, c);
		}

		return getCoeff600plus(n, a, b, c);
	}

	private double[] getCoeff1to99(int n, double a, double b, double c) {
		switch (n) {

		case 1:
			return a(0, a4 * c2 - 2 * a2 * c4 + a2 * c2 * b2 + c6 + c4 * b2
					- 2 * c2 * b4, 0);
		case 2:
			return a(0, c2, 0);
		case 3:
			return a(0, a2 * b2 * c2 - b4 * c2 + b2 * c4, 0);
		case 4:
			return a(0, a4 * c2 - 2 * a2 * c4 + 2 * a2 * c2 * b2 + c6
					+ 2 * c4 * b2 - 3 * c2 * b4, 0);
		case 5:
			return a(0, a4 * c2 - 2 * a2 * c4 - a2 * c2 * b2 + c6 - c4 * b2, 0);
		case 6:
			return a(0, -a4 * c2 + 2 * a2 * c4 - c6 + c2 * b4, 0);
		case 7:
			return a(0, a2 - b2 + c2, 0);
		case 8:
			return a(0, a4 - a2 * c2 + c4 - b4, 0);
		case 9:
			return a(0, -a2 * c4 + c6 - c4 * b2, -2 * a4 * c2 + 2 * a4 * b2
					+ 2 * a2 * c4 - 2 * a2 * b4 - 2 * c4 * b2 + 2 * c2 * b4);
		case 10:
			return s(0, a2 - b2 + c2, -2 * a2 - 2 * b2 - 2 * c2);
		case 11:
			return s(0,
					a6 - a4 * b2 - a4 * c2 - a2 * b4 + 2 * a2 * b2 * c2
							- a2 * c4 + b6 - b4 * c2 - b2 * c4 + c6,
					2 * a6 - 2 * a4 * b2 - 2 * a4 * c2 - 2 * a2 * b4
							+ 12 * a2 * b2 * c2 - 2 * a2 * c4 + 2 * b6
							- 2 * b4 * c2 - 2 * b2 * c4 + 2 * c6);
		case 12:
			return s(0, a2 * b2 * c2,
					-a4 * b2 - a4 * c2 - a2 * b4 - a2 * c4 - b4 * c2 - b2 * c4);
		case 13:
			return s(0,
					a3 - a2 * b - a2 * c - a * b2 + 2 * a * b * c - a * c2 + b3
							- b2 * c - b * c2 + c3,
					2 * a3 - 2 * a2 * b - 2 * a2 * c - 2 * a * b2
							+ 12 * a * b * c - 2 * a * c2 + 2 * b3 - 2 * b2 * c
							- 2 * b * c2 + 2 * c3);
		case 14:
			return s(0, a * b * c,
					-a2 * b - a2 * c - a * b2 - a * c2 - b2 * c - b * c2);
		case 15:
			return s(0, 1, -6);
		case 16:
			return s(0, 1, 0);
		case 17:
			return s(0, b4 * c2 - c4 * a2, 0);
		case 18:
			return a(0, -a2 * c2 + c4, 0);
		case 19:
			return a(0, -a4 * b2 * c2 + a2 * b4 * c2 - b4 * c4 + b2 * c6, 0);
		case 20:
			return a(0, b4 * c2 + c4 * a2, 0);
		case 21:
			return s(0, -a2 * b2 * c2 + b2 * c4, 0);
		case 22:
			return s(0, a4 * c2 - a2 * c2 * b2 - c6 + 3 * c4 * b2 - 2 * c2 * b4,
					2 * a6 - 2 * a4 * c2 - 2 * a4 * b2 - 2 * a2 * c4
							+ 6 * a2 * c2 * b2 - 2 * a2 * b4 + 2 * c6
							- 2 * c4 * b2 - 2 * c2 * b4 + 2 * b6);
		case 23:
			return a(0, -a8 * b2 + 3 * a6 * b4 + a6 * b2 * c2 - a6 * c4
					- 3 * a4 * b6 - 2 * a4 * b4 * c2 - a4 * b2 * c4
					+ 3 * a4 * c6 + a2 * b8 + 2 * a2 * b6 * c2 + a2 * b2 * c6
					- 3 * a2 * c8 - b8 * c2 + b6 * c4 - b4 * c6 + c10, 0);
		case 24:
			return s(0, b2 * c2, 0);
		case 25:
			return a(0,
					a6 - 3 * a4 * b2 + 3 * a2 * b4 + a2 * b2 * c2 - 2 * a2 * c4
							- b6 - b4 * c2 + b2 * c4 + c6,
					-2 * a4 * b2 + 2 * a4 * c2 + 2 * a2 * b4 - 2 * a2 * c4
							- 2 * b4 * c2 + 2 * b2 * c4);
		case 26:
			return a(0,
					-a4 * c2 + a2 * c4 + 2 * a2 * c2 * b2 + c4 * b2 - c2 * b4,
					-2 * a4 * c2 + 2 * a4 * b2 + 2 * a2 * c4 - 2 * a2 * b4
							- 2 * c4 * b2 + 2 * c2 * b4);
		case 27:
			return s(0,
					a6 * c2 - 2 * a4 * c4 - 2 * a4 * c2 * b2 + a2 * c6
							+ 2 * a2 * c4 * b2 + a2 * c2 * b4,
					2 * a6 * c2 + 2 * a6 * b2 - 4 * a4 * c4 - 2 * a4 * c2 * b2
							- 4 * a4 * b4 + 2 * a2 * c6 - 2 * a2 * c4 * b2
							- 2 * a2 * c2 * b4 + 2 * a2 * b6 + 2 * c6 * b2
							- 4 * c4 * b4 + 2 * c2 * b6);
		case 28:
			return a(0, a4 * c2 - a2 * c4 - a2 * c2 * b2,
					2 * a4 * c2 - 2 * a4 * b2 - 2 * a2 * c4 + 2 * a2 * b4
							+ 2 * c4 * b2 - 2 * c2 * b4);
		case 29:
			return a(0, 2 * b * c2
					* Math.cos(Math.acos((a2 + b2 - c2) / (2 * a * b)) / 3)
					* Math.cos(Math.acos((-a2 + b2 + c2) / (2 * b * c)) / 3)
					+ b * c2 * Math.cos(Math.acos((a2 - b2 + c2) / (2 * a * c)) / 3),
					0);
		case 30:
			return a(0, 2 * b * c2
					* Math.cos((2 * Math.PI + Math.acos(
									(a2 + b2 - c2) / (2 * a * b))) / 3)
					* Math.cos((2 * Math.PI + Math.acos(
									(-a2 + b2 + c2) / (2 * b * c))) / 3)
					+ b * c2 * Math.cos((2 * Math.PI
									+ Math.acos((a2 - b2 + c2) / (2 * a * c))) / 3),
					0);
		case 31:
			return a(0, 2 * b * c2
					* Math.cos((-2 * Math.PI + Math.acos(
									(a2 + b2 - c2) / (2 * a * b))) / 3)
					* Math.cos((-2 * Math.PI + Math.acos(
									(-a2 + b2 + c2) / (2 * b * c))) / 3)
					+ b * c2 * Math.cos((-2 * Math.PI
									+ Math.acos((a2 - b2 + c2) / (2 * a * c))) / 3),
					0);
		case 32:
			return a(0,
					a6 - 3 * a4 * b2 + 3 * a4 * c2 + 3 * a2 * b4
							+ 6 * a2 * b2 * c2 - 9 * a2 * c4 - b6 - 9 * b4 * c2
							+ 5 * b2 * c4 + 5 * c6,
					0);
		case 33:
			return a(0, a2 * c + a * c2 + c2 * b - c * b2, 0);
		case 34:
			return a(0, a * c, 0);
		case 35:
			return a(0, -a2 * b2 * c2 + a2 * c4 + b4 * c2 - b2 * c4, 0);
		case 36:
			return s(0, a20 - 5 * a18 * b2 - 3 * a18 * c2 + 13 * a16 * b4
					+ 9 * a16 * b2 * c2 + 6 * a16 * c4 - 27 * a14 * b6
					- 8 * a14 * b4 * c2 - 8 * a14 * b2 * c4 - 13 * a14 * c6
					+ 45 * a12 * b8 + 3 * a12 * b6 * c2 - 18 * a12 * b4 * c4
					+ 22 * a12 * b2 * c6 + 18 * a12 * c8 - 53 * a10 * b10
					- 5 * a10 * b8 * c2 + 40 * a10 * b6 * c4
					+ 15 * a10 * b4 * c6 - 40 * a10 * b2 * c8 - 13 * a10 * c10
					+ 45 * a8 * b12 - 3 * a8 * b10 * c2 - 20 * a8 * b8 * c4
					- 40 * a8 * b6 * c6 + 10 * a8 * b4 * c8 + 30 * a8 * b2 * c10
					+ 6 * a8 * c12 - 33 * a6 * b14 + 30 * a6 * b12 * c2
					- 13 * a6 * b10 * c4 + 10 * a6 * b8 * c6 + 30 * a6 * b6 * c8
					- 20 * a6 * b4 * c10 - 9 * a6 * b2 * c12 - 3 * a6 * c14
					+ 22 * a4 * b16 - 43 * a4 * b14 * c2 + 30 * a4 * b12 * c4
					- 3 * a4 * b10 * c6 - 5 * a4 * b8 * c8 - 10 * a4 * b6 * c10
					+ 6 * a4 * b4 * c12 + 3 * a4 * b2 * c14 + a4 * c16
					- 10 * a2 * b18 + 26 * a2 * b16 * c2 - 25 * a2 * b14 * c4
					+ 20 * a2 * b12 * c6 - 28 * a2 * b10 * c8
					+ 25 * a2 * b8 * c10 - 9 * a2 * b6 * c12 + 3 * a2 * b4 * c14
					- 2 * a2 * b2 * c16 + 2 * b20 - 6 * b18 * c2 + 8 * b16 * c4
					- 11 * b14 * c6 + 15 * b12 * c8 - 12 * b10 * c10
					+ 6 * b8 * c12 - 3 * b6 * c14 + b4 * c16,
					4 * a20 - 16 * a18 * b2 - 16 * a18 * c2 + 30 * a16 * b4
							+ 52 * a16 * b2 * c2 + 30 * a16 * c4 - 44 * a14 * b6
							- 68 * a14 * b4 * c2 - 68 * a14 * b2 * c4
							- 44 * a14 * c6 + 62 * a12 * b8 + 42 * a12 * b6 * c2
							+ 72 * a12 * b4 * c4 + 42 * a12 * b2 * c6
							+ 62 * a12 * c8 - 72 * a10 * b10
							- 10 * a10 * b8 * c2 - 30 * a10 * b6 * c4
							- 30 * a10 * b4 * c6 - 10 * a10 * b2 * c8
							- 72 * a10 * c10 + 62 * a8 * b12
							- 10 * a8 * b10 * c2 - 4 * a8 * b8 * c4
							+ 16 * a8 * b6 * c6 - 4 * a8 * b4 * c8
							- 10 * a8 * b2 * c10 + 62 * a8 * c12 - 44 * a6 * b14
							+ 42 * a6 * b12 * c2 - 30 * a6 * b10 * c4
							+ 16 * a6 * b8 * c6 + 16 * a6 * b6 * c8
							- 30 * a6 * b4 * c10 + 42 * a6 * b2 * c12
							- 44 * a6 * c14 + 30 * a4 * b16 - 68 * a4 * b14 * c2
							+ 72 * a4 * b12 * c4 - 30 * a4 * b10 * c6
							- 4 * a4 * b8 * c8 - 30 * a4 * b6 * c10
							+ 72 * a4 * b4 * c12 - 68 * a4 * b2 * c14
							+ 30 * a4 * c16 - 16 * a2 * b18 + 52 * a2 * b16 * c2
							- 68 * a2 * b14 * c4 + 42 * a2 * b12 * c6
							- 10 * a2 * b10 * c8 - 10 * a2 * b8 * c10
							+ 42 * a2 * b6 * c12 - 68 * a2 * b4 * c14
							+ 52 * a2 * b2 * c16 - 16 * a2 * c18 + 4 * b20
							- 16 * b18 * c2 + 30 * b16 * c4 - 44 * b14 * c6
							+ 62 * b12 * c8 - 72 * b10 * c10 + 62 * b8 * c12
							- 44 * b6 * c14 + 30 * b4 * c16 - 16 * b2 * c18
							+ 4 * c20);
		case 37:
			return a(0, a12 - 3 * a10 * b2 - 3 * a10 * c2 + 9 * a8 * b2 * c2
					+ 3 * a8 * c4 + 10 * a6 * b6 - 12 * a6 * b4 * c2
					- 6 * a6 * b2 * c4 - 2 * a6 * c6 - 15 * a4 * b8
					+ 12 * a4 * b6 * c2 + 6 * a4 * b4 * c4 - 3 * a4 * b2 * c6
					+ 3 * a4 * c8 + 9 * a2 * b10 - 9 * a2 * b8 * c2
					- 3 * a2 * b6 * c4 + 3 * a2 * b4 * c6 + 3 * a2 * b2 * c8
					- 3 * a2 * c10 - 2 * b12 + 3 * b10 * c2 + b6 * c6
					- 3 * b4 * c8 + c12, 0);
		case 38:
			return a(b4 * c2 - b2 * c4,
					-b4 * c2 - b2 * c4 + 2 * b2 * c2 * a2 + 2 * c6
							- 2 * c4 * a2,
					4 * b4 * c2 - 4 * b4 * a2 - 4 * b2 * c4 + 4 * b2 * a4
							+ 4 * c4 * a2 - 4 * c2 * a4);
		case 39:
			return a(0,
					-a6 * b2 * c4 - a4 * b4 * c4 + 2 * a4 * b2 * c6
							+ a2 * b6 * c4 - a2 * b4 * c6 + b8 * c4
							- 3 * b6 * c6 + 3 * b4 * c8 - b2 * c10,
					-2 * a6 * b4 * c2 + 2 * a6 * b2 * c4 + 2 * a4 * b6 * c2
							- 2 * a4 * b2 * c6 - 2 * a2 * b6 * c4
							+ 2 * a2 * b4 * c6);
		case 40:
			return a(0, a2 * b * c2 - a * b2 * c2 + b2 * c3 - b * c4, 0);
		case 41:
			return a(0,
					a6 - 3 * a4 * b2 + 3 * a4 * c2 + 3 * a2 * b4
							- 2 * a2 * b2 * c2 - a2 * c4 - b6 - b4 * c2
							+ 5 * b2 * c4 - 3 * c6,
					-16 * a4 * b2 + 16 * a4 * c2 + 16 * a2 * b4 - 16 * a2 * c4
							- 16 * b4 * c2 + 16 * b2 * c4);
		case 42:
			return a(0, -a10 * c2 + 2 * a8 * c4 + a8 * c2 * b2 + 2 * a6 * c6
					- 5 * a6 * c4 * b2 - 4 * a4 * c8 + 3 * a4 * c6 * b2
					+ 6 * a4 * c4 * b4 - 4 * a4 * c2 * b6 - a2 * c10
					+ 4 * a2 * c8 * b2 - 9 * a2 * c6 * b4 + 5 * a2 * c4 * b6
					+ a2 * c2 * b8 + 2 * c12 - 5 * c10 * b2 + 6 * c8 * b4
					+ 2 * c6 * b6 - 8 * c4 * b8 + 3 * c2 * b10, 0);
		case 43:
			return a(0, a2 * c2 - 2 * c4 + c2 * b2, 0);
		case 44:
			return a(0,
					a10 * c2 - 4 * a8 * c4 - a8 * c2 * b2 + 6 * a6 * c6
							+ 2 * a6 * c4 * b2 - 2 * a6 * c2 * b4 - 4 * a4 * c8
							+ 2 * a4 * c4 * b4 + 2 * a4 * c2 * b6 + a2 * c10
							- 2 * a2 * c8 * b2 + 2 * a2 * c6 * b4
							- 2 * a2 * c4 * b6 + a2 * c2 * b8 + c10 * b2
							- 2 * c8 * b4 + 2 * c4 * b8 - c2 * b10,
					0);
		case 45:
			return a(0, a6 * c2 - 2 * a4 * c4 + a2 * c6 - a2 * c2 * b4, 0);
		case 46:
			return a(0, -S * a4 - 4 * S * a2 * b2 - S * a2 * c2 + 5 * S * b4
					- S * b2 * c2 - 4 * S * c4 + a6 * r3 - 3 * a4 * b2 * r3
					- 2 * a4 * c2 * r3 + 3 * a2 * b4 * r3
					- 6 * a2 * b2 * c2 * r3 + a2 * c4 * r3 - b6 * r3
					+ 8 * b4 * c2 * r3 - 7 * b2 * c4 * r3, 0);
		case -46:
			return a(0, S * a4 + 4 * S * a2 * b2 + S * a2 * c2 - 5 * S * b4
					+ S * b2 * c2 + 4 * S * c4 + a6 * r3 - 3 * a4 * b2 * r3
					- 2 * a4 * c2 * r3 + 3 * a2 * b4 * r3
					- 6 * a2 * b2 * c2 * r3 + a2 * c4 * r3 - b6 * r3
					+ 8 * b4 * c2 * r3 - 7 * b2 * c4 * r3, 0);
		case 47:
			return a(0,
					-3 * a4 * c2 + 2 * a2 * c4 - 2 * a2 * c2 * b2 + c6
							- 6 * c4 * b2 + 5 * c2 * b4,
					-8 * a4 * c2 + 8 * a4 * b2 + 8 * a2 * c4 - 8 * a2 * b4
							- 8 * c4 * b2 + 8 * c2 * b4);
		case 48:
			return a(-a2 * b4 * c2 + a2 * b2 * c4 + b6 * c2 - b2 * c6,
					-a4 * b2 * c2 + a2 * b2 * c4 + b6 * c2 - 3 * b4 * c4
							+ 2 * b2 * c6,
					0);
		case 49:
			return a(0, a6 - 3 * a4 * b2 - 2 * a4 * c2 + 3 * a2 * b4
					+ 2 * a2 * b2 * c2 + a2 * c4 - b6 + b2 * c4, 0);
		case 50:
			return a(0, a10 * c2 - 4 * a8 * c4 - 2 * a8 * c2 * b2 + 6 * a6 * c6
					+ 4 * a6 * c4 * b2 + a6 * c2 * b4 - 4 * a4 * c8
					- a4 * c6 * b2 - a4 * c2 * b6 + a2 * c10 - 2 * a2 * c8 * b2
					+ a2 * c6 * b4 - 2 * a2 * c4 * b6 + 2 * a2 * c2 * b8
					+ c10 * b2 - 2 * c8 * b4 + 2 * c4 * b8 - c2 * b10, 0);
		case 51:
			return s(
					-a8 + 2 * a6 * b2 + 2 * a6 * c2 - 4 * a4 * b2 * c2
							- 2 * a2 * b6 + 2 * a2 * b4 * c2 + 2 * a2 * b2 * c4
							- 2 * a2 * c6
							+ b8 - 2 * b4
									* c4
							+ c8,
					-a8 + 2 * a6 * b2 - 2 * a4 * b2 * c2 + 2 * a4 * c4
							- 2 * a2 * b6
							+ 4 * a2 * b4
									* c2
							- 2 * a2 * b2 * c4 + b8 - 2 * b6 * c2 + 2 * b2 * c6
							- c8,
					-3 * a8 + 4 * a6 * b2 + 4 * a6 * c2 - 2 * a4 * b4
							- 4 * a4 * b2 * c2 - 2 * a4 * c4 + 4 * a2 * b6
							- 4 * a2 * b4 * c2 - 4 * a2 * b2 * c4 + 4 * a2 * c6
							- 3 * b8 + 4 * b6 * c2 - 2 * b4 * c4 + 4 * b2 * c6
							- 3 * c8);
		case 52:
			return s(0,
					a4 * b4 - 2 * a4 * b2 * c2 + a4 * c4 - 2 * a2 * b4 * c2
							+ 4 * a2 * b2 * c4 - 2 * a2 * c6 + b4 * c4
							- 2 * b2 * c6 + c8,
					0);
		case 54:
			return a(0,
					a6 - 3 * a4 * b2 - 3 * a4 * c2 + 3 * a2 * b4
							+ 4 * a2 * b2 * c2 + 2 * a2 * c4 - b6 - b4 * c2
							+ 2 * b2 * c4,
					2 * a4 * b2 - 2 * a4 * c2 - 2 * a2 * b4 + 2 * a2 * c4
							+ 2 * b4 * c2 - 2 * b2 * c4);
		case 55:
			return a(0, -a2 * c4 + c6 + 3 * c4 * b2, 2 * a4 * c2 - 2 * a4 * b2
					- 2 * a2 * c4 + 2 * a2 * b4 + 2 * c4 * b2 - 2 * c2 * b4);
		case 56:
			return a(0, -a6 * b4 + a4 * b6 + a4 * b4 * c2 + 2 * a4 * b2 * c4,
					0);
		case 58:
			return a(0, -a2 * c + c3 - c2 * b + c * b2, 0);
		case 59:
			return a(0,
					a6 - 3 * a4 * b2 + 3 * a2 * b4 + 2 * a2 * b2 * c2
							- 3 * a2 * c4 - b6 - 2 * b4 * c2 + b2 * c4 + 2 * c6,
					0);
		case 60:
			return a(0, -a6 + 3 * a4 * b2 + a4 * c2 - 3 * a2 * b4 - a2 * b2 * c2
					+ a2 * c4 + b6 - c6, 0);
		case 61:
			return a(0, 2 * a6 - 6 * a4 * b2 - a4 * c2 + 6 * a2 * b4
					+ 3 * a2 * b2 * c2 - 4 * a2 * c4 - a2 * c2 * r3 * S - 2 * b6
					- 2 * b4 * c2 + b2 * c4 + 3 * c6 + c4 * r3 * S, 0);
		case -61:
			return a(0, 2 * a6 - 6 * a4 * b2 - a4 * c2 + 6 * a2 * b4
					+ 3 * a2 * b2 * c2 - 4 * a2 * c4 + a2 * c2 * r3 * S - 2 * b6
					- 2 * b4 * c2 + b2 * c4 + 3 * c6 - c4 * r3 * S, 0);
		case 62:
			return a(0, a6 * c2 - 2 * a4 * c4 - 2 * a4 * c2 * b2 + a2 * c6
					+ 2 * a2 * c2 * b4 + 2 * c6 * b2 - c4 * b4 - c2 * b6, 0);
		case 63:
			return a(0,
					2 * a6 * c2 - a4 * c4 - 5 * a4 * c2 * b2 - 2 * a2 * c6
							+ 8 * a2 * c2 * b4 + c8 + 2 * c6 * b2 - 2 * c4 * b4
							- 3 * c2 * b6,
					0);
		case 64:
			return s(0, -a4 + 2 * a2 * b2 + 2 * a2 * c2 - b4 - b2 * c2 - c4, 0);
		case 65:
			return a(0, -a4 + 2 * a2 * c2 - 3 * c4 + c2 * b2 + b4, 0);
		case 66:
			return a(0,
					a6 - 3 * a4 * b2 - 4 * a4 * c2 - a4 * r3 * S + 3 * a2 * b4
							- 2 * a2 * b2 * c2 + 5 * a2 * c4 + a2 * c2 * r3 * S
							- b6 + 6 * b4 * c2 + b4 * r3 * S - 3 * b2 * c4
							+ b2 * c2 * r3 * S - 2 * c6 - 2 * c4 * r3 * S,
					0);
		case -66:
			return a(0,
					a6 - 3 * a4 * b2 - 4 * a4 * c2 + a4 * r3 * S + 3 * a2 * b4
							- 2 * a2 * b2 * c2 + 5 * a2 * c4 - a2 * c2 * r3 * S
							- b6 + 6 * b4 * c2 - b4 * r3 * S - 3 * b2 * c4
							- b2 * c2 * r3 * S - 2 * c6 + 2 * c4 * r3 * S,
					0);
		case 67:
			return a(0, a10 * c2 - 5 * a8 * c4 - 2 * a8 * c2 * b2 + 10 * a6 * c6
					+ 5 * a6 * c4 * b2 - 10 * a4 * c8 - a4 * c4 * b4
					+ 2 * a4 * c2 * b6 + 5 * a2 * c10 - 7 * a2 * c8 * b2
					+ a2 * c6 * b4 + 2 * a2 * c4 * b6 - a2 * c2 * b8 - c12
					+ 4 * c10 * b2 - 6 * c8 * b4 + 4 * c6 * b6 - c4 * b8, 0);
		case 68:
			return a(0, a4 - 3 * a2 * b2 + a2 * c2 + 2 * b4 - b2 * c2, 0);
		case 69:
			return a(0,
					a10 * c2 - 2 * a8 * c4 + 5 * a6 * c4 * b2 - 4 * a6 * c2 * b4
							+ 2 * a4 * c8 - 5 * a4 * c6 * b2 + a4 * c4 * b4
							+ 2 * a4 * c2 * b6 - a2 * c10 - a2 * c8 * b2
							+ 8 * a2 * c6 * b4 - 9 * a2 * c4 * b6
							+ 3 * a2 * c2 * b8 + c10 * b2 - c8 * b4
							- 3 * c6 * b6 + 5 * c4 * b8 - 2 * c2 * b10,
					2 * a10 * c2 - 2 * a10 * b2 - 4 * a8 * c4 + 4 * a8 * b4
							+ 18 * a6 * c4 * b2 - 18 * a6 * c2 * b4
							+ 4 * a4 * c8 - 18 * a4 * c6 * b2
							+ 18 * a4 * c2 * b6 - 4 * a4 * b8 - 2 * a2 * c10
							+ 18 * a2 * c6 * b4 - 18 * a2 * c4 * b6
							+ 2 * a2 * b10 + 2 * c10 * b2 - 4 * c8 * b4
							+ 4 * c4 * b8 - 2 * c2 * b10);
		case 70:
			return a(0, -a4 + 2 * a2 * b2 + a2 * S - b4 - b2 * S - S * c2 + c4,
					0);
		case -70:
			return a(0, -a4 + 2 * a2 * b2 - a2 * S - b4 + b2 * S + S * c2 + c4,
					0);
		case 71:
			return a(0,
					a6 - 3 * a4 * b2 + 3 * a2 * b4 - 2 * a2 * b2 * c2 - a2 * c4
							- b6 + 2 * b4 * c2 - b2 * c4,
					-4 * a4 * b2 + 4 * a4 * c2 + 4 * a2 * b4 - 4 * a2 * c4
							- 4 * b4 * c2 + 4 * b2 * c4);
		case 72:
			return a(0,
					-a6 * c2 + 2 * a4 * c4 - 2 * a2 * c6 + a2 * c2 * b4 + c8
							- c4 * b4,
					-a6 * c2 + a6 * b2 + a2 * c6 - a2 * b6 - c6 * b2 + c2 * b6);
		case 73:
			return a(0,
					a6 * b2 * c4 - a4 * b2 * c6 + a2 * b4 * c6 - a2 * b2 * c8
							- b8 * c4 + 3 * b6 * c6 - 3 * b4 * c8 + b2 * c10,
					0);
		case 74:
			return s(0,
					a6 * c2 - a4 * c4 - a4 * c2 * b2 - a2 * c6
							+ 2 * a2 * c4 * b2
							- a2 * c2
									* b4
							+ c8 - c6 * b2 - c4 * b4 + c2 * b6,
					2 * a8 - 4 * a6 * c2 - 4 * a6 * b2 + 4 * a4 * c4
							+ 4 * a4 * c2 * b2 + 4 * a4 * b4 - 4 * a2 * c6
							+ 4 * a2 * c4 * b2 + 4 * a2 * c2 * b4 - 4 * a2 * b6
							+ 2 * c8 - 4 * c6 * b2 + 4 * c4 * b4 - 4 * c2 * b6
							+ 2 * b8);
		case 75:
			return a(2 * b8 * c4 - 2 * b4 * c8,
					b8 * c4 - b4 * c8 + b4 * c4 * a4, 0);
		case 76:
			return a(2 * b8 * c4 - 2 * b4 * c8,
					-b8 * c4 + b4 * c8 + 3 * b4 * c4 * a4, 0);
		case 77:
			return a(2 * b4 * c2 - 2 * b2 * c4,
					b4 * c2 - b2 * c4 + b2 * c2 * a2, 0);
		case 78:
			return a(2 * b4 * c2 - 2 * b2 * c4,
					-b4 * c2 + b2 * c4 + 3 * b2 * c2 * a2, 0);
		case 79:
			return s(
					2 * a6 - 3 * a4 * b2 - 3 * a4 * c2 + a2 * b4
							+ 4 * a2 * b2 * c2 + a2 * c4 - b4 * c2 - b2 * c4,
					a6 - a4 * b2 - 2 * a4 * c2 + 2 * a2 * b2 * c2 + a2 * c4
							- b2 * c4,
					0);
		case 80:
			return a(0,
					a4 * c2 - a2 * c4 + a2 * c2 * b2 + 2 * c4 * b2
							- 2 * c2 * b4,
					2 * a4 * c2 - 2 * a4 * b2 - 2 * a2 * c4 + 2 * a2 * b4
							+ 2 * c4 * b2 - 2 * c2 * b4);
		case 81:
			return s(0,
					2 * a6 * c2 - 2 * a4 * c4 - 2 * a4 * c2 * b2 - 2 * a2 * c6
							+ 4 * a2 * c4 * b2
							- 2 * a2 * c2 * b4 + 2
									* c8
							- 2 * c6 * b2 - 2 * c4 * b4 + 2 * c2 * b6,
					3 * a8 - 4 * a6 * c2 - 4 * a6 * b2 + 2 * a4 * c4
							+ 4 * a4 * c2 * b2 + 2 * a4 * b4 - 4 * a2 * c6
							+ 4 * a2 * c4 * b2 + 4 * a2 * c2 * b4 - 4 * a2 * b6
							+ 3 * c8 - 4 * c6 * b2 + 2 * c4 * b4 - 4 * c2 * b6
							+ 3 * b8);
		case 82:
			return s(0, c2, 0);
		case 84:
			return s(0,
					a6 * b2 * c2 - 3 * a4 * b4 * c2 + 2 * a4 * b2 * c4
							- 2 * a4 * c6 + a2 * b6 * c2 + 2 * a2 * b2 * c6
							+ b6 * c4 - 3 * b4 * c6 + b2 * c8,
					-2 * a6 * b4 + 8 * a6 * b2 * c2 - 2 * a6 * c4 - 2 * a4 * b6
							- 4 * a4 * b4 * c2 - 4 * a4 * b2 * c4 - 2 * a4 * c6
							+ 8 * a2 * b6 * c2 - 4 * a2 * b4 * c4
							+ 8 * a2 * b2 * c6 - 2 * b6 * c4 - 2 * b4 * c6);
		case 85:
			return s(0, a * b2 * c2 - 2 * a * b * c3 + b3 * c2 + b2 * c3,
					-2 * a3 * b * c - 2 * a * b3 * c - 2 * a * b * c3);
		case 86:
			return a(0, -a * c2 + c3, 2 * a2 * c - 2 * a2 * b - 2 * a * c2
					+ 2 * a * b2 + 2 * c2 * b - 2 * c * b2);
		case 87:
			return s(0,
					a6 - a4 * b2 - 2 * a4 * c2 - 3 * a2 * b4 + 8 * a2 * b2 * c2
							- 2 * a2 * c4 + 2 * b6 - 3 * b4 * c2 - b2 * c4 + c6,
					4 * a6 - 6 * a4 * b2 - 6 * a4 * c2 - 6 * a2 * b4
							+ 24 * a2 * b2 * c2 - 6 * a2 * c4 + 4 * b6
							- 6 * b4 * c2 - 6 * b2 * c4 + 4 * c6);
		case 88:
			return s(0, a4 + 2 * a2 * b2 - 7 * a2 * c2 + b4 + 2 * b2 * c2 + c4,
					-6 * a4 + 6 * a2 * b2 + 6 * a2 * c2 - 6 * b4 + 6 * b2 * c2
							- 6 * c4);
		case 89:
			return s(0, 4 * a2 * b2 - 5 * a2 * c2 + 4 * b2 * c2,
					-6 * a2 * b2 - 6 * a2 * c2 - 6 * b2 * c2);
		case 90:
			return s(0, a - 3 * b + c, 2 * a + 2 * b + 2 * c);
		case 91:
			return s(0,
					a6 - a4 * b2 - 2 * a4 * c2 - a2 * b4 - a2 * b2 * c2
							- 2 * a2 * c4 + b6 - b4 * c2 - b2 * c4 + c6,
					2 * a6 - 4 * a4 * b2 - 4 * a4 * c2 - 4 * a2 * b4
							- 4 * a2 * c4 + 2 * b6 - 4 * b4 * c2 - 4 * b2 * c4
							+ 2 * c6);
		case 92:
			return a(0, 2 * a4 + 2 * a2 * b2 - a2 * c2 - 4 * b4 + 2 * b2 * c2
					+ 2 * c4, 0);
		case 93:
			return s(0, 2 * a2 * b2 - a2 * c2 + 2 * b2 * c2, 0);
		case 94:
			return s(0, a4 * c2 + a2 * c4 - 5 * a2 * c2 * b2,
					2 * a4 * c2 + 2 * a4 * b2 + 2 * a2 * c4 - 12 * a2 * c2 * b2
							+ 2 * a2 * b4 + 2 * c4 * b2 + 2 * c2 * b4);
		case 95:
			return a(0, -a4 * c2 + 2 * a2 * c4 + 2 * a2 * c2 * b2 - c6 - c4 * b2
					- c2 * b4, 0);
		case 96:
			return a(0, -a10 * c2 + 4 * a8 * c4 - a8 * c2 * b2 - 6 * a6 * c6
					+ 6 * a6 * c2 * b4 + 4 * a4 * c8 + 2 * a4 * c6 * b2
					- 4 * a4 * c4 * b4 - 2 * a4 * c2 * b6 - a2 * c10
					- 2 * a2 * c6 * b4 + 8 * a2 * c4 * b6 - 5 * a2 * c2 * b8
					- c10 * b2 + 6 * c6 * b6 - 8 * c4 * b8 + 3 * c2 * b10, 0);
		case 97:
			return a(0,
					-a4 * b * c - a3 * b * c2 + 2 * a2 * b3 * c + a2 * b2 * c2
							+ a * b3 * c2 + a * b2 * c3 + a * b * c4 - b5 * c
							- b4 * c2 + b2 * c4 + b * c5,
					0);
		case 98:
			return s(0,
					a4 * b2 * c2 - 2 * a2 * b4 * c2 - a2 * b2 * c4 + b6 * c2
							- 2 * b4 * c4 + b2 * c6,
					a4 * b2 * c2 + a2 * b4 * c2 + a2 * b2 * c4);
		case 99:
			return a(0,
					a6 * c2 - a4 * c4 + a4 * c2 * b2 - a2 * c6
							+ 2 * a2 * c4 * b2 - a2 * c2 * b4 + c8 - 3 * c6 * b2
							+ 3 * c4 * b4 - c2 * b6,
					0);
		default:
			return new double[0];
		}
	}

	private double[] getCoeff100to199(int n, double a, double b, double c) {
		switch (n) {

		case 100:
			return a(b4 * c2 - b2 * c4, b2 * c2 * a2, 0);
		case 101:
			return a(0, b * c, 0);
		case 102:
			return a(0, b2 * c2, 0);
		case 103:
			return a(0, a4 - b4 + b2 * c2 - c4, 0);
		case 104:
			return a(0, 4 * a4 - 2 * a2 * b2 - 2 * a2 * c2 - 2 * b4 + b2 * c2
					- 2 * c4, 0);
		case 105:
			return s(0,
					a4 * b2 * c2 - 2 * a2 * b4 * c2 + b6 * c2 - 2 * b4 * c4
							+ b2 * c6,
					2 * a4 * b2 * c2 + 2 * a2 * b4 * c2 + 2 * a2 * b2 * c4);
		case 106:
			return a(0, a2 * b2 - a2 * c2 - b4 + 2 * b2 * c2 - c4, 0);
		case 107:
			return s(0, -a2 * b2 * c2 + b4 * c2 - b2 * c4, 6 * a2 * b2 * c2);
		case 108:
			return a(0, a4 * b2 * c4 - a2 * b2 * c6 - b6 * c4 + b2 * c8, 0);
		case 109:
			return a(0, a5 * b * c2 + a5 * c3 + a4 * b2 * c2 + a4 * b * c3
					- 2 * a3 * b * c4 - 2 * a3 * c5 - 2 * a2 * b2 * c4
					- 2 * a2 * b * c5 - a * b5 * c2 - a * b4 * c3 + a * b * c6
					+ a * c7 - b6 * c2 - b5 * c3 + b2 * c6 + b * c7, 0);
		case 112:
			return a(0,
					-a8 * b2 * c2 - a8 * c4 + 2 * a6 * b4 * c2
							+ 2 * a6 * b2 * c4 + 4 * a6 * c6 - a4 * b4 * c4
							+ a4 * b2 * c6 - 6 * a4 * c8 - 2 * a2 * b8 * c2
							+ 2 * a2 * b6 * c4 - 4 * a2 * b2 * c8 + 4 * a2 * c10
							+ b10 * c2 - 2 * b8 * c4 + b6 * c6 - b4 * c8
							+ 2 * b2 * c10 - c12,
					0);
		case 113:
			return a(0,
					-a6 * b2 * c2 - a6 * c4 - a4 * b4 * c2 + 2 * a4 * b2 * c4
							+ a4 * c6 + a2 * b6 * c2 + a2 * b4 * c4
							- 3 * a2 * b2 * c6 + a2 * c8 + b8 * c2 - 2 * b6 * c4
							+ 2 * b2 * c8 - c10,
					0);
		case 114:
			return a(0,
					-2 * a6 * b2 * c4 - a4 * b4 * c4 + 3 * a4 * b2 * c6
							+ 2 * a2 * b6 * c4 - 2 * a2 * b4 * c6 + b8 * c4
							- 3 * b6 * c6 + 3 * b4 * c8 - b2 * c10,
					0);
		case 115:
			return a(0, a6 - 3 * a4 * b2 - 2 * a4 * c2 + 3 * a2 * b4
					+ a2 * b2 * c2 + a2 * c4 - b6 + b4 * c2, 0);
		case 116:
			return a(0, 2 * a6 - 6 * a4 * b2 - 3 * a4 * c2 + 6 * a2 * b4
					+ 3 * a2 * b2 * c2 - 2 * b6 + b2 * c4 + c6, 0);
		case 117:
			return a(0, 3 * a2 - 3 * b2 - c2, 0);
		case 118:
			return a(0,
					-2 * a6 + 6 * a4 * b2 - 7 * a4 * c2 - 6 * a2 * b4
							- 13 * a2 * b2 * c2 + 20 * a2 * c4 + 2 * b6
							+ 20 * b4 * c2 - 11 * b2 * c4 - 11 * c6,
					0);
		case 119:
			return a(0,
					2 * a6 - 6 * a4 * b2 - 5 * a4 * c2 + 6 * a2 * b4
							+ a2 * b2 * c2 + 4 * a2 * c4 - 2 * b6 + 4 * b4 * c2
							- b2 * c4 - c6,
					0);
		case 120:
			return a(0, a6 - 3 * a4 * b2 - 3 * a4 * c2 + 3 * a2 * b4
					+ 3 * a2 * c4 - b6 + 3 * b4 * c2 - b2 * c4 - c6, 0);
		case 121:
			return a(0, -2 * a6 + 6 * a4 * b2 + a4 * c2 - 6 * a2 * b4
					- a2 * b2 * c2 + 4 * a2 * c4 + 2 * b6 + b2 * c4 - 3 * c6,
					0);
		case 122:
			return a(0, -a6 + 3 * a4 * b2 - 3 * a2 * b4 + 3 * a2 * c4 + b6
					+ b2 * c4 - 2 * c6, 0);
		case 123:
			return a(0, -a6 + 3 * a4 * b2 - a4 * c2 - 3 * a2 * b4 + a2 * b2 * c2
					+ 5 * a2 * c4 + b6 + 2 * b2 * c4 - 3 * c6, 0);
		case 124:
			return a(0, a2 - b2 - 2 * c2, 0);
		case 125:
			return a(0,
					a6 - 3 * a4 * b2 - 3 * a4 * c2 + 3 * a2 * b4
							+ 3 * a2 * b2 * c2 + 3 * a2 * c4 - b6 + 2 * b2 * c4
							- c6,
					0);
		case 126:
			return a(0, a6 * c2 - a4 * c4 - 2 * a4 * c2 * b2 - a2 * c6
					+ a2 * c2 * b4 + c8 - c4 * b4, 0);
		case 127:
			return a(0,
					3 * a6 - 9 * a4 * b2 - 4 * a4 * c2 + 9 * a2 * b4
							+ 4 * a2 * b2 * c2 - a2 * c4 - 3 * b6 + b2 * c4
							+ 2 * c6,
					0);
		case 128:
			return a(0, -b4 * c2 + c4 * a2, 0);
		case 129:
			return a(0, S * c2 - c2 * b2 * r3, 0);
		case -129:
			return a(0, -S * c2 - c2 * b2 * r3, 0);
		case 130:
			return a(0, -a10 * b2 * c2 + a10 * c4 + 2 * a8 * b4 * c2
					+ 2 * a8 * b2 * c4 - 4 * a8 * c6 - a6 * b6 * c2
					- 4 * a6 * b4 * c4 - a6 * b2 * c6 + 6 * a6 * c8
					+ a4 * b8 * c2 - a4 * b6 * c4 + 5 * a4 * b4 * c6
					- a4 * b2 * c8 - 4 * a4 * c10 - 2 * a2 * b10 * c2
					+ 4 * a2 * b8 * c4 - a2 * b6 * c6 - 4 * a2 * b4 * c8
					+ 2 * a2 * b2 * c10 + a2 * c12 + b12 * c2 - 2 * b10 * c4
					+ b8 * c6 - b6 * c8 + 2 * b4 * c10 - b2 * c12, 0);
		case 131:
			return a(0, -b3 * c3 - b * c4 * a, 0);
		case 132:
			return a(0, -b2 * c - c2 * a, 0);
		case 133:
			return a(0,
					-a7 * c - 2 * a6 * c2 + a5 * c3 + 3 * a5 * c * b2
							+ 4 * a4 * c4 - 4 * a4 * c2 * b2 + a3 * c5
							+ 2 * a3 * c3 * b2 - 3 * a3 * c * b4 - 2 * a2 * c6
							- 4 * a2 * c4 * b2 + 6 * a2 * c2 * b4 - a * c7
							+ 3 * a * c5 * b2 - 3 * a * c3 * b4 + a * c * b6,
					0);
		case 134:
			return a(0, a2 * c - a * c * b - c3 - c2 * b, 0);
		case 135:
			return a(0, -a2 * b * c3 + b2 * c4, 0);
		case 136:
			return a(0, a2 * b2 * c2 - b3 * c3, 0);
		case 137:
			return a(0, -a * b * c2 + b * c3, 0);
		case 138:
			return s(-2 * b4 * c2 - 2 * b2 * c4,
					b4 * c2 + b2 * c4 + b2 * c2 * a2, -6 * b2 * a2 * c2);
		case 139:
			return a(0, a18 - 8 * a16 * b2 + 28 * a14 * b4 + 6 * a14 * b2 * c2
					- 9 * a14 * c4 - 56 * a12 * b6 - 28 * a12 * b4 * c2
					+ 29 * a12 * b2 * c4 + 15 * a12 * c6 + 70 * a10 * b8
					+ 51 * a10 * b6 * c2 - 36 * a10 * b4 * c4
					- 41 * a10 * b2 * c6 - 9 * a10 * c8 - 56 * a8 * b10
					- 45 * a8 * b8 * c2 + 21 * a8 * b6 * c4 + 52 * a8 * b4 * c6
					+ 3 * a8 * b2 * c8 + 9 * a8 * c10 + 28 * a6 * b12
					+ 20 * a6 * b10 * c2 - 7 * a6 * b8 * c4 - 36 * a6 * b6 * c6
					- 7 * a6 * b4 * c8 + 20 * a6 * b2 * c10 - 15 * a6 * c12
					- 8 * a4 * b14 - 6 * a4 * b12 * c2 + 9 * a4 * b10 * c4
					+ 4 * a4 * b8 * c6 + 15 * a4 * b6 * c8 - 18 * a4 * b4 * c10
					- 5 * a4 * b2 * c12 + 9 * a4 * c14 + a2 * b16
					+ 3 * a2 * b14 * c2 - 12 * a2 * b12 * c4
					+ 16 * a2 * b10 * c6 - 12 * a2 * b8 * c8 - 6 * a2 * b6 * c10
					+ 19 * a2 * b4 * c12 - 9 * a2 * b2 * c14 - b16 * c2
					+ 5 * b14 * c4 - 10 * b12 * c6 + 11 * b10 * c8
					- 10 * b8 * c10 + 11 * b6 * c12 - 10 * b4 * c14
					+ 5 * b2 * c16 - c18,
					-12 * a16 * b2 + 12 * a16 * c2 + 48 * a14 * b4
							- 48 * a14 * c4 - 72 * a12 * b6 - 84 * a12 * b4 * c2
							+ 84 * a12 * b2 * c4 + 72 * a12 * c6 + 60 * a10 * b8
							+ 120 * a10 * b6 * c2 - 120 * a10 * b2 * c6
							- 60 * a10 * c8 - 60 * a8 * b10 - 120 * a8 * b6 * c4
							+ 120 * a8 * b4 * c6 + 60 * a8 * c10 + 72 * a6 * b12
							- 120 * a6 * b10 * c2 + 120 * a6 * b8 * c4
							- 120 * a6 * b4 * c8 + 120 * a6 * b2 * c10
							- 72 * a6 * c12 - 48 * a4 * b14 + 84 * a4 * b12 * c2
							- 120 * a4 * b8 * c6 + 120 * a4 * b6 * c8
							- 84 * a4 * b2 * c12 + 48 * a4 * c14 + 12 * a2 * b16
							- 84 * a2 * b12 * c4 + 120 * a2 * b10 * c6
							- 120 * a2 * b6 * c10 + 84 * a2 * b4 * c12
							- 12 * a2 * c16 - 12 * b16 * c2 + 48 * b14 * c4
							- 72 * b12 * c6 + 60 * b10 * c8 - 60 * b8 * c10
							+ 72 * b6 * c12 - 48 * b4 * c14 + 12 * b2 * c16);
		case 140:
			return a(0, -a4 * c2 - a2 * c4 - c4 * b2 + c2 * b4, 0);
		case 141:
			return a(0, -a2 * c2, 0);
		case 142:
			return s(0, a2 * c2 + 2 * c2 * b2,
					2 * a2 * c2 + 2 * a2 * b2 + 2 * c2 * b2);
		case 144:
			return a(
					a6 * b2 - a6 * c2 - 3 * a4 * b4 + 3 * a4 * c4 + 3 * a2 * b6
							- a2 * b4 * c2
							+ a2 * b2
									* c4
							- 3 * a2 * c6 - b8 + 2 * b6 * c2 - 2 * b2 * c6 + c8,
					a8 - 2 * a6 * b2 + a6 * c2 + 3 * a4 * b2 * c2 - 3 * a4 * c4
							+ 2 * a2 * b6 - 5 * a2 * b4 * c2 + 4 * a2 * b2 * c4
							- a2 * c6 - b8 + b6 * c2 + 3 * b4 * c4 - 5 * b2 * c6
							+ 2 * c8,
					0);
		case 145:
			return a(0, a2 * b2 * c3 - b4 * c3 + b2 * c5, 0);
		case 146:
			return a(0, a2 * b2 - b4 + b2 * c2, 0);
		case 147:
			return s(0, a4 * b6 * c4 - 2 * a4 * b4 * c6 + a4 * b2 * c8
					- 2 * a2 * b6 * c6 + 4 * a2 * b4 * c8 - 2 * a2 * b2 * c10
					+ b6 * c8 - 2 * b4 * c10 + b2 * c12, 0);
		case 148:
			return s(0, a4 * b2 * c4 + a2 * b4 * c4 - 2 * a2 * b2 * c6 + b6 * c4
					- 2 * b4 * c6 + b2 * c8, 0);
		case 149:
			return s(0, b2, 0);
		case 150:
			return s(0, -a2 * c4 + 2 * c6 - c4 * b2, -a4 * c2 - a4 * b2
					- a2 * c4 + 6 * a2 * c2 * b2 - a2 * b4 - c4 * b2 - c2 * b4);
		case 151:
			return s(0, a2 * b4 - a2 * b2 * c2 - b4 * c2 + b2 * c4, 0);
		case 152:
			return a(0, a10 * c2 - 3 * a8 * c4 - a8 * c2 * b2 + 2 * a6 * c6
					+ 4 * a6 * c4 * b2 - 2 * a6 * c2 * b4 + 2 * a4 * c8
					- 6 * a4 * c6 * b2 + 18 * a4 * c4 * b4 + 2 * a4 * c2 * b6
					- 3 * a2 * c10 + 4 * a2 * c8 * b2 + 18 * a2 * c6 * b4
					- 20 * a2 * c4 * b6 + a2 * c2 * b8 + c12 - c10 * b2
					- 2 * c8 * b4 + 2 * c6 * b6 + c4 * b8 - c2 * b10, 0);
		case 153:
			return s(a2 * b2 * c2, 3 * a2 * b2 * c2,
					-a6 - 3 * a4 * b2 - 3 * a4 * c2 - 3 * a2 * b4 - 3 * a2 * c4
							- b6 - 3 * b4 * c2 - 3 * b2 * c4 - c6);
		case 154:
			return a(0,
					a4 * c - a3 * c2 + a3 * c * b - a2 * c3 + 2 * a2 * c2 * b
							- a2 * c * b2 + a * c4 + a * c3 * b - a * c2 * b2
							- a * c * b3,
					0);
		case 155:
			return a(0, b3 * c3 - b * c4 * a, 0);
		case 156:
			return a(0, 5 * a4 * c2 - 10 * a2 * c4 + 6 * a2 * c2 * b2 + 5 * c6
					+ 6 * c4 * b2 - 11 * c2 * b4, 0);
		case 157:
			return a(0, -a * b * c - b2 * c + b * c2, 0);
		case 158:
			return a(0,
					-a8 * b2 * c2 + 3 * a6 * b4 * c2 + a6 * b2 * c4
							- 3 * a4 * b6 * c2 - a4 * b4 * c4 + a2 * b8 * c2
							- a2 * b6 * c4 - a2 * b4 * c6 + a2 * b2 * c8
							+ b8 * c4 - 3 * b6 * c6 + 3 * b4 * c8 - b2 * c10,
					0);
		case 159:
			return a(0,
					-a6 - a4 * b2 + 3 * a4 * c2 + 5 * a2 * b4 - 2 * a2 * b2 * c2
							- 3 * a2 * c4 - 3 * b6 - b4 * c2 + 3 * b2 * c4 + c6,
					0);
		case 160:
			return a(0, a4 * b4 * c4 - b8 * c4 + b4 * c8, 0);
		case 161:
			return a(0, -a8 * c4 + 2 * a4 * c8 - 2 * a4 * c4 * b4 - c12
					- 2 * c8 * b4 + 3 * c4 * b8, 0);
		case 162:
			return s(0, a2 * b2 * c4 - b4 * c4 + b2 * c6,
					-2 * a4 * b2 * c2 - 2 * a2 * b4 * c2 - 2 * a2 * b2 * c4);
		case 163:
			return a(0,
					a6 * c2 - 3 * a4 * c4 - a4 * c2 * b2 + 3 * a2 * c6
							+ 2 * a2 * c4 * b2 - a2 * c2 * b4 - c8 - c6 * b2
							+ c4 * b4 + c2 * b6,
					0);
		case 164:
			return a(0, a6 * c2 - 3 * a4 * c4 + 3 * a2 * c6 - a2 * c2 * b4 - c8
					+ c4 * b4, 0);
		case 165:
			return a(0,
					-a4 * c2 + 2 * a3 * c3 - 2 * a2 * c3 * b + a2 * c2 * b2
							- 2 * a * c5 + 2 * a * c4 * b + c6 - c4 * b2,
					2 * a5 * c - 2 * a5 * b - 4 * a4 * c2 + 4 * a4 * b2
							+ 6 * a3 * c2 * b - 6 * a3 * c * b2 + 4 * a2 * c4
							- 6 * a2 * c3 * b + 6 * a2 * c * b3 - 4 * a2 * b4
							- 2 * a * c5 + 6 * a * c3 * b2 - 6 * a * c2 * b3
							+ 2 * a * b5 + 2 * c5 * b - 4 * c4 * b2
							+ 4 * c2 * b4 - 2 * c * b5);
		case 166:
			return a(0,
					-a6 * b2 * c2 + a4 * b4 * c2 + a4 * b2 * c4 - a2 * b2 * c6
							- b4 * c6 + b2 * c8,
					-a8 * b2 + a8 * c2 + 3 * a6 * b4 - 3 * a6 * c4 - 3 * a4 * b6
							+ 3 * a4 * c6 + a2 * b8 - a2 * c8 - b8 * c2
							+ 3 * b6 * c4 - 3 * b4 * c6 + b2 * c8);
		case 167:
			return a(0, -a2 * b2 * c4 - b4 * c4 + b2 * c6, 0);
		case 168:
			return a(0, -a2 * c2 + c4 - c2 * b2, 0);
		case 169:
			return a(0, -a2 * c2 - c4 + c2 * b2, 0);
		case 170:
			return a(0, a4 - 2 * a2 * c2 + c4 - b4, 0);
		case 171:
			return a(0, a4 * b2 * c4 - 2 * a2 * b2 * c6 - b6 * c4 + b2 * c8, 0);
		case 172:
			return a(0, -a2 * b2 * c4 + b4 * c4 - b2 * c6, 0);
		case 173:
			return a(0,
					-a6 * c2 + a4 * c4 + a4 * c2 * b2 + a2 * c6
							+ 6 * a2 * c4 * b2 - 3 * a2 * c2 * b4 - c8 + c6 * b2
							- 3 * c4 * b4 + 3 * c2 * b6,
					0);
		case 174:
			return a(0, -a4 * b2 * c4 + b6 * c4 - b2 * c8, 0);
		case 175:
			return a(0, -b * c4, 0);
		case 176:
			return a(0, a4 * c4 - 2 * a2 * c6 + c8 - c4 * b4, 0);
		case 177:
			return a(0, -c4, 0);
		case 178:
			return a(0, -a2 * c4 - c6 + c4 * b2, 0);
		case 179:
			return a(0,
					-a3 * b * c4 - a2 * b2 * c4 + a2 * b * c5 + a * b3 * c4
							- 2 * a * b2 * c5 + a * b * c6 + b4 * c4 + b3 * c5
							- b2 * c6 - b * c7,
					0);
		case 180:
			return a(0,
					a6 * b * c4 + 2 * a5 * b * c5 - 3 * a4 * b3 * c4
							- a4 * b * c6 + 4 * a3 * b3 * c5 - 4 * a3 * b * c7
							+ 3 * a2 * b5 * c4 - 2 * a2 * b3 * c6 - a2 * b * c8
							- 6 * a * b5 * c5 + 4 * a * b3 * c7 + 2 * a * b * c9
							- b7 * c4 + 3 * b5 * c6 - 3 * b3 * c8 + b * c10,
					0);
		case 181:
			return a(0, -a2 + b2 + c2, 0);
		case 182:
			return a(0,
					a8 - 2 * a6 * b2 + 6 * a4 * b2 * c2 - 6 * a4 * c4
							+ 2 * a2 * b6 - 4 * a2 * b4 * c2 - 6 * a2 * b2 * c4
							+ 8 * a2 * c6 - b8 - 2 * b6 * c2 + 4 * b4 * c4
							+ 2 * b2 * c6 - 3 * c8,
					0);
		case 183:
			return a(0, -a6 - 2 * a4 * b2 + 2 * a4 * c2 + 3 * a2 * b4
					- 2 * a2 * b2 * c2 - a2 * c4, 0);
		case 184:
			return a(0, a2, 0);
		case 185:
			return a(0, -a2 + c2, 0);
		case 186:
			return s(0,
					a8 - 4 * a6 * b2 - a6 * c2 + 6 * a4 * b4 + 2 * a4 * b2 * c2
							- 4 * a2 * b6 - a2 * b4 * c2 + 2 * a2 * b2 * c4
							- a2 * c6 + b8 - 2 * b4 * c4 + c8,
					2 * a8 - 2 * a6 * b2 - 2 * a6 * c2 + 2 * a4 * b2 * c2
							- 2 * a2 * b6 + 2 * a2 * b4 * c2 + 2 * a2 * b2 * c4
							- 2 * a2 * c6 + 2 * b8 - 2 * b6 * c2 - 2 * b2 * c6
							+ 2 * c8);
		case 187:
			return a(0, a4 * c2 - a2 * c2 * b2 - c6 + c4 * b2,
					4 * a4 * c2 - 4 * a4 * b2 - 4 * a2 * c4 + 4 * a2 * b4
							+ 4 * c4 * b2 - 4 * c2 * b4);
		case 188:
			return a(0, -a6 * c2 + 3 * a4 * c4 - a4 * c2 * b2 - 3 * a2 * c6
					+ 2 * a2 * c2 * b4 + c8 + c6 * b2 - 2 * c4 * b4, 0);
		case 189:
			return s(0, -a4 * c2 + 2 * a2 * c4 + a2 * c2 * b2 - c6 + c4 * b2
					- 2 * c2 * b4, 0);
		case 190:
			return s(0,
					a6 * c2 - a4 * c4 - 2 * a4 * c2 * b2 - a2 * c6
							+ 4 * a2 * c4 * b2 - a2 * c2 * b4 + c8 - 2 * c6 * b2
							- c4 * b4 + 2 * c2 * b6,
					0);
		case 191:
			return s(0, -2 * b2 * c2,
					b4 - 2 * b2 * c2 - 2 * b2 * a2 + c4 - 2 * c2 * a2 + a4);
		case 192:
			return s(0, -b2 * c2,
					b4 - 2 * b2 * c2 - 2 * b2 * a2 + c4 - 2 * c2 * a2 + a4);
		case 193:
			return s(a2 * b6 * c4 + a2 * b4 * c6,
					a4 * b4 * c4 + a2 * b6 * c4 - 2 * a2 * b4 * c6,
					-6 * a4 * b4 * c4);
		case 194:
			return a(0,
					4 * a6 - 12 * a4 * b2 - 4 * a4 * c2 + 12 * a2 * b4
							- a2 * b2 * c2 - 4 * a2 * c4 - 4 * b6 + 5 * b4 * c2
							- 5 * b2 * c4 + 4 * c6,
					0);
		case 195:
			return a(0,
					9 * a6 - 27 * a4 * b2 - 9 * a4 * c2 + 27 * a2 * b4
							+ 2 * a2 * b2 * c2 - 9 * a2 * c4 - 9 * b6
							+ 7 * b4 * c2 - 7 * b2 * c4 + 9 * c6,
					0);
		case 196:
			return s(0, a2 * b2, 0);
		case 197:
			return s(0, -a4 + 3 * a2 * c2 - c4 - b4, 0);
		case 198:
			return s(0, a2 * c2, 2 * a2 * c2 + 2 * a2 * b2 + 2 * c2 * b2);
		case 199:
			return a(0, a2 * c - c3 + 2 * c2 * b - c * b2, 0);
		default:
			return new double[0];
		}
	}

	private double[] getCoeff200to299(int n, double a, double b, double c) {
		switch (n) {
		case 200:
			return a(0, a - b + c, 0);
		case 201:
			return a(0, a2 * c - 2 * a * c * b - c3 + 4 * c2 * b - 3 * c * b2,
					0);
		case 202:
			return a(0, -a2 * c + 2 * a * c2 - 2 * a * c * b - c3 - 2 * c2 * b
					+ 3 * c * b2, 0);
		case 203:
			return s(
					-a8 + 2 * a6 * b2 + 2 * a6 * c2 - a4 * b4 - 4 * a4 * b2 * c2
							- a4 * c4 + 2 * a2 * b4 * c2 + 2 * a2 * b2 * c4
							- b4 * c4,
					a4 * b4 - 2 * a4 * b2 * c2 + a4 * c4 - 2 * a2 * b6
							+ 4 * a2 * b4 * c2 - 2 * a2 * b2 * c4 + b8
							- 2 * b6 * c2 + b4 * c4,
					-2 * a8 + 4 * a6 * b2 + 4 * a6 * c2 - 6 * a4 * b4
							- 6 * a4 * c4 + 4 * a2 * b6 + 4 * a2 * c6 - 2 * b8
							+ 4 * b6 * c2 - 6 * b4 * c4 + 4 * b2 * c6 - 2 * c8);
		case 204:
			return a(0, -a8 * b4 * c2 + a8 * b2 * c4 + 4 * a6 * b6 * c2
					- 3 * a6 * b4 * c4 - a6 * b2 * c6 - 6 * a4 * b8 * c2
					+ 5 * a4 * b6 * c4 + a4 * b4 * c6 + 4 * a2 * b10 * c2
					- 5 * a2 * b8 * c4 + 2 * a2 * b4 * c8 - a2 * b2 * c10
					- b12 * c2 + 2 * b10 * c4 - b8 * c6 + b6 * c8 - 2 * b4 * c10
					+ b2 * c12, 0);
		case 205:
			return a(0,
					-a10 + 4 * a8 * b2 + 2 * a8 * c2 - 6 * a6 * b4
							- 5 * a6 * b2 * c2 - a6 * c4 + 4 * a4 * b6
							+ 5 * a4 * b4 * c2 + a4 * c6 - a2 * b8
							- 3 * a2 * b6 * c2 + a2 * b4 * c4 + 2 * a2 * b2 * c6
							- 2 * a2 * c8 + b8 * c2 - b6 * c4 - b2 * c8 + c10,
					0);
		case 206:
			return a(0, -a2 * b * c3 + a * b2 * c3 - b3 * c3 + b * c5, 0);
		case 207:
			return a(0, -a * c2 - 2 * c3 - c2 * b,
					a2 * c - a2 * b - a * c2 + a * b2 + c2 * b - c * b2);
		case 208:
			return a(0, a12 - 6 * a10 * b2 - a10 * c2 + 15 * a8 * b4
					+ 3 * a8 * b2 * c2 - 2 * a8 * c4 - 20 * a6 * b6
					- 2 * a6 * b4 * c2 + 8 * a6 * b2 * c4 + 2 * a6 * c6
					+ 15 * a4 * b8 - 2 * a4 * b6 * c2 - 12 * a4 * b4 * c4
					- 2 * a4 * b2 * c6 + a4 * c8 - 6 * a2 * b10
					+ 3 * a2 * b8 * c2 + 8 * a2 * b6 * c4 - 2 * a2 * b4 * c6
					- 2 * a2 * b2 * c8 - a2 * c10 + b12 - b10 * c2 - 2 * b8 * c4
					+ 2 * b6 * c6 + b4 * c8 - b2 * c10,
					4 * a10 * b2 - 4 * a10 * c2 - 8 * a8 * b4 + 8 * a8 * c4
							+ 20 * a6 * b4 * c2 - 20 * a6 * b2 * c4
							+ 8 * a4 * b8 - 20 * a4 * b6 * c2
							+ 20 * a4 * b2 * c6 - 8 * a4 * c8 - 4 * a2 * b10
							+ 20 * a2 * b6 * c4 - 20 * a2 * b4 * c6
							+ 4 * a2 * c10 + 4 * b10 * c2 - 8 * b8 * c4
							+ 8 * b4 * c8 - 4 * b2 * c10);
		case 209:
			return a(0, a4 - 3 * a2 * c2 + 2 * c4 + c2 * b2 - b4, 0);
		case 210:
			return a(0,
					a4 * b2 * c2 - a4 * c4 - 2 * a2 * b4 * c2 + a2 * b2 * c4
							+ a2 * c6 + b6 * c2 - b2 * c6,
					a6 * b2 - a6 * c2 - a2 * b6 + a2 * c6 + b6 * c2 - b2 * c6);
		case 211:
			return s(0, -a4 * c2 + a2 * c4 + a2 * c2 * b2, 2 * a2 * c2 * b2);
		case 212:
			return s(0,
					a4 * c2 - a2 * c4 - 2 * a2 * c2 * b2 + c4 * b2 + c2 * b4,
					-2 * a2 * c2 * b2);
		case 213:
			return a(0, 2 * a2 * b2 - 2 * b4 + b2 * c2 + 2 * c4, 0);
		case 214:
			return s(0, b2 * c4, -b6 + b4 * c2 + b4 * a2 + b2 * c4 + b2 * a4
					- c6 + c4 * a2 + c2 * a4 - a6);
		case 215:
			return s(b4 * c4, -b2 * c4 * a2,
					b4 * c2 * a2 + b2 * c4 * a2 + b2 * c2 * a4);
		case 216:
			return s(0, -a4 * c2 + 2 * a2 * c4 + a2 * c2 * b2 - c6 + c4 * b2,
					-2 * a6 + 2 * a4 * c2 + 2 * a4 * b2 + 2 * a2 * c4
							- 2 * a2 * c2 * b2 + 2 * a2 * b4 - 2 * c6
							+ 2 * c4 * b2 + 2 * c2 * b4 - 2 * b6);
		case 217:
			return a(0,
					-a12 + 3 * a10 * b2 + 2 * a10 * c2 - 5 * a8 * b4
							- 5 * a8 * b2 * c2 + 7 * a6 * b6 + 4 * a6 * b4 * c2
							+ a6 * b2 * c4 - 2 * a6 * c6 - 6 * a4 * b8
							- 3 * a4 * b6 * c2 + 3 * a4 * b2 * c6 + a4 * c8
							+ 2 * a2 * b10 + 4 * a2 * b8 * c2 - 3 * a2 * b6 * c4
							- 2 * a2 * b2 * c8 - 2 * b10 * c2 + 2 * b8 * c4
							- b6 * c6 + b4 * c8,
					0);
		case 218:
			return s(0,
					-a10 + 4 * a8 * b2 + a8 * c2 - 5 * a6 * b4
							- 6 * a6 * b2 * c2 + a6 * c4 + 2 * a4 * b6
							+ 9 * a4 * b4 * c2 - a4 * c6 - 4 * a2 * b6 * c2
							- 3 * a2 * b4 * c4 + 2 * a2 * b2 * c6 + 2 * b6 * c4
							- b4 * c6,
					0);
		case 219:
			return s(-1, 1, -3);
		case 220:
			return a(0, a * c2 - c3 + c2 * b, -2 * a2 * b + 2 * a2 * c
					+ 2 * a * b2 - 2 * a * c2 - 2 * b2 * c + 2 * b * c2);
		case 221:
			return a(0, -a2 * b * c2 + b * c4, 0);
		case 222:
			return a(0, -a2 * b2 * c4 + b2 * c6, 0);
		case 223:
			return a(0, -a4 * b2 * c4 + a2 * b4 * c4 - b4 * c6 + b2 * c8, 0);
		case 224:
			return a(0, -a * b2 * c4 + b2 * c5, 0);
		case 225:
			return a(0, -a2 * b2 * c4 + a * b3 * c4 - b3 * c5 + b2 * c6, 0);
		case 226:
			return s(2 * a4 * b2 * c2 - 2 * a2 * b4 * c2 - 2 * a2 * b2 * c4,
					a6 * c2 - 2 * a4 * c4 - a4 * c2 * b2 + a2 * c6
							+ 2 * a2 * c4 * b2 + a2 * c2 * b4 - c6 * b2
							+ 2 * c4 * b4 - c2 * b6,
					2 * a6 * b2 + 2 * a6 * c2 - 4 * a4 * b4 - 2 * a4 * b2 * c2
							- 4 * a4 * c4 + 2 * a2 * b6 - 2 * a2 * b4 * c2
							- 2 * a2 * b2 * c4 + 2 * a2 * c6 + 2 * b6 * c2
							- 4 * b4 * c4 + 2 * b2 * c6);
		case 228:
			return s(0, c2 * b, -6 * a * c * b);
		case 229:
			return s(0, c4 * b2, -6 * a2 * c2 * b2);
		case 230:
			return s(0, a10 * c - 2 * a9 * c2 - 2 * a9 * c * b - a8 * c3
					+ 8 * a8 * c2 * b - 4 * a8 * c * b2 + 5 * a7 * c4
					- 8 * a7 * c3 * b + a7 * c2 * b2 + 8 * a7 * c * b3
					- 3 * a6 * c5 - 6 * a6 * c4 * b + 18 * a6 * c3 * b2
					- 24 * a6 * c2 * b3 + 6 * a6 * c * b4 - 3 * a5 * c6
					+ 18 * a5 * c5 * b - 24 * a5 * c4 * b2 + 12 * a5 * c3 * b3
					+ 9 * a5 * c2 * b4 - 12 * a5 * c * b5 + 5 * a4 * c7
					- 12 * a4 * c6 * b + 3 * a4 * c5 * b2 + 22 * a4 * c4 * b3
					- 33 * a4 * c3 * b4 + 24 * a4 * c2 * b5 - 4 * a4 * c * b6
					- a3 * c8 - 4 * a3 * c7 * b + 18 * a3 * c6 * b2
					- 32 * a3 * c5 * b3 + 22 * a3 * c4 * b4 - 13 * a3 * c2 * b6
					+ 8 * a3 * c * b7 - 2 * a2 * c9 + 10 * a2 * c8 * b
					- 17 * a2 * c7 * b2 + 14 * a2 * c6 * b3 + 2 * a2 * c5 * b4
					- 16 * a2 * c4 * b5 + 16 * a2 * c3 * b6 - 8 * a2 * c2 * b7
					+ a2 * c * b8 + a * c10 - 4 * a * c9 * b + 5 * a * c8 * b2
					- 8 * a * c6 * b4 + 10 * a * c5 * b5 - 3 * a * c4 * b6
					- 4 * a * c3 * b7 + 5 * a * c2 * b8 - 2 * a * c * b9,
					2 * a10 * c + 2 * a10 * b - 4 * a9 * c2 - 8 * a9 * c * b
							- 4 * a9 * b2 - 2 * a8 * c3 + 12 * a8 * c2 * b
							+ 12 * a8 * c * b2 - 2 * a8 * b3 + 10 * a7 * c4
							- 4 * a7 * c3 * b - 12 * a7 * c2 * b2
							- 4 * a7 * c * b3 + 10 * a7 * b4 - 6 * a6 * c5
							- 14 * a6 * c4 * b + 10 * a6 * c3 * b2
							+ 10 * a6 * c2 * b3 - 14 * a6 * c * b4 - 6 * a6 * b5
							- 6 * a5 * c6 + 24 * a5 * c5 * b - 6 * a5 * c4 * b2
							- 8 * a5 * c3 * b3 - 6 * a5 * c2 * b4
							+ 24 * a5 * c * b5 - 6 * a5 * b6 + 10 * a4 * c7
							- 14 * a4 * c6 * b - 6 * a4 * c5 * b2
							+ 8 * a4 * c4 * b3 + 8 * a4 * c3 * b4
							- 6 * a4 * c2 * b5 - 14 * a4 * c * b6 + 10 * a4 * b7
							- 2 * a3 * c8 - 4 * a3 * c7 * b + 10 * a3 * c6 * b2
							- 8 * a3 * c5 * b3 + 8 * a3 * c4 * b4
							- 8 * a3 * c3 * b5 + 10 * a3 * c2 * b6
							- 4 * a3 * c * b7 - 2 * a3 * b8 - 4 * a2 * c9
							+ 12 * a2 * c8 * b - 12 * a2 * c7 * b2
							+ 10 * a2 * c6 * b3 - 6 * a2 * c5 * b4
							- 6 * a2 * c4 * b5 + 10 * a2 * c3 * b6
							- 12 * a2 * c2 * b7 + 12 * a2 * c * b8 - 4 * a2 * b9
							+ 2 * a * c10 - 8 * a * c9 * b + 12 * a * c8 * b2
							- 4 * a * c7 * b3 - 14 * a * c6 * b4
							+ 24 * a * c5 * b5 - 14 * a * c4 * b6
							- 4 * a * c3 * b7 + 12 * a * c2 * b8
							- 8 * a * c * b9 + 2 * a * b10 + 2 * c10 * b
							- 4 * c9 * b2 - 2 * c8 * b3 + 10 * c7 * b4
							- 6 * c6 * b5 - 6 * c5 * b6 + 10 * c4 * b7
							- 2 * c3 * b8 - 4 * c2 * b9 + 2 * c * b10);
		case 231:
			return s(0,
					-a4 * c2 + 2 * a2 * c4 - 2 * a2 * c2 * b2 - c6 - 2 * c4 * b2
							+ 3 * c2 * b4,
					2 * a6 - 2 * a4 * c2 - 2 * a4 * b2 - 2 * a2 * c4
							+ 12 * a2 * c2 * b2 - 2 * a2 * b4 + 2 * c6
							- 2 * c4 * b2 - 2 * c2 * b4 + 2 * b6);
		case 233:
			return a(0, -a2 * c2 + c4 + c2 * b2, 0);
		case 235:
			return a(0, -a6 * c2 + 2 * a4 * c4 - 2 * a4 * c2 * b2 - a2 * c6
					- 2 * a2 * c4 * b2 + 3 * a2 * c2 * b4, 0);
		case 236:
			return a(0, a4 * c4 - 2 * a2 * c6 + 2 * a2 * c4 * b2 + c8
					+ 2 * c6 * b2 - 3 * c4 * b4, 0);
		case 237:
			return a(0, a4 - 2 * a2 * b2 + b4, 0);
		case 238:
			return a(0,
					-a8 + 2 * a6 * b2 + 2 * a6 * c2 - 4 * a4 * b2 * c2 - a4 * c4
							- 2 * a2 * b6 + 2 * a2 * b4 * c2 + 2 * a2 * b2 * c4
							+ b8 - b4 * c4,
					0);
		case 239:
			return a(0,
					-2 * a8 + 3 * a6 * b2 + 5 * a6 * c2 + a4 * b4
							- 11 * a4 * b2 * c2 - 2 * a4 * c4 - 3 * a2 * b6
							+ 7 * a2 * b4 * c2 + 4 * a2 * b2 * c4 + b8 - b6 * c2
							- 2 * b4 * c4,
					0);
		case 240:
			return a(0,
					-2 * a6 * b2 + 2 * a6 * c2 + a4 * b4 + 4 * a4 * b2 * c2
							- 5 * a4 * c4 + 2 * a2 * b6 - 8 * a2 * b4 * c2
							+ 4 * a2 * b2 * c4 + 2 * a2 * c6 - b8 + 2 * b6 * c2
							+ b4 * c4 - 2 * b2 * c6,
					0);
		case 241:
			return a(0,
					-2 * a8 + a6 * b2 + 7 * a6 * c2 + 2 * a4 * b4
							- 7 * a4 * b2 * c2 - 7 * a4 * c4 - a2 * b6
							- a2 * b4 * c2 + 8 * a2 * b2 * c4 + 2 * a2 * c6
							+ b6 * c2 - b4 * c4 - 2 * b2 * c6,
					0);
		case 242:
			return a(0, -a2 * b2 + a2 * c2 + b4 - b2 * c2, 0);
		case 243:
			return a(0, a4 * c2 - 2 * a2 * c4 + 4 * a2 * c2 * b2 + c6
					+ 4 * c4 * b2 - 5 * c2 * b4, 0);
		case 244:
			return s(a6, 3 * a4 * b2, -21 * a2 * b2 * c2);
		case 245:
			return a(0, -a6 + 2 * a4 * b2 - a2 * b4 - a2 * b2 * c2 + a2 * c4
					+ b4 * c2 - b2 * c4, 0);
		case 246:
			return a(0, -a10 * b2 * c2 + 2 * a8 * b4 * c2 - 3 * a6 * b4 * c4
					+ 2 * a6 * b2 * c6 - 2 * a4 * b8 * c2 + 5 * a4 * b6 * c4
					- 3 * a4 * b4 * c6 + a2 * b10 * c2 - a2 * b8 * c4
					- 2 * a2 * b6 * c6 + 3 * a2 * b4 * c8 - a2 * b2 * c10
					- b10 * c4 + 3 * b8 * c6 - 3 * b6 * c8 + b4 * c10, 0);
		case 247:
			return a(0,
					a6 * b * c - a5 * b2 * c - a5 * b * c2 - 2 * a4 * b3 * c
							+ 3 * a4 * b2 * c2 + 2 * a3 * b4 * c
							- 2 * a3 * b2 * c3 + a2 * b5 * c - 4 * a2 * b4 * c2
							+ 2 * a2 * b3 * c3 + 2 * a2 * b2 * c4 - a2 * b * c5
							- a * b6 * c + a * b5 * c2 + 2 * a * b4 * c3
							- 2 * a * b3 * c4 - a * b2 * c5 + a * b * c6
							+ b6 * c2 - 2 * b5 * c3 + 2 * b3 * c5 - b2 * c6,
					0);
		case 248:
			return a(0, -a2 * b2 * c2 + b2 * c4, -2 * a4 * b2 + 2 * a4 * c2
					+ 2 * a2 * b4 - 2 * a2 * c4 - 2 * b4 * c2 + 2 * b2 * c4);
		case 249:
			return a(0,
					-a6 * c2 + 2 * a4 * c4 + 3 * a4 * c2 * b2 - 2 * a2 * c6
							- 5 * a2 * c2 * b4 + c8 - 3 * c6 * b2 + 5 * c4 * b4,
					2 * a6 * c2 - 2 * a6 * b2 - 2 * a2 * c6 + 2 * a2 * b6
							+ 2 * c6 * b2 - 2 * c2 * b6);
		case 250:
			return a(0,
					-a6 * c2 + 2 * a4 * c4 + 2 * a4 * c2 * b2 - 2 * a2 * c6
							- 3 * a2 * c2 * b4 + c8 - 2 * c6 * b2 + 3 * c4 * b4,
					a6 * c2 - a6 * b2 - a2 * c6 + a2 * b6 + c6 * b2 - c2 * b6);
		case 251:
			return a(0, -a * b * c + c3, 0);
		case 252:
			return a(0, -a2 * b2 * c2 + c6, 0);
		case 253:
			return a(0, a3 * c2 + a2 * c3 + a2 * c2 * b + 2 * a * c3 * b
					+ a * c2 * b2 + c3 * b2 + c2 * b3, 0);
		case 254:
			return a(0, a3 * b * c + a3 * c2 + a2 * b * c2 + a2 * c3
					- a * b3 * c + a * b * c3, 0);
		case 255:
			return a(0, -a16 * c2 + 5 * a14 * c4 - 9 * a12 * c6
					- 10 * a12 * c4 * b2 + 10 * a12 * c2 * b4 + 5 * a10 * c8
					+ 36 * a10 * c6 * b2 - 18 * a10 * c4 * b4
					- 16 * a10 * c2 * b6 + 5 * a8 * c10 - 45 * a8 * c8 * b2
					- 21 * a8 * c6 * b4 + 59 * a8 * c4 * b6 - 9 * a6 * c12
					+ 20 * a6 * c10 * b2 + 52 * a6 * c8 * b4 - 36 * a6 * c6 * b6
					- 43 * a6 * c4 * b8 + 16 * a6 * c2 * b10 + 5 * a4 * c14
					- 18 * a4 * c10 * b4 - 28 * a4 * c8 * b6 + 51 * a4 * c6 * b8
					- 10 * a4 * c2 * b12 - a2 * c16 - 6 * a2 * c12 * b4
					+ 20 * a2 * c10 * b6 - 9 * a2 * c8 * b8 - 12 * a2 * c6 * b10
					+ 8 * a2 * c4 * b12 - c16 * b2 + c14 * b4 + 9 * c12 * b6
					- 25 * c10 * b8 + 25 * c8 * b10 - 9 * c6 * b12 - c4 * b14
					+ c2 * b16, 0);
		case 256:
			return s(0, -a6 * b6 * c2 + a6 * b4 * c4 + a6 * b2 * c6 - a6 * c8
					+ 2 * a4 * b8 * c2 - 5 * a4 * b6 * c4 + 7 * a4 * b4 * c6
					- 7 * a4 * b2 * c8 + 3 * a4 * c10 - a2 * b10 * c2
					+ a2 * b8 * c4 + 4 * a2 * b6 * c6 - 10 * a2 * b4 * c8
					+ 9 * a2 * b2 * c10 - 3 * a2 * c12 + b10 * c4 - 3 * b8 * c6
					+ 2 * b6 * c8 + 2 * b4 * c10 - 3 * b2 * c12 + c14, 0);
		case 257:
			return a(0, -a4 * c2 + a2 * c4 - a2 * c2 * b2,
					-2 * a4 * c2 + 2 * a4 * b2 + 2 * a2 * c4 - 2 * a2 * b4
							- 2 * c4 * b2 + 2 * c2 * b4);
		case 258:
			return a(b4 * c2 - b2 * c4, -b4 * c2 + b2 * c4 + 2 * b2 * c2 * a2,
					0);
		case 259:
			return a(0, a * c2 - c3 + c2 * b, -2 * a2 * c + 2 * a2 * b
					+ 2 * a * c2 - 2 * a * b2 - 2 * c2 * b + 2 * c * b2);
		case 260:
			return a(0, a2 * c4 - c6 + c4 * b2, -2 * a4 * c2 + 2 * a4 * b2
					+ 2 * a2 * c4 - 2 * a2 * b4 - 2 * c4 * b2 + 2 * c2 * b4);
		case 261:
			return a(0, a4 * c2 - 2 * a2 * c4 + a2 * c2 * b2 - a2 * c2 * r3 * S
					+ c6 + c4 * b2 + c4 * r3 * S - 2 * c2 * b4, 0);
		case -261:
			return a(0, -a4 * c2 + 2 * a2 * c4 - a2 * c2 * b2 - a2 * c2 * r3 * S
					- c6 - c4 * b2 + c4 * r3 * S + 2 * c2 * b4, 0);
		case 262:
			return a(0,
					-S * a2 * c2 + S * c4 - a4 * c2 * r3 + 2 * a2 * c4 * r3
							- a2 * c2 * r3 * b2 - c6 * r3 - c4 * r3 * b2
							+ 2 * c2 * r3 * b4,
					0);
		case -262:
			return a(0,
					-S * a2 * c2 + S * c4 + a4 * c2 * r3 - 2 * a2 * c4 * r3
							+ a2 * c2 * r3 * b2 + c6 * r3 + c4 * r3 * b2
							- 2 * c2 * r3 * b4,
					0);
		case 263:
			return a(0, a6 * c2 - 2 * a4 * c4 + a2 * c6 + 2 * c6 * b2 - c4 * b4
					- c2 * b6, 0);
		case 264:
			return a(0, -a8 + a6 * b2 - 2 * a6 * c2 - a6 * r3 * S + 3 * a4 * b4
					+ 2 * a4 * b2 * c2 + 2 * a4 * b2 * r3 * S + 6 * a4 * c4
					+ a4 * c2 * r3 * S - 5 * a2 * b6 + 5 * a2 * b4 * c2
					- a2 * b4 * r3 * S + 2 * a2 * b2 * c4
					+ a2 * b2 * c2 * r3 * S - 2 * a2 * c6 + a2 * c4 * r3 * S
					+ 2 * b8 - 5 * b6 * c2 + 3 * b4 * c4 - b4 * c2 * r3 * S
					+ b2 * c6 + 2 * b2 * c4 * r3 * S - c8 - c6 * r3 * S, 0);
		case -264:
			return a(0, -a8 + a6 * b2 - 2 * a6 * c2 + a6 * r3 * S + 3 * a4 * b4
					+ 2 * a4 * b2 * c2 - 2 * a4 * b2 * r3 * S + 6 * a4 * c4
					- a4 * c2 * r3 * S - 5 * a2 * b6 + 5 * a2 * b4 * c2
					+ a2 * b4 * r3 * S + 2 * a2 * b2 * c4
					- a2 * b2 * c2 * r3 * S - 2 * a2 * c6 - a2 * c4 * r3 * S
					+ 2 * b8 - 5 * b6 * c2 + 3 * b4 * c4 + b4 * c2 * r3 * S
					+ b2 * c6 - 2 * b2 * c4 * r3 * S - c8 + c6 * r3 * S, 0);
		case 265:
			return a(
					a10 * b2 - a10 * c2 - 4 * a8 * b4 + 4 * a8 * c4
							+ 6 * a6 * b6 + 2 * a6 * b4 * c2 - 2 * a6 * b2 * c4
							- 6 * a6 * c6 - 4 * a4 * b8 + 4 * a4 * c8 + a2 * b10
							- a2 * b8 * c2 - 2 * a2 * b6 * c4 + 2 * a2 * b4 * c6
							+ a2 * b2 * c8 - a2 * c10,
					-a8 * b2 * c2 - a8 * c4 + 2 * a6 * c6 + 2 * a4 * b6 * c2
							- 2 * a4 * b4 * c4 - 2 * a2 * b4 * c6
							+ 4 * a2 * b2 * c8 - 2 * a2 * c10 - b10 * c2
							+ 3 * b8 * c4 - 4 * b6 * c6 + 4 * b4 * c8
							- 3 * b2 * c10 + c12,
					2 * a10 * b2 - 2 * a10 * c2 - 4 * a8 * b4 + 4 * a8 * c4
							+ 10 * a6 * b4 * c2 - 10 * a6 * b2 * c4
							+ 4 * a4 * b8 - 10 * a4 * b6 * c2
							+ 10 * a4 * b2 * c6 - 4 * a4 * c8 - 2 * a2 * b10
							+ 10 * a2 * b6 * c4 - 10 * a2 * b4 * c6
							+ 2 * a2 * c10 + 2 * b10 * c2 - 4 * b8 * c4
							+ 4 * b4 * c8 - 2 * b2 * c10);
		case 266:
			return a(
					5 * a16 * b2 - 5 * a16 * c2 - 31 * a14 * b4 + 31 * a14 * c4
							+ 77 * a12 * b6 + 51 * a12 * b4 * c2
							- 51 * a12 * b2 * c4 - 77 * a12 * c6 - 91 * a10 * b8
							- 98 * a10 * b6 * c2 + 98 * a10 * b2 * c6
							+ 91 * a10 * c8 + 35 * a8 * b10 + 75 * a8 * b8 * c2
							+ 16 * a8 * b6 * c4 - 16 * a8 * b4 * c6
							- 75 * a8 * b2 * c8 - 35 * a8 * c10 + 35 * a6 * b12
							- 60 * a6 * b10 * c2 + 15 * a6 * b8 * c4
							- 15 * a6 * b4 * c8 + 60 * a6 * b2 * c10
							- 35 * a6 * c12 - 49 * a4 * b14 + 85 * a4 * b12 * c2
							- 27 * a4 * b10 * c4 - a4 * b8 * c6 + a4 * b6 * c8
							+ 27 * a4 * b4 * c10 - 85 * a4 * b2 * c12
							+ 49 * a4 * c14 + 23 * a2 * b16 - 66 * a2 * b14 * c2
							+ 34 * a2 * b12 * c4 + 38 * a2 * b10 * c6
							- 38 * a2 * b6 * c10 - 34 * a2 * b4 * c12
							+ 66 * a2 * b2 * c14 - 23 * a2 * c16 - 4 * b18
							+ 18 * b16 * c2 - 18 * b14 * c4 - 42 * b12 * c6
							+ 126 * b10 * c8 - 126 * b8 * c10 + 42 * b6 * c12
							+ 18 * b4
									* c14
							- 18 * b2 * c16 + 4 * c18,
					4 * a18 - 18 * a16 * b2 - 15 * a16 * c2 + 18 * a14 * b4
							+ 39 * a14 * b2 * c2 + 14 * a14 * c4 + 42 * a12 * b6
							- 23 * a12 * b4 * c2 - 45 * a12 * b2 * c4
							+ 7 * a12 * c6 - 126 * a10 * b8 + 24 * a10 * b6 * c2
							- 9 * a10 * b4 * c4 + 48 * a10 * b2 * c6
							+ 126 * a8 * b10 - 90 * a8 * b8 * c2
							+ 95 * a8 * b6 * c4 - 114 * a8 * b4 * c6
							+ 72 * a8 * b2 * c8 - 49 * a8 * c10 - 42 * a6 * b12
							+ 55 * a6 * b10 * c2 + 48 * a6 * b8 * c4
							- 186 * a6 * b6 * c6 + 280 * a6 * b4 * c8
							- 225 * a6 * b2 * c10 + 70 * a6 * c12
							- 18 * a4 * b14 + 81 * a4 * b12 * c2
							- 243 * a4 * b10 * c4 + 393 * a4 * b8 * c6
							- 246 * a4 * b6 * c8 - 51 * a4 * b4 * c10
							+ 123 * a4 * b2 * c12 - 39 * a4 * c14
							+ 18 * a2 * b16 - 102 * a2 * b14 * c2
							+ 179 * a2 * b12 * c4 - 54 * a2 * b10 * c6
							- 228 * a2 * b8 * c8 + 362 * a2 * b6 * c10
							- 225 * a2 * b4 * c12 + 42 * a2 * b2 * c14
							+ 8 * a2 * c16 - 4 * b18 + 31 * b16 * c2
							- 39 * b14 * c4 - 94 * b12 * c6 + 250 * b10 * c8
							- 141 * b8 * c10 - 91 * b6 * c12 + 124 * b4 * c14
							- 36 * b2 * c16,
					2 * a16 * b2 - 2 * a16 * c2 - 14 * a14 * b4 + 14 * a14 * c4
							+ 42 * a12 * b6 + 20 * a12 * b4 * c2
							- 20 * a12 * b2 * c4 - 42 * a12 * c6 - 70 * a10 * b8
							- 32 * a10 * b6 * c2 + 32 * a10 * b2 * c6
							+ 70 * a10 * c8 + 70 * a8 * b10 + 2 * a8 * b6 * c4
							- 2 * a8 * b4 * c6 - 70 * a8 * c10 - 42 * a6 * b12
							+ 32 * a6 * b10 * c2 - 2 * a6 * b8 * c4
							+ 2 * a6 * b4 * c8 - 32 * a6 * b2 * c10
							+ 42 * a6 * c12 + 14 * a4 * b14 - 20 * a4 * b12 * c2
							+ 2 * a4 * b8 * c6 - 2 * a4 * b6 * c8
							+ 20 * a4 * b2 * c12 - 14 * a4 * c14 - 2 * a2 * b16
							+ 20 * a2 * b12 * c4 - 32 * a2 * b10 * c6
							+ 32 * a2 * b6 * c10 - 20 * a2 * b4 * c12
							+ 2 * a2 * c16 + 2 * b16 * c2 - 14 * b14 * c4
							+ 42 * b12 * c6 - 70 * b10 * c8 + 70 * b8 * c10
							- 42 * b6 * c12 + 14 * b4 * c14 - 2 * b2 * c16);
		case 267:
			return s(0,
					a6 * b2 - a6 * c2 - 2 * a4 * b4 + 2 * a4 * c4 + a2 * b6
							- a2 * c6 + b6 * c2 - 2 * b4 * c4 + b2 * c6,
					-2 * a6 * b2 - 2 * a6 * c2 + 4 * a4 * b4 + 4 * a4 * c4
							- 2 * a2 * b6 - 2 * a2 * c6 - 2 * b6 * c2
							+ 4 * b4 * c4 - 2 * b2 * c6);
		case 268:
			return a(0,
					a6 - 3 * a4 * b2 - 2 * a4 * c2 + 3 * a2 * b4
							- 4 * a2 * b2 * c2 + a2 * c4 - b6 + 6 * b4 * c2
							- 5 * b2 * c4,
					0);
		case 269:
			return a(0, -a4 * c2 + a3 * c2 * b + 2 * a2 * c4 - a2 * c3 * b
					- a2 * c2 * b2 - a * c4 * b + 2 * a * c3 * b2 - a * c2 * b3
					- c6 + c5 * b - c4 * b2 - c3 * b3 + 2 * c2 * b4, 0);
		case 270:
			return a(0, a6 * c2 - a4 * c4 - a2 * c6 + a2 * c2 * b4 + c8
					+ c4 * b4 - 2 * c2 * b6, 0);
		case 271:
			return s(
					-b8 + 3 * b6 * c2 - b4 * c4 - 2 * b4 * c2 * a2 + 3 * b2 * c6
							- 2 * b2 * c4 * a2 + b2 * c2 * a4 - c8,
					-2 * b6 * a2 - b6 * c2 - b4 * a4 + 9 * b4 * a2 * c2
							- 3 * b2 * a4 * c2 - 7 * b2 * c6 + a6 * c2
							- 2 * a4 * c4 + 5 * a2 * c6 + c8,
					b8 - b6 * a2 - b6 * c2 + 10 * b4 * a4 - 9 * b4 * a2 * c2
							+ 10 * b4 * c4 - b2 * a6 - 9 * b2 * a4 * c2
							- 9 * b2 * a2 * c4 - b2 * c6 + a8 - a6 * c2
							+ 10 * a4 * c4 - a2 * c6 + c8);
		case 272:
			return s(
					a8 * b2 * c2 - 9 * a4 * b4 * c4 - 2 * a2 * b8 * c2
							- 2 * a2 * b2 * c8 + 12 * b6 * c6,
					3 * a6 * c6 + 9 * a4 * c4 * b4 + 3 * a2 * c8 * b2
							- 3 * a2 * c2 * b8 - 12 * c6 * b6,
					3 * a8 * b2 * c2 + 6 * a6 * b6 + 6 * a6 * c6
							- 27 * a4 * b4 * c4 + 3 * a2 * b8 * c2
							+ 3 * a2 * b2 * c8 + 6 * b6 * c6);
		case 273:
			return a(0, -2 * a2 * c2 + c4 + c2 * b2, 0);
		case 274:
			return a(0, -a6 * b2 * c4 + 2 * a5 * b3 * c4 - 2 * a4 * b4 * c4
					- 2 * a4 * b3 * c5 + 3 * a4 * b2 * c6 + 4 * a3 * b4 * c5
					- 2 * a3 * b3 * c6 - 2 * a3 * b2 * c7 + 2 * a2 * b6 * c4
					- 4 * a2 * b5 * c5 - a2 * b4 * c6 + 4 * a2 * b3 * c7
					- a2 * b2 * c8 - 2 * a * b7 * c4 + 2 * a * b6 * c5
					+ 4 * a * b5 * c6 - 4 * a * b4 * c7 - 2 * a * b3 * c8
					+ 2 * a * b2 * c9 + b8 * c4 - 3 * b6 * c6 + 3 * b4 * c8
					- b2 * c10,
					2 * a7 * b3 * c2 - 2 * a7 * b2 * c3 - 4 * a6 * b4 * c2
							+ 4 * a6 * b2 * c4 + 6 * a5 * b4 * c3
							- 6 * a5 * b3 * c4 + 4 * a4 * b6 * c2
							- 6 * a4 * b5 * c3 + 6 * a4 * b3 * c5
							- 4 * a4 * b2 * c6 - 2 * a3 * b7 * c2
							+ 6 * a3 * b5 * c4 - 6 * a3 * b4 * c5
							+ 2 * a3 * b2 * c7 + 2 * a2 * b7 * c3
							- 4 * a2 * b6 * c4 + 4 * a2 * b4 * c6
							- 2 * a2 * b3 * c7);
		case 275:
			return a(0, a6 - 2 * a5 * c + a4 * c2 + 2 * a4 * c * b - 3 * a4 * b2
					+ 2 * a3 * c3 - 4 * a3 * c2 * b + 4 * a3 * c * b2
					- 3 * a2 * c4 + 2 * a2 * c3 * b + a2 * c2 * b2
					- 4 * a2 * c * b3 + 3 * a2 * b4 + 2 * a * c4 * b
					- 4 * a * c3 * b2 + 4 * a * c2 * b3 - 2 * a * c * b4 + c6
					- 2 * c5 * b + 2 * c4 * b2 - 2 * c2 * b4 + 2 * c * b5 - b6,
					-2 * a5 * c + 2 * a5 * b + 4 * a4 * c2 - 4 * a4 * b2
							- 6 * a3 * c2 * b + 6 * a3 * c * b2 - 4 * a2 * c4
							+ 6 * a2 * c3 * b - 6 * a2 * c * b3 + 4 * a2 * b4
							+ 2 * a * c5 - 6 * a * c3 * b2 + 6 * a * c2 * b3
							- 2 * a * b5 - 2 * c5 * b + 4 * c4 * b2
							- 4 * c2 * b4 + 2 * c * b5);
		case 276:
			return a(0, a6 + a4 * b2 - 2 * a4 * c2 - 2 * a2 * b4 + a2 * b2 * c2
					+ a2 * c4, 0);
		case 277:
			return a(-a4 * b2 + a4 * c2 + 2 * a2 * b4 - 2 * a2 * c4 - b6 + c6,
					a6 - 4 * a4 * c2 + 2 * a2 * c4 + 4 * a2 * c2 * b2
							- 3 * a2 * b4 + c6 - 3 * c2 * b4 + 2 * b6,
					-6 * a4 * b2 + 6 * a4 * c2 + 6 * a2 * b4 - 6 * a2 * c4
							- 6 * b4 * c2 + 6 * b2 * c4);
		case 278:
			return a(0, a4 - 2 * a2 * b2 - 2 * a2 * c2 + b4 + b2 * c2 + c4, 0);
		case 279:
			return a(0, a6 * c2 - 2 * a4 * c4 + a4 * c2 * b2 + a2 * c6
					+ a2 * c4 * b2 - 2 * a2 * c2 * b4, 0);
		case 280:
			return a(0, -a2 * c2 - 2 * c2 * b2, 0);
		case 281:
			return a(0, 2 * b2 * c2 + c4, 0);
		case 282:
			return a(0, a2 * c2 + c4 + 4 * c2 * b2, 0);
		case 283:
			return a(0, -a2 * c2 + 2 * c4 + 2 * c2 * b2, 0);
		case 284:
			return a(0, -2 * a2 * c2 + c4 - 2 * c2 * b2, 0);
		case 285:
			return s(0,
					-a2 * b2 * c5 + a2 * b * c6 + a * b4 * c4 - a * b2 * c6
							- b5 * c4 + b4 * c5,
					2 * a5 * b2 * c2 - 2 * a4 * b4 * c - 2 * a4 * b * c4
							+ 2 * a2 * b5 * c2 + 2 * a2 * b2 * c5
							- 2 * a * b4 * c4);
		case 286:
			return s(0,
					a3 * b * c2 - a3 * c3 - a2 * b3 * c + a2 * b * c3
							+ a * b4 * c - a * b3 * c2,
					-2 * a4 * b * c + 2 * a3 * b3 + 2 * a3 * c3 - 2 * a * b4 * c
							- 2 * a * b * c4 + 2 * b3 * c3);
		case 287:
			return a(0, 2 * a2 * c2 + c2 * b2, 0);
		case 288:
			return a(
					0, a8 - 2 * a6 * b2 - a6 * c2 + 2 * a4 * b2 * c2
							+ 2 * a2 * b6 - a2 * b4 * c2 - a2 * c6 - b8 + c8,
					0);
		case 289:
			return a(0,
					a6 * c2 - a4 * c4 - 2 * a4 * c2 * b2 + 2 * a2 * c2 * b4
							+ c6 * b2 - c2 * b6,
					a6 * c2 - a6 * b2 - a2 * c6 + a2 * b6 + c6 * b2 - c2 * b6);
		case 290:
			return a(0, -a4 * c2 + a2 * c4 - a2 * c2 * b2 + c2 * b4, 0);
		case 291:
			return a(0, -b4 * c2 + b2 * c4 + c6 - c4 * a2, 0);
		case 292:
			return a(0, -2 * a4 * b2 * c2 - a4 * c4 + a2 * b4 * c2 + 2 * a2 * c6
					+ b6 * c2 - c8, 0);
		case 293:
			return a(0,
					a6 - 5 * a4 * b2 + 2 * a4 * c2 - a4 * S + 5 * a2 * b4
							+ 6 * a2 * b2 * c2 - 8 * a2 * c4 + 2 * a2 * c2 * S
							- b6 - 5 * b4 * c2 + b4 * S + 2 * b2 * c4
							+ b2 * c2 * S + 3 * c6 - 3 * c4 * S,
					0);
		case -293:
			return a(0,
					a6 - 5 * a4 * b2 + 2 * a4 * c2 + a4 * S + 5 * a2 * b4
							+ 6 * a2 * b2 * c2 - 8 * a2 * c4 - 2 * a2 * c2 * S
							- b6 - 5 * b4 * c2 - b4 * S + 2 * b2 * c4
							- b2 * c2 * S + 3 * c6 + 3 * c4 * S,
					0);
		case 294:
			return a(0, a3 * b * c2 - 2 * a2 * b2 * c2 - a2 * b * c3
					+ a * b3 * c2 + a * b * c4 + b3 * c3 - b * c5, 0);
		case 295:
			return a(0, a2 - b2 - 3 * c2, 0);
		case 296:
			return a(0, a - c, 0);
		case 297:
			return a(0, 3 * a2 * b2 * c4 + b4 * c4 - b2 * c6, 0);
		case 298:
			return a(0,
					2 * a6 * c2 - a4 * c4 - 3 * a4 * c2 * b2 - 2 * a2 * c6
							+ 4 * a2 * c2 * b4 + c8 + 3 * c6 * b2 - c4 * b4
							- 3 * c2 * b6,
					2 * a6 * c2 - 2 * a6 * b2 - 2 * a2 * c6 + 2 * a2 * b6
							+ 2 * c6 * b2 - 2 * c2 * b6);
		case 299:
			return a(0,
					2 * a3 * b * c - 2 * a3 * c2 - 2 * a2 * b * c2 + 2 * a2 * c3
							- 2 * a * b3 * c + a * b2 * c2 + a * c4
							+ 3 * b3 * c2 - 3 * b2 * c3 + b * c4 - c5,
					2 * a3 * b2 - 2 * a3 * c2 - 2 * a2 * b3 + 2 * a2 * c3
							+ 2 * b3 * c2 - 2 * b2 * c3);
		default:
			return new double[0];
		}
	}

	private double[] getCoeff300to399(int n, double a, double b, double c) {
		switch (n) {
		case 300:
			return a(0,
					2 * a6 * c2 - 3 * a4 * c4 - 4 * a4 * c2 * b2 + a2 * c6
							+ 4 * a2 * c2 * b4 + 2 * c6 * b2 - 2 * c2 * b6,
					a6 * c2 - a6 * b2 - a2 * c6 + a2 * b6 + c6 * b2 - c2 * b6);
		case 301:
			return a(0, a8 - 2 * a6 * b2 - a6 * c2 + 2 * a4 * b2 * c2 + a4 * c4
					+ 2 * a2 * b6 - a2 * b4 * c2 - 2 * a2 * c6 - b8 + c8,
					-a6 * b2 + a6 * c2 + a2 * b6 - a2 * c6 - b6 * c2 + b2 * c6);
		case 302:
			return a(0,
					-a4 * b2 * c2 - 2 * a4 * c4 + 3 * a2 * c6 + b6 * c2
							+ b4 * c4 - b2 * c6 - c8,
					a6 * b2 - a6 * c2 - a2 * b6 + a2 * c6 + b6 * c2 - b2 * c6);
		case 303:
			return a(0,
					2 * S * a2 * c2 - S * c4 - S * c2 * b2 + 2 * a4 * c2 * r3
							- 3 * a2 * c4 * r3 + a2 * c2 * b2 * r3 + c6 * r3
							+ 2 * c4 * b2 * r3 - 3 * c2 * b4 * r3,
					2 * a4 * c2 * r3 - 2 * a4 * r3 * b2 - 2 * a2 * c4 * r3
							+ 2 * a2 * r3 * b4 + 2 * c4 * r3 * b2
							- 2 * c2 * r3 * b4);
		case -303:
			return a(0,
					2 * S * a2 * c2 - S * c4 - S * c2 * b2 - 2 * a4 * c2 * r3
							+ 3 * a2 * c4 * r3 - a2 * c2 * b2 * r3 - c6 * r3
							- 2 * c4 * b2 * r3 + 3 * c2 * b4 * r3,
					-2 * a4 * c2 * r3 + 2 * a4 * r3 * b2 + 2 * a2 * c4 * r3
							- 2 * a2 * r3 * b4 - 2 * c4 * r3 * b2
							+ 2 * c2 * r3 * b4);
		case 304:
			return a(0,
					-2 * a6 * c2 + 5 * a4 * c4 + 2 * a4 * c2 * b2 - 4 * a2 * c6
							- a2 * c2 * b4 + c8 - c6 * b2 - c4 * b4 + c2 * b6,
					0);
		case 305:
			return a(0,
					-2 * a6 * c2 + 3 * a4 * c4 + a4 * c2 * b2 - a2 * c6
							- a2 * c2 * b4 - 2 * c6 * b2 + 2 * c2 * b6,
					-a6 * c2 + a6 * b2 + a2 * c6 - a2 * b6 - c6 * b2 + c2 * b6);
		case 306:
			return a(0, -a3 * b * c2 - a3 * c3 + a * b3 * c2 + a * c5,
					a5 * b - a5 * c + 2 * a3 * b2 * c - 2 * a3 * b * c2
							- 2 * a2 * b3 * c + 2 * a2 * b * c3 - a * b5
							+ 2 * a * b3 * c2 - 2 * a * b2 * c3 + a * c5
							+ b5 * c - b * c5);
		case 307:
			return a(0,
					a4 * c2 - a2 * c4 - 2 * a2 * c2 * b2 - c4 * b2 + c2 * b4,
					-2 * a4 * c2 + 2 * a4 * b2 + 2 * a2 * c4 - 2 * a2 * b4
							- 2 * c4 * b2 + 2 * c2 * b4);
		case 308:
			return a(0, a * c + c2 - c * b, 0);
		case 309:
			return a(0,
					-a4 * c2 + a2 * c4 - 3 * a2 * c2 * b2 - 4 * c4 * b2
							+ 4 * c2 * b4,
					-2 * a4 * c2 + 2 * a4 * b2 + 2 * a2 * c4 - 2 * a2 * b4
							- 2 * c4 * b2 + 2 * c2 * b4);
		case 310:
			return a(0,
					-7 * a6 + 21 * a4 * b2 - a4 * c2 - 21 * a2 * b4
							- 10 * a2 * b2 * c2 + 15 * a2 * c4 + 7 * b6
							+ 11 * b4 * c2 - 11 * b2 * c4 - 7 * c6,
					16 * a4 * b2 - 16 * a4 * c2 - 16 * a2 * b4 + 16 * a2 * c4
							+ 16 * b4 * c2 - 16 * b2 * c4);
		case 311:
			return a(0, -a2 + a * c - c2 + b2, 0);
		case 312:
			return a(0, a2 * b2 * c4 - a * b2 * c5 - b4 * c4 + b2 * c6, 0);
		case 313:
			return a(0,
					a6 - 3 * a4 * b2 + 2 * a4 * c2 + 3 * a2 * b4
							+ 4 * a2 * b2 * c2 - 7 * a2 * c4 - b6 - 6 * b4 * c2
							+ 3 * b2 * c4 + 4 * c6,
					0);
		case 314:
			return a(0, -a2 * c2 + c4 + 3 * c2 * b2, 0);
		case 315:
			return a(0, a2 - b2 + 3 * c2, 0);
		case 316:
			return a(0, -a2 * b4 * c2 + a2 * b2 * c4 + b6 * c2 - b4 * c4, 0);
		case 317:
			return a(0, b * c + c2, 0);
		case 318:
			return a(0, -a * b2 * c3 - a * b * c4 + b3 * c3 - b * c5, 0);
		case 319:
			return a(0, b2 * c3 + b * c4, 0);
		case 320:
			return a(0, -a * b2 * c2 - 2 * a * b * c3 - a * c4 + b3 * c2
					+ b2 * c3 - b * c4 - c5, 0);
		case 321:
			return a(0, 2 * a * b * c3 + a * c4 + b * c4 + c5, 0);
		case 322:
			return a(0, -a4 * b2 + b4 * c2, 0);
		case 323:
			return a(0, b2 * c - c2 * a, 0);
		case 324:
			return s(0, b2 * c - c2 * a, 0);
		case 325:
			return a(0, -a2 * b2 * c2 + b2 * c4, 0);
		case 326:
			return a(0, a2 * b2 * c2 + b2 * c4, 0);
		case 327:
			return s(0, 1, 1);
		case 328:
			return a(0, a * c + c2, 0);
		case 329:
			return a(0,
					a6 - 3 * a4 * b2 + a4 * c2 + 3 * a2 * b4 + 2 * a2 * b2 * c2
							- 5 * a2 * c4 - b6 - 3 * b4 * c2 + b2 * c4 + 3 * c6,
					0);
		case 330:
			return a(0,
					2 * a6 * c2 - 5 * a4 * c4 + 4 * a4 * c2 * b2 + a2 * c6
							- 2 * a2 * c2 * b4 + 2 * c8 - 2 * c6 * b2
							+ 4 * c4 * b4 - 4 * c2 * b6,
					-3 * a6 * c2 + 3 * a6 * b2 + 3 * a2 * c6 - 3 * a2 * b6
							- 3 * c6 * b2 + 3 * c2 * b6);
		case 331:
			return s(0,
					2 * a4 * b2 * c2 - a4 * c4
							+ 4 * a2 * b4
									* c2
							- 6 * a2 * b2 * c4 + 3 * a2 * c6 - 2 * b4 * c4,
					a6 * b2 + a6 * c2 + 2 * a4 * b4 - 4 * a4 * b2 * c2
							+ 2 * a4 * c4 + a2 * b6 - 4 * a2 * b4 * c2
							- 4 * a2 * b2 * c4 + a2 * c6 + b6 * c2 + 2 * b4 * c4
							+ b2 * c6);
		case 332:
			return a(0, -a3 * c + a2 * c2 - a2 * c * b + a * c3 - 2 * a * c2 * b
					+ a * c * b2 - c4 - c3 * b + c2 * b2 + c * b3, 0);
		case 333:
			return a(0,
					a4 * c - 2 * a3 * c * b - 2 * a2 * c3 + 2 * a2 * c2 * b
							- 2 * a * c3 * b + 2 * a * c * b3 + c5 + 2 * c4 * b
							- 2 * c2 * b3 - c * b4,
					0);
		case 334:
			return a(0,
					a5 * c - a4 * c * b - 2 * a3 * c3 + 2 * a3 * c2 * b
							- 2 * a3 * c * b2 + 2 * a2 * c * b3 + a * c5
							- 2 * a * c4 * b + 2 * a * c3 * b2 - 2 * a * c2 * b3
							+ a * c * b4 + c5 * b - c * b5,
					0);
		case 335:
			return a(0, -2 * a18 * b2 * c4 + 3 * a16 * b4 * c4
					+ 3 * a16 * b2 * c6 + 7 * a14 * b6 * c4 - 16 * a14 * b4 * c6
					+ 3 * a14 * b2 * c8 - 10 * a12 * b8 * c4 + 5 * a12 * b6 * c6
					+ 15 * a12 * b4 * c8 - 8 * a12 * b2 * c10
					- 9 * a10 * b10 * c4 + 32 * a10 * b8 * c6
					- 46 * a10 * b6 * c8 + 20 * a10 * b4 * c10
					+ 3 * a10 * b2 * c12 + 12 * a8 * b12 * c4
					- 19 * a8 * b10 * c6 + 9 * a8 * b8 * c8 + 29 * a8 * b6 * c10
					- 37 * a8 * b4 * c12 + 6 * a8 * b2 * c14 + 5 * a6 * b14 * c4
					- 16 * a6 * b12 * c6 + 31 * a6 * b10 * c8
					- 56 * a6 * b8 * c10 + 35 * a6 * b6 * c12
					+ 8 * a6 * b4 * c14 - 7 * a6 * b2 * c16 - 6 * a4 * b16 * c4
					+ 11 * a4 * b14 * c6 - 19 * a4 * b12 * c8
					+ 26 * a4 * b10 * c10 + 8 * a4 * b8 * c12
					- 37 * a4 * b6 * c14 + 17 * a4 * b4 * c16 - a2 * b18 * c4
					+ 12 * a2 * b14 * c8 - 12 * a2 * b12 * c10
					- 18 * a2 * b10 * c12 + 24 * a2 * b8 * c14
					+ 4 * a2 * b6 * c16 - 12 * a2 * b4 * c18 + 3 * a2 * b2 * c20
					+ b20 * c4 - 5 * b16 * c8 + b14 * c10 + 9 * b12 * c12
					- 3 * b10 * c14 - 7 * b8 * c16 + 3 * b6 * c18 + 2 * b4 * c20
					- b2 * c10 * c12, 0);
		case 336:
			return a(0, a4 * b2 * c2 + a4 * c4 - 2 * a2 * c6 - b6 * c2 + b4 * c4
					- b2 * c6 + c8, 0);
		case 337:
			return a(0, a6 * c2 - 2 * a4 * c4 - a4 * c2 * b2 + a2 * c6
					+ a2 * c2 * b4 + c6 * b2 - c2 * b6, 0);
		case 338:
			return a(0, a2 * c - a * c2 - 2 * c3 + 3 * c2 * b - c * b2, 0);
		case 339:
			return a(0,
					-a10 * c2 + 4 * a8 * c4 + a8 * c2 * b2 - 6 * a6 * c6
							- 4 * a6 * c4 * b2 + 2 * a6 * c2 * b4 + 4 * a4 * c8
							+ 4 * a4 * c6 * b2 - 2 * a4 * c4 * b4
							- 2 * a4 * c2 * b6 - a2 * c10 - 2 * a2 * c6 * b4
							+ 4 * a2 * c4 * b6 - a2 * c2 * b8 - c10 * b2
							+ 2 * c8 * b4 - 2 * c4 * b8 + c2 * b10,
					0);
		case 340:
			return a(0,
					-a4 * b2 * c3 - a4 * b * c4 - a2 * b3 * c4 + a2 * b2 * c5
							+ 2 * a2 * b * c6 + a * b4 * c4 - a * b2 * c6
							+ b6 * c3 + b5 * c4 - b2 * c7 - b * c8,
					0);
		case 341:
			return a(0, S * c2 - c4 * r3 + c2 * r3 * b2 + c2 * r3 * a2, 0);
		case -341:
			return a(0, S * c2 + c4 * r3 - c2 * r3 * b2 - c2 * r3 * a2, 0);
		case 342:
			return a(0, 2 * a10 - 5 * a8 * b2 - 5 * a8 * c2 + 3 * a6 * b4
					+ 5 * a6 * b2 * c2 - a6 * b2 * r3 * S + 3 * a6 * c4
					- a6 * c2 * r3 * S + a4 * b6 + 2 * a4 * b4 * c2
					+ 2 * a4 * b4 * r3 * S + 2 * a4 * b2 * c4
					+ a4 * b2 * c2 * r3 * S + a4 * c6 + 2 * a4 * c4 * r3 * S
					- a2 * b8 - 2 * a2 * b6 * c2 - a2 * b6 * r3 * S
					+ 6 * a2 * b4 * c4 + a2 * b4 * c2 * r3 * S
					- 2 * a2 * b2 * c6 + a2 * b2 * c4 * r3 * S - a2 * c8
					- a2 * c6 * r3 * S, 0);
		case -342:
			return a(0, 2 * a10 - 5 * a8 * b2 - 5 * a8 * c2 + 3 * a6 * b4
					+ 5 * a6 * b2 * c2 + a6 * b2 * r3 * S + 3 * a6 * c4
					+ a6 * c2 * r3 * S + a4 * b6 + 2 * a4 * b4 * c2
					- 2 * a4 * b4 * r3 * S + 2 * a4 * b2 * c4
					- a4 * b2 * c2 * r3 * S + a4 * c6 - 2 * a4 * c4 * r3 * S
					- a2 * b8 - 2 * a2 * b6 * c2 + a2 * b6 * r3 * S
					+ 6 * a2 * b4 * c4 - a2 * b4 * c2 * r3 * S
					- 2 * a2 * b2 * c6 - a2 * b2 * c4 * r3 * S - a2 * c8
					+ a2 * c6 * r3 * S, 0);
		case 343:
			return a(0, a2 * b * c2 - b3 * c2 + b * c4, 0);
		case 344:
			return a(0,
					-a3 * b * c - a3 * c2 - a2 * b2 * c + a2 * c3 + a * b3 * c
							- a * b2 * c2 - a * b * c3 + a * c4 + b4 * c
							+ 2 * b3 * c2 - 2 * b * c4 - c5,
					0);
		case 345:
			return a(0, a * c + c * b, 0);
		case 346:
			return a(0, b2 * c6, 0);
		case 347:
			return a(0,
					9 * a8 - 24 * a6 * b2 + 18 * a4 * b4 + 20 * a4 * b2 * c2
							- 22 * a4 * c4 - 24 * a2 * b4 * c2
							+ 16 * a2 * b2 * c4 + 8 * a2 * c6 - 3 * b8
							+ 4 * b6 * c2 + 6 * b4 * c4 - 12 * b2 * c6 + 5 * c8,
					0);
		case 348:
			return a(0,
					3 * a10 - 8 * a8 * b2 + a8 * c2 + 6 * a6 * b4
							+ 4 * a6 * b2 * c2 - 6 * a6 * c4 - 10 * a4 * b4 * c2
							+ 12 * a4 * b2 * c4 - 2 * a4 * c6 - a2 * b8
							+ 4 * a2 * b6 * c2 - 2 * a2 * b4 * c4
							- 4 * a2 * b2 * c6 + 3 * a2 * c8 + b8 * c2
							- 4 * b6 * c4 + 6 * b4 * c6 - 4 * b2 * c8 + c10,
					0);
		case 349:
			return a(0,
					a6 * b2 * c4 - 2 * a4 * b4 * c4 - a4 * b2 * c6
							+ 2 * a2 * b6 * c4 - a2 * b4 * c6 - a2 * b2 * c8
							- b8 * c4 + 3 * b6 * c6 - 3 * b4 * c8 + b2 * c10,
					0);
		case 350:
			return a(0,
					a6 * c2 - 2 * a4 * c4 - 3 * a4 * c2 * b2 + a2 * c6
							+ 2 * a2 * c4 * b2 + 3 * a2 * c2 * b4 + c6 * b2
							- c2 * b6,
					0);
		case 351:
			return a(0, a * b * c2 - b2 * c2 + b * c3, 0);
		case 352:
			return a(0, -a2 * c2 + a * c2 * b + c4 - c3 * b,
					-2 * a3 * c + 2 * a3 * b + 2 * a * c3 - 2 * a * b3
							- 2 * c3 * b + 2 * c * b3);
		case 353:
			return a(0, -2 * a4 * c2 + a2 * c4 + 2 * c4 * b2 - c2 * b4, 0);
		case 354:
			return a(0, -a4 * c2 + c4 * b2, 0);
		case 355:
			return a(0, -a4 * b2 + a2 * b4 + b4 * c2 - b2 * c4, 0);
		case 356:
			return a(0, a4 * b2 - a2 * c4, 0);
		case 357:
			return a(0, -a4 * c2 + a2 * c4 + c4 * b2 - c2 * b4, 0);
		case 358:
			return a(0,
					2 * a4 * c2 - 2 * a2 * c4 - 3 * a2 * c2 * b2 - c4 * b2
							+ c2 * b4,
					4 * a4 * c2 - 4 * a4 * b2 - 4 * a2 * c4 + 4 * a2 * b4
							+ 4 * c4 * b2 - 4 * c2 * b4);
		case 359:
			return a(0,
					a3 * b2 * c2 - a2 * b3 * c2 - a2 * b2 * c3 + a * b2 * c4
							+ b3 * c4 - b2 * c5,
					-2 * a4 * b2 * c + 2 * a4 * b * c2 + 2 * a2 * b4 * c
							- 2 * a2 * b * c4 - 2 * a * b4 * c2
							+ 2 * a * b2 * c4);
		case 360:
			return a(0, -a * c2 + c3 + c2 * b, 2 * a2 * c - 2 * a2 * b
					- 2 * a * c2 + 2 * a * b2 + 2 * c2 * b - 2 * c * b2);
		case 361:
			return a(0, -a2 * b2 * c2 - a2 * c4 + b4 * c2 - 2 * b2 * c4 + c6,
					2 * a4 * b2 - 2 * a4 * c2 - 2 * a2 * b4 + 2 * a2 * c4
							+ 2 * b4 * c2 - 2 * b2 * c4);
		case 362:
			return a(0, a * b * c3 + b2 * c3, 0);
		case 363:
			return a(0,
					-3 * a6 + 9 * a4 * b2 + 4 * a4 * c2 - 9 * a2 * b4
							- 4 * a2 * b2 * c2 + a2 * c4 + 3 * b6 - b2 * c4
							- 2 * c6,
					0);
		case 364:
			return a(0, a10 * c2 - 3 * a8 * c4 - 2 * a8 * c2 * b2 + 2 * a6 * c6
					+ 2 * a6 * c4 * b2 + 2 * a6 * c2 * b4 + 2 * a4 * c8
					+ 2 * a4 * c4 * b4 - 4 * a4 * c2 * b6 - 3 * a2 * c10
					+ 2 * a2 * c8 * b2 + 2 * a2 * c6 * b4 - 6 * a2 * c4 * b6
					+ 5 * a2 * c2 * b8 + c12 - 2 * c10 * b2 + 2 * c8 * b4
					- 4 * c6 * b6 + 5 * c4 * b8 - 2 * c2 * b10, 0);
		case 365:
			return a(0, -a * c + c2 + c * b, 0);
		case 366:
			return a(0, a2 + a * b, 0);
		case 367:
			return s(0, a2 * b2 * c4 - b4 * c4, 0);
		case 368:
			return a(0, a2 * b2 * c4 + b4 * c4, 0);
		case 369:
			return a(b10 * c4 - 3 * b8 * c6 + 3 * b6 * c8 - b4 * c10,
					b12 * c2 - b12 * a2 + 4 * b10 * c4 - 8 * b10 * c2 * a2
							+ 4 * b10 * a4 - 9 * b8 * c6 + 3 * b8 * c4 * a2
							+ 12 * b8 * c2 * a4 - 6 * b8 * a6 + 2 * b6 * c8
							+ 22 * b6 * c6 * a2 - 26 * b6 * c4 * a4
							- 2 * b6 * c2 * a6 + 4 * b6 * a8 + 2 * b4 * c10
							- 16 * b4 * c8 * a2 + b4 * c6 * a4
							+ 19 * b4 * c4 * a6 - 5 * b4 * c2 * a8 - b4 * a10
							+ 8 * b2 * c8 * a4 - 8 * b2 * c6 * a6
							- 2 * b2 * c4 * a8 + 2 * b2 * c2 * a10 + c10 * a4
							- 3 * c8 * a6 + 3 * c6 * a8 - c4 * a10,
					-2 * b12 * c2 + 2 * b12 * a2 + 10 * b8 * c6
							- 12 * b8 * c4 * a2 + 12 * b8 * c2 * a4
							- 10 * b8 * a6 - 10 * b6 * c8 + 10 * b6 * a8
							+ 12 * b4 * c8 * a2 - 12 * b4 * c2 * a8
							+ 2 * b2 * c12 - 12 * b2 * c8 * a4
							+ 12 * b2 * c4 * a8 - 2 * b2 * a12 - 2 * c12 * a2
							+ 10 * c8 * a6 - 10 * c6 * a8 + 2 * c2 * a12);
		case 370:
			return a(0,
					-11 * a4 * b2 + 11 * a4 * c2 + 8 * a2 * b4
							+ 6 * a2 * b2 * c2 - 14 * a2 * c4 - 5 * b6
							+ 7 * b4 * c2 - 10 * b2 * c4 + 8 * c6,
					0);
		case 371:
			return a(0, 4 * a2 * b2 - 5 * a2 * c2 + 4 * b2 * c2, 0);
		case 372:
			return a(0, a2 * c + a * c2 - 2 * a * c * b + c2 * b - 3 * c * b2,
					0);
		case 373:
			return a(0,
					-a4 * b4 * c4 - a4 * b2 * c6 - 2 * a2 * b4 * c6
							+ 2 * a2 * b2 * c8 + b8 * c4 - 3 * b6 * c6
							+ 3 * b4 * c8 - b2 * c10,
					0);
		case 374:
			return a(0, -a12 * b2 * c4 + 3 * a10 * b2 * c6 + 3 * a8 * b6 * c4
					- 3 * a8 * b4 * c6 - 3 * a8 * b2 * c8 - a6 * b8 * c4
					- 3 * a6 * b6 * c6 + 3 * a6 * b4 * c8 + 2 * a6 * b2 * c10
					+ 3 * a4 * b8 * c6 - 6 * a4 * b6 * c8 + 6 * a4 * b4 * c10
					- 3 * a4 * b2 * c12 - 3 * a2 * b12 * c4 + 9 * a2 * b10 * c6
					- 12 * a2 * b8 * c8 + 12 * a2 * b6 * c10 - 9 * a2 * b4 * c12
					+ 3 * a2 * b2 * c14 + 2 * b14 * c4 - 9 * b12 * c6
					+ 15 * b10 * c8 - 10 * b8 * c10 + 3 * b4 * c14 - b2 * c16,
					0);
		case 375:
			return a(0,
					a8 * c2 - a6 * c4 - a6 * c2 * b2 - a4 * c6
							+ 4 * a4 * c4 * b2 - a4 * c2 * b4 + a2 * c8
							- 3 * a2 * c6 * b2 - 3 * a2 * c4 * b4 + a2 * c2 * b6
							- 2 * c8 * b2 + 4 * c6 * b4 - 2 * c4 * b6,
					0);
		case 376:
			return a(0, -a4 * c2 + 2 * a2 * c2 * b2 + c6 - c2 * b4,
					-4 * a4 * c2 + 4 * a4 * b2 + 4 * a2 * c4 - 4 * a2 * b4
							- 4 * c4 * b2 + 4 * c2 * b4);
		case 377:
			return a(0,
					-a8 * b2 + 2 * a6 * b4 - a6 * b2 * c2 + 2 * a4 * b4 * c2
							+ a4 * b2 * c4 - 2 * a2 * b8 + a2 * b6 * c2
							+ 2 * a2 * b4 * c4 - a2 * b2 * c6 + b10
							- 2 * b8 * c2 + 2 * b4 * c6 - b2 * c8,
					0);
		case 378:
			return a(0,
					a2 * b4 * c6 + a2 * b2 * c8 - b6 * c6 + 2 * b4 * c8
							- b2 * c10,
					2 * a6 * b4 * c2 - 2 * a6 * b2 * c4 - 2 * a4 * b6 * c2
							+ 2 * a4 * b2 * c6 + 2 * a2 * b6 * c4
							- 2 * a2 * b4 * c6);
		case 379:
			return a(0, a * b2 * c2 + 2 * a * b * c3 + a * c4 + b3 * c2
					+ b2 * c3 + b * c4 + c5, 0);
		case 380:
			return a(0, -a2 * b2 * c2 - a2 * c4 + b4 * c2 + c6, 0);
		case 381:
			return a(0, -a2 * c4 + c6, 2 * a4 * c2 - 2 * a4 * b2 - 2 * a2 * c4
					+ 2 * a2 * b4 + 2 * c4 * b2 - 2 * c2 * b4);
		case 382:
			return s(0,
					a6 * b * c2 - a6 * c3 - 2 * a5 * b2 * c2 + 2 * a5 * c4
							+ a4 * b3 * c2 + a4 * b2 * c3 - 2 * a4 * b * c4
							+ 2 * a3 * b2 * c4 - 2 * a3 * c6 - a2 * b5 * c2
							- 3 * a2 * b4 * c3 + 2 * a2 * b2 * c5 + a2 * b * c6
							+ a2 * c7 + 2 * a * b6 * c2 - 2 * a * b2 * c6
							- b7 * c2 + 3 * b6 * c3 - 2 * b5 * c4 - 2 * b4 * c5
							+ 3 * b3 * c6 - b2 * c7,
					a7 * b2 + a7 * c2 - 3 * a6 * b3 - 2 * a6 * b2 * c
							- 2 * a6 * b * c2 - 3 * a6 * c3 + 2 * a5 * b4
							+ 2 * a5 * b2 * c2 + 2 * a5 * c4 + 2 * a4 * b5
							+ 4 * a4 * b4 * c - a4 * b3 * c2 - a4 * b2 * c3
							+ 4 * a4 * b * c4 + 2 * a4 * c5 - 3 * a3 * b6
							- a3 * b4 * c2 - a3 * b2 * c4 - 3 * a3 * c6
							+ a2 * b7 - 2 * a2 * b6 * c + 2 * a2 * b5 * c2
							- a2 * b4 * c3 - a2 * b3 * c4 + 2 * a2 * b2 * c5
							- 2 * a2 * b * c6 + a2 * c7 - 2 * a * b6 * c2
							+ 4 * a * b4 * c4 - 2 * a * b2 * c6 + b7 * c2
							- 3 * b6 * c3 + 2 * b5 * c4 + 2 * b4 * c5
							- 3 * b3 * c6 + b2 * c7);
		case 383:
			return s(0,
					-a4 * b * c2 + a4 * c3 + a3 * b2 * c2 - a3 * c4
							- a2 * b3 * c2 + 2 * a2 * b * c4 - a2 * c5
							- a * b4 * c2 + a * c6 + 2 * b5 * c2 - b4 * c3
							- b3 * c4 + b2 * c5 - b * c6,
					-2 * a5 * b2 - 2 * a5 * c2 + 2 * a4 * b3 + 2 * a4 * c3
							+ 2 * a3 * b4 + 2 * a3 * c4 - 2 * a2 * b5
							- 2 * a2 * c5 - 2 * b5 * c2 + 2 * b4 * c3
							+ 2 * b3 * c4 - 2 * b2 * c5);
		case 384:
			return s(0, -a5 * b * c2 + a5 * c3 + a3 * b2 * c3 + a3 * b * c4
					- 2 * a3 * c5 + 2 * a2 * b4 * c2 - a2 * b3 * c3
					- 2 * a2 * b2 * c4 + a2 * b * c5 + a * b5 * c2
					- 2 * a * b4 * c3 - a * b3 * c4 + a * b2 * c5 + a * c7
					- 2 * b6 * c2 + b5 * c3 + 2 * b4 * c4 - b * c7,
					2 * a6 * b2 + 2 * a6 * c2 - 2 * a5 * b2 * c
							- 2 * a5 * b * c2 - 4 * a4 * b4 + 2 * a4 * b3 * c
							+ 2 * a4 * b * c3 - 4 * a4 * c4 + 2 * a3 * b4 * c
							+ 2 * a3 * b * c4 + 2 * a2 * b6 - 2 * a2 * b5 * c
							- 2 * a2 * b * c5 + 2 * a2 * c6 - 2 * a * b5 * c2
							+ 2 * a * b4 * c3 + 2 * a * b3 * c4
							- 2 * a * b2 * c5 + 2 * b6 * c2 - 4 * b4 * c4
							+ 2 * b2 * c6);
		case 385:
			return s(0, -a8 * b2 * c2 + a8 * c4 + 2 * a7 * b3 * c2 - 2 * a7 * c5
					+ a6 * b2 * c4 - a6 * c6 - 2 * a5 * b5 * c2
					- 2 * a5 * b4 * c3 + 4 * a5 * c7 + 2 * a4 * b6 * c2
					+ 2 * a4 * b5 * c3 - a4 * b4 * c4 - 2 * a4 * b3 * c5
					- a4 * c8 - 2 * a3 * b7 * c2 + 4 * a3 * b6 * c3
					+ 4 * a3 * b5 * c4 - 2 * a3 * b4 * c5 - 2 * a3 * b3 * c6
					- 2 * a3 * c9 - 4 * a2 * b7 * c3 - a2 * b6 * c4
					+ 4 * a2 * b5 * c5 - a2 * b4 * c6 + a2 * b2 * c8 + a2 * c10
					+ 2 * a * b9 * c2 - 2 * a * b8 * c3 - 4 * a * b7 * c4
					+ 4 * a * b6 * c5 + 2 * a * b5 * c6 - 2 * a * b4 * c7
					- b10 * c2 + 2 * b9 * c3 - 2 * b7 * c5 + 2 * b6 * c6
					- 2 * b5 * c7 + 2 * b3 * c9 - b2 * c10,
					a10 * b2 + a10 * c2 - 2 * a9 * b3 - 2 * a9 * b2 * c
							- 2 * a9 * b * c2 - 2 * a9 * c3 + 2 * a8 * b3 * c
							+ 2 * a8 * b * c3 + 2 * a7 * b5 + 6 * a7 * b4 * c
							+ 4 * a7 * b3 * c2 + 4 * a7 * b2 * c3
							+ 6 * a7 * b * c4 + 2 * a7 * c5 - 2 * a6 * b6
							- 6 * a6 * b5 * c - a6 * b4 * c2 - 4 * a6 * b3 * c3
							- a6 * b2 * c4 - 6 * a6 * b * c5 - 2 * a6 * c6
							+ 2 * a5 * b7 - 6 * a5 * b6 * c - 4 * a5 * b5 * c2
							- 4 * a5 * b2 * c5 - 6 * a5 * b * c6 + 2 * a5 * c7
							+ 6 * a4 * b7 * c - a4 * b6 * c2 + 6 * a4 * b4 * c4
							- a4 * b2 * c6 + 6 * a4 * b * c7 - 2 * a3 * b9
							+ 2 * a3 * b8 * c + 4 * a3 * b7 * c2
							- 4 * a3 * b6 * c3 - 4 * a3 * b3 * c6
							+ 4 * a3 * b2 * c7 + 2 * a3 * b * c8 - 2 * a3 * c9
							+ a2 * b10 - 2 * a2 * b9 * c + 4 * a2 * b7 * c3
							- a2 * b6 * c4 - 4 * a2 * b5 * c5 - a2 * b4 * c6
							+ 4 * a2 * b3 * c7 - 2 * a2 * b * c9 + a2 * c10
							- 2 * a * b9 * c2 + 2 * a * b8 * c3
							+ 6 * a * b7 * c4 - 6 * a * b6 * c5
							- 6 * a * b5 * c6 + 6 * a * b4 * c7
							+ 2 * a * b3 * c8 - 2 * a * b2 * c9 + b10 * c2
							- 2 * b9 * c3 + 2 * b7 * c5 - 2 * b6 * c6
							+ 2 * b5 * c7 - 2 * b3 * c9 + b2 * c10);
		case 386:
			return s(0, -a6 * b2 * c2 + a6 * c4 + 2 * a5 * b2 * c3
					- 2 * a5 * b * c4 + a4 * b4 * c2 - 2 * a4 * b3 * c3
					+ a4 * b2 * c4 + 2 * a4 * b * c5 - 2 * a4 * c6
					+ 2 * a3 * b4 * c3 - 4 * a3 * b2 * c5 + 2 * a3 * b * c6
					+ a2 * b6 * c2 + 2 * a2 * b5 * c3 - 3 * a2 * b4 * c4
					+ a2 * b2 * c6 - 2 * a2 * b * c7 + a2 * c8 - 4 * a * b6 * c3
					+ 2 * a * b5 * c4 + 2 * a * b4 * c5 - 2 * a * b3 * c6
					+ 2 * a * b2 * c7 - b8 * c2 + b6 * c4 + b4 * c6 - b2 * c8,
					a8 * b2 + a8 * c2 - a6 * b4 + 4 * a6 * b3 * c
							- 2 * a6 * b2 * c2 + 4 * a6 * b * c3 - a6 * c4
							- 4 * a5 * b4 * c - 4 * a5 * b * c4 - a4 * b6
							- 4 * a4 * b5 * c + 2 * a4 * b4 * c2
							+ 2 * a4 * b2 * c4 - 4 * a4 * b * c5 - a4 * c6
							+ 4 * a3 * b6 * c + 4 * a3 * b * c6 + a2 * b8
							- 2 * a2 * b6 * c2 + 2 * a2 * b4 * c4
							- 2 * a2 * b2 * c6 + a2 * c8 + 4 * a * b6 * c3
							- 4 * a * b5 * c4 - 4 * a * b4 * c5
							+ 4 * a * b3 * c6 + b8 * c2 - b6 * c4 - b4 * c6
							+ b2 * c8);
		case 387:
			return s(0,
					-a5 * b * c2 + a5 * c3 + a4 * b2 * c2 - a4 * c4
							- 4 * a3 * b2 * c3 + 5 * a3 * b * c4 - a3 * c5
							+ 4 * a2 * b3 * c3 - a2 * b2 * c4 - 4 * a2 * b * c5
							+ a2 * c6 + a * b5 * c2 - a * b4 * c3
							- 5 * a * b3 * c4
							+ 5 * a * b2
									* c5
							- b6 * c2 + 2 * b4 * c4 - b2 * c6,
					a6 * b2 + a6 * c2 - a5 * b2 * c - a5 * b * c2 - 2 * a4 * b4
							+ a4 * b3 * c + a4 * b * c3 - 2 * a4 * c4
							+ a3 * b4 * c + a3 * b * c4 + a2 * b6 - a2 * b5 * c
							- a2 * b * c5 + a2 * c6 - a * b5 * c2 + a * b4 * c3
							+ a * b3 * c4 - a * b2 * c5 + b6 * c2 - 2 * b4 * c4
							+ b2 * c6);
		case 388:
			return a(0, a12 * b2 * c4 - 2 * a10 * b4 * c4 - 3 * a10 * b2 * c6
					+ a8 * b6 * c4 + 2 * a8 * b4 * c6 + 3 * a8 * b2 * c8
					+ 2 * a6 * b6 * c6 - 2 * a6 * b2 * c10 - a4 * b10 * c4
					- 2 * a4 * b8 * c6 + 2 * a4 * b6 * c8 - 2 * a4 * b4 * c10
					+ 3 * a4 * b2 * c12 + 2 * a2 * b12 * c4 - 3 * a2 * b10 * c6
					- 2 * a2 * b6 * c10 + 6 * a2 * b4 * c12 - 3 * a2 * b2 * c14
					- b14 * c4 + 4 * b12 * c6 - 5 * b10 * c8 + 5 * b6 * c12
					- 4 * b4 * c14 + b2 * c16, 0);
		case 389:
			return a(0,
					-a6 * b2 * c4 - a4 * b4 * c4 + 3 * a4 * b2 * c6
							+ a2 * b6 * c4 - a2 * b2 * c8 + b8 * c4
							- 3 * b6 * c6 + 3 * b4 * c8 - b2 * c10,
					-4 * a6 * b4 * c2 + 4 * a6 * b2 * c4 + 4 * a4 * b6 * c2
							- 4 * a4 * b2 * c6 - 4 * a2 * b6 * c4
							+ 4 * a2 * b4 * c6);
		case 390:
			return a(0, a4 * b2 * c4 + a2 * b4 * c4 - 2 * a2 * b2 * c6 + b6 * c4
					- 2 * b4 * c6 + b2 * c8, 0);
		case 391:
			return a(0,
					-a3 * c2 - a2 * c2 * b + a * c4 + a * c2 * b2 + c4 * b
							+ c2 * b3,
					-a4 * c + a4 * b - a3 * c2 + a3 * b2 + a2 * c3 - a2 * b3
							+ a * c4 - a * b4 - c4 * b - c3 * b2 + c2 * b3
							+ c * b4);
		case 392:
			return s(0, a6 - a4 * b2 - 2 * a4 * c2 + 2 * a2 * b2 * c2 + a2 * c4
					- b2 * c4, 0);
		case 393:
			return s(0,
					a6 - 3 * a4 * b2 + 3 * a2 * b4 + 2 * a2 * b2 * c2
							- 3 * a2 * c4 - b6 - 2 * b4 * c2 + b2 * c4 + 2 * c6,
					0);
		case 394:
			return a(0, a4 + a2 * c2 - 3 * c4 + 2 * c2 * b2 - b4, 0);
		case 395:
			return a(0, 2 * a4 - 2 * a2 * b2 - a2 * c2 - 2 * c4, 0);
		case 396:
			return s(0,
					-a4 * c2 + 2 * a2 * c4 + 2 * a2 * c2 * b2 - c6 - c4 * b2
							- c2 * b4,
					2 * a6 - 2 * a4 * c2 - 2 * a4 * b2 - 2 * a2 * c4
							+ 6 * a2 * c2 * b2 - 2 * a2 * b4 + 2 * c6
							- 2 * c4 * b2 - 2 * c2 * b4 + 2 * b6);
		case 397:
			return s(0,
					a4 * c2 - 2 * a2 * c4 + a2 * c2 * b2 + c6 + c4 * b2
							- 2 * c2 * b4,
					-2 * a6 + 2 * a4 * c2 + 2 * a4 * b2 + 2 * a2 * c4
							- 6 * a2 * c2 * b2 + 2 * a2 * b4 - 2 * c6
							+ 2 * c4 * b2 + 2 * c2 * b4 - 2 * b6);
		case 398:
			return a(0,
					-a8 - 2 * a6 * b2 + 6 * a6 * c2 + 3 * a4 * b4 - 9 * a4 * c4
							+ 2 * a2 * b6 - 12 * a2 * b4 * c2
							+ 12 * a2 * b2 * c4 + 2 * a2 * c6 - 2 * b8
							+ 6 * b6 * c2 - 3 * b4 * c4 - 2 * b2 * c6,
					0);
		case 399:
			return a(0,
					-a4 - 2 * a3 * b + 6 * a3 * c + 3 * a2 * b2 - 9 * a2 * c2
							+ 2 * a * b3 - 12 * a * b2 * c + 12 * a * b * c2
							+ 2 * a * c3 - 2 * b4 + 6 * b3 * c - 3 * b2 * c2
							- 2 * b * c3,
					0);

		default:
			return new double[0];
		}
	}

	private double[] getCoeff400to499(int n, double a, double b, double c) {

		switch (n) {
		case 400:
			return a(0, -b2 + c2 - (S), 0);
		case -400:
			return a(0, -b2 + c2 + S, 0);
		case 401:
			return a(
					a10 * b2 - a10 * c2 - a8 * b4 + a8 * c4 - 2 * a6 * b6
							+ 6 * a6 * b4 * c2 - 6 * a6 * b2 * c4 + 2 * a6 * c6
							+ 2 * a4 * b8 - 4 * a4 * b6 * c2 + 4 * a4 * b2 * c6
							- 2 * a4 * c8 + a2 * b10 - 5 * a2 * b8 * c2
							+ 10 * a2 * b6 * c4 - 10 * a2 * b4 * c6
							+ 5 * a2 * b2 * c8 - a2 * c10 - b12
							+ 4 * b10
									* c2
							- 5 * b8 * c4 + 5 * b4 * c8 - 4 * b2 * c10 + c12,
					a12 + a10 * b2 - 6 * a10 * c2 - 4 * a8 * b4
							+ 5 * a8 * b2 * c2 + 7 * a8 * c4 - 2 * a6 * b6
							+ 16 * a6 * b4 * c2 - 18 * a6 * b2 * c4
							+ 4 * a6 * c6 + 5 * a4 * b8 - 14 * a4 * b6 * c2
							+ 4 * a4 * b4 * c4 + 14 * a4 * b2 * c6 - 9 * a4 * c8
							+ a2 * b10 - 10 * a2 * b8 * c2 + 22 * a2 * b6 * c4
							- 16 * a2 * b4 * c6 + a2 * b2 * c8 + 2 * a2 * c10
							- 2 * b12 + 9 * b10 * c2 - 15 * b8 * c4
							+ 10 * b6 * c6 - 3 * b2 * c10 + c12,
					8 * a10 * b2 - 8 * a10 * c2 - 16 * a8 * b4 + 16 * a8 * c4
							+ 40 * a6 * b4 * c2 - 40 * a6 * b2 * c4
							+ 16 * a4 * b8 - 40 * a4 * b6 * c2
							+ 40 * a4 * b2 * c6 - 16 * a4 * c8 - 8 * a2 * b10
							+ 40 * a2 * b6 * c4 - 40 * a2 * b4 * c6
							+ 8 * a2 * c10 + 8 * b10 * c2 - 16 * b8 * c4
							+ 16 * b4 * c8 - 8 * b2 * c10);
		case 402:
			return s(0, b4 * c6 - b2 * c8,
					2 * b4 * a4 * c2 + 2 * b4 * a2 * c4 + 2 * b2 * a4 * c4);
		case 403:
			return s(0, b2 * c2 - c4, -2 * b2 * c2 - 2 * b2 * a2 - 2 * c2 * a2);
		case 404:
			return a(0, -a10 * b2 * c2 + 3 * a8 * b4 * c2 + a8 * b2 * c4
					- 4 * a6 * b6 * c2 - a6 * b4 * c4 + 4 * a4 * b8 * c2
					- 3 * a4 * b6 * c4 + a4 * b2 * c8 - 3 * a2 * b10 * c2
					+ 7 * a2 * b8 * c4 - 6 * a2 * b6 * c6 + 3 * a2 * b4 * c8
					- a2 * b2 * c10 + b12 * c2 - 4 * b10 * c4 + 6 * b8 * c6
					- 4 * b6 * c8 + b4 * c10, 0);
		case 405:
			return a(0, 2 * a2 * b2 * c2 - a2 * c4 - 2 * b4 * c2 + b2 * c4 + c6,
					2 * a4 * b2 - 2 * a4 * c2 - 2 * a2 * b4 + 2 * a2 * c4
							+ 2 * b4 * c2 - 2 * b2 * c4);
		case 406:
			return s(0,
					a8 - 4 * a6 * b2 + 6 * a4 * b4 - 2 * a4 * c4 - 4 * a2 * b6
							+ 4 * a2 * b2 * c4 + b8 - 2 * b4 * c4 + c8,
					2 * a8 - 4 * a4 * b4 - 4 * a4 * c4 + 2 * b8 - 4 * b4 * c4
							+ 2 * c8);
		case 407:
			return s(0, -a2 * b * c2 + 2 * a * b * c3 + b3 * c2 - b * c4,
					2 * a3 * b * c - 4 * a2 * b2 * c - 4 * a2 * b * c2
							+ 2 * a * b3 * c - 4 * a * b2 * c2
							+ 2 * a * b * c3);
		case 408:
			return s(0, a2 - 5 * b2 + c2, 6 * a2 + 6 * b2 + 6 * c2);
		case 409:
			return s(0, c4, 2 * a2 * b2 + 2 * a2 * c2 + 2 * b2 * c2);
		case 410:
			return a(0, a2 * b2 * c2 - a2 * c4 + b2 * c4, 0);
		case 411:
			return a(0, a2 * b4 * c6 + b6 * c6 - b4 * c8, 0);
		case 412:
			return a(0,
					a6 - 3 * a4 * b2 - 4 * a4 * c2 + 3 * a2 * b4
							+ 6 * a2 * b2 * c2 + 3 * a2 * c4 - b6 - 2 * b4 * c2
							+ 3 * b2 * c4,
					4 * a4 * b2 - 4 * a4 * c2 - 4 * a2 * b4 + 4 * a2 * c4
							+ 4 * b4 * c2 - 4 * b2 * c4);
		case 413:
			return a(0, a10 * b2 - 5 * a8 * b4 - 3 * a8 * b2 * c2 + 10 * a6 * b6
					+ 4 * a6 * b4 * c2 + a6 * b2 * c4 - 10 * a4 * b8
					+ 6 * a4 * b6 * c2 + a4 * b4 * c4 + 3 * a4 * b2 * c6
					+ 5 * a2 * b10 - 12 * a2 * b8 * c2 + 7 * a2 * b6 * c4
					+ 2 * a2 * b4 * c6 - 2 * a2 * b2 * c8 - b12 + 5 * b10 * c2
					- 9 * b8 * c4 + 7 * b6 * c6 - 2 * b4 * c8, 0);
		case 414:
			return a(0,
					a3 * b * c2 + a2 * b2 * c2 - a2 * b * c3 - a * b3 * c2
							+ 2 * a * b2 * c3 - a * b * c4 - b4 * c2 - b3 * c3
							+ b2 * c4 + b * c5,
					0);
		case 415:
			return a(0,
					-a10 * c2 + 4 * a8 * c4 + 3 * a8 * c2 * b2 - 6 * a6 * c6
							- 6 * a6 * c4 * b2 - 4 * a6 * c2 * b4 + 4 * a4 * c8
							+ 2 * a4 * c6 * b2 + 2 * a4 * c4 * b4
							+ 4 * a4 * c2 * b6 - a2 * c10 + 2 * a2 * c8 * b2
							+ 2 * a2 * c4 * b6 - 3 * a2 * c2 * b8 - c10 * b2
							+ 2 * c8 * b4 - 2 * c4 * b8 + c2 * b10,
					0);
		case 416:
			return a(0,
					-a10 * c2 + 4 * a8 * c4 + 4 * a8 * c2 * b2 - 6 * a6 * c6
							- 8 * a6 * c4 * b2 - 7 * a6 * c2 * b4 + 4 * a4 * c8
							+ 3 * a4 * c6 * b2 + 4 * a4 * c4 * b4
							+ 7 * a4 * c2 * b6 - a2 * c10 + 2 * a2 * c8 * b2
							+ a2 * c6 * b4 + 2 * a2 * c4 * b6 - 4 * a2 * c2 * b8
							- c10 * b2 + 2 * c8 * b4 - 2 * c4 * b8 + c2 * b10,
					0);
		case 417:
			return a(-a2 * b4 * c2 + a2 * b2 * c4 + b6 * c2 - b2 * c6,
					a4 * b2 * c2 - 2 * a2 * b4 * c2 + a2 * b2 * c4 + b6 * c2
							- b4 * c4,
					0);
		case 418:
			return a(-a2 * b4 * c2 + a2 * b2 * c4 + b6 * c2 - b2 * c6,
					-2 * a4 * b2 * c2 + a2 * b4 * c2 + a2 * b2 * c4 + b6 * c2
							- 4 * b4 * c4 + 3 * b2 * c6,
					0);
		case 419:
			return a(0, -3 * S * a2 + 3 * S * b2 + a4 * r3 - 2 * a2 * b2 * r3
					+ a2 * r3 * c2 + b4 * r3 + b2 * r3 * c2 - 2 * r3 * c4, 0);
		case -419:
			return a(0, 3 * S * a2 - 3 * S * b2 + a4 * r3 - 2 * a2 * b2 * r3
					+ a2 * r3 * c2 + b4 * r3 + b2 * r3 * c2 - 2 * r3 * c4, 0);
		case 420:
			return a(0, -S * a2 + S * b2 + 2 * S * c2 + a4 * r3
					- 2 * a2 * b2 * r3 - a2 * c2 * r3 + b4 * r3 - b2 * c2 * r3,
					0);
		case -420:
			return a(0, S * a2 - S * b2 - 2 * S * c2 + a4 * r3
					- 2 * a2 * b2 * r3 - a2 * c2 * r3 + b4 * r3 - b2 * c2 * r3,
					0);
		case 421:
			return a(0,
					a2 * b4 + a2 * b2 * c2 + a2 * c4 + b4 * c2 + b2 * c4 + c6,
					0);
		case 422:
			return a(0,
					2 * a6 * b2 * c2 + a6 * c4 - a4 * b4 * c2 - 2 * a4 * c6
							- a2 * b4 * c4 + a2 * c8 - b8 * c2 - b4 * c6
							+ 2 * b2 * c8,
					0);
		case 423:
			return a(0, 2 * a2 * b2 * c2 + a2 * c4 + b4 * c2 + 2 * b2 * c4, 0);
		case 424:
			return a(0, 2 * b2 * c2 + c2 * S, 0);
		case -424:
			return a(0, 2 * b2 * c2 - c2 * S, 0);
		case 425:
			return a(0,
					-3 * a6 + 9 * a4 * b2 - 5 * a4 * c2 - 9 * a2 * b4
							- 2 * a2 * b2 * c2 + 11 * a2 * c4 + 3 * b6
							+ 7 * b4 * c2 - 7 * b2 * c4 - 3 * c6,
					16 * a4 * b2 - 16 * a4 * c2 - 16 * a2 * b4 + 16 * a2 * c4
							+ 16 * b4 * c2 - 16 * b2 * c4);
		case 426:
			return a(0,
					-a4 * c2 - 2 * a2 * c4 + 2 * a2 * c2 * b2 + 3 * c6
							- 2 * c4 * b2 - c2 * b4,
					-8 * a4 * c2 + 8 * a4 * b2 + 8 * a2 * c4 - 8 * a2 * b4
							- 8 * c4 * b2 + 8 * c2 * b4);
		case 427:
			return a(0,
					-2 * a6 + 6 * a4 * b2 - a4 * c2 - 6 * a2 * b4
							- 2 * a2 * b2 * c2 + 5 * a2 * c4 + 2 * b6
							+ 3 * b4 * c2 - 3 * b2 * c4 - 2 * c6,
					6 * a4 * b2 - 6 * a4 * c2 - 6 * a2 * b4 + 6 * a2 * c4
							+ 6 * b4 * c2 - 6 * b2 * c4);
		case 428:
			return a(0, -a8 * b4 * c4 + 2 * a6 * b6 * c4 - a6 * b4 * c6
					+ 2 * a4 * b6 * c6 + a4 * b4 * c8 - 2 * a2 * b10 * c4
					+ a2 * b8 * c6 + 2 * a2 * b6 * c8 - a2 * b4 * c10 + b12 * c4
					- 2 * b10 * c6 + 2 * b6 * c10 - b4 * c12, 0);
		case 429:
			return a(0, -a2 * c4 + c6 + c4 * b2, 2 * a4 * c2 - 2 * a4 * b2
					- 2 * a2 * c4 + 2 * a2 * b4 + 2 * c4 * b2 - 2 * c2 * b4);
		case 430:
			return a(0, a2 * b2 * c4 + a2 * b * c5 + a * b2 * c5 + a * b * c6
					- b4 * c4 + b2 * c6, 0);
		case 431:
			return a(0,
					-a5 * b2 * c4 - a5 * b * c5 - a4 * b3 * c4 - a4 * b2 * c5
							+ 2 * a3 * b2 * c6 + 2 * a3 * b * c7
							+ 2 * a2 * b3 * c6 + 2 * a2 * b2 * c7 + a * b6 * c4
							+ a * b5 * c5 - a * b2 * c8 - a * b * c9 + b7 * c4
							+ b6 * c5 - b3 * c8 - b2 * c9,
					0);
		case 432:
			return a(0, b5 * c4 - b * c6 * a2, 0);
		case 433:
			return a(0, -a4 * c4 + a2 * c6,
					-a6 * c2 + a6 * b2 + a2 * c6 - a2 * b6 - c6 * b2 + c2 * b6);
		case 434:
			return a(0,
					a4 * c2 + a3 * c2 * b - 2 * a2 * c4 - a2 * c3 * b
							+ a2 * c2 * b2 - a * c4 * b - a * c2 * b3 + c6
							+ c5 * b + c4 * b2 - c3 * b3 - 2 * c2 * b4,
					0);
		case 435:
			return a(0,
					a6 * c2 - 2 * a4 * c4 + 2 * a4 * c2 * b2 - a2 * c6
							- 2 * a2 * c2 * b4 + 2 * c8 - 5 * c6 * b2
							+ 8 * c4 * b4 - 3 * c2 * b6,
					0);
		case 436:
			return a(0, -a4 * b2 * c3 - a4 * b * c4 + 2 * a3 * b2 * c4
					- 2 * a2 * b3 * c4 + 2 * a2 * b * c6 + 2 * a * b4 * c4
					- 2 * a * b2 * c6 + b6 * c3 - b5 * c4 - 2 * b4 * c5
					+ 2 * b3 * c6 + b2 * c7 - b * c8, 0);
		case 437:
			return a(b4 * c2 - b2 * c4, -b4 * c2 - b2 * c4 - 2 * b2 * c2 * a2,
					0);
		case 438:
			return a(0, -S * a10 * c2 + 6 * S * a8 * c4 + 6 * S * a8 * c2 * b2
					- 8 * S * a6 * c6 + 4 * S * a6 * c4 * b2 + S * a6 * c2 * b4
					+ 4 * S * a4 * c8 - 11 * S * a4 * c6 * b2
					- 2 * S * a4 * c4 * b4 + S * a4 * c2 * b6 - 3 * S * a2 * c10
					- 14 * S * a2 * c8 * b2 + 13 * S * a2 * c6 * b4
					+ 10 * S * a2 * c4 * b6 - 6 * S * a2 * c2 * b8 + 2 * S * c12
					+ 9 * S * c10 * b2 - 38 * S * c8 * b4 + 40 * S * c6 * b6
					- 12 * S * c4 * b8 - S * c2 * b10 - a12 * c2 * r3
					+ 3 * a10 * c4 * r3 - a10 * c2 * b2 * r3 - 4 * a8 * c6 * r3
					+ 14 * a8 * c4 * b2 * r3 + 5 * a8 * c2 * b4 * r3
					+ 6 * a6 * c8 * r3 - 15 * a6 * c6 * b2 * r3
					- 3 * a6 * c4 * b4 * r3 - 2 * a6 * c2 * b6 * r3
					- 9 * a4 * c10 * r3 + 3 * a4 * c8 * b2 * r3
					+ 3 * a4 * c4 * b6 * r3 + a4 * c2 * b8 * r3
					+ 7 * a2 * c12 * r3 - 12 * a2 * c10 * b2 * r3
					- 15 * a2 * c8 * b4 * r3 + 27 * a2 * c6 * b6 * r3
					- 2 * a2 * c4 * b8 * r3 - 5 * a2 * c2 * b10 * r3
					- 2 * c14 * r3 + 11 * c12 * b2 * r3 - 15 * c10 * b4 * r3
					- 4 * c8 * b6 * r3 + 22 * c6 * b8 * r3 - 15 * c4 * b10 * r3
					+ 3 * c2 * b12 * r3, 0);
		case -438:
			return a(0, -S * a10 * c2 + 6 * S * a8 * c4 + 6 * S * a8 * c2 * b2
					- 8 * S * a6 * c6 + 4 * S * a6 * c4 * b2 + S * a6 * c2 * b4
					+ 4 * S * a4 * c8 - 11 * S * a4 * c6 * b2
					- 2 * S * a4 * c4 * b4 + S * a4 * c2 * b6 - 3 * S * a2 * c10
					- 14 * S * a2 * c8 * b2 + 13 * S * a2 * c6 * b4
					+ 10 * S * a2 * c4 * b6 - 6 * S * a2 * c2 * b8 + 2 * S * c12
					+ 9 * S * c10 * b2 - 38 * S * c8 * b4 + 40 * S * c6 * b6
					- 12 * S * c4 * b8 - S * c2 * b10 + a12 * c2 * r3
					- 3 * a10 * c4 * r3 + a10 * c2 * b2 * r3 + 4 * a8 * c6 * r3
					- 14 * a8 * c4 * b2 * r3 - 5 * a8 * c2 * b4 * r3
					- 6 * a6 * c8 * r3 + 15 * a6 * c6 * b2 * r3
					+ 3 * a6 * c4 * b4 * r3 + 2 * a6 * c2 * b6 * r3
					+ 9 * a4 * c10 * r3 - 3 * a4 * c8 * b2 * r3
					- 3 * a4 * c4 * b6 * r3 - a4 * c2 * b8 * r3
					- 7 * a2 * c12 * r3 + 12 * a2 * c10 * b2 * r3
					+ 15 * a2 * c8 * b4 * r3 - 27 * a2 * c6 * b6 * r3
					+ 2 * a2 * c4 * b8 * r3 + 5 * a2 * c2 * b10 * r3
					+ 2 * c14 * r3 - 11 * c12 * b2 * r3 + 15 * c10 * b4 * r3
					+ 4 * c8 * b6 * r3 - 22 * c6 * b8 * r3 + 15 * c4 * b10 * r3
					- 3 * c2 * b12 * r3, 0);
		case 439:
			return a(0,
					-a10 * c2 + 5 * a8 * c4 + 4 * a8 * c2 * b2 - 10 * a6 * c6
							- 7 * a6 * c4 * b2 - 6 * a6 * c2 * b4 + 10 * a4 * c8
							+ a4 * c4 * b4 + 4 * a4 * c2 * b6 - 5 * a2 * c10
							+ 5 * a2 * c8 * b2 - a2 * c6 * b4 + 2 * a2 * c4 * b6
							- a2 * c2 * b8 + c12 - 2 * c10 * b2 + 2 * c6 * b6
							- c4 * b8,
					0);
		case 440:
			return a(0, -a6 * c2 + 2 * a4 * c4 - a2 * c6 - c6 * b2 + c2 * b6,
					0);
		case 441:
			return a(0, -a6 * c2 + 3 * a4 * c4 + 2 * a4 * c2 * b2 - 3 * a2 * c6
					- a2 * c2 * b4 + c8 - c4 * b4, 0);
		case 442:
			return a(0, -a4 + a2 * c2 - 2 * c4 + c2 * b2 + b4, 0);
		case 443:
			return a(0, -a4 * c2 + c6 - 2 * c4 * b2 + c2 * b4,
					-4 * a4 * c2 + 4 * a4 * b2 + 4 * a2 * c4 - 4 * a2 * b4
							- 4 * c4 * b2 + 4 * c2 * b4);
		case 444:
			return a(0, -b2 * c4,
					b4 * c2 - b4 * a2 - b2 * c4 + b2 * a4 + c4 * a2 - c2 * a4);
		case 445:
			return a(0, -a10 * c2 + 3 * a8 * c4 + 5 * a8 * c2 * b2 - 2 * a6 * c6
					- 8 * a6 * c4 * b2 - 10 * a6 * c2 * b4 - 2 * a4 * c8
					+ 2 * a4 * c6 * b2 + 6 * a4 * c4 * b4 + 10 * a4 * c2 * b6
					+ 3 * a2 * c10 + 2 * a2 * c6 * b4 - 5 * a2 * c2 * b8 - c12
					+ c10 * b2 + 2 * c8 * b4 - 2 * c6 * b6 - c4 * b8 + c2 * b10,
					0);
		case 446:
			return a(0,
					a4 * c2 + a2 * c4 - 2 * a2 * c2 * b2 - 2 * c6 + c4 * b2
							+ c2 * b4,
					6 * a4 * c2 - 6 * a4 * b2 - 6 * a2 * c4 + 6 * a2 * b4
							+ 6 * c4 * b2 - 6 * c2 * b4);
		case 447:
			return a(0,
					2 * a4 * c2 - a2 * c4 - a2 * c2 * b2 - c6 + 2 * c4 * b2
							- c2 * b4,
					6 * a4 * c2 - 6 * a4 * b2 - 6 * a2 * c4 + 6 * a2 * b4
							+ 6 * c4 * b2 - 6 * c2 * b4);
		case 448:
			return a(0, a16 * c2 - 3 * a14 * c4 - 2 * a14 * c2 * b2
					+ 11 * a12 * c4 * b2 - a12 * c2 * b4 + 10 * a10 * c8
					- 21 * a10 * c6 * b2 + a10 * c2 * b6 - 15 * a8 * c10
					+ 10 * a8 * c8 * b2 + 30 * a8 * c6 * b4 - 30 * a8 * c4 * b6
					+ 10 * a8 * c2 * b8 + 9 * a6 * c12 + 14 * a6 * c10 * b2
					- 55 * a6 * c8 * b4 + 24 * a6 * c6 * b6 + 23 * a6 * c4 * b8
					- 16 * a6 * c2 * b10 - 2 * a4 * c14 - 15 * a4 * c12 * b2
					+ 21 * a4 * c10 * b4 + 31 * a4 * c8 * b6 - 51 * a4 * c6 * b8
					+ 9 * a4 * c4 * b10 + 7 * a4 * c2 * b12 + a2 * c14 * b2
					+ 12 * a2 * c12 * b4 - 33 * a2 * c10 * b6
					+ 16 * a2 * c8 * b8 + 15 * a2 * c6 * b10
					- 12 * a2 * c4 * b12 + a2 * c2 * b14 + 2 * c16 * b2
					- 7 * c14 * b4 + 6 * c12 * b6 + 5 * c10 * b8 - 10 * c8 * b10
					+ 3 * c6 * b12 + 2 * c4 * b14 - c2 * b16,
					2 * a16 * c2 - 2 * a16 * b2 - 8 * a14 * c4 + 8 * a14 * b4
							+ 12 * a12 * c6 + 14 * a12 * c4 * b2
							- 14 * a12 * c2 * b4 - 12 * a12 * b6 - 10 * a10 * c8
							- 20 * a10 * c6 * b2 + 20 * a10 * c2 * b6
							+ 10 * a10 * b8 + 10 * a8 * c10 + 20 * a8 * c6 * b4
							- 20 * a8 * c4 * b6 - 10 * a8 * b10 - 12 * a6 * c12
							+ 20 * a6 * c10 * b2 - 20 * a6 * c8 * b4
							+ 20 * a6 * c4 * b8 - 20 * a6 * c2 * b10
							+ 12 * a6 * b12 + 8 * a4 * c14 - 14 * a4 * c12 * b2
							+ 20 * a4 * c8 * b6 - 20 * a4 * c6 * b8
							+ 14 * a4 * c2 * b12 - 8 * a4 * b14 - 2 * a2 * c16
							+ 14 * a2 * c12 * b4 - 20 * a2 * c10 * b6
							+ 20 * a2 * c6 * b10 - 14 * a2 * c4 * b12
							+ 2 * a2 * b16 + 2 * c16 * b2 - 8 * c14 * b4
							+ 12 * c12 * b6 - 10 * c10 * b8 + 10 * c8 * b10
							- 12 * c6 * b12 + 8 * c4 * b14 - 2 * c2 * b16);
		case 449:
			return a(0,
					a6 - 3 * a4 * b2 + 2 * a4 * c2 + 3 * a2 * b4 + a2 * b2 * c2
							- 4 * a2 * c4 - b6 - 3 * b4 * c2 + 3 * b2 * c4 + c6,
					-6 * a4 * b2 + 6 * a4 * c2 + 6 * a2 * b4 - 6 * a2 * c4
							- 6 * b4 * c2 + 6 * b2 * c4);
		case 450:
			return a(0, -a16 + 4 * a14 * b2 + 3 * a14 * c2 - 6 * a12 * b4
					- 9 * a12 * b2 * c2 - 5 * a12 * c4 + 4 * a10 * b6
					+ 8 * a10 * b4 * c2 + 10 * a10 * b2 * c4 + 8 * a10 * c6
					- a8 * b6 * c2 - 4 * a8 * b4 * c4 - 11 * a8 * b2 * c6
					- 9 * a8 * c8 - 4 * a6 * b10 + a6 * b8 * c2
					- 2 * a6 * b6 * c4 + a6 * b4 * c6 + 8 * a6 * b2 * c8
					+ 7 * a6 * c10 + 6 * a4 * b12 - 5 * a4 * b10 * c2
					+ 3 * a4 * b8 * c4 - 4 * a4 * b6 * c6 + 3 * a4 * b4 * c8
					+ 2 * a4 * b2 * c10 - 7 * a4 * c12 - 4 * a2 * b14
					+ 4 * a2 * b12 * c2 - 2 * a2 * b10 * c4 + 7 * a2 * b8 * c6
					- 6 * a2 * b6 * c8 + 3 * a2 * b4 * c10 - 8 * a2 * b2 * c12
					+ 6 * a2 * c14 + b16 - b14 * c2 + b10 * c6 - 6 * b8 * c8
					+ 8 * b6 * c10 - 5 * b4 * c12 + 4 * b2 * c14 - 2 * c16, 0);
		case 451:
			return a(0,
					a12 - 5 * a10 * c2 + 16 * a8 * c4 - 3 * a8 * c2 * b2
							- 3 * a8 * b4 - 17 * a6 * c6 - 22 * a6 * c4 * b2
							+ 29 * a6 * c2 * b4 + a4 * c8 + 48 * a4 * c6 * b2
							- 27 * a4 * c4 * b4 - 20 * a4 * c2 * b6
							+ 3 * a4 * b8 + 4 * a2 * c10 - 18 * a2 * c8 * b2
							- 21 * a2 * c6 * b4 + 40 * a2 * c4 * b6
							- 6 * a2 * c2 * b8 - 4 * c10 * b2 + 17 * c8 * b4
							- 10 * c6 * b6 - 7 * c4 * b8 + 5 * c2 * b10 - b12,
					0);
		case 452:
			return a(0, -a4 + 5 * a2 * c2 - 6 * c4 + c2 * b2 + b4, 0);
		case 453:
			return a(0, a * c - 2 * c2 + c * b, 0);
		case 454:
			return a(0, 2 * a * b * c3 - b2 * c3 - b * c4, 0);
		case 455:
			return a(0, a2 + a * c + c2 - b2, 0);
		case 457:
			return s(0,
					a6 * b3 * c2 - a6 * b * c4 - 2 * a4 * b5 * c2
							+ 2 * a4 * b * c6 + a2 * b7 * c2 - a2 * b * c8
							+ b7 * c4 - 2 * b5 * c6 + b3 * c8,
					-2 * a7 * b3 * c - 2 * a7 * b * c3 + 4 * a5 * b5 * c
							+ 4 * a5 * b * c5 - 2 * a3 * b7 * c
							- 2 * a3 * b * c7 - 2 * a * b7 * c3
							+ 4 * a * b5 * c5 - 2 * a * b3 * c7);
		case 458:
			return a(-a8 * b6 + 3 * a8 * b4 * c2 - 3 * a8 * b2 * c4 + a8 * c6
					+ 4 * a6 * b8 - 8 * a6 * b6 * c2 + 8 * a6 * b2 * c6
					- 4 * a6 * c8 - 6 * a4 * b10 + 8 * a4 * b8 * c2
					+ 6 * a4 * b6 * c4 - 6 * a4 * b4 * c6 - 8 * a4 * b2 * c8
					+ 6 * a4 * c10 + 4 * a2 * b12 - 4 * a2 * b10 * c2
					- 4 * a2 * b8 * c4 + 4 * a2 * b4 * c8 + 4 * a2 * b2 * c10
					- 4 * a2 * c12 - b14 + b12 * c2 + 2 * b8 * c6 - 2 * b6 * c8
					- b2 * c12 + c14,
					-2 * a6 * b6 * c2 + 7 * a6 * b4 * c4 - 8 * a6 * b2 * c6
							+ 3 * a6 * c8 + 4 * a4 * b8 * c2 - 6 * a4 * b6 * c4
							- 9 * a4 * b4 * c6 + 20 * a4 * b2 * c8
							- 9 * a4 * c10 - 2 * a2 * b10 * c2
							+ 6 * a2 * b6 * c6 + 5 * a2 * b4 * c8
							- 18 * a2 * b2 * c10 + 9 * a2 * c12 + 2 * b10 * c4
							- 4 * b8 * c6 + 2 * b6 * c8 - 3 * b4 * c10
							+ 6 * b2 * c12 - 3 * c14,
					-2 * a10 * b4 + 2 * a10 * c4 + 6 * a8 * b6
							- 2 * a8 * b4 * c2 + 2 * a8 * b2 * c4 - 6 * a8 * c6
							- 6 * a6 * b8 + 6 * a6 * c8 + 2 * a4 * b10
							+ 2 * a4 * b8 * c2 - 2 * a4 * b2 * c8 - 2 * a4 * c10
							- 2 * a2 * b8 * c4 + 2 * a2 * b4 * c8 - 2 * b10 * c4
							+ 6 * b8 * c6 - 6 * b6 * c8 + 2 * b4 * c10);
		case 459:
			return s(
					a8 * b4 - 2 * a8 * b2 * c2 + a8 * c4 - 2 * a6 * b6
							+ 2 * a6 * b4 * c2 + 2 * a6 * b2 * c4 - 2 * a6 * c6
							+ a4 * b8 - 6 * a4 * b6 * c2 + 10 * a4 * b4 * c4
							- 6 * a4 * b2 * c6 + a4 * c8 + 6 * a2 * b8 * c2
							- 6 * a2 * b6 * c4 - 6 * a2 * b4 * c6
							+ 6 * a2 * b2 * c8 - 4 * b10 * c2 + 9 * b8 * c4
							- 10 * b6 * c6 + 9 * b4 * c8 - 4 * b2 * c10,
					-a8 * b4 + 2 * a8 * b2 * c2 - a8 * c4 + 2 * a6 * b6
							- 6 * a6 * b4 * c2 - 2 * a6 * b2 * c4 + 6 * a6 * c6
							- a4 * b8 + 10 * a4 * b6 * c2 - 2 * a4 * b4 * c4
							+ 2 * a4 * b2 * c6 - 9 * a4 * c8 - 6 * a2 * b8 * c2
							- 2 * a2 * b6 * c4 + 2 * a2 * b4 * c6
							+ 2 * a2 * b2 * c8 + 4 * a2 * c10 + 4 * b10 * c2
							- 9 * b8 * c4 + 14 * b6 * c6 - 9 * b4 * c8,
					-4 * a10 * b2 - 4 * a10 * c2 + 10 * a8 * b4
							+ 12 * a8 * b2 * c2 + 10 * a8 * c4 - 12 * a6 * b6
							- 12 * a6 * b4 * c2 - 12 * a6 * b2 * c4
							- 12 * a6 * c6 + 10 * a4 * b8 - 12 * a4 * b6 * c2
							+ 36 * a4 * b4 * c4 - 12 * a4 * b2 * c6
							+ 10 * a4 * c8 - 4 * a2 * b10 + 12 * a2 * b8 * c2
							- 12 * a2 * b6 * c4 - 12 * a2 * b4 * c6
							+ 12 * a2 * b2 * c8 - 4 * a2 * c10 - 4 * b10 * c2
							+ 10 * b8 * c4 - 12 * b6 * c6 + 10 * b4 * c8
							- 4 * b2 * c10);
		case 460:
			return s(
					-2 * a4 * b4 + 4 * a4 * b2 * c2 - 2 * a4 * c4 + 2 * a2 * b6
							- 2 * a2 * b4 * c2 - 2 * a2 * b2 * c4 + 2 * a2 * c6
							- b8 + 2 * b6 * c2 - 2 * b4 * c4 + 2 * b2 * c6 - c8,
					-2 * a6 * b2 + 2 * a6 * c2 + 4 * a4 * b4 - 2 * a4 * b2 * c2
							- 2 * a4 * c4 - 2 * a2 * b6 - 2 * a2 * b4 * c2
							+ 4 * a2 * b2 * c4 + b8 - 2 * b6 * c2 + 4 * b4 * c4
							- 4 * b2 * c6 + c8,
					-2 * a8 + 4 * a6 * b2 + 4 * a6 * c2 - 6 * a4 * b4
							- 6 * a4 * c4 + 4 * a2 * b6 + 4 * a2 * c6 - 2 * b8
							+ 4 * b6 * c2 - 6 * b4 * c4 + 4 * b2 * c6 - 2 * c8);
		case 461:
			return a(
					3 * a4 * b2 - 3 * a4 * c2 - 2 * a2 * b4 + 2 * a2 * c4 - b6
							+ 3 * b4 * c2 - 3 * b2 * c4 + c6,
					3 * a6 + 2 * a4 * b2 - 12 * a4 * c2 - a2 * b4
							+ 2 * a2 * b2 * c2 + 7 * a2 * c4 - 4 * b6
							+ 10 * b4 * c2 - 8 * b2 * c4 + 2 * c6,
					16 * a4 * b2 - 16 * a4 * c2 - 16 * a2 * b4 + 16 * a2 * c4
							+ 16 * b4 * c2 - 16 * b2 * c4);
		case 462:
			return s(
					a6 + 6 * a4 * b2 + 6 * a4 * c2 + 9 * a2 * b4
							- 18 * a2 * b2 * c2 + 9 * a2 * c4,
					6 * a6 + 21 * a4 * b2 + 15 * a4 * c2 + 12 * a2 * b4
							- 60 * a2 * b2 * c2 + 9 * b6 + 9 * b4 * c2
							- 45 * b2 * c4 + 27 * c6,
					27 * a6 + 21 * a4 * b2 + 21 * a4 * c2 + 21 * a2 * b4
							- 210 * a2 * b2 * c2 + 21 * a2 * c4 + 27 * b6
							+ 21 * b4 * c2 + 21 * b2 * c4 + 27 * c6);
		case 463:
			return a(-2 * b4 * c2 + 2 * b2 * c4,
					2 * b4 * c2 + b2 * c4 - 3 * b2 * c2 * a2 - 3 * c6
							+ 4 * c4 * a2 - c2 * a4,
					-4 * b4 * c2 + 4 * b4 * a2 + 4 * b2 * c4 - 4 * b2 * a4
							- 4 * c4 * a2 + 4 * c2 * a4);
		case 464:
			return a(0,
					a10 * c2 - 4 * a8 * c4 - 3 * a8 * c2 * b2 + 6 * a6 * c6
							+ 5 * a6 * c4 * b2 + 4 * a6 * c2 * b4 - 4 * a4 * c8
							- a4 * c6 * b2 - 4 * a4 * c2 * b6 + a2 * c10
							- 2 * a2 * c8 * b2 + a2 * c6 * b4 - 3 * a2 * c4 * b6
							+ 3 * a2 * c2 * b8 + c10 * b2 - 2 * c8 * b4
							+ 2 * c4 * b8 - c2 * b10,
					-2 * a6 * c4 * b2 + 2 * a6 * c2 * b4 + 2 * a4 * c6 * b2
							- 2 * a4 * c2 * b6 - 2 * a2 * c6 * b4
							+ 2 * a2 * c4 * b6);
		case 465:
			return a(0,
					-a10 * c2 + 4 * a8 * c4 + a8 * c2 * b2 - 6 * a6 * c6
							- 3 * a6 * c4 * b2 + 2 * a6 * c2 * b4 + 4 * a4 * c8
							+ a4 * c6 * b2 - 2 * a4 * c2 * b6 - a2 * c10
							+ 2 * a2 * c8 * b2 - a2 * c6 * b4 + a2 * c4 * b6
							- a2 * c2 * b8 - c10 * b2 + 2 * c8 * b4
							- 2 * c4 * b8 + c2 * b10,
					-2 * a6 * c4 * b2 + 2 * a6 * c2 * b4 + 2 * a4 * c6 * b2
							- 2 * a4 * c2 * b6 - 2 * a2 * c6 * b4
							+ 2 * a2 * c4 * b6);
		case 466:
			return a(0, a8 * b2 * c2 + a8 * c4 - 2 * a6 * b4 * c2
					- 2 * a6 * b2 * c4 - 4 * a6 * c6 + a4 * b4 * c4
					- a4 * b2 * c6 + 6 * a4 * c8 + 2 * a2 * b8 * c2
					- a2 * b6 * c4 + 3 * a2 * b2 * c8 - 4 * a2 * c10 - b10 * c2
					+ b8 * c4 + 2 * b6 * c6 - 2 * b4 * c8 - b2 * c10 + c12,
					2 * a6 * b4 * c2 - 2 * a6 * b2 * c4 - 2 * a4 * b6 * c2
							+ 2 * a4 * b2 * c6 + 2 * a2 * b6 * c4
							- 2 * a2 * b4 * c6);
		case 467:
			return a(0,
					a8 * b2 * c2 + a8 * c4 - 2 * a6 * b4 * c2 - 2 * a6 * b2 * c4
							- 4 * a6 * c6 + a4 * b4 * c4 - a4 * b2 * c6
							+ 6 * a4 * c8 + 2 * a2 * b8 * c2 - 3 * a2 * b6 * c4
							+ 5 * a2 * b2 * c8 - 4 * a2 * c10 - b10 * c2
							+ 3 * b8 * c4 - 4 * b6 * c6 + 4 * b4 * c8
							- 3 * b2 * c10 + c12,
					-2 * a6 * b4 * c2 + 2 * a6 * b2 * c4 + 2 * a4 * b6 * c2
							- 2 * a4 * b2 * c6 - 2 * a2 * b6 * c4
							+ 2 * a2 * b4 * c6);
		case 468:
			return a(0, 2 * a4 * b2 * c2 + a4 * c4 - a2 * b4 * c2 - 2 * a2 * c6
					- b6 * c2 + 2 * b4 * c4 - 2 * b2 * c6 + c8, 0);
		case 469:
			return s(0,
					-a8 * b2 * c2 + a8 * c4 + 2 * a6 * b4 * c2 + a6 * b2 * c4
							- 3 * a6 * c6 - 3 * a4 * b6 * c2 - 2 * a4 * b4 * c4
							+ 2 * a4 * b2 * c6 + 3 * a4 * c8 + 4 * a2 * b8 * c2
							- 6 * a2 * b6 * c4 + 6 * a2 * b4 * c6
							- 3 * a2 * b2 * c8 - a2 * c10 - 2 * b10 * c2
							+ 6 * b8 * c4 - 5 * b6 * c6 + b2 * c10,
					2 * a10 * b2 + 2 * a10 * c2 - 8 * a8 * b4 - 4 * a8 * b2 * c2
							- 8 * a8 * c4 + 12 * a6 * b6 + 2 * a6 * b4 * c2
							+ 2 * a6 * b2 * c4 + 12 * a6 * c6 - 8 * a4 * b8
							+ 2 * a4 * b6 * c2 + 2 * a4 * b2 * c6 - 8 * a4 * c8
							+ 2 * a2 * b10 - 4 * a2 * b8 * c2 + 2 * a2 * b6 * c4
							+ 2 * a2 * b4 * c6 - 4 * a2 * b2 * c8 + 2 * a2 * c10
							+ 2 * b10 * c2 - 8 * b8 * c4 + 12 * b6 * c6
							- 8 * b4 * c8 + 2 * b2 * c10);
		case 470:
			return s(0, a8 * b2 * c2 - a8 * c4 - 3 * a6 * b2 * c4 + 3 * a6 * c6
					- 5 * a4 * b6 * c2 + 6 * a4 * b4 * c4 + 2 * a4 * b2 * c6
					- 3 * a4 * c8 + 6 * a2 * b8 * c2 - 6 * a2 * b6 * c4
					- 2 * a2 * b4 * c6 + a2 * b2 * c8 + a2 * c10 - 2 * b10 * c2
					+ 4 * b8 * c4 - 3 * b6 * c6 + 2 * b4 * c8 - b2 * c10,
					2 * a10 * b2 + 2 * a10 * c2 - 8 * a8 * b4 - 4 * a8 * b2 * c2
							- 8 * a8 * c4 + 12 * a6 * b6 + 2 * a6 * b4 * c2
							+ 2 * a6 * b2 * c4 + 12 * a6 * c6 - 8 * a4 * b8
							+ 2 * a4 * b6 * c2 + 2 * a4 * b2 * c6 - 8 * a4 * c8
							+ 2 * a2 * b10 - 4 * a2 * b8 * c2 + 2 * a2 * b6 * c4
							+ 2 * a2 * b4 * c6 - 4 * a2 * b2 * c8 + 2 * a2 * c10
							+ 2 * b10 * c2 - 8 * b8 * c4 + 12 * b6 * c6
							- 8 * b4 * c8 + 2 * b2 * c10);
		case 471:
			return s(0, -a10 * b2 * c2 + a10 * c4 + 3 * a8 * b4 * c2
					+ a8 * b2 * c4 - 4 * a8 * c6 - 3 * a6 * b6 * c2
					- a6 * b4 * c4 - 2 * a6 * b2 * c6 + 6 * a6 * c8
					+ a4 * b8 * c2 - a4 * b6 * c4 + 4 * a4 * b2 * c8
					- 4 * a4 * c10 + 2 * a2 * b8 * c4 - a2 * b6 * c6
					- a2 * b4 * c8 - a2 * b2 * c10 + a2 * c12 - 2 * b10 * c4
					+ 7 * b8 * c6 - 9 * b6 * c8 + 5 * b4 * c10 - b2 * c12,
					4 * a10 * b2 * c2 - 10 * a8 * b4 * c2 - 10 * a8 * b2 * c4
							+ 12 * a6 * b6 * c2 + 4 * a6 * b4 * c4
							+ 12 * a6 * b2 * c6 - 10 * a4 * b8 * c2
							+ 4 * a4 * b6 * c4 + 4 * a4 * b4 * c6
							- 10 * a4 * b2 * c8 + 4 * a2 * b10 * c2
							- 10 * a2 * b8 * c4 + 12 * a2 * b6 * c6
							- 10 * a2 * b4 * c8 + 4 * a2 * b2 * c10);
		case 472:
			return a(0, a4 - 2 * a2 * b2 + a2 * c2 + b4 + b2 * c2 - 2 * c4, 0);
		case 473:
			return a(0,
					a6 * c2 - 3 * a4 * c4 + a4 * c2 * b2 + 2 * a2 * c6
							- a2 * c2 * b4 + c6 * b2 - c2 * b6,
					-a6 * c2 + a6 * b2 + a2 * c6 - a2 * b6 - c6 * b2 + c2 * b6);
		case 474:
			return a(0, a6 * c2 - 2 * a4 * c4 + a4 * c2 * b2 + a2 * c6
					- a2 * c2 * b4 + c6 * b2 - c2 * b6, 0);
		case 475:
			return a(0,
					-a8 * c2 + a6 * c4 + 2 * a6 * c2 * b2 + a4 * c6
							- 3 * a4 * c4 * b2 - a2 * c8 + 2 * a2 * c6 * b2
							+ a2 * c4 * b4 - 2 * a2 * c2 * b6 - c8 * b2
							- c6 * b4 + c4 * b6 + c2 * b8,
					0);
		case 476:
			return s(0,
					a6 - a4 * b2 - a4 * c2 - a2 * b4 + a2 * b2 * c2 - a2 * c4
							+ b6 + c6,
					2 * a6 - 2 * a4 * b2 - 2 * a4 * c2 - 2 * a2 * b4
							+ 6 * a2 * b2 * c2 - 2 * a2 * c4 + 2 * b6
							- 2 * b4 * c2 - 2 * b2 * c4 + 2 * c6);
		case 477:
			return a(0,
					-a8 + 2 * a6 * b2 + a6 * c2 - 2 * a4 * b2 * c2 + 2 * a4 * c4
							- 2 * a2 * b6 + a2 * b4 * c2 - a2 * c6 + b8 - c8,
					-2 * a6 * b2 + 2 * a6 * c2 + 2 * a2 * b6 - 2 * a2 * c6
							- 2 * b6 * c2 + 2 * b2 * c6);
		case 478:
			return a(0,
					a6 * c4 - a4 * c6 - 2 * a4 * c4 * b2 - a2 * c8
							+ 4 * a2 * c6 * b2 - a2 * c4 * b4 + c10
							- 2 * c8 * b2 - c6 * b4 + 2 * c4 * b6,
					0);
		case 479:
			return a(0,
					a8 - 2 * a6 * b2 + 3 * a4 * b2 * c2 - 2 * a4 * c4
							+ 2 * a2 * b6 - 2 * a2 * b4 * c2 - b8 - b6 * c2
							+ b2 * c6 + c8,
					0);
		case 480:
			return a(0,
					-a12 * c2 + 3 * a10 * c4 + 2 * a10 * c2 * b2 - 2 * a8 * c6
							- 6 * a8 * c4 * b2 - a8 * c2 * b4 - 2 * a6 * c8
							+ 10 * a6 * c6 * b2 - a6 * c4 * b4 + 3 * a4 * c10
							- 9 * a4 * c8 * b2 + 3 * a4 * c4 * b6 + a4 * c2 * b8
							- a2 * c12 + 4 * a2 * c10 * b2 - a2 * c8 * b4
							- 2 * a2 * c6 * b6 + 2 * a2 * c4 * b8
							- 2 * a2 * c2 * b10 - c12 * b2 + c10 * b4
							+ 2 * c8 * b6 - 2 * c6 * b8 - c4 * b10 + c2 * b12,
					2 * a8 * c4 * b2 - 2 * a8 * c2 * b4 - 2 * a4 * c8 * b2
							+ 2 * a4 * c2 * b8 + 2 * a2 * c8 * b4
							- 2 * a2 * c4 * b8);
		case 481:
			return a(0, a8 - 2 * a6 * b2 - 2 * a6 * c2 + a4 * b2 * c2 + a4 * c4
					+ 2 * a2 * b6 - a2 * c6 - b8 + b6 * c2 - b2 * c6 + c8,
					a6 * b2 - a6 * c2 - a2 * b6 + a2 * c6 + b6 * c2 - b2 * c6);
		case 482:
			return a(0,
					-a4 * b2 * c2 + a4 * c4 - a2 * b4 * c2 + 2 * a2 * b2 * c4
							- a2 * c6 + 2 * b4 * c4 - 3 * b2 * c6 + c8,
					a6 * b2 - a6 * c2 - a2 * b6 + a2 * c6 + b6 * c2 - b2 * c6);
		case 483:
			return a(0, -a6 * c2 + 2 * a4 * c4 + a4 * c2 * b2 - 2 * a2 * c6
					- 2 * a2 * c4 * b2 + a2 * c2 * b4 + c8 + c4 * b4 - c2 * b6,
					a6 * c2 - a6 * b2 - a2 * c6 + a2 * b6 + c6 * b2 - c2 * b6);
		case 484:
			return a(0,
					-a6 * c2 + a4 * c4 + 2 * a4 * c2 * b2 - a2 * c6
							- 4 * a2 * c4 * b2 + 2 * a2 * c2 * b4 + 3 * c6 * b2
							- c4 * b4 - c2 * b6,
					2 * a6 * c2 - 2 * a6 * b2 - 2 * a2 * c6 + 2 * a2 * b6
							+ 2 * c6 * b2 - 2 * c2 * b6);
		case 485:
			return a(0,
					2 * a4 - 4 * a2 * b2 - a2 * c2 + 2 * b4 - b2 * c2 - 4 * c4,
					0);
		case 486:
			return a(0, -a2 * c4 + c6 + 2 * c4 * b2, 2 * a4 * c2 - 2 * a4 * b2
					- 2 * a2 * c4 + 2 * a2 * b4 + 2 * c4 * b2 - 2 * c2 * b4);
		case 487:
			return a(0,
					2 * a6 * c2 - 5 * a4 * c4 - 2 * a4 * c2 * b2 + a2 * c6
							+ 3 * a2 * c4 * b2 - 2 * a2 * c2 * b4 + 2 * c8
							- 2 * c6 * b2 + c4 * b4 + 2 * c2 * b6,
					3 * a6 * c2 - 3 * a6 * b2 - 3 * a2 * c6 + 3 * a2 * b6
							+ 3 * c6 * b2 - 3 * c2 * b6);
		case 488:
			return a(0,
					-2 * a8 * c4 + 5 * a6 * c6 - 4 * a6 * c4 * b2 - 3 * a4 * c8
							+ 7 * a4 * c6 * b2 - 3 * a4 * c4 * b4 - a2 * c10
							+ a2 * c8 * b2 - 8 * a2 * c6 * b4 + 8 * a2 * c4 * b6
							+ c12 - 4 * c10 * b2 + 6 * c8 * b4 - 4 * c6 * b6
							+ c4 * b8,
					-3 * a10 * c2 + 3 * a10 * b2 + 6 * a8 * c4 - 6 * a8 * b4
							- 9 * a6 * c4 * b2 + 9 * a6 * c2 * b4 - 6 * a4 * c8
							+ 9 * a4 * c6 * b2 - 9 * a4 * c2 * b6 + 6 * a4 * b8
							+ 3 * a2 * c10 - 9 * a2 * c6 * b4 + 9 * a2 * c4 * b6
							- 3 * a2 * b10 - 3 * c10 * b2 + 6 * c8 * b4
							- 6 * c4 * b8 + 3 * c2 * b10);
		case 489:
			return a(0,
					-a6 * c2 + 2 * a4 * c4 + a4 * c2 * b2 - a2 * c6
							- 2 * a2 * c4 * b2 + a2 * c2 * b4 - c6 * b2
							+ 2 * c4 * b4 - c2 * b6,
					0);
		case 490:
			return a(0,
					a8 - 4 * a6 * b2 - a6 * c2 + 6 * a4 * b4 + 2 * a4 * b2 * c2
							- 4 * a2 * b6 - a2 * b4 * c2 + 2 * a2 * b2 * c4
							- a2 * c6 + b8 - 2 * b4 * c4 + c8,
					0);
		case 491:
			return a(0,
					-a8 + 4 * a6 * b2 + 2 * a6 * c2 - 6 * a4 * b4
							- 3 * a4 * b2 * c2 - 2 * a4 * c4 + 4 * a2 * b6
							+ 2 * a2 * c6 - b8 + b6 * c2 + b2 * c6 - c8,
					0);
		case 492:
			return a(0, -a4 + 2 * a2 * b2 + a2 * c2 - b4 - c4, 0);
		case 493:
			return a(0,
					-a6 * c4 + 2 * a4 * c6 + a4 * c4 * b2 - a2 * c8
							- a2 * c6 * b2,
					-a8 * c2 + a8 * b2 + 3 * a6 * c4 - 3 * a6 * b4 - 3 * a4 * c6
							+ 3 * a4 * b6 + a2 * c8 - a2 * b8 - c8 * b2
							+ 3 * c6 * b4 - 3 * c4 * b6 + c2 * b8);
		case 494:
			return a(0,
					a6 * c4 - 2 * a4 * c6 + a4 * c4 * b2 + a2 * c8
							- a2 * c6 * b2,
					-a8 * c2 + a8 * b2 + 3 * a6 * c4 - 3 * a6 * b4 - 3 * a4 * c6
							+ 3 * a4 * b6 + a2 * c8 - a2 * b8 - c8 * b2
							+ 3 * c6 * b4 - 3 * c4 * b6 + c2 * b8);
		case 495:
			return a(0, -a4 * c4 + 2 * a2 * c6 - a2 * c4 * b2 - c8 - c6 * b2
					+ 2 * c4 * b4, 0);
		case 496:
			return a(0,
					a6 * c2 - 3 * a4 * c2 * b2 - 3 * a2 * c6 + 2 * a2 * c4 * b2
							+ 3 * a2 * c2 * b4 + 2 * c8 + c6 * b2 - 2 * c4 * b4
							- c2 * b6,
					0);
		case 497:
			return a(0,
					-a6 * c2 + a4 * c4 + 3 * a4 * c2 * b2 + a2 * c6
							- a2 * c4 * b2 - 3 * a2 * c2 * b4 - c8 + c2 * b6,
					0);
		case 498:
			return a(0, -a2 * c3 + c5 - c4 * b + c3 * b2,
					a4 * c - a4 * b + a3 * c2 - a3 * b2 - a2 * c3 + a2 * b3
							- a * c4 + a * b4 + c4 * b + c3 * b2 - c2 * b3
							- c * b4);
		case 499:
			return a(0, a6 * c2 - a4 * c4 - a2 * c6 + a2 * c4 * b2 + c8
					- 3 * c6 * b2 + 3 * c4 * b4 - c2 * b6, 0);
		default:
			return new double[0];
		}
	}

	private double[] getCoeff500to549(int n, double a, double b, double c) {
		switch (n) {
		case 500:
			return a(0,
					-a8 * b2 * c4 + a6 * b4 * c4 + 2 * a6 * b2 * c6
							- 2 * a4 * b2 * c8 + a2 * b8 * c4 - 3 * a2 * b4 * c8
							+ 2 * a2 * b2 * c10 - b10 * c4 + 4 * b8 * c6
							- 6 * b6 * c8 + 4 * b4 * c10 - b2 * c12,
					0);
		case 501:
			return a(0,
					-a8 + 3 * a6 * b2 + 2 * a6 * c2 - 3 * a4 * b4
							- 4 * a4 * b2 * c2 + a2 * b6 + 3 * a2 * b4 * c2
							+ a2 * b2 * c4 - 2 * a2 * c6 - b6 * c2 + c8,
					0);
		case 502:
			return a(0,
					-a6 * c2 + a4 * c4 + a2 * c6 + 2 * a2 * c4 * b2
							+ 3 * a2 * c2 * b4 - c8 + 3 * c4 * b4 - 2 * c2 * b6,
					0);
		case 503:
			return a(0, a6 * c4 + a4 * c4 * b2 - 3 * a2 * c8 + 3 * a2 * c6 * b2
					- 2 * a2 * c4 * b4 + 2 * c10 - 4 * c8 * b2 + 2 * c6 * b4,
					a8 * c2 - a8 * b2 - 3 * a6 * c4 + 3 * a6 * b4 + 3 * a4 * c6
							- 3 * a4 * b6 - a2 * c8 + a2 * b8 + c8 * b2
							- 3 * c6 * b4 + 3 * c4 * b6 - c2 * b8);
		case 504:
			return a(0,
					-2 * a8 * c2 + 3 * a6 * c4 + 4 * a6 * c2 * b2
							- 3 * a4 * c4 * b2 - 2 * a4 * c2 * b4 - a2 * c8
							- a2 * c6 * b2 + 2 * a2 * c4 * b4,
					a8 * c2 - a8 * b2 - 3 * a6 * c4 + 3 * a6 * b4 + 3 * a4 * c6
							- 3 * a4 * b6 - a2 * c8 + a2 * b8 + c8 * b2
							- 3 * c6 * b4 + 3 * c4 * b6 - c2 * b8);
		case 505:
			return s(0, -a10 * b2 * c2 + a10 * c4 + 5 * a8 * b4 * c2
					- a8 * b2 * c4 - 4 * a8 * c6 - 7 * a6 * b6 * c2
					- a6 * b4 * c4 + 2 * a6 * b2 * c6 + 6 * a6 * c8
					+ a4 * b8 * c2 + 11 * a4 * b6 * c4 - 10 * a4 * b4 * c6
					+ 2 * a4 * b2 * c8 - 4 * a4 * c10 + 4 * a2 * b10 * c2
					- 14 * a2 * b8 * c4 + 11 * a2 * b6 * c6 - a2 * b4 * c8
					- a2 * b2 * c10 + a2 * c12 - 2 * b12 * c2 + 4 * b10 * c4
					+ b8 * c6 - 7 * b6 * c8 + 5 * b4 * c10 - b2 * c12,
					2 * a12 * b2 + 2 * a12 * c2 - 6 * a10 * b4
							- 4 * a10 * b2 * c2 - 6 * a10 * c4 + 4 * a8 * b6
							+ 8 * a8 * b4 * c2 + 8 * a8 * b2 * c4 + 4 * a8 * c6
							+ 4 * a6 * b8 - 12 * a6 * b6 * c2
							- 12 * a6 * b2 * c6 + 4 * a6 * c8 - 6 * a4 * b10
							+ 8 * a4 * b8 * c2 + 8 * a4 * b2 * c8 - 6 * a4 * c10
							+ 2 * a2 * b12 - 4 * a2 * b10 * c2
							+ 8 * a2 * b8 * c4 - 12 * a2 * b6 * c6
							+ 8 * a2 * b4 * c8 - 4 * a2 * b2 * c10
							+ 2 * a2 * c12 + 2 * b12 * c2 - 6 * b10 * c4
							+ 4 * b8 * c6 + 4 * b6 * c8 - 6 * b4 * c10
							+ 2 * b2 * c12);
		case 506:
			return s(0,
					-a5 * b2 * c2 + a5 * c4 + a4 * b3 * c2 + a4 * b2 * c3
							- a4 * b * c4 - a4 * c5 - a3 * b2 * c4
							+ 2 * a3 * b * c5 - a3 * c6 + 2 * a2 * b4 * c3
							- a2 * b3 * c4 - a2 * b2 * c5 - a2 * b * c6
							+ a2 * c7 + a * b6 * c2 - 4 * a * b5 * c3
							+ 2 * a * b4 * c4
							+ a * b2 * c6 - b7
									* c2
							+ b6 * c3 + b3 * c6 - b2 * c7,
					a7 * b2 + a7 * c2 - a6 * b3 - a6 * b2 * c - a6 * b * c2
							- a6 * c3 + 2 * a5 * b3 * c + 4 * a5 * b2 * c2
							+ 2 * a5 * b * c3 - 2 * a4 * b4 * c
							- 2 * a4 * b3 * c2 - 2 * a4 * b2 * c3
							- 2 * a4 * b * c4 - a3 * b6 + 2 * a3 * b5 * c
							- 2 * a3 * b4 * c2 - 2 * a3 * b2 * c4
							+ 2 * a3 * b * c5 - a3 * c6 + a2 * b7 - a2 * b6 * c
							+ 4 * a2 * b5 * c2 - 2 * a2 * b4 * c3
							- 2 * a2 * b3 * c4 + 4 * a2 * b2 * c5 - a2 * b * c6
							+ a2 * c7 - a * b6 * c2 + 2 * a * b5 * c3
							- 2 * a * b4 * c4 + 2 * a * b3 * c5 - a * b2 * c6
							+ b7 * c2 - b6 * c3 - b3 * c6 + b2 * c7);
		case 507:
			return s(0,
					-a5 * b3 * c4 + a5 * c7 + a4 * b5 * c3 - a4 * b3 * c5
							+ a3 * b5 * c4 - a3 * b3 * c6 + a2 * b5 * c5
							- a2 * b3 * c7 - a * b8 * c3 + a * b5 * c6,
					a8 * b3 * c + a8 * b * c3 - a7 * b5 + a7 * b3 * c2
							+ a7 * b2 * c3 - a7 * c5 - a6 * b5 * c
							+ 2 * a6 * b3 * c3 - a6 * b * c5 - a5 * b7
							- a5 * b6 * c - 2 * a5 * b5 * c2 - 2 * a5 * b2 * c5
							- a5 * b * c6 - a5 * c7 + a3 * b8 * c + a3 * b7 * c2
							+ 2 * a3 * b6 * c3 + 2 * a3 * b3 * c6 + a3 * b2 * c7
							+ a3 * b * c8 + a2 * b7 * c3 - 2 * a2 * b5 * c5
							+ a2 * b3 * c7 + a * b8 * c3 - a * b6 * c5
							- a * b5 * c6 + a * b3 * c8 - b7 * c5 - b5 * c7);
		case 508:
			return a(
					a6 * b4 - a6 * c4 - 3 * a4 * b6 - a4 * b4 * c2
							+ a4 * b2 * c4 + 3 * a4 * c6 + 3 * a2 * b8
							+ a2 * b6 * c2
							- a2 * b2
									* c6
							- 3 * a2 * c8 - b10 - b6 * c4 + b4 * c6 + c10,
					a8 * b2 - 2 * a6 * b4 - 2 * a6 * b2 * c2 + a6 * c4
							+ 5 * a4 * b4 * c2 + 2 * a4 * b2 * c4 - 4 * a4 * c6
							+ 2 * a2 * b8 - 6 * a2 * b6 * c2 + 2 * a2 * b4 * c4
							- 4 * a2 * b2 * c6 + 5 * a2 * c8 - b10 + 3 * b8 * c2
							- 3 * b6 * c4 + 2 * b4 * c6 + b2 * c8 - 2 * c10,
					-3 * a8 * b2 + 3 * a8 * c2 + 9 * a6 * b4 - 9 * a6 * c4
							- 9 * a4 * b6 + 9 * a4 * c6 + 3 * a2 * b8
							- 3 * a2 * c8 - 3 * b8 * c2 + 9 * b6 * c4
							- 9 * b4 * c6 + 3 * b2 * c8);
		case 509:
			return a(a4 * b2 - a4 * c2 - 2 * a2 * b4 + 2 * a2 * c4 + b6 - c6,
					a6 - 3 * a4 * b2 - a4 * c2 + 2 * a2 * b4 + 6 * a2 * b2 * c2
							- 3 * a2 * c4 - 3 * b4 * c2 - b2 * c4 + 2 * c6,
					6 * a4 * b2 - 6 * a4 * c2 - 6 * a2 * b4 + 6 * a2 * c4
							+ 6 * b4 * c2 - 6 * b2 * c4);
		case 510:
			return a(0,
					-a5 * c + 2 * a4 * c2 + a4 * c * b + 2 * a3 * c3
							- 5 * a3 * c2 * b - 4 * a2 * c4 + 3 * a2 * c3 * b
							+ 6 * a2 * c2 * b2 - 4 * a2 * c * b3 - a * c5
							+ 4 * a * c4 * b - 9 * a * c3 * b2 + 5 * a * c2 * b3
							+ a * c * b4 + 2 * c6 - 5 * c5 * b + 6 * c4 * b2
							+ 2 * c3 * b3 - 8 * c2 * b4 + 3 * c * b5,
					0);
		case 511:
			return a(-a2 * b4 * c2 + a2 * b2 * c4 + b6 * c2 - b2 * c6,
					a4 * b2 * c2 - a2 * b4 * c2 + b4 * c4 - b2 * c6, 0);
		case 512:
			return a(0, a2 * b2 * c2,
					a4 * b2 - a4 * c2 - a2 * b4 + a2 * c4 + b4 * c2 - b2 * c4);
		case 513:
			return a(0,
					-a8 * b2 * c4 + 3 * a6 * b4 * c4 + 2 * a6 * b2 * c6
							- 2 * a4 * b6 * c4 - a4 * b4 * c6 - a4 * b2 * c8
							- a2 * b8 * c4 + a2 * b6 * c6 + b10 * c4
							- 3 * b8 * c6 + 3 * b6 * c8 - b4 * c10,
					0);
		case 514:
			return a(0,
					-a8 * c2 + 3 * a6 * c4 + a6 * c2 * b2 - 3 * a4 * c6
							+ 4 * a4 * c2 * b4 + a2 * c8 + 2 * a2 * c4 * b4
							- 7 * a2 * c2 * b6 - 3 * c8 * b2 + 7 * c6 * b4
							- 7 * c4 * b6 + 3 * c2 * b8,
					-2 * a8 * c2 + 2 * a8 * b2 + 6 * a6 * c4 - 6 * a6 * b4
							- 6 * a4 * c6 + 6 * a4 * b6 + 2 * a2 * c8
							- 2 * a2 * b8 - 2 * c8 * b2 + 6 * c6 * b4
							- 6 * c4 * b6 + 2 * c2 * b8);
		case 515:
			return a(0, a12 - 6 * a10 * b2 - 3 * a10 * c2 + 15 * a8 * b4
					+ 6 * a8 * b2 * c2 - 20 * a6 * b6 - 3 * a6 * b4 * c2
					- 6 * a6 * b2 * c4 + 10 * a6 * c6 + 15 * a4 * b8
					+ 3 * a4 * b6 * c2 - 12 * a4 * b4 * c4 + 15 * a4 * b2 * c6
					- 15 * a4 * c8 - 6 * a2 * b10 - 6 * a2 * b8 * c2
					+ 24 * a2 * b6 * c4 - 15 * a2 * b4 * c6 - 6 * a2 * b2 * c8
					+ 9 * a2 * c10 + b12 + 3 * b10 * c2 - 6 * b8 * c4
					- 8 * b6 * c6 + 15 * b4 * c8 - 3 * b2 * c10 - 2 * c12, 0);
		case 516:
			return a(0, -a6 * b2 * c2 + a4 * b4 * c2 + 2 * a2 * b4 * c4
					+ a2 * b2 * c6 - b6 * c4 + b4 * c6, 0);
		case 517:
			return a(0, a4 - a2 * c2 - c2 * b2 - b4, 0);
		case 518:
			return a(0, -a6 + 3 * a4 * b2 - 3 * a2 * b4 + a2 * c4 + b6
					- 4 * b4 * c2 + 3 * b2 * c4, 0);
		case 519:
			return a(0, -a8 * c4 + 4 * a6 * c6 - 2 * a6 * c2 * b4 - 6 * a4 * c8
					+ 2 * a4 * c6 * b2 - 2 * a4 * c4 * b4 + 6 * a4 * c2 * b6
					+ 4 * a2 * c10 - 4 * a2 * c8 * b2 - 2 * a2 * c6 * b4
					+ 8 * a2 * c4 * b6 - 6 * a2 * c2 * b8 - c12 + 2 * c10 * b2
					- 2 * c8 * b4 + 4 * c6 * b6 - 5 * c4 * b8 + 2 * c2 * b10,
					0);
		case 520:
			return a(0,
					-2 * a8 * b2 * c2 - a8 * c4 + 4 * a6 * b4 * c2 + 4 * a6 * c6
							- 2 * a4 * b4 * c4 + 8 * a4 * b2 * c6 - 6 * a4 * c8
							- 4 * a2 * b8 * c2 + 8 * a2 * b6 * c4
							- 8 * a2 * b2 * c8 + 4 * a2 * c10 + 2 * b10 * c2
							- 5 * b8 * c4 + 4 * b6 * c6 - 2 * b4 * c8
							+ 2 * b2 * c10 - c12,
					0);
		case 521:
			return a(0,
					a6 - 3 * a4 * b2 - a4 * c2 + 8 * a3 * b * c2 + 3 * a2 * b4
							- 2 * a2 * b2 * c2 - a2 * c4 - 8 * a * b3 * c2
							+ 16 * a * b2 * c3 - 8 * a * b * c4 - b6
							+ 3 * b4 * c2 - 3 * b2 * c4 + c6,
					0);
		case 522:
			return a(0,
					-a6 + 3 * a4 * b2 + 3 * a4 * c2 - 3 * a2 * b4
							+ 6 * a2 * b2 * c2 - 3 * a2 * c4 + b6 - 9 * b4 * c2
							+ 7 * b2 * c4 + c6,
					0);
		case 523:
			return a(0, -4 * a6 * b2 * c4 - 3 * a4 * b4 * c4 + 7 * a4 * b2 * c6
					+ 6 * a2 * b6 * c4 - 4 * a2 * b4 * c6 - 2 * a2 * b2 * c8
					+ b8 * c4 - 3 * b6 * c6 + 3 * b4 * c8 - b2 * c10, 0);
		case 524:
			return a(0,
					a6 * c2 - a4 * c4 + 2 * a4 * c2 * b2 - a2 * c6
							- a2 * c2 * b4 + c8 - c6 * b2 + 2 * c4 * b4
							- 2 * c2 * b6,
					0);
		case 525:
			return a(0,
					-2 * a4 * c2 + 2 * a2 * c4 + a2 * c2 * b2 - c4 * b2
							+ c2 * b4,
					-4 * a4 * c2 + 4 * a4 * b2 + 4 * a2 * c4 - 4 * a2 * b4
							- 4 * c4 * b2 + 4 * c2 * b4);
		case 526:
			return a(0,
					a10 * c2 - 3 * a8 * c4 - 2 * a8 * c2 * b2 + 3 * a6 * c6
							+ 2 * a6 * c4 * b2 - a4 * c8 + a4 * c6 * b2
							+ a4 * c4 * b4 + 2 * a4 * c2 * b6 - a2 * c8 * b2
							+ 2 * a2 * c6 * b4 - a2 * c2 * b8,
					a10 * c2 - a10 * b2 - 2 * a8 * c4 + 2 * a8 * b4
							+ 3 * a6 * c4 * b2 - 3 * a6 * c2 * b4 + 2 * a4 * c8
							- 3 * a4 * c6 * b2 + 3 * a4 * c2 * b6 - 2 * a4 * b8
							- a2 * c10 + 3 * a2 * c6 * b4 - 3 * a2 * c4 * b6
							+ a2 * b10 + c10 * b2 - 2 * c8 * b4 + 2 * c4 * b8
							- c2 * b10);
		case 527:
			return a(0, -a6 * c2 + 2 * a4 * c4 - a2 * c6 + 2 * a2 * c4 * b2
					+ a2 * c2 * b4, 0);
		case 528:
			return a(0, a14 * b2 * c2 + a14 * c4 - a12 * b4 * c2 - 5 * a12 * c6
					- 9 * a10 * b6 * c2 + 6 * a10 * b4 * c4 + 9 * a10 * c8
					+ 25 * a8 * b8 * c2 - 20 * a8 * b6 * c4 + 18 * a8 * b4 * c6
					- 20 * a8 * b2 * c8 - 5 * a8 * c10 - 25 * a6 * b10 * c2
					+ 9 * a6 * b8 * c4 + 28 * a6 * b6 * c6 - 52 * a6 * b4 * c8
					+ 45 * a6 * b2 * c10 - 5 * a6 * c12 + 9 * a4 * b12 * c2
					+ 12 * a4 * b10 * c4 - 51 * a4 * b8 * c6 + 36 * a4 * b6 * c8
					+ 21 * a4 * b4 * c10 - 36 * a4 * b2 * c12 + 9 * a4 * c14
					+ a2 * b14 * c2 - 8 * a2 * b12 * c4 + 43 * a2 * b8 * c8
					- 59 * a2 * b6 * c10 + 18 * a2 * b4 * c12
					+ 10 * a2 * b2 * c14 - 5 * a2 * c16 - b16 * c2
					+ 10 * b12 * c6 - 16 * b10 * c8 + 16 * b6 * c12
					- 10 * b4 * c14 + c18, 0);
		case 529:
			return a(0,
					a5 * c - a4 * c2 - a4 * c * b - a3 * c3 + 3 * a3 * c2 * b
							- 2 * a3 * c * b2 + a2 * c4 - 2 * a2 * c3 * b
							+ 2 * a2 * c * b3 - a * c4 * b + 3 * a * c3 * b2
							- 3 * a * c2 * b3 + a * c * b4 + c5 * b - c4 * b2
							+ c2 * b4 - c * b5,
					a5 * c - a5 * b - 2 * a4 * c2 + 2 * a4 * b2
							+ 5 * a3 * c2 * b - 5 * a3 * c * b2 + 2 * a2 * c4
							- 5 * a2 * c3 * b + 5 * a2 * c * b3 - 2 * a2 * b4
							- a * c5 + 5 * a * c3 * b2 - 5 * a * c2 * b3
							+ a * b5 + c5 * b - 2 * c4 * b2 + 2 * c2 * b4
							- c * b5);
		case 530:
			return a(0,
					a6 - 3 * a4 * b2 + a4 * c2 + 3 * a2 * b4 + a2 * b2 * c2
							- 3 * a2 * c4 - b6 - 2 * b4 * c2 + 2 * b2 * c4 + c6,
					-4 * a4 * b2 + 4 * a4 * c2 + 4 * a2 * b4 - 4 * a2 * c4
							- 4 * b4 * c2 + 4 * b2 * c4);
		case 531:
			return a(0, -a4 * c2 + c6 - c4 * b2 + c2 * b4, 0);
		case 532:
			return a(0, -a4 * b2 * c6 + b4 * c8, 0);
		case 533:
			return a(0, -a6 + a4 * b2 + a4 * c2 + a2 * b4 - a2 * b2 * c2
					+ a2 * c4 - b6 - c6, 0);
		case 534:
			return a(0,
					-a4 * c2 + a2 * c2 * b2 + c6 - 3 * c4 * b2 + 2 * c2 * b4,
					0);
		case 535:
			return a(0,
					-a4 * c2 + 3 * a2 * c2 * b2 + c6 - c4 * b2 - 2 * c2 * b4,
					0);
		case 536:
			return a(0,
					a6 * c2 - 3 * a4 * c4 + 4 * a4 * c2 * b2 - a2 * c6
							- a2 * c2 * b4 + 3 * c8 - 2 * c6 * b2 + 3 * c4 * b4
							- 4 * c2 * b6,
					-4 * a6 * c2 + 4 * a6 * b2 + 4 * a2 * c6 - 4 * a2 * b6
							- 4 * c6 * b2 + 4 * c2 * b6);
		case 537:
			return a(0,
					3 * a6 * c2 - a4 * c4 - 2 * a4 * c2 * b2 - 3 * a2 * c6
							+ 3 * a2 * c2 * b4 + c8 + 4 * c6 * b2 - c4 * b4
							- 4 * c2 * b6,
					4 * a6 * c2 - 4 * a6 * b2 - 4 * a2 * c6 + 4 * a2 * b6
							+ 4 * c6 * b2 - 4 * c2 * b6);
		case 538:
			return a(0, a4 * c2 + a2 * c2 * b2 - c6 + c4 * b2, 0);
		case 539:
			return a(0, -a4 * c2 + a2 * c2 * b2 + c6 + c4 * b2, 0);
		case 540:
			return s(0,
					a10 - a8 * c2 - 3 * a6 * b4 - a4 * c6 - a4 * c4 * b2
							+ 2 * a4 * c2 * b4 + 5 * a4 * b6 + a2 * c8
							+ 2 * a2 * c6 * b2 - a2 * c4 * b4 + 2 * a2 * c2 * b6
							- 6 * a2 * b8 - c8 * b2 - c6 * b4 + 2 * c4 * b6
							- 3 * c2 * b8 + 3 * b10,
					0);
		case 541:
			return s(0, a14 * c2 - 4 * a12 * c4 + 3 * a12 * c2 * b2
					+ 5 * a10 * c6 + 5 * a10 * c4 * b2 - 13 * a10 * c2 * b4
					- 30 * a8 * c6 * b2 + 30 * a8 * c4 * b4 + 5 * a8 * c2 * b6
					- 5 * a6 * c10 + 29 * a6 * c8 * b2 + 5 * a6 * c6 * b4
					- 46 * a6 * c4 * b6 + 15 * a6 * c2 * b8 + 4 * a4 * c12
					- 2 * a4 * c10 * b2 - 46 * a4 * c8 * b4 + 59 * a4 * c6 * b6
					- 4 * a4 * c4 * b8 - 11 * a4 * c2 * b10 - a2 * c14
					- 6 * a2 * c12 * b2 + 22 * a2 * c10 * b4 - 3 * a2 * c8 * b6
					- 34 * a2 * c6 * b8 + 25 * a2 * c4 * b10 - 3 * a2 * c2 * b12
					+ c14 * b2 + 2 * c12 * b4 - 15 * c10 * b6 + 20 * c8 * b8
					- 5 * c6 * b10 - 6 * c4 * b12 + 3 * c2 * b14, 0);
		case 542:
			return s(0, a9 * c - 2 * a8 * c2 - a8 * c * b - 2 * a7 * c3
					+ 7 * a7 * c2 * b + 6 * a6 * c4 - 5 * a6 * c3 * b
					- 8 * a6 * c2 * b2 - 14 * a5 * c4 * b + 21 * a5 * c3 * b2
					+ 5 * a5 * c2 * b3 - 6 * a5 * c * b4 - 6 * a4 * c6
					+ 14 * a4 * c5 * b - 2 * a4 * c4 * b2 - 30 * a4 * c3 * b3
					+ 16 * a4 * c2 * b4 + 6 * a4 * c * b5 + 2 * a3 * c7
					+ 7 * a3 * c6 * b - 26 * a3 * c5 * b2 + 38 * a3 * c4 * b3
					+ 2 * a3 * c3 * b4 - 31 * a3 * c2 * b5 + 8 * a3 * c * b6
					+ 2 * a2 * c8 - 9 * a2 * c7 * b + 12 * a2 * c6 * b2
					- 4 * a2 * c5 * b3 - 30 * a2 * c4 * b4 + 37 * a2 * c3 * b5
					- 8 * a2 * c * b7 - a * c9 + 5 * a * c7 * b2
					- 11 * a * c6 * b3 + 20 * a * c5 * b4 - 8 * a * c4 * b5
					- 21 * a * c3 * b6 + 19 * a * c2 * b7 - 3 * a * c * b8
					+ c9 * b - 2 * c8 * b2 + 2 * c7 * b3 - 2 * c6 * b4
					- 4 * c5 * b5 + 10 * c4 * b6 - 2 * c3 * b7 - 6 * c2 * b8
					+ 3 * c * b9, 0);
		case 543:
			return a(0, -a18 + 2 * a16 * b2 + 3 * a16 * c2 + 11 * a14 * b4
					- 20 * a14 * b2 * c2 - 49 * a12 * b6 + 38 * a12 * b4 * c2
					+ 27 * a12 * b2 * c4 - 9 * a12 * c6 + 77 * a10 * b8
					+ 6 * a10 * b6 * c2 - 123 * a10 * b4 * c4
					+ 26 * a10 * b2 * c6 + 12 * a10 * c8 - 49 * a8 * b10
					- 110 * a8 * b8 * c2 + 204 * a8 * b6 * c4
					+ 11 * a8 * b4 * c6 - 47 * a8 * b2 * c8 - 9 * a8 * c10
					- 7 * a6 * b12 + 152 * a6 * b10 * c2 - 126 * a6 * b8 * c4
					- 144 * a6 * b6 * c6 + 137 * a6 * b4 * c8
					- 24 * a6 * b2 * c10 + 12 * a6 * c12 + 29 * a4 * b14
					- 90 * a4 * b12 * c2 - 21 * a4 * b10 * c4
					+ 209 * a4 * b8 * c6 - 117 * a4 * b6 * c8
					- 48 * a4 * b4 * c10 + 53 * a4 * b2 * c12 - 15 * a4 * c14
					- 16 * a2 * b16 + 22 * a2 * b14 * c2 + 57 * a2 * b12 * c4
					- 106 * a2 * b10 * c6 - 17 * a2 * b8 * c8
					+ 114 * a2 * b6 * c10 - 49 * a2 * b4 * c12
					- 14 * a2 * b2 * c14 + 9 * a2 * c16 + 3 * b18 - b16 * c2
					- 18 * b14 * c4 + 13 * b12 * c6 + 32 * b10 * c8
					- 33 * b8 * c10 - 14 * b6 * c12 + 23 * b4 * c14
					- 3 * b2 * c16 - 2 * c18, 0);
		case 544:
			return a(0, -a16 * c2 + 5 * a14 * c4 + 2 * a14 * c2 * b2
					- 9 * a12 * c6 - 10 * a12 * c4 * b2 + 5 * a10 * c8
					+ 18 * a10 * c6 * b2 + 2 * a10 * c2 * b6 + 5 * a8 * c10
					- 13 * a8 * c8 * b2 - 9 * a8 * c6 * b4 + 11 * a8 * c4 * b6
					- 10 * a8 * c2 * b8 - 9 * a6 * c12 + 2 * a6 * c10 * b2
					+ 18 * a6 * c8 * b4 - 18 * a6 * c6 * b6 + 5 * a6 * c4 * b8
					+ 6 * a6 * c2 * b10 + 5 * a4 * c14 - 6 * a4 * c10 * b4
					- 16 * a4 * c8 * b6 + 45 * a4 * c6 * b8 - 36 * a4 * c4 * b10
					+ 8 * a4 * c2 * b12 - a2 * c16 + 2 * a2 * c14 * b2
					- 6 * a2 * c12 * b4 + 8 * a2 * c10 * b6 + 17 * a2 * c8 * b8
					- 48 * a2 * c6 * b10 + 38 * a2 * c4 * b12
					- 10 * a2 * c2 * b14 - c16 * b2 + 3 * c14 * b4
					- 3 * c12 * b6 + 5 * c10 * b8 - 15 * c8 * b10
					+ 21 * c6 * b12 - 13 * c4 * b14 + 3 * c2 * b16, 0);
		case 545:
			return a(0, -a4 * b2 - a2 * c4 - b4 * c2, 0);
		case 546:
			return a(0, -a4 * b2 * c6 - a2 * b6 * c4 - b4 * c8, 0);
		case 547:
			return a(0,
					-a8 * b4 * c2 + a6 * b4 * c4 + a4 * b6 * c4 + a4 * b4 * c6
							- a4 * b2 * c8 - a2 * b8 * c4,
					a8 * b6 + a8 * b4 * c2 - a8 * b2 * c4 - a8 * c6 - a6 * b8
							+ a6 * c8 - a4 * b8 * c2 + a4 * b2 * c8
							+ a2 * b8 * c4 - a2 * b4 * c8 + b8 * c6 - b6 * c8);
		case 548:
			return a(0, a4 * b2 + a2 * b2 * c2 + a2 * c4 + b4 * c2,
					a4 * b2 - a4 * c2 - a2 * b4 + a2 * c4 + b4 * c2 - b2 * c4);
		case 549:
			return a(0, -2 * a8 * b2 * c6 + a8 * c8 + a6 * b8 * c2
					- a6 * b6 * c4 + a6 * b4 * c6 + a6 * b2 * c8 + a4 * b8 * c4
					- a4 * b6 * c6 + 2 * a4 * b4 * c8 - 2 * a4 * b2 * c10
					- 2 * a2 * b10 * c4 + a2 * b8 * c6 - 2 * a2 * b6 * c8
					+ a2 * b4 * c10 + b10 * c6, 0);
		default:
			return new double[0];
		}
	}

	private double[] getCoeff550to599(int n, double a, double b, double c) {
		switch (n) {
		case 550:
			return a(0, a4 * b2 - 2 * a2 * b2 * c2 + a2 * c4 + b4 * c2,
					-2 * a4 * b2 + 2 * a4 * c2 + 2 * a2 * b4 - 2 * a2 * c4
							- 2 * b4 * c2 + 2 * b2 * c4);
		case 551:
			return a(0, a4 * b2 - 4 * a2 * b2 * c2 + a2 * c4 + b4 * c2,
					-4 * a4 * b2 + 4 * a4 * c2 + 4 * a2 * b4 - 4 * a2 * c4
							- 4 * b4 * c2 + 4 * b2 * c4);
		case 552:
			return s(0,
					-a8 * b4 + a6 * b6 + a6 * b2 * c4 + a6 * c6 + a4 * b6 * c2
							- 3 * a4 * b4 * c4 - a4 * c8 + a2 * b4 * c6
							- b8 * c4 + b6 * c6,
					a10 * b2 + a10 * c2 - 2 * a8 * b4 - 2 * a8 * c4
							+ a6 * b4 * c2 + a6 * b2 * c4 - 2 * a4 * b8
							+ a4 * b6 * c2 + a4 * b2 * c6 - 2 * a4 * c8
							+ a2 * b10 + a2 * b6 * c4 + a2 * b4 * c6 + a2 * c10
							+ b10 * c2 - 2 * b8 * c4 - 2 * b4 * c8 + b2 * c10);
		case 553:
			return a(0, a4 * b2 - 3 * a2 * b2 * c2 + a2 * c4 + b4 * c2,
					-3 * a4 * b2 + 3 * a4 * c2 + 3 * a2 * b4 - 3 * a2 * c4
							- 3 * b4 * c2 + 3 * b2 * c4);
		case 554:
			return a(0, a6 * b2 * c4 - 2 * a4 * b4 * c4 + a4 * b2 * c6
					+ a2 * b6 * c4 - 2 * a2 * b4 * c6 + b6 * c6, 0);
		case 555:
			return a(0, a4 * c2 - a2 * c4 - a2 * c2 * b2,
					-2 * a4 * c2 + 2 * a4 * b2 + 2 * a2 * c4 - 2 * a2 * b4
							- 2 * c4 * b2 + 2 * c2 * b4);
		case 556:
			return s(0,
					-a8 * b2 * c2 + a8 * c4 + 2 * a6 * b4 * c2 - 2 * a6 * c6
							- a4 * b6 * c2 - 2 * a4 * b4 * c4
							+ 2 * a4 * b2
									* c6
							+ a4 * c8 + a2 * b6 * c4 - a2 * b2 * c8,
					a10 * b2 + a10 * c2 - 2 * a8 * b4 - 2 * a8 * b2 * c2
							- 2 * a8 * c4 + 2 * a6 * b6 + a6 * b4 * c2
							+ a6 * b2 * c4 + 2 * a6 * c6 - 2 * a4 * b8
							+ a4 * b6 * c2 + a4 * b2 * c6 - 2 * a4 * c8
							+ a2 * b10 - 2 * a2 * b8 * c2 + a2 * b6 * c4
							+ a2 * b4 * c6 - 2 * a2 * b2 * c8 + a2 * c10
							+ b10 * c2 - 2 * b8 * c4 + 2 * b6 * c6 - 2 * b4 * c8
							+ b2 * c10);
		case 557:
			return a(0, a12 - 6 * a10 * b2 + 15 * a8 * b4 - 3 * a8 * c4
					- 20 * a6 * b6 + 9 * a6 * b2 * c4 + a6 * c6 + 15 * a4 * b8
					- 12 * a4 * b4 * c4 - 6 * a2 * b10 + 9 * a2 * b6 * c4
					- 6 * a2 * b2 * c8 + 3 * a2 * c10 + b12 - 3 * b8 * c4
					+ b6 * c6 + 3 * b2 * c10 - 2 * c12,
					-9 * a10 * b2 + 9 * a10 * c2 + 18 * a8 * b4 - 18 * a8 * c4
							- 27 * a6 * b4 * c2 + 27 * a6 * b2 * c4
							- 18 * a4 * b8 + 27 * a4 * b6 * c2
							- 27 * a4 * b2 * c6 + 18 * a4 * c8 + 9 * a2 * b10
							- 27 * a2 * b6 * c4 + 27 * a2 * b4 * c6
							- 9 * a2 * c10 - 9 * b10 * c2 + 18 * b8 * c4
							- 18 * b4 * c8 + 9 * b2 * c10);
		case 558:
			return a(0, -3 * a10 * c2 + 8 * a8 * c4 + 5 * a8 * c2 * b2
					- 6 * a6 * c6 - 8 * a6 * c4 * b2 + 2 * a6 * c2 * b4
					+ 2 * a4 * c6 * b2 + 4 * a4 * c4 * b4 - 6 * a4 * c2 * b6
					+ a2 * c10 - 2 * a2 * c6 * b4 + a2 * c2 * b8 + c10 * b2
					- 4 * c8 * b4 + 6 * c6 * b6 - 4 * c4 * b8 + c2 * b10,
					-4 * a10 * c2 + 4 * a10 * b2 + 8 * a8 * c4 - 8 * a8 * b4
							- 20 * a6 * c4 * b2 + 20 * a6 * c2 * b4
							- 8 * a4 * c8 + 20 * a4 * c6 * b2
							- 20 * a4 * c2 * b6 + 8 * a4 * b8 + 4 * a2 * c10
							- 20 * a2 * c6 * b4 + 20 * a2 * c4 * b6
							- 4 * a2 * b10 - 4 * c10 * b2 + 8 * c8 * b4
							- 8 * c4 * b8 + 4 * c2 * b10);
		case 559:
			return a(0, a2 * b2 * c2 - a2 * c4 - b2 * c4 + c6,
					3 * a4 * b2 - 3 * a4 * c2 - 3 * a2 * b4 + 3 * a2 * c4
							+ 3 * b4 * c2 - 3 * b2 * c4);
		case 560:
			return s(0, -a6 * b * c2 + 6 * a5 * b * c3 + 3 * a4 * b3 * c2
					- 6 * a4 * b2 * c3 - 12 * a4 * b * c4 - 12 * a3 * b3 * c3
					+ 24 * a3 * b2 * c4 + 8 * a3 * b * c5 - 3 * a2 * b5 * c2
					+ 12 * a2 * b4 * c3 - 24 * a2 * b2 * c5 + 6 * a * b5 * c3
					- 24 * a * b4 * c4 + 24 * a * b3 * c5 + b7 * c2
					- 6 * b6 * c3 + 12 * b5 * c4 - 8 * b4 * c5,
					-2 * a7 * b * c + 6 * a6 * b2 * c + 6 * a6 * b * c2
							- 12 * a5 * b3 * c - 6 * a5 * b2 * c2
							- 12 * a5 * b * c3 + 14 * a4 * b4 * c
							+ 6 * a4 * b3 * c2 + 6 * a4 * b2 * c3
							+ 14 * a4 * b * c4 - 12 * a3 * b5 * c
							+ 6 * a3 * b4 * c2 - 18 * a3 * b3 * c3
							+ 6 * a3 * b2 * c4 - 12 * a3 * b * c5
							+ 6 * a2 * b6 * c - 6 * a2 * b5 * c2
							+ 6 * a2 * b4 * c3 + 6 * a2 * b3 * c4
							- 6 * a2 * b2 * c5 + 6 * a2 * b * c6
							- 2 * a * b7 * c + 6 * a * b6 * c2
							- 12 * a * b5 * c3 + 14 * a * b4 * c4
							- 12 * a * b3 * c5 + 6 * a * b2 * c6
							- 2 * a * b * c7);
		case 561:
			return a(0,
					a3 * c3 - a2 * c4 - a2 * c3 * b
							- a * c5 + a * c3
									* b2
							+ c6 + c5 * b - c4 * b2 - c3 * b3,
					a5 * c - a5 * b - 2 * a4 * c2 + 2 * a4 * b2 + a3 * c2 * b
							- a3 * c * b2 + 2 * a2 * c4 - a2 * c3 * b
							+ a2 * c * b3 - 2 * a2 * b4 - a * c5 + a * c3 * b2
							- a * c2 * b3 + a * b5 + c5 * b - 2 * c4 * b2
							+ 2 * c2 * b4 - c * b5);
		case 562:
			return a(0,
					-a10 * c2 + 3 * a8 * c4 + 2 * a8 * c2 * b2 - 3 * a6 * c6
							- 3 * a6 * c4 * b2 + a4 * c8 + a4 * c4 * b4
							- 2 * a4 * c2 * b6 + a2 * c8 * b2 - a2 * c6 * b4
							- a2 * c4 * b6 + a2 * c2 * b8,
					-a10 * c2 + a10 * b2 + 2 * a8 * c4 - 2 * a8 * b4
							- 5 * a6 * c4 * b2 + 5 * a6 * c2 * b4 - 2 * a4 * c8
							+ 5 * a4 * c6 * b2 - 5 * a4 * c2 * b6 + 2 * a4 * b8
							+ a2 * c10 - 5 * a2 * c6 * b4 + 5 * a2 * c4 * b6
							- a2 * b10 - c10 * b2 + 2 * c8 * b4 - 2 * c4 * b8
							+ c2 * b10);
		case 563:
			return a(0, -a8 * c4 + 3 * a6 * c6 - 3 * a4 * c8 + a4 * c6 * b2
					+ a2 * c10 - a2 * c8 * b2 - a2 * c6 * b4 + a2 * c4 * b6,
					-a10 * c2 + a10 * b2 + 2 * a8 * c4 - 2 * a8 * b4
							- 3 * a6 * c4 * b2 + 3 * a6 * c2 * b4 - 2 * a4 * c8
							+ 3 * a4 * c6 * b2 - 3 * a4 * c2 * b6 + 2 * a4 * b8
							+ a2 * c10 - 3 * a2 * c6 * b4 + 3 * a2 * c4 * b6
							- a2 * b10 - c10 * b2 + 2 * c8 * b4 - 2 * c4 * b8
							+ c2 * b10);
		case 564:
			return a(0, 2 * a12 * c2 - 3 * a10 * c4 - 3 * a10 * c2 * b2
					- 6 * a8 * c6 + 18 * a8 * c4 * b2 - 6 * a8 * c2 * b4
					+ 14 * a6 * c8 - 15 * a6 * c6 * b2 - 15 * a6 * c4 * b4
					+ 14 * a6 * c2 * b6 - 6 * a4 * c10 - 15 * a4 * c8 * b2
					+ 42 * a4 * c6 * b4 - 15 * a4 * c4 * b6 - 6 * a4 * c2 * b8
					- 3 * a2 * c12 + 18 * a2 * c10 * b2 - 15 * a2 * c8 * b4
					- 15 * a2 * c6 * b6 + 18 * a2 * c4 * b8 - 3 * a2 * c2 * b10
					+ 2 * c14 - 3 * c12 * b2 - 6 * c10 * b4 + 14 * c8 * b6
					- 6 * c6 * b8 - 3 * c4 * b10 + 2 * c2 * b12,
					-3 * a12 * c2 + 3 * a12 * b2 + 15 * a10 * c4 - 15 * a10 * b4
							- 30 * a8 * c6 - 9 * a8 * c4 * b2 + 9 * a8 * c2 * b4
							+ 30 * a8 * b6 + 30 * a6 * c8 - 30 * a6 * b8
							- 15 * a4 * c10 + 9 * a4 * c8 * b2
							- 9 * a4 * c2 * b8 + 15 * a4 * b10 + 3 * a2 * c12
							- 9 * a2 * c8 * b4 + 9 * a2 * c4 * b8 - 3 * a2 * b12
							- 3 * c12 * b2 + 15 * c10 * b4 - 30 * c8 * b6
							+ 30 * c6 * b8 - 15 * c4 * b10 + 3 * c2 * b12);
		case 565:
			return a(0,
					2 * a6 * c2 - 3 * a4 * c4 - 3 * a4 * c2 * b2 - 3 * a2 * c6
							+ 12 * a2 * c4 * b2 - 3 * a2 * c2 * b4 + 2 * c8
							- 3 * c6 * b2 - 3 * c4 * b4 + 2 * c2 * b6,
					-9 * a6 * c2 + 9 * a6 * b2 + 9 * a2 * c6 - 9 * a2 * b6
							- 9 * c6 * b2 + 9 * c2 * b6);
		case 566:
			return a(0,
					-2 * a4 * c2 + a2 * c4 - a2 * c2 * b2 + c6 - 4 * c4 * b2
							+ 3 * c2 * b4,
					-6 * a4 * c2 + 6 * a4 * b2 + 6 * a2 * c4 - 6 * a2 * b4
							- 6 * c4 * b2 + 6 * c2 * b4);
		case 567:
			return s(0, a2 * c2 - c2 * b2, 2 * a4 - 2 * a2 * c2 - 2 * a2 * b2
					+ 2 * c4 - 2 * c2 * b2 + 2 * b4);
		case 568:
			return s(0, b2 * c2 - c4, -2 * b4 + 2 * b2 * c2 + 2 * b2 * a2
					- 2 * c4 + 2 * c2 * a2 - 2 * a4);
		case 569:
			return a(0,
					a4 * c2 - 3 * a2 * c4 - 2 * a2 * c2 * b2 + 2 * c6
							- 3 * c4 * b2 + c2 * b4,
					-2 * a4 * c2 + 2 * a4 * b2 + 2 * a2 * c4 - 2 * a2 * b4
							- 2 * c4 * b2 + 2 * c2 * b4);
		case 570:
			return a(0, -a4 * c4 + a2 * c6 + c6 * b2 - c4 * b4,
					-a6 * c2 + a6 * b2 + a2 * c6 - a2 * b6 - c6 * b2 + c2 * b6);
		case 571:
			return a(0, -a2 * c + a * c2 + c2 * b + c * b2,
					-2 * a2 * c + 2 * a2 * b + 2 * a * c2 - 2 * a * b2
							- 2 * c2 * b + 2 * c * b2);
		case 572:
			return s(0,
					-a3 * c + 2 * a2 * c2 + a2 * c * b - a * c3 - 2 * a * c2 * b
							+ a * c * b2 + c3 * b - c * b3,
					-2 * a3 * c - 2 * a3 * b + 4 * a2 * c2 + 2 * a2 * c * b
							+ 4 * a2 * b2 - 2 * a * c3 + 2 * a * c2 * b
							+ 2 * a * c * b2 - 2 * a * b3 - 2 * c3 * b
							+ 4 * c2 * b2 - 2 * c * b3);
		case 573:
			return a(0,
					2 * a6 - 17 * a5 * c + 28 * a4 * c2 + 13 * a4 * c * b
							- 6 * a4 * b2 - 2 * a3 * c3 - 10 * a3 * c2 * b
							+ 34 * a3 * c * b2 - 26 * a2 * c4 + 12 * a2 * c3 * b
							- 30 * a2 * c2 * b2 - 26 * a2 * c * b3 + 6 * a2 * b4
							+ 19 * a * c5 - 26 * a * c4 * b - 18 * a * c3 * b2
							+ 10 * a * c2 * b3 - 17 * a * c * b4 - 4 * c6
							+ 11 * c5 * b + 4 * c4 * b2 - 24 * c3 * b3
							+ 2 * c2 * b4 + 13 * c * b5 - 2 * b6,
					-36 * a5 * c + 36 * a5 * b + 72 * a4 * c2 - 72 * a4 * b2
							- 108 * a3 * c2 * b + 108 * a3 * c * b2
							- 72 * a2 * c4 + 108 * a2 * c3 * b
							- 108 * a2 * c * b3 + 72 * a2 * b4 + 36 * a * c5
							- 108 * a * c3 * b2 + 108 * a * c2 * b3
							- 36 * a * b5 - 36 * c5 * b + 72 * c4 * b2
							- 72 * c2 * b4 + 36 * c * b5);
		case 574:
			return a(0,
					2 * a4 - 4 * a3 * b - 3 * a3 * c + 5 * a2 * b * c
							- 4 * a2 * c2 + 4 * a * b3 - a * b2 * c
							- 2 * a * b * c2 + 9 * a * c3 - 2 * b4 - b3 * c
							+ 6 * b2 * c2 + b * c3 - 4 * c4,
					0);
		case 575:
			return a(0,
					a6 - 4 * a5 * c + 5 * a4 * c2 + 2 * a4 * c * b - 3 * a4 * b2
							- a3 * c3 - 5 * a3 * c2 * b + 8 * a3 * c * b2
							- 4 * a2 * c4 + 6 * a2 * c3 * b - 6 * a2 * c2 * b2
							- 4 * a2 * c * b3 + 3 * a2 * b4 + 5 * a * c5
							- 4 * a * c4 * b + 5 * a * c2 * b3 - 4 * a * c * b4
							- 2 * c6 + c5 * b + 2 * c4 * b2 - 3 * c3 * b3
							+ c2 * b4 + 2 * c * b5 - b6,
					-9 * a5 * c + 9 * a5 * b + 18 * a4 * c2 - 18 * a4 * b2
							- 27 * a3 * c2 * b + 27 * a3 * c * b2 - 18 * a2 * c4
							+ 27 * a2 * c3 * b - 27 * a2 * c * b3 + 18 * a2 * b4
							+ 9 * a * c5 - 27 * a * c3 * b2 + 27 * a * c2 * b3
							- 9 * a * b5 - 9 * c5 * b + 18 * c4 * b2
							- 18 * c2 * b4 + 9 * c * b5);
		case 576:
			return a(0,
					-a6 * b2 * c6 - a4 * b4 * c6 + a4 * b2 * c8 + a2 * b6 * c6
							- 2 * a2 * b4 * c8 + a2 * b2 * c10 + b8 * c6
							- 3 * b6 * c8 + 3 * b4 * c10 - b2 * c12,
					0);
		case 577:
			return a(0, -a * c2 + c3 + c2 * b, -2 * a2 * c + 2 * a2 * b
					+ 2 * a * c2 - 2 * a * b2 - 2 * c2 * b + 2 * c * b2);
		case 578:
			return s(0,
					-a3 * b * c2 + a3 * c3 + 2 * a2 * b2 * c2 - 2 * a2 * c4
							- a * b3 * c2 - 2 * a * b2 * c3
							+ 2 * a * b
									* c4
							+ a * c5 + b3 * c3 - b * c5,
					a5 * b + a5 * c - 2 * a4 * b2 - 2 * a4 * b * c - 2 * a4 * c2
							+ 2 * a3 * b3 + a3 * b2 * c + a3 * b * c2
							+ 2 * a3 * c3 - 2 * a2 * b4 + a2 * b3 * c
							+ a2 * b * c3 - 2 * a2 * c4 + a * b5
							- 2 * a * b4 * c + a * b3 * c2 + a * b2 * c3
							- 2 * a * b * c4 + a * c5 + b5 * c - 2 * b4 * c2
							+ 2 * b3 * c3 - 2 * b2 * c4 + b * c5);
		case 579:
			return s(0,
					-a4 * b4 * c4 + a4 * b2 * c6 + a2 * b6 * c4 - a2 * b2 * c8
							- b6 * c6 + b4 * c8,
					-2 * a8 * b2 * c2 + 3 * a6 * b4 * c2 + 3 * a6 * b2 * c4
							+ 3 * a4 * b6 * c2 - 12 * a4 * b4 * c4
							+ 3 * a4 * b2 * c6 - 2 * a2 * b8 * c2
							+ 3 * a2 * b6 * c4 + 3 * a2 * b4 * c6
							- 2 * a2 * b2 * c8);
		case 580:
			return a(0,
					a6 - 3 * a4 * b2 - a4 * c2 + 3 * a2 * b4 + a2 * b2 * c2 - b6
							+ b2 * c4,
					-2 * a4 * b2 + 2 * a4 * c2 + 2 * a2 * b4 - 2 * a2 * c4
							- 2 * b4 * c2 + 2 * b2 * c4);
		case 581:
			return a(0,
					-a4 * c2 + a2 * c4 + 3 * a2 * c2 * b2 + 2 * c4 * b2
							- 2 * c2 * b4,
					-2 * a4 * c2 + 2 * a4 * b2 + 2 * a2 * c4 - 2 * a2 * b4
							- 2 * c4 * b2 + 2 * c2 * b4);
		case 582:
			return a(0,
					-a6 * c2 - 4 * a4 * c2 * b2 + a2 * c6 + 6 * a2 * c4 * b2
							+ 7 * a2 * c2 * b4 + 2 * c6 * b2 - 2 * c2 * b6,
					-2 * a6 * c2 + 2 * a6 * b2 + 2 * a2 * c6 - 2 * a2 * b6
							- 2 * c6 * b2 + 2 * c2 * b6);
		case 583:
			return s(
					a14 - 4 * a12 * b2 - 4 * a12 * c2 + 6 * a10 * b4
							+ 12 * a10 * b2 * c2 + 6 * a10 * c4 - 4 * a8 * b6
							- 12 * a8 * b4 * c2 - 12 * a8 * b2 * c4
							- 4 * a8 * c6 + a6 * b8 + 4 * a6 * b6 * c2
							+ 6 * a6 * b4 * c4 + 4 * a6 * b2 * c6 + a6 * c8,
					3 * a12 * b2 - 12 * a10 * b4 - 4 * a10 * b2 * c2
							+ 18 * a8 * b6 + 12 * a8 * b4 * c2
							- 2 * a8 * b2 * c4 - 12 * a6 * b8
							- 12 * a6 * b6 * c2 + 4 * a6 * b4 * c4
							+ 4 * a6 * b2 * c6 + 3 * a4 * b10 + 4 * a4 * b8 * c2
							- 2 * a4 * b6 * c4 - 4 * a4 * b4 * c6
							- a4 * b2 * c8,
					3 * a10 * b2 * c2 - 4 * a8 * b4 * c2 - 4 * a8 * b2 * c4
							+ 2 * a6 * b6 * c2 + 4 * a6 * b4 * c4
							+ 2 * a6 * b2 * c6 - 4 * a4 * b8 * c2
							+ 4 * a4 * b6 * c4 + 4 * a4 * b4 * c6
							- 4 * a4 * b2 * c8 + 3 * a2 * b10 * c2
							- 4 * a2 * b8 * c4 + 2 * a2 * b6 * c6
							- 4 * a2 * b4 * c8 + 3 * a2 * b2 * c10);
		case 584:
			return s(0, -2 * b * c2 * cA3 * cC3 + b * c2 * cB3,
					8 * a * b * cA3 * cB3 * cC3 * c - a * b * c);
		case 585:
			return a(0, 2 * c2 * cA3 * b * cC3 - c2 * b * cB3, 0);
		case 586:
			return a(0, -4 * c2 * cA3 * cA3 * b * cB3 + c2 * b * cB3, 0);
		case 587:
			return s(0, -16 * a4 * b3 * c2 * cA3 * cA3 * cB3 * cC3 * cC3 + 16
					* a4 * b2 * c3 * cA3 * cA3 * cB3 * cB3 * cC3
					- 16 * a3 * b4
							* c2 * cA3 * cA3 * cB3 * cC3
					+ 8 * a3 * b3 * c3 * cA3 * cA3 * cB3 * cB3 - 8 * a3 * b3
							* c3 * cA3 * cA3 * cC3 * cC3
					- 4 * a3 * b3 * c3 * cA3 * cB3 * cC3 + 16 * a3 * b2 * c4
							* cA3 * cA3 * cB3 * cC3
					+ 4 * a3 * b2 * c4 * cA3 * cB3 * cB3 + 16 * a2 * b5 * c2
							* cA3 * cA3 * cB3 * cC3 * cC3
					- 4 * a2 * b5 * c2 * cA3 * cA3 * cB3 - 16 * a2 * b4 * c3
							* cA3 * cA3 * cB3 * cB3 * cC3
					- 8 * a2 * b4 * c3 * cA3 * cA3 * cC3 + 8 * a2 * b4 * c3
							* cA3 * cB3 * cC3 * cC3
					- 2 * a2 * b4 * c3 * cA3 * cB3 + 8 * a2 * b3 * c4 * cA3
							* cA3 * cB3
					- 8 * a2 * b3 * c4 * cA3 * cB3 * cB3 * cC3 - 2 * a2 * b3
							* c4 * cA3 * cC3
					+ 4 * a2 * b2 * c5 * cA3 * cA3 * cC3 + 4 * a2 * b2 * c5
							* cA3 * cB3
					+ 16 * a * b6 * c2 * cA3 * cA3 * cB3 * cC3 - 8 * a * b5 * c3
							* cA3 * cA3 * cB3 * cB3
					- 2 * a * b5 * c3 * cA3 * cA3 + 12 * a * b5 * c3 * cA3 * cB3
							* cC3
					- 8 * a * b4 * c4 * cA3 * cA3 * cB3 * cC3 - 8 * a * b4 * c4
							* cA3 * cB3 * cB3
					- a * b4 * c4 * cA3 + 2 * a * b4 * c4 * cB3 * cC3 + 2 * a
							* b3 * c5 * cA3 * cA3
					- 4 * a * b3 * c5 * cA3 * cB3 * cC3 - 2 * a * b3 * c5 * cB3
							* cB3
					+ a * b2 * c6 * cA3 + 4 * b7 * c2 * cA3 * cA3 * cB3 + 4 * b6
							* c3 * cA3 * cB3
					- 4 * b5 * c4 * cA3 * cA3 * cB3 + b5 * c4 * cB3 - 4 * b4
							* c5 * cA3 * cB3
					- b3 * c6
							* cB3,
					-8 * a7 * b * cA3 * cC3 * c * cB3
							- 16 * a6 * b2 * cA3 * cC3 * cC3 * c * cB3
							- 4 * a6 * b2 * cA3 * c * cB3
							- 16 * a6 * b * cA3 * cC3 * c2 * cB3 * cB3
							- 4 * a6 * b * cA3 * cC3 * c2
							+ 8 * a5 * b3 * cA3 * cC3 * c * cB3
							+ 16 * a5 * b2 * cA3 * cA3 * cC3 * c2 * cB3
							- 32 * a5 * b2 * cA3 * cC3 * cC3 * c2 * cB3 * cB3
							- 12 * a5 * b2 * cA3 * cC3 * cC3 * c2
							- 12 * a5 * b2 * cA3 * c2 * cB3 * cB3
							- 2 * a5 * b2 * cA3 * c2
							+ 4 * a5 * b2 * cC3 * c2 * cB3
							+ 8 * a5 * b * cA3 * cC3 * c3 * cB3
							+ 32 * a4 * b4 * cA3 * cC3 * cC3 * c * cB3
							+ 8 * a4 * b4 * cA3 * c * cB3
							+ 16 * a4 * b3 * cA3 * cA3 * cC3 * cC3 * c2 * cB3
							+ 8 * a4 * b3 * cA3 * cA3 * c2 * cB3
							- 16 * a4 * b3 * cA3 * cC3 * c2 * cB3 * cB3
							- 4 * a4 * b3 * cA3 * cC3 * c2
							+ 8 * a4 * b3 * cC3 * cC3 * c2 * cB3
							+ a4 * b3 * c2 * cB3
							+ 16 * a4 * b2 * cA3 * cA3 * cC3 * c3 * cB3 * cB3
							+ 8 * a4 * b2 * cA3 * cA3 * cC3 * c3
							- 16 * a4 * b2 * cA3 * cC3 * cC3 * c3 * cB3
							- 4 * a4 * b2 * cA3 * c3 * cB3
							+ 8 * a4 * b2 * cC3 * c3 * cB3 * cB3
							+ a4 * b2 * cC3 * c3
							+ 32 * a4 * b * cA3 * cC3 * c4 * cB3 * cB3
							+ 8 * a4 * b * cA3 * cC3 * c4
							+ 8 * a3 * b5 * cA3 * cC3 * c * cB3
							- 16 * a3 * b4 * cA3 * cA3 * cC3 * c2 * cB3
							+ 16 * a3 * b4 * cA3 * cC3 * cC3 * c2 * cB3 * cB3
							+ 8 * a3 * b4 * cA3 * cC3 * cC3 * c2
							+ 8 * a3 * b4 * cA3 * c2 * cB3 * cB3
							+ a3 * b4 * cA3 * c2 - 4 * a3 * b4 * cC3 * c2 * cB3
							+ 16 * a3 * b3 * cA3 * cA3 * cC3 * cC3 * c3
							+ 16 * a3 * b3 * cA3 * cA3 * c3 * cB3 * cB3
							+ 4 * a3 * b3 * cA3 * cA3 * c3
							- 48 * a3 * b3 * cA3 * cC3 * c3 * cB3
							+ 16 * a3 * b3 * cC3 * cC3 * c3 * cB3 * cB3
							+ 4 * a3 * b3 * cC3 * cC3 * c3
							+ 4 * a3 * b3 * c3 * cB3 * cB3
							- 16 * a3 * b2 * cA3 * cA3 * cC3 * c4 * cB3
							+ 16 * a3 * b2 * cA3 * cC3 * cC3 * c4 * cB3 * cB3
							+ 8 * a3 * b2 * cA3 * cC3 * cC3 * c4
							+ 8 * a3 * b2 * cA3 * c4 * cB3 * cB3
							+ a3 * b2 * cA3 * c4 - 4 * a3 * b2 * cC3 * c4 * cB3
							+ 8 * a3 * b * cA3 * cC3 * c5 * cB3
							- 16 * a2 * b6 * cA3 * cC3 * cC3 * c * cB3
							- 4 * a2 * b6 * cA3 * c * cB3
							- 32 * a2 * b5 * cA3 * cA3 * cC3 * cC3 * c2 * cB3
							- 12 * a2 * b5 * cA3 * cA3 * c2 * cB3
							+ 16 * a2 * b5 * cA3 * cC3 * c2 * cB3 * cB3
							+ 4 * a2 * b5 * cA3 * cC3 * c2
							- 12 * a2 * b5 * cC3 * cC3 * c2 * cB3
							- 2 * a2 * b5 * c2 * cB3
							+ 16 * a2 * b4 * cA3 * cA3 * cC3 * c3 * cB3 * cB3
							+ 8 * a2 * b4 * cA3 * cA3 * cC3 * c3
							- 16 * a2 * b4 * cA3 * cC3 * cC3 * c3 * cB3
							- 4 * a2 * b4 * cA3 * c3 * cB3
							+ 8 * a2 * b4 * cC3 * c3 * cB3 * cB3
							+ a2 * b4 * cC3 * c3
							+ 16 * a2 * b3 * cA3 * cA3 * cC3 * cC3 * c4 * cB3
							+ 8 * a2 * b3 * cA3 * cA3 * c4 * cB3
							- 16 * a2 * b3 * cA3 * cC3 * c4 * cB3 * cB3
							- 4 * a2 * b3 * cA3 * cC3 * c4
							+ 8 * a2 * b3 * cC3 * cC3 * c4 * cB3
							+ a2 * b3 * c4 * cB3
							- 32 * a2 * b2 * cA3 * cA3 * cC3 * c5 * cB3 * cB3
							- 12 * a2 * b2 * cA3 * cA3 * cC3 * c5
							+ 16 * a2 * b2 * cA3 * cC3 * cC3 * c5 * cB3
							+ 4 * a2 * b2 * cA3 * c5 * cB3
							- 12 * a2 * b2 * cC3 * c5 * cB3 * cB3
							- 2 * a2 * b2 * cC3 * c5
							- 16 * a2 * b * cA3 * cC3 * c6 * cB3 * cB3
							- 4 * a2 * b * cA3 * cC3 * c6
							- 8 * a * b7 * cA3 * cC3 * c * cB3
							- 16 * a * b6 * cA3 * cA3 * cC3 * c2 * cB3
							- 4 * a * b6 * cC3 * c2 * cB3
							+ 8 * a * b5 * cA3 * cC3 * c3 * cB3
							+ 32 * a * b4 * cA3 * cA3 * cC3 * c4 * cB3
							+ 8 * a * b4 * cC3 * c4 * cB3
							+ 8 * a * b3 * cA3 * cC3 * c5 * cB3
							- 16 * a * b2 * cA3 * cA3 * cC3 * c6 * cB3
							- 4 * a * b2 * cC3 * c6 * cB3
							- 8 * a * b * cA3 * cC3 * c7 * cB3);
		case 588:
			return s(0, -a * b3 * c2 + a * b2 * c3 + b4 * c2 - b3 * c3,
					-2 * a4 * b * c + 2 * a3 * b2 * c + 2 * a3 * b * c2
							+ 2 * a2 * b3 * c - 6 * a2 * b2 * c2
							+ 2 * a2 * b * c3 - 2 * a * b4 * c + 2 * a * b3 * c2
							+ 2 * a * b2 * c3 - 2 * a * b * c4);
		case 589:
			return s(0,
					a8 * b4 - a8 * b2 * c2 - 4 * a6 * b6 + 3 * a6 * b4 * c2
							+ a6 * b2 * c4 + 6 * a4 * b8 - 5 * a4 * b6 * c2
							- a4 * b4 * c4 - 4 * a2 * b10 + 5 * a2 * b8 * c2
							- a2 * b6 * c4 + b12 - 2 * b10 * c2 + b8 * c4,
					-2 * a12 + 6 * a10 * b2 + 6 * a10 * c2 - 6 * a8 * b4
							- 12 * a8 * b2 * c2 - 6 * a8 * c4 + 4 * a6 * b6
							+ 6 * a6 * b4 * c2 + 6 * a6 * b2 * c4 + 4 * a6 * c6
							- 6 * a4 * b8 + 6 * a4 * b6 * c2 - 6 * a4 * b4 * c4
							+ 6 * a4 * b2 * c6 - 6 * a4 * c8 + 6 * a2 * b10
							- 12 * a2 * b8 * c2 + 6 * a2 * b6 * c4
							+ 6 * a2 * b4 * c6 - 12 * a2 * b2 * c8
							+ 6 * a2 * c10 - 2 * b12 + 6 * b10 * c2
							- 6 * b8 * c4 + 4 * b6 * c6 - 6 * b4 * c8
							+ 6 * b2 * c10 - 2 * c12);
		case 590:
			return s(0,
					a4 * b10 * c4 - a4 * b8 * c6 - a4 * b6 * c8 + a4 * b4 * c10
							- 2 * a2 * b12 * c4 + 5 * a2 * b10 * c6
							- 5 * a2 * b8 * c8 + 3 * a2 * b6 * c10
							- a2 * b4 * c12 + b14 * c4 - 4 * b12 * c6
							+ 6 * b10 * c8 - 4 * b8 * c10 + b6 * c12,
					-2 * a14 * b2 * c2 + 6 * a12 * b4 * c2 + 6 * a12 * b2 * c4
							- 6 * a10 * b6 * c2 - 12 * a10 * b4 * c4
							- 6 * a10 * b2 * c6 + 4 * a8 * b8 * c2
							+ 6 * a8 * b6 * c4 + 6 * a8 * b4 * c6
							+ 4 * a8 * b2 * c8 - 6 * a6 * b10 * c2
							+ 6 * a6 * b8 * c4 - 6 * a6 * b6 * c6
							+ 6 * a6 * b4 * c8 - 6 * a6 * b2 * c10
							+ 6 * a4 * b12 * c2 - 12 * a4 * b10 * c4
							+ 6 * a4 * b8 * c6 + 6 * a4 * b6 * c8
							- 12 * a4 * b4 * c10 + 6 * a4 * b2 * c12
							- 2 * a2 * b14 * c2 + 6 * a2 * b12 * c4
							- 6 * a2 * b10 * c6 + 4 * a2 * b8 * c8
							- 6 * a2 * b6 * c10 + 6 * a2 * b4 * c12
							- 2 * a2 * b2 * c14);
		case 591:
			return a(
					-a4 * b2 + a4 * c2 + 2 * a2 * b4 - 2 * a2 * c4 - b6
							- b4 * c2 + b2 * c4 + c6,
					-a4 * b2 + a4 * c2 + 2 * a2 * b4 - 2 * a2 * b2 * c2 - b6
							+ b4 * c2 + b2 * c4 - c6,
					-4 * a4 * b2 + 4 * a4 * c2 + 4 * a2 * b4 - 4 * a2 * c4
							- 4 * b4 * c2 + 4 * b2 * c4);
		case 592:
			return a(a12 * b6 - 3 * a12 * b4 * c2 + 3 * a12 * b2 * c4 - a12 * c6
					- 4 * a10 * b8 + 8 * a10 * b6 * c2 - 8 * a10 * b2 * c6
					+ 4 * a10 * c8 + 5 * a8 * b10 - 3 * a8 * b8 * c2
					- 16 * a8 * b6 * c4 + 16 * a8 * b4 * c6 + 3 * a8 * b2 * c8
					- 5 * a8 * c10 - 12 * a6 * b10 * c2 + 24 * a6 * b8 * c4
					- 24 * a6 * b4 * c8 + 12 * a6 * b2 * c10 - 5 * a4 * b14
					+ 19 * a4 * b12 * c2 - 16 * a4 * b10 * c4
					- 12 * a4 * b8 * c6 + 12 * a4 * b6 * c8 + 16 * a4 * b4 * c10
					- 19 * a4 * b2 * c12 + 5 * a4 * c14 + 4 * a2 * b16
					- 12 * a2 * b14 * c2 + 8 * a2 * b12 * c4 + 4 * a2 * b10 * c6
					- 4 * a2 * b6 * c10 - 8 * a2 * b4 * c12 + 12 * a2 * b2 * c14
					- 4 * a2 * c16 - b18 + 3 * b16 * c2 - 3 * b14 * c4
					+ 3 * b12 * c6 - 6 * b10 * c8 + 6 * b8 * c10 - 3 * b6 * c12
					+ 3 * b4 * c14 - 3 * b2 * c16 + c18,
					3 * a14 * b4 - 6 * a14 * b2 * c2 + 3 * a14 * c4
							- 12 * a12 * b6 + 12 * a12 * b4 * c2
							+ 12 * a12 * b2 * c4 - 12 * a12 * c6 + 15 * a10 * b8
							+ 11 * a10 * b6 * c2 - 51 * a10 * b4 * c4
							+ 9 * a10 * b2 * c6 + 16 * a10 * c8
							- 53 * a8 * b8 * c2 + 62 * a8 * b6 * c4
							+ 30 * a8 * b4 * c6 - 34 * a8 * b2 * c8
							- 5 * a8 * c10 - 15 * a6 * b12 + 60 * a6 * b10 * c2
							- 23 * a6 * b8 * c4 - 67 * a6 * b6 * c6
							+ 33 * a6 * b4 * c8 + 17 * a6 * b2 * c10
							- 5 * a6 * c12 + 12 * a4 * b14 - 26 * a4 * b12 * c2
							- 19 * a4 * b10 * c4 + 60 * a4 * b8 * c6
							- 6 * a4 * b6 * c8 - 32 * a4 * b4 * c10
							+ 9 * a4 * b2 * c12 + 2 * a4 * c14 - 3 * a2 * b16
							- a2 * b14 * c2 + 27 * a2 * b12 * c4
							- 32 * a2 * b10 * c6 - 9 * a2 * b8 * c8
							+ 25 * a2 * b6 * c10 - a2 * b4 * c12
							- 8 * a2 * b2 * c14 + 2 * a2 * c16 + 3 * b16 * c2
							- 11 * b14 * c4 + 14 * b12 * c6 - 9 * b10 * c8
							+ 10 * b8 * c10 - 13 * b6 * c12 + 6 * b4 * c14
							+ b2 * c16 - c18,
					-3 * a16 * b2 + 3 * a16 * c2 + 7 * a14 * b4 - 7 * a14 * c4
							+ 7 * a12 * b6 - 31 * a12 * b4 * c2
							+ 31 * a12 * b2 * c4 - 7 * a12 * c6 - 35 * a10 * b8
							+ 50 * a10 * b6 * c2 - 50 * a10 * b2 * c6
							+ 35 * a10 * c8 + 35 * a8 * b10 - 90 * a8 * b6 * c4
							+ 90 * a8 * b4 * c6 - 35 * a8 * c10 - 7 * a6 * b12
							- 50 * a6 * b10 * c2 + 90 * a6 * b8 * c4
							- 90 * a6 * b4 * c8 + 50 * a6 * b2 * c10
							+ 7 * a6 * c12 - 7 * a4 * b14 + 31 * a4 * b12 * c2
							- 90 * a4 * b8 * c6 + 90 * a4 * b6 * c8
							- 31 * a4 * b2 * c12 + 7 * a4 * c14 + 3 * a2 * b16
							- 31 * a2 * b12 * c4 + 50 * a2 * b10 * c6
							- 50 * a2 * b6 * c10 + 31 * a2 * b4 * c12
							- 3 * a2 * c16 - 3 * b16 * c2 + 7 * b14 * c4
							+ 7 * b12 * c6 - 35 * b10 * c8 + 35 * b8 * c10
							- 7 * b6 * c12 - 7 * b4 * c14 + 3 * b2 * c16);
		case 593:
			return a(-a8 * b6 + 3 * a8 * b4 * c2 - 3 * a8 * b2 * c4 + a8 * c6
					+ 5 * a6 * b8 - 10 * a6 * b6 * c2 + 10 * a6 * b2 * c6
					- 5 * a6 * c8 - 9 * a4 * b10 + 12 * a4 * b8 * c2
					+ 9 * a4 * b6 * c4 - 9 * a4 * b4 * c6 - 12 * a4 * b2 * c8
					+ 9 * a4 * c10 + 7 * a2 * b12 - 8 * a2 * b10 * c2
					- 5 * a2 * b8 * c4 + 5 * a2 * b4 * c8 + 8 * a2 * b2 * c10
					- 7 * a2 * c12 - 2 * b14 + 3 * b12 * c2 - 3 * b10 * c4
					+ 8 * b8 * c6 - 8 * b6 * c8 + 3 * b4 * c10 - 3 * b2 * c12
					+ 2 * c14,
					3 * a8 * b6 - 9 * a8 * b4 * c2 + 9 * a8 * b2 * c4
							- 3 * a8 * c6 - 9 * a6 * b8 + 20 * a6 * b6 * c2
							- 6 * a6 * b4 * c4 - 12 * a6 * b2 * c6 + 7 * a6 * c8
							+ 9 * a4 * b10 - 16 * a4 * b8 * c2
							- 3 * a4 * b6 * c4 + 15 * a4 * b4 * c6
							- 2 * a4 * b2 * c8 - 3 * a4 * c10 - 3 * a2 * b12
							+ 2 * a2 * b10 * c2 + 9 * a2 * b8 * c4
							- 6 * a2 * b6 * c6 - 11 * a2 * b4 * c8
							+ 12 * a2 * b2 * c10 - 3 * a2 * c12 + 3 * b12 * c2
							- 11 * b10 * c4 + 16 * b8 * c6 - 14 * b6 * c8
							+ 11 * b4 * c10 - 7 * b2 * c12 + 2 * c14,
					6 * a12 * b2 - 6 * a12 * c2 - 20 * a10 * b4 + 20 * a10 * c4
							+ 30 * a8 * b6 + 10 * a8 * b4 * c2
							- 10 * a8 * b2 * c4 - 30 * a8 * c6 - 30 * a6 * b8
							+ 30 * a6 * c8 + 20 * a4 * b10 - 10 * a4 * b8 * c2
							+ 10 * a4 * b2 * c8 - 20 * a4 * c10 - 6 * a2 * b12
							+ 10 * a2 * b8 * c4 - 10 * a2 * b4 * c8
							+ 6 * a2 * c12 + 6 * b12 * c2 - 20 * b10 * c4
							+ 30 * b8 * c6 - 30 * b6 * c8 + 20 * b4 * c10
							- 6 * b2 * c12);
		case 594:
			return a(0, 2 * a3 * b * c2 - a2 * b2 * c2 - 2 * a2 * b * c3
					- 2 * a * b3 * c2 + 2 * a * b2 * c3 + b4 * c2 - b2 * c4,
					-4 * a3 * b2 * c + 4 * a3 * b * c2 + 4 * a2 * b3 * c
							- 4 * a2 * b * c3 - 4 * a * b3 * c2
							+ 4 * a * b2 * c3);
		case 595:
			return a(0,
					2 * a8 * b2 + 2 * a8 * c2 - 5 * a6 * b4 - 4 * a6 * b2 * c2
							- 3 * a6 * c4 + 3 * a4 * b6 + 2 * a4 * b4 * c2
							+ 5 * a4 * b2 * c4 + 2 * a4 * c6 + a2 * b8
							- a2 * b6 * c2 - a2 * b4 * c4 - a2 * b2 * c6
							- 2 * a2 * c8 - b10 + b8 * c2 - 2 * b6 * c4
							+ 2 * b4 * c6 - b2 * c8 + c10,
					-2 * a8 * b2 + 2 * a8 * c2 + 4 * a6 * b4 - 4 * a6 * c4
							- 4 * a4 * b6 + 4 * a4 * c6 + 2 * a2 * b8
							- 2 * a2 * c8 - 2 * b8 * c2 + 4 * b6 * c4
							- 4 * b4 * c6 + 2 * b2 * c8);
		case 596:
			return a(0,
					2 * a8 * b2 - 2 * a8 * c2 - 5 * a6 * b4 + 5 * a6 * c4
							+ 3 * a4 * b6 + 2 * a4 * b4 * c2 + a4 * b2 * c4
							- 6 * a4 * c6 + a2 * b8 - 5 * a2 * b6 * c2
							+ 3 * a2 * b4 * c4 - a2 * b2 * c6 + 2 * a2 * c8
							- b10 + 5 * b8 * c2 - 10 * b6 * c4 + 10 * b4 * c6
							- 5 * b2 * c8 + c10,
					6 * a8 * b2 - 6 * a8 * c2 - 12 * a6 * b4 + 12 * a6 * c4
							+ 12 * a4 * b6 - 12 * a4 * c6 - 6 * a2 * b8
							+ 6 * a2 * c8 + 6 * b8 * c2 - 12 * b6 * c4
							+ 12 * b4 * c6 - 6 * b2 * c8);
		case 597:
			return a(0, 2 * a16 * b2 + 2 * a16 * c2 - 13 * a14 * b4
					- 2 * a14 * b2 * c2 - 5 * a14 * c4 + 35 * a12 * b6
					- 8 * a12 * b4 * c2 + 17 * a12 * b2 * c4 - 4 * a12 * c6
					- 49 * a10 * b8 + a10 * b6 * c2 + 25 * a10 * b4 * c4
					- 41 * a10 * b2 * c6 + 24 * a10 * c8 + 35 * a8 * b10
					+ 45 * a8 * b8 * c2 - 120 * a8 * b6 * c4 + 86 * a8 * b4 * c6
					- a8 * b2 * c8 - 25 * a8 * c10 - 7 * a6 * b12
					- 72 * a6 * b10 * c2 + 106 * a6 * b8 * c4
					+ 36 * a6 * b6 * c6 - 136 * a6 * b4 * c8
					+ 66 * a6 * b2 * c10 + 3 * a6 * c12 - 7 * a4 * b14
					+ 42 * a4 * b12 * c2 + 2 * a4 * b10 * c4
					- 142 * a4 * b8 * c6 + 128 * a4 * b6 * c8
					+ 18 * a4 * b4 * c10 - 51 * a4 * b2 * c12 + 10 * a4 * c14
					+ 5 * a2 * b16 - 7 * a2 * b14 * c2 - 34 * a2 * b12 * c4
					+ 66 * a2 * b10 * c6 + 10 * a2 * b8 * c8
					- 80 * a2 * b6 * c10 + 37 * a2 * b4 * c12
					+ 9 * a2 * b2 * c14 - 6 * a2 * c16 - b18 - b16 * c2
					+ 9 * b14 * c4 - b12 * c6 - 26 * b10 * c8 + 26 * b8 * c10
					+ b6 * c12 - 9 * b4 * c14 + b2 * c16 + c18,
					-2 * a16 * b2 + 2 * a16 * c2 + 28 * a12 * b6
							- 54 * a12 * b4 * c2 + 54 * a12 * b2 * c4
							- 28 * a12 * c6 - 70 * a10 * b8
							+ 100 * a10 * b6 * c2 - 100 * a10 * b2 * c6
							+ 70 * a10 * c8 + 70 * a8 * b10 - 260 * a8 * b6 * c4
							+ 260 * a8 * b4 * c6 - 70 * a8 * c10 - 28 * a6 * b12
							- 100 * a6 * b10 * c2 + 260 * a6 * b8 * c4
							- 260 * a6 * b4 * c8 + 100 * a6 * b2 * c10
							+ 28 * a6 * c12 + 54 * a4 * b12 * c2
							- 260 * a4 * b8 * c6 + 260 * a4 * b6 * c8
							- 54 * a4 * b2 * c12 + 2 * a2 * b16
							- 54 * a2 * b12 * c4 + 100 * a2 * b10 * c6
							- 100 * a2 * b6 * c10 + 54 * a2 * b4 * c12
							- 2 * a2 * c16 - 2 * b16 * c2 + 28 * b12 * c6
							- 70 * b10 * c8 + 70 * b8 * c10 - 28 * b6 * c12
							+ 2 * b2 * c16);
		case 598:
			return s(-a2 * b2 * c2 + b4 * c2 + b2 * c4, 0, 6 * a2 * b2 * c2);
		case 599:
			return a(0,
					a4 * c2 - 3 * a2 * c4 - 2 * a2 * c2 * b2 + 2 * c6 + c4 * b2
							+ c2 * b4,
					2 * a4 * c2 - 2 * a4 * b2 - 2 * a2 * c4 + 2 * a2 * b4
							+ 2 * c4 * b2 - 2 * c2 * b4);
		default:
			return new double[0];
		}
	}

	private double[] getCoeff600plus(int n, double a, double b, double c) {
		switch (n) {
		case 600:
			return s(
					a6 - 2 * a4 * b2 - 2 * a4 * c2 + a2 * b4
							+ 2 * a2 * b2 * c2 + a2
									* c4,
					-a6 + 3 * a4 * c2 - 3 * a2 * c4 - 2 * a2 * c2 * b2
							+ 3 * a2 * b4 + c6 + 2 * c4 * b2 - c2 * b4 - 2 * b6,
					3 * a6 - 3 * a4 * b2 - 3 * a4 * c2 - 3 * a2 * b4
							+ 6 * a2 * b2 * c2 - 3 * a2 * c4 + 3 * b6
							- 3 * b4 * c2 - 3 * b2 * c4 + 3 * c6);
		case 601:
			return s(0, -a18 + 4 * a16 * b2 + 2 * a16 * c2 - 3 * a14 * b4
					- 14 * a14 * b2 * c2 + 2 * a14 * c4 - 10 * a12 * b6
					+ 33 * a12 * b4 * c2 + 4 * a12 * b2 * c4 - 7 * a12 * c6
					+ 25 * a10 * b8 - 28 * a10 * b6 * c2 - 39 * a10 * b4 * c4
					+ 22 * a10 * b2 * c6 + 5 * a10 * c8 - 24 * a8 * b10
					- 8 * a8 * b8 * c2 + 80 * a8 * b6 * c4 - 30 * a8 * b4 * c6
					- 8 * a8 * b2 * c8 - 4 * a8 * c10 + 11 * a6 * b12
					+ 30 * a6 * b10 * c2 - 68 * a6 * b8 * c4 - 4 * a6 * b6 * c6
					+ 42 * a6 * b4 * c8 - 20 * a6 * b2 * c10 + 8 * a6 * c12
					- 2 * a4 * b14 - 19 * a4 * b12 * c2 + 18 * a4 * b10 * c4
					+ 46 * a4 * b8 * c6 - 64 * a4 * b6 * c8 + 18 * a4 * b4 * c10
					+ 10 * a4 * b2 * c12 - 7 * a4 * c14 + 4 * a2 * b14 * c2
					+ 5 * a2 * b12 * c4 - 30 * a2 * b10 * c6 + 19 * a2 * b8 * c8
					+ 20 * a2 * b6 * c10 - 24 * a2 * b4 * c12
					+ 4 * a2 * b2 * c14 + 2 * a2 * c16 - 2 * b14 * c4
					+ 3 * b12 * c6 + 6 * b10 * c8 - 14 * b8 * c10 + 6 * b6 * c12
					+ 3 * b4 * c14 - 2 * b2 * c16, 0);
		case 602:
			return s(0, a4 * b2 - a4 * c2 - 2 * a2 * b2 * c2 + 2 * a2 * c4 + b6
					- 3 * b4 * c2 + 4 * b2 * c4 - 2 * c6, 0);
		case 603:
			return s(0,
					a3 * b2 * c - a3 * b * c2 - a2 * b3 * c + a2 * b * c3
							- a * b4 * c + 5 * a * b3 * c2 - 6 * a * b2 * c3
							+ 2 * a * b * c4 + b5 * c - 2 * b4 * c2 - b3 * c3
							+ 4 * b2 * c4 - 2 * b * c5,
					0);
		case 604:
			return s(0, -a2 * b2 * c2 - b4 * c2 + 2 * b2 * c4, 0);
		case 605:
			return a(0, a3 * c + a * c3 - a * c * b2, 0);
		case 606:
			return a(0, a4 - 2 * a2 * b2 + a2 * c2 + b4 - b2 * c2, 0);
		case 607:
			return a(0, a18 - 8 * a16 * b2 + 28 * a14 * b4 + 6 * a14 * b2 * c2
					- 9 * a14 * c4 - 56 * a12 * b6 - 28 * a12 * b4 * c2
					+ 29 * a12 * b2 * c4 + 15 * a12 * c6 + 70 * a10 * b8
					+ 51 * a10 * b6 * c2 - 36 * a10 * b4 * c4
					- 41 * a10 * b2 * c6 - 9 * a10 * c8 - 56 * a8 * b10
					- 45 * a8 * b8 * c2 + 21 * a8 * b6 * c4 + 52 * a8 * b4 * c6
					+ 3 * a8 * b2 * c8 + 9 * a8 * c10 + 28 * a6 * b12
					+ 20 * a6 * b10 * c2 - 7 * a6 * b8 * c4 - 36 * a6 * b6 * c6
					- 7 * a6 * b4 * c8 + 20 * a6 * b2 * c10 - 15 * a6 * c12
					- 8 * a4 * b14 - 6 * a4 * b12 * c2 + 9 * a4 * b10 * c4
					+ 4 * a4 * b8 * c6 + 15 * a4 * b6 * c8 - 18 * a4 * b4 * c10
					- 5 * a4 * b2 * c12 + 9 * a4 * c14 + a2 * b16
					+ 3 * a2 * b14 * c2 - 12 * a2 * b12 * c4
					+ 16 * a2 * b10 * c6 - 12 * a2 * b8 * c8 - 6 * a2 * b6 * c10
					+ 19 * a2 * b4 * c12 - 9 * a2 * b2 * c14 - b16 * c2
					+ 5 * b14 * c4 - 10 * b12 * c6 + 11 * b10 * c8
					- 10 * b8 * c10 + 11 * b6 * c12 - 10 * b4 * c14
					+ 5 * b2 * c16 - c18,
					-12 * a16 * b2 + 12 * a16 * c2 + 48 * a14 * b4
							- 48 * a14 * c4 - 72 * a12 * b6 - 84 * a12 * b4 * c2
							+ 84 * a12 * b2 * c4 + 72 * a12 * c6 + 60 * a10 * b8
							+ 120 * a10 * b6 * c2 - 120 * a10 * b2 * c6
							- 60 * a10 * c8 - 60 * a8 * b10 - 120 * a8 * b6 * c4
							+ 120 * a8 * b4 * c6 + 60 * a8 * c10 + 72 * a6 * b12
							- 120 * a6 * b10 * c2 + 120 * a6 * b8 * c4
							- 120 * a6 * b4 * c8 + 120 * a6 * b2 * c10
							- 72 * a6 * c12 - 48 * a4 * b14 + 84 * a4 * b12 * c2
							- 120 * a4 * b8 * c6 + 120 * a4 * b6 * c8
							- 84 * a4 * b2 * c12 + 48 * a4 * c14 + 12 * a2 * b16
							- 84 * a2 * b12 * c4 + 120 * a2 * b10 * c6
							- 120 * a2 * b6 * c10 + 84 * a2 * b4 * c12
							- 12 * a2 * c16 - 12 * b16 * c2 + 48 * b14 * c4
							- 72 * b12 * c6 + 60 * b10 * c8 - 60 * b8 * c10
							+ 72 * b6 * c12 - 48 * b4 * c14 + 12 * b2 * c16);
		case 608:
			return a(0,
					a8 * b2 - 2 * a6 * b4 - 2 * a6 * b2 * c2 + 6 * a4 * b4 * c2
							- a4 * b2 * c4 + 2 * a2 * b8 - 6 * a2 * b6 * c2
							+ 2 * a2 * b2 * c6 - b10 + 2 * b8 * c2 + b6 * c4
							- 2 * b4 * c6,
					0);
		case 609:
			return a(4 * b4 * c2 - 4 * b2 * c4,
					-b4 * c2 + 4 * b2 * c2 * a2 + c6 - 2 * c4 * a2 + c2 * a4,
					0);
		case 610:
			return a(0,
					a6 * b * c + a6 * c2 + a5 * b2 * c + a5 * b * c2
							- 2 * a4 * b * c3 - 2 * a4 * c4 - 2 * a3 * b2 * c3
							- 2 * a3 * b * c4 - a2 * b5 * c - a2 * b4 * c2
							+ a2 * b * c5 + a2 * c6 - a * b6 * c - a * b5 * c2
							+ a * b2 * c5 + a * b * c6,
					0);
		case 611:
			return a(0,
					a8 - 2 * a6 * b2 - a6 * c2 + 2 * a4 * b2 * c2 + 2 * a2 * b6
							- 3 * a2 * b4 * c2 + 2 * a2 * b2 * c4 - a2 * c6 - b8
							+ 2 * b6 * c2 - 2 * b2 * c6 + c8,
					0);
		case 612:
			return a(0,
					-a6 * c2 + 2 * a4 * c4 + a4 * c2 * b2 - a2 * c6
							+ a2 * c2 * b4 - c6 * b2 + 2 * c4 * b4 - c2 * b6,
					0);
		case 613:
			return a(0,
					-a6 * b4 * c2 + a6 * b2 * c4 + 3 * a4 * b6 * c2
							- 3 * a4 * b4 * c4 - 3 * a2 * b8 * c2
							+ 4 * a2 * b6 * c4 - a2 * b4 * c6 + b10 * c2
							- 2 * b8 * c4 + 2 * b4 * c8 - b2 * c10,
					-6 * a6 * b4 * c2 + 6 * a6 * b2 * c4 + 6 * a4 * b6 * c2
							- 6 * a4 * b2 * c6 - 6 * a2 * b6 * c4
							+ 6 * a2 * b4 * c6);
		case 614:
			return a(0, a10 * c2 - 2 * a8 * c4 - a8 * c2 * b2 - 2 * a6 * c6
					+ 5 * a6 * c4 * b2 + 8 * a4 * c8 - 15 * a4 * c6 * b2
					+ 10 * a4 * c4 * b4 - 4 * a4 * c2 * b6 - 7 * a2 * c10
					+ 10 * a2 * c8 * b2 + 5 * a2 * c6 * b4 - 15 * a2 * c4 * b6
					+ 7 * a2 * c2 * b8 + 2 * c12 + c10 * b2 - 12 * c8 * b4
					+ 10 * c6 * b6 + 2 * c4 * b8 - 3 * c2 * b10,
					-18 * a6 * c4 * b2 + 18 * a6 * c2 * b4 + 18 * a4 * c6 * b2
							- 18 * a4 * c2 * b6 - 18 * a2 * c6 * b4
							+ 18 * a2 * c4 * b6);
		case 615:
			return a(0,
					-a4 * c2 - 2 * a2 * c4 + 6 * a2 * c2 * b2 + 3 * c6
							+ 2 * c4 * b2 - 5 * c2 * b4,
					-8 * a4 * c2 + 8 * a4 * b2 + 8 * a2 * c4 - 8 * a2 * b4
							- 8 * c4 * b2 + 8 * c2 * b4);
		case 616:
			return a(0,
					a6 - 3 * a4 * b2 + 3 * a4 * c2 + 3 * a2 * b4
							- 2 * a2 * b2 * c2 - 5 * a2 * c4 - b6 - b4 * c2
							+ b2 * c4 + c6,
					-8 * a4 * b2 + 8 * a4 * c2 + 8 * a2 * b4 - 8 * a2 * c4
							- 8 * b4 * c2 + 8 * b2 * c4);
		case 617:
			return a(0,
					a6 - 3 * a4 * b2 + a4 * c2 + 3 * a2 * b4 - 3 * a2 * c4 - b6
							- b4 * c2 + b2 * c4 + c6,
					-4 * a4 * b2 + 4 * a4 * c2 + 4 * a2 * b4 - 4 * a2 * c4
							- 4 * b4 * c2 + 4 * b2 * c4);
		case 618:
			return a(0,
					a6 - 3 * a4 * b2 + 2 * a4 * c2 + 3 * a2 * b4 - a2 * b2 * c2
							- 4 * a2 * c4 - b6 - b4 * c2 + b2 * c4 + c6,
					-6 * a4 * b2 + 6 * a4 * c2 + 6 * a2 * b4 - 6 * a2 * c4
							- 6 * b4 * c2 + 6 * b2 * c4);
		case 619:
			return a(0,
					-a5 * c - a4 * c2 + a4 * c * b + a3 * c3 - a3 * c2 * b
							+ 2 * a3 * c * b2 + a2 * c4 + a2 * c2 * b2
							- 2 * a2 * c * b3 + a * c4 * b
							- a * c3 * b2 + a * c2
									* b3
							- a * c * b4 - c5 * b + c * b5,
					-a5 * c + a5 * b - 2 * a4 * c2 + 2 * a4 * b2 - a3 * c2 * b
							+ a3 * c * b2 + 2 * a2 * c4 + a2 * c3 * b
							- a2 * c * b3 - 2 * a2 * b4 + a * c5 - a * c3 * b2
							+ a * c2 * b3 - a * b5 - c5 * b - 2 * c4 * b2
							+ 2 * c2 * b4 + c * b5);
		case 620:
			return a(0, -a10 * c2 + 2 * a8 * c4 + 4 * a8 * c2 * b2
					- 6 * a6 * c4 * b2 - 6 * a6 * c2 * b4 - 2 * a4 * c8
					+ 6 * a4 * c4 * b4 + 4 * a4 * c2 * b6 + a2 * c10
					+ 2 * a2 * c8 * b2 - 2 * a2 * c4 * b6 - a2 * c2 * b8,
					-2 * a10 * c2 + 2 * a10 * b2 + 4 * a8 * c4 - 4 * a8 * b4
							- 10 * a6 * c4 * b2 + 10 * a6 * c2 * b4
							- 4 * a4 * c8 + 10 * a4 * c6 * b2
							- 10 * a4 * c2 * b6 + 4 * a4 * b8 + 2 * a2 * c10
							- 10 * a2 * c6 * b4 + 10 * a2 * c4 * b6
							- 2 * a2 * b10 - 2 * c10 * b2 + 4 * c8 * b4
							- 4 * c4 * b8 + 2 * c2 * b10);
		case 621:
			return a(0, a6 * c2 - 2 * a4 * c4 - 2 * a4 * c2 * b2 + a2 * c6
					+ 2 * a2 * c4 * b2 + a2 * c2 * b4, 0);
		case 622:
			return a(0,
					a8 * b2 * c2 - a8 * c4 - 2 * a6 * b4 * c2 + a6 * b2 * c4
							+ 3 * a6 * c6 + 3 * a4 * b4 * c4 - 4 * a4 * b2 * c6
							- 3 * a4 * c8 + 2 * a2 * b8 * c2 - 5 * a2 * b6 * c4
							+ a2 * b4 * c6 + a2 * b2 * c8 + a2 * c10 - b10 * c2
							+ 2 * b8 * c4 - 2 * b4 * c8 + b2 * c10,
					a10 * b2 - a10 * c2 - 2 * a8 * b4 + 2 * a8 * c4
							+ 5 * a6 * b4 * c2 - 5 * a6 * b2 * c4 + 2 * a4 * b8
							- 5 * a4 * b6 * c2 + 5 * a4 * b2 * c6 - 2 * a4 * c8
							- a2 * b10 + 5 * a2 * b6 * c4 - 5 * a2 * b4 * c6
							+ a2 * c10 + b10 * c2 - 2 * b8 * c4 + 2 * b4 * c8
							- b2 * c10);
		case 623:
			return a(0,
					a3 - 2 * a2 * b - a2 * c + a * b2 + a * c2 + b2 * c - c3,
					0);
		case 624:
			return a(0, a6 * c2 - 2 * a4 * c4 + a2 * c6 + 3 * a2 * c4 * b2
					- 3 * a2 * c2 * b4 - c6 * b2 - c4 * b4 + 2 * c2 * b6, 0);
		case 625:
			return a(0,
					-a10 * c2 + 3 * a8 * c4 + a8 * c2 * b2 - a6 * c6
							- 4 * a6 * c4 * b2 - a6 * c2 * b4 - 3 * a4 * c8
							+ 3 * a4 * c6 * b2 + 3 * a4 * c4 * b4 + a4 * c2 * b6
							+ 2 * a2 * c10 + 3 * a2 * c6 * b4 - 8 * a2 * c4 * b6
							+ 2 * a2 * c2 * b8 - 2 * c10 * b2 + 3 * c8 * b4
							- 5 * c6 * b6 + 6 * c4 * b8 - 2 * c2 * b10,
					0);
		case 626:
			return a(0,
					-a6 * b2 * c4 - a4 * b4 * c4 + 5 * a4 * b2 * c6
							+ a2 * b6 * c4 + 2 * a2 * b4 * c6 - 3 * a2 * b2 * c8
							+ b8 * c4 - 3 * b6 * c6 + 3 * b4 * c8 - b2 * c10,
					-8 * a6 * b4 * c2 + 8 * a6 * b2 * c4 + 8 * a4 * b6 * c2
							- 8 * a4 * b2 * c6 - 8 * a2 * b6 * c4
							+ 8 * a2 * b4 * c6);
		case 627:
			return a(0, a10 * c2 - 4 * a8 * c4 - 3 * a8 * c2 * b2 + 6 * a6 * c6
					+ 8 * a6 * c4 * b2 + 2 * a6 * c2 * b4 - 4 * a4 * c8
					- 6 * a4 * c6 * b2 - 4 * a4 * c4 * b4 + 2 * a4 * c2 * b6
					+ a2 * c10 + 2 * a2 * c6 * b4 - 3 * a2 * c2 * b8 + c10 * b2
					- 2 * c6 * b6 + c2 * b10, 0);
		case 628:
			return s(0, -a7 * b - a7 * c - a6 * b2 - a6 * b * c + 3 * a5 * b3
					+ 3 * a5 * b2 * c + 3 * a4 * b4 + 3 * a4 * b3 * c
					- 3 * a3 * b5 - 3 * a3 * b4 * c - 2 * a3 * b3 * c2
					- 2 * a3 * b2 * c3 + 3 * a3 * b * c4 + 3 * a3 * c5
					- 3 * a2 * b6 - 3 * a2 * b5 * c - 2 * a2 * b4 * c2
					- 2 * a2 * b3 * c3 + 3 * a2 * b2 * c4 + 3 * a2 * b * c5
					+ a * b7 + a * b6 * c + 2 * a * b5 * c2 + 2 * a * b4 * c3
					- a * b3 * c4 - a * b2 * c5 - 2 * a * b * c6 - 2 * a * c7
					+ b8 + b7 * c + 2 * b6 * c2 + 2 * b5 * c3 - b4 * c4
					- b3 * c5 - 2 * b2 * c6 - 2 * b * c7,
					2 * a8 - 2 * a6 * b2 - 2 * a6 * c2 + 2 * a4 * b2 * c2
							- 2 * a2 * b6 + 2 * a2 * b4 * c2 + 2 * a2 * b2 * c4
							- 2 * a2 * c6 + 2 * b8 - 2 * b6 * c2 - 2 * b2 * c6
							+ 2 * c8);
		case 629:
			return s(0, a2 * b2 * c2 - b4 * c2, a6 - a4 * b2 - a4 * c2 - a2 * b4
					+ 3 * a2 * b2 * c2 - a2 * c4 + b6 - b4 * c2 - b2 * c4 + c6);
		case 630:
			return s(0, -b4 * c2 + b2 * c4, b6 - b4 * c2 - b4 * a2 - b2 * c4
					+ 3 * b2 * c2 * a2 - b2 * a4 + c6 - c4 * a2 - c2 * a4 + a6);
		case 631:
			return a(0, -a * c2 + c3 + c2 * b, 0);
		case 632:
			return a(0, -a2 * b * c3 + 2 * a * b2 * c3 - b3 * c3 + b * c5, 0);
		case 633:
			return a(0,
					-2 * a6 * b2 * c4 + a4 * b4 * c4 + 3 * a4 * b2 * c6
							+ b8 * c4 - 3 * b6 * c6 + 3 * b4 * c8 - b2 * c10,
					0);
		case 634:
			return s(0, 4 * b2 * c2,
					-b4 + 2 * b2 * c2 + 2 * b2 * a2 - c4 + 2 * c2 * a2 - a4);
		case 635:
			return s(0, b2 * c2,
					-2 * b2 * c * a - 2 * b * c2 * a - 2 * b * c * a2);
		case 636:
			return a(0, a6 - a4 * c2 - a2 * c4 + a2 * c2 * b2 + c6 - 3 * c4 * b2
					+ 3 * c2 * b4 - b6, 0);
		case 637:
			return a(0, a * c + 2 * c2 + c * b, 0);
		case 638:
			return s(0, a10 * b2 - a10 * c2 - 3 * a8 * b4 + 3 * a8 * c4
					+ 2 * a6 * b6 + 5 * a6 * b4 * c2 - 4 * a6 * b2 * c4
					- 3 * a6 * c6 + 2 * a4 * b8 - 7 * a4 * b6 * c2
					- a4 * b4 * c4 + 5 * a4 * b2 * c6 + a4 * c8 - 3 * a2 * b10
					+ 4 * a2 * b8 * c2 + 2 * a2 * b6 * c4 - a2 * b4 * c6
					- 2 * a2 * b2 * c8 + b12 - b10 * c2 - b6 * c6 + b4 * c8,
					-2 * a12 + 4 * a10 * b2 + 4 * a10 * c2 + 2 * a8 * b4
							- 16 * a8 * b2 * c2 + 2 * a8 * c4 - 8 * a6 * b6
							+ 12 * a6 * b4 * c2 + 12 * a6 * b2 * c4
							- 8 * a6 * c6 + 2 * a4 * b8 + 12 * a4 * b6 * c2
							- 30 * a4 * b4 * c4 + 12 * a4 * b2 * c6
							+ 2 * a4 * c8 + 4 * a2 * b10 - 16 * a2 * b8 * c2
							+ 12 * a2 * b6 * c4 + 12 * a2 * b4 * c6
							- 16 * a2 * b2 * c8 + 4 * a2 * c10 - 2 * b12
							+ 4 * b10 * c2 + 2 * b8 * c4 - 8 * b6 * c6
							+ 2 * b4 * c8 + 4 * b2 * c10 - 2 * c12);
		case 639:
			return s(0, a8 * b6 * c4 - 2 * a8 * b4 * c6 + a8 * b2 * c8
					- a6 * b8 * c4 - a6 * b6 * c6 + 5 * a6 * b4 * c8
					- 3 * a6 * b2 * c10 + 2 * a4 * b8 * c6 - a4 * b6 * c8
					- 4 * a4 * b4 * c10 + 3 * a4 * b2 * c12 - a2 * b12 * c4
					+ 4 * a2 * b10 * c6 - 7 * a2 * b8 * c8 + 5 * a2 * b6 * c10
					- a2 * b2 * c14 + b14 * c4 - 3 * b12 * c6 + 2 * b10 * c8
					+ 2 * b8 * c10 - 3 * b6 * c12 + b4 * c14,
					-2 * a14 * b2 * c2 + 4 * a12 * b4 * c2 + 4 * a12 * b2 * c4
							+ 2 * a10 * b6 * c2 - 16 * a10 * b4 * c4
							+ 2 * a10 * b2 * c6 - 8 * a8 * b8 * c2
							+ 12 * a8 * b6 * c4 + 12 * a8 * b4 * c6
							- 8 * a8 * b2 * c8 + 2 * a6 * b10 * c2
							+ 12 * a6 * b8 * c4 - 30 * a6 * b6 * c6
							+ 12 * a6 * b4 * c8 + 2 * a6 * b2 * c10
							+ 4 * a4 * b12 * c2 - 16 * a4 * b10 * c4
							+ 12 * a4 * b8 * c6 + 12 * a4 * b6 * c8
							- 16 * a4 * b4 * c10 + 4 * a4 * b2 * c12
							- 2 * a2 * b14 * c2 + 4 * a2 * b12 * c4
							+ 2 * a2 * b10 * c6 - 8 * a2 * b8 * c8
							+ 2 * a2 * b6 * c10 + 4 * a2 * b4 * c12
							- 2 * a2 * b2 * c14);
		case 640:
			return a(0, 3 * a4 * b2 * c4 - a2 * b4 * c4 - 2 * a2 * b2 * c6
					- b6 * c4 + b2 * c8, 0);
		case 641:
			return a(0,
					-3 * a8 * b2 * c4 + a6 * b4 * c4 + 5 * a6 * b2 * c6
							- a4 * b6 * c4 - 2 * a4 * b2 * c8 + 2 * a2 * b8 * c4
							- 2 * a2 * b6 * c6 - a2 * b4 * c8 + a2 * b2 * c10
							+ b10 * c4 - 2 * b8 * c6 + 2 * b4 * c10 - b2 * c12,
					0);
		case 642:
			return a(0,
					a4 * c4 - 2 * a2 * c4 * b2 - 2 * a2 * c2 * b4 - c8
							- 2 * c6 * b2 + c4 * b4 + 2 * c2 * b6,
					-2 * a6 * b2 + 2 * a6 * c2 + 2 * a2 * b6 - 2 * a2 * c6
							- 2 * b6 * c2 + 2 * b2 * c6);
		case 643:
			return a(0, a4 * b2 * c2 - 2 * a2 * b4 * c2 - 2 * a2 * b2 * c4
					+ b6 * c2 - b2 * c6, 0);
		case 644:
			return a(0, b2 * c2 + c4, 0);
		case 645:
			return a(a10 * b2 - a10 * c2 - 5 * a8 * b4 + 5 * a8 * c4
					+ 10 * a6 * b6 + 2 * a6 * b4 * c2 - 2 * a6 * b2 * c4
					- 10 * a6 * c6 - 10 * a4 * b8 + 4 * a4 * b6 * c2
					- 4 * a4 * b2 * c6 + 10 * a4 * c8 + 5 * a2 * b10
					- 9 * a2 * b8 * c2 + 2 * a2 * b6 * c4 - 2 * a2 * b4 * c6
					+ 9 * a2 * b2 * c8 - 5 * a2 * c10 - b12 + 4 * b10 * c2
					- 5 * b8 * c4 + 5 * b4 * c8 - 4 * b2 * c10 + c12,
					a12 - 3 * a10 * b2 + 2 * a10 * c2 + a8 * b2 * c2
							- 17 * a8 * c4 + 10 * a6 * b6 - 16 * a6 * b4 * c2
							+ 10 * a6 * b2 * c4 + 28 * a6 * c6 - 15 * a4 * b8
							+ 22 * a4 * b6 * c2 + 16 * a4 * b4 * c4
							- 6 * a4 * b2 * c6 - 17 * a4 * c8 + 9 * a2 * b10
							- 10 * a2 * b8 * c2 - 10 * a2 * b6 * c4
							+ 16 * a2 * b4 * c6 - 7 * a2 * b2 * c8
							+ 2 * a2 * c10 - 2 * b12 + b10 * c2 + b8 * c4
							+ 10 * b6 * c6 - 16 * b4 * c8 + 5 * b2 * c10 + c12,
					0);
		case 646:
			return a(0, a4 * b2 * c2 + a4 * c4 + 2 * a2 * b2 * c4 - 2 * a2 * c6
					- b6 * c2 + 3 * b4 * c4 - 3 * b2 * c6 + c8, 0);
		case 647:
			return a(0,
					a8 - 3 * a6 * b2 - a6 * c2 + 3 * a4 * b4 + 2 * a4 * b2 * c2
							- a4 * c4 - a2 * b6 - a2 * b4 * c2 + a2 * b2 * c4
							+ a2 * c6,
					0);
		case 648:
			return a(a4 * b2 - a4 * c2 - b6 + 3 * b4 * c2 - 3 * b2 * c4 + c6,
					3 * a6 - 4 * a4 * b2 + a4 * c2 + 5 * a2 * b4
							- 4 * a2 * b2 * c2 - a2 * c4 - 4 * b6 + 5 * b4 * c2
							+ 2 * b2 * c4 - 3 * c6,
					-20 * a4 * b2 + 20 * a4 * c2 + 20 * a2 * b4 - 20 * a2 * c4
							- 20 * b4 * c2 + 20 * b2 * c4);
		case 649:
			return a(-a4 * b2 + a4 * c2 + b6 - 3 * b4 * c2 + 3 * b2 * c4 - c6,
					3 * a6 - 14 * a4 * b2 - a4 * c2 + 13 * a2 * b4
							- 8 * a2 * b2 * c2 - 5 * a2 * c4 - 2 * b6
							+ 7 * b4 * c2 - 8 * b2 * c4 + 3 * c6,
					-4 * a4 * b2 + 4 * a4 * c2 + 4 * a2 * b4 - 4 * a2 * c4
							- 4 * b4 * c2 + 4 * b2 * c4);
		case 650:
			return a(a4 * b2 - a4 * c2 - b6 + 3 * b4 * c2 - 3 * b2 * c4 + c6,
					5 * a6 - 10 * a4 * b2 + a4 * c2 + 11 * a2 * b4
							- 8 * a2 * b2 * c2 - 3 * a2 * c4 - 6 * b6
							+ 9 * b4 * c2 - 3 * c6,
					-28 * a4 * b2 + 28 * a4 * c2 + 28 * a2 * b4 - 28 * a2 * c4
							- 28 * b4 * c2 + 28 * b2 * c4);
		case 651:
			return a(-a4 * b2 + a4 * c2 + b6 - 3 * b4 * c2 + 3 * b2 * c4 - c6,
					2 * a6 - 11 * a4 * b2 - a4 * c2 + 10 * a2 * b4
							- 6 * a2 * b2 * c2 - 4 * a2 * c4 - b6 + 5 * b4 * c2
							- 7 * b2 * c4 + 3 * c6,
					0);
		case 652:
			return a(-a4 * b2 + a4 * c2 + b6 - 3 * b4 * c2 + 3 * b2 * c4 - c6,
					a6 - 8 * a4 * b2 - a4 * c2 + 7 * a2 * b4 - 4 * a2 * b2 * c2
							- 3 * a2 * c4 + 3 * b4 * c2 - 6 * b2 * c4 + 3 * c6,
					4 * a4 * b2 - 4 * a4 * c2 - 4 * a2 * b4 + 4 * a2 * c4
							+ 4 * b4 * c2 - 4 * b2 * c4);
		case 653:
			return a(0, -a4 * b2 * c2 + a2 * c6, 0);
		case 654:
			return a(0,
					-a4 * c2 - 2 * a3 * c3 + 2 * a2 * c3 * b + 2 * a2 * c2 * b2
							+ 2 * a * c5 - 2 * a * c3 * b2 + c6 - 2 * c5 * b
							+ 2 * c3 * b3 - c2 * b4,
					-2 * a5 * c + 2 * a5 * b - 4 * a4 * c2 + 4 * a4 * b2
							- 2 * a3 * c2 * b + 2 * a3 * c * b2 + 4 * a2 * c4
							+ 2 * a2 * c3 * b - 2 * a2 * c * b3 - 4 * a2 * b4
							+ 2 * a * c5 - 2 * a * c3 * b2 + 2 * a * c2 * b3
							- 2 * a * b5 - 2 * c5 * b - 4 * c4 * b2
							+ 4 * c2 * b4 + 2 * c * b5);
		case 655:
			return a(
					-3 * a4 * b2 + 3 * a4 * c2 + 2 * a2 * b4 - 2 * a2 * c4 + b6
							- 3 * b4 * c2 + 3 * b2 * c4 - c6,
					-3 * a6 - 2 * a4 * b2 + 12 * a4 * c2 + a2 * b4
							- 2 * a2 * b2 * c2 - 7 * a2 * c4 + 4 * b6
							- 10 * b4 * c2 + 8 * b2 * c4 - 2 * c6,
					-16 * a4 * b2 + 16 * a4 * c2 + 16 * a2 * b4 - 16 * a2 * c4
							- 16 * b4 * c2 + 16 * b2 * c4);
		case 656:
			return s(1, 3, -21);
		default:
			return new double[0];
		}
	}
}
