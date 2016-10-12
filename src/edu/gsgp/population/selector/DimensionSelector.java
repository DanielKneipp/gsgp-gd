/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsgp.population.selector;

import edu.gsgp.nodes.Node;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;
import edu.gsgp.utils.MersenneTwister;

/**
 *
 * @author bruno
 */
public class DimensionSelector implements IndividualSelector{

    public DimensionSelector(){
        
    }
    
    @Override
    public Individual selectIndividual(Population population, Individual individual, MersenneTwister rnd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
//    private void calculeResult(Node node){
//        
//    }
    
}
