package edu.nyu.opticalMapping;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by ling on 2/10/14.
 */
public class ExperimentGenerator {
    private static ArrayList<ExperimentMolecule> experimentalResults;
    private static String fileName;
    private static int problemType;
    private static double probabilityIncluded;
    private static Molecule goldenMolecule;
    private static double epsilon;


    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException {
        problemType = Integer.parseInt(args[0]);
        String generatorType = args[1];
        probabilityIncluded = Double.parseDouble(args[2]);
        epsilon = Double.parseDouble(args[3]);
        int meanNumberSpuriousCuts = Integer.parseInt(args[4]);
        fileName = args[5];
        Random random = new Random();

        goldenMolecule = new Molecule(generatorType, probabilityIncluded, epsilon, meanNumberSpuriousCuts, true);

        experimentalResults = new ArrayList<ExperimentMolecule>();

        int numBad = (problemType < 2) ? 0 :random.nextInt(60);

        // Good molecules
        for (int i = 0; i < 200 - numBad; i++) {
            experimentalResults.add(goldenMolecule.generateExperimentResult());
        }

        Molecule badMolecule;
        for (int i = 0; i < numBad; i++) {
            badMolecule = new Molecule(generatorType, probabilityIncluded, epsilon, meanNumberSpuriousCuts, false);
            experimentalResults.add(badMolecule.generateExperimentResult());
        }

        Collections.shuffle(experimentalResults);


        // print p
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));

        writeInputFile();
        writeGoldFile();
        writeParametersFile();

    }

    private static void writeInputFile() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(fileName + ".txt", "UTF-8");
        writer.println(problemType);
        writer.println((problemType < 3) ? probabilityIncluded : 0);
        for (ExperimentMolecule experimentMolecule : experimentalResults) {
            writer.println(experimentMolecule.getInputString());
        }
        writer.close();
    }

    private static void writeGoldFile() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(fileName + "_gold" + ".txt", "UTF-8");
        writer.println(goldenMolecule.getCutsString());
        for (ExperimentMolecule experimentMolecule : experimentalResults) {
            writer.println(experimentMolecule.getGoldenString());
        }
        writer.close();
    }

    private static void writeParametersFile() throws FileNotFoundException, UnsupportedEncodingException, IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        PrintWriter writer = new PrintWriter(fileName + "_parameters" + ".txt", "UTF-8");
        String cipherText = Utils.encryptString((new Double(epsilon).toString()));
        writer.println(cipherText);
        writer.close();
    }
}
