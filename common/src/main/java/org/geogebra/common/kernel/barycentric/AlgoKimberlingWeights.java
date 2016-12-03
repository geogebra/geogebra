package org.geogebra.common.kernel.barycentric;

import org.geogebra.common.main.AlgoKimberlingWeightsInterface;
import org.geogebra.common.main.AlgoKimberlingWeightsParams;

/**
 * Most of the content of this class is moved here from AlgoKimberling, to
 * facilitate asyncronous running... comment from AlgoKimberling:
 *
 * credit goes to Jason Cantarella of the Univerity of Georgia for creating a
 * perl script which was used to create this class.
 */

public class AlgoKimberlingWeights implements AlgoKimberlingWeightsInterface {

	private double a2, a3, a4, a5, a6, a7, a8, a9, a10;
	private double b2, b3, b4, b5, b6, b7, b8, b9, b10;
	private double c2, c3, c4, c5, c6, c7, c8, c9, c10;
	private double Q, R, S, T, U, V, angleA, angleB, angleC;

	/**
	 * This class is instantiated for only technical reasons i.e. to be able to
	 * run this part of code async in web
	 */
	public AlgoKimberlingWeights() {

	}

	private static double p(double a, double b) {
		return Math.pow(a, b);
	}

	private static double u(double a) {
		return Math.sqrt(a);
	}

	/**
	 * Making it possible to call the weight method from a JSNI setting where
	 * the precision of primitive types may suffer at conversion
	 * 
	 * @param kw
	 *            weight parameters
	 * @return the same as weight(int,double,double,double)
	 */
	public double weight(AlgoKimberlingWeightsParams kw) {
		return weight(kw.k, kw.a, kw.b, kw.c);
	}

	public double weight(int k, double a, double b, double c) {

		a2 = a * a;
		a3 = a * a2;
		a4 = a * a3;
		a5 = a * a4;
		a6 = a * a5;
		a7 = a * a6;
		a8 = a * a7;
		a9 = a * a8;
		a10 = a * a9;

		b2 = b * b;
		b3 = b * b2;
		b4 = b * b3;
		b5 = b * b4;
		b6 = b * b5;
		b7 = b * b6;
		b8 = b * b7;
		b9 = b * b8;
		b10 = b * b9;

		c2 = c * c;
		c3 = c * c2;
		c4 = c * c3;
		c5 = c * c4;
		c6 = c * c5;
		c7 = c * c6;
		c8 = c * c7;
		c9 = c * c8;
		c10 = c * c9;

		R = b2 + c2;
		Q = (b2 - c2) * (b2 - c2);
		angleA = Math.acos(0.5 * (b2 + c2 - a2) / b / c);
		angleB = Math.acos(0.5 * (-b2 + c2 + a2) / a / c);
		angleC = Math.acos(0.5 * (b2 - c2 + a2) / b / a);
		T = (a2 - b2 - c2);
		U = (a2 + b2 - c2);
		V = (a2 - b2 + c2);
		S = u((a + b + c) * (-a + b + c) * (a - b + c) * (a + b - c));

		if (k < 100) {
			return weight0to99(k, a, b, c);
		}
		if (k < 200) {
			return weight100to199(k, a, b, c);
		}
		if (k < 300) {
			return weight200to299(k, a, b, c);
		}
		if (k < 400) {
			return weight300to399(k, a, b, c);
		}
		if (k < 500) {
			return weight400to499(k, a, b, c);
		}
		if (k < 600) {
			return weight500to599(k, a, b, c);
		}
		if (k < 700) {
			return weight600to699(k, a, b, c);
		}
		if (k < 800) {
			return weight700to799(k, a, b, c);
		}
		if (k < 900) {
			return weight800to899(k, a, b, c);
		}
		if (k < 1000) {
			return weight900to999(k, a, b, c);
		}
		if (k < 1100) {
			return weight1000to1099(k, a, b, c);
		}
		if (k < 1200) {
			return weight1100to1199(k, a, b, c);
		}
		if (k < 1300) {
			return weight1200to1299(k, a, b, c);
		}
		if (k < 1400) {
			return weight1300to1399(k, a, b, c);
		}
		if (k < 1500) {
			return weight1400to1499(k, a, b, c);
		}
		if (k < 1600) {
			return weight1500to1599(k, a, b, c);
		}
		if (k < 1700) {
			return weight1600to1699(k, a, b, c);
		}
		if (k < 1800) {
			return weight1700to1799(k, a, b, c);
		}
		if (k < 1900) {
			return weight1800to1899(k, a, b, c);
		}
		if (k < 2000) {
			return weight1900to1999(k, a, b, c);
		}
		if (k < 2100) {
			return weight2000to2099(k, a, b, c);
		}
		if (k < 2200) {
			return weight2100to2199(k, a, b, c);
		}
		if (k < 2300) {
			return weight2200to2299(k, a, b, c);
		}
		if (k < 2400) {
			return weight2300to2399(k, a, b, c);
		}
		if (k < 2500) {
			return weight2400to2499(k, a, b, c);
		}
		if (k < 2600) {
			return weight2500to2599(k, a, b, c);
		}
		if (k < 2700) {
			return weight2600to2699(k, a, b, c);
		}
		if (k < 2750) {
			return weight2700to2749(k, a, b, c);
		}
		if (k < 2800) {
			return weight2750to2799(k, a, b, c);
		}
		if (k < 2850) {
			return weight2800to2849(k, a, b, c);
		}
		if (k < 2900) {
			return weight2850to2899(k, a, b, c);
		}
		if (k < 2950) {
			return weight2900to2949(k, a, b, c);
		}
		if (k < 3000) {
			return weight2950to2999(k, a, b, c);
		}
		return weight3000plus(k, a, b, c);
	}

	private double weight0to99(int k, double a, double b, double c) {
		switch (k) {
		case 1:
			return a;
		case 2:
			return 1;
		case 3:
			return a2 * T;
		case 4:
			return -a4 + Q;
		case 5:
			return Q - a2 * R;
		case 6:
			return a2;
		case 7:
			return -((a + b - c) * (a - b + c));
		case 8:
			return -a + b + c;
		case 9:
			return a * (a - b - c);
		case 10:
			return b + c;
		case 11:
			return -((a - b - c) * p(b - c, 2));
		case 12:
			return -((a + b - c) * (a - b + c) * p(b + c, 2));
		case 13:
			return a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4
					+ u(3) * a2 * S;
		case 14:
			return -a4 - a2 * b2 + 2 * b4 - a2 * c2 - 4 * b2 * c2 + 2 * c4
					+ u(3) * a2 * S;
		case 15:
			return u(3) * a2 * T - a2 * S;
		case 16:
			return u(3) * a2 * T + a2 * S;
		case 17:
			return 1 / (-a2 + b2 + c2 + u(3) * S);
		case 18:
			return 1 / (-a2 + b2 + c2 - u(3) * S);
		case 19:
			return a * (a4 - Q);
		case 20:
			return 3 * a4 - Q - 2 * a2 * R;
		case 21:
			return a * (a + b) * (a - b - c) * (a + c);
		case 22:
			return a2 * (a4 - b4 - c4);
		case 23:
			return a2 * (a4 - b4 + b2 * c2 - c4);
		case 24:
			return a2 * U * V * (a4 + b4 + c4 - 2 * a2 * R);
		case 25:
			return a2 * U * V;
		case 26:
			return a2 * (a8 - 2 * a6 * R - Q * (b4 + c4) + 2 * a2 * (b6 + c6));
		case 27:
			return (a + b) * (a + c) * U * V;
		case 28:
			return a * (a + b) * (a + c) * U * V;
		case 29:
			return (a + b) * (a - b - c) * (a + c) * U * V;
		case 30:
			return 2 * a4 - Q - a2 * R;
		case 31:
			return a3;
		case 32:
			return a4;
		case 33:
			return a * (a - b - c) * U * V;
		case 34:
			return a * (a + b - c) * (a - b + c) * U * V;
		case 35:
			return a2 * (a2 - b2 - b * c - c2);
		case 36:
			return a2 * (a2 - b2 + b * c - c2);
		case 37:
			return a * (b + c);
		case 38:
			return a * R;
		case 39:
			return a2 * R;
		case 40:
			return a
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2));
		case 41:
			return a3 * (a - b - c);
		case 42:
			return a2 * (b + c);
		case 43:
			return a * (-(b * c) + a * (b + c));
		case 44:
			return a * (2 * a - b - c);
		case 45:
			return a * (a - 2 * (b + c));
		case 46:
			return a * (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a * R);
		case 47:
			return a3 * (a4 + b4 + c4 - 2 * a2 * R);
		case 48:
			return a3 * T;
		case 49:
			return a4 * T * (a4 + b4 - b2 * c2 + c4 - 2 * a2 * R);
		case 50:
			return a4 * (a2 - b2 - b * c - c2) * (a2 - b2 + b * c - c2);
		case 51:
			return a2 * (-Q + a2 * R);
		case 52:
			return a2 * (a4 + b4 + c4 - 2 * a2 * R) * (-Q + a2 * R);
		case 53:
			return U * V * (-Q + a2 * R);
		case 54:
			return a2 * (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 55:
			return a2 * (a - b - c);
		case 56:
			return a2 * (a + b - c) * (a - b + c);
		case 57:
			return a * (a + b - c) * (a - b + c);
		case 58:
			return a2 * (a + b) * (a + c);
		case 59:
			return a2 * p(a - b, 2) * p(a - c, 2) * (a + b - c) * (a - b + c);
		case 60:
			return a2 * p(a + b, 2) * (a - b - c) * p(a + c, 2);
		case 61:
			return a2 * T - u(3) * a2 * S;
		case 62:
			return a2 * T + u(3) * a2 * S;
		case 63:
			return a * T;
		case 64:
			return a2 * (a4 + b4 + 2 * b2 * c2 - 3 * c4 - 2 * a2 * (b2 - c2))
					* (a4 - 3 * b4 + 2 * b2 * c2 + c4 + 2 * a2 * (b2 - c2));
		case 65:
			return a * (a + b - c) * (a - b + c) * (b + c);
		case 66:
			return -a8 + p(b4 - c4, 2);
		case 67:
			return -((a4 - a2 * b2 + b4 - c4) * (a4 - b4 - a2 * c2 + c4));
		case 68:
			return -(T * (a4 - 2 * a2 * b2 + Q) * (a4 - 2 * a2 * c2 + Q));
		case 69:
			return -a2 + b2 + c2;
		case 70:
			return -((a8 + 2 * a4 * b4 - 2 * a6 * R + p(b2 - c2, 3) * R - 2
					* a2 * (b6 - c6)) * (a8 + 2 * a4 * c4 - 2 * a6 * R
					- p(b2 - c2, 3) * R + 2 * a2 * (b6 - c6)));
		case 71:
			return a2 * (b + c) * T;
		case 72:
			return -(a * (b + c) * T);
		case 73:
			return a2 * (a + b - c) * (a - b + c) * (b + c) * T;
		case 74:
			return a2 * (a4 - 2 * b4 + b2 * c2 + c4 + a2 * (b2 - 2 * c2))
					* (a4 + b4 + b2 * c2 - 2 * c4 + a2 * (-2 * b2 + c2));
		case 75:
			return b * c;
		case 76:
			return b2 * c2;
		case 77:
			return a * (a + b - c) * (a - b + c) * T;
		case 78:
			return a * (a - b - c) * T;
		case 79:
			return -((a2 + a * b + b2 - c2) * (a2 - b2 + a * c + c2));
		case 80:
			return -((a2 - a * b + b2 - c2) * (a2 - b2 - a * c + c2));
		case 81:
			return a * (a + b) * (a + c);
		case 82:
			return a * (a2 + b2) * (a2 + c2);
		case 83:
			return (a2 + b2) * (a2 + c2);
		case 84:
			return a
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2))
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 85:
			return b * (-a + b - c) * (a + b - c) * c;
		case 86:
			return (a + b) * (a + c);
		case 87:
			return a * (a * (b - c) - b * c) * (a * (b - c) + b * c);
		case 88:
			return a * (a + b - 2 * c) * (a - 2 * b + c);
		case 89:
			return a * (2 * a + 2 * b - c) * (2 * a - b + 2 * c);
		case 90:
			return a * (a3 + a2 * (b - c) - (b - c) * p(b + c, 2) - a * R)
					* (a3 + a2 * (-b + c) + (b - c) * p(b + c, 2) - a * R);
		case 91:
			return b * c * (a4 - 2 * a2 * b2 + Q) * (a4 - 2 * a2 * c2 + Q);
		case 92:
			return b * c * (-a4 + Q);
		case 93:
			return b2 * c2 * (-V) * U * (a4 + Q - a2 * (2 * b2 + c2))
					* (a4 + Q - a2 * (b2 + 2 * c2));
		case 94:
			return b2 * c2 * (a2 - a * b + b2 - c2) * (a2 + a * b + b2 - c2)
					* (-a2 + b2 - a * c - c2) * (-a2 + b2 + a * c - c2);
		case 95:
			return (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 96:
			return (a4 - 2 * a2 * b2 + Q) * (a4 - 2 * a2 * c2 + Q)
					* (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 97:
			return a2 * T * (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 98:
			return (a4 + b4 - a2 * c2 - b2 * c2)
					* (a4 - a2 * b2 - b2 * c2 + c4);
		case 99:
			return (a - b) * (a + b) * (a - c) * (a + c);
		default:
			return Double.NaN;
		}
	}

	private double weight100to199(int k, double a, double b, double c) {

		switch (k) {
		case 100:
			return a * (a - b) * (a - c);
		case 101:
			return a2 * (a - b) * (a - c);
		case 102:
			return a2
					* (a4 - a3 * b - 2 * b4 + a * b * p(b - c, 2) + b3 * c + b2
							* c2 - b * c3 + c4 + a2 * (b2 + b * c - 2 * c2))
					* (a4 + b4 - a3 * c - b3 * c + a * p(b - c, 2) * c + b2
							* c2 + b * c3 - 2 * c4 + a2
							* (-2 * b2 + b * c + c2));
		case 103:
			return a2 * (a3 - 2 * b3 - a2 * c + b2 * c + c3 + a * (b2 - c2))
					* (a3 - a2 * b + b3 + b * c2 - 2 * c3 + a * (-b2 + c2));
		case 104:
			return a * (a3 - a2 * b + b3 - a * p(b - c, 2) - b * c2)
					* (a3 - a * p(b - c, 2) - a2 * c - b2 * c + c3);
		case 105:
			return a * (a2 + b * (b - c) - a * c) * (a2 - a * b + c * (-b + c));
		case 106:
			return a2 * (a + b - 2 * c) * (a - 2 * b + c);
		case 107:
			return (a - b) * (a + b) * (a - c) * (a + c) * p(a4 - Q, 2);
		case 108:
			return a * (a - b) * (a - c) * (a + b - c) * (a - b + c) * U * V;
		case 109:
			return a2 * (a - b) * (a - c) * (a + b - c) * (a - b + c);
		case 110:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c);
		case 111:
			return a2 * (a2 + b2 - 2 * c2) * (a2 - 2 * b2 + c2);
		case 112:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c) * U * V;
		case 113:
			return -((2 * a4 - Q - a2 * R) * (a4 * R + Q * R - 2 * a2
					* (b4 - b2 * c2 + c4)));
		case 114:
			return -((2 * a4 + Q - a2 * R) * (-b4 - c4 + a2 * R));
		case 115:
			return p(b - c, 2) * p(b + c, 2);
		case 116:
			return p(b - c, 2) * (b2 + b * c + c2 - a * (b + c));
		case 117:
			return -((2 * a4 - a2 * p(b - c, 2) - a3 * (b + c) + a
					* p(b - c, 2) * (b + c) - Q) * (-(a3 * b * c * (b + c)) + a
					* b * p(b - c, 2) * c * (b + c) + a4 * R + Q
					* (b2 - b * c + c2) - a2 * p(b - c, 2)
					* (2 * b2 + 3 * b * c + 2 * c2)));
		case 118:
			return -((2 * a3 - a2 * (b + c) - p(b - c, 2) * (b + c)) * (b5 - b3
					* c2 - b2 * c3 + c5 - a * Q + a3 * R - a2 * (b3 + c3)));
		case 119:
			return -((-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c)) * (a3
					* (b + c) - a * p(b - c, 2) * (b + c) + Q - a2 * R));
		case 120:
			return -((-b2 - c2 + a * (b + c)) * (-2 * a * b * c + a2 * (b + c) + p(
					b - c, 2) * (b + c)));
		case 121:
			return -((2 * a - b - c) * (b3 - 2 * b2 * c - 2 * b * c2 + c3 + a
					* R));
		case 122:
			return p(b - c, 2) * p(b + c, 2) * (T * T)
					* (-3 * a4 + Q + 2 * a2 * R);
		case 123:
			return -((a - b - c) * p(b - c, 2) * T * (a4 + 2 * a2 * b * c - 2
					* a * b * c * (b + c) - Q));
		case 124:
			return (a - b - c) * p(b - c, 2)
					* (-b3 - a * b * c - c3 + a2 * (b + c));
		case 125:
			return p(b - c, 2) * p(b + c, 2) * (-T);
		case 126:
			return -((2 * a2 - b2 - c2) * (b4 - 4 * b2 * c2 + c4 + a2 * R));
		case 127:
			return p(b - c, 2) * p(b + c, 2) * (-T) * (-a4 + b4 + c4);
		case 128:
			return -((a2 - b2 - b * c - c2) * (a2 - b2 + b * c - c2)
					* (-Q + a2 * R) * (2 * a8 + p(b2 - c2, 4) - 4 * a6 * R - 2
					* a2 * Q * R + 3 * a4 * (b4 + c4)));
		case 129:
			return -(a2
					* (-Q + a2 * R)
					* (a8 + b2 * c2 * Q - 2 * a6 * R + a4 * (b4 + b2 * c2 + c4)) * (a8
					* (b4 + c4)
					- 4
					* a6
					* (b6 + c6)
					+ Q
					* (b8 + c8)
					+ a4
					* (6 * b8 - 2 * b6 * c2 - 2 * b4 * c4 - 2 * b2 * c6 + 6 * c8) - 4
					* a2 * (b10 - b8 * c2 - b2 * c8 + c10)));
		case 130:
			return a2
					* p(b - c, 2)
					* p(b + c, 2)
					* (T * T)
					* (-Q + a2 * R)
					* (a8 - b2 * c2 * Q - 2 * a6 * R + a4
							* (b4 + 3 * b2 * c2 + c4));
		case 131:
			return -(T
					* (2 * a8 + p(b2 - c2, 4) - 3 * a6 * R - a2 * Q * R + a4
							* p(b2 + c2, 2)) * (a4 * R + Q * R - 2 * a2
					* (b4 - b2 * c2 + c4)));
		case 132:
			return -(U * V * (-b4 - c4 + a2 * R) * (2 * a6 - a4 * R - Q * R));
		case 133:
			return -(U * V * (2 * a4 - Q - a2 * R) * (a6 * R + 3 * a2 * Q * R
					+ a4 * (-3 * b4 + 4 * b2 * c2 - 3 * c4) - Q
					* (b4 + 4 * b2 * c2 + c4)));
		case 134:
			return a2
					* p(b - c, 2)
					* p(b + c, 2)
					* p(a4 + b4 + c4 - 2 * a2 * R, 2)
					* (-Q + a2 * R)
					* (a10 * a2 - b2 * c2 * p(b2 - c2, 4) - 4 * a10 * R + 2
							* a2 * b2 * c2 * Q * R + a8
							* (6 * b4 + 5 * b2 * c2 + 6 * c4) + a6
							* (-4 * b6 + 2 * b4 * c2 + 2 * b2 * c4 - 4 * c6) + a4
							* (b8 - 4 * b6 * c2 + 2 * b4 * c4 - 4 * b2 * c6 + c8));
		case 135:
			return p(b - c, 2)
					* p(b + c, 2)
					* (-V)
					* U
					* (a4 + b4 + c4 - 2 * a2 * R)
					* (-a6 + 3 * a4 * R + Q * R + a2
							* (-3 * b4 + 2 * b2 * c2 - 3 * c4));
		case 136:
			return p(b - c, 2) * p(b + c, 2) * (-V) * U
					* (a4 + b4 + c4 - 2 * a2 * R);
		case 137:
			return p(b - c, 2) * p(b + c, 2)
					* (a4 + b4 - b2 * c2 + c4 - 2 * a2 * R) * (Q - a2 * R);
		case 138:
			return -(U
					* V
					* (-Q + a2 * R)
					* (2 * a8 - 4 * a6 * R + 2 * a2 * Q * R - Q * (b4 + c4) + a4
							* (b4 + 4 * b2 * c2 + c4)) * (a8 - 2 * a6 * R - 2
					* a2 * Q * R + Q * (b4 + 3 * b2 * c2 + c4) + a4
					* (2 * b4 - b2 * c2 + 2 * c4)));
		case 139:
			return p(b - c, 2)
					* p(b + c, 2)
					* (-V)
					* U
					* (a4 + b4 + c4 - 2 * a2 * R)
					* (Q - a2 * R)
					* (a10 * a2 - 4 * a10 * R + p(b2 - c2, 4)
							* (b4 + b2 * c2 + c4) + a8
							* (7 * b4 + 11 * b2 * c2 + 7 * c4) - 2 * a2 * Q
							* (2 * b6 + b4 * c2 + b2 * c4 + 2 * c6) - 2 * a6
							* (4 * b6 + 5 * b4 * c2 + 5 * b2 * c4 + 4 * c6) + a4
							* (7 * b8 + 2 * b4 * c4 + 7 * c8));
		case 140:
			return 2 * a4 + Q - 3 * a2 * R;
		case 141:
			return b2 + c2;
		case 142:
			return p(b - c, 2) - a * (b + c);
		case 143:
			return a2 * (a4 + b4 - b2 * c2 + c4 - 2 * a2 * R) * (-Q + a2 * R);
		case 144:
			return 3 * a2 - p(b - c, 2) - 2 * a * (b + c);
		case 145:
			return 3 * a - b - c;
		case 146:
			return -a10 - a8 * R + p(b2 - c2, 4) * R + a2 * Q
					* (b4 + 9 * b2 * c2 + c4) + a6
					* (8 * b4 - 9 * b2 * c2 + 8 * c4) + a4
					* (-8 * b6 + 6 * b4 * c2 + 6 * b2 * c4 - 8 * c6);
		case 147:
			return -a8 + b8 - b6 * c2 - b2 * c6 + c8 - a6 * R + a4
					* (2 * b4 + 3 * b2 * c2 + 2 * c4) - a2
					* (b6 + b4 * c2 + b2 * c4 + c6);
		case 148:
			return -a4 + b4 - 3 * b2 * c2 + c4 + a2 * R;
		case 149:
			return -a3 + a2 * (b + c) + p(b - c, 2) * (b + c) - a
					* (b2 - b * c + c2);
		case 150:
			return -a4 - a2 * b * c + a3 * (b + c) - a * p(b - c, 2) * (b + c)
					+ p(b - c, 2) * (b2 + b * c + c2);
		case 151:
			return -a10 + a9 * (b + c) - 12 * a5 * b * p(b - c, 2) * c
					* (b + c) - a * p(b - c, 6) * p(b + c, 3) + p(b2 - c2, 4)
					* (b2 - b * c + c2) - a8 * (b2 + 3 * b * c + c2) - 4 * a4
					* Q * (2 * b2 - 3 * b * c + 2 * c2) - 2 * a7
					* (b3 - 3 * b2 * c - 3 * b * c2 + c3) + 2 * a3
					* p(b - c, 4) * (b3 + 5 * b2 * c + 5 * b * c2 + c3) + a2
					* Q * (b4 - 6 * b3 * c + 14 * b2 * c2 - 6 * b * c3 + c4)
					+ 2 * a6
					* (4 * b4 - b3 * c - 8 * b2 * c2 - b * c3 + 4 * c4);
		case 152:
			return -a8
					+ a7
					* (b + c)
					- a
					* p(b - c, 4)
					* p(b + c, 3)
					+ a4
					* b
					* c
					* (b2 - 6 * b * c + c2)
					+ p(b - c, 4)
					* p(b + c, 2)
					* (b2 + b * c + c2)
					- a6
					* (2 * b2 + b * c + 2 * c2)
					+ a5
					* (5 * b3 - b2 * c - b * c2 + 5 * c3)
					- a3
					* p(b - c, 2)
					* (5 * b3 + 11 * b2 * c + 11 * b * c2 + 5 * c3)
					+ a2
					* p(b - c, 2)
					* (2 * b4 + 5 * b3 * c + 10 * b2 * c2 + 5 * b * c3 + 2 * c4);
		case 153:
			return -a7 + a6 * (b + c) + p(b - c, 4) * p(b + c, 3) + a5
					* (b2 - 7 * b * c + c2) - a * Q * (b2 - 5 * b * c + c2)
					- a4 * (b3 - 5 * b2 * c - 5 * b * c2 + c3) - a2
					* p(b - c, 2) * (b3 + 7 * b2 * c + 7 * b * c2 + c3) + a3
					* (b4 + 2 * b3 * c - 10 * b2 * c2 + 2 * b * c3 + c4);
		case 154:
			return a2 * (3 * a4 - Q - 2 * a2 * R);
		case 155:
			return a2
					* (a8 - 4 * a6 * R + p(b4 - c4, 2) + a4
							* (6 * b4 + 4 * b2 * c2 + 6 * c4) - 4 * a2
							* (b6 + c6));
		case 156:
			return a2
					* (a8 + b2 * c2 * Q - 3 * a6 * R + a4
							* (3 * b4 + 2 * b2 * c2 + 3 * c4) - a2 * (b6 + c6));
		case 157:
			return a2 * (a6 - a4 * R - Q * R + a2 * (b4 + c4));
		case 158:
			return b * c * p(a4 - Q, 2);
		case 159:
			return a2 * (a6 + a4 * R - Q * R - a2 * p(b2 + c2, 2));
		case 160:
			return a4 * (-b4 - b2 * c2 - c4 + a2 * R);
		case 161:
			return a2
					* (a10 - a8 * R - p(b2 - c2, 4) * R + a2 * Q * (b4 + c4)
							- 2 * a6 * (b4 + b2 * c2 + c4) + 2 * a4
							* (b6 + b4 * c2 + b2 * c4 + c6));
		case 162:
			return a * (a - b) * (a + b) * (a - c) * (a + c) * U * V;
		case 163:
			return a3 * (a - b) * (a + b) * (a - c) * (a + c);
		case 164:
			return a
					* (-(a * (a + b - c) * (a - b + c) * u(-(b * (a - b - c)
							* c * (a + b + c))))
							+ b
							* (a + b - c)
							* (-a + b + c)
							* u(a * c * (a - b + c) * (a + b + c)) + c
							* (a - b + c) * (-a + b + c)
							* u(a * b * (a + b - c) * (a + b + c)));
		case 165:
			return a * (3 * a2 - p(b - c, 2) - 2 * a * (b + c));
		case 166:
			return a
					* (a4 - 4 * a3 * b + 6 * a2 * b2 - 4 * a * b3 + b4 - 4 * a3
							* c - 4 * a2 * b * c + 4 * a * b2 * c + 4 * b3 * c
							+ 6 * a2 * c2 + 4 * a * b * c2 - 10 * b2 * c2 - 4
							* a * c3 + 4 * b * c3 + c4 - 2 * (a - b + c)
							* (-a + b + c) * u(a * (-a + b + c))
							* u(b * (a - b + c)) - 2 * (a + b - c)
							* (-a + b + c) * u(a * (-a + b + c))
							* u(c * (a + b - c)) + 2 * (a + b - c)
							* (a - b + c) * u(b * (a - b + c))
							* u(c * (a + b - c)));
		case 167:
			return a
					* ((a2 - 2 * a * b + b2 - 2 * a * c - 2 * b * c + c2)
							* u(a * (-a + b + c))
							+ (a2 - 2 * a * b + b2 + 2 * a * c + 2 * b * c - 3 * c2)
							* u(b * (a - b + c)) + (a2 + 2 * a * b - 3 * b2 - 2
							* a * c + 2 * b * c + c2)
							* u(c * (a + b - c)));
		case 168:
			return a
					* ((-a + b + c) * S - 2 * a
							* u(-(b * (a - b - c) * c * (a + b + c))) + 2
							* (a - c) * u(a * c * (a - b + c) * (a + b + c)) + 2
							* (a - b) * u(a * b * (a + b - c) * (a + b + c)));
		case 169:
			return a * (a3 - a2 * (b + c) - p(b - c, 2) * (b + c) + a * R);
		case 170:
			return a
					* (-(b * p(b - c, 4) * c) + a5 * (b + c) + a * p(b - c, 2)
							* p(b + c, 3) - 2 * a2 * p(b - c, 2)
							* (2 * b2 + 3 * b * c + 2 * c2) - a4
							* (4 * b2 + b * c + 4 * c2) + a3
							* (6 * b3 - 2 * b2 * c - 2 * b * c2 + 6 * c3));
		case 171:
			return a3 + a * b * c;
		case 172:
			return a4 + a2 * b * c;
		case 173:
			return a
					* (-u(a * (-a + b + c)) + u(b * (a - b + c)) + u(c
							* (a + b - c)));
		case 174:
			return a * u(b * (a - b + c)) * u(c * (a + b - c));
		case 175:
			return -2 * a * (a - b - c) * (a + b - c) * (a - b + c)
					- (a + b - c) * (a - b + c) * S;
		case 176:
			return 2 * a * (a - b - c) * (a + b - c) * (a - b + c)
					- (a + b - c) * (a - b + c) * S;
		case 177:
			return (a + b - c) * (a - b + c) * u(a * (-a + b + c))
					* (u(b * (a - b + c)) + u(c * (a + b - c)));
		case 178:
			return u(b * (a - b + c)) + u(c * (a + b - c));
		case 179:
			return b * c
					* p(2 * a * c + u(a * c * (a - b + c) * (a + b + c)), 2)
					* p(2 * a * b + u(a * b * (a + b - c) * (a + b + c)), 2);
		case 180:
			return a
					* ((c2 * (2 * a * b + u(a * b * (a + b - c) * (a + b + c))))
							/ ((2 * b * c + u(-(b * (a - b - c) * c * (a + b + c))))
									* (2 * a * c + u(a * c * (a - b + c)
											* (a + b + c))) + 2
									* c2
									* (2 * a * b + u(a * b * (a + b - c)
											* (a + b + c))))
							+ (b2 * (2 * a * c + u(a * c * (a - b + c)
									* (a + b + c))))
							/ (2
									* b2
									* (2 * a * c + u(a * c * (a - b + c)
											* (a + b + c))) + (2 * b * c + u(-(b
									* (a - b - c) * c * (a + b + c))))
									* (2 * a * b + u(a * b * (a + b - c)
											* (a + b + c)))) - (a2 * (2 * b * c + u(-(b
							* (a - b - c) * c * (a + b + c)))))
							/ (2
									* a2
									* (2 * b * c + u(-(b * (a - b - c) * c * (a
											+ b + c)))) + (2 * a * c + u(a * c
									* (a - b + c) * (a + b + c)))
									* (2 * a * b + u(a * b * (a + b - c)
											* (a + b + c)))));
		case 181:
			return a2 * (a + b - c) * (a - b + c) * p(b + c, 2);
		case 182:
			return a6 - 2 * a2 * b2 * c2 - a4 * R;
		case 183:
			return a4 - 2 * b2 * c2 - a2 * R;
		case 184:
			return a4 * T;
		case 185:
			return a2 * T * (-2 * a2 * Q + a4 * R + Q * R);
		case 186:
			return a2 * U * (a2 - b2 - b * c - c2) * (a2 - b2 + b * c - c2) * V;
		case 187:
			return a2 * (2 * a2 - b2 - c2);
		case 188:
			return u(a * (-a + b + c));
		case 189:
			return -((a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
					* p(b + c, 2)) * (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
					* p(b + c, 2)));
		case 190:
			return (a - b) * (a - c);
		case 191:
			return a
					* (a3 - b3 - b2 * c - b * c2 - c3 + a2 * (b + c) - a
							* (b2 + b * c + c2));
		case 192:
			return -(b * c) + a * (b + c);
		case 193:
			return 3 * a2 - b2 - c2;
		case 194:
			return -(b2 * c2) + a2 * R;
		case 195:
			return a2
					* (a8 - 4 * a6 * R + Q * (b4 + c4) + a4
							* (6 * b4 + 5 * b2 * c2 + 6 * c4) + a2
							* (-4 * b6 + b4 * c2 + b2 * c4 - 4 * c6));
		case 196:
			return -((a + b - c) * (a - b + c) * U * V * (a3 + a2 * (b + c)
					- p(b - c, 2) * (b + c) - a * p(b + c, 2)));
		case 197:
			return a2 * (a4 + 2 * a2 * b * c - 2 * a * b * c * (b + c) - Q);
		case 198:
			return a2
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2));
		case 199:
			return a2
					* (a4 + a2 * b * c + a3 * (b + c) - p(b + c, 2)
							* (b2 - b * c + c2) - a
							* (b3 + b2 * c + b * c2 + c3));
		default:
			return Double.NaN;
		}
	}

	private double weight200to299(int k, double a, double b, double c) {

		switch (k) {
		case 200:
			return a * p(-a + b + c, 2);
		case 201:
			return a * (a + b - c) * (a - b + c) * p(b + c, 2) * T;
		case 202:
			return 2
					* a2
					* (a5 + 2 * a4 * b - 2 * a3 * b2 - 4 * a2 * b3 + a * b4 + 2
							* b5 + 2 * a4 * c - a2 * b2 * c - b4 * c - 2 * a3
							* c2 - a2 * b * c2 + 4 * a * b2 * c2 - b3 * c2 - 4
							* a2 * c3 - b2 * c3 + a * c4 - b * c4 + 2 * c5) - 2
					* u(3) * a2 * (a3 - a * b2 + b2 * c - a * c2 + b * c2) * S;
		case 203:
			return -2
					* a2
					* (a5 + 2 * a4 * b - 2 * a3 * b2 - 4 * a2 * b3 + a * b4 + 2
							* b5 + 2 * a4 * c - a2 * b2 * c - b4 * c - 2 * a3
							* c2 - a2 * b * c2 + 4 * a * b2 * c2 - b3 * c2 - 4
							* a2 * c3 - b2 * c3 + a * c4 - b * c4 + 2 * c5) - 2
					* u(3) * a2 * (a3 - a * b2 + b2 * c - a * c2 + b * c2) * S;
		case 204:
			return a * U * V * (3 * a4 - Q - 2 * a2 * R);
		case 205:
			return a3 * (a4 + 2 * a2 * b * c - 2 * a * b * c * (b + c) - Q);
		case 206:
			return a4 * (a4 - b4 - c4);
		case 207:
			return a
					* (a + b - c)
					* (a - b + c)
					* U
					* V
					* (a6 - 2 * a5 * (b + c) - a4 * p(b + c, 2) + p(b - c, 2)
							* p(b + c, 4) - a2 * Q + 4 * a3 * (b3 + c3) - 2 * a
							* (b5 - b4 * c - b * c4 + c5));
		case 208:
			return a
					* (a + b - c)
					* (a - b + c)
					* U
					* V
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2));
		case 209:
			return a2 * (b + c) * (-b3 + a * b * c - c3 + a2 * (b + c));
		case 210:
			return -(a * (a - b - c) * (b + c));
		case 211:
			return a4 * (-b6 - c6 + a2 * p(b2 + c2, 2));
		case 212:
			return a3 * (a - b - c) * T;
		case 213:
			return a3 * (b + c);
		case 214:
			return a * (2 * a - b - c) * (a2 - b2 + b * c - c2);
		case 215:
			return a4 * (a - b - c) * p(-a2 + b2 - b * c + c2, 2);
		case 216:
			return a2 * T * (-Q + a2 * R);
		case 217:
			return a4 * T * (-Q + a2 * R);
		case 218:
			return a2 * (a2 + b2 + c2 - 2 * a * (b + c));
		case 219:
			return a2 * (a - b - c) * T;
		case 220:
			return a2 * p(-a + b + c, 2);
		case 221:
			return a2
					* (a + b - c)
					* (a - b + c)
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2));
		case 222:
			return a2 * (a + b - c) * (a - b + c) * T;
		case 223:
			return a
					* (a + b - c)
					* (a - b + c)
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2));
		case 224:
			return a
					* T
					* (a4 - 2 * a2 * b * c - 2 * a3 * (b + c) - Q + 2 * a
							* (b3 + c3));
		case 225:
			return (a + b - c) * (a - b + c) * (b + c) * U * V;
		case 226:
			return -((a + b - c) * (a - b + c) * (b + c));
		case 227:
			return a
					* (a + b - c)
					* (a - b + c)
					* (b + c)
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2));
		case 228:
			return a3 * (b + c) * T;
		case 229:
			return a * (a + b) * (a + c)
					* (a4 + a2 * b * c + a * b * c * (b + c) - Q);
		case 230:
			return 2 * a4 + Q - a2 * R;
		case 231:
			return 2 * a8 + p(b2 - c2, 4) - 4 * a6 * R - 2 * a2 * Q * R + 3
					* a4 * (b4 + c4);
		case 232:
			return a2 * U * V * (-b4 - c4 + a2 * R);
		case 233:
			return -((2 * a4 + Q - 3 * a2 * R) * (-Q + a2 * R));
		case 234:
			return (a + b - c) * (a - b + c)
					* (u(b * (a - b + c)) + u(c * (a + b - c)));
		case 235:
			return -(U * V * (-2 * a2 * Q + a4 * R + Q * R));
		case 236:
			return a * (-a + b + c) - u(a * (-a + b + c))
					* (u(b * (a - b + c)) + u(c * (a + b - c)));
		case 237:
			return a4 * (-b4 - c4 + a2 * R);
		case 238:
			return a3 - a * b * c;
		case 239:
			return a2 - b * c;
		case 240:
			return a * U * V * (-b4 - c4 + a2 * R);
		case 241:
			return a * (a + b - c) * (a - b + c) * (-b2 - c2 + a * (b + c));
		case 242:
			return (a2 - b * c) * U * V;
		case 243:
			return (a - b - c) * U * V
					* (a4 - b * p(b - c, 2) * c - a2 * (b2 - b * c + c2));
		case 244:
			return a * p(b - c, 2);
		case 245:
			return a
					* p(b - c, 2)
					* (b + c)
					* (a7 + b7 + b6 * c + b * c6 + c7 - a * b2 * c2
							* (b2 + b * c + c2) - a5
							* (2 * b2 + b * c + 2 * c2) + a4
							* (b3 + b2 * c + b * c2 + c3) + a3
							* (b4 + b3 * c + 3 * b2 * c2 + b * c3 + c4) - 2
							* a2 * (b5 + b4 * c + b * c4 + c5));
		case 246:
			return a2
					* p(b - c, 2)
					* p(b + c, 2)
					* (a8 + b8 + 2 * b6 * c2 + 2 * b2 * c6 + c8 - 4 * a6 * R
							+ a4 * (6 * b4 + 7 * b2 * c2 + 6 * c4) - a2
							* (4 * b6 + 5 * b4 * c2 + 5 * b2 * c4 + 4 * c6));
		case 247:
			return p(b - c, 2)
					* p(b + c, 2)
					* (a10 - 4 * a8 * R + a6 * (5 * b4 + 7 * b2 * c2 + 5 * c4)
							+ Q * (b6 + c6) - a4
							* (b6 + 6 * b4 * c2 + 6 * b2 * c4 + c6) + a2
							* (-2 * b8 + 5 * b6 * c2 - 2 * b4 * c4 + 5 * b2
									* c6 - 2 * c8));
		case 248:
			return a2 * T * (a4 + b4 - a2 * c2 - b2 * c2)
					* (a4 - a2 * b2 - b2 * c2 + c4);
		case 249:
			return a2 * p(a - b, 2) * p(a + b, 2) * p(a - c, 2) * p(a + c, 2);
		case 250:
			return a2 * p(a - b, 2) * p(a + b, 2) * p(a - c, 2) * p(a + c, 2)
					* U * V;
		case 251:
			return a2 * (a2 + b2) * (a2 + c2);
		case 252:
			return (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 + Q - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2))
					* (a4 + Q - a2 * (b2 + 2 * c2));
		case 253:
			return -((a4 + b4 + 2 * b2 * c2 - 3 * c4 - 2 * a2 * (b2 - c2)) * (a4
					- 3 * b4 + 2 * b2 * c2 + c4 + 2 * a2 * (b2 - c2)));
		case 254:
			return U
					* V
					* (a6 - p(b2 - c2, 3) - a4 * (3 * b2 + c2) + a2
							* (3 * b4 + 2 * b2 * c2 - c4))
					* (a6 + p(b2 - c2, 3) - a4 * (b2 + 3 * c2) + a2
							* (-b4 + 2 * b2 * c2 + 3 * c4));
		case 255:
			return a3 * (T * T);
		case 256:
			return a * (b2 + a * c) * (a * b + c2);
		case 257:
			return (b2 + a * c) * (a * b + c2);
		case 258:
			return a
					* (-a2 + p(b - c, 2) + 2 * u(b * (a - b + c))
							* u(c * (a + b - c)));
		case 259:
			return a * u(a * (-a + b + c));
		case 260:
			return a
					* (u(a * (-a + b + c)) * u(b * (a - b + c))
							* u(c * (a + b - c)) + a
							* (-a + b + c)
							* (u(a * (-a + b + c)) + u(b * (a - b + c)) + u(c
									* (a + b - c))));
		case 261:
			return p(a + b, 2) * (a - b - c) * p(a + c, 2);
		case 262:
			return -((c2 * (b2 - c2) + a2 * (2 * b2 + c2)) * (-b4 + b2 * c2 + a2
					* (b2 + 2 * c2)));
		case 263:
			return a2 * (c2 * (b2 - c2) + a2 * (2 * b2 + c2))
					* (-b4 + b2 * c2 + a2 * (b2 + 2 * c2));
		case 264:
			return b2 * c2 * (-V) * U;
		case 265:
			return -(T * (a2 - a * b + b2 - c2) * (a2 + a * b + b2 - c2)
					* (a2 - b2 - a * c + c2) * (a2 - b2 + a * c + c2));
		case 266:
			return a2 * u(b * (a - b + c)) * u(c * (a + b - c));
		case 267:
			return a
					* (a3 + a2 * (b + c) + (b - c) * p(b + c, 2) + a
							* (b2 + b * c - c2))
					* (a3 + a2 * (b + c) - (b - c) * p(b + c, 2) + a
							* (-b2 + b * c + c2));
		case 268:
			return a2
					* (a - b - c)
					* T
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2))
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 269:
			return a * p(a + b - c, 2) * p(a - b + c, 2);
		case 270:
			return a * p(a + b, 2) * (a - b - c) * p(a + c, 2) * U * V;
		case 271:
			return a
					* (a - b - c)
					* T
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2))
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 272:
			return (a + b) * (a + c) * (a3 - b2 * c + c3 - a * b * (b + c))
					* (a3 + b3 - b * c2 - a * c * (b + c));
		case 273:
			return b * (-a + b - c) * (a + b - c) * c * (-V) * U;
		case 274:
			return b * (a + b) * c * (a + c);
		case 275:
			return U * V * (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 276:
			return b2 * c2 * (-V) * U
					* (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (-a4 + c2 * (b2 - c2) + a2 * (b2 + 2 * c2));
		case 277:
			return (a2 - 2 * a * b + p(b - c, 2))
					* (a2 + p(b - c, 2) - 2 * a * c);
		case 278:
			return (a + b - c) * (a - b + c) * U * V;
		case 279:
			return p(a + b - c, 2) * p(a - b + c, 2);
		case 280:
			return (a - b - c)
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2))
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 281:
			return (a - b - c) * U * V;
		case 282:
			return a
					* (a - b - c)
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2))
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 283:
			return a2 * (a + b) * (a - b - c) * (a + c) * T;
		case 284:
			return a2 * (a + b) * (a - b - c) * (a + c);
		case 285:
			return a
					* (a + b)
					* (a - b - c)
					* (a + c)
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2))
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 286:
			return b * (a + b) * c * (a + c) * (-V) * U;
		case 287:
			return T * (a4 + b4 - a2 * c2 - b2 * c2)
					* (a4 - a2 * b2 - b2 * c2 + c4);
		case 288:
			return a2 * (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2))
					* (a4 + 2 * b4 - 3 * b2 * c2 + c4 - a2 * (3 * b2 + 2 * c2))
					* (a4 + b4 - 3 * b2 * c2 + 2 * c4 - a2 * (2 * b2 + 3 * c2));
		case 289:
			return a2 * (a + b - c) * (a - b + c)
					* (2 * b * c - u(b * (a - b + c)) * u(c * (a + b - c)));
		case 290:
			return b2 * c2 * (a4 + b4 - a2 * c2 - b2 * c2)
					* (-a4 + a2 * b2 + b2 * c2 - c4);
		case 291:
			return -(a * (-b2 + a * c) * (a * b - c2));
		case 292:
			return a2 * (-b2 + a * c) * (a * b - c2);
		case 293:
			return a * T * (a4 + b4 - a2 * c2 - b2 * c2)
					* (a4 - a2 * b2 - b2 * c2 + c4);
		case 294:
			return a * (a - b - c) * (a2 + b * (b - c) - a * c)
					* (a2 - a * b + c * (-b + c));
		case 295:
			return a2 * (-b2 + a * c) * (a * b - c2) * T;
		case 296:
			return a2
					* (a + b - c)
					* (a - b + c)
					* T
					* (a3 * b + c2 * (b2 - c2) + a2 * (-2 * b2 + c2) + a
							* (b3 - b * c2))
					* (-b4 + a3 * c + b2 * c2 + a2 * (b2 - 2 * c2) + a
							* (-(b2 * c) + c3));
		case 297:
			return U * V * (-b4 - c4 + a2 * R);
		case 298:
			return -(u(3) * T) + S;
		case 299:
			return -(u(3) * T) - S;
		default:
			return Double.NaN;
		}
	}

	private double weight300to399(int k, double a, double b, double c) {

		switch (k) {
		case 300:
			return (b2 * c2) / (u(3) * (-T) + S);
		case 301:
			return (b2 * c2) / (u(3) * (-T) - S);
		case 302:
			return -a2 + b2 + c2 + u(3) * S;
		case 303:
			return a2 - b2 - c2 + u(3) * S;
		case 304:
			return b * c * (-T);
		case 305:
			return b2 * c2 * (-T);
		case 306:
			return (b + c) * (-T);
		case 307:
			return (a + b - c) * (a - b + c) * (b + c) * T;
		case 308:
			return b2 * (a2 + b2) * c2 * (a2 + c2);
		case 309:
			return b
					* c
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2))
					* (-a3 + a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 310:
			return b2 * (a + b) * c2 * (a + c);
		case 311:
			return b2 * c2 * (Q - a2 * R);
		case 312:
			return b * c * (-a + b + c);
		case 313:
			return b2 * c2 * (b + c);
		case 314:
			return b * (a + b) * c * (a + c) * (-a + b + c);
		case 315:
			return -a4 + b4 + c4;
		case 316:
			return -a4 + b4 - b2 * c2 + c4;
		case 317:
			return -(U * V * (a4 + b4 + c4 - 2 * a2 * R));
		case 318:
			return b * c * (-a + b + c) * (-V) * U;
		case 319:
			return -a2 + b2 + b * c + c2;
		case 320:
			return -a2 + b2 - b * c + c2;
		case 321:
			return b * c * (b + c);
		case 322:
			return b
					* c
					* (-a3 - a2 * (b + c) + p(b - c, 2) * (b + c) + a
							* p(b + c, 2));
		case 323:
			return a2 * (a2 - b2 - b * c - c2) * (a2 - b2 + b * c - c2);
		case 324:
			return b2 * c2 * (-V) * U * (Q - a2 * R);
		case 325:
			return b4 + c4 - a2 * R;
		case 326:
			return a * (T * T);
		case 327:
			return b2 * c2 * (c2 * (b2 - c2) + a2 * (2 * b2 + c2))
					* (b4 - b2 * c2 - a2 * (b2 + 2 * c2));
		case 328:
			return b2 * c2 * (a2 - a * b + b2 - c2) * (a2 + a * b + b2 - c2)
					* (-a2 + b2 - a * c - c2) * (-a2 + b2 + a * c - c2) * (-T);
		case 329:
			return -a3 - a2 * (b + c) + p(b - c, 2) * (b + c) + a * p(b + c, 2);
		case 330:
			return (a * (b - c) - b * c) * (a * (b - c) + b * c);
		case 331:
			return b2 * (-a + b - c) * (a + b - c) * c2 * (-V) * U;
		case 332:
			return (a + b) * (a - b - c) * (a + c) * T;
		case 333:
			return (a + b) * (a - b - c) * (a + c);
		case 334:
			return b * c * (b2 - a * c) * (a * b - c2);
		case 335:
			return (b2 - a * c) * (a * b - c2);
		case 336:
			return b * c * (-T) * (a4 + b4 - a2 * c2 - b2 * c2)
					* (-a4 + a2 * b2 + b2 * c2 - c4);
		case 337:
			return (b2 - a * c) * (a * b - c2) * (-T);
		case 338:
			return b2 * p(b - c, 2) * c2 * p(b + c, 2);
		case 339:
			return b2 * p(b - c, 2) * c2 * p(b + c, 2) * (-T);
		case 340:
			return -(U * (a2 - b2 - b * c - c2) * (a2 - b2 + b * c - c2) * V);
		case 341:
			return b * c * p(-a + b + c, 2);
		case 342:
			return b
					* (-a + b - c)
					* (a + b - c)
					* c
					* (-V)
					* U
					* (-a3 - a2 * (b + c) + p(b - c, 2) * (b + c) + a
							* p(b + c, 2));
		case 343:
			return T * (-Q + a2 * R);
		case 344:
			return a2 + b2 + c2 - 2 * a * (b + c);
		case 345:
			return (a - b - c) * T;
		case 346:
			return p(-a + b + c, 2);
		case 347:
			return (a + b - c)
					* (a - b + c)
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2));
		case 348:
			return (a + b - c) * (a - b + c) * T;
		case 349:
			return b2 * (-a + b - c) * (a + b - c) * c2 * (b + c);
		case 350:
			return b * c * (-a2 + b * c);
		case 351:
			return a2 * (b2 - c2) * (2 * a2 - b2 - c2);
		case 352:
			return a2 * (a4 + b4 + 5 * b2 * c2 + c4 - 4 * a2 * R);
		case 353:
			return a2 * (4 * a4 - 2 * b4 - b2 * c2 - 2 * c4 - 4 * a2 * R);
		case 354:
			return a * (-p(b - c, 2) + a * (b + c));
		case 355:
			return -a4 - 2 * a2 * b * c + a3 * (b + c) - a * p(b - c, 2)
					* (b + c) + Q;
		case 356:
			return a
					* (Math.cos(angleA / 3) + 2 * Math.cos(angleB / 3)
							* Math.cos(angleC / 3));
		case 357:
			return a / Math.cos(angleA / 3);
		case 358:
			return a * Math.cos(angleA / 3);
		case 359:
			return a2 / angleA;
		case 360:
			return angleA;
		case 361:
			return a
					* (-u(-(b * (a - b - c) * c * (a + b + c)))
							+ u(a * c * (a - b + c) * (a + b + c)) + u(a * b
							* (a + b - c) * (a + b + c)));
		case 362:
			return a
					* (-(a2 * u(-(b * (a - b - c) * c * (a + b + c)))) + b2
							* u(a * c * (a - b + c) * (a + b + c)) + c2
							* u(a * b * (a + b - c) * (a + b + c)));
		case 363:
			return a
					* (-4 * a * b * c * (-a + b + c) + c * (a - 3 * b + c)
							* u(a * (-a + b + c)) * u(b * (a - b + c)) + b
							* (a + b - 3 * c) * u(a * (-a + b + c))
							* u(c * (a + b - c)) - a * (a + b + c)
							* u(b * (a - b + c)) * u(c * (a + b - c)));
		case 364:
			return a * (-u(a) + u(b) + u(c));
		case 365:
			return a * u(a);
		case 366:
			return u(a);
		case 367:
			return a * (u(b) + u(c));
			/* case 368 to 370: perl script returns 0 */
		case 371:
			return a2 * T - a2 * S;
		case 372:
			return a2 * T + a2 * S;
		case 373:
			return a2 * (-b4 + 6 * b2 * c2 - c4 + a2 * R);
		case 374:
			return -(a * (a3 * (b + c) - Q + a2 * (b2 - 6 * b * c + c2) - a
					* (b3 - 5 * b2 * c - 5 * b * c2 + c3)));
		case 375:
			return a2
					* (-(a * b * c * (b + c)) + a2 * R - p(b + c, 2)
							* (b2 - 3 * b * c + c2));
		case 376:
			return 5 * a4 - Q - 4 * a2 * R;
		case 377:
			return -a4 - 2 * a2 * b * c - 2 * a * b * c * (b + c) + Q;
		case 378:
			return a2 * U * V * (a4 + b4 + 4 * b2 * c2 + c4 - 2 * a2 * R);
		case 379:
			return a5 + a2 * b * c * (b + c) - b * p(b - c, 2) * c * (b + c)
					- a * Q;
		case 380:
			return a
					* (a - b - c)
					* (3 * a3 + 3 * a2 * (b + c) + p(b - c, 2) * (b + c) + a
							* p(b + c, 2));
		case 381:
			return -a4 + 2 * Q - a2 * R;
		case 382:
			return -3 * a4 + 2 * Q + a2 * R;
		case 383:
			return -(u(3) * U * V * (a2 + b2 + c2)) - (a - b - c) * (a + b - c)
					* (a - b + c) * (a + b + c) * S;
		case 384:
			return a4 + b2 * c2;
		case 385:
			return a4 - b2 * c2;
		case 386:
			return a2 * (b2 + b * c + c2 + a * (b + c));
		case 387:
			return a4 + 4 * a3 * (b + c) + 2 * a2 * p(b + c, 2) + Q;
		case 388:
			return -a4 - 4 * a2 * b * c + Q;
		case 389:
			return a2
					* (a6 * R + 3 * a2 * Q * R - 3 * a4 * (b4 + c4) - Q
							* (b4 + c4));
		case 390:
			return (3 * a2 + p(b - c, 2)) * (a - b - c);
		case 391:
			return (a - b - c) * (3 * a + b + c);
		case 392:
			return a
					* (b3 + 4 * a * b * c + b2 * c + b * c2 + c3 - a2 * (b + c));
		case 393:
			return p(a4 - Q, 2);
		case 394:
			return a2 * (T * T);
		case 395:
			return (a - b - c) * (a + b - c) * (a - b + c) * (a + b + c) + u(3)
					* a2 * S;
		case 396:
			return -((a - b - c) * (a + b - c) * (a - b + c) * (a + b + c))
					+ u(3) * a2 * S;
		case 397:
			return U * V + u(3) * a2 * S;
		case 398:
			return -(U * V) + u(3) * a2 * S;
		case 399:
			return a2
					* (a8 - 4 * a6 * R + Q * (b4 + 4 * b2 * c2 + c4) + a4
							* (6 * b4 + b2 * c2 + 6 * c4) + a2
							* (-4 * b6 + b4 * c2 + b2 * c4 - 4 * c6));
		default:
			return Double.NaN;
		}
	}

	private double weight400to499(int k, double a, double b, double c) {

		switch (k) {
		case 400:
			return (a2 - b2 + 6 * a * c + c2 - 4 * u(a * c * (a - b + c)
					* (a + b + c)))
					* (a2 + 6 * a * b + b2 - c2 - 4 * u(a * b * (a + b - c)
							* (a + b + c)));
		case 401:
			return a8 + b2 * c2 * Q - 2 * a6 * R + a4 * (b4 + b2 * c2 + c4);
		case 402:
			return (2 * a4 - Q - a2 * R)
					* (a8 - a6 * R + 3 * a2 * Q * R + a4
							* (-2 * b4 + 5 * b2 * c2 - 2 * c4) - Q
							* (b4 + 3 * b2 * c2 + c4));
		case 403:
			return -(U * V * (a4 * R + Q * R - 2 * a2 * (b4 - b2 * c2 + c4)));
		case 404:
			return a * (a3 + b * c * (b + c) - a * (b2 - b * c + c2));
		case 405:
			return a * (a3 - 2 * b * c * (b + c) - a * p(b + c, 2));
		case 406:
			return U
					* V
					* (a3 - b3 - b2 * c - b * c2 - c3 + a2 * (b + c) - a
							* p(b + c, 2));
		case 407:
			return (b + c) * (-V) * U * (-2 * a2 + p(b - c, 2) - a * (b + c));
		case 408:
			return a3
					* (b + c)
					* (T * T)
					* (a4 * (b + c) - a2 * p(b - c, 2) * (b + c) - 2 * b
							* p(b - c, 2) * c * (b + c) - a * Q + a3 * R);
		case 409:
			return a
					* (a + b)
					* (a + c)
					* (a4 + a2 * b * c - a3 * (b + c) - p(b + c, 2)
							* (b2 - 3 * b * c + c2) + a
							* (b3 + b2 * c + b * c2 + c3));
		case 410:
			return (a + b)
					* (a + c)
					* U
					* V
					* (-(a7 * b * c * (b + c))
							+ a3
							* b
							* p(b - c, 2)
							* c
							* p(b + c, 3)
							- b2
							* p(b - c, 2)
							* c2
							* p(b + c, 4)
							- a
							* b
							* p(b - c, 2)
							* c
							* p(b + c, 3)
							* R
							+ a8
							* (b2 + 3 * b * c + c2)
							+ a5
							* b
							* c
							* (b3 + b2 * c + b * c2 + c3)
							- a2
							* Q
							* (b4 - b3 * c + b2 * c2 - b * c3 + c4)
							+ a4
							* p(b + c, 2)
							* (3 * b4 - 5 * b3 * c + 9 * b2 * c2 - 5 * b * c3 + 3 * c4) - a6
							* (3 * b4 + 5 * b3 * c + 3 * b2 * c2 + 5 * b * c3 + 3 * c4));
		case 411:
			return a
					* (a6 - a5 * (b + c) + b * c * Q + a2 * p(b2 + c2, 2) - a4
							* (2 * b2 + b * c + 2 * c2) + 2 * a3 * (b3 + c3) - a
							* (b5 - b4 * c - b * c4 + c5));
		case 412:
			return U
					* V
					* (a6 + a5 * (b + c) + b * c * Q + a2 * p(b2 + c2, 2) - a4
							* (2 * b2 + b * c + 2 * c2) - 2 * a3 * (b3 + c3) + a
							* (b5 - b4 * c - b * c4 + c5));
		case 413:
			return a
					* (a + b)
					* p(a - b - c, 3)
					* (a + c)
					* (a4 + a2 * b * c + a3 * (b + c) + a * p(b - c, 2)
							* (b + c) + p(b - c, 2) * (b2 + b * c + c2));
		case 414:
			return (a + b)
					* p(a - b - c, 3)
					* (a + c)
					* U
					* V
					* (b2 * p(b - c, 4) * c2 * p(b + c, 2) + a * b
							* p(b - c, 4) * c * p(b + c, 3) + a8
							* (b2 - b * c + c2) + a7
							* (2 * b3 - b2 * c - b * c2 + 2 * c3) - a5
							* p(b - c, 2)
							* (4 * b3 + 5 * b2 * c + 5 * b * c2 + 4 * c3) - a6
							* (b4 - 3 * b3 * c + 3 * b2 * c2 - 3 * b * c3 + c4)
							+ a2 * Q * (b4 + b3 * c - b2 * c2 + b * c3 + c4)
							- a4 * p(b - c, 2)
							* (b4 + 5 * b3 * c + 5 * b2 * c2 + 5 * b * c3 + c4) + a3
							* (2 * b7 - 3 * b6 * c + b5 * c2 + b2 * c5 - 3 * b
									* c6 + 2 * c7));
		case 415:
			return (a + b) * (a + c) * U * V
					* (a3 + b3 + a * b * c + c3 - 2 * a2 * (b + c));
		case 416:
			return a
					* (a + b)
					* (a + c)
					* (b2 * p(b - c, 2) * c2 * p(b + c, 3) - a2 * p(b - c, 2)
							* p(b + c, 3) * R + a3 * Q * (b2 - b * c + c2) + a
							* b * c * Q * (b2 - b * c + c2) + a7
							* (b2 + b * c + c2) - a6
							* (b3 + b2 * c + b * c2 + c3) - a5
							* (2 * b4 + b3 * c - b2 * c2 + b * c3 + 2 * c4) + a4
							* (2 * b5 + 2 * b4 * c - b3 * c2 - b2 * c3 + 2 * b
									* c4 + 2 * c5));
		case 417:
			return a4 * p(a2 - b2 - c2, 3) * (-2 * a2 * Q + a4 * R + Q * R);
		case 418:
			return a4 * (T * T) * (-Q + a2 * R);
		case 419:
			return (a2 - b * c) * (a2 + b * c) * U * V;
		case 420:
			return U * V * (a4 - b4 - b2 * c2 - c4 + a2 * R);
		case 421:
			return U
					* V
					* (a8 - b2 * c2 * Q - 2 * a6 * R + a4 * (b4 + b2 * c2 + c4));
		case 422:
			return (a + b) * (a + c) * U * V
					* (a3 + a * b * c - b * c * (b + c));
		case 423:
			return (a + b) * (a + c) * U * V
					* (a2 - b2 - b * c - c2 + a * (b + c));
		case 424:
			return (b + c) * (-V) * U
					* (b4 + c4 - a3 * (b + c) - a2 * R + a * (b3 + c3));
		case 425:
			return (a + b)
					* (a + c)
					* U
					* V
					* (a6 + 2 * a2 * b2 * c2 - a5 * (b + c) - 2 * a * b2 * c2
							* (b + c) - b * c * Q - a4 * (b2 - b * c + c2) + a3
							* (b3 + b2 * c + b * c2 + c3));
		case 426:
			return a2 * p(a2 - b2 - c2, 3) * (a4 + Q);
		case 427:
			return -(U * V * R);
		case 428:
			return U * V * (2 * a2 + b2 + c2);
		case 429:
			return (b + c) * (-V) * U * (b2 + c2 + a * (b + c));
		case 430:
			return (b + c) * (2 * a + b + c) * (-V) * U;
		case 431:
			return (b + c)
					* (-V)
					* U
					* (b5 + 2 * a3 * b * c - b4 * c - b * c4 + c5 + a4
							* (b + c) - 2 * a2 * p(b - c, 2) * (b + c));
		case 432:
			return -(U * V * (a10
					* a6
					* R
					+ p(b2 - c2, 8)
					* R
					- 8
					* a10
					* a4
					* (b4 + c4)
					- 8
					* a2
					* p(b2 - c2, 6)
					* (b4 + b2 * c2 + c4)
					+ 4
					* a10
					* a2
					* (7 * b6 - b4 * c2 - b2 * c4 + 7 * c6)
					- 8
					* a10
					* (7 * b8 - b6 * c2 - 4 * b4 * c4 - b2 * c6 + 7 * c8)
					- 8
					* a6
					* Q
					* (7 * b8 + 4 * b6 * c2 + 6 * b4 * c4 + 4 * b2 * c6 + 7 * c8)
					+ 4
					* a4
					* Q
					* (7 * b10 - 7 * b8 * c2 + 4 * b6 * c4 + 4 * b4 * c6 - 7
							* b2 * c8 + 7 * c10) + a8
					* (70 * b10 - 34 * b8 * c2 - 20 * b6 * c4 - 20 * b4 * c6
							- 34 * b2 * c8 + 70 * c10)));
		case 433:
			return -(U * V * (2 * a10 * Q + a10 * a2 * R - a8 * Q * R + 2 * a2
					* p(b2 - c2, 4) * p(b2 + c2, 2) + Q * p(R, 5) - 4 * a6 * Q
					* (b4 + c4) - a4 * Q
					* (b6 + 7 * b4 * c2 + 7 * b2 * c4 + c6)));
		case 434:
			return -(U * V * (a10
					* a6
					* R
					+ p(b2 - c2, 8)
					* R
					- 8
					* a10
					* a4
					* (b4 + b2 * c2 + c4)
					- 2
					* a2
					* p(b2 - c2, 6)
					* (4 * b4 + 5 * b2 * c2 + 4 * c4)
					+ a10
					* a2
					* (28 * b6 + 26 * b4 * c2 + 26 * b2 * c4 + 28 * c6)
					- 2
					* a10
					* (28 * b8 + 17 * b6 * c2 + 18 * b4 * c4 + 17 * b2 * c6 + 28 * c8)
					- 2
					* a6
					* Q
					* (28 * b8 + 22 * b6 * c2 + 25 * b4 * c4 + 22 * b2 * c6 + 28 * c8)
					+ a4
					* Q
					* (28 * b10 - 22 * b8 * c2 - 5 * b6 * c4 - 5 * b4 * c6 - 22
							* b2 * c8 + 28 * c10) + a8
					* (70 * b10 - 6 * b8 * c2 + 17 * b6 * c4 + 17 * b4 * c6 - 6
							* b2 * c8 + 70 * c10)));
		case 435:
			return -(U * V * (a10
					* a6
					* R
					+ p(b2 - c2, 8)
					* R
					- 8
					* a10
					* a4
					* (b4 - b2 * c2 + c4)
					- 2
					* a2
					* p(b2 - c2, 6)
					* (4 * b4 + 5 * b2 * c2 + 4 * c4)
					- 2
					* a10
					* p(b2 + c2, 2)
					* (28 * b4 - 55 * b2 * c2 + 28 * c4)
					+ a10
					* a2
					* (28 * b6 - 22 * b4 * c2 - 22 * b2 * c4 + 28 * c6)
					- 2
					* a6
					* Q
					* (28 * b8 + 46 * b6 * c2 + 81 * b4 * c4 + 46 * b2 * c6 + 28 * c8)
					+ a4
					* Q
					* (28 * b10 - 6 * b8 * c2 + 59 * b6 * c4 + 59 * b4 * c6 - 6
							* b2 * c8 + 28 * c10) + a8
					* (70 * b10 + 26 * b8 * c2 - 95 * b6 * c4 - 95 * b4 * c6
							+ 26 * b2 * c8 + 70 * c10)));
		case 436:
			return U
					* V
					* (a8 - b2 * c2 * Q - 2 * a6 * R + a4
							* (b4 + 3 * b2 * c2 + c4));
		case 437:
			return -(U * V * (5
					* a6
					- 6
					* a5
					* (b + c)
					- 2
					* a4
					* (4 * b2 - 9 * b * c + 4 * c2)
					- 3
					* a2
					* b
					* c
					* (4 * b2 - 7 * b * c + 4 * c2)
					+ a3
					* (11 * b3 - 5 * b2 * c - 5 * b * c2 + 11 * c3)
					+ p(b + c, 2)
					* (3 * b4 - 11 * b3 * c + 17 * b2 * c2 - 11 * b * c3 + 3 * c4) - a
					* (5 * b5 - 12 * b4 * c + 11 * b3 * c2 + 11 * b2 * c3 - 12
							* b * c4 + 5 * c5)));
		case 438:
			return U
					* V
					* (5 * a10 * a2 - 7 * a10 * R + 18 * a6 * Q * R + a8
							* (-8 * b4 + 26 * b2 * c2 - 8 * c4) - a4 * Q
							* (11 * b4 + 38 * b2 * c2 + 11 * c4) + a2 * Q
							* (5 * b6 + 27 * b4 * c2 + 27 * b2 * c4 + 5 * c6) - 2
							* Q
							* (b8 + 3 * b6 * c2 + 8 * b4 * c4 + 3 * b2 * c6 + c8));
		case 439:
			return p(-3 * a2 + b2 + c2, 2);
		case 440:
			return (b + c) * (-T)
					* (2 * a3 + a2 * (b + c) + p(b - c, 2) * (b + c));
		case 441:
			return T * (2 * a6 - a4 * R - Q * R);
		case 442:
			return (b + c)
					* (-2 * a * b * c - a2 * (b + c) + p(b - c, 2) * (b + c));
		case 443:
			return -a4 - 4 * a2 * b * c - 4 * a * b * c * (b + c) + Q;
		case 444:
			return a * (a2 + b * c) * U * V * (b2 + c2 + a * (b + c));
		case 445:
			return -(U * (a2 - b2 - b * c - c2) * V * (2 * a * b * c + a2
					* (b + c) - p(b - c, 2) * (b + c)));
		case 446:
			return a2
					* (-b4 - c4 + a2 * R)
					* (2 * a2 * b2 * c2 * Q + a8 * R + b2 * c2 * Q * R - 2 * a6
							* (b4 + c4) + a4 * (b6 + c6));
		case 447:
			return (a + b)
					* (a + c)
					* U
					* V
					* (a4 + a2 * b * c - a3 * (b + c) - p(b + c, 2)
							* (b2 - b * c + c2) + a
							* (b3 + b2 * c + b * c2 + c3));
		case 448:
			return (a + b)
					* (a + c)
					* (a6 - a5 * (b + c) + b * c * Q - a4 * (b2 + b * c + c2) + a3
							* (b3 + b2 * c + b * c2 + c3));
		case 449:
			return 2 * a8 - 8 * a5 * b * c * (b + c) + 4 * a * b * p(b - c, 2)
					* c * p(b + c, 3) - a2 * Q * (b2 - 4 * b * c + c2) - a6
					* (3 * b2 + 8 * b * c + 3 * c2) + 4 * a3 * b * c
					* (b3 + b2 * c + b * c2 + c3) + p(b4 - c4, 2) + a4
					* (b4 + 4 * b3 * c + 2 * b2 * c2 + 4 * b * c3 + c4);
		case 450:
			return U * V * (a4 - b * p(b - c, 2) * c - a2 * (b2 - b * c + c2))
					* (a4 + b * c * p(b + c, 2) - a2 * (b2 + b * c + c2));
		case 451:
			return U
					* V
					* (a3 - b3 - b2 * c - b * c2 - c3 + a2 * (b + c) - a
							* (b2 + b * c + c2));
		case 452:
			return (a - b - c)
					* (3 * a3 + 3 * a2 * (b + c) + p(b - c, 2) * (b + c) + a
							* p(b + c, 2));
		case 453:
			return a * (a + b) * (a - b - c) * (a + c)
					* p(a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a * R, 2);
		case 454:
			return a2
					* T
					* p(a6 - 3 * a4 * R - Q * R + a2
							* (3 * b4 - 2 * b2 * c2 + 3 * c4), 2);
		case 455:
			return a2 * U * V * p(a6 + a4 * R - Q * R - a2 * p(b2 + c2, 2), 2);
		case 456:
			return a2
					* U
					* V
					* p(a8 - 4 * a6 * R + Q * (b4 + c4) + a4
							* (6 * b4 + 5 * b2 * c2 + 6 * c4) + a2
							* (-4 * b6 + b4 * c2 + b2 * c4 - 4 * c6), 2);
		case 457:
			return a2
					* U
					* V
					* p(a8 - 4 * a6 * R + Q * (b4 + 4 * b2 * c2 + c4) + a4
							* (6 * b4 + b2 * c2 + 6 * c4) + a2
							* (-4 * b6 + b4 * c2 + b2 * c4 - 4 * c6), 2);
		case 458:
			return U * V * (a4 - 2 * b2 * c2 - a2 * R);
		case 459:
			return (3 * a2 - b2 - c2) * U * V;
		case 460:
			return U * V * (2 * a4 + Q - a2 * R);
		case 461:
			return (a - b - c) * (3 * a + b + c) * U * V;
		case 462:
			return (a - b - c) * (a + b - c) * (a - b + c) * (a + b + c) * U
					* V + u(3) * a2 * U * V * S;
		case 463:
			return -((a - b - c) * (a + b - c) * (a - b + c) * (a + b + c) * U * V)
					+ u(3) * a2 * U * V * S;
		case 464:
			return -(T * (a4 + 4 * a3 * (b + c) + 2 * a2 * p(b + c, 2) + Q));
		case 465:
			return T * U * V + u(3) * a2 * T * S;
		case 466:
			return -(T * U * V) + u(3) * a2 * T * S;
		case 467:
			return U * V * (a4 + b4 + c4 - 2 * a2 * R) * (-Q + a2 * R);
		case 468:
			return (2 * a2 - b2 - c2) * U * V;
		case 469:
			return -(U * V * (b2 + b * c + c2 + a * (b + c)));
		case 470:
			return 3 * T * U * V - u(3) * U * V * S;
		case 471:
			return -3 * T * U * V - u(3) * U * V * S;
		case 472:
			return -(T * U * V) - u(3) * U * V * S;
		case 473:
			return T * U * V - u(3) * U * V * S;
		case 474:
			return a * (a3 - a * p(b - c, 2) + 2 * b * c * (b + c));
		case 475:
			return U
					* V
					* (a3 - b3 - a * p(b - c, 2) - b2 * c - b * c2 - c3 + a2
							* (b + c));
		case 476:
			return (a - b) * (a + b) * (a - c) * (a + c)
					* (a2 - a * b + b2 - c2) * (a2 + a * b + b2 - c2)
					* (a2 - b2 - a * c + c2) * (a2 - b2 + a * c + c2);
		case 477:
			return (a8 + a6 * (b2 - 3 * c2) + b2 * p(b2 - c2, 3) + a4
					* (-4 * b4 + 2 * b2 * c2 + 3 * c4) + a2
					* (b6 + 2 * b4 * c2 - 2 * b2 * c4 - c6))
					* (a8 + a6 * (-3 * b2 + c2) + c2 * p(-b2 + c2, 3) + a4
							* (3 * b4 + 2 * b2 * c2 - 4 * c4) + a2
							* (-b6 - 2 * b4 * c2 + 2 * b2 * c4 + c6));
		case 478:
			return a2 * (a + b - c) * (a - b + c)
					* (a4 + 2 * a2 * b * c - 2 * a * b * c * (b + c) - Q);
		case 479:
			return -(p(a + b - c, 3) * p(a - b + c, 3));
		case 480:
			return a2 * p(a - b - c, 3);
		case 481:
			return -(a * (a - b - c) * (a + b - c) * (a - b + c)) - (a + b - c)
					* (a - b + c) * S;
		case 482:
			return a * (a - b - c) * (a + b - c) * (a - b + c) - (a + b - c)
					* (a - b + c) * S;
		case 483:
			return (2 * a * c + u(a * c * (a - b + c) * (a + b + c)))
					* (2 * a * b + u(a * b * (a + b - c) * (a + b + c)));
		case 484:
			return a
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* (b2 + b * c + c2));
		case 485:
			return a2 * b2 - b4 + a2 * c2 + 2 * b2 * c2 - c4 + a2 * S;
		case 486:
			return -(a2 * b2) + b4 - a2 * c2 - 2 * b2 * c2 + c4 + a2 * S;
		case 487:
			return 2 * a2 * T + (-T) * S;
		case 488:
			return -2 * a2 * T + (-T) * S;
		case 489:
			return 3 * a4 - 2 * a2 * b2 - b4 - 2 * a2 * c2 + 2 * b2 * c2 - c4
					+ (-T) * S;
		case 490:
			return -3 * a4 + 2 * a2 * b2 + b4 + 2 * a2 * c2 - 2 * b2 * c2 + c4
					+ (-T) * S;
		case 491:
			return a2 - b2 - c2 + S;
		case 492:
			return -a2 + b2 + c2 + S;
		case 493:
			return -(a2 * (a4 - 2 * a2 * b2 + b4 - 2 * a2 * c2 - 6 * b2 * c2 + c4))
					+ 2 * a2 * R * S;
		case 494:
			return a2
					* (a4 - 2 * a2 * b2 + b4 - 2 * a2 * c2 - 6 * b2 * c2 + c4)
					+ 2 * a2 * R * S;
		case 495:
			return Q - a2 * (b2 + 4 * b * c + c2);
		case 496:
			return Q - a2 * (b2 - 4 * b * c + c2);
		case 497:
			return -((a2 + p(b - c, 2)) * (a - b - c));
		case 498:
			return a4 + Q - 2 * a2 * (b2 + b * c + c2);
		case 499:
			return a4 + Q - 2 * a2 * (b2 - b * c + c2);
		default:
			return Double.NaN;
		}
	}

	private double weight500to599(int k, double a, double b, double c) {

		switch (k) {
		case 500:
			return a2 * (a2 - b2 - b * c - c2)
					* (2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 501:
			return a2
					* (a + b)
					* (a + c)
					* (a3 - b3 - b2 * c - b * c2 - c3 + a2 * (b + c) - a
							* (b2 + b * c + c2));
		case 502:
			return (b + c)
					* (-a3 - a2 * (b + c) + (b - c) * p(b + c, 2) + a
							* (b2 - b * c - c2))
					* (a3 + a2 * (b + c) + (b - c) * p(b + c, 2) + a
							* (b2 + b * c - c2));
		case 503:
			return a
					* (-(b * (a + b - c) * c * (a - b + c) * u(a * (-a + b + c)))
							+ a
							* (a + b - c)
							* c
							* (-a + b + c)
							* u(b * (a - b + c)) + a * b * (a - b + c)
							* (-a + b + c) * u(c * (a + b - c)));
		case 504:
			return a
					* (-(a2 * (a + b - c) * (a - b + c) * u(-(b * (a - b - c)
							* c * (a + b + c))))
							+ b2
							* (a + b - c)
							* (-a + b + c)
							* u(a * c * (a - b + c) * (a + b + c)) + c2
							* (a - b + c) * (-a + b + c)
							* u(a * b * (a + b - c) * (a + b + c)));
		case 505:
			return a
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2) + 2 * (-a + b + c)
							* u(b * (a - b + c)) * u(c * (a + b - c)));
		case 506:
			return -((a + b - c) * (a - b + c) * p(a * (-a + b + c), 2 / 3));
		case 507:
			return -((a + b - c) * (a - b + c) * p(a * (-a + b + c), 3 / 4));
		case 508:
			return u(a - b + c) * u(a + b - c);
		case 509:
			return a * u(a - b + c) * u(a + b - c);
		case 510:
			return a * (-a * u(a) + b * u(b) + c * u(c));
		case 511:
			return a2 * (-b4 - c4 + a2 * R);
		case 512:
			return a2 * (b2 - c2);
		case 513:
			return a * (b - c);
		case 514:
			return b - c;
		case 515:
			return 2 * a4 - a2 * p(b - c, 2) - a3 * (b + c) + a * p(b - c, 2)
					* (b + c) - Q;
		case 516:
			return 2 * a3 - a2 * (b + c) - p(b - c, 2) * (b + c);
		case 517:
			return a * (-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 518:
			return a * (-b2 - c2 + a * (b + c));
		case 519:
			return 2 * a - b - c;
		case 520:
			return a2 * (b2 - c2) * (T * T);
		case 521:
			return a * (a - b - c) * (b - c) * T;
		case 522:
			return (a - b - c) * (b - c);
		case 523:
			return (b2 - c2);
		case 524:
			return 2 * a2 - b2 - c2;
		case 525:
			return -((b2 - c2) * (-T));
		case 526:
			return a2 * (b6 - c6 + a4 * (b2 - c2) - 2 * a2 * (b4 - c4));
		case 527:
			return 2 * a2 - p(b - c, 2) - a * (b + c);
		case 528:
			return 2 * a3 - 2 * a2 * (b + c) - p(b - c, 2) * (b + c) + a * R;
		case 529:
			return 2 * a4 - 2 * a * b * c * (b + c) - Q - a2
					* (b2 - 4 * b * c + c2);
		case 530:
			return 3 * (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4)
					+ u(3) * (2 * a2 - b2 - c2) * S;
		case 531:
			return 3 * (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4)
					- u(3) * (2 * a2 - b2 - c2) * S;
		case 532:
			return 2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4 + u(3)
					* (2 * a2 - b2 - c2) * S;
		case 533:
			return 2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4 - u(3)
					* (2 * a2 - b2 - c2) * S;
		case 534:
			return 2 * a5 - b5 + b4 * c + b * c4 - c5 + a4 * (b + c) - 2 * a2
					* b * c * (b + c) - 2 * a * Q;
		case 535:
			return 2 * a4 - a2 * p(b - c, 2) - a * b * c * (b + c) - Q;
		case 536:
			return -2 * b * c + a * (b + c);
		case 537:
			return a2 * (b + c) + b * c * (b + c) - 2 * a * R;
		case 538:
			return -2 * b2 * c2 + a2 * R;
		case 539:
			return T
					* (2 * a8 + p(b2 - c2, 4) - 4 * a6 * R - 2 * a2 * Q * R + 3
							* a4 * (b4 + c4));
		case 540:
			return 2 * a4 + 2 * a2 * b * c + 2 * a3 * (b + c) - p(b + c, 2)
					* (b2 - b * c + c2) - a * (b3 + b2 * c + b * c2 + c3);
		case 541:
			return 2 * a10 - p(b2 - c2, 4) * R + a6
					* (-11 * b4 + 16 * b2 * c2 - 11 * c4) - a2 * Q
					* (3 * b4 + 14 * b2 * c2 + 3 * c4) + a4
					* (13 * b6 - 11 * b4 * c2 - 11 * b2 * c4 + 13 * c6);
		case 542:
			return 2 * a6 - 2 * a4 * R - Q * R + a2 * (b4 + c4);
		case 543:
			return 2 * a4 - b4 + 4 * b2 * c2 - c4 - 2 * a2 * R;
		case 544:
			return 2 * a4 + 2 * a2 * b * c - 2 * a3 * (b + c) + a * p(b - c, 2)
					* (b + c) - p(b - c, 2) * (b2 + b * c + c2);
		case 545:
			return 2 * a2 - b2 + 4 * b * c - c2 - 2 * a * (b + c);
		case 546:
			return -2 * a4 + 3 * Q - a2 * R;
		case 547:
			return 2 * a4 + 5 * Q - 7 * a2 * R;
		case 548:
			return 6 * a4 - Q - 5 * a2 * R;
		case 549:
			return 4 * a4 + Q - 5 * a2 * R;
		case 550:
			return 4 * a4 - Q - 3 * a2 * R;
		case 551:
			return 4 * a + b + c;
		case 552:
			return p(a + b, 2) * (a + b - c) * p(a + c, 2) * (a - b + c);
		case 553:
			return (a + b - c) * (a - b + c) * (2 * a + b + c);
		case 554:
			return 1 / (u(3) * (-a + b + c) * (a + b + c) + S);
		case 555:
			return b * p(a + b - c, 2) * c * p(a - b + c, 2)
					* u(a * (-a + b + c));
		case 556:
			return b * c * u(a * (-a + b + c));
		case 557:
			return a * (2 * b * c + u(-(b * (a - b - c) * c * (a + b + c))));
		case 558:
			return a * (2 * b * c - u(-(b * (a - b - c) * c * (a + b + c))));
		case 559:
			return a * (a + b - c) * (a - b + c) + u(3) * a * S;
		case 560:
			return a5;
		case 561:
			return b3 * c3;
		case 562:
			return -(U * (a2 - b2 - b * c - c2) * (a2 - b2 + b * c - c2) * V
					* (a4 + Q - a2 * (2 * b2 + c2)) * (a4 + Q - a2
					* (b2 + 2 * c2)));
		case 563:
			return a5 * T * (a4 + b4 + c4 - 2 * a2 * R);
		case 564:
			return b * c * (p(b2 - c2, 4) - 2 * a2 * Q * R + a4 * (b4 + c4));
		case 565:
			return b2
					* c2
					* (Q - a2 * R)
					* (p(b2 - c2, 4) - 2 * a2 * Q * R + a4
							* (b4 - b2 * c2 + c4));
		case 566:
			return a2 * (a4 * R + Q * R - a2 * (2 * b4 + b2 * c2 + 2 * c4));
		case 567:
			return a2
					* (a8 - 2 * b2 * c2 * Q - 3 * a6 * R + 3 * a4
							* (b4 + b2 * c2 + c4) - a2
							* (b6 - 2 * b4 * c2 - 2 * b2 * c4 + c6));
		case 568:
			return a2
					* (a6 * R - Q * (b4 + c4) - a4
							* (3 * b4 + b2 * c2 + 3 * c4) + a2
							* (3 * b6 - 2 * b4 * c2 - 2 * b2 * c4 + 3 * c6));
		case 569:
			return a2
					* (a8 - 2 * b2 * c2 * Q - 3 * a6 * R + a4
							* (3 * b4 + 2 * b2 * c2 + 3 * c4) - a2
							* (b6 - 3 * b4 * c2 - 3 * b2 * c4 + c6));
		case 570:
			return a2 * (a4 * R + Q * R - 2 * a2 * (b4 + b2 * c2 + c4));
		case 571:
			return a4 * (a4 + b4 + c4 - 2 * a2 * R);
		case 572:
			return a2 * (a3 - b * c * (b + c) - a * (b2 - b * c + c2));
		case 573:
			return a2 * (-b3 - a * b * c - c3 + a2 * (b + c));
		case 574:
			return a2 * (a2 - 2 * R);
		case 575:
			return a2 * (2 * a4 + b4 - 4 * b2 * c2 + c4 - 3 * a2 * R);
		case 576:
			return a2 * (a4 - 3 * a2 * R + 2 * (b4 - b2 * c2 + c4));
		case 577:
			return a4 * (T * T);
		case 578:
			return a2
					* (a8 - 2 * b2 * c2 * Q - 3 * a6 * R - a2 * Q * R + a4
							* (3 * b4 + 4 * b2 * c2 + 3 * c4));
		case 579:
			return a2 * (-b3 + a * b * c - c3 + a2 * (b + c));
		case 580:
			return a2
					* (a5 - a2 * b * c * (b + c) + b * p(b - c, 2) * c
							* (b + c) - a3 * (2 * b2 + b * c + 2 * c2) + a
							* (b4 + b3 * c + b * c3 + c4));
		case 581:
			return a2
					* (b5 + a3 * b * c - b3 * c2 - b2 * c3 + c5 + a4 * (b + c)
							- a * b * c * p(b + c, 2) - a2
							* (2 * b3 + b2 * c + b * c2 + 2 * c3));
		case 582:
			return a2
					* (a5 - a2 * b * c * (b + c) + b * p(b - c, 2) * c
							* (b + c) - 2 * a3 * (b2 + b * c + c2) + a
							* (b4 + 2 * b3 * c + 2 * b * c3 + c4));
		case 583:
			return a2 * (-b3 + 2 * a * b * c - c3 + a2 * (b + c));
		case 584:
			return a2 * (a3 - b * c * (b + c) - a * p(b + c, 2));
		case 585:
			return -2 * a * b * (a - b - c) * c + (a * b + a * c - b * c) * S;
		case 586:
			return 2 * a * b * (a - b - c) * c + (a * b + a * c - b * c) * S;
		case 587:
			return U * V * (2 * (a + b + c) * (-T) - (-a + b + c) * S);
		case 588:
			return a2 / (a2 + S);
		case 589:
			return a2 / (a2 - S);
		case 590:
			return a2 + S;
		case 591:
			return -2 * a6 + 5 * a4 * R + Q * R - 4 * a2 * (b4 + c4) + 2
					* (Q - a2 * R) * S;
		case 592:
			return a2
					* (a6 * (b2 + 2 * c2) + b2 * c2
							* (2 * b4 - 3 * b2 * c2 + c4) - a4
							* (2 * b4 + 3 * b2 * c2 + 3 * c4) + a2
							* (b6 - 3 * b4 * c2 - 6 * b2 * c4 + c6))
					* (a6 * (2 * b2 + c2) + b2 * c2
							* (b4 - 3 * b2 * c2 + 2 * c4) - a4
							* (3 * b4 + 3 * b2 * c2 + 2 * c4) + a2
							* (b6 - 6 * b4 * c2 - 3 * b2 * c4 + c6));
		case 593:
			return a2 * p(a + b, 2) * p(a + c, 2);
		case 594:
			return p(b + c, 2);
		case 595:
			return a2 * (a2 - b * c + a * (b + c));
		case 596:
			return -((a * (b - c) + b * (b + c)) * (a * (b - c) - c * (b + c)));
		case 597:
			return 4 * a2 + b2 + c2;
		case 598:
			return (2 * a2 + 2 * b2 - c2) * (2 * a2 - b2 + 2 * c2);
		case 599:
			return -a2 + 2 * R;
		default:
			return Double.NaN;
		}
	}

	private double weight600to699(int k, double a, double b, double c) {

		switch (k) {
		case 600:
			return a2 * (2 * a2 - a * b + 2 * b2 - 2 * c2)
					* (2 * a2 - 2 * b2 - a * c + 2 * c2)
					* (-(b2 * c2) + a2 * R);
		case 601:
			return a3
					* (a4 + b4 - 2 * b3 * c - 2 * b2 * c2 - 2 * b * c3 + c4 - 2
							* a2 * (b2 - b * c + c2));
		case 602:
			return a3
					* (a4 + b4 + 2 * b3 * c - 2 * b2 * c2 + 2 * b * c3 + c4 - 2
							* a2 * (b2 + b * c + c2));
		case 603:
			return a3 * (a + b - c) * (a - b + c) * T;
		case 604:
			return a3 * (a + b - c) * (a - b + c);
		case 605:
			return 2 * a3 * b * c + a3 * S;
		case 606:
			return -2 * a3 * b * c + a3 * S;
		case 607:
			return a2 * (a - b - c) * U * V;
		case 608:
			return a2 * (a + b - c) * (a - b + c) * U * V;
		case 609:
			return 2 * a4 + a2 * b * c;
		case 610:
			return a * (3 * a4 - Q - 2 * a2 * R);
		case 611:
			return a2
					* (a4 + b4 - 2 * b3 * c - 2 * b2 * c2 - 2 * b * c3 + c4 - 2
							* a2 * (b2 + b * c + c2));
		case 612:
			return a * (a2 + p(b + c, 2));
		case 613:
			return a2
					* (a4 + b4 + 2 * b3 * c - 2 * b2 * c2 + 2 * b * c3 + c4 - 2
							* a2 * (b2 - b * c + c2));
		case 614:
			return a * (a2 + p(b - c, 2));
		case 615:
			return -a2 + S;
		case 616:
			return -5 * a4 + 4 * a2 * b2 + b4 + 4 * a2 * c2 - 2 * b2 * c2 + c4
					- u(3) * T * S;
		case 617:
			return 5 * a4 - 4 * a2 * b2 - b4 - 4 * a2 * c2 + 2 * b2 * c2 - c4
					- u(3) * T * S;
		case 618:
			return -4 * a4 + 5 * a2 * b2 - b4 + 5 * a2 * c2 + 2 * b2 * c2 - c4
					+ u(3) * R * S;
		case 619:
			return 4 * a4 - 5 * a2 * b2 + b4 - 5 * a2 * c2 - 2 * b2 * c2 + c4
					+ u(3) * R * S;
		case 620:
			return 2 * a4 + b4 + c4 - 2 * a2 * R;
		case 621:
			return 3 * U * V - u(3) * T * S;
		case 622:
			return -3 * U * V - u(3) * T * S;
		case 623:
			return 3 * (a2 * b2 - b4 + a2 * c2 + 2 * b2 * c2 - c4) + u(3) * R
					* S;
		case 624:
			return -3 * (a2 * b2 - b4 + a2 * c2 + 2 * b2 * c2 - c4) + u(3) * R
					* S;
		case 625:
			return -(a2 * R) + 2 * (b4 - b2 * c2 + c4);
		case 626:
			return b4 + c4;
		case 627:
			return -3 * a4 + 4 * a2 * b2 - b4 + 4 * a2 * c2 + 2 * b2 * c2 - c4
					- u(3) * T * S;
		case 628:
			return 3 * a4 - 4 * a2 * b2 + b4 - 4 * a2 * c2 - 2 * b2 * c2 + c4
					- u(3) * T * S;
		case 629:
			return -4 * a4 + 7 * a2 * b2 - 3 * b4 + 7 * a2 * c2 + 6 * b2 * c2
					- 3 * c4 + u(3) * R * S;
		case 630:
			return 4 * a4 - 7 * a2 * b2 + 3 * b4 - 7 * a2 * c2 - 6 * b2 * c2
					+ 3 * c4 + u(3) * R * S;
		case 631:
			return 3 * a4 + Q - 4 * a2 * R;
		case 632:
			return 4 * a4 + 3 * Q - 7 * a2 * R;
		case 633:
			return U * V - u(3) * T * S;
		case 634:
			return -(U * V) - u(3) * T * S;
		case 635:
			return a2 * b2 - b4 + a2 * c2 + 2 * b2 * c2 - c4 + u(3) * R * S;
		case 636:
			return -(a2 * b2) + b4 - a2 * c2 - 2 * b2 * c2 + c4 + u(3) * R * S;
		case 637:
			return U * V + (-T) * S;
		case 638:
			return -(U * V) + (-T) * S;
		case 639:
			return a2 * b2 - b4 + a2 * c2 + 2 * b2 * c2 - c4 + R * S;
		case 640:
			return -(a2 * b2) + b4 - a2 * c2 - 2 * b2 * c2 + c4 + R * S;
		case 641:
			return -2 * a4 + 3 * a2 * b2 - b4 + 3 * a2 * c2 + 2 * b2 * c2 - c4
					+ R * S;
		case 642:
			return 2 * a4 - 3 * a2 * b2 + b4 - 3 * a2 * c2 - 2 * b2 * c2 + c4
					+ R * S;
		case 643:
			return a * (a - b) * (a + b) * (a - c) * (a - b - c) * (a + c);
		case 644:
			return a * (a - b) * (a - c) * (a - b - c);
		case 645:
			return (a - b) * (a + b) * (a - c) * (a - b - c) * (a + c);
		case 646:
			return -((a - b) * b * (a - c) * (a - b - c) * c);
		case 647:
			return a2 * (b2 - c2) * T;
		case 648:
			return (a - b) * (a + b) * (a - c) * (a + c) * U * V;
		case 649:
			return a2 * (b - c);
		case 650:
			return a * (a - b - c) * (b - c);
		case 651:
			return a * (a - b) * (a - c) * (a + b - c) * (a - b + c);
		case 652:
			return a2 * (a - b - c) * (b - c) * T;
		case 653:
			return (a - b) * (a - c) * (a + b - c) * (a - b + c) * U * V;
		case 654:
			return a2 * (a - b - c) * (b - c) * (a2 - b2 + b * c - c2);
		case 655:
			return (a - b) * (a - c) * (a + b - c) * (a - b + c)
					* (a2 - a * b + b2 - c2) * (a2 - b2 - a * c + c2);
		case 656:
			return a * (b2 - c2) * T;
		case 657:
			return a2 * (b - c) * p(-a + b + c, 2);
		case 658:
			return (a - b) * (a - c) * p(a + b - c, 2) * p(a - b + c, 2);
		case 659:
			return a * (b - c) * (a2 - b * c);
		case 660:
			return a * (a - b) * (a - c) * (-b2 + a * c) * (a * b - c2);
		case 661:
			return -(a * (b2 - c2));
		case 662:
			return a * (a - b) * (a + b) * (a - c) * (a + c);
		case 663:
			return a2 * (a - b - c) * (b - c);
		case 664:
			return (a - b) * (a - c) * (a + b - c) * (a - b + c);
		case 665:
			return a2 * (b - c) * (-b2 - c2 + a * (b + c));
		case 666:
			return (a - b) * (a - c) * (a2 + b * (b - c) - a * c)
					* (a2 - a * b + c * (-b + c));
		case 667:
			return a3 * (b - c);
		case 668:
			return (a - b) * b * (a - c) * c;
		case 669:
			return a4 * (b2 - c2);
		case 670:
			return (a - b) * b2 * (a + b) * (a - c) * c2 * (a + c);
		case 671:
			return -((a2 + b2 - 2 * c2) * (a2 - 2 * b2 + c2));
		case 672:
			return a2 * (-b2 - c2 + a * (b + c));
		case 673:
			return (a2 + b * (b - c) - a * c) * (a2 - a * b + c * (-b + c));
		case 674:
			return a2 * (-b3 - c3 + a * R);
		case 675:
			return (a3 + b2 * (b - c) - a2 * c) * (a3 - a2 * b + c2 * (-b + c));
		case 676:
			return -((b - c) * (-2 * a3 + a2 * (b + c) + p(b - c, 2) * (b + c)));
		case 677:
			return a2 * (a - b) * (a - c)
					* (a3 - 2 * b3 - a2 * c + b2 * c + c3 + a * (b2 - c2))
					* (a3 - a2 * b + b3 + b * c2 - 2 * c3 + a * (-b2 + c2));
		case 678:
			return a * p(-2 * a + b + c, 2);
		case 679:
			return a * p(a + b - 2 * c, 2) * p(a - 2 * b + c, 2);
		case 680:
			return a3
					* (b - c)
					* (T * T)
					* (a4 * (b2 + b * c + c2) + Q * (b2 + b * c + c2) - 2 * a2
							* (b4 + b3 * c + b * c3 + c4));
		case 681:
			return 1 / (a * (b - c) * (T * T) * (a4 * (b2 + b * c + c2) + Q
					* (b2 + b * c + c2) - 2 * a2 * (b4 + b3 * c + b * c3 + c4)));
		case 682:
			return a4 * T * (Q + a2 * R);
		case 683:
			return b2 * c2 * (-V) * U * (a4 + a2 * (-2 * b2 + c2) + b2 * R)
					* (a4 + a2 * (b2 - 2 * c2) + c2 * R);
		case 684:
			return a2 * (-b8 + c8 + a4 * (-b4 + c4) + 2 * a2 * (b6 - c6));
		case 685:
			return (a - b) * (a + b) * (a - c) * (a + c) * U * V
					* (a4 + b4 - a2 * c2 - b2 * c2)
					* (a4 - a2 * b2 - b2 * c2 + c4);
		case 686:
			return a2 * (b2 - c2) * T
					* (a4 * R + Q * R - 2 * a2 * (b4 - b2 * c2 + c4));
		case 687:
			return (a - b)
					* (a + b)
					* (a - c)
					* (a + c)
					* U
					* V
					* (a6 - a4 * (2 * b2 + c2) + p(-(b2 * c) + c3, 2) + a2
							* (b4 + 2 * b2 * c2 - c4))
					* (a6 - a4 * (b2 + 2 * c2) + p(b3 - b * c2, 2) + a2
							* (-b4 + 2 * b2 * c2 + c4));
		case 688:
			return a4 * (b4 - c4);
		case 689:
			return (a - b) * b2 * (a + b) * (a2 + b2) * (a - c) * c2 * (a + c)
					* (a2 + c2);
		case 690:
			return -((b2 - c2) * (-2 * a2 + b2 + c2));
		case 691:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c)
					* (a2 + b2 - 2 * c2) * (a2 - 2 * b2 + c2);
		case 692:
			return a3 * (a - b) * (a - c);
		case 693:
			return b * (b - c) * c;
		case 694:
			return -(a6 * b2 * c2) - a2 * b4 * c4 + a4 * (b6 + c6);
		case 695:
			return a2 * (b4 + a2 * c2) * (a2 * b2 + c4);
		case 696:
			return -(b * c * (b3 + c3)) + a * (b4 + c4);
		case 697:
			return a2 * (a * b4 + a4 * (b - c) - b4 * c)
					* (a4 * (b - c) - a * c4 + b * c4);
		case 698:
			return -(b2 * c2 * R) + a2 * (b4 + c4);
		case 699:
			return a2 * (a2 * b4 - b4 * c2 + a4 * (b2 - c2))
					* (-(a2 * c4) + b2 * c4 + a4 * (b2 - c2));
		default:
			return Double.NaN;
		}
	}

	private double weight700to799(int k, double a, double b, double c) {

		switch (k) {
		case 700:
			return -(b3 * c3 * (b + c)) + a3 * (b4 + c4);
		case 701:
			return a2 * (a3 * b4 - b4 * c3 + a4 * (b3 - c3))
					* (-(a3 * c4) + b3 * c4 + a4 * (b3 - c3));
		case 702:
			return -2 * b4 * c4 + a4 * (b4 + c4);
		case 703:
			return a2 * (b4 * c4 + a4 * (b4 - 2 * c4))
					* (-(b4 * c4) + a4 * (2 * b4 - c4));
		case 704:
			return -(b4 * c4 * (b + c)) + a5 * (b4 + c4);
		case 705:
			return a2 * (a5 * b4 - b4 * c5 + a4 * (b5 - c5))
					* (a5 * c4 - b5 * c4 + a4 * (-b5 + c5));
		case 706:
			return -(b4 * c4 * R) + a6 * (b4 + c4);
		case 707:
			return a2 * (a6 * b4 - b4 * c6 + a4 * (b6 - c6))
					* (a6 * c4 - b6 * c4 + a4 * (-b6 + c6));
		case 708:
			return -(b4 * c4 * (b3 + c3)) + a7 * (b4 + c4);
		case 709:
			return a2 * (a7 * b4 - b4 * c7 + a4 * (b7 - c7))
					* (a7 * c4 - b7 * c4 + a4 * (-b7 + c7));
		case 710:
			return (b4 + c4) * (a8 - b4 * c4);
		case 711:
			return a2 * (a4 + b4) * (-b2 + a * c) * (b2 + a * c) * (a * b - c2)
					* (a * b + c2) * (b4 + a2 * c2) * (a4 + c4)
					* (a2 * b2 + c4);
		case 712:
			return -(b * c * R) + a * (b3 + c3);
		case 713:
			return a2 * (a * b3 + a3 * (b - c) - b3 * c)
					* (a3 * (b - c) - a * c3 + b * c3);
		case 714:
			return -(b2 * c2 * (b + c)) + a2 * (b3 + c3);
		case 715:
			return a2 * (a + b) * (a + c)
					* (a * b * c2 - b2 * c2 + a2 * (b2 - c2))
					* (-(a * b2 * c) + b2 * c2 + a2 * (b2 - c2));
		case 716:
			return -2 * b3 * c3 + a3 * (b3 + c3);
		case 717:
			return a2 * (b3 * c3 + a3 * (b3 - 2 * c3))
					* (-(b3 * c3) + a3 * (2 * b3 - c3));
		case 718:
			return -(b3 * c3 * (b + c)) + a4 * (b3 + c3);
		case 719:
			return a2 * (a + b) * (a + c)
					* (-(a2 * b4) + a * b4 * c - b4 * c2 + a3 * c3)
					* (a3 * b3 - a2 * c4 + a * b * c4 - b2 * c4);
		case 720:
			return -(b3 * c3 * R) + a5 * (b3 + c3);
		case 721:
			return a2 * (a5 * b3 - b3 * c5 + a3 * (b5 - c5))
					* (a5 * c3 - b5 * c3 + a3 * (-b5 + c5));
		case 722:
			return (b3 + c3) * (a6 - b3 * c3);
		case 723:
			return a2 * (a + b) * (a2 - a * b + b2) * (a + c) * (-b2 + a * c)
					* (a * b - c2) * (a2 - a * c + c2)
					* (b4 + a * b2 * c + a2 * c2) * (a2 * b2 + a * b * c2 + c4);
		case 724:
			return a7 * (b3 + c3) - b3 * c3 * (b4 + c4);
		case 725:
			return a2 * (a7 * b3 - b3 * c7 + a3 * (b7 - c7))
					* (a7 * c3 - b7 * c3 + a3 * (-b7 + c7));
		case 726:
			return -(b * c * (b + c)) + a * R;
		case 727:
			return a2 * (a * b2 + a2 * (b - c) - b2 * c)
					* (a2 * (b - c) - a * c2 + b * c2);
		case 728:
			return a * p(a - b - c, 3);
		case 729:
			return a2 * (b2 * c2 + a2 * (b2 - 2 * c2))
					* (-(b2 * c2) + a2 * (2 * b2 - c2));
		case 730:
			return -(b2 * c2 * (b + c)) + a3 * R;
		case 731:
			return a2 * (a3 * b2 - b2 * c3 + a2 * (b3 - c3))
					* (a3 * c2 - b3 * c2 + a2 * (-b3 + c3));
		case 732:
			return (a2 - b * c) * (a2 + b * c) * R;
		case 733:
			return a2 * (a2 + b2) * (-b2 + a * c) * (b2 + a * c) * (a * b - c2)
					* (a2 + c2) * (a * b + c2);
		case 734:
			return a5 * R - b2 * c2 * (b3 + c3);
		case 735:
			return a2 * (a5 * b2 - b2 * c5 + a2 * (b5 - c5))
					* (a5 * c2 - b5 * c2 + a2 * (-b5 + c5));
		case 736:
			return a6 * R - b2 * c2 * (b4 + c4);
		case 737:
			return a2 * (a6 * b2 - b2 * c6 + a2 * (b6 - c6))
					* (a6 * c2 - b6 * c2 + a2 * (-b6 + c6));
		case 738:
			return a * p(a + b - c, 3) * p(a - b + c, 3);
		case 739:
			return a2 * (2 * a * b - a * c - b * c) * (a * (b - 2 * c) + b * c);
		case 740:
			return (b + c) * (-a2 + b * c);
		case 741:
			return a2 / ((b + c) * (-a2 + b * c));
		case 742:
			return a3 * (b + c) - b * c * R;
		case 743:
			return a2 * (a3 * b - b * c3 + a * (b3 - c3))
					* (a3 * c - b3 * c + a * (-b3 + c3));
		case 744:
			return a4 * (b + c) - b * c * (b3 + c3);
		case 745:
			return a2 * (a + b) * (a + c) * (a3 * b - a2 * b2 + a * b3 - c4)
					* (-b4 + a * c * (a2 - a * c + c2));
		case 746:
			return a5 * (b + c) - b * c * (b4 + c4);
		case 747:
			return a2 / (a5 * (b + c) - b * c * (b4 + c4));
		case 748:
			return a3 - 2 * a * b * c;
		case 749:
			return -(a * (-b2 + 2 * a * c) * (2 * a * b - c2));
		case 750:
			return a3 + 2 * a * b * c;
		case 751:
			return a * (b2 + 2 * a * c) * (2 * a * b + c2);
		case 752:
			return 2 * a3 - b3 - c3;
		case 753:
			return a2 * (a3 + b3 - 2 * c3) * (a3 - 2 * b3 + c3);
		case 754:
			return 2 * a4 - b4 - c4;
		case 755:
			return a2 * (a4 + b4 - 2 * c4) * (a4 - 2 * b4 + c4);
		case 756:
			return a * p(b + c, 2);
		case 757:
			return a * p(a + b, 2) * p(a + c, 2);
		case 758:
			return a * (b + c) * (a2 - b2 + b * c - c2);
		case 759:
			return a * (a + b) * (a + c) * (a2 - a * b + b2 - c2)
					* (a2 - b2 - a * c + c2);
		case 760:
			return a * (-b4 - c4 + a3 * (b + c));
		case 761:
			return a * (a4 + b4 - a * c3 - b * c3)
					* (a4 - a * b3 - b3 * c + c4);
		case 762:
			return a * p(b + c, 3);
		case 763:
			return a * p(a + b, 3) * p(a + c, 3);
		case 764:
			return a * p(b - c, 3);
		case 765:
			return a * p(a - b, 2) * p(a - c, 2);
		case 766:
			return a3 * (-b4 - c4 + a * (b3 + c3));
		case 767:
			return b * c * (a4 + b3 * (b - c) - a3 * c)
					* (-a4 + a3 * b + (b - c) * c3);
		case 768:
			return a * b4 + b4 * c - a * c4 - b * c4;
		case 769:
			return a2
					* (a - b)
					* (a - c)
					* (b3 * c + a3 * (b + c) + a2 * b * (b + c) + a * b2
							* (b + c))
					* (b * c3 + a3 * (b + c) + a2 * c * (b + c) + a * c2
							* (b + c));
		case 770:
			return a
					* (a - b - c)
					* (b - c)
					* (-2 * a2 * Q + Q * (b2 - b * c + c2) + a4
							* (b2 + b * c + c2));
		case 771:
			return a
					* (a - b)
					* (a - c)
					* (a + b - c)
					* (a - b + c)
					* (a6 - a5 * c + 2 * a3 * c3 - a4 * (2 * b2 + c2)
							+ p(-(b2 * c) + c3, 2) + a * c * (b4 - c4) + a2
							* (b4 + 4 * b2 * c2 - c4))
					* (a6 - a5 * b + 2 * a3 * b3 - a4 * (b2 + 2 * c2)
							+ p(b3 - b * c2, 2) + a2 * (-b4 + 4 * b2 * c2 + c4) + a
							* (-b5 + b * c4));
		case 772:
			return b3 * (b - c) * c3 + a3 * (b4 - c4);
		case 773:
			return a2 * (a - b) * (a - c)
					* (a2 * b3 * c + a * b3 * c2 + b3 * c3 + a3 * (b3 + c3))
					* (a2 * b * c3 + a * b2 * c3 + b3 * c3 + a3 * (b3 + c3));
		case 774:
			return a * (-2 * a2 * Q + a4 * R + Q * R);
		case 775:
			return a
					* (a6 - a4 * (2 * b2 + c2) + p(-(b2 * c) + c3, 2) + a2
							* (b4 + 4 * b2 * c2 - c4))
					* (a6 - a4 * (b2 + 2 * c2) + p(b3 - b * c2, 2) + a2
							* (-b4 + 4 * b2 * c2 + c4));
		case 776:
			return b4 * c4 * (-b + c) + a5 * (b4 - c4);
		case 777:
			return a2
					* (a - b)
					* (a - c)
					* (-(a3 * b5) - a2 * b5 * c - a * b5 * c2 - b5 * c3 + a4
							* c4)
					* (a4 * b4 - a3 * c5 - a2 * b * c5 - a * b2 * c5 - b3 * c5);
		case 778:
			return -(b6 * c4) + b4 * c6 + a6 * (b4 - c4);
		case 779:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c)
					* (-(a2 * b6) - b6 * c2 + a4 * c4)
					* (a4 * b4 - a2 * c6 - b2 * c6);
		case 780:
			return -(b7 * c4) + b4 * c7 + a7 * (b4 - c4);
		case 781:
			return a2
					* (a - b)
					* (a - c)
					* (-(a3 * b7) - a2 * b7 * c - a * b7 * c2 - b7 * c3 + a6
							* c4 + a5 * c5 + a4 * c6)
					* (a6 * b4 + a5 * b5 + a4 * b6 - a3 * c7 - a2 * b * c7 - a
							* b2 * c7 - b3 * c7);
		case 782:
			return (b4 - c4) * (a8 - b4 * c4);
		case 783:
			return a2 * (a - b) * (a + b) * (a2 + b2) * (a - c) * (a + c)
					* (-b2 + a * c) * (b2 + a * c) * (a * b - c2) * (a2 + c2)
					* (a * b + c2) * (b4 + a2 * c2) * (a2 * b2 + c4);
		case 784:
			return a * b3 + b3 * c - a * c3 - b * c3;
		case 785:
			return a2 * (a - b) * (a - c)
					* (b2 * c + a2 * (b + c) + a * b * (b + c))
					* (b * c2 + a2 * (b + c) + a * c * (b + c));
		case 786:
			return b2 * (b - c) * c2 + a2 * (b3 - c3);
		case 787:
			return a2 * (a - b) * (a - c) * (a * b2 * c + b2 * c2 + a2 * R)
					* (a * b * c2 + b2 * c2 + a2 * R);
		case 788:
			return a3 * (b3 - c3);
		case 789:
			return b * (a3 - b3) * c * (a3 - c3);
		case 790:
			return b3 * c3 * (-b + c) + a4 * (b3 - c3);
		case 791:
			return a2 * (a - b) * (a - c)
					* (-(a2 * b4) - a * b4 * c - b4 * c2 + a3 * c3)
					* (a3 * b3 - a2 * c4 - a * b * c4 - b2 * c4);
		case 792:
			return -(b5 * c3) + b3 * c5 + a5 * (b3 - c3);
		case 793:
			return a2 * (a - b) * (a - c)
					* (-(a2 * b5) - a * b5 * c - b5 * c2 + a4 * c3 + a3 * c4)
					* (a4 * b3 + a3 * b4 - a2 * c5 - a * b * c5 - b2 * c5);
		case 794:
			return (b3 - c3) * (a6 - b3 * c3);
		case 795:
			return a2 * (a - b) * (a2 + a * b + b2) * (a - c) * (-b2 + a * c)
					* (a * b - c2) * (a2 + a * c + c2)
					* (b4 + a * b2 * c + a2 * c2) * (a2 * b2 + a * b * c2 + c4);
		case 796:
			return -(b7 * c3) + b3 * c7 + a7 * (b3 - c3);
		case 797:
			return a2
					* (a - b)
					* (a - c)
					* (-(a2 * b7) - a * b7 * c - b7 * c2 + a6 * c3 + a5 * c4
							+ a4 * c5 + a3 * c6)
					* (a6 * b3 + a5 * b4 + a4 * b5 + a3 * b6 - a2 * c7 - a * b
							* c7 - b2 * c7);
		case 798:
			return a3 * (b2 - c2);
		case 799:
			return (a - b) * b * (a + b) * (a - c) * c * (a + c);
		default:
			return Double.NaN;
		}
	}

	private double weight800to899(int k, double a, double b, double c) {

		switch (k) {
		case 800:
			return a2 * (-2 * a2 * Q + a4 * R + Q * R);
		case 801:
			return (a6 - a4 * (2 * b2 + c2) + p(-(b2 * c) + c3, 2) + a2
					* (b4 + 4 * b2 * c2 - c4))
					* (a6 - a4 * (b2 + 2 * c2) + p(b3 - b * c2, 2) + a2
							* (-b4 + 4 * b2 * c2 + c4));
		case 802:
			return (b - c) * (-(b2 * c2) + a3 * (b + c));
		case 803:
			return a2 * (a - b) * (a - c) * (-(a * b3) - b3 * c + a2 * c2)
					* (a2 * b2 - a * c3 - b * c3);
		case 804:
			return -((b2 - c2) * (-a2 + b * c) * (a2 + b * c));
		case 805:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c) * (-b2 + a * c)
					* (b2 + a * c) * (a * b - c2) * (a * b + c2);
		case 806:
			return -(b5 * c2) + b2 * c5 + a5 * (b2 - c2);
		case 807:
			return a2 * (a - b) * (a - c)
					* (-(a * b5) - b5 * c + a4 * c2 + a3 * c3 + a2 * c4)
					* (a4 * b2 + a3 * b3 + a2 * b4 - a * c5 - b * c5);
		case 808:
			return -((b2 - c2) * (-a6 + b2 * c2 * R));
		case 809:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c)
					* (a4 * b2 + a2 * b4 - c6) * (-b6 + a2 * c2 * (a2 + c2));
		case 810:
			return a3 * (b2 - c2) * T;
		case 811:
			return (a - b) * b * (a + b) * (a - c) * c * (a + c) * U * V;
		case 812:
			return (b - c) * (a2 - b * c);
		case 813:
			return a2 * (a - b) * (a - c) * (-b2 + a * c) * (a * b - c2);
		case 814:
			return (b - c) * (a3 - b * c * (b + c));
		case 815:
			return a2 * (a - b) * (a - c) * (a2 * b + a * b2 - c3)
					* (-b3 + a * c * (a + c));
		case 816:
			return a4 * (b - c) - b4 * c + b * c4;
		case 817:
			return a2 * (a - b) * (a - c) * (a3 * b + a2 * b2 + a * b3 - c4)
					* (-b4 + a * c * (a2 + a * c + c2));
		case 818:
			return a5 * (b - c) - b5 * c + b * c5;
		case 819:
			return a2 * (a - b) * (a - c)
					* (a4 * b + a3 * b2 + a2 * b3 + a * b4 - c5)
					* (-b5 + a * c * (a3 + a2 * c + a * c2 + c3));
		case 820:
			return a3 * (T * T) * (-2 * a2 * Q + a4 * R + Q * R);
		case 821:
			return b
					* c
					* p(a4 - Q, 2)
					* (a6 - a4 * (2 * b2 + c2) + p(-(b2 * c) + c3, 2) + a2
							* (b4 + 4 * b2 * c2 - c4))
					* (a6 - a4 * (b2 + 2 * c2) + p(b3 - b * c2, 2) + a2
							* (-b4 + 4 * b2 * c2 + c4));
		case 822:
			return a3 * (b2 - c2) * (T * T);
		case 823:
			return (a - b) * b * (a + b) * (a - c) * c * (a + c) * p(a4 - Q, 2);
		case 824:
			return b3 - c3;
		case 825:
			return a2 * (a3 - b3) * (a3 - c3);
		case 826:
			return b4 - c4;
		case 827:
			return a2 * (a4 - b4) * (a4 - c4);
		case 828:
			return a3
					* (b + c)
					* (T * T)
					* (a4 * (b2 - b * c + c2) + Q * (b2 - b * c + c2) - 2 * a2
							* p(b - c, 2) * (b2 + b * c + c2));
		case 829:
			return b
					* (a + b)
					* c
					* (a + c)
					* p(a4 - Q, 2)
					* (a6 - a5 * c - a * c * Q + 2 * a3 * c * R - a4
							* (2 * b2 + c2) + p(-(b2 * c) + c3, 2) + a2
							* (b4 - c4))
					* (a6 - a5 * b - a * b * Q + 2 * a3 * b * R - a4
							* (b2 + 2 * c2) + p(b3 - b * c2, 2) + a2
							* (-b4 + c4));
		case 830:
			return a * (b - c) * (a2 + b2 + b * c + c2);
		case 831:
			return a * (a - b) * (a - c) * (a2 + a * b + b2 + c2)
					* (a2 + b2 + a * c + c2);
		case 832:
			return a * (b4 + a3 * (b - c) - c4);
		case 833:
			return a * (a - b) * (a - c) * (a3 + a2 * b + a * b2 + b3 + c3)
					* (a3 + b3 + a2 * c + a * c2 + c3);
		case 834:
			return a2 * (b - c) * (b2 + b * c + c2 + a * (b + c));
		case 835:
			return (a - b) * (a - c) * (a2 + a * (b + c) + b * (b + c))
					* (a2 + a * (b + c) + c * (b + c));
		case 836:
			return a2 * (b + c) * (T * T) * (a4 - 2 * a2 * p(b - c, 2) + Q);
		case 837:
			return (a + b) * (a + c) * p(a4 - Q, 2)
					* (a4 + 4 * a * b2 * c + Q - 2 * a2 * R)
					* (a4 + 4 * a * b * c2 + Q - 2 * a2 * R);
		case 838:
			return a3 * (b4 - c4 + a * (b3 - c3));
		case 839:
			return (a - b) * b * (a - c) * c
					* (a3 + a2 * (b + c) + a * b * (b + c) + b2 * (b + c))
					* (a3 + a2 * (b + c) + a * c * (b + c) + c2 * (b + c));
		case 840:
			return a2
					* (a3 + b3 - b2 * c + 2 * b * c2 - 2 * c3 - a2 * (b + c) - a
							* (b2 - 2 * c2))
					* (a3 - 2 * b3 + 2 * b2 * c - b * c2 + c3 - a2 * (b + c) + a
							* (2 * b2 - c2));
		case 841:
			return a2
					* (a10 - 3 * a8 * (b2 - c2) + a6
							* (2 * b4 + 8 * b2 * c2 - 13 * c4) + p(b2 - c2, 3)
							* (b4 + 6 * b2 * c2 + 2 * c4) + a4
							* (2 * b6 - 22 * b4 * c2 + 11 * b2 * c4 + 11 * c6) + a2
							* (-3 * b8 + 8 * b6 * c2 + 11 * b4 * c4 - 16 * b2
									* c6))
					* (a10 + 3 * a8 * (b2 - c2) - p(b2 - c2, 3)
							* (2 * b4 + 6 * b2 * c2 + c4) + a6
							* (-13 * b4 + 8 * b2 * c2 + 2 * c4) + a4
							* (11 * b6 + 11 * b4 * c2 - 22 * b2 * c4 + 2 * c6) + a2
							* (-16 * b6 * c2 + 11 * b4 * c4 + 8 * b2 * c6 - 3 * c8));
		case 842:
			return a2
					* (a6 + b6 - b4 * c2 + 2 * b2 * c4 - 2 * c6 - a4 * R - a2
							* (b4 - 2 * c4))
					* (a6 - 2 * b6 + 2 * b4 * c2 - b2 * c4 + c6 - a4 * R + a2
							* (2 * b4 - c4));
		case 843:
			return a2
					* (a4 - 2 * b4 + 2 * b2 * c2 + c4 + 2 * a2 * (b2 - 2 * c2))
					* (a4 + b4 + 2 * b2 * c2 - 2 * c4 + a2 * (-4 * b2 + 2 * c2));
		case 844:
			return a
					* (-(b * p(a + b - c, 2) * c * p(a - b + c, 2) * u(a
							* (-a + b + c)))
							+ a
							* p(a + b - c, 2)
							* c
							* p(-a + b + c, 2)
							* u(b * (a - b + c)) + a * b * p(a - b + c, 2)
							* p(-a + b + c, 2) * u(c * (a + b - c)));
		case 845:
			return a
					* (-(p(a + b - c, 2) * p(a - b + c, 2) * u(a * (-a + b + c)))
							+ p(a + b - c, 2)
							* p(-a + b + c, 2)
							* u(b * (a - b + c)) + p(a - b + c, 2)
							* p(-a + b + c, 2) * u(c * (a + b - c)));
		case 846:
			return a * (a2 - b2 - b * c - c2 - a * (b + c));
		case 847:
			return b2 * c2 * (-V) * U * (a4 - 2 * a2 * b2 + Q)
					* (a4 - 2 * a2 * c2 + Q);
		case 848:
			return Math.sin((2 * a * Math.PI) / (a + b + c))
					* ((a4 + Q - 2 * a2 * R)
							* Math.cos((2 * b * Math.PI) / (a + b + c))
							+ V * S * Math.sin((2 * b * Math.PI) / (a + b + c)))
					* ((a4 + Q - 2 * a2 * R)
							* Math.cos((2 * c * Math.PI) / (a + b + c))
							+ U * S * Math
									.sin((2 * c * Math.PI) / (a + b + c)));
		case 849:
			return a3 * p(a + b, 2) * p(a + c, 2);
		case 850:
			return b2 * (b - c) * c2 * (b + c);
		case 851:
			return a * (b + c)
					* (a4 - b * p(b - c, 2) * c - a2 * (b2 - b * c + c2));
		case 852:
			return a2
					* T
					* (-2 * b2 * c2 * Q + a6 * R + a2 * Q * R - 2 * a4
							* (b4 - b2 * c2 + c4));
		case 853:
			return a2
					* (a - b - c)
					* (-(a4 * p(b - c, 2) * (b + c)) - 2 * b2 * p(b - c, 2)
							* c2 * (b + c) + a5 * R - a3 * (b4 + c4) + a2
							* (b5 - b4 * c - b * c4 + c5));
		case 854:
			return a2
					* (a4 * p(b - c, 2) * (b + c) + 2 * b2 * p(b - c, 2) * c2
							* (b + c) + a5 * R - a3 * (b4 + c4) - a2
							* (b5 - b4 * c - b * c4 + c5));
		case 855:
			return a
					* (a4 * p(b - c, 2) + a5 * (b + c) - a * b * p(b - c, 2)
							* c * (b + c) + b * c * Q - a2 * p(b - c, 2)
							* (b2 + b * c + c2) - a3 * (b3 + c3));
		case 856:
			return a
					* (b + c)
					* T
					* (a6 + a4 * (-2 * b2 + 3 * b * c - 2 * c2) - b * c * Q + a2
							* p(b - c, 2) * R);
		case 857:
			return (b + c)
					* (-a4 + a2 * b * c + p(b - c, 2) * (b2 + b * c + c2));
		case 858:
			return 2 * a2 * b2 * c2 - a4 * R + Q * R;
		case 859:
			return a2 * (a + b) * (a + c)
					* (-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 860:
			return (b + c) * (-V) * U * (-a2 + b2 - b * c + c2);
		case 861:
			return a
					* (a - b - c)
					* (-(a4 * p(b - c, 2)) + a5 * (b + c) - a * b * p(b - c, 2)
							* c * (b + c) - b * c * Q + a2 * p(b - c, 2)
							* (b2 + b * c + c2) - a3 * (b3 + c3));
		case 862:
			return a * (b + c) * (a2 - b * c) * U * V;
		case 863:
			return a3
					* (b2 * p(b - c, 2) * c2 * (b + c) + a4 * (b3 + c3) - a2
							* (b5 + c5));
		case 864:
			return a4 * (b2 * c2 * Q + a4 * (b4 + c4) - a2 * (b6 + c6));
		case 865:
			return a2 * p(b - c, 2) * p(b + c, 2)
					* (a6 + 3 * a2 * b2 * c2 - a4 * R - b2 * c2 * R);
		case 866:
			return a
					* p(b - c, 2)
					* (a5 + a2 * b * c * (b + c) + a * b * c * p(b + c, 2) - a3
							* (b2 + b * c + c2) - b * c
							* (b3 + b2 * c + b * c2 + c3));
		case 867:
			return p(b - c, 2)
					* (-a4 + b4 - a2 * b * c + b3 * c + b * c3 + c4 + a3
							* (b + c) - a * (b3 + b2 * c + b * c2 + c3));
		case 868:
			return p(b - c, 2) * p(b + c, 2) * (b4 + c4 - a2 * R);
		case 869:
			return a3 * (b2 + b * c + c2);
		case 870:
			return b * (a2 + a * b + b2) * c * (a2 + a * c + c2);
		case 871:
			return b3 * (a2 + a * b + b2) * c3 * (a2 + a * c + c2);
		case 872:
			return a3 * p(b + c, 2);
		case 873:
			return b * p(a + b, 2) * c * p(a + c, 2);
		case 874:
			return (a - b) * b * (a - c) * c * (a2 - b * c);
		case 875:
			return a3 * (b - c) * (-b2 + a * c) * (a * b - c2);
		case 876:
			return a * (b - c) * (-b2 + a * c) * (a * b - c2);
		case 877:
			return (a - b) * (a + b) * (a - c) * (a + c) * U * V
					* (-b4 - c4 + a2 * R);
		case 878:
			return a2 * (b2 - c2) * T * (a4 + b4 - a2 * c2 - b2 * c2)
					* (a4 - a2 * b2 - b2 * c2 + c4);
		case 879:
			return (b2 - c2) * (-T) * (a4 + b4 - a2 * c2 - b2 * c2)
					* (-a4 + a2 * b2 + b2 * c2 - c4);
		case 880:
			return (a - b) * b2 * (a + b) * (a - c) * c2 * (a + c)
					* (a2 - b * c) * (a2 + b * c);
		case 881:
			return a4 * (b2 - c2) * (-b2 + a * c) * (b2 + a * c) * (a * b - c2)
					* (a * b + c2);
		case 882:
			return a2 * (b2 - c2) * (-b2 + a * c) * (b2 + a * c) * (a * b - c2)
					* (a * b + c2);
		case 883:
			return (a - b) * (a - c) * (a + b - c) * (a - b + c)
					* (-b2 - c2 + a * (b + c));
		case 884:
			return a2 * (a - b - c) * (b - c) * (a2 + b * (b - c) - a * c)
					* (a2 - a * b + c * (-b + c));
		case 885:
			return (a - b - c) * (b - c) * (a2 + b * (b - c) - a * c)
					* (a2 - a * b + c * (-b + c));
		case 886:
			return -((a - b) * b2 * (a + b) * (a - c) * c2 * (a + c)
					* (b2 * c2 + a2 * (b2 - 2 * c2)) * (-(b2 * c2) + a2
					* (2 * b2 - c2)));
		case 887:
			return a4 * (b2 - c2) * (-2 * b2 * c2 + a2 * R);
		case 888:
			return a2 * (b2 - c2) * (-2 * b2 * c2 + a2 * R);
		case 889:
			return -((a - b) * b * (a - c) * c * (2 * a * b - a * c - b * c) * (a
					* (b - 2 * c) + b * c));
		case 890:
			return a3 * (b - c) * (-2 * b * c + a * (b + c));
		case 891:
			return a * (b - c) * (-2 * b * c + a * (b + c));
		case 892:
			return (a - b) * (a + b) * (a - c) * (a + c) * (a2 + b2 - 2 * c2)
					* (a2 - 2 * b2 + c2);
		case 893:
			return a2 * (b2 + a * c) * (a * b + c2);
		case 894:
			return a2 + b * c;
		case 895:
			return a2 * (a2 + b2 - 2 * c2) * T * (a2 - 2 * b2 + c2);
		case 896:
			return a * (2 * a2 - b2 - c2);
		case 897:
			return a * (a2 + b2 - 2 * c2) * (a2 - 2 * b2 + c2);
		case 898:
			return a * (a - b) * (a - c) * (2 * a * b - a * c - b * c)
					* (a * (b - 2 * c) + b * c);
		case 899:
			return a * (-2 * b * c + a * (b + c));
		default:
			return Double.NaN;
		}
	}

	private double weight900to999(int k, double a, double b, double c) {

		switch (k) {
		case 900:
			return (2 * a - b - c) * (b - c);
		case 901:
			return a2 * (a - b) * (a + b - 2 * c) * (a - c) * (a - 2 * b + c);
		case 902:
			return a2 * (2 * a - b - c);
		case 903:
			return -((a + b - 2 * c) * (a - 2 * b + c));
		case 904:
			return a3 * (b2 + a * c) * (a * b + c2);
		case 905:
			return a * (b - c) * T;
		case 906:
			return a3 * (a - b) * (a - c) * T;
		case 907:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c)
					* (a2 + 3 * b2 + c2) * (a2 + b2 + 3 * c2);
		case 908:
			return 2 * a * b * c - a2 * (b + c) + p(b - c, 2) * (b + c);
		case 909:
			return a2 * (a3 - a2 * b + b3 - a * p(b - c, 2) - b * c2)
					* (a3 - a * p(b - c, 2) - a2 * c - b2 * c + c3);
		case 910:
			return a * (2 * a3 - a2 * (b + c) - p(b - c, 2) * (b + c));
		case 911:
			return a3 * (a3 - 2 * b3 - a2 * c + b2 * c + c3 + a * (b2 - c2))
					* (a3 - a2 * b + b3 + b * c2 - 2 * c3 + a * (-b2 + c2));
		case 912:
			return a * T
					* (a3 * (b + c) - a * p(b - c, 2) * (b + c) + Q - a2 * R);
		case 913:
			return a2
					* U
					* V
					* (a4 - a3 * c + b * p(b - c, 2) * (b + c) + a * c * R - a2
							* (2 * b2 - b * c + c2))
					* (a4 - a3 * b + p(b - c, 2) * c * (b + c) + a * b * R - a2
							* (b2 - b * c + 2 * c2));
		case 914:
			return -(T * (a3 * (b + c) - a * p(b - c, 2) * (b + c) + Q - a2 * R));
		case 915:
			return a
					* U
					* V
					* (a4 - a3 * c + b * p(b - c, 2) * (b + c) + a * c * R - a2
							* (2 * b2 - b * c + c2))
					* (a4 - a3 * b + p(b - c, 2) * c * (b + c) + a * b * R - a2
							* (b2 - b * c + 2 * c2));
		case 916:
			return a2
					* T
					* (b5 - b3 * c2 - b2 * c3 + c5 - a * Q + a3 * R - a2
							* (b3 + c3));
		case 917:
			return U
					* V
					* (a5 - a4 * b + p(b - c, 2) * c2 * (b + c) - a3 * R + a2
							* (b3 + 2 * b * c2 - c3))
					* (a5 - a4 * c + b2 * p(b - c, 2) * (b + c) - a3 * R + a2
							* (-b3 + 2 * b2 * c + c3));
		case 918:
			return -((b - c) * (b2 + c2 - a * (b + c)));
		case 919:
			return a2 * (a - b) * (a - c) * (a2 + b * (b - c) - a * c)
					* (a2 - a * b + c * (-b + c));
		case 920:
			return a
					* (a6 - 3 * a4 * R - Q * R + a2
							* (3 * b4 - 2 * b2 * c2 + 3 * c4));
		case 921:
			return a
					* (a6 - p(b2 - c2, 3) - a4 * (3 * b2 + c2) + a2
							* (3 * b4 + 2 * b2 * c2 - c4))
					* (a6 + p(b2 - c2, 3) - a4 * (b2 + 3 * c2) + a2
							* (-b4 + 2 * b2 * c2 + 3 * c4));
		case 922:
			return a3 * (2 * a2 - b2 - c2);
		case 923:
			return a3 * (a2 + b2 - 2 * c2) * (a2 - 2 * b2 + c2);
		case 924:
			return a2 * (b2 - c2) * (a4 + b4 + c4 - 2 * a2 * R);
		case 925:
			return (a - b) * (a + b) * (a - c) * (a + c)
					* (a4 - 2 * a2 * b2 + Q) * (a4 - 2 * a2 * c2 + Q);
		case 926:
			return a2 * (a - b - c) * (b - c) * (-b2 - c2 + a * (b + c));
		case 927:
			return (a - b) * (a - c) * (a + b - c) * (a - b + c)
					* (a2 + b * (b - c) - a * c) * (a2 - a * b + c * (-b + c));
		case 928:
			return a2
					* (b - c)
					* (b4 + c4 + a3 * (b + c) - a * p(b - c, 2) * (b + c) - a2
							* p(b + c, 2));
		case 929:
			return (a - b)
					* (a - c)
					* (a4 - a3 * c + a2 * (b - c) * c + a * p(b - c, 2) * c + b
							* p(b - c, 2) * (b + c))
					* (a4 - a3 * b + a * b * p(b - c, 2) + a2 * b * (-b + c) + p(
							b - c, 2) * c * (b + c));
		case 930:
			return (a - b) * (a + b) * (a - c) * (a + c)
					* (a4 + Q - a2 * (2 * b2 + c2))
					* (a4 + Q - a2 * (b2 + 2 * c2));
		case 931:
			return a * (a - b) * (a + b) * (a - c) * (a + c)
					* (c * (b + c) + a * (2 * b + c))
					* (b * (b + c) + a * (b + 2 * c));
		case 932:
			return a * (a - b) * (a - c) * (a * (b - c) - b * c)
					* (a * (b - c) + b * c);
		case 933:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c) * U * V
					* (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 934:
			return a * (a - b) * (a - c) * p(a + b - c, 2) * p(a - b + c, 2);
		case 935:
			return (a - b) * (a + b) * (a - c) * (a + c) * U * V
					* (a4 - a2 * b2 + b4 - c4) * (a4 - b4 - a2 * c2 + c4);
		case 936:
			return a * (a3 - a * p(b - c, 2) - a2 * (b + c) + p(b + c, 3));
		case 937:
			return a
					* (a3 + a2 * (3 * b - c) + p(b - c, 2) * (b + c) + a
							* (3 * b2 + 2 * b * c - c2))
					* (a3 - a2 * (b - 3 * c) + p(b - c, 2) * (b + c) + a
							* (-b2 + 2 * b * c + 3 * c2));
		case 938:
			return -a4 + 4 * a2 * b * c + 2 * a3 * (b + c) - 2 * a
					* p(b - c, 2) * (b + c) + Q;
		case 939:
			return a2
					* (a4 - 2 * a3 * b + 2 * a2 * (b - c) * c - p(b - c, 3)
							* (b + c) + 2 * a * b * p(b + c, 2))
					* (a4 - 2 * a2 * b * (b - c) - 2 * a3 * c + p(b - c, 3)
							* (b + c) + 2 * a * c * p(b + c, 2));
		case 940:
			return a * (a2 + 2 * b * c + a * (b + c));
		case 941:
			return a * (c * (b + c) + a * (2 * b + c))
					* (b * (b + c) + a * (b + 2 * c));
		case 942:
			return a * (2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 943:
			return a * (a3 - a2 * b + b3 - b * c2 - a * p(b + c, 2))
					* (a3 - a2 * c - b2 * c + c3 - a * p(b + c, 2));
		case 944:
			return 3 * a4 - 2 * a2 * p(b - c, 2) - 2 * a3 * (b + c) + 2 * a
					* p(b - c, 2) * (b + c) - Q;
		case 945:
			return a2
					* (a4 - 2 * a3 * b - 3 * b4 + 2 * a * b * p(b - c, 2) + 2
							* b3 * c + 2 * b2 * c2 - 2 * b * c3 + c4 + 2 * a2
							* (b2 + b * c - c2))
					* (a4 + b4 - 2 * a3 * c - 2 * b3 * c + 2 * a * p(b - c, 2)
							* c + 2 * b2 * c2 + 2 * b * c3 - 3 * c4 + 2 * a2
							* (-b2 + b * c + c2));
		case 946:
			return -(a2 * p(b - c, 2)) - a3 * (b + c) + a * p(b - c, 2)
					* (b + c) + Q;
		case 947:
			return a2
					* (a4 + a3 * c - a * p(b - c, 2) * c + b * (b - c)
							* p(b + c, 2) - a2 * (2 * b2 + b * c + c2))
					* (a4 + a3 * b - a * b * p(b - c, 2) - (b - c) * c
							* p(b + c, 2) - a2 * (b2 + b * c + 2 * c2));
		case 948:
			return (a + b - c)
					* (a - b + c)
					* (a3 - a2 * (b + c) - p(b - c, 2) * (b + c) + a
							* p(b + c, 2));
		case 949:
			return a2
					* (a - b - c)
					* (a3 - b3 + b2 * c - b * c2 + c3 - a2 * (b + c) + a
							* (b2 - 2 * b * c - c2))
					* (a3 + b3 - b2 * c + b * c2 - c3 - a2 * (b + c) + a
							* (-b2 - 2 * b * c + c2));
		case 950:
			return -((a - b - c) * (2 * a3 + a2 * (b + c) + p(b - c, 2)
					* (b + c)));
		case 951:
			return a2 * (a + b - c) * (a - b + c)
					* (a3 + 2 * b3 - a2 * c + b2 * c + c3 + a * (b2 - c2))
					* (a3 - a2 * b + b3 + b * c2 + 2 * c3 + a * (-b2 + c2));
		case 952:
			return 2 * a4 - 2 * a3 * (b + c) + 2 * a * p(b - c, 2) * (b + c)
					- Q - a2 * (b2 - 4 * b * c + c2);
		case 953:
			return a2
					* (a4 - 2 * a3 * b - 2 * b4 + 2 * a * b * p(b - c, 2) + 2
							* b3 * c + b2 * c2 - 2 * b * c3 + c4 + a2
							* (b2 + 2 * b * c - 2 * c2))
					* (a4 + b4 - 2 * a3 * c - 2 * b3 * c + 2 * a * p(b - c, 2)
							* c + b2 * c2 + 2 * b * c3 - 2 * c4 + a2
							* (-2 * b2 + 2 * b * c + c2));
		case 954:
			return a
					* (a5 - 2 * a4 * (b + c) - 2 * b * p(b - c, 2) * c
							* (b + c) - a * Q + 2 * a2
							* (b3 + 2 * b2 * c + 2 * b * c2 + c3));
		case 955:
			return a
					* (-(b * p(b - c, 3) * (b + c)) + a4 * (b + 2 * c) + 2 * a
							* Q - 2 * a3 * R - 2 * a2 * c
							* (2 * b2 + b * c + c2))
					* (p(b - c, 3) * c * (b + c) + a4 * (2 * b + c) + 2 * a * Q
							- 2 * a3 * R - 2 * a2 * b * (b2 + b * c + 2 * c2));
		case 956:
			return a * (a3 - a * p(b - c, 2) - 2 * b * c * (b + c));
		case 957:
			return a * (-b3 + b * c2 + 2 * a * c * (-b + c) + a2 * (b + 2 * c))
					* (2 * a * b * (b - c) + a2 * (2 * b + c) + c * (b2 - c2));
		case 958:
			return a * (a - b - c) * (a2 + 2 * b * c + a * (b + c));
		case 959:
			return a * (a + b - c) * (a - b + c)
					* (c * (b + c) + a * (2 * b + c))
					* (b * (b + c) + a * (b + 2 * c));
		case 960:
			return -(a * (a - b - c) * (b2 + c2 + a * (b + c)));
		case 961:
			return a * (a + b - c) * (a - b + c) * (a2 + a * c + b * (b + c))
					* (a2 + a * b + c * (b + c));
		case 962:
			return -a4 + 4 * a2 * b * c - 2 * a3 * (b + c) + 2 * a
					* p(b - c, 2) * (b + c) + Q;
		case 963:
			return a2
					* (a4 + 2 * a3 * b - 2 * a * b * p(b - c, 2) - 2 * a2 * c
							* (b + c) - (b - c) * p(b + c, 3))
					* (a4 + 2 * a3 * c - 2 * a * p(b - c, 2) * c - 2 * a2 * b
							* (b + c) + (b - c) * p(b + c, 3));
		case 964:
			return a4 + a3 * (b + c) + a2 * p(b + c, 2) + b * c * p(b + c, 2)
					+ a * (b3 + 2 * b2 * c + 2 * b * c2 + c3);
		case 965:
			return a
					* (a4 - a3 * (b + c) + 2 * b * c * p(b + c, 2) + a
							* p(b + c, 3) - a2 * R);
		case 966:
			return -a2 + 2 * a * (b + c) + p(b + c, 2);
		case 967:
			return a2 * (a2 + b2 + 2 * b * c - c2 + 2 * a * (b + c))
					* (a2 - b2 + 2 * b * c + c2 + 2 * a * (b + c));
		case 968:
			return a * (a2 - 2 * a * (b + c) - p(b + c, 2));
		case 969:
			return a * (a2 + b2 + 2 * b * c - c2 + 2 * a * (b + c))
					* (a2 - b2 + 2 * b * c + c2 + 2 * a * (b + c));
		case 970:
			return a2
					* (-b5 - b4 * c - b * c4 - c5 + a3 * p(b + c, 2) + a2
							* (b3 + b2 * c + b * c2 + c3) - a
							* (b4 + 2 * b3 * c + 2 * b * c3 + c4));
		case 971:
			return a
					* (a4 * (b + c) - p(b - c, 2) * p(b + c, 3) - 2 * a3
							* (b2 - b * c + c2) + 2 * a * p(b - c, 2)
							* (b2 + b * c + c2));
		case 972:
			return a
					* (a5 + a4 * (b - 2 * c) - 2 * a3 * b * (b - c) + b
							* p(b - c, 3) * (b + c) + a * (b - c) * p(b + c, 3) - 2
							* a2 * (b3 - c3))
					* (a5 + 2 * a3 * (b - c) * c + a4 * (-2 * b + c)
							- p(b - c, 3) * c * (b + c) - a * (b - c)
							* p(b + c, 3) + 2 * a2 * (b3 - c3));
		case 973:
			return a2
					* (-Q + a2 * R)
					* (a10 - 3 * a8 * R + a6 * (2 * b4 + 3 * b2 * c2 + 2 * c4)
							- a2 * Q * (3 * b4 + 5 * b2 * c2 + 3 * c4) + Q
							* (b6 + c6) + a4
							* (2 * b6 + b4 * c2 + b2 * c4 + 2 * c6));
		case 974:
			return a2
					* T
					* (a10 * R + a8 * (-3 * b4 + 2 * b2 * c2 - 3 * c4)
							+ p(b2 - c2, 4) * (b4 + b2 * c2 + c4) + a4 * Q
							* (2 * b4 - 7 * b2 * c2 + 2 * c4) - 3 * a2 * Q
							* (b6 - 2 * b4 * c2 - 2 * b2 * c4 + c6) + a6
							* (2 * b6 - b4 * c2 - b2 * c4 + 2 * c6));
		case 975:
			return a
					* (a3 + a2 * (b + c) + p(b + c, 3) + a
							* (b2 + 4 * b * c + c2));
		case 976:
			return a * (a3 + b3 + b2 * c + b * c2 + c3);
		case 977:
			return a * (a3 + a2 * b + a * b2 + b3 + c3)
					* (a3 + b3 + a2 * c + a * c2 + c3);
		case 978:
			return a * (a2 * (b + c) - b * c * (b + c) + a * (b2 - b * c + c2));
		case 979:
			return a * (a2 * (b - c) + b * c * (b + c) + a * (b2 - b * c - c2))
					* (a2 * (b - c) - b * c * (b + c) + a * (b2 + b * c - c2));
		case 980:
			return a
					* (b * c * R + a2 * (b2 + b * c + c2) + a
							* (b3 + b2 * c + b * c2 + c3));
		case 981:
			return a
					* (a3 * (b + c) + a2 * c * (b + c) + b2 * c * (b + c) + a
							* b * (b2 + b * c + c2))
					* (a3 * (b + c) + a2 * b * (b + c) + b * c2 * (b + c) + a
							* c * (b2 + b * c + c2));
		case 982:
			return a * (b2 - b * c + c2);
		case 983:
			return a * (a2 - a * b + b2) * (a2 - a * c + c2);
		case 984:
			return a * (b2 + b * c + c2);
		case 985:
			return a * (a2 + a * b + b2) * (a2 + a * c + c2);
		case 986:
			return a * (b3 + c3 + a * (b2 + b * c + c2));
		case 987:
			return a * (a3 + a2 * c + a * b * c + b2 * (b + c))
					* (a3 + a2 * b + a * b * c + c2 * (b + c));
		case 988:
			return a
					* (a3 - b3 - b2 * c - b * c2 - c3 - a2 * (b + c) - 3 * a
							* R);
		case 989:
			return a
					* (a3 - b3 + b2 * c + 3 * b * c2 + c3 + a2 * (3 * b + c) + a
							* R)
					* (a3 + b3 + 3 * b2 * c + b * c2 - c3 + a2 * (b + 3 * c) + a
							* R);
		case 990:
			return a
					* (a5 - 2 * a3 * b * c - a4 * (b + c) + p(b - c, 2)
							* p(b + c, 3) - a * p(b - c, 2) * R);
		case 991:
			return a2
					* (a3 * (b + c) - a2 * (b2 - b * c + c2) + p(b - c, 2)
							* (b2 + b * c + c2) - a
							* (b3 + b2 * c + b * c2 + c3));
		case 992:
			return a
					* (a3 * (b + c) - a * b * c * (b + c) - b * c * p(b + c, 2) + a2
							* R);
		case 993:
			return a * (a3 - b * c * (b + c) - a * R);
		case 994:
			return a * (-b3 + a * c2 + b * c2 + a2 * (b + c))
					* (a * b2 + a2 * (b + c) + c * (b2 - c2));
		case 995:
			return a2 * (b2 - b * c + c2 + a * (b + c));
		case 996:
			return (a2 + a * (-b + c) + b * (b + c))
					* (a2 + a * (b - c) + c * (b + c));
		case 997:
			return a
					* (a3 + b3 - a * p(b - c, 2) + b2 * c + b * c2 + c3 - a2
							* (b + c));
		case 998:
			return a
					* (a3 + a2 * (b - c) + p(b - c, 2) * (b + c) + a
							* (b2 + 2 * b * c - c2))
					* (a3 + a2 * (-b + c) + p(b - c, 2) * (b + c) + a
							* (-b2 + 2 * b * c + c2));
		case 999:
			return a2 * (a2 - b2 + 4 * b * c - c2);
		default:
			return Double.NaN;
		}
	}

	private double weight1000to1099(int k, double a, double b, double c) {

		switch (k) {
		case 1000:
			return -((a2 - 4 * a * b + b2 - c2) * (a2 - b2 - 4 * a * c + c2));
		case 1001:
			return a * (a2 - 2 * b * c - a * (b + c));
		case 1002:
			return a * ((b - c) * c + a * (2 * b + c))
					* (b * (-b + c) + a * (b + 2 * c));
		case 1003:
			return 3 * a4 + 2 * b2 * c2 - a2 * R;
		case 1004:
			return a
					* (a5 - 2 * a4 * (b + c) + 2 * b * p(b - c, 2) * c
							* (b + c) - a * p(b2 + c2, 2) + 2 * a2 * (b3 + c3));
		case 1005:
			return a
					* (a5 - a3 * b * c - 2 * a4 * (b + c) + b * p(b - c, 2) * c
							* (b + c) - a * p(b + c, 2) * (b2 - 3 * b * c + c2) + a2
							* (2 * b3 + b2 * c + b * c2 + 2 * c3));
		case 1006:
			return a
					* (a6 - a5 * (b + c) - b * c * Q + a2 * p(b + c, 2) * R
							- a4 * (2 * b2 + b * c + 2 * c2) + 2 * a3
							* (b3 + c3) - a * (b5 - b4 * c - b * c4 + c5));
		case 1007:
			return a4 + 3 * b4 - 2 * b2 * c2 + 3 * c4 - 4 * a2 * R;
		case 1008:
			return a5 * (b + c) + a4 * p(b + c, 2) + b2 * c2 * p(b + c, 2) + a
					* b * c * p(b + c, 3) + a2 * p(b2 + b * c + c2, 2) + a3
					* (b3 + 2 * b2 * c + 2 * b * c2 + c3);
		case 1009:
			return a
					* (2 * a3 * b * c + a4 * (b + c) - a2 * (b3 + c3) + b * c
							* (b3 + b2 * c + b * c2 + c3));
		case 1010:
			return (a + b) * (a + c) * (a2 + p(b + c, 2));
		case 1011:
			return a2
					* (a2 * b * c + a3 * (b + c) - b * c * p(b + c, 2) - a
							* (b3 + b2 * c + b * c2 + c3));
		case 1012:
			return a
					* (a6 - 2 * a4 * p(b - c, 2) - a5 * (b + c) - 2 * b * c * Q
							+ a2 * p(b - c, 2) * R + 2 * a3 * (b3 + c3) - a
							* (b5 - b4 * c - b * c4 + c5));
		case 1013:
			return a
					* U
					* V
					* (a4 + 2 * b2 * c2 - a3 * (b + c) - a2 * R + a
							* (b3 + b2 * c + b * c2 + c3));
		case 1014:
			return a * (a + b) * (a + b - c) * (a + c) * (a - b + c);
		case 1015:
			return a2 * p(b - c, 2);
		case 1016:
			return p(a - b, 2) * p(a - c, 2);
		case 1017:
			return a2 * p(-2 * a + b + c, 2);
		case 1018:
			return a * (a - b) * (a - c) * (b + c);
		case 1019:
			return a * (a + b) * (b - c) * (a + c);
		case 1020:
			return a * (a - b) * (a - c) * p(a + b - c, 2) * p(a - b + c, 2)
					* (b + c);
		case 1021:
			return a * (a + b) * (b - c) * (a + c) * p(-a + b + c, 2);
		case 1022:
			return a * (a + b - 2 * c) * (b - c) * (a - 2 * b + c);
		case 1023:
			return a * (a - b) * (a - c) * (2 * a - b - c);
		case 1024:
			return a * (a - b - c) * (b - c) * (a2 + b * (b - c) - a * c)
					* (a2 - a * b + c * (-b + c));
		case 1025:
			return a * (a - b) * (a - c) * (a + b - c) * (a - b + c)
					* (-b2 - c2 + a * (b + c));
		case 1026:
			return a * (a - b) * (a - c) * (-b2 - c2 + a * (b + c));
		case 1027:
			return a * (b - c) * (a2 + b * (b - c) - a * c)
					* (a2 - a * b + c * (-b + c));
		case 1028:
			return a * p(angleB * angleC, 2);
		case 1029:
			return -((a3 + a2 * (b + c) + (b - c) * p(b + c, 2) + a
					* (b2 + b * c - c2)) * (a3 + a2 * (b + c) - (b - c)
					* p(b + c, 2) + a * (-b2 + b * c + c2)));
		case 1030:
			return a2
					* (a3 - b3 - b2 * c - b * c2 - c3 + a2 * (b + c) - a
							* (b2 + b * c + c2));
		case 1031:
			return -((a4 + b4 + b2 * c2 - c4 + a2 * R) * (a4 - b4 + b2 * c2
					+ c4 + a2 * R));
		case 1032:
			return -(T
					* (a8 - 4 * a6 * (b2 - c2) + p(b2 - c2, 4) - 4 * a2
							* (b2 - c2) * p(b2 + c2, 2) + 2 * a4
							* (3 * b4 + 2 * b2 * c2 - 5 * c4)) * (a8 + 4 * a6
					* (b2 - c2) + p(b2 - c2, 4) + 4 * a2 * (b2 - c2)
					* p(b2 + c2, 2) + a4 * (-10 * b4 + 4 * b2 * c2 + 6 * c4)));
		case 1033:
			return a2
					* U
					* V
					* (a8 - 4 * a6 * R - 4 * a2 * Q * R + Q
							* (b4 + 6 * b2 * c2 + c4) + a4
							* (6 * b4 - 4 * b2 * c2 + 6 * c4));
		case 1034:
			return -((a - b - c)
					* (a6 - 2 * a5 * (b - c) - a4 * p(b - c, 2) + p(b - c, 4)
							* p(b + c, 2) - a2 * Q + 4 * a3 * (b3 - c3) - 2 * a
							* (b5 + b4 * c - b * c4 - c5)) * (a6 + 2 * a5
					* (b - c) - a4 * p(b - c, 2) + p(b - c, 4) * p(b + c, 2)
					- a2 * Q - 4 * a3 * (b3 - c3) + 2 * a
					* (b5 + b4 * c - b * c4 - c5)));
		case 1035:
			return a2
					* (a + b - c)
					* (a - b + c)
					* (a6 - 2 * a5 * (b + c) - a4 * p(b + c, 2) + p(b - c, 2)
							* p(b + c, 4) - a2 * Q + 4 * a3 * (b3 + c3) - 2 * a
							* (b5 - b4 * c - b * c4 + c5));
		case 1036:
			return a2 * (a - b - c) * (a2 + 2 * a * b + b2 + c2)
					* (a2 + b2 + 2 * a * c + c2);
		case 1037:
			return a2 * (a + b - c) * (a - b + c) * (a2 - 2 * a * b + b2 + c2)
					* (a2 + b2 - 2 * a * c + c2);
		case 1038:
			return a * (a + b - c) * (a - b + c) * T * (a2 + p(b + c, 2));
		case 1039:
			return a * (a - b - c) * U * V * (a2 + 2 * a * b + b2 + c2)
					* (a2 + b2 + 2 * a * c + c2);
		case 1040:
			return a * (a2 + p(b - c, 2)) * (a - b - c) * T;
		case 1041:
			return a * (a + b - c) * (a - b + c) * U * V
					* (a2 - 2 * a * b + b2 + c2) * (a2 + b2 - 2 * a * c + c2);
		case 1042:
			return a2 * p(a + b - c, 2) * p(a - b + c, 2) * (b + c);
		case 1043:
			return (a + b) * (a + c) * p(-a + b + c, 2);
		case 1044:
			return a
					* (3 * a4 * b * c + a5 * (b + c) - 2 * a3 * p(b - c, 2)
							* (b + c) + a * p(b - c, 4) * (b + c) - b * c * Q - 2
							* a2 * b * c * R);
		case 1045:
			return a
					* (-(b2 * c2) + a * b * c * (b + c) + a2
							* (b2 + b * c + c2));
		case 1046:
			return a * (a3 - b3 + a * b * c - c3 + 2 * a2 * (b + c));
		case 1047:
			return a
					* (-(b2 * p(b - c, 2) * c2 * p(b + c, 3)) + a2
							* p(b - c, 2) * p(b + c, 3) * R + a3 * Q
							* (b2 - b * c + c2) + a * b * c * Q
							* (b2 - b * c + c2) + a7 * (b2 + b * c + c2) + a6
							* (b3 + b2 * c + b * c2 + c3) - a5
							* (2 * b4 + b3 * c - b2 * c2 + b * c3 + 2 * c4) + a4
							* (-2 * b5 - 2 * b4 * c + b3 * c2 + b2 * c3 - 2 * b
									* c4 - 2 * c5));
		case 1048:
			return a
					* (-(b2 * p(b - c, 2) * c2 * p(b + c, 3))
							+ a
							* b
							* c
							* Q
							* R
							- 2
							* a4
							* p(b + c, 3)
							* (b2 - b * c + c2)
							+ a7
							* (b2 + b * c + c2)
							+ a6
							* (b3 + 2 * b2 * c + 2 * b * c2 + c3)
							- a5
							* (2 * b4 + b3 * c + b * c3 + 2 * c4)
							+ a3
							* (b6 - b5 * c - 2 * b4 * c2 - b3 * c3 - 2 * b2
									* c4 - b * c5 + c6) + a2
							* (b7 + 2 * b6 * c - 2 * b4 * c3 - 2 * b3 * c4 + 2
									* b * c6 + c7));
		case 1049:
			return a * angleA;
		case 1050:
			return a
					* (-(b2 * c2 * p(b + c, 2)) + a4 * (b2 + b * c + c2) + a
							* b * c * (b3 + b2 * c + b * c2 + c3) + a3
							* (2 * b3 - 3 * b2 * c - 3 * b * c2 + 2 * c3) + a2
							* (b4 - 3 * b3 * c + 7 * b2 * c2 - 3 * b * c3 + c4));
		case 1051:
			return a * (3 * a2 + b2 + b * c + c2 + 5 * a * (b + c));
		case 1052:
			return a
					* (a4 - b4 + 2 * b3 * c - b2 * c2 + 2 * b * c3 - c4 - 2
							* a3 * (b + c) - a2 * (b2 - 8 * b * c + c2) + 2 * a
							* (b3 - 2 * b2 * c - 2 * b * c2 + c3));
		case 1053:
			return a
					* (a6 - 2 * a5 * (b + c) + 2 * a * p(b - c, 4) * (b + c)
							+ 2 * a4 * (b2 + b * c + c2) + a2 * b * c
							* (4 * b2 - 7 * b * c + 4 * c2) - 2 * a3
							* (b3 + c3) - p(b - c, 2) * (b4 - b2 * c2 + c4));
		case 1054:
			return a * (a2 - b2 + 3 * b * c - c2 - a * (b + c));
		case 1055:
			return a2 * (2 * a2 - p(b - c, 2) - a * (b + c));
		case 1056:
			return -a4 - 8 * a2 * b * c + Q;
		case 1057:
			return a2 * (a4 - 2 * a2 * b2 + b4 - 8 * a * b * c2 - c4)
					* (a4 - b4 - 8 * a * b2 * c - 2 * a2 * c2 + c4);
		case 1058:
			return -a4 + 8 * a2 * b * c + Q;
		case 1059:
			return a2 * (a4 - 2 * a2 * b2 + b4 + 8 * a * b * c2 - c4)
					* (a4 - b4 + 8 * a * b2 * c - 2 * a2 * c2 + c4);
		case 1060:
			return a * T * (a4 + 2 * a2 * b * c - Q);
		case 1061:
			return a * U * V * (a4 - 2 * a2 * b2 + b4 - 2 * a * b * c2 - c4)
					* (a4 - b4 - 2 * a * b2 * c - 2 * a2 * c2 + c4);
		case 1062:
			return a * T * (a4 - 2 * a2 * b * c - Q);
		case 1063:
			return a * U * V * (a4 - 2 * a2 * b2 + b4 + 2 * a * b * c2 - c4)
					* (a4 - b4 + 2 * a * b2 * c - 2 * a2 * c2 + c4);
		case 1064:
			return a2
					* (b5 - b4 * c - 4 * a * b2 * c2 - b * c4 + c5 + a4
							* (b + c) - 2 * a2 * (b3 + c3));
		case 1065:
			return (a5 - 2 * a3 * b2 - a4 * c - 4 * a2 * b * c2 + c * Q + a
					* (b4 - c4))
					* (a5 - a4 * b - 4 * a2 * b2 * c - 2 * a3 * c2 + b * Q + a
							* (-b4 + c4));
		case 1066:
			return a2
					* (b5 - b4 * c + 4 * a * b2 * c2 - b * c4 + c5 + a4
							* (b + c) - 2 * a2 * (b3 + c3));
		case 1067:
			return (a5 - 2 * a3 * b2 - a4 * c + 4 * a2 * b * c2 + c * Q + a
					* (b4 - c4))
					* (a5 - a4 * b + 4 * a2 * b2 * c - 2 * a3 * c2 + b * Q + a
							* (-b4 + c4));
		case 1068:
			return U * V * (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a * R);
		case 1069:
			return a2 * T * (a3 + a2 * (b - c) - (b - c) * p(b + c, 2) - a * R)
					* (a3 + a2 * (-b + c) + (b - c) * p(b + c, 2) - a * R);
		case 1070:
			return 8 * a3 * b2 * c2 + a6 * (b + c) - a4 * p(b - c, 2) * (b + c)
					- a2 * p(b - c, 2) * p(b + c, 3) + p(b - c, 4)
					* p(b + c, 3);
		case 1071:
			return a2 * T * (-(a2 * p(b - c, 2)) + a3 * (b + c) + Q);
		case 1072:
			return -8 * a3 * b2 * c2 + a6 * (b + c) - a4 * p(b - c, 2)
					* (b + c) - a2 * p(b - c, 2) * p(b + c, 3) + p(b - c, 4)
					* p(b + c, 3);
		case 1073:
			return a2 * T
					* (a4 + b4 + 2 * b2 * c2 - 3 * c4 - 2 * a2 * (b2 - c2))
					* (a4 - 3 * b4 + 2 * b2 * c2 + c4 + 2 * a2 * (b2 - c2));
		case 1074:
			return -4 * a5 * b * c + a6 * (b + c) - a4 * p(b - c, 2) * (b + c)
					- a2 * p(b - c, 2) * p(b + c, 3) + p(b - c, 4)
					* p(b + c, 3) + 4 * a3 * b * c * R;
		case 1075:
			return U
					* V
					* (-(b2 * c2 * p(b2 - c2, 4)) + a10 * R + a2 * Q
							* p(b2 + c2, 3) - 2 * a4 * Q
							* (2 * b4 + 3 * b2 * c2 + 2 * c4) - a8
							* (4 * b4 + b2 * c2 + 4 * c4) + a6
							* (6 * b6 - 2 * b4 * c2 - 2 * b2 * c4 + 6 * c6));
		case 1076:
			return 4 * a5 * b * c + a6 * (b + c) - a4 * p(b - c, 2) * (b + c)
					- a2 * p(b - c, 2) * p(b + c, 3) + p(b - c, 4)
					* p(b + c, 3) - 4 * a3 * b * c * R;
		case 1077:
			return a * angleB * angleC;
		case 1078:
			return a4 - b2 * c2 - a2 * R;
		case 1079:
			return a * p(a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a * R, 2);
		case 1080:
			return -3 * U * V * (a2 + b2 + c2) + u(3) * (a - b - c)
					* (a + b - c) * (a - b + c) * (a + b + c) * S;
		case 1081:
			return 1 / (u(3) * (-a + b + c) * (a + b + c) - S);
		case 1082:
			return a * (a + b - c) * (a - b + c) - u(3) * a * S;
		case 1083:
			return a
					* (a4 - a2 * b * c - a3 * (b + c) + 2 * a * b * c * (b + c) - b
							* c * R);
		case 1084:
			return a4 * p(b - c, 2) * p(b + c, 2);
		case 1085:
			return a * angleA * angleA;
		case 1086:
			return p(b - c, 2);
		case 1087:
			return b * c * p(Q - a2 * R, 2);
		case 1088:
			return b * p(a + b - c, 2) * c * p(a - b + c, 2);
		case 1089:
			return b * c * p(b + c, 2);
		case 1090:
			return b * p(b - c, 4) * c * p(-a + b + c, 2);
		case 1091:
			return b * p(a + b - c, 2) * c * p(a - b + c, 2) * p(b + c, 4);
		case 1092:
			return a4 * p(a2 - b2 - c2, 3);
		case 1093:
			return b2 * c2 * p(-a4 + Q, 3);
		case 1094:
			return -(a3 * (a4 - 2 * a2 * b2 + b4 - 2 * a2 * c2 + 4 * b2 * c2 + c4))
					+ u(3) * a3 * T * S;
		case 1095:
			return a3
					* (a4 - 2 * a2 * b2 + b4 - 2 * a2 * c2 + 4 * b2 * c2 + c4)
					+ u(3) * a3 * T * S;
		case 1096:
			return a * p(a4 - Q, 2);
		case 1097:
			return b * c * p(-3 * a4 + Q + 2 * a2 * R, 2);
		case 1098:
			return a * p(a + b, 2) * p(a + c, 2) * p(-a + b + c, 2);
		case 1099:
			return b * c * p(-2 * a4 + Q + a2 * R, 2);
		default:
			return Double.NaN;
		}
	}

	private double weight1100to1199(int k, double a, double b, double c) {

		switch (k) {
		case 1100:
			return a * (2 * a + b + c);
		case 1101:
			return a3 * p(a - b, 2) * p(a + b, 2) * p(a - c, 2) * p(a + c, 2);
		case 1102:
			return a * p(a2 - b2 - c2, 3);
		case 1103:
			return a
					* p(a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2), 2);
		case 1104:
			return a * (2 * a3 + a2 * (b + c) + p(b - c, 2) * (b + c));
		case 1105:
			return U
					* V
					* (a6 - a4 * (2 * b2 + c2) + p(-(b2 * c) + c3, 2) + a2
							* (b4 + 4 * b2 * c2 - c4))
					* (a6 - a4 * (b2 + 2 * c2) + p(b3 - b * c2, 2) + a2
							* (-b4 + 4 * b2 * c2 + c4));
		case 1106:
			return a3 * p(a + b - c, 2) * p(a - b + c, 2);
		case 1107:
			return a * (b * c * (b + c) + a * R);
		case 1108:
			return a
					* (-(a2 * p(b - c, 2)) + a3 * (b + c) - a * p(b - c, 2)
							* (b + c) + Q);
		case 1109:
			return b * p(b - c, 2) * c * p(b + c, 2);
		case 1110:
			return a3 * p(a - b, 2) * p(a - c, 2);
		case 1111:
			return b * p(b - c, 2) * c;
		case 1112:
			return a2 * U * V * (b6 + c6 + a4 * R - 2 * a2 * (b4 + c4));
		case 1113:
			return -(a * b * c * (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4))
					+ a2
					* T
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 1114:
			return a
					* b
					* c
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4)
					+ a2
					* T
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 1115:
			return -angleA + Math.PI;
		case 1116:
			return -((b2 - c2) * (-2 * a8 + p(b2 - c2, 4) + 5 * a6 * R - a4
					* (3 * b4 + 8 * b2 * c2 + 3 * c4) - a2
					* (b6 - 4 * b4 * c2 - 4 * b2 * c4 + c6)));
		case 1117:
			return (a2 - a * b + b2 - c2)
					* (a2 + a * b + b2 - c2)
					* (a2 - b2 - a * c + c2)
					* (a2 - b2 + a * c + c2)
					* (a6 - a4 * b2 - a2 * b4 + b6 - 3 * a4 * c2 + a2 * b2 * c2
							- 3 * b4 * c2 + 3 * a2 * c4 + 3 * b2 * c4 - c6)
					* (a6 - 3 * a4 * b2 + 3 * a2 * b4 - b6 - a4 * c2 + a2 * b2
							* c2 + 3 * b4 * c2 - a2 * c4 - 3 * b2 * c4 + c6)
					* (a8 - 4 * a6 * b2 + 6 * a4 * b4 - 4 * a2 * b6 + b8 - 4
							* a6 * c2 + a4 * b2 * c2 + a2 * b4 * c2 + 2 * b6
							* c2 + 6 * a4 * c4 + a2 * b2 * c4 - 6 * b4 * c4 - 4
							* a2 * c6 + 2 * b2 * c6 + c8);
		case 1118:
			return -((a + b - c) * (a - b + c) * p(a4 - Q, 2));
		case 1119:
			return -(p(a + b - c, 2) * p(a - b + c, 2) * U * V);
		case 1120:
			return (a2 + a * (-4 * b + c) + b * (b + c))
					* (a2 + a * (b - 4 * c) + c * (b + c));
		case 1121:
			return -((a2 - 2 * b2 + a * (b - 2 * c) + b * c + c2) * (a2 + b2
					+ b * c - 2 * c2 + a * (-2 * b + c)));
		case 1122:
			return a * (a + b - c) * (a - b + c) * (p(b - c, 2) + a * (b + c));
		case 1123:
			return -a4 + 2 * a2 * b2 - b4 + 4 * a2 * b * c + 2 * a2 * c2 + 2
					* b2 * c2 - c4 + 2 * a * (b + c) * S;
		case 1124:
			return 2 * a2 * b * c + a2 * S;
		case 1125:
			return 2 * a + b + c;
		case 1126:
			return a2 * (a + 2 * b + c) * (a + b + 2 * c);
		case 1127:
			return (a * c + u(a * c * (a - b + c) * (a + b + c)))
					* (a * b + u(a * b * (a + b - c) * (a + b + c)));
		case 1128:
			return a
					* (a * b * c + c * u(a * (-a + b + c)) * u(b * (a - b + c))
							+ b * u(a * (-a + b + c)) * u(c * (a + b - c)) + (-a
							+ b + c)
							* u(b * (a - b + c)) * u(c * (a + b - c)));
		case 1129:
			return a2 * (b * c + u(-(b * (a - b - c) * c * (a + b + c))));
		case 1130:
			return a2 * (b * c + u(b * (a - b + c)) * u(c * (a + b - c)));
		case 1131:
			return 3 * a4 + 2 * a2 * b2 - 5 * b4 + 2 * a2 * c2 + 10 * b2 * c2
					- 5 * c4 + 4 * a2 * S;
		case 1132:
			return -3 * a4 - 2 * a2 * b2 + 5 * b4 - 2 * a2 * c2 - 10 * b2 * c2
					+ 5 * c4 + 4 * a2 * S;
		case 1133:
			return a * (u(3) * Math.cos(angleA / 3) - Math.sin(angleA / 3))
					* (u(3) * Math.cos(angleB / 3) + Math.sin(angleB / 3))
					* (u(3) * Math.cos(angleC / 3) + Math.sin(angleC / 3));
		case 1134:
			return -(a * (Math.cos(angleB / 3) + u(3) * Math.sin(angleB / 3)) * (Math
					.cos(angleC / 3) + u(3) * Math.sin(angleC / 3)));
		case 1135:
			return -(a * (Math.cos(angleA / 3) + u(3) * Math.sin(angleA / 3)));
		case 1136:
			return a * (-Math.cos(angleB / 3) + u(3) * Math.sin(angleB / 3))
					* (-Math.cos(angleC / 3) + u(3) * Math.sin(angleC / 3));
		case 1137:
			return a * (-Math.cos(angleA / 3) + u(3) * Math.sin(angleA / 3));
		case 1138:
			return (a8 + 2 * a6 * (b2 - 2 * c2) + p(b2 - c2, 4) + a4
					* (-6 * b4 + b2 * c2 + 6 * c4) + a2
					* (2 * b6 + b4 * c2 + b2 * c4 - 4 * c6))
					* (a8 + p(b2 - c2, 4) + a6 * (-4 * b2 + 2 * c2) + a4
							* (6 * b4 + b2 * c2 - 6 * c4) + a2
							* (-4 * b6 + b4 * c2 + b2 * c4 + 2 * c6));
		case 1139:
			return 2
					* (a4 + u(5) * a4 + 3 * a2 * b2 - u(5) * a2 * b2 - 4 * b4
							+ 3 * a2 * c2 - u(5) * a2 * c2 + 8 * b2 * c2 - 4 * c4)
					+ (-1 + u(5)) * u(2 * (5 + u(5))) * a2 * S;
		case 1140:
			return -2
					* (a4 + u(5) * a4 + 3 * a2 * b2 - u(5) * a2 * b2 - 4 * b4
							+ 3 * a2 * c2 - u(5) * a2 * c2 + 8 * b2 * c2 - 4 * c4)
					+ (-1 + u(5)) * u(2 * (5 + u(5))) * a2 * S;
		case 1141:
			return (a2 - a * b + b2 - c2) * (a2 + a * b + b2 - c2)
					* (a2 - b2 - a * c + c2) * (a2 - b2 + a * c + c2)
					* (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 1142:
			return 2
					* (a + b + c)
					* p(1
							+ (2 * b * c - u(-(b * (a - b - c) * c * (a + b + c))))
							/ (u(b * (a - b + c)) * u(c * (a + b - c))), 2)
					- (b + c)
					* (2 + (2
							* (2 * b * c - u(-(b * (a - b - c) * c * (a + b + c))))
							* (2 * a * c - u(a * c * (a - b + c) * (a + b + c))) * (2
							* a * b - u(a * b * (a + b - c) * (a + b + c))))
							/ (a * b * (a + b - c) * c * (a - b + c) * (-a + b + c)));
		case 1143:
			return u(a * (-a + b + c))
					* (2 * b * c - u(-(b * (a - b - c) * c * (a + b + c))));
			/* case 1144: perl script returns zero */
		case 1145:
			return (2 * a - b - c)
					* (-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 1146:
			return p(b - c, 2) * p(-a + b + c, 2);
		case 1147:
			return a4 * T * (a4 + b4 + c4 - 2 * a2 * R);
		case 1148:
			return U
					* V
					* (a4 * b * c + a5 * (b + c) - b * c * Q - 2 * a3
							* (b3 + c3) + a * (b5 - b4 * c - b * c4 + c5));
		case 1149:
			return a2 * (b2 - 4 * b * c + c2 + a * (b + c));
		case 1150:
			return a3 - b * c * (b + c) - a * R;
		case 1151:
			return 2 * a2 * T - a2 * S;
		case 1152:
			return 2 * a2 * T + a2 * S;
		case 1153:
			return 10 * a4 + 4 * b4 - 10 * b2 * c2 + 4 * c4 - 13 * a2 * R;
		case 1154:
			return a2 * (a2 - b2 - b * c - c2) * (a2 - b2 + b * c - c2)
					* (-Q + a2 * R);
		case 1155:
			return a * (2 * a2 - p(b - c, 2) - a * (b + c));
		case 1156:
			return a * (a2 - 2 * b2 + a * (b - 2 * c) + b * c + c2)
					* (a2 + b2 + b * c - 2 * c2 + a * (-2 * b + c));
		case 1157:
			return a2
					* (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2))
					* (a6 - 3 * a4 * R - Q * R + a2
							* (3 * b4 - b2 * c2 + 3 * c4));
		case 1158:
			return a
					* (a6 + 2 * a3 * b * c * (b + c) - 2 * a * b * p(b - c, 2)
							* c * (b + c) + a4 * (-3 * b2 + 2 * b * c - 3 * c2)
							- Q * R + a2 * p(b - c, 2)
							* (3 * b2 + 4 * b * c + 3 * c2));
		case 1159:
			return a
					* (a3 - 4 * a2 * (b + c) + 4 * p(b - c, 2) * (b + c) - a
							* R);
		case 1160:
			return 4 * a2 * (a2 * b2 - b4 + a2 * c2 - c4) + a2 * T * S;
		case 1161:
			return -4 * a2 * (a2 * b2 - b4 + a2 * c2 - c4) + a2 * T * S;
		case 1162:
			return -(U * V * (a4 - 10 * a2 * b2 + b4 - 10 * a2 * c2 - 2 * b2
					* c2 + c4))
					+ 2 * U * V * (2 * a2 + b2 + c2) * S;
		case 1163:
			return U
					* V
					* (a4 - 10 * a2 * b2 + b4 - 10 * a2 * c2 - 2 * b2 * c2 + c4)
					+ 2 * U * V * (2 * a2 + b2 + c2) * S;
		case 1164:
			return -(U * V * (a4 + 6 * a2 * b2 + b4 + 6 * a2 * c2 - 2 * b2 * c2 + c4))
					+ 2 * (2 * a2 - b2 - c2) * U * V * S;
		case 1165:
			return U * V
					* (a4 + 6 * a2 * b2 + b4 + 6 * a2 * c2 - 2 * b2 * c2 + c4)
					+ 2 * (2 * a2 - b2 - c2) * U * V * S;
		case 1166:
			return a2
					* (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2))
					* (a6 - a4 * (2 * b2 + c2) + p(-(b2 * c) + c3, 2) + a2
							* (b4 - 2 * b2 * c2 - c4))
					* (a6 - a4 * (b2 + 2 * c2) + p(b3 - b * c2, 2) + a2
							* (-b4 - 2 * b2 * c2 + c4));
		case 1167:
			return a2
					* (a4 - a3 * c + b * p(b - c, 2) * (b + c) + a * c
							* p(b + c, 2) - a2 * (2 * b2 - b * c + c2))
					* (a4 - a3 * b + p(b - c, 2) * c * (b + c) + a * b
							* p(b + c, 2) - a2 * (b2 - b * c + 2 * c2));
		case 1168:
			return a * (a + b - 2 * c) * (a - 2 * b + c)
					* (a2 - a * b + b2 - c2) * (a2 - b2 - a * c + c2);
		case 1169:
			return a2 * (a + b) * (a + c) * (a2 + a * c + b * (b + c))
					* (a2 + a * b + c * (b + c));
		case 1170:
			return a * (a + b - c) * (a - b + c)
					* (a2 + b * (b - c) - a * (2 * b + c))
					* (a2 + c * (-b + c) - a * (b + 2 * c));
		case 1171:
			return a2 * (a + b) * (a + c) * (a + 2 * b + c) * (a + b + 2 * c);
		case 1172:
			return a * (a + b) * (a - b - c) * (a + c) * U * V;
		case 1173:
			return a2
					* (a4 + 2 * b4 - 3 * b2 * c2 + c4 - a2 * (3 * b2 + 2 * c2))
					* (a4 + b4 - 3 * b2 * c2 + 2 * c4 - a2 * (2 * b2 + 3 * c2));
		case 1174:
			return a2 * (a2 + b * (b - c) - a * (2 * b + c))
					* (a2 + c * (-b + c) - a * (b + 2 * c));
		case 1175:
			return a2 * (a + b) * (a + c)
					* (a3 - a2 * b + b3 - b * c2 - a * p(b + c, 2))
					* (a3 - a2 * c - b2 * c + c3 - a * p(b + c, 2));
		case 1176:
			return a2 * (a2 + b2) * T * (a2 + c2);
		case 1177:
			return a2 * (a6 - a4 * b2 + b6 - b2 * c4 - a2 * Q)
					* (a6 - a4 * c2 - b4 * c2 + c6 - a2 * Q);
		case 1178:
			return a2 * (a + b) * (a + c) * (b2 + a * c) * (a * b + c2);
		case 1179:
			return U
					* V
					* (a6 - a4 * (2 * b2 + c2) + p(-(b2 * c) + c3, 2) + a2
							* (b4 - 2 * b2 * c2 - c4))
					* (a6 - a4 * (b2 + 2 * c2) + p(b3 - b * c2, 2) + a2
							* (-b4 - 2 * b2 * c2 + c4));
		case 1180:
			return a2 * (b4 + b2 * c2 + c4 + a2 * R);
		case 1181:
			return a2 * T * (a6 + 3 * a2 * Q - 3 * a4 * R - Q * R);
		case 1182:
			return a2
					* (a5 * (b + c) + 2 * a2 * Q - Q * (b2 - b * c + c2) - a4
							* (b2 + b * c + c2) - 2 * a3 * (b3 + c3) + a
							* (b5 - b4 * c - b * c4 + c5));
		case 1183:
			return a
					* (a - b - c)
					* (a5 + 2 * a4 * (b + c) + b * p(b - c, 2) * c * (b + c)
							+ a * p(b - c, 2) * (b2 + b * c + c2) + a3
							* (2 * b2 + 5 * b * c + 2 * c2) + a2
							* (2 * b3 + b2 * c + b * c2 + 2 * c3));
		case 1184:
			return a2 * (a2 + p(b - c, 2)) * (a2 + p(b + c, 2));
		case 1185:
			return a3 * (b * c * (b + c) + a * (b2 + b * c + c2));
		case 1186:
			return a4 * (b2 * c2 * R + a2 * (b4 + b2 * c2 + c4));
		case 1187:
			return a2
					* (b + c)
					* (b5 + b4 * c + b3 * c2 + b2 * c3 + b * c4 + c5 + a4
							* (b + c) + 2 * a3 * (b2 + b * c + c2) + 2 * a2
							* (b3 + 2 * b2 * c + 2 * b * c2 + c3) + 2 * a
							* (b4 + 2 * b3 * c + b2 * c2 + 2 * b * c3 + c4));
		case 1188:
			return a3
					* (a - b - c)
					* (-(b * p(b - c, 4) * c) - a * p(b - c, 4) * (b + c) + a4
							* (b2 + b * c + c2) + a2 * p(b - c, 2)
							* (3 * b2 + 4 * b * c + 3 * c2) - a3
							* (3 * b3 + b2 * c + b * c2 + 3 * c3));
		case 1189:
			return a2
					* (-(b * c) + a * (b + c))
					* (a4 * p(b - c, 4) + a3 * p(b - c, 4) * (b + c) - b3 * c3
							* (b2 + b * c + c2) - a2 * b * p(b - c, 2) * c
							* (3 * b2 + 4 * b * c + 3 * c2) + a * b2 * c2
							* (3 * b3 + b2 * c + b * c2 + 3 * c3));
		case 1190:
			return a2
					* (a - b - c)
					* (a3 + 3 * a * p(b - c, 2) - 3 * a2 * (b + c) - p(b - c, 2)
							* (b + c));
		case 1191:
			return a2 * (a2 + p(b - c, 2) + 2 * a * (b + c));
		case 1192:
			return a2
					* (3 * a8 - 6 * a4 * Q - 4 * a6 * R + 12 * a2 * Q * R - Q
							* (5 * b4 + 6 * b2 * c2 + 5 * c4));
		case 1193:
			return a2 * (b2 + c2 + a * (b + c));
		case 1194:
			return a2 * (b4 + c4 + a2 * R);
		case 1195:
			return a2
					* (a - b - c)
					* (b5 + 2 * a3 * b * c - b4 * c - b * c4 + c5 + a4
							* (b + c) - 2 * a2 * p(b - c, 2) * (b + c));
		case 1196:
			return a2 * (Q + a2 * R);
		case 1197:
			return a3 * (b * c * (b + c) + a * R);
		case 1198:
			return a2
					* (-(b * c) + a * (b + c))
					* (a4 * p(b - c, 4) - b3 * c3 * R - a2 * b * p(b - c, 2)
							* c * (3 * b2 + 5 * b * c + 3 * c2) + a3
							* p(b - c, 2) * (b3 + c3) + 3 * a * b2 * c2
							* (b3 + c3));
		case 1199:
			return a2
					* (a8 - 4 * a6 * R - 4 * a2 * Q * R + Q
							* (b4 - b2 * c2 + c4) + 3 * a4
							* (2 * b4 + b2 * c2 + 2 * c4));
		default:
			return Double.NaN;
		}
	}

	private double weight1200to1299(int k, double a, double b, double c) {

		switch (k) {
		case 1200:
			return a2
					* (a - b - c)
					* (-2 * a * p(b - c, 2) + a2 * (b + c) + p(b - c, 2)
							* (b + c));
		case 1201:
			return a2 * (p(b - c, 2) + a * (b + c));
		case 1202:
			return a2
					* (a3 * (b + c) + 3 * a * p(b - c, 2) * (b + c) - 3 * a2
							* R - p(b - c, 2) * R);
		case 1203:
			return a2 * (a2 + b2 + b * c + c2 + 2 * a * (b + c));
		case 1204:
			return a2 * T * (a6 - 3 * a2 * Q + 2 * Q * R);
		case 1205:
			return -(a2 * (a10 * R - a8 * p(b2 + c2, 2) - p(b4 - c4, 2)
					* (b4 + c4) + 2 * a4 * Q * (b4 + 3 * b2 * c2 + c4) + a6
					* (-2 * b6 + 3 * b4 * c2 + 3 * b2 * c4 - 2 * c6) + a2 * Q
					* (b6 - 2 * b4 * c2 - 2 * b2 * c4 + c6)));
		case 1206:
			return a2
					* (b2 * c2 + 2 * a * b * c * (b + c) + a2
							* (b2 + b * c + c2));
		case 1207:
			return a2 * b4 * c4 + 2 * a4 * b2 * c2 * R + a6
					* (b4 + b2 * c2 + c4);
		case 1208:
			return a2
					* (a7 * (b + c) - a * p(b - c, 4) * p(b + c, 3) + a6 * R
							- p(b - c, 2) * p(b + c, 4) * R - a4 * p(b - c, 2)
							* (3 * b2 + 8 * b * c + 3 * c2) - a5
							* (3 * b3 + b2 * c + b * c2 + 3 * c3) + a3
							* p(b - c, 2)
							* (3 * b3 + 5 * b2 * c + 5 * b * c2 + 3 * c3) + a2
							* p(b - c, 2)
							* (3 * b4 + 10 * b3 * c + 6 * b2 * c2 + 10 * b * c3 + 3 * c4));
		case 1209:
			return -((-Q + a2 * R) * (a4 * R + Q * R - 2 * a2
					* (b4 + b2 * c2 + c4)));
		case 1210:
			return -(a2 * p(b - c, 2)) + a3 * (b + c) - a * p(b - c, 2)
					* (b + c) + Q;
		case 1211:
			return (b + c) * (b2 + c2 + a * (b + c));
		case 1212:
			return a * (a - b - c) * (-p(b - c, 2) + a * (b + c));
		case 1213:
			return (b + c) * (2 * a + b + c);
		case 1214:
			return a * (a + b - c) * (a - b + c) * (b + c) * T;
		case 1215:
			return (b + c) * (a2 + b * c);
		case 1216:
			return -(a2 * T * (a4 * R + Q * R - 2 * a2 * (b4 + b2 * c2 + c4)));
		case 1217:
			return U
					* V
					* (a6 - p(b2 - c2, 3) - a4 * (3 * b2 + c2) + a2
							* (3 * b4 + 6 * b2 * c2 - c4))
					* (a6 + p(b2 - c2, 3) - a4 * (b2 + 3 * c2) + a2
							* (-b4 + 6 * b2 * c2 + 3 * c4));
		case 1218:
			return b * c * (b2 * c + a2 * (b + c) + a * b * (b + c))
					* (b * c2 + a2 * (b + c) + a * c * (b + c));
		case 1219:
			return (a2 - 2 * a * (b - c) + p(b + c, 2))
					* (a2 + 2 * a * (b - c) + p(b + c, 2));
		case 1220:
			return (a2 + a * c + b * (b + c)) * (a2 + a * b + c * (b + c));
		case 1221:
			return b * c * (a * b2 + b2 * c + a2 * (b + c))
					* (a * c2 + b * c2 + a2 * (b + c));
		case 1222:
			return (a2 + a * (-2 * b + c) + b * (b + c))
					* (a2 + a * (b - 2 * c) + c * (b + c));
		case 1223:
			return (a4 - p(b - c, 3) * c - a * p(b - c, 2) * (b + 2 * c) - a3
					* (3 * b + 2 * c) + a2 * (3 * b2 + 3 * b * c + 2 * c2))
					* (a4 + b * p(b - c, 3) - a * p(b - c, 2) * (2 * b + c)
							- a3 * (2 * b + 3 * c) + a2
							* (2 * b2 + 3 * b * c + 3 * c2));
		case 1224:
			return (a2 + p(b + c, 2) + a * (2 * b + c))
					* (a2 + p(b + c, 2) + a * (b + 2 * c));
		case 1225:
			return b2 * c2 * (Q - a2 * R)
					* (a4 * R + Q * R - 2 * a2 * (b4 + b2 * c2 + c4));
		case 1226:
			return b2
					* c2
					* (-(a2 * p(b - c, 2)) + a3 * (b + c) - a * p(b - c, 2)
							* (b + c) + Q);
		case 1227:
			return b * c * (-2 * a + b + c) * (-a2 + b2 - b * c + c2);
		case 1228:
			return b2 * c2 * (b + c) * (b2 + c2 + a * (b + c));
		case 1229:
			return b * c * (-a + b + c) * (p(b - c, 2) - a * (b + c));
		case 1230:
			return b2 * c2 * (b + c) * (2 * a + b + c);
		case 1231:
			return b * (-a + b - c) * (a + b - c) * c * (b + c) * (-T);
		case 1232:
			return b2 * c2 * (2 * a4 + Q - 3 * a2 * R);
		case 1233:
			return b2 * c2 * (p(b - c, 2) - a * (b + c));
		case 1234:
			return b2 * c2 * (b + c)
					* (-2 * a * b * c - a2 * (b + c) + p(b - c, 2) * (b + c));
		case 1235:
			return b2 * c2 * (-V) * U * R;
		case 1236:
			return b2 * c2 * (2 * a2 * b2 * c2 - a4 * R + Q * R);
		case 1237:
			return b2 * c2 * (b + c) * (a2 + b * c);
		case 1238:
			return -(T * (a4 * R + Q * R - 2 * a2 * (b4 + b2 * c2 + c4)));
		case 1239:
			return b2 * c2 * (a4 + a2 * R + b2 * R) * (a4 + a2 * R + c2 * R);
		case 1240:
			return b2 * c2 * (a2 + a * c + b * (b + c))
					* (a2 + a * b + c * (b + c));
		case 1241:
			return b2 * c2 * (a4 + b4 + a2 * c2 + b2 * c2)
					* (a4 + a2 * b2 + b2 * c2 + c4);
		case 1242:
			return -(a
					* (a4 * (b - c) - p(b - c, 3) * c * (b + c) + a2 * b
							* (-b2 + 4 * b * c + c2) + a3
							* (-b2 + b * c + 2 * c2) + a
							* (b4 + b3 * c + b2 * c2 - b * c3 - 2 * c4)) * (a4
					* (b - c) - b * p(b - c, 3) * (b + c) + a2 * c
					* (-b2 - 4 * b * c + c2) + a3 * (-2 * b2 - b * c + c2) + a
					* (2 * b4 + b3 * c - b2 * c2 - b * c3 - c4)));
		case 1243:
			return a
					* (a5 * (b + c) - a4 * b * (b + c) - b * p(b - c, 3)
							* p(b + c, 2) + 2 * a2 * (b4 - b2 * c2) - 2 * a3
							* (b3 + b2 * c + c3) + a
							* (b5 + b4 * c - 2 * b2 * c3 - b * c4 + c5))
					* (a5 * (b + c) - a4 * c * (b + c) + p(b - c, 3) * c
							* p(b + c, 2) - 2 * a3 * (b3 + b * c2 + c3) + a2
							* (-2 * b2 * c2 + 2 * c4) + a
							* (b5 - b4 * c - 2 * b3 * c2 + b * c4 + c5));
		case 1244:
			return a
					* (a4 * c + a2 * c3 + b2 * c * (b2 - c2) + a3 * (-b2 + c2) + a
							* (b4 + 2 * b3 * c + c4))
					* (a4 * b + a2 * b3 - b3 * c2 + b * c4 + a3 * (b2 - c2) + a
							* (b4 + 2 * b * c3 + c4));
		case 1245:
			return a2 * (b + c) * (a2 + 2 * a * b + b2 + c2)
					* (a2 + b2 + 2 * a * c + c2);
		case 1246:
			return -((-(b3 * c) + b * c3 + a3 * (b + c) - a * (b - c)
					* p(b + c, 2) + a2 * c * (b + 2 * c)) * (a3 * (b + c) + a
					* (b - c) * p(b + c, 2) + a2 * b * (2 * b + c) + b * c
					* (b2 - c2)));
		case 1247:
			return a * (a3 - b3 - 2 * b2 * c + c3 - a * b * (2 * b + c))
					* (a3 + b3 - 2 * b * c2 - c3 - a * c * (b + 2 * c));
		case 1248:
			return a
					* (a6
							* (b - c)
							* p(b + c, 2)
							+ b2
							* p(b - c, 2)
							* c2
							* p(b + c, 3)
							+ a7
							* (b2 + b * c - c2)
							+ a
							* b
							* c
							* Q
							* (b2 + b * c + c2)
							+ a2
							* (b - c)
							* p(b3 + b2 * c + b * c2 + c3, 2)
							- a5
							* (2 * b4 + b3 * c + b2 * c2 + b * c3 - 2 * c4)
							- a4
							* (2 * b5 + 2 * b4 * c + b3 * c2 + b2 * c3 - 2 * b
									* c4 - 2 * c5) + a3
							* (b6 - b5 * c + b4 * c2 + 2 * b3 * c3 - b2 * c4
									- b * c5 - c6))
					* (a6
							* (b - c)
							* p(b + c, 2)
							- b2
							* p(b - c, 2)
							* c2
							* p(b + c, 3)
							+ a7
							* (b2 - b * c - c2)
							- a
							* b
							* c
							* Q
							* (b2 + b * c + c2)
							+ a2
							* (b - c)
							* p(b3 + b2 * c + b * c2 + c3, 2)
							+ a5
							* (-2 * b4 + b3 * c + b2 * c2 + b * c3 + 2 * c4)
							+ a4
							* (-2 * b5 - 2 * b4 * c + b3 * c2 + b2 * c3 + 2 * b
									* c4 + 2 * c5) + a3
							* (b6 + b5 * c + b4 * c2 - 2 * b3 * c3 - b2 * c4
									+ b * c5 - c6));
		case 1249:
			return U * V * (3 * a4 - Q - 2 * a2 * R);
		case 1250:
			return a2 * (u(3) * (-a + b + c) * (a + b + c) - S);
		case 1251:
			return a / ((a + b - c) * (a - b + c) - u(3) * S);
		case 1252:
			return a2 * p(a - b, 2) * p(a - c, 2);
		case 1253:
			return a3 * p(-a + b + c, 2);
		case 1254:
			return a * p(a + b - c, 2) * p(a - b + c, 2) * p(b + c, 2);
		case 1255:
			return a * (a + 2 * b + c) * (a + b + 2 * c);
		case 1256:
			return a
					* p(a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2), 2)
					* p(a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2), 2);
		case 1257:
			return a * (a3 + 2 * b3 - a2 * c + b2 * c + c3 + a * (b2 - c2))
					* (a3 - a2 * b + b3 + b * c2 + 2 * c3 + a * (-b2 + c2));
		case 1258:
			return a * (a * b2 + b2 * c + a2 * (b + c))
					* (a * c2 + b * c2 + a2 * (b + c));
		case 1259:
			return a2 * (a - b - c) * (T * T);
		case 1260:
			return a2 * p(-a + b + c, 2) * T;
		case 1261:
			return a * (a - b - c) * (a2 + a * (-2 * b + c) + b * (b + c))
					* (a2 + a * (b - 2 * c) + c * (b + c));
		case 1262:
			return a2 * p(a - b, 2) * p(a - c, 2) * p(a + b - c, 2)
					* p(a - b + c, 2);
		case 1263:
			return (-Q + a2 * R)
					* (a6 - p(b2 - c2, 3) - a4 * (3 * b2 + c2) + a2
							* (3 * b4 + b2 * c2 - c4))
					* (a6 + p(b2 - c2, 3) - a4 * (b2 + 3 * c2) + a2
							* (-b4 + b2 * c2 + 3 * c4));
		case 1264:
			return -((a - b - c) * (T * T));
		case 1265:
			return -(p(-a + b + c, 2) * T);
		case 1266:
			return b2 - 4 * b * c + c2 + a * (b + c);
		case 1267:
			return 2 * b * c + S;
		case 1268:
			return (a + 2 * b + c) * (a + b + 2 * c);
		case 1269:
			return b2 * c2 * (2 * a + b + c);
		case 1270:
			return -2 * T + S;
		case 1271:
			return 2 * T + S;
		case 1272:
			return a8 - 4 * a6 * R + Q * (b4 + 4 * b2 * c2 + c4) + a4
					* (6 * b4 + b2 * c2 + 6 * c4) + a2
					* (-4 * b6 + b4 * c2 + b2 * c4 - 4 * c6);
		case 1273:
			return -((a2 - b2 - b * c - c2) * (a2 - b2 + b * c - c2) * (-Q + a2
					* R));
		case 1274:
			return 1 / (u(a * (-a + b + c)) * (2 * b * c - u(-(b * (a - b - c)
					* c * (a + b + c)))));
		case 1275:
			return p(a - b, 2) * p(a - c, 2) * p(a + b - c, 2)
					* p(a - b + c, 2);
		case 1276:
			return u(3)
					* a
					* (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 - c3) - a * (a - b - c) * S;
		case 1277:
			return u(3)
					* a
					* (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 - c3) + a * (a - b - c) * S;
		case 1278:
			return 3 * b * c - a * (b + c);
		case 1279:
			return a * (2 * a2 + p(b - c, 2) - a * (b + c));
		case 1280:
			return a * (a2 + b2 - b * c + 2 * c2 - a * (2 * b + c))
					* (a2 + 2 * b2 - b * c + c2 - a * (b + 2 * c));
		case 1281:
			return (a2 - b * c) * (a3 - b3 + a * b * c - c3);
		case 1282:
			return a
					* (a4 + a3 * (b + c) - p(b - c, 2) * (b2 + b * c + c2) - a2
							* (2 * b2 + 3 * b * c + 2 * c2) + a
							* (b3 + b2 * c + b * c2 + c3));
		case 1283:
			return a2 * (a4 - b4 + b2 * c2 - c4 - a3 * (b + c) + a * (b3 + c3));
		case 1284:
			return a * (a + b - c) * (a - b + c) * (b + c) * (a2 - b * c);
		case 1285:
			return a * (9 * a4 - Q);
		case 1286:
			return (a - b)
					* (a + b)
					* (a - c)
					* (a + c)
					* (a6 + a4 * (b2 - c2) + Q * R + a2
							* (b4 - 2 * b2 * c2 - c4))
					* (a6 + a4 * (-b2 + c2) + Q * R + a2
							* (-b4 - 2 * b2 * c2 + c4));
		case 1287:
			return (a - b) * (a + b) * (a - c) * (a + c)
					* (a6 + a4 * (b2 - c2) + Q * R + a2 * (b4 - b2 * c2 - c4))
					* (a6 + a4 * (-b2 + c2) + Q * R - a2 * (b4 + b2 * c2 - c4));
		case 1288:
			return (a - b)
					* (a + b)
					* (a - c)
					* (a + c)
					* U
					* V
					* (a8 + 2 * a4 * b4 - 2 * a6 * R + p(b2 - c2, 3) * R - 2
							* a2 * (b6 - c6))
					* (a8 + 2 * a4 * c4 - 2 * a6 * R - p(b2 - c2, 3) * R + 2
							* a2 * (b6 - c6));
		case 1289:
			return (a - b) * (a + b) * (a - c) * (a + c) * U * V
					* (a4 + b4 - c4) * (a4 - b4 + c4);
		case 1290:
			return a
					* (a - b)
					* (a - c)
					* (a3 + a2 * (b - c) + p(b - c, 2) * (b + c) + a
							* (b2 - b * c - c2))
					* (a3 + a2 * (-b + c) + p(b - c, 2) * (b + c) - a
							* (b2 + b * c - c2));
		case 1291:
			return a2
					* (a - b)
					* (a + b)
					* (a - c)
					* (a + c)
					* (a6 - p(b2 - c2, 3) - a4 * (3 * b2 + c2) + a2
							* (3 * b4 + b2 * c2 - c4))
					* (a6 + p(b2 - c2, 3) - a4 * (b2 + 3 * c2) + a2
							* (-b4 + b2 * c2 + 3 * c4));
		case 1292:
			return a * (a - b) * (a2 - 2 * a * b + p(b - c, 2)) * (a - c)
					* (a2 + p(b - c, 2) - 2 * a * c);
		case 1293:
			return a2 * (a - b) * (a + b - 3 * c) * (a - c) * (a - 3 * b + c);
		case 1294:
			return (a8 + c2 * p(-b2 + c2, 3) + a6 * (-3 * b2 + 2 * c2) + 3 * a4
					* (b4 + b2 * c2 - 2 * c4) - a2
					* (b6 + 4 * b4 * c2 - 3 * b2 * c4 - 2 * c6))
					* (a8 + a6 * (2 * b2 - 3 * c2) + b2 * p(b2 - c2, 3) + 3
							* a4 * (-2 * b4 + b2 * c2 + c4) + a2
							* (2 * b6 + 3 * b4 * c2 - 4 * b2 * c4 - c6));
		case 1295:
			return a
					* (a6 - a5 * b - p(b - c, 3) * c * p(b + c, 2) - a4
							* (2 * b2 - 3 * b * c + c2) + 2 * a3
							* (b3 - b * c2) + a2
							* (b4 - 2 * b3 * c + 4 * b2 * c2 - 2 * b * c3 - c4) - a
							* (b5 + 2 * b3 * c2 - 3 * b * c4))
					* (a6
							- a5
							* c
							+ b
							* p(b - c, 3)
							* p(b + c, 2)
							- a4
							* (b2 - 3 * b * c + 2 * c2)
							+ a3
							* (-2 * b2 * c + 2 * c3)
							+ a2
							* (-b4 - 2 * b3 * c + 4 * b2 * c2 - 2 * b * c3 + c4) + a
							* (3 * b4 * c - 2 * b2 * c3 - c5));
		case 1296:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c)
					* (a2 + b2 - 5 * c2) * (a2 - 5 * b2 + c2);
		case 1297:
			return a2 * (a6 - 2 * b6 - a4 * c2 + b4 * c2 + c6 + a2 * (b4 - c4))
					* (a6 - a4 * b2 + b6 + b2 * c4 - 2 * c6 + a2 * (-b4 + c4));
		case 1298:
			return a2
					* (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2))
					* (a6 * b2 + c4 * Q + a4 * (-2 * b4 + c4) + a2
							* (b6 + b2 * c4 - 2 * c6))
					* (a6 * c2 + b4 * Q + a4 * (b4 - 2 * c4) + a2
							* (-2 * b6 + b4 * c2 + c6));
		case 1299:
			return a2
					* U
					* V
					* (a8 - a6 * (b2 + 4 * c2) + Q * (2 * b4 + b2 * c2 + c4)
							+ a4 * (b4 + b2 * c2 + 6 * c4) + a2
							* (-3 * b6 + 2 * b4 * c2 + b2 * c4 - 4 * c6))
					* (a8 - a6 * (4 * b2 + c2) + a4 * (6 * b4 + b2 * c2 + c4)
							+ Q * (b4 + b2 * c2 + 2 * c4) + a2
							* (-4 * b6 + b4 * c2 + 2 * b2 * c4 - 3 * c6));
		default:
			return Double.NaN;
		}
	}

	private double weight1300to1399(int k, double a, double b, double c) {

		switch (k) {
		case 1300:
			return U
					* V
					* (a6 - a4 * (2 * b2 + c2) + p(-(b2 * c) + c3, 2) + a2
							* (b4 + 2 * b2 * c2 - c4))
					* (a6 - a4 * (b2 + 2 * c2) + p(b3 - b * c2, 2) + a2
							* (-b4 + 2 * b2 * c2 + c4));
		case 1301:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c) * U * V
					* (a4 + b4 + 2 * b2 * c2 - 3 * c4 - 2 * a2 * (b2 - c2))
					* (a4 - 3 * b4 + 2 * b2 * c2 + c4 + 2 * a2 * (b2 - c2));
		case 1302:
			return (a - b) * (a + b) * (a - c) * (a + c)
					* (a4 - 2 * a2 * (b2 - 2 * c2) + Q)
					* (a4 + a2 * (4 * b2 - 2 * c2) + Q);
		case 1303:
			return a2
					* (a - b)
					* (a + b)
					* (a - c)
					* (a + c)
					* (a6 * c2 - b4 * Q - a4 * (b4 + 2 * c4) + a2
							* (2 * b6 - 3 * b4 * c2 + c6))
					* (a6 * b2 - c4 * Q - a4 * (2 * b4 + c4) + a2
							* (b6 - 3 * b2 * c4 + 2 * c6));
		case 1304:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c) * U * V
					* (a4 - 2 * b4 + b2 * c2 + c4 + a2 * (b2 - 2 * c2))
					* (a4 + b4 + b2 * c2 - 2 * c4 + a2 * (-2 * b2 + c2));
		case 1305:
			return (a - b) * (a - c) * (a + b - c) * (a - b + c)
					* (a3 - b2 * c + c3 - a * b * (b + c))
					* (a3 + b3 - b * c2 - a * c * (b + c));
		case 1306:
			return -(a2 * (a - b) * (a + b) * (a - c) * (a + c) * (a4 - 2 * a2
					* b2 + b4 - 2 * a2 * c2 - 6 * b2 * c2 + c4))
					+ 2 * a2 * (a - b) * (a + b) * (a - c) * (a + c) * R * S;
		case 1307:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c)
					* (a4 - 2 * a2 * b2 + b4 - 2 * a2 * c2 - 6 * b2 * c2 + c4)
					+ 2 * a2 * (a - b) * (a + b) * (a - c) * (a + c) * R * S;
		case 1308:
			return a * (a - b) * (a2 + a * (b - 2 * c) + p(b - c, 2)) * (a - c)
					* (a2 + p(b - c, 2) + a * (-2 * b + c));
		case 1309:
			return (a - b) * (a - c) * U * V
					* (a3 - a2 * b + b3 - a * p(b - c, 2) - b * c2)
					* (a3 - a * p(b - c, 2) - a2 * c - b2 * c + c3);
		case 1310:
			return a * (a - b) * (a - c) * (a2 + 2 * a * b + b2 + c2)
					* (a2 + b2 + 2 * a * c + c2);
		case 1311:
			return (a4 - a3 * c - b2 * c2 + a * (b - c) * c2 + c4 + a2 * b
					* (-b + c))
					* (a4 - a3 * b + b4 + a2 * (b - c) * c - b2 * c2 + a * b2
							* (-b + c));
		case 1312:
			return -((a2 * b2 - b4 + a2 * c2 + 2 * b2 * c2 - c4) * (a6 - a4
					* b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2 * c2 - b4 * c2
					- a2 * c4 - b2 * c4 + c6))
					- a
					* b
					* c
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 1313:
			return -((a2 * b2 - b4 + a2 * c2 + 2 * b2 * c2 - c4) * (a6 - a4
					* b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2 * c2 - b4 * c2
					- a2 * c4 - b2 * c4 + c6))
					+ a
					* b
					* c
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 1314:
			return (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4)
					* u(-a10
							- 2
							* a7
							* p(b - c, 2)
							* (b + c)
							+ 2
							* a
							* b
							* p(b - c, 2)
							* c
							* p(b + c, 3)
							* R
							+ 2
							* a8
							* (b2 + b * c + c2)
							- a6
							* p(b2 + b * c + c2, 2)
							- p(b + c, 4)
							* p(b3 - 2 * b2 * c + 2 * b * c2 - c3, 2)
							+ 2
							* a5
							* p(b - c, 2)
							* (2 * b3 + 3 * b2 * c + 3 * b * c2 + 2 * c3)
							- a4
							* p(b + c, 2)
							* (b4 - 3 * b2 * c2 + c4)
							+ a2
							* Q
							* (2 * b4 + 2 * b3 * c + b2 * c2 + 2 * b * c3 + 2 * c4)
							- 2
							* a3
							* p(b - c, 2)
							* (b5 + 3 * b4 * c + 6 * b3 * c2 + 6 * b2 * c3 + 3
									* b * c4 + c5))
					+ (a + b)
					* (a + c)
					* (2 * a5 - 2 * a4 * b + a2 * b3 - 2 * a * b4 + b5 - 2 * a4
							* c + 2 * a3 * b * c - a2 * b2 * c + b4 * c - a2
							* b * c2 + 4 * a * b2 * c2 - 2 * b3 * c2 + a2 * c3
							- 2 * b2 * c3 - 2 * a * c4 + b * c4 + c5) * S;
		case 1315:
			return -((2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4) * u(-a10
					- 2
					* a7
					* p(b - c, 2)
					* (b + c)
					+ 2
					* a
					* b
					* p(b - c, 2)
					* c
					* p(b + c, 3)
					* R
					+ 2
					* a8
					* (b2 + b * c + c2)
					- a6
					* p(b2 + b * c + c2, 2)
					- p(b + c, 4)
					* p(b3 - 2 * b2 * c + 2 * b * c2 - c3, 2)
					+ 2
					* a5
					* p(b - c, 2)
					* (2 * b3 + 3 * b2 * c + 3 * b * c2 + 2 * c3)
					- a4
					* p(b + c, 2)
					* (b4 - 3 * b2 * c2 + c4)
					+ a2
					* Q
					* (2 * b4 + 2 * b3 * c + b2 * c2 + 2 * b * c3 + 2 * c4)
					- 2
					* a3
					* p(b - c, 2)
					* (b5 + 3 * b4 * c + 6 * b3 * c2 + 6 * b2 * c3 + 3 * b * c4 + c5)))
					+ (a + b)
					* (a + c)
					* (2 * a5 - 2 * a4 * b + a2 * b3 - 2 * a * b4 + b5 - 2 * a4
							* c + 2 * a3 * b * c - a2 * b2 * c + b4 * c - a2
							* b * c2 + 4 * a * b2 * c2 - 2 * b3 * c2 + a2 * c3
							- 2 * b2 * c3 - 2 * a * c4 + b * c4 + c5) * S;
		case 1316:
			return a8 + a4 * b2 * c2 + b2 * c2 * Q - a6 * R;
		case 1317:
			return (a + b - c) * (a - b + c) * p(-2 * a + b + c, 2);
		case 1318:
			return a2 * p(a + b - 2 * c, 2) * (a - b - c) * p(a - 2 * b + c, 2);
		case 1319:
			return a * (2 * a - b - c) * (a + b - c) * (a - b + c);
		case 1320:
			return a * (a + b - 2 * c) * (a - b - c) * (a - 2 * b + c);
		case 1321:
			return U
					* V
					* (a6 - 3 * a2 * b4 + 2 * b6 - 2 * a2 * b2 * c2 - 2 * b4
							* c2 - 3 * a2 * c4 - 2 * b2 * c4 + 2 * c6)
					+ U
					* V
					* (2 * a4 - 3 * a2 * b2 + b4 - 3 * a2 * c2 - 2 * b2 * c2 + c4)
					* S;
		case 1322:
			return -(U * V * (a6 - 3 * a2 * b4 + 2 * b6 - 2 * a2 * b2 * c2 - 2
					* b4 * c2 - 3 * a2 * c4 - 2 * b2 * c4 + 2 * c6))
					+ U
					* V
					* (2 * a4 - 3 * a2 * b2 + b4 - 3 * a2 * c2 - 2 * b2 * c2 + c4)
					* S;
		case 1323:
			return (a + b - c) * (a - b + c)
					* (2 * a2 - p(b - c, 2) - a * (b + c));
		case 1324:
			return a2
					* (a5 - b5 + b3 * c2 + b2 * c3 - c5 - a * b * c * R - a3
							* (b2 - b * c + c2) + a2 * (b3 + c3));
		case 1325:
			return a * (a + b) * (a + c)
					* (a4 + a2 * b * c - a * b * c * (b + c) - Q);
		case 1326:
			return a2 * (a + b) * (a + c)
					* (a2 - b2 - b * c - c2 + a * (b + c));
		case 1327:
			return 4 * a4 + a2 * b2 - 5 * b4 + a2 * c2 + 10 * b2 * c2 - 5 * c4
					+ 3 * a2 * S;
		case 1328:
			return -4 * a4 - a2 * b2 + 5 * b4 - a2 * c2 - 10 * b2 * c2 + 5 * c4
					+ 3 * a2 * S;
		case 1329:
			return -((a - b - c) * (p(b - c, 2) * (b + c) + a * R));
		case 1330:
			return -a4 + b4 - a2 * b * c + b3 * c + b * c3 + c4 - a3 * (b + c)
					+ a * (b3 + b2 * c + b * c2 + c3);
		case 1331:
			return a2 * (a - b) * (a - c) * T;
		case 1332:
			return a * (a - b) * (a - c) * T;
		case 1333:
			return a3 * (a + b) * (a + c);
		case 1334:
			return a2 * (a - b - c) * (b + c);
		case 1335:
			return -2 * a2 * b * c + a2 * S;
		case 1336:
			return a4 - 2 * a2 * b2 + b4 - 4 * a2 * b * c - 2 * a2 * c2 - 2
					* b2 * c2 + c4 + 2 * a * (b + c) * S;
		case 1337:
			return 3
					* a2
					* (a8 - a6 * b2 - 3 * a4 * b4 + 5 * a2 * b6 - 2 * b8 - a6
							* c2 - 9 * a4 * b2 * c2 + 3 * a2 * b4 * c2 + 7 * b6
							* c2 - 3 * a4 * c4 + 3 * a2 * b2 * c4 - 10 * b4
							* c4 + 5 * a2 * c6 + 7 * b2 * c6 - 2 * c8)
					+ u(3)
					* a2
					* (a6 - 6 * a4 * b2 + 3 * a2 * b4 + 2 * b6 - 6 * a4 * c2
							- a2 * b2 * c2 + b4 * c2 + 3 * a2 * c4 + b2 * c4 + 2 * c6)
					* S;
		case 1338:
			return -3
					* a2
					* (a8 - a6 * b2 - 3 * a4 * b4 + 5 * a2 * b6 - 2 * b8 - a6
							* c2 - 9 * a4 * b2 * c2 + 3 * a2 * b4 * c2 + 7 * b6
							* c2 - 3 * a4 * c4 + 3 * a2 * b2 * c4 - 10 * b4
							* c4 + 5 * a2 * c6 + 7 * b2 * c6 - 2 * c8)
					+ u(3)
					* a2
					* (a6 - 6 * a4 * b2 + 3 * a2 * b4 + 2 * b6 - 6 * a4 * c2
							- a2 * b2 * c2 + b4 * c2 + 3 * a2 * c4 + b2 * c4 + 2 * c6)
					* S;
		case 1339:
			return a
					* (a + b - 3 * c)
					* (2 * a - b - c)
					* (a - 3 * b + c)
					* (a3 - b3 + 3 * b2 * c + 3 * b * c2 - c3 + a2 * (b + c) - a
							* (b2 + 4 * b * c + c2));
		case 1340:
			return a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2) + a2 * T
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4);
		case 1341:
			return -(a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)) + a2 * T
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4);
		case 1342:
			return -a4 + a2 * u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1343:
			return -(a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)) + a2 * T
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1344:
			return -(a * b * c * (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4))
					+ a2
					* T
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 1345:
			return a
					* b
					* c
					* (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4)
					+ a2
					* T
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 1346:
			return -(a * b * c * (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4))
					+ (-(a2 * b2) + b4 - a2 * c2 - 2 * b2 * c2 + c4)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 1347:
			return a
					* b
					* c
					* (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4)
					+ (-(a2 * b2) + b4 - a2 * c2 - 2 * b2 * c2 + c4)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 1348:
			return a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					+ (-(a2 * b2) + b4 - a2 * c2 - 2 * b2 * c2 + c4)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4);
		case 1349:
			return -(a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2))
					+ (-(a2 * b2) + b4 - a2 * c2 - 2 * b2 * c2 + c4)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4);
		case 1350:
			return a2 * (a4 - 3 * b4 - 2 * b2 * c2 - 3 * c4 + 2 * a2 * R);
		case 1351:
			return a2 * (a4 + 3 * b4 - 2 * b2 * c2 + 3 * c4 - 4 * a2 * R);
		case 1352:
			return -a6 + a4 * R + Q * R - a2 * p(b2 + c2, 2);
		case 1353:
			return 4 * a6 - 7 * a4 * R - Q * R + 4 * a2 * (b4 - b2 * c2 + c4);
		case 1354:
			return (a + b - c) * (a - b + c) * p(-2 * a4 + Q + a2 * R, 2);
		case 1355:
			return a4 * (a + b - c) * (a - b + c) * p(b4 + c4 - a2 * R, 2);
		case 1356:
			return -(a4 * p(b - c, 2) * (a + b - c) * (a - b + c) * p(b + c, 2));
		case 1357:
			return -(a2 * p(b - c, 2) * (a + b - c) * (a - b + c));
		case 1358:
			return p(b - c, 2) * (-a + b - c) * (a + b - c);
		case 1359:
			return (a + b - c)
					* (a - b + c)
					* p(-2 * a4 + a2 * p(b - c, 2) + a3 * (b + c) - a
							* p(b - c, 2) * (b + c) + Q, 2);
		case 1360:
			return (a + b - c) * (a - b + c)
					* p((-2 * a3 + a2 * (b + c) + p(b - c, 2) * (b + c)), 2);
		case 1361:
			return a2
					* (a + b - c)
					* (a - b + c)
					* p(2 * a * b * c - a2 * (b + c) + p(b - c, 2) * (b + c), 2);
		case 1362:
			return a2 * (a + b - c) * (a - b + c) * (b2 + c2 - a * (b + c))
					* (b2 + c2 - a * (b + c));
		case 1363:
			return -(a4 * p(b - c, 2) * (a + b - c) * (a - b + c) * p(b + c, 2) * p(
					-a2 + b2 + c2, 4));
		case 1364:
			return -(a2 * (a - b - c) * p(b - c, 2) * (T * T));
		case 1365:
			return p(b - c, 2) * (-a + b - c) * (a + b - c) * p(b + c, 2);
		case 1366:
			return (a + b - c) * (a - b + c) * p(-2 * a2 + b2 + c2, 2);
		case 1367:
			return p(b - c, 2) * (-a + b - c) * (a + b - c) * p(b + c, 2)
					* (T * T);
		case 1368:
			return -(T * (Q + a2 * R));
		case 1369:
			return -a6 + b6 + b4 * c2 + b2 * c4 + c6 - a4 * R + a2
					* (b4 + b2 * c2 + c4);
		case 1370:
			return -a6 - a4 * R + Q * R + a2 * p(b2 + c2, 2);
		case 1371:
			return 3 * a * (a - b - c) * (a + b - c) * (a - b + c) - 2
					* (a + b - c) * (a - b + c) * S;
		case 1372:
			return -3 * a * (a - b - c) * (a + b - c) * (a - b + c) - 2
					* (a + b - c) * (a - b + c) * S;
		case 1373:
			return a * (a - b - c) * (a + b - c) * (a - b + c) - 2
					* (a + b - c) * (a - b + c) * S;
		case 1374:
			return -(a * (a - b - c) * (a + b - c) * (a - b + c)) - 2
					* (a + b - c) * (a - b + c) * S;
		case 1375:
			return 2 * a5 + b5 - b4 * c - b * c4 + c5 - a2 * p(b - c, 2)
					* (b + c) - a * Q - a3 * R;
		case 1376:
			return a * (a2 + 2 * b * c - a * (b + c));
		case 1377:
			return 2 * a * b * c * (b + c) + a2 * S;
		case 1378:
			return -2 * a * b * c * (b + c) + a2 * S;
		case 1379:
			return -(a2 * (a2 * b2 - b4 + a2 * c2 - c4)) + a2 * T
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4);
		case 1380:
			return a2 * (a2 * b2 - b4 + a2 * c2 - c4) + a2 * T
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4);
		case 1381:
			return -(a2 * b * c * (a2 * b - b3 + a2 * c - 2 * a * b * c + b2
					* c + b * c2 - c3))
					+ a2
					* T
					* u(a
							* b
							* c
							* (a3 - a2 * b - a * b2 + b3 - a2 * c + 3 * a * b
									* c - b2 * c - a * c2 - b * c2 + c3));
		case 1382:
			return a2
					* b
					* c
					* (a2 * b - b3 + a2 * c - 2 * a * b * c + b2 * c + b * c2 - c3)
					+ a2
					* T
					* u(a
							* b
							* c
							* (a3 - a2 * b - a * b2 + b3 - a2 * c + 3 * a * b
									* c - b2 * c - a * c2 - b * c2 + c3));
		case 1383:
			return a2 * (2 * a2 + 2 * b2 - c2) * (2 * a2 - b2 + 2 * c2);
		case 1384:
			return a2 * (5 * a2 - b2 - c2);
		case 1385:
			return a
					* (2 * a3 - a2 * (b + c) + p(b - c, 2) * (b + c) - 2 * a
							* (b2 - b * c + c2));
		case 1386:
			return a * (2 * a2 + b2 + c2 + a * (b + c));
		case 1387:
			return 2 * a4 - 2 * a3 * (b + c) + 2 * a * p(b - c, 2) * (b + c)
					+ a2 * (-3 * b2 + 8 * b * c - 3 * c2) + Q;
		case 1388:
			return a * (a + b - c) * (a - b + c) * (3 * a - 2 * (b + c));
		case 1389:
			return a
					* (a3 + 2 * b3 - a * p(b - c, 2) - b2 * c - 2 * b * c2 + c3 - a2
							* (2 * b + c))
					* (a3 + b3 - a * p(b - c, 2) - 2 * b2 * c - b * c2 + 2 * c3 - a2
							* (b + 2 * c));
		case 1390:
			return a * (a2 + a * b + 2 * b2 + b * c + c2)
					* (a2 + b2 + a * c + b * c + 2 * c2);
		case 1391:
			return a2
					* (a4 + 2 * a3 * b + 2 * b4 - 2 * b3 * c - 3 * b2 * c2 + 2
							* b * c3 + c4 - 2 * a * b * (b2 - 4 * b * c + c2) - a2
							* (3 * b2 + 2 * b * c + 2 * c2))
					* (a4 + b4 + 2 * a3 * c + 2 * b3 * c - 3 * b2 * c2 - 2 * b
							* c3 + 2 * c4 - 2 * a * c * (b2 - 4 * b * c + c2) - a2
							* (2 * b2 + 2 * b * c + 3 * c2));
		case 1392:
			return a * (2 * a + 2 * b - 3 * c) * (a - b - c)
					* (2 * a - 3 * b + 2 * c);
		case 1393:
			return a * (a + b - c) * (a - b + c) * (-Q + a2 * R);
		case 1394:
			return a * (a + b - c) * (a - b + c) * (3 * a4 - Q - 2 * a2 * R);
		case 1395:
			return a3 * (a + b - c) * (a - b + c) * U * V;
		case 1396:
			return a * (a + b) * (a + b - c) * (a + c) * (a - b + c) * U * V;
		case 1397:
			return a4 * (a + b - c) * (a - b + c);
		case 1398:
			return a2 * p(a + b - c, 2) * p(a - b + c, 2) * U * V;
		case 1399:
			return a3 * (a + b - c) * (a - b + c) * (a2 - b2 - b * c - c2);
		default:
			return Double.NaN;
		}
	}

	private double weight1400to1499(int k, double a, double b, double c) {

		switch (k) {
		case 1400:
			return a2 * (a + b - c) * (a - b + c) * (b + c);
		case 1401:
			return a2 * (a + b - c) * (a - b + c) * R;
		case 1402:
			return a3 * (a + b - c) * (a - b + c) * (b + c);
		case 1403:
			return a2 * (a + b - c) * (a - b + c) * (-(b * c) + a * (b + c));
		case 1404:
			return a2 * (2 * a - b - c) * (a + b - c) * (a - b + c);
		case 1405:
			return a2 * (a + b - c) * (a - b + c) * (a - 2 * (b + c));
		case 1406:
			return a2 * (a + b - c) * (a - b + c)
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a * R);
		case 1407:
			return a2 * p(a + b - c, 2) * p(a - b + c, 2);
		case 1408:
			return a3 * (a + b) * (a + b - c) * (a + c) * (a - b + c);
		case 1409:
			return a3 * (a + b - c) * (a - b + c) * (b + c) * T;
		case 1410:
			return a3 * p(a + b - c, 2) * p(a - b + c, 2) * (b + c) * T;
		case 1411:
			return a * (a + b - c) * (a - b + c) * (a2 - a * b + b2 - c2)
					* (a2 - b2 - a * c + c2);
		case 1412:
			return a2 * (a + b) * (a + b - c) * (a + c) * (a - b + c);
		case 1413:
			return a2
					* (a + b - c)
					* (a - b + c)
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2))
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 1414:
			return a * (a - b) * (a + b) * (a - c) * (a + b - c) * (a + c)
					* (a - b + c);
		case 1415:
			return a3 * (a - b) * (a - c) * (a + b - c) * (a - b + c);
		case 1416:
			return a2 * (a + b - c) * (a - b + c) * (a2 + b * (b - c) - a * c)
					* (a2 - a * b + c * (-b + c));
		case 1417:
			return a3 * (a + b - 2 * c) * (a + b - c) * (a - 2 * b + c)
					* (a - b + c);
		case 1418:
			return a * (a + b - c) * (a - b + c) * (-p(b - c, 2) + a * (b + c));
		case 1419:
			return a * (a + b - c) * (a - b + c)
					* (3 * a2 - p(b - c, 2) - 2 * a * (b + c));
		case 1420:
			return a * (3 * a - b - c) * (a + b - c) * (a - b + c);
		case 1421:
			return a
					* (a + b - c)
					* (a - b + c)
					* (a3 - a2 * (b + c) - p(b - c, 2) * (b + c) + a
							* (b2 - b * c + c2));
		case 1422:
			return a
					* (a + b - c)
					* (a - b + c)
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2))
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 1423:
			return a * (a + b - c) * (a - b + c) * (-(b * c) + a * (b + c));
		case 1424:
			return a * (a + b - c) * (a - b + c) * (-(b2 * c2) + a2 * R);
		case 1425:
			return a2 * p(a + b - c, 2) * p(a - b + c, 2) * p(b + c, 2) * T;
		case 1426:
			return a * p(a + b - c, 2) * p(a - b + c, 2) * (b + c) * U * V;
		case 1427:
			return a * p(a + b - c, 2) * p(a - b + c, 2) * (b + c);
		case 1428:
			return a2 * (a + b - c) * (a - b + c) * (a2 - b * c);
		case 1429:
			return a * (a + b - c) * (a - b + c) * (a2 - b * c);
		case 1430:
			return a * U * V
					* (a4 - b * p(b - c, 2) * c - a2 * (b2 - b * c + c2));
		case 1431:
			return a2 * (a + b - c) * (a - b + c) * (b2 + a * c) * (a * b + c2);
		case 1432:
			return -(a * (a + b - c) * (a - b + c) * (b2 + a * c) * (a * b + c2));
		case 1433:
			return a2
					* T
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2))
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 1434:
			return (a + b) * (a + b - c) * (a + c) * (a - b + c);
		case 1435:
			return a * p(a + b - c, 2) * p(a - b + c, 2) * U * V;
		case 1436:
			return a2
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2))
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 1437:
			return a3 * (a + b) * (a + c) * T;
		case 1438:
			return a2 * (a2 + b * (b - c) - a * c)
					* (a2 - a * b + c * (-b + c));
		case 1439:
			return a * p(a + b - c, 2) * p(a - b + c, 2) * (b + c) * T;
		case 1440:
			return (a + b - c)
					* (a - b + c)
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2))
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 1441:
			return b * (-a + b - c) * (a + b - c) * c * (b + c);
		case 1442:
			return a * (a + b - c) * (a - b + c) * (a2 - b2 - b * c - c2);
		case 1443:
			return a * (a + b - c) * (a - b + c) * (a2 - b2 + b * c - c2);
		case 1444:
			return a * (a + b) * (a + c) * T;
		case 1445:
			return a * (a + b - c) * (a - b + c)
					* (a2 + b2 + c2 - 2 * a * (b + c));
		case 1446:
			return b * p(a + b - c, 2) * c * p(a - b + c, 2) * (b + c);
		case 1447:
			return (a + b - c) * (a - b + c) * (a2 - b * c);
		case 1448:
			return a * (a + b - c) * (a - b + c)
					* (a4 + 2 * a2 * b * c + 2 * a * b * c * (b + c) - Q);
		case 1449:
			return a * (3 * a + b + c);
		case 1450:
			return a2
					* (a + b - c)
					* (a - b + c)
					* (a2 * b - b3 + a2 * c - 4 * a * b * c - b2 * c - b * c2 - c3);
		case 1451:
			return a2 * (a + b - c) * (a - b + c)
					* (a3 - 2 * b * c * (b + c) - a * p(b + c, 2));
		case 1452:
			return a
					* (a + b - c)
					* (a - b + c)
					* U
					* V
					* (a3 - b3 - b2 * c - b * c2 - c3 + a2 * (b + c) - a
							* p(b + c, 2));
		case 1453:
			return a
					* (3 * a3 + 3 * a2 * (b + c) + p(b - c, 2) * (b + c) + a
							* p(b + c, 2));
		case 1454:
			return a * (a + b - c) * (a - b + c)
					* (a4 + Q - 2 * a2 * (b2 + b * c + c2));
		case 1455:
			return a
					* (a + b - c)
					* (a - b + c)
					* (2 * a4 - a2 * p(b - c, 2) - a3 * (b + c) + a
							* p(b - c, 2) * (b + c) - Q);
		case 1456:
			return a * (a + b - c) * (a - b + c)
					* (2 * a3 - a2 * (b + c) - p(b - c, 2) * (b + c));
		case 1457:
			return a2 * (a + b - c) * (a - b + c)
					* (-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 1458:
			return a2 * (a + b - c) * (a - b + c) * (-b2 - c2 + a * (b + c));
		case 1459:
			return a2 * (b - c) * T;
		case 1460:
			return a2 * (a + b - c) * (a - b + c) * (a2 + p(b + c, 2));
		case 1461:
			return a2 * (a - b) * (a - c) * p(a + b - c, 2) * p(a - b + c, 2);
		case 1462:
			return a * (a + b - c) * (a - b + c) * (a2 + b * (b - c) - a * c)
					* (a2 - a * b + c * (-b + c));
		case 1463:
			return a * (a + b - c) * (a - b + c) * (-(b * c * (b + c)) + a * R);
		case 1464:
			return a2 * (a + b - c) * (a - b + c) * (b + c)
					* (a2 - b2 + b * c - c2);
		case 1465:
			return a * (a + b - c) * (a - b + c)
					* (-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 1466:
			return a2 * (a + b - c) * (a - b + c)
					* (a3 - a * p(b - c, 2) - a2 * (b + c) + p(b + c, 3));
		case 1467:
			return a
					* (a + b - c)
					* (a - b + c)
					* (a4 - 4 * a2 * b * c - 2 * a3 * (b + c) + 2 * a
							* p(b - c, 2) * (b + c) - Q);
		case 1468:
			return a2 * (a2 + 2 * b * c + a * (b + c));
		case 1469:
			return a2 * (a + b - c) * (a - b + c) * (b2 + b * c + c2);
		case 1470:
			return a2
					* (a + b - c)
					* (a - b + c)
					* (a3 + b3 - a * p(b - c, 2) + b2 * c + b * c2 + c3 - a2
							* (b + c));
		case 1471:
			return a2 * (a + b - c) * (a - b + c)
					* (a2 - 2 * b * c - a * (b + c));
		case 1472:
			return a3 * (a2 + 2 * a * b + b2 + c2) * (a2 + b2 + 2 * a * c + c2);
		case 1473:
			return a2 * (a2 + p(b - c, 2)) * T;
		case 1474:
			return a2 * (a + b) * (a + c) * U * V;
		case 1475:
			return a2 * (-p(b - c, 2) + a * (b + c));
		case 1476:
			return a * (a + b - c) * (a - b + c)
					* (a2 + a * (-2 * b + c) + b * (b + c))
					* (a2 + a * (b - 2 * c) + c * (b + c));
		case 1477:
			return a2 * (a + b - c) * (a - b + c)
					* (a2 + b2 - b * c + 2 * c2 - a * (2 * b + c))
					* (a2 + 2 * b2 - b * c + c2 - a * (b + 2 * c));
		case 1478:
			return -a4 - 2 * a2 * b * c + Q;
		case 1479:
			return -a4 + 2 * a2 * b * c + Q;
		case 1480:
			return a2
					* (a2 - b2 + 4 * b * c - c2)
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* (b2 + 4 * b * c + c2));
		case 1481:
			return a2
					* (a2 - b2 + 4 * b * c - c2)
					* (a3 + a2 * (b - c) - (b - c) * p(b + c, 2) - a
							* (b2 - 4 * b * c + c2))
					* (a3 + a2 * (-b + c) + (b - c) * p(b + c, 2) - a
							* (b2 - 4 * b * c + c2));
		case 1482:
			return a
					* (a3 - 2 * a2 * (b + c) + 2 * p(b - c, 2) * (b + c) - a
							* (b2 - 4 * b * c + c2));
		case 1483:
			return 4 * a4 - 4 * a3 * (b + c) + 4 * a * p(b - c, 2) * (b + c)
					+ a2 * (-3 * b2 + 8 * b * c - 3 * c2) - Q;
		case 1484:
			return p(b - c, 4) * p(b + c, 3) - a5 * (b2 - 4 * b * c + c2) - a
					* Q * (b2 - b * c + c2) - 2 * a2 * p(b - c, 2) * (b3 + c3)
					+ a4 * (b3 - 3 * b2 * c - 3 * b * c2 + c3) + a3
					* (2 * b4 - 5 * b3 * c + 8 * b2 * c2 - 5 * b * c3 + 2 * c4);
		case 1485:
			return a2
					* (a6 - b6 + b4 * c2 - b2 * c4 + c6 - a4 * R + a2
							* (b4 - c4))
					* (a6 + b6 - b4 * c2 + b2 * c4 - c6 - a4 * R + a2
							* (-b4 + c4));
		case 1486:
			return a2 * (a3 - a2 * (b + c) - p(b - c, 2) * (b + c) + a * R);
		case 1487:
			return (a4 + Q - a2 * (2 * b2 + c2))
					* (a4 + Q - a2 * (b2 + 2 * c2))
					* (a4 + 2 * b4 - 3 * b2 * c2 + c4 - a2 * (3 * b2 + 2 * c2))
					* (a4 + b4 - 3 * b2 * c2 + 2 * c4 - a2 * (2 * b2 + 3 * c2));
		case 1488:
			return a
					* (b * (2 * u(a * (-a + b + c)) + u(b * (a - b + c)))
							* u(c * (a + b - c)) + c * u(b * (a - b + c))
							* (2 * u(a * (-a + b + c)) + u(c * (a + b - c))) + a
							* (4 * b * c - u(b * (a - b + c))
									* u(c * (a + b - c))));
		case 1489:
			return a
					* (-2 * b * c + u(b * (a - b + c)) * u(c * (a + b - c)) + u(-(b
							* (a - b - c) * c * (a + b + c))));
		case 1490:
			return a
					* (a6 - 2 * a5 * (b + c) - a4 * p(b + c, 2) + p(b - c, 2)
							* p(b + c, 4) - a2 * Q + 4 * a3 * (b3 + c3) - 2 * a
							* (b5 - b4 * c - b * c4 + c5));
		case 1491:
			return a * (-b3 + c3);
		case 1492:
			return a * (a3 - b3) * (a3 - c3);
		case 1493:
			return a2 * (2 * a4 + Q - 3 * a2 * R)
					* (a4 + b4 - b2 * c2 + c4 - 2 * a2 * R);
		case 1494:
			return -((a4 - 2 * b4 + b2 * c2 + c4 + a2 * (b2 - 2 * c2)) * (a4
					+ b4 + b2 * c2 - 2 * c4 + a2 * (-2 * b2 + c2)));
		case 1495:
			return a2 * (2 * a4 - Q - a2 * R);
		case 1496:
			return a3 * (a4 + b4 + 6 * b2 * c2 + c4 - 2 * a2 * R);
		case 1497:
			return a3 * (a4 + b4 - 6 * b2 * c2 + c4 - 2 * a2 * R);
		case 1498:
			return a2
					* (a8 - 4 * a6 * R - 4 * a2 * Q * R + Q
							* (b4 + 6 * b2 * c2 + c4) + a4
							* (6 * b4 - 4 * b2 * c2 + 6 * c4));
		case 1499:
			return -((b2 - c2) * (-5 * a2 + b2 + c2));
		default:
			return Double.NaN;
		}
	}

	private double weight1500to1599(int k, double a, double b, double c) {

		switch (k) {
		case 1500:
			return a2 * p(b + c, 2);
		case 1501:
			return a6;
		case 1502:
			return b4 * c4;
		case 1503:
			return 2 * a6 - a4 * R - Q * R;
		case 1504:
			return a2 * R + a2 * S;
		case 1505:
			return -(a2 * R) + a2 * S;
		case 1506:
			return Q - 2 * a2 * R;
		case 1507:
			return a
					* (1 - 2 * Math.cos(angleA / 3) + 2 * Math.cos(angleB / 3) + 2 * Math
							.cos(angleC / 3));
		case 1508:
			return a
					* (2 - 1 / Math.cos(angleA / 3) + 1 / Math.cos(angleB / 3) + 1 / Math
							.cos(angleC / 3));
		case 1509:
			return p(a + b, 2) * p(a + c, 2);
		case 1510:
			return a2 * (b2 - c2) * (a4 + b4 - b2 * c2 + c4 - 2 * a2 * R);
		case 1511:
			return a2 * (a2 - b2 - b * c - c2) * (a2 - b2 + b * c - c2)
					* (2 * a4 - Q - a2 * R);
		case 1512:
			return (-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c))
					* (a4 - 2 * a3 * (b + c) + 2 * a * p(b - c, 2) * (b + c) - Q);
		case 1513:
			return -((3 * a4 + Q) * (-b4 - c4 + a2 * R));
		case 1514:
			return -((2 * a4 - Q - a2 * R) * (a6 - 5 * a2 * Q + a4 * R + 3 * Q
					* R));
		case 1515:
			return -(U * V * (5 * a4 - Q - 4 * a2 * R) * (a6 * R + 3 * a2 * Q
					* R + a4 * (-3 * b4 + 4 * b2 * c2 - 3 * c4) - Q
					* (b4 + 4 * b2 * c2 + c4)));
		case 1516:
			return -(T
					* (4 * a8 - a4 * Q + p(b2 - c2, 4) - 5 * a6 * R + a2 * Q
							* R) * (a4 * R + Q * R - 2 * a2
					* (b4 - b2 * c2 + c4)));
		case 1517:
			return -((5 * a6 - a4 * p(b - c, 2) - 4 * a5 * (b + c) - 4 * a3
					* p(b - c, 2) * (b + c) + p(b - c, 4) * p(b + c, 2) + 3
					* a2 * Q) * (2 * a2 * b2 * c2 + a4 * R - 2 * a3 * (b3 + c3)
					- p(b - c, 2)
					* (b4 + 2 * b3 * c + 4 * b2 * c2 + 2 * b * c3 + c4) + 2 * a
					* (b5 - b3 * c2 - b2 * c3 + c5)));
		case 1518:
			return -((5 * a5 - a4 * (b + c) + 4 * a2 * p(b - c, 2) * (b + c)
					+ p(b - c, 2) * p(b + c, 3) - a * Q) * (-b5 + b4 * c - 2
					* b3 * c2 - 2 * b2 * c3 + b * c4 - c5 + a2 * p(b - c, 2)
					* (b + c) + a3 * R - a * (b4 - 4 * b2 * c2 + c4)));
		case 1519:
			return -((-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c)) * (a4
					- 2 * a2 * p(b - c, 2) + Q));
		case 1520:
			return -((4 * a5 + a4 * (b + c) + 2 * a2 * p(b - c, 2) * (b + c) + p(
					b - c, 2) * p(b + c, 3)) * (-b5 - b3 * c2 - b2 * c3 - c5
					- a * Q + a3 * R + a2 * (b3 + c3)));
		case 1521:
			return -((2 * a3 - a2 * (b + c) - p(b - c, 2) * (b + c))
					* (2 * a4 - a2 * p(b - c, 2) - a3 * (b + c) + a
							* p(b - c, 2) * (b + c) - Q) * (a5 * R - (b + c)
					* p(b3 - b2 * c + b * c2 - c3, 2) - a4
					* (b3 + b2 * c + b * c2 + c3) + a * p(b - c, 2) * (b4 + c4)
					- 2 * a3 * (b4 - b3 * c - b2 * c2 - b * c3 + c4) + 2 * a2
					* (b5 - b3 * c2 - b2 * c3 + c5)));
		case 1522:
			return -((a6 * b2 - 3 * a4 * b4 + 3 * a2 * b6 - b8 + a6 * c2 + 2
					* a4 * b2 * c2 - 2 * a2 * b4 * c2 - b6 * c2 - 3 * a4 * c4
					- 2 * a2 * b2 * c4 + 4 * b4 * c4 + 3 * a2 * c6 - b2 * c6 - c8) * (9
					* a8
					- 9
					* a6
					* b2
					- 7
					* a4
					* b4
					+ 5
					* a2
					* b6
					+ 2
					* b8
					- 9
					* a6
					* c2
					+ 14
					* a4
					* b2
					* c2
					- 5
					* a2
					* b4
					* c2
					- 8
					* b6
					* c2
					- 7
					* a4
					* c4
					- 5
					* a2
					* b2
					* c4
					+ 12
					* b4
					* c4
					+ 5
					* a2 * c6 - 8 * b2 * c6 + 2 * c8))
					- u(3)
					* a2
					* (a - b - c)
					* (a + b - c)
					* (a - b + c)
					* (a + b + c)
					* (a6 * b2 - 3 * a4 * b4 + 3 * a2 * b6 - b8 + a6 * c2 + 2
							* a4 * b2 * c2 - 2 * a2 * b4 * c2 - b6 * c2 - 3
							* a4 * c4 - 2 * a2 * b2 * c4 + 4 * b4 * c4 + 3 * a2
							* c6 - b2 * c6 - c8) * S;
		case 1523:
			return -((a6 * b2 - 3 * a4 * b4 + 3 * a2 * b6 - b8 + a6 * c2 + 2
					* a4 * b2 * c2 - 2 * a2 * b4 * c2 - b6 * c2 - 3 * a4 * c4
					- 2 * a2 * b2 * c4 + 4 * b4 * c4 + 3 * a2 * c6 - b2 * c6 - c8) * (9
					* a8
					- 9
					* a6
					* b2
					- 7
					* a4
					* b4
					+ 5
					* a2
					* b6
					+ 2
					* b8
					- 9
					* a6
					* c2
					+ 14
					* a4
					* b2
					* c2
					- 5
					* a2
					* b4
					* c2
					- 8
					* b6
					* c2
					- 7
					* a4
					* c4
					- 5
					* a2
					* b2
					* c4
					+ 12
					* b4
					* c4
					+ 5
					* a2 * c6 - 8 * b2 * c6 + 2 * c8))
					+ u(3)
					* a2
					* (a - b - c)
					* (a + b - c)
					* (a - b + c)
					* (a + b + c)
					* (a6 * b2 - 3 * a4 * b4 + 3 * a2 * b6 - b8 + a6 * c2 + 2
							* a4 * b2 * c2 - 2 * a2 * b4 * c2 - b6 * c2 - 3
							* a4 * c4 - 2 * a2 * b2 * c4 + 4 * b4 * c4 + 3 * a2
							* c6 - b2 * c6 - c8) * S;
		case 1524:
			return -(u(3)
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4) * (a6
					+ a4
					* b2
					- 5
					* a2
					* b4
					+ 3
					* b6
					+ a4
					* c2
					+ 10
					* a2
					* b2
					* c2 - 3 * b4 * c2 - 5 * a2 * c4 - 3 * b2 * c4 + 3 * c6))
					+ (a - b - c)
					* (a + b - c)
					* (a - b + c)
					* (a + b + c)
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4) * S;
		case 1525:
			return -(u(3)
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4) * (a6
					+ a4
					* b2
					- 5
					* a2
					* b4
					+ 3
					* b6
					+ a4
					* c2
					+ 10
					* a2
					* b2
					* c2 - 3 * b4 * c2 - 5 * a2 * c4 - 3 * b2 * c4 + 3 * c6))
					- (a - b - c)
					* (a + b - c)
					* (a - b + c)
					* (a + b + c)
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4) * S;
		case 1526:
			return -((a2 - b2 - b * c - c2) * (a2 - b2 + b * c - c2)
					* (a2 * b2 - b4 + a2 * c2 + 2 * b2 * c2 - c4) * (7 * a8
					- 11 * a6 * b2 + 3 * a4 * b4 - a2 * b6 + 2 * b8 - 11 * a6
					* c2 - 6 * a4 * b2 * c2 + a2 * b4 * c2 - 8 * b6 * c2 + 3
					* a4 * c4 + a2 * b2 * c4 + 12 * b4 * c4 - a2 * c6 - 8 * b2
					* c6 + 2 * c8))
					- u(3)
					* a2
					* (a - b - c)
					* (a + b - c)
					* (a - b + c)
					* (a + b + c)
					* (a2 - b2 - b * c - c2)
					* (a2 - b2 + b * c - c2)
					* (a2 * b2 - b4 + a2 * c2 + 2 * b2 * c2 - c4) * S;
		case 1527:
			return -((a2 - b2 - b * c - c2) * (a2 - b2 + b * c - c2)
					* (a2 * b2 - b4 + a2 * c2 + 2 * b2 * c2 - c4) * (7 * a8
					- 11 * a6 * b2 + 3 * a4 * b4 - a2 * b6 + 2 * b8 - 11 * a6
					* c2 - 6 * a4 * b2 * c2 + a2 * b4 * c2 - 8 * b6 * c2 + 3
					* a4 * c4 + a2 * b2 * c4 + 12 * b4 * c4 - a2 * c6 - 8 * b2
					* c6 + 2 * c8))
					+ u(3)
					* a2
					* (a - b - c)
					* (a + b - c)
					* (a - b + c)
					* (a + b + c)
					* (a2 - b2 - b * c - c2)
					* (a2 - b2 + b * c - c2)
					* (a2 * b2 - b4 + a2 * c2 + 2 * b2 * c2 - c4) * S;
		case 1528:
			return -(U
					* V
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2)) * (a5 * (b + c) - 2 * a3
					* p(b - c, 2) * (b + c) + a * p(b - c, 4) * (b + c) + 2
					* a2 * Q - a4 * R - Q * R));
		case 1529:
			return -(U * V * (a4 - 3 * b4 - 2 * b2 * c2 - 3 * c4 + 2 * a2 * R) * (2
					* a6 - a4 * R - Q * R));
		case 1530:
			return -((2 * a3 - a2 * (b + c) - p(b - c, 2) * (b + c)) * (a5 - a
					* Q - 2 * a2 * (b3 + c3) + 2 * (b5 - b3 * c2 - b2 * c3 + c5)));
		case 1531:
			return -(T * (2 * a4 - Q - a2 * R) * (a4 - 2 * Q + a2 * R));
		case 1532:
			return -((-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c)) * (-(a2 * p(
					b - c, 2)) + a3 * (b + c) - a * p(b - c, 2) * (b + c) + Q));
		case 1533:
			return -((2 * a4 - Q - a2 * R) * (a4 * R + Q * R - 2 * a2
					* (b4 - 4 * b2 * c2 + c4)));
		case 1534:
			return -((a7 + 3 * a6 * (b + c) - 5 * a4 * p(b - c, 2) * (b + c)
					+ p(b - c, 4) * p(b + c, 3) + 3 * a * p(b - c, 2)
					* p(b + c, 4) - 5 * a3 * Q + a5 * (b2 - 6 * b * c + c2) + a2
					* p(b - c, 2) * (b3 - 5 * b2 * c - 5 * b * c2 + c3)) * (a5
					* (b + c) - p(b - c, 2) * p(b + c, 4) - a4
					* (b2 - 4 * b * c + c2) + 2 * a2 * p(b + c, 2)
					* (b2 - 3 * b * c + c2) + a * p(b - c, 2)
					* (b3 - 3 * b2 * c - 3 * b * c2 + c3) - 2 * a3
					* (b3 - 2 * b2 * c - 2 * b * c2 + c3)));
		case 1535:
			return -((2 * a4 - a2 * p(b - c, 2) - a3 * (b + c) + a
					* p(b - c, 2) * (b + c) - Q) * (a6 - a5 * (b + c) + 2 * Q
					* (b2 - b * c + c2) - a2 * p(b - c, 2)
					* (3 * b2 + 4 * b * c + 3 * c2) + 2 * a3 * (b3 + c3) - a
					* (b5 - b4 * c - b * c4 + c5)));
		case 1536:
			return (2 * a3 - a2 * (b + c) - p(b - c, 2) * (b + c))
					* (-b5 + b4 * c + b * c4 - c5 + a4 * (b + c) - 2 * a2 * b
							* c * (b + c) + 2 * a * Q - 2 * a3 * R);
		case 1537:
			return -((-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c)) * (2
					* a4
					- 3
					* a2
					* p(b - c, 2)
					- a3
					* (b + c)
					+ a
					* p(b - c, 2) * (b + c) + Q));
		case 1538:
			return -((-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c)) * (a3
					- 3 * a * p(b - c, 2) + 2 * p(b - c, 2) * (b + c)));
		case 1539:
			return -((2 * a4 - Q - a2 * R) * (a6 + 2 * Q * R + a2
					* (-3 * b4 + 5 * b2 * c2 - 3 * c4)));
		case 1540:
			return -((2 * a10 - 4 * a8 * R + a4 * Q * R - p(b2 - c2, 4) * R
					+ a2 * Q * (b4 + c4) + a6 * (b4 + 4 * b2 * c2 + c4)) * (a10
					* a2 - 3 * a10 * R + p(b2 - c2, 4)
					* (2 * b4 + b2 * c2 + 2 * c4) + a8
					* (4 * b4 + 9 * b2 * c2 + 4 * c4) + a4 * Q
					* (9 * b4 + 8 * b2 * c2 + 9 * c4) - 6 * a6
					* (b6 + b4 * c2 + b2 * c4 + c6) - a2 * Q
					* (7 * b6 - 3 * b4 * c2 - 3 * b2 * c4 + 7 * c6)));
		case 1541:
			return -((2 * a3 - a2 * (b + c) - p(b - c, 2) * (b + c)) * (a5 - a4
					* (b + c) - 2 * a2 * p(b - c, 2) * (b + c) - 3 * a * Q + 2
					* a3 * R + p(b - c, 2)
					* (3 * b3 + 5 * b2 * c + 5 * b * c2 + 3 * c3)));
		case 1542:
			return -((2 * a4 - a2 * p(b - c, 2) - a3 * (b + c) + a
					* p(b - c, 2) * (b + c) - Q) * (a6 - 4 * a3 * b * c
					* (b + c) + 4 * a * b * p(b - c, 2) * c * (b + c) + a4
					* p(b + c, 2) - 5 * a2 * Q + Q
					* (3 * b2 - 2 * b * c + 3 * c2)));
		case 1543:
			return -((a4 * (b + c) - p(b - c, 2) * p(b + c, 3) - 2 * a3
					* (b2 - b * c + c2) + 2 * a * p(b - c, 2)
					* (b2 + b * c + c2)) * (a6 + 2 * a5 * (b + c) - 4 * a3
					* p(b - c, 2) * (b + c) + p(b - c, 4) * p(b + c, 2) + 2 * a
					* p(b - c, 2) * p(b + c, 3) - a2 * Q - a4
					* (b2 + 6 * b * c + c2)));
		case 1544:
			return -((2 * a4 - Q - a2 * R) * (a6 + a4 * b * c + a5 * (b + c)
					+ a * p(b - c, 2) * p(b + c, 3) + Q
					* (2 * b2 + b * c + 2 * c2) - a2 * p(b + c, 2)
					* (3 * b2 - 4 * b * c + 3 * c2) - 2 * a3
					* (b3 + b2 * c + b * c2 + c3)));
		case 1545:
			return -((2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4) * (a6
					+ a4 * b2 - 5 * a2 * b4 + 3 * b6 + a4 * c2 + 10 * a2 * b2
					* c2 - 3 * b4 * c2 - 5 * a2 * c4 - 3 * b2 * c4 + 3 * c6))
					+ u(3)
					* (a - b - c)
					* (a + b - c)
					* (a - b + c)
					* (a + b + c)
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4) * S;
		case 1546:
			return -((2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4) * (a6
					+ a4 * b2 - 5 * a2 * b4 + 3 * b6 + a4 * c2 + 10 * a2 * b2
					* c2 - 3 * b4 * c2 - 5 * a2 * c4 - 3 * b2 * c4 + 3 * c6))
					- u(3)
					* (a - b - c)
					* (a + b - c)
					* (a - b + c)
					* (a + b + c)
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4) * S;
		case 1547:
			return -((2 * a3 - a2 * (b + c) - p(b - c, 2) * (b + c)) * (a4
					* p(b - c, 2) + a5 * (b + c) - 2 * a3 * p(b - c, 2)
					* (b + c) + a * p(b - c, 4) * (b + c) + p(b - c, 2)
					* p(b + c, 4) - 2 * a2 * Q));
		case 1548:
			return -((-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c)) * (4
					* a5
					* b
					* c
					+ 3
					* a6
					* (b + c)
					+ a2
					* p(b - c, 2)
					* p(b + c, 3)
					+ 4
					* a
					* b
					* c
					* Q
					+ p(b - c, 2)
					* p(b + c, 3) * R + a4
					* (-5 * b3 + 3 * b2 * c + 3 * b * c2 - 5 * c3)));
		case 1549:
			return (2 * a4 - a2 * p(b - c, 2) - a3 * (b + c) + a * p(b - c, 2)
					* (b + c) - Q)
					* (a8 * (b + c) - 2 * a6 * p(b - c, 2) * (b + c) - 4 * a4
							* b * p(b - c, 2) * c * (b + c) + 2 * a2
							* p(b - c, 4) * p(b + c, 3) - p(b - c, 6)
							* p(b + c, 3) - 2 * a7 * R - 6 * a3 * Q * R + 2 * a
							* p(b4 - c4, 2) + a5
							* (6 * b4 - 4 * b2 * c2 + 6 * c4));
		case 1550:
			return -((2 * a6 - 2 * a4 * R - Q * R + a2 * (b4 + c4)) * (a8 - a6
					* R - 3 * a2 * Q * R + a4 * (b4 - b2 * c2 + c4) + Q
					* (2 * b4 - b2 * c2 + 2 * c4)));
		case 1551:
			return -((2 * a6 - 2 * a4 * R - Q * R + a2 * (b4 + c4)) * (a8 + 3
					* a6 * R - 5 * a4 * (b4 + b2 * c2 + c4) + Q
					* (4 * b4 - b2 * c2 + 4 * c4) + a2
					* (-3 * b6 + 7 * b4 * c2 + 7 * b2 * c4 - 3 * c6)));
		case 1552:
			return -(U * V * (a4 - 2 * b4 + b2 * c2 + c4 + a2 * (b2 - 2 * c2))
					* (a4 + b4 + b2 * c2 - 2 * c4 + a2 * (-2 * b2 + c2)) * (2
					* a10 - 2 * a8 * R + 7 * a4 * Q * R - p(b2 - c2, 4) * R
					+ a6 * (-5 * b4 + 12 * b2 * c2 - 5 * c4) - a2 * Q
					* (b4 + 8 * b2 * c2 + c4)));
		case 1553:
			return -(p(-2 * a4 + Q + a2 * R, 2) * (a6 * R + a4
					* (-3 * b4 + 2 * b2 * c2 - 3 * c4) - Q
					* (b4 + 3 * b2 * c2 + c4) + a2
					* (3 * b6 - 2 * b4 * c2 - 2 * b2 * c4 + 3 * c6)));
		case 1554:
			return -((2 * a4 - Q - a2 * R) * (2 * a6 - a4 * R - Q * R) * (-b10
					+ b6 * c4 + b4 * c6 - c10 + a8 * R + a4 * b2 * c2 * R - 2
					* a6 * (b4 + c4) + 2 * a2 * (b8 - b6 * c2 - b2 * c6 + c8)));
		case 1555:
			return -((2 * a4 - Q - a2 * R) * (a8 - a6 * R - 3 * a2 * Q * R + a4
					* (b4 + 8 * b2 * c2 + c4) + 2 * (b8 - b6 * c2 - b2 * c6 + c8)));
		case 1556:
			return -((2 * a6 - Q * R - a2 * (b4 + c4)) * (a8 + a6 * R - a2
					* p(b2 + c2, 3) + Q * (2 * b4 + 3 * b2 * c2 + 2 * c4) - a4
					* (3 * b4 + b2 * c2 + 3 * c4)));
		case 1557:
			return -((-2 * b2 * c2 * Q + 3 * a6 * R - a2 * Q * R - 2 * a4
					* (b4 + b2 * c2 + c4)) * (b2 * c2 * Q
					* (b4 + 6 * b2 * c2 + c4) + a8
					* (6 * b4 + 9 * b2 * c2 + 6 * c4) + 2 * a2 * Q
					* (b6 + 5 * b4 * c2 + 5 * b2 * c4 + c6) - 2 * a6
					* (5 * b6 + b4 * c2 + b2 * c4 + 5 * c6) + 2 * a4
					* (b8 - 7 * b6 * c2 - 7 * b2 * c6 + c8)));
		case 1558:
			return -((2 * a4 - Q - a2 * R) * (a6 + a4 * b * c - a5 * (b + c)
					- 3 * a2 * Q + Q * (2 * b2 - b * c + 2 * c2) + 2 * a3
					* (b3 + c3) - a * (b5 - b4 * c - b * c4 + c5)));
		case 1559:
			return -(U * V * (3 * a4 - Q - 2 * a2 * R) * (a6 * R + 3 * a2 * Q
					* R + a4 * (-3 * b4 + 4 * b2 * c2 - 3 * c4) - Q
					* (b4 + 4 * b2 * c2 + c4)));
		case 1560:
			return -((2 * a2 - b2 - c2) * U * V * (-2 * a2 * b2 * c2 + a4 * R - Q
					* R));
		case 1561:
			return -((2 * a4 - Q - a2 * R) * (3 * a6 * R + a2 * Q * R + a4
					* (-5 * b4 + 4 * b2 * c2 - 5 * c4) + Q
					* (b4 + 4 * b2 * c2 + c4)));
		case 1562:
			return p(b - c, 2) * p(b + c, 2) * (-T)
					* (-3 * a4 + Q + 2 * a2 * R);
		case 1563:
			return a10
					* a2
					* b2
					- 4
					* a10
					* b4
					+ 5
					* a8
					* b6
					- 5
					* a4
					* b10
					+ 4
					* a2
					* b10
					* b2
					- b4
					* b10
					+ a10
					* a2
					* c2
					+ 14
					* a10
					* b2
					* c2
					- 44
					* a8
					* b4
					* c2
					+ 27
					* a6
					* b6
					* c2
					+ 18
					* a4
					* b8
					* c2
					- 21
					* a2
					* b10
					* c2
					+ 5
					* b10
					* b2
					* c2
					- 4
					* a10
					* c4
					- 44
					* a8
					* b2
					* c4
					+ 16
					* a6
					* b4
					* c4
					- 13
					* a4
					* b6
					* c4
					+ 50
					* a2
					* b8
					* c4
					- 9
					* b10
					* c4
					+ 5
					* a8
					* c6
					+ 27
					* a6
					* b2
					* c6
					- 13
					* a4
					* b4
					* c6
					- 66
					* a2
					* b6
					* c6
					+ 5
					* b8
					* c6
					+ 18
					* a4
					* b2
					* c8
					+ 50
					* a2
					* b4
					* c8
					+ 5
					* b6
					* c8
					- 5
					* a4
					* c10
					- 21
					* a2
					* b2
					* c10
					- 9
					* b4
					* c10
					+ 4
					* a2
					* c10
					* c2
					+ 5
					* b2
					* c10
					* c2
					- c10
					* c4
					+ (-2 * a10 * a2 + 9 * a10 * b2 - 16 * a8 * b4 + 13 * a6
							* b6 - 3 * a4 * b8 - 2 * a2 * b10 + b10 * b2 + 9
							* a10 * c2 - 26 * a8 * b2 * c2 + 4 * a6 * b4 * c2
							+ 9 * a4 * b6 * c2 + 9 * a2 * b8 * c2 - 5 * b10
							* c2 - 16 * a8 * c4 + 4 * a6 * b2 * c4 - 4 * a4
							* b4 * c4 - 7 * a2 * b6 * c4 + 11 * b8 * c4 + 13
							* a6 * c6 + 9 * a4 * b2 * c6 - 7 * a2 * b4 * c6
							- 14 * b6 * c6 - 3 * a4 * c8 + 9 * a2 * b2 * c8
							+ 11 * b4 * c8 - 2 * a2 * c10 - 5 * b2 * c10 + c10
							* c2) * S;
		case 1564:
			return -(a10 * a2 * b2)
					+ 4
					* a10
					* b4
					- 5
					* a8
					* b6
					+ 5
					* a4
					* b10
					- 4
					* a2
					* b10
					* b2
					+ b4
					* b10
					- a10
					* a2
					* c2
					- 14
					* a10
					* b2
					* c2
					+ 44
					* a8
					* b4
					* c2
					- 27
					* a6
					* b6
					* c2
					- 18
					* a4
					* b8
					* c2
					+ 21
					* a2
					* b10
					* c2
					- 5
					* b10
					* b2
					* c2
					+ 4
					* a10
					* c4
					+ 44
					* a8
					* b2
					* c4
					- 16
					* a6
					* b4
					* c4
					+ 13
					* a4
					* b6
					* c4
					- 50
					* a2
					* b8
					* c4
					+ 9
					* b10
					* c4
					- 5
					* a8
					* c6
					- 27
					* a6
					* b2
					* c6
					+ 13
					* a4
					* b4
					* c6
					+ 66
					* a2
					* b6
					* c6
					- 5
					* b8
					* c6
					- 18
					* a4
					* b2
					* c8
					- 50
					* a2
					* b4
					* c8
					- 5
					* b6
					* c8
					+ 5
					* a4
					* c10
					+ 21
					* a2
					* b2
					* c10
					+ 9
					* b4
					* c10
					- 4
					* a2
					* c10
					* c2
					- 5
					* b2
					* c10
					* c2
					+ c10
					* c4
					+ (-2 * a10 * a2 + 9 * a10 * b2 - 16 * a8 * b4 + 13 * a6
							* b6 - 3 * a4 * b8 - 2 * a2 * b10 + b10 * b2 + 9
							* a10 * c2 - 26 * a8 * b2 * c2 + 4 * a6 * b4 * c2
							+ 9 * a4 * b6 * c2 + 9 * a2 * b8 * c2 - 5 * b10
							* c2 - 16 * a8 * c4 + 4 * a6 * b2 * c4 - 4 * a4
							* b4 * c4 - 7 * a2 * b6 * c4 + 11 * b8 * c4 + 13
							* a6 * c6 + 9 * a4 * b2 * c6 - 7 * a2 * b4 * c6
							- 14 * b6 * c6 - 3 * a4 * c8 + 9 * a2 * b2 * c8
							+ 11 * b4 * c8 - 2 * a2 * c10 - 5 * b2 * c10 + c10
							* c2) * S;
		case 1565:
			return p(b - c, 2) * (-T);
		case 1566:
			return -((a - b - c) * p(b - c, 2) * (-b2 - c2 + a * (b + c)) * (2
					* a3 - a2 * (b + c) - p(b - c, 2) * (b + c)));
		case 1567:
			return -((-(b2 * c2 * Q) + a6 * R + a2 * b2 * c2 * R - a4
					* p(b2 + c2, 2)) * (b4 * c4 * Q + a8
					* (2 * b4 + b2 * c2 + 2 * c4) - 3 * a6 * (b6 + c6) + a4
					* (-5 * b6 * c2 + 7 * b4 * c4 - 5 * b2 * c6) + a2
					* (b10 + b6 * c4 + b4 * c6 + c10)));
		case 1568:
			return -(T * (2 * a4 - Q - a2 * R) * (-Q + a2 * R));
		case 1569:
			return -(b2 * c2 * Q) + 2 * a6 * R - 2 * a4 * p(b2 + c2, 2) + a2
					* (b6 + b4 * c2 + b2 * c4 + c6);
		case 1570:
			return a2 * (2 * a4 + 3 * b4 - 2 * b2 * c2 + 3 * c4 - 3 * a2 * R);
		case 1571:
			return a
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* (3 * b2 + 2 * b * c + 3 * c2));
		case 1572:
			return a
					* (a3 + a * p(b - c, 2) + a2 * (b + c) - p(b - c, 2)
							* (b + c));
		case 1573:
			return a * (2 * b * c * (b + c) + a * R);
		case 1574:
			return a * (-2 * b * c * (b + c) + a * R);
		case 1575:
			return a * (-(b * c * (b + c)) + a * R);
		case 1576:
			return a4 * (a - b) * (a + b) * (a - c) * (a + c);
		case 1577:
			return b * (b - c) * c * (b + c);
		case 1578:
			return 8 * a4 * b2 * c2 * T + a2 * T * U * V * S;
		case 1579:
			return -8 * a4 * b2 * c2 * T + a2 * T * U * V * S;
		case 1580:
			return a5 - a * b2 * c2;
		case 1581:
			return -(a5 * b2 * c2) - a * b4 * c4 + a3 * (b6 + c6);
		case 1582:
			return a5 + a * b2 * c2;
		case 1583:
			return -4 * a2 * b2 * c2 + a2 * T * S;
		case 1584:
			return 4 * a2 * b2 * c2 + a2 * T * S;
		case 1585:
			return T * U * V - U * V * S;
		case 1586:
			return -(T * U * V) - U * V * S;
		case 1587:
			return U * V + 2 * a2 * S;
		case 1588:
			return -(U * V) + 2 * a2 * S;
		case 1589:
			return T * U * V + 2 * a2 * T * S;
		case 1590:
			return -(T * U * V) + 2 * a2 * T * S;
		case 1591:
			return -4 * a2 * b2 * c2
					+ (-(a2 * b2) + b4 - a2 * c2 - 2 * b2 * c2 + c4) * S;
		case 1592:
			return 4 * a2 * b2 * c2
					+ (-(a2 * b2) + b4 - a2 * c2 - 2 * b2 * c2 + c4) * S;
		case 1593:
			return a2 * U * V * (a4 + b4 + 6 * b2 * c2 + c4 - 2 * a2 * R);
		case 1594:
			return -(U * V * (a4 * R + Q * R - 2 * a2 * (b4 + b2 * c2 + c4)));
		case 1595:
			return -(U * V * (a4 * R + Q * R - 2 * a2 * (b4 + 4 * b2 * c2 + c4)));
		case 1596:
			return -(U * V * (a4 * R + Q * R - 2 * a2 * (b4 - 4 * b2 * c2 + c4)));
		case 1597:
			return a2 * U * V * (a4 + b4 + 10 * b2 * c2 + c4 - 2 * a2 * R);
		case 1598:
			return a2 * U * V * (a4 + b4 - 6 * b2 * c2 + c4 - 2 * a2 * R);
		case 1599:
			return -2 * a2 * b2 * c2 + a2 * T * S;
		default:
			return Double.NaN;
		}
	}

	private double weight1600to1699(int k, double a, double b, double c) {

		switch (k) {
		case 1600:
			return 2 * a2 * b2 * c2 + a2 * T * S;
		case 1601:
			return a2
					* (a10
							* a10
							- 6
							* a10
							* a8
							* R
							+ 3
							* a10
							* a6
							* (5 * b4 + 8 * b2 * c2 + 5 * c4)
							- 2
							* a10
							* a4
							* (10 * b6 + 17 * b4 * c2 + 17 * b2 * c4 + 10 * c6)
							- p(b2 - c2, 6)
							* (b8 + c8)
							+ a10
							* a2
							* (14 * b8 + 14 * b6 * c2 + 17 * b4 * c4 + 14 * b2
									* c6 + 14 * c8)
							+ 2
							* a10
							* (7 * b8 * c2 + 5 * b6 * c4 + 5 * b4 * c6 + 7 * b2
									* c8)
							+ 2
							* a2
							* p(b2 - c2, 4)
							* (3 * b10 - b6 * c4 - b4 * c6 + 3 * c10)
							+ 2
							* a6
							* Q
							* (10 * b10 + 13 * b8 * c2 + 15 * b6 * c4 + 15 * b4
									* c6 + 13 * b2 * c8 + 10 * c10)
							- 2
							* a8
							* (7 * b10 * b2 + 7 * b10 * c2 + 3 * b8 * c4 + 2
									* b6 * c6 + 3 * b4 * c8 + 7 * b2 * c10 + 7
									* c10 * c2) - a4
							* Q
							* (15 * b10 * b2 - 4 * b10 * c2 - 4 * b8 * c4 - 6
									* b6 * c6 - 4 * b4 * c8 - 4 * b2 * c10 + 15
									* c10 * c2));
		case 1602:
			return a2
					* (a6 - 2 * a5 * (b + c) + a4 * p(b + c, 2) - a2
							* (b4 + c4) - p(b - c, 2) * (b4 + c4) + 2 * a
							* (b5 - b4 * c - b * c4 + c5));
		case 1603:
			return a2
					* (a8 - 2 * a6 * p(b - c, 2) - 2 * a4 * b * c
							* (2 * b2 - 3 * b * c + 2 * c2) + 4 * a3 * b * c
							* (b3 + c3) - Q * (b4 + c4) - 4 * a * b * c
							* (b5 - b3 * c2 - b2 * c3 + c5) + 2 * a2
							* (b6 - 3 * b4 * c2 - 3 * b2 * c4 + c6));
		case 1604:
			return a2
					* (a6 + 4 * a3 * b * c * (b + c) - 4 * a * b * p(b - c, 2)
							* c * (b + c) + a4 * (-3 * b2 + 4 * b * c - 3 * c2)
							- Q * R + a2
							* (3 * b4 - 4 * b3 * c - 6 * b2 * c2 - 4 * b * c3 + 3 * c4));
		case 1605:
			return -(a2 * (a8 - 2 * a6 * b2 + 2 * a2 * b6 - b8 - 2 * a6 * c2
					+ 4 * a4 * b2 * c2 - 4 * a2 * b4 * c2 + 2 * b6 * c2 - 4
					* a2 * b2 * c4 - 2 * b4 * c4 + 2 * a2 * c6 + 2 * b2 * c6 - c8))
					+ u(3)
					* a2
					* (a6 - a4 * b2 + a2 * b4 - b6 - a4 * c2 + b4 * c2 + a2
							* c4 + b2 * c4 - c6) * S;
		case 1606:
			return a2
					* (a8 - 2 * a6 * b2 + 2 * a2 * b6 - b8 - 2 * a6 * c2 + 4
							* a4 * b2 * c2 - 4 * a2 * b4 * c2 + 2 * b6 * c2 - 4
							* a2 * b2 * c4 - 2 * b4 * c4 + 2 * a2 * c6 + 2 * b2
							* c6 - c8)
					+ u(3)
					* a2
					* (a6 - a4 * b2 + a2 * b4 - b6 - a4 * c2 + b4 * c2 + a2
							* c4 + b2 * c4 - c6) * S;
		case 1607:
			return a2
					* (a8 - 2 * a6 * b2 + 2 * a2 * b6 - b8 - 2 * a6 * c2 - 4
							* a4 * b2 * c2 + 4 * a2 * b4 * c2 + 2 * b6 * c2 + 4
							* a2 * b2 * c4 - 2 * b4 * c4 + 2 * a2 * c6 + 2 * b2
							* c6 - c8)
					+ u(3)
					* a2
					* (a6 - a4 * b2 + a2 * b4 - b6 - a4 * c2 + b4 * c2 + a2
							* c4 + b2 * c4 - c6) * S;
		case 1608:
			return -(a2 * (a8 - 2 * a6 * b2 + 2 * a2 * b6 - b8 - 2 * a6 * c2
					- 4 * a4 * b2 * c2 + 4 * a2 * b4 * c2 + 2 * b6 * c2 + 4
					* a2 * b2 * c4 - 2 * b4 * c4 + 2 * a2 * c6 + 2 * b2 * c6 - c8))
					+ u(3)
					* a2
					* (a6 - a4 * b2 + a2 * b4 - b6 - a4 * c2 + b4 * c2 + a2
							* c4 + b2 * c4 - c6) * S;
		case 1609:
			return a2
					* (a6 - 3 * a4 * R - Q * R + a2
							* (3 * b4 - 2 * b2 * c2 + 3 * c4));
		case 1610:
			return a
					* (a6 + 3 * a4 * b * c + a5 * (b + c) - 2 * a3 * b * c
							* (b + c) - b * c * Q - a2
							* (b4 + 2 * b3 * c - 2 * b2 * c2 + 2 * b * c3 + c4) - a
							* (b5 - b4 * c - b * c4 + c5));
		case 1611:
			return a2 * (a4 + b4 - 6 * b2 * c2 + c4 + 2 * a2 * R);
		case 1612:
			return a
					* (a6 - a5 * (b + c) + 2 * a3 * p(b - c, 2) * (b + c) - a
							* p(b - c, 2) * p(b + c, 3) - b * c * Q + a2
							* p(b + c, 2) * R - a4
							* (2 * b2 + 5 * b * c + 2 * c2));
		case 1613:
			return -(a2 * b2 * c2) + a4 * R;
		case 1614:
			return a2
					* (a8 + b2 * c2 * Q - 3 * a6 * R - a2 * Q * R + a4
							* (3 * b4 + b2 * c2 + 3 * c4));
		case 1615:
			return a2
					* (a4 - 4 * a3 * (b + c) - 4 * a * p(b - c, 2) * (b + c)
							+ p(b - c, 2) * (b2 + 6 * b * c + c2) + a2
							* (6 * b2 - 4 * b * c + 6 * c2));
		case 1616:
			return a2 * (a2 + b2 - 6 * b * c + c2 + 2 * a * (b + c));
		case 1617:
			return a2 * (a + b - c) * (a - b + c)
					* (a2 + b2 + c2 - 2 * a * (b + c));
		case 1618:
			return a2
					* (a - b)
					* (a - c)
					* (a4 + b * p(b - c, 2) * c - a3 * (b + c) + a
							* p(b - c, 2) * (b + c) - a2
							* (b2 - 3 * b * c + c2));
		case 1619:
			return a2
					* (a10 + a2 * p(b2 - c2, 4) - a8 * R - Q * p(b2 + c2, 3)
							- 2 * a6 * (b4 - 6 * b2 * c2 + c4) + 2 * a4
							* (b6 - 3 * b4 * c2 - 3 * b2 * c4 + c6));
		case 1620:
			return a2
					* (7 * a8 - 12 * a6 * R + 20 * a2 * Q * R + a4
							* (-6 * b4 + 28 * b2 * c2 - 6 * c4) - Q
							* (9 * b4 + 14 * b2 * c2 + 9 * c4));
		case 1621:
			return a * (a2 - b * c - a * (b + c));
		case 1622:
			return a2
					* (a8 + 2 * a7 * (b + c) - 2 * a * p(b - c, 4)
							* p(b + c, 3) - p(b - c, 2) * p(b + c, 4) * R - 2
							* a6 * (b2 + b * c + c2) + 2 * a4 * b * c
							* (b2 + 6 * b * c + c2) - 2 * a5
							* (3 * b3 + b2 * c + b * c2 + 3 * c3) + 2 * a3
							* p(b - c, 2)
							* (3 * b3 + 5 * b2 * c + 5 * b * c2 + 3 * c3) + 2
							* a2 * p(b - c, 2)
							* (b4 + 3 * b3 * c + 3 * b * c3 + c4));
		case 1623:
			return a2
					* (2 * a4 - 2 * b4 + a2 * b * c + 2 * b3 * c - b2 * c2 + 2
							* b * c3 - 2 * c4 - 2 * a3 * (b + c) + a
							* (2 * b3 - b2 * c - b * c2 + 2 * c3));
		case 1624:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c)
					* (-2 * a2 * Q + a4 * R + Q * R);
		case 1625:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c) * (-Q + a2 * R);
		case 1626:
			return a2
					* (a4 - a3 * (b + c) - p(b - c, 2) * (b2 + b * c + c2) + a
							* (b3 + c3));
		case 1627:
			return a6 - a2 * b2 * c2 + a4 * R;
		case 1628:
			return a2
					* U
					* V
					* (a10
							* a6
							- 8
							* a10
							* a4
							* R
							+ p(b2 - c2, 6)
							* (b4 + c4)
							+ a10
							* a2
							* (28 * b4 + 38 * b2 * c2 + 28 * c4)
							- 8
							* a2
							* p(b2 - c2, 4)
							* (b6 + b4 * c2 + b2 * c4 + c6)
							- 8
							* a10
							* (7 * b6 + 9 * b4 * c2 + 9 * b2 * c4 + 7 * c6)
							+ a8
							* (70 * b8 + 62 * b6 * c2 + 72 * b4 * c4 + 62 * b2
									* c6 + 70 * c8)
							- 8
							* a6
							* (7 * b10 + b8 * c2 + 4 * b6 * c4 + 4 * b4 * c6
									+ b2 * c8 + 7 * c10) + 2
							* a4
							* (14 * b10 * b2 - 15 * b10 * c2 + 6 * b8 * c4 + 6
									* b6 * c6 + 6 * b4 * c8 - 15 * b2 * c10 + 14
									* c10 * c2));
		case 1629:
			return p(a4 - Q, 2) * (a4 - b2 * c2 - a2 * R);
		case 1630:
			return a2
					* (a6 - a5 * (b + c) + b * c * Q + a2 * p(b2 + c2, 2) - a4
							* (2 * b2 + b * c + 2 * c2) + 2 * a3 * (b3 + c3) - a
							* (b5 - b4 * c - b * c4 + c5));
		case 1631:
			return a2 * (a3 - b3 - c3);
		case 1632:
			return (a - b) * (a + b) * (a - c) * (a + c) * (a4 + Q);
		case 1633:
			return a * (a - b) * (a2 + p(b - c, 2)) * (a - c);
		case 1634:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c) * R;
		case 1635:
			return a * (2 * a - b - c) * (b - c);
		case 1636:
			return a2 * (b2 - c2) * (T * T) * (2 * a4 - Q - a2 * R);
		case 1637:
			return -((b2 - c2) * (-2 * a4 + Q + a2 * R));
		case 1638:
			return -((b - c) * (-2 * a2 + p(b - c, 2) + a * (b + c)));
		case 1639:
			return (a - b - c) * (2 * a - b - c) * (b - c);
		case 1640:
			return -((b2 - c2) * (-2 * a6 + 2 * a4 * R + Q * R - a2 * (b4 + c4)));
		case 1641:
			return 4 * a6 + b6 + 12 * a2 * b2 * c2 - 3 * b4 * c2 - 3 * b2 * c4
					+ c6 - 6 * a4 * R;
		case 1642:
			return a
					* (-b2 - c2 + a * (b + c))
					* (2 * a3 - 2 * a2 * (b + c) - p(b - c, 2) * (b + c) + a
							* R);
		case 1643:
			return a
					* (b - c)
					* (2 * a3 - 2 * a2 * (b + c) - p(b - c, 2) * (b + c) + a
							* R);
		case 1644:
			return (2 * a - b - c)
					* (2 * a2 - b2 + 4 * b * c - c2 - 2 * a * (b + c));
		case 1645:
			return a4 * p(b - c, 2) * p(b + c, 2) * (-2 * b2 * c2 + a2 * R);
		case 1646:
			return a2 * p(b - c, 2) * (-2 * b * c + a * (b + c));
		case 1647:
			return -((2 * a - b - c) * p(b - c, 2));
		case 1648:
			return p(b - c, 2) * p(b + c, 2) * (-2 * a2 + b2 + c2);
		case 1649:
			return (b2 - c2) * p(-2 * a2 + b2 + c2, 2);
		case 1650:
			return p(b - c, 2) * p(b + c, 2) * (T * T) * (-2 * a4 + Q + a2 * R);
		case 1651:
			return (2 * a4 - Q - a2 * R)
					* (2 * a8 - 2 * a6 * R + 4 * a2 * Q * R + a4
							* (-3 * b4 + 8 * b2 * c2 - 3 * c4) - Q
							* (b4 + 4 * b2 * c2 + c4));
		case 1652:
			return u(3) * a * (a + b - c) * (a - b + c) * (a + b + c) - a
					* (a - b - c) * S;
		case 1653:
			return u(3) * a * (a + b - c) * (a - b + c) * (a + b + c) + a
					* (a - b - c) * S;
		case 1654:
			return -a2 + b2 + b * c + c2 + a * (b + c);
		case 1655:
			return -(b2 * c2) + a * b * c * (b + c) + a2 * (b2 + b * c + c2);
		case 1656:
			return a4 + 2 * Q - 3 * a2 * R;
		case 1657:
			return 5 * a4 - 2 * Q - 3 * a2 * R;
		case 1658:
			return a2
					* (a8 - b8 + 2 * a4 * b2 * c2 + b6 * c2 + b2 * c6 - c8 - 2
							* a6 * R + a2
							* (2 * b6 - b4 * c2 - b2 * c4 + 2 * c6));
		case 1659:
			return (a2 + 2 * a * b + b2 - c2 + S)
					* (a2 - b2 + 2 * a * c + c2 + S);
		case 1660:
			return a4
					* (a8 + 10 * a4 * b2 * c2 - 2 * a6 * R - Q
							* (b4 + 4 * b2 * c2 + c4) + 2 * a2
							* (b6 - 3 * b4 * c2 - 3 * b2 * c4 + c6));
		case 1661:
			return a2
					* (a10 + 3 * a8 * R + 14 * a4 * Q * R - p(b2 - c2, 4) * R
							- 3 * a2 * Q * (b4 + 6 * b2 * c2 + c4) - 2 * a6
							* (7 * b4 - 10 * b2 * c2 + 7 * c4));
		case 1662:
			return a2
					* (a2 * b2 + a2 * c2 + b2 * c2)
					* (a2 * b2 - b4 + a2 * c2 - c4)
					+ a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 1663:
			return -(a2 * (a2 * b2 + a2 * c2 + b2 * c2) * (a2 * b2 - b4 + a2
					* c2 - c4))
					+ a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 1664:
			return a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* (a2 * b2 + a2 * c2 + b2 * c2)
					+ a2
					* (a2 * b2 - b4 + a2 * c2 - c4)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 1665:
			return a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* (a2 * b2 + a2 * c2 + b2 * c2)
					- a2
					* (a2 * b2 - b4 + a2 * c2 - c4)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 1666:
			return a2
					* (a2 * b2 + a2 * c2 + b2 * c2)
					* (a2 * b2 - b4 + a2 * c2 - c4)
					- a2
					* S
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 1667:
			return a2
					* (a2 * b2 + a2 * c2 + b2 * c2)
					* (a2 * b2 - b4 + a2 * c2 - c4)
					+ a2
					* S
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 1668:
			return a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* (a2 * b2 + a2 * c2 + b2 * c2)
					+ a2
					* S
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 1669:
			return -(a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2) * (a2 * b2
					+ a2 * c2 + b2 * c2))
					+ a2
					* S
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 1670:
			return a2 * R - a2 * u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1671:
			return a2 * (-b4 - c4 + a2 * R) + a2 * (-T)
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1672:
			return a2 * (2 * b2 * c2 + a2 * (-T)) + 2 * a2 * b * c
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1673:
			return a2 * (2 * b2 * c2 + a2 * (-T)) - 2 * a2 * b * c
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1674:
			return -(a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2) * (a2 * b2
					+ a2 * c2 + b2 * c2))
					+ 2
					* a2
					* b
					* c
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 1675:
			return a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* (a2 * b2 + a2 * c2 + b2 * c2)
					+ 2
					* a2
					* b
					* c
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 1676:
			return a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					+ (-(a2 * b2) + b4 - a2 * c2 - 2 * b2 * c2 + c4)
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1677:
			return -(a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2))
					+ (-(a2 * b2) + b4 - a2 * c2 - 2 * b2 * c2 + c4)
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1678:
			return -(a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2) * (a2 * b2
					+ a2 * c2 + b2 * c2))
					+ 2
					* a
					* b
					* c
					* (b + c)
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 1679:
			return a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* (a2 * b2 + a2 * c2 + b2 * c2)
					+ 2
					* a
					* b
					* c
					* (b + c)
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 1680:
			return -(a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)) + 2 * a * b
					* c * (b + c) * u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1681:
			return a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2) + 2 * a * b * c
					* (b + c) * u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1682:
			return a2 * (-a + b + c) * (b2 + c2 + a * (b + c))
					* (b2 + c2 + a * (b + c));
		case 1683:
			return a2
					* (a4 * b - a2 * b3 + a4 * c - a3 * b * c - a * b2 * c2
							- b3 * c2 - a2 * c3 - b2 * c3) + a2
					* (a2 * b - b3 + a2 * c - a * b * c - c3)
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1684:
			return -(a2 * (a4 * b - a2 * b3 + a4 * c - a3 * b * c - a * b2 * c2
					- b3 * c2 - a2 * c3 - b2 * c3))
					+ a2
					* (a2 * b - b3 + a2 * c - a * b * c - c3)
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1685:
			return -(a2 * (a + b + c) * (a2 * b - b3 + a2 * c - a * b * c - c3))
					+ a2 * (a * b + b2 + a * c + b * c + c2) * S;
		case 1686:
			return a2 * (a + b + c) * (a2 * b - b3 + a2 * c - a * b * c - c3)
					+ a2 * (a * b + b2 + a * c + b * c + c2) * S;
		case 1687:
			return a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2) - a2 * S
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1688:
			return a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2) + a2 * S
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1689:
			return a2 * (a2 * b2 - b4 + a2 * c2 - c4) + a2 * S
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1690:
			return a2 * (a2 * b2 - b4 + a2 * c2 - c4) - a2 * S
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1691:
			return a6 - a2 * b2 * c2;
		case 1692:
			return a2 * (2 * a4 + Q - a2 * R);
		case 1693:
			return a2
					* (a2 * b + a * b2 + a2 * c + a * b * c + b2 * c + a * c2 + b
							* c2)
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					+ a2
					* (a3 * b2 + a2 * b3 - a * b4 - b5 + 2 * a3 * b * c + a2
							* b2 * c - 2 * a * b3 * c - b4 * c + a3 * c2 + a2
							* b * c2 + a2 * c3 - 2 * a * b * c3 - a * c4 - b
							* c4 - c5)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4);
		case 1694:
			return -(a2
					* (a2 * b + a * b2 + a2 * c + a * b * c + b2 * c + a * c2 + b
							* c2) * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2))
					+ a2
					* (a3 * b2 + a2 * b3 - a * b4 - b5 + 2 * a3 * b * c + a2
							* b2 * c - 2 * a * b3 * c - b4 * c + a3 * c2 + a2
							* b * c2 + a2 * c3 - 2 * a * b * c3 - a * c4 - b
							* c4 - c5)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4);
		case 1695:
			return a
					* (a5 * (b + c) - b * c * Q + a4
							* (4 * b2 + 7 * b * c + 4 * c2) + 2 * a3
							* (b3 + b2 * c + b * c2 + c3) - 2 * a2
							* (2 * b4 + 3 * b3 * c + 3 * b * c3 + 2 * c4) - a
							* (3 * b5 + 3 * b4 * c + 2 * b3 * c2 + 2 * b2 * c3
									+ 3 * b * c4 + 3 * c5));
		case 1696:
			return a2
					* (a3 - b3 + 5 * b2 * c + 5 * b * c2 - c3 + a2 * (b + c) - a
							* p(b + c, 2));
		case 1697:
			return a * (a - b - c) * (a2 + p(b - c, 2) + 2 * a * (b + c));
		case 1698:
			return a + 2 * (b + c);
		case 1699:
			return -a3 - a * p(b - c, 2) + 2 * p(b - c, 2) * (b + c);
		default:
			return Double.NaN;
		}
	}

	private double weight1700to1799(int k, double a, double b, double c) {

		switch (k) {
		case 1700:
			return 2
					* a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					+ a
					* (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 - c3)
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1701:
			return -2
					* a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					+ a
					* (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 - c3)
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 1702:
			return -(a * (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c
					+ b2 * c - a * c2 + b * c2 - c3))
					+ 2 * a2 * S;
		case 1703:
			return a
					* (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 - c3) + 2 * a2 * S;
		case 1704:
			return 2
					* a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* (a2 * b2 + a2 * c2 + b2 * c2)
					+ a
					* (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 - c3)
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 1705:
			return -2
					* a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* (a2 * b2 + a2 * c2 + b2 * c2)
					+ a
					* (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 - c3)
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 1706:
			return a
					* (a3 - b3 + 5 * b2 * c + 5 * b * c2 - c3 + a2 * (b + c) - a
							* p(b + c, 2));
		case 1707:
			return a * (3 * a2 - b2 - c2);
		case 1708:
			return a
					* (a + b - c)
					* (a - b + c)
					* (a3 + b3 + b2 * c + b * c2 + c3 - a2 * (b + c) - a
							* p(b + c, 2));
		case 1709:
			return a
					* (a5 - a4 * (b + c) + 2 * a2 * p(b - c, 2) * (b + c)
							- p(b - c, 2) * p(b + c, 3) + a * p(b - c, 2) * R - 2
							* a3 * (b2 - 3 * b * c + c2));
		case 1710:
			return a
					* (a6 + a5 * (b + c) - a * p(b - c, 2) * p(b + c, 3) - a4
							* (b2 - b * c + c2) - Q * (b2 + b * c + c2) + a2
							* (b4 + c4));
		case 1711:
			return a
					* (a5 - a4 * (b + c) + 2 * a2 * p(b - c, 2) * (b + c)
							- p(b - c, 2) * p(b + c, 3) - 2 * a3 * R + a
							* p(b2 + c2, 2));
		case 1712:
			return a
					* U
					* V
					* (a8 - 4 * a6 * R - 4 * a2 * Q * R + Q
							* (b4 + 6 * b2 * c2 + c4) + a4
							* (6 * b4 - 4 * b2 * c2 + 6 * c4));
		case 1713:
			return a
					* (a6 * (b + c) - b * p(b - c, 2) * c * p(b + c, 3) + 2
							* a3 * Q - a5 * R - a * Q * R - a4
							* (2 * b3 + 3 * b2 * c + 3 * b * c2 + 2 * c3) + a2
							* (b5 + 3 * b4 * c + 3 * b * c4 + c5));
		case 1714:
			return a4 + 2 * a3 * (b + c) - 2 * a * b * c * (b + c) + Q;
		case 1715:
			return a
					* (a8 * (b + c) - b * p(b - c, 4) * c * p(b + c, 3) + a7
							* R + 3 * a3 * Q * R - a2 * p(b - c, 4)
							* (b3 + 2 * b2 * c + 2 * b * c2 + c3) - a6
							* (3 * b3 + 2 * b2 * c + 2 * b * c2 + 3 * c3) + a5
							* (-3 * b4 + 2 * b2 * c2 - 3 * c4) - a
							* p(b4 - c4, 2) + a4
							* (3 * b5 + b3 * c2 + b2 * c3 + 3 * c5));
		case 1716:
			return a
					* (a2 * b * c + a3 * (b + c) + a * p(b - c, 2) * (b + c) - b
							* c * R);
		case 1717:
			return a
					* (a6 - a3 * b * c * (b + c) + a * b * p(b - c, 2) * c
							* (b + c) + p(b - c, 2) * p(b + c, 4) - a4
							* (b2 + 3 * b * c + c2) + a2
							* (-b4 + b3 * c + 2 * b2 * c2 + b * c3 - c4));
		case 1718:
			return a
					* (a6 - a3 * b * c * (b + c) + a * b * p(b - c, 2) * c
							* (b + c) + p(b - c, 4) * p(b + c, 2) - a4
							* (b2 - b * c + c2) + a2
							* (-b4 + b3 * c + 2 * b2 * c2 + b * c3 - c4));
		case 1719:
			return a
					* (a5 + 3 * a3 * b * c + a4 * (b + c) - p(b - c, 2)
							* p(b + c, 3) - a
							* (b4 + b3 * c - 2 * b2 * c2 + b * c3 + c4));
		case 1720:
			return a
					* (a9 - a8 * (b + c) - p(b - c, 4) * p(b + c, 5) + a7
							* (-4 * b2 + 2 * b * c - 4 * c2) + 4 * a2
							* p(b - c, 2) * p(b + c, 3) * (b2 - b * c + c2) + 2
							* a5 * p(b - c, 2) * (3 * b2 + b * c + 3 * c2) + 4
							* a6 * (b3 + c3) - 2 * a4 * p(b - c, 2)
							* (3 * b3 + 5 * b2 * c + 5 * b * c2 + 3 * c3) + a
							* Q
							* (b4 - 6 * b3 * c + 2 * b2 * c2 - 6 * b * c3 + c4) - 2
							* a3
							* p(b - c, 2)
							* (2 * b4 - 3 * b3 * c - 6 * b2 * c2 - 3 * b * c3 + 2 * c4));
		case 1721:
			return a
					* (a4 - 2 * a3 * (b + c) - 2 * a * p(b - c, 2) * (b + c)
							+ 2 * a2 * (b2 - b * c + c2) + p(b - c, 2)
							* (b2 + 4 * b * c + c2));
		case 1722:
			return a
					* (a3 + b3 - 3 * b2 * c - 3 * b * c2 + c3 + a2 * (b + c) + a
							* R);
		case 1723:
			return a
					* (a4 - 2 * a2 * b * c - 2 * a3 * (b + c) - Q + 2 * a
							* (b3 + c3));
		case 1724:
			return a * (a3 + a2 * (b + c) - b * c * (b + c));
		case 1725:
			return a * (a4 * R + Q * R - 2 * a2 * (b4 - b2 * c2 + c4));
		case 1726:
			return a
					* (a5 - b5 + b3 * c2 + b2 * c3 - c5 - a3 * R + a2
							* (b3 + c3));
		case 1727:
			return a
					* (a6 + a3 * b * c * (b + c) - a * b * p(b - c, 2) * c
							* (b + c) + a4 * (-3 * b2 + b * c - 3 * c2) - Q * R + a2
							* (3 * b4 - b3 * c - 2 * b2 * c2 - b * c3 + 3 * c4));
		case 1728:
			return a
					* (a6 - 2 * a3 * b * c * (b + c) + 2 * a * b * p(b - c, 2)
							* c * (b + c) - p(b - c, 2) * p(b + c, 4) - 3 * a4
							* R + a2 * p(b + c, 2)
							* (3 * b2 - 4 * b * c + 3 * c2));
		case 1729:
			return a
					* (a6 - a5 * (b + c) + a3 * b * c * (b + c) - a4 * R - Q
							* (b2 - b * c + c2) + a2 * p(b - c, 2)
							* (b2 + b * c + c2) + a
							* (b5 - b3 * c2 - b2 * c3 + c5));
		case 1730:
			return a
					* (a4 * (b + c) - b * p(b - c, 2) * c * (b + c) - a * Q
							+ a3 * R - a2 * (b3 + c3));
		case 1731:
			return a * (a - b - c) * (a3 - a * b * c + p(b - c, 2) * (b + c));
		case 1732:
			return a
					* (a4 - 4 * a2 * b * c - 4 * a3 * (b + c) - Q + 4 * a
							* (b3 + c3));
		case 1733:
			return b * c * (2 * a4 + Q - a2 * R);
		case 1734:
			return a * (-b3 + c3 + a * (b2 - c2));
		case 1735:
			return a
					* (-(a3 * b * c * (b + c)) + a * b * p(b - c, 2) * c
							* (b + c) + a4 * R + Q * (b2 - b * c + c2) - a2
							* p(b - c, 2) * (2 * b2 + 3 * b * c + 2 * c2));
		case 1736:
			return a
					* (b5 - b3 * c2 - b2 * c3 + c5 - a * Q + a3 * R - a2
							* (b3 + c3));
		case 1737:
			return a3 * (b + c) - a * p(b - c, 2) * (b + c) + Q - a2 * R;
		case 1738:
			return -2 * a * b * c + a2 * (b + c) + p(b - c, 2) * (b + c);
		case 1739:
			return a * (b3 - 2 * b2 * c - 2 * b * c2 + c3 + a * R);
		case 1740:
			return -(a * b2 * c2) + a3 * R;
		case 1741:
			return a
					* (a - b - c)
					* (a6 - a4 * p(b - c, 2) + 2 * a5 * (b + c) + Q * R - a2
							* p(b - c, 2) * (b2 + 4 * b * c + c2) + a3
							* (-4 * b3 + 2 * b2 * c + 2 * b * c2 - 4 * c3) + 2
							* a * p(b - c, 2) * (b3 + c3));
		case 1742:
			return a
					* (-(b * p(b - c, 2) * c) + a3 * (b + c) + a * p(b - c, 2)
							* (b + c) + a2 * (-2 * b2 + b * c - 2 * c2));
		case 1743:
			return a * (3 * a - b - c);
		case 1744:
			return a
					* (a7 + a * b * c * Q - p(b - c, 2) * p(b + c, 3)
							* (b2 - b * c + c2) - a5
							* (2 * b2 + b * c + 2 * c2) - a4
							* (b3 + 2 * b2 * c + 2 * b * c2 + c3) + a3
							* (b4 + c4) + 2 * a2
							* (b5 + b4 * c - b3 * c2 - b2 * c3 + b * c4 + c5));
		case 1745:
			return a
					* (a4 * b * c + a5 * (b + c) - b * c * Q - 2 * a3
							* (b3 + c3) + a * (b5 - b4 * c - b * c4 + c5));
		case 1746:
			return a6 - b * c * Q - a4 * (b2 - b * c + c2) + a3 * (b3 + c3) - a
					* (b5 - b3 * c2 - b2 * c3 + c5);
		case 1747:
			return a
					* (a8 - 4 * a4 * b2 * c2 - 2 * a6 * R - Q * (b4 + c4) + 2
							* a2 * (b6 + c6));
		case 1748:
			return a * U * V * (a4 + b4 + c4 - 2 * a2 * R);
		case 1749:
			return a
					* (a6 - 3 * a4 * R - Q * R + a2
							* (3 * b4 - b2 * c2 + 3 * c4));
		case 1750:
			return a
					* (a5 - 3 * a4 * (b + c) + 2 * a2 * p(b - c, 2) * (b + c)
							- 3 * a * Q + 2 * a3 * R + p(b - c, 2)
							* (b3 + 7 * b2 * c + 7 * b * c2 + c3));
		case 1751:
			return (a3 - b2 * c + c3 - a * b * (b + c))
					* (a3 + b3 - b * c2 - a * c * (b + c));
		case 1752:
			return a
					* (a6 - 2 * a5 * (b + c) + 2 * a3 * b * c * (b + c)
							- p(b - c, 4) * p(b + c, 2) + a4 * R - a2
							* (b4 + 2 * b3 * c - 2 * b2 * c2 + 2 * b * c3 + c4) + 2
							* a * (b5 - b3 * c2 - b2 * c3 + c5));
		case 1753:
			return a
					* U
					* V
					* (a5 + a4 * (b + c) + p(b - c, 2) * p(b + c, 3) - 2 * a3
							* R - 2 * a2 * (b3 + b2 * c + b * c2 + c3) + a
							* (b4 + 6 * b2 * c2 + c4));
		case 1754:
			return a
					* (a5 - a4 * (b + c) + b * p(b - c, 2) * c * (b + c) - a3
							* R + a2 * (b3 + c3));
		case 1755:
			return a3 * (-b4 - c4 + a2 * R);
		case 1756:
			return a
					* (a4 * (b + c) + b * p(b - c, 2) * c * (b + c) + a3 * R
							- a2 * (b3 + c3) - a * (b4 + c4));
		case 1757:
			return a * (a2 - b2 - b * c - c2 + a * (b + c));
		case 1758:
			return a * (a + b - c) * (a - b + c)
					* (a3 + b3 + a * b * c + c3 - 2 * a2 * (b + c));
		case 1759:
			return a * (a3 - b3 - c3);
		case 1760:
			return a * (a4 - b4 - c4);
		case 1761:
			return a
					* (a4 + a2 * b * c + a3 * (b + c) - p(b + c, 2)
							* (b2 - b * c + c2) - a
							* (b3 + b2 * c + b * c2 + c3));
		case 1762:
			return a
					* (a5 - b5 + b3 * c2 + b2 * c3 - c5 + a * b * c
							* p(b + c, 2) - a3 * (b2 + b * c + c2) + a2
							* (b3 + c3));
		case 1763:
			return a
					* (a5 - b5 + b4 * c + b * c4 - c5 + a4 * (b + c) - 2 * a2
							* b * c * (b + c) - a * Q);
		case 1764:
			return a
					* (a4 * (b + c) - b * p(b - c, 2) * c * (b + c) + a3 * R
							- a * p(b2 + c2, 2) - a2 * (b3 + c3));
		case 1765:
			return a
					* (a6 * (b + c) - b * p(b - c, 2) * c * p(b + c, 3) - a5
							* R - a * Q * R + a4
							* (-2 * b3 + b2 * c + b * c2 - 2 * c3) + 2 * a3
							* (b4 + c4) + a2 * (b5 - b4 * c - b * c4 + c5));
		case 1766:
			return a * (a4 + 2 * a2 * b * c - 2 * a * b * c * (b + c) - Q);
		case 1767:
			return a
					* (a + b - c)
					* (a - b + c)
					* U
					* V
					* (a5 - 2 * a3 * p(b - c, 2) - a4 * (b + c) - p(b - c, 2)
							* p(b + c, 3) + 2 * a2
							* (b3 + b2 * c + b * c2 + c3) + a
							* (b4 - 4 * b3 * c - 2 * b2 * c2 - 4 * b * c3 + c4));
		case 1768:
			return a
					* (a5 - b5 + b4 * c + b * c4 - c5 - a4 * (b + c) + 2 * a2
							* p(b - c, 2) * (b + c) + a3
							* (-2 * b2 + 5 * b * c - 2 * c2) + a * p(b - c, 2)
							* (b2 - b * c + c2));
		case 1769:
			return a * (b - c)
					* (-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 1770:
			return 2 * a4 + a3 * (b + c) - a * p(b - c, 2) * (b + c) - Q - a2
					* R;
		case 1771:
			return a
					* (a6 + a3 * b * c * (b + c) - a * b * p(b - c, 2) * c
							* (b + c) + b * c * Q - 2 * a4 * R + a2
							* p(b - c, 2) * (b2 + b * c + c2));
		case 1772:
			return a
					* (-2 * a3 * b * c * (b + c) + 2 * a * b * p(b - c, 2) * c
							* (b + c) + p(b - c, 4) * p(b + c, 2) + a4 * R - 2
							* a2 * (b4 - b3 * c - b2 * c2 - b * c3 + c4));
		case 1773:
			return a
					* (a6 + 2 * a5 * (b + c) + 2 * a3 * b * c * (b + c) + a4
							* p(b + c, 2) - Q * R - a2
							* (b4 - 2 * b3 * c - 2 * b2 * c2 - 2 * b * c3 + c4) - 2
							* a * (b5 - 3 * b3 * c2 - 3 * b2 * c3 + c5));
		case 1774:
			return a
					* (a9 + a8 * (b + c) - 2 * a4 * b * p(b - c, 2) * c
							* (b + c) - p(b - c, 4) * p(b + c, 3) * R - a
							* p(b - c, 2) * p(b + c, 4) * R - 2 * a7
							* (b2 - b * c + c2) + 2 * a2 * p(b - c, 2)
							* p(b + c, 3) * (b2 - b * c + c2) - 2 * a5 * b * c
							* (3 * b2 - 2 * b * c + 3 * c2) - 2 * a6
							* (b3 + c3) + 2
							* a3
							* (b6 + 3 * b5 * c - b4 * c2 - 2 * b3 * c3 - b2
									* c4 + 3 * b * c5 + c6));
		case 1775:
			return a
					* (a9 + a8 * (b + c) - 2 * a4 * b * p(b - c, 2) * c
							* (b + c) - a * p(b - c, 4) * p(b + c, 2) * R
							- p(b - c, 4) * p(b + c, 3) * R + 2 * a2
							* p(b - c, 2) * p(b + c, 3) * (b2 - b * c + c2) - 2
							* a7 * (b2 + b * c + c2) + 2 * a5 * b * c
							* (3 * b2 + 2 * b * c + 3 * c2) - 2 * a6
							* (b3 + c3) + 2
							* a3
							* (b6 - 3 * b5 * c - b4 * c2 + 2 * b3 * c3 - b2
									* c4 - 3 * b * c5 + c6));
		case 1776:
			return a
					* (a - b - c)
					* (a4 + a2 * (-2 * b2 + 3 * b * c - 2 * c2) + p(b - c, 2)
							* (b2 + b * c + c2));
		case 1777:
			return a
					* (a6 + a3 * b * c * (b + c) - a * b * p(b - c, 2) * c
							* (b + c) - b * c * Q - 2 * a4 * (b2 - b * c + c2) + a2
							* p(b - c, 2) * (b2 + b * c + c2));
		case 1778:
			return a * (a + b) * (a + c) * (a2 + 2 * a * (b + c) - p(b + c, 2));
		case 1779:
			return a2
					* (-b7 - a5 * b * c + b4 * c3 + b3 * c4 - c7 + a6 * (b + c)
							+ a * b * c * Q - 3 * a4 * (b3 + c3) + a2
							* (3 * b5 - b4 * c - b * c4 + 3 * c5));
		case 1780:
			return a2
					* (a + b)
					* (a + c)
					* (a3 + b3 + b2 * c + b * c2 + c3 - a2 * (b + c) - a
							* p(b + c, 2));
		case 1781:
			return a * (a4 + a2 * b * c + a * b * c * (b + c) - Q);
		case 1782:
			return a
					* (a6 + a5 * (b + c) - a3 * b * c * (b + c) - Q * R - a4
							* (b2 + b * c + c2) + a2
							* (b4 + b3 * c + b * c3 + c4) - a
							* (b5 - b3 * c2 - b2 * c3 + c5));
		case 1783:
			return a * (a - b) * (a - c) * U * V;
		case 1784:
			return b * c * (-V) * U * (-2 * a4 + Q + a2 * R);
		case 1785:
			return U * V
					* (-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 1786:
			return a
					* (a + b - c)
					* (a - b + c)
					* (a6 + a5 * (b + c) - a2 * Q - a4 * (b2 + b * c + c2) + Q
							* (b2 + b * c + c2) - 2 * a3
							* (b3 + b2 * c + b * c2 + c3) + a
							* (b5 + b4 * c + b * c4 + c5));
		case 1787:
			return a
					* (a + b - c)
					* (a - b + c)
					* (a6 - a5 * (b + c) - a2 * p(b + c, 2) * R - a4
							* (b2 - 3 * b * c + c2) + Q * (b2 - b * c + c2) + 2
							* a3 * (b3 + c3) + a
							* (-b5 + b4 * c + 2 * b3 * c2 + 2 * b2 * c3 + b
									* c4 - c5));
		case 1788:
			return (a + b - c) * (a - b + c)
					* (a2 + 2 * a * (b + c) - p(b + c, 2));
		case 1789:
			return a * (a + b) * (a - b - c) * (a + c) * T
					* (a2 + a * b + b2 - c2) * (a2 - b2 + a * c + c2);
		case 1790:
			return a2 * (a + b) * (a + c) * T;
		case 1791:
			return a * T * (a2 + a * c + b * (b + c))
					* (a2 + a * b + c * (b + c));
		case 1792:
			return a * (a + b) * (a + c) * p(-a + b + c, 2) * T;
		case 1793:
			return a * (a + b) * (a - b - c) * (a + c) * T
					* (a2 - a * b + b2 - c2) * (a2 - b2 - a * c + c2);
		case 1794:
			return a2 * T * (a3 - a2 * b + b3 - b * c2 - a * p(b + c, 2))
					* (a3 - a2 * c - b2 * c + c3 - a * p(b + c, 2));
		case 1795:
			return a2 * T * (a3 - a2 * b + b3 - a * p(b - c, 2) - b * c2)
					* (a3 - a * p(b - c, 2) - a2 * c - b2 * c + c3);
		case 1796:
			return a2 * (a + 2 * b + c) * (a + b + 2 * c) * T;
		case 1797:
			return a2 * (a + b - 2 * c) * (a - 2 * b + c) * T;
		case 1798:
			return a2 * (a + b) * (a + c) * T * (a2 + a * c + b * (b + c))
					* (a2 + a * b + c * (b + c));
		case 1799:
			return (a2 + b2) * T * (a2 + c2);
		default:
			return Double.NaN;
		}
	}

	private double weight1800to1899(int k, double a, double b, double c) {

		switch (k) {
		case 1800:
			return a2 * (a + b) * (a - b - c) * (a + c) * T
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a * R);
		case 1801:
			return a2
					* (a + b)
					* (a + c)
					* T
					* (a3 - b3 - b2 * c - b * c2 - c3 - a2 * (b + c) + a
							* p(b + c, 2));
		case 1802:
			return a3 * p(-a + b + c, 2) * T;
		case 1803:
			return a2 * (a + b - c) * (a - b + c) * T
					* (a2 + b * (b - c) - a * (2 * b + c))
					* (a2 + c * (-b + c) - a * (b + 2 * c));
		case 1804:
			return a2 * (a + b - c) * (a - b + c) * (T * T);
		case 1805:
			return a2 * (a + b) * (a - b - c) * (a + b - c) * (a + c)
					* (a - b + c) * (b + c) * T + a3 * (a + b) * (a - b - c)
					* (a + c) * T * S;
		case 1806:
			return -(a2 * (a + b) * (a - b - c) * (a + b - c) * (a + c)
					* (a - b + c) * (b + c) * T)
					+ a3 * (a + b) * (a - b - c) * (a + c) * T * S;
		case 1807:
			return a * T * (a2 - a * b + b2 - c2) * (a2 - b2 - a * c + c2);
		case 1808:
			return a2 * (a + b) * (a - b - c) * (a + c) * (-b2 + a * c)
					* (a * b - c2) * T;
		case 1809:
			return a * (a - b - c) * T
					* (a3 - a2 * b + b3 - a * p(b - c, 2) - b * c2)
					* (a3 - a * p(b - c, 2) - a2 * c - b2 * c + c3);
		case 1810:
			return a2 * T * (a2 + b2 - b * c + 2 * c2 - a * (2 * b + c))
					* (a2 + 2 * b2 - b * c + c2 - a * (b + 2 * c));
		case 1811:
			return a * T * (a2 + a * (-4 * b + c) + b * (b + c))
					* (a2 + a * (b - 4 * c) + c * (b + c));
		case 1812:
			return a * (a + b) * (a - b - c) * (a + c) * T;
		case 1813:
			return a2 * (a - b) * (a - c) * (a + b - c) * (a - b + c) * T;
		case 1814:
			return a * (a2 + b * (b - c) - a * c) * T
					* (a2 - a * b + c * (-b + c));
		case 1815:
			return a2 * T
					* (a3 - 2 * b3 - a2 * c + b2 * c + c3 + a * (b2 - c2))
					* (a3 - a2 * b + b3 + b * c2 - 2 * c3 + a * (-b2 + c2));
		case 1816:
			return a
					* (a + b)
					* (a - b - c)
					* (a + c)
					* (a4 * b * c + a5 * (b + c) - b * c * Q - 2 * a3
							* (b3 + c3) + a * (b5 - b4 * c - b * c4 + c5));
		case 1817:
			return a
					* (a + b)
					* (a + c)
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2));
		case 1818:
			return a2 * T * (-b2 - c2 + a * (b + c));
		case 1819:
			return a2
					* (a + b)
					* (a - b - c)
					* (a + c)
					* T
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2));
		case 1820:
			return a * T * (a4 - 2 * a2 * b2 + Q) * (a4 - 2 * a2 * c2 + Q);
		case 1821:
			return -(b * c * (a4 + b4 - a2 * c2 - b2 * c2) * (-a4 + a2 * b2
					+ b2 * c2 - c4));
		case 1822:
			return (a2 * (a2 - b2) * (a2 - c2))
					/ (-(b * c * U * V) + a2 * b * c * (-T) + a
							* (-T)
							* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2
									* b2 * c2 - b4 * c2 - a2 * c4 - b2 * c4
									+ c6));
		case 1823:
			return (a2 * (a2 - b2) * (a2 - c2))
					/ (-(b * c * U * V) + a2 * b * c * (-T) - a
							* (-T)
							* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2
									* b2 * c2 - b4 * c2 - a2 * c4 - b2 * c4
									+ c6));
		case 1824:
			return -(a * (b + c) * U * V);
		case 1825:
			return a * (a + b - c) * (a - b + c) * (b + c) * U
					* (a2 - b2 - b * c - c2) * V;
		case 1826:
			return (b + c) * (-V) * U;
		case 1827:
			return a * (a - b - c) * U * V * (-p(b - c, 2) + a * (b + c));
		case 1828:
			return a * U * V * (p(b - c, 2) + a * (b + c));
		case 1829:
			return a * U * V * (b2 + c2 + a * (b + c));
		case 1830:
			return a
					* U
					* V
					* (a3 * (b + c) - a2 * p(b + c, 2) + p(b - c, 2)
							* (b2 + b * c + c2) - a
							* (b3 - 2 * b2 * c - 2 * b * c2 + c3));
		case 1831:
			return a
					* (a - b - c)
					* U
					* V
					* (a3 * (b + c) + a2 * p(b + c, 2) - p(b - c, 2)
							* (b2 + b * c + c2) - a
							* (b3 - 2 * b2 * c - 2 * b * c2 + c3));
		case 1832:
			return u(3) * (a + b - c) * (a - b + c) * (b + c) * U * V + (b + c)
					* U * V * S;
		case 1833:
			return u(3) * (a + b - c) * (a - b + c) * (b + c) * U * V - (b + c)
					* U * V * S;
		case 1834:
			return (b + c) * (2 * a3 + a2 * (b + c) + p(b - c, 2) * (b + c));
		case 1835:
			return a * (a + b - c) * (a - b + c) * (b + c) * U
					* (a2 - b2 + b * c - c2) * V;
		case 1836:
			return -a3 + p(b - c, 2) * (b + c);
		case 1837:
			return -((a - b - c) * (a3 + p(b - c, 2) * (b + c)));
		case 1838:
			return U * V
					* (2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 1839:
			return -((2 * a + b + c) * U * V);
		case 1840:
			return (b + c) * (a2 + b * c) * (-V) * U;
		case 1841:
			return a * U * V
					* (2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 1842:
			return U * V * (2 * a3 + a2 * (b + c) + p(b - c, 2) * (b + c));
		case 1843:
			return a2 * U * V * R;
		case 1844:
			return a * U * (a2 - b2 - b * c - c2) * V
					* (2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 1845:
			return a * U * (a2 - b2 + b * c - c2) * V
					* (-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 1846:
			return (2 * a - b - c) * (a + b - c) * (a - b + c) * U * V
					* (-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 1847:
			return b * p(a + b - c, 2) * c * p(a - b + c, 2) * (-V) * U;
		case 1848:
			return -(U * V * (b2 + c2 + a * (b + c)));
		case 1849:
			return -(a * (a - b - c) * (a + b + c) * U
					* (a * b - b2 + a * c + 2 * b * c - c2) * V)
					- 4 * a * b * c * U * V * S;
		case 1850:
			return a * (a - b - c) * (a + b + c) * U
					* (a * b - b2 + a * c + 2 * b * c - c2) * V - 4 * a * b * c
					* U * V * S;
		case 1851:
			return -((a2 + p(b - c, 2)) * U * V);
		case 1852:
			return -((a - b - c) * U * V * (2 * a5 - b5 + b4 * c + b * c4 - c5
					+ 2 * a4 * (b + c) - a2 * p(b - c, 2) * (b + c) - a * Q - a3
					* (b2 - 4 * b * c + c2)));
		case 1853:
			return -a6 - a2 * Q + 2 * Q * R;
		case 1854:
			return a
					* (a - b - c)
					* (a5 + 2 * a2 * p(b - c, 2) * (b + c) - a * Q - 2 * (b5
							- b4 * c - b * c4 + c5));
		case 1855:
			return -((a - b - c) * U * V * (-p(b - c, 2) + a * (b + c)));
		case 1856:
			return -((a - b - c) * U * V * (a2 * p(b - c, 2) + a3 * (b + c) - a
					* p(b - c, 2) * (b + c) - Q));
		case 1857:
			return -((a - b - c) * p(a4 - Q, 2));
		case 1858:
			return -(a * (a - b - c) * (b5 + 2 * a3 * b * c - b4 * c - b * c4
					+ c5 + a4 * (b + c) - 2 * a2 * p(b - c, 2) * (b + c)));
		case 1859:
			return a * (a - b - c) * U * V
					* (2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 1860:
			return U
					* V
					* (a2 * b * c * (b + c) - b * p(b - c, 2) * c * (b + c) - a
							* Q + a3 * R);
		case 1861:
			return U * V * (-b2 - c2 + a * (b + c));
		case 1862:
			return U * V * (2 * a2 + b2 + c2 - 2 * a * (b + c));
		case 1863:
			return -((a2 + p(b - c, 2)) * p(-a + b + c, 2) * U * V);
		case 1864:
			return -(a * (a - b - c) * (-(a2 * p(b - c, 2)) + a3 * (b + c) - a
					* p(b - c, 2) * (b + c) + Q));
		case 1865:
			return (b + c) * (-V) * U
					* (-2 * a * b * c - a2 * (b + c) + p(b - c, 2) * (b + c));
		case 1866:
			return a * (a + b - c) * (a - b + c) * U * V
					* (-b3 - 2 * a * b * c - c3 + a2 * (b + c));
		case 1867:
			return (b + c) * (-V) * U * (a2 + 2 * b * c + a * (b + c));
		case 1868:
			return -(a * (b + c) * U * V * (a4 + b4 - 2 * a2 * p(b - c, 2) + 6
					* b2 * c2 + c4 + 4 * a * b * c * (b + c)));
		case 1869:
			return (b + c) * (-V) * U
					* (-3 * a2 + p(b - c, 2) - 2 * a * (b + c));
		case 1870:
			return a * U * (a2 - b2 + b * c - c2) * V;
		case 1871:
			return -(a * U * V * (-4 * a * b2 * c2 + a4 * (b + c) + p(b - c, 2)
					* p(b + c, 3) - 2 * a2 * (b3 + b2 * c + b * c2 + c3)));
		case 1872:
			return -(a * U * V * (4 * a * b2 * c2 + a4 * (b + c) + p(b - c, 2)
					* p(b + c, 3) - 2 * a2 * (b3 + b2 * c + b * c2 + c3)));
		case 1873:
			return -((a + b - c) * (a - b + c) * (b + c) * U * V * (a3 - 2 * b
					* c * (b + c) - a * (b2 + 3 * b * c + c2)));
		case 1874:
			return (a + b - c) * (a - b + c) * (b + c) * (a2 - b * c) * U * V;
		case 1875:
			return a * (a + b - c) * (a - b + c) * U * V
					* (-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 1876:
			return a * (a + b - c) * (a - b + c) * U * V
					* (-b2 - c2 + a * (b + c));
		case 1877:
			return (2 * a - b - c) * (a + b - c) * (a - b + c) * U * V;
		case 1878:
			return a * U * V * (b2 - 4 * b * c + c2 + a * (b + c));
		case 1879:
			return p(b2 - c2, 4) - 2 * a2 * Q * R + a4 * (b4 + c4);
		case 1880:
			return a * (a + b - c) * (a - b + c) * (b + c) * U * V;
		case 1881:
			return (b + c)
					* (-V)
					* U
					* (a4 * (b2 - b * c + c2) + Q * (b2 - b * c + c2) - 2 * a2
							* p(b - c, 2) * (b2 + b * c + c2));
		case 1882:
			return -((a + b - c) * (a - b + c) * (b + c) * U * V * (a3 - 2 * b
					* c * (b + c) - a * p(b + c, 2)));
		case 1883:
			return -(U * V * (b3 + a * p(b - c, 2) + b2 * c + b * c2 + c3));
		case 1884:
			return U * V * (2 * a3 + p(b - c, 2) * (b + c) - a * R);
		case 1885:
			return -(U * V * (2 * a6 + 8 * a2 * b2 * c2 - 3 * a4 * R + Q * R));
		case 1886:
			return U * V * (2 * a3 - a2 * (b + c) - p(b - c, 2) * (b + c));
		case 1887:
			return a * (a + b - c) * (a - b + c) * U * V
					* (2 * a * b * c + a2 * (b + c) - p(b + c, 3));
		case 1888:
			return a
					* U
					* V
					* (-2 * a3 * b * c + a4 * (b + c) - 2 * a2 * p(b - c, 2)
							* (b + c) + p(b - c, 4) * (b + c) + 2 * a * b * c
							* p(b + c, 2));
		case 1889:
			return U * V * (a2 + 2 * b * c + 2 * a * (b + c));
		case 1890:
			return -(U * V * (2 * a2 + b2 + c2 + a * (b + c)));
		case 1891:
			return -(U * V * (2 * a3 + b3 + 2 * a * b * c + b2 * c + b * c2
					+ c3 + a2 * (b + c)));
		case 1892:
			return -((a + b - c) * (a - b + c) * U * V * (a3 - b3 - b2 * c - b
					* c2 - c3));
		case 1893:
			return -((a + b - c) * (a - b + c) * (b + c) * U * V * (a2 - 2 * b
					* c - a * (b + c)));
		case 1894:
			return U * V * (2 * a2 * (b + c) - p(b - c, 2) * (b + c) + a * R);
		case 1895:
			return b * c * (-V) * U * (-3 * a4 + Q + 2 * a2 * R);
		case 1896:
			return b * (a + b) * c * (a + c) * (-a + b + c) * p(a4 - Q, 2);
		case 1897:
			return (a - b) * (a - c) * U * V;
		case 1898:
			return -(a * (-(a4 * p(b - c, 2)) + a5 * (b + c) - p(b - c, 2)
					* p(b + c, 4) + 2 * a2 * Q - 2 * a3 * (b3 + c3) + a
					* (b5 - b4 * c - b * c4 + c5)));
		case 1899:
			return -(T * (a4 + Q));
		default:
			return Double.NaN;
		}
	}

	private double weight1900to1999(int k, double a, double b, double c) {

		switch (k) {
		case 1900:
			return -(a * U * V * (b2 + 4 * b * c + c2 + a * (b + c)));
		case 1901:
			return (b + c)
					* (-2 * a4 + a2 * p(b - c, 2) - a3 * (b + c) + a
							* p(b - c, 2) * (b + c) + Q);
		case 1902:
			return -(a * U * V * (b5 - 2 * a3 * b * c - b4 * c - b * c4 + c5
					+ a4 * (b + c) + 2 * a * b * c * p(b + c, 2) - 2 * a2
					* (b3 + c3)));
		case 1903:
			return -(a
					* (b + c)
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2)) * (a3 - a * p(b - c, 2) + a2
					* (-b + c) + (b - c) * p(b + c, 2)));
		case 1904:
			return -(U * V * (b3 + b2 * c + b * c2 + c3 + a
					* (b2 + 4 * b * c + c2)));
		case 1905:
			return a
					* U
					* V
					* (b5 - b4 * c - 4 * a * b2 * c2 - b * c4 + c5 + a4
							* (b + c) - 2 * a2 * (b3 + c3));
		case 1906:
			return -(U * V * (a4 * R + Q * R - 2 * a2 * (b4 - 6 * b2 * c2 + c4)));
		case 1907:
			return -(U * V * (a4 * R + Q * R - 2 * a2 * (b4 + 6 * b2 * c2 + c4)));
		case 1908:
			return a2 * (a * b * c * (b + c) + a2 * R + b * c * R);
		case 1909:
			return b * c * (a2 + b * c);
		case 1910:
			return a * (a4 + b4 - a2 * c2 - b2 * c2)
					* (a4 - a2 * b2 - b2 * c2 + c4);
		case 1911:
			return a3 * (-b2 + a * c) * (a * b - c2);
		case 1912:
			return a3 * (b2 * c2 * (-b + c) + a * (b4 - c4));
		case 1913:
			return a3 * (a3 * R + a * b * c * R + b * c * (b3 + c3));
		case 1914:
			return a4 - a2 * b * c;
		case 1915:
			return a6 + a2 * b2 * c2;
		case 1916:
			return (b2 - a * c) * (b2 + a * c) * (a * b - c2) * (a * b + c2);
		case 1917:
			return a7;
		case 1918:
			return a4 * (b + c);
		case 1919:
			return a4 * (b - c);
		case 1920:
			return b2 * c2 * (a2 + b * c);
		case 1921:
			return b2 * c2 * (-a2 + b * c);
		case 1922:
			return a4 * (-b2 + a * c) * (a * b - c2);
		case 1923:
			return a5 * R;
		case 1924:
			return a5 * (b2 - c2);
		case 1925:
			return b3 * c3 * (a4 + b2 * c2);
		case 1926:
			return -(a4 * b3 * c3) + b5 * c5;
		case 1927:
			return a5 * (-b2 + a * c) * (b2 + a * c) * (a * b - c2)
					* (a * b + c2);
		case 1928:
			return b5 * c5;
		case 1929:
			return a * (a2 + b2 + a * (b - c) - b * c - c2)
					* (a2 - b2 - b * c + c2 + a * (-b + c));
		case 1930:
			return b * c * R;
		case 1931:
			return a * (a + b) * (a + c) * (a2 - b2 - b * c - c2 + a * (b + c));
		case 1932:
			return a7 + a3 * b2 * c2;
		case 1933:
			return a7 - a3 * b2 * c2;
		case 1934:
			return b * c * (b2 - a * c) * (b2 + a * c) * (a * b - c2)
					* (a * b + c2);
		case 1935:
			return a * (a + b - c) * (a - b + c)
					* (a4 + b * c * p(b + c, 2) - a2 * (b2 + b * c + c2));
		case 1936:
			return a * (a - b - c)
					* (a4 - b * p(b - c, 2) * c - a2 * (b2 - b * c + c2));
		case 1937:
			return a
					* (a + b - c)
					* (a - b + c)
					* (a3 * b + c2 * (b2 - c2) + a2 * (-2 * b2 + c2) + a
							* (b3 - b * c2))
					* (-b4 + a3 * c + b2 * c2 + a2 * (b2 - 2 * c2) + a
							* (-(b2 * c) + c3));
		case 1938:
			return a
					* (b - c)
					* (a4 * (b + c) - b * p(b - c, 2) * c * (b + c) - a3
							* (b2 + 3 * b * c + c2) - a2
							* (b3 - 2 * b2 * c - 2 * b * c2 + c3) + a
							* (b4 + b3 * c + b * c3 + c4));
		case 1939:
			return a
					* (a4 * p(b - c, 2) - b * c * Q - a2 * p(b - c, 2)
							* (b2 + b * c + c2) - a3
							* (b3 - 2 * b2 * c - 2 * b * c2 + c3) + a
							* (b5 - b3 * c2 - b2 * c3 + c5));
		case 1940:
			return (a + b - c) * (a - b + c) * U * V
					* (a4 + b * c * p(b + c, 2) - a2 * (b2 + b * c + c2));
		case 1941:
			return U
					* V
					* (a10 * a2 + b2 * c2 * p(b2 - c2, 4) - 4 * a10 * R - 4
							* a6 * p(b2 + c2, 3) + a8
							* (6 * b4 + 13 * b2 * c2 + 6 * c4) + a4
							* (b8 + 2 * b6 * c2 + 10 * b4 * c4 + 2 * b2 * c6 + c8));
		case 1942:
			return a2
					* T
					* (a3 * b - b2 * c2 + c4 + a2 * (2 * b2 - c2) + a
							* (b3 - b * c2))
					* (a3 * b + c2 * (b2 - c2) + a2 * (-2 * b2 + c2) + a
							* (b3 - b * c2))
					* (b4 + a3 * c - b2 * c2 - a2 * (b2 - 2 * c2) + a
							* (-(b2 * c) + c3))
					* (-b4 + a3 * c + b2 * c2 + a2 * (b2 - 2 * c2) + a
							* (-(b2 * c) + c3));
		case 1943:
			return (a + b - c) * (a - b + c)
					* (a4 + b * c * p(b + c, 2) - a2 * (b2 + b * c + c2));
		case 1944:
			return (a - b - c)
					* (a4 - b * p(b - c, 2) * c - a2 * (b2 - b * c + c2));
		case 1945:
			return a2
					* (a + b - c)
					* (a - b + c)
					* (a3 * b + c2 * (b2 - c2) + a2 * (-2 * b2 + c2) + a
							* (b3 - b * c2))
					* (-b4 + a3 * c + b2 * c2 + a2 * (b2 - 2 * c2) + a
							* (-(b2 * c) + c3));
		case 1946:
			return a3
					* (b - c)
					* (b5 + b3 * c2 + b2 * c3 + c5 + a3 * (b2 + b * c + c2)
							- a2 * (b3 + 2 * b2 * c + 2 * b * c2 + c3) + a
							* (-b4 + b3 * c + 2 * b2 * c2 + b * c3 - c4));
		case 1947:
			return b * (-a + b - c) * (a + b - c) * c * (-V) * U
					* (a4 + b * c * p(b + c, 2) - a2 * (b2 + b * c + c2));
		case 1948:
			return b * c * (-a + b + c) * (-V) * U
					* (-a4 + b * p(b - c, 2) * c + a2 * (b2 - b * c + c2));
		case 1949:
			return a3
					* (a + b - c)
					* (a - b + c)
					* T
					* (a3 * b + c2 * (b2 - c2) + a2 * (-2 * b2 + c2) + a
							* (b3 - b * c2))
					* (-b4 + a3 * c + b2 * c2 + a2 * (b2 - 2 * c2) + a
							* (-(b2 * c) + c3));
		case 1950:
			return a2 * (a + b - c) * (a - b + c)
					* (a4 + b * c * p(b + c, 2) - a2 * (b2 + b * c + c2));
		case 1951:
			return a2 * (a - b - c)
					* (a4 - b * p(b - c, 2) * c - a2 * (b2 - b * c + c2));
		case 1952:
			return (a + b - c)
					* (a - b + c)
					* (a3 * b + c2 * (b2 - c2) + a2 * (-2 * b2 + c2) + a
							* (b3 - b * c2))
					* (-b4 + a3 * c + b2 * c2 + a2 * (b2 - 2 * c2) + a
							* (-(b2 * c) + c3));
		case 1953:
			return -(a * (-Q + a2 * R));
		case 1954:
			return a
					* (a8 - b2 * c2 * Q - 2 * a6 * R + a4
							* (b4 + 3 * b2 * c2 + c4));
		case 1955:
			return a
					* (a8 + b2 * c2 * Q - 2 * a6 * R + a4 * (b4 + b2 * c2 + c4));
		case 1956:
			return a
					* (a6 * b2 + c4 * Q + a4 * (-2 * b4 + c4) + a2
							* (b6 + b2 * c4 - 2 * c6))
					* (a6 * c2 + b4 * Q + a4 * (b4 - 2 * c4) + a2
							* (-2 * b6 + b4 * c2 + c6));
		case 1957:
			return a * U * V * (a4 + 2 * b2 * c2 - a2 * R);
		case 1958:
			return a5 + 2 * a * b2 * c2 - a3 * R;
		case 1959:
			return a * (b4 + c4 - a2 * R);
		case 1960:
			return a2 * (2 * a - b - c) * (b - c);
		case 1961:
			return a * (a2 + b2 + 3 * b * c + c2 + a * (b + c));
		case 1962:
			return a * (b + c) * (2 * a + b + c);
		case 1963:
			return a * (a + b) * (a + c)
					* (a2 + b2 + 3 * b * c + c2 + a * (b + c));
		case 1964:
			return a3 * R;
		case 1965:
			return b * c * (a4 + b2 * c2);
		case 1966:
			return b * c * (a4 - b2 * c2);
		case 1967:
			return a3 * (-b2 + a * c) * (b2 + a * c) * (a * b - c2)
					* (a * b + c2);
		case 1968:
			return a2 * U * V * (a4 + 2 * b2 * c2 - a2 * R);
		case 1969:
			return b3 * c3 * (-V) * U;
		case 1970:
			return a2
					* (a8 - b2 * c2 * Q - 2 * a6 * R + a4
							* (b4 + 3 * b2 * c2 + c4));
		case 1971:
			return a2
					* (a8 + b2 * c2 * Q - 2 * a6 * R + a4 * (b4 + b2 * c2 + c4));
		case 1972:
			return (a6 * b2 + c4 * Q + a4 * (-2 * b4 + c4) + a2
					* (b6 + b2 * c4 - 2 * c6))
					* (a6 * c2 + b4 * Q + a4 * (b4 - 2 * c4) + a2
							* (-2 * b6 + b4 * c2 + c6));
		case 1973:
			return a3 * U * V;
		case 1974:
			return a4 * U * V;
		case 1975:
			return a4 + 2 * b2 * c2 - a2 * R;
		case 1976:
			return a2 * (a4 + b4 - a2 * c2 - b2 * c2)
					* (a4 - a2 * b2 - b2 * c2 + c4);
		case 1977:
			return a4 * p(b - c, 2);
		case 1978:
			return (a - b) * b2 * (a - c) * c2;
		case 1979:
			return a2
					* (-(b2 * c2) + a * b * c * (b + c) + a2
							* (b2 - 3 * b * c + c2));
		case 1980:
			return a5 * (b - c);
		case 1981:
			return (a - b) * (a - c) * U * V
					* (a4 - b * p(b - c, 2) * c - a2 * (b2 - b * c + c2));
		case 1982:
			return (a + b)
					* (a + c)
					* U
					* V
					* (a4 - 2 * a3 * (b + c) + b * c * p(b + c, 2) - a2
							* (b2 + b * c + c2) + 2 * a
							* (b3 + b2 * c + b * c2 + c3));
		case 1983:
			return a3 * (a - b) * (a - c) * (a2 - b2 + b * c - c2);
		case 1984:
			return a * (a + b) * p(b - c, 2) * (a + c) * p(-a + b + c, 4)
					* (a4 - b * p(b - c, 2) * c - a2 * (b2 - b * c + c2));
		case 1985:
			return -(a4 * b * c) + b * c * Q - a3 * (b3 + c3) + a
					* (b5 - b3 * c2 - b2 * c3 + c5);
		case 1986:
			return a2 * U * (a2 - b2 - b * c - c2) * (a2 - b2 + b * c - c2) * V
					* (a4 * R + Q * R - 2 * a2 * (b4 - b2 * c2 + c4));
		case 1987:
			return a2
					* (a6 * b2 + c4 * Q + a4 * (-2 * b4 + c4) + a2
							* (b6 + b2 * c4 - 2 * c6))
					* (a6 * c2 + b4 * Q + a4 * (b4 - 2 * c4) + a2
							* (-2 * b6 + b4 * c2 + c6));
		case 1988:
			return a2
					* (a6 * (b2 - c2) + b2 * c2 * Q + a2 * (b2 - c2)
							* p(b2 + c2, 2) - a4 * (2 * b4 + b2 * c2 - 2 * c4))
					* (a6 * (b2 - c2) - b2 * c2 * Q + a2 * (b2 - c2)
							* p(b2 + c2, 2) + a4 * (-2 * b4 + b2 * c2 + 2 * c4));
		case 1989:
			return (a2 - a * b + b2 - c2) * (a2 + a * b + b2 - c2)
					* (a2 - b2 - a * c + c2) * (a2 - b2 + a * c + c2);
		case 1990:
			return U * V * (2 * a4 - Q - a2 * R);
		case 1991:
			return -2 * a2 + b2 + c2 - S;
		case 1992:
			return 5 * a2 - b2 - c2;
		case 1993:
			return a2 * (a4 + b4 + c4 - 2 * a2 * R);
		case 1994:
			return a2 * (a4 + b4 - b2 * c2 + c4 - 2 * a2 * R);
		case 1995:
			return a2 * (a4 - b4 + 4 * b2 * c2 - c4);
		case 1996:
			return p(a + b - c, 2) * p(a - b + c, 2)
					* (a2 + b2 + 4 * b * c + c2 - 2 * a * (b + c));
		case 1997:
			return a3 + b3 - 3 * b2 * c - 3 * b * c2 + c3 - a2 * (b + c) - a
					* (b2 - 8 * b * c + c2);
		case 1998:
			return a
					* (a5 - 3 * a4 * (b + c) + 2 * a3 * p(b + c, 2)
							+ p(b - c, 2) * p(b + c, 3) + 2 * a2
							* (b3 + b2 * c + b * c2 + c3) - a
							* (3 * b4 + 4 * b3 * c - 6 * b2 * c2 + 4 * b * c3 + 3 * c4));
		case 1999:
			return a3 + a * b * c + a2 * (b + c) - b * c * (b + c);
		default:
			return Double.NaN;
		}
	}

	private double weight2000to2099(int k, double a, double b, double c) {

		switch (k) {
		case 2000:
			return a
					* (a5 + 2 * a3 * b * c - a4 * (b + c) + p(b - c, 2)
							* p(b + c, 3) - a
							* (b4 + 2 * b3 * c - 2 * b2 * c2 + 2 * b * c3 + c4));
		case 2001:
			return a2
					* (a8 + 2 * b4 * c4 - a6 * R - a2 * Q * R + a4 * (b4 + c4));
		case 2002:
			return a
					* (a + b - c)
					* (a - b + c)
					* (a5 + 2 * a3 * b * c - a4 * (b + c) + p(b - c, 2)
							* p(b + c, 3) - a
							* (b4 + 2 * b3 * c - 2 * b2 * c2 + 2 * b * c3 + c4));
		case 2003:
			return a2 * (a + b - c) * (a - b + c) * (a2 - b2 - b * c - c2);
		case 2004:
			return a2
					* (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 6 * b2 * c2 - 2 * c4)
					+ u(3) * a4 * S;
		case 2005:
			return -(a2 * (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 6 * b2 * c2 - 2 * c4))
					+ u(3) * a4 * S;
		case 2006:
			return (a + b - c) * (a - b + c) * (a2 - a * b + b2 - c2)
					* (a2 - b2 - a * c + c2);
		case 2007:
			return a2 * R * S + 2 * a2 * b * c * u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2008:
			return a2 * R * S - 2 * a2 * b * c * u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2009:
			return a2 * R * S + (a2 * b2 - b4 + a2 * c2 + 2 * b2 * c2 - c4)
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2010:
			return a2 * R * S + (-(a2 * b2) + b4 - a2 * c2 - 2 * b2 * c2 + c4)
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2011:
			return -(a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2) * (a2 * b2
					+ a2 * c2 + b2 * c2))
					+ a2
					* R
					* S
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 2012:
			return a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* (a2 * b2 + a2 * c2 + b2 * c2)
					+ a2
					* R
					* S
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 2013:
			return a2 * (a - b - c) * (a + b - c) * (a - b + c) * (a + b + c)
					* R - 2 * a * b * c * (b + c) * S
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2014:
			return a2 * (a - b - c) * (a + b - c) * (a - b + c) * (a + b + c)
					* R + 2 * a * b * c * (b + c) * S
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2015:
			return -(a * b * c
					* (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4) * u(a2
					* b2 + a2 * c2 + b2 * c2))
					- a2
					* R
					* S
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2016:
			return -(a * b * c
					* (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4) * u(a2
					* b2 + a2 * c2 + b2 * c2))
					+ a2
					* R
					* S
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2017:
			return 2
					* a2
					* R
					* S
					- a
					* (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 - c3)
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2018:
			return 2
					* a2
					* R
					* S
					+ a
					* (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 - c3)
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2019:
			return a2
					* R
					* (a2 * b + a * b2 + a2 * c + a * b * c + b2 * c + a * c2 + b
							* c2)
					* S
					- a2
					* (a3 * b2 + a2 * b3 - a * b4 - b5 + 2 * a3 * b * c + a2
							* b2 * c - 2 * a * b3 * c - b4 * c + a3 * c2 + a2
							* b * c2 + a2 * c3 - 2 * a * b * c3 - a * c4 - b
							* c4 - c5) * u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2020:
			return a2
					* R
					* (a2 * b + a * b2 + a2 * c + a * b * c + b2 * c + a * c2 + b
							* c2)
					* S
					+ a2
					* (a3 * b2 + a2 * b3 - a * b4 - b5 + 2 * a3 * b * c + a2
							* b2 * c - 2 * a * b3 * c - b4 * c + a3 * c2 + a2
							* b * c2 + a2 * c3 - 2 * a * b * c3 - a * c4 - b
							* c4 - c5) * u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2021:
			return a2
					* (b6 + c6 + R * (3 * a4 - b2 * c2) - 2 * a2
							* (b4 + b2 * c2 + c4));
		case 2022:
			return a2
					* (a8 * b + 2 * a7 * b2 - 3 * a6 * b3 - 4 * a5 * b4 + 3
							* a4 * b5 + 2 * a3 * b6 - a2 * b7 + a8 * c - a6
							* b2 * c - a4 * b4 * c + a2 * b6 * c + 2 * a7 * c2
							- a6 * b * c2 - 4 * a5 * b2 * c2 - a4 * b3 * c2 + 2
							* a3 * b4 * c2 + 3 * a2 * b5 * c2 - b7 * c2 - 3
							* a6 * c3 - a4 * b2 * c3 - a2 * b4 * c3 + b6 * c3
							- 4 * a5 * c4 - a4 * b * c4 + 2 * a3 * b2 * c4 - a2
							* b3 * c4 + 3 * a4 * c5 + 3 * a2 * b2 * c5 + 2 * a3
							* c6 + a2 * b * c6 + b3 * c6 - a2 * c7 - b2 * c7);
		case 2023:
			return b2 * c2 * Q + a6 * R - a4 * p(b2 + c2, 2) + a2
					* (2 * b6 - b4 * c2 - b2 * c4 + 2 * c6);
		case 2024:
			return a2
					* (3 * a6 * b2 - a4 * b4 + a2 * b6 + b8 + 3 * a6 * c2 - 5
							* a2 * b4 * c2 + 2 * b6 * c2 - a4 * c4 - 5 * a2
							* b2 * c4 - 2 * b4 * c4 + a2 * c6 + 2 * b2 * c6 + c8);
		case 2025:
			return a2
					* (b8 + 4 * b6 * c2 - 2 * b4 * c4 + 4 * b2 * c6 + c8 + 3
							* a6 * R - a4 * (3 * b4 + 4 * b2 * c2 + 3 * c4) + a2
							* (3 * b6 - 5 * b4 * c2 - 5 * b2 * c4 + 3 * c6));
		case 2026:
			return -(a2 * (a2 * b2 + a2 * c2 + b2 * c2) * (a2 * b2 - b4 + a2
					* c2 - c4))
					+ a2
					* R
					* S
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 2027:
			return a2
					* (a2 * b2 + a2 * c2 + b2 * c2)
					* (a2 * b2 - b4 + a2 * c2 - c4)
					+ a2
					* R
					* S
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 2028:
			return -(a2 * (a2 * b2 + a2 * c2 + b2 * c2) * (a2 * b2 - b4 + a2
					* c2 - c4))
					+ a2
					* R
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 2029:
			return a2
					* (a2 * b2 + a2 * c2 + b2 * c2)
					* (a2 * b2 - b4 + a2 * c2 - c4)
					+ a2
					* R
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 2030:
			return a2 * (4 * a4 + b4 - 4 * b2 * c2 + c4 - a2 * R);
		case 2031:
			return a2
					* (4 * a6 - 5 * a4 * b2 + 4 * a2 * b4 + b6 - 5 * a4 * c2
							- 6 * a2 * b2 * c2 + b4 * c2 + 4 * a2 * c4 + b2
							* c4 + c6);
		case 2032:
			return a2
					* (4 * a8 - a6 * R + a2 * Q * R + Q * (b4 + c4) + a4
							* (3 * b4 - 8 * b2 * c2 + 3 * c4));
		case 2033:
			return 2
					* a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					- 2
					* a2
					* R
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 2034:
			return 2
					* a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					+ 2
					* a2
					* R
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 2035:
			return -2 * a2 * R * (a2 * b2 + a2 * c2 + b2 * c2) + 2 * a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2036:
			return a2 * R * (a2 * b2 + a2 * c2 + b2 * c2) + a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2037:
			return a2
					* (a2 * b2 + a2 * c2 + b2 * c2)
					* (a2 * b2 - b4 + a2 * c2 - c4)
					* (a * b * c + (a + b) * c2 + b2 * (a + c) + a2 * (b + c))
					+ a2
					* (-(a3 * b2) - a2 * b3 + a * b4 + b5 - 2 * a3 * b * c - a2
							* b2 * c + 2 * a * b3 * c + b4 * c - a3 * c2 - a2
							* b * c2 - a2 * c3 + 2 * a * b * c3 + a * c4 + b
							* c4 + c5)
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 2038:
			return a2
					* (a2 * b2 + a2 * c2 + b2 * c2)
					* (a2 * b2 - b4 + a2 * c2 - c4)
					* (a * b * c + (a + b) * c2 + b2 * (a + c) + a2 * (b + c))
					- a2
					* (-(a3 * b2) - a2 * b3 + a * b4 + b5 - 2 * a3 * b * c - a2
							* b2 * c + 2 * a * b3 * c + b4 * c - a3 * c2 - a2
							* b * c2 - a2 * c3 + 2 * a * b * c3 + a * c4 + b
							* c4 + c5)
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u((a4 + b4 + c4 - b2 * c2 - c2 * a2 - a2 * b2)
							* (b2 * c2 + c2 * a2 + a2 * b2));
		case 2039:
			return a2 * (a2 * b2 - b4 + a2 * c2 - c4) + (Q - a2 * R)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4);
		case 2040:
			return a2 * (a2 * b2 - b4 + a2 * c2 - c4) - (Q - a2 * R)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4);
		case 2041:
			return -Q - a2 * R * (-1 + u(3)) + a4 * u(3);
		case 2042:
			return Q + a4 * u(3) - a2 * R * (1 + u(3));
		case 2043:
			return a4 + Q * (-2 + u(3)) - a2 * R * (-1 + u(3));
		case 2044:
			return -a4 - a2 * R * (1 + u(3)) + Q * (2 + u(3));
		case 2045:
			return -a4 - a2 * R * (-3 + u(3)) + Q * (-2 + u(3));
		case 2046:
			return a4 + Q * (2 + u(3)) - a2 * R * (3 + u(3));
		case 2047:
			return -((a - b - c) * (a + b - c) * (a - b + c) * p(a + b + c, 3))
					+ U * V * S;
		case 2048:
			return -((a + b + c) * U * V) + (a - b - c) * (a + b - c)
					* (a - b + c) * S;
		case 2049:
			return a4 + 2 * a3 * (b + c) + 3 * a2 * p(b + c, 2) + 2 * b * c
					* p(b + c, 2) + 2 * a * p(b + c, 3);
		case 2050:
			return a6 + 4 * a4 * b * c + 2 * a3 * p(b - c, 2) * (b + c) - 2 * b
					* c * Q - a2 * p(b - c, 2) * (b2 + 4 * b * c + c2) - 2 * a
					* (b5 - b4 * c - b * c4 + c5);
		case 2051:
			return -((-b3 + b * c2 + a * c * (-b + c) + a2 * (b + c)) * (a * b
					* (b - c) + a2 * (b + c) + c * (b2 - c2)));
		case 2052:
			return b2 * c2 * p(a4 - Q, 2);
		case 2053:
			return a2 * (a - b - c) * (a * (b - c) - b * c)
					* (a * (b - c) + b * c);
		case 2054:
			return a2 * (b + c) * (a2 + b2 + a * (b - c) - b * c - c2)
					* (a2 - b2 - b * c + c2 + a * (-b + c));
		case 2055:
			return a2
					* T
					* (a10 * a2 + b2 * c2 * p(b2 - c2, 4) - 4 * a10 * R + a4
							* Q * (b4 + c4) + a8
							* (6 * b4 + 9 * b2 * c2 + 6 * c4) - 4 * a6
							* (b6 + b4 * c2 + b2 * c4 + c6));
		case 2056:
			return a6 + a2 * b2 * c2 - 2 * a4 * R;
		case 2057:
			return a
					* (a - b - c)
					* (a5 - 2 * a3 * p(b - c, 2) - a4 * (b + c) - p(b - c, 2)
							* p(b + c, 3) + 2 * a2
							* (b3 + b2 * c + b * c2 + c3) + a
							* (b4 - 4 * b3 * c - 2 * b2 * c2 - 4 * b * c3 + c4));
		case 2058:
			return u(3)
					* a2
					* (a10 * a4 - 6 * a10 * a2 * b2 + 15 * a10 * b4 - 20 * a8
							* b6 + 15 * a6 * b8 - 6 * a4 * b10 + a2 * b10 * b2
							- 6 * a10 * a2 * c2 + 24 * a10 * b2 * c2 - 31 * a8
							* b4 * c2 + 10 * a6 * b6 * c2 + 6 * a4 * b8 * c2
							- 2 * a2 * b10 * c2 - b10 * b2 * c2 + 15 * a10 * c4
							- 31 * a8 * b2 * c4 + 5 * a6 * b4 * c4 + 11 * a4
							* b6 * c4 - 3 * a2 * b8 * c4 + 3 * b10 * c4 - 20
							* a8 * c6 + 10 * a6 * b2 * c6 + 11 * a4 * b4 * c6
							+ 8 * a2 * b6 * c6 - 2 * b8 * c6 + 15 * a6 * c8 + 6
							* a4 * b2 * c8 - 3 * a2 * b4 * c8 - 2 * b6 * c8 - 6
							* a4 * c10 - 2 * a2 * b2 * c10 + 3 * b4 * c10 + a2
							* c10 * c2 - b2 * c10 * c2)
					- a2
					* (a10 * a2 - 3 * a10 * b2 + 2 * a8 * b4 + 2 * a6 * b6 - 3
							* a4 * b8 + a2 * b10 - 3 * a10 * c2 - 6 * a8 * b2
							* c2 + 25 * a6 * b4 * c2 - 19 * a4 * b6 * c2 + 2
							* a2 * b8 * c2 + b10 * c2 + 2 * a8 * c4 + 25 * a6
							* b2 * c4 - 21 * a4 * b4 * c4 - 3 * a2 * b6 * c4
							- 4 * b8 * c4 + 2 * a6 * c6 - 19 * a4 * b2 * c6 - 3
							* a2 * b4 * c6 + 6 * b6 * c6 - 3 * a4 * c8 + 2 * a2
							* b2 * c8 - 4 * b4 * c8 + a2 * c10 + b2 * c10) * S;
		case 2059:
			return u(3)
					* a2
					* (a10 * a4 - 6 * a10 * a2 * b2 + 15 * a10 * b4 - 20 * a8
							* b6 + 15 * a6 * b8 - 6 * a4 * b10 + a2 * b10 * b2
							- 6 * a10 * a2 * c2 + 24 * a10 * b2 * c2 - 31 * a8
							* b4 * c2 + 10 * a6 * b6 * c2 + 6 * a4 * b8 * c2
							- 2 * a2 * b10 * c2 - b10 * b2 * c2 + 15 * a10 * c4
							- 31 * a8 * b2 * c4 + 5 * a6 * b4 * c4 + 11 * a4
							* b6 * c4 - 3 * a2 * b8 * c4 + 3 * b10 * c4 - 20
							* a8 * c6 + 10 * a6 * b2 * c6 + 11 * a4 * b4 * c6
							+ 8 * a2 * b6 * c6 - 2 * b8 * c6 + 15 * a6 * c8 + 6
							* a4 * b2 * c8 - 3 * a2 * b4 * c8 - 2 * b6 * c8 - 6
							* a4 * c10 - 2 * a2 * b2 * c10 + 3 * b4 * c10 + a2
							* c10 * c2 - b2 * c10 * c2)
					+ a2
					* (a10 * a2 - 3 * a10 * b2 + 2 * a8 * b4 + 2 * a6 * b6 - 3
							* a4 * b8 + a2 * b10 - 3 * a10 * c2 - 6 * a8 * b2
							* c2 + 25 * a6 * b4 * c2 - 19 * a4 * b6 * c2 + 2
							* a2 * b8 * c2 + b10 * c2 + 2 * a8 * c4 + 25 * a6
							* b2 * c4 - 21 * a4 * b4 * c4 - 3 * a2 * b6 * c4
							- 4 * b8 * c4 + 2 * a6 * c6 - 19 * a4 * b2 * c6 - 3
							* a2 * b4 * c6 + 6 * b6 * c6 - 3 * a4 * c8 + 2 * a2
							* b2 * c8 - 4 * b4 * c8 + a2 * c10 + b2 * c10) * S;
		case 2060:
			return (3 * a4 - Q - 2 * a2 * R)
					* (5 * a10 * a2 + p(b2 - c2, 6) - 10 * a10 * R + 36 * a6
							* Q * R + a8 * (-9 * b4 + 34 * b2 * c2 - 9 * c4)
							- a4 * Q * (29 * b4 + 54 * b2 * c2 + 29 * c4) + 2
							* a2 * Q
							* (3 * b6 + 13 * b4 * c2 + 13 * b2 * c4 + 3 * c6));
		case 2061:
			return a2
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2))
					* (a10
							* a4
							+ 2
							* a10
							* a3
							* (b + c)
							+ 2
							* a
							* p(b - c, 6)
							* p(b + c, 7)
							+ a10
							* a2
							* (-5 * b2 + 3 * b * c - 5 * c2)
							- 5
							* a2
							* p(b - c, 6)
							* p(b + c, 4)
							* R
							- 4
							* a3
							* p(b - c, 4)
							* p(b + c, 5)
							* (3 * b2 - 2 * b * c + 3 * c2)
							- 4
							* a10
							* a
							* (3 * b3 + b2 * c + b * c2 + 3 * c3)
							+ p(b - c, 4)
							* p(b + c, 6)
							* (b4 - 3 * b3 * c + 8 * b2 * c2 - 3 * b * c3 + c4)
							+ a10
							* (9 * b4 - 6 * b3 * c + 26 * b2 * c2 - 6 * b * c3 + 9 * c4)
							+ 2
							* a5
							* p(b - c, 2)
							* p(b + c, 3)
							* (15 * b4 - 16 * b3 * c + 18 * b2 * c2 - 16 * b
									* c3 + 15 * c4)
							- 8
							* a7
							* p(b - c, 2)
							* (5 * b5 + 9 * b4 * c + 10 * b3 * c2 + 10 * b2
									* c3 + 9 * b * c4 + 5 * c5)
							+ a9
							* (30 * b5 - 2 * b4 * c + 4 * b3 * c2 + 4 * b2 * c3
									- 2 * b * c4 + 30 * c5)
							- a6
							* p(b - c, 2)
							* (5 * b6 - 18 * b5 * c - 101 * b4 * c2 - 60 * b3
									* c3 - 101 * b2 * c4 - 18 * b * c5 + 5 * c6)
							- a8
							* (5 * b6 + 7 * b5 * c + 55 * b4 * c2 - 70 * b3
									* c3 + 55 * b2 * c4 + 7 * b * c5 + 5 * c6) + a4
							* p(b - c, 2)
							* (9 * b8 - 9 * b7 * c - 62 * b6 * c2 - 39 * b5
									* c3 - 54 * b4 * c4 - 39 * b3 * c5 - 62
									* b2 * c6 - 9 * b * c7 + 9 * c8));
		case 2062:
			return a2
					* (a + b - c)
					* (a - b + c)
					* T
					* (a8 + 5 * a4 * b * c * p(b + c, 2) - a6
							* (2 * b2 + 3 * b * c + 2 * c2) - Q
							* (b4 + b3 * c + 4 * b2 * c2 + b * c3 + c4) + a2
							* p(b + c, 2)
							* (2 * b4 - 5 * b3 * c + 2 * b2 * c2 - 5 * b * c3 + 2 * c4));
		case 2063:
			return a2
					* T
					* (a8 + 10 * a4 * b2 * c2 - 2 * a6 * R - Q
							* (b4 + 4 * b2 * c2 + c4) + 2 * a2
							* (b6 - 3 * b4 * c2 - 3 * b2 * c4 + c6));
		case 2064:
			return b * c * (-a4 + b4 - a2 * b * c + b3 * c + b * c3 + c4);
		case 2065:
			return a2 * (a4 + b4 - a2 * c2 - b2 * c2)
					* (a4 - a2 * b2 - b2 * c2 + c4)
					* (a4 + b4 - b2 * c2 + 2 * c4 - a2 * (2 * b2 + c2))
					* (a4 + 2 * b4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 2066:
			return a2 * (a - b - c) * (a + b + c) - a2 * S;
		case 2067:
			return a2 * (a + b - c) * (a - b + c) - a2 * S;
		case 2068:
			return a * (a + u(b) * u(c));
		case 2069:
			return a * (a - u(b) * u(c));
		case 2070:
			return a2
					* (a8 + a4 * b2 * c2 - 2 * a6 * R - Q * (b4 + c4) + a2
							* (2 * b6 - b4 * c2 - b2 * c4 + 2 * c6));
		case 2071:
			return a2
					* (a8 + 7 * a4 * b2 * c2 - 2 * a6 * R - Q
							* (b4 + 3 * b2 * c2 + c4) + 2 * a2
							* (b6 - 2 * b4 * c2 - 2 * b2 * c4 + c6));
		case 2072:
			return -(T * (p(b2 - c2, 4) + a6 * R - a2 * Q * R - a4 * (b4 + c4)));
		case 2073:
			return a2
					* (a + b)
					* (a + c)
					* U
					* V
					* (b4 + b3 * c + b2 * c2 + b * c3 + c4 + a3 * (b + c) - a2
							* (b2 + b * c + c2) - a
							* (b3 + b2 * c + b * c2 + c3));
		case 2074:
			return a
					* (a + b)
					* (a + c)
					* U
					* V
					* (a3 + b3 + b2 * c + b * c2 + c3 - a2 * (b + c) - a
							* (b2 + b * c + c2));
		case 2075:
			return a2
					* (a + b)
					* (a + c)
					* U
					* V
					* (b5 - a3 * b * c + c5 + a4 * (b + c) + a * b * c
							* (b2 + b * c + c2) - a2
							* (2 * b3 + b2 * c + b * c2 + 2 * c3));
		case 2076:
			return a2 * (a4 - b4 - b2 * c2 - c4 + a2 * R);
		case 2077:
			return a2
					* (a5 - b5 + b3 * c2 + b2 * c3 - c5 - a4 * (b + c) + a3
							* (-2 * b2 + 3 * b * c - 2 * c2) + a2
							* (2 * b3 + b2 * c + b * c2 + 2 * c3) + a
							* (b4 - 3 * b3 * c - 3 * b * c3 + c4));
		case 2078:
			return a2 * (a + b - c) * (a - b + c)
					* (a2 + b2 + b * c + c2 - 2 * a * (b + c));
		case 2079:
			return a2
					* (a8 + 5 * a4 * b2 * c2 - 2 * a6 * R - Q * (b4 + c4) + a2
							* (2 * b6 - 3 * b4 * c2 - 3 * b2 * c4 + 2 * c6));
		case 2080:
			return a2
					* (a6 - 3 * a4 * R + b2 * c2 * R + a2
							* (2 * b4 - b2 * c2 + 2 * c4));
		case 2081:
			return a2 * (b2 - c2) * (a2 - b2 - b * c - c2)
					* (a2 - b2 + b * c - c2) * (-Q + a2 * R);
		case 2082:
			return a * (a2 + p(b - c, 2)) * (a - b - c);
		case 2083:
			return a * T * (a4 + Q);
		case 2084:
			return a3 * (-b4 + c4);
		case 2085:
			return a3 * (b4 + c4);
		case 2086:
			return a2 * p(b - c, 2) * p(b + c, 2) * (a2 - b * c) * (a2 + b * c);
		case 2087:
			return -(a * (2 * a - b - c) * p(b - c, 2));
		case 2088:
			return a2 * p(b - c, 2) * p(b + c, 2) * (a2 - b2 - b * c - c2)
					* (a2 - b2 + b * c - c2);
		case 2089:
			return a
					* ((a + b - c) * c * u(b * (a - b + c)) + b * (a - b + c)
							* u(c * (a + b - c)) - u(a * (-a + b + c))
							* u(b * (a - b + c)) * u(c * (a + b - c)));
		case 2090:
			return (-a + b + c)
					* ((a + b - c) * u(b * (a - b + c)) + (a - b + c)
							* u(c * (a + b - c)));
		case 2091:
			return (a + b - c) * (a - b + c) * u(a)
					* (u(c) * u(a - b + c) + u(b) * u(a + b - c));
		case 2092:
			return a2 * (b + c) * (b2 + c2 + a * (b + c));
		case 2093:
			return a
					* (a3 + 3 * a2 * (b + c) - 3 * p(b - c, 2) * (b + c) - a
							* p(b + c, 2));
		case 2094:
			return 5 * a3 + a2 * (b + c) - p(b - c, 2) * (b + c) + a
					* (-5 * b2 + 6 * b * c - 5 * c2);
		case 2095:
			return a
					* (a6
							+ a5
							* (b + c)
							- 2
							* p(b - c, 4)
							* p(b + c, 2)
							- 2
							* a4
							* (2 * b2 + b * c + 2 * c2)
							- 2
							* a3
							* (b3 + c3)
							+ a2
							* (5 * b4 - 2 * b3 * c + 2 * b2 * c2 - 2 * b * c3 + 5 * c4) + a
							* (b5 - b4 * c - b * c4 + c5));
		case 2096:
			return 3 * a7 - a6 * (b + c) + a4 * p(b - c, 2) * (b + c) + a2
					* p(b - c, 2) * p(b + c, 3) - p(b - c, 4) * p(b + c, 3) - a
					* p(b - c, 2) * p(b + c, 4) + a5
					* (-7 * b2 + 10 * b * c - 7 * c2) + a3 * p(b - c, 2)
					* (5 * b2 + 2 * b * c + 5 * c2);
		case 2097:
			return a
					* (a4 + a2 * p(b - c, 2) + a3 * (b + c) - a * p(b - c, 2)
							* (b + c) - 2 * p(b - c, 2) * R);
		case 2098:
			return a * (a - b - c) * (a2 - 2 * p(b - c, 2) - a * (b + c));
		case 2099:
			return a * (a + b - c) * (a - b + c) * (a - 2 * (b + c));
		default:
			return Double.NaN;
		}
	}

	private double weight2100to2199(int k, double a, double b, double c) {
		switch (k) {
		case 2100:
			return -2
					* a
					* b
					* c
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4)
					+ a
					* (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 - c3)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2101:
			return 2
					* a
					* b
					* c
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4)
					+ a
					* (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 - c3)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2102:
			return a
					* b
					* c
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4)
					+ a
					* (a3 - 2 * a2 * b - a * b2 + 2 * b3 - 2 * a2 * c + 4 * a
							* b * c - 2 * b2 * c - a * c2 - 2 * b * c2 + 2 * c3)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2103:
			return -(a * b * c * (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4))
					+ a
					* (a3 - 2 * a2 * b - a * b2 + 2 * b3 - 2 * a2 * c + 4 * a
							* b * c - 2 * b2 * c - a * c2 - 2 * b * c2 + 2 * c3)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2104:
			return a
					* b
					* c
					* (a2 + b2 + c2)
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4)
					+ a2
					* (a4 - 4 * a2 * b2 + 3 * b4 - 4 * a2 * c2 - 2 * b2 * c2 + 3 * c4)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2105:
			return -(a * b * c * (a2 + b2 + c2) * (2 * a4 - a2 * b2 - b4 - a2
					* c2 + 2 * b2 * c2 - c4))
					+ a2
					* (a4 - 4 * a2 * b2 + 3 * b4 - 4 * a2 * c2 - 2 * b2 * c2 + 3 * c4)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2106:
			return a
					* (a + b)
					* (a + c)
					* (-(b2 * c2) - a * b * c * (b + c) + a2
							* (b2 + b * c + c2));
		case 2107:
			return a2 * (b + c)
					* (a * b * (b - c) * c + b2 * c2 + a2 * (b2 - b * c - c2))
					* (a * b * (b - c) * c - b2 * c2 + a2 * (b2 + b * c - c2));
		case 2108:
			return a
					* (a3 * (b + c) - a2 * (b2 - b * c + c2) + b * c
							* (b2 + b * c + c2) - a
							* (b3 + b2 * c + b * c2 + c3));
		case 2109:
			return a2
					* (a3 * (b - c) + a2 * (b2 + b * c - c2) + b * c
							* (-b2 + b * c + c2) - a
							* (b3 + b2 * c - b * c2 + c3))
					* (a3 * (b - c) + a2 * (b2 - b * c - c2) - b * c
							* (b2 + b * c - c2) + a
							* (b3 - b2 * c + b * c2 + c3));
		case 2110:
			return a2
					* (b2 * c2 * (b + c) - a * b * c * (b2 - b * c + c2) + a3
							* (b2 + b * c + c2) - a2
							* (b3 + b2 * c + b * c2 + c3));
		case 2111:
			return a
					* (b2 * c2 * (-b + c) + a3 * (b2 - b * c - c2) - a * b * c
							* (b2 + b * c - c2) + a2
							* (b3 + b2 * c - b * c2 + c3))
					* (b2 * c2 * (-b + c) + a3 * (b2 + b * c - c2) + a * b * c
							* (-b2 + b * c + c2) - a2
							* (b3 - b2 * c + b * c2 + c3));
		case 2112:
			return a2 * (a4 - a2 * b * c + 2 * b2 * c2 - a * (b3 + c3));
		case 2113:
			return -(a * (a3 * b - b4 + a * b2 * c - 2 * a2 * c2 + b * c3) * (-2
					* a2 * b2 + a3 * c + b3 * c + a * b * c2 - c4));
		case 2114:
			return a
					* (a + b - c)
					* (a - b + c)
					* (a4 + a3 * (b + c) - p(b - c, 2) * (b2 + b * c + c2) - a2
							* (2 * b2 + 3 * b * c + 2 * c2) + a
							* (b3 + b2 * c + b * c2 + c3));
		case 2115:
			return a2
					* (a - b - c)
					* (a4 + b4 - b3 * c + 2 * b2 * c2 - b * c3 - c4 - a3
							* (b + c) + a2 * c * (-b + 2 * c) - a
							* (b3 + b2 * c - 3 * b * c2 + c3))
					* (a4 - b4 + a2 * b * (2 * b - c) - b3 * c + 2 * b2 * c2
							- b * c3 + c4 - a3 * (b + c) - a
							* (b3 - 3 * b2 * c + b * c2 + c3));
		case 2116:
			return a
					* ((b - c) * c + a * (2 * b + c))
					* (b * (-b + c) + a * (b + 2 * c))
					* (-(b2 * p(b - c, 2) * c2) + 2 * a5 * (b + c) - 3 * a3 * b
							* c * (b + c) - a4 * (b2 - b * c + c2) - a * b * c
							* (b3 - 3 * b2 * c - 3 * b * c2 + c3) - a2
							* (b4 - b3 * c + b2 * c2 - b * c3 + c4));
		case 2117:
			return a2
					* (a2 - 2 * b * c - a * (b + c))
					* (a4 * (b2 + b * c + c2) - a3 * c
							* (b2 + 3 * b * c + 2 * c2) + b2 * c
							* (-2 * b3 + b2 * c + c3) - a * b
							* (2 * b4 + b3 * c - 3 * b2 * c2 + b * c3 - c4) + a2
							* (b4 + 3 * b3 * c + b2 * c2 - 3 * b * c3 + c4))
					* (a4 * (b2 + b * c + c2) - a3 * b
							* (2 * b2 + 3 * b * c + c2) + b * c2
							* (b3 + b * c2 - 2 * c3) + a * c
							* (b4 - b3 * c + 3 * b2 * c2 - b * c3 - 2 * c4) + a2
							* (b4 - 3 * b3 * c + b2 * c2 + 3 * b * c3 + c4));
		case 2118:
			return a
					* u(a)
					* ((a + b + c) * u(a) * u(b) * u(c) - (a * b + a * c + b
							* c)
							* (-u(a) + u(b) + u(c)));
		case 2119:
			return a
					* u(a)
					* (-(b3 * c2) - b2 * c3 + a3 * (b2 + 3 * b * c + c2) + a2
							* (-b3 + b2 * c + b * c2 - c3) + a
							* (-(b3 * c) - b2 * c2 - b * c3) - 2 * (a2 - b * c)
							* (a * b + a * c + b * c) * u(b) * u(c));
		case 2120:
			return a2
					* (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2))
					* (a10
							* a8
							- 6
							* a10
							* a6
							* R
							+ 3
							* a10
							* a4
							* (5 * b4 + 8 * b2 * c2 + 5 * c4)
							- p(b2 - c2, 6)
							* (b6 + c6)
							- a10
							* a2
							* (21 * b6 + 37 * b4 * c2 + 37 * b2 * c4 + 21 * c6)
							+ 3
							* a2
							* p(b2 - c2, 4)
							* (2 * b8 - b4 * c4 + 2 * c8)
							+ a6
							* Q
							* (21 * b8 + 22 * b6 * c2 + 24 * b4 * c4 + 22 * b2
									* c6 + 21 * c8)
							+ a10
							* (21 * b8 + 28 * b6 * c2 + 31 * b4 * c4 + 28 * b2
									* c6 + 21 * c8)
							- 3
							* a8
							* (7 * b10 + 2 * b8 * c2 + 3 * b6 * c4 + 3 * b4
									* c6 + 2 * b2 * c8 + 7 * c10) - a4
							* Q
							* (15 * b10 - 5 * b8 * c2 - 6 * b6 * c4 - 6 * b4
									* c6 - 5 * b2 * c8 + 15 * c10));
		case 2121:
			return (-Q + a2 * R)
					* (a10
							* a8
							- 6
							* a10
							* a6
							* R
							- p(b2 - c2, 7)
							* (b4 + b2 * c2 + c4)
							+ 3
							* a10
							* a4
							* (5 * b4 + 8 * b2 * c2 + 5 * c4)
							- a10
							* a2
							* (21 * b6 + 35 * b4 * c2 + 33 * b2 * c4 + 19 * c6)
							+ a2
							* p(b2 - c2, 4)
							* (6 * b8 + b4 * c4 - 6 * c8)
							+ a10
							* (21 * b8 + 20 * b6 * c2 + 19 * b4 * c4 + 12 * b2
									* c6 + 9 * c8)
							- a4
							* p(b2 - c2, 3)
							* (15 * b8 + 8 * b6 * c2 + 10 * b4 * c4 + 12 * b2
									* c6 + 15 * c8)
							+ a8
							* (-21 * b10 + 6 * b8 * c2 - b6 * c4 + b4 * c6 + 6
									* b2 * c8 + 9 * c10) + a6
							* (21 * b10 * b2 - 28 * b10 * c2 + 9 * b8 * c4 + 4
									* b6 * c6 + b4 * c8 + 12 * b2 * c10 - 19
									* c10 * c2))
					* (a10
							* a8
							- 6
							* a10
							* a6
							* R
							+ p(b2 - c2, 7)
							* (b4 + b2 * c2 + c4)
							+ 3
							* a10
							* a4
							* (5 * b4 + 8 * b2 * c2 + 5 * c4)
							- a10
							* a2
							* (19 * b6 + 33 * b4 * c2 + 35 * b2 * c4 + 21 * c6)
							- a2
							* p(b2 - c2, 4)
							* (6 * b8 - b4 * c4 - 6 * c8)
							+ a4
							* p(b2 - c2, 3)
							* (15 * b8 + 12 * b6 * c2 + 10 * b4 * c4 + 8 * b2
									* c6 + 15 * c8)
							+ a10
							* (9 * b8 + 12 * b6 * c2 + 19 * b4 * c4 + 20 * b2
									* c6 + 21 * c8)
							+ a8
							* (9 * b10 + 6 * b8 * c2 + b6 * c4 - b4 * c6 + 6
									* b2 * c8 - 21 * c10) + a6
							* (-19 * b10 * b2 + 12 * b10 * c2 + b8 * c4 + 4
									* b6 * c6 + 9 * b4 * c8 - 28 * b2 * c10 + 21
									* c10 * c2));
		case 2122:
			return a2
					* (a + b - c)
					* (a - b + c)
					* (a6 + 4 * a3 * b * c * (b + c) - 4 * a * b * p(b - c, 2)
							* c * (b + c) + a4 * (-3 * b2 + 4 * b * c - 3 * c2)
							- Q * R + a2
							* (3 * b4 - 4 * b3 * c - 6 * b2 * c2 - 4 * b * c3 + 3 * c4));
		case 2123:
			return (a - b - c)
					* (a6 + 4 * a3 * b * c * (-b + c) + 4 * a * b * (b - c) * c
							* p(b + c, 2) + p(b2 - c2, 3) - a4
							* (b2 - 4 * b * c + 3 * c2) - a2
							* (b4 + 4 * b3 * c - 6 * b2 * c2 + 4 * b * c3 - 3 * c4))
					* (a6 + 4 * a3 * b * (b - c) * c - 4 * a * b * (b - c) * c
							* p(b + c, 2) - p(b2 - c2, 3) - a4
							* (3 * b2 - 4 * b * c + c2) + a2
							* (3 * b4 - 4 * b3 * c + 6 * b2 * c2 - 4 * b * c3 - c4));
		case 2124:
			return a
					* (a + b - c)
					* (a - b + c)
					* (a4 - 4 * a3 * (b + c) - 4 * a * p(b - c, 2) * (b + c)
							+ p(b - c, 2) * (b2 + 6 * b * c + c2) + a2
							* (6 * b2 - 4 * b * c + 6 * c2));
		case 2125:
			return a
					* (a - b - c)
					* (a4 - 4 * a3 * (b - c) + p(b - c, 4) - 4 * a * (b - c)
							* p(b + c, 2) + 2 * a2
							* (3 * b2 + 2 * b * c - 5 * c2))
					* (a4 + 4 * a3 * (b - c) + p(b - c, 4) + 4 * a * (b - c)
							* p(b + c, 2) + a2
							* (-10 * b2 + 4 * b * c + 6 * c2));
		case 2126:
			return a2
					* (a + b)
					* (a + c)
					* (a6 + 2 * a5 * (b + c) + 2 * a3 * b * c * (b + c) - 2 * a
							* p(b + c, 3) * (b2 - b * c + c2) + a4
							* (b2 + 4 * b * c + c2) - p(b + c, 2) * (b4 + c4) - a2
							* (b4 + 2 * b3 * c + b2 * c2 + 2 * b * c3 + c4));
		case 2127:
			return (b + c)
					* (-a6 - 2 * a5 * (b + c) - 2 * a3 * b * c * (b + c) - a4
							* (b2 + 4 * b * c + c2) + 2 * a * p(b + c, 2)
							* (b3 - c3) + p(b + c, 3)
							* (b3 - b2 * c + b * c2 - c3) + a2
							* (b4 + 2 * b3 * c - b2 * c2 - 2 * b * c3 - c4))
					* (a6 + 2 * a5 * (b + c) + 2 * a3 * b * c * (b + c) + a4
							* (b2 + 4 * b * c + c2) + 2 * a * p(b + c, 2)
							* (b3 - c3) + p(b + c, 3)
							* (b3 - b2 * c + b * c2 - c3) + a2
							* (b4 + 2 * b3 * c + b2 * c2 - 2 * b * c3 - c4));
		case 2128:
			return a * T * (a4 + b4 - 6 * b2 * c2 + c4 + 2 * a2 * R);
		case 2129:
			return a * U * V * (a4 + 2 * a2 * (b2 - 3 * c2) + p(b2 + c2, 2))
					* (a4 + p(b2 + c2, 2) + a2 * (-6 * b2 + 2 * c2));
		case 2130:
			return a2
					* T
					* (a4 + b4 + 2 * b2 * c2 - 3 * c4 - 2 * a2 * (b2 - c2))
					* (a4 - 3 * b4 + 2 * b2 * c2 + c4 + 2 * a2 * (b2 - c2))
					* (a10 * a6 - 8 * a10 * a4 * R - 56 * a10 * Q * R - 8 * a2
							* p(b2 - c2, 6) * R + p(b2 - c2, 6)
							* (b4 + 14 * b2 * c2 + c4) + 4 * a10 * a2
							* (7 * b4 - 10 * b2 * c2 + 7 * c4) + 2 * a8 * Q
							* (35 * b4 + 114 * b2 * c2 + 35 * c4) - 8 * a6 * Q
							* (7 * b6 + 25 * b4 * c2 + 25 * b2 * c4 + 7 * c6) + 4
							* a4 * Q * (7 * b8 + 50 * b4 * c4 + 7 * c8));
		case 2131:
			return U
					* V
					* (3 * a4 - Q - 2 * a2 * R)
					* (a10
							* a6
							- 8
							* a10
							* a4
							* (b2 - c2)
							+ p(b2 - c2, 8)
							+ 4
							* a10
							* a2
							* (7 * b4 + 10 * b2 * c2 - 17 * c4)
							- 8
							* a2
							* p(b2 - c2, 3)
							* p(b2 + c2, 2)
							* (b4 + 6 * b2 * c2 + c4)
							- 8
							* a10
							* (7 * b6 + 7 * b4 * c2 + 9 * b2 * c4 - 23 * c6)
							+ a8
							* (70 * b8 - 88 * b6 * c2 + 228 * b4 * c4 + 40 * b2
									* c6 - 250 * c8)
							+ 4
							* a4
							* Q
							* (7 * b8 + 28 * b6 * c2 - 30 * b4 * c4 - 52 * b2
									* c6 - 17 * c8) - 8
							* a6
							* (7 * b10 - 11 * b8 * c2 - 18 * b6 * c4 + 50 * b4
									* c6 - 5 * b2 * c8 - 23 * c10))
					* (a10
							* a6
							+ 8
							* a10
							* a4
							* (b2 - c2)
							+ p(b2 - c2, 8)
							+ 8
							* a2
							* p(b2 - c2, 3)
							* p(b2 + c2, 2)
							* (b4 + 6 * b2 * c2 + c4)
							+ a10
							* a2
							* (-68 * b4 + 40 * b2 * c2 + 28 * c4)
							+ 8
							* a10
							* (23 * b6 - 9 * b4 * c2 - 7 * b2 * c4 - 7 * c6)
							- 4
							* a4
							* Q
							* (17 * b8 + 52 * b6 * c2 + 30 * b4 * c4 - 28 * b2
									* c6 - 7 * c8)
							+ a8
							* (-250 * b8 + 40 * b6 * c2 + 228 * b4 * c4 - 88
									* b2 * c6 + 70 * c8) + 8
							* a6
							* (23 * b10 + 5 * b8 * c2 - 50 * b6 * c4 + 18 * b4
									* c6 + 11 * b2 * c8 - 7 * c10));
		case 2132:
			return a2
					* (a4 - 2 * b4 + b2 * c2 + c4 + a2 * (b2 - 2 * c2))
					* (a4 + b4 + b2 * c2 - 2 * c4 + a2 * (-2 * b2 + c2))
					* (a10
							* a8
							- 6
							* a10
							* a6
							* R
							+ a10
							* a4
							* (15 * b4 + 8 * b2 * c2 + 15 * c4)
							- p(b2 - c2, 6)
							* (b6 + 8 * b4 * c2 + 8 * b2 * c4 + c6)
							- a10
							* a2
							* (21 * b6 + 5 * b4 * c2 + 5 * b2 * c4 + 21 * c6)
							+ a2
							* p(b2 - c2, 4)
							* (6 * b8 + 8 * b6 * c2 - 19 * b4 * c4 + 8 * b2
									* c6 + 6 * c8)
							+ 3
							* a10
							* (7 * b8 + 12 * b6 * c2 - 27 * b4 * c4 + 12 * b2
									* c6 + 7 * c8)
							+ a6
							* Q
							* (21 * b8 + 22 * b6 * c2 + 120 * b4 * c4 + 22 * b2
									* c6 + 21 * c8)
							- 3
							* a4
							* Q
							* (5 * b10 - 7 * b8 * c2 + 14 * b6 * c4 + 14 * b4
									* c6 - 7 * b2 * c8 + 5 * c10) - a8
							* (21 * b10 + 46 * b8 * c2 - 63 * b6 * c4 - 63 * b4
									* c6 + 46 * b2 * c8 + 21 * c10));
		case 2133:
			return (2 * a4 - Q - a2 * R)
					* (a10
							* a8
							+ 2
							* a10
							* a6
							* (b2 - 3 * c2)
							+ p(b2 - c2, 7)
							* (b4 + b2 * c2 + c4)
							+ a10
							* a4
							* (-25 * b4 + 16 * b2 * c2 + 15 * c4)
							+ a10
							* a2
							* (53 * b6 + 15 * b4 * c2 - 51 * b2 * c4 - 21 * c6)
							- a4
							* p(b2 - c2, 3)
							* (25 * b8 + 60 * b6 * c2 + 6 * b4 * c4 - 40 * b2
									* c6 - 15 * c8)
							+ a2
							* p(b2 - c2, 4)
							* (2 * b8 + 24 * b6 * c2 + 33 * b4 * c4 + 16 * b2
									* c6 + 6 * c8)
							+ a10
							* (-31 * b8 - 108 * b6 * c2 + 99 * b4 * c4 + 20
									* b2 * c6 + 21 * c8)
							- a8
							* (31 * b10 - 166 * b8 * c2 + 63 * b6 * c4 + 97
									* b4 * c6 - 46 * b2 * c8 + 21 * c10) + a6
							* (53 * b10 * b2 - 108 * b10 * c2 - 63 * b8 * c4
									+ 196 * b6 * c6 - 63 * b4 * c8 - 36 * b2
									* c10 + 21 * c10 * c2))
					* (a10
							* a8
							+ a10
							* a6
							* (-6 * b2 + 2 * c2)
							+ a10
							* a4
							* (15 * b4 + 16 * b2 * c2 - 25 * c4)
							- p(b2 - c2, 7)
							* (b4 + b2 * c2 + c4)
							+ a10
							* a2
							* (-21 * b6 - 51 * b4 * c2 + 15 * b2 * c4 + 53 * c6)
							+ a10
							* (21 * b8 + 20 * b6 * c2 + 99 * b4 * c4 - 108 * b2
									* c6 - 31 * c8)
							- a4
							* p(b2 - c2, 3)
							* (15 * b8 + 40 * b6 * c2 - 6 * b4 * c4 - 60 * b2
									* c6 - 25 * c8)
							+ a2
							* p(b2 - c2, 4)
							* (6 * b8 + 16 * b6 * c2 + 33 * b4 * c4 + 24 * b2
									* c6 + 2 * c8)
							- a8
							* (21 * b10 - 46 * b8 * c2 + 97 * b6 * c4 + 63 * b4
									* c6 - 166 * b2 * c8 + 31 * c10) + a6
							* (21 * b10 * b2 - 36 * b10 * c2 - 63 * b8 * c4
									+ 196 * b6 * c6 - 63 * b4 * c8 - 108 * b2
									* c10 + 53 * c10 * c2));
		case 2134:
			return a
					* (a + b)
					* (a + c)
					* (a4 - b4 - 2 * b3 * c - b2 * c2 - 2 * b * c3 - c4 + 2
							* a3 * (b + c) - a2 * R - 2 * a
							* (b3 + 2 * b2 * c + 2 * b * c2 + c3));
		case 2135:
			return -(a
					* (b + c)
					* (a4 + b4 + 2 * b3 * c + b2 * c2 - 2 * b * c3 - c4 + 2
							* a3 * (b + c) + a2 * (b2 + 4 * b * c + c2) + 2 * a
							* (b3 + 2 * b2 * c - c3)) * (a4 - b4 - 2 * b3 * c
					+ b2 * c2 + 2 * b * c3 + c4 + 2 * a3 * (b + c) + a2
					* (b2 + 4 * b * c + c2) + a
					* (-2 * b3 + 4 * b * c2 + 2 * c3)));
		case 2136:
			return a * (a - b - c)
					* (a2 + b2 - 6 * b * c + c2 + 2 * a * (b + c));
		case 2137:
			return a * (a + b - c) * (a - b + c)
					* (a2 + 2 * a * (b - 3 * c) + p(b + c, 2))
					* (a2 + p(b + c, 2) + a * (-6 * b + 2 * c));
		case 2138:
			return a2
					* U
					* V
					* (a10 + a2 * p(b2 - c2, 4) - a8 * R - Q * p(b2 + c2, 3)
							- 2 * a6 * (b4 - 6 * b2 * c2 + c4) + 2 * a4
							* (b6 - 3 * b4 * c2 - 3 * b2 * c4 + c6));
		case 2139:
			return T
					* (a10 + a8 * (b2 - c2) - 2 * a6 * Q + p(b2 - c2, 3)
							* p(b2 + c2, 2) - 2 * a4
							* (b6 + 3 * b4 * c2 - 3 * b2 * c4 - c6) + a2
							* (b8 + 4 * b6 * c2 + 6 * b4 * c4 - 12 * b2 * c6 + c8))
					* (a10 - 2 * a6 * Q + a8 * (-b2 + c2) - p(b2 - c2, 3)
							* p(b2 + c2, 2) + 2 * a4
							* (b6 + 3 * b4 * c2 - 3 * b2 * c4 - c6) + a2
							* (b8 - 12 * b6 * c2 + 6 * b4 * c4 + 4 * b2 * c6 + c8));
		case 2140:
			return b * p(b - c, 2) * c + a * p(b - c, 2) * (b + c) - a2 * R;
		case 2141:
			return a2
					* (a * b2 * (b - c) + b2 * (b - c) * c + a3 * (b + c) - a2
							* (2 * b2 + b * c + c2))
					* (a * c2 * (-b + c) + b * c2 * (-b + c) + a3 * (b + c) - a2
							* (b2 + b * c + 2 * c2));
		case 2142:
			return (a - b)
					* (a + b)
					* (a - c)
					* (a + c)
					* (a8 * b2 * c2 + b6 * c6 - a6
							* (b6 + b4 * c2 + b2 * c4 + c6) - a2 * b2 * c2
							* (b6 + b4 * c2 + b2 * c4 + c6) + a4
							* (5 * b6 * c2 - 4 * b4 * c4 + 5 * b2 * c6));
		case 2143:
			return a2
					* (b2 - c2)
					* (a8 * b2 * c2 + b6 * c6 + a4 * b2 * c2
							* (b4 + 4 * b2 * c2 + c4) + a2 * b2 * c2
							* (b6 - 5 * b4 * c2 + b2 * c4 - c6) + a6
							* (-b6 + b4 * c2 - 5 * b2 * c4 + c6))
					* (a8 * b2 * c2 + b6 * c6 + a4 * b2 * c2
							* (b4 + 4 * b2 * c2 + c4) + a6
							* (b6 - 5 * b4 * c2 + b2 * c4 - c6) + a2
							* (-(b8 * c2) + b6 * c4 - 5 * b4 * c6 + b2 * c8));
		case 2144:
			return a2
					* (-b2 + a * c)
					* (a * b - c2)
					* (-5 * a5 * b2 * c2 - 5 * a * b4 * c4 + a6 * (b3 + c3)
							+ a4 * b * c * (b3 + c3) + a2 * b2 * c2 * (b3 + c3)
							+ b3 * c3 * (b3 + c3) - a3
							* (b6 - 4 * b3 * c3 + c6));
		case 2145:
			return a
					* (a2 - b * c)
					* (-(a5 * b2 * c2) - b6 * c3 - a * b4 * c4 + b3 * c6 + a6
							* (b3 - c3) + a4 * (-(b4 * c) + 5 * b * c4) + a2
							* (5 * b5 * c2 - b2 * c5) - a3
							* (b6 + 4 * b3 * c3 + c6))
					* (a5 * b2 * c2 - b6 * c3 + a * b4 * c4 + b3 * c6 + a2 * b2
							* c2 * (b3 - 5 * c3) + a6 * (b3 - c3) + a4
							* (-5 * b4 * c + b * c4) + a3
							* (b6 + 4 * b3 * c3 + c6));
		case 2146:
			return a
					* u(a)
					* (-((b2 - a * c) * (-(a * b) + c2) * u(a)) + (a2 - b * c)
							* (-(a * b) + c2) * u(b) + (b2 - a * c)
							* (a2 - b * c) * u(c));
		case 2147:
			return (a * u(a))
					/ (-(u(a) / (a2 - b * c)) + u(b) / (b2 - a * c) + u(c)
							/ (-(a * b) + c2));
		case 2148:
			return a3 * (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 2149:
			return a3 * p(a - b, 2) * p(a - c, 2) * (a + b - c) * (a - b + c);
		case 2150:
			return a3 * p(a + b, 2) * (a - b - c) * p(a + c, 2);
		case 2151:
			return u(3) * a3 * T - a3 * S;
		case 2152:
			return u(3) * a3 * T + a3 * S;
		case 2153:
			return a * (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4)
					+ u(3) * a3 * S;
		case 2154:
			return -(a * (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4))
					+ u(3) * a3 * S;
		case 2155:
			return a3 * (a4 + b4 + 2 * b2 * c2 - 3 * c4 - 2 * a2 * (b2 - c2))
					* (a4 - 3 * b4 + 2 * b2 * c2 + c4 + 2 * a2 * (b2 - c2));
		case 2156:
			return a * (a8 - p(b4 - c4, 2));
		case 2157:
			return a * (a4 - a2 * b2 + b4 - c4) * (a4 - b4 - a2 * c2 + c4);
		case 2158:
			return a
					* (a8 + 2 * a4 * b4 - 2 * a6 * R + p(b2 - c2, 3) * R - 2
							* a2 * (b6 - c6))
					* (a8 + 2 * a4 * c4 - 2 * a6 * R - p(b2 - c2, 3) * R + 2
							* a2 * (b6 - c6));
		case 2159:
			return a3 * (a4 - 2 * b4 + b2 * c2 + c4 + a2 * (b2 - 2 * c2))
					* (a4 + b4 + b2 * c2 - 2 * c4 + a2 * (-2 * b2 + c2));
		case 2160:
			return a * (a2 + a * b + b2 - c2) * (a2 - b2 + a * c + c2);
		case 2161:
			return a * (a2 - a * b + b2 - c2) * (a2 - b2 - a * c + c2);
		case 2162:
			return a2 * (a * (b - c) - b * c) * (a * (b - c) + b * c);
		case 2163:
			return a2 * (2 * a + 2 * b - c) * (2 * a - b + 2 * c);
		case 2164:
			return a2 * (a3 + a2 * (b - c) - (b - c) * p(b + c, 2) - a * R)
					* (a3 + a2 * (-b + c) + (b - c) * p(b + c, 2) - a * R);
		case 2165:
			return (a4 - 2 * a2 * b2 + Q) * (a4 - 2 * a2 * c2 + Q);
		case 2166:
			return b * c * (a2 - a * b + b2 - c2) * (a2 + a * b + b2 - c2)
					* (-a2 + b2 - a * c - c2) * (-a2 + b2 + a * c - c2);
		case 2167:
			return a * (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 2168:
			return a * (a4 - 2 * a2 * b2 + Q) * (a4 - 2 * a2 * c2 + Q)
					* (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 2169:
			return a3 * T * (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 2170:
			return -(a * (a - b - c) * p(b - c, 2));
		case 2171:
			return -(a * (a + b - c) * (a - b + c) * p(b + c, 2));
		case 2172:
			return a3 * (a4 - b4 - c4);
		case 2173:
			return a * (2 * a4 - Q - a2 * R);
		case 2174:
			return a3 * (a2 - b2 - b * c - c2);
		case 2175:
			return a4 * (a - b - c);
		case 2176:
			return a2 * (-(b * c) + a * (b + c));
		case 2177:
			return a2 * (a - 2 * (b + c));
		case 2178:
			return a2 * (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a * R);
		case 2179:
			return a3 * (-Q + a2 * R);
		case 2180:
			return a3 * (a4 + b4 + c4 - 2 * a2 * R) * (-Q + a2 * R);
		case 2181:
			return a * U * V * (-Q + a2 * R);
		case 2182:
			return a
					* (2 * a4 - a2 * p(b - c, 2) - a3 * (b + c) + a
							* p(b - c, 2) * (b + c) - Q);
		case 2183:
			return a2 * (-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 2184:
			return a * (a4 + b4 + 2 * b2 * c2 - 3 * c4 - 2 * a2 * (b2 - c2))
					* (a4 - 3 * b4 + 2 * b2 * c2 + c4 + 2 * a2 * (b2 - c2));
		case 2185:
			return a * p(a + b, 2) * (a - b - c) * p(a + c, 2);
		case 2186:
			return -(a * (c2 * (b2 - c2) + a2 * (2 * b2 + c2)) * (-b4 + b2 * c2 + a2
					* (b2 + 2 * c2)));
		case 2187:
			return a3
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2));
		case 2188:
			return a3
					* (a - b - c)
					* T
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2))
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 2189:
			return a2 * p(a + b, 2) * (a - b - c) * p(a + c, 2) * U * V;
		case 2190:
			return a * U * V * (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 2191:
			return a * (a2 - 2 * a * b + p(b - c, 2))
					* (a2 + p(b - c, 2) - 2 * a * c);
		case 2192:
			return a2
					* (a - b - c)
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2))
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 2193:
			return a3 * (a + b) * (a - b - c) * (a + c) * T;
		case 2194:
			return a3 * (a + b) * (a - b - c) * (a + c);
		case 2195:
			return a2 * (a - b - c) * (a2 + b * (b - c) - a * c)
					* (a2 - a * b + c * (-b + c));
		case 2196:
			return a3 * (-b2 + a * c) * (a * b - c2) * T;
		case 2197:
			return a2 * (a + b - c) * (a - b + c) * p(b + c, 2) * T;
		case 2198:
			return a3 * (b + c) * (-b3 + a * b * c - c3 + a2 * (b + c));
		case 2199:
			return a3
					* (a + b - c)
					* (a - b + c)
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2));
		default:
			return Double.NaN;
		}
	}

	private double weight2200to2299(int k, double a, double b, double c) {
		switch (k) {
		case 2200:
			return a4 * (b + c) * T;
		case 2201:
			return a * (a2 - b * c) * U * V;
		case 2202:
			return a * (a - b - c) * U * V
					* (a4 - b * p(b - c, 2) * c - a2 * (b2 - b * c + c2));
		case 2203:
			return a3 * (a + b) * (a + c) * U * V;
		case 2204:
			return a3 * (a + b) * (a - b - c) * (a + c) * U * V;
		case 2205:
			return a5 * (b + c);
		case 2206:
			return a4 * (a + b) * (a + c);
		case 2207:
			return a2 * p(a4 - Q, 2);
		case 2208:
			return a3
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2))
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 2209:
			return a3 * (-(b * c) + a * (b + c));
		case 2210:
			return a5 - a3 * b * c;
		case 2211:
			return a4 * U * V * (-b4 - c4 + a2 * R);
		case 2212:
			return a3 * (a - b - c) * U * V;
		case 2213:
			return a2
					* (a + b - c)
					* (a - b + c)
					* (a3 + 3 * b3 + a2 * (b - c) + 3 * b2 * c + b * c2 + c3 + a
							* (3 * b2 + 2 * b * c - c2))
					* (a3 + b3 + b2 * c + 3 * b * c2 + 3 * c3 + a2 * (-b + c) + a
							* (-b2 + 2 * b * c + 3 * c2));
		case 2214:
			return a * (a2 + a * (b + c) + b * (b + c))
					* (a2 + a * (b + c) + c * (b + c));
		case 2215:
			return a2 * (-b3 + b * c2 + 2 * a * c * (b + c) + a2 * (b + 2 * c))
					* (2 * a * b * (b + c) + a2 * (2 * b + c) + c * (b2 - c2));
		case 2216:
			return a
					* (a6 - a4 * (2 * b2 + c2) + p(-(b2 * c) + c3, 2) + a2
							* (b4 - 2 * b2 * c2 - c4))
					* (a6 - a4 * (b2 + 2 * c2) + p(b3 - b * c2, 2) + a2
							* (-b4 - 2 * b2 * c2 + c4));
		case 2217:
			return a * (a3 + b3 + a * (b - c) * c - b * c2)
					* (a3 - b2 * c + c3 + a * b * (-b + c));
		case 2218:
			return a * (a3 - b2 * c + c3 - a * b * (b + c))
					* (a3 + b3 - b * c2 - a * c * (b + c));
		case 2219:
			return a
					* (a5 + a * b * (b - c) * p(b + c, 2) - a2 * c
							* p(b + c, 2) + c * Q - a3 * (2 * b2 + b * c + c2))
					* (a5 - a2 * b * p(b + c, 2) - a * (b - c) * c
							* p(b + c, 2) + b * Q - a3 * (b2 + b * c + 2 * c2));
		case 2220:
			return a3 * (a2 - b * c + a * (b + c));
		case 2221:
			return a2 * (a2 + 2 * a * b + b2 + c2) * (a2 + b2 + 2 * a * c + c2);
		case 2222:
			return a * (a - b) * (a - c) * (a + b - c) * (a - b + c)
					* (a2 - a * b + b2 - c2) * (a2 - b2 - a * c + c2);
		case 2223:
			return a3 * (-b2 - c2 + a * (b + c));
		case 2224:
			return a * (a3 + b2 * (b - c) - a2 * c)
					* (a3 - a2 * b + c2 * (-b + c));
		case 2225:
			return a3 * (-b3 - c3 + a * R);
		case 2226:
			return a2 * p(a + b - 2 * c, 2) * p(a - 2 * b + c, 2);
		case 2227:
			return a * (-(b2 * c2 * R) + a2 * (b4 + c4));
		case 2228:
			return a * (-(b * c * R) + a * (b3 + c3));
		case 2229:
			return -(a * b2 * c2 * (b + c)) + a3 * (b3 + c3);
		case 2230:
			return -2 * a * b3 * c3 + a4 * (b3 + c3);
		case 2231:
			return a * (-(b3 * c3 * (b + c)) + a4 * (b3 + c3));
		case 2232:
			return a * (-(b3 * c3 * R) + a5 * (b3 + c3));
		case 2233:
			return a * (b3 + c3) * (a6 - b3 * c3);
		case 2234:
			return -2 * a * b2 * c2 + a3 * R;
		case 2235:
			return a * (-(b2 * c2 * (b + c)) + a3 * R);
		case 2236:
			return a * (a2 - b * c) * (a2 + b * c) * R;
		case 2237:
			return a * (a5 * R - b2 * c2 * (b3 + c3));
		case 2238:
			return a * (b + c) * (a2 - b * c);
		case 2239:
			return a * (a3 * (b + c) - b * c * R);
		case 2240:
			return a * (a4 * (b + c) - b * c * (b3 + c3));
		case 2241:
			return a4 - 2 * a2 * b * c;
		case 2242:
			return a4 + 2 * a2 * b * c;
		case 2243:
			return a * (2 * a3 - b3 - c3);
		case 2244:
			return a * (2 * a4 - b4 - c4);
		case 2245:
			return a2 * (b + c) * (a2 - b2 + b * c - c2);
		case 2246:
			return a
					* (2 * a3 - 2 * a2 * (b + c) - p(b - c, 2) * (b + c) + a
							* R);
		case 2247:
			return a * (2 * a6 - 2 * a4 * R - Q * R + a2 * (b4 + c4));
		case 2248:
			return a2 * (a2 + b2 + b * c - c2 + a * (b + c))
					* (a2 - b2 + b * c + c2 + a * (b + c));
		case 2249:
			return a2
					* (a + b)
					* (a + c)
					* (a3 * b + c2 * (b2 - c2) + a2 * (-2 * b2 + c2) + a
							* (b3 - b * c2))
					* (-b4 + a3 * c + b2 * c2 + a2 * (b2 - 2 * c2) + a
							* (-(b2 * c) + c3));
		case 2250:
			return a * (b + c) * (a3 - a2 * b + b3 - a * p(b - c, 2) - b * c2)
					* (a3 - a * p(b - c, 2) - a2 * c - b2 * c + c3);
		case 2251:
			return a3 * (2 * a - b - c);
		case 2252:
			return a2 * T
					* (a3 * (b + c) - a * p(b - c, 2) * (b + c) + Q - a2 * R);
		case 2253:
			return a3
					* T
					* (b5 - b3 * c2 - b2 * c3 + c5 - a * Q + a3 * R - a2
							* (b3 + c3));
		case 2254:
			return a * (b - c) * (-b2 - c2 + a * (b + c));
		case 2255:
			return a2
					* (a3 + a2 * (3 * b - c) + p(b - c, 2) * (b + c) + a
							* (3 * b2 + 2 * b * c - c2))
					* (a3 - a2 * (b - 3 * c) + p(b - c, 2) * (b + c) + a
							* (-b2 + 2 * b * c + 3 * c2));
		case 2256:
			return a2 * (a3 - a * p(b - c, 2) - a2 * (b + c) + p(b + c, 3));
		case 2257:
			return a
					* (a4 - 4 * a2 * b * c - 2 * a3 * (b + c) + 2 * a
							* p(b - c, 2) * (b + c) - Q);
		case 2258:
			return a2 * (c * (b + c) + a * (2 * b + c))
					* (b * (b + c) + a * (b + 2 * c));
		case 2259:
			return a2 * (a3 - a2 * b + b3 - b * c2 - a * p(b + c, 2))
					* (a3 - a2 * c - b2 * c + c3 - a * p(b + c, 2));
		case 2260:
			return a2 * (2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 2261:
			return a
					* (3 * a4 - 2 * a2 * p(b - c, 2) - 2 * a3 * (b + c) + 2 * a
							* p(b - c, 2) * (b + c) - Q);
		case 2262:
			return -(a * (a2 * p(b - c, 2) + a3 * (b + c) - a * p(b - c, 2)
					* (b + c) - Q));
		case 2263:
			return a
					* (a + b - c)
					* (a - b + c)
					* (a3 - a2 * (b + c) - p(b - c, 2) * (b + c) + a
							* p(b + c, 2));
		case 2264:
			return a * (a - b - c)
					* (2 * a3 + a2 * (b + c) + p(b - c, 2) * (b + c));
		case 2265:
			return a
					* (2 * a4 - 2 * a3 * (b + c) + 2 * a * p(b - c, 2)
							* (b + c) - Q - a2 * (b2 - 4 * b * c + c2));
		case 2266:
			return a2
					* (a5 - 2 * a4 * (b + c) - 2 * b * p(b - c, 2) * c
							* (b + c) - a * Q + 2 * a2
							* (b3 + 2 * b2 * c + 2 * b * c2 + c3));
		case 2267:
			return a2 * (a3 - a * p(b - c, 2) - 2 * b * c * (b + c));
		case 2268:
			return a2 * (a - b - c) * (a2 + 2 * b * c + a * (b + c));
		case 2269:
			return a2 * (a - b - c) * (b2 + c2 + a * (b + c));
		case 2270:
			return a
					* (a4 - 4 * a2 * b * c + 2 * a3 * (b + c) - 2 * a
							* p(b - c, 2) * (b + c) - Q);
		case 2271:
			return a2 * (a2 - 2 * a * (b + c) - p(b + c, 2));
		case 2272:
			return a2
					* (a4 * (b + c) - p(b - c, 2) * p(b + c, 3) - 2 * a3
							* (b2 - b * c + c2) + 2 * a * p(b - c, 2)
							* (b2 + b * c + c2));
		case 2273:
			return a2 * (a3 + b3 + b2 * c + b * c2 + c3);
		case 2274:
			return a2
					* (b * c * R + a2 * (b2 + b * c + c2) + a
							* (b3 + b2 * c + b * c2 + c3));
		case 2275:
			return a2 * (b2 - b * c + c2);
		case 2276:
			return a2 * (b2 + b * c + c2);
		case 2277:
			return a2 * (b3 + c3 + a * (b2 + b * c + c2));
		case 2278:
			return a2 * (a3 - b * c * (b + c) - a * R);
		case 2279:
			return a2 * ((b - c) * c + a * (2 * b + c))
					* (b * (-b + c) + a * (b + 2 * c));
		case 2280:
			return a2 * (a2 - 2 * b * c - a * (b + c));
		case 2281:
			return a3 * (b + c) * (a2 + 2 * a * b + b2 + c2)
					* (a2 + b2 + 2 * a * c + c2);
		case 2282:
			return a
					* (-(b3 * c) + b * c3 + a3 * (b + c) - a * (b - c)
							* p(b + c, 2) + a2 * c * (b + 2 * c))
					* (a3 * (b + c) + a * (b - c) * p(b + c, 2) + a2 * b
							* (2 * b + c) + b * c * (b2 - c2));
		case 2283:
			return a2 * (a - b) * (a - c) * (a + b - c) * (a - b + c)
					* (-b2 - c2 + a * (b + c));
		case 2284:
			return a2 * (a - b) * (a - c) * (-b2 - c2 + a * (b + c));
		case 2285:
			return a * (a + b - c) * (a - b + c) * (a2 + p(b + c, 2));
		case 2286:
			return a2 * (a + b - c) * (a - b + c) * T * (a2 + p(b + c, 2));
		case 2287:
			return a * (a + b) * (a + c) * p(-a + b + c, 2);
		case 2288:
			return a3
					* (b5 - b4 * c - 4 * a * b2 * c2 - b * c4 + c5 + a4
							* (b + c) - 2 * a2 * (b3 + c3));
		case 2289:
			return a3 * (a - b - c) * (T * T);
		case 2290:
			return a3 * (a2 - b2 - b * c - c2) * (a2 - b2 + b * c - c2)
					* (-Q + a2 * R);
		case 2291:
			return a2 * (a2 - 2 * b2 + a * (b - 2 * c) + b * c + c2)
					* (a2 + b2 + b * c - 2 * c2 + a * (-2 * b + c));
		case 2292:
			return a * (b + c) * (b2 + c2 + a * (b + c));
		case 2293:
			return a2 * (a - b - c) * (-p(b - c, 2) + a * (b + c));
		case 2294:
			return -(a * (b + c) * (2 * a * b * c + a2 * (b + c) - p(b - c, 2)
					* (b + c)));
		case 2295:
			return a * (b + c) * (a2 + b * c);
		case 2296:
			return (b2 * c + a2 * (b + c) + a * b * (b + c))
					* (b * c2 + a2 * (b + c) + a * c * (b + c));
		case 2297:
			return a * (a2 - 2 * a * (b - c) + p(b + c, 2))
					* (a2 + 2 * a * (b - c) + p(b + c, 2));
		case 2298:
			return a * (a2 + a * c + b * (b + c)) * (a2 + a * b + c * (b + c));
		case 2299:
			return a2 * (a + b) * (a - b - c) * (a + c) * U * V;
		default:
			return Double.NaN;
		}
	}

	private double weight2300to2399(int k, double a, double b, double c) {
		switch (k) {
		case 2300:
			return a3 * (b2 + c2 + a * (b + c));
		case 2301:
			return a2
					* (a5 - a3 * b * c - 2 * a4 * (b + c) + b * p(b - c, 2) * c
							* (b + c) - a * p(b + c, 2) * (b2 - 3 * b * c + c2) + a2
							* (2 * b3 + b2 * c + b * c2 + 2 * c3));
		case 2302:
			return a2
					* (a6 - a5 * (b + c) - b * c * Q + a2 * p(b + c, 2) * R
							- a4 * (2 * b2 + b * c + 2 * c2) + 2 * a3
							* (b3 + c3) - a * (b5 - b4 * c - b * c4 + c5));
		case 2303:
			return a * (a + b) * (a + c) * (a2 + p(b + c, 2));
		case 2304:
			return a3
					* (a2 * b * c + a3 * (b + c) - b * c * p(b + c, 2) - a
							* (b3 + b2 * c + b * c2 + c3));
		case 2305:
			return a2 * (a3 - b3 + a * b * c - c3 + 2 * a2 * (b + c));
		case 2306:
			return a / (u(3) * (-a2 + p(b + c, 2)) - S);
		case 2307:
			return u(3) * a2 * (a - b - c) * (a + b - c) * (a - b + c)
					* (a + b + c) + a2 * (a + b - c) * (a - b + c) * S;
		case 2308:
			return a2 * (2 * a + b + c);
		case 2309:
			return a2 * (b * c * (b + c) + a * R);
		case 2310:
			return a * p(b - c, 2) * p(-a + b + c, 2);
		case 2311:
			return a2 * (a + b) * (a - b - c) * (a + c) * (-b2 + a * c)
					* (a * b - c2);
		case 2312:
			return a * (2 * a6 - a4 * R - Q * R);
		case 2313:
			return a
					* (-Q + a2 * R)
					* (a8 + b2 * c2 * Q - 2 * a6 * R + a4 * (b4 + b2 * c2 + c4));
		case 2314:
			return a
					* T
					* (2 * a8 + p(b2 - c2, 4) - 3 * a6 * R - a2 * Q * R + a4
							* p(b2 + c2, 2));
		case 2315:
			return a3 * T * (a4 * R + Q * R - 2 * a2 * (b4 - b2 * c2 + c4));
		case 2316:
			return a2 * (a + b - 2 * c) * (a - b - c) * (a - 2 * b + c);
		case 2317:
			return a2
					* (2 * a3 - a2 * (b + c) + p(b - c, 2) * (b + c) - 2 * a
							* (b2 - b * c + c2));
		case 2318:
			return a2 * (a - b - c) * (b + c) * T;
		case 2319:
			return a * (a - b - c) * (a * (b - c) - b * c)
					* (a * (b - c) + b * c);
		case 2320:
			return a * (a - b - c) * (2 * a + 2 * b - c) * (2 * a - b + 2 * c);
		case 2321:
			return -((a - b - c) * (b + c));
		case 2322:
			return (a + b) * (a + c) * p(-a + b + c, 2) * U * V;
		case 2323:
			return a2 * (a - b - c) * (a2 - b2 + b * c - c2);
		case 2324:
			return a
					* (a - b - c)
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2));
		case 2325:
			return (a - b - c) * (2 * a - b - c);
		case 2326:
			return a * p(a + b, 2) * p(a + c, 2) * p(-a + b + c, 2) * U * V;
		case 2327:
			return a2 * (a + b) * (a + c) * p(-a + b + c, 2) * T;
		case 2328:
			return a2 * (a + b) * (a + c) * p(-a + b + c, 2);
		case 2329:
			return a * (a - b - c) * (a2 + b * c);
		case 2330:
			return a2 * (a - b - c) * (a2 + b * c);
		case 2331:
			return a
					* U
					* V
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2));
		case 2332:
			return a2 * (a + b) * (a + c) * p(-a + b + c, 2) * U * V;
		case 2333:
			return a2 * (b + c) * U * V;
		case 2334:
			return a2 * (a + 3 * b + c) * (a + b + 3 * c);
		case 2335:
			return a * (a - b - c)
					* (-b3 + b * c2 + 2 * a * c * (b + c) + a2 * (b + 2 * c))
					* (2 * a * b * (b + c) + a2 * (2 * b + c) + c * (b2 - c2));
		case 2336:
			return a2
					* (a3 + 3 * b3 + a2 * (b - c) + 3 * b2 * c + b * c2 + c3 + a
							* (3 * b2 + 2 * b * c - c2))
					* (a3 + b3 + b2 * c + 3 * b * c2 + 3 * c3 + a2 * (-b + c) + a
							* (-b2 + 2 * b * c + 3 * c2));
		case 2337:
			return a2 * (a - b - c) * (a4 - 2 * a * b2 * c + Q - 2 * a2 * R)
					* (a4 - 2 * a * b * c2 + Q - 2 * a2 * R);
		case 2338:
			return a2 * (a - b - c)
					* (a3 - 2 * b3 - a2 * c + b2 * c + c3 + a * (b2 - c2))
					* (a3 - a2 * b + b3 + b * c2 - 2 * c3 + a * (-b2 + c2));
		case 2339:
			return a * (a - b - c) * (a2 + 2 * a * b + b2 + c2)
					* (a2 + b2 + 2 * a * c + c2);
		case 2340:
			return a2 * (a - b - c) * (-b2 - c2 + a * (b + c));
		case 2341:
			return a * (a + b) * (a - b - c) * (a + c) * (a2 - a * b + b2 - c2)
					* (a2 - b2 - a * c + c2);
		case 2342:
			return a2 * (a - b - c)
					* (a3 - a2 * b + b3 - a * p(b - c, 2) - b * c2)
					* (a3 - a * p(b - c, 2) - a2 * c - b2 * c + c3);
		case 2343:
			return a2
					* (a - b - c)
					* (a4 - 2 * a3 * b + 2 * a2 * (b - c) * c - p(b - c, 3)
							* (b + c) + 2 * a * b * p(b + c, 2))
					* (a4 - 2 * a2 * b * (b - c) - 2 * a3 * c + p(b - c, 3)
							* (b + c) + 2 * a * c * p(b + c, 2));
		case 2344:
			return a * (a2 + a * b + b2) * (a - b - c) * (a2 + a * c + c2);
		case 2345:
			return a2 + p(b + c, 2);
		case 2346:
			return a * (a2 + b * (b - c) - a * (2 * b + c))
					* (a2 + c * (-b + c) - a * (b + 2 * c));
		case 2347:
			return a2 * (a - b - c) * (p(b - c, 2) + a * (b + c));
		case 2348:
			return a * (a - b - c) * (2 * a2 + p(b - c, 2) - a * (b + c));
		case 2349:
			return a * (a4 - 2 * b4 + b2 * c2 + c4 + a2 * (b2 - 2 * c2))
					* (a4 + b4 + b2 * c2 - 2 * c4 + a2 * (-2 * b2 + c2));
		case 2350:
			return a2 * ((b - c) * c + a * (b + c))
					* (b * (-b + c) + a * (b + c));
		case 2351:
			return a2 * T * (a4 - 2 * a2 * b2 + Q) * (a4 - 2 * a2 * c2 + Q);
		case 2352:
			return a3 * (-b3 + a * b * c - c3 + a2 * (b + c));
		case 2353:
			return a2 * (a4 + b4 - c4) * (a4 - b4 + c4);
		case 2354:
			return a2 * U * V * (b2 + c2 + a * (b + c));
		case 2355:
			return a * (2 * a + b + c) * U * V;
		case 2356:
			return a2 * U * V * (-b2 - c2 + a * (b + c));
		case 2357:
			return a2
					* (b + c)
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2))
					* (a3 - a * p(b - c, 2) + a2 * (-b + c) + (b - c)
							* p(b + c, 2));
		case 2358:
			return -(a
					* (a + b - c)
					* (a - b + c)
					* (b + c)
					* U
					* V
					* (a3 + a2 * (b - c) - a * p(b - c, 2) - (b - c)
							* p(b + c, 2)) * (a3 - a * p(b - c, 2) + a2
					* (-b + c) + (b - c) * p(b + c, 2)));
		case 2359:
			return a2 * T * (a2 + a * c + b * (b + c))
					* (a2 + a * b + c * (b + c));
		case 2360:
			return a2
					* (a + b)
					* (a + c)
					* (a3 + a2 * (b + c) - p(b - c, 2) * (b + c) - a
							* p(b + c, 2));
		case 2361:
			return a3 * (a - b - c) * (a2 - b2 + b * c - c2);
		case 2362:
			return a * (a2 + 2 * a * b + b2 - c2 + S)
					* (a2 - b2 + 2 * a * c + c2 + S);
		case 2363:
			return a * (a + b) * (a + c) * (a2 + a * c + b * (b + c))
					* (a2 + a * b + c * (b + c));
		case 2364:
			return a2 * (a - b - c) * (2 * a + 2 * b - c) * (2 * a - b + 2 * c);
		case 2365:
			return a2
					* (a6 - 2 * b6 + a5 * (b - 2 * c) + b5 * c + a4 * (b - c)
							* c + b4 * c2 - 2 * b3 * c3 + b * c5 + c6 + a2
							* (b - c) * p(b + c, 3) - 2 * a3
							* (b3 + b * c2 - 2 * c3) + a
							* (b5 - 2 * b4 * c + 2 * b3 * c2 + b * c4 - 2 * c5))
					* (a6 + b6 + b5 * c - 2 * b3 * c3 + b2 * c4 + b * c5 - 2
							* c6 + a5 * (-2 * b + c) + a4 * b * (-b + c) - a2
							* (b - c) * p(b + c, 3) + a3
							* (4 * b3 - 2 * b2 * c - 2 * c3) + a
							* (-2 * b5 + b4 * c + 2 * b2 * c3 - 2 * b * c4 + c5));
		case 2366:
			return (a8 + a6 * (-2 * b2 + c2) + b2 * (b2 - c2) * p(b2 + c2, 2)
					+ a4 * (2 * b4 - b2 * c2 - c4) - a2
					* (2 * b6 + b4 * c2 - 4 * b2 * c4 + c6))
					* (a8 + a6 * (b2 - 2 * c2) + c2 * (-b2 + c2)
							* p(b2 + c2, 2) - a4 * (b4 + b2 * c2 - 2 * c4) - a2
							* (b6 - 4 * b4 * c2 + b2 * c4 + 2 * c6));
		case 2367:
			return -(b2 * c2 * (a6 + b6 - a4 * c2 - b4 * c2) * (-a6 + a4 * b2
					+ b2 * c4 - c6));
		case 2368:
			return (a + b)
					* (a + c)
					* (a * c2 * (-b + c) + b * c2 * (-b + c) + a3 * (b + c) - a2
							* b * (b + c))
					* (a * b2 * (b - c) + b2 * (b - c) * c + a3 * (b + c) - a2
							* c * (b + c));
		case 2369:
			return (a + b - c)
					* (a - b + c)
					* (a4 + a * (b - c) * c2 + p(b - c, 2) * c2 + a2 * b
							* (b + c) - a3 * (2 * b + c))
					* (a4 + b2 * p(b - c, 2) + a * b2 * (-b + c) + a2 * c
							* (b + c) - a3 * (b + 2 * c));
		case 2370:
			return (a5 + a4 * (-2 * b + c) + b2 * (b - c) * p(b + c, 2) + a3
					* (b2 - c2) - 2 * a * (b4 - b2 * c2) + a2
					* (b3 - 2 * b2 * c + 2 * b * c2 - c3))
					* (a5 + a4 * (b - 2 * c) - (b - c) * c2 * p(b + c, 2) + 2
							* a * c2 * (b2 - c2) + a3 * (-b2 + c2) + a2
							* (-b3 + 2 * b2 * c - 2 * b * c2 + c3));
		case 2371:
			return a2
					* (a4 - 2 * b4 + a3 * (3 * b - 4 * c) + b3 * c - 3 * b2
							* c2 + 3 * b * c3 + c4 - 3 * a2
							* (b2 + b * c - 2 * c2) + a
							* (b3 + 6 * b2 * c - 3 * b * c2 - 4 * c3))
					* (a4 + b4 + 3 * b3 * c - 3 * b2 * c2 + b * c3 - 2 * c4
							+ a3 * (-4 * b + 3 * c) + a2
							* (6 * b2 - 3 * b * c - 3 * c2) + a
							* (-4 * b3 - 3 * b2 * c + 6 * b * c2 + c3));
		case 2372:
			return (a5 + a4 * c - a3 * c2 - a2 * c3 + b2 * (b - c)
					* p(b + c, 2))
					* (a5 + a4 * b - a3 * b2 - a2 * b3 - (b - c) * c2
							* p(b + c, 2));
		case 2373:
			return (a6 - a4 * b2 + b6 - b2 * c4 - a2 * Q)
					* (a6 - a4 * c2 - b4 * c2 + c6 - a2 * Q);
		case 2374:
			return U * V * (a4 + a2 * (-4 * b2 + c2) + b2 * R)
					* (a4 + a2 * (b2 - 4 * c2) + c2 * R);
		case 2375:
			return a2
					* (a3 * b + a2 * b * c - b * c3 + a
							* (b3 + b2 * c - 2 * b * c2 - c3))
					* (a3 * c + a2 * b * c - b3 * c + a
							* (-b3 - 2 * b2 * c + b * c2 + c3));
		case 2376:
			return a
					* (a + b - c)
					* (a - b + c)
					* U
					* V
					* (a5 - p(b - c, 3) * c * (b + c) - a4 * (2 * b + c) + 2
							* a2 * (b3 + 2 * b * c2) - a
							* (b4 + 2 * b3 * c + c4))
					* (a5 + b * p(b - c, 3) * (b + c) - a4 * (b + 2 * c) + 2
							* a2 * (2 * b2 * c + c3) - a
							* (b4 + 2 * b * c3 + c4));
		case 2377:
			return a
					* p(a + b - c, 2)
					* p(a - b + c, 2)
					* (a4 + b * p(b - c, 3) - a3 * (2 * b + 3 * c) + a2
							* (2 * b2 + 3 * b * c + 3 * c2) - a
							* (2 * b3 - 3 * b2 * c + 4 * b * c2 + c3))
					* (a4 - p(b - c, 3) * c - a3 * (3 * b + 2 * c) + a2
							* (3 * b2 + 3 * b * c + 2 * c2) - a
							* (b3 + 4 * b2 * c - 3 * b * c2 + 2 * c3));
		case 2378:
			return a2
					* (a8 - 5 * a4 * b4 + 6 * a2 * b6 - 2 * b8 + 7 * a4 * b2
							* c2 - 5 * a2 * b4 * c2 - 6 * b6 * c2 - 5 * a4 * c4
							- 5 * a2 * b2 * c4 + 16 * b4 * c4 + 6 * a2 * c6 - 6
							* b2 * c6 - 2 * c8)
					+ u(3)
					* a2
					* (a6 - a4 * b2 + 2 * a2 * b4 - 2 * b6 - a4 * c2 - 3 * a2
							* b2 * c2 + 2 * b4 * c2 + 2 * a2 * c4 + 2 * b2 * c4 - 2 * c6)
					* S;
		case 2379:
			return -(a2 * (a8 - 5 * a4 * b4 + 6 * a2 * b6 - 2 * b8 + 7 * a4
					* b2 * c2 - 5 * a2 * b4 * c2 - 6 * b6 * c2 - 5 * a4 * c4
					- 5 * a2 * b2 * c4 + 16 * b4 * c4 + 6 * a2 * c6 - 6 * b2
					* c6 - 2 * c8))
					+ u(3)
					* a2
					* (a6 - a4 * b2 + 2 * a2 * b4 - 2 * b6 - a4 * c2 - 3 * a2
							* b2 * c2 + 2 * b4 * c2 + 2 * a2 * c4 + 2 * b2 * c4 - 2 * c6)
					* S;
		case 2380:
			return -(a2 * (a8 - 4 * a6 * b2 + 3 * a4 * b4 + 2 * a2 * b6 - 2
					* b8 - 4 * a6 * c2 + 7 * a4 * b2 * c2 - 5 * a2 * b4 * c2
					+ 14 * b6 * c2 + 3 * a4 * c4 - 5 * a2 * b2 * c4 - 24 * b4
					* c4 + 2 * a2 * c6 + 14 * b2 * c6 - 2 * c8))
					+ u(3)
					* a2
					* (a6 - a4 * b2 + 2 * a2 * b4 - 2 * b6 - a4 * c2 - 3 * a2
							* b2 * c2 + 2 * b4 * c2 + 2 * a2 * c4 + 2 * b2 * c4 - 2 * c6)
					* S;
		case 2381:
			return a2
					* (a8 - 4 * a6 * b2 + 3 * a4 * b4 + 2 * a2 * b6 - 2 * b8
							- 4 * a6 * c2 + 7 * a4 * b2 * c2 - 5 * a2 * b4 * c2
							+ 14 * b6 * c2 + 3 * a4 * c4 - 5 * a2 * b2 * c4
							- 24 * b4 * c4 + 2 * a2 * c6 + 14 * b2 * c6 - 2 * c8)
					+ u(3)
					* a2
					* (a6 - a4 * b2 + 2 * a2 * b4 - 2 * b6 - a4 * c2 - 3 * a2
							* b2 * c2 + 2 * b4 * c2 + 2 * a2 * c4 + 2 * b2 * c4 - 2 * c6)
					* S;
		case 2382:
			return a2 * (a2 * (2 * b - c) - b * (b - 2 * c) * c - a * R)
					* (a2 * (b - 2 * c) + b * c * (-2 * b + c) + a * R);
		case 2383:
			return a2
					* U
					* V
					* (a8 - 2 * a6 * (2 * b2 + c2) + Q * (b4 + 2 * c4) + a4
							* (6 * b4 + 2 * b2 * c2 + 3 * c4) + a2
							* (-4 * b6 + 2 * b4 * c2 - 4 * c6))
					* (a8 - 2 * a6 * (b2 + 2 * c2) + Q * (2 * b4 + c4) + a4
							* (3 * b4 + 2 * b2 * c2 + 6 * c4) + a2
							* (-4 * b6 + 2 * b2 * c4 - 4 * c6));
		case 2384:
			return a2 * (a2 - 4 * a * b + b2 + 2 * a * c + 2 * b * c - 2 * c2)
					* (a2 - 2 * b2 + 2 * a * (b - 2 * c) + 2 * b * c + c2);
		case 2385:
			return 2 * a6 - a5 * (b + c) - a4 * p(b - c, 2) + 2 * a3
					* p(b - c, 2) * (b + c) - a * p(b - c, 2) * p(b + c, 3)
					- p(b - c, 4) * p(b + c, 2);
		case 2386:
			return a2
					* (a6 * R - a2 * Q * R - Q * (b4 + c4) + a4
							* (b4 - 4 * b2 * c2 + c4));
		case 2387:
			return a4 * (-b6 - c6 + a2 * (b4 + c4));
		case 2388:
			return a2
					* (b + c)
					* (a2 * R + a * (-b3 + b2 * c + b * c2 - c3) - b3 * c - b
							* c3);
		case 2389:
			return a2
					* (-a + b + c)
					* (a2 * R + a * (-2 * b3 + b2 * c + b * c2 - 2 * c3) + b4
							- b3 * c - b * c3 + c4);
		case 2390:
			return a2
					* (-(a * Q) + a3 * R - p(b - c, 2) * (b3 + c3) + a2
							* (b3 - 2 * b2 * c - 2 * b * c2 + c3));
		case 2391:
			return 2 * a4 + 3 * a2 * p(b - c, 2) - p(b - c, 4) - a3 * (b + c)
					- 3 * a * p(b - c, 2) * (b + c);
		case 2392:
			return a2 * (-b5 - c5 + a3 * R + a2 * (b3 + c3) - a * (b4 + c4));
		case 2393:
			return a2 * (-2 * a2 * b2 * c2 + a4 * R - Q * R);
		case 2394:
			return (b2 - c2)
					* (-a4 - a2 * b2 + 2 * b4 + 2 * a2 * c2 - b2 * c2 - c4)
					* (a4 + b4 + b2 * c2 - 2 * c4 + a2 * (-2 * b2 + c2));
		case 2395:
			return -((b2 - c2) * (a4 + b4 - a2 * c2 - b2 * c2) * (-a4 + a2 * b2
					+ b2 * c2 - c4));
		case 2396:
			return (a - b) * (a + b) * (a - c) * (a + c) * (-b4 - c4 + a2 * R);
		case 2397:
			return (a - b) * (a - c)
					* (-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 2398:
			return (a - b) * (a - c)
					* (2 * a3 - a2 * (b + c) - p(b - c, 2) * (b + c));
		case 2399:
			return (a - b - c)
					* (b - c)
					* (a4 - a3 * b - 2 * b4 + a * b * p(b - c, 2) + b3 * c + b2
							* c2 - b * c3 + c4 + a2 * (b2 + b * c - 2 * c2))
					* (a4 + b4 - a3 * c - b3 * c + a * p(b - c, 2) * c + b2
							* c2 + b * c3 - 2 * c4 + a2
							* (-2 * b2 + b * c + c2));

		default:
			return Double.NaN;
		}
	}

	private double weight2400to2499(int k, double a, double b, double c) {
		switch (k) {
		case 2400:
			return -((b - c)
					* (-a3 - a * b2 + 2 * b3 + a2 * c - b2 * c + a * c2 - c3) * (a3
					- a2 * b + b3 + b * c2 - 2 * c3 + a * (-b2 + c2)));
		case 2401:
			return -((b - c) * (a3 - a2 * b + b3 - a * p(b - c, 2) - b * c2) * (-a3
					+ a * p(b - c, 2) + a2 * c + c * (b2 - c2)));
		case 2402:
			return -((b - c) * (a2 + b * (b - c) - a * c)
					* (-a2 + a * b + (b - c) * c) * (a2 + b2 + c2 - 2 * a
					* (b + c)));
		case 2403:
			return (a + b - 2 * c) * (3 * a - b - c) * (b - c)
					* (a - 2 * b + c);
		case 2404:
			return (a - b)
					* (a + b)
					* (a - c)
					* (a + c)
					* p(a4 - Q, 2)
					* (a6 * R + 3 * a2 * Q * R + a4
							* (-3 * b4 + 4 * b2 * c2 - 3 * c4) - Q
							* (b4 + 4 * b2 * c2 + c4));
		case 2405:
			return (a - b)
					* (a - c)
					* (a + b - c)
					* (a - b + c)
					* U
					* V
					* (a5 * (b + c) - 2 * a3 * p(b - c, 2) * (b + c) + a
							* p(b - c, 4) * (b + c) + 2 * a2 * Q - a4 * R - Q
							* R);
		case 2406:
			return (a - b)
					* (a - c)
					* (a + b - c)
					* (a - b + c)
					* (2 * a4 - a2 * p(b - c, 2) - a3 * (b + c) + a
							* p(b - c, 2) * (b + c) - Q);
		case 2407:
			return (a - b) * (a + b) * (a - c) * (a + c)
					* (2 * a4 - Q - a2 * R);
		case 2408:
			return (b2 - c2) * (a2 + b2 - 2 * c2) * (-a2 + 2 * b2 - c2)
					* (-5 * a2 + b2 + c2);
		case 2409:
			return (a - b) * (a + b) * (a - c) * (a + c) * U * V
					* (2 * a6 - a4 * R - Q * R);
		case 2410:
			return (a - b)
					* (a + b)
					* (a - c)
					* (a + c)
					* (a2 - a * b + b2 - c2)
					* (a2 + a * b + b2 - c2)
					* (a2 - b2 - a * c + c2)
					* (a2 - b2 + a * c + c2)
					* (a6 * R + a4 * (-3 * b4 + 2 * b2 * c2 - 3 * c4) - Q
							* (b4 + 3 * b2 * c2 + c4) + a2
							* (3 * b6 - 2 * b4 * c2 - 2 * b2 * c4 + 3 * c6));
		case 2411:
			return -((b2 - c2)
					* (-a2 + b2 - b * c + c2)
					* (-a2 + b2 + b * c + c2)
					* (a8 + a6 * (b2 - 3 * c2) + b2 * p(b2 - c2, 3) + a4
							* (-4 * b4 + 2 * b2 * c2 + 3 * c4) + a2
							* (b6 + 2 * b4 * c2 - 2 * b2 * c4 - c6)) * (-a8
					+ c2 * p(b2 - c2, 3) + a6 * (3 * b2 - c2) + a4
					* (-3 * b4 - 2 * b2 * c2 + 4 * c4) + a2
					* (b6 + 2 * b4 * c2 - 2 * b2 * c4 - c6)));
		case 2412:
			return -((b - c) * (a3 + b2 * (b - c) - a2 * c)
					* (-a3 + a2 * b + (b - c) * c2) * (b4 + b3 * c - 2 * b2
					* c2 + b * c3 + c4 + a3 * (b + c) - a2 * (b2 + b * c + c2) - a
					* (b3 + b2 * c + b * c2 + c3)));
		case 2413:
			return -((b2 - c2) * (a2 - a * b + b2 - c2)
					* (a2 + a * b + b2 - c2) * (-a2 + b2 - a * c - c2)
					* (-a2 + b2 + a * c - c2)
					* (a4 + b4 - b2 * c2 + c4 - 2 * a2 * R)
					* (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2)) * (-a4 + c2
					* (b2 - c2) + a2 * (b2 + 2 * c2)));
		case 2414:
			return (a - b) * (a2 - 2 * a * b + p(b - c, 2)) * (a - c)
					* (a2 + p(b - c, 2) - 2 * a * c) * (-b2 - c2 + a * (b + c));
		case 2415:
			return (a - b) * (a + b - 3 * c) * (a - c) * (2 * a - b - c)
					* (a - 3 * b + c);
		case 2416:
			return -((b2 - c2)
					* (T * T)
					* (-a8 + a6 * (3 * b2 - 2 * c2) + c2 * p(b2 - c2, 3) - 3
							* a4 * (b4 + b2 * c2 - 2 * c4) + a2
							* (b6 + 4 * b4 * c2 - 3 * b2 * c4 - 2 * c6)) * (a8
					+ a6 * (2 * b2 - 3 * c2) + b2 * p(b2 - c2, 3) + 3 * a4
					* (-2 * b4 + b2 * c2 + c4) + a2
					* (2 * b6 + 3 * b4 * c2 - 4 * b2 * c4 - c6)));
		case 2417:
			return (a - b - c)
					* (b - c)
					* T
					* (a6 - a5 * b - p(b - c, 3) * c * p(b + c, 2) - a4
							* (2 * b2 - 3 * b * c + c2) + 2 * a3
							* (b3 - b * c2) + a2
							* (b4 - 2 * b3 * c + 4 * b2 * c2 - 2 * b * c3 - c4) - a
							* (b5 + 2 * b3 * c2 - 3 * b * c4))
					* (a6
							- a5
							* c
							+ b
							* p(b - c, 3)
							* p(b + c, 2)
							- a4
							* (b2 - 3 * b * c + 2 * c2)
							+ a3
							* (-2 * b2 * c + 2 * c3)
							+ a2
							* (-b4 - 2 * b3 * c + 4 * b2 * c2 - 2 * b * c3 + c4) + a
							* (3 * b4 * c - 2 * b2 * c3 - c5));
		case 2418:
			return (a - b) * (a + b) * (a - c) * (a + c) * (a2 + b2 - 5 * c2)
					* (2 * a2 - b2 - c2) * (a2 - 5 * b2 + c2);
		case 2419:
			return (b2 - c2)
					* (-T)
					* (-a6 - a2 * b4 + 2 * b6 + a4 * c2 - b4 * c2 + a2 * c4 - c6)
					* (a6 - a4 * b2 + b6 + b2 * c4 - 2 * c6 + a2 * (-b4 + c4));
		case 2420:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c)
					* (2 * a4 - Q - a2 * R);
		case 2421:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c)
					* (-b4 - c4 + a2 * R);
		case 2422:
			return a2 * (b2 - c2) * (a4 + b4 - a2 * c2 - b2 * c2)
					* (a4 - a2 * b2 - b2 * c2 + c4);
		case 2423:
			return a2 * (b - c) * (a3 - a2 * b + b3 - a * p(b - c, 2) - b * c2)
					* (a3 - a * p(b - c, 2) - a2 * c - b2 * c + c3);
		case 2424:
			return a2 * (b - c)
					* (a3 - 2 * b3 - a2 * c + b2 * c + c3 + a * (b2 - c2))
					* (a3 - a2 * b + b3 + b * c2 - 2 * c3 + a * (-b2 + c2));
		case 2425:
			return a2
					* (a - b)
					* (a - c)
					* (a + b - c)
					* (a - b + c)
					* (2 * a4 - a2 * p(b - c, 2) - a3 * (b + c) + a
							* p(b - c, 2) * (b + c) - Q);
		case 2426:
			return a2 * (a - b) * (a - c)
					* (2 * a3 - a2 * (b + c) - p(b - c, 2) * (b + c));
		case 2427:
			return a2 * (a - b) * (a - c)
					* (-2 * a * b * c + a2 * (b + c) - p(b - c, 2) * (b + c));
		case 2428:
			return a2 * (a - b) * (a2 - 2 * a * b + p(b - c, 2)) * (a - c)
					* (a2 + p(b - c, 2) - 2 * a * c) * (-b2 - c2 + a * (b + c));
		case 2429:
			return a2 * (a - b) * (a + b - 3 * c) * (a - c) * (2 * a - b - c)
					* (a - 3 * b + c);
		case 2430:
			return a2
					* (b2 - c2)
					* (T * T)
					* (a8 + c2 * p(-b2 + c2, 3) + a6 * (-3 * b2 + 2 * c2) + 3
							* a4 * (b4 + b2 * c2 - 2 * c4) - a2
							* (b6 + 4 * b4 * c2 - 3 * b2 * c4 - 2 * c6))
					* (a8 + a6 * (2 * b2 - 3 * c2) + b2 * p(b2 - c2, 3) + 3
							* a4 * (-2 * b4 + b2 * c2 + c4) + a2
							* (2 * b6 + 3 * b4 * c2 - 4 * b2 * c4 - c6));
		case 2431:
			return a2
					* (a - b - c)
					* (b - c)
					* T
					* (a6 - a5 * b - p(b - c, 3) * c * p(b + c, 2) - a4
							* (2 * b2 - 3 * b * c + c2) + 2 * a3
							* (b3 - b * c2) + a2
							* (b4 - 2 * b3 * c + 4 * b2 * c2 - 2 * b * c3 - c4) - a
							* (b5 + 2 * b3 * c2 - 3 * b * c4))
					* (a6
							- a5
							* c
							+ b
							* p(b - c, 3)
							* p(b + c, 2)
							- a4
							* (b2 - 3 * b * c + 2 * c2)
							+ a3
							* (-2 * b2 * c + 2 * c3)
							+ a2
							* (-b4 - 2 * b3 * c + 4 * b2 * c2 - 2 * b * c3 + c4) + a
							* (3 * b4 * c - 2 * b2 * c3 - c5));
		case 2432:
			return a2
					* (a - b - c)
					* (b - c)
					* (a4 - a3 * b - 2 * b4 + a * b * p(b - c, 2) + b3 * c + b2
							* c2 - b * c3 + c4 + a2 * (b2 + b * c - 2 * c2))
					* (a4 + b4 - a3 * c - b3 * c + a * p(b - c, 2) * c + b2
							* c2 + b * c3 - 2 * c4 + a2
							* (-2 * b2 + b * c + c2));
		case 2433:
			return a2 * (b2 - c2)
					* (a4 - 2 * b4 + b2 * c2 + c4 + a2 * (b2 - 2 * c2))
					* (a4 + b4 + b2 * c2 - 2 * c4 + a2 * (-2 * b2 + c2));
		case 2434:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c)
					* (a2 + b2 - 5 * c2) * (2 * a2 - b2 - c2)
					* (a2 - 5 * b2 + c2);
		case 2435:
			return -(a2 * (b2 - c2) * T
					* (a6 - 2 * b6 - a4 * c2 + b4 * c2 + c6 + a2 * (b4 - c4)) * (a6
					- a4 * b2 + b6 + b2 * c4 - 2 * c6 + a2 * (-b4 + c4)));
		case 2436:
			return a2
					* (b2 - c2)
					* (a2 - b2 - b * c - c2)
					* (a2 - b2 + b * c - c2)
					* (a8 + a6 * (b2 - 3 * c2) + b2 * p(b2 - c2, 3) + a4
							* (-4 * b4 + 2 * b2 * c2 + 3 * c4) + a2
							* (b6 + 2 * b4 * c2 - 2 * b2 * c4 - c6))
					* (a8 + a6 * (-3 * b2 + c2) + c2 * p(-b2 + c2, 3) + a4
							* (3 * b4 + 2 * b2 * c2 - 4 * c4) + a2
							* (-b6 - 2 * b4 * c2 + 2 * b2 * c4 + c6));
		case 2437:
			return a2
					* (a - b)
					* (a + b)
					* (a - c)
					* (a + c)
					* (a2 - a * b + b2 - c2)
					* (a2 + a * b + b2 - c2)
					* (a2 - b2 - a * c + c2)
					* (a2 - b2 + a * c + c2)
					* (a6 * R + a4 * (-3 * b4 + 2 * b2 * c2 - 3 * c4) - Q
							* (b4 + 3 * b2 * c2 + c4) + a2
							* (3 * b6 - 2 * b4 * c2 - 2 * b2 * c4 + 3 * c6));
		case 2438:
			return a2
					* (a - b)
					* (a - c)
					* (-b3 - c3 + a * R)
					* (a4 + a3 * (b - c) + a * p(b - c, 2) * (b + c) + b
							* p(b - c, 2) * (b + c) - a2
							* (2 * b2 + b * c + c2))
					* (a4 + a3 * (-b + c) + a * p(b - c, 2) * (b + c)
							+ p(b - c, 2) * c * (b + c) - a2
							* (b2 + b * c + 2 * c2));
		case 2439:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c)
					* (a2 - b2 - b * c - c2) * (a2 - b2 + b * c - c2)
					* (-Q + a2 * R) * (a4 + Q - a2 * (2 * b2 + c2))
					* (a4 + Q - a2 * (b2 + 2 * c2));
		case 2440:
			return a2 * (b - c) * (a2 + b * (b - c) - a * c)
					* (a2 - a * b + c * (-b + c))
					* (a2 + b2 + c2 - 2 * a * (b + c));
		case 2441:
			return a2 * (a + b - 2 * c) * (3 * a - b - c) * (b - c)
					* (a - 2 * b + c);
		case 2442:
			return a2
					* (a - b)
					* (a + b)
					* (a - c)
					* (a + c)
					* p(a4 - Q, 2)
					* (a6 * R + 3 * a2 * Q * R + a4
							* (-3 * b4 + 4 * b2 * c2 - 3 * c4) - Q
							* (b4 + 4 * b2 * c2 + c4));
		case 2443:
			return a2
					* (a - b)
					* (a - c)
					* (a + b - c)
					* (a - b + c)
					* U
					* V
					* (a5 * (b + c) - 2 * a3 * p(b - c, 2) * (b + c) + a
							* p(b - c, 4) * (b + c) + 2 * a2 * Q - a4 * R - Q
							* R);
		case 2444:
			return a2 * (b2 - c2) * (a2 + b2 - 2 * c2) * (5 * a2 - b2 - c2)
					* (a2 - 2 * b2 + c2);
		case 2445:
			return a2 * (a - b) * (a + b) * (a - c) * (a + c) * U * V
					* (2 * a6 - a4 * R - Q * R);
		case 2446:
			return -(a * (a2 * b - b3 + a2 * c - 2 * a * b * c + b2 * c + b
					* c2 - c3))
					+ 2
					* a
					* u(a
							* b
							* c
							* (a3 - a2 * b - a * b2 + b3 - a2 * c + 3 * a * b
									* c - b2 * c - a * c2 - b * c2 + c3));
		case 2447:
			return a
					* (a2 * b - b3 + a2 * c - 2 * a * b * c + b2 * c + b * c2 - c3)
					+ 2
					* a
					* u(a
							* b
							* c
							* (a3 - a2 * b - a * b2 + b3 - a2 * c + 3 * a * b
									* c - b2 * c - a * c2 - b * c2 + c3));
		case 2448:
			return -2
					* a2
					* b
					* c
					* (a2 * b - b3 + a2 * c - 2 * a * b * c + b2 * c + b * c2 - c3)
					+ a
					* (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 - c3)
					* u(a
							* b
							* c
							* (a3 - a2 * b - a * b2 + b3 - a2 * c + 3 * a * b
									* c - b2 * c - a * c2 - b * c2 + c3));
		case 2449:
			return 2
					* a2
					* b
					* c
					* (a2 * b - b3 + a2 * c - 2 * a * b * c + b2 * c + b * c2 - c3)
					+ a
					* (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 - c3)
					* u(a
							* b
							* c
							* (a3 - a2 * b - a * b2 + b3 - a2 * c + 3 * a * b
									* c - b2 * c - a * c2 - b * c2 + c3));
		case 2450:
			return -((a4 + Q) * (-b4 - c4 + a2 * R));
		case 2451:
			return a2 * (b2 - c2) * (a4 + 2 * b2 * c2 - a2 * R);
		case 2452:
			return a8 - b2 * c2 * Q - a6 * R + 2 * a2 * Q * R + a4
					* (-2 * b4 + 5 * b2 * c2 - 2 * c4);
		case 2453:
			return a8 + 2 * b2 * c2 * Q - a6 * R - a2 * Q * R + a4
					* (b4 - b2 * c2 + c4);
		case 2454:
			return -2
					* a4
					+ a2
					* b2
					+ b4
					+ a2
					* c2
					- 2
					* b2
					* c2
					+ c4
					+ 2
					* u(a8 - a6 * b2 - a2 * b6 + b8 - a6 * c2 + a4 * b2 * c2
							+ a2 * b4 * c2 - b6 * c2 + a2 * b2 * c4 - a2 * c6
							- b2 * c6 + c8);
		case 2455:
			return 2
					* a4
					- a2
					* b2
					- b4
					- a2
					* c2
					+ 2
					* b2
					* c2
					- c4
					+ 2
					* u(a8 - a6 * b2 - a2 * b6 + b8 - a6 * c2 + a4 * b2 * c2
							+ a2 * b4 * c2 - b6 * c2 + a2 * b2 * c4 - a2 * c6
							- b2 * c6 + c8);
		case 2456:
			return a2
					* (a8 - 2 * a6 * b2 + 3 * a4 * b4 - 2 * a2 * b6 - 2 * a6
							* c2 + a4 * b2 * c2 + 2 * a2 * b4 * c2 - 3 * b6
							* c2 + 3 * a4 * c4 + 2 * a2 * b2 * c4 + 2 * b4 * c4
							- 2 * a2 * c6 - 3 * b2 * c6);
		case 2457:
			return -((b2 - c2) * (-2 * a3 - a2 * (b + c) + p(b - c, 2)
					* (b + c) + 2 * a * (b2 - b * c + c2)));
		case 2458:
			return a2
					* (a8 + 2 * a4 * b4 - a2 * b6 + a4 * b2 * c2 - 2 * b6 * c2
							+ 2 * a4 * c4 - a2 * c6 - 2 * b2 * c6);
		case 2459:
			return 2 * a2 * (a2 * b2 - b4 + a2 * c2 - c4) + 2 * a2
					* (2 * a2 - b2 - c2) * S;
		case 2460:
			return 2 * a2 * (a2 * b2 - b4 + a2 * c2 - c4) - 2 * a2
					* (2 * a2 - b2 - c2) * S;
		case 2461:
			return a2
					* (a8 - 2 * a6 * b2 + 3 * a4 * b4 - 2 * a2 * b6 - 2 * a6
							* c2 + a4 * b2 * c2 + 2 * a2 * b4 * c2 - 3 * b6
							* c2 + 3 * a4 * c4 + 2 * a2 * b2 * c4 + 2 * b4 * c4
							- 2 * a2 * c6 - 3 * b2 * c6) + a2 * (a2 - b * c)
					* (a2 + b * c) * (a2 + b2 + c2) * S;
		case 2462:
			return a2
					* (a8 - 2 * a6 * b2 + 3 * a4 * b4 - 2 * a2 * b6 - 2 * a6
							* c2 + a4 * b2 * c2 + 2 * a2 * b4 * c2 - 3 * b6
							* c2 + 3 * a4 * c4 + 2 * a2 * b2 * c4 + 2 * b4 * c4
							- 2 * a2 * c6 - 3 * b2 * c6) - a2 * (a2 - b * c)
					* (a2 + b * c) * (a2 + b2 + c2) * S;
		case 2463:
			return a4
					+ a2
					* b2
					- 2
					* b4
					+ a2
					* c2
					+ 4
					* b2
					* c2
					- 2
					* c4
					+ 2
					* a
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2464:
			return -a4
					- a2
					* b2
					+ 2
					* b4
					- a2
					* c2
					- 4
					* b2
					* c2
					+ 2
					* c4
					+ 2
					* a
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2465:
			return a
					* b
					* c
					* (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4)
					+ a2
					* S
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2466:
			return a
					* b
					* c
					* (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4)
					- a2
					* S
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2467:
			return a4
					+ a2
					* b2
					- 2
					* b4
					+ a2
					* c2
					+ 4
					* b2
					* c2
					- 2
					* c4
					+ 2
					* (b + c)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2468:
			return -a4
					- a2
					* b2
					+ 2
					* b4
					- a2
					* c2
					- 4
					* b2
					* c2
					+ 2
					* c4
					+ 2
					* (b + c)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2469:
			return -2
					* a
					* b
					* c
					* (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4)
					+ 2
					* a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2470:
			return -2
					* a
					* b
					* c
					* (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4)
					- 2
					* a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2471:
			return -2
					* a
					* b
					* c
					* (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4)
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					+ 2
					* a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2472:
			return -2
					* a
					* b
					* c
					* (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4)
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					- 2
					* a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2473:
			return a
					* (b - c)
					* (a4 + 2 * a2 * b * c - a3 * (b + c) - a * p(b + c, 3) + p(
							b - c, 2) * R);
		case 2474:
			return -(a2 * (a2 + p(b - c, 2)) * (b2 - c2) * R * (a2 + p(b + c, 2)));
		case 2475:
			return -a4 - a2 * b * c - a * b * c * (b + c) + Q;
		case 2476:
			return -(a * b * c * (b + c)) + Q - a2 * (b2 + b * c + c2);
		case 2477:
			return a4 * (a + b - c) * (a - b + c) * p(-a2 + b2 + b * c + c2, 2);
		case 2478:
			return -a4 + 2 * a2 * b * c + 2 * a * b * c * (b + c) + Q;
		case 2479:
			return -2
					* a4
					+ a2
					* b2
					+ b4
					+ a2
					* c2
					- 2
					* b2
					* c2
					+ c4
					+ u(a8 - a6 * b2 - a2 * b6 + b8 - a6 * c2 + a4 * b2 * c2
							+ a2 * b4 * c2 - b6 * c2 + a2 * b2 * c4 - a2 * c6
							- b2 * c6 + c8);
		case 2480:
			return 2
					* a4
					- a2
					* b2
					- b4
					- a2
					* c2
					+ 2
					* b2
					* c2
					- c4
					+ u(a8 - a6 * b2 - a2 * b6 + b8 - a6 * c2 + a4 * b2 * c2
							+ a2 * b4 * c2 - b6 * c2 + a2 * b2 * c4 - a2 * c6
							- b2 * c6 + c8);
		case 2481:
			return b * c * (a2 + b * (b - c) - a * c)
					* (-a2 + a * b + (b - c) * c);
		case 2482:
			return p(-2 * a2 + b2 + c2, 2);
		case 2483:
			return a2 * (b - c) * (a2 + b2 + b * c + c2);
		case 2484:
			return a2 * (b - c) * (a2 + p(b + c, 2));
		case 2485:
			return a2 * (b2 - c2) * (a4 - b4 - c4);
		case 2486:
			return p(b - c, 2) * (b + c) * (-a2 + b * c + a * (b + c));
		case 2487:
			return -((b - c) * (-4 * a2 + p(b - c, 2) + a * (b + c)));
		case 2488:
			return a2 * (a - b - c) * (b - c) * (-p(b - c, 2) + a * (b + c));
		case 2489:
			return a2 * (b2 - c2) * U * V;
		case 2490:
			return (b - c) * (4 * a2 - 3 * a * (b + c) + p(b + c, 2));
		case 2491:
			return a4 * (b2 - c2) * (-b4 - c4 + a2 * R);
		case 2492:
			return a2 * (b2 - c2) * (a4 - b4 + b2 * c2 - c4);
		case 2493:
			return a2
					* (a6 * R - a4 * p(b2 + c2, 2) + Q * (b4 - b2 * c2 + c4) - a2
							* (b6 - 2 * b4 * c2 - 2 * b2 * c4 + c6));
		case 2494:
			return a2
					* (b - c)
					* (b5 - b4 * c - b * c4 + c5 + a4 * (b + c) - 2 * a3 * R
							+ 2 * a2 * (b3 + c3) - 2 * a * (b4 + c4));
		case 2495:
			return a2
					* (b - c)
					* (b5 - b4 * c - b * c4 + c5 + a4 * (b + c) - 2 * a * Q - 2
							* a3 * R + 2 * a2 * (b3 + c3));
		case 2496:
			return -((b - c) * (-7 * a3 + 4 * a2 * (b + c) + 2 * p(b - c, 2)
					* (b + c) + a * (-3 * b2 + 2 * b * c - 3 * c2)));
		case 2497:
			return a2
					* (b - c)
					* (b2 * p(b - c, 2) * c2 * (b + c) + a2 * (b + c)
							* p(b2 - b * c + c2, 2) + a4
							* (b3 + b2 * c + b * c2 + c3) - 2 * a3
							* (b4 + b2 * c2 + c4));
		case 2498:
			return a2
					* (b - c)
					* (b5 - b4 * c - b * c4 + c5 + a4 * (b + c) - 2 * a3 * R
							+ 2 * a2 * (b3 + c3) - 2 * a * (b4 - b2 * c2 + c4));
		case 2499:
			return a2
					* (b - c)
					* (b3 - 3 * b2 * c - 3 * b * c2 + c3 + a2 * (b + c) - 2 * a
							* (b2 + b * c + c2));
		default:
			return Double.NaN;
		}
	}

	private double weight2500to2599(int k, double a, double b, double c) {

		switch (k) {
		case 2500:
			return a
					* (b - c)
					* (5 * a3 * (b + c) - 4 * b * c * p(b + c, 2) + 2 * a2 * R - a
							* (3 * b3 + b2 * c + b * c2 + 3 * c3));
		case 2501:
			return (b2 - c2) * V * U;
		case 2502:
			return a2 * (2 * a4 - b4 + 4 * b2 * c2 - c4 - 2 * a2 * R);
		case 2503:
			return a2
					* (b + c)
					* (a4 - b4 - 2 * a2 * b * c + b3 * c + b2 * c2 + b * c3 - c4);
		case 2504:
			return (b - c) * (-T)
					* (2 * a3 - a2 * (b + c) + p(b - c, 2) * (b + c));
		case 2505:
			return (b - c)
					* (-2 * a3 + 5 * a2 * (b + c) + p(b + c, 3) - 8 * a * R);
		case 2506:
			return a2
					* (b2 - c2)
					* (a6 + (b2 + c2) * (a4 - b4 + 4 * b2 * c2 - c4) - a2
							* (b4 + 4 * b2 * c2 + c4));
		case 2507:
			return a2
					* (2 * b4 * c4 * (b2 - c2) + a6 * (b4 - c4) + a2
							* (-b8 + c8));
		case 2508:
			return a2 * (b2 - c2)
					* (a8 - b8 - a4 * b2 * c2 + b6 * c2 + b2 * c6 - c8);
		case 2509:
			return a
					* (b - c)
					* (a3 - a2 * (b + c) + a * p(b + c, 2) - (b + c)
							* (b2 + c2))
					* (a3 + a2 * (b + c) + a * p(b + c, 2) + (b + c)
							* (b2 + c2));
		case 2510:
			return a2 * (b2 - c2) * T * (a4 + b4 - 3 * b2 * c2 + c4);
		case 2511:
			return a * (b - c) * p(b + c, 2)
					* (a4 - a2 * p(b - c, 2) - b2 * c2);
		case 2512:
			return -(a * (b2 - c2) * (b * c * (b + c) + a
					* (2 * b2 + b * c + 2 * c2)));
		case 2513:
			return -(a2 * (b2 - c2) * (b2 * c2 * R + a2
					* (2 * b4 + 3 * b2 * c2 + 2 * c4)));
		case 2514:
			return -(a2 * (b2 - c2) * (b4 + c4 + a2 * R));
		case 2515:
			return a2 * (b - c) * (2 * a2 + 2 * b2 + 3 * b * c + 2 * c2);
		case 2516:
			return a * (b - c) * (5 * a - 3 * (b + c));
		case 2517:
			return b * (b - c) * c * (a2 + p(b + c, 2));
		case 2518:
			return -(a2 * (b2 - c2) * R * (2 * a4 + 2 * b4 - 3 * b2 * c2 + 2 * c4));
		case 2519:
			return a2 * (b2 - c2) * T
					* (a4 + b4 - 6 * b2 * c2 + c4 + 2 * a2 * R);
		case 2520:
			return -(a * (a - b - c) * (b - c) * (a3 - p(b - c, 2) * (b + c)));
		case 2521:
			return a2
					* (b - c)
					* (2 * a4 + 2 * b4 + 3 * a2 * b * c + 3 * b3 * c + 3 * b
							* c3 + 2 * c4 + 3 * a * b * c * (b + c));
		case 2522:
			return a * (b - c) * T * (a2 + p(b + c, 2));
		case 2523:
			return a * (b - c) * T * (a2 + a * (b + c) + p(b + c, 2));
		case 2524:
			return a2 * (b2 - c2) * T * (-(b2 * c2) + a2 * R);
		case 2525:
			return (b2 - c2) * R * (-T);
		case 2526:
			return -(a * (b - c) * (a2 + 3 * b2 + 2 * b * c + 3 * c2));
		case 2527:
			return (b - c) * (6 * a2 - a * (b + c) + p(b + c, 2));
		case 2528:
			return (b2 - c2) * p(b2 + c2, 2);
		case 2529:
			return (b - c) * (7 * a2 + a * (b + c) + 2 * p(b + c, 2));
		case 2530:
			return -(a * (b - c) * R);
		case 2531:
			return -(a4 * (b2 - c2) * p(b2 + c2, 2));
		case 2532:
			return a
					* (b - c)
					* (5 * a3 * (b + c) - 4 * b * c * p(b + c, 2) + 2 * a2
							* (b2 - b * c + c2) - 3 * a
							* (b3 + b2 * c + b * c2 + c3));
		case 2533:
			return (b2 - c2) * (a2 + b * c);
		case 2534:
			return (b + c)
					* S
					+ 2
					* a
					* u((a + b + c)
							* (a2 * (b + c) + b2 * (c + a) + c2 * (a + b) + a
									* b * c));
		case 2535:
			return (b + c)
					* S
					- 2
					* a
					* u((a + b + c)
							* (a2 * (b + c) + b2 * (c + a) + c2 * (a + b) + a
									* b * c));
		case 2536:
			return a
					* b
					* c
					* (b + c)
					* S
					- a2
					* T
					* u((a + b + c)
							* (a2 * (b + c) + b2 * (c + a) + c2 * (a + b) + a
									* b * c));
		case 2537:
			return a
					* b
					* c
					* (b + c)
					* S
					+ a2
					* T
					* u((a + b + c)
							* (a2 * (b + c) + b2 * (c + a) + c2 * (a + b) + a
									* b * c));
		case 2538:
			return a
					* b
					* c
					* (b + c)
					* (a2 * b + a * b2 + a2 * c + a * b * c + b2 * c + a * c2 + b
							* c2)
					* S
					- a2
					* (a3 * b2 + a2 * b3 - a * b4 - b5 + 2 * a3 * b * c + a2
							* b2 * c - 2 * a * b3 * c - b4 * c + a3 * c2 + a2
							* b * c2 + a2 * c3 - 2 * a * b * c3 - a * c4 - b
							* c4 - c5)
					* u((a + b + c)
							* (a2 * (b + c) + b2 * (c + a) + c2 * (a + b) + a
									* b * c));
		case 2539:
			return a
					* b
					* c
					* (b + c)
					* (a2 * b + a * b2 + a2 * c + a * b * c + b2 * c + a * c2 + b
							* c2)
					* S
					+ a2
					* (a3 * b2 + a2 * b3 - a * b4 - b5 + 2 * a3 * b * c + a2
							* b2 * c - 2 * a * b3 * c - b4 * c + a3 * c2 + a2
							* b * c2 + a2 * c3 - 2 * a * b * c3 - a * c4 - b
							* c4 - c5)
					* u((a + b + c)
							* (a2 * (b + c) + b2 * (c + a) + c2 * (a + b) + a
									* b * c));
		case 2540:
			return a
					* b
					* c
					* (b + c)
					* S
					+ (a2 * b2 - b4 + a2 * c2 + 2 * b2 * c2 - c4)
					* u((a + b + c)
							* (a2 * (b + c) + b2 * (c + a) + c2 * (a + b) + a
									* b * c));
		case 2541:
			return a
					* b
					* c
					* (b + c)
					* S
					+ (-(a2 * b2) + b4 - a2 * c2 - 2 * b2 * c2 + c4)
					* u((a + b + c)
							* (a2 * (b + c) + b2 * (c + a) + c2 * (a + b) + a
									* b * c));
		case 2542:
			return -2 * a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2) + U * V
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4);
		case 2543:
			return -2 * a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2) - U * V
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4);
		case 2544:
			return -2 * (a2 * b2 - b4 + 2 * a2 * c2 + b2 * c2)
					* (2 * a2 * b2 + a2 * c2 + b2 * c2 - c4)
					+ (-a4 - 4 * a2 * b2 + b4 - 4 * a2 * c2 - 2 * b2 * c2 + c4)
					* S * u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2545:
			return 2 * (a2 * b2 - b4 + 2 * a2 * c2 + b2 * c2)
					* (2 * a2 * b2 + a2 * c2 + b2 * c2 - c4)
					+ (-a4 - 4 * a2 * b2 + b4 - 4 * a2 * c2 - 2 * b2 * c2 + c4)
					* S * u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2546:
			return 2 * a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2) - U * V
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2547:
			return -2 * a2 * (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2) - U * V
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2548:
			return -a4 + Q - 2 * a2 * R;
		case 2549:
			return -a4 + Q + 2 * a2 * R;
		case 2550:
			return -a3 + a2 * (b + c) + p(b - c, 2) * (b + c) - a * p(b + c, 2);
		case 2551:
			return -a4 + 4 * a * b * c * (b + c) + Q;
		case 2552:
			return -2
					* a
					* b
					* c
					* (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4)
					- U
					* V
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2553:
			return 2
					* a
					* b
					* c
					* (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4)
					- U
					* V
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2554:
			return -(a * b * c
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4) * u(a4
					- a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4))
					+ a2
					* T
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2555:
			return a
					* b
					* c
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4)
					+ a2
					* T
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2556:
			return a2
					* b
					* c
					* (a2 * b - b3 + a2 * c - 2 * a * b * c + b2 * c + b * c2 - c3)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4)
					+ a2
					* (-T)
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u(a
							* b
							* c
							* (a3 - a2 * b - a * b2 + b3 - a2 * c + 3 * a * b
									* c - b2 * c - a * c2 - b * c2 + c3));
		case 2557:
			return a2
					* b
					* c
					* (a2 * b - b3 + a2 * c - 2 * a * b * c + b2 * c + b * c2 - c3)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4)
					- a2
					* (-T)
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u(a
							* b
							* c
							* (a3 - a2 * b - a * b2 + b3 - a2 * c + 3 * a * b
									* c - b2 * c - a * c2 - b * c2 + c3));
		case 2558:
			return -2 * a2 * T * (a2 * b2 + a2 * c2 + b2 * c2) - 2 * a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4);
		case 2559:
			return 2 * a2 * T * (a2 * b2 + a2 * c2 + b2 * c2) - 2 * a2
					* (a4 - a2 * b2 - a2 * c2 - 2 * b2 * c2)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4);
		case 2560:
			return -(a2 * u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4) * S)
					+ a2 * T * u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2561:
			return a2 * u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4) * S + a2
					* T * u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2562:
			return -(a2 * T * (a2 * b2 + a2 * c2 + b2 * c2)) + a2 * R
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4) * S;
		case 2563:
			return -(a2 * T * (a2 * b2 + a2 * c2 + b2 * c2)) - a2 * R
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4) * S;
		case 2564:
			return -2 * a2 * b * c
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4) + a2 * T
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2565:
			return 2 * a2 * b * c
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4) + a2 * T
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2566:
			return -((a2 * b2 - b4 + a2 * c2 + 2 * b2 * c2 - c4) * u(a4 - a2
					* b2 + b4 - a2 * c2 - b2 * c2 + c4))
					+ a2 * T * u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2567:
			return (a2 * b2 - b4 + a2 * c2 + 2 * b2 * c2 - c4)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4) + a2 * T
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2568:
			return -2 * a * b * c * (b + c)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4) + a2 * T
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2569:
			return 2 * a * b * c * (b + c)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4) + a2 * T
					* u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2570:
			return -(a * b * c
					* (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4) * u(a4
					- a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4))
					+ a2
					* T
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2571:
			return -(a * b * c
					* (a4 + a2 * b2 - 2 * b4 + a2 * c2 + 4 * b2 * c2 - 2 * c4) * u(a4
					- a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4))
					- a2
					* T
					* u(a2 * b2 + a2 * c2 + b2 * c2)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2572:
			return a
					* (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 - c3)
					* u(a4 - a2 * b2 + b4 - a2 * c2 - b2 * c2 + c4) + 2 * a2
					* T * u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2573:
			return -(a
					* (a3 + a2 * b - a * b2 - b3 + a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 - c3) * u(a4 - a2 * b2 + b4
					- a2 * c2 - b2 * c2 + c4))
					+ 2 * a2 * T * u(a2 * b2 + a2 * c2 + b2 * c2);
		case 2574:
			return a
					/ (-(b * c * U * V) + a2 * b * c * (-T) - a
							* (-T)
							* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2
									* b2 * c2 - b4 * c2 - a2 * c4 - b2 * c4
									+ c6));
		case 2575:
			return a
					/ (-(b * c * U * V) + a2 * b * c * (-T) + a
							* (-T)
							* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2
									* b2 * c2 - b4 * c2 - a2 * c4 - b2 * c4
									+ c6));
		case 2576:
			return -(a2 * b * c * (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2
					* c2 - c4))
					+ a3
					* T
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2577:
			return a2
					* b
					* c
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4)
					+ a3
					* T
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2578:
			return a2
					/ (-(b * c * U * V) + a2 * b * c * (-T) - a
							* (-T)
							* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2
									* b2 * c2 - b4 * c2 - a2 * c4 - b2 * c4
									+ c6));
		case 2579:
			return a2
					/ (-(b * c * U * V) + a2 * b * c * (-T) + a
							* (-T)
							* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2
									* b2 * c2 - b4 * c2 - a2 * c4 - b2 * c4
									+ c6));
		case 2580:
			return -(b * c * (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4))
					+ a
					* T
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2581:
			return b
					* c
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4)
					+ a
					* T
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2582:
			return 1 / (b * c
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4) + a
					* (-T)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6));
		case 2583:
			return 1 / (b * c
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4) - a
					* (-T)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6));
		case 2584:
			return (a2 * (-T))
					/ (-(b * c * U * V) + a2 * b * c * (-T) - a
							* (-T)
							* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2
									* b2 * c2 - b4 * c2 - a2 * c4 - b2 * c4
									+ c6));
		case 2585:
			return (a2 * (-T))
					/ (-(b * c * U * V) + a2 * b * c * (-T) + a
							* (-T)
							* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2
									* b2 * c2 - b4 * c2 - a2 * c4 - b2 * c4
									+ c6));
		case 2586:
			return -(b * c * U * V * (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2
					* c2 - c4))
					+ a
					* T
					* U
					* V
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2587:
			return b
					* c
					* U
					* V
					* (2 * a4 - a2 * b2 - b4 - a2 * c2 + 2 * b2 * c2 - c4)
					+ a
					* T
					* U
					* V
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2588:
			return 1 / ((-T) * (-(b * c * U * V) + a2 * b * c * (-T) - a
					* (-T)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6)));
		case 2589:
			return 1 / ((-T) * (-(b * c * U * V) + a2 * b * c * (-T) + a
					* (-T)
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6)));
		case 2590:
			return a
					/ (-(b4 * c) - 2 * a * b2 * c2 + b3 * c2 + b2 * c3 - b * c4
							+ a2 * b * c * (b + c) + (-T)
							* u(a
									* b
									* c
									* (a3 - a2 * b - a * b2 + b3 - a2 * c + 3
											* a * b * c - b2 * c - a * c2 - b
											* c2 + c3)));
		case 2591:
			return a
					/ (-(b4 * c) - 2 * a * b2 * c2 + b3 * c2 + b2 * c3 - b * c4
							+ a2 * b * c * (b + c) - (-T)
							* u(a
									* b
									* c
									* (a3 - a2 * b - a * b2 + b3 - a2 * c + 3
											* a * b * c - b2 * c - a * c2 - b
											* c2 + c3)));
		case 2592:
			return -(b2 * (b - c) * c2 * (b + c) * (2 * a4 - a2 * b2 - b4 - a2
					* c2 + 2 * b2 * c2 - c4))
					- a
					* b
					* (b - c)
					* c
					* (b + c)
					* T
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2593:
			return -(b2 * (b - c) * c2 * (b + c) * (2 * a4 - a2 * b2 - b4 - a2
					* c2 + 2 * b2 * c2 - c4))
					+ a
					* b
					* (b - c)
					* c
					* (b + c)
					* T
					* u(a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 3 * a2 * b2
							* c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2594:
			return a2 * (a + b - c) * (a - b + c) * (b + c)
					* (a2 - b2 - b * c - c2);
		case 2595:
			return (a + b - c)
					* (a - b + c)
					* (a8 - b * p(b - c, 2) * c * p(b + c, 4) - 2 * a6
							* (b2 + b * c + c2) + a4 * p(b2 + b * c + c2, 2) + a2
							* b * c * (b4 + b3 * c + b * c3 + c4));
		case 2596:
			return (a - b - c)
					* (a8 + b * p(b - c, 4) * c * p(b + c, 2) - 2 * a6
							* (b2 - b * c + c2) + a4 * p(b2 - b * c + c2, 2) - a2
							* b * p(b - c, 2) * c * (b2 + b * c + c2));
		case 2597:
			return a2
					* (a + b - c)
					* (a - b + c)
					* (a7 * b - 2 * a6 * b2 + c4 * Q - a5 * b * R + a4
							* (4 * b4 + b2 * c2 + c4) - a3 * (b5 + 2 * b * c4)
							+ a2 * (-2 * b6 + b4 * c2 + 3 * b2 * c4 - 2 * c6) + a
							* (b7 - b5 * c2 - 2 * b3 * c4 + 2 * b * c6))
					* (a7 * c - 2 * a6 * c2 + b4 * Q - a5 * c * R + a4
							* (b4 + b2 * c2 + 4 * c4) - a3 * (2 * b4 * c + c5)
							+ a2 * (-2 * b6 + 3 * b4 * c2 + b2 * c4 - 2 * c6) + a
							* (2 * b6 * c - 2 * b4 * c3 - b2 * c5 + c7));
		case 2598:
			return a2
					* (2
							* a9
							* b
							* c
							+ b2
							* p(b - c, 4)
							* c2
							* p(b + c, 3)
							+ a8
							* (b3 - 2 * b2 * c - 2 * b * c2 + c3)
							- a7
							* (b4 + 5 * b3 * c - 4 * b2 * c2 + 5 * b * c3 + c4)
							- a6
							* (3 * b5 - 6 * b4 * c + b3 * c2 + b2 * c3 - 6 * b
									* c4 + 3 * c5)
							+ a
							* Q
							* (b6 - b5 * c + b3 * c3 - b * c5 + c6)
							+ a5
							* (3 * b6 + 3 * b5 * c - 7 * b4 * c2 + 8 * b3 * c3
									- 7 * b2 * c4 + 3 * b * c5 + 3 * c6)
							- a3
							* p(b - c, 2)
							* (3 * b6 + 5 * b5 * c + 2 * b4 * c2 + 3 * b3 * c3
									+ 2 * b2 * c4 + 5 * b * c5 + 3 * c6)
							- a2
							* p(b - c, 2)
							* (b7 + 3 * b5 * c2 + 7 * b4 * c3 + 7 * b3 * c4 + 3
									* b2 * c5 + c7) + 3
							* a4
							* (b7 - 2 * b6 * c + 2 * b5 * c2 + 2 * b2 * c5 - 2
									* b * c6 + c7));
		case 2599:
			return -(a * (a + b - c) * (a - b + c) * (b + c)
					* (a2 - b2 - b * c - c2) * (-Q + a2 * R));
		default:
			return Double.NaN;
		}
	}

	private double weight2600to2699(int k, double a, double b, double c) {
		switch (k) {
		case 2600:
			return a * (a - b - c) * (b - c) * (a2 - b2 + b * c - c2)
					* (-Q + a2 * R);
		case 2601:
			return a
					* (a + b - c)
					* (a - b + c)
					* (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2))
					* (a8 - b * p(b - c, 2) * c * p(b + c, 4) - 2 * a6
							* (b2 + b * c + c2) + a4 * p(b2 + b * c + c2, 2) + a2
							* b * c * (b4 + b3 * c + b * c3 + c4));
		case 2602:
			return a
					* (a - b - c)
					* (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a8 + b * p(b - c, 4) * c * p(b + c, 2) - 2 * a6
							* (b2 - b * c + c2) + a4 * p(b2 - b * c + c2, 2) - a2
							* b * p(b - c, 2) * c * (b2 + b * c + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 2603:
			return a
					* (a + b - c)
					* (a - b + c)
					* (-Q + a2 * R)
					* (a7 * b - 2 * a6 * b2 + c4 * Q - a5 * b * R + a4
							* (4 * b4 + b2 * c2 + c4) - a3 * (b5 + 2 * b * c4)
							+ a2 * (-2 * b6 + b4 * c2 + 3 * b2 * c4 - 2 * c6) + a
							* (b7 - b5 * c2 - 2 * b3 * c4 + 2 * b * c6))
					* (a7 * c - 2 * a6 * c2 + b4 * Q - a5 * c * R + a4
							* (b4 + b2 * c2 + 4 * c4) - a3 * (2 * b4 * c + c5)
							+ a2 * (-2 * b6 + 3 * b4 * c2 + b2 * c4 - 2 * c6) + a
							* (2 * b6 * c - 2 * b4 * c3 - b2 * c5 + c7));
		case 2604:
			return -(a * (-Q + a2 * R) * (a10
					* a2
					* R
					- a10
					* a
					* (b3 + b2 * c + b * c2 + c3)
					- a
					* b
					* p(b - c, 4)
					* c
					* p(b + c, 3)
					* (b4 + 3 * b2 * c2 + c4)
					+ b
					* c
					* p(b2 - c2, 4)
					* (b4 - b3 * c + b2 * c2 - b * c3 + c4)
					- 2
					* a10
					* (2 * b4 + 3 * b2 * c2 + 2 * c4)
					+ a9
					* (4 * b5 + 3 * b4 * c + 5 * b3 * c2 + 5 * b2 * c3 + 3 * b
							* c4 + 4 * c5)
					+ a4
					* Q
					* (b6 + 6 * b5 * c - b4 * c2 + 5 * b3 * c3 - b2 * c4 + 6
							* b * c5 + c6)
					- a2
					* b
					* c
					* Q
					* (4 * b6 - 3 * b5 * c - 3 * b * c5 + 4 * c6)
					+ a8
					* (6 * b6 + b5 * c + 9 * b4 * c2 - 2 * b3 * c3 + 9 * b2
							* c4 + b * c5 + 6 * c6)
					+ a5
					* p(b - c, 2)
					* (4 * b7 + 6 * b6 * c + 12 * b5 * c2 + 15 * b4 * c3 + 15
							* b3 * c4 + 12 * b2 * c5 + 6 * b * c6 + 4 * c7)
					- a7
					* (6 * b7 + 2 * b6 * c + 7 * b5 * c2 + 3 * b4 * c3 + 3 * b3
							* c4 + 7 * b2 * c5 + 2 * b * c6 + 6 * c7)
					- a6
					* (4 * b8 + 4 * b7 * c + 3 * b6 * c2 - 4 * b5 * c3 + 4 * b4
							* c4 - 4 * b3 * c5 + 3 * b2 * c6 + 4 * b * c7 + 4 * c8) - a3
					* p(b - c, 2)
					* (b9 - b8 * c - b7 * c2 - 3 * b6 * c3 - 8 * b5 * c4 - 8
							* b4 * c5 - 3 * b3 * c6 - b2 * c7 - b * c8 + c9)));
		case 2605:
			return a2 * (-b3 + a2 * (b - c) + c3);
		case 2606:
			return a6 + a2 * b2 * c2 - b * c * Q - a4 * R;
		case 2607:
			return a6 + a2 * b2 * c2 + b * c * Q - a4 * R;
		case 2608:
			return -(a2
					* (a5 * b - 2 * a3 * b3 + a * b5 - b2 * c4 + c6 + a2 * c2
							* (b2 - c2)) * (b6 + a5 * c - b4 * c2 - 2 * a3 * c3
					+ a * c5 + a2 * (-b4 + b2 * c2)));
		case 2609:
			return a2
					* (2 * a6 * b * c + 2 * a2 * b3 * c3 + b2 * c2 * Q - 2 * a4
							* b * c * R - a5 * (b3 + c3) + 2 * a3 * (b5 + c5) - a
							* (b7 + c7));
		case 2610:
			return -(a * (b - c) * p(b + c, 2) * (a2 - b2 + b * c - c2));
		case 2611:
			return a * p(b - c, 2) * (b + c) * (a2 - b2 - b * c - c2);
		case 2612:
			return -(a * (a - b) * (a + b) * (a - c) * (a + c) * (a6 + a2 * b2
					* c2 - b * c * Q - a4 * R));
		case 2613:
			return -(a * (a - b) * (a + b) * (a - c) * (a + c) * (a6 + a2 * b2
					* c2 + b * c * Q - a4 * R));
		case 2614:
			return a
					* (b2 - c2)
					* (a5 * b - 2 * a3 * b3 + a * b5 - b2 * c4 + c6 + a2 * c2
							* (b2 - c2))
					* (b6 + a5 * c - b4 * c2 - 2 * a3 * c3 + a * c5 + a2
							* (-b4 + b2 * c2));
		case 2615:
			return a
					* p(b - c, 2)
					* p(b + c, 2)
					* (a7 - a * b2 * p(b - c, 2) * c2 - a4 * b * c * (b + c)
							- 2 * a5 * (b2 - b * c + c2) + a3
							* p(b2 - b * c + c2, 2) + 2 * a2 * b * c
							* (b3 + c3) - b * c * (b5 + c5));
		case 2616:
			return a * (b2 - c2) * (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 2617:
			return -(a * (a - b) * (a + b) * (a - c) * (a + c) * (-Q + a2 * R));
		case 2618:
			return b * (b - c) * c * (b + c) * (Q - a2 * R);
		case 2619:
			return -(b
					* c
					* (2 * a8 + 2 * a4 * b2 * c2 + p(b2 - c2, 4) - 2 * a6 * R - a2
							* Q * R) * (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2)) * (-a4
					+ c2 * (b2 - c2) + a2 * (b2 + 2 * c2)));
		case 2620:
			return a3
					* (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2))
					* (a6 * (b4 + c4) + a4
							* (-3 * b6 + b4 * c2 + b2 * c4 - 3 * c6) - Q
							* (b6 + c6) + a2
							* (3 * b8 - 3 * b6 * c2 + 2 * b4 * c4 - 3 * b2 * c6 + 3 * c8));
		case 2621:
			return -(b
					* c
					* (Q - a2 * R)
					* (a10 + b4 * p(b2 - c2, 3) - a8 * (2 * b2 + 3 * c2) - a2
							* b4 * (2 * b4 - 3 * b2 * c2 + c4) + a6
							* (b4 + 3 * b2 * c2 + 3 * c4) + a4
							* (b6 - 2 * b4 * c2 - b2 * c4 - c6)) * (-a10 + c4
					* p(b2 - c2, 3) + a8 * (3 * b2 + 2 * c2) - a6
					* (3 * b4 + 3 * b2 * c2 + c4) + a2 * c4
					* (b4 - 3 * b2 * c2 + 2 * c4) + a4
					* (b6 + b4 * c2 + 2 * b2 * c4 - c6)));
		case 2622:
			return a2
					* (-Q + a2 * R)
					* (b4
							* c4
							* p(b2 - c2, 4)
							+ a10
							* a4
							* R
							- a2
							* b4
							* c4
							* Q
							* R
							- a10
							* a3
							* (b3 + c3)
							- a10
							* a2
							* (5 * b4 + 6 * b2 * c2 + 5 * c4)
							+ a10
							* a
							* (5 * b5 + 3 * b3 * c2 + 3 * b2 * c3 + 5 * c5)
							- a4
							* p(b6 - c6, 2)
							+ a
							* b2
							* p(b - c, 2)
							* c2
							* p(b + c, 3)
							* (b6 - 3 * b5 * c + 3 * b4 * c2 - 3 * b3 * c3 + 3
									* b2 * c4 - 3 * b * c5 + c6)
							+ 2
							* a10
							* (5 * b6 + 6 * b4 * c2 + 6 * b2 * c4 + 5 * c6)
							- a9
							* (10 * b7 + 9 * b5 * c2 + 3 * b4 * c3 + 3 * b3
									* c4 + 9 * b2 * c5 + 10 * c7)
							- 2
							* a8
							* (5 * b8 + 5 * b6 * c2 + 4 * b4 * c4 + 5 * b2 * c6 + 5 * c8)
							+ a7
							* (10 * b9 + 8 * b7 * c2 + 3 * b6 * c3 + 3 * b5
									* c4 + 3 * b4 * c5 + 3 * b3 * c6 + 8 * b2
									* c7 + 10 * c9)
							+ a6
							* (5 * b10 + 3 * b8 * c2 + b6 * c4 + b4 * c6 + 3
									* b2 * c8 + 5 * c10)
							+ a3
							* p(b - c, 2)
							* (b10 * b + 2 * b10 * c + 4 * b8 * c3 + 11 * b7
									* c4 + 12 * b6 * c5 + 12 * b5 * c6 + 11
									* b4 * c7 + 4 * b3 * c8 + 2 * b * c10 + c10
									* c) - a5
							* (5 * b10 * b + 6 * b8 * c3 + b7 * c4 - 3 * b6
									* c5 - 3 * b5 * c6 + b4 * c7 + 6 * b3 * c8 + 5
									* c10 * c));
		case 2623:
			return a2 * (b2 - c2) * (a4 + b4 - b2 * c2 - a2 * (2 * b2 + c2))
					* (a4 - b2 * c2 + c4 - a2 * (b2 + 2 * c2));
		case 2624:
			return a3 * (-b6 + c6 + a4 * (-b2 + c2) + 2 * a2 * (b4 - c4));
		case 2625:
			return -((a - b) * b * (a + b) * (a - c) * c * (a + c) * (2 * a8
					+ 2 * a4 * b2 * c2 + p(b2 - c2, 4) - 2 * a6 * R - a2 * Q
					* R));
		case 2626:
			return -(a3 * (a - b) * (a + b) * (a - c) * (a + c) * (a6
					* (b4 + c4) + a4 * (-3 * b6 + b4 * c2 + b2 * c4 - 3 * c6)
					- Q * (b6 + c6) + a2
					* (3 * b8 - 3 * b6 * c2 + 2 * b4 * c4 - 3 * b2 * c6 + 3 * c8)));
		case 2627:
			return b
					* (b - c)
					* c
					* (b + c)
					* (a10 + b4 * p(b2 - c2, 3) - a8 * (2 * b2 + 3 * c2) - a2
							* b4 * (2 * b4 - 3 * b2 * c2 + c4) + a6
							* (b4 + 3 * b2 * c2 + 3 * c4) + a4
							* (b6 - 2 * b4 * c2 - b2 * c4 - c6))
					* (-a10 + c4 * p(b2 - c2, 3) + a8 * (3 * b2 + 2 * c2) - a6
							* (3 * b4 + 3 * b2 * c2 + c4) + a2 * c4
							* (b4 - 3 * b2 * c2 + 2 * c4) + a4
							* (b6 + b4 * c2 + 2 * b2 * c4 - c6));
		case 2628:
			return -(a2 * p(b - c, 2) * (b + c) * (a10
					* a2
					* (b + c)
					- b4
					* p(b - c, 2)
					* c4
					* p(b + c, 3)
					- a10
					* a
					* (b2 + b * c + c2)
					- 4
					* a10
					* (b3 + b2 * c + b * c2 + c3)
					+ a4
					* (b + c)
					* p(b4 + b2 * c2 + c4, 2)
					+ a9
					* (4 * b4 + 4 * b3 * c + 6 * b2 * c2 + 4 * b * c3 + 4 * c4)
					+ 2
					* a8
					* (3 * b5 + 3 * b4 * c + 5 * b3 * c2 + 5 * b2 * c3 + 3 * b
							* c4 + 3 * c5)
					- a7
					* (6 * b6 + 6 * b5 * c + 11 * b4 * c2 + 10 * b3 * c3 + 11
							* b2 * c4 + 6 * b * c5 + 6 * c6)
					- 4
					* a6
					* (b7 + b6 * c + 2 * b5 * c2 + 2 * b4 * c3 + 2 * b3 * c4
							+ 2 * b2 * c5 + b * c6 + c7)
					+ a
					* b2
					* c2
					* (b8 - b7 * c - 2 * b6 * c2 - 2 * b2 * c6 - b * c7 + c8)
					+ a5
					* (4 * b8 + 4 * b7 * c + 9 * b6 * c2 + 7 * b5 * c3 + 6 * b4
							* c4 + 7 * b3 * c5 + 9 * b2 * c6 + 4 * b * c7 + 4 * c8) - a3
					* (b10 + b9 * c + 4 * b8 * c2 - b6 * c4 + 3 * b5 * c5 - b4
							* c6 + 4 * b2 * c8 + b * c9 + c10)));
		case 2629:
			return a
					* (a8 - a6 * R + 3 * a2 * Q * R + a4
							* (-2 * b4 + 5 * b2 * c2 - 2 * c4) - Q
							* (b4 + 3 * b2 * c2 + c4));
		case 2630:
			return a
					* (2
							* a9
							- a8
							* (b + c)
							- 2
							* a
							* b2
							* c2
							* Q
							- 2
							* a7
							* R
							+ 2
							* a3
							* Q
							* R
							- a6
							* (b3 - 3 * b2 * c - 3 * b * c2 + c3)
							- 2
							* a5
							* (b4 - 3 * b2 * c2 + c4)
							- p(b - c, 2)
							* p(b + c, 3)
							* (b4 - 2 * b3 * c + b2 * c2 - 2 * b * c3 + c4)
							- a2
							* p(b - c, 2)
							* (b5 + 3 * b4 * c + 8 * b3 * c2 + 8 * b2 * c3 + 3
									* b * c4 + c5) + a4
							* (4 * b5 - 2 * b4 * c - 3 * b3 * c2 - 3 * b2 * c3
									- 2 * b * c4 + 4 * c5));
		case 2631:
			return a * (b2 - c2) * T * (2 * a4 - Q - a2 * R);
		case 2632:
			return -(a * p(b - c, 2) * p(b + c, 2) * (T * T));
		case 2633:
			return -(a * (a - b) * (a + b) * (a - c) * (a + c) * U * V * (a8
					- a6 * R + 3 * a2 * Q * R + a4
					* (-2 * b4 + 5 * b2 * c2 - 2 * c4) - Q
					* (b4 + 3 * b2 * c2 + c4)));
		case 2634:
			return -(a * p(b - c, 2) * (b + c) * T * (a10
					* a2
					+ 3
					* a10
					* b
					* c
					- 2
					* a10
					* a
					* (b + c)
					- 10
					* a7
					* b2
					* c2
					* (b + c)
					- 2
					* a
					* b2
					* p(b - c, 2)
					* c2
					* p(b + c, 3)
					* R
					- b
					* c
					* p(b + c, 4)
					* p(b3 - 2 * b2 * c + 2 * b * c2 - c3, 2)
					+ 4
					* a9
					* (b3 + b2 * c + b * c2 + c3)
					+ 2
					* a3
					* p(b - c, 2)
					* p(b + c, 3)
					* (b4 + 3 * b2 * c2 + c4)
					+ a4
					* b
					* c
					* p(b + c, 2)
					* (3 * b4 - 13 * b3 * c + 17 * b2 * c2 - 13 * b * c3 + 3 * c4)
					- a8
					* (5 * b4 + 6 * b3 * c - b2 * c2 + 6 * b * c3 + 5 * c4)
					- a2
					* Q
					* (b6 - 2 * b4 * c2 + 3 * b3 * c3 - 2 * b2 * c4 + c6)
					+ a6
					* (5 * b6 + b5 * c + 2 * b4 * c2 + 13 * b3 * c3 + 2 * b2
							* c4 + b * c5 + 5 * c6) + a5
					* (-4 * b7 - 4 * b6 * c + 6 * b5 * c2 + 6 * b4 * c3 + 6
							* b3 * c4 + 6 * b2 * c5 - 4 * b * c6 - 4 * c7)));
		case 2635:
			return a
					* (a4 * (b + c) - a2 * p(b - c, 2) * (b + c) - 2 * b
							* p(b - c, 2) * c * (b + c) + a * Q - a3 * R);
		case 2636:
			return a
					* (-(b2 * p(b - c, 4) * c2 * p(b + c, 2)) + a * b
							* p(b - c, 4) * c * p(b + c, 3) + a8
							* (b2 - 3 * b * c + c2) + a7
							* (-2 * b3 + 3 * b2 * c + 3 * b * c2 - 2 * c3) + a5
							* p(b - c, 2)
							* (4 * b3 + 3 * b2 * c + 3 * b * c2 + 4 * c3) - a6
							* (b4 - 5 * b3 * c + 9 * b2 * c2 - 5 * b * c3 + c4)
							+ a2 * Q
							* (b4 - b3 * c + 5 * b2 * c2 - b * c3 + c4) - a4
							* p(b - c, 2)
							* (b4 + 3 * b3 * c - b2 * c2 + 3 * b * c3 + c4) - a3
							* p(b - c, 2)
							* (2 * b5 + 3 * b4 * c + 7 * b3 * c2 + 7 * b2 * c3
									+ 3 * b * c4 + 2 * c5));
		case 2637:
			return a2
					* (a - b - c)
					* (b - c)
					* T
					* (a4 * (b + c) - a2 * p(b - c, 2) * (b + c) - 2 * b
							* p(b - c, 2) * c * (b + c) + a * Q - a3 * R);
		case 2638:
			return a3 * p(b - c, 2) * p(-a + b + c, 2) * (T * T);
		case 2639:
			return (a - b)
					* (a - c)
					* (a + b - c)
					* (a - b + c)
					* U
					* V
					* (-(b2 * p(b - c, 4) * c2 * p(b + c, 2)) + a * b
							* p(b - c, 4) * c * p(b + c, 3) + a8
							* (b2 - 3 * b * c + c2) + a7
							* (-2 * b3 + 3 * b2 * c + 3 * b * c2 - 2 * c3) + a5
							* p(b - c, 2)
							* (4 * b3 + 3 * b2 * c + 3 * b * c2 + 4 * c3) - a6
							* (b4 - 5 * b3 * c + 9 * b2 * c2 - 5 * b * c3 + c4)
							+ a2 * Q
							* (b4 - b3 * c + 5 * b2 * c2 - b * c3 + c4) - a4
							* p(b - c, 2)
							* (b4 + 3 * b3 * c - b2 * c2 + 3 * b * c3 + c4) - a3
							* p(b - c, 2)
							* (2 * b5 + 3 * b4 * c + 7 * b3 * c2 + 7 * b2 * c3
									+ 3 * b * c4 + 2 * c5));
		case 2640:
			return a * (a4 - b4 + 3 * b2 * c2 - c4 - a2 * R);
		case 2641:
			return a
					* (2 * a5 + 2 * a * b2 * c2 - a4 * (b + c) - 2 * a3 * R
							- (b + c) * p(b2 - b * c + c2, 2) + a2
							* (3 * b3 - b2 * c - b * c2 + 3 * c3));
		case 2642:
			return a * (b2 - c2) * (-2 * a2 + b2 + c2);
		case 2643:
			return -(a * p(b - c, 2) * p(b + c, 2));
		case 2644:
			return -(a * (a - b) * (a + b) * (a - c) * (a + c) * (a4 - b4 + 3
					* b2 * c2 - c4 - a2 * R));
		case 2645:
			return a
					* (b + c)
					* p(b - c, 2)
					* (a6 + 2 * a5 * (b + c) + 2 * a * b2 * c2 * (b + c) - 3
							* a4 * (b2 + b * c + c2) - 2 * a3
							* (b3 + b2 * c + b * c2 + c3) - b * c
							* (b4 + 2 * b3 * c + b2 * c2 + 2 * b * c3 + c4) + a2
							* (b4 + 3 * b3 * c + 7 * b2 * c2 + 3 * b * c3 + c4));
		case 2646:
			return a * (a - b - c) * (2 * a2 - p(b - c, 2) + a * (b + c));
		case 2647:
			return a
					* (a + b - c)
					* (a - b + c)
					* (a4 + a2 * b * c - a3 * (b + c) - p(b + c, 2)
							* (b2 - 3 * b * c + c2) + a
							* (b3 + b2 * c + b * c2 + c3));
		case 2648:
			return a * (a - b - c)
					* (a3 + b3 + a * (b - 2 * c) * c - 2 * b * c2 + c3)
					* (a3 + b3 - 2 * b2 * c + c3 + a * b * (-2 * b + c));
		case 2649:
			return a
					* (-a + b + c)
					* (2 * a5 - a4 * b - 5 * a3 * b2 + 2 * a2 * b3 + 3 * a * b4
							- b5 - a4 * c + 4 * a3 * b * c - 3 * a2 * b2 * c
							- 4 * a * b3 * c + 2 * b4 * c - 5 * a3 * c2 - 3
							* a2 * b * c2 + 2 * a * b2 * c2 - b3 * c2 + 2 * a2
							* c3 - 4 * a * b * c3 - b2 * c3 + 3 * a * c4 + 2
							* b * c4 - c5);
		case 2650:
			return a * (b + c) * (2 * a2 - p(b - c, 2) + a * (b + c));
		case 2651:
			return a * (a + b) * (a + c)
					* (a3 + b3 + a * b * c + c3 - 2 * a2 * (b + c));
		case 2652:
			return a * (b + c)
					* (a3 + b3 + a * (b - 2 * c) * c - 2 * b * c2 + c3)
					* (a3 + b3 - 2 * b2 * c + c3 + a * b * (-2 * b + c));
		case 2653:
			return a2
					* (b + c)
					* (-b4 + b3 * c + 2 * b2 * c2 + b * c3 - c4 + a3 * (b + c)
							+ 3 * a * b * c * (b + c) + 2 * a2
							* (b2 + b * c + c2));
		case 2654:
			return a
					* (-2 * a4 * b * c + a5 * (b + c) + 2 * b * c * Q - 2 * a3
							* (b3 + c3) + a * (b5 - b4 * c - b * c4 + c5));
		case 2655:
			return a
					* (a + b - c)
					* (a - b + c)
					* (b2 * p(b - c, 2) * c2 * p(b + c, 3) - a2 * p(b - c, 2)
							* p(b + c, 3) * R + a3 * Q * (b2 - b * c + c2) + a
							* b * c * Q * (b2 - b * c + c2) + a7
							* (b2 + b * c + c2) - a6
							* (b3 + b2 * c + b * c2 + c3) - a5
							* (2 * b4 + b3 * c - b2 * c2 + b * c3 + 2 * c4) + a4
							* (2 * b5 + 2 * b4 * c - b3 * c2 - b2 * c3 + 2 * b
									* c4 + 2 * c5));
		case 2656:
			return a
					* (a - b - c)
					* (a6
							* p(b - c, 2)
							* (b + c)
							- b2
							* p(b - c, 3)
							* c2
							* p(b + c, 2)
							+ a7
							* (b2 + b * c - c2)
							+ a
							* b
							* c
							* Q
							* (b2 - b * c + c2)
							+ a2
							* (b + c)
							* p(b3 - b2 * c + b * c2 - c3, 2)
							- a5
							* (2 * b4 + b3 * c - b2 * c2 + b * c3 - 2 * c4)
							+ a4
							* (-2 * b5 + 2 * b4 * c + b3 * c2 - b2 * c3 + 2 * b
									* c4 - 2 * c5) + a3
							* (b6 - b5 * c + b4 * c2 + 2 * b3 * c3 - b2 * c4
									- b * c5 - c6))
					* (-(a6 * p(b - c, 2) * (b + c))
							- b2
							* p(b - c, 3)
							* c2
							* p(b + c, 2)
							+ a7
							* (b2 - b * c - c2)
							- a
							* b
							* c
							* Q
							* (b2 - b * c + c2)
							- a2
							* (b + c)
							* p(b3 - b2 * c + b * c2 - c3, 2)
							+ a5
							* (-2 * b4 + b3 * c - b2 * c2 + b * c3 + 2 * c4)
							+ a4
							* (2 * b5 - 2 * b4 * c + b3 * c2 - b2 * c3 - 2 * b
									* c4 + 2 * c5) + a3
							* (b6 + b5 * c + b4 * c2 - 2 * b3 * c3 - b2 * c4
									+ b * c5 - c6));
		case 2657:
			return a
					* (a - b - c)
					* (2
							* a9
							* b
							* c
							- b2
							* p(b - c, 4)
							* c2
							* p(b + c, 3)
							- a5
							* b
							* c
							* Q
							- a
							* b
							* c
							* p(b2 - c2, 4)
							- 3
							* a7
							* b
							* c
							* R
							+ 3
							* a3
							* b
							* c
							* Q
							* R
							+ a8
							* (b3 + c3)
							- 3
							* a6
							* (b5 + c5)
							+ a4
							* (3 * b7 - b5 * c2 - 2 * b4 * c3 - 2 * b3 * c4
									- b2 * c5 + 3 * c7) - a2
							* (b9 - 2 * b7 * c2 + b5 * c4 + b4 * c5 - 2 * b2
									* c7 + c9));
		case 2658:
			return a2
					* (b + c)
					* T
					* (a4 * (b + c) - a2 * p(b - c, 2) * (b + c) - 2 * b
							* p(b - c, 2) * c * (b + c) - a * Q + a3 * R);
		case 2659:
			return (a + b)
					* (a + c)
					* U
					* V
					* (b2 * p(b - c, 2) * c2 * p(b + c, 3) - a2 * p(b - c, 2)
							* p(b + c, 3) * R + a3 * Q * (b2 - b * c + c2) + a
							* b * c * Q * (b2 - b * c + c2) + a7
							* (b2 + b * c + c2) - a6
							* (b3 + b2 * c + b * c2 + c3) - a5
							* (2 * b4 + b3 * c - b2 * c2 + b * c3 + 2 * c4) + a4
							* (2 * b5 + 2 * b4 * c - b3 * c2 - b2 * c3 + 2 * b
									* c4 + 2 * c5));
		case 2660:
			return a2
					* (b + c)
					* T
					* (a6
							* p(b - c, 2)
							* (b + c)
							- b2
							* p(b - c, 3)
							* c2
							* p(b + c, 2)
							+ a7
							* (b2 + b * c - c2)
							+ a
							* b
							* c
							* Q
							* (b2 - b * c + c2)
							+ a2
							* (b + c)
							* p(b3 - b2 * c + b * c2 - c3, 2)
							- a5
							* (2 * b4 + b3 * c - b2 * c2 + b * c3 - 2 * c4)
							+ a4
							* (-2 * b5 + 2 * b4 * c + b3 * c2 - b2 * c3 + 2 * b
									* c4 - 2 * c5) + a3
							* (b6 - b5 * c + b4 * c2 + 2 * b3 * c3 - b2 * c4
									- b * c5 - c6))
					* (-(a6 * p(b - c, 2) * (b + c))
							- b2
							* p(b - c, 3)
							* c2
							* p(b + c, 2)
							+ a7
							* (b2 - b * c - c2)
							- a
							* b
							* c
							* Q
							* (b2 - b * c + c2)
							- a2
							* (b + c)
							* p(b3 - b2 * c + b * c2 - c3, 2)
							+ a5
							* (-2 * b4 + b3 * c - b2 * c2 + b * c3 + 2 * c4)
							+ a4
							* (2 * b5 - 2 * b4 * c + b3 * c2 - b2 * c3 - 2 * b
									* c4 + 2 * c5) + a3
							* (b6 + b5 * c + b4 * c2 - 2 * b3 * c3 - b2 * c4
									+ b * c5 - c6));
		case 2661:
			return a2
					* (b + c)
					* T
					* (2
							* a10
							* a
							* b
							* c
							* (b + c)
							- b3
							* c3
							* p(b2 - c2, 4)
							- a
							* b2
							* p(b - c, 4)
							* c2
							* p(b + c, 3)
							* (2 * b2 + b * c + 2 * c2)
							+ a10
							* (b4 + b3 * c + 2 * b2 * c2 + b * c3 + c4)
							- a2
							* b
							* p(b - c, 4)
							* c
							* p(b + c, 2)
							* (2 * b4 + 3 * b3 * c + 5 * b2 * c2 + 3 * b * c3 + 2 * c4)
							+ a9
							* (b5 - 4 * b4 * c - 4 * b * c4 + c5)
							- a4
							* Q
							* (b6 - 5 * b5 * c + 2 * b4 * c2 - 7 * b3 * c3 + 2
									* b2 * c4 - 5 * b * c5 + c6)
							- a3
							* p(b - c, 2)
							* p(b + c, 3)
							* (b6 + b5 * c - 4 * b4 * c2 + 5 * b3 * c3 - 4 * b2
									* c4 + b * c5 + c6)
							- a8
							* (3 * b6 + b5 * c + 2 * b4 * c2 + 2 * b3 * c3 + 2
									* b2 * c4 + b * c5 + 3 * c6)
							+ a6
							* p(b - c, 2)
							* (3 * b6 + 3 * b5 * c + 2 * b4 * c2 + 3 * b3 * c3
									+ 2 * b2 * c4 + 3 * b * c5 + 3 * c6)
							- a7
							* (3 * b7 + 5 * b5 * c2 - 5 * b4 * c3 - 5 * b3 * c4
									+ 5 * b2 * c5 + 3 * c7) + a5
							* (3 * b9 + 4 * b8 * c - 9 * b6 * c3 + 2 * b5 * c4
									+ 2 * b4 * c5 - 9 * b3 * c6 + 4 * b * c8 + 3 * c9));
		case 2662:
			return a
					* (a + b - c)
					* (a - b + c)
					* (-(a7 * b * c * (b + c))
							+ a3
							* b
							* p(b - c, 2)
							* c
							* p(b + c, 3)
							- b2
							* p(b - c, 2)
							* c2
							* p(b + c, 4)
							- a
							* b
							* p(b - c, 2)
							* c
							* p(b + c, 3)
							* R
							+ a8
							* (b2 + 3 * b * c + c2)
							+ a5
							* b
							* c
							* (b3 + b2 * c + b * c2 + c3)
							- a2
							* Q
							* (b4 - b3 * c + b2 * c2 - b * c3 + c4)
							+ a4
							* p(b + c, 2)
							* (3 * b4 - 5 * b3 * c + 9 * b2 * c2 - 5 * b * c3 + 3 * c4) - a6
							* (3 * b4 + 5 * b3 * c + 3 * b2 * c2 + 5 * b * c3 + 3 * c4));
		case 2663:
			return a
					* (b2 * c2 + a * b * c * (b + c) + a2
							* (b2 + 3 * b * c + c2));
		case 2664:
			return a
					* (-(b2 * c2) - a * b * c * (b + c) + a2
							* (b2 + b * c + c2));
		case 2665:
			return a * (a * b * (b - c) * c + b2 * c2 + a2 * (b2 - b * c - c2))
					* (a * b * (b - c) * c - b2 * c2 + a2 * (b2 + b * c - c2));
		case 2666:
			return a
					* ((3 + a / b + b / c + c / a) * (1 / c + 1 / a) + (3 + b
							/ a + c / b + a / c)
							* (1 / b + 1 / a));
		case 2667:
			return a2 * (b + c) * (2 * b * c + a * (b + c));
		case 2668:
			return (a + b)
					* (a + c)
					* (b2 * c2 + a * b * c * (b + c) + a2
							* (b2 + 3 * b * c + c2));
		case 2669:
			return (a + b)
					* (a + c)
					* (-(b2 * c2) - a * b * c * (b + c) + a2
							* (b2 + b * c + c2));
		case 2670:
			return a
					* ((b * a2 / (b + a) + c * b2 / (c + b) + a * c2 / (a + c))
							* c * a / (c + a) + (c * a2 / (c + a) + a * b2
							/ (a + b) + b * c2 / (b + c))
							* b * a / (b + a));
		case 2671:
			return 1 / (4 * (-T) + (3 + u(5)) * S);
		case 2672:
			return 1 / (4 * (-T) - (3 + u(5)) * S);
		case 2673:
			return a2 * (4 * (-T) + (3 + u(5)) * S);
		case 2674:
			return a2 * (4 * (-T) - (3 + u(5)) * S);
		case 2675:
			return 17 * a4 - 15 * Q - 2 * a2 * R + 3 * (a + b - c)
					* (a - b + c) * (-a + b + c) * (a + b + c) * u(5);
		case 2676:
			return a4 - 15 * Q + 14 * a2 * R + 3 * (a + b - c) * (a - b + c)
					* (-a + b + c) * (a + b + c) * u(5);
		case 2677:
			return p(b - c, 2)
					* (b + c)
					* (-(a2 * b) + b3 - a2 * c + 2 * a * b * c - b2 * c - b
							* c2 + c3)
					* (a3 - a2 * b - a * b2 + b3 - a2 * c - a * b * c + b2 * c
							- a * c2 + b * c2 + c3);
		case 2678:
			return p(b - c, 2)
					* (b + c)
					* (-2 * a3 + a2 * b + b3 + a2 * c - b2 * c - b * c2 + c3)
					* (a3 * b - a2 * b2 - a * b3 + b4 + a3 * c - a2 * b * c - a
							* b2 * c + b3 * c - a2 * c2 - a * b * c2 + b2 * c2
							- a * c3 + b * c3 + c4);
		case 2679:
			return a2 * p(b - c, 2) * p(b + c, 2) * (a2 - b * c) * (a2 + b * c)
					* (-(a2 * b2) + b4 - a2 * c2 + c4);
		case 2680:
			return a
					* p(b - c, 2)
					* (b + c)
					* (a3 + a * b * c - b2 * c - b * c2)
					* (-(a2 * b) + b3 - a2 * c + 2 * a * b * c - b2 * c - b
							* c2 + c3);
		case 2681:
			return p(b - c, 2) * (b + c)
					* (a2 + a * b - b2 + a * c - b * c - c2)
					* (-2 * a3 + a2 * b + b3 + a2 * c - b2 * c - b * c2 + c3);
		case 2682:
			return p(b - c, 2) * p(b + c, 2) * (2 * a2 - b2 - c2)
					* (-2 * a4 + a2 * b2 + b4 + a2 * c2 - 2 * b2 * c2 + c4);
		case 2683:
			return a
					* (-(a2 * b) + b3 - a2 * c + 2 * a * b * c - b2 * c - b
							* c2 + c3)
					* (-(a2 * b2) + b4 - a2 * c2 + c4)
					* (a5 * b - a3 * b3 + a5 * c - 2 * a4 * b * c + a2 * b3 * c
							+ a * b4 * c - b5 * c - a * b3 * c2 - a3 * c3 + a2
							* b * c3 - a * b2 * c3 + 2 * b3 * c3 + a * b * c4 - b
							* c5);
		case 2684:
			return (-2 * a3 + a2 * b + b3 + a2 * c - b2 * c - b * c2 + c3)
					* (-(a2 * b2) + b4 - a2 * c2 + c4)
					* (2 * a5 - a4 * b - a3 * b2 + a * b4 - b5 - a4 * c + a2
							* b2 * c - a3 * c2 + a2 * b * c2 - 2 * a * b2 * c2
							+ b3 * c2 + b2 * c3 + a * c4 - c5);
		case 2685:
			return (-2 * a3 + a2 * b + b3 + a2 * c - b2 * c - b * c2 + c3)
					* (-2 * a4 + a2 * b2 + b4 + a2 * c2 - 2 * b2 * c2 + c4)
					* (a5 * b2 - a4 * b3 - 2 * a3 * b4 + 2 * a2 * b5 + a * b6
							- b7 + a5 * c2 + 2 * a3 * b2 * c2 - a2 * b3 * c2
							- a * b4 * c2 - b5 * c2 - a4 * c3 - a2 * b2 * c3
							+ 2 * b4 * c3 - 2 * a3 * c4 - a * b2 * c4 + 2 * b3
							* c4 + 2 * a2 * c5 - b2 * c5 + a * c6 - c7);
		case 2686:
			return p(b - c, 2)
					* p(b + c, 2)
					* (-5 * a2 + b2 + c2)
					* (-2 * a4 + a2 * b2 + b4 + a2 * c2 - 2 * b2 * c2 + c4)
					* (a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 9 * a2 * b2 * c2
							- 4 * b4 * c2 - a2 * c4 - 4 * b2 * c4 + c6);
		case 2687:
			return a
					* (-a6 + a4 * b2 + a2 * b4 - b6 + a5 * c - 2 * a4 * b * c
							+ a3 * b2 * c + a2 * b3 * c - 2 * a * b4 * c + b5
							* c + 2 * a4 * c2 - 2 * a2 * b2 * c2 + 2 * b4 * c2
							- 2 * a3 * c3 + a2 * b * c3 + a * b2 * c3 - 2 * b3
							* c3 - a2 * c4 - b2 * c4 + a * c5 + b * c5)
					* (-a6 + a5 * b + 2 * a4 * b2 - 2 * a3 * b3 - a2 * b4 + a
							* b5 - 2 * a4 * b * c + a2 * b3 * c + b5 * c + a4
							* c2 + a3 * b * c2 - 2 * a2 * b2 * c2 + a * b3 * c2
							- b4 * c2 + a2 * b * c3 - 2 * b3 * c3 + a2 * c4 - 2
							* a * b * c4 + 2 * b2 * c4 + b * c5 - c6);
		case 2688:
			return (-a7 - a5 * b2 + 2 * a4 * b3 + 2 * a3 * b4 - a2 * b5 - b7
					+ a6 * c - a4 * b2 * c - a2 * b4 * c + b6 * c + 2 * a5 * c2
					- a3 * b2 * c2 - a2 * b3 * c2 + 2 * b5 * c2 - 2 * a4 * c3
					+ 2 * a2 * b2 * c3 - 2 * b4 * c3 - a3 * c4 - b3 * c4 + a2
					* c5 + b2 * c5)
					* (-a7 + a6 * b + 2 * a5 * b2 - 2 * a4 * b3 - a3 * b4 + a2
							* b5 - a5 * c2 - a4 * b * c2 - a3 * b2 * c2 + 2
							* a2 * b3 * c2 + b5 * c2 + 2 * a4 * c3 - a2 * b2
							* c3 - b4 * c3 + 2 * a3 * c4 - a2 * b * c4 - 2 * b3
							* c4 - a2 * c5 + 2 * b2 * c5 + b * c6 - c7);
		case 2689:
			return (a - b)
					* (-a + c)
					* (a5 + b5 + a3 * b * c + a2 * b2 * c + a * b3 * c - 2 * a3
							* c2 - a2 * b * c2 - a * b2 * c2 - 2 * b3 * c2 - a
							* b * c3 + a * c4 + b * c4)
					* (a5 - 2 * a3 * b2 + a * b4 + a3 * b * c - a2 * b2 * c - a
							* b3 * c + b4 * c + a2 * b * c2 - a * b2 * c2 + a
							* b * c3 - 2 * b2 * c3 + c5);
		case 2690:
			return (a - b)
					* (-a + c)
					* (a4 + a3 * b + a2 * b2 + a * b3 + b4 - a3 * c - a2 * b
							* c - a * b2 * c - b3 * c - a2 * c2 - a * b * c2
							- b2 * c2 + a * c3 + b * c3)
					* (a4 - a3 * b - a2 * b2 + a * b3 + a3 * c - a2 * b * c - a
							* b2 * c + b3 * c + a2 * c2 - a * b * c2 - b2 * c2
							+ a * c3 - b * c3 + c4);
		case 2691:
			return a
					* (a - b)
					* (-a + c)
					* (a5 - a4 * b - a * b4 + b5 - a4 * c - a3 * b * c + 4 * a2
							* b2 * c - a * b3 * c - b4 * c - 2 * a2 * b * c2
							+ 4 * a * b2 * c2 - a * b * c3 - a * c4 - b * c4 + c5)
					* (a5 - a4 * b - a * b4 + b5 - a4 * c - a3 * b * c - 2 * a2
							* b2 * c - a * b3 * c - b4 * c + 4 * a2 * b * c2
							+ 4 * a * b2 * c2 - a * b * c3 - a * c4 - b * c4 + c5);
		case 2692:
			return (a - b)
					* (-a + c)
					* (a5 - 2 * a4 * b - 2 * a3 * b2 - 2 * a2 * b3 - 2 * a * b4
							+ b5 + 3 * a3 * b * c + 3 * a2 * b2 * c + 3 * a
							* b3 * c - 2 * a3 * c2 + a2 * b * c2 + a * b2 * c2
							- 2 * b3 * c2 - 3 * a * b * c3 + a * c4 + b * c4)
					* (a5 - 2 * a3 * b2 + a * b4 - 2 * a4 * c + 3 * a3 * b * c
							+ a2 * b2 * c - 3 * a * b3 * c + b4 * c - 2 * a3
							* c2 + 3 * a2 * b * c2 + a * b2 * c2 - 2 * a2 * c3
							+ 3 * a * b * c3 - 2 * b2 * c3 - 2 * a * c4 + c5);
		case 2693:
			return a2
					* (-a10 - a8 * b2 + 7 * a6 * b4 - 5 * a4 * b6 - 2 * a2 * b8
							+ 2 * b10 + 3 * a8 * c2 - 6 * a6 * b2 * c2 - 7 * a4
							* b4 * c2 + 12 * a2 * b6 * c2 - 2 * b8 * c2 - 2
							* a6 * c4 + 14 * a4 * b2 * c4 - 7 * a2 * b4 * c4
							- 5 * b6 * c4 - 2 * a4 * c6 - 6 * a2 * b2 * c6 + 7
							* b4 * c6 + 3 * a2 * c8 - b2 * c8 - c10)
					* (-a10 + 3 * a8 * b2 - 2 * a6 * b4 - 2 * a4 * b6 + 3 * a2
							* b8 - b10 - a8 * c2 - 6 * a6 * b2 * c2 + 14 * a4
							* b4 * c2 - 6 * a2 * b6 * c2 - b8 * c2 + 7 * a6
							* c4 - 7 * a4 * b2 * c4 - 7 * a2 * b4 * c4 + 7 * b6
							* c4 - 5 * a4 * c6 + 12 * a2 * b2 * c6 - 5 * b4
							* c6 - 2 * a2 * c8 - 2 * b2 * c8 + 2 * c10);
		case 2694:
			return a
					* (-a9 + a8 * b + 2 * a7 * b2 - 2 * a6 * b3 - 2 * a3 * b6
							+ 2 * a2 * b7 + a * b8 - b9 - 3 * a7 * b * c + 3
							* a5 * b3 * c + 3 * a3 * b5 * c - 3 * a * b7 * c
							+ 2 * a7 * c2 + a6 * b * c2 - 7 * a5 * b2 * c2 + 4
							* a4 * b3 * c2 + 4 * a3 * b4 * c2 - 7 * a2 * b5
							* c2 + a * b6 * c2 + 2 * b7 * c2 + 4 * a5 * b * c3
							- 8 * a3 * b3 * c3 + 4 * a * b5 * c3 - 4 * a4 * b
							* c4 + 4 * a3 * b2 * c4 + 4 * a2 * b3 * c4 - 4 * a
							* b4 * c4 + a3 * b * c5 + a * b3 * c5 - 2 * a3 * c6
							+ a2 * b * c6 + a * b2 * c6 - 2 * b3 * c6 - 2 * a
							* b * c7 + a * c8 + b * c8)
					* (-a9 + 2 * a7 * b2 - 2 * a3 * b6 + a * b8 + a8 * c - 3
							* a7 * b * c + a6 * b2 * c + 4 * a5 * b3 * c - 4
							* a4 * b4 * c + a3 * b5 * c + a2 * b6 * c - 2 * a
							* b7 * c + b8 * c + 2 * a7 * c2 - 7 * a5 * b2 * c2
							+ 4 * a3 * b4 * c2 + a * b6 * c2 - 2 * a6 * c3 + 3
							* a5 * b * c3 + 4 * a4 * b2 * c3 - 8 * a3 * b3 * c3
							+ 4 * a2 * b4 * c3 + a * b5 * c3 - 2 * b6 * c3 + 4
							* a3 * b2 * c4 - 4 * a * b4 * c4 + 3 * a3 * b * c5
							- 7 * a2 * b2 * c5 + 4 * a * b3 * c5 - 2 * a3 * c6
							+ a * b2 * c6 + 2 * a2 * c7 - 3 * a * b * c7 + 2
							* b2 * c7 + a * c8 - c9);
		case 2695:
			return (-a8 + a7 * b - a6 * b2 - a5 * b3 + 4 * a4 * b4 - a3 * b5
					- a2 * b6 + a * b7 - b8 - a6 * b * c + 2 * a5 * b2 * c - a4
					* b3 * c - a3 * b4 * c + 2 * a2 * b5 * c - a * b6 * c + 3
					* a6 * c2 - 2 * a5 * b * c2 - 2 * a4 * b2 * c2 + 2 * a3
					* b3 * c2 - 2 * a2 * b4 * c2 - 2 * a * b5 * c2 + 3 * b6
					* c2 + 2 * a4 * b * c3 - a3 * b2 * c3 - a2 * b3 * c3 + 2
					* a * b4 * c3 - 3 * a4 * c4 + a3 * b * c4 + 2 * a2 * b2
					* c4 + a * b3 * c4 - 3 * b4 * c4 - a2 * b * c5 - a * b2
					* c5 + a2 * c6 + b2 * c6)
					* (-a8 + 3 * a6 * b2 - 3 * a4 * b4 + a2 * b6 + a7 * c - a6
							* b * c - 2 * a5 * b2 * c + 2 * a4 * b3 * c + a3
							* b4 * c - a2 * b5 * c - a6 * c2 + 2 * a5 * b * c2
							- 2 * a4 * b2 * c2 - a3 * b3 * c2 + 2 * a2 * b4
							* c2 - a * b5 * c2 + b6 * c2 - a5 * c3 - a4 * b
							* c3 + 2 * a3 * b2 * c3 - a2 * b3 * c3 + a * b4
							* c3 + 4 * a4 * c4 - a3 * b * c4 - 2 * a2 * b2 * c4
							+ 2 * a * b3 * c4 - 3 * b4 * c4 - a3 * c5 + 2 * a2
							* b * c5 - 2 * a * b2 * c5 - a2 * c6 - a * b * c6
							+ 3 * b2 * c6 + a * c7 - c8);
		case 2696:
			return (a - b)
					* (a + b)
					* (-a + c)
					* (a + c)
					* (a6 - a4 * b2 - a2 * b4 + b6 - 4 * a4 * c2 + 9 * a2 * b2
							* c2 - b4 * c2 - 4 * a2 * c4 - b2 * c4 + c6)
					* (a6 - 4 * a4 * b2 - 4 * a2 * b4 + b6 - a4 * c2 + 9 * a2
							* b2 * c2 - b4 * c2 - a2 * c4 - b2 * c4 + c6);
		case 2697:
			return (-a10 + a6 * b4 + a4 * b6 - b10 + 2 * a8 * c2 - 2 * a6 * b2
					* c2 - 2 * a2 * b6 * c2 + 2 * b8 * c2 + a4 * b2 * c4 + a2
					* b4 * c4 - 2 * a4 * c6 - 2 * b4 * c6 + a2 * c8 + b2 * c8)
					* (-a10 + 2 * a8 * b2 - 2 * a4 * b6 + a2 * b8 - 2 * a6 * b2
							* c2 + a4 * b4 * c2 + b8 * c2 + a6 * c4 + a2 * b4
							* c4 - 2 * b6 * c4 + a4 * c6 - 2 * a2 * b2 * c6 + 2
							* b2 * c8 - c10);
		case 2698:
			return a2
					* (-(a4 * b4) + a2 * b6 - a6 * c2 + a4 * b2 * c2 - 2 * a2
							* b4 * c2 + b6 * c2 + 2 * a4 * c4 + a2 * b2 * c4
							- b4 * c4 - a2 * c6)
					* (-(a6 * b2) + 2 * a4 * b4 - a2 * b6 + a4 * b2 * c2 + a2
							* b4 * c2 - a4 * c4 - 2 * a2 * b2 * c4 - b4 * c4
							+ a2 * c6 + b2 * c6);
		case 2699:
			return a2
					* (-(a3 * b3) + a * b5 - a5 * c + a4 * b * c + a3 * b2 * c
							- 2 * a * b4 * c + b5 * c - a3 * b * c2 + 2 * a3
							* c3 - a2 * b * c3 + a * b2 * c3 - b3 * c3 + a * b
							* c4 - a * c5)
					* (-(a5 * b) + 2 * a3 * b3 - a * b5 + a4 * b * c - a3 * b2
							* c - a2 * b3 * c + a * b4 * c + a3 * b * c2 + a
							* b3 * c2 - a3 * c3 - b3 * c3 - 2 * a * b * c4 + a
							* c5 + b * c5);
		default:
			return Double.NaN;
		}
	}

	private double weight2700to2749(int k, double a, double b, double c) {
		switch (k) {
		case 2700:
			return a2
					* (-a5 + a4 * b - a2 * b3 - a * b4 + 2 * b5 + a2 * b2 * c
							- b4 * c + a3 * c2 - 2 * a2 * b * c2 + a * b2 * c2
							- b3 * c2 + a2 * c3 + b * c4 - c5)
					* (-a5 + a3 * b2 + a2 * b3 - b5 + a4 * c - 2 * a2 * b2 * c
							+ b4 * c + a2 * b * c2 + a * b2 * c2 - a2 * c3 - b2
							* c3 - a * c4 - b * c4 + 2 * c5);
		case 2701:
			return a2 * (a - b) * (-a + c)
					* (a3 - 2 * a * b2 + b3 + a * b * c - 2 * b2 * c + c3)
					* (a3 + b3 + a * b * c - 2 * a * c2 - 2 * b * c2 + c3);
		case 2702:
			return a2 * (a - b) * (-a + c)
					* (-a2 + a * b + b2 - a * c + b * c - c2)
					* (-a2 - a * b - b2 + a * c + b * c + c2);
		case 2703:
			return a2 * (a - b) * (-a + c) * (b3 - a2 * c + a * b * c - a * c2)
					* (-(a2 * b) - a * b2 + a * b * c + c3);
		case 2704:
			return a2
					* (a - b)
					* (-a + c)
					* (a2 * b3 - 2 * a * b4 + b5 - a4 * c + a3 * b * c - a2
							* b2 * c + a * b3 * c - 2 * b4 * c + a3 * c2 - a
							* b2 * c2 + b3 * c2 + a2 * c3 + a * b * c3 - a * c4)
					* (-(a4 * b) + a3 * b2 + a2 * b3 - a * b4 + a3 * b * c + a
							* b3 * c - a2 * b * c2 - a * b2 * c2 + a2 * c3 + a
							* b * c3 + b2 * c3 - 2 * a * c4 - 2 * b * c4 + c5);
		case 2705:
			return a2
					* (a - b)
					* (-a + c)
					* (a3 - 2 * a * b2 + 3 * b3 - 2 * a2 * c + 3 * a * b * c
							- 2 * b2 * c - 2 * a * c2 + c3)
					* (a3 - 2 * a2 * b - 2 * a * b2 + b3 + 3 * a * b * c - 2
							* a * c2 - 2 * b * c2 + 3 * c3);
		case 2706:
			return a2
					* (-(a8 * b4) + 3 * a6 * b6 - 3 * a4 * b8 + a2 * b10 - a10
							* c2 - a8 * b2 * c2 + a6 * b4 * c2 - 2 * a4 * b6
							* c2 + 2 * a2 * b8 * c2 + b10 * c2 + 4 * a8 * c4
							+ a6 * b2 * c4 - 2 * a2 * b6 * c4 - 3 * b8 * c4 - 6
							* a6 * c6 + a4 * b2 * c6 + a2 * b4 * c6 + 3 * b6
							* c6 + 4 * a4 * c8 - a2 * b2 * c8 - b4 * c8 - a2
							* c10)
					* (-(a10 * b2) + 4 * a8 * b4 - 6 * a6 * b6 + 4 * a4 * b8
							- a2 * b10 - a8 * b2 * c2 + a6 * b4 * c2 + a4 * b6
							* c2 - a2 * b8 * c2 - a8 * c4 + a6 * b2 * c4 + a2
							* b6 * c4 - b8 * c4 + 3 * a6 * c6 - 2 * a4 * b2
							* c6 - 2 * a2 * b4 * c6 + 3 * b6 * c6 - 3 * a4 * c8
							+ 2 * a2 * b2 * c8 - 3 * b4 * c8 + a2 * c10 + b2
							* c10);
		case 2707:
			return a2
					* (-(a6 * b3) + a5 * b4 + 2 * a4 * b5 - 2 * a3 * b6 - a2
							* b7 + a * b8 - a8 * c + a6 * b2 * c - 2 * a4 * b4
							* c + a2 * b6 * c + b8 * c + a7 * c2 - 2 * a6 * b
							* c2 + a5 * b2 * c2 + a4 * b3 * c2 + a3 * b4 * c2
							- 2 * a2 * b5 * c2 + a * b6 * c2 - b7 * c2 + 3 * a6
							* c3 - 2 * a4 * b2 * c3 + a2 * b4 * c3 - 2 * b6
							* c3 - 3 * a5 * c4 + 4 * a4 * b * c4 - 2 * a3 * b2
							* c4 + a2 * b3 * c4 - 2 * a * b4 * c4 + 2 * b5 * c4
							- 3 * a4 * c5 + a2 * b2 * c5 + b4 * c5 + 3 * a3
							* c6 - 2 * a2 * b * c6 + a * b2 * c6 - b3 * c6 + a2
							* c7 - a * c8)
					* (-(a8 * b) + a7 * b2 + 3 * a6 * b3 - 3 * a5 * b4 - 3 * a4
							* b5 + 3 * a3 * b6 + a2 * b7 - a * b8 - 2 * a6 * b2
							* c + 4 * a4 * b4 * c - 2 * a2 * b6 * c + a6 * b
							* c2 + a5 * b2 * c2 - 2 * a4 * b3 * c2 - 2 * a3
							* b4 * c2 + a2 * b5 * c2 + a * b6 * c2 - a6 * c3
							+ a4 * b2 * c3 + a2 * b4 * c3 - b6 * c3 + a5 * c4
							- 2 * a4 * b * c4 + a3 * b2 * c4 + a2 * b3 * c4 - 2
							* a * b4 * c4 + b5 * c4 + 2 * a4 * c5 - 2 * a2 * b2
							* c5 + 2 * b4 * c5 - 2 * a3 * c6 + a2 * b * c6 + a
							* b2 * c6 - 2 * b3 * c6 - a2 * c7 - b2 * c7 + a
							* c8 + b * c8);
		case 2708:
			return a2
					* (-a6 + a4 * b2 + a3 * b3 - 2 * a2 * b4 - a * b5 + 2 * b6
							+ a5 * c - a4 * b * c - a3 * b2 * c + 2 * a * b4
							* c - b5 * c + a4 * c2 + a3 * b * c2 - 2 * b4 * c2
							- 2 * a3 * c3 + a2 * b * c3 - a * b2 * c3 + b3 * c3
							+ a2 * c4 - a * b * c4 + b2 * c4 + a * c5 - c6)
					* (-a6 + a5 * b + a4 * b2 - 2 * a3 * b3 + a2 * b4 + a * b5
							- b6 - a4 * b * c + a3 * b2 * c + a2 * b3 * c - a
							* b4 * c + a4 * c2 - a3 * b * c2 - a * b3 * c2 + b4
							* c2 + a3 * c3 + b3 * c3 - 2 * a2 * c4 + 2 * a * b
							* c4 - 2 * b2 * c4 - a * c5 - b * c5 + 2 * c6);
		case 2709:
			return a2 * (a - b) * (a + b) * (-a + c) * (a + c)
					* (a4 - a2 * b2 + 4 * b4 - 4 * a2 * c2 - b2 * c2 + c4)
					* (a4 - 4 * a2 * b2 + b4 - a2 * c2 - b2 * c2 + 4 * c4);
		case 2710:
			return a2
					* (-a8 + a4 * b4 - 2 * a2 * b6 + 2 * b8 + 2 * a6 * c2 - 2
							* b6 * c2 - 2 * a4 * c4 + b4 * c4 + 2 * a2 * c6 - c8)
					* (-a8 + 2 * a6 * b2 - 2 * a4 * b4 + 2 * a2 * b6 - b8 + a4
							* c4 + b4 * c4 - 2 * a2 * c6 - 2 * b2 * c6 + 2 * c8);
		case 2711:
			return a2
					* (-(a2 * b3) + a * b4 - a4 * c - a2 * b2 * c + b4 * c + a3
							* c2 + 2 * a2 * b * c2 - a * b2 * c2 - b3 * c2 + a2
							* c3 - a * c4)
					* (-(a4 * b) + a3 * b2 + a2 * b3 - a * b4 + 2 * a2 * b2 * c
							- a2 * b * c2 - a * b2 * c2 - a2 * c3 - b2 * c3 + a
							* c4 + b * c4);
		case 2712:
			return a2
					* (-a3 - a2 * b - a * b2 + 2 * b3 + 2 * a2 * c - b2 * c + 2
							* a * c2 - b * c2 - c3)
					* (-a3 + 2 * a2 * b + 2 * a * b2 - b3 - a2 * c - b2 * c - a
							* c2 - b * c2 + 2 * c3);
		case 2713:
			return a2
					* (a - b)
					* (a + b)
					* (-a + c)
					* (a + c)
					* (-(a2 * b2) + b4 - a3 * c + a * b2 * c + 2 * a2 * c2 - b2
							* c2 - a * c3)
					* (-(a2 * b2) + b4 + a3 * c - a * b2 * c + 2 * a2 * c2 - b2
							* c2 + a * c3)
					* (a3 * b + 2 * a2 * b2 + a * b3 - a2 * c2 - a * b * c2
							- b2 * c2 + c4)
					* (-(a3 * b) + 2 * a2 * b2 - a * b3 - a2 * c2 + a * b * c2
							- b2 * c2 + c4);
		case 2714:
			return a2
					* (a - b)
					* (-a + c)
					* (a3 * b3 - a2 * b4 - a * b5 + b6 - a5 * c + a2 * b3 * c
							+ a * b4 * c - b5 * c - 2 * a3 * b * c2 + 2 * a2
							* b2 * c2 + a * b3 * c2 - b4 * c2 + 2 * a3 * c3 - 2
							* a2 * b * c3 + b3 * c3 - a * c5)
					* (-(a5 * b) + 2 * a3 * b3 - a * b5 - 2 * a3 * b2 * c - 2
							* a2 * b3 * c + 2 * a2 * b2 * c2 + a3 * c3 + a2 * b
							* c3 + a * b2 * c3 + b3 * c3 - a2 * c4 + a * b * c4
							- b2 * c4 - a * c5 - b * c5 + c6);
		case 2715:
			return a2 * (a - b) * (a + b) * (-a + c) * (a + c)
					* (-a4 - b4 + a2 * c2 + b2 * c2)
					* (-a4 + a2 * b2 + b2 * c2 - c4);
		case 2716:
			return a
					* (-a6 + a5 * b + a4 * b2 - 2 * a3 * b3 + a2 * b4 + a * b5
							- b6 + a5 * c - 4 * a4 * b * c + 3 * a3 * b2 * c
							+ 3 * a2 * b3 * c - 4 * a * b4 * c + b5 * c + 2
							* a4 * c2 + a3 * b * c2 - 6 * a2 * b2 * c2 + a * b3
							* c2 + 2 * b4 * c2 - 2 * a3 * c3 + 3 * a2 * b * c3
							+ 3 * a * b2 * c3 - 2 * b3 * c3 - a2 * c4 - 2 * a
							* b * c4 - b2 * c4 + a * c5 + b * c5)
					* (-a6 + a5 * b + 2 * a4 * b2 - 2 * a3 * b3 - a2 * b4 + a
							* b5 + a5 * c - 4 * a4 * b * c + a3 * b2 * c + 3
							* a2 * b3 * c - 2 * a * b4 * c + b5 * c + a4 * c2
							+ 3 * a3 * b * c2 - 6 * a2 * b2 * c2 + 3 * a * b3
							* c2 - b4 * c2 - 2 * a3 * c3 + 3 * a2 * b * c3 + a
							* b2 * c3 - 2 * b3 * c3 + a2 * c4 - 4 * a * b * c4
							+ 2 * b2 * c4 + a * c5 + b * c5 - c6);
		case 2717:
			return a
					* (-a5 + a3 * b2 + a2 * b3 - b5 + 2 * a4 * c - 2 * a3 * b
							* c - 2 * a * b3 * c + 2 * b4 * c + a2 * b * c2 + a
							* b2 * c2 - 2 * a2 * c3 - 2 * b2 * c3 + a * c4 + b
							* c4)
					* (-a5 + 2 * a4 * b - 2 * a2 * b3 + a * b4 - 2 * a3 * b * c
							+ a2 * b2 * c + b4 * c + a3 * c2 + a * b2 * c2 - 2
							* b3 * c2 + a2 * c3 - 2 * a * b * c3 + 2 * b * c4 - c5);
		case 2718:
			return a
					* (-a3 + 2 * a2 * b + 2 * a * b2 - b3 - 4 * a * b * c + a
							* c2 + b * c2)
					* (-a3 + a * b2 + 2 * a2 * c - 4 * a * b * c + b2 * c + 2
							* a * c2 - c3);
		case 2719:
			return a2
					* (a - b)
					* (-a + c)
					* (a4 * b3 - 2 * a2 * b5 + b7 - a6 * c + a5 * b * c - 2
							* a4 * b2 * c + 2 * a3 * b3 * c + 3 * a2 * b4 * c
							- 3 * a * b5 * c - a5 * c2 - 2 * a3 * b2 * c2 + 2
							* a2 * b3 * c2 + 3 * a * b4 * c2 - 2 * b5 * c2 + 2
							* a4 * c3 - 2 * a3 * b * c3 - 2 * a2 * b2 * c3 + 2
							* a * b3 * c3 + 2 * a3 * c4 - 2 * a * b2 * c4 + b3
							* c4 - a2 * c5 + a * b * c5 - a * c6)
					* (-(a6 * b) - a5 * b2 + 2 * a4 * b3 + 2 * a3 * b4 - a2
							* b5 - a * b6 + a5 * b * c - 2 * a3 * b3 * c + a
							* b5 * c - 2 * a4 * b * c2 - 2 * a3 * b2 * c2 - 2
							* a2 * b3 * c2 - 2 * a * b4 * c2 + a4 * c3 + 2 * a3
							* b * c3 + 2 * a2 * b2 * c3 + 2 * a * b3 * c3 + b4
							* c3 + 3 * a2 * b * c4 + 3 * a * b2 * c4 - 2 * a2
							* c5 - 3 * a * b * c5 - 2 * b2 * c5 + c7);
		case 2720:
			return a2
					* (a - b)
					* (-a + b - c)
					* (-a + c)
					* (-a - b + c)
					* (-a3 + a2 * b + a * b2 - b3 - 2 * a * b * c + a * c2 + b
							* c2)
					* (-a3 + a * b2 + a2 * c - 2 * a * b * c + b2 * c + a * c2 - c3);
		case 2721:
			return a
					* (-a4 + 4 * a2 * b2 - b4 + a3 * c - 2 * a2 * b * c - 2 * a
							* b2 * c + b3 * c - a2 * c2 - b2 * c2 + a * c3 + b
							* c3)
					* (-a4 + a3 * b - a2 * b2 + a * b3 - 2 * a2 * b * c + b3
							* c + 4 * a2 * c2 - 2 * a * b * c2 - b2 * c2 + b
							* c3 - c4);
		case 2722:
			return a
					* (a - b)
					* (-a + c)
					* (a5 + a4 * b + a * b4 + b5 - a4 * c - a3 * b * c - a * b3
							* c - b4 * c + a * b * c3 - a * c4 - b * c4 + c5)
					* (a5 - a4 * b - a * b4 + b5 + a4 * c - a3 * b * c + a * b3
							* c - b4 * c - a * b * c3 + a * c4 - b * c4 + c5);
		case 2723:
			return (-a7 + a6 * b - a5 * b2 + a4 * b3 + a3 * b4 - a2 * b5 + a
					* b6 - b7 + a6 * c - 2 * a5 * b * c + a4 * b2 * c + a2 * b4
					* c - 2 * a * b5 * c + b6 * c + 2 * a5 * c2 - 2 * a3 * b2
					* c2 - 2 * a2 * b3 * c2 + 2 * b5 * c2 - 2 * a4 * c3 + 2
					* a3 * b * c3 + 2 * a2 * b2 * c3 + 2 * a * b3 * c3 - 2 * b4
					* c3 - a3 * c4 - a2 * b * c4 - a * b2 * c4 - b3 * c4 + a2
					* c5 + b2 * c5)
					* (-a7 + a6 * b + 2 * a5 * b2 - 2 * a4 * b3 - a3 * b4 + a2
							* b5 + a6 * c - 2 * a5 * b * c + 2 * a3 * b3 * c
							- a2 * b4 * c - a5 * c2 + a4 * b * c2 - 2 * a3 * b2
							* c2 + 2 * a2 * b3 * c2 - a * b4 * c2 + b5 * c2
							+ a4 * c3 - 2 * a2 * b2 * c3 + 2 * a * b3 * c3 - b4
							* c3 + a3 * c4 + a2 * b * c4 - 2 * b3 * c4 - a2
							* c5 - 2 * a * b * c5 + 2 * b2 * c5 + a * c6 + b
							* c6 - c7);
		case 2724:
			return (-a6 - a4 * b2 + 4 * a3 * b3 - a2 * b4 - b6 + 2 * a5 * c - 2
					* a3 * b2 * c - 2 * a2 * b3 * c + 2 * b5 * c + 2 * a2 * b2
					* c2 - 2 * a3 * c3 - 2 * b3 * c3 + a2 * c4 + b2 * c4)
					* (-a6 + 2 * a5 * b - 2 * a3 * b3 + a2 * b4 - a4 * c2 - 2
							* a3 * b * c2 + 2 * a2 * b2 * c2 + b4 * c2 + 4 * a3
							* c3 - 2 * a2 * b * c3 - 2 * b3 * c3 - a2 * c4 + 2
							* b * c5 - c6);
		case 2725:
			return a
					* (-a4 + a3 * b + a * b3 - b4 + a3 * c + b3 * c - a2 * c2
							- 2 * a * b * c2 - b2 * c2 + a * c3 + b * c3)
					* (-a4 + a3 * b - a2 * b2 + a * b3 + a3 * c - 2 * a * b2
							* c + b3 * c - b2 * c2 + a * c3 + b * c3 - c4);
		case 2726:
			return (-a4 + 2 * a3 * b + 2 * a * b3 - b4 - 2 * a2 * b * c - 2 * a
					* b2 * c + a2 * c2 + b2 * c2)
					* (-a4 + a2 * b2 + 2 * a3 * c - 2 * a2 * b * c - 2 * a * b
							* c2 + b2 * c2 + 2 * a * c3 - c4);
		case 2727:
			return a2
					* (a - b)
					* (-a + c)
					* (-a6 + a5 * b - a4 * b2 + 2 * a3 * b3 + a2 * b4 - 3 * a
							* b5 + b6 - a5 * c + a4 * b * c - 2 * a3 * b2 * c
							+ 2 * a2 * b3 * c + 3 * a * b4 * c - 3 * b5 * c
							+ a4 * c2 - 2 * a3 * b * c2 - 2 * a2 * b2 * c2 + 2
							* a * b3 * c2 + b4 * c2 + 2 * a3 * c3 - 2 * a2 * b
							* c3 - 2 * a * b2 * c3 + 2 * b3 * c3 + a2 * c4 + a
							* b * c4 - b2 * c4 - a * c5 + b * c5 - c6)
					* (-a6 - a5 * b + a4 * b2 + 2 * a3 * b3 + a2 * b4 - a * b5
							- b6 + a5 * c + a4 * b * c - 2 * a3 * b2 * c - 2
							* a2 * b3 * c + a * b4 * c + b5 * c - a4 * c2 - 2
							* a3 * b * c2 - 2 * a2 * b2 * c2 - 2 * a * b3 * c2
							- b4 * c2 + 2 * a3 * c3 + 2 * a2 * b * c3 + 2 * a
							* b2 * c3 + 2 * b3 * c3 + a2 * c4 + 3 * a * b * c4
							+ b2 * c4 - 3 * a * c5 - 3 * b * c5 + c6);
		case 2728:
			return a
					* (a - b)
					* (-a + c)
					* (a5 - a4 * b - a * b4 + b5 + a3 * b * c - 3 * a2 * b2 * c
							+ 3 * a * b3 * c - b4 * c - a3 * c2 + 4 * a2 * b
							* c2 - 3 * a * b2 * c2 - a2 * c3 + a * b * c3 - b
							* c4 + c5)
					* (a5 - a3 * b2 - a2 * b3 + b5 - a4 * c + a3 * b * c + 4
							* a2 * b2 * c + a * b3 * c - b4 * c - 3 * a2 * b
							* c2 - 3 * a * b2 * c2 + 3 * a * b * c3 - a * c4
							- b * c4 + c5);
		case 2729:
			return (-a5 + 2 * a3 * b2 + 2 * a2 * b3 - b5 + a4 * c - 4 * a2 * b2
					* c + b4 * c - a3 * c2 - b3 * c2 + a2 * c3 + b2 * c3)
					* (-a5 + a4 * b - a3 * b2 + a2 * b3 + 2 * a3 * c2 - 4 * a2
							* b * c2 + b3 * c2 + 2 * a2 * c3 - b2 * c3 + b * c4 - c5);
		case 2730:
			return a
					* (a - b)
					* (-a + c)
					* (a5 - a4 * b - a * b4 + b5 - 2 * a4 * c + a3 * b * c + 3
							* a2 * b2 * c - a * b3 * c - b4 * c + a3 * c2 - 4
							* a2 * b * c2 + 3 * a * b2 * c2 + a2 * c3 + a * b
							* c3 - 2 * a * c4 - b * c4 + c5)
					* (a5 - 2 * a4 * b + a3 * b2 + a2 * b3 - 2 * a * b4 + b5
							- a4 * c + a3 * b * c - 4 * a2 * b2 * c + a * b3
							* c - b4 * c + 3 * a2 * b * c2 + 3 * a * b2 * c2
							- a * b * c3 - a * c4 - b * c4 + c5);
		case 2731:
			return (a - b)
					* (-a + c)
					* (a5 - 3 * a4 * b - 3 * a * b4 + b5 + 4 * a3 * b * c - 2
							* a2 * b2 * c + 4 * a * b3 * c - 2 * a3 * c2 + 2
							* a2 * b * c2 + 2 * a * b2 * c2 - 2 * b3 * c2 - 4
							* a * b * c3 + a * c4 + b * c4)
					* (a5 - 2 * a3 * b2 + a * b4 - 3 * a4 * c + 4 * a3 * b * c
							+ 2 * a2 * b2 * c - 4 * a * b3 * c + b4 * c - 2
							* a2 * b * c2 + 2 * a * b2 * c2 + 4 * a * b * c3
							- 2 * b2 * c3 - 3 * a * c4 + c5);
		case 2732:
			return a2
					* (-a10 - a8 * b2 + a7 * b3 + 7 * a6 * b4 - 3 * a5 * b5 - 5
							* a4 * b6 + 3 * a3 * b7 - 2 * a2 * b8 - a * b9 + 2
							* b10 + a9 * c - a8 * b * c + a7 * b2 * c - 2 * a6
							* b3 * c - 3 * a5 * b4 * c + 6 * a4 * b5 * c - a3
							* b6 * c - 2 * a2 * b7 * c + 2 * a * b8 * c - b9
							* c + 3 * a8 * c2 + a7 * b * c2 - 6 * a6 * b2 * c2
							+ 4 * a5 * b3 * c2 - 7 * a4 * b4 * c2 - 3 * a3 * b5
							* c2 + 12 * a2 * b6 * c2 - 2 * a * b7 * c2 - 2 * b8
							* c2 - 4 * a7 * c3 + 3 * a6 * b * c3 - a5 * b2 * c3
							- 3 * a4 * b3 * c3 + 6 * a3 * b4 * c3 - 3 * a2 * b5
							* c3 - a * b6 * c3 + 3 * b7 * c3 - 2 * a6 * c4 - 3
							* a5 * b * c4 + 14 * a4 * b2 * c4 - 3 * a3 * b3
							* c4 - 7 * a2 * b4 * c4 + 6 * a * b5 * c4 - 5 * b6
							* c4 + 6 * a5 * c5 - 3 * a4 * b * c5 - a3 * b2 * c5
							+ 4 * a2 * b3 * c5 - 3 * a * b4 * c5 - 3 * b5 * c5
							- 2 * a4 * c6 + 3 * a3 * b * c6 - 6 * a2 * b2 * c6
							- 2 * a * b3 * c6 + 7 * b4 * c6 - 4 * a3 * c7 + a2
							* b * c7 + a * b2 * c7 + b3 * c7 + 3 * a2 * c8 - a
							* b * c8 - b2 * c8 + a * c9 - c10)
					* (-a10 + a9 * b + 3 * a8 * b2 - 4 * a7 * b3 - 2 * a6 * b4
							+ 6 * a5 * b5 - 2 * a4 * b6 - 4 * a3 * b7 + 3 * a2
							* b8 + a * b9 - b10 - a8 * b * c + a7 * b2 * c + 3
							* a6 * b3 * c - 3 * a5 * b4 * c - 3 * a4 * b5 * c
							+ 3 * a3 * b6 * c + a2 * b7 * c - a * b8 * c - a8
							* c2 + a7 * b * c2 - 6 * a6 * b2 * c2 - a5 * b3
							* c2 + 14 * a4 * b4 * c2 - a3 * b5 * c2 - 6 * a2
							* b6 * c2 + a * b7 * c2 - b8 * c2 + a7 * c3 - 2
							* a6 * b * c3 + 4 * a5 * b2 * c3 - 3 * a4 * b3 * c3
							- 3 * a3 * b4 * c3 + 4 * a2 * b5 * c3 - 2 * a * b6
							* c3 + b7 * c3 + 7 * a6 * c4 - 3 * a5 * b * c4 - 7
							* a4 * b2 * c4 + 6 * a3 * b3 * c4 - 7 * a2 * b4
							* c4 - 3 * a * b5 * c4 + 7 * b6 * c4 - 3 * a5 * c5
							+ 6 * a4 * b * c5 - 3 * a3 * b2 * c5 - 3 * a2 * b3
							* c5 + 6 * a * b4 * c5 - 3 * b5 * c5 - 5 * a4 * c6
							- a3 * b * c6 + 12 * a2 * b2 * c6 - a * b3 * c6 - 5
							* b4 * c6 + 3 * a3 * c7 - 2 * a2 * b * c7 - 2 * a
							* b2 * c7 + 3 * b3 * c7 - 2 * a2 * c8 + 2 * a * b
							* c8 - 2 * b2 * c8 - a * c9 - b * c9 + 2 * c10);
		case 2733:
			return a
					* (-a9 + 2 * a8 * b + a7 * b2 - 5 * a6 * b3 + 3 * a5 * b4
							+ 3 * a4 * b5 - 5 * a3 * b6 + a2 * b7 + 2 * a * b8
							- b9 - 4 * a7 * b * c + 4 * a6 * b2 * c + 4 * a5
							* b3 * c - 8 * a4 * b4 * c + 4 * a3 * b5 * c + 4
							* a2 * b6 * c - 4 * a * b7 * c + 2 * a7 * c2 + a6
							* b * c2 - 11 * a5 * b2 * c2 + 8 * a4 * b3 * c2 + 8
							* a3 * b4 * c2 - 11 * a2 * b5 * c2 + a * b6 * c2
							+ 2 * b7 * c2 + 4 * a5 * b * c3 + 4 * a4 * b2 * c3
							- 16 * a3 * b3 * c3 + 4 * a2 * b4 * c3 + 4 * a * b5
							* c3 - 7 * a4 * b * c4 + 7 * a3 * b2 * c4 + 7 * a2
							* b3 * c4 - 7 * a * b4 * c4 + 4 * a3 * b * c5 - 8
							* a2 * b2 * c5 + 4 * a * b3 * c5 - 2 * a3 * c6 + 3
							* a2 * b * c6 + 3 * a * b2 * c6 - 2 * b3 * c6 - 4
							* a * b * c7 + a * c8 + b * c8)
					* (-a9 + 2 * a7 * b2 - 2 * a3 * b6 + a * b8 + 2 * a8 * c
							- 4 * a7 * b * c + a6 * b2 * c + 4 * a5 * b3 * c
							- 7 * a4 * b4 * c + 4 * a3 * b5 * c + 3 * a2 * b6
							* c - 4 * a * b7 * c + b8 * c + a7 * c2 + 4 * a6
							* b * c2 - 11 * a5 * b2 * c2 + 4 * a4 * b3 * c2 + 7
							* a3 * b4 * c2 - 8 * a2 * b5 * c2 + 3 * a * b6 * c2
							- 5 * a6 * c3 + 4 * a5 * b * c3 + 8 * a4 * b2 * c3
							- 16 * a3 * b3 * c3 + 7 * a2 * b4 * c3 + 4 * a * b5
							* c3 - 2 * b6 * c3 + 3 * a5 * c4 - 8 * a4 * b * c4
							+ 8 * a3 * b2 * c4 + 4 * a2 * b3 * c4 - 7 * a * b4
							* c4 + 3 * a4 * c5 + 4 * a3 * b * c5 - 11 * a2 * b2
							* c5 + 4 * a * b3 * c5 - 5 * a3 * c6 + 4 * a2 * b
							* c6 + a * b2 * c6 + a2 * c7 - 4 * a * b * c7 + 2
							* b2 * c7 + 2 * a * c8 - c9);
		case 2734:
			return (-a8 + 2 * a7 * b - 2 * a6 * b2 - 2 * a5 * b3 + 6 * a4 * b4
					- 2 * a3 * b5 - 2 * a2 * b6 + 2 * a * b7 - b8 - 2 * a6 * b
					* c + 6 * a5 * b2 * c - 4 * a4 * b3 * c - 4 * a3 * b4 * c
					+ 6 * a2 * b5 * c - 2 * a * b6 * c + 3 * a6 * c2 - 4 * a5
					* b * c2 - 3 * a4 * b2 * c2 + 8 * a3 * b3 * c2 - 3 * a2
					* b4 * c2 - 4 * a * b5 * c2 + 3 * b6 * c2 + 4 * a4 * b * c3
					- 4 * a3 * b2 * c3 - 4 * a2 * b3 * c3 + 4 * a * b4 * c3 - 3
					* a4 * c4 + 2 * a3 * b * c4 + 4 * a2 * b2 * c4 + 2 * a * b3
					* c4 - 3 * b4 * c4 - 2 * a2 * b * c5 - 2 * a * b2 * c5 + a2
					* c6 + b2 * c6)
					* (-a8 + 3 * a6 * b2 - 3 * a4 * b4 + a2 * b6 + 2 * a7 * c
							- 2 * a6 * b * c - 4 * a5 * b2 * c + 4 * a4 * b3
							* c + 2 * a3 * b4 * c - 2 * a2 * b5 * c - 2 * a6
							* c2 + 6 * a5 * b * c2 - 3 * a4 * b2 * c2 - 4 * a3
							* b3 * c2 + 4 * a2 * b4 * c2 - 2 * a * b5 * c2 + b6
							* c2 - 2 * a5 * c3 - 4 * a4 * b * c3 + 8 * a3 * b2
							* c3 - 4 * a2 * b3 * c3 + 2 * a * b4 * c3 + 6 * a4
							* c4 - 4 * a3 * b * c4 - 3 * a2 * b2 * c4 + 4 * a
							* b3 * c4 - 3 * b4 * c4 - 2 * a3 * c5 + 6 * a2 * b
							* c5 - 4 * a * b2 * c5 - 2 * a2 * c6 - 2 * a * b
							* c6 + 3 * b2 * c6 + 2 * a * c7 - c8);
		case 2735:
			return (a - b)
					* (-a + c)
					* (a7 - 5 * a5 * b2 - 5 * a2 * b5 + b7 + a5 * b * c + a4
							* b2 * c - 4 * a3 * b3 * c + a2 * b4 * c + a * b5
							* c - a5 * c2 - a4 * b * c2 + 9 * a3 * b2 * c2 + 9
							* a2 * b3 * c2 - a * b4 * c2 - b5 * c2 - 5 * a2
							* b2 * c3 - a3 * c4 - b3 * c4 - a * b * c5 + a * c6 + b
							* c6)
					* (a7 - a5 * b2 - a3 * b4 + a * b6 + a5 * b * c - a4 * b2
							* c - a * b5 * c + b6 * c - 5 * a5 * c2 + a4 * b
							* c2 + 9 * a3 * b2 * c2 - 5 * a2 * b3 * c2 - 4 * a3
							* b * c3 + 9 * a2 * b2 * c3 - b4 * c3 + a2 * b * c4
							- a * b2 * c4 - 5 * a2 * c5 + a * b * c5 - b2 * c5 + c7);
		case 2736:
			return a
					* (a - b)
					* (-a + c)
					* (a4 - a3 * b - a * b3 + b4 - 2 * a3 * c - 2 * b3 * c + 2
							* a2 * c2 + 3 * a * b * c2 + 2 * b2 * c2 - 2 * a
							* c3 - 2 * b * c3 + c4)
					* (a4 - 2 * a3 * b + 2 * a2 * b2 - 2 * a * b3 + b4 - a3 * c
							+ 3 * a * b2 * c - 2 * b3 * c + 2 * b2 * c2 - a
							* c3 - 2 * b * c3 + c4);
		case 2737:
			return (a - b)
					* (-a + c)
					* (a4 - 2 * a3 * b - 2 * a2 * b2 - 2 * a * b3 + b4 - a3 * c
							+ 5 * a2 * b * c + 5 * a * b2 * c - b3 * c - a2
							* c2 - 4 * a * b * c2 - b2 * c2 + a * c3 + b * c3)
					* (a4 - a3 * b - a2 * b2 + a * b3 - 2 * a3 * c + 5 * a2 * b
							* c - 4 * a * b2 * c + b3 * c - 2 * a2 * c2 + 5 * a
							* b * c2 - b2 * c2 - 2 * a * c3 - b * c3 + c4);
		case 2738:
			return a2
					* (-a9 + a8 * b - 2 * a7 * b2 + a6 * b3 + 6 * a5 * b4 - 3
							* a4 * b5 - 2 * a3 * b6 - a2 * b7 - a * b8 + 2 * b9
							+ a6 * b2 * c - 3 * a4 * b4 * c + 3 * a2 * b6 * c
							- b8 * c + 3 * a7 * c2 - 4 * a6 * b * c2 - 3 * a5
							* b2 * c2 - a4 * b3 * c2 - 3 * a3 * b4 * c2 + 6
							* a2 * b5 * c2 + 3 * a * b6 * c2 - b7 * c2 + a6
							* c3 + 4 * a4 * b2 * c3 - 3 * a2 * b4 * c3 - 2 * b6
							* c3 - 3 * a5 * c4 + 6 * a4 * b * c4 + 4 * a3 * b2
							* c4 - a2 * b3 * c4 - 3 * a * b4 * c4 - 3 * b5 * c4
							- 3 * a4 * c5 - 3 * a2 * b2 * c5 + 6 * b4 * c5 + a3
							* c6 - 4 * a2 * b * c6 + a * b2 * c6 + b3 * c6 + 3
							* a2 * c7 - 2 * b2 * c7 + b * c8 - c9)
					* (-a9 + 3 * a7 * b2 + a6 * b3 - 3 * a5 * b4 - 3 * a4 * b5
							+ a3 * b6 + 3 * a2 * b7 - b9 + a8 * c - 4 * a6 * b2
							* c + 6 * a4 * b4 * c - 4 * a2 * b6 * c + b8 * c
							- 2 * a7 * c2 + a6 * b * c2 - 3 * a5 * b2 * c2 + 4
							* a4 * b3 * c2 + 4 * a3 * b4 * c2 - 3 * a2 * b5
							* c2 + a * b6 * c2 - 2 * b7 * c2 + a6 * c3 - a4
							* b2 * c3 - a2 * b4 * c3 + b6 * c3 + 6 * a5 * c4
							- 3 * a4 * b * c4 - 3 * a3 * b2 * c4 - 3 * a2 * b3
							* c4 - 3 * a * b4 * c4 + 6 * b5 * c4 - 3 * a4 * c5
							+ 6 * a2 * b2 * c5 - 3 * b4 * c5 - 2 * a3 * c6 + 3
							* a2 * b * c6 + 3 * a * b2 * c6 - 2 * b3 * c6 - a2
							* c7 - b2 * c7 - a * c8 - b * c8 + 2 * c9);
		case 2739:
			return a
					* (-a8 + a7 * b + 2 * a6 * b2 - a5 * b3 - 2 * a4 * b4 - a3
							* b5 + 2 * a2 * b6 + a * b7 - b8 + a7 * c - 4 * a6
							* b * c - 2 * a5 * b2 * c + 5 * a4 * b3 * c + 5
							* a3 * b4 * c - 2 * a2 * b5 * c - 4 * a * b6 * c
							+ b7 * c + a6 * c2 + 4 * a5 * b * c2 - 5 * a4 * b2
							* c2 - 5 * a2 * b4 * c2 + 4 * a * b5 * c2 + b6 * c2
							- a5 * c3 + a4 * b * c3 + a * b4 * c3 - b5 * c3
							+ a4 * c4 - 3 * a3 * b * c4 + 4 * a2 * b2 * c4 - 3
							* a * b3 * c4 + b4 * c4 - a3 * c5 + 2 * a2 * b * c5
							+ 2 * a * b2 * c5 - b3 * c5 - a2 * c6 - 2 * a * b
							* c6 - b2 * c6 + a * c7 + b * c7)
					* (-a8 + a7 * b + a6 * b2 - a5 * b3 + a4 * b4 - a3 * b5
							- a2 * b6 + a * b7 + a7 * c - 4 * a6 * b * c + 4
							* a5 * b2 * c + a4 * b3 * c - 3 * a3 * b4 * c + 2
							* a2 * b5 * c - 2 * a * b6 * c + b7 * c + 2 * a6
							* c2 - 2 * a5 * b * c2 - 5 * a4 * b2 * c2 + 4 * a2
							* b4 * c2 + 2 * a * b5 * c2 - b6 * c2 - a5 * c3 + 5
							* a4 * b * c3 - 3 * a * b4 * c3 - b5 * c3 - 2 * a4
							* c4 + 5 * a3 * b * c4 - 5 * a2 * b2 * c4 + a * b3
							* c4 + b4 * c4 - a3 * c5 - 2 * a2 * b * c5 + 4 * a
							* b2 * c5 - b3 * c5 + 2 * a2 * c6 - 4 * a * b * c6
							+ b2 * c6 + a * c7 + b * c7 - c8);
		case 2740:
			return (a - b)
					* (-a + c)
					* (a6 + a5 * b - 4 * a4 * b2 - 4 * a3 * b3 - 4 * a2 * b4
							+ a * b5 + b6 - a5 * c - a4 * b * c + 4 * a3 * b2
							* c + 4 * a2 * b3 * c - a * b4 * c - b5 * c + 5
							* a2 * b2 * c2 - a2 * c4 - a * b * c4 - b2 * c4 + a
							* c5 + b * c5)
					* (a6 - a5 * b - a2 * b4 + a * b5 + a5 * c - a4 * b * c - a
							* b4 * c + b5 * c - 4 * a4 * c2 + 4 * a3 * b * c2
							+ 5 * a2 * b2 * c2 - b4 * c2 - 4 * a3 * c3 + 4 * a2
							* b * c3 - 4 * a2 * c4 - a * b * c4 + a * c5 - b
							* c5 + c6);
		case 2741:
			return (-a9 + 2 * a6 * b3 - a5 * b4 - a4 * b5 + 2 * a3 * b6 - b9
					+ a8 * c - 2 * a6 * b2 * c + 2 * a4 * b4 * c - 2 * a2 * b6
					* c + b8 * c + a7 * c2 - a4 * b3 * c2 - a3 * b4 * c2 + b7
					* c2 - a6 * c3 + a4 * b2 * c3 + a2 * b4 * c3 - b6 * c3 + a5
					* c4 + b5 * c4 - a4 * c5 - b4 * c5 - a3 * c6 - b3 * c6 + a2
					* c7 + b2 * c7)
					* (-a9 + a8 * b + a7 * b2 - a6 * b3 + a5 * b4 - a4 * b5
							- a3 * b6 + a2 * b7 - 2 * a6 * b * c2 + a4 * b3
							* c2 + b7 * c2 + 2 * a6 * c3 - a4 * b2 * c3 - b6
							* c3 - a5 * c4 + 2 * a4 * b * c4 - a3 * b2 * c4
							+ a2 * b3 * c4 - b5 * c4 - a4 * c5 + b4 * c5 + 2
							* a3 * c6 - 2 * a2 * b * c6 - b3 * c6 + b2 * c7 + b
							* c8 - c9);
		case 2742:
			return a2
					* (a - b)
					* (-a + c)
					* (a3 - a2 * b - a * b2 + b3 - 2 * a2 * c + 2 * a * b * c
							- 2 * b2 * c + a * c2 + b * c2)
					* (a3 - 2 * a2 * b + a * b2 - a2 * c + 2 * a * b * c + b2
							* c - a * c2 - 2 * b * c2 + c3);
		case 2743:
			return a
					* (a - b)
					* (-a + c)
					* (a3 - a2 * b - a * b2 + b3 - 2 * a2 * c + 5 * a * b * c
							- b2 * c - 2 * a * c2 - b * c2 + c3)
					* (a3 - 2 * a2 * b - 2 * a * b2 + b3 - a2 * c + 5 * a * b
							* c - b2 * c - a * c2 - b * c2 + c3);
		case 2744:
			return a2
					* (-(a7 * b3) + 3 * a5 * b5 - 3 * a3 * b7 + a * b9 - a9 * c
							+ a8 * b * c - a7 * b2 * c + 2 * a6 * b3 * c + 3
							* a5 * b4 * c - 6 * a4 * b5 * c + a3 * b6 * c + 2
							* a2 * b7 * c - 2 * a * b8 * c + b9 * c - a7 * b
							* c2 - 4 * a5 * b3 * c2 + 3 * a3 * b5 * c2 + 2 * a
							* b7 * c2 + 4 * a7 * c3 - 3 * a6 * b * c3 + a5 * b2
							* c3 + 3 * a4 * b3 * c3 - 6 * a3 * b4 * c3 + 3 * a2
							* b5 * c3 + a * b6 * c3 - 3 * b7 * c3 + 3 * a5 * b
							* c4 + 3 * a3 * b3 * c4 - 6 * a * b5 * c4 - 6 * a5
							* c5 + 3 * a4 * b * c5 + a3 * b2 * c5 - 4 * a2 * b3
							* c5 + 3 * a * b4 * c5 + 3 * b5 * c5 - 3 * a3 * b
							* c6 + 2 * a * b3 * c6 + 4 * a3 * c7 - a2 * b * c7
							- a * b2 * c7 - b3 * c7 + a * b * c8 - a * c9)
					* (-(a9 * b) + 4 * a7 * b3 - 6 * a5 * b5 + 4 * a3 * b7 - a
							* b9 + a8 * b * c - a7 * b2 * c - 3 * a6 * b3 * c
							+ 3 * a5 * b4 * c + 3 * a4 * b5 * c - 3 * a3 * b6
							* c - a2 * b7 * c + a * b8 * c - a7 * b * c2 + a5
							* b3 * c2 + a3 * b5 * c2 - a * b7 * c2 - a7 * c3
							+ 2 * a6 * b * c3 - 4 * a5 * b2 * c3 + 3 * a4 * b3
							* c3 + 3 * a3 * b4 * c3 - 4 * a2 * b5 * c3 + 2 * a
							* b6 * c3 - b7 * c3 + 3 * a5 * b * c4 - 6 * a3 * b3
							* c4 + 3 * a * b5 * c4 + 3 * a5 * c5 - 6 * a4 * b
							* c5 + 3 * a3 * b2 * c5 + 3 * a2 * b3 * c5 - 6 * a
							* b4 * c5 + 3 * b5 * c5 + a3 * b * c6 + a * b3 * c6
							- 3 * a3 * c7 + 2 * a2 * b * c7 + 2 * a * b2 * c7
							- 3 * b3 * c7 - 2 * a * b * c8 + a * c9 + b * c9);
		case 2745:
			return a2
					* (-a7 + a6 * b + 3 * a3 * b4 - 3 * a2 * b5 - 2 * a * b6
							+ 2 * b7 + a6 * c - 4 * a5 * b * c + 4 * a4 * b2
							* c - 4 * a3 * b3 * c - 3 * a2 * b4 * c + 8 * a
							* b5 * c - 2 * b6 * c + 3 * a5 * c2 - a4 * b * c2
							- 4 * a3 * b2 * c2 + 8 * a2 * b3 * c2 - 3 * a * b4
							* c2 - 3 * b5 * c2 - 3 * a4 * c3 + 8 * a3 * b * c3
							- 4 * a2 * b2 * c3 - 4 * a * b3 * c3 + 3 * b4 * c3
							- 3 * a3 * c4 - a2 * b * c4 + 4 * a * b2 * c4 + 3
							* a2 * c5 - 4 * a * b * c5 + a * c6 + b * c6 - c7)
					* (-a7 + a6 * b + 3 * a5 * b2 - 3 * a4 * b3 - 3 * a3 * b4
							+ 3 * a2 * b5 + a * b6 - b7 + a6 * c - 4 * a5 * b
							* c - a4 * b2 * c + 8 * a3 * b3 * c - a2 * b4 * c
							- 4 * a * b5 * c + b6 * c + 4 * a4 * b * c2 - 4
							* a3 * b2 * c2 - 4 * a2 * b3 * c2 + 4 * a * b4 * c2
							- 4 * a3 * b * c3 + 8 * a2 * b2 * c3 - 4 * a * b3
							* c3 + 3 * a3 * c4 - 3 * a2 * b * c4 - 3 * a * b2
							* c4 + 3 * b3 * c4 - 3 * a2 * c5 + 8 * a * b * c5
							- 3 * b2 * c5 - 2 * a * c6 - 2 * b * c6 + 2 * c7);
		case 2746:
			return a
					* (a - b)
					* (-a + c)
					* (a5 + a4 * b - 4 * a3 * b2 - 4 * a2 * b3 + a * b4 + b5
							- a4 * c - a3 * b * c + 4 * a2 * b2 * c - a * b3
							* c - b4 * c + 5 * a * b * c3 - a * c4 - b * c4 + c5)
					* (a5 - a4 * b - a * b4 + b5 + a4 * c - a3 * b * c + 5 * a
							* b3 * c - b4 * c - 4 * a3 * c2 + 4 * a2 * b * c2
							- 4 * a2 * c3 - a * b * c3 + a * c4 - b * c4 + c5);
		case 2747:
			return a
					* (-a8 + 2 * a6 * b2 - 2 * a4 * b4 + 2 * a2 * b6 - b8 + a7
							* c - 2 * a6 * b * c + a4 * b3 * c + a3 * b4 * c
							- 2 * a * b6 * c + b7 * c + a6 * c2 - a4 * b2 * c2
							- a2 * b4 * c2 + b6 * c2 - a5 * c3 + a4 * b * c3
							+ a * b4 * c3 - b5 * c3 + a4 * c4 + b4 * c4 - a3
							* c5 - b3 * c5 - a2 * c6 - b2 * c6 + a * c7 + b
							* c7)
					* (-a8 + a7 * b + a6 * b2 - a5 * b3 + a4 * b4 - a3 * b5
							- a2 * b6 + a * b7 - 2 * a6 * b * c + a4 * b3 * c
							+ b7 * c + 2 * a6 * c2 - a4 * b2 * c2 - b6 * c2
							+ a4 * b * c3 - b5 * c3 - 2 * a4 * c4 + a3 * b * c4
							- a2 * b2 * c4 + a * b3 * c4 + b4 * c4 - b3 * c5
							+ 2 * a2 * c6 - 2 * a * b * c6 + b2 * c6 + b * c7 - c8);
		case 2748:
			return a * (a - b) * (-a + c) * (a2 - 3 * a * b + b2 + c2)
					* (a2 + b2 - 3 * a * c + c2);
		case 2749:
			return a2
					* (-(a6 * b3) + a5 * b4 + 2 * a4 * b5 - 2 * a3 * b6 - a2
							* b7 + a * b8 - a8 * c - 3 * a6 * b2 * c + 2 * a4
							* b4 * c + a2 * b6 * c + b8 * c + a7 * c2 + 2 * a6
							* b * c2 + a5 * b2 * c2 + a4 * b3 * c2 - 3 * a3
							* b4 * c2 - 2 * a2 * b5 * c2 + a * b6 * c2 - b7
							* c2 + 3 * a6 * c3 + 2 * a4 * b2 * c3 - 3 * a2 * b4
							* c3 - 2 * b6 * c3 - 3 * a5 * c4 - 4 * a4 * b * c4
							+ 2 * a3 * b2 * c4 + a2 * b3 * c4 + 2 * a * b4 * c4
							+ 2 * b5 * c4 - 3 * a4 * c5 + a2 * b2 * c5 + b4
							* c5 + 3 * a3 * c6 + 2 * a2 * b * c6 - 3 * a * b2
							* c6 - b3 * c6 + a2 * c7 - a * c8)
					* (-(a8 * b) + a7 * b2 + 3 * a6 * b3 - 3 * a5 * b4 - 3 * a4
							* b5 + 3 * a3 * b6 + a2 * b7 - a * b8 + 2 * a6 * b2
							* c - 4 * a4 * b4 * c + 2 * a2 * b6 * c - 3 * a6
							* b * c2 + a5 * b2 * c2 + 2 * a4 * b3 * c2 + 2 * a3
							* b4 * c2 + a2 * b5 * c2 - 3 * a * b6 * c2 - a6
							* c3 + a4 * b2 * c3 + a2 * b4 * c3 - b6 * c3 + a5
							* c4 + 2 * a4 * b * c4 - 3 * a3 * b2 * c4 - 3 * a2
							* b3 * c4 + 2 * a * b4 * c4 + b5 * c4 + 2 * a4 * c5
							- 2 * a2 * b2 * c5 + 2 * b4 * c5 - 2 * a3 * c6 + a2
							* b * c6 + a * b2 * c6 - 2 * b3 * c6 - a2 * c7 - b2
							* c7 + a * c8 + b * c8);
		default:
			return Double.NaN;
		}
	}

	private double weight2750to2799(int k, double a, double b, double c) {
		switch (k) {
		case 2750:
			return a2
					* (-a6 - 2 * a4 * b2 + 2 * a3 * b3 + a2 * b4 - 2 * a * b5
							+ 2 * b6 + 2 * a5 * c + 2 * a3 * b2 * c - 2 * a2
							* b3 * c - 2 * b5 * c + a4 * c2 - 2 * a * b3 * c2
							+ b4 * c2 - 4 * a3 * c3 + 2 * a * b2 * c3 + 2 * b3
							* c3 + a2 * c4 - 2 * b2 * c4 + 2 * a * c5 - c6)
					* (-a6 + 2 * a5 * b + a4 * b2 - 4 * a3 * b3 + a2 * b4 + 2
							* a * b5 - b6 - 2 * a4 * c2 + 2 * a3 * b * c2 + 2
							* a * b3 * c2 - 2 * b4 * c2 + 2 * a3 * c3 - 2 * a2
							* b * c3 - 2 * a * b2 * c3 + 2 * b3 * c3 + a2 * c4
							+ b2 * c4 - 2 * a * c5 - 2 * b * c5 + 2 * c6);
		case 2751:
			return a
					* (-a5 + 2 * a4 * b - a3 * b2 - a2 * b3 + 2 * a * b4 - b5
							+ a2 * b * c2 + a * b2 * c2 - 4 * a * b * c3 + a
							* c4 + b * c4)
					* (-a5 + a * b4 + 2 * a4 * c + a2 * b2 * c - 4 * a * b3 * c
							+ b4 * c - a3 * c2 + a * b2 * c2 - a2 * c3 + 2 * a
							* c4 - c5);
		case 2752:
			return a
					* (-a5 + a4 * b + a * b4 - b5 + a3 * b * c + a * b3 * c
							- a2 * b * c2 - a * b2 * c2 - 2 * a * b * c3 + a
							* c4 + b * c4)
					* (-a5 + a * b4 + a4 * c + a3 * b * c - a2 * b2 * c - 2 * a
							* b3 * c + b4 * c - a * b2 * c2 + a * b * c3 + a
							* c4 - c5);
		case 2753:
			return a
					* (a - b)
					* (-a + c)
					* (a4 + 2 * a2 * b2 + b4 - 3 * a2 * b * c + 3 * a * b2 * c
							- 4 * a2 * c2 - 3 * a * b * c2 + 2 * b2 * c2 + c4)
					* (a4 - 4 * a2 * b2 + b4 - 3 * a2 * b * c - 3 * a * b2 * c
							+ 2 * a2 * c2 + 3 * a * b * c2 + 2 * b2 * c2 + c4);
		case 2754:
			return a
					* (-a7 + a6 * b + a5 * b2 - a4 * b3 - a3 * b4 + a2 * b5 + a
							* b6 - b7 + a5 * b * c - 2 * a3 * b3 * c + a * b5
							* c - a5 * c2 + a3 * b2 * c2 + a2 * b3 * c2 - b5
							* c2 + a3 * b * c3 + a * b3 * c3 + a3 * c4 - 2 * a2
							* b * c4 - 2 * a * b2 * c4 + b3 * c4 - 2 * a * b
							* c5 + a * c6 + b * c6)
					* (-a7 - a5 * b2 + a3 * b4 + a * b6 + a6 * c + a5 * b * c
							+ a3 * b3 * c - 2 * a2 * b4 * c - 2 * a * b5 * c
							+ b6 * c + a5 * c2 + a3 * b2 * c2 - 2 * a * b4 * c2
							- a4 * c3 - 2 * a3 * b * c3 + a2 * b2 * c3 + a * b3
							* c3 + b4 * c3 - a3 * c4 + a2 * c5 + a * b * c5
							- b2 * c5 + a * c6 - c7);
		case 2755:
			return a2
					* (-a7 - a6 * b - 3 * a5 * b2 + 5 * a3 * b4 - a2 * b5 - a
							* b6 + 2 * b7 + 2 * a6 * c + 3 * a4 * b2 * c - 4
							* a2 * b4 * c - b6 * c + 4 * a5 * c2 + a4 * b * c2
							- 4 * a * b4 * c2 - b5 * c2 - 5 * a4 * c3 + 5 * b4
							* c3 - 5 * a3 * c4 + a2 * b * c4 + 3 * a * b2 * c4
							+ 4 * a2 * c5 - 3 * b2 * c5 + 2 * a * c6 - b * c6 - c7)
					* (-a7 + 2 * a6 * b + 4 * a5 * b2 - 5 * a4 * b3 - 5 * a3
							* b4 + 4 * a2 * b5 + 2 * a * b6 - b7 - a6 * c + a4
							* b2 * c + a2 * b4 * c - b6 * c - 3 * a5 * c2 + 3
							* a4 * b * c2 + 3 * a * b4 * c2 - 3 * b5 * c2 + 5
							* a3 * c4 - 4 * a2 * b * c4 - 4 * a * b2 * c4 + 5
							* b3 * c4 - a2 * c5 - b2 * c5 - a * c6 - b * c6 + 2 * c7);
		case 2756:
			return a
					* (-a6 + 3 * a5 * b + a4 * b2 - 6 * a3 * b3 + a2 * b4 + 3
							* a * b5 - b6 - a5 * c - 2 * a4 * b * c + 3 * a3
							* b2 * c + 3 * a2 * b3 * c - 2 * a * b4 * c - b5
							* c + 3 * a3 * b * c2 - 6 * a2 * b2 * c2 + 3 * a
							* b3 * c2 + a2 * b * c3 + a * b2 * c3 + a2 * c4 - 6
							* a * b * c4 + b2 * c4 + a * c5 + b * c5)
					* (-a6 - a5 * b + a2 * b4 + a * b5 + 3 * a5 * c - 2 * a4
							* b * c + 3 * a3 * b2 * c + a2 * b3 * c - 6 * a
							* b4 * c + b5 * c + a4 * c2 + 3 * a3 * b * c2 - 6
							* a2 * b2 * c2 + a * b3 * c2 + b4 * c2 - 6 * a3
							* c3 + 3 * a2 * b * c3 + 3 * a * b2 * c3 + a2 * c4
							- 2 * a * b * c4 + 3 * a * c5 - b * c5 - c6);
		case 2757:
			return (-a5 + 3 * a4 * b - 2 * a3 * b2 - 2 * a2 * b3 + 3 * a * b4
					- b5 - a4 * c + 4 * a2 * b2 * c - b4 * c + a3 * c2 - 3 * a2
					* b * c2 - 3 * a * b2 * c2 + b3 * c2 + a2 * c3 + b2 * c3)
					* (-a5 - a4 * b + a3 * b2 + a2 * b3 + 3 * a4 * c - 3 * a2
							* b2 * c - 2 * a3 * c2 + 4 * a2 * b * c2 - 3 * a
							* b2 * c2 + b3 * c2 - 2 * a2 * c3 + b2 * c3 + 3 * a
							* c4 - b * c4 - c5);
		case 2758:
			return (-a5 + 2 * a4 * b + 2 * a * b4 - b5 - a4 * c - b4 * c + a3
					* c2 - 2 * a2 * b * c2 - 2 * a * b2 * c2 + b3 * c2 + a2
					* c3 + b2 * c3)
					* (-a5 - a4 * b + a3 * b2 + a2 * b3 + 2 * a4 * c - 2 * a2
							* b2 * c - 2 * a * b2 * c2 + b3 * c2 + b2 * c3 + 2
							* a * c4 - b * c4 - c5);
		case 2759:
			return (a - b)
					* (-a + c)
					* (a4 - a3 * b - 7 * a2 * b2 - a * b3 + b4 + a3 * c + a2
							* b * c + a * b2 * c + b3 * c + a2 * c2 - a * b
							* c2 + b2 * c2 + a * c3 + b * c3)
					* (a4 + a3 * b + a2 * b2 + a * b3 - a3 * c + a2 * b * c - a
							* b2 * c + b3 * c - 7 * a2 * c2 + a * b * c2 + b2
							* c2 - a * c3 + b * c3 + c4);
		case 2760:
			return (-a7 + 2 * a6 * b + a5 * b2 - 2 * a4 * b3 - 2 * a3 * b4 + a2
					* b5 + 2 * a * b6 - b7 - a6 * c + a4 * b2 * c + a2 * b4 * c
					- b6 * c + a3 * b2 * c2 + a2 * b3 * c2 - 2 * a2 * b2 * c3
					+ a3 * c4 - 2 * a2 * b * c4 - 2 * a * b2 * c4 + b3 * c4
					+ a2 * c5 + b2 * c5)
					* (-a7 - a6 * b + a3 * b4 + a2 * b5 + 2 * a6 * c - 2 * a2
							* b4 * c + a5 * c2 + a4 * b * c2 + a3 * b2 * c2 - 2
							* a2 * b3 * c2 - 2 * a * b4 * c2 + b5 * c2 - 2 * a4
							* c3 + a2 * b2 * c3 + b4 * c3 - 2 * a3 * c4 + a2
							* b * c4 + a2 * c5 + 2 * a * c6 - b * c6 - c7);
		case 2761:
			return a2
					* (a - b)
					* (-a + c)
					* (a7 * b3 - a6 * b4 - 3 * a5 * b5 + 3 * a4 * b6 + 3 * a3
							* b7 - 3 * a2 * b8 - a * b9 + b10 - a9 * c - 2 * a7
							* b2 * c + a6 * b3 * c + 4 * a5 * b4 * c - 3 * a4
							* b5 * c + 2 * a3 * b6 * c + 3 * a2 * b7 * c - 3
							* a * b8 * c - b9 * c - 2 * a7 * b * c2 + 2 * a6
							* b2 * c2 - a5 * b3 * c2 + a4 * b4 * c2 + 3 * a
							* b7 * c2 - 3 * b8 * c2 + 4 * a7 * c3 - 2 * a6 * b
							* c3 + 2 * a5 * b2 * c3 - a4 * b3 * c3 - 8 * a3
							* b4 * c3 + 2 * a * b6 * c3 + 3 * b7 * c3 + 4 * a5
							* b * c4 - 4 * a4 * b2 * c4 - a3 * b3 * c4 + a2
							* b4 * c4 - 3 * a * b5 * c4 + 3 * b6 * c4 - 6 * a5
							* c5 + 4 * a4 * b * c5 + 2 * a3 * b2 * c5 - a2 * b3
							* c5 + 4 * a * b4 * c5 - 3 * b5 * c5 - 2 * a3 * b
							* c6 + 2 * a2 * b2 * c6 + a * b3 * c6 - b4 * c6 + 4
							* a3 * c7 - 2 * a2 * b * c7 - 2 * a * b2 * c7 + b3
							* c7 - a * c9)
					* (-(a9 * b) + 4 * a7 * b3 - 6 * a5 * b5 + 4 * a3 * b7 - a
							* b9 - 2 * a7 * b2 * c - 2 * a6 * b3 * c + 4 * a5
							* b4 * c + 4 * a4 * b5 * c - 2 * a3 * b6 * c - 2
							* a2 * b7 * c - 2 * a7 * b * c2 + 2 * a6 * b2 * c2
							+ 2 * a5 * b3 * c2 - 4 * a4 * b4 * c2 + 2 * a3 * b5
							* c2 + 2 * a2 * b6 * c2 - 2 * a * b7 * c2 + a7 * c3
							+ a6 * b * c3 - a5 * b2 * c3 - a4 * b3 * c3 - a3
							* b4 * c3 - a2 * b5 * c3 + a * b6 * c3 + b7 * c3
							- a6 * c4 + 4 * a5 * b * c4 + a4 * b2 * c4 - 8 * a3
							* b3 * c4 + a2 * b4 * c4 + 4 * a * b5 * c4 - b6
							* c4 - 3 * a5 * c5 - 3 * a4 * b * c5 - 3 * a * b4
							* c5 - 3 * b5 * c5 + 3 * a4 * c6 + 2 * a3 * b * c6
							+ 2 * a * b3 * c6 + 3 * b4 * c6 + 3 * a3 * c7 + 3
							* a2 * b * c7 + 3 * a * b2 * c7 + 3 * b3 * c7 - 3
							* a2 * c8 - 3 * a * b * c8 - 3 * b2 * c8 - a * c9
							- b * c9 + c10);
		case 2762:
			return a2
					* (a - b)
					* (-a + c)
					* (a7 + a4 * b3 - 3 * a3 * b4 - 2 * a2 * b5 + 2 * a * b6
							+ b7 + a5 * b * c - 2 * a4 * b2 * c + 2 * a3 * b3
							* c - 3 * a * b5 * c + 2 * b6 * c - 2 * a5 * c2 + 2
							* a3 * b2 * c2 + 2 * a2 * b3 * c2 - 2 * b5 * c2
							+ a4 * c3 - 2 * a3 * b * c3 + 2 * a2 * b2 * c3 + 2
							* a * b3 * c3 - 3 * b4 * c3 + a3 * c4 - 2 * a * b2
							* c4 + b3 * c4 - 2 * a2 * c5 + a * b * c5 + c7)
					* (a7 - 2 * a5 * b2 + a4 * b3 + a3 * b4 - 2 * a2 * b5 + b7
							+ a5 * b * c - 2 * a3 * b3 * c + a * b5 * c - 2
							* a4 * b * c2 + 2 * a3 * b2 * c2 + 2 * a2 * b3 * c2
							- 2 * a * b4 * c2 + a4 * c3 + 2 * a3 * b * c3 + 2
							* a2 * b2 * c3 + 2 * a * b3 * c3 + b4 * c3 - 3 * a3
							* c4 - 3 * b3 * c4 - 2 * a2 * c5 - 3 * a * b * c5
							- 2 * b2 * c5 + 2 * a * c6 + 2 * b * c6 + c7);
		case 2763:
			return a2
					* (-a8 - 4 * a6 * b2 + 5 * a4 * b4 - 2 * a2 * b6 + 2 * b8
							+ 6 * a6 * c2 + 4 * a4 * b2 * c2 - 8 * a2 * b4 * c2
							- 2 * b6 * c2 - 10 * a4 * c4 + 4 * a2 * b2 * c4 + 5
							* b4 * c4 + 6 * a2 * c6 - 4 * b2 * c6 - c8)
					* (-a8 + 6 * a6 * b2 - 10 * a4 * b4 + 6 * a2 * b6 - b8 - 4
							* a6 * c2 + 4 * a4 * b2 * c2 + 4 * a2 * b4 * c2 - 4
							* b6 * c2 + 5 * a4 * c4 - 8 * a2 * b2 * c4 + 5 * b4
							* c4 - 2 * a2 * c6 - 2 * b2 * c6 + 2 * c8);
		case 2764:
			return a2
					* (a - b)
					* (a + b)
					* (-a + c)
					* (a + c)
					* (a8 + a6 * b2 - a4 * b4 - 5 * a2 * b6 + 4 * b8 - 2 * a6
							* c2 - a4 * b2 * c2 + 8 * a2 * b4 * c2 - 5 * b6
							* c2 + 2 * a4 * c4 - a2 * b2 * c4 - b4 * c4 - 2
							* a2 * c6 + b2 * c6 + c8)
					* (a8 - 2 * a6 * b2 + 2 * a4 * b4 - 2 * a2 * b6 + b8 + a6
							* c2 - a4 * b2 * c2 - a2 * b4 * c2 + b6 * c2 - a4
							* c4 + 8 * a2 * b2 * c4 - b4 * c4 - 5 * a2 * c6 - 5
							* b2 * c6 + 4 * c8);
		case 2765:
			return a
					* (a - b)
					* (-a + c)
					* (a6 - a5 * b - a4 * b2 + 2 * a3 * b3 - a2 * b4 - a * b5
							+ b6 + 2 * a4 * b * c - 2 * a3 * b2 * c - 2 * a2
							* b3 * c + 2 * a * b4 * c - a4 * c2 + 6 * a2 * b2
							* c2 - b4 * c2 - 2 * a2 * b * c3 - 2 * a * b2 * c3
							- a2 * c4 + a * b * c4 - b2 * c4 + c6)
					* (a6 - a4 * b2 - a2 * b4 + b6 - a5 * c + 2 * a4 * b * c
							- 2 * a2 * b3 * c + a * b4 * c - a4 * c2 - 2 * a3
							* b * c2 + 6 * a2 * b2 * c2 - 2 * a * b3 * c2 - b4
							* c2 + 2 * a3 * c3 - 2 * a2 * b * c3 - a2 * c4 + 2
							* a * b * c4 - b2 * c4 - a * c5 + c6);
		case 2766:
			return a
					* (a - b)
					* (-a + c)
					* (-V)
					* (-U)
					* (-a4 + b4 - a2 * b * c + a * b2 * c + 2 * a2 * c2 - a * b
							* c2 - c4)
					* (-a4 + 2 * a2 * b2 - b4 - a2 * b * c - a * b2 * c + a * b
							* c2 + c4);
		case 2767:
			return a
					* (-a7 + a6 * b + 5 * a5 * b2 - 5 * a4 * b3 - 5 * a3 * b4
							+ 5 * a2 * b5 + a * b6 - b7 - 3 * a5 * b * c + 6
							* a3 * b3 * c - 3 * a * b5 * c - a5 * c2 + 4 * a4
							* b * c2 - 3 * a3 * b2 * c2 - 3 * a2 * b3 * c2 + 4
							* a * b4 * c2 - b5 * c2 + a3 * b * c3 + a * b3 * c3
							+ a3 * c4 - 2 * a2 * b * c4 - 2 * a * b2 * c4 + b3
							* c4 - 2 * a * b * c5 + a * c6 + b * c6)
					* (-a7 - a5 * b2 + a3 * b4 + a * b6 + a6 * c - 3 * a5 * b
							* c + 4 * a4 * b2 * c + a3 * b3 * c - 2 * a2 * b4
							* c - 2 * a * b5 * c + b6 * c + 5 * a5 * c2 - 3
							* a3 * b2 * c2 - 2 * a * b4 * c2 - 5 * a4 * c3 + 6
							* a3 * b * c3 - 3 * a2 * b2 * c3 + a * b3 * c3 + b4
							* c3 - 5 * a3 * c4 + 4 * a * b2 * c4 + 5 * a2 * c5
							- 3 * a * b * c5 - b2 * c5 + a * c6 - c7);
		case 2768:
			return (-a6 + a5 * b + 2 * a4 * b2 - 4 * a3 * b3 + 2 * a2 * b4 + a
					* b5 - b6 - a4 * b * c + 2 * a3 * b2 * c + 2 * a2 * b3 * c
					- a * b4 * c + a3 * b * c2 - 4 * a2 * b2 * c2 + a * b3 * c2
					- a2 * b * c3 - a * b2 * c3 + a2 * c4 + b2 * c4)
					* (-a6 + a2 * b4 + a5 * c - a4 * b * c + a3 * b2 * c - a2
							* b3 * c + 2 * a4 * c2 + 2 * a3 * b * c2 - 4 * a2
							* b2 * c2 - a * b3 * c2 + b4 * c2 - 4 * a3 * c3 + 2
							* a2 * b * c3 + a * b2 * c3 + 2 * a2 * c4 - a * b
							* c4 + a * c5 - c6);
		case 2769:
			return (a - b)
					* (-a + c)
					* (a7 - a5 * b2 - a2 * b5 + b7 + a5 * b * c + a4 * b2 * c
							+ a2 * b4 * c + a * b5 * c - a5 * c2 - a4 * b * c2
							+ a3 * b2 * c2 + a2 * b3 * c2 - a * b4 * c2 - b5
							* c2 - a2 * b2 * c3 - a3 * c4 - b3 * c4 - a * b
							* c5 + a * c6 + b * c6)
					* (a7 - a5 * b2 - a3 * b4 + a * b6 + a5 * b * c - a4 * b2
							* c - a * b5 * c + b6 * c - a5 * c2 + a4 * b * c2
							+ a3 * b2 * c2 - a2 * b3 * c2 + a2 * b2 * c3 - b4
							* c3 + a2 * b * c4 - a * b2 * c4 - a2 * c5 + a * b
							* c5 - b2 * c5 + c7);
		case 2770:
			return (-a6 + 2 * a4 * b2 + 2 * a2 * b4 - b6 - 4 * a2 * b2 * c2
					+ a2 * c4 + b2 * c4)
					* (-a6 + a2 * b4 + 2 * a4 * c2 - 4 * a2 * b2 * c2 + b4 * c2
							+ 2 * a2 * c4 - c6);
		case 2771:
			return a
					* (a5 * b - a4 * b2 - 2 * a3 * b3 + 2 * a2 * b4 + a * b5
							- b6 + a5 * c + a3 * b2 * c - 2 * a * b4 * c - a4
							* c2 + a3 * b * c2 - 2 * a2 * b2 * c2 + a * b3 * c2
							+ b4 * c2 - 2 * a3 * c3 + a * b2 * c3 + 2 * a2 * c4
							- 2 * a * b * c4 + b2 * c4 + a * c5 - c6);
		case 2772:
			return a2
					* (a5 * b2 - a4 * b3 - 2 * a3 * b4 + 2 * a2 * b5 + a * b6
							- b7 + a5 * c2 + 2 * a3 * b2 * c2 - a2 * b3 * c2
							- a * b4 * c2 - b5 * c2 - a4 * c3 - a2 * b2 * c3
							+ 2 * b4 * c3 - 2 * a3 * c4 - a * b2 * c4 + 2 * b3
							* c4 + 2 * a2 * c5 - b2 * c5 + a * c6 - c7);
		case 2773:
			return a2
					* (b - c)
					* (a4 * b - 2 * a2 * b3 + b5 + a4 * c - a3 * b * c - a2
							* b2 * c + a * b3 * c - a2 * b * c2 + a * b2 * c2
							- 2 * a2 * c3 + a * b * c3 + c5);
		case 2774:
			return a2
					* (b - c)
					* (a3 * b - a2 * b2 - a * b3 + b4 + a3 * c - a2 * b * c - a
							* b2 * c + b3 * c - a2 * c2 - a * b * c2 + b2 * c2
							- a * c3 + b * c3 + c4);
		case 2775:
			return a
					* (b - c)
					* (a5 - a4 * b - a * b4 + b5 - a4 * c - a3 * b * c + 4 * a2
							* b2 * c - a * b3 * c - b4 * c + 4 * a2 * b * c2
							- 2 * a * b2 * c2 - a * b * c3 - a * c4 - b * c4 + c5);
		case 2776:
			return a2
					* (b - c)
					* (a4 * b - 2 * a2 * b3 + b5 + a4 * c - 3 * a3 * b * c + a2
							* b2 * c + 3 * a * b3 * c - 2 * b4 * c + a2 * b
							* c2 + 3 * a * b2 * c2 - 2 * b3 * c2 - 2 * a2 * c3
							+ 3 * a * b * c3 - 2 * b2 * c3 - 2 * b * c4 + c5);
		case 2777:
			return 2 * a10 - 2 * a8 * b2 - 5 * a6 * b4 + 7 * a4 * b6 - a2 * b8
					- b10 - 2 * a8 * c2 + 12 * a6 * b2 * c2 - 7 * a4 * b4 * c2
					- 6 * a2 * b6 * c2 + 3 * b8 * c2 - 5 * a6 * c4 - 7 * a4
					* b2 * c4 + 14 * a2 * b4 * c4 - 2 * b6 * c4 + 7 * a4 * c6
					- 6 * a2 * b2 * c6 - 2 * b4 * c6 - a2 * c8 + 3 * b2 * c8
					- c10;
		case 2778:
			return a
					* (a8 * b - 2 * a6 * b3 + 2 * a2 * b7 - b9 + a8 * c - 2
							* a7 * b * c + a6 * b2 * c + a5 * b3 * c - 4 * a4
							* b4 * c + 4 * a3 * b5 * c + a2 * b6 * c - 3 * a
							* b7 * c + b8 * c + a6 * b * c2 + 4 * a4 * b3 * c2
							- 7 * a2 * b5 * c2 + 2 * b7 * c2 - 2 * a6 * c3 + a5
							* b * c3 + 4 * a4 * b2 * c3 - 8 * a3 * b3 * c3 + 4
							* a2 * b4 * c3 + 3 * a * b5 * c3 - 2 * b6 * c3 - 4
							* a4 * b * c4 + 4 * a2 * b3 * c4 + 4 * a3 * b * c5
							- 7 * a2 * b2 * c5 + 3 * a * b3 * c5 + a2 * b * c6
							- 2 * b3 * c6 + 2 * a2 * c7 - 3 * a * b * c7 + 2
							* b2 * c7 + b * c8 - c9);
		case 2779:
			return a2
					* (a6 * b2 - 3 * a4 * b4 + 3 * a2 * b6 - b8 - a5 * b2 * c
							+ a4 * b3 * c + 2 * a3 * b4 * c - 2 * a2 * b5 * c
							- a * b6 * c + b7 * c + a6 * c2 - a5 * b * c2 + 2
							* a4 * b2 * c2 - a3 * b3 * c2 - 2 * a2 * b4 * c2
							+ 2 * a * b5 * c2 - b6 * c2 + a4 * b * c3 - a3 * b2
							* c3 + 2 * a2 * b3 * c3 - a * b4 * c3 - b5 * c3 - 3
							* a4 * c4 + 2 * a3 * b * c4 - 2 * a2 * b2 * c4 - a
							* b3 * c4 + 4 * b4 * c4 - 2 * a2 * b * c5 + 2 * a
							* b2 * c5 - b3 * c5 + 3 * a2 * c6 - a * b * c6 - b2
							* c6 + b * c7 - c8);
		case 2780:
			return a2
					* (b2 - c2)
					* (a6 - a4 * b2 - a2 * b4 + b6 - a4 * c2 + 9 * a2 * b2 * c2
							- 4 * b4 * c2 - a2 * c4 - 4 * b2 * c4 + c6);
		case 2781:
			return a2
					* (a8 * b2 - 2 * a6 * b4 + 2 * a2 * b8 - b10 + a8 * c2 + a4
							* b4 * c2 - 2 * a2 * b6 * c2 - 2 * a6 * c4 + a4
							* b2 * c4 + b6 * c4 - 2 * a2 * b2 * c6 + b4 * c6
							+ 2 * a2 * c8 - c10);
		case 2782:
			return a6 * b2 - a4 * b4 + a6 * c2 - 2 * a4 * b2 * c2 + a2 * b4
					* c2 - b6 * c2 - a4 * c4 + a2 * b2 * c4 + 2 * b4 * c4 - b2
					* c6;
		case 2783:
			return a5 * b - a3 * b3 + a5 * c - 2 * a4 * b * c + a2 * b3 * c + a
					* b4 * c - b5 * c - a * b3 * c2 - a3 * c3 + a2 * b * c3 - a
					* b2 * c3 + 2 * b3 * c3 + a * b * c4 - b * c5;
		case 2784:
			return 2 * a5 - a4 * b - a3 * b2 + a * b4 - b5 - a4 * c + a2 * b2
					* c - a3 * c2 + a2 * b * c2 - 2 * a * b2 * c2 + b3 * c2
					+ b2 * c3 + a * c4 - c5;
		case 2785:
			return (b - c)
					* (a3 - 2 * a2 * b + b3 - 2 * a2 * c + a * b * c + c3);
		case 2786:
			return (b - c) * (a2 + a * b - b2 + a * c - b * c - c2);
		case 2787:
			return (b - c) * (a3 + a * b * c - b2 * c - b * c2);
		case 2788:
			return (b - c)
					* (a5 - 2 * a4 * b + a3 * b2 - 2 * a4 * c + a3 * b * c - a2
							* b2 * c + a * b3 * c - b4 * c + a3 * c2 - a2 * b
							* c2 + b3 * c2 + a * b * c3 + b2 * c3 - b * c4);
		case 2789:
			return (b - c)
					* (3 * a3 - 2 * a2 * b + b3 - 2 * a2 * c + 3 * a * b * c
							- 2 * b2 * c - 2 * b * c2 + c3);
		case 2790:
			return a10 * b2 - 3 * a8 * b4 + 3 * a6 * b6 - a4 * b8 + a10 * c2
					+ 2 * a8 * b2 * c2 - 2 * a6 * b4 * c2 + a4 * b6 * c2 - a2
					* b8 * c2 - b10 * c2 - 3 * a8 * c4 - 2 * a6 * b2 * c4 + a2
					* b6 * c4 + 4 * b8 * c4 + 3 * a6 * c6 + a4 * b2 * c6 + a2
					* b4 * c6 - 6 * b6 * c6 - a4 * c8 - a2 * b2 * c8 + 4 * b4
					* c8 - b2 * c10;
		case 2791:
			return a8 * b - a7 * b2 - 2 * a6 * b3 + 2 * a5 * b4 + a4 * b5 - a3
					* b6 + a8 * c + a6 * b2 * c - 2 * a4 * b4 * c + a2 * b6 * c
					- b8 * c - a7 * c2 + a6 * b * c2 - 2 * a5 * b2 * c2 + a4
					* b3 * c2 + a3 * b4 * c2 + a2 * b5 * c2 - 2 * a * b6 * c2
					+ b7 * c2 - 2 * a6 * c3 + a4 * b2 * c3 - 2 * a2 * b4 * c3
					+ 3 * b6 * c3 + 2 * a5 * c4 - 2 * a4 * b * c4 + a3 * b2
					* c4 - 2 * a2 * b3 * c4 + 4 * a * b4 * c4 - 3 * b5 * c4
					+ a4 * c5 + a2 * b2 * c5 - 3 * b4 * c5 - a3 * c6 + a2 * b
					* c6 - 2 * a * b2 * c6 + 3 * b3 * c6 + b2 * c7 - b * c8;
		case 2792:
			return 2 * a6 - a5 * b - 2 * a4 * b2 + a3 * b3 + a2 * b4 - b6 - a5
					* c + 2 * a4 * b * c - a2 * b3 * c - a * b4 * c + b5 * c
					- 2 * a4 * c2 + a * b3 * c2 + b4 * c2 + a3 * c3 - a2 * b
					* c3 + a * b2 * c3 - 2 * b3 * c3 + a2 * c4 - a * b * c4
					+ b2 * c4 + b * c5 - c6;
		case 2793:
			return (b2 - c2)
					* (4 * a4 - a2 * b2 + b4 - a2 * c2 - 4 * b2 * c2 + c4);
		case 2794:
			return 2 * a8 - 2 * a6 * b2 + a4 * b4 - b8 - 2 * a6 * c2 + 2 * b6
					* c2 + a4 * c4 - 2 * b4 * c4 + 2 * b2 * c6 - c8;
		case 2795:
			return a4 * b - a3 * b2 + a4 * c - a2 * b2 * c - b4 * c - a3 * c2
					- a2 * b * c2 + 2 * a * b2 * c2 + b3 * c2 + b2 * c3 - b
					* c4;
		case 2796:
			return 2 * a3 - a2 * b - a * b2 - b3 - a2 * c + 2 * b2 * c - a * c2
					+ 2 * b * c2 - c3;
		case 2797:
			return (b2 - c2)
					* (a4 - a2 * b2 + a2 * b * c - b3 * c - a2 * c2 + 2 * b2
							* c2 - b * c3)
					* (a4 - a2 * b2 - a2 * b * c + b3 * c - a2 * c2 + 2 * b2
							* c2 + b * c3);
		case 2798:
			return (b - c)
					* (a6 - a5 * b - a4 * b2 + a3 * b3 - a5 * c + a4 * b * c
							+ a3 * b2 * c - b5 * c - a4 * c2 + a3 * b * c2 + 2
							* a2 * b2 * c2 - 2 * a * b3 * c2 + a3 * c3 - 2 * a
							* b2 * c3 + 2 * b3 * c3 - b * c5);
		case 2799:
			return (b2 - c2) * (a2 * b2 - b4 + a2 * c2 - c4);
		default:
			return Double.NaN;
		}
	}

	private double weight2800to2849(int k, double a, double b, double c) {

		switch (k) {
		case 2800:
			return a
					* (a5 * b - a4 * b2 - 2 * a3 * b3 + 2 * a2 * b4 + a * b5
							- b6 + a5 * c - 2 * a4 * b * c + 3 * a3 * b2 * c
							+ a2 * b3 * c - 4 * a * b4 * c + b5 * c - a4 * c2
							+ 3 * a3 * b * c2 - 6 * a2 * b2 * c2 + 3 * a * b3
							* c2 + b4 * c2 - 2 * a3 * c3 + a2 * b * c3 + 3 * a
							* b2 * c3 - 2 * b3 * c3 + 2 * a2 * c4 - 4 * a * b
							* c4 + b2 * c4 + a * c5 + b * c5 - c6);
		case 2801:
			return a
					* (a4 * b - 2 * a3 * b2 + 2 * a * b4 - b5 + a4 * c + a2
							* b2 * c - 2 * a * b3 * c - 2 * a3 * c2 + a2 * b
							* c2 + b3 * c2 - 2 * a * b * c3 + b2 * c3 + 2 * a
							* c4 - c5);
		case 2802:
			return a
					* (a2 * b - b3 + a2 * c - 4 * a * b * c + 2 * b2 * c + 2
							* b * c2 - c3);
		case 2803:
			return (b - c)
					* (a7 - 2 * a5 * b2 + a3 * b4 - 3 * a5 * b * c + 3 * a4
							* b2 * c + 2 * a3 * b3 * c - 2 * a2 * b4 * c + a
							* b5 * c - b6 * c - 2 * a5 * c2 + 3 * a4 * b * c2
							+ 2 * a3 * b2 * c2 - 2 * a2 * b3 * c2 - b5 * c2 + 2
							* a3 * b * c3 - 2 * a2 * b2 * c3 - 2 * a * b3 * c3
							+ 2 * b4 * c3 + a3 * c4 - 2 * a2 * b * c4 + 2 * b3
							* c4 + a * b * c5 - b2 * c5 - b * c6);
		case 2804:
			return (a - b - c)
					* (b - c)
					* (a2 * b - b3 + a2 * c - 2 * a * b * c + b2 * c + b * c2 - c3);
		case 2805:
			return a
					* (a3 * b - a2 * b2 + a * b3 - b4 + a3 * c - 2 * a * b2 * c
							- a2 * c2 - 2 * a * b * c2 + 4 * b2 * c2 + a * c3 - c4);
		case 2806:
			return a
					* (b - c)
					* (a5 - a4 * b - a * b4 + b5 - a4 * c + a3 * b * c - a * b3
							* c + b4 * c - a * b * c3 - a * c4 + b * c4 + c5);
		case 2807:
			return a2
					* (a5 * b2 - a4 * b3 - 2 * a3 * b4 + 2 * a2 * b5 + a * b6
							- b7 - a4 * b2 * c + 2 * a3 * b3 * c - 2 * a * b5
							* c + b6 * c + a5 * c2 - a4 * b * c2 + 2 * a3 * b2
							* c2 - 2 * a2 * b3 * c2 + a * b4 * c2 - b5 * c2
							- a4 * c3 + 2 * a3 * b * c3 - 2 * a2 * b2 * c3 + b4
							* c3 - 2 * a3 * c4 + a * b2 * c4 + b3 * c4 + 2 * a2
							* c5 - 2 * a * b * c5 - b2 * c5 + a * c6 + b * c6 - c7);
		case 2808:
			return a2
					* (a4 * b2 - 2 * a3 * b3 + 2 * a * b5 - b6 + a4 * c2 + 2
							* a2 * b2 * c2 - 2 * a * b3 * c2 - b4 * c2 - 2 * a3
							* c3 - 2 * a * b2 * c3 + 4 * b3 * c3 - b2 * c4 + 2
							* a * c5 - c6);
		case 2809:
			return a
					* (a3 * b - a2 * b2 + a * b3 - b4 + a3 * c - 2 * a2 * b * c
							+ b3 * c - a2 * c2 + a * c3 + b * c3 - c4);
		case 2810:
			return a2
					* (a2 * b2 - b4 - 2 * a * b2 * c + 2 * b3 * c + a2 * c2 - 2
							* a * b * c2 + 2 * b * c3 - c4);
		case 2811:
			return (b - c)
					* (a6 - 3 * a5 * b + a4 * b2 + 2 * a3 * b3 - a2 * b4 + a
							* b5 - b6 - 3 * a5 * c + 3 * a4 * b * c + 2 * a3
							* b2 * c - 2 * a2 * b3 * c + a * b4 * c - b5 * c
							+ a4 * c2 + 2 * a3 * b * c2 - 2 * a2 * b2 * c2 - 2
							* a * b3 * c2 + b4 * c2 + 2 * a3 * c3 - 2 * a2 * b
							* c3 - 2 * a * b2 * c3 + 2 * b3 * c3 - a2 * c4 + a
							* b * c4 + b2 * c4 + a * c5 - b * c5 - c6);
		case 2812:
			return a
					* (b - c)
					* (a5 - a4 * b - a * b4 + b5 - a4 * c + 3 * a3 * b * c - 3
							* a2 * b2 * c + a * b3 * c - 3 * a2 * b * c2 + 4
							* a * b2 * c2 - b3 * c2 + a * b * c3 - b2 * c3 - a
							* c4 + c5);
		case 2813:
			return a2
					* (a3 * b2 - a2 * b3 + a * b4 - b5 + a3 * c2 - 4 * a * b2
							* c2 + 2 * b3 * c2 - a2 * c3 + 2 * b2 * c3 + a * c4 - c5);
		case 2814:
			return a
					* (b - c)
					* (a5 - a4 * b - a * b4 + b5 - a4 * c - a3 * b * c + 3 * a2
							* b2 * c + a * b3 * c - 2 * b4 * c + 3 * a2 * b
							* c2 - 4 * a * b2 * c2 + b3 * c2 + a * b * c3 + b2
							* c3 - a * c4 - 2 * b * c4 + c5);
		case 2815:
			return a2
					* (b - c)
					* (a4 * b - 2 * a2 * b3 + b5 + a4 * c - 4 * a3 * b * c + 2
							* a2 * b2 * c + 4 * a * b3 * c - 3 * b4 * c + 2
							* a2 * b * c2 - 2 * a * b2 * c2 - 2 * a2 * c3 + 4
							* a * b * c3 - 3 * b * c4 + c5);
		case 2816:
			return 2 * a10 - a9 * b - 2 * a8 * b2 + 3 * a7 * b3 - 5 * a6 * b4
					- 3 * a5 * b5 + 7 * a4 * b6 + a3 * b7 - a2 * b8 - b10 - a9
					* c + 2 * a8 * b * c - 2 * a7 * b2 * c - a6 * b3 * c + 6
					* a5 * b4 * c - 3 * a4 * b5 * c - 2 * a3 * b6 * c + a2 * b7
					* c - a * b8 * c + b9 * c - 2 * a8 * c2 - 2 * a7 * b * c2
					+ 12 * a6 * b2 * c2 - 3 * a5 * b3 * c2 - 7 * a4 * b4 * c2
					+ 4 * a3 * b5 * c2 - 6 * a2 * b6 * c2 + a * b7 * c2 + 3
					* b8 * c2 + 3 * a7 * c3 - a6 * b * c3 - 3 * a5 * b2 * c3
					+ 6 * a4 * b3 * c3 - 3 * a3 * b4 * c3 - a2 * b5 * c3 + 3
					* a * b6 * c3 - 4 * b7 * c3 - 5 * a6 * c4 + 6 * a5 * b * c4
					- 7 * a4 * b2 * c4 - 3 * a3 * b3 * c4 + 14 * a2 * b4 * c4
					- 3 * a * b5 * c4 - 2 * b6 * c4 - 3 * a5 * c5 - 3 * a4 * b
					* c5 + 4 * a3 * b2 * c5 - a2 * b3 * c5 - 3 * a * b4 * c5
					+ 6 * b5 * c5 + 7 * a4 * c6 - 2 * a3 * b * c6 - 6 * a2 * b2
					* c6 + 3 * a * b3 * c6 - 2 * b4 * c6 + a3 * c7 + a2 * b
					* c7 + a * b2 * c7 - 4 * b3 * c7 - a2 * c8 - a * b * c8 + 3
					* b2 * c8 + b * c9 - c10;
		case 2817:
			return a
					* (a8 * b - 2 * a6 * b3 + 2 * a2 * b7 - b9 + a8 * c - 4
							* a7 * b * c + 3 * a6 * b2 * c + 4 * a5 * b3 * c
							- 7 * a4 * b4 * c + 4 * a3 * b5 * c + a2 * b6 * c
							- 4 * a * b7 * c + 2 * b8 * c + 3 * a6 * b * c2 - 8
							* a5 * b2 * c2 + 7 * a4 * b3 * c2 + 4 * a3 * b4
							* c2 - 11 * a2 * b5 * c2 + 4 * a * b6 * c2 + b7
							* c2 - 2 * a6 * c3 + 4 * a5 * b * c3 + 7 * a4 * b2
							* c3 - 16 * a3 * b3 * c3 + 8 * a2 * b4 * c3 + 4 * a
							* b5 * c3 - 5 * b6 * c3 - 7 * a4 * b * c4 + 4 * a3
							* b2 * c4 + 8 * a2 * b3 * c4 - 8 * a * b4 * c4 + 3
							* b5 * c4 + 4 * a3 * b * c5 - 11 * a2 * b2 * c5 + 4
							* a * b3 * c5 + 3 * b4 * c5 + a2 * b * c6 + 4 * a
							* b2 * c6 - 5 * b3 * c6 + 2 * a2 * c7 - 4 * a * b
							* c7 + b2 * c7 + 2 * b * c8 - c9);
		case 2818:
			return a2
					* (a6 * b2 - 3 * a4 * b4 + 3 * a2 * b6 - b8 - 2 * a5 * b2
							* c + 2 * a4 * b3 * c + 4 * a3 * b4 * c - 4 * a2
							* b5 * c - 2 * a * b6 * c + 2 * b7 * c + a6 * c2
							- 2 * a5 * b * c2 + 4 * a4 * b2 * c2 - 4 * a3 * b3
							* c2 - 3 * a2 * b4 * c2 + 6 * a * b5 * c2 - 2 * b6
							* c2 + 2 * a4 * b * c3 - 4 * a3 * b2 * c3 + 8 * a2
							* b3 * c3 - 4 * a * b4 * c3 - 2 * b5 * c3 - 3 * a4
							* c4 + 4 * a3 * b * c4 - 3 * a2 * b2 * c4 - 4 * a
							* b3 * c4 + 6 * b4 * c4 - 4 * a2 * b * c5 + 6 * a
							* b2 * c5 - 2 * b3 * c5 + 3 * a2 * c6 - 2 * a * b
							* c6 - 2 * b2 * c6 + 2 * b * c7 - c8);
		case 2819:
			return a2
					* (b - c)
					* (a6 * b - a4 * b3 - a2 * b5 + b7 + a6 * c - a5 * b * c
							- a2 * b4 * c + a * b5 * c - 5 * a3 * b2 * c2 + 9
							* a2 * b3 * c2 + a * b4 * c2 - 5 * b5 * c2 - a4
							* c3 + 9 * a2 * b2 * c3 - 4 * a * b3 * c3 - a2 * b
							* c4 + a * b2 * c4 - a2 * c5 + a * b * c5 - 5 * b2
							* c5 + c7);
		case 2820:
			return a
					* (b - c)
					* (a4 - 2 * a3 * b + 2 * a2 * b2 - 2 * a * b3 + b4 - 2 * a3
							* c + 3 * a2 * b * c - b3 * c + 2 * a2 * c2 - 2 * a
							* c3 - b * c3 + c4);
		case 2821:
			return a2
					* (b - c)
					* (a3 * b - a2 * b2 - a * b3 + b4 + a3 * c - 4 * a2 * b * c
							+ 5 * a * b2 * c - 2 * b3 * c - a2 * c2 + 5 * a * b
							* c2 - 2 * b2 * c2 - a * c3 - 2 * b * c3 + c4);
		case 2822:
			return 2 * a9 - a8 * b - a7 * b2 - 2 * a6 * b3 - 3 * a5 * b4 + 6
					* a4 * b5 + a3 * b6 - 2 * a2 * b7 + a * b8 - b9 - a8 * c
					+ 3 * a6 * b2 * c - 3 * a4 * b4 * c + a2 * b6 * c - a7 * c2
					+ 3 * a6 * b * c2 + 6 * a5 * b2 * c2 - 3 * a4 * b3 * c2
					- a3 * b4 * c2 - 3 * a2 * b5 * c2 - 4 * a * b6 * c2 + 3
					* b7 * c2 - 2 * a6 * c3 - 3 * a4 * b2 * c3 + 4 * a2 * b4
					* c3 + b6 * c3 - 3 * a5 * c4 - 3 * a4 * b * c4 - a3 * b2
					* c4 + 4 * a2 * b3 * c4 + 6 * a * b4 * c4 - 3 * b5 * c4 + 6
					* a4 * c5 - 3 * a2 * b2 * c5 - 3 * b4 * c5 + a3 * c6 + a2
					* b * c6 - 4 * a * b2 * c6 + b3 * c6 - 2 * a2 * c7 + 3 * b2
					* c7 + a * c8 - c9;
		case 2823:
			return a
					* (a7 * b - a6 * b2 - a5 * b3 + a4 * b4 - a3 * b5 + a2 * b6
							+ a * b7 - b8 + a7 * c - 2 * a6 * b * c + 2 * a5
							* b2 * c - 3 * a4 * b3 * c + a3 * b4 * c + 4 * a2
							* b5 * c - 4 * a * b6 * c + b7 * c - a6 * c2 + 2
							* a5 * b * c2 + 4 * a4 * b2 * c2 - 5 * a2 * b4 * c2
							- 2 * a * b5 * c2 + 2 * b6 * c2 - a5 * c3 - 3 * a4
							* b * c3 + 5 * a * b4 * c3 - b5 * c3 + a4 * c4 + a3
							* b * c4 - 5 * a2 * b2 * c4 + 5 * a * b3 * c4 - 2
							* b4 * c4 - a3 * c5 + 4 * a2 * b * c5 - 2 * a * b2
							* c5 - b3 * c5 + a2 * c6 - 4 * a * b * c6 + 2 * b2
							* c6 + a * c7 + b * c7 - c8);
		case 2824:
			return a2
					* (b - c)
					* (a5 * b - a4 * b2 - a * b5 + b6 + a5 * c - a4 * b * c - a
							* b4 * c + b5 * c - a4 * c2 + 5 * a2 * b2 * c2 + 4
							* a * b3 * c2 - 4 * b4 * c2 + 4 * a * b2 * c3 - 4
							* b3 * c3 - a * b * c4 - 4 * b2 * c4 - a * c5 + b
							* c5 + c6);
		case 2825:
			return a2
					* (a7 * b2 - a6 * b3 - a5 * b4 + a4 * b5 - a3 * b6 + a2
							* b7 + a * b8 - b9 + a7 * c2 + a3 * b4 * c2 - 2 * a
							* b6 * c2 - a6 * c3 - a2 * b4 * c3 + 2 * b6 * c3
							- a5 * c4 + a3 * b2 * c4 - a2 * b3 * c4 + 2 * a
							* b4 * c4 - b5 * c4 + a4 * c5 - b4 * c5 - a3 * c6
							- 2 * a * b2 * c6 + 2 * b3 * c6 + a2 * c7 + a * c8 - c9);
		case 2826:
			return (b - c)
					* (a2 * b - 2 * a * b2 + b3 + a2 * c + 2 * a * b * c - b2
							* c - 2 * a * c2 - b * c2 + c3);
		case 2827:
			return a
					* (b - c)
					* (a3 - a2 * b - a * b2 + b3 - a2 * c + 5 * a * b * c - 2
							* b2 * c - a * c2 - 2 * b * c2 + c3);
		case 2828:
			return a9 * b - 3 * a7 * b3 + 3 * a5 * b5 - a3 * b7 + a9 * c - 2
					* a8 * b * c + 2 * a7 * b2 * c + a6 * b3 * c - 6 * a5 * b4
					* c + 3 * a4 * b5 * c + 2 * a3 * b6 * c - a2 * b7 * c + a
					* b8 * c - b9 * c + 2 * a7 * b * c2 + 3 * a5 * b3 * c2 - 4
					* a3 * b5 * c2 - a * b7 * c2 - 3 * a7 * c3 + a6 * b * c3
					+ 3 * a5 * b2 * c3 - 6 * a4 * b3 * c3 + 3 * a3 * b4 * c3
					+ a2 * b5 * c3 - 3 * a * b6 * c3 + 4 * b7 * c3 - 6 * a5 * b
					* c4 + 3 * a3 * b3 * c4 + 3 * a * b5 * c4 + 3 * a5 * c5 + 3
					* a4 * b * c5 - 4 * a3 * b2 * c5 + a2 * b3 * c5 + 3 * a
					* b4 * c5 - 6 * b5 * c5 + 2 * a3 * b * c6 - 3 * a * b3 * c6
					- a3 * c7 - a2 * b * c7 - a * b2 * c7 + 4 * b3 * c7 + a * b
					* c8 - b * c9;
		case 2829:
			return 2 * a7 - 2 * a6 * b - 3 * a5 * b2 + 3 * a4 * b3 + a * b6
					- b7 - 2 * a6 * c + 8 * a5 * b * c - 3 * a4 * b2 * c - 4
					* a3 * b3 * c + 4 * a2 * b4 * c - 4 * a * b5 * c + b6 * c
					- 3 * a5 * c2 - 3 * a4 * b * c2 + 8 * a3 * b2 * c2 - 4 * a2
					* b3 * c2 - a * b4 * c2 + 3 * b5 * c2 + 3 * a4 * c3 - 4
					* a3 * b * c3 - 4 * a2 * b2 * c3 + 8 * a * b3 * c3 - 3 * b4
					* c3 + 4 * a2 * b * c4 - a * b2 * c4 - 3 * b3 * c4 - 4 * a
					* b * c5 + 3 * b2 * c5 + a * c6 + b * c6 - c7;
		case 2830:
			return a
					* (b - c)
					* (a5 - a4 * b - a * b4 + b5 - a4 * c + 5 * a3 * b * c - a
							* b3 * c + b4 * c + 4 * a * b2 * c2 - 4 * b3 * c2
							- a * b * c3 - 4 * b2 * c3 - a * c4 + b * c4 + c5);
		case 2831:
			return a
					* (a7 * b - a6 * b2 - a5 * b3 + a4 * b4 - a3 * b5 + a2 * b6
							+ a * b7 - b8 + a7 * c + a3 * b4 * c - 2 * a * b6
							* c - a6 * c2 - a2 * b4 * c2 + 2 * b6 * c2 - a5
							* c3 + a * b4 * c3 + a4 * c4 + a3 * b * c4 - a2
							* b2 * c4 + a * b3 * c4 - 2 * b4 * c4 - a3 * c5
							+ a2 * c6 - 2 * a * b * c6 + 2 * b2 * c6 + a * c7 - c8);
		case 2832:
			return a * (b - c) * (a2 + b2 - 3 * b * c + c2);
		case 2833:
			return a8 * b - a7 * b2 - 2 * a6 * b3 + 2 * a5 * b4 + a4 * b5 - a3
					* b6 + a8 * c + a6 * b2 * c + 2 * a4 * b4 * c - 3 * a2 * b6
					* c - b8 * c - a7 * c2 + a6 * b * c2 - 2 * a5 * b2 * c2 - 3
					* a4 * b3 * c2 + a3 * b4 * c2 + a2 * b5 * c2 + 2 * a * b6
					* c2 + b7 * c2 - 2 * a6 * c3 - 3 * a4 * b2 * c3 + 2 * a2
					* b4 * c3 + 3 * b6 * c3 + 2 * a5 * c4 + 2 * a4 * b * c4
					+ a3 * b2 * c4 + 2 * a2 * b3 * c4 - 4 * a * b4 * c4 - 3
					* b5 * c4 + a4 * c5 + a2 * b2 * c5 - 3 * b4 * c5 - a3 * c6
					- 3 * a2 * b * c6 + 2 * a * b2 * c6 + 3 * b3 * c6 + b2 * c7
					- b * c8;
		case 2834:
			return 2 * a6 - 2 * a5 * b + a4 * b2 + 2 * a3 * b3 - 2 * a2 * b4
					- b6 - 2 * a5 * c - 2 * a3 * b2 * c + 2 * a2 * b3 * c + 2
					* b5 * c + a4 * c2 - 2 * a3 * b * c2 + b4 * c2 + 2 * a3
					* c3 + 2 * a2 * b * c3 - 4 * b3 * c3 - 2 * a2 * c4 + b2
					* c4 + 2 * b * c5 - c6;
		case 2835:
			return a
					* (a4 * b - b5 + a4 * c - 4 * a3 * b * c + a2 * b2 * c + 2
							* b4 * c + a2 * b * c2 - b3 * c2 - b2 * c3 + 2 * b
							* c4 - c5);
		case 2836:
			return a
					* (a4 * b - b5 + a4 * c - 2 * a3 * b * c - a2 * b2 * c + a
							* b3 * c + b4 * c - a2 * b * c2 + a * b * c3 + b
							* c4 - c5);
		case 2837:
			return a
					* (b - c)
					* (a4 + 2 * a2 * b2 + b4 + 3 * a2 * b * c - 3 * a * b2 * c
							+ 2 * a2 * c2 - 3 * a * b * c2 - 4 * b2 * c2 + c4);
		case 2838:
			return a
					* (a6 * b + a4 * b3 - a2 * b5 - b7 + a6 * c - 2 * a5 * b
							* c - 2 * a4 * b2 * c + a3 * b3 * c + a * b5 * c
							+ b6 * c - 2 * a4 * b * c2 + a2 * b3 * c2 + b5 * c2
							+ a4 * c3 + a3 * b * c3 + a2 * b2 * c3 - 2 * a * b3
							* c3 - b4 * c3 - b3 * c4 - a2 * c5 + a * b * c5
							+ b2 * c5 + b * c6 - c7);
		case 2839:
			return 2 * a7 - a6 * b - a5 * b2 + 5 * a4 * b3 - 3 * a2 * b5 - a
					* b6 - b7 - a6 * c - 4 * a4 * b2 * c + 3 * a2 * b4 * c + 2
					* b6 * c - a5 * c2 - 4 * a4 * b * c2 + a * b4 * c2 + 4 * b5
					* c2 + 5 * a4 * c3 - 5 * b4 * c3 + 3 * a2 * b * c4 + a * b2
					* c4 - 5 * b3 * c4 - 3 * a2 * c5 + 4 * b2 * c5 - a * c6 + 2
					* b * c6 - c7;
		case 2840:
			return a
					* (a5 * b + a4 * b2 - a * b5 - b6 + a5 * c - 6 * a4 * b * c
							+ a3 * b2 * c + 3 * a2 * b3 * c - 2 * a * b4 * c
							+ 3 * b5 * c + a4 * c2 + a3 * b * c2 - 6 * a2 * b2
							* c2 + 3 * a * b3 * c2 + b4 * c2 + 3 * a2 * b * c3
							+ 3 * a * b2 * c3 - 6 * b3 * c3 - 2 * a * b * c4
							+ b2 * c4 - a * c5 + 3 * b * c5 - c6);
		case 2841:
			return a2
					* (a3 * b2 + a2 * b3 - a * b4 - b5 - 3 * a2 * b2 * c + 3
							* b4 * c + a3 * c2 - 3 * a2 * b * c2 + 4 * a * b2
							* c2 - 2 * b3 * c2 + a2 * c3 - 2 * b2 * c3 - a * c4
							+ 3 * b * c4 - c5);
		case 2842:
			return a2
					* (a3 * b2 + a2 * b3 - a * b4 - b5 - 2 * a2 * b2 * c + 2
							* b4 * c + a3 * c2 - 2 * a2 * b * c2 + a2 * c3 - a
							* c4 + 2 * b * c4 - c5);
		case 2843:
			return a2
					* (b - c)
					* (a3 * b + a2 * b2 + a * b3 + b4 + a3 * c - a2 * b * c + a
							* b2 * c - b3 * c + a2 * c2 + a * b * c2 - 7 * b2
							* c2 + a * c3 - b * c3 + c4);
		case 2844:
			return a2
					* (a5 * b2 + a4 * b3 - a * b6 - b7 - 2 * a4 * b2 * c + 2
							* b6 * c + a5 * c2 - 2 * a4 * b * c2 - 2 * a3 * b2
							* c2 + a2 * b3 * c2 + a * b4 * c2 + b5 * c2 + a4
							* c3 + a2 * b2 * c3 - 2 * b4 * c3 + a * b2 * c4 - 2
							* b3 * c4 + b2 * c5 - a * c6 + 2 * b * c6 - c7);
		case 2845:
			return (b - c)
					* (a10 - a9 * b - 3 * a8 * b2 + 3 * a7 * b3 + 3 * a6 * b4
							- 3 * a5 * b5 - a4 * b6 + a3 * b7 - a9 * c - 3 * a8
							* b * c + 3 * a7 * b2 * c + 2 * a6 * b3 * c - 3
							* a5 * b4 * c + 4 * a4 * b5 * c + a3 * b6 * c - 2
							* a2 * b7 * c - b9 * c - 3 * a8 * c2 + 3 * a7 * b
							* c2 + a4 * b4 * c2 - a3 * b5 * c2 + 2 * a2 * b6
							* c2 - 2 * a * b7 * c2 + 3 * a7 * c3 + 2 * a6 * b
							* c3 - 8 * a4 * b3 * c3 - a3 * b4 * c3 + 2 * a2
							* b5 * c3 - 2 * a * b6 * c3 + 4 * b7 * c3 + 3 * a6
							* c4 - 3 * a5 * b * c4 + a4 * b2 * c4 - a3 * b3
							* c4 - 4 * a2 * b4 * c4 + 4 * a * b5 * c4 - 3 * a5
							* c5 + 4 * a4 * b * c5 - a3 * b2 * c5 + 2 * a2 * b3
							* c5 + 4 * a * b4 * c5 - 6 * b5 * c5 - a4 * c6 + a3
							* b * c6 + 2 * a2 * b2 * c6 - 2 * a * b3 * c6 + a3
							* c7 - 2 * a2 * b * c7 - 2 * a * b2 * c7 + 4 * b3
							* c7 - b * c9);
		case 2846:
			return (b - c)
					* (a7 + 2 * a6 * b - 2 * a5 * b2 - 3 * a4 * b3 + a3 * b4
							+ b7 + 2 * a6 * c - 3 * a5 * b * c + 2 * a3 * b3
							* c - 2 * a2 * b4 * c + a * b5 * c - 2 * a5 * c2
							+ 2 * a3 * b2 * c2 + 2 * a2 * b3 * c2 - 2 * b5 * c2
							- 3 * a4 * c3 + 2 * a3 * b * c3 + 2 * a2 * b2 * c3
							- 2 * a * b3 * c3 + b4 * c3 + a3 * c4 - 2 * a2 * b
							* c4 + b3 * c4 + a * b * c5 - 2 * b2 * c5 + c7);
		case 2847:
			return 2 * a8 - 2 * a6 * b2 + 5 * a4 * b4 - 4 * a2 * b6 - b8 - 2
					* a6 * c2 - 8 * a4 * b2 * c2 + 4 * a2 * b4 * c2 + 6 * b6
					* c2 + 5 * a4 * c4 + 4 * a2 * b2 * c4 - 10 * b4 * c4 - 4
					* a2 * c6 + 6 * b2 * c6 - c8;
		case 2848:
			return (b2 - c2)
					* (4 * a8 - 5 * a6 * b2 - a4 * b4 + a2 * b6 + b8 - 5 * a6
							* c2 + 8 * a4 * b2 * c2 - a2 * b4 * c2 - 2 * b6
							* c2 - a4 * c4 - a2 * b2 * c4 + 2 * b4 * c4 + a2
							* c6 - 2 * b2 * c6 + c8);
		case 2849:
			return a
					* (b - c)
					* (a6 - a4 * b2 - a2 * b4 + b6 + a4 * b * c - 2 * a3 * b2
							* c + 2 * a * b4 * c - b5 * c - a4 * c2 - 2 * a3
							* b * c2 + 6 * a2 * b2 * c2 - 2 * a * b3 * c2 - b4
							* c2 - 2 * a * b2 * c3 + 2 * b3 * c3 - a2 * c4 + 2
							* a * b * c4 - b2 * c4 - b * c5 + c6);
		default:
			return Double.NaN;
		}
	}

	private double weight2850to2899(int k, double a, double b, double c) {
		switch (k) {
		case 2850:
			return a
					* (b - c)
					* T
					* (a4 - b4 + a2 * b * c - a * b2 * c - a * b * c2 + 2 * b2
							* c2 - c4);
		case 2851:
			return a
					* (a6 * b + a4 * b3 - a2 * b5 - b7 + a6 * c - 2 * a5 * b
							* c - 2 * a4 * b2 * c + a3 * b3 * c + 4 * a2 * b4
							* c - 3 * a * b5 * c + b6 * c - 2 * a4 * b * c2 - 3
							* a2 * b3 * c2 + 5 * b5 * c2 + a4 * c3 + a3 * b
							* c3 - 3 * a2 * b2 * c3 + 6 * a * b3 * c3 - 5 * b4
							* c3 + 4 * a2 * b * c4 - 5 * b3 * c4 - a2 * c5 - 3
							* a * b * c5 + 5 * b2 * c5 + b * c6 - c7);
		case 2852:
			return a2
					* (a4 * b2 - b6 - a3 * b2 * c + a2 * b3 * c - a * b4 * c
							+ b5 * c + a4 * c2 - a3 * b * c2 - 4 * a2 * b2 * c2
							+ 2 * a * b3 * c2 + 2 * b4 * c2 + a2 * b * c3 + 2
							* a * b2 * c3 - 4 * b3 * c3 - a * b * c4 + 2 * b2
							* c4 + b * c5 - c6);
		case 2853:
			return a2
					* (b - c)
					* (a6 * b - a4 * b3 - a2 * b5 + b7 + a6 * c - a5 * b * c
							- a2 * b4 * c + a * b5 * c - a3 * b2 * c2 + a2 * b3
							* c2 + a * b4 * c2 - b5 * c2 - a4 * c3 + a2 * b2
							* c3 - a2 * b * c4 + a * b2 * c4 - a2 * c5 + a * b
							* c5 - b2 * c5 + c7);
		case 2854:
			return a2
					* (a4 * b2 - b6 + a4 * c2 - 4 * a2 * b2 * c2 + 2 * b4 * c2
							+ 2 * b2 * c4 - c6);
		case 2855:
			return (a - b)
					* (a + b)
					* (-a + c)
					* (a + c)
					* (a8 + a6 * b2 - 4 * a4 * b4 + a2 * b6 + b8 - 2 * a6 * c2
							- 2 * a4 * b2 * c2 - 2 * a2 * b4 * c2 - 2 * b6 * c2
							+ 2 * a4 * c4 + 7 * a2 * b2 * c4 + 2 * b4 * c4 - 2
							* a2 * c6 - 2 * b2 * c6 + c8)
					* (a8 - 2 * a6 * b2 + 2 * a4 * b4 - 2 * a2 * b6 + b8 + a6
							* c2 - 2 * a4 * b2 * c2 + 7 * a2 * b4 * c2 - 2 * b6
							* c2 - 4 * a4 * c4 - 2 * a2 * b2 * c4 + 2 * b4 * c4
							+ a2 * c6 - 2 * b2 * c6 + c8);
		case 2856:
			return a
					* (-a6 + a4 * b2 + a2 * b4 - b6 + a5 * c - a3 * b2 * c - a2
							* b3 * c + b5 * c + 2 * a2 * b2 * c2 - a2 * b * c3
							- a * b2 * c3 - a2 * c4 - b2 * c4 + a * c5 + b * c5)
					* (-a6 + a5 * b - a2 * b4 + a * b5 - a2 * b3 * c + b5 * c
							+ a4 * c2 - a3 * b * c2 + 2 * a2 * b2 * c2 - a * b3
							* c2 - b4 * c2 - a2 * b * c3 + a2 * c4 + b * c5 - c6);
		case 2857:
			return (-a8 + a6 * b2 + a2 * b6 - b8 + a6 * c2 + b6 * c2 - a4 * c4
					- 2 * a2 * b2 * c4 - b4 * c4 + a2 * c6 + b2 * c6)
					* (-a8 + a6 * b2 - a4 * b4 + a2 * b6 + a6 * c2 - 2 * a2
							* b4 * c2 + b6 * c2 - b4 * c4 + a2 * c6 + b2 * c6 - c8);
		case 2858:
			return (a - b) * (a + b) * (-a + c) * (a + c)
					* (a4 - 3 * a2 * b2 + b4 + c4)
					* (a4 + b4 - 3 * a2 * c2 + c4);
		case 2859:
			return -c
					* b
					* (b * c * Math.sin(2 * angleB) + b * a
							* Math.sin(2 * angleA) - b * a
							* Math.sin(2 * angleB) + c * a
							* Math.sin(2 * angleC) - c * a
							* Math.sin(2 * angleA) - c * b
							* Math.sin(2 * angleC))
					/ (b3 * a * Math.sin(2 * angleA) - b3 * a
							* Math.sin(2 * angleB) - b4 * Math.sin(2 * angleA)
							+ b4 * Math.sin(2 * angleB) - c4
							* Math.sin(2 * angleC) + c3 * a
							* Math.sin(2 * angleC) + c4 * Math.sin(2 * angleA) - c3
							* a * Math.sin(2 * angleA));
		case 2860:
			return b
					* (-a + b)
					* (a - c)
					* c
					* (a4 + a3 * b + a2 * b2 + a * b3 + b4 - 2 * a3 * c - 2
							* a2 * b * c - 2 * a * b2 * c - 2 * b3 * c + a2
							* c2 + a * b * c2 + b2 * c2)
					* (a4 - 2 * a3 * b + a2 * b2 + a3 * c - 2 * a2 * b * c + a
							* b2 * c + a2 * c2 - 2 * a * b * c2 + b2 * c2 + a
							* c3 - 2 * b * c3 + c4);
		case 2861:
			return (-a6 + a4 * b2 + a2 * b4 - b6 + 2 * a5 * c - 2 * a4 * b * c
					- 2 * a * b4 * c + 2 * b5 * c + 2 * a3 * b * c2 - 2 * a2
					* b2 * c2 + 2 * a * b3 * c2 - 2 * a3 * c3 - 2 * b3 * c3
					+ a2 * c4 + b2 * c4)
					* (-a6 + 2 * a5 * b - 2 * a3 * b3 + a2 * b4 - 2 * a4 * b
							* c + 2 * a3 * b2 * c + a4 * c2 - 2 * a2 * b2 * c2
							+ b4 * c2 + 2 * a * b2 * c3 - 2 * b3 * c3 + a2 * c4
							- 2 * a * b * c4 + 2 * b * c5 - c6);
		case 2862:
			return (-a5 + a4 * b + a * b4 - b5 + a4 * c + b4 * c - a3 * c2 - a2
					* b * c2 - a * b2 * c2 - b3 * c2 + a2 * c3 + b2 * c3)
					* (-a5 + a4 * b - a3 * b2 + a2 * b3 + a4 * c - a2 * b2 * c
							- a * b2 * c2 + b3 * c2 - b2 * c3 + a * c4 + b * c4 - c5);
		case 2863:
			return b
					* c
					* (a5 - 2 * a4 * b - 2 * a * b4 + b5 + 2 * a3 * b * c + 2
							* a * b3 * c - a3 * c2 - b3 * c2)
					* (a5 - a3 * b2 - 2 * a4 * c + 2 * a3 * b * c + 2 * a * b
							* c3 - b2 * c3 - 2 * a * c4 + c5);
		case 2864:
			return b
					* (-a + b)
					* (a - c)
					* c
					* (a5 + a4 * b + a3 * b2 + a2 * b3 + a * b4 + b5 - a4 * c
							- a3 * b * c - a2 * b2 * c - a * b3 * c - b4 * c
							- a3 * c2 - a2 * b * c2 - a * b2 * c2 - b3 * c2
							+ a2 * c3 + a * b * c3 + b2 * c3)
					* (a5 - a4 * b - a3 * b2 + a2 * b3 + a4 * c - a3 * b * c
							- a2 * b2 * c + a * b3 * c + a3 * c2 - a2 * b * c2
							- a * b2 * c2 + b3 * c2 + a2 * c3 - a * b * c3 - b2
							* c3 + a * c4 - b * c4 + c5);
		case 2865:
			return (a - b)
					* (-a + c)
					* (a6 - a4 * b2 - a2 * b4 + b6 - a5 * c + a4 * b * c - 2
							* a3 * b2 * c - 2 * a2 * b3 * c + a * b4 * c - b5
							* c + 2 * a3 * b * c2 - 2 * a2 * b2 * c2 + 2 * a
							* b3 * c2 + 2 * a2 * b * c3 + 2 * a * b2 * c3 - a2
							* c4 - 2 * a * b * c4 - b2 * c4 + a * c5 + b * c5)
					* (a6 - a5 * b - a2 * b4 + a * b5 + a4 * b * c + 2 * a3
							* b2 * c + 2 * a2 * b3 * c - 2 * a * b4 * c + b5
							* c - a4 * c2 - 2 * a3 * b * c2 - 2 * a2 * b2 * c2
							+ 2 * a * b3 * c2 - b4 * c2 - 2 * a2 * b * c3 + 2
							* a * b2 * c3 - a2 * c4 + a * b * c4 - b * c5 + c6);
		case 2866:
			return b
					* c
					* (a7 - 2 * a6 * b + a5 * b2 + a2 * b5 - 2 * a * b6 + b7
							- a3 * b2 * c2 - a2 * b3 * c2 + 2 * a3 * b * c3 + 2
							* a * b3 * c3 - a3 * c4 - b3 * c4)
					* (a7 - a3 * b4 - 2 * a6 * c + 2 * a3 * b3 * c + a5 * c2
							- a3 * b2 * c2 - a2 * b2 * c3 + 2 * a * b3 * c3
							- b4 * c3 + a2 * c5 - 2 * a * c6 + c7);
		case 2867:
			return (a - b) * (a + b) * (-a + c) * (a + c)
					* (-a4 + b4 - a3 * c + a * b2 * c - a * c3 - c4)
					* (-a4 + b4 + a3 * c - a * b2 * c + a * c3 - c4)
					* (-a4 + a3 * b + a * b3 - b4 - a * b * c2 + c4)
					* (-a4 - a3 * b - a * b3 - b4 + a * b * c2 + c4);
		case 2868:
			return b2
					* c2
					* (a8 - 2 * a6 * b2 - 2 * a2 * b6 + b8 + 2 * a4 * b2 * c2
							+ 2 * a2 * b4 * c2 - a4 * c4 - b4 * c4)
					* (a8 - a4 * b4 - 2 * a6 * c2 + 2 * a4 * b2 * c2 + 2 * a2
							* b2 * c4 - b4 * c4 - 2 * a2 * c6 + c8);
		case 2869:
			return a2
					* (b2 - c2)
					* (a8 - 2 * a6 * b2 + 2 * a4 * b4 - 2 * a2 * b6 + b8 - 2
							* a6 * c2 + 7 * a4 * b2 * c2 - 2 * a2 * b4 * c2
							+ b6 * c2 + 2 * a4 * c4 - 2 * a2 * b2 * c4 - 4 * b4
							* c4 - 2 * a2 * c6 + b2 * c6 + c8);
		case 2870:
			return a
					* (a5 * b - a4 * b2 + a * b5 - b6 + a5 * c - a3 * b2 * c
							- a4 * c2 - a3 * b * c2 + 2 * a2 * b2 * c2 - a * b3
							* c2 + b4 * c2 - a * b2 * c3 + b2 * c4 + a * c5 - c6);
		case 2871:
			return a2
					* (a6 * b2 - a4 * b4 + a2 * b6 - b8 + a6 * c2 - 2 * a4 * b2
							* c2 + b6 * c2 - a4 * c4 + a2 * c6 + b2 * c6 - c8);
		case 2872:
			return a2 * (b2 - c2) * (a4 + b4 - 3 * b2 * c2 + c4);
		case 2873:
			return a2
					/ (-c
							* b
							* (b * c * Math.sin(2 * angleB) + b * a
									* Math.sin(2 * angleA) - b * a
									* Math.sin(2 * angleB) + c * a
									* Math.sin(2 * angleC) - c * a
									* Math.sin(2 * angleA) - c * b
									* Math.sin(2 * angleC)) / (b3 * a
							* Math.sin(2 * angleA) - b3 * a
							* Math.sin(2 * angleB) - b4 * Math.sin(2 * angleA)
							+ b4 * Math.sin(2 * angleB) - c4
							* Math.sin(2 * angleC) + c3 * a
							* Math.sin(2 * angleC) + c4 * Math.sin(2 * angleA) - c3
							* a * Math.sin(2 * angleA)));
		case 2874:
			return a3
					* (b - c)
					* (a2 * b2 - 2 * a * b3 + b4 + a2 * b * c - 2 * a * b2 * c
							+ b3 * c + a2 * c2 - 2 * a * b * c2 + b2 * c2 - 2
							* a * c3 + b * c3 + c4);
		case 2875:
			return a2
					* (a4 * b2 - 2 * a3 * b3 + 2 * a * b5 - b6 + 2 * a2 * b3
							* c - 2 * a * b4 * c + a4 * c2 - 2 * a2 * b2 * c2
							+ b4 * c2 - 2 * a3 * c3 + 2 * a2 * b * c3 - 2 * a
							* b * c4 + b2 * c4 + 2 * a * c5 - c6);
		case 2876:
			return a2
					* (a3 * b2 - a2 * b3 + a * b4 - b5 - a2 * b2 * c + b4 * c
							+ a3 * c2 - a2 * b * c2 - a2 * c3 + a * c4 + b * c4 - c5);
		case 2877:
			return a3
					* (a2 * b3 - b5 - 2 * a * b3 * c + 2 * b4 * c + a2 * c3 - 2
							* a * b * c3 + 2 * b * c4 - c5);
		case 2878:
			return a3
					* (b - c)
					* (a3 * b2 - a2 * b3 - a * b4 + b5 + a3 * b * c - a2 * b2
							* c - a * b3 * c + b4 * c + a3 * c2 - a2 * b * c2
							- a * b2 * c2 + b3 * c2 - a2 * c3 - a * b * c3 + b2
							* c3 - a * c4 + b * c4 + c5);
		case 2879:
			return a2
					* (b - c)
					* (a5 * b - a4 * b2 - a * b5 + b6 + a5 * c - 2 * a4 * b * c
							+ 2 * a3 * b2 * c + 2 * a2 * b3 * c + a * b4 * c
							- a4 * c2 + 2 * a3 * b * c2 - 2 * a2 * b2 * c2 - 2
							* a * b3 * c2 - b4 * c2 + 2 * a2 * b * c3 - 2 * a
							* b2 * c3 + a * b * c4 - b2 * c4 - a * c5 + c6);
		case 2880:
			return a3
					* (a4 * b3 - b7 - 2 * a3 * b3 * c + 2 * b6 * c + a2 * b3
							* c2 - b5 * c2 + a4 * c3 - 2 * a3 * b * c3 + a2
							* b2 * c3 - b2 * c5 + 2 * b * c6 - c7);
		case 2881:
			return a2 * (b2 - c2)
					* (a4 - b4 + a2 * b * c - b3 * c - b * c3 - c4)
					* (a4 - b4 - a2 * b * c + b3 * c + b * c3 - c4);
		case 2882:
			return a4
					* (a4 * b4 - b8 - 2 * a2 * b4 * c2 + 2 * b6 * c2 + a4 * c4
							- 2 * a2 * b2 * c4 + 2 * b2 * c6 - c8);
		case 2883:
			return (3 * a4 - 2 * a2 * b2 - b4 - 2 * a2 * c2 + 2 * b2 * c2 - c4)
					* (a4 * b2 - 2 * a2 * b4 + b6 + a4 * c2 + 4 * a2 * b2 * c2
							- b4 * c2 - 2 * a2 * c4 - b2 * c4 + c6);
		case 2884:
			return (3 * a2 - 2 * a * b - b2 - 2 * a * c + 2 * b * c - c2)
					* (a2 * b2 - 2 * a * b3 + b4 + 2 * a * b2 * c + 2 * b3 * c
							+ a2 * c2 + 2 * a * b * c2 - 6 * b2 * c2 - 2 * a
							* c3 + 2 * b * c3 + c4);
		case 2885:
			return (3 * a - b - c)
					* (a * b2 + b3 - 3 * b2 * c + a * c2 - 3 * b * c2 + c3);
		case 2886:
			return a * b2 - b3 + b2 * c + a * c2 + b * c2 - c3;
		case 2887:
			return (b + c) * (b2 - b * c + c2);
		case 2888:
			return a10 - 3 * a8 * b2 + 4 * a6 * b4 - 4 * a4 * b6 + 3 * a2 * b8
					- b10 - 3 * a8 * c2 + 5 * a6 * b2 * c2 - 2 * a4 * b4 * c2
					- 3 * a2 * b6 * c2 + 3 * b8 * c2 + 4 * a6 * c4 - 2 * a4
					* b2 * c4 - 2 * b6 * c4 - 4 * a4 * c6 - 3 * a2 * b2 * c6
					- 2 * b4 * c6 + 3 * a2 * c8 + 3 * b2 * c8 - c10;
		case 2889:
			return a10 - 7 * a8 * b2 + 16 * a6 * b4 - 16 * a4 * b6 + 7 * a2
					* b8 - b10 - 7 * a8 * c2 + 25 * a6 * b2 * c2 - 14 * a4 * b4
					* c2 - 7 * a2 * b6 * c2 + 3 * b8 * c2 + 16 * a6 * c4 - 14
					* a4 * b2 * c4 - 2 * b6 * c4 - 16 * a4 * c6 - 7 * a2 * b2
					* c6 - 2 * b4 * c6 + 7 * a2 * c8 + 3 * b2 * c8 - c10;
		case 2890:
			return a6 - 3 * a5 * b + 3 * a4 * b2 - 3 * a2 * b4 + 3 * a * b5
					- b6 - 3 * a5 * c + 3 * a4 * b * c + 2 * a3 * b2 * c - 2
					* a2 * b3 * c - 3 * a * b4 * c + 3 * b5 * c + 3 * a4 * c2
					+ 2 * a3 * b * c2 - 2 * a2 * b2 * c2 - 3 * b4 * c2 - 2 * a2
					* b * c3 + 2 * b3 * c3 - 3 * a2 * c4 - 3 * a * b * c4 - 3
					* b2 * c4 + 3 * a * c5 + 3 * b * c5 - c6;
		case 2891:
			return a4 + 3 * a3 * b - 3 * a * b3 - b4 + 3 * a3 * c + 5 * a2 * b
					* c - 5 * a * b2 * c - 3 * b3 * c - 5 * a * b * c2 - 4 * b2
					* c2 - 3 * a * c3 - 3 * b * c3 - c4;
		case 2892:
			return a10 * a4 - a10 * a2 * b2 - a10 * b4 + a8 * b6 - a6 * b8 + a4
					* b10 + a2 * b10 * b2 - b4 * b10 - a10 * a2 * c2 + 7 * a10
					* b2 * c2 - 5 * a8 * b4 * c2 - 2 * a6 * b6 * c2 + 5 * a4
					* b8 * c2 - 5 * a2 * b10 * c2 + b10 * b2 * c2 - a10 * c4
					- 5 * a8 * b2 * c4 + 10 * a6 * b4 * c4 - 6 * a4 * b6 * c4
					- a2 * b8 * c4 + 3 * b10 * c4 + a8 * c6 - 2 * a6 * b2 * c6
					- 6 * a4 * b4 * c6 + 10 * a2 * b6 * c6 - 3 * b8 * c6 - a6
					* c8 + 5 * a4 * b2 * c8 - a2 * b4 * c8 - 3 * b6 * c8 + a4
					* c10 - 5 * a2 * b2 * c10 + 3 * b4 * c10 + a2 * c10 * c2
					+ b2 * c10 * c2 - c10 * c4;
		case 2893:
			return a5 - a3 * b2 + a2 * b3 - b5 - a3 * b * c + a * b3 * c - a3
					* c2 + 2 * a * b2 * c2 + b3 * c2 + a2 * c3 + a * b * c3
					+ b2 * c3 - c5;
		case 2894:
			return a7 - a6 * b - a5 * b2 + a4 * b3 - a3 * b4 + a2 * b5 + a * b6
					- b7 - a6 * c - a5 * b * c + 3 * a4 * b2 * c - 2 * a3 * b3
					* c - 3 * a2 * b4 * c + 3 * a * b5 * c + b6 * c - a5 * c2
					+ 3 * a4 * b * c2 + 2 * a3 * b2 * c2 - 6 * a2 * b3 * c2 - a
					* b4 * c2 + 3 * b5 * c2 + a4 * c3 - 2 * a3 * b * c3 - 6
					* a2 * b2 * c3 - 6 * a * b3 * c3 - 3 * b4 * c3 - a3 * c4
					- 3 * a2 * b * c4 - a * b2 * c4 - 3 * b3 * c4 + a2 * c5 + 3
					* a * b * c5 + 3 * b2 * c5 + a * c6 + b * c6 - c7;
		case 2895:
			return a3 + a2 * b - a * b2 - b3 + a2 * c - a * b * c - b2 * c - a
					* c2 - b * c2 - c3;
		case 2896:
			return a4 - a2 * b2 - b4 - a2 * c2 - b2 * c2 - c4;
		case 2897:
			return a8 - 2 * a6 * b2 + 2 * a2 * b6 - b8 - 3 * a6 * b * c - 3
					* a5 * b2 * c + 2 * a4 * b3 * c + 2 * a3 * b4 * c + a2 * b5
					* c + a * b6 * c - 2 * a6 * c2 - 3 * a5 * b * c2 + 4 * a4
					* b2 * c2 + 2 * a3 * b3 * c2 - 2 * a2 * b4 * c2 + a * b5
					* c2 + 2 * a4 * b * c3 + 2 * a3 * b2 * c3 - 2 * a2 * b3
					* c3 - 2 * a * b4 * c3 + 2 * a3 * b * c4 - 2 * a2 * b2 * c4
					- 2 * a * b3 * c4 + 2 * b4 * c4 + a2 * b * c5 + a * b2 * c5
					+ 2 * a2 * c6 + a * b * c6 - c8;
		case 2898:
			return (a + b - c)
					* (a - b + c)
					* (a4 - 2 * a3 * b + 2 * a2 * b2 - 2 * a * b3 + b4 - 2 * a3
							* c - 2 * a2 * b * c + 2 * a * b2 * c + 2 * b3 * c
							+ 2 * a2 * c2 + 2 * a * b * c2 - 6 * b2 * c2 - 2
							* a * c3 + 2 * b * c3 + c4);
		case 2899:
			return (a - b - c)
					* (a3 + a2 * b + a * b2 + b3 + a2 * c - 3 * b2 * c + a * c2
							- 3 * b * c2 + c3);
		default:
			return Double.NaN;
		}
	}

	private double weight2900to2949(int k, double a, double b, double c) {

		switch (k) {
		case 2900:
			return a
					* (-a + b + c)
					* (-a4 + 2 * a3 * b - 2 * a * b3 + b4 + 2 * a3 * c + 2 * a2
							* b * c - 2 * b2 * c2 - 2 * a * c3 + c4);
		case 2901:
			return (b + c) * (a3 + a2 * b + a2 * c - b2 * c - b * c2);
		case 2902:
			return a2
					* (u(3) * a2 - u(3) * b2 - u(3) * c2 - S)
					* (u(3) * a6 - 3 * u(3) * a4 * b2 + 3 * u(3) * a2 * b4
							- u(3) * b6 - 3 * u(3) * a4 * c2 - 2 * u(3) * a2
							* b2 * c2 + u(3) * b4 * c2 + 3 * u(3) * a2 * c4
							+ u(3) * b2 * c4 - u(3) * c6 - a4 * S + b4 * S - 2
							* b2 * c2 * S + c4 * S);
		case 2903:
			return a2
					* (u(3) * a2 - u(3) * b2 - u(3) * c2 + S)
					* (u(3) * a6 - 3 * u(3) * a4 * b2 + 3 * u(3) * a2 * b4
							- u(3) * b6 - 3 * u(3) * a4 * c2 - 2 * u(3) * a2
							* b2 * c2 + u(3) * b4 * c2 + 3 * u(3) * a2 * c4
							+ u(3) * b2 * c4 - u(3) * c6 + a4 * S - b4 * S + 2
							* b2 * c2 * S - c4 * S);
		case 2904:
			return a2
					* (-V)
					* (-U)
					* (a4 - 2 * a2 * b2 + b4 - 2 * a2 * c2 + c4)
					* (a6 - 3 * a4 * b2 + 3 * a2 * b4 - b6 - 3 * a4 * c2 + b4
							* c2 + 3 * a2 * c4 + b2 * c4 - c6);
		case 2905:
			return (a + b) * (a + c) * (-V)
					* (a2 - a * b - b2 - a * c - b * c - c2) * (-U);
		case 2906:
			return a
					* (a + b)
					* (a + c)
					* (-V)
					* (-U)
					* (-a3 - a2 * b + a * b2 + b3 - a2 * c + a * b * c + b2 * c
							+ a * c2 + b * c2 + c3);
		case 2907:
			return (a + b) * (a - b - c) * (a + c) * (-V) * (-U)
					* (a3 + 2 * a2 * b - b3 + 2 * a2 * c + a * b * c - c3);
		case 2908:
			return a3
					* (a5 - a3 * b2 + a2 * b3 - b5 - a3 * c2 + b3 * c2 + a2
							* c3 + b2 * c3 - c5);
		case 2909:
			return a4
					* (a6 - a4 * b2 + a2 * b4 - b6 - a4 * c2 + b4 * c2 + a2
							* c4 + b2 * c4 - c6);
		case 2910:
			return a
					* (-a3 - a2 * b + a * b2 + b3 - a2 * c + 2 * a * b * c - b2
							* c + a * c2 - b * c2 + c3)
					* (-a6 + 3 * a4 * b2 - 3 * a2 * b4 + b6 + 2 * a3 * b2 * c
							- 2 * a2 * b3 * c - 2 * a * b4 * c + 2 * b5 * c + 3
							* a4 * c2 + 2 * a3 * b * c2 + 2 * a2 * b2 * c2 + 2
							* a * b3 * c2 - b4 * c2 - 2 * a2 * b * c3 + 2 * a
							* b2 * c3 - 4 * b3 * c3 - 3 * a2 * c4 - 2 * a * b
							* c4 - b2 * c4 + 2 * b * c5 + c6);
		case 2911:
			return a2
					* (a3 - a2 * b - a * b2 + b3 - a2 * c - 2 * a * b * c + b2
							* c - a * c2 + b * c2 + c3);
		case 2912:
			return a2
					* (a2 - b2 - c2 - u(3) * S)
					* (a6 - 3 * a4 * b2 + 3 * a2 * b4 - b6 - 3 * a4 * c2 - 2
							* a2 * b2 * c2 + b4 * c2 + 3 * a2 * c4 + b2 * c4
							- c6 - u(3) * a4 * S + u(3) * b4 * S - 2 * u(3)
							* b2 * c2 * S + u(3) * c4 * S);
		case 2913:
			return a2
					* (a2 - b2 - c2 + u(3) * S)
					* (a6 - 3 * a4 * b2 + 3 * a2 * b4 - b6 - 3 * a4 * c2 - 2
							* a2 * b2 * c2 + b4 * c2 + 3 * a2 * c4 + b2 * c4
							- c6 + u(3) * a4 * S - u(3) * b4 * S + 2 * u(3)
							* b2 * c2 * S - u(3) * c4 * S);
		case 2914:
			return a2
					* (-V)
					* (a2 - b2 - b * c - c2)
					* (a2 - b2 + b * c - c2)
					* (-U)
					* (a6 - 3 * a4 * b2 + 3 * a2 * b4 - b6 - 3 * a4 * c2 - a2
							* b2 * c2 + b4 * c2 + 3 * a2 * c4 + b2 * c4 - c6);
		case 2915:
			return a2
					* (a5 + a4 * b - a * b4 - b5 + a4 * c + a3 * b * c - a * b3
							* c - b4 * c - a * b * c3 - a * c4 - b * c4 - c5);
		case 2916:
			return a2
					* (a6 + a4 * b2 - a2 * b4 - b6 + a4 * c2 - a2 * b2 * c2
							- b4 * c2 - a2 * c4 - b2 * c4 - c6);
		case 2917:
			return a2
					* (a10 * a4 - 3 * a10 * a2 * b2 + a10 * b4 + 5 * a8 * b6
							- 5 * a6 * b8 - a4 * b10 + 3 * a2 * b10 * b2 - b4
							* b10 - 3 * a10 * a2 * c2 + 3 * a10 * b2 * c2 + 5
							* a8 * b4 * c2 - 6 * a6 * b6 * c2 + 3 * a4 * b8
							* c2 - 5 * a2 * b10 * c2 + 3 * b10 * b2 * c2 + a10
							* c4 + 5 * a8 * b2 * c4 - 2 * a6 * b4 * c4 - 2 * a4
							* b6 * c4 + a2 * b8 * c4 - 3 * b10 * c4 + 5 * a8
							* c6 - 6 * a6 * b2 * c6 - 2 * a4 * b4 * c6 + 2 * a2
							* b6 * c6 + b8 * c6 - 5 * a6 * c8 + 3 * a4 * b2
							* c8 + a2 * b4 * c8 + b6 * c8 - a4 * c10 - 5 * a2
							* b2 * c10 - 3 * b4 * c10 + 3 * a2 * c10 * c2 + 3
							* b2 * c10 * c2 - c10 * c4);
		case 2918:
			return a2
					* (a10 * a4 - 3 * a10 * a2 * b2 + a10 * b4 + 5 * a8 * b6
							- 5 * a6 * b8 - a4 * b10 + 3 * a2 * b10 * b2 - b4
							* b10 - 3 * a10 * a2 * c2 - a10 * b2 * c2 + 13 * a8
							* b4 * c2 - 6 * a6 * b6 * c2 - 5 * a4 * b8 * c2
							- a2 * b10 * c2 + 3 * b10 * b2 * c2 + a10 * c4 + 13
							* a8 * b2 * c4 - 2 * a6 * b4 * c4 - 6 * a4 * b6
							* c4 - 3 * a2 * b8 * c4 - 3 * b10 * c4 + 5 * a8
							* c6 - 6 * a6 * b2 * c6 - 6 * a4 * b4 * c6 + 2 * a2
							* b6 * c6 + b8 * c6 - 5 * a6 * c8 - 5 * a4 * b2
							* c8 - 3 * a2 * b4 * c8 + b6 * c8 - a4 * c10 - a2
							* b2 * c10 - 3 * b4 * c10 + 3 * a2 * c10 * c2 + 3
							* b2 * c10 * c2 - c10 * c4);
		case 2919:
			return a2
					* (a10 - 2 * a9 * b - a8 * b2 + 4 * a7 * b3 - 2 * a6 * b4
							+ 2 * a4 * b6 - 4 * a3 * b7 + a2 * b8 + 2 * a * b9
							- b10 - 2 * a9 * c + 4 * a8 * b * c - 2 * a7 * b2
							* c - 2 * a6 * b3 * c + 6 * a5 * b4 * c - 6 * a4
							* b5 * c + 2 * a3 * b6 * c + 2 * a2 * b7 * c - 4
							* a * b8 * c + 2 * b9 * c - a8 * c2 - 2 * a7 * b
							* c2 + 5 * a6 * b2 * c2 + 2 * a5 * b3 * c2 - 6 * a4
							* b4 * c2 + 6 * a3 * b5 * c2 - 7 * a2 * b6 * c2 + 2
							* a * b7 * c2 + b8 * c2 + 4 * a7 * c3 - 2 * a6 * b
							* c3 + 2 * a5 * b2 * c3 + 8 * a4 * b3 * c3 - 8 * a3
							* b4 * c3 - 2 * a2 * b5 * c3 + 2 * a * b6 * c3 - 4
							* b7 * c3 - 2 * a6 * c4 + 6 * a5 * b * c4 - 6 * a4
							* b2 * c4 - 8 * a3 * b3 * c4 + 12 * a2 * b4 * c4
							- 2 * a * b5 * c4 - 6 * a4 * b * c5 + 6 * a3 * b2
							* c5 - 2 * a2 * b3 * c5 - 2 * a * b4 * c5 + 4 * b5
							* c5 + 2 * a4 * c6 + 2 * a3 * b * c6 - 7 * a2 * b2
							* c6 + 2 * a * b3 * c6 - 4 * a3 * c7 + 2 * a2 * b
							* c7 + 2 * a * b2 * c7 - 4 * b3 * c7 + a2 * c8 - 4
							* a * b * c8 + b2 * c8 + 2 * a * c9 + 2 * b * c9 - c10);
		case 2920:
			return a2
					* (a8 - 2 * a6 * b2 + 2 * a2 * b6 - b8 + 2 * a6 * b * c + 2
							* a5 * b2 * c - 2 * a2 * b5 * c - 2 * a * b6 * c
							- 2 * a6 * c2 + 2 * a5 * b * c2 - 3 * a4 * b2 * c2
							- 2 * a3 * b3 * c2 + a2 * b4 * c2 - 2 * a * b5 * c2
							+ 2 * b6 * c2 - 2 * a3 * b2 * c3 + 6 * a2 * b3 * c3
							+ a2 * b2 * c4 - 2 * b4 * c4 - 2 * a2 * b * c5 - 2
							* a * b2 * c5 + 2 * a2 * c6 - 2 * a * b * c6 + 2
							* b2 * c6 - c8);
		case 2921:
			return a2
					* (a7 - a6 * b - a5 * b2 + a4 * b3 - a3 * b4 + a2 * b5 + a
							* b6 - b7 - a6 * c + 7 * a5 * b * c - 3 * a4 * b2
							* c + 3 * a2 * b4 * c - 7 * a * b5 * c + b6 * c
							- a5 * c2 - 3 * a4 * b * c2 + 2 * a3 * b2 * c2 - 2
							* a2 * b3 * c2 + 3 * a * b4 * c2 + b5 * c2 + a4
							* c3 - 2 * a2 * b2 * c3 - 2 * a * b3 * c3 - b4 * c3
							- a3 * c4 + 3 * a2 * b * c4 + 3 * a * b2 * c4 - b3
							* c4 + a2 * c5 - 7 * a * b * c5 + b2 * c5 + a * c6
							+ b * c6 - c7);
		case 2922:
			return a2
					* (a8 + a7 * b + a5 * b3 - a3 * b5 - a * b7 - b8 + a7 * c
							+ a6 * b * c + a5 * b2 * c + a4 * b3 * c - a3 * b4
							* c - a2 * b5 * c - a * b6 * c - b7 * c + a5 * b
							* c2 - 2 * a4 * b2 * c2 - 2 * a3 * b3 * c2 - a * b5
							* c2 + a5 * c3 + a4 * b * c3 - 2 * a3 * b2 * c3 + 2
							* a2 * b3 * c3 - a * b4 * c3 - b5 * c3 - a3 * b
							* c4 - a * b3 * c4 - 2 * b4 * c4 - a3 * c5 - a2 * b
							* c5 - a * b2 * c5 - b3 * c5 - a * b * c6 - a * c7
							- b * c7 - c8);
		case 2929:
			return a2
					* (a10 * a4 - 3 * a10 * a2 * b2 + a10 * b4 + 5 * a8 * b6
							- 5 * a6 * b8 - a4 * b10 + 3 * a2 * b10 * b2 - b4
							* b10 - 3 * a10 * a2 * c2 + 11 * a10 * b2 * c2 - 11
							* a8 * b4 * c2 - 6 * a6 * b6 * c2 + 19 * a4 * b8
							* c2 - 13 * a2 * b10 * c2 + 3 * b10 * b2 * c2 + a10
							* c4 - 11 * a8 * b2 * c4 + 22 * a6 * b4 * c4 - 18
							* a4 * b6 * c4 + 9 * a2 * b8 * c4 - 3 * b10 * c4
							+ 5 * a8 * c6 - 6 * a6 * b2 * c6 - 18 * a4 * b4
							* c6 + 2 * a2 * b6 * c6 + b8 * c6 - 5 * a6 * c8
							+ 19 * a4 * b2 * c8 + 9 * a2 * b4 * c8 + b6 * c8
							- a4 * c10 - 13 * a2 * b2 * c10 - 3 * b4 * c10 + 3
							* a2 * c10 * c2 + 3 * b2 * c10 * c2 - c10 * c4);
		case 2930:
			return a2
					* (a6 + a4 * b2 - a2 * b4 - b6 + a4 * c2 - 5 * a2 * b2 * c2
							+ 3 * b4 * c2 - a2 * c4 + 3 * b2 * c4 - c6);
		case 2931:
			return a2
					* T
					* (a10 * a2 - 2 * a10 * b2 - a8 * b4 + 4 * a6 * b6 - a4
							* b8 - 2 * a2 * b10 + b10 * b2 - 2 * a10 * c2 + 3
							* a8 * b2 * c2 - a6 * b4 * c2 - 3 * a4 * b6 * c2
							+ 7 * a2 * b8 * c2 - 4 * b10 * c2 - a8 * c4 - a6
							* b2 * c4 + 4 * a4 * b4 * c4 - 5 * a2 * b6 * c4 + 7
							* b8 * c4 + 4 * a6 * c6 - 3 * a4 * b2 * c6 - 5 * a2
							* b4 * c6 - 8 * b6 * c6 - a4 * c8 + 7 * a2 * b2
							* c8 + 7 * b4 * c8 - 2 * a2 * c10 - 4 * b2 * c10 + c10
							* c2);

		case 2923:
		case 2924:
		case 2925:
		case 2926:
		case 2927:

		case 2928:

			double x = weight(k - 2910, a, b, c);
			double y = weight(k - 2910, b, c, a);
			double z = weight(k - 2910, c, a, b);

			return a2
					* (-a3 / (c * y + b * z) + b3 / (a * z + c * x) + c3
							/ (b * x + a * y));

		case 2932:
			return a2
					* (a5 - a4 * b - 2 * a3 * b2 + 2 * a2 * b3 + a * b4 - b5
							- a4 * c + 5 * a3 * b * c - 5 * a * b3 * c + b4 * c
							- 2 * a3 * c2 + 2 * b3 * c2 + 2 * a2 * c3 - 5 * a
							* b * c3 + 2 * b2 * c3 + a * c4 + b * c4 - c5);
		case 2933:
			return a2
					* (a5 - a3 * b2 + a2 * b3 - b5 + 2 * a3 * b * c - 2 * a
							* b3 * c - a3 * c2 + b3 * c2 + a2 * c3 - 2 * a * b
							* c3 + b2 * c3 - c5);
		case 2934:
			return a2
					* (a10 * a2 - 3 * a10 * b2 + 3 * a8 * b4 - 3 * a4 * b8 + 3
							* a2 * b10 - b10 * b2 - 3 * a10 * c2 + 3 * a8 * b2
							* c2 + 2 * a6 * b4 * c2 - 2 * a4 * b6 * c2 - 3 * a2
							* b8 * c2 + 3 * b10 * c2 + 3 * a8 * c4 + 2 * a6
							* b2 * c4 - 2 * a4 * b4 * c4 - 3 * b8 * c4 - 2 * a4
							* b2 * c6 + 2 * b6 * c6 - 3 * a4 * c8 - 3 * a2 * b2
							* c8 - 3 * b4 * c8 + 3 * a2 * c10 + 3 * b2 * c10 - c10
							* c2);
		case 2935:
			return a2
					* (a10 * a4 - 3 * a10 * a2 * b2 + a10 * b4 + 5 * a8 * b6
							- 5 * a6 * b8 - a4 * b10 + 3 * a2 * b10 * b2 - b4
							* b10 - 3 * a10 * a2 * c2 + 15 * a10 * b2 * c2 - 15
							* a8 * b4 * c2 - 14 * a6 * b6 * c2 + 27 * a4 * b8
							* c2 - 9 * a2 * b10 * c2 - b10 * b2 * c2 + a10 * c4
							- 15 * a8 * b2 * c4 + 46 * a6 * b4 * c4 - 26 * a4
							* b6 * c4 - 15 * a2 * b8 * c4 + 9 * b10 * c4 + 5
							* a8 * c6 - 14 * a6 * b2 * c6 - 26 * a4 * b4 * c6
							+ 42 * a2 * b6 * c6 - 7 * b8 * c6 - 5 * a6 * c8
							+ 27 * a4 * b2 * c8 - 15 * a2 * b4 * c8 - 7 * b6
							* c8 - a4 * c10 - 9 * a2 * b2 * c10 + 9 * b4 * c10
							+ 3 * a2 * c10 * c2 - b2 * c10 * c2 - c10 * c4);
		case 2936:
			return a2
					* (a8 - a6 * b2 + a2 * b6 - b8 - a6 * c2 + 5 * a4 * b2 * c2
							- 5 * a2 * b4 * c2 + b6 * c2 - 5 * a2 * b2 * c4 + 4
							* b4 * c4 + a2 * c6 + b2 * c6 - c8);
		case 2937:
			return a2
					* (a8 - 2 * a6 * b2 + 2 * a2 * b6 - b8 - 2 * a6 * c2 - a4
							* b2 * c2 + a2 * b4 * c2 + 2 * b6 * c2 + a2 * b2
							* c4 - 2 * b4 * c4 + 2 * a2 * c6 + 2 * b2 * c6 - c8);
		case 2938:
			return a
					* (a4 + 3 * a3 * b - 2 * a2 * b2 - a * b3 - b4 + 3 * a3 * c
							+ a2 * b * c - 3 * a * b2 * c - b3 * c - 2 * a2
							* c2 - 3 * a * b * c2 + 4 * b2 * c2 - a * c3 - b
							* c3 - c4);
		case 2939:
			return a
					* (a6 + 3 * a5 * b + a4 * b2 - 2 * a3 * b3 - a2 * b4 - a
							* b5 - b6 + 3 * a5 * c + 3 * a4 * b * c - 2 * a3
							* b2 * c - 2 * a2 * b3 * c - a * b4 * c - b5 * c
							+ a4 * c2 - 2 * a3 * b * c2 - 2 * a2 * b2 * c2 + 2
							* a * b3 * c2 + b4 * c2 - 2 * a3 * c3 - 2 * a2 * b
							* c3 + 2 * a * b2 * c3 + 2 * b3 * c3 - a2 * c4 - a
							* b * c4 + b2 * c4 - a * c5 - b * c5 - c6);
		case 2940:
			return a
					* (a6 + 4 * a5 * b + 3 * a4 * b2 - 2 * a3 * b3 - 3 * a2
							* b4 - 2 * a * b5 - b6 + 4 * a5 * c + 8 * a4 * b
							* c - 6 * a2 * b3 * c - 4 * a * b4 * c - 2 * b5 * c
							+ 3 * a4 * c2 - 5 * a2 * b2 * c2 + b4 * c2 - 2 * a3
							* c3 - 6 * a2 * b * c3 + 4 * b3 * c3 - 3 * a2 * c4
							- 4 * a * b * c4 + b2 * c4 - 2 * a * c5 - 2 * b
							* c5 - c6);
		case 2941:
			return a
					* (a5 + a4 * b - a * b4 - b5 + a4 * c + 5 * a3 * b * c - 2
							* a2 * b2 * c - 3 * a * b3 * c - b4 * c - 2 * a2
							* b * c2 + 2 * b3 * c2 - 3 * a * b * c3 + 2 * b2
							* c3 - a * c4 - b * c4 - c5);
		case 2942:
			return a
					* (a8 - a7 * b - 8 * a6 * b2 + 21 * a5 * b3 - 20 * a4 * b4
							+ 9 * a3 * b5 - 4 * a2 * b6 + 3 * a * b7 - b8 - a7
							* c - 3 * a6 * b * c + 11 * a5 * b2 * c - 7 * a4
							* b3 * c - 3 * a3 * b4 * c + 7 * a2 * b5 * c - 7
							* a * b6 * c + 3 * b7 * c - 8 * a6 * c2 + 11 * a5
							* b * c2 + 6 * a4 * b2 * c2 - 6 * a3 * b3 * c2 - 8
							* a2 * b4 * c2 + 3 * a * b5 * c2 + 2 * b6 * c2 + 21
							* a5 * c3 - 7 * a4 * b * c3 - 6 * a3 * b2 * c3 + 10
							* a2 * b3 * c3 + a * b4 * c3 - 19 * b5 * c3 - 20
							* a4 * c4 - 3 * a3 * b * c4 - 8 * a2 * b2 * c4 + a
							* b3 * c4 + 30 * b4 * c4 + 9 * a3 * c5 + 7 * a2 * b
							* c5 + 3 * a * b2 * c5 - 19 * b3 * c5 - 4 * a2 * c6
							- 7 * a * b * c6 + 2 * b2 * c6 + 3 * a * c7 + 3 * b
							* c7 - c8);
		case 2943:
			return a
					* (a6 + a5 * b - 3 * a4 * b2 - 2 * a3 * b3 + 3 * a2 * b4
							+ a * b5 - b6 + a5 * c - a4 * b * c + 8 * a3 * b2
							* c - 9 * a * b4 * c + b5 * c - 3 * a4 * c2 + 8
							* a3 * b * c2 - 18 * a2 * b2 * c2 + 8 * a * b3 * c2
							+ b4 * c2 - 2 * a3 * c3 + 8 * a * b2 * c3 - 2 * b3
							* c3 + 3 * a2 * c4 - 9 * a * b * c4 + b2 * c4 + a
							* c5 + b * c5 - c6);
		case 2944:
			return a
					* (a6 + 3 * a5 * b + a4 * b2 - 2 * a3 * b3 - a2 * b4 - a
							* b5 - b6 + 3 * a5 * c + 3 * a4 * b * c + 2 * a3
							* b2 * c - 2 * a2 * b3 * c - 5 * a * b4 * c - b5
							* c + a4 * c2 + 2 * a3 * b * c2 - 6 * a2 * b2 * c2
							- 2 * a * b3 * c2 + b4 * c2 - 2 * a3 * c3 - 2 * a2
							* b * c3 - 2 * a * b2 * c3 + 2 * b3 * c3 - a2 * c4
							- 5 * a * b * c4 + b2 * c4 - a * c5 - b * c5 - c6);
		case 2945:
			return a
					* (a6 - a5 * b - 5 * a4 * b2 + 5 * a2 * b4 + a * b5 - b6
							- a5 * c - 5 * a4 * b * c + 4 * a2 * b3 * c + a
							* b4 * c + b5 * c - 5 * a4 * c2 + 2 * a2 * b2 * c2
							+ 2 * a * b3 * c2 + b4 * c2 + 4 * a2 * b * c3 + 2
							* a * b2 * c3 - 2 * b3 * c3 + 5 * a2 * c4 + a * b
							* c4 + b2 * c4 + a * c5 + b * c5 - c6 - u(3) * a4
							* S - u(3) * a3 * b * S + u(3) * a * b3 * S + u(3)
							* b4 * S - u(3) * a3 * c * S - u(3) * a2 * b * c
							* S + u(3) * a * b2 * c * S + u(3) * b3 * c * S
							+ u(3) * a * b * c2 * S + u(3) * a * c3 * S + u(3)
							* b * c3 * S + u(3) * c4 * S);
		case 2946:
			return a
					* (a6 - a5 * b - 5 * a4 * b2 + 5 * a2 * b4 + a * b5 - b6
							- a5 * c - 5 * a4 * b * c + 4 * a2 * b3 * c + a
							* b4 * c + b5 * c - 5 * a4 * c2 + 2 * a2 * b2 * c2
							+ 2 * a * b3 * c2 + b4 * c2 + 4 * a2 * b * c3 + 2
							* a * b2 * c3 - 2 * b3 * c3 + 5 * a2 * c4 + a * b
							* c4 + b2 * c4 + a * c5 + b * c5 - c6 + u(3) * a4
							* S + u(3) * a3 * b * S - u(3) * a * b3 * S - u(3)
							* b4 * S + u(3) * a3 * c * S + u(3) * a2 * b * c
							* S - u(3) * a * b2 * c * S - u(3) * b3 * c * S
							- u(3) * a * b * c2 * S - u(3) * a * c3 * S - u(3)
							* b * c3 * S - u(3) * c4 * S);
		case 2947:
			return a
					* (a7 * b - 2 * a6 * b2 - a5 * b3 + 4 * a4 * b4 - a3 * b5
							- 2 * a2 * b6 + a * b7 + a7 * c - a6 * b * c - a5
							* b2 * c + a4 * b3 * c - a3 * b4 * c + a2 * b5 * c
							+ a * b6 * c - b7 * c - 2 * a6 * c2 - a5 * b * c2
							- 2 * a4 * b2 * c2 + 2 * a3 * b3 * c2 + 2 * a2 * b4
							* c2 - a * b5 * c2 + 2 * b6 * c2 - a5 * c3 + a4 * b
							* c3 + 2 * a3 * b2 * c3 - 2 * a2 * b3 * c3 - a * b4
							* c3 + b5 * c3 + 4 * a4 * c4 - a3 * b * c4 + 2 * a2
							* b2 * c4 - a * b3 * c4 - 4 * b4 * c4 - a3 * c5
							+ a2 * b * c5 - a * b2 * c5 + b3 * c5 - 2 * a2 * c6
							+ a * b * c6 + 2 * b2 * c6 + a * c7 - b * c7);
		case 2948:
			return a
					* (a6 + 2 * a5 * b - a4 * b2 - 2 * a3 * b3 + a2 * b4 - b6
							+ 2 * a5 * c - 2 * a3 * b2 * c - a4 * c2 - 2 * a3
							* b * c2 - a2 * b2 * c2 + 2 * a * b3 * c2 + b4 * c2
							- 2 * a3 * c3 + 2 * a * b2 * c3 + a2 * c4 + b2 * c4 - c6);
		case 2949:
			return a
					* (a9 - a8 * b - 4 * a7 * b2 + 4 * a6 * b3 + 6 * a5 * b4
							- 6 * a4 * b5 - 4 * a3 * b6 + 4 * a2 * b7 + a * b8
							- b9 - a8 * c - 3 * a7 * b * c + 4 * a6 * b2 * c
							+ 7 * a5 * b3 * c - 4 * a4 * b4 * c - 5 * a3 * b5
							* c + a * b7 * c + b8 * c - 4 * a7 * c2 + 4 * a6
							* b * c2 + 10 * a5 * b2 * c2 - 2 * a4 * b3 * c2 - 4
							* a3 * b4 * c2 - 4 * a2 * b5 * c2 - 2 * a * b6 * c2
							+ 2 * b7 * c2 + 4 * a6 * c3 + 7 * a5 * b * c3 - 2
							* a4 * b2 * c3 - 6 * a3 * b3 * c3 - a * b5 * c3 - 2
							* b6 * c3 + 6 * a5 * c4 - 4 * a4 * b * c4 - 4 * a3
							* b2 * c4 + 2 * a * b4 * c4 - 6 * a4 * c5 - 5 * a3
							* b * c5 - 4 * a2 * b2 * c5 - a * b3 * c5 - 4 * a3
							* c6 - 2 * a * b2 * c6 - 2 * b3 * c6 + 4 * a2 * c7
							+ a * b * c7 + 2 * b2 * c7 + a * c8 + b * c8 - c9);
		default:
			return Double.NaN;
		}
	}

	private double weight2950to2999(int k, double a, double b, double c) {

		switch (k) {
		case 2950:
			return a
					* (a9 - a8 * b - 4 * a7 * b2 + 4 * a6 * b3 + 6 * a5 * b4
							- 6 * a4 * b5 - 4 * a3 * b6 + 4 * a2 * b7 + a * b8
							- b9 - a8 * c + 5 * a7 * b * c + 4 * a6 * b2 * c
							- 17 * a5 * b3 * c - 4 * a4 * b4 * c + 19 * a3 * b5
							* c - 7 * a * b7 * c + b8 * c - 4 * a7 * c2 + 4
							* a6 * b * c2 + 2 * a5 * b2 * c2 + 14 * a4 * b3
							* c2 - 4 * a3 * b4 * c2 - 20 * a2 * b5 * c2 + 6 * a
							* b6 * c2 + 2 * b7 * c2 + 4 * a6 * c3 - 17 * a5 * b
							* c3 + 14 * a4 * b2 * c3 - 22 * a3 * b3 * c3 + 16
							* a2 * b4 * c3 + 7 * a * b5 * c3 - 2 * b6 * c3 + 6
							* a5 * c4 - 4 * a4 * b * c4 - 4 * a3 * b2 * c4 + 16
							* a2 * b3 * c4 - 14 * a * b4 * c4 - 6 * a4 * c5
							+ 19 * a3 * b * c5 - 20 * a2 * b2 * c5 + 7 * a * b3
							* c5 - 4 * a3 * c6 + 6 * a * b2 * c6 - 2 * b3 * c6
							+ 4 * a2 * c7 - 7 * a * b * c7 + 2 * b2 * c7 + a
							* c8 + b * c8 - c9);
		case 2951:
			return a
					* (a4 - 4 * a3 * b + 6 * a2 * b2 - 4 * a * b3 + b4 - 4 * a3
							* c - 4 * a2 * b * c + 4 * a * b2 * c + 4 * b3 * c
							+ 6 * a2 * c2 + 4 * a * b * c2 - 10 * b2 * c2 - 4
							* a * c3 + 4 * b * c3 + c4);
		case 2952:
			return a
					* (a6 + 3 * a5 * b + 3 * a4 * b2 - 3 * a2 * b4 - 3 * a * b5
							- b6 + 3 * a5 * c + 11 * a4 * b * c + 4 * a3 * b2
							* c - 8 * a2 * b3 * c - 7 * a * b4 * c - 3 * b5 * c
							+ 3 * a4 * c2 + 4 * a3 * b * c2 - 6 * a2 * b2 * c2
							- 2 * a * b3 * c2 + b4 * c2 - 8 * a2 * b * c3 - 2
							* a * b2 * c3 + 6 * b3 * c3 - 3 * a2 * c4 - 7 * a
							* b * c4 + b2 * c4 - 3 * a * c5 - 3 * b * c5 - c6
							+ u(3) * a4 * S + u(3) * a3 * b * S - u(3) * a * b3
							* S - u(3) * b4 * S + u(3) * a3 * c * S + u(3) * a2
							* b * c * S - u(3) * a * b2 * c * S - u(3) * b3 * c
							* S - u(3) * a * b * c2 * S - u(3) * a * c3 * S
							- u(3) * b * c3 * S - u(3) * c4 * S);
		case 2953:
			return a
					* (a6 + 3 * a5 * b + 3 * a4 * b2 - 3 * a2 * b4 - 3 * a * b5
							- b6 + 3 * a5 * c + 11 * a4 * b * c + 4 * a3 * b2
							* c - 8 * a2 * b3 * c - 7 * a * b4 * c - 3 * b5 * c
							+ 3 * a4 * c2 + 4 * a3 * b * c2 - 6 * a2 * b2 * c2
							- 2 * a * b3 * c2 + b4 * c2 - 8 * a2 * b * c3 - 2
							* a * b2 * c3 + 6 * b3 * c3 - 3 * a2 * c4 - 7 * a
							* b * c4 + b2 * c4 - 3 * a * c5 - 3 * b * c5 - c6
							- u(3) * a4 * S - u(3) * a3 * b * S + u(3) * a * b3
							* S + u(3) * b4 * S - u(3) * a3 * c * S - u(3) * a2
							* b * c * S + u(3) * a * b2 * c * S + u(3) * b3 * c
							* S + u(3) * a * b * c2 * S + u(3) * a * c3 * S
							+ u(3) * b * c3 * S + u(3) * c4 * S);
		case 2954:
			return a
					* (a8 - 3 * a7 * b + 7 * a5 * b3 - 4 * a4 * b4 - 5 * a3
							* b5 + 4 * a2 * b6 + a * b7 - b8 - 3 * a7 * c + a6
							* b * c + 7 * a5 * b2 * c - a4 * b3 * c - 5 * a3
							* b4 * c - a2 * b5 * c + a * b6 * c + b7 * c + 7
							* a5 * b * c2 + 2 * a4 * b2 * c2 - 6 * a3 * b3 * c2
							- 4 * a2 * b4 * c2 - a * b5 * c2 + 2 * b6 * c2 + 7
							* a5 * c3 - a4 * b * c3 - 6 * a3 * b2 * c3 + 2 * a2
							* b3 * c3 - a * b4 * c3 - b5 * c3 - 4 * a4 * c4 - 5
							* a3 * b * c4 - 4 * a2 * b2 * c4 - a * b3 * c4 - 2
							* b4 * c4 - 5 * a3 * c5 - a2 * b * c5 - a * b2 * c5
							- b3 * c5 + 4 * a2 * c6 + a * b * c6 + 2 * b2 * c6
							+ a * c7 + b * c7 - c8);
		case 2955:
			return a
					* (a9 + a8 * b - 2 * a7 * b2 - 2 * a6 * b3 + 2 * a3 * b6
							+ 2 * a2 * b7 - a * b8 - b9 + a8 * c - 3 * a7 * b
							* c + 4 * a6 * b2 * c + 3 * a5 * b3 * c - 8 * a4
							* b4 * c + 3 * a3 * b5 * c - 3 * a * b7 * c + 3
							* b8 * c - 2 * a7 * c2 + 4 * a6 * b * c2 + 10 * a5
							* b2 * c2 - 6 * a3 * b4 * c2 - 4 * a2 * b5 * c2 - 2
							* a * b6 * c2 - 2 * a6 * c3 + 3 * a5 * b * c3 - 14
							* a3 * b3 * c3 + 2 * a2 * b4 * c3 + 3 * a * b5 * c3
							- 8 * b6 * c3 - 8 * a4 * b * c4 - 6 * a3 * b2 * c4
							+ 2 * a2 * b3 * c4 + 6 * a * b4 * c4 + 6 * b5 * c4
							+ 3 * a3 * b * c5 - 4 * a2 * b2 * c5 + 3 * a * b3
							* c5 + 6 * b4 * c5 + 2 * a3 * c6 - 2 * a * b2 * c6
							- 8 * b3 * c6 + 2 * a2 * c7 - 3 * a * b * c7 - a
							* c8 + 3 * b * c8 - c9);
		case 2956:
			return a
					* (3 * a6 + 2 * a5 * b - 7 * a4 * b2 - 4 * a3 * b3 + 5 * a2
							* b4 + 2 * a * b5 - b6 + 2 * a5 * c + 6 * a4 * b
							* c + 4 * a3 * b2 * c - 4 * a2 * b3 * c - 6 * a
							* b4 * c - 2 * b5 * c - 7 * a4 * c2 + 4 * a3 * b
							* c2 - 2 * a2 * b2 * c2 + 4 * a * b3 * c2 + b4 * c2
							- 4 * a3 * c3 - 4 * a2 * b * c3 + 4 * a * b2 * c3
							+ 4 * b3 * c3 + 5 * a2 * c4 - 6 * a * b * c4 + b2
							* c4 + 2 * a * c5 - 2 * b * c5 - c6);
		case 2957:
			return a
					* (a7 - 2 * a6 * b + 3 * a4 * b3 - 3 * a3 * b4 + 2 * a * b6
							- b7 - 2 * a6 * c + 6 * a5 * b * c - 5 * a4 * b2
							* c - 2 * a3 * b3 * c + 7 * a2 * b4 * c - 6 * a
							* b5 * c + 2 * b6 * c - 5 * a4 * b * c2 + 11 * a3
							* b2 * c2 - 7 * a2 * b3 * c2 + a * b4 * c2 + 3 * a4
							* c3 - 2 * a3 * b * c3 - 7 * a2 * b2 * c3 + 6 * a
							* b3 * c3 - b4 * c3 - 3 * a3 * c4 + 7 * a2 * b * c4
							+ a * b2 * c4 - b3 * c4 - 6 * a * b * c5 + 2 * a
							* c6 + 2 * b * c6 - c7);
		case 2958:
			return a
					* (a9 - 2 * a8 * b - a7 * b2 + 3 * a6 * b3 + a5 * b4 - a4
							* b5 - 3 * a3 * b6 + a2 * b7 + 2 * a * b8 - b9 - 2
							* a8 * c + 8 * a7 * b * c - 5 * a6 * b2 * c - 4
							* a5 * b3 * c - 6 * a4 * b4 * c + 12 * a3 * b5 * c
							+ 3 * a2 * b6 * c - 8 * a * b7 * c + 2 * b8 * c
							- a7 * c2 - 5 * a6 * b * c2 + 7 * a5 * b2 * c2 + 7
							* a4 * b3 * c2 + a3 * b4 * c2 - 19 * a2 * b5 * c2
							+ 9 * a * b6 * c2 + b7 * c2 + 3 * a6 * c3 - 4 * a5
							* b * c3 + 7 * a4 * b2 * c3 - 20 * a3 * b3 * c3
							+ 15 * a2 * b4 * c3 + 4 * a * b5 * c3 - 5 * b6 * c3
							+ a5 * c4 - 6 * a4 * b * c4 + a3 * b2 * c4 + 15
							* a2 * b3 * c4 - 14 * a * b4 * c4 + 3 * b5 * c4
							- a4 * c5 + 12 * a3 * b * c5 - 19 * a2 * b2 * c5
							+ 4 * a * b3 * c5 + 3 * b4 * c5 - 3 * a3 * c6 + 3
							* a2 * b * c6 + 9 * a * b2 * c6 - 5 * b3 * c6 + a2
							* c7 - 8 * a * b * c7 + b2 * c7 + 2 * a * c8 + 2
							* b * c8 - c9);
		case 2959:
			return a
					* (a5 + 3 * a4 * b + a3 * b2 - a2 * b3 - a * b4 - b5 + 3
							* a4 * c + 4 * a3 * b * c - a2 * b2 * c - 2 * a
							* b3 * c - b4 * c + a3 * c2 - a2 * b * c2 - a * b2
							* c2 + b3 * c2 - a2 * c3 - 2 * a * b * c3 + b2 * c3
							- a * c4 - b * c4 - c5);
		case 2960:
			return a
					* (a6 + 2 * a5 * b + a4 * b2 - a2 * b4 - 2 * a * b5 - b6
							+ 2 * a5 * c + 5 * a4 * b * c + a3 * b2 * c - 3
							* a2 * b3 * c - 3 * a * b4 * c - 2 * b5 * c + a4
							* c2 + a3 * b * c2 - 2 * a2 * b2 * c2 + a * b3 * c2
							+ b4 * c2 - 3 * a2 * b * c3 + a * b2 * c3 + 4 * b3
							* c3 - a2 * c4 - 3 * a * b * c4 + b2 * c4 - 2 * a
							* c5 - 2 * b * c5 - c6);
		case 2961:
			return a
					* (a7 - a6 * b - a5 * b2 + a4 * b3 - a3 * b4 + a2 * b5 + a
							* b6 - b7 - a6 * c + 2 * a5 * b * c + a4 * b2 * c
							- 4 * a3 * b3 * c + 5 * a2 * b4 * c - 6 * a * b5
							* c + 3 * b6 * c - a5 * c2 + a4 * b * c2 + 6 * a3
							* b2 * c2 - 6 * a2 * b3 * c2 + 3 * a * b4 * c2 - 3
							* b5 * c2 + a4 * c3 - 4 * a3 * b * c3 - 6 * a2 * b2
							* c3 + 4 * a * b3 * c3 + b4 * c3 - a3 * c4 + 5 * a2
							* b * c4 + 3 * a * b2 * c4 + b3 * c4 + a2 * c5 - 6
							* a * b * c5 - 3 * b2 * c5 + a * c6 + 3 * b * c6 - c7);
		case 2962:
			return b * c * (a4 - a2 * b2 + b4 - 2 * a2 * c2 - 2 * b2 * c2 + c4)
					* (a4 - 2 * a2 * b2 + b4 - a2 * c2 - 2 * b2 * c2 + c4);
		case 2963:
			return (a4 - a2 * b2 + b4 - 2 * a2 * c2 - 2 * b2 * c2 + c4)
					* (a4 - 2 * a2 * b2 + b4 - a2 * c2 - 2 * b2 * c2 + c4);
		case 2964:
			return a3 * (a4 - 2 * a2 * b2 + b4 - 2 * a2 * c2 - b2 * c2 + c4);
		case 2965:
			return a4 * (a4 - 2 * a2 * b2 + b4 - 2 * a2 * c2 - b2 * c2 + c4);
		case 2966:
			return (a - b) * (a + b) * (-a + c) * (a + c)
					* (a4 + b4 - a2 * c2 - b2 * c2)
					* (a4 - a2 * b2 - b2 * c2 + c4);
		case 2967:
			return a2 * (-V) * (-U) * p(a2 * b2 - b4 + a2 * c2 - c4, 2);
		case 2968:
			return p(b - c, 2) * p(-a + b + c, 2) * (-T);
		case 2969:
			return p(b - c, 2) * (-V) * (-U);
		case 2970:
			return b2 * p(b - c, 2) * c2 * p(b + c, 2) * (-V) * (-U);
		case 2971:
			return a2 * p(b - c, 2) * p(b + c, 2) * U * V;
		case 2972:
			return a2 * p(b - c, 2) * p(b + c, 2) * p(a2 - b2 - c2, 3);
		case 2973:
			return b2 * p(b - c, 2) * c2 * (-V) * (-U);
		case 2974:
			return b2 * c2 * T
					* p(2 * a4 - a2 * b2 + b4 - a2 * c2 - 2 * b2 * c2 + c4, 2);
		case 2975:
			return a * (a3 - a * b2 + a * b * c - b2 * c - a * c2 - b * c2);
		case 2976:
			return (3 * a - b - c) * (b - c)
					* (2 * a2 - a * b + b2 - a * c - 2 * b * c + c2);
		case 2977:
			return (b - c)
					* (2 * a3 + a2 * b - 2 * a * b2 + b3 + a2 * c - 4 * a * b
							* c + b2 * c - 2 * a * c2 + b * c2 + c3);
		case 2978:
			return a2 * (b - c)
					* (a * b2 + a * c2 + a * b * c + b2 * c + b * c2);
		case 2979:
			return a2 * (a2 * b2 - b4 + a2 * c2 - b2 * c2 - c4);
		case 2980:
			return (-a4 - a2 * b2 - b4 + a2 * c2 + b2 * c2)
					* (-a4 + a2 * b2 - a2 * c2 + b2 * c2 - c4);
		case 2981:
			return a2
					* (2 * a4 - a2 * b2 - b4 - 4 * a2 * c2 - b2 * c2 + 2 * c4 - u(3)
							* b2 * S)
					* (2 * a4 - 4 * a2 * b2 + 2 * b4 - a2 * c2 - b2 * c2 - c4 - u(3)
							* c2 * S)
					/ (4 * a4 - 5 * a2 * b2 + b4 - 5 * a2 * c2 - 2 * b2 * c2
							+ c4 - u(3) * b2 * S - u(3) * c2 * S);
		case 2982:
			return a
					* (a + b - c)
					* (a - b + c)
					* (-a3 + a2 * b + a * b2 - b3 + 2 * a * b * c + a * c2 + b
							* c2)
					* (-a3 + a * b2 + a2 * c + 2 * a * b * c + b2 * c + a * c2 - c3);
		case 2983:
			return a2 * (a3 + a * b2 + 2 * b3 - a2 * c + b2 * c - a * c2 + c3)
					* (a3 - a2 * b - a * b2 + b3 + a * c2 + b * c2 + 2 * c3);
		case 2984:
			return a2
					* (a4 - 2 * a2 * b2 + b4 - a2 * c2 - b2 * c2)
					* (a4 - a2 * b2 - 2 * a2 * c2 - b2 * c2 + c4)
					* (-a6 + 2 * a4 * b2 - 3 * a2 * b4 + 2 * b6 + a4 * c2 - 4
							* a2 * b2 * c2 - 3 * b4 * c2 + a2 * c4 + 2 * b2
							* c4 - c6)
					* (-a6 + a4 * b2 + a2 * b4 - b6 + 2 * a4 * c2 - 4 * a2 * b2
							* c2 + 2 * b4 * c2 - 3 * a2 * c4 - 3 * b2 * c4 + 2 * c6);
		case 2985:
			return (a3 - a2 * b - a * b2 + b3 + a2 * c + b2 * c)
					* (a3 + a2 * b - a2 * c - a * c2 + b * c2 + c3);
		case 2986:
			return (a6 - a4 * b2 - a2 * b4 + b6 - 2 * a4 * c2 + 2 * a2 * b2
					* c2 - 2 * b4 * c2 + a2 * c4 + b2 * c4)
					* (a6 - 2 * a4 * b2 + a2 * b4 - a4 * c2 + 2 * a2 * b2 * c2
							+ b4 * c2 - a2 * c4 - 2 * b2 * c4 + c6);
		case 2987:
			return a2 * (a4 - a2 * b2 + 2 * b4 - 2 * a2 * c2 - b2 * c2 + c4)
					* (a4 - 2 * a2 * b2 + b4 - a2 * c2 - b2 * c2 + 2 * c4);
		case 2988:
			return (a6 - a5 * b - a4 * b2 + 2 * a3 * b3 - a2 * b4 - a * b5 + b6
					+ a4 * b * c - a3 * b2 * c - a2 * b3 * c + a * b4 * c - 2
					* a4 * c2 + a3 * b * c2 + 2 * a2 * b2 * c2 + a * b3 * c2
					- 2 * b4 * c2 - a2 * b * c3 - a * b2 * c3 + a2 * c4 + b2
					* c4)
					* (a6 - 2 * a4 * b2 + a2 * b4 - a5 * c + a4 * b * c + a3
							* b2 * c - a2 * b3 * c - a4 * c2 - a3 * b * c2 + 2
							* a2 * b2 * c2 - a * b3 * c2 + b4 * c2 + 2 * a3
							* c3 - a2 * b * c3 + a * b2 * c3 - a2 * c4 + a * b
							* c4 - 2 * b2 * c4 - a * c5 + c6);
		case 2989:
			return (a5 - a3 * b2 - a2 * b3 + b5 - a4 * c + 2 * a2 * b2 * c - b4
					* c - a3 * c2 - b3 * c2 + a2 * c3 + b2 * c3)
					* (a5 - a4 * b - a3 * b2 + a2 * b3 - a3 * c2 + 2 * a2 * b
							* c2 + b3 * c2 - a2 * c3 - b2 * c3 - b * c4 + c5);
		case 2990:
			return a
					* (a4 - 2 * a2 * b2 + b4 - a3 * c + a2 * b * c + a * b2 * c
							- b3 * c - a2 * c2 - b2 * c2 + a * c3 + b * c3)
					* (a4 - a3 * b - a2 * b2 + a * b3 + a2 * b * c + b3 * c - 2
							* a2 * c2 + a * b * c2 - b2 * c2 - b * c3 + c4);
		case 2991:
			return a
					* (a3 - a2 * b - a * b2 + b3 - 2 * a * b * c + a * c2 + b
							* c2)
					* (a3 + a * b2 - a2 * c - 2 * a * b * c + b2 * c - a * c2 + c3);
		case 2992:
			return 1 / (2 * a10 - 4 * a8 * b2 + 2 * a6 * b4 - 2 * a4 * b6 + 4
					* a2 * b8 - 2 * b10 - 4 * a8 * c2 + 9 * a6 * b2 * c2 - 6
					* a4 * b4 * c2 - 5 * a2 * b6 * c2 + 6 * b8 * c2 + 2 * a6
					* c4 - 6 * a4 * b2 * c4 + 2 * a2 * b4 * c4 - 4 * b6 * c4
					- 2 * a4 * c6 - 5 * a2 * b2 * c6 - 4 * b4 * c6 + 4 * a2
					* c8 + 6 * b2 * c8 - 2 * c10 - u(3) * a4 * b2 * c2 * S
					- u(3) * a2 * b4 * c2 * S + 2 * u(3) * b6 * c2 * S - u(3)
					* a2 * b2 * c4 * S - 4 * u(3) * b4 * c4 * S + 2 * u(3) * b2
					* c6 * S);
		case 2993:
			return 1 / (2 * a10 - 4 * a8 * b2 + 2 * a6 * b4 - 2 * a4 * b6 + 4
					* a2 * b8 - 2 * b10 - 4 * a8 * c2 + 9 * a6 * b2 * c2 - 6
					* a4 * b4 * c2 - 5 * a2 * b6 * c2 + 6 * b8 * c2 + 2 * a6
					* c4 - 6 * a4 * b2 * c4 + 2 * a2 * b4 * c4 - 4 * b6 * c4
					- 2 * a4 * c6 - 5 * a2 * b2 * c6 - 4 * b4 * c6 + 4 * a2
					* c8 + 6 * b2 * c8 - 2 * c10 + u(3) * a4 * b2 * c2 * S
					+ u(3) * a2 * b4 * c2 * S - 2 * u(3) * b6 * c2 * S + u(3)
					* a2 * b2 * c4 * S + 4 * u(3) * b4 * c4 * S - 2 * u(3) * b2
					* c6 * S);
		case 2994:
			return (-a3 - a2 * b + a * b2 + b3 + a2 * c + b2 * c + a * c2 - b
					* c2 - c3)
					* (-a3 + a2 * b + a * b2 - b3 - a2 * c - b2 * c + a * c2
							+ b * c2 + c3);
		case 2995:
			return b * c * (-a3 - b3 - a * b * c + a * c2 + b * c2)
					* (-a3 + a * b2 - a * b * c + b2 * c - c3);
		case 2996:
			return (-a2 + 3 * b2 - c2) * (-a2 - b2 + 3 * c2);
		case 2997:
			return b * c * (-a3 - b3 + a * b * c + a * c2 + b * c2)
					* (-a3 + a * b2 + a * b * c + b2 * c - c3);
		case 2998:
			return (a2 * b2 - a2 * c2 + b2 * c2)
					* (-(a2 * b2) + a2 * c2 + b2 * c2);
		case 2999:
			return a * (a2 + 2 * a * b + b2 + 2 * a * c - 2 * b * c + c2);
		default:
			return Double.NaN;
		}
	}

	private double weight3000plus(int k, double a, double b, double c) {

		switch (k) {
		case 3000:
			return a
					* (a3 * b - 2 * a2 * b2 + a * b3 + a3 * c + 2 * a2 * b * c
							- a * b2 * c - 2 * b3 * c - 2 * a2 * c2 - a * b
							* c2 + 4 * b2 * c2 + a * c3 - 2 * b * c3);
		case 3001:
			return a2 * (a2 * b4 - b6 + a2 * c4 - c6);
		case 3002:
			return a2
					* (a3 * b2 - a2 * b3 - a * b4 + b5 + a2 * b2 * c - b4 * c
							+ a3 * c2 + a2 * b * c2 - a2 * c3 - a * c4 - b * c4 + c5);
		case 3003:
			return a2
					* (a4 * b2 - 2 * a2 * b4 + b6 + a4 * c2 + 2 * a2 * b2 * c2
							- b4 * c2 - 2 * a2 * c4 - b2 * c4 + c6);
		case 3004:
			return (b - c) * (a * b + b2 + a * c + c2);
		case 3005:
			return a2 * (b2 - c2) * R;
		case 3006:
			return a * b2 - b3 + a * c2 - c3;
		case 3007:
			return a3 * b2 + a2 * b3 - a * b4 - b5 - 2 * a2 * b2 * c + 2 * b4
					* c + a3 * c2 - 2 * a2 * b * c2 + 2 * a * b2 * c2 - b3 * c2
					+ a2 * c3 - b2 * c3 - a * c4 + 2 * b * c4 - c5;
		case 3008:
			return 2 * a2 - a * b + b2 - a * c - 2 * b * c + c2;
		case 3009:
			return a2 * (a * b2 - b2 * c + a * c2 - b * c2);
		case 3010:
			return a2
					* (a3 * b2 - 2 * a2 * b3 + a * b4 + a2 * b2 * c - b4 * c
							+ a3 * c2 + a2 * b * c2 - 2 * a * b2 * c2 + b3 * c2
							- 2 * a2 * c3 + b2 * c3 + a * c4 - b * c4);
		case 3011:
			return 2 * a3 - a2 * b + b3 - a2 * c - b2 * c - b * c2 + c3;
		case 3012:
			return 2 * a5 - a4 * b - 4 * a2 * b3 + 2 * a * b4 + b5 - a4 * c + 4
					* a2 * b2 * c - 3 * b4 * c + 4 * a2 * b * c2 - 4 * a * b2
					* c2 + 2 * b3 * c2 - 4 * a2 * c3 + 2 * b2 * c3 + 2 * a * c4
					- 3 * b * c4 + c5;
		case 3013:
			return a
					* (b + c)
					* (a6 - 2 * a4 * b2 + a2 * b4 + 2 * a4 * b * c - a2 * b3
							* c - b5 * c - 2 * a4 * c2 + a2 * b2 * c2 - a2 * b
							* c3 + 2 * b3 * c3 + a2 * c4 - b * c5);
		case 3014:
			return a6 * b2 - b8 + a6 * c2 - 4 * a4 * b2 * c2 + a2 * b4 * c2 + 2
					* b6 * c2 + a2 * b2 * c4 - 2 * b4 * c4 + 2 * b2 * c6 - c8;
		case 3015:
			return 2 * a7 - 2 * a5 * b2 - 2 * a4 * b3 + a3 * b4 + a2 * b5 - a
					* b6 + b7 + 2 * a4 * b2 * c - a2 * b4 * c - b6 * c - 2 * a5
					* c2 + 2 * a4 * b * c2 + a * b4 * c2 - 3 * b5 * c2 - 2 * a4
					* c3 + 3 * b4 * c3 + a3 * c4 - a2 * b * c4 + a * b2 * c4
					+ 3 * b3 * c4 + a2 * c5 - 3 * b2 * c5 - a * c6 - b * c6
					+ c7;
		case 3016:
			return a2
					* (a6 * b2 - 2 * a4 * b4 + a2 * b6 + a6 * c2 - 2 * b6 * c2
							- 2 * a4 * c4 + 4 * b4 * c4 + a2 * c6 - 2 * b2 * c6);
		case 3017:
			return a4 + 3 * a3 * b + a2 * b2 + b4 + 3 * a3 * c + 3 * a2 * b * c
					+ a2 * c2 - 2 * b2 * c2 + c4;
		case 3018:
			return 2 * a8 - 2 * a6 * b2 - a4 * b4 + b8 - 2 * a6 * c2 + 4 * a4
					* b2 * c2 - 4 * b6 * c2 - a4 * c4 + 6 * b4 * c4 - 4 * b2
					* c6 + c8;
		case 3019:
			return 3 * a6 - a5 * b - 4 * a4 * b2 - a3 * b3 + 2 * a2 * b4 + 2
					* a * b5 - b6 - a5 * c - a4 * b * c - a3 * b2 * c - a2 * b3
					* c + 2 * a * b4 * c + 2 * b5 * c - 4 * a4 * c2 - a3 * b
					* c2 - 2 * a2 * b2 * c2 - 4 * a * b3 * c2 + b4 * c2 - a3
					* c3 - a2 * b * c3 - 4 * a * b2 * c3 - 4 * b3 * c3 + 2 * a2
					* c4 + 2 * a * b * c4 + b2 * c4 + 2 * a * c5 + 2 * b * c5
					- c6;
		case 3020:
			return a * p(b - c, 2) * (a + b - c) * (a - b + c);
		case 3021:
			return (a - b - c)
					* p(2 * a2 - a * b + b2 - a * c - 2 * b * c + c2, 2);
		case 3022:
			return a2 * p(a - b - c, 3) * p(b - c, 2);
		case 3023:
			return (a - b - c) * p(b - c, 2) * p(a2 + b * c, 2);
		case 3024:
			return a2 * (a - b - c) * p(b - c, 2) * p(a2 - b2 - b * c - c2, 2);
		case 3025:
			return a2 * (a - b - c) * p(b - c, 2) * p(a2 - b2 + b * c - c2, 2);
		case 3026:
			return (a - b - c) * p(b - c, 2)
					* p(a2 + a * b + a * c + 2 * b * c, 2);
		case 3027:
			return (a + b - c) * (a - b + c) * p(b + c, 2) * p(a2 - b * c, 2);
		case 3028:
			return a2 * (a + b - c) * (a - b + c) * p(b + c, 2)
					* p(a2 - b2 + b * c - c2, 2);
		case 3029:
			return p(b + c, 2) * (a3 - a * b2 - b3 + a2 * c)
					* (a3 + a2 * b - a * c2 - c3);
		case 3030:
			return a2 * (a * b + b2 + a * c - 2 * b * c - c2)
					* (a * b - b2 + a * c - 2 * b * c + c2);
		case 3031:
			return a2 * p(b + c, 2) * (a3 + b3 + a2 * c - b2 * c - a * c2 - c3)
					* (a3 + a2 * b - a * b2 - b3 - b * c2 + c3);
		case 3032:
			return a * (a2 * b - b3 + a2 * c - a * b * c - b2 * c + a * c2)
					* (a2 * b + a * b2 + a2 * c - a * b * c - b * c2 - c3);
		case 3033:
			return a2 * (a2 * b + b3 + a2 * c - b2 * c - b * c2 - c3)
					* (a2 * b - b3 + a2 * c - b2 * c - b * c2 + c3);
		case 3034:
			return a * (a2 * b + b3 + a2 * c - a * b * c - b2 * c - a * c2)
					* (a2 * b - a * b2 + a2 * c - a * b * c - b * c2 + c3);
		case 3035:
			return 2 * a3 - 2 * a2 * b - a * b2 + b3 - 2 * a2 * c + 4 * a * b
					* c - b2 * c - a * c2 - b * c2 + c3;
		case 3036:
			return (a - b - c)
					* (2 * a3 - 2 * a2 * b - a * b2 + 3 * b3 - 2 * a2 * c + 4
							* a * b * c - 3 * b2 * c - a * c2 - 3 * b * c2 + 3 * c3);
		case 3037:
			return a
					* (a - b - c)
					* (a4 * b4 + a3 * b5 - a3 * b4 * c - 2 * a2 * b4 * c2 - 2
							* a * b5 * c2 + 2 * a * b4 * c3 + a4 * c4 - a3 * b
							* c4 - 2 * a2 * b2 * c4 + 2 * a * b3 * c4 + 2 * b4
							* c4 + a3 * c5 - 2 * a * b2 * c5);
		case 3038:
			return a
					* (a - b - c)
					* (a2 * b2 + a * b3 - 3 * a * b2 * c - 2 * b3 * c + a2 * c2
							- 3 * a * b * c2 + 6 * b2 * c2 + a * c3 - 2 * b
							* c3);
		case 3039:
			return (a - b - c)
					* (2 * a3 - 2 * a2 * b + 3 * a * b2 - b3 - 2 * a2 * c - 4
							* a * b * c + b2 * c + 3 * a * c2 + b * c2 - c3);
		case 3040:
			return a
					* (a - b - c)
					* (a6 * b2 + a5 * b3 - 2 * a4 * b4 - 2 * a3 * b5 + a2 * b6
							+ a * b7 - 3 * a5 * b2 * c + 2 * a4 * b3 * c + 6
							* a3 * b4 * c - 4 * a2 * b5 * c - 3 * a * b6 * c
							+ 2 * b7 * c + a6 * c2 - 3 * a5 * b * c2 + 2 * a4
							* b2 * c2 - 4 * a3 * b3 * c2 - a2 * b4 * c2 + 7 * a
							* b5 * c2 - 2 * b6 * c2 + a5 * c3 + 2 * a4 * b * c3
							- 4 * a3 * b2 * c3 + 8 * a2 * b3 * c3 - 5 * a * b4
							* c3 - 2 * b5 * c3 - 2 * a4 * c4 + 6 * a3 * b * c4
							- a2 * b2 * c4 - 5 * a * b3 * c4 + 4 * b4 * c4 - 2
							* a3 * c5 - 4 * a2 * b * c5 + 7 * a * b2 * c5 - 2
							* b3 * c5 + a2 * c6 - 3 * a * b * c6 - 2 * b2 * c6
							+ a * c7 + 2 * b * c7);
		case 3041:
			return a
					* (a - b - c)
					* (a4 * b2 - a3 * b3 - a2 * b4 + a * b5 - a3 * b2 * c - a
							* b4 * c + 2 * b5 * c + a4 * c2 - a3 * b * c2 + 4
							* a2 * b2 * c2 - 2 * b4 * c2 - a3 * c3 - a2 * c4
							- a * b * c4 - 2 * b2 * c4 + a * c5 + 2 * b * c5);
		case 3042:
			return a
					* (a6 * b2 - a5 * b3 - 2 * a4 * b4 + 2 * a3 * b5 + a2 * b6
							- a * b7 - a5 * b2 * c + 2 * a4 * b3 * c + 2 * a3
							* b4 * c - 4 * a2 * b5 * c - a * b6 * c + 2 * b7
							* c + a6 * c2 - a5 * b * c2 + 2 * a4 * b2 * c2 - 4
							* a3 * b3 * c2 - a2 * b4 * c2 + 5 * a * b5 * c2 - 2
							* b6 * c2 - a5 * c3 + 2 * a4 * b * c3 - 4 * a3 * b2
							* c3 + 8 * a2 * b3 * c3 - 3 * a * b4 * c3 - 2 * b5
							* c3 - 2 * a4 * c4 + 2 * a3 * b * c4 - a2 * b2 * c4
							- 3 * a * b3 * c4 + 4 * b4 * c4 + 2 * a3 * c5 - 4
							* a2 * b * c5 + 5 * a * b2 * c5 - 2 * b3 * c5 + a2
							* c6 - a * b * c6 - 2 * b2 * c6 - a * c7 + 2 * b
							* c7);
		case 3043:
			return a4 * U * p(a2 - b2 - b * c - c2, 2)
					* p(a2 - b2 + b * c - c2, 2) * V;
		case 3044:
			return a2 * (a4 - a2 * b2 + b4 - a2 * c2)
					* (a4 - a2 * b2 - a2 * c2 + c4);
		case 3045:
			return a3 * (a3 - a2 * b - a * b2 + b3 + a * b * c - a * c2)
					* (a3 - a * b2 - a2 * c + a * b * c - a * c2 + c3);
		case 3046:
			return a4 * (a3 - a2 * b - a * b2 + b3 + b * c2 - c3)
					* (a3 - b3 - a2 * c + b2 * c - a * c2 + c3);
		case 3047:
			return a4 * (a4 - 2 * a2 * b2 + b4 + b2 * c2 - c4)
					* (a4 - b4 - 2 * a2 * c2 + b2 * c2 + c4);
		case 3048:
			return a4 * (a4 - 4 * a2 * b2 + b4 + 3 * b2 * c2 - c4)
					* (a4 - b4 - 4 * a2 * c2 + 3 * b2 * c2 + c4);
		case 3049:
			return a4 * (b2 - c2) * T;
		case 3050:
			return a2 * (b2 - c2) * (a4 - a2 * b2 - a2 * c2 - b2 * c2);
		case 3051:
			return a4 * R;
		case 3052:
			return a2 * (3 * a - b - c);
		case 3053:
			return a2 * (3 * a2 - b2 - c2);
		default:
			return Double.NaN;
		}
	}
}
