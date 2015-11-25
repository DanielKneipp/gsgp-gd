/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsgp.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import edu.gsgp.MersenneTwister;
import edu.gsgp.population.Population;
import edu.gsgp.nodes.Node;
import edu.gsgp.nodes.functions.Function;
import edu.gsgp.nodes.terminals.Input;
import edu.gsgp.nodes.terminals.Terminal;
import edu.gsgp.population.builders.FullBuilder;
import edu.gsgp.population.builders.GrowBuilder;
import edu.gsgp.population.builders.HalfBuilder;
import edu.gsgp.population.Individual;
import edu.gsgp.population.builders.IndividualBuilder;
import edu.gsgp.population.fitness.Fitness;
import edu.gsgp.population.fitness.FitnessRMSE;
import edu.gsgp.population.selector.IndividualSelector;
import edu.gsgp.population.selector.TournamentSelector;
import java.io.FileNotFoundException;

/**
 *
 * @author luiz
 */
public class PropertiesManager {
    protected boolean parameterLoaded;
    
    protected Properties fileParameters;
    protected CommandLine cliParameters;

    protected Options cliOptions;
    
    private DataProducer dataProducer;
    private MersenneTwister mersennePRNG;
    private ExperimentalData data;
    private int numExperiments;
    private int numGenerations;
    private int numThreads;
    private int populationSize;
    private int rtPoolSize;
    private double minError;
    private double ms;
    private double mutProb;
    private double xoverProb;
    private double semSimThres;
    private Fitness fitnessFunction;
    private Terminal[] terminals;
    private Function[] functions;
    
    private IndividualSelector individualSelector;
    
    private IndividualBuilder individualBuilder;
    private IndividualBuilder randomTreeBuilder;
    
    private String outputDir;
    private String filePrefix;
    

    private void loadParameters() throws Exception{
        dataProducer = getDataProducer();
        mersennePRNG = new MersenneTwister(getLongPropertie(ParameterList.SEED, System.currentTimeMillis()));
        terminals = getTerminals();
        functions = getFunctions();
        fitnessFunction = getFitnessClass();
        numExperiments = getIntegerPropertie(ParameterList.NUM_REPETITIONS, 1);
        numGenerations = getIntegerPropertie(ParameterList.NUM_GENERATION, 200);
        
        numThreads = getIntegerPropertie(ParameterList.NUMBER_THREADS, -1);
        if(numThreads == -1) numThreads = Runtime.getRuntime().availableProcessors();
        
        populationSize = getIntegerPropertie(ParameterList.POP_SIZE, 1000);
        rtPoolSize = getIntegerPropertie(ParameterList.RT_POOL_SIZE, 200);
        minError = getDoublePropertie(ParameterList.MIN_ERROR, 0);
        ms = getDoublePropertie(ParameterList.MUT_STEP, -1);
        mutProb = getDoublePropertie(ParameterList.MUT_PROB, 0.5);
        xoverProb = getDoublePropertie(ParameterList.XOVER_PROB, 0.5);
        semSimThres = getDoublePropertie(ParameterList.SEMANTIC_SIMILARITY_THRESHOLD, 0.1);
        outputDir = getStringPropertie(ParameterList.PATH_OUTPUT_DIR, true);
        filePrefix = getStringPropertie(ParameterList.FILE_PREFIX, false);
        
        individualBuilder = getIndividualBuilder(false);
        randomTreeBuilder = getIndividualBuilder(true);
        
        individualSelector = getIndividualSelector();        
    }
    
    public enum ParameterList {

        PATH_DATA_FILE("experiment.data", "Path for the training/test files. See experiment.sampling option for more details", true),
        PATH_OUTPUT_DIR("experiment.output.dir", "Output directory", false),
        SEED("experiment.seed", "Seed (long int) used by the pseudo-random number generator", false),
        FILE_PREFIX("experiment.file.prefix", "Identifier prefix for files", false),
        TERMINAL_LIST("tree.build.terminals", "List of terminals used to build new trees (separeted by commas)", true),
        FUNCTION_LIST("tree.build.functions", "List of functions used to build new trees (separeted by commas)", true),
        FITNESS_FUNCTION("pop.fitness", "Fitness function adopted during the evolution", true),
        INDIVIDUAL_BUILDER_POP("tree.build.builder", "Builder used to generate trees for the initial population", true),
        INDIVIDUAL_BUILDER_RAND_TREE("tree.build.builder.random.tree", "Builder used to generate random trees for the semantic operators", true),
        INDIVIDUAL_SELECTOR("pop.ind.selector", "Type of selector used to select individuals for next generations", true),
        
        EXPERIMENT_DESIGN("experiment.design", "Type of experiment (cross-validation or holdout):"
                                                + "\n# - If crossvalidation, uses splited data from a list of files. Use paths to the"
                                                + "\n# files in the form /pathToFile/repeatedName#repeatedName, where # indicates "
                                                + "\n# where the fold index is placed (a number from 0 to k-1). E.g. /home/iris-#.dat,"
                                                + "\n# with 3 folds in the path will look for iris-0.dat, iris-1.dat and iris-2.dat"
                                                + "\n# - If holdout, Use paths to the files in the form /pathToFile/repeatedName#repeatedName,"
                                                + "\n# where # is composed by the pattern (train|test)-i with i=0,1,...,n-1, where n is"
                                                + "\n# the number of experiment files. E.g. /home/iris-#.dat, with 4 files (2x(train+test))"
                                                + "\n# in the path will look for iris-train-0.dat, iris-test-0.dat, iris-train-1.dat and iris-test-1.dat", true),
        
        MAX_TREE_DEPTH("tree.build.max.depth", "Max depth allowed when building trees", false),
        MIN_TREE_DEPTH("tree.min.depth", "Min depth allowed when building trees", false),
        NUM_GENERATION("evol.num.generation", "Number of generations", false),
        NUM_REPETITIONS("experiment.num.repetition", "Number of experiment repetitions (per fold) - default = 1", false),
        POP_SIZE("pop.size", "Population size", false),
        RT_POOL_SIZE("rt.pool.size", "Size of the pool of random trees used by GSX/GSM", false),
        NUMBER_THREADS("evol.num.threads", "Number of threads (for parallel execution)", false),
        TOURNAMENT_SIZE("pop.ind.selector.tourn.size", "Tournament size, when using tournament as selector", false),
        
        MIN_ERROR("evol.min.error", "Minimum error to consider a hit", false),
        MUT_PROB("breed.mut.prob", "Probability of applying the mutation operator", false),
        MUT_STEP("breed.mut.step", "Mutation step", false),
        XOVER_PROB("breed.xover.prob", "Probability of applying the crossover operator", false),
        SEMANTIC_SIMILARITY_THRESHOLD("sem.gp.epsilon", "Threshold used to determine if two semantics are similar", false);

        public final String name;
        public final String description;
        public final boolean mandatory;

        private ParameterList(String name, String description, boolean mandatory) {
            this.name = name;
            this.description = description;
            this.mandatory = mandatory;
        }
    }

    public PropertiesManager(String args[]) throws Exception{
        setOptions();
        parameterLoaded = loadParameterFile(args);
        loadParameters();        
    }

    /**
     * Load the parameters from the CLI and file
     * @param args CLI parameters
     * @return True if and only if parameters are loaded both from CLI and file
     * @throws Exception 
     */
    private boolean loadParameterFile(String[] args) throws Exception{
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine parametersCLI = parser.parse(cliOptions, args);
            if(parametersCLI.hasOption("H")){
                writeParameterModel();
                return false;
            }
            if(!parametersCLI.hasOption("p"))
                throw new Exception("The parameter file was not specified.");
            String path = parametersCLI.getOptionValue("p");
            path = path.replaceFirst("^~",System.getProperty("user.home"));
            File parameterFile = new File(path);
            if(!parameterFile.canRead()) 
                throw new FileNotFoundException("Parameter file can not be read: " + parameterFile.getCanonicalPath());
            FileInputStream fileInput = new FileInputStream(parameterFile);
            fileParameters = new Properties();
            fileParameters.load(fileInput);
            return true;
        } 
        catch (MissingOptionException ex){
            throw new Exception("Required parameter not found.");
        }
        catch (ParseException ex) {
            throw new Exception("Error while parsing the command line.");
        }
    }
    private void writeParameterModel(){
        try{
            File file = new File("model.param");

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            StringBuilder textToPrint = new StringBuilder();
            for(ParameterList p : ParameterList.values()){
                textToPrint.append("# " + p.description + "\n");
                textToPrint.append(p.name + " = \n");
            }
            bw.write(textToPrint.toString());
            bw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private IndividualSelector getIndividualSelector() throws Exception{
        String value = getStringPropertie(ParameterList.INDIVIDUAL_SELECTOR, false).toLowerCase();
        IndividualSelector indSelector;
        switch(value){
            case "tournament":
                indSelector = new TournamentSelector(getIntegerPropertie(ParameterList.TOURNAMENT_SIZE, 7));
                break;
            default:
                throw new Exception("The inidividual selector must be defined.");
        }
        return indSelector;
    }
    
    public DataProducer getDataProducer() throws Exception{
        String value = getStringPropertie(ParameterList.EXPERIMENT_DESIGN, false).toLowerCase();
        DataProducer dataProducer;
        switch(value){
            case "crossvalidation":
                dataProducer = new CrossvalidationHandler();
                break;
            case "holdout":
                dataProducer = new HoldoutHandler();
                break;
            default:
                throw new Exception("Experiment design must be crossvalidation or holdout.");
        }
        dataProducer.setDataset(getStringPropertie(ParameterList.PATH_DATA_FILE, true));
        return dataProducer;
    }
    
    private int getIntegerPropertie(ParameterList key, int defaultValue) throws Exception {
        try {
            if (!fileParameters.containsKey(key.name) && key.mandatory) {
                throw new NoSuchFieldException("The input parameter (" + key.name + ") was not found");
            }
            else if(!fileParameters.containsKey(key.name) || fileParameters.getProperty(key.name).replaceAll("\\s", "").equals(""))
                return defaultValue;
            return Integer.parseInt(fileParameters.getProperty(key.name));
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage() + "\nThe input parameter (" + key.name + ") could not be converted to int.");
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage() + "\nThe parameter file was not initialized.");
        }
    }
    
    private long getLongPropertie(ParameterList key, long defaultValue) throws Exception {
        try {
            if (!fileParameters.containsKey(key.name) && key.mandatory) {
                throw new NoSuchFieldException("The input parameter (" + key.name + ") was not found");
            }
            else if(!fileParameters.containsKey(key.name) || fileParameters.getProperty(key.name).replaceAll("\\s", "").equals(""))
                return defaultValue;
            return Integer.parseInt(fileParameters.getProperty(key.name));
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage() + "\nThe input parameter (" + key.name + ") could not be converted to int.");
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage() + "\nThe parameter file was not initialized.");
        }
    }

    private Double getDoublePropertie(ParameterList key, double defaultValue) throws Exception {
        try {
            if (!fileParameters.containsKey(key.name) && key.mandatory) {
                throw new NoSuchFieldException("The input parameter (" + key.name + ") was not found");
            }
            else if(!fileParameters.containsKey(key.name))
                return defaultValue;
            return Double.parseDouble(fileParameters.getProperty(key.name));
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage() + "\nThe input parameter (" + key.name + ") could not be converted to double.");
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage() + "\nThe parameter file was not initialized.");
        }
    }
    
    private String getStringPropertie(ParameterList key, boolean isFile) throws Exception {
        try {
            if (!fileParameters.containsKey(key.name) && key.mandatory) {
                throw new NoSuchFieldException("Input parameter not found: " + key.name);
            }
            else if(!fileParameters.containsKey(key.name))
                return null;
            String output = fileParameters.getProperty(key.name);
            if(isFile)
                output = output.replaceFirst("^~",System.getProperty("user.home"));
            return output;
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage() + "\nThe parameter file was not initialized.");
        }
    }

    private IndividualBuilder getIndividualBuilder(boolean isForRandomTrees) throws Exception{
        String builderType;
        if(isForRandomTrees)
            builderType = getStringPropertie(ParameterList.INDIVIDUAL_BUILDER_RAND_TREE, false).toLowerCase();
        else
            builderType = getStringPropertie(ParameterList.INDIVIDUAL_BUILDER_POP, false).toLowerCase();
        int maxDepth = getIntegerPropertie(ParameterList.MAX_TREE_DEPTH, 6);
        int minDepth = getIntegerPropertie(ParameterList.MIN_TREE_DEPTH, 2);
        switch(builderType){
            case "grow":
                return new GrowBuilder(maxDepth, minDepth, functions, terminals);
            case "full":
                return new FullBuilder(maxDepth, minDepth, functions, terminals);
            case "rhh":
                return new HalfBuilder(maxDepth, minDepth, functions, terminals);
            default:
                throw new Exception("There is no builder called " + builderType + ".");
        }
    }
    
    public boolean isParameterLoaded() {
        return parameterLoaded;
    }
    
    private Terminal[] getTerminals() throws Exception{
        String[] sTerminals = getStringPropertie(ParameterList.TERMINAL_LIST, false).replaceAll("\\s", "").split(",");
        boolean useAllInputs = true;
        for(String str : sTerminals){
            if(str.toLowerCase().matches("x\\d+")){
                useAllInputs = false;
                break;
            }
        }
        ArrayList<Terminal> terminals = new ArrayList<Terminal>();
        if(useAllInputs){
            for(int i = 0; i < dataProducer.getNumInputs(); i++) terminals.add(new Input(i));
            for(String str : sTerminals){
                try{
                    Class<?> terminal = Class.forName(str);
                    Terminal newTerminal = (Terminal)terminal.newInstance();
                    terminals.add(newTerminal);
                }
                catch(ClassNotFoundException e){
                    throw new ClassNotFoundException("Error loading the terminal set. Class " + str + " not found", e);
                }
            }
        }
        else{
            // ************************ TO IMPLEMENT ************************
        }
        return terminals.toArray(new Terminal[terminals.size()]);
    }

    /**
     * Get the list of functions from the String read from the file
     * @return An array of functions
     * @throws Exception Parameter not found, error while parsing the function type 
     */
    private Function[] getFunctions()throws Exception{
        String[] sFunctions = getStringPropertie(ParameterList.FUNCTION_LIST, false).replaceAll("\\s", "").split(",");
        ArrayList<Function> functionArray = new ArrayList<Function>();
       
        for(String str : sFunctions){
            try{
                Class<?> function = Class.forName(str);
                functionArray.add((Function)function.newInstance());
            }
            catch(ClassNotFoundException e){
                throw new ClassNotFoundException("Error loading the function set. Class " + str + " not found", e);
            }
        }
        
        return functionArray.toArray(new Function[functionArray.size()]);
    }
    
    /**
     * Get the fitness class form the file
     * @return A new fintess function
     * @throws Exception Parameter not found, error while parsing the function type 
     */
    private Fitness getFitnessClass() throws Exception {
        String fitnessClassname = getStringPropertie(ParameterList.FITNESS_FUNCTION, false).replaceAll("\\s", "");
//        Class<?> fitnessClass = Class.forName(fitnessClassname);
//        return (Fitness)fitnessClass.newInstance();
        return new FitnessRMSE();
    }

    /**
     * There are tree input options from the CLI: 
     * - The path for the parameter file
     * - One or more parameters to overwrite parameters from the file 
     * - The option of creating a parameter file model on the classpath
     */
    private void setOptions() {
        cliOptions = new Options();
        cliOptions.addOption(Option.builder("p")
                .required(false)
                .hasArg()
                .desc("Paramaters file")
                .type(String.class)
                .build());
        cliOptions.addOption(Option.builder("P")
                .required(false)
                .hasArg()
                .desc("Overwrite of one or more parameters provided by file.")
                .type(String.class)
                .hasArgs()
                .build());
        cliOptions.addOption(Option.builder("H")
                .required(false)
                .desc("Create a parameter file model on the classpath.")
                .type(String.class)
                .build());
    }

    public MersenneTwister getMersennePRNG() {
        return mersennePRNG;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public int getNumExperiments() {
        return numExperiments;
    }

    public int getNumGenerations() {
        return numGenerations;
    }

    public int getNumThreads() {
        return numThreads;
    }
    
    public int getRTPoolSize() {
        return rtPoolSize;
    }

    public double getMinError() {
        return minError;
    }

    public double getMutationStep() {
        return ms;
    }

    public double getMutProb() {
        return mutProb;
    }

    public double getXoverProb() {
        return xoverProb;
    }

    public double getSemSimThreshold() {
        return semSimThres;
    }
    
    public String getOutputDir() {
        return outputDir;
    }

    public String getFilePrefix() {
        return filePrefix;
    }
    
    public Fitness geFitness(){
        return fitnessFunction.softClone();
    }
    
    public Node getRandomTree(MersenneTwister rnd){
        return randomTreeBuilder.newRootedTree(0, rnd);
    }
    
    public Node  getRandomTree(){
        return randomTreeBuilder.newRootedTree(0, mersennePRNG);
    }
    
    public Node getNewIndividualTree(MersenneTwister rnd){
        return individualBuilder.newRootedTree(0, rnd);
    }
    
    public Individual selectIndividual(Population population, MersenneTwister rndGenerator){
        return individualSelector.selectIndividual(population, rndGenerator);
    }
    
    public MersenneTwister[] getMersennePRGNArray(int size){
        MersenneTwister[] generators = new MersenneTwister[size];
        for(int i = 0; i < size; i++){
            long seed = mersennePRNG.nextLong();
//            generators[i] = new MersenneTwister(getLongPropertie(ParameterList.SEED, System.currentTimeMillis())+(long)i);
            generators[i] = new MersenneTwister(seed);
        }
        return generators;
    }
}
