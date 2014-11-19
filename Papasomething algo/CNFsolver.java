package project2d;


public class CNFsolver {
	public static void main(String[] args){
		CnfSatInstance CNF = CNFparser.parseDimacsCnfFile("/Users/Nikhil/Downloads/3SAT_CNF-7ce00e3ab30aede82ef0fa59eb84d01f6230eccc/SAT Solver/src/4t.cnf");
//		System.out.println(CNF);
		System.out.println(CNF);

//		CNF.computeSCCGraph();
//		CNF.solve();		
		/**CNF.computeOccurrenceMap();
		CNF.createResolution();**/
		if (CNF.parseRandom()){
			System.out.println("FORMULA SATISFIABLE");
		}
		else {
			System.out.println("FORMULA UNSATISFIABLE");
		}
	}
}
