package UsingHQSimplify;


public class CNFsolver {
	public static void main(String[] args){
		CnfSatInstance CNF = CNFparser.parseDimacsCnfFile("src/sat/generated2sat.cnf");

		long started = System.nanoTime();
		CNF.initialise();
		long intermediate = System.nanoTime() - started;
		System.out.println("Time Taken: "+intermediate/1000000.0 + "ms");
		CNF.solve();
		long timeTaken = System.nanoTime() - started;
		System.out.println("Time Taken: "+timeTaken/1000000.0 + "ms");
		/**CNF.createResolution();**/
	}
}
