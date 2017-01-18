/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp;

import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.utils.Statistics;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.population.Population;
import edu.gsgp.population.Individual;
import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.population.populator.Populator;
import edu.gsgp.population.pipeline.Pipeline;
import edu.gsgp.utils.StatisticsDimension;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class GSGP {
    private final PropertiesManager properties;
    private final Statistics statistics;
//    private final StatisticsDimension statisticsDim;
    private final ExperimentalData expData;
    private final MersenneTwister rndGenerator;
    private final int currentExec;

    public GSGP(PropertiesManager properties, ExperimentalData expData, int execution) throws Exception{
        this.properties = properties;
        this.expData = expData;
        this.currentExec = execution;
        statistics = new Statistics(properties.getNumGenerations(), expData);
        statistics.setCurrentRepetition(execution);
//        statisticsDim = new StatisticsDimension();
        rndGenerator = properties.getRandomGenerator();
    }
    
    public void evolve() throws Exception{
        boolean canStop = false;     
        
        Populator populator = properties.getPopulationInitializer();
        Pipeline pipe = properties.getPipeline();
        
        statistics.startClock();
        
        Population population = populator.populate(rndGenerator, expData, properties.getPopulationSize());
        pipe.setup(properties, statistics, expData, rndGenerator);
        
        statistics.addGenerationStatistic(population);
//        statisticsDim.setCurrentExp(this.currentExec);
        for(int i = 0; i < properties.getNumGenerations() && !canStop; i++){
            System.out.println("Generation " + (i+1) + ":");
//            statisticsDim.setCurrentGen(i);
                        
            // Evolve a new Population
            Population newPopulation = pipe.evolvePopulation(population, expData, properties.getPopulationSize()-1);
            // The first position is reserved for the best of the generation (elitism)
            newPopulation.add(population.getBestIndividual());
            Individual bestIndividual = newPopulation.getBestIndividual();
            if(bestIndividual.isBestSolution(properties.getMinError())) canStop = true;
            population = newPopulation;

            statistics.genDimsStatsStr(Statistics.StatsTypeDimension.DIM_BETWEENNESS);
            statistics.genDimsStatsStr(Statistics.StatsTypeDimension.DIM_TOURNAMENT);
            statistics.clearDimsStats();
            statistics.addGenerationStatistic(population);
//            statisticsDim.incrementGeneration();
        }
//        statisticsDim.writeInfoDimension(properties.getOutputDir(), properties.getFilePrefix());
        statistics.finishEvolution(population.getBestIndividual());
    }

    public Statistics getStatistics() {
        return statistics;
    }
}
