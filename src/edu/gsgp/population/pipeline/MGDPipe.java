/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsgp.population.pipeline;

import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;
import edu.gsgp.population.operator.Breeder;
import edu.gsgp.population.operator.MGDBreeder;
import edu.gsgp.utils.Statistics;
import edu.gsgp.utils.StatisticsDimension;

/**
 *
 * @author luiz
 */
public class MGDPipe extends Pipeline{    
    private int currentGen;

    public MGDPipe() {
        currentGen = 0;
    }
    
    @Override
    public Population evolvePopulation(Population originalPop, ExperimentalData expData, int size) {
//        StatisticsDimension statistics = StatisticsDimension.getInstance();
        MGDBreeder spreader = new MGDBreeder(properties, 0.0);
        spreader.setup(originalPop, expData, currentGen++);
        Population newPopulation = new Population();
        for(int i = 0; i < originalPop.size(); i++){
            double floatDice = rndGenerator.nextDouble();
            if(floatDice < spreader.getEffectiveProb()){
                Individual ind = (Individual)originalPop.get(i);
                originalPop.set(i, spreader.generateIndividual(rndGenerator, expData, (Individual)ind));
            }
        }
        
        // ======================= ADDED FOR GECCO PAPER =======================
//        stats.storeDistInfo(originalPop);
        // =====================================================================
        
        // Update the breeder with the current population before generating a new one
        for(Breeder breeder : breederArray) ((Breeder)breeder).setup(originalPop, expData);     
        
        // Generate the new population from the original one
        for(int i = 0; i < size; i++){
            double floatDice = rndGenerator.nextDouble();
            double probabilitySum = 0;
            Breeder selectedBreeder = breederArray[0];
            for (Breeder breeder : breederArray) {
                if (floatDice < probabilitySum + breeder.getProbability()) {
                    selectedBreeder = breeder;
                    break;
                }
                probabilitySum += breeder.getProbability();
            }
            Individual newInd = selectedBreeder.generateIndividual(rndGenerator, expData, this.stats);
//            floatDice = rndGenerator.nextDouble();
//            if(floatDice < spreader.getEffectiveProb()){
//                newInd = spreader.generateIndividual(rndGenerator, expData);
//            }
            newPopulation.add(newInd);
//            statistics.clear();
        }        
        return newPopulation;
    }

    @Override
    public Pipeline softClone() {
        return new MGDPipe();
    }
}
