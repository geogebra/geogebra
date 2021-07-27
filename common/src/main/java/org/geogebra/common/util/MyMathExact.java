package org.geogebra.common.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.math3.exception.MathRuntimeException;
import org.apache.commons.math3.linear.AnyMatrix;

/*
 * utilities for "exact" arithmetics
 * of arbitrary precision
 * 
 */
public class MyMathExact {

	/**
	 * BigDecimal wrapper with support for fixed scale
	 */
	public static class MyDecimal {

		private final int fixedScale;
		private static final int roundingMode = BigDecimal.ROUND_HALF_EVEN;

		private final BigDecimal impl;

		/**
		 * @param significance
		 *            significance
		 */
		public MyDecimal(int significance) {
			impl = BigDecimal.ZERO.setScale(significance);
			fixedScale = significance;
		}

		/**
		 * @param significance
		 *            significance
		 * @param val
		 *            value
		 */
		public MyDecimal(int significance, double val) {
			// super(val);
			// super.setScale(significance, roundingMode);
			impl = (new BigDecimal(val)).setScale(significance, roundingMode);
			fixedScale = significance;
		}

		/**
		 * @param md
		 *            decimal
		 */
		public MyDecimal(MyDecimal md) {
			impl = new BigDecimal(md.unscaledValue(), md.scale());
			fixedScale = md.scale();
		}

		@Override
		public String toString() {
			return impl.toString();
		}

		private int scale() {
			return impl.scale();
		}

		private BigInteger unscaledValue() {
			return impl.unscaledValue();
		}

		/**
		 * @param bd
		 *            decimal
		 */
		public MyDecimal(BigDecimal bd) {
			impl = new BigDecimal(bd.unscaledValue(), bd.scale());
			fixedScale = bd.scale();
		}

		/**
		 * @param significance
		 *            significance
		 * @param bd
		 *            value
		 */
		public MyDecimal(int significance, BigDecimal bd) {
			impl = new BigDecimal(bd.unscaledValue(), bd.scale())
					.setScale(significance, roundingMode);
			fixedScale = significance;
		}

		public int getScale() {
			return fixedScale;
		}

		public MyDecimal copy() {
			return new MyDecimal(this.getScale(), impl);
		}

		public MyDecimal negate() {
			return new MyDecimal(this.getScale(), impl.negate());
		}

		public MyDecimal add(MyDecimal md) {
			return new MyDecimal(this.getScale(), impl.add(md.getImpl()));
		}

		public BigDecimal getImpl() {
			return impl;
		}

		/**
		 * @param md
		 *            factor
		 * @return this * md
		 */
		public MyDecimal multiply(MyDecimal md) {
			return new MyDecimal(this.getScale(), impl.multiply(md.getImpl()));
		}

		/**
		 * @param md
		 *            subtrahend
		 * @return this - md
		 */
		public MyDecimal subtract(MyDecimal md) {
			return new MyDecimal(this.getScale(), impl.subtract(md.getImpl()));
		}

		/**
		 * @param md
		 *            divisor
		 * @return this / md
		 */
		public MyDecimal divide(MyDecimal md) {
			return new MyDecimal(this.getScale(), impl.divide(md.getImpl(),
					this.getScale(), BigDecimal.ROUND_HALF_EVEN));
		}

		/**
		 * @return square root of this
		 */
		public MyDecimal sqrt() {

			if (impl.compareTo(BigDecimal.ZERO) == 0) {
				return new MyDecimal(BigDecimal.ZERO);
			}

			MyDecimal TWO = new MyDecimal(BigDecimal.ONE.add(BigDecimal.ONE));
			double lower_bound = Math.sqrt(impl.doubleValue());

			int thisScale = this.getScale();
			int thisScalePlusOne = thisScale + 1;

			MyDecimal ret = new MyDecimal(thisScalePlusOne, lower_bound);
			MyDecimal radicand = new MyDecimal(thisScalePlusOne, impl);

			int iterCount = 0;
			while (ret.multiply(ret).subtract(radicand).divide(radicand)
					.divide(new MyDecimal(thisScalePlusOne, lower_bound * 2))
					.abs().doubleValue() > Math.pow(10, -thisScale)
					&& iterCount < 5) {
				ret = ret.add(radicand.divide(ret)).divide(TWO);
				iterCount++;
			}

			return new MyDecimal(thisScale, ret.getImpl());
		}

		public BigDecimal abs() {
			return impl.abs();
		}

		public double doubleValue() {
			return impl.doubleValue();
		}

		public int intValue() {
			return impl.intValue();
		}

		public int signum() {
			return impl.signum();
		}
	}

	public static class MyDecimalMatrix implements AnyMatrix {

		private int fixedScale;
		private int rowD;
		private int colD;

		private MyDecimal[][] data;

		/**
		 * @param significance
		 *            significance
		 * @param rowD
		 *            number of rows
		 * @param colD
		 *            number of columns
		 */
		public MyDecimalMatrix(int significance, int rowD, int colD) {
			fixedScale = significance;
			this.rowD = rowD;
			this.colD = colD;
			data = new MyDecimal[rowD][colD];
		}

		@Override
		public boolean isSquare() {
			return rowD == colD;
		}

		@Override
		public int getRowDimension() {
			return rowD;
		}

		@Override
		public int getColumnDimension() {
			return colD;
		}

		public int getScale() {
			return this.fixedScale;
		}

		public MyDecimal getEntry(int i, int j) {
			return data[i][j].copy();
		}

		public void setEntry(int i, int j, MyDecimal md) {
			data[i][j] = new MyDecimal(fixedScale, md.getImpl());
		}

		/**
		 * @return copy of the matrix
		 */
		public MyDecimalMatrix copy() {

			MyDecimalMatrix mdm = new MyDecimalMatrix(fixedScale, rowD, colD);

			for (int i = 0; i < rowD; i++) {
				for (int j = 0; j < colD; j++) {
					mdm.setEntry(i, j, data[i][j]);
				}
			}

			return mdm;
		}

		/**
		 * @param j
		 *            column
		 * @return numbers in given column
		 */
		public MyDecimal[] getColumn(int j) {
			MyDecimal[] ret = new MyDecimal[this.getRowDimension()];
			for (int i = 0; i < this.getRowDimension(); i++) {
				ret[i] = this.getEntry(i, j);
			}
			return ret;
		}

		/**
		 * @param j
		 *            column
		 * @param column
		 *            numbers for given column
		 */
		public void setColumn(int j, MyDecimal[] column) {
			for (int i = 0; i < this.getRowDimension(); i++) {
				this.setEntry(i, j, column[i]);
			}
		}

		/**
		 * @param i
		 *            row
		 * @return numbers in given row
		 */
		public MyDecimal[] getRow(int i) {
			MyDecimal[] ret = new MyDecimal[this.getColumnDimension()];
			for (int j = 0; j < this.getColumnDimension(); j++) {
				ret[j] = this.getEntry(i, j);
			}
			return ret;
		}

		/**
		 * @param i
		 *            row
		 * @param row
		 *            numbers for given row
		 */
		public void setRow(int i, MyDecimal[] row) {
			for (int j = 0; j < this.getColumnDimension(); j++) {
				this.setEntry(i, j, row[j]);
			}
		}

		/**
		 * @param matrix
		 *            matrix
		 * @param m
		 *            number of rows
		 * @param n
		 *            number of columns
		 * @return frobenius norm
		 */
		public MyDecimal frobNormSq(MyDecimalMatrix matrix, int m, int n) {
			MyDecimal ret = new MyDecimal(BigDecimal.ZERO);

			if (m == 0 || n == 0) {
				return ret;
			}

			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					ret = ret.add(matrix.getEntry(i, j)
							.multiply(matrix.getEntry(i, j)));
				}
			}

			return ret;
		}

		/**
		 * @param m
		 *            matrix
		 * @return product of matrices
		 */
		public MyDecimalMatrix multiply(MyDecimalMatrix m) {

			if (this.getColumnDimension() != m.getRowDimension()) {
				throw MathRuntimeException.createIllegalArgumentException(
						"Cannot multiply " + getRowDimension() + " x "
								+ getColumnDimension() + " and "
								+ m.getRowDimension() + " x "
								+ m.getColumnDimension() + "matrices!");

			}

			MyDecimalMatrix ret = new MyDecimalMatrix(this.getScale(),
					this.getRowDimension(), m.getColumnDimension());
			for (int i = 0; i < this.getRowDimension(); i++) {
				for (int j = 0; j < m.getColumnDimension(); j++) {
					MyDecimal entry = new MyDecimal(this.fixedScale, 0);
					for (int k = 0; k < this.getColumnDimension(); k++) {
						entry = entry.add(
								this.getEntry(i, k).multiply(m.getEntry(k, j)));
					}
					ret.setEntry(i, j, entry);
				}
			}
			return ret;
		}
	}
}
