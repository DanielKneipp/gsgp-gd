/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.builder.individual;

import edu.gsgp.MersenneTwister;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.population.Population;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 * 
 * Populator methods are responsible for the population initialization
 */
public abstract class Populator extends IndividualBuilder{
    public Populator(PropertiesManager properties, ExperimentalData expData) {
        super(properties, expData);
    }
    
    public abstract Population populate(MersenneTwister rndGenerator, int size);
    
}
