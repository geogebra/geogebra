package org.geogebra.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentBoolean;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Traversing.GeoCollector;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.debug.Log;

class StatementFeatures {

	static final String[] rules = { "Intersect", "Segment", "Midpoint",
			"OrthogonalLine", "Circle", "Line", "Point", "Free Point",
			"Ray", "Area", "Distance", "LineBisector", "Expression",
			"Translate", "Vector", "Polygon", "Tangent", "Parabola",
			"Mirror", "Ellipse", "AngularBisector", "Rotate", "Angle",
			"Hyperbola" };

	static final String[] obj_types = { "Point", "Circle", "Line",
			"Segment", "Triangle", "Numeric", "Pentagon", "Angle",
			"Triangle", "Parabola", "Ray", "Ellipse", "Hyperbola",
			"Quadrilateral", "Vector" };

	private static String csv_header = "", csv_data = "";

	private static HashMap<GeoElement, Integer> nodeLongestPath;
	private static HashMap<GeoElement, Integer> nodeComplexity;
	private static int longestPath;
	private static HashSet<ArrayList<GeoElement>> deps;

	private static void computeNodeLongestPath(GeoElement node, int set) {
		nodeLongestPath.put(node, set);
		if (set > longestPath) {
			longestPath = set;
		}
		AlgoElement ae = node.getParentAlgorithm();
		if (ae != null) {
			for (GeoElement dependency : ae.getInput()) {
				ArrayList<GeoElement> item = new ArrayList<GeoElement>();
				item.add(dependency);
				item.add(node);
				deps.add(item);
				computeNodeLongestPath(dependency, set + 1);
			}
		}
	}

	private static int computeNodeComplexity(GeoElement node) {
		Integer complexity = nodeComplexity.get(node);
		if (complexity != null) {
			return complexity;
		}
		AlgoElement ae = node.getParentAlgorithm();
		if (ae == null) {
			nodeComplexity.put(node, 0);
			return 0;
		}

		int parentsComplexity = 1;

		/*
		 * Compute node complexity by counting multiplicites in occurrences
		 * of GeoElement objects, if an expression is found.
		 */

		if (ae instanceof AlgoDependentBoolean) {
			ExpressionNode root = ((AlgoDependentBoolean) ae)
					.getExpression();

			HashMap<GeoElement, Integer> gSet = new HashMap<GeoElement, Integer>();
			GeoCollector gc = GeoCollector.getCollector(gSet);
			root.traverse(gc);
			Iterator<Entry<GeoElement, Integer>> it = gSet.entrySet().iterator();

			while (it.hasNext()) {
				Entry<GeoElement, Integer> entry = it.next();
				GeoElement dependency = entry.getKey();
				parentsComplexity += computeNodeComplexity(dependency)
						* entry.getValue();
			}

		} else {
			/* Otherwise just count each GeoElement once. */
			for (GeoElement dependency : ae.getInput()) {
				parentsComplexity += computeNodeComplexity(dependency);
			}
		}
		nodeComplexity.put(node, parentsComplexity);
		return parentsComplexity;
	}

	static void generateStatistics(String description, List<Object> nodes,
			String[] categories) {
		/*
		 * collecting algos, generating population and computing basic
		 * statistics
		 */
		int size = 0;

		double mean, variation_coefficient, minimum, maximum, entropy;
		HashMap<Object, Integer> frequencies = new HashMap<Object, Integer>();
		Iterator<Object> it = nodes.iterator();

		int number_of_nodes = 0;
		maximum = 1;
		minimum = -1; // assuming non-negative values
		mean = 0;
		while (it.hasNext()) {
			number_of_nodes++;
			int freq = 1;
			Object node = it.next();
			if (frequencies.containsKey(node)) {
				freq = frequencies.get(node) + 1;
			}
			frequencies.put(node, freq);
			if (node instanceof Integer) {
				maximum = Math.max(maximum, (Integer) node);
				if (minimum == -1) {
					minimum = (Integer) node;
				} else {
					minimum = Math.min(minimum, (Integer) node);
				}
				mean += (Integer) node;
			} else {
				maximum = Math.max(maximum, freq);
				if (categories == null) {
					if (minimum == -1) {
						minimum = freq;
					} else {
						minimum = Math.min(minimum, freq);
					}
					mean += freq;
				}
			}
		}

		int zeros;
		if (categories != null) {
			size = categories.length;
			minimum = maximum;
			// normalize
			maximum /= number_of_nodes;
			mean = (double) number_of_nodes / size;
			zeros = size - frequencies.size();
		} else {
			size = number_of_nodes;
			mean /= size;
			zeros = 0;
		}

		/* computing rest of statistics */

		/* ((3/7-1/23)^2+(1/7-1/23)^2*4+18*(1/23)^2)/23 == .00925 */
		variation_coefficient = 0;
		/*
		 * -((3/7)*log(3/7;A)+(1/7)*log(1/7;A)+(1/7)*log(1/7;A)+(1/7)*log(1/
		 * 7 ;A)+(1/7)*log(1/7;A))
		 */
		entropy = 0;
		Iterator<Entry<Object, Integer>> it2 = frequencies.entrySet()
				.iterator();
		while (it2.hasNext()) {
			Entry<Object, Integer> entry = it2.next();
			Object node = entry.getKey();
			int freq = entry.getValue();
			if (freq < minimum) {
				minimum = freq;
			}
			double rel_freq = freq / (double) number_of_nodes;
			double value;
			if (node instanceof Integer) {
				value = ((Integer) node) - mean;
				variation_coefficient += freq * value * value;
			} else {
				value = rel_freq - 1.0 / size;
				variation_coefficient += value * value;
			}
			entropy -= rel_freq * Math.log(rel_freq) / Math.log(2);
		}
		if (categories != null) {
			if (zeros > 0) {
				minimum = 0;
			} else {
				// normalize
				minimum /= number_of_nodes;
			}
		}

		double value = 1.0 / size;
		variation_coefficient += zeros * value * value;
		variation_coefficient /= size;
		Log.debug("population=" + frequencies);
		Log.debug("minimum=" + minimum + " maximum=" + maximum + " mean="
				+ mean + " variation_coefficient=" + variation_coefficient
				+ " entropy=" + entropy);

		if (categories != null) {
			description = "NF(" + description + ")";
			double rel_freq;
			for (String category : categories) {
				if (frequencies.containsKey(category)) {
					rel_freq = (double) frequencies.get(category)
							/ number_of_nodes;
				} else {
					rel_freq = 0;
				}
				csvAdd("NF(" + category + ")", rel_freq);
			}
		}

		csvAdd("max " + description, maximum);
		csvAdd("min " + description, minimum);
		csvAdd("mean " + description, mean);
		csvAdd("variation " + description, variation_coefficient);
		csvAdd("entropy " + description, entropy);
	}

	static void csvAdd(String header, double data) {
		csv_header += header + ",";
		csv_data += data + ",";
	}

	static void csvAdd(String header, String data) {
		csv_header += header + ",";
		csv_data += data + ",";
	}

	static void init(GeoElement statement) {

		nodeLongestPath = new HashMap<GeoElement, Integer>();
		nodeComplexity = new HashMap<GeoElement, Integer>();
		longestPath = 0;
		deps = new HashSet<ArrayList<GeoElement>>();
		csv_header = "";
		csv_data = "";

		TreeSet<GeoElement> geos = statement.getAllPredecessors();
		geos.add(statement);
		Iterator<GeoElement> it = geos.iterator();

		List<Object> geo_nodes, nodes_in_deg, nodes_out_deg, nodes_deg,
				types, objs;
		geo_nodes = new ArrayList<Object>();
		nodes_in_deg = new ArrayList<Object>();
		nodes_out_deg = new ArrayList<Object>();
		nodes_deg = new ArrayList<Object>();
		types = new ArrayList<Object>();
		objs = new ArrayList<Object>();

		int number_of_nodes = 0, free = 0, edges = 0;
		while (it.hasNext()) {
			GeoElement geo = it.next();
			TreeSet<GeoElement> children = geo.getAllChildren();
			int out = 0;
			for (GeoElement child : children) {
				if (geos.contains(child)) {
					boolean directChild = false;
					for (GeoElement father : child.getParentAlgorithm()
							.getInput()) {
						if (father.equals(geo)) {
							directChild = true;
						}
					}
					if (directChild && !child.equals(statement)) {
						out++;
					}
				}
			}
			int in = 0;
			AlgoElement ae = geo.getParentAlgorithm();
			String algo = "Free Point";
			if (ae != null) {
				GetCommand gc = ae.getClassName();
				if (gc != null) {
					/*
					 * Some algos don't have commands, e.g. IsPointOnPath.
					 * In such cases the digraph will use "null" label for
					 * all such nodes.
					 */
					algo = gc.getCommand();
				}
				GeoElement[] inputs = ae.getInput();
				if (!geo.equals(statement)) {
					edges += inputs.length;
				}
				in = inputs.length;
				for (GeoElement ref : inputs) {
					objs.add(ref);
				}
			} else {
				free++;
			}
			if (!geo.equals(statement)) {
				geo_nodes.add(algo);
				types.add(geo.getTypeString());
				nodes_in_deg.add(in);
				nodes_out_deg.add(out);
				nodes_deg.add(in + out);
				number_of_nodes++;
			}
		}

		computeNodeLongestPath(statement, 0);
		longestPath--;
		computeNodeComplexity(statement);

		// CSV output
		csvAdd("number of nodes", number_of_nodes);
		csvAdd("number of nodes with in-degree 0", free);
		csvAdd("number of edges", edges);
		csvAdd("num of nodes/num of edges",
				(double) number_of_nodes / edges);
		csvAdd("num of edges/num of nodes",
				(double) edges / number_of_nodes);
		csvAdd("max path length/num of nodes",
				(double) longestPath / number_of_nodes);
		csvAdd("num of nodes/max path length",
				(double) number_of_nodes / longestPath);
		csvAdd("max path length/num of edges",
				(double) longestPath / edges);
		csvAdd("num of edges/max path length",
				(double) edges / longestPath);
		csvAdd("statement complexity", nodeComplexity.get(statement));
		GetCommand dominantPredicate = statement.getParentAlgorithm()
				.getClassName();
		String dominantPredicateS = "";
		if (dominantPredicate != null) {
			dominantPredicateS = dominantPredicate.toString();
		}
		csvAdd("statement dominant predicate",
				dominantPredicateS);
		csvAdd("statement predicates",
				'"' + statement.getDefinition(StringTemplate.ogpTemplate)
						+ '"');
		generateStatistics("node in-degree", nodes_in_deg, null);
		generateStatistics("node out-degree", nodes_out_deg, null);
		generateStatistics("node degree", nodes_deg, null);
		/*
		 * csvAdd("num of nodes not labeled by A or B or C with in-degree 0"
		 * , 0);
		 */
		generateStatistics("Wi", geo_nodes, rules);
		generateStatistics("types", types, obj_types);
		generateStatistics("objs", objs, null);
		csvAdd("statement size", number_of_nodes - free);

		Log.debug("portfolio csv_header:" + csv_header);
		Log.debug("portfolio csv_data:" + csv_data);

		StringBuilder digraph = new StringBuilder("digraph dependencies { ");
		Iterator<ArrayList<GeoElement>> it2 = deps.iterator();
		digraph.append(statement.getLabelSimple()); 
		digraph.append("_");
		digraph.append(nodeComplexity.get(statement));
		digraph.append(" [style=filled]; ");
		while (it2.hasNext()) {
			ArrayList<GeoElement> al = it2.next();
			digraph.append(al.get(0).getLabelSimple());
			digraph.append("_");
			digraph.append(nodeComplexity.get(al.get(0)));
			digraph.append(" -> ");
			digraph.append(al.get(1).getLabelSimple());
			digraph.append("_");
			digraph.append(nodeComplexity.get(al.get(1)));
			if (al.get(1).equals(statement)) {
				digraph.append(" [style=dashed]");
			}
			digraph.append("; ");
		}
		digraph.append("}");
		Log.debug(digraph);
	}

}