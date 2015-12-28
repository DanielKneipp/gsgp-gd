/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp;

import edu.gsgp.data.DataProducer;
import edu.gsgp.data.DataWriter;
import edu.gsgp.data.PropertiesManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Experimenter {    
    protected PropertiesManager parameters;
    
    protected DataProducer dataProducer;
    
     public Experimenter(String[] args) throws Exception{
        parameters = new PropertiesManager(args);
        if(parameters.isParameterLoaded())
            execute();
    }
    
    private void execute(){
        try {
            Experiment experiments[] = new Experiment[parameters.getNumExperiments()];
            int numThreads = Math.min(parameters.getNumThreads(), parameters.getNumExperiments());
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
//            DataWriter.resetInitialSemantics(parameters.getOutputDir(), parameters.getFilePrefix());
            
            // Run the algorithm for a defined number of repetitions
            for(int execution = 0; execution < parameters.getNumExperiments(); execution++){
//                System.out.println("Execution " + (execution+1) + ":");
                parameters.updateExperimentalData();
                experiments[execution] = new Experiment(new GSGP(parameters, parameters.getExperimentalData()), execution);
                executor.execute(experiments[execution]);
                
//                sgp.evolve();
//                stats[execution] = sgp.getStatistics();
//                DataWriter.writeInitialSemantics(parameters.getOutputDir(), parameters.getFilePrefix(), stats[execution]);
//                stats[execution].resetInitialSemantics();
            }
            executor.shutdown();
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private class Experiment implements Runnable{
        GSGP gsgpInstance;
        int id;

        public Experiment(GSGP gsgpInstance, int id) {
            this.gsgpInstance = gsgpInstance;
            this.id = id;
        }
        
        private synchronized void writeStatistics() throws Exception{
            DataWriter.writeResults(parameters.getOutputDir(), 
                    parameters.getFilePrefix(), 
                    gsgpInstance.getStatistics(), id);
        }
        
        @Override
        public void run() {
            try{
                gsgpInstance.evolve();
                writeStatistics();
                
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
    }
}
