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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bruno
 */
public class BetweennessSelector implements IndividualSelector {

    private List<Integer> indexIndividuals;
    private int bigger;

    public BetweennessSelector() {

    }

    @Override
    public Individual selectIndividual(Population population, Individual individual, MersenneTwister rnd, ExperimentalData expData) {
        this.indexIndividuals = new ArrayList<>();
        this.bigger = -1;
        double[] outputs = expData.getDataset(Utils.DatasetType.TRAINING).getOutputs();
        int index = identifyCloserIndividual(population, individual, outputs);
        return population.get(index);
    }

//    private int compare(int index1, int index2, int[] dimensions) {
    private void compare(int index1, int[] dimensions) {
        if (this.bigger < dimensions[index1]) {
            this.indexIndividuals.clear();
            this.indexIndividuals.add(index1);
            this.bigger = dimensions[index1];
        } else if (this.bigger == dimensions[index1]) {
            this.indexIndividuals.add(index1);
        }
//        if(dimensions[index1] > dimensions[index2]){
//            return index1;
//        }
//        return index2;
    }

    private int identifyCloserIndividual(Population population, Individual individual, double[] outputs) {
        int[] dimensions = new int[population.size()];
        int index = 0;
        for (Individual ind : population) {
            for (int i = 0; i < outputs.length; i++) {
                double fitnessSemantic1 = individual.getFitnessFunction().getSemantics(Utils.DatasetType.TRAINING)[i];
                double fitnessSemantic2 = ind.getFitnessFunction().getSemantics(Utils.DatasetType.TRAINING)[i];
                dimensions[index] = (((fitnessSemantic1 < outputs[i]) && (outputs[i] < fitnessSemantic2)) || ((fitnessSemantic2 < outputs[i]) && (outputs[i] < fitnessSemantic1))) ? dimensions[index] + 1 : dimensions[index];
            }
//            bestIndex = compare(index, bestIndex, dimensions);
            compare(index, dimensions);
            index++;
        }
        int indexIndividual = (int) (Math.random() * this.indexIndividuals.size());
        System.out.println("Index selected: " + indexIndividual + " | " + "Value selected: " + indexIndividuals.get(indexIndividual) + " | " + 
        "Size of list: " + indexIndividuals.size() + " | " + "First value of list: " + indexIndividuals.get(0) + " | " + "Last value of list: " + 
        indexIndividuals.get(indexIndividuals.size() - 1));
        return this.indexIndividuals.get(indexIndividual);
    }

}
