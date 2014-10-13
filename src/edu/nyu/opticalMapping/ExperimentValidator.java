package edu.nyu.opticalMapping;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by root on 10/12/14.
 */

public class ExperimentValidator {

    private static List<Double> inputMolecule = new ArrayList<Double>();
    private static List<Integer> inputFlips = new ArrayList<Integer>();
    private static List<Integer> inputTypes = new ArrayList<Integer>();
    private static List<Double> goldenMolecule = new ArrayList<Double>();
    private static List<Integer> correctFlips = new ArrayList<Integer>();
    private static List<Integer> correctTypes = new ArrayList<Integer>();
    private static final int numFlips = 200;

    private static double epsilon;

    public static void main(String args[]) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, InvalidAlgorithmParameterException {

        BufferedReader inp = new BufferedReader( new InputStreamReader(System.in));

        // read input file
        String[] firstLine = inp.readLine().split(" ");

        for( int i = 0 ; i < firstLine.length ; i++ ){
            inputMolecule.add(Double.parseDouble(firstLine[i]));
        }
        Collections.sort(inputMolecule);

        String[] flips;
        for( int i = 0 ; i < numFlips ; i++){
            flips = inp.readLine().split(" ");
            inputTypes.add(Integer.parseInt(flips[0]));
            if(inputTypes.get(i) == 1){
                inputFlips.add(Integer.parseInt(flips[1]));
            }
            else{
                inputFlips.add(0);
            }
        }


        // read golden molecule
        String[] secondLine = inp.readLine().split(" ");

        for( int i = 0 ; i < secondLine.length ; i++ ){
            goldenMolecule.add(Double.parseDouble(secondLine[i]));
        }

        Collections.sort(goldenMolecule);

        for( int i = 0 ; i < numFlips ; i++){
            flips = inp.readLine().split(" ");
            correctTypes.add(Integer.parseInt(flips[0]));
            if(correctTypes.get(i) == 1){
                correctFlips.add(Integer.parseInt(flips[1]));
            }
            else{
                correctFlips.add(0);
            }
        }

        // read epsilon
        String cipherText = Utils.decryptString(inp.readLine());
        System.out.println(cipherText);
        epsilon = Double.parseDouble(cipherText);
        if (epsilon < 0.001) epsilon = 0.01;
//        epsilon = Double.parseDouble(inp.readLine());

        // hereafter assuming that we have the values of epsilon

        // first check

        // calculate hamming distance
        double hammingDistance = 0;

        for(int i = 0 ; i < numFlips ; i++){
            if( correctTypes.get(i) != inputTypes.get(i)){
                hammingDistance = hammingDistance + 2;
            }
            if(correctTypes.get(i) == 1 && inputTypes.get(i) == 1 && inputFlips.get(i) != correctFlips.get(i) ){
                hammingDistance = hammingDistance + 1;
            }
        }

        // calculate number of positions which are correctly calculated

        int firstCheck = calculateSimilar();

        double answer = (hammingDistance*80)/numFlips + inputMolecule.size() + goldenMolecule.size() - 2*firstCheck;

//        System.out.println(answer);

        // second check
        Collections.reverse(inputMolecule);

        double hammingDistance2 = 0;

        for(int i = 0 ; i < numFlips ; i++){
            if( correctTypes.get(i) != inputTypes.get(i)){
                hammingDistance2 = hammingDistance2 + 2;
            }
            if(correctTypes.get(i) == 1 && inputTypes.get(i) == 1 && inputFlips.get(i) == correctFlips.get(i) ){
                hammingDistance2 = hammingDistance2 + 1;
            }
        }

        int secondCheck = calculateSimilar();

        double answer2 = (hammingDistance2*80)/numFlips + inputMolecule.size() + goldenMolecule.size() - 2*secondCheck;

//        System.out.println(answer2);

        String actualHammingDistance, actualFP, actualFN, actualScore;
        if( answer < answer2){
            actualHammingDistance = new Double(hammingDistance).toString();
            actualFP = new Double(inputMolecule.size() - firstCheck).toString();
            actualFN = new Double(goldenMolecule.size() - firstCheck).toString();
            actualScore = new Double(answer).toString();
        }
        else{
            actualHammingDistance = new Double(hammingDistance2).toString();
            actualFP = new Double(inputMolecule.size() - secondCheck).toString();
            actualFN = new Double(goldenMolecule.size() - secondCheck).toString();
            actualScore = new Double(answer2).toString();
        }

        System.out.println("Hamming Distance: " + actualHammingDistance);
        System.out.println("False Positive Cuts: " + actualFP);
        System.out.println("False Negative Cuts: " + actualFN);
        System.out.println("Final Score: " + actualScore);
    }

    public static int calculateSimilar(){       // returns the number of positions that are matched in the golden molecule and the input molecule with difference less than epsilon
        int answerLength = inputMolecule.size();
        int correctLength = goldenMolecule.size();
        int[][] DP  = new int[answerLength][correctLength];   // stored the number of positions that are matched

        DP[0][0] = Math.abs( goldenMolecule.get(0) - inputMolecule.get(0) ) <= epsilon ? 1 : 0;
        for(int i = 1 ; i < correctLength ; i++){
            DP[0][i] = Math.abs( goldenMolecule.get(i) - inputMolecule.get(0) ) <= epsilon ? 1 : 0;
            DP[0][i] = Math.max(DP[0][1], DP[0][i - 1]);
        }
        for(int i = 1 ; i < answerLength ; i++){
            DP[i][0] = Math.abs( goldenMolecule.get(0) - inputMolecule.get(i) ) <= epsilon ? 1 : 0;
            DP[i][0] = Math.max(DP[i][0], DP[i - 1][0]);
        }

        for(int i = 1 ; i < answerLength ; i++){
            for(int j = 1 ; j < correctLength ; j++){
                if( j > 0 && i > 0 && Math.abs( goldenMolecule.get(j) - inputMolecule.get(i) ) <= epsilon ){
                    DP[i][j] = DP[i-1][j-1] + 1;
                }
                else{
                    DP[i][j] = Math.max(DP[i - 1][j], DP[i][j - 1]);
                }
            }
        }
        return DP[answerLength - 1][correctLength - 1];
    }

}
