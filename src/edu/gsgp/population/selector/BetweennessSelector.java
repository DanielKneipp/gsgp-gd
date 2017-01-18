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
import edu.gsgp.utils.Statistics;
import edu.gsgp.utils.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bruno
 */
public class BetweennessSelector implements IndividualSelector {

    public BetweennessSelector() {

    }

    @Override
    public Individual selectIndividual(Population population, Individual individual, MersenneTwister rnd, ExperimentalData expData, Statistics stats) {
//        StatisticsDimension statistic = StatisticsDimension.getInstance();
//        statistic.addGeneration(StatisticsDimension.StatsTypeDimension.DIM_BETWEENNESS);
//        statistic.addInfoBetweenness(String.valueOf(-1));
        List<Integer> indexIndividuals = new ArrayList<>();
        Integer bigger = -1;
        double[] outputs = expData.getDataset(Utils.DatasetType.TRAINING).getOutputs();
        int index = identifyCloserIndividual(population, individual, outputs, indexIndividuals, bigger);
        getStatisticsDim(individual, population.get(index), stats, outputs);
        return population.get(index);
    }
    
    private void getStatisticsDim(Individual firstIndividual, Individual secondIndividual, Statistics stats, double[] outputs){
        int numDim = 0;
        for (int i = 0; i < outputs.length; i++) {
            double fitnessSemantic1 = firstIndividual.getFitnessFunction().getSemantics(Utils.DatasetType.TRAINING)[i];
            double fitnessSemantic2 = secondIndividual.getFitnessFunction().getSemantics(Utils.DatasetType.TRAINING)[i];
            if(((fitnessSemantic1 < outputs[i]) && (outputs[i] < fitnessSemantic2)) || ((fitnessSemantic2 < outputs[i]) && (outputs[i] < fitnessSemantic1))){
                numDim++;
//                statistics.addInfoBetweenness(String.valueOf(i));
            }
        }
//        statistics.updateInfoBetweenness(String.valueOf(numDim), 1);
//        statistics.asWritableString(StatisticsDimension.StatsTypeDimension.DIM_BETWEENNESS);
        stats.addInfoBetweenness(String.valueOf(numDim));

    }

//    private int compare(int index1, int index2, int[] dimensions) {
    private void compare(int index1, int[] dimensions, List<Integer> indexIndividuals, Integer bigger) {
        if (bigger < dimensions[index1]) {
            indexIndividuals.clear();
            indexIndividuals.add(index1);
            bigger = dimensions[index1];
        } else if (bigger == dimensions[index1]) {
            indexIndividuals.add(index1);
        }
//        if(dimensions[index1] > dimensions[index2]){
//            return index1;
//        }
//        return index2;
    }

    private int identifyCloserIndividual(Population population, Individual individual, double[] outputs, List<Integer> indexIndividuals, Integer bigger) {
        int[] dimensions = new int[population.size()];
        int index = 0;
        for (Individual ind : population) {
            for (int i = 0; i < outputs.length; i++) {
                double fitnessSemantic1 = individual.getFitnessFunction().getSemantics(Utils.DatasetType.TRAINING)[i];
                double fitnessSemantic2 = ind.getFitnessFunction().getSemantics(Utils.DatasetType.TRAINING)[i];
                dimensions[index] = (((fitnessSemantic1 < outputs[i]) && (outputs[i] < fitnessSemantic2)) || ((fitnessSemantic2 < outputs[i]) && (outputs[i] < fitnessSemantic1))) ? dimensions[index] + 1 : dimensions[index];
            }
//            bestIndex = compare(index, bestIndex, dimensions);
            compare(index, dimensions, indexIndividuals, bigger);
            index++;
        }
        int indexIndividual = (int) (Math.random() * indexIndividuals.size());
        return indexIndividuals.get(indexIndividual);
    }

}
