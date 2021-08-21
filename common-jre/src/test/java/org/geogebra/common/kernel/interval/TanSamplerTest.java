package org.geogebra.common.kernel.interval;

import static java.lang.Math.PI;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TanSamplerTest extends SamplerTest {
	@Test
	public void tanX() {
		IntervalTupleList tuples = functionValuesWithSampleCount("tan(x)",
				0, PI, -8, 8, 100);
		assertEquals(interval(-4.9E-324, 0.03142626604335116), tuples.valueAt(0));
		assertEquals(interval(0.03142626604335114, 0.06291466725364978), tuples.valueAt(1));
		assertEquals(interval(0.06291466725364975, 0.09452783117928207), tuples.valueAt(2));
		assertEquals(interval(0.09452783117928204, 0.1263293784461082), tuples.valueAt(3));
		assertEquals(interval(0.12632937844610814, 0.1583844403245363), tuples.valueAt(4));
		assertEquals(interval(0.15838444032453625, 0.19076020221856677), tuples.valueAt(5));
		assertEquals(interval(0.1907602022185667, 0.2235264828971491), tuples.valueAt(6));
		assertEquals(interval(0.22352648289714905, 0.2567563603677268), tuples.valueAt(7));
		assertEquals(interval(0.2567563603677267, 0.29052685673191647), tuples.valueAt(8));
		assertEquals(interval(0.29052685673191636, 0.3249196962329063), tuples.valueAt(9));
		assertEquals(interval(0.3249196962329062, 0.3600221530957566), tuples.valueAt(10));
		assertEquals(interval(0.36002215309575647, 0.3959280087977212), tuples.valueAt(11));
		assertEquals(interval(0.3959280087977211, 0.43273864224742586), tuples.valueAt(12));
		assertEquals(interval(0.43273864224742575, 0.4705642812122514), tuples.valueAt(13));
		assertEquals(interval(0.4705642812122513, 0.5095254494944288), tuples.valueAt(14));
		assertEquals(interval(0.5095254494944286, 0.5497546521927701), tuples.valueAt(15));
		assertEquals(interval(0.5497546521927699, 0.5913983513994712), tuples.valueAt(16));
		assertEquals(interval(0.591398351399471, 0.6346192975441483), tuples.valueAt(17));
		assertEquals(interval(0.634619297544148, 0.6795992982245267), tuples.valueAt(18));
		assertEquals(interval(0.6795992982245265, 0.7265425280053611), tuples.valueAt(19));
		assertEquals(interval(0.7265425280053609, 0.7756795110496134), tuples.valueAt(20));
		assertEquals(interval(0.7756795110496132, 0.827271945972476), tuples.valueAt(21));
		assertEquals(interval(0.8272719459724758, 0.8816185923631896), tuples.valueAt(22));
		assertEquals(interval(0.8816185923631894, 0.939062505817493), tuples.valueAt(23));
		assertEquals(interval(0.9390625058174927, 1.0000000000000009), tuples.valueAt(24));
		assertEquals(interval(1.0000000000000004, 1.064891840324793), tuples.valueAt(25));
		assertEquals(interval(1.0648918403247924, 1.1342773492554066), tuples.valueAt(26));
		assertEquals(interval(1.1342773492554061, 1.2087923504096103), tuples.valueAt(27));
		assertEquals(interval(1.2087923504096099, 1.2891922317850681), tuples.valueAt(28));
		assertEquals(interval(1.2891922317850677, 1.3763819204711751), tuples.valueAt(29));
		assertEquals(interval(1.3763819204711747, 1.471455315819971), tuples.valueAt(30));
		assertEquals(interval(1.4714553158199706, 1.575747859968653), tuples.valueAt(31));
		assertEquals(interval(1.5757478599686525, 1.6909076557850136), tuples.valueAt(32));
		assertEquals(interval(1.6909076557850131, 1.818993247281069), tuples.valueAt(33));
		assertEquals(interval(1.8189932472810686, 1.962610505505154), tuples.valueAt(34));
		assertEquals(interval(1.9626105055051535, 2.125108173157207), tuples.valueAt(35));
		assertEquals(interval(2.125108173157206, 2.3108636538824157), tuples.valueAt(36));
		assertEquals(interval(2.3108636538824148, 2.525711689447311), tuples.valueAt(37));
		assertEquals(interval(2.52571168944731, 2.7776068539149823), tuples.valueAt(38));
		assertEquals(interval(2.7776068539149814, 3.0776835371752624), tuples.valueAt(39));
		assertEquals(interval(3.0776835371752616, 3.4420225766692303), tuples.valueAt(40));
		assertEquals(interval(3.4420225766692294, 3.894742854929875), tuples.valueAt(41));
		assertEquals(interval(3.894742854929874, 4.4737428292115755), tuples.valueAt(42));
		assertEquals(interval(4.473742829211574, 5.242183581113206), tuples.valueAt(43));
		assertEquals(interval(5.2421835811132045, 6.313751514675087), tuples.valueAt(44));
		assertEquals(interval(6.3137515146750856, 7.915815088305898), tuples.valueAt(45));
		assertEquals(interval(7.915815088305896, 10.578894993405767), tuples.valueAt(46));
		assertEquals(interval(10.578894993405763, 15.894544843865607), tuples.valueAt(47));
		assertEquals(interval(15.894544843865603, 31.820515953775207), tuples.valueAt(48));
		assertEquals(invertedInterval(-1.0E-4, 1.0E-4), tuples.valueAt(49));
		assertEquals(interval(-7.176175255700845E14, -31.8205159537725), tuples.valueAt(50));
		assertEquals(interval(-31.820515953772507, -15.894544843864926), tuples.valueAt(51));
		assertEquals(interval(-15.89454484386493, -10.578894993405461), tuples.valueAt(52));
		assertEquals(interval(-10.578894993405465, -7.9158150883057266), tuples.valueAt(53));
		assertEquals(interval(-7.915815088305728, -6.313751514674976), tuples.valueAt(54));
		assertEquals(interval(-6.313751514674978, -5.242183581113129), tuples.valueAt(55));
		assertEquals(interval(-5.242183581113131, -4.473742829211518), tuples.valueAt(56));
		assertEquals(interval(-4.47374282921152, -3.894742854929831), tuples.valueAt(57));
		assertEquals(interval(-3.8947428549298317, -3.442022576669195), tuples.valueAt(58));
		assertEquals(interval(-3.442022576669196, -3.077683537175234), tuples.valueAt(59));
		assertEquals(interval(-3.077683537175235, -2.7776068539149583), tuples.valueAt(60));
		assertEquals(interval(-2.777606853914959, -2.5257116894472906), tuples.valueAt(61));
		assertEquals(interval(-2.5257116894472915, -2.310863653882398), tuples.valueAt(62));
		assertEquals(interval(-2.310863653882399, -2.1251081731571912), tuples.valueAt(63));
		assertEquals(interval(-2.125108173157192, -1.9626105055051415), tuples.valueAt(64));
		assertEquals(interval(-1.962610505505142, -1.818993247281059), tuples.valueAt(65));
		assertEquals(interval(-1.8189932472810595, -1.6909076557850053), tuples.valueAt(66));
		assertEquals(interval(-1.6909076557850058, -1.5757478599686463), tuples.valueAt(67));
		assertEquals(interval(-1.5757478599686467, -1.4714553158199652), tuples.valueAt(68));
		assertEquals(interval(-1.4714553158199657, -1.3763819204711707), tuples.valueAt(69));
		assertEquals(interval(-1.3763819204711711, -1.2891922317850644), tuples.valueAt(70));
		assertEquals(interval(-1.2891922317850648, -1.2087923504096076), tuples.valueAt(71));
		assertEquals(interval(-1.208792350409608, -1.1342773492554044), tuples.valueAt(72));
		assertEquals(interval(-1.1342773492554048, -1.0648918403247911), tuples.valueAt(73));
		assertEquals(interval(-1.0648918403247916, -0.9999999999999998), tuples.valueAt(74));
		assertEquals(interval(-1.0, -0.9390625058174925), tuples.valueAt(75));
		assertEquals(interval(-0.9390625058174927, -0.8816185923631896), tuples.valueAt(76));
		assertEquals(interval(-0.8816185923631898, -0.8272719459724763), tuples.valueAt(77));
		assertEquals(interval(-0.8272719459724766, -0.7756795110496141), tuples.valueAt(78));
		assertEquals(interval(-0.7756795110496143, -0.7265425280053621), tuples.valueAt(79));
		assertEquals(interval(-0.7265425280053623, -0.6795992982245279), tuples.valueAt(80));
		assertEquals(interval(-0.6795992982245281, -0.6346192975441497), tuples.valueAt(81));
		assertEquals(interval(-0.6346192975441499, -0.5913983513994728), tuples.valueAt(82));
		assertEquals(interval(-0.591398351399473, -0.549754652192772), tuples.valueAt(83));
		assertEquals(interval(-0.5497546521927722, -0.5095254494944309), tuples.valueAt(84));
		assertEquals(interval(-0.5095254494944311, -0.4705642812122538), tuples.valueAt(85));
		assertEquals(interval(-0.4705642812122539, -0.43273864224742836), tuples.valueAt(86));
		assertEquals(interval(-0.4327386422474285, -0.3959280087977239), tuples.valueAt(87));
		assertEquals(interval(-0.395928008797724, -0.36002215309575936), tuples.valueAt(88));
		assertEquals(interval(-0.36002215309575947, -0.3249196962329092), tuples.valueAt(89));
		assertEquals(interval(-0.32491969623290934, -0.29052685673191947), tuples.valueAt(90));
		assertEquals(interval(-0.2905268567319196, -0.2567563603677299), tuples.valueAt(91));
		assertEquals(interval(-0.25675636036773003, -0.22352648289715243), tuples.valueAt(92));
		assertEquals(interval(-0.2235264828971525, -0.1907602022185702), tuples.valueAt(93));
		assertEquals(interval(-0.19076020221857026, -0.1583844403245399), tuples.valueAt(94));
		assertEquals(interval(-0.15838444032453997, -0.12632937844611195), tuples.valueAt(95));
		assertEquals(interval(-0.126329378446112, -0.09452783117928597), tuples.valueAt(96));
		assertEquals(interval(-0.094527831179286, -0.06291466725365384), tuples.valueAt(97));
		assertEquals(interval(-0.06291466725365387, -0.031426266043355404), tuples.valueAt(98));
		assertEquals(interval(-0.03142626604335542, -4.440892098500625E-15), tuples.valueAt(99));
		assertEquals(interval(-4.440892098500627E-15, 0.03142626604334653), tuples.valueAt(100));
	}

	@Test
	public void tanXHiRes() {
		for (IntervalTuple tuple: hiResFunction("tan(x)")) {
			assertEquals(IntervalConstants.whole().invert(), tuple.y());
		}
	}

		@Test
	public void minusTanX() {
		IntervalTupleList tuples = functionValuesWithSampleCount("-tan(x)",
				0, PI, -8, 8, 100);
		assertEquals(interval(-0.031426266043351164, 1.0E-323), tuples.valueAt(0));
		assertEquals(interval(-0.06291466725364979, -0.031426266043351136), tuples.valueAt(1));
		assertEquals(interval(-0.09452783117928208, -0.06291466725364973), tuples.valueAt(2));
		assertEquals(interval(-0.12632937844610823, -0.09452783117928203), tuples.valueAt(3));
		assertEquals(interval(-0.15838444032453633, -0.12632937844610811), tuples.valueAt(4));
		assertEquals(interval(-0.1907602022185668, -0.15838444032453622), tuples.valueAt(5));
		assertEquals(interval(-0.22352648289714913, -0.19076020221856668), tuples.valueAt(6));
		assertEquals(interval(-0.25675636036772687, -0.22352648289714902), tuples.valueAt(7));
		assertEquals(interval(-0.2905268567319165, -0.25675636036772664), tuples.valueAt(8));
		assertEquals(interval(-0.32491969623290634, -0.2905268567319163), tuples.valueAt(9));
		assertEquals(interval(-0.36002215309575664, -0.3249196962329061), tuples.valueAt(10));
		assertEquals(interval(-0.3959280087977213, -0.3600221530957564), tuples.valueAt(11));
		assertEquals(interval(-0.4327386422474259, -0.39592800879772105), tuples.valueAt(12));
		assertEquals(interval(-0.47056428121225147, -0.4327386422474257), tuples.valueAt(13));
		assertEquals(interval(-0.5095254494944289, -0.47056428121225125), tuples.valueAt(14));
		assertEquals(interval(-0.5497546521927702, -0.5095254494944285), tuples.valueAt(15));
		assertEquals(interval(-0.5913983513994713, -0.5497546521927698), tuples.valueAt(16));
		assertEquals(interval(-0.6346192975441484, -0.5913983513994708), tuples.valueAt(17));
		assertEquals(interval(-0.6795992982245268, -0.6346192975441479), tuples.valueAt(18));
		assertEquals(interval(-0.7265425280053612, -0.6795992982245264), tuples.valueAt(19));
		assertEquals(interval(-0.7756795110496135, -0.7265425280053608), tuples.valueAt(20));
		assertEquals(interval(-0.8272719459724761, -0.7756795110496131), tuples.valueAt(21));
		assertEquals(interval(-0.8816185923631897, -0.8272719459724757), tuples.valueAt(22));
		assertEquals(interval(-0.9390625058174931, -0.8816185923631893), tuples.valueAt(23));
		assertEquals(interval(-1.000000000000001, -0.9390625058174926), tuples.valueAt(24));
		assertEquals(interval(-1.0648918403247931, -1.0000000000000002), tuples.valueAt(25));
		assertEquals(interval(-1.1342773492554068, -1.0648918403247922), tuples.valueAt(26));
		assertEquals(interval(-1.2087923504096105, -1.134277349255406), tuples.valueAt(27));
		assertEquals(interval(-1.2891922317850684, -1.2087923504096096), tuples.valueAt(28));
		assertEquals(interval(-1.3763819204711754, -1.2891922317850675), tuples.valueAt(29));
		assertEquals(interval(-1.4714553158199712, -1.3763819204711745), tuples.valueAt(30));
		assertEquals(interval(-1.5757478599686532, -1.4714553158199704), tuples.valueAt(31));
		assertEquals(interval(-1.6909076557850138, -1.5757478599686523), tuples.valueAt(32));
		assertEquals(interval(-1.8189932472810693, -1.690907655785013), tuples.valueAt(33));
		assertEquals(interval(-1.9626105055051541, -1.8189932472810684), tuples.valueAt(34));
		assertEquals(interval(-2.1251081731572072, -1.9626105055051533), tuples.valueAt(35));
		assertEquals(interval(-2.310863653882416, -2.1251081731572055), tuples.valueAt(36));
		assertEquals(interval(-2.5257116894473115, -2.3108636538824143), tuples.valueAt(37));
		assertEquals(interval(-2.7776068539149827, -2.5257116894473097), tuples.valueAt(38));
		assertEquals(interval(-3.077683537175263, -2.777606853914981), tuples.valueAt(39));
		assertEquals(interval(-3.4420225766692307, -3.077683537175261), tuples.valueAt(40));
		assertEquals(interval(-3.8947428549298753, -3.442022576669229), tuples.valueAt(41));
		assertEquals(interval(-4.473742829211576, -3.8947428549298735), tuples.valueAt(42));
		assertEquals(interval(-5.242183581113207, -4.473742829211573), tuples.valueAt(43));
		assertEquals(interval(-6.313751514675088, -5.242183581113204), tuples.valueAt(44));
		assertEquals(interval(-7.915815088305899, -6.313751514675085), tuples.valueAt(45));
		assertEquals(interval(-10.578894993405768, -7.915815088305895), tuples.valueAt(46));
		assertEquals(interval(-15.894544843865608, -10.578894993405761), tuples.valueAt(47));
		assertEquals(interval(-31.82051595377521, -15.894544843865601), tuples.valueAt(48));
		assertEquals(invertedInterval(-1.0000000000000002E-4, 1.0000000000000002E-4), tuples.valueAt(49));
		assertEquals(interval(31.820515953772496, 7.176175255700846E14), tuples.valueAt(50));
		assertEquals(interval(15.894544843864924, 31.82051595377251), tuples.valueAt(51));
		assertEquals(interval(10.57889499340546, 15.894544843864931), tuples.valueAt(52));
		assertEquals(interval(7.915815088305726, 10.578894993405466), tuples.valueAt(53));
		assertEquals(interval(6.313751514674975, 7.915815088305729), tuples.valueAt(54));
		assertEquals(interval(5.242183581113128, 6.313751514674979), tuples.valueAt(55));
		assertEquals(interval(4.473742829211517, 5.242183581113132), tuples.valueAt(56));
		assertEquals(interval(3.8947428549298304, 4.4737428292115204), tuples.valueAt(57));
		assertEquals(interval(3.4420225766691948, 3.894742854929832), tuples.valueAt(58));
		assertEquals(interval(3.0776835371752336, 3.4420225766691965), tuples.valueAt(59));
		assertEquals(interval(2.777606853914958, 3.0776835371752354), tuples.valueAt(60));
		assertEquals(interval(2.52571168944729, 2.7776068539149597), tuples.valueAt(61));
		assertEquals(interval(2.3108636538823975, 2.525711689447292), tuples.valueAt(62));
		assertEquals(interval(2.125108173157191, 2.3108636538823992), tuples.valueAt(63));
		assertEquals(interval(1.9626105055051413, 2.1251081731571926), tuples.valueAt(64));
		assertEquals(interval(1.8189932472810588, 1.9626105055051422), tuples.valueAt(65));
		assertEquals(interval(1.6909076557850051, 1.8189932472810597), tuples.valueAt(66));
		assertEquals(interval(1.575747859968646, 1.690907655785006), tuples.valueAt(67));
		assertEquals(interval(1.471455315819965, 1.575747859968647), tuples.valueAt(68));
		assertEquals(interval(1.3763819204711705, 1.471455315819966), tuples.valueAt(69));
		assertEquals(interval(1.2891922317850641, 1.3763819204711714), tuples.valueAt(70));
		assertEquals(interval(1.2087923504096074, 1.289192231785065), tuples.valueAt(71));
		assertEquals(interval(1.1342773492554041, 1.2087923504096083), tuples.valueAt(72));
		assertEquals(interval(1.064891840324791, 1.134277349255405), tuples.valueAt(73));
		assertEquals(interval(0.9999999999999997, 1.0648918403247918), tuples.valueAt(74));
		assertEquals(interval(0.9390625058174924, 1.0000000000000002), tuples.valueAt(75));
		assertEquals(interval(0.8816185923631895, 0.9390625058174928), tuples.valueAt(76));
		assertEquals(interval(0.8272719459724762, 0.88161859236319), tuples.valueAt(77));
		assertEquals(interval(0.775679511049614, 0.8272719459724767), tuples.valueAt(78));
		assertEquals(interval(0.726542528005362, 0.7756795110496144), tuples.valueAt(79));
		assertEquals(interval(0.6795992982245278, 0.7265425280053625), tuples.valueAt(80));
		assertEquals(interval(0.6346192975441496, 0.6795992982245282), tuples.valueAt(81));
		assertEquals(interval(0.5913983513994727, 0.63461929754415), tuples.valueAt(82));
		assertEquals(interval(0.5497546521927719, 0.5913983513994732), tuples.valueAt(83));
		assertEquals(interval(0.5095254494944308, 0.5497546521927723), tuples.valueAt(84));
		assertEquals(interval(0.47056428121225374, 0.5095254494944312), tuples.valueAt(85));
		assertEquals(interval(0.4327386422474283, 0.47056428121225397), tuples.valueAt(86));
		assertEquals(interval(0.39592800879772383, 0.43273864224742853), tuples.valueAt(87));
		assertEquals(interval(0.3600221530957593, 0.39592800879772405), tuples.valueAt(88));
		assertEquals(interval(0.32491969623290917, 0.3600221530957595), tuples.valueAt(89));
		assertEquals(interval(0.2905268567319194, 0.3249196962329094), tuples.valueAt(90));
		assertEquals(interval(0.25675636036772986, 0.29052685673191964), tuples.valueAt(91));
		assertEquals(interval(0.2235264828971524, 0.2567563603677301), tuples.valueAt(92));
		assertEquals(interval(0.19076020221857018, 0.22352648289715252), tuples.valueAt(93));
		assertEquals(interval(0.15838444032453988, 0.1907602022185703), tuples.valueAt(94));
		assertEquals(interval(0.12632937844611192, 0.15838444032454), tuples.valueAt(95));
		assertEquals(interval(0.09452783117928595, 0.12632937844611203), tuples.valueAt(96));
		assertEquals(interval(0.06291466725365383, 0.09452783117928601), tuples.valueAt(97));
		assertEquals(interval(0.0314262660433554, 0.06291466725365388), tuples.valueAt(98));
		assertEquals(interval(4.4408920985006246E-15, 0.031426266043355425), tuples.valueAt(99));
		assertEquals(interval(-0.031426266043346536, 4.440892098500628E-15), tuples.valueAt(100));
	}

	@Test
	public void minusTanXHiRes() {
		for (IntervalTuple tuple: hiResFunction("-tan(x)")) {
			assertEquals(IntervalConstants.whole().invert(), tuple.y());
		}
	}
}
