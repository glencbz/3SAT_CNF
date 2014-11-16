
public class CNFsolver {
	public static void main(String[] args){
		CnfSatInstance CNF = CNFparser.parseDimacsCnfFile("src/cnf.txt");
		System.out.println(CNF);
		CNF.computeOccurrenceMap();
		CNF.createResolution();
		System.out.println(CNF);
	}
}
