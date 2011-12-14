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
import java.util.List;

/**
 * Mutation for {@link BinaryChromosome}s. Randomly changes one gene.
 *
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 * @since 2.0
 */
public class BinaryMutation implements MutationPolicy {

    /**
     * Mutate the given chromosome. Randomly changes one gene.
     * @param original the original chromosome.
     * @return the mutated chromomsome.
     */
    public Chromosome mutate(Chromosome original) {
        if (!(original instanceof BinaryChromosome)) {
            throw new IllegalArgumentException("Binary mutation works on BinaryChromosome only.");
        }

        BinaryChromosome origChrom = (BinaryChromosome) original;
        List<Integer> newRepr = new ArrayList<Integer>(origChrom.getRepresentation());

        // randomly select a gene
        int geneIndex = GeneticAlgorithm.getRandomGenerator().nextInt(origChrom.getLength());
        // and change it
        newRepr.set(geneIndex, origChrom.getRepresentation().get(geneIndex) == 0 ? 1 : 0);

        Chromosome newChrom = origChrom.newFixedLengthChromosome(newRepr);
        return newChrom;
    }

}
