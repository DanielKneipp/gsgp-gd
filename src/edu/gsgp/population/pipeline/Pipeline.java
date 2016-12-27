/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.pipeline;

import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.utils.Statistics;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.population.Population;
import edu.gsgp.population.operator.Breeder;
import edu.gsgp.utils.StatisticsDimension;

/**
 *
 * @author luiz
 */
public abstract class Pipeline {
    protected PropertiesManager properties;
    protected Statistics stats;
    protected Breeder[] breederArray;
    protected MersenneTwister rndGenerator;
    
    public void setup(PropertiesManager properties, 
                      Statistics stats, 
                      ExperimentalData expData, 
                      MersenneTwister rndGenerator){
        this.properties = properties;
        this.stats = stats;
        this.rndGenerator = rndGenerator;
        breederArray = properties.getBreederList();
    }
    
    public abstract Population evolvePopulation(Population originalPop, ExperimentalData expData, int size);

    public abstract Pipeline softClone();
}
