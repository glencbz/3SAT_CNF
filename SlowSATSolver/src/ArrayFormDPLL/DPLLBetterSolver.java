package ArrayFormDPLL;

/**
 * Created by JiaHao on 19/11/14.
 */
public class DPLLBetterSolver {


    public static void main(String[] args) {



    }


    private static CNFSatInstance testSATinstance;
    public static boolean solve(CNFSatInstance satInstance) {

        // TODO shift this method into CNFSATInstance


        testSATinstance = satInstance;
        System.out.println("Simplifying formula...");
        int[][] simplifiedFormula = testSATinstance.simplify(testSATinstance.getClauses());

        System.out.println("Now solving...");
        boolean result = recurSolve(simplifiedFormula);
//        boolean result = recurSolve(testSATinstance.getClauses());
        // The environment is stored in the static knownAssignments

        if (result) {
            System.out.println("SATISFIABLE");
        } else {
            System.out.println("NOT SATISFIABLE");
        }

        return result;
    }

    private static boolean recurSolve(int[][] formula) {


        // First, we deal with the base cases,
        // if there are no clauses,
        // and if there is an empty clause



        if (formula.length == 0) {
            return true;
        }


        /**
         * Search through the clauses, and find either
         * an empty clause and return, or
         * find the smallest clause
          */

        // Smallest clause
        int[] smallestClause = null;
        // track smallest clause size to reduce
        // smallestClause.length operations
        int smallestClauseLength = 0;
        for (int[] aClause: formula) {

            int currentClauseLength = countNumberOfClausesIn(aClause);
            // First check for empty clause
            // if empty clause, just return false
            if (currentClauseLength == 0) {
                return false;
            } else if (currentClauseLength == 1) {
                /**
                 * The next smallest clause would be if
                 * the clauseLength == 1, so we can
                 * stop the iteration here
                 */

                // Substitute and recurse


                int literal = findLiteralIn(aClause);
                int[][] reducedFormula = testSATinstance.givenVar(formula, literal);



                boolean result = recurSolve(reducedFormula);


                if (result) {
                    testSATinstance.addKnownAssignment(literal);
                    return true;
                } else {
                    return false;
                }


            }

            // now we search for the smallest clause

            // lazy init of smallest clause
            if (smallestClause == null) {
                smallestClause = aClause;
                smallestClauseLength = countNumberOfClausesIn(aClause);
            }

            // Check and assign smallest clause to smallestClause

            if (currentClauseLength > smallestClauseLength) {
                smallestClause = aClause;
                smallestClauseLength = currentClauseLength;
            }

        }

        // We are left with clauses that have length > 1
        // We pick an arbitrary literal,
        // TODO optimise find literal
        // substitute in all clauses and solve recursively.
        // If false, repeat but instead with the negated literal.

        int literal = findLiteralIn(smallestClause);

        // Store the SATInstance here, in case the recursion fails,
        // need to revert back to the stored SATInstance

        CNFSatInstance storedSATInstance = testSATinstance;

        int[][] reducedFormula = testSATinstance.givenVar(formula, literal);
        boolean result = recurSolve(reducedFormula);

        if (result) {
            testSATinstance.addKnownAssignment(literal);
            return true;
        } else {

            // Revert back to the stored SATInstance
            testSATinstance = storedSATInstance;
            int negatedLiteral = literal*-1;
            int[][] negatedReducedFormula = testSATinstance.givenVar(formula, negatedLiteral);

            boolean negatedResult = recurSolve(negatedReducedFormula);

            if (negatedResult) {
                testSATinstance.addKnownAssignment(negatedLiteral);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Helper method to get the length of a clause, excluding
     * 0s from the length
     *
     * @param clause input
     * @return the length of the clause
     */
    private static int countNumberOfClausesIn(int[] clause) {
        int counter = 0;
        for (int i: clause) {
            if (i != 0) {
                counter+= 1;
            }
        }
        return counter;
    }



    /**
     * Helper method to find the first literal in a clause
     * @param clause
     * @return
     */
    private static int findLiteralIn(int[] clause) {
        // finds the first non zero clause
        for (int i: clause) {
            if (i != 0) {
                return i;
            }
        }

        System.out.println("Clause is all zeroes!");
        return 0;
    }


}
// remove unit clauses
// deal with a and ~a