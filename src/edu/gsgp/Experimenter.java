/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp;




import edu.gsgp.data.ExperimentalData;
import edu.gsgp.data.DataProducer;
import edu.gsgp.data.DataWriter;
import edu.gsgp.data.PropertiesManager;

/**
 *
 * @author luiz
 */
public class Experimenter {    
    protected PropertiesManager parameters;
    
    protected DataProducer dataProducer;
    
    public Experimenter(String[] args) throws Exception{
        parameters = new PropertiesManager(args);
        if(parameters.isParameterLoaded())
            execute();
    }
    
    protected void execute(){
        try {
            Statistics[] stats = new Statistics[parameters.getNumExperiments()];
//            DataWriter.resetInitialSemantics(parameters.getOutputDir(), parameters.getFilePrefix());
            
            // Run the algorithm for a defined number of repetitions
            for(int execution = 0; execution < parameters.getNumExperiments(); execution++){
                System.out.println("Execution " + (execution+1) + ":");
                ExperimentalData data = parameters.getDataProducer().getExperimentDataset();
                GSGP sgp = new GSGP(data, parameters);
                sgp.evolve();
                stats[execution] = sgp.getStatistics();
//                DataWriter.writeInitialSemantics(parameters.getOutputDir(), parameters.getFilePrefix(), stats[execution]);
//                stats[execution].resetInitialSemantics();
            }
            DataWriter.writeResults(parameters.getOutputDir(), parameters.getFilePrefix(), stats);
            DataWriter.writeOutputs(parameters.getOutputDir(), parameters.getFilePrefix(), stats, parameters.getDataProducer().getExperimentDataset());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}