/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.utils;

import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.population.Population;
import edu.gsgp.population.Individual;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Statistics {
    
    public enum StatsType{
        BEST_OF_GEN_SIZE("individualSize.csv"), 
        SEMANTICS("outputs.csv"),
        BEST_OF_GEN_TS_FIT("tsFitness.csv"), 
        BEST_OF_GEN_TR_FIT("trFitness.csv"),
        ELAPSED_TIME("elapsedTime.csv"),
        LOADED_PARAMETERS("loadedParams.txt"),
        MDD_AVG("mddAverage.csv"),
        MDD_SD("mddStdDev.csv"),
        DIM_TOURNAMENT("dimensionTournament.csv"),
        DIM_BETWEENNESS("dimensionBetweenness.csv");
        
        private final String filePath;

        private StatsType(String filePath) {
            this.filePath = filePath;
        }
        
        public String getPath(){
            return filePath;
        }
    }

    public enum StatsTypeDimension{
        DIM_TOURNAMENT("dimensionTournament.csv"),
        DIM_BETWEENNESS("dimensionBetweenness.csv");

        private final String filePath;

        private StatsTypeDimension(String filePath) {
            this.filePath = filePath;
        }

        public String getPath(){
            return filePath;
        }
    }
    
    protected ExperimentalData expData;
    
    protected float elapsedTime;
    protected String[] bestOfGenSize;
    protected String[] bestOfGenTsFitness;
    protected String[] bestOfGenTrFitness;
    
    protected float[] meanMDD;
    protected float[] sdMDD;
    
    private double[] bestTrainingSemantics;
    private double[] bestTestSemantics;

    protected String dimTournament;
    protected String dimBetweenness;

    protected List<String> dimensionTournament;
    protected List<String> dimensionBetweenness;
        
    protected int currentGeneration;

    public int getCurrentRepetition() {
        return currentRepetition;
    }

    public void setCurrentRepetition(int currentRepetition) {
        this.currentRepetition = currentRepetition;
    }

    protected int currentRepetition;
    // ========================= ADDED FOR GECCO PAPER =========================
//    private ArrayList<int[]> trGeTarget;
//    private ArrayList<int[]> tsGeTarget;
    // =========================================================================
    
    public Statistics(int numGenerations, ExperimentalData expData) {
        bestOfGenSize = new String[numGenerations+1];
        bestOfGenTsFitness = new String[numGenerations+1];
        bestOfGenTrFitness = new String[numGenerations+1];
        meanMDD = new float[numGenerations+1];
        sdMDD = new float[numGenerations+1];
        currentGeneration = 0;
        this.expData = expData;
        dimensionTournament = new ArrayList<>();
        dimensionBetweenness = new ArrayList<>();
        this.dimTournament = "";
        this.dimBetweenness = "";
        this.currentRepetition = 0;
        
        // ======================= ADDED FOR GECCO PAPER =======================
//        trGeTarget = new ArrayList<>();
//        tsGeTarget = new ArrayList<>();
        // =====================================================================
    }
    
    // ========================= ADDED FOR GECCO PAPER =========================
//    public void storeDristInfo(Population pop){
//        
//        if(currentGeneration > 0 && (currentGeneration-1) % 10 == 0){
//        
//            int[] tsGE = new int[expData.getDataset(Utils.DatasetType.TEST).size()];
//            int[] trGE = new int[expData.getDataset(Utils.DatasetType.TRAINING).size()];
//            for(Individual ind : pop){
//                double[] tsSem = ind.getTestSemantics();
//                double[] trSem = ind.getTrainingSemantics();
//                for(int i = 0; i < tsSem.length; i++){
//                    if(tsSem[i] >= expData.getDataset(Utils.DatasetType.TEST).getOutputs()[i])
//                        tsGE[i]++;
//                }
//                for(int i = 0; i < trSem.length; i++){
//                    if(trSem[i] >= expData.getDataset(Utils.DatasetType.TRAINING).getOutputs()[i])
//                        trGE[i]++;
//                }
//            }
//
//            tsGeTarget.add(tsGE);
//            trGeTarget.add(trGE);
//            
//        }
//    }
    // =========================================================================
    
    /**
     * Update the statistics with information obtained in the end of the generation
     * @param pop Current population
     */
    public void addGenerationStatistic(Population pop){        
        // In order to not contabilize the time elapsed by this method we subtract
        // the time elapsed
        long methodTime = System.nanoTime();
        
        Individual bestOfGen = pop.getBestIndividual();
        
        bestOfGenSize[currentGeneration] = bestOfGen.getNumNodesAsString();
        bestOfGenTrFitness[currentGeneration] = bestOfGen.getTrainingFitnessAsString();
        bestOfGenTsFitness[currentGeneration] = bestOfGen.getTestFitnessAsString();
        
        computeMDD(pop);
        
        System.out.println("Best of Gen (RMSE-TR/RMSE-TS/nodes: " + bestOfGenTrFitness[currentGeneration] + 
                           "/" + bestOfGenTsFitness[currentGeneration] + "/" + bestOfGenSize[currentGeneration]);
        currentGeneration++;
        
        // Ignore the time elapsed to store the statistics
        elapsedTime += System.nanoTime() - methodTime;
    }
    
    public void finishEvolution(Individual bestIndividual) {
        elapsedTime = System.nanoTime() - elapsedTime;
        // Convert nanosecs to secs
        elapsedTime /= 1000000000;
        
        bestTestSemantics = ((Individual)bestIndividual).getTestSemantics();
        bestTrainingSemantics = bestIndividual.getTrainingSemantics();
    }
    
    public String asWritableString(StatsType type) {
        switch(type){
            case BEST_OF_GEN_SIZE:
                return concatenateArray(bestOfGenSize);
            case BEST_OF_GEN_TR_FIT:
                return concatenateArray(bestOfGenTrFitness);
            case SEMANTICS:
                return getSemanticsAsString();
            case BEST_OF_GEN_TS_FIT:
                return concatenateArray(bestOfGenTsFitness);
            case ELAPSED_TIME:
                return elapsedTime + "";
            case MDD_AVG:
                return concatenateFloatArray(meanMDD);
            case MDD_SD:
                return concatenateFloatArray(sdMDD);
            default:
                return null;
        }
    }

    public void genDimsStatsStr(StatsTypeDimension type) {
        switch(type){
            case DIM_TOURNAMENT:
                this.dimTournament += concatenateArrayDimInfo(dimensionTournament);
                break;
            case DIM_BETWEENNESS:
                this.dimBetweenness += concatenateArrayDimInfo(dimensionBetweenness);
                break;
            default:
                System.out.println("There isn't this type!!!");
                break;
        }
    }
    
    private String concatenateArray(String[] stringArray){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < stringArray.length-1; i++){
            str.append(stringArray[i] + ",");
        }
        str.append(stringArray[stringArray.length-1]);        
        return str.toString();
    }

    private String concatenateArrayDimInfo(List<String> stringArray){
        StringBuilder str = new StringBuilder();
        if (stringArray.isEmpty()) {
            stringArray.add(String.valueOf(this.currentRepetition));
            stringArray.add(String.valueOf(this.currentGeneration));
        }
        for (String strArray : stringArray) {
            str.append(strArray).append(",");
        }
        str.replace((str.length() - 1), str.length(), "");
        str.append("\n");
        return str.toString();
    }
    
    private String concatenateFloatArray(float[] floatArray) {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < floatArray.length-1; i++){
            str.append(Utils.format(floatArray[i]) + ",");
        }
        str.append(Utils.format(floatArray[floatArray.length-1]));        
        return str.toString();
    }
        
    private String getSemanticsAsString() {
        StringBuffer str = new StringBuffer();
        // ======================= ADDED FOR GECCO PAPER =======================
//        for(int[] trGE : trGeTarget){
//            for(int i = 0; i < trGE.length; i++){
//                str.append(trGE[i] + ",");
//            }
//        
//        }
        // =====================================================================
        
        for(int i = 0; i < bestTrainingSemantics.length; i++){
            str.append(bestTrainingSemantics[i] + ",");
        }
        
        String sep = "";
        // ======================= ADDED FOR GECCO PAPER =======================
//        for(int[] tsGE : tsGeTarget){
//            for(int i = 0; i < tsGE.length; i++){
//                str.append(sep + tsGE[i]);
//                sep = ",";
//            }
//        }
        // =====================================================================
            
        for(int i = 0; i < bestTestSemantics.length; i++){
            str.append(sep + bestTestSemantics[i]);
            sep = ",";
        }
        
        return str.toString();
    }
    
    public void startClock(){
        elapsedTime = System.nanoTime();
    }
    
    private void computeMDD(Population pop) {
        // Target vector
        double[] t = expData.getDataset(Utils.DatasetType.TRAINING).getOutputs();
        // Store the counting of individuals greater or equal to the target in each dimension
        float[] ge = new float[t.length];
        for(Individual ind : pop){
            for(int i = 0; i < ge.length; i++){
                if(ind.getTrainingSemantics()[i] >= t[i]){
                    ge[i]++;
                }
            }
        }
        float mean = 0;
        double sd = 0;
        for(int i = 0; i < ge.length; i++){
            ge[i] /= pop.size();
            ge[i] = (float)Math.abs(ge[i]-0.5);
            mean += ge[i];
        }
        mean /= (ge.length);
        for(int i = 0; i < ge.length; i++){
            double aux = ge[i] - mean;
            sd += aux*aux;
        }
        sd /= ge.length-1;
        sd = Math.sqrt(sd);
        meanMDD[currentGeneration] = mean;
        sdMDD[currentGeneration] = (float)sd;
    }

    public void addInfoTournament(String value){
        if (this.dimensionTournament.isEmpty()) {
            this.dimensionTournament.add(String.valueOf(this.currentRepetition));
            this.dimensionTournament.add(String.valueOf(this.currentGeneration));
        }
        this.dimensionTournament.add(value);
    }

    public void addInfoBetweenness(String value){
        if (this.dimensionBetweenness.isEmpty()) {
            this.dimensionBetweenness.add(String.valueOf(this.currentRepetition));
            this.dimensionBetweenness.add(String.valueOf(this.currentGeneration));
        }
        this.dimensionBetweenness.add(value);
    }

    public void updateInfoTournament(String value, int index){
        this.dimensionTournament.set(index, value);
    }

    public void updateInfoBetweenness(String value, int index){
        this.dimensionBetweenness.set(index, value);
    }

    public void clearDimsStats(){
        this.dimensionTournament.clear();
        this.dimensionBetweenness.clear();
    }

    public void clearDimsStrs() {
        this.dimTournament = "";
        this.dimBetweenness = "";
    }

    public boolean isDimmensionEmpty(StatsTypeDimension type){
        switch(type){
            case DIM_TOURNAMENT:
                return this.dimensionTournament.isEmpty();
            case DIM_BETWEENNESS:
                return this.dimensionBetweenness.isEmpty();
            default:
                return false;
        }
    }

    public boolean isDimmensionStrEmty(StatsTypeDimension type){
        switch(type){
            case DIM_TOURNAMENT:
                return Objects.equals(this.dimTournament, "");
            case DIM_BETWEENNESS:
                return Objects.equals(this.dimBetweenness, "");
            default:
                return false;
        }
    }

    public void writeInfoDimension(String outputPath,
                                   String outputPrefix) throws Exception {

        StatsTypeDimension writeableStats[] = {StatsTypeDimension.DIM_TOURNAMENT,
                StatsTypeDimension.DIM_BETWEENNESS};

        for (StatsTypeDimension type : writeableStats) {
            if (!isDimmensionStrEmty(type)) {
                writeOnFile(outputPath, outputPrefix, getDimsStatsString(type) + "\n", type);
            }
        }
    }

    private String getDimsStatsString(StatsTypeDimension type) {
        switch(type){
            case DIM_TOURNAMENT:
                return this.dimTournament;
            case DIM_BETWEENNESS:
                return this.dimBetweenness;
            default:
                return null;
        }
    }

    private void writeOnFile(String outputPath,
                             String outputPrefix,
                             String info,
                             StatsTypeDimension statsType) throws NullPointerException, SecurityException, IOException {
        File outputDir = getOutputDir(outputPath);
        outputDir = new File(outputDir.getAbsolutePath() + File.separator + outputPrefix);
        outputDir.mkdirs();
        // Object to write results on file
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath() + File.separator + statsType.getPath(), true));
        bw.write(info);
        bw.close();
    }

    protected static File getOutputDir(String outputPath){
        File outputDir;
        if(!outputPath.equals("")){
            outputDir = new File(outputPath);
        }
        else{
            outputDir = new File(System.getProperty("user.dir"));
        }
        return outputDir;
    }
}
