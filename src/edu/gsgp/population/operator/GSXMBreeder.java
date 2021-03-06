/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.operator;

import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.utils.Statistics;
import edu.gsgp.utils.Utils;
import edu.gsgp.utils.Utils.DatasetType;
import edu.gsgp.experiment.data.Dataset;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.experiment.data.Instance;
import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.nodes.Node;
import edu.gsgp.population.Individual;
import edu.gsgp.population.fitness.Fitness;
import java.math.BigInteger;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class GSXMBreeder extends Breeder{

    public GSXMBreeder(PropertiesManager properties, Double probability) {
        super(properties, probability);
    }
    
    private Fitness evaluate(Individual ind1,
                            Individual ind2, 
                            Node randomTree, 
                            ExperimentalData expData){
        Fitness fitnessFunction = ind1.getFitnessFunction().softClone();
        for(DatasetType dataType : DatasetType.values()){
            // Compute the (training/test) semantics of generated random tree
            fitnessFunction.resetFitness(dataType, expData);
            Dataset dataset = expData.getDataset(dataType);
            double[] semInd1;
            double[] semInd2;
            if(dataType == DatasetType.TRAINING){
                semInd1 = ind1.getTrainingSemantics();
                semInd2 = ind2.getTrainingSemantics();
            }
            else{
                semInd1 = ind1.getTestSemantics();
                semInd2 = ind2.getTestSemantics();
            }
            int instanceIndex = 0;
            for (Instance instance : dataset) {
                double rtValue = Utils.sigmoid(randomTree.eval(instance.input));
//                double estimated = rtValue*ind1.getTrainingSemantics()[instanceIndex] + (1-rtValue)*ind2.getTrainingSemantics()[instanceIndex];
                double estimated = rtValue*semInd1[instanceIndex] + (1-rtValue)*semInd2[instanceIndex];
                fitnessFunction.setSemanticsAtIndex(estimated, instance.output, instanceIndex++, dataType);
            }
            fitnessFunction.computeFitness(dataType);
        }
        return fitnessFunction;
    }

    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator, ExperimentalData expData, Statistics stats) {
        Individual[] individuals = properties.selectIndividuals(originalPopulation, rndGenerator, expData, stats);
//        Individual p1 = (Individual)properties.selectIndividual(originalPopulation, rndGenerator);
//        Individual p2 = (Individual)properties.selectIndividual(originalPopulation, rndGenerator);
        while(individuals[0].equals(individuals[1])) individuals[1] = (Individual)properties.selectIndividual(originalPopulation, rndGenerator, stats);
        Node rt = properties.getRandomTree(rndGenerator);
        BigInteger numNodes = individuals[0].getNumNodes().add(individuals[1].getNumNodes()).add(new BigInteger(rt.getNumNodes() + "")).add(BigInteger.ONE);
        Fitness fitnessFunction = evaluate(individuals[0], individuals[1], rt, expData);
        Individual offspring = new Individual(null, numNodes, fitnessFunction);
        return offspring;
    }

    @Override
    public Breeder softClone(PropertiesManager properties) {
        return new GSXMBreeder(properties, probability);
    }
}
