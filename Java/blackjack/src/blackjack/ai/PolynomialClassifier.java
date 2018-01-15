package blackjack.ai;

import java.util.Vector;

public class PolynomialClassifier extends Classifier {
    int m_degrees;
    public PolynomialClassifier(String modelFile, int degrees) {
        super(modelFile);
        m_degrees = degrees;
    }

    @Override
    protected double[] sampleToFloatArr(Sample sample) {
        double x1 = sample.bestPips;
        double x2 = sample.dealerPips;
        // Calculate polynomial version of the original 2-dimensional feature vector [x1, x2].
        // TODO use proper matrix / math library, this doesn't really scale
        Vector<Double> polynomials = new Vector<>();
        polynomials.add(1.0);
        for (int i = 1; i <= m_degrees; i++) {
            for (int j = 0; j <= i; j++) {
                double val = Math.pow(x1, (i-j)) * Math.pow(x2, j);
                polynomials.add(val);
            }
        }
        return polynomials.stream().mapToDouble(d -> d).toArray();
    }


    public static void main(String[] args) {
        PolynomialClassifier c = new PolynomialClassifier("model-polynomial-5.csv", 5);

        Sample s = new Sample(17, 17, 10);
        double[] test = c.sampleToFloatArr(s);

        boolean shouldHit = c.predict(s);
        System.out.println("Should hit? " + shouldHit);
    }
}
