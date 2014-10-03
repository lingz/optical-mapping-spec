package edu.nyu.opticalMapping;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ling on 2/10/14.
 */
public class ExperimentMolecule {
    boolean isTarget;
    boolean isFlipped;
    List<Double> cuts;
    Random random = new Random();

    public ExperimentMolecule(ArrayList<Double> cuts, boolean isTarget) {
        this.isTarget = isTarget;
        isFlipped = (random.nextDouble() > 0.5) ? true : false;
        this.cuts = cuts;

        // Randomly flip the cuts
        if (isFlipped) {
            for (int i = 0; i < cuts.size(); i++) {
                cuts.set(i, 1 - cuts.get(i));
            }
        }
    }



    public String getInputString() {
        return Utils.joinDoubles(cuts);
    }

    public String getGoldenString() {
        return ((isTarget) ?  "1 " + ((isFlipped) ? "1" : "0" ) : "0");
    }
}
