package project2d;

public class CNFsolver {
	public static void main(String[] args){
		CnfSatInstance CNF = CNFparser.parseFile("/Users/Nikhil/Downloads/3SAT_CNF-master/SAT Solver/src/testcase10.cnf");
//		System.out.println(CNF);

		long start_time = System.nanoTime();
		if (CNF.parseRandom()){
			System.out.println("\nFORMULA SATISFIABLE");
		}
		else {
			System.out.println("FORMULA UNSATISFIABLE");
		}
		long end_time = System.nanoTime();
		double difference = (end_time - start_time)/1e6;
		System.out.println(difference + "ms");
	}
}
