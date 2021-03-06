/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.operator;

import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.population.Individual;
import edu.gsgp.utils.Statistics;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class ReproductionBreeder extends Breeder {

//    public ReproductionBreeder(PropertiesManager properties, ExperimentalData expData,  Double probability) {
//        super(properties, expData, probability);
//    }
    
    public ReproductionBreeder(PropertiesManager properties, Double probability) {
        super(properties, probability);
    }

    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator, ExperimentalData expData, Statistics stats) {
        return properties.selectIndividual(originalPopulation, rndGenerator, stats).clone();
    }
    
    @Override
    public Breeder softClone(PropertiesManager properties) {
        return new ReproductionBreeder(properties, this.probability);
    }
}
