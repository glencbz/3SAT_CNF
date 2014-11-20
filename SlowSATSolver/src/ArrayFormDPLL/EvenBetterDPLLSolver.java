package ArrayFormDPLL;

import java.util.Arrays;

/**
* Created by JiaHao on 19/11/14.
*/
public class EvenBetterDPLLSolver {




    public static boolean solve(CNFSatInstance satInstance) {


        testSatInstance = satInstance;

        System.out.println("Simplifying formula...");
        testSatInstance.simplify();
//
        System.out.println("Now solving...");

        boolean result;
        // First check for empty clause right away
        if (findEmptyClause(testSatInstance)) {
            result = false;
        } else {
            result = recurSolve();
        }



//        boolean result = recurSolve(testSATinstance.getClauses());
        // The environment is stored in the static knownAssignments

        if (result) {
            System.out.println("SATISFIABLE");
        } else {
            System.out.println("NOT SATISFIABLE");
        }

        return result;


    }

    public static CNFSatInstance testSatInstance;
    public static boolean recurSolve() {

        // Init some variables to reduce operations

        // Smallest clause
        int[] smallestClause = null;
        // track smallest clause size to reduce
        // smallestClause.length operations
        int smallestClauseLength = 0;


        // Iterate through the list of clauses,
        // currentSatInstance.getDeleted() is a map to determine
        // deleted clauses at index i

        int[] deletedTracker = testSatInstance.getDeleted();
        int deletedTrackerLength = deletedTracker.length;

        // First, we deal with the base cases,
        // if there are no clauses,
        // and if there is an empty clause
        boolean nothingLeft = checkIfEmptyFormula(deletedTracker, deletedTrackerLength);
        if (nothingLeft) {
            return true;
        }

        int[][] allClauses = testSatInstance.getClauses();
//        System.out.println(Arrays.toString(allClauses));
        boolean clauseLengthOneAssigned = false;


        for (int i = 0; i < deletedTrackerLength; i++) {

            // Using deleted tracker to skip over
            // removed clauses
            if (deletedTracker[i] != 1) {

                int[] currentClause = allClauses[i];
                int currentClauseLength = countNumberOfLiteralsIn(currentClause);

                // First check for empty clause
                // if empty clause, just return false
                if (currentClauseLength == 0) {
                    return false;
                }

                if (smallestClause == null) {
                    smallestClause = currentClause;
                    smallestClauseLength = countNumberOfLiteralsIn(currentClause);
                }



                // check if length1 has been found, so skip comparing to get
                // minimum
                if (currentClauseLength == 1 && !clauseLengthOneAssigned) {

                    clauseLengthOneAssigned = true;
                    smallestClause = currentClause;
                } else {
                    // compare and find smallest

                    if (currentClauseLength > smallestClauseLength) {
                        smallestClause = currentClause;
                        smallestClauseLength = currentClauseLength;
                    }
                }

            }
        }


        if (smallestClauseLength == 1) {
            /**
             * The next smallest clause would be if
             * the clauseLength == 1, so we can
             * stop the iteration here
             */

            // Substitute and recurse

            int literal = findLiteralIn(smallestClause);


            // Recursion sequence
            // Store current SatInstance
            // Mutate, add to the static field, recurse
            // if fail, revert static field to storedSatInstance

            // Recursion sequence with undo
            // Store current satInstance
            // when going down, create netestSATinstance
            //
            // if fail, undo stored field



            testSatInstance.givenVarMutator(literal);
            testSatInstance.simplify();

            boolean result = recurSolve();

            if (result) {
                return true;
            } else {
                // revert to the storedSatInstance
                testSatInstance.undoSimplify();
                testSatInstance.undoChanges();
                return false;

            }
        }


        // We are left with clauses that have length > 1
        // We pick an arbitrary literal,
        // TODO optimise find literal
        // substitute in all clauses and solve recursively.
        // If false, repeat but instead with the negated literal.


        int literal = findLiteralIn(smallestClause);

        // Store the SATInstance here, in case the recurtestSATinstance


        testSatInstance.givenVarMutator(literal);
        testSatInstance.simplify();

        boolean result = recurSolve();

        if (result) {
            return true;
        } else {

            testSatInstance.undoSimplify();
            testSatInstance.undoChanges();
            int negatedLiteral = literal*-1;

            testSatInstance.givenVarMutator(negatedLiteral);
            testSatInstance.simplify();
            boolean negatedResult = recurSolve();

            if (negatedResult) {
                return true;
            } else {
                testSatInstance.undoSimplify();
                testSatInstance.undoChanges();
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
    private static int countNumberOfLiteralsIn(int[] clause) {
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

    private static boolean findEmptyClause(CNFSatInstance satInstance) {
        // Iterate through the list of clatestSATinstance/ Use currentSatInstance.getDeleted() to determine if need to skip
        int[] deletedTracker = satInstance.getDeleted();
        int deletedTrackerLength = deletedTracker.length;
        int[][] allClauses = satInstance.getClauses();


        for (int i = 0; i < deletedTrackerLength; i++) {


            if (deletedTracker[i] != 1) {
                int[] aClause = allClauses[i];
                if (countNumberOfLiteralsIn(aClause) == 0) {
                    return true;
                }
            }

        }

        return false;
    }

    /**
     * Uses the deletedTrack array to check if all clauses have been removed,
     * which indicates that the recursed SAT is true
     * @param deletedTrack
     * @param deletedTrackerLength takes a length here, as it has already been
     *                             computed in the main function, so we reduce the
     *                             number of deletedTrack.length calls
     * @return
     */
    private static boolean checkIfEmptyFormula(int[] deletedTrack, int deletedTrackerLength) {

        for (int i = 0; i < deletedTrackerLength; i++) {

            if (deletedTrack[i] == 0) {
                return false;
            }
        }
        return true;

    }
}
