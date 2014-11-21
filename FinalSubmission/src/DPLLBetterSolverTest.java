/**
 * Tester for SATSolver
 * Team 8 ISTD
 * 21 Nov 2014
 */
public class DPLLBetterSolverTest {

    public static void main(String[] args) {


        System.out.println("Solving Sequence started");
        System.out.println("Parsing file...");
        long parseStarted = System.nanoTime();

        CNFSatInstance testInstance = CNFparser.parseDimacsCnfFile("src/sat1.cnf");

        long parseTime = System.nanoTime();
        long parseTimeTaken = parseTime - parseStarted;

        System.out.println("Parsing completed in " + parseTimeTaken/1000000.0 + "ms");
        System.out.println("Solving formulas...");

        long started = System.nanoTime();
        DPLLBetterSolver.solve(testInstance);

        long time = System.nanoTime();
        long timeTaken = time - started;

        System.out.println("Solving completed in " + timeTaken/1000000.0 + "ms");
    }

}
