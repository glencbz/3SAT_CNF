
public class Main {
	public static void main(String[] args){
		
		CNFsolver test = new CNFsolver(CNFparser.parseDimacsCnfFile("src/s8.cnf"));
		System.out.println(test.getCNF());
		test.PPSZsearch(test.getCNF());
	}
}
