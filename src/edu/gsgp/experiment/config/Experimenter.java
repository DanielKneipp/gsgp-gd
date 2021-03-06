/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.experiment.config;

import edu.gsgp.GSGP;
import edu.gsgp.experiment.data.DataProducer;
import edu.gsgp.experiment.data.DataWriter;
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
    }
    
    public void runExperiment(){
        if(parameters.isParameterLoaded()){
            try {
                Experiment experiments[] = new Experiment[parameters.getNumExperiments()];
                int numThreads = Math.min(parameters.getNumThreads(), parameters.getNumExperiments());
                ExecutorService executor = Executors.newFixedThreadPool(numThreads);

                // Run the algorithm for a defined number of repetitions
                for(int execution = 0; execution < parameters.getNumExperiments(); execution++){
                    parameters.updateExperimentalData();
                    experiments[execution] = new Experiment(new GSGP(parameters, parameters.getExperimentalData(), execution), execution, this);
                    executor.execute(experiments[execution]);
                }
                executor.shutdown();
                executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                DataWriter.writeLoadedParameters(parameters);
            } 
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private synchronized void writeStatistics(GSGP gsgpInstance, int id) throws Exception{
        DataWriter.writeResults(parameters.getOutputDir(),
                parameters.getFilePrefix(),
                gsgpInstance.getStatistics(), id);
        gsgpInstance.getStatistics().writeInfoDimension(parameters.getOutputDir(), parameters.getFilePrefix());
    }
    
    private class Experiment implements Runnable{
        GSGP gsgpInstance;
        int id;
        Experimenter exp;

        public Experiment(GSGP gsgpInstance, int id, Experimenter exp) {
            this.gsgpInstance = gsgpInstance;
            this.id = id;
            this.exp = exp;
        }
        
        @Override
        public void run() {
            try{
                gsgpInstance.evolve();
                exp.writeStatistics(gsgpInstance, id);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                System.exit(0);
            }
        }
        
    }
}
