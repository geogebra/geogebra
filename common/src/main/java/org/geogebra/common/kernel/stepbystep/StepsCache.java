package org.geogebra.common.kernel.stepbystep;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.steps.StepStrategies;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;

import java.util.HashMap;
import java.util.Map;

public class StepsCache {

    private static StepsCache instance;

    private Map<StepNode, Cache> regroupCache;
    private Map<StepNode, Cache> expandCache;
    private Map<StepNode, Cache> factorCache;

    public class Cache {
        private StepNode result;
        private SolutionStep steps;
    }

    private StepsCache() {
        regroupCache = new HashMap<>();
        expandCache = new HashMap<>();
        factorCache = new HashMap<>();
    }

    public StepNode regroup(StepNode sn, SolutionBuilder sb) {
        Cache cache = regroupCache.get(sn);

        if (cache == null) {
            SolutionBuilder tempSteps = new SolutionBuilder();
            cache = new Cache();

            cache.result = StepStrategies.defaultRegroup(sn, tempSteps);
            cache.steps = tempSteps.getSteps();

            regroupCache.put(sn, cache);
        }

        if (sb != null) {
            sb.addAll(cache.steps);
        }
        return cache.result;
    }

    public StepNode expand(StepNode sn, SolutionBuilder sb) {
        Cache cache = expandCache.get(sn);

        if (cache == null) {
            SolutionBuilder tempSteps = new SolutionBuilder();
            cache = new Cache();

            cache.result = StepStrategies.defaultExpand(sn, tempSteps);
            cache.steps = tempSteps.getSteps();

            expandCache.put(sn, cache);
        }

        if (sb != null) {
            sb.addAll(cache.steps);
        }
        return cache.result;
    }

    public StepNode factor(StepNode sn, SolutionBuilder sb) {
        Cache cache = factorCache.get(sn);

        if (cache == null) {
            SolutionBuilder tempSteps = new SolutionBuilder();
            cache = new Cache();

            cache.result = StepStrategies.defaultFactor(sn, tempSteps);
            cache.steps = tempSteps.getSteps();

            factorCache.put(sn, cache);
        }

        if (sb != null) {
            sb.addAll(cache.steps);
        }
        return cache.result;
    }

    public static StepsCache getInstance() {
        if (instance == null) {
            instance = new StepsCache();
        }

        return instance;
    }
}
