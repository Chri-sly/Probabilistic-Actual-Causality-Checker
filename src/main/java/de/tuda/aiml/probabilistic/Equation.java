package de.tuda.aiml.probabilistic;

import org.logicng.formulas.Formula;
import org.logicng.formulas.Variable;

public class Equation {
    private Variable variable;
    private Formula formula;
    private double probability;

    public Equation(Variable variable, Formula formula, double probability) {
        this.variable = variable;
        this.formula = formula;
        this.probability = probability;
    }

    public Equation(Equation equation) {
        this(equation.variable, equation.formula, equation.probability);
    }

    @Override
    public String toString() {
        return variable + " = " + formula;
    }

    public Variable getVariable() {
        return variable;
    }

    public Formula getFormula() {
        return formula;
    }

    public double getProbability() { return probability;}

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    public void setProbability(double probability) { this.probability = probability; }
}
