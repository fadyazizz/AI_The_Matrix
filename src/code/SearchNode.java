package code;


public abstract class SearchNode implements Comparable<SearchNode> {
	
	
	int depth;
	
	Operator operator;
	State state;
	SearchNode parent;
	Cost cost;

	public SearchNode(int depth,SearchNode parent,State state,Operator operator,Cost cost) {
		this.depth=depth;
		this.parent=parent;
		this.state=state;
		this.operator=operator;
		this.cost=cost;
	}

	

}
