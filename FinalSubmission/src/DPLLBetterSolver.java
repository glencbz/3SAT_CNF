/**
 * DPLL Algorithm
 */
public class DPLLBetterSolver {



    private static CNFSatInstance testSATinstance;
    public static boolean solve(CNFSatInstance satInstance) {


        // The environment is stored in the static field testSATinstance
        testSATinstance = satInstance;
        int[][] formula= testSATinstance.simplify(testSATinstance.getClauses());


        boolean result = recurSolve(formula);
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
        boolean clauseLengthOneAssigned = false;
        for (int[] aClause: formula) {

            int currentClauseLength = countNumberOfClausesIn(aClause);
            // First check for empty clause
            // if empty clause, just return false
            if (currentClauseLength == 0) {
                return false;
            }

            // now we search for the smallest clause

            // lazy init of smallest clause
            if (smallestClause == null) {
                smallestClause = aClause;
                smallestClauseLength = currentClauseLength;
            }

            // check if length1 has been found, so skip comparing to get
            // minimum
            if (currentClauseLength == 1 && !clauseLengthOneAssigned) {
                clauseLengthOneAssigned = true;
                smallestClause = aClause;
            } else {
                // compare and find smallest

                if (currentClauseLength > smallestClauseLength) {
                    smallestClause = aClause;
                    smallestClauseLength = currentClauseLength;
                }
            }

            // Check and assign smallest clause to smallestClause



        }


        if (smallestClauseLength == 1) {

            /**
             * The next smallest clause would be if
             * the clauseLength == 1, so we can
             * stop the iteration here
             */

            // Substitute and recurse

            int literal = findLiteralIn(smallestClause);
            int[][] reducedFormula = testSATinstance.givenVar(formula, literal);

            boolean result = recurSolve(reducedFormula);

            if (result) {
                testSATinstance.addKnownAssignment(literal);
                return true;
            } else {
                return false;
            }
        }

        // We are left with clauses that have length > 1
        // We pick an arbitrary literal,
        // substitute in all clauses and solve recursively.
        // If false, repeat but instead with the negated literal.
        int literal = findLiteralIn(smallestClause);
        int[][] reducedFormula = testSATinstance.givenVar(formula, literal);
        boolean result = recurSolve(reducedFormula);

        if (result) {
            testSATinstance.addKnownAssignment(literal);
            return true;
        } else {

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
     * Helper method, get the number of clauses
     * @param clause
     * @return
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
     * Helper method, finds the first literal
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
        // Not supposed to get here
        System.out.println("Clause is all zeroes!");
        return 0;
    }
}
