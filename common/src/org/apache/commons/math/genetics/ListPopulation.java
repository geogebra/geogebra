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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math.exception.NotPositiveException;
import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Population of chromosomes represented by a {@link List}.
 *
 * @since 2.0
 * @version $Revision: 983921 $ $Date: 2010-08-10 12:46:06 +0200 (mar. 10 août 2010) $
 */
public abstract class ListPopulation implements Population {

    /** List of chromosomes */
    private List<Chromosome> chromosomes;

    /** maximial size of the population */
    private int populationLimit;


    /**
     * Creates a new ListPopulation instance.
     *
     * @param chromosomes list of chromosomes in the population
     * @param populationLimit maximal size of the population
     */
    public ListPopulation (List<Chromosome> chromosomes, int populationLimit) {
        if (chromosomes.size() > populationLimit) {
            throw new NumberIsTooLargeException(LocalizedFormats.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE,
                                                chromosomes.size(), populationLimit, false);
        }
        if (populationLimit < 0) {
            throw new NotPositiveException(LocalizedFormats.POPULATION_LIMIT_NOT_POSITIVE, populationLimit);
        }

        this.chromosomes = chromosomes;
        this.populationLimit = populationLimit;
    }

    /**
     * Creates a new ListPopulation instance and initializes its inner
     * chromosome list.
     *
     * @param populationLimit maximal size of the population
     */
    public ListPopulation (int populationLimit) {
        if (populationLimit < 0) {
            throw new NotPositiveException(LocalizedFormats.POPULATION_LIMIT_NOT_POSITIVE, populationLimit);
        }
        this.populationLimit = populationLimit;
        this.chromosomes = new ArrayList<Chromosome>(populationLimit);
    }

    /**
     * Sets the list of chromosomes.
     * @param chromosomes the list of chromosomes
     */
    public void setChromosomes(List<Chromosome> chromosomes) {
        this.chromosomes = chromosomes;
    }

    /**
     * Access the list of chromosomes.
     * @return the list of chromosomes
     */
    public List<Chromosome> getChromosomes() {
        return chromosomes;
    }

    /**
     * Add the given chromosome to the population.
     * @param chromosome the chromosome to add.
     */
    public void addChromosome(Chromosome chromosome) {
        this.chromosomes.add(chromosome);
    }

    /**
     * Access the fittest chromosome in this population.
     * @return the fittest chromosome.
     */
    public Chromosome getFittestChromosome() {
        // best so far
        Chromosome bestChromosome = this.chromosomes.get(0);
        for (Chromosome chromosome : this.chromosomes) {
            if (chromosome.compareTo(bestChromosome) > 0) {
                // better chromosome found
                bestChromosome = chromosome;
            }
        }
        return bestChromosome;
    }

    /**
     * Access the maximum population size.
     * @return the maximum population size.
     */
    public int getPopulationLimit() {
        return this.populationLimit;
    }

    /**
     * Sets the maximal population size.
     * @param populationLimit maximal population size.
     */
    public void setPopulationLimit(int populationLimit) {
        this.populationLimit = populationLimit;
    }

    /**
     * Access the current population size.
     * @return the current population size.
     */
    public int getPopulationSize() {
        return this.chromosomes.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.chromosomes.toString();
    }

    /**
     * Chromosome list iterator
     *
     * @return chromosome iterator
     */
    public Iterator<Chromosome> iterator() {
        return chromosomes.iterator();
    }
}
