package code;

public class MatrixSearchNode extends SearchNode {
	@Override
	public int compareTo(SearchNode o) {

		return this.cost.compareTo(o.cost);
	}
	
	public MatrixSearchNode(int depth,MatrixSearchNode parent,MatrixState state,MatrixOperator operator,Cost cost) {
		super(depth,parent,state,operator,cost);
		
		
	}
	public MatrixState getState() {
		return (MatrixState)this.state;
	}

	
	
	
}
