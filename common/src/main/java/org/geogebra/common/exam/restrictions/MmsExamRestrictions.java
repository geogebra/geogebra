package org.geogebra.common.exam.restrictions;

// TODO uncomment with introduction of MMS exam type
//final class MmsExamRestrictions extends ExamRestrictions {
//
//	MmsExamRestrictions() {
//		super(ExamRegion.MMS,
//				Set.of(SuiteSubApp.GRAPHING, SuiteSubApp.GEOMETRY, SuiteSubApp.G3D,
//						SuiteSubApp.PROBABILITY),
//				SuiteSubApp.CAS,
//				null,
//				MmsExamRestrictions.createCommandFilters(),
//				null,
//				null);
//	}
//
//	private static Set<CommandFilter> createCommandFilters() {
//		NameCommandFilter nameFilter = new NameCommandFilter(true);
//		nameFilter.addCommands(Commands.Plane);
//		return Set.of(new EnglishCommandFilter(nameFilter));
//	}
//}