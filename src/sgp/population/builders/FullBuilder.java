/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package sgp.population.builders;
import sgp.MersenneTwister;
import sgp.nodes.Node;
import sgp.nodes.functions.Function;



public class FullBuilder extends IndividualBuilder {

    public FullBuilder(final int maxDepth, 
                       final int minDepth, 
                       final Function[] functions,
                       final Node[] terminals,
                       final MersenneTwister rnd) {
        super(maxDepth, minDepth, functions, terminals, rnd);
    }

    @Override
    public Node newRootedTree(final int current){
        return fullNode(0, maxDepth);
    }
}