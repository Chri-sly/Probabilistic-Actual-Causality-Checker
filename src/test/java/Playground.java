import de.tum.in.i4.hp2sat.causality.CausalModel;
import de.tum.in.i4.hp2sat.causality.CausalitySolverResult;
import de.tum.in.i4.hp2sat.causality.SolvingStrategy;
import de.tum.in.i4.hp2sat.exceptions.InvalidCausalModelException;
import de.tum.in.i4.hp2sat.exceptions.InvalidCauseException;
import de.tum.in.i4.hp2sat.exceptions.InvalidContextException;
import de.tum.in.i4.hp2sat.exceptions.InvalidPhiException;
import de.tum.in.i4.hp2sat.util.ExampleProvider;
import org.junit.Ignore;
import org.junit.Test;
import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.formulas.Variable;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Playground {

    public static void main(String[] args) throws InvalidCausalModelException, InvalidContextException, InvalidCauseException, InvalidPhiException {
        CausalModel prisoners = ExampleProvider.prisoners();
        FormulaFactory f = prisoners.getFormulaFactory();
        Set<Literal> context = new HashSet<>(Arrays.asList(
                f.literal("A_exo", true), f.literal("B_exo", false),
                f.literal("C_exo", true)));
        Set<Literal> cause = new HashSet<>(Collections.singletonList(f.variable("A")));
        Formula phi = f.variable("D");

        CausalitySolverResult result = prisoners.isCause(context, phi, cause, SolvingStrategy.ORIGINAL_HP);

        CausalitySolverResult resultMod = prisoners.isCause(context, phi, cause, SolvingStrategy.BRUTE_FORCE);

        CausalitySolverResult resultUpd = prisoners.isCause(context, phi, cause, SolvingStrategy.UPDATED_HP);

        System.out.println("Original version: " + result.toString());
        System.out.println("Updated version: " + resultUpd.toString());
        System.out.println("Modified version: " + resultMod.toString());
        System.out.println("Responsibility: " + result.getResponsibility());

        CausalModel billySuzy = ExampleProvider.billySuzy();
        FormulaFactory f1 = billySuzy.getFormulaFactory();
        Set<Literal> context1 = new HashSet<>(Arrays.asList(
                f.literal("BT_exo", true), f.literal("ST_exo", true)
        ));
        Set<Literal> cause1 = new HashSet<>(Collections.singletonList(f.variable("ST")));
        Formula phi1 = f.variable("BS");

        CausalitySolverResult BSResult = billySuzy.isCause(context1, phi1, cause1, SolvingStrategy.UPDATED_HP);

        System.out.println("Updated Billy Suzy: " + BSResult.toString());
        System.out.println("Resp. : " + BSResult.getResponsibility());

    }
    @Ignore
    @Test
    public void assignmentAndSat() {
        FormulaFactory f = new FormulaFactory();
        Variable a = f.variable("A");
        Variable b = f.variable("B");
        Literal notC = f.literal("C", false);

        // A & ~(B | ~C); CNF: A & ~B & C => true for A=1, B=0, C=1
        Formula formula = f.and(a, f.not(f.or(b, notC)));
        // assign a positive literal for TRUE and a negative literal for FALSE
        Assignment assignment = new Assignment();
        assignment.addLiteral(a);
        assignment.addLiteral(b.negate());
        assignment.addLiteral(notC.negate());
        System.out.println(formula.evaluate(assignment)); // true

        // NNF and CNF
        Formula nnf = formula.nnf();
        Formula cnf = formula.cnf();

        // SAT
        final SATSolver miniSat = MiniSat.miniSat(f);
        miniSat.add(formula);
        final Tristate result = miniSat.sat();
    }

    @Ignore
    @Test
    public void restrict() {
        FormulaFactory f = new FormulaFactory();
        Variable a = f.variable("A");
        Variable b = f.variable("B");
        Literal notC = f.literal("C", false);

        // A & ~(B | ~C); CNF: A & ~B & C => true for A=1, B=0, C=1
        Formula formula = f.and(a, f.not(f.or(b, notC)));
        // assign a positive literal for TRUE and a negative literal for FALSE
        Assignment assignment = new Assignment();
        assignment.addLiteral(a);
        assignment.addLiteral(b.negate());
        assignment.addLiteral(notC.negate());

        System.out.println(assignment.formula(f));
        System.out.println(formula.restrict(assignment));
    }
}
