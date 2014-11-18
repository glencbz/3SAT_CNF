package sat;

import immutable.EmptyImList;
import immutable.ImList;
import javafx.geometry.Pos;
import sat.env.Bool;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.*;

import java.util.Iterator;


/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     * 
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */
    public static void main(String[] args) {


        Variable testVariable = new Variable("A");
        PosLiteral testLiteral = PosLiteral.make(testVariable);
        Clause testClause = new Clause(testLiteral);

        Variable testVariable1 = new Variable("A");
        NegLiteral testLiteral1 = NegLiteral.make(testVariable1);
        Clause testClause1 = new Clause(testLiteral1);

//        Variable testVariable2 = new Variable("C");
//        PosLiteral testLiteral2 = make(testVariable2);
//        Clause testClause2 = new Clause(testLiteral2);


        Formula testFormula = new Formula();
        Formula testFormula1 = testFormula.addClause(testClause1);
        Formula testFormula2 = testFormula1.addClause(testClause);
//        Formula testFormula3 = testFormula2.addClause(testClause2);

//        System.out.println(testFormula3);
        Environment answer = solve(testFormula2);
//        System.out.println(answer);
    }

    // Used during recursion to temporarily store the environment
    private static Environment testEnvironment;

    public static Environment solve(Formula formula) {
        // TODO: implement this.



        // Resets the test environment


        testEnvironment = new Environment();



        Environment result = recursSolve(formula);

        if (result == null) {
            System.out.println("CANNOT SOLVE");
        } else {
            System.out.println("SOLVED");
            System.out.println(result);
        }

        return result;
    }


    /**
     * Recursive SAT solver
     * Uses the static testEnvironment variable to track the problem
     * @param formula Formula object as an argument
     * @return Environment that reflects the variables that satisfy the problem
     */
    private static Environment recursSolve(Formula formula) {


        if (formula.getSize() == 0) {
            // No clauses, base case
            return testEnvironment;
        }

        Clause currentSmallestClause = null;
        // Check for empty clause
        Iterator<Clause> clauseItr = formula.iterator();

        while (clauseItr.hasNext()) {
            Clause aClause = clauseItr.next();
            // return if empty

            if (aClause.isEmpty()) {

                // Seems like this works lol
                return null;
            }


            // Find the smallest clause
            // Initialise currentSmallestClause for first iteration
            if (currentSmallestClause == null) {
                currentSmallestClause = aClause;
            }

            if (aClause.size() > currentSmallestClause.size()) {
                currentSmallestClause = aClause;
            }
        }



        // the smallest clause has been found
        // we now check if the clause has only one literal


        if (currentSmallestClause.size() == 1) {
            Literal currentLiteral = currentSmallestClause.chooseLiteral();
            Variable currentVariable = currentLiteral.getVariable();

            // TODO better way to Check if literal is negative
            if (currentLiteral.toString().contains("~")) {
                testEnvironment = testEnvironment.putFalse(currentVariable);
            } else {

                testEnvironment = testEnvironment.putTrue(currentVariable);
            }


            ImList<Clause> currentClauses = formula.getClauses();
            ImList<Clause> newClauses = substitute(currentClauses, currentLiteral);

            Formula newFormula = newFormulaFromClauses(newClauses, formula);

            return recursSolve(newFormula);

        }


        /**
         * if the smallest clause has more than one variable,
         * pick an arbitrary variable
         */
        {
            Literal chosenLiteral = currentSmallestClause.chooseLiteral();
            Variable currentVariable = chosenLiteral.getVariable();
            ImList<Clause> currentClauses = formula.getClauses();

            Bool literalSign;
            // Check sign of literal and assigns it to boolean
            if (chosenLiteral.toString().contains("~")) {
                literalSign = Bool.FALSE;
            } else {
                literalSign = Bool.TRUE;
            }


            // First test for positive Literal

            // First test the literal as it is, and if the result if null, test the negated case

            ImList<Clause> positiveNewClauses = substitute(currentClauses, chosenLiteral);
            Formula positiveTestFormula = newFormulaFromClauses(positiveNewClauses, formula);
            Environment result = recursSolve(positiveTestFormula);

            // If the result succeeds, return the tested environment
            if (result != null) {
                testEnvironment = result.put(currentVariable, literalSign);
                return testEnvironment;
            } else {

                // If the result fails, need to test the negated case

                // Put the negated literal into the new list of clauses
                ImList<Clause> negativeNewClauses = substitute(currentClauses, chosenLiteral.getNegation());
                Formula negativeTestFormula = newFormulaFromClauses(negativeNewClauses, formula);
                Environment negativeResult = recursSolve(negativeTestFormula);

                // If negatedcase succeeds, return the tested environment
                if (negativeResult != null) {
                    testEnvironment = negativeResult.put(currentVariable, literalSign.not());
                    return testEnvironment;
                } else {

                    // If both fail, return null
                    return null;
                }

            }


        }




//        return null;

    }

    private static Formula newFormulaFromClauses(ImList<Clause> clauses, Formula f) {
        Formula newFormula = new Formula();

        Iterator<Clause> newClausesItr = clauses.iterator();
        while (newClausesItr.hasNext()) {
            Clause aNewClause = newClausesItr.next();
            newFormula = newFormula.addClause(aNewClause);
        }

        return newFormula;
    }


    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     * 
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
        // TODO: implement this.
        throw new RuntimeException("not yet implemented.");
    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     * 
     * @param clauses
     *            , a list of clauses
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
            Literal l) {
        // TODO: implement this.
        ImList<Clause> substitutedClauses = new EmptyImList<Clause>();
        Iterator<Clause> imItr = clauses.iterator();
        while (imItr.hasNext()) {
            Clause aClause = imItr.next();
            Clause reducedClause = aClause.reduce(l);

            // clause.reduce(l) makes it null if there's only one variable
            // This conditional only allows non null clauses to be added
            if (reducedClause != null) {

                substitutedClauses =  substitutedClauses.add(reducedClause);
            }
        }
        return substitutedClauses;
    }

}
