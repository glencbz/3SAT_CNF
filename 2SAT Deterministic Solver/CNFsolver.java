
public class CNFsolver {
	public static void main(String[] args){

		//Replace src/testcase.cnf with the cnf file that you want to solve
		CnfSatInstance CNF = CNFparser.parseCnfFile("src/testcase.cnf");
		long started = System.nanoTime();
		CNF.initialise();
		CNF.solve();
		long timeTaken = System.nanoTime() - started;
		System.out.println("Time Taken: "+timeTaken/1000000.0 + "ms");

	}
}
