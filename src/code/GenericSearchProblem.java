package code;

public abstract class GenericSearchProblem {

	
	
	State initialState;
	Operators operators; 
	
	
	public static String GenericSearch(GenericSearchProblem problem,String strategy,boolean visualization) {
	String solution="";
	
		
		
		SearchNode goal=null;
		switch(strategy) {
		case "BF":
			 goal= problem.solveBF(visualization);
			if(goal==null) {
				return "No Solution";
			}
			else {
				solution=getOperators(goal);
				//solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
				
			}
			
			
			break;
		case "DF": 
			 goal= problem.solveDF(visualization);
			if(goal==null) {
				return "No Solution";
			}
			else {
				solution=getOperators(goal);
				//solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
				
			}
			
			break;
		case "ID":  
			
			 goal= problem.solveID(visualization);
				if(goal==null) {
					return "No Solution";
				}
				else {
					solution=getOperators(goal);
					//solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
					
				}
			
			break;
		case "UC":  
			
			 goal= problem.solveUC(visualization);
				if(goal==null) {
					return "No Solution";
				}
				else {
					solution=getOperators(goal);
					
					
					//solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
					
				}
			
			
			
			
			break;
		case "GR1":  
			
			goal= problem.solveGR1(visualization);
			if(goal==null) {
				return "No Solution";
			}
			else {
				solution=getOperators(goal);
				
				
				//solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
				
			}
		
		
			
			
			break;
		case "GR2":  
			
			
		goal= problem.solveGR2(visualization);
		if(goal==null) {
			return "No Solution";
		}
		else {
			solution=getOperators(goal);
			
			
			//solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
			
		}
	break;
		case "AS1":  
			
			 goal= problem.solveAS1(visualization);
				if(goal==null) {
					return "No Solution";
				}
				else {
					solution=getOperators(goal);
					//solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
					
				}
			
			
			
			
			
			
			
			
			break;
		case "AS2": 
			
			 goal= problem.solveAS2(visualization);
				if(goal==null) {
					return "No Solution";
				}
				else {
					solution=getOperators(goal);
					//solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
					
				}
			
			
			
			
			
			
			break;
		}
		
		

		if(visualization) {
			problem.visualize(goal);
			}
		 
	 
	
	
		
		
		return solution;
		
	}
	public static String getOperators(SearchNode s ) {
		String operators="";
		while(s!=null && s.operator!=null) {
			
			operators=","+s.operator+operators;
			s=(MatrixSearchNode)s.parent;
		}
		return operators.substring(1);
	}
	public abstract SearchNode solveBF(boolean visualization);
	public abstract SearchNode solveDF(boolean visualization);
	public abstract SearchNode solveID(boolean visualization);
	public abstract SearchNode solveGR1(boolean visualization);
	public abstract SearchNode solveGR2(boolean visualization);
	public abstract SearchNode solveAS1(boolean visualization);
	public abstract SearchNode solveAS2(boolean visualization);
	public abstract SearchNode solveUC(boolean visualization);
	
	public abstract boolean goalTest(SearchNode s);
	public abstract Cost pathCost(SearchNode currentNode); 
	public abstract void visualize(SearchNode goal);
	public static void main(String[] args) {
		
	}
	

	

}
