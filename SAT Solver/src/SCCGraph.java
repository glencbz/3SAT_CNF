import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class SCCGraph{
	
	public int numberOfVariables;
	public int numberOfVertices;
	public List<Integer>[] edges;
	
	private Integer[] id;
	private Integer[] lowlink;
	
	private int index;
	private Stack<Integer> stack;
	
	public ArrayList<ArrayList<Integer>> scc;
	public List<Integer>[] sccEdges;
	public Integer[] sccSearch;
	public int sccSize;
	public Boolean[] truthtable;
	
	public SCCGraph(ImplicationGraph g){
		
		this.numberOfVariables = g.numberOfVariables;
		this.numberOfVertices = g.numberOfVertices;
		this.edges = g.edges;
		this.index = 0;
		this.stack = new Stack<Integer>();
		this.id = new Integer[this.numberOfVertices];
		this.lowlink = new Integer[this.numberOfVertices];
		this.scc = new ArrayList<ArrayList<Integer>>();
		this.sccSearch = new Integer[this.numberOfVertices];
		this.sccSize = 0;
		this.truthtable = new Boolean[this.numberOfVertices];
		
		for (int vertex = 1;vertex < this.numberOfVertices;vertex++){
			if(this.id[vertex] == null){
				visit(vertex);
			}
		}
		
		for(int i = 0 ; i < this.truthtable.length ; i++){
			this.truthtable[i] = null;
		}
	}
	
	public void visit(int v){
		
		System.out.println("Visiting node "+convert(v));
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
			ArrayList<Integer> currentSCC = new ArrayList<Integer>();
			int poppedNode;
			do {
				poppedNode = this.stack.pop();
				this.sccSearch[poppedNode] = sccSize;
				currentSCC.add(convert(poppedNode));
			} while (poppedNode!=v);
			if(currentSCC.size() != 0){
				this.scc.add(currentSCC);
				String out = "SCC "+sccSize+" found including vertices: ";
				for(Integer i: currentSCC){
					out = out.concat(i+" ");
				}
				System.out.println(out);
				sccSize+=1;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean evaluate(){
		for(ArrayList<Integer> components:this.scc){
			for(Integer component: components){
				if(components.contains(-component)){
					System.out.println("FORMULA UNSATISFIABLE");
					return false;
				}
			}
		}
		System.out.println("FORMULA SATISFIABLE");
		this.sccEdges = new List[this.sccSize];
		for (int i = 0 ; i < this.sccSize ; i++){
			this.sccEdges[i] = new ArrayList<Integer>();
			for (Integer j : this.scc.get(i)){
				for (Integer k : this.edges[convert(j)]){
					if(!this.sccEdges[i].contains(this.sccSearch[convert(k)])&&this.sccSearch[convert(k)]!=i){
						this.sccEdges[i].add(this.sccSearch[convert(k)]);					
					}
				}
			}		
		}
		for(int i = 0; i<this.scc.size();i++){
			ArrayList<Integer> components = this.scc.get(i);
			assignValue(components,false);
			if (this.sccEdges[i].size()!=0){
				for(Integer j:this.sccEdges[i]){
					assignValue(this.scc.get(j),false);
				}
			}
		}
		StringBuffer solution = new StringBuffer();
		for(int i = 1;i<this.truthtable.length/2+1;i++){
			solution.append((this.truthtable[i]?1:0)+" ");
		}
		System.out.println(solution);
		return true;
	}
	
	//Function to assign boolean value to solution
	public void assignValue(ArrayList<Integer> components,boolean value){
		for(int component:components){
			if(this.truthtable[convert(component)] == null){
				System.out.println(component+" is set to "+value);
				System.out.println(convert(negate(component))+" is set to "+!value);
				this.truthtable[convert(component)] = value;
				this.truthtable[negate(component)] = !value;
			}
		}
	}
	
	public Integer negate(Integer i){
		if (i<0){
			return -i;
		}
		else{
			return i+this.numberOfVariables;
		}
	}
	
	//Function to convert negative indexes to positive and vice versa
	public Integer convert(Integer i){
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
		
	public String toString(){
		StringBuffer out = new StringBuffer();
		for(int i = 0;i < this.sccSize;i++){
			out.append("Vertex "+i+" ( ");
			for(int j = 0;j < this.scc.get(i).size();j++){
				out.append(this.scc.get(i).get(j)+" ");
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
