package UsingHQSimplify;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


/**
 * A class written to process a 2-SAT CNF into a strongly connected component graph required 
 * to solve the CNF problem
 * @author Glen Choo, Goh Jia Hao, Lau Siaw Young, Nikhil Sharma, Tan Hao Qin
 *
 */
public class SCCGraph{
	
	//Data used in many calculations
	private int numberOfVariables;
	private int numberOfVertices;
	
	//Contains information for the implication graph. Vertex number goes from 0 to edges.size()
	private List<Integer>[] edges;
	
	//Data used to process the calculation of the Strongly Connected Component
	private Integer[] id;
	private Integer[] lowlink;
	
	private int index;
	private Stack<Integer> stack;
	
	//Actual SCC Graph Data
	public LinkedList<Integer>[] scc;
	public List<Integer>[] sccEdges;
	public Integer[] sccSearch;
	public int sccSize;
	
	//Truthtable that gives a possible answer for the 2-SAT
	public int[] knownAssignments;
	
	/**
	 * Constructor for the SCC Graph used in the CNFSolver
	 * @param numberOfVertices The number of vertices in the graph
	 * @param clauses The clauses in the CNF problem.
	 * @param clauseRemoved 
	 */
	@SuppressWarnings("unchecked")
	public SCCGraph(int numberOfVertices, int[][] clauses, int[] knownAssignments, boolean[] clauseRemoved){	

		this.edges = (List<Integer>[]) new List[numberOfVertices];
		this.numberOfVertices = numberOfVertices;
		this.numberOfVariables = (numberOfVertices-1)/2;
		this.index = 0;
		this.stack = new Stack<Integer>();
		this.id = new Integer[this.numberOfVertices];
		this.lowlink = new Integer[this.numberOfVertices];
		this.scc = (LinkedList<Integer>[]) new LinkedList[numberOfVertices];
		this.sccSearch = new Integer[this.numberOfVertices];
		this.sccSize = 0;
		this.knownAssignments = knownAssignments;	
		
    	for(int i = 0;i < this.numberOfVertices;i++){
    		this.edges[i] = new LinkedList<Integer>();
    	}
    	for(int i = 0;i<clauses.length;i++){
    		if(!clauseRemoved[i]){
        		this.addClause(clauses[i]);
    		}
    	}
    	this.generateSCC();
		
	}
	
	/**
	 * Adds an edge from an initial vertex to a final vertex in the implication graph
	 * @param initialVertex 
	 * @param finalVertex
	 */
	public void addEdge(Integer initialVertex, Integer finalVertex){
		if(!edges[initialVertex].contains(finalVertex)){
			edges[initialVertex].add(finalVertex);
		}
	}
	
	/**
	 * Adds the edges required in a implication graph given a clause 
	 * @param clause
	 */
	public void addClause(int[] clause){
		if (clause.length == 2){
			addEdge(negate(clause[0]), clause[1]);
			addEdge(negate(clause[1]), clause[0]);
		}
	}
	
	/**
	 * Generates the SCC for the implication graph. Uses Tarjan's algorithm
	 */
	private void generateSCC(){
		for (int vertex = 1;vertex < this.numberOfVertices;vertex++){
			if(this.id[vertex] == null){
				visit(vertex);
			}
		}
		this.getSCCEdges();
	}
	
	/**
	 * The recursive function used in Tarjan's algorithm on the nodes in the graph
	 * @param v, the visited node
	 */
	public void visit(int v){
		
		this.id[v] = this.index;
		this.lowlink[v] = this.index;
		this.index++;
		this.stack.add(v);
		
		// Depth first search on successors of V
		
		for(int w: this.edges[v]){
			w = convert(w);
			if(this.id[w] == null){
				visit(w);
				this.lowlink[v] = Math.min(this.lowlink[v],this.lowlink[w]);
			}
			else if(this.stack.contains(w)){
				// Successor exists in the current SCC.
				this.lowlink[v] = Math.min(this.lowlink[v],this.id[w]);
			}
		}
		// V is a root node

		if(this.lowlink[v] == this.id[v]){
			LinkedList<Integer> currentSCC = new LinkedList<Integer>();
			int poppedNode;
			do {
				poppedNode = this.stack.pop();
				this.sccSearch[poppedNode] = sccSize;
				currentSCC.add(convert(poppedNode));
			} while (poppedNode!=v);
			if(currentSCC.size() != 0){
				this.scc[sccSize]=currentSCC;
/*				String out = "SCC "+sccSize+" found including vertices: ";
				for(Integer i: currentSCC){
					out = out.concat(i+" ");
				}
				System.out.println(out);*/
				sccSize+=1;
			}
		}
	}
	
	/**
	 * Evaluates the satisfiability of the CNF problem
	 * @return a boolean value showing its satisfiability, prints out the outcome as well
	 */
	public boolean evaluate(){
		for(int i = 0;i<this.sccSize;i++){
			for(Integer component: this.scc[i]){
				if(this.scc[i].contains(-component)){
					System.out.println("FORMULA UNSATISFIABLE");
					return false;
				}
			}
		}
		System.out.println("FORMULA SATISFIABLE");
		this.getSolution();
		return true;
	}
	
	/**
	 * Calculates the edges in the SCC graph
	 * Complexity O(c) since it examines every single edge in the graph
	 */
	@SuppressWarnings("unchecked")
	private void getSCCEdges(){
		this.sccEdges = new List[this.sccSize];
		for (int i = 0 ; i < this.sccSize ; i++){
			this.sccEdges[i] = new ArrayList<Integer>();
			for (Integer j : this.scc[i]){
				for (Integer k : this.edges[convert(j)]){
					if(!this.sccEdges[i].contains(this.sccSearch[convert(k)])&&this.sccSearch[convert(k)]!=i){
						this.sccEdges[i].add(this.sccSearch[convert(k)]);					
					}
				}
			}		
		}
	}
	/**
	 * Calculates the solution of the formula
	 * Works only after the edges of the SCC graph is calculated
	 * Complexity O(n + c) as it examines every single literal vertex and possibly all edges
	 * @return solution of the formula
	 */
	private int[] getSolution(){
		
			//Recursive assignment of solution
			for(int i = this.sccSize -1; i>=0;i--){
				LinkedList<Integer> components = this.scc[i];
				assignValue(i,components,false);
			}

			for(int i = 0;i<this.knownAssignments.length;i++){
				System.out.print(this.knownAssignments[i]<0?0:this.knownAssignments[i]);
				System.out.print(" ");
				if(i%2000==1999){
					System.out.println();
				}
			}
			System.out.println();
			return this.knownAssignments;
		}
	
	/**
	 * Recursive assignment of a possible truth value for all variables 
	 * in a SCC component graph
	 * @param components individual condensed graph from the SCC
	 * @param value boolean value to be assigned
	 */
	private void assignValue(int i, List<Integer> components,boolean value){
		for(int component:components){
			int assignment = value?1:-1;
			if(this.knownAssignments[Math.abs(component) - 1] == 0){
				this.knownAssignments[Math.abs(component) - 1] = component<0?-assignment:assignment;
				//System.out.println("Literal "+(Math.abs(component) - 1) + " set to " + assignment);
			}
		}
		if (this.sccEdges[i].size()!=0){
			//Accounts for edges from components
			for(Integer j:this.sccEdges[i]){
				assignValue(j,this.scc[j],false);
			}
		}
	}
	
	/**
	 * Calculates the negation of a literal
	 * @param i, the index number of the literal given
	 * @return the index number of the negation of the literal given
	 */
	private Integer negate(Integer i){
		if (i<0){
			return -i;
		}
		else{
			return i+this.numberOfVariables;
		}
	}
	
	/**
	 * Converts between negative indices used in printing and 
	 * positive indices used in calculations.
	 * @param initial index
	 * @return final converted index
	 */
	private Integer convert(Integer i){
		if( i < 0){
			return this.numberOfVariables-i;
		}
		else if(i > this.numberOfVariables){
			return -(i-this.numberOfVariables);
		}
		else{
			return i;
		}
	}
	
	/**
	 * Prints out the details for the implication graph. 
	 * Not used in actual algorithm, only used in debugging.
	 * @return the implication graph in the form of a string 
	 */
	public String printGraph(){
		String out = "";
		for(int i = 1;i < this.numberOfVertices;i++){
			out = out.concat("Vertex "+convert(i)+" contains edges to the following vertices: ");
			String edgeList = "";
			for(Integer j:this.edges[i]){
				edgeList = edgeList.concat(j+" ");
			}
			out = out.concat(edgeList.concat("\n"));
		}
		return out;
	}
	
	/**
	 * Prints out the strongly connected component graph. 
	 * Not used in actual algorithm, only used in debugging.
	 */
	public String toString(){
		StringBuffer out = new StringBuffer();
		for(int i = 0;i < this.sccSize;i++){
			out.append("Vertex "+i+" ( ");
			for(int j = 0;j < this.scc[i].size();j++){
				out.append(this.scc[i].get(j)+" ");
			}
			out.append(") contains edges to the following vertices: ");
			for(Integer j:this.sccEdges[i]){
				if(j!=null){
				out.append(j+" ");
				}
			}
			out.append("\n");
		}
		return out.toString();
	}
}
