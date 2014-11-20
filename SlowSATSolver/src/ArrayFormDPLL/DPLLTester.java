package ArrayFormDPLL;

/**
 * Created by JiaHao on 20/11/14.
 */
public class DPLLTester {

    public static void main(String[] args) {

        System.out.println("Solving Sequence started");
        System.out.println("Parsing file...");
        long parseStarted = System.nanoTime();



//                CNFSatInstance testInstance = CNFparser.parseDimacsCnfFile("src/sat/largeSat.cnf");
            CNFSatInstance testInstance = CNFparser.parseDimacsCnfFile("src/sat/aim-50-1_6-yes1-4.cnf");
//        CNFSatInstance testInstance = CNFparser.parseDimacsCnfFile("src/sat/generated2SAT.cnf");
//        CNFSatInstance testInstance = CNFparser.parseDimacsCnfFile("src/sat/s8SatSmaller.cnf");

        long parseTime = System.nanoTime();
        long parseTimeTaken = parseTime - parseStarted;

        System.out.println("Parsing completed in " + parseTimeTaken/1000000.0 + "ms");
        System.out.println("Solving formulas...");

        long started = System.nanoTime();
        EvenBetterDPLLSolver.solve(testInstance);

        long time = System.nanoTime();
        long timeTaken = time - started;

        System.out.println("Solving completed in " + timeTaken/1000000.0 + "ms");
    }
}
