# Probabilistic Actual Causality: Approaches and Implementation
Bachelor thesis project by Christopher Schubert in computer science at Artificial Intelligence and Machine Learning Lab at TU Darmstadt.

## Background
In my bachelor's thesis I looked at the different approaches towards extending actual causation
for probabilistic scenarios and implementing them algorithmically. In the course of this, I went from the simple conditional probability raising principle
to causal modelling approaches employing interventions to create counterfactual scenarios. The leading work on actual causation
in causal models is the Halpern-Pearl (HP) definition of actual causation [2]. However, this definition is only defined in
deterministic scenarios. Thus, I focused on probabilistic extensions of the Halpern-Pearl definition like 
[Fenton-Glynn's definition of PC]().

## Implementation
To implement these approaches I chose to extend a previous work on actual causation, namely that of [Ibrahim et al.](https://arxiv.org/abs/1904.13101) [1].
In their project, they provide an implementation to efficiently compute the modified variant of the HP definition https://github.com/amjadKhalifah/HP2SAT1.0.
I extended their work to also handle probabilistic scenarios for actual causation. Addtionally, I implemented the other two variants
of the HP definition that were missing (Original and Updated). All of my work can be found in the ```main/java/de/tuda.aiml/``` folder and the respective
tests in ```test/java/de/tuda.aiml/```.

### Algorithms
* Original HP definition
* Updated HP definition
* Simple Probability Raising
* Interventional Probability Raising
* Pull Out The Probability, Blame
* PC
* PC'
* PAC

## Installation

Currently, this library is _not_ published in a Maven repository. Please build it manually from source: 

```bash
$ mvn install
```

## Usage

### General

#### Creation of a probabilistic causal model
```java
// instantiate a new FormulaFactory
FormulaFactory f = new FormulaFactory();

// create exogenous variables
Variable CIExo = f.variable("CI_exo");
Variable BIExo = f.variable("BI_exo");
Variable U1 = f.variable("SonnyShoots");
Variable U2 = f.variable("TurkShoots");
Variable U3 = f.variable("SonnyHits");
Variable U4 = f.variable("TurkHits");

// create endogenous variables that are at the center of the system/example
Variable C = f.variable("C");
Variable B = f.variable("B");
Variable S = f.variable("S");
Variable T = f.variable("T");
Variable D = f.variable("D");

// create the formula/function for each endogenous variable. One can also use the PropositionalParser for complex Formulas.
Formula CFormula = CIExo;
Formula BFormula = BIExo;
Formula SFormula = f.and(C, U1);
Formula TFormula = f.and(f.and(B, f.not(S)), U2);
Formula DFormula = f.or(f.and(S, U3), f.and(T, U4));

// create the structural equations of the causal model: each endogenous variable and its formula form an equation
Equation CEquation = new Equation(C, CFormula);
Equation BEquation = new Equation(B, BFormula);
Equation SEquation = new Equation(S, SFormula);
Equation TEquation = new Equation(T, TFormula);
Equation DEquation = new Equation(D, DFormula);

Set<Equation> equations = new HashSet<>(Arrays.asList(CEquation, BEquation, SEquation, TEquation, DEquation));

// probability is introduced in the exogenous variables by mapping each exogenous variable to a double-value
Map<Variable, Double> exogenousVariables = new HashMap<>();
exogenousVariables.put(CIExo, 0.9);
exogenousVariables.put(BIExo, 0.9);
exogenousVariables.put(U1, 0.9);
exogenousVariables.put(U2, 0.9);
exogenousVariables.put(U3, 0.5);
exogenousVariables.put(U4, 0.9);

// instantiate the ProbabilisticCausalModel
ProbabilisticCausalModel causalModel = new ProbabilisticCausalModel("Don_Corleone", equations, exogenousVariables, f);
```

#### Check whether *C = 1* is a cause of *D = 1* in the previously created probabilistic causal model given *CI_exo, BI_exo = 1* as context
```java
// IMPORTANT: Use the same FormulaFactory instance as in the above!
ProbabilisticCausalModel police_parade = ProbabilisticExampleProvider.donPolice();
FormulaFactory f = police_parade.getFormulaFactory();

Set<Literal> context = new HashSet<>(Arrays.asList(f.literal("CI_exo", true), f.literal("BI_exo", true), 
        f.literal("SonnyShoots", true),f.literal("SonnyHits", true)));
/*
* Similar as for the context, we specify f.literal("C", true) as cause and f.variable("D") as phi, as we
* want to express C = 1 and D = 1 respectively.
*/
Set<Literal> cause = new HashSet<>((Collections.singletonList(f.literal("C", true))));
Formula phi = f.variable("D");

// check whether the cause with this phi fulfills all conditions of PC in this context
PCSolver pcSolver = new PCSolver();
ProbabilisticCausalitySolverResult result = pcSolver.solve(police_parade, context, phi, cause, ProbabilisticSolvingStrategy.PC);
```

### Important Notes

- When working with a causal model, *always* use the *same* `FormulaFactory` instance. If not, an exception might occur.
- When creating a `CausalModel` or a `ProbabilisticCausalModel`, it is checked whether the latter is valid. It needs to fulfill the following 
characteristics; otherwise an exception is thrown:
    - Each variable needs to be either exogenous or defined by *exactly one* equation.
    - The causal model must be *acyclic*. That is, no variables are allowed to mutually depend on each other 
    (directly and indirectly)
    - Variables must not be named with `"_dummy"`.
    
## Literature

[1] Ibrahim, A., Rehwald, S., Pretschner, A.: Efficiently checking actual causality with sat solving. In: Dependable Software Systems Engineering (2019), https://arxiv.org/abs/1904.13101

[2] Joseph Y. Halpern. Actual Causality. The MIT Press, 2016
