package org.geogebra.common.exam.restrictions;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ANGLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ANGLE_FIXED;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ANGULAR_BISECTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_AREA;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCLE_POINT_RADIUS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCLE_THREE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCLE_TWO_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_COMPASSES;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CONIC_FIVE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_DILATE_FROM_POINT;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_DISTANCE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ELLIPSE_THREE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_FREEHAND_SHAPE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_IMAGE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_JOIN;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_LINE_BISECTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_LOCUS;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIDPOINT;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIRROR_AT_CIRCLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIRROR_AT_LINE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIRROR_AT_POINT;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ORTHOGONAL;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PARABOLA;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PARALLEL;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_POLAR_DIAMETER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_POLYGON;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_POLYLINE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RAY;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_REGULAR_POLYGON;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RELATION;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RIGID_POLYGON;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ROTATE_BY_ANGLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SEGMENT;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SEGMENT_FIXED;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SEMICIRCLE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TEXT;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRANSLATE_BY_VECTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_VECTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_VECTOR_FROM_POINT;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_VECTOR_POLYGON;
import static org.geogebra.common.kernel.commands.Commands.AffineRatio;
import static org.geogebra.common.kernel.commands.Commands.Angle;
import static org.geogebra.common.kernel.commands.Commands.AngleBisector;
import static org.geogebra.common.kernel.commands.Commands.Append;
import static org.geogebra.common.kernel.commands.Commands.Arc;
import static org.geogebra.common.kernel.commands.Commands.AreCollinear;
import static org.geogebra.common.kernel.commands.Commands.AreConcurrent;
import static org.geogebra.common.kernel.commands.Commands.AreConcyclic;
import static org.geogebra.common.kernel.commands.Commands.AreCongruent;
import static org.geogebra.common.kernel.commands.Commands.AreEqual;
import static org.geogebra.common.kernel.commands.Commands.AreParallel;
import static org.geogebra.common.kernel.commands.Commands.ArePerpendicular;
import static org.geogebra.common.kernel.commands.Commands.Area;
import static org.geogebra.common.kernel.commands.Commands.Asymptote;
import static org.geogebra.common.kernel.commands.Commands.Axes;
import static org.geogebra.common.kernel.commands.Commands.BarChart;
import static org.geogebra.common.kernel.commands.Commands.Barycenter;
import static org.geogebra.common.kernel.commands.Commands.Bernoulli;
import static org.geogebra.common.kernel.commands.Commands.BetaDist;
import static org.geogebra.common.kernel.commands.Commands.Bottom;
import static org.geogebra.common.kernel.commands.Commands.BoxPlot;
import static org.geogebra.common.kernel.commands.Commands.CFactor;
import static org.geogebra.common.kernel.commands.Commands.Cauchy;
import static org.geogebra.common.kernel.commands.Commands.Center;
import static org.geogebra.common.kernel.commands.Commands.Centroid;
import static org.geogebra.common.kernel.commands.Commands.Circle;
import static org.geogebra.common.kernel.commands.Commands.CircularArc;
import static org.geogebra.common.kernel.commands.Commands.CircularSector;
import static org.geogebra.common.kernel.commands.Commands.CircumcircularArc;
import static org.geogebra.common.kernel.commands.Commands.CircumcircularSector;
import static org.geogebra.common.kernel.commands.Commands.Circumference;
import static org.geogebra.common.kernel.commands.Commands.Classes;
import static org.geogebra.common.kernel.commands.Commands.ClosestPoint;
import static org.geogebra.common.kernel.commands.Commands.ClosestPointRegion;
import static org.geogebra.common.kernel.commands.Commands.Coefficients;
import static org.geogebra.common.kernel.commands.Commands.CompleteSquare;
import static org.geogebra.common.kernel.commands.Commands.Cone;
import static org.geogebra.common.kernel.commands.Commands.Conic;
import static org.geogebra.common.kernel.commands.Commands.ConjugateDiameter;
import static org.geogebra.common.kernel.commands.Commands.ContingencyTable;
import static org.geogebra.common.kernel.commands.Commands.ContinuedFraction;
import static org.geogebra.common.kernel.commands.Commands.ConvexHull;
import static org.geogebra.common.kernel.commands.Commands.Corner;
import static org.geogebra.common.kernel.commands.Commands.CountIf;
import static org.geogebra.common.kernel.commands.Commands.Covariance;
import static org.geogebra.common.kernel.commands.Commands.Cross;
import static org.geogebra.common.kernel.commands.Commands.CrossRatio;
import static org.geogebra.common.kernel.commands.Commands.Cube;
import static org.geogebra.common.kernel.commands.Commands.Cubic;
import static org.geogebra.common.kernel.commands.Commands.Curvature;
import static org.geogebra.common.kernel.commands.Commands.CurvatureVector;
import static org.geogebra.common.kernel.commands.Commands.Cylinder;
import static org.geogebra.common.kernel.commands.Commands.DataFunction;
import static org.geogebra.common.kernel.commands.Commands.DelaunayTriangulation;
import static org.geogebra.common.kernel.commands.Commands.Derivative;
import static org.geogebra.common.kernel.commands.Commands.Dilate;
import static org.geogebra.common.kernel.commands.Commands.Direction;
import static org.geogebra.common.kernel.commands.Commands.Directrix;
import static org.geogebra.common.kernel.commands.Commands.Distance;
import static org.geogebra.common.kernel.commands.Commands.Division;
import static org.geogebra.common.kernel.commands.Commands.Divisors;
import static org.geogebra.common.kernel.commands.Commands.DivisorsList;
import static org.geogebra.common.kernel.commands.Commands.DivisorsSum;
import static org.geogebra.common.kernel.commands.Commands.Dodecahedron;
import static org.geogebra.common.kernel.commands.Commands.Dot;
import static org.geogebra.common.kernel.commands.Commands.DotPlot;
import static org.geogebra.common.kernel.commands.Commands.DynamicCoordinates;
import static org.geogebra.common.kernel.commands.Commands.Eccentricity;
import static org.geogebra.common.kernel.commands.Commands.Element;
import static org.geogebra.common.kernel.commands.Commands.Ellipse;
import static org.geogebra.common.kernel.commands.Commands.Ends;
import static org.geogebra.common.kernel.commands.Commands.Envelope;
import static org.geogebra.common.kernel.commands.Commands.Erlang;
import static org.geogebra.common.kernel.commands.Commands.Expand;
import static org.geogebra.common.kernel.commands.Commands.Exponential;
import static org.geogebra.common.kernel.commands.Commands.Factor;
import static org.geogebra.common.kernel.commands.Commands.Factors;
import static org.geogebra.common.kernel.commands.Commands.First;
import static org.geogebra.common.kernel.commands.Commands.FitImplicit;
import static org.geogebra.common.kernel.commands.Commands.FitLineX;
import static org.geogebra.common.kernel.commands.Commands.Flatten;
import static org.geogebra.common.kernel.commands.Commands.Focus;
import static org.geogebra.common.kernel.commands.Commands.FormulaText;
import static org.geogebra.common.kernel.commands.Commands.FractionText;
import static org.geogebra.common.kernel.commands.Commands.Frequency;
import static org.geogebra.common.kernel.commands.Commands.FrequencyPolygon;
import static org.geogebra.common.kernel.commands.Commands.FrequencyTable;
import static org.geogebra.common.kernel.commands.Commands.Function;
import static org.geogebra.common.kernel.commands.Commands.Gamma;
import static org.geogebra.common.kernel.commands.Commands.GeometricMean;
import static org.geogebra.common.kernel.commands.Commands.GroebnerDegRevLex;
import static org.geogebra.common.kernel.commands.Commands.GroebnerLex;
import static org.geogebra.common.kernel.commands.Commands.GroebnerLexDeg;
import static org.geogebra.common.kernel.commands.Commands.HarmonicMean;
import static org.geogebra.common.kernel.commands.Commands.Height;
import static org.geogebra.common.kernel.commands.Commands.Histogram;
import static org.geogebra.common.kernel.commands.Commands.HistogramRight;
import static org.geogebra.common.kernel.commands.Commands.Hyperbola;
import static org.geogebra.common.kernel.commands.Commands.IFactor;
import static org.geogebra.common.kernel.commands.Commands.Icosahedron;
import static org.geogebra.common.kernel.commands.Commands.ImplicitCurve;
import static org.geogebra.common.kernel.commands.Commands.ImplicitDerivative;
import static org.geogebra.common.kernel.commands.Commands.Incircle;
import static org.geogebra.common.kernel.commands.Commands.IndexOf;
import static org.geogebra.common.kernel.commands.Commands.InfiniteCone;
import static org.geogebra.common.kernel.commands.Commands.InfiniteCylinder;
import static org.geogebra.common.kernel.commands.Commands.Insert;
import static org.geogebra.common.kernel.commands.Commands.IntegralBetween;
import static org.geogebra.common.kernel.commands.Commands.IntegralSymbolic;
import static org.geogebra.common.kernel.commands.Commands.IntersectConic;
import static org.geogebra.common.kernel.commands.Commands.IntersectPath;
import static org.geogebra.common.kernel.commands.Commands.Intersection;
import static org.geogebra.common.kernel.commands.Commands.InverseBeta;
import static org.geogebra.common.kernel.commands.Commands.InverseCauchy;
import static org.geogebra.common.kernel.commands.Commands.InverseExponential;
import static org.geogebra.common.kernel.commands.Commands.InverseGamma;
import static org.geogebra.common.kernel.commands.Commands.InverseLogNormal;
import static org.geogebra.common.kernel.commands.Commands.InverseLogistic;
import static org.geogebra.common.kernel.commands.Commands.InversePascal;
import static org.geogebra.common.kernel.commands.Commands.InverseWeibull;
import static org.geogebra.common.kernel.commands.Commands.InverseZipf;
import static org.geogebra.common.kernel.commands.Commands.IsFactored;
import static org.geogebra.common.kernel.commands.Commands.IsPrime;
import static org.geogebra.common.kernel.commands.Commands.IsVertexForm;
import static org.geogebra.common.kernel.commands.Commands.Iteration;
import static org.geogebra.common.kernel.commands.Commands.IterationList;
import static org.geogebra.common.kernel.commands.Commands.Join;
import static org.geogebra.common.kernel.commands.Commands.Last;
import static org.geogebra.common.kernel.commands.Commands.LeftSide;
import static org.geogebra.common.kernel.commands.Commands.LeftSum;
import static org.geogebra.common.kernel.commands.Commands.Length;
import static org.geogebra.common.kernel.commands.Commands.LetterToUnicode;
import static org.geogebra.common.kernel.commands.Commands.Limit;
import static org.geogebra.common.kernel.commands.Commands.LimitAbove;
import static org.geogebra.common.kernel.commands.Commands.LimitBelow;
import static org.geogebra.common.kernel.commands.Commands.Line;
import static org.geogebra.common.kernel.commands.Commands.LinearEccentricity;
import static org.geogebra.common.kernel.commands.Commands.Locus;
import static org.geogebra.common.kernel.commands.Commands.LocusEquation;
import static org.geogebra.common.kernel.commands.Commands.LogNormal;
import static org.geogebra.common.kernel.commands.Commands.Logistic;
import static org.geogebra.common.kernel.commands.Commands.LowerSum;
import static org.geogebra.common.kernel.commands.Commands.MajorAxis;
import static org.geogebra.common.kernel.commands.Commands.MatrixRank;
import static org.geogebra.common.kernel.commands.Commands.Midpoint;
import static org.geogebra.common.kernel.commands.Commands.MinimumSpanningTree;
import static org.geogebra.common.kernel.commands.Commands.MinorAxis;
import static org.geogebra.common.kernel.commands.Commands.Mod;
import static org.geogebra.common.kernel.commands.Commands.NDerivative;
import static org.geogebra.common.kernel.commands.Commands.NSolveODE;
import static org.geogebra.common.kernel.commands.Commands.Net;
import static org.geogebra.common.kernel.commands.Commands.NextPrime;
import static org.geogebra.common.kernel.commands.Commands.NormalQuantilePlot;
import static org.geogebra.common.kernel.commands.Commands.Normalize;
import static org.geogebra.common.kernel.commands.Commands.Numerator;
import static org.geogebra.common.kernel.commands.Commands.Octahedron;
import static org.geogebra.common.kernel.commands.Commands.Ordinal;
import static org.geogebra.common.kernel.commands.Commands.OrdinalRank;
import static org.geogebra.common.kernel.commands.Commands.OsculatingCircle;
import static org.geogebra.common.kernel.commands.Commands.Parabola;
import static org.geogebra.common.kernel.commands.Commands.Parameter;
import static org.geogebra.common.kernel.commands.Commands.ParametricDerivative;
import static org.geogebra.common.kernel.commands.Commands.PartialFractions;
import static org.geogebra.common.kernel.commands.Commands.Pascal;
import static org.geogebra.common.kernel.commands.Commands.PathParameter;
import static org.geogebra.common.kernel.commands.Commands.Percentile;
import static org.geogebra.common.kernel.commands.Commands.Perimeter;
import static org.geogebra.common.kernel.commands.Commands.PerpendicularBisector;
import static org.geogebra.common.kernel.commands.Commands.PerpendicularLine;
import static org.geogebra.common.kernel.commands.Commands.PerpendicularPlane;
import static org.geogebra.common.kernel.commands.Commands.PerpendicularVector;
import static org.geogebra.common.kernel.commands.Commands.PieChart;
import static org.geogebra.common.kernel.commands.Commands.Plane;
import static org.geogebra.common.kernel.commands.Commands.PlaneBisector;
import static org.geogebra.common.kernel.commands.Commands.Point;
import static org.geogebra.common.kernel.commands.Commands.PointIn;
import static org.geogebra.common.kernel.commands.Commands.PointList;
import static org.geogebra.common.kernel.commands.Commands.Polar;
import static org.geogebra.common.kernel.commands.Commands.Polygon;
import static org.geogebra.common.kernel.commands.Commands.Polyline;
import static org.geogebra.common.kernel.commands.Commands.Polynomial;
import static org.geogebra.common.kernel.commands.Commands.PreviousPrime;
import static org.geogebra.common.kernel.commands.Commands.Prism;
import static org.geogebra.common.kernel.commands.Commands.Product;
import static org.geogebra.common.kernel.commands.Commands.Prove;
import static org.geogebra.common.kernel.commands.Commands.ProveDetails;
import static org.geogebra.common.kernel.commands.Commands.Pyramid;
import static org.geogebra.common.kernel.commands.Commands.Radius;
import static org.geogebra.common.kernel.commands.Commands.RandomBetween;
import static org.geogebra.common.kernel.commands.Commands.RandomBinomial;
import static org.geogebra.common.kernel.commands.Commands.RandomDiscrete;
import static org.geogebra.common.kernel.commands.Commands.RandomElement;
import static org.geogebra.common.kernel.commands.Commands.RandomNormal;
import static org.geogebra.common.kernel.commands.Commands.RandomPointIn;
import static org.geogebra.common.kernel.commands.Commands.RandomPoisson;
import static org.geogebra.common.kernel.commands.Commands.RandomPolynomial;
import static org.geogebra.common.kernel.commands.Commands.RandomUniform;
import static org.geogebra.common.kernel.commands.Commands.Ray;
import static org.geogebra.common.kernel.commands.Commands.RectangleSum;
import static org.geogebra.common.kernel.commands.Commands.Reflect;
import static org.geogebra.common.kernel.commands.Commands.RemovableDiscontinuity;
import static org.geogebra.common.kernel.commands.Commands.Remove;
import static org.geogebra.common.kernel.commands.Commands.RemoveUndefined;
import static org.geogebra.common.kernel.commands.Commands.ReplaceAll;
import static org.geogebra.common.kernel.commands.Commands.ResidualPlot;
import static org.geogebra.common.kernel.commands.Commands.Reverse;
import static org.geogebra.common.kernel.commands.Commands.RightSide;
import static org.geogebra.common.kernel.commands.Commands.RigidPolygon;
import static org.geogebra.common.kernel.commands.Commands.RootList;
import static org.geogebra.common.kernel.commands.Commands.RootMeanSquare;
import static org.geogebra.common.kernel.commands.Commands.Rotate;
import static org.geogebra.common.kernel.commands.Commands.Sample;
import static org.geogebra.common.kernel.commands.Commands.SampleVariance;
import static org.geogebra.common.kernel.commands.Commands.ScientificText;
import static org.geogebra.common.kernel.commands.Commands.Sector;
import static org.geogebra.common.kernel.commands.Commands.Segment;
import static org.geogebra.common.kernel.commands.Commands.SelectedIndex;
import static org.geogebra.common.kernel.commands.Commands.SemiMajorAxisLength;
import static org.geogebra.common.kernel.commands.Commands.SemiMinorAxisLength;
import static org.geogebra.common.kernel.commands.Commands.Semicircle;
import static org.geogebra.common.kernel.commands.Commands.Shear;
import static org.geogebra.common.kernel.commands.Commands.ShortestDistance;
import static org.geogebra.common.kernel.commands.Commands.Shuffle;
import static org.geogebra.common.kernel.commands.Commands.Side;
import static org.geogebra.common.kernel.commands.Commands.SigmaXX;
import static org.geogebra.common.kernel.commands.Commands.SigmaXY;
import static org.geogebra.common.kernel.commands.Commands.SigmaYY;
import static org.geogebra.common.kernel.commands.Commands.Simplify;
import static org.geogebra.common.kernel.commands.Commands.SlopeField;
import static org.geogebra.common.kernel.commands.Commands.Solutions;
import static org.geogebra.common.kernel.commands.Commands.Solve;
import static org.geogebra.common.kernel.commands.Commands.SolveODE;
import static org.geogebra.common.kernel.commands.Commands.Sort;
import static org.geogebra.common.kernel.commands.Commands.Spearman;
import static org.geogebra.common.kernel.commands.Commands.Sphere;
import static org.geogebra.common.kernel.commands.Commands.Spline;
import static org.geogebra.common.kernel.commands.Commands.Split;
import static org.geogebra.common.kernel.commands.Commands.StickGraph;
import static org.geogebra.common.kernel.commands.Commands.Stretch;
import static org.geogebra.common.kernel.commands.Commands.Substitute;
import static org.geogebra.common.kernel.commands.Commands.Sum;
import static org.geogebra.common.kernel.commands.Commands.SumSquaredErrors;
import static org.geogebra.common.kernel.commands.Commands.SurdText;
import static org.geogebra.common.kernel.commands.Commands.Surface;
import static org.geogebra.common.kernel.commands.Commands.Sxx;
import static org.geogebra.common.kernel.commands.Commands.Sxy;
import static org.geogebra.common.kernel.commands.Commands.Syy;
import static org.geogebra.common.kernel.commands.Commands.TTestPaired;
import static org.geogebra.common.kernel.commands.Commands.Take;
import static org.geogebra.common.kernel.commands.Commands.TaylorPolynomial;
import static org.geogebra.common.kernel.commands.Commands.Tetrahedron;
import static org.geogebra.common.kernel.commands.Commands.Text;
import static org.geogebra.common.kernel.commands.Commands.TextToUnicode;
import static org.geogebra.common.kernel.commands.Commands.TiedRank;
import static org.geogebra.common.kernel.commands.Commands.ToComplex;
import static org.geogebra.common.kernel.commands.Commands.ToPoint;
import static org.geogebra.common.kernel.commands.Commands.Top;
import static org.geogebra.common.kernel.commands.Commands.Translate;
import static org.geogebra.common.kernel.commands.Commands.TrapezoidalSum;
import static org.geogebra.common.kernel.commands.Commands.TravelingSalesman;
import static org.geogebra.common.kernel.commands.Commands.TriangleCenter;
import static org.geogebra.common.kernel.commands.Commands.TriangleCurve;
import static org.geogebra.common.kernel.commands.Commands.Triangular;
import static org.geogebra.common.kernel.commands.Commands.TrigCombine;
import static org.geogebra.common.kernel.commands.Commands.TrigExpand;
import static org.geogebra.common.kernel.commands.Commands.TrigSimplify;
import static org.geogebra.common.kernel.commands.Commands.Trilinear;
import static org.geogebra.common.kernel.commands.Commands.UnicodeToLetter;
import static org.geogebra.common.kernel.commands.Commands.UnicodeToText;
import static org.geogebra.common.kernel.commands.Commands.Uniform;
import static org.geogebra.common.kernel.commands.Commands.Union;
import static org.geogebra.common.kernel.commands.Commands.Unique;
import static org.geogebra.common.kernel.commands.Commands.UnitPerpendicularVector;
import static org.geogebra.common.kernel.commands.Commands.UnitVector;
import static org.geogebra.common.kernel.commands.Commands.UpperSum;
import static org.geogebra.common.kernel.commands.Commands.Vector;
import static org.geogebra.common.kernel.commands.Commands.Vertex;
import static org.geogebra.common.kernel.commands.Commands.Volume;
import static org.geogebra.common.kernel.commands.Commands.Voronoi;
import static org.geogebra.common.kernel.commands.Commands.Weibull;
import static org.geogebra.common.kernel.commands.Commands.Zip;
import static org.geogebra.common.kernel.commands.Commands.Zipf;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.BETA;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.CAUCHY;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.EXPONENTIAL;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.GAMMA;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.LOGISTIC;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.LOGNORMAL;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.PASCAL;
import static org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist.WEIBULL;

import java.util.Map;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.ib.PointDerivativeFilter;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFilter;
import org.geogebra.common.gui.toolcategorization.impl.ToolCollectionSetFilter;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.BaseCommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
import org.geogebra.common.main.syntax.suggestionfilter.LineSelectorSyntaxFilter;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;

public final class IBExamRestrictions extends ExamRestrictions {

	IBExamRestrictions() {
		super(ExamType.IB,
				Set.of(SuiteSubApp.CAS, SuiteSubApp.G3D),
				SuiteSubApp.GRAPHING,
				null,
				createExpressionFilters(),
				null,
				createCommandFilters(),
				createCommandArgumentFilters(),
				null,
				null,
				createSyntaxFilter(),
				createToolCollectionFilter(),
				createDistributionPropertyRestriction(),
				null,
				null);
	}

	private static Set<ExpressionFilter> createExpressionFilters() {
		return Set.of(new PointDerivativeFilter());
	}

	private static Set<CommandFilter> createCommandFilters() {
		CommandNameFilter nameFilter = new CommandNameFilter(true,
				Bottom, Cone, Cube, Cylinder, Dodecahedron, Ends, Height, Icosahedron, InfiniteCone,
				InfiniteCylinder, IntersectConic, Net, Octahedron, PerpendicularPlane, Plane,
				PlaneBisector, Prism, Pyramid, Side, Sphere, Surface, Tetrahedron, Top, Volume,
				CompleteSquare, Cross, Division, Divisors, DivisorsList, DivisorsSum, Dot, Expand,
				Factor, IFactor, IsFactored, IsPrime, LeftSide, Mod, NextPrime, PreviousPrime,
				RightSide, Simplify, Solutions, Solve, BarChart, BoxPlot, ContingencyTable, DotPlot,
				FrequencyPolygon, FrequencyTable, Histogram, HistogramRight, NormalQuantilePlot,
				PieChart, ResidualPlot, StickGraph, Axes, Center, Circle, Conic, ConjugateDiameter,
				Directrix, Eccentricity, Ellipse, Focus, Hyperbola, Incircle, LinearEccentricity,
				MajorAxis, MinorAxis, Parabola, Parameter, Polar, SemiMajorAxisLength,
				SemiMinorAxisLength, Semicircle, ConvexHull, DelaunayTriangulation,
				MinimumSpanningTree, ShortestDistance, TravelingSalesman, Voronoi, Asymptote,
				Coefficients, Curvature, CurvatureVector, DataFunction, Derivative, Factors,
				Function, ImplicitCurve, ImplicitDerivative, IntegralBetween, IntegralSymbolic,
				Iteration, IterationList, IsVertexForm, LeftSum, Limit, LimitAbove, LimitBelow,
				LowerSum, NSolveODE, Normalize, Numerator, OsculatingCircle, ParametricDerivative,
				PartialFractions, PathParameter, Polynomial, RectangleSum, RemovableDiscontinuity,
				RootList, SlopeField, SolveODE, Spline, TaylorPolynomial, TrapezoidalSum,
				TrigCombine, TrigExpand, TrigSimplify, UpperSum, AffineRatio, Angle, AngleBisector,
				Arc, AreCollinear, AreConcurrent, AreConcyclic, AreCongruent, AreEqual, AreParallel,
				ArePerpendicular, Area, Barycenter, Centroid, CircularArc, CircularSector,
				CircumcircularArc, CircumcircularSector, Circumference, ClosestPoint,
				ClosestPointRegion, CrossRatio, Cubic, Direction, Distance, Envelope, IntersectPath,
				Intersection, Length, Line, Locus, LocusEquation, Midpoint, Perimeter,
				PerpendicularBisector, PerpendicularLine, Point, PointIn, Polygon, Polyline, Prove,
				ProveDetails, Radius, Ray, RigidPolygon, Sector, Segment, TriangleCenter,
				TriangleCurve, Trilinear, Vertex, Corner, DynamicCoordinates, ToComplex, ToPoint,
				Append, Classes, Element, First, Flatten, Frequency, IndexOf, Insert,
				Join, Last, OrdinalRank, PointList, Product, RandomElement, Remove, RemoveUndefined,
				Reverse, SelectedIndex, Shuffle, Sort, Take, TiedRank, Union, Unique, Zip, CountIf,
				Bernoulli, Cauchy, Erlang, Exponential, Gamma, InverseCauchy, InverseExponential,
				InverseGamma, InverseLogNormal, InverseLogistic, InversePascal, InverseWeibull,
				InverseZipf, LogNormal, Logistic, Pascal, RandomBetween, RandomBinomial,
				RandomDiscrete, RandomNormal, RandomPointIn, RandomPoisson, RandomPolynomial,
				RandomUniform, Triangular, Uniform, Weibull, Zipf, Covariance, FitImplicit,
				FitLineX, GeometricMean, HarmonicMean, Percentile,
				RootMeanSquare, Sample, SampleVariance, SigmaXX, SigmaXY, SigmaYY,
				Spearman, Sum, SumSquaredErrors, Sxx, Sxy, Syy, TTestPaired, ContinuedFraction,
				FormulaText, FractionText, LetterToUnicode, Ordinal, ReplaceAll, Split,
				ScientificText, SurdText, Text, TextToUnicode, UnicodeToLetter, UnicodeToText,
				Dilate, Reflect, Rotate, Shear, Stretch, Translate, MatrixRank,
				PerpendicularVector, UnitPerpendicularVector, UnitVector, Vector, CFactor,
				GroebnerDegRevLex, GroebnerLexDeg, GroebnerLex, Substitute, NDerivative, BetaDist,
				InverseBeta);
		return Set.of(nameFilter);
	}

	private static Set<CommandArgumentFilter> createCommandArgumentFilters() {
		return Set.of(new IBExamCommandFilter());
	}

	private static SyntaxFilter createSyntaxFilter() {
		LineSelectorSyntaxFilter filter = new LineSelectorSyntaxFilter();
		filter.addSelector(Commands.Integral, 2);
		filter.addSelector(Commands.Invert, 0);
		return filter;
	}

	private static ToolCollectionFilter createToolCollectionFilter() {
		return new ToolCollectionSetFilter(MODE_IMAGE, MODE_TEXT, MODE_ANGLE, MODE_DISTANCE,
				MODE_AREA, MODE_ANGLE_FIXED, MODE_MIDPOINT, MODE_ORTHOGONAL, MODE_LINE_BISECTOR,
				MODE_PARALLEL, MODE_ANGULAR_BISECTOR, MODE_LOCUS, MODE_SEGMENT, MODE_JOIN,
				MODE_RAY, MODE_VECTOR, MODE_SEGMENT_FIXED, MODE_VECTOR_FROM_POINT,
				MODE_POLAR_DIAMETER, MODE_POLYLINE, MODE_POLYGON, MODE_REGULAR_POLYGON,
				MODE_VECTOR_POLYGON, MODE_RIGID_POLYGON, MODE_CIRCLE_TWO_POINTS, MODE_COMPASSES,
				MODE_SEMICIRCLE, MODE_CIRCLE_POINT_RADIUS, MODE_CIRCLE_THREE_POINTS,
				MODE_CIRCLE_ARC_THREE_POINTS, MODE_CIRCUMCIRCLE_ARC_THREE_POINTS,
				MODE_CIRCLE_SECTOR_THREE_POINTS, MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS,
				MODE_ELLIPSE_THREE_POINTS, MODE_CONIC_FIVE_POINTS, MODE_PARABOLA,
				MODE_HYPERBOLA_THREE_POINTS, MODE_MIRROR_AT_LINE, MODE_MIRROR_AT_POINT,
				MODE_TRANSLATE_BY_VECTOR, MODE_ROTATE_BY_ANGLE, MODE_DILATE_FROM_POINT,
				MODE_MIRROR_AT_CIRCLE, MODE_FREEHAND_SHAPE, MODE_RELATION);
	}

	private static Map<String, PropertyRestriction> createDistributionPropertyRestriction() {
		Set<ProbabilityCalculatorSettings.Dist> restrictedDistributions = Set.of(
				EXPONENTIAL, CAUCHY, WEIBULL, GAMMA, BETA, LOGNORMAL, LOGISTIC, PASCAL
		);
		return Map.of("Distribution", new PropertyRestriction(false, value ->
						!restrictedDistributions.contains(value)));
	}

	private static class IBExamCommandFilter extends BaseCommandArgumentFilter {

		@Override
		public void checkAllowed(Command command, CommandProcessor commandProcessor)
				throws MyError {
			if (isCommand(command, Commands.Integral)) {
				if (command.getArgumentNumber() != 3) {
					throw commandProcessor.argNumErr(command, command.getArgumentNumber());
				}
			} else if (isCommand(command, Commands.Invert)) {
				GeoElement[] elements = commandProcessor.resArgs(command);
				if (elements.length == 1 && elements[0] instanceof GeoFunction) {
					throw commandProcessor.argErr(command, elements[0]);
				}
			}
		}
	}
}
