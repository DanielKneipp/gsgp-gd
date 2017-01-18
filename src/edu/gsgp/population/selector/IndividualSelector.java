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
public abstract class IndividualSelector {
    public abstract Individual selectIndividual(Population population, Individual individual, MersenneTwister rnd, ExperimentalData expData, Statistics stats);

    public Individual[] selectIndividuals(Population population, Individual individual, MersenneTwister rnd, ExperimentalData expData, Statistics stats, int numIndividuals) {
        Individual[] individuals = new Individual[numIndividuals];
        for (int i = 0; i < numIndividuals; i++) {
            individuals[i] = this.selectIndividual(population, individual, rnd, expData, stats);
        }
        return individuals;
    }
}
