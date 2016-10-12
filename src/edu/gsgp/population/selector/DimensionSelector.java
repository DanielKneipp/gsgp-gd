/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsgp.population.selector;

import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.nodes.Node;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;
import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.utils.Utils;

/**
 *
 * @author bruno
 */
public class DimensionSelector implements IndividualSelector {

    public DimensionSelector() {

    }

    @Override
    public Individual selectIndividual(Population population, Individual individual, MersenneTwister rnd, ExperimentalData expData) {
        double[] outputs = expData.getDataset(Utils.DatasetType.TRAINING).getOutputs();
        int[] dimensions = calculateDimension(population, individual, outputs);
        int index = identifyCloserIndividual(dimensions);
        return population.get(index);
    }

    private int identifyCloserIndividual(int[] dimensions) {
        int bestIndex = 0;
        for (int i = 1; i < dimensions.length; i++) {
            if (dimensions[i] > dimensions[bestIndex]) {
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    private int[] calculateDimension(Population population, Individual individual, double[] outputs) {
        int[] dimensions = new int[population.size()];
        int index = 0;
        for (Individual ind : population) {
            for (int i = 0; i < outputs.length; i++) {
                if(((individual.getFitnessFunction().getSemantics(Utils.DatasetType.TRAINING)[i] < outputs[i]) && (outputs[i] < ind.getFitnessFunction().getSemantics(Utils.DatasetType.TRAINING)[i]))){
                    dimensions[index] += 1;
                }
            }
            index++;
        }
        return dimensions;
    }

}
