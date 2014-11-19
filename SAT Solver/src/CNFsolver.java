
public class CNFsolver {
	public static void main(String[] args){
		CnfSatInstance CNF = CNFparser.parseDimacsCnfFile("src/largeUnSat.cnf");
		System.out.println(CNF);
		long started = System.nanoTime();
		CNF.initialise();
		CNF.solve();
		long timeTaken = System.nanoTime() - started;
		System.out.println("Time Taken: "+timeTaken/1000000.0 + "ms");
		/**CNF.createResolution();**/
	}
}
