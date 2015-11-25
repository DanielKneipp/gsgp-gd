/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.data;

import edu.gsgp.Utils.DataType;

/**
 *
 * @author luiz
 */
public class ExperimentalData {
    private Dataset training;
    private Dataset test;

    public ExperimentalData(){
        training = new Dataset();
        test = new Dataset();
    }

    public ExperimentalData(Dataset training, Dataset test) {
        this.training = training;
        this.test = test;
    }    
    
    /**
     * Return the dataset given by the dataType parameter
     * @param dataType Indicate what dataset to return
     * @return The selected dataset
     */
    public Dataset getDataset(DataType dataType){
        switch(dataType){
            case TEST:
                return test;
            case TRAINING:
                return training;
            default:
                return null;
        }
    }

    public void setDataset(Dataset dataset, DataType dataType) {
        switch(dataType){
            case TEST:
                test = dataset;
                break;
            case TRAINING:
                training = dataset;
                break;
        }
    }
}