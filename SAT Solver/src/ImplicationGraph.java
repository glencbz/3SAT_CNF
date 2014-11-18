import java.util.List;

public class ImplicationGraph {
	
	public int numberOfVariables;
	public int numberOfVertices;
	public List<Integer>[] edges;
	
	@SuppressWarnings("unchecked")
	public ImplicationGraph(int numberOfVertices){
		this.edges = (List<Integer>[]) new List[numberOfVertices];
		this.numberOfVertices = numberOfVertices;
		this.numberOfVariables = (numberOfVertices-1)/2;
	};
	
	public void addEdge(Integer initialVertex, Integer finalVertex){
		if(!edges[initialVertex].contains(finalVertex)){
			edges[initialVertex].add(finalVertex);
		}
	}
	
	public void addClause(List<Integer> clause){
		if (clause.size() == 2){
			addEdge(negate(clause.get(0)), clause.get(1));
			addEdge(negate(clause.get(1)), clause.get(0));
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
}
