/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bruno
 */
public class StatisticsDimension {
    
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
    
    protected String dimTournament;
    protected String dimBetweenness;
    
    protected List<String> dimensionTournament;
    protected List<String> dimensionBetweenness;

    public int getCurrentGen() {
        return currentGen;
    }

    public void setCurrentGen(int currentGen) {
        this.currentGen = currentGen;
    }

    protected int currentGen;

    public int getCurrentExp() {
        return currentExp;
    }

    public void setCurrentExp(int currentExp) {
        this.currentExp = currentExp;
    }

    protected int currentExp;
    private static StatisticsDimension instance;
    
    public StatisticsDimension() {
        dimensionTournament = new ArrayList<>();
        dimensionBetweenness = new ArrayList<>();
        this.dimTournament = new String();
        this.dimBetweenness = new String();
        this.currentGen = 0;
        this.currentExp = 0;
    }
    
    /*public static StatisticsDimension getInstance(){
        if(instance == null){
            instance = new StatisticsDimension(0);
        }
        return instance;
    }*/
    
    public void addInfoTournament(String value){
        this.dimensionTournament.add(value);
    }
    
    public void addInfoBetweenness(String value){
        this.dimensionBetweenness.add(value);
    }
    
    public void updateInfoTournament(String value, int index){
        this.dimensionTournament.set(index, value);
    }
    
    public void updateInfoBetweenness(String value, int index){
        this.dimensionBetweenness.set(index, value);
    }
    
    public void incrementGeneration(){
        this.currentGen++;
    }
    
    public void addGeneration(StatsTypeDimension type){
        switch(type){
            case DIM_TOURNAMENT:
                this.dimensionTournament.add(String.valueOf(this.currentGen));
                break;
            case DIM_BETWEENNESS:
                this.dimensionBetweenness.add(String.valueOf(this.currentGen));
                break;
            default:
                System.out.println("There isn't this type!!!");
                break;
        }
    }
    
    public void clear(){
        this.dimensionTournament.clear();
        this.dimensionBetweenness.clear();
    }
    
    public void asWritableString(StatsTypeDimension type) {
        switch(type){
            case DIM_TOURNAMENT:
                this.dimTournament += concatenateArray(dimensionTournament);
                break;
            case DIM_BETWEENNESS:
                this.dimBetweenness += concatenateArray(dimensionBetweenness);
                break;
            default:
                System.out.println("There isn't this type!!!");
                break;
        }
    }
    
    private String getString(StatsTypeDimension type) {
        switch(type){
            case DIM_TOURNAMENT:
                return this.dimTournament;
            case DIM_BETWEENNESS:
                return this.dimBetweenness;
            default:
                return null;
        }
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
    
    private String concatenateArray(List<String> stringArray){
        StringBuilder str = new StringBuilder();
        for(String strArray : stringArray){
            str.append(strArray).append(",");
        }
        str.replace((str.length() - 1), str.length(), ""); 
        str.append("\n");
        return str.toString();
    }
    
    public void writeInfoDimension(String outputPath,
            String outputPrefix) throws Exception {
        
        StatsTypeDimension writeableStats[] = {StatsTypeDimension.DIM_TOURNAMENT,
            StatsTypeDimension.DIM_BETWEENNESS};

        for (StatsTypeDimension type : writeableStats) {
            if (isDimmensionEmpty(type)) {
                writeOnFile(outputPath, outputPrefix, getString(type) + "\n", type);
            }
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
