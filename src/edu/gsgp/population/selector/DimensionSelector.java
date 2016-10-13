/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsgp.population.selector;

import edu.gsgp.experiment.data.ExperimentalData;
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
        int index = identifyCloserIndividual(population, individual, outputs);
        return population.get(index);
    }

    private int compare(int index1, int index2, int[] dimensions) {
        if(dimensions[index1] > dimensions[index2]){
            return index1;
        }
        return index2;
    }

    private int identifyCloserIndividual(Population population, Individual individual, double[] outputs) {
        int[] dimensions = new int[population.size()];
        int index = 0;
        int bestIndex = 0;
        for (Individual ind : population) {
            for (int i = 0; i < outputs.length; i++) {
                dimensions[index] = (((individual.getFitnessFunction().getSemantics(Utils.DatasetType.TRAINING)[i] < outputs[i]) && (outputs[i] < ind.getFitnessFunction().getSemantics(Utils.DatasetType.TRAINING)[i]))) ? dimensions[index] + 1: dimensions[index];
            }
            bestIndex = compare(index, bestIndex, dimensions);
            index++;
        }
        return bestIndex;
    }

}
