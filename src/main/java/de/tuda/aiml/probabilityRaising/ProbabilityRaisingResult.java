package de.tuda.aiml.probabilityRaising;

import java.util.Objects;

/**
 * Class that contains the result of a probability raising approach.
 * In detail, it contains information if C is regarded a cause of E, the probability of P(E | C)
 * and the probability of P(E | not(C))
 */
public class ProbabilityRaisingResult {
    boolean probRaised;
    double pC;
    double pNotC;

    public ProbabilityRaisingResult(boolean probRaised, double pC, double pNotC) {
        this.probRaised = probRaised;
        this.pC = pC;
        this.pNotC = pNotC;
    }

    @Override
    public String toString() {
        return "ProbabilityRaisingResult{" +
                "C raises prob of E=" + probRaised +
                ", P(E | C)=" + pC +
                ", P(E | not(C))=" + pNotC +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProbabilityRaisingResult that = (ProbabilityRaisingResult) o;
        return probRaised == that.probRaised &&
                pC == that.pC &&
                pNotC == that.pNotC;
    }

    @Override
    public int hashCode() {
        return Objects.hash(probRaised, pC, pNotC);
    }

    public boolean isCause() {
        return probRaised;
    }

    public double getPC() {
        return pC;
    }

    public double getNotPC() {
        return pNotC;
    }

}

