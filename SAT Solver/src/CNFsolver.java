
public class CNFsolver {
	public static void main(String[] args){
		CnfSatInstance CNF = CNFparser.parseDimacsCnfFile("src/test.cnf");
		System.out.println(CNF);
		CNF.computeSCCGraph();
		CNF.solve();		
		/**CNF.computeOccurrenceMap();
		CNF.createResolution();**/
		System.out.println(CNF);
	}
}
