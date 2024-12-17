package de.tuda.aiml.probabilistic;

import org.logicng.formulas.Literal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Probabilistic version of the {@link de.tum.in.i4.hp2sat.causality.CausalitySolverResult} class.
 * Represents the result of probabilistic solvers that extend the HP definition.
 */
public class ProbabilisticCausalitySolverResult {
    private boolean pc1;
    private boolean pc2;
    private boolean pc3;
    private Set<Literal> cause;
    private Set<Literal> w;

    public ProbabilisticCausalitySolverResult(boolean pc1, boolean pc2, boolean pc3, Set<Literal> cause, Set<Literal> w) {
        this.pc1 = pc1;
        this.pc2 = pc2;
        this.pc3 = pc3;
        this.cause = cause;
        this.w = w;
    }

    /**
     * Compute the degree of responsibility. IMPORTANT: Does not ensure minimality of cause X and W!
     *
     * @return the degree of responsibility of each part of the cause as map
     */
    public Map<Literal, Double> getResponsibility() {
        Map<Literal, Double> responsibility = new HashMap<>();

        if (this.pc1 && this.pc2 && this.pc3) {
            int w = this.w == null ? 0 : this.w.size();
            int x = cause.size();
            this.cause.forEach(l -> responsibility.put(l, 1D / (x + w)));
        } else {
            this.cause.forEach(l -> responsibility.put(l, 0D));
        }

        return responsibility;
    }

    @Override
    public String toString() {
        return "CausalitySolverResult{" +
                "pc1=" + pc1 +
                ", pc2=" + pc2 +
                ", pc3=" + pc3 +
                ", cause=" + cause +
                ", w=" + w +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProbabilisticCausalitySolverResult that = (ProbabilisticCausalitySolverResult) o;
        return pc1 == that.pc1 &&
                pc2 == that.pc2 &&
                pc3 == that.pc3 &&
                Objects.equals(cause, that.cause) &&
                Objects.equals(w, that.w);
    }

    @Override
    public int hashCode() {

        return Objects.hash(pc1, pc2, pc3, cause, w);
    }

    public boolean isAc1() {
        return pc1;
    }

    public boolean isAc2() {
        return pc2;
    }

    public boolean isAc3() {
        return pc3;
    }

    public Set<Literal> getCause() {
        return cause;
    }

    public Set<Literal> getW() {
        return w;
    }
}
