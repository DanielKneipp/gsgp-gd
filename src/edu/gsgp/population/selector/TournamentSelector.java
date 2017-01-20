/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.selector;

import edu.gsgp.experiment.data.ExperimentalData;
import java.util.ArrayList;
import java.util.Arrays;
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
public class TournamentSelector implements IndividualSelector{
    private int tournamentSize;
    private int tournamentBetweennessSize;

    public TournamentSelector(int tournamentSize, int tournamentBetweennessSize) {
        this.tournamentSize = tournamentSize;
        this.tournamentBetweennessSize = tournamentBetweennessSize;
    }
    
    @Override
    public Individual selectIndividual(Population population, Individual individual, MersenneTwister rnd, ExperimentalData expData, Statistics stats) throws NullPointerException{
        int popSize = population.size();
        ArrayList<Integer> indexes = new ArrayList<>();
        for(int i = 0; i < popSize; i++) indexes.add(i);
        Individual[] tournament = new Individual[tournamentSize];
        for(int i = 0; i < tournamentSize; i++){
            tournament[i] = population.get(indexes.remove(rnd.nextInt(indexes.size())));
        }
        Arrays.sort(tournament);
        return tournament[0];
    }
    
    public Individual[] selectIndividuals(Population population, Individual individual, MersenneTwister rnd, ExperimentalData expData, Statistics stats) {
        Individual[] individuals = new Individual[this.tournamentBetweennessSize];
        for (int i = 0; i < this.tournamentBetweennessSize; i++) {
            individuals[i] = this.selectIndividual(population, individual, rnd, expData, stats);
        }
        return individuals;
    }
}
