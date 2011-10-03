/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.genetics;

import java.util.Collections;
import java.util.List;

import org.apache.commons.math.util.FastMath;

/**
 * Population of chromosomes which uses elitism (certain percentace of the best
 * chromosomes is directly copied to the next generation).
 *
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 * @since 2.0
 */
public class ElitisticListPopulation extends ListPopulation {

    /** percentage of chromosomes copied to the next generation */
    private double elitismRate = 0.9;

    /**
     * Creates a new ElitisticListPopulation instance.
     *
     * @param chromosomes
     *            list of chromosomes in the population
     * @param populationLimit
     *            maximal size of the population
     * @param elitismRate
     *            how many best chromosomes will be directly transferred to the
     *            next generation [in %]
     */
    public ElitisticListPopulation(List<Chromosome> chromosomes, int populationLimit, double elitismRate) {
        super(chromosomes, populationLimit);
        this.elitismRate = elitismRate;
    }

    /**
     * Creates a new ListPopulation instance and initializes its inner
     * chromosome list.
     *
     * @param populationLimit maximal size of the population
     * @param elitismRate
     *            how many best chromosomes will be directly transferred to the
     *            next generation [in %]
     */
    public ElitisticListPopulation(int populationLimit, double elitismRate) {
        super(populationLimit);
        this.elitismRate = elitismRate;
    }

    /**
     * Start the population for the next generation. The
     * <code>{@link #elitismRate}<code> percents of the best
     * chromosomes are directly copied to the next generation.
     *
     * @return the beginnings of the next generation.
     */
    public Population nextGeneration() {
        // initialize a new generation with the same parameters
        ElitisticListPopulation nextGeneration = new ElitisticListPopulation(this.getPopulationLimit(), this.getElitismRate());

        List<Chromosome> oldChromosomes = this.getChromosomes();
        Collections.sort(oldChromosomes);

        // index of the last "not good enough" chromosome
        int boundIndex = (int) FastMath.ceil((1.0 - this.getElitismRate()) * oldChromosomes.size());
        for (int i=boundIndex; i<oldChromosomes.size(); i++) {
            nextGeneration.addChromosome(oldChromosomes.get(i));
        }
        return nextGeneration;
    }

    /**
     * Sets the elitism rate, i.e. how many best chromosomes will be directly
     * transferred to the next generation [in %].
     *
     * @param elitismRate
     *            how many best chromosomes will be directly transferred to the
     *            next generation [in %]
     */
    public void setElitismRate(double elitismRate) {
        if (elitismRate < 0 || elitismRate > 1)
            throw new IllegalArgumentException("Elitism rate has to be in [0,1]");
        this.elitismRate = elitismRate;
    }

    /**
     * Access the elitism rate.
     * @return the elitism rate
     */
    public double getElitismRate() {
        return this.elitismRate;
    }

}
