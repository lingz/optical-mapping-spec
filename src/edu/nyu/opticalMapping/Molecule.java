package edu.nyu.opticalMapping;


import org.uncommons.maths.random.ExponentialGenerator;
import org.uncommons.maths.random.GaussianGenerator;
import org.uncommons.maths.random.PoissonGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ling on 2/10/14.
 */



public class Molecule {
    public static final String EXPONENTIAL = "exponential";
    public static final String NORMAL = "normal";
    public static final String UNIFORM = "uniform";
    private double probabilityIncluded;
    boolean isTarget;

    private Random randomNumberGenreator = new Random();
    private double epsilon;

    private GaussianGenerator jiggleGenerator;
    private PoissonGenerator spuriousCutGenerator;

    private List<Double> cuts = new ArrayList<Double>(40);

    public Molecule(String generatorType, double probabilityIncluded, double epsilon, int meanNumberSpuriousCuts, boolean isTarget) {
        this.probabilityIncluded = probabilityIncluded;
        this.epsilon = epsilon;
        this.isTarget = isTarget;

        // Initialize randomness
        randomNumberGenreator = new Random();
        double mean = randomNumberGenreator.nextDouble();
        if (meanNumberSpuriousCuts > 0) {
            spuriousCutGenerator = new PoissonGenerator(meanNumberSpuriousCuts, randomNumberGenreator);
        }
        jiggleGenerator = new GaussianGenerator(0, epsilon, randomNumberGenreator);

        if (generatorType == EXPONENTIAL) {
            ExponentialGenerator exponentialGenerator = new ExponentialGenerator(mean, randomNumberGenreator);
            for (int i = 0; i < 40; i++) {
                double nextVal = exponentialGenerator.nextValue();
                while (nextVal >= 1) {
                    nextVal = exponentialGenerator.nextValue();
                }
                cuts.add(nextVal);
            }
        } else if (generatorType == NORMAL) {
            GaussianGenerator gaussianGenerator = new GaussianGenerator(mean, 0.5, randomNumberGenreator);
            for (int i = 0; i < 40; i++) {
                double nextVal = gaussianGenerator.nextValue();
                while (nextVal >= 1) {
                    nextVal = gaussianGenerator.nextValue();
                }
                cuts.add(nextVal);
            }
        } else {
            for (int i = 0; i < 40; i++) {
                cuts.add(randomNumberGenreator.nextDouble());
            }
        }

    }

    // Creates an results
    public ExperimentMolecule generateExperimentResult() {
        ArrayList<Double> includedCuts = new ArrayList<Double>();

        ExperimentMolecule newMolecule;
        for (int i = 0; i < 40; i++) {
            if (randomNumberGenreator.nextDouble() < probabilityIncluded) {
                includedCuts.add(jiggle(cuts.get(i)));
            }
        }

        if (spuriousCutGenerator != null) {
            // Now do spurious cuts
            int numSpuriousCuts = spuriousCutGenerator.nextValue();
            for (int i = 0; i < numSpuriousCuts; i++) {
                includedCuts.add(randomNumberGenreator.nextDouble());
            }
        }


        return new ExperimentMolecule(includedCuts, isTarget);
    }

    private double jiggle(double originalValue) {
        double jiggledValue = -1;
        while (jiggledValue < 0 || jiggledValue >= 1) {
            jiggledValue = originalValue + jiggleGenerator.nextValue();
        }
        return jiggledValue;
    }

    public String getCutsString() {
        return Utils.joinDoubles(cuts);
    }


}


