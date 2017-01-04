/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.selector;

import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;
import edu.gsgp.utils.Statistics;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public interface IndividualSelector {
    public Individual selectIndividual(Population population, Individual individual, MersenneTwister rnd, ExperimentalData expData, Statistics stats);
}
