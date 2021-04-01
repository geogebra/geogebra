package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.statistics.CmdANOVA;
import org.geogebra.common.kernel.statistics.CmdBernoulli;
import org.geogebra.common.kernel.statistics.CmdBinomialDist;
import org.geogebra.common.kernel.statistics.CmdBoxPlot;
import org.geogebra.common.kernel.statistics.CmdCauchy;
import org.geogebra.common.kernel.statistics.CmdCell;
import org.geogebra.common.kernel.statistics.CmdCellRange;
import org.geogebra.common.kernel.statistics.CmdChiSquared;
import org.geogebra.common.kernel.statistics.CmdChiSquaredTest;
import org.geogebra.common.kernel.statistics.CmdClasses;
import org.geogebra.common.kernel.statistics.CmdColumn;
import org.geogebra.common.kernel.statistics.CmdColumnName;
import org.geogebra.common.kernel.statistics.CmdContingencyTable;
import org.geogebra.common.kernel.statistics.CmdCovariance;
import org.geogebra.common.kernel.statistics.CmdDotPlot;
import org.geogebra.common.kernel.statistics.CmdErlang;
import org.geogebra.common.kernel.statistics.CmdExponential;
import org.geogebra.common.kernel.statistics.CmdFDistribution;
import org.geogebra.common.kernel.statistics.CmdFillCells;
import org.geogebra.common.kernel.statistics.CmdFillColumn;
import org.geogebra.common.kernel.statistics.CmdFillRow;
import org.geogebra.common.kernel.statistics.CmdFit;
import org.geogebra.common.kernel.statistics.CmdFitExp;
import org.geogebra.common.kernel.statistics.CmdFitGrowth;
import org.geogebra.common.kernel.statistics.CmdFitImplicit;
import org.geogebra.common.kernel.statistics.CmdFitLineX;
import org.geogebra.common.kernel.statistics.CmdFitLineY;
import org.geogebra.common.kernel.statistics.CmdFitLog;
import org.geogebra.common.kernel.statistics.CmdFitLogistic;
import org.geogebra.common.kernel.statistics.CmdFitPoly;
import org.geogebra.common.kernel.statistics.CmdFitPow;
import org.geogebra.common.kernel.statistics.CmdFitSin;
import org.geogebra.common.kernel.statistics.CmdFrequency;
import org.geogebra.common.kernel.statistics.CmdFrequencyPolygon;
import org.geogebra.common.kernel.statistics.CmdFrequencyTable;
import org.geogebra.common.kernel.statistics.CmdGamma;
import org.geogebra.common.kernel.statistics.CmdGeometricMean;
import org.geogebra.common.kernel.statistics.CmdHarmonicMean;
import org.geogebra.common.kernel.statistics.CmdHistogram;
import org.geogebra.common.kernel.statistics.CmdHistogramRight;
import org.geogebra.common.kernel.statistics.CmdHyperGeometric;
import org.geogebra.common.kernel.statistics.CmdInverseBinomial;
import org.geogebra.common.kernel.statistics.CmdInverseCauchy;
import org.geogebra.common.kernel.statistics.CmdInverseChiSquared;
import org.geogebra.common.kernel.statistics.CmdInverseExponential;
import org.geogebra.common.kernel.statistics.CmdInverseFDistribution;
import org.geogebra.common.kernel.statistics.CmdInverseGamma;
import org.geogebra.common.kernel.statistics.CmdInverseHyperGeometric;
import org.geogebra.common.kernel.statistics.CmdInverseLogNormal;
import org.geogebra.common.kernel.statistics.CmdInverseLogistic;
import org.geogebra.common.kernel.statistics.CmdInverseNormal;
import org.geogebra.common.kernel.statistics.CmdInversePascal;
import org.geogebra.common.kernel.statistics.CmdInversePoisson;
import org.geogebra.common.kernel.statistics.CmdInverseTDistribution;
import org.geogebra.common.kernel.statistics.CmdInverseWeibull;
import org.geogebra.common.kernel.statistics.CmdInverseZipf;
import org.geogebra.common.kernel.statistics.CmdLineGraph;
import org.geogebra.common.kernel.statistics.CmdLogNormal;
import org.geogebra.common.kernel.statistics.CmdLogistic;
import org.geogebra.common.kernel.statistics.CmdMAD;
import org.geogebra.common.kernel.statistics.CmdMean;
import org.geogebra.common.kernel.statistics.CmdMeanX;
import org.geogebra.common.kernel.statistics.CmdMeanY;
import org.geogebra.common.kernel.statistics.CmdMedian;
import org.geogebra.common.kernel.statistics.CmdMode;
import org.geogebra.common.kernel.statistics.CmdNormal;
import org.geogebra.common.kernel.statistics.CmdNormalQuantilePlot;
import org.geogebra.common.kernel.statistics.CmdOrdinalRank;
import org.geogebra.common.kernel.statistics.CmdPMCC;
import org.geogebra.common.kernel.statistics.CmdPascal;
import org.geogebra.common.kernel.statistics.CmdPercentile;
import org.geogebra.common.kernel.statistics.CmdPieChart;
import org.geogebra.common.kernel.statistics.CmdPoisson;
import org.geogebra.common.kernel.statistics.CmdQ1;
import org.geogebra.common.kernel.statistics.CmdQ3;
import org.geogebra.common.kernel.statistics.CmdRSquare;
import org.geogebra.common.kernel.statistics.CmdRandomBinomial;
import org.geogebra.common.kernel.statistics.CmdRandomDiscrete;
import org.geogebra.common.kernel.statistics.CmdRandomElement;
import org.geogebra.common.kernel.statistics.CmdRandomNormal;
import org.geogebra.common.kernel.statistics.CmdRandomPoisson;
import org.geogebra.common.kernel.statistics.CmdRandomPolynomial;
import org.geogebra.common.kernel.statistics.CmdRandomUniform;
import org.geogebra.common.kernel.statistics.CmdResidualPlot;
import org.geogebra.common.kernel.statistics.CmdRootMeanSquare;
import org.geogebra.common.kernel.statistics.CmdRow;
import org.geogebra.common.kernel.statistics.CmdSD;
import org.geogebra.common.kernel.statistics.CmdSDX;
import org.geogebra.common.kernel.statistics.CmdSDY;
import org.geogebra.common.kernel.statistics.CmdSXX;
import org.geogebra.common.kernel.statistics.CmdSXY;
import org.geogebra.common.kernel.statistics.CmdSYY;
import org.geogebra.common.kernel.statistics.CmdSample;
import org.geogebra.common.kernel.statistics.CmdSampleSD;
import org.geogebra.common.kernel.statistics.CmdSampleSDX;
import org.geogebra.common.kernel.statistics.CmdSampleSDY;
import org.geogebra.common.kernel.statistics.CmdSampleVariance;
import org.geogebra.common.kernel.statistics.CmdShuffle;
import org.geogebra.common.kernel.statistics.CmdSigmaXX;
import org.geogebra.common.kernel.statistics.CmdSigmaXY;
import org.geogebra.common.kernel.statistics.CmdSigmaYY;
import org.geogebra.common.kernel.statistics.CmdSpearman;
import org.geogebra.common.kernel.statistics.CmdStemPlot;
import org.geogebra.common.kernel.statistics.CmdStepGraph;
import org.geogebra.common.kernel.statistics.CmdStickGraph;
import org.geogebra.common.kernel.statistics.CmdSumSquaredErrors;
import org.geogebra.common.kernel.statistics.CmdTDistribution;
import org.geogebra.common.kernel.statistics.CmdTMean2Estimate;
import org.geogebra.common.kernel.statistics.CmdTMeanEstimate;
import org.geogebra.common.kernel.statistics.CmdTTest;
import org.geogebra.common.kernel.statistics.CmdTTest2;
import org.geogebra.common.kernel.statistics.CmdTTestPaired;
import org.geogebra.common.kernel.statistics.CmdTiedRank;
import org.geogebra.common.kernel.statistics.CmdTriangular;
import org.geogebra.common.kernel.statistics.CmdUniform;
import org.geogebra.common.kernel.statistics.CmdVariance;
import org.geogebra.common.kernel.statistics.CmdWeibull;
import org.geogebra.common.kernel.statistics.CmdZMean2Estimate;
import org.geogebra.common.kernel.statistics.CmdZMean2Test;
import org.geogebra.common.kernel.statistics.CmdZMeanEstimate;
import org.geogebra.common.kernel.statistics.CmdZMeanTest;
import org.geogebra.common.kernel.statistics.CmdZProportion2Estimate;
import org.geogebra.common.kernel.statistics.CmdZProportion2Test;
import org.geogebra.common.kernel.statistics.CmdZProportionEstimate;
import org.geogebra.common.kernel.statistics.CmdZProportionTest;
import org.geogebra.common.kernel.statistics.CmdZipf;

/**
 * class to split off some CmdXXX classes into another jar (for faster applet
 * loading)
 *
 */
public class CommandDispatcherStats implements CommandDispatcherInterface {
	@Override
	public CommandProcessor dispatch(Commands c, Kernel kernel) {
		switch (c) {
		case RandomElement:
			return new CmdRandomElement(kernel);
		case RandomPolynomial:
			return new CmdRandomPolynomial(kernel);
		case Classes:
			return new CmdClasses(kernel);
		case OrdinalRank:
			return new CmdOrdinalRank(kernel);
		case TiedRank:
			return new CmdTiedRank(kernel);
		case BoxPlot:
			return new CmdBoxPlot(kernel);
		case Histogram:
			return new CmdHistogram(kernel);
		case HistogramRight:
			return new CmdHistogramRight(kernel);
		case DotPlot:
			return new CmdDotPlot(kernel);
		case StemPlot:
			return new CmdStemPlot(kernel);
		case StickGraph:
			return new CmdStickGraph(kernel);
		case StepGraph:
			return new CmdStepGraph(kernel);
		case LineGraph:
			return new CmdLineGraph(kernel);
		case PieChart:
			return new CmdPieChart(kernel);
		case ResidualPlot:
			return new CmdResidualPlot(kernel);
		case FrequencyPolygon:
			return new CmdFrequencyPolygon(kernel);
		case NormalQuantilePlot:
			return new CmdNormalQuantilePlot(kernel);
		case FrequencyTable:
			return new CmdFrequencyTable(kernel);
		case ContingencyTable:
			return new CmdContingencyTable(kernel);
		case Mean:
		case mean:
			return new CmdMean(kernel);
		case var:
		case Variance:
			return new CmdVariance(kernel);
		case stdevp:
		case SD:
			return new CmdSD(kernel);
		case MAD:
		case mad:
			return new CmdMAD(kernel);
		case SampleVariance:
			return new CmdSampleVariance(kernel);
		case stdev:
		case SampleSD:
			return new CmdSampleSD(kernel);
		case Median:
			return new CmdMedian(kernel);
		case Q1:
		case Quartile1:
			return new CmdQ1(kernel);
		case Q3:
		case Quartile3:
			return new CmdQ3(kernel);
		case Mode:
			return new CmdMode(kernel);
		case SigmaXX:
			return new CmdSigmaXX(kernel);
		case SigmaXY:
			return new CmdSigmaXY(kernel);
		case SigmaYY:
			return new CmdSigmaYY(kernel);
		case cov:
		case Covariance:
			return new CmdCovariance(kernel);
		case SXY:
			return new CmdSXY(kernel);
		case SXX:
			return new CmdSXX(kernel);
		case SYY:
			return new CmdSYY(kernel);
		case MeanX:
			return new CmdMeanX(kernel);
		case MeanY:
			return new CmdMeanY(kernel);

		case CorrelationCoefficient:
		case PMCC:
			return new CmdPMCC(kernel);

		case SampleSDX:
			return new CmdSampleSDX(kernel);
		case SampleSDY:
			return new CmdSampleSDY(kernel);
		case SDX:
			return new CmdSDX(kernel);
		case SDY:
			return new CmdSDY(kernel);

		case FitLine:
		case FitLineY:
			return new CmdFitLineY(kernel);

		case FitLineX:
			return new CmdFitLineX(kernel);
		case FitPoly:
			return new CmdFitPoly(kernel);
		case FitExp:
			return new CmdFitExp(kernel);
		case FitLog:
			return new CmdFitLog(kernel);
		case FitPow:
			return new CmdFitPow(kernel);
		case Fit:
			return new CmdFit(kernel);
		case FitImplicit:
			return new CmdFitImplicit(kernel);
		case FitGrowth:
			return new CmdFitGrowth(kernel);
		case FitSin:
			return new CmdFitSin(kernel);
		case FitLogistic:
			return new CmdFitLogistic(kernel);
		case SumSquaredErrors:
			return new CmdSumSquaredErrors(kernel);
		case RSquare:
			return new CmdRSquare(kernel);
		case Sample:
			return new CmdSample(kernel);
		case Shuffle:
			return new CmdShuffle(kernel);
		case Spearman:
			return new CmdSpearman(kernel);
		case TTest:
			return new CmdTTest(kernel);
		case TTestPaired:
			return new CmdTTestPaired(kernel);
		case TTest2:
			return new CmdTTest2(kernel);
		case TMeanEstimate:
			return new CmdTMeanEstimate(kernel);
		case TMean2Estimate:
			return new CmdTMean2Estimate(kernel);
		case ChiSquaredTest:
			return new CmdChiSquaredTest(kernel);
		case ANOVA:
			return new CmdANOVA(kernel);
		case Percentile:
			return new CmdPercentile(kernel);
		case GeometricMean:
			return new CmdGeometricMean(kernel);
		case HarmonicMean:
			return new CmdHarmonicMean(kernel);
		case RootMeanSquare:
			return new CmdRootMeanSquare(kernel);
		case RandomDiscrete:
			return new CmdRandomDiscrete(kernel);
		case RandomNormal:
			return new CmdRandomNormal(kernel);
		case RandomUniform:
			return new CmdRandomUniform(kernel);
		case RandomBinomial:
			return new CmdRandomBinomial(kernel);
		case RandomPoisson:
			return new CmdRandomPoisson(kernel);
		case Normal:
			return new CmdNormal(kernel);
		case LogNormal:
			return new CmdLogNormal(kernel);
		case InverseLogNormal:
			return new CmdInverseLogNormal(kernel);
		case Logistic:
			return new CmdLogistic(kernel);
		case InverseLogistic:
			return new CmdInverseLogistic(kernel);
		case InverseNormal:
			return new CmdInverseNormal(kernel);
		case BinomialDist:
			return new CmdBinomialDist(kernel);
		case Bernoulli:
			return new CmdBernoulli(kernel);
		case InverseBinomial:
			return new CmdInverseBinomial(kernel);
		case TDistribution:
			return new CmdTDistribution(kernel);
		case InverseTDistribution:
			return new CmdInverseTDistribution(kernel);
		case FDistribution:
			return new CmdFDistribution(kernel);
		case InverseFDistribution:
			return new CmdInverseFDistribution(kernel);
		case Gamma:
			return new CmdGamma(kernel);
		case InverseGamma:
			return new CmdInverseGamma(kernel);
		case Cauchy:
			return new CmdCauchy(kernel);
		case InverseCauchy:
			return new CmdInverseCauchy(kernel);
		case ChiSquared:
			return new CmdChiSquared(kernel);
		case InverseChiSquared:
			return new CmdInverseChiSquared(kernel);
		case Exponential:
			return new CmdExponential(kernel);
		case InverseExponential:
			return new CmdInverseExponential(kernel);
		case HyperGeometric:
			return new CmdHyperGeometric(kernel);
		case InverseHyperGeometric:
			return new CmdInverseHyperGeometric(kernel);
		case Pascal:
			return new CmdPascal(kernel);
		case InversePascal:
			return new CmdInversePascal(kernel);
		case Poisson:
			return new CmdPoisson(kernel);
		case InversePoisson:
			return new CmdInversePoisson(kernel);
		case Weibull:
			return new CmdWeibull(kernel);
		case InverseWeibull:
			return new CmdInverseWeibull(kernel);
		case Zipf:
			return new CmdZipf(kernel);
		case InverseZipf:
			return new CmdInverseZipf(kernel);
		case Triangular:
			return new CmdTriangular(kernel);
		case Uniform:
			return new CmdUniform(kernel);
		case Erlang:
			return new CmdErlang(kernel);
		case CellRange:
			return new CmdCellRange(kernel); // cell range for spreadsheet
												// like A1:A5
		case Row:
			return new CmdRow(kernel);
		case Column:
			return new CmdColumn(kernel);
		case ColumnName:
			return new CmdColumnName(kernel);
		case FillRow:
			return new CmdFillRow(kernel);
		case FillColumn:
			return new CmdFillColumn(kernel);
		case FillCells:
			return new CmdFillCells(kernel);
		case Cell:
			return new CmdCell(kernel);

		case Frequency:
			return new CmdFrequency(kernel);
		case ZProportionTest:
			return new CmdZProportionTest(kernel);
		case ZProportion2Test:
			return new CmdZProportion2Test(kernel);
		case ZProportionEstimate:
			return new CmdZProportionEstimate(kernel);
		case ZProportion2Estimate:
			return new CmdZProportion2Estimate(kernel);
		case ZMeanEstimate:
			return new CmdZMeanEstimate(kernel);
		case ZMean2Estimate:
			return new CmdZMean2Estimate(kernel);
		case ZMeanTest:
			return new CmdZMeanTest(kernel);
		case ZMean2Test:
			return new CmdZMean2Test(kernel);
		default:
			break;
		}
		return null;
	}
}
