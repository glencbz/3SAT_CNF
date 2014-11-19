package ArrayFormDPLL;

/**
 * Created by JiaHao on 19/11/14.
 */
public class DPLLBetterSolverTest {

    public static void main(String[] args) {

//        ArrayList<Formula> listOfFormulas = AwesomeCnfParser.parseFile("src/sat/largeSat.cnf");
//        ArrayList<Formula> listOfFormulas = AwesomeCnfParser.parseFile("src/sat/largeUnsat.cnf");
//        ArrayList<Formula> listOfFormulas = AwesomeCnfParser.parseFile("src/sat/s8Sat.cnf");
//        ArrayList<Formula> listOfFormulas = AwesomeCnfParser.parseFile("src/sat/aim-50-1_6-yes1-4.cnf");



        System.out.println("Solving Sequence started");
        System.out.println("Parsing file...");
        long parseStarted = System.nanoTime();



        CNFSatInstance testInstance = CNFparser.parseDimacsCnfFile("src/sat/largeunSAT.cnf");

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
