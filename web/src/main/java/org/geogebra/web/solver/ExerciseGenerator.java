package org.geogebra.web.solver;

import org.geogebra.common.util.DoubleUtil;

public class ExerciseGenerator {

	// static class
	private ExerciseGenerator() {
	}

	public static class Exercise {

		public final String equation;

		public final double[] solutions;

		/**
		 * @param equation equation to solve
		 * @param solutions all solutions
		 */
		public Exercise(String equation, double... solutions) {
			this.equation = equation;
			this.solutions = solutions;
		}
	}

	private static int randint(int a, int b) {
		return (int) Math.floor(Math.random() * (b - a + 1)) + a;
	}

	private static int randIntNZ(int a, int b) {
		int ret = 0;
		while (ret == 0) {
			ret = randint(a, b);
		}

		return ret;
	}

	/**
	 * @param level difficulty 1 to 4; other values = random difficulty
	 * @return random exercise with given difficulty
	 */
	public static Exercise getExercise(int level) {
		int type;
		double a, b, c, d, e;

		switch (level) {
		case 1:
			type = randint(0, 15);
			break;
		case 2:
			type = randint(10, 27);
			break;
		case 3:
			type = randint(20, 34);
			break;
		case 4:
			type = randint(25, 37);
			break;
		default:
			type = randint(0, 37);
		}

		switch (type) {

		// level 1 start
		default:
			a = randIntNZ(-9, 9);
			b = randint(-9, 9);
			return new Exercise(a + " x = " + b, b / a);

		case 1:
			a = randIntNZ(2, 9);
			b = randint(-9, 9);
			return new Exercise("x/" + a + " = " + b, a * b);

		case 2:
			a = randint(0, 9);
			return new Exercise("x^2 = " + (a * a), a, -a);

		case 3:
			a = randint(1, 9);
			b = randint(-9, 9);
			return new Exercise("x + " + a + " = " + b, b - a);

		case 4:
			a = randint(1, 9);
			b = randint(-9, 9);
			return new Exercise("x - " + a + " = " + b, b + a);

		case 5:
			a = randint(-9, 9);
			b = randint(-9, 9);
			return new Exercise(a + " - x = " + b, a - b);

		case 6:
			a = randint(0, 9);
			return new Exercise("sqrt(x) = " + a, a * a);

		case 7:
			a = randint(2, 9);
			return new Exercise("1/x = " + a, 1 / a);

		case 8:
			a = randint(2, 5);
			b = randint(1, 9);
			c = randint(2, 9);
			return new Exercise(a + "(x + " + b + ") = " + (a * c), c - b);

		case 9:
			a = randint(1, 9);
			b = randint(1, 9);
			return new Exercise("(x - " + a + ")(x - " + b + ") = 0", a, b);

		case 10:
			a = randint(1, 9);
			b = randint(1, 9);
			return new Exercise("(x - " + a + ")(x + " + b + ") = 0", a, -b);

		case 11:
			a = randint(1, 9);
			b = randint(1, 9);
			return new Exercise("(x + " + a + ")(x + " + b + ") = 0", -a, -b);

		case 12:
			a = randIntNZ(-9, 9);
			b = randint(1, 9);
			c = randIntNZ(-9, 9);
			return new Exercise(a + " x + " + b + " = " + c, (c - b) / a);

		case 13:
			a = randIntNZ(-9, 9);
			b = randint(1, 9);
			c = randIntNZ(-9, 9);
			return new Exercise(a + " x - " + b + " = " + c, (c + b) / a);

		case 14:
			a = randIntNZ(-9, 9);
			b = randint(1, 9);
			c = randIntNZ(-9, 9);
			d = randint(1, 8);
			if (a == d) {
				d++;
			}
			return new Exercise(a + " x + " + b + " = " + c + " + " + d + " x",
					(c - b) / (a - d));

		case 15:
			a = randIntNZ(-9, 9);
			b = randint(1, 9);
			c = randIntNZ(-9, 9);
			d = randint(1, 8);
			if (a == -d) {
				d++;
			}
			return new Exercise(a + " x + " + b + " = " + c + " - " + d + " x",
					(c - b) / (a + d));

		// level 2 start
		case 16:
			a = randint(2, 9);
			b = randint(1, 9);
			c = randint(-9, 9);
			if (a == b) {
				a++;
			}
			return new Exercise(a + "(x + " + b + ") = " + c, c / a - b);

		case 17:
			a = randIntNZ(-9, 9);
			b = randIntNZ(1, 9);
			c = randIntNZ(-9, 9);
			if (c == b) {
				b++;
			}
			return new Exercise(a + "/(x + " + b + ") = " + c, a / c - b);

		case 18:
			a = randIntNZ(-9, 9);
			b = randIntNZ(-9, 9);
			c = randint(1, 9);
			return new Exercise(a + "/x = " + b + "/" + c, a * c / b);

		case 19:
			a = randIntNZ(2, 9);
			b = randIntNZ(2, 9);
			if (b == -a) {
				b++;
			}
			c = randint(-9, 9);
			return new Exercise("x/" + a + " + x/" + b + " = " + c, a * b * c / (a + b));

		case 20:
			a = randIntNZ(2, 9);
			b = randIntNZ(2, 9);
			if (b == a) {
				b++;
			}
			c = randint(-9, 9);
			return new Exercise("x/" + a + " - x/" + b + " = " + c, a * b * c / (b - a));

		case 21:
			a = randIntNZ(1, 9);
			b = randIntNZ(1, 9);
			c = randIntNZ(-9, 9);
			return new Exercise(a + "/x - " + b + " = " + c, b + a / c);

		case 22:
			a = randIntNZ(1, 9);
			b = randIntNZ(1, 9);
			c = randint(1, 9);
			if (c == a) {
				c++;
			}
			return new Exercise("(" + a + " x)/(x + " + b + ") = " + c, b * c / (a - c));

		case 23:
			a = randIntNZ(1, 9);
			b = randIntNZ(1, 9);
			c = randIntNZ(-9, 9);
			return new Exercise("" + a + "/(" + b + " - x) = " + c, b - a / c);

		case 24:
			a = randIntNZ(1, 9);
			return new Exercise(a + "/x = x/" + a, a, -a);

		case 25:
			a = randint(2, 99);
			return new Exercise("x^2 = " + a, Math.sqrt(a), -Math.sqrt(a));

		case 26:
			a = randint(1, 9);
			b = randint(1, 9);
			return new Exercise("(x + " + a + ")^2 = " + (b * b), b - a, -b - a);

		case 27:
			a = randint(1, 9);
			b = randint(1, 9);
			return new Exercise("(x - " + a + ")^2 = " + (b * b), b + a, -b + a);

		// level 3 start
		case 28:
			a = randint(1, 9);
			b = randint(2, 99);
			return new Exercise("(x + " + a + ")^2 = " + b,
					Math.sqrt(b) - a, -Math.sqrt(b) - a);

		case 29:
			a = randint(1, 9);
			b = randint(2, 9);
			c = randIntNZ(-9, 9);
			d = randint(2, 8);
			if (b == -d) {
				d++;
			}
			e = randint(-9, 9);
			return new Exercise("(x + " + a + ")/" + b + " - (" + c + " - x)/" + d + " = " + e,
					(-a * d + b * c + b * d * e) / (b + d));

		case 30: // (ax+c)(bx+d)=0 multiplied out
			a = randint(1, 1);
			b = randint(1, 2);
			c = randint(1, 9);
			d = randint(1, 9);
			String coeff = a * b + "";
			if ("1".equals(coeff)) {
				coeff = "";
			}
			return new Exercise(coeff + "x^2 + " + (b * c + a * d) + " x + " + (c * d) + " = 0",
					-c / a, -d / b);

		case 31:
			a = randint(1, 9);
			b = randint(1, 9);
			c = randint(1, 9);
			return new Exercise("(x + " + a + ")(x + " + b + ") = " + ((c + a) * (c + b)),
					-a - b - c, c);

		case 32:
			a = randint(2, 9);
			b = randint(1, 9);
			c = randint(2, 9);
			d = randint(1, 9);
			// ie if b/a!=d/c
			if (!DoubleUtil.isEqual(c * b, a * d)) {
				// 2 answers
				return new Exercise("(" + a + " x + " + b + ")(" + c + " x + " + d + ") = 0",
						-b / a, -d / c);
			}
			return new Exercise("(" + a + " x + " + b + ")(" + c + " x + " + d + ") = 0",
					-b / a);

		case 33:
			a = randint(2, 9);
			b = randint(1, 9);
			c = randint(2, 9);
			d = randint(1, 9);
			return new Exercise("(" + a + " x - " + b + ")(" + c + " x + " + d + ") = 0",
					b / a, -d / c);

		case 34:
			a = randint(2, 9);
			b = randint(1, 9);
			c = randint(2, 9);
			d = randint(1, 9);
			// ie if b/a!=d/c
			if (!DoubleUtil.isEqual(c * b, a * d)) {
				// 2 answers
				return new Exercise("(" + a + " x - " + b + ")(" + c + " x - " + d + ") = 0",
						b / a, d / c);
			}
			return new Exercise("(" + a + " x - " + b + ")(" + c + " x - " + d + ") = 0",
					b / a);

		// level 4 start
		case 35:
			double disc;
			do {
				a = randint(1, 9);
				b = randint(1, 9);
				c = randint(1, 9);
				d = randint(1, 8);

				if (b == d) {
					d++;
				}

				e = randIntNZ(-3, 3);
				disc = Math.pow(b * e + d * e - a - c, 2) - 4 * e * (e * b * d - a * d - c * b);
			} while (disc < 0 || !DoubleUtil.isInteger(Math.sqrt(disc)));

			return new Exercise(
					a + "/(x + " + b + ") + " + c + "/(x + " + d + ") = " + e,
					(a - b * e + c - d * e + Math.sqrt(disc)) / (2 * e),
					(a - b * e + c - d * e - Math.sqrt(disc)) / (2 * e));

		case 36:
			a = randint(1, 9);
			b = randint(1, 9);
			c = randint(1, 9);
			d = randint(1, 9);

			if (DoubleUtil.isEqual(a + b, c + d)) {
				a++;
			}

			return new Exercise("(x - " + a + ")(x - " + b + ") = (x + " + c + ")(x - " + d + ")",
					(a * b - c * d) / (a + b - c - d));

		case 37:
			a = randint(1, 9);
			b = randint(2, 9);
			c = randint(1, 9);

			return new Exercise("(x + " + a + ")^2 = (" + b + " x + " + c + ")^2",
					(a - c) / (b - 1), (-a - c) / (b + 1));
		}
	}
}
