package blackjack.ai;

import behave.tools.Log;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class Classifier {
    double[] m_theta;
    protected double m_threshold = 0.5;

    public Classifier(String modelFile) {
        this(readModelFromFile(modelFile));
    }

    public Classifier(double[] theta) {
        m_theta = theta;
        int length = m_theta.length;
        Log.info("Created classifier with " + (length - 1)+ " features (Theta length " + length + ")");
    }

    // Override this method for more complex models.
    public boolean predict(Sample sample) {
        double[] sampleData = sampleToFloatArr(sample);
        //TODO use a proper matrix library
        double z = 0.0;
        for (int n = 0; n < m_theta.length; n++) {
            z += sampleData[n] * m_theta[n];
        }
        double estimate = sigmoid(z);
        Log.debug("should hit for " + z + "? estimate " + estimate);
        return estimate > m_threshold;
    }

    protected double sigmoid(double z) {
        return 1.0 / (1.0 + Math.exp(-z));
    }

    protected double[] sampleToFloatArr(Sample sample) {
        double[] arr = new double[4];
        arr[0] = 1;
        arr[1] = sample.bestPips;
        arr[2] = sample.dealerPips;
        arr[3] = sample.minPips;
        return arr;
    }

    public static double[] readModelFromFile(String filename) {
        try {
            File file = new File(filename);
            List<String> lines = Files.readAllLines(file.toPath())
                    .stream()
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());

            double[] model = new double[lines.size()];
            int index = 0;
            for(String line: lines) {
                double f = Double.parseDouble(line);
                model[index++] = f;
            }
            return model;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new double[0];
        }
    }

    public static void main(String[] args) {
        Classifier c = new Classifier("model-simple.csv");
        boolean shouldHit = c.predict(new Sample(17, 17, 10, 0));
        System.out.println("Should hit? " + shouldHit);
    }
}
