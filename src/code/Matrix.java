package code;


import java.util.*;

public class Matrix extends GenericSearchProblem {
	
	int width;
	int height;
	int maxNumberToCarry;
	List<Integer> telephoneBooth;
	Hashtable<List<Integer>,List<Integer>> pads;
	int numberOfExpandedNodes=0;
	
	Hashtable<String,Object> generatedStates=new Hashtable<String, Object>();
	
	@Override
	public Cost pathCost(SearchNode currentNode) {
		
		MatrixSearchNode current=(MatrixSearchNode)currentNode;
		
		MatrixState currentState=current.getState();
		
		
		
		MatrixCost newCost=new MatrixCost( currentState.getNumberOfDeaths(), currentState.getNumberOfKills(),current.depth );
		
		
		return newCost;
	}
	
	public  MatrixCost calculateHeuristic1(MatrixSearchNode s) {
		MatrixCost costFromSavingAHostage=getCostToSaveNearstHostage(s);
		MatrixCost costToKillMutantAgents=calculateCostToKillMutantAgents(s);
		MatrixCost costToReturnToTheTB=new MatrixCost(0, 0,  getMinimumDistance(s.getState().getNeoPos(), this.telephoneBooth));
		MatrixCost costToDrop=new MatrixCost(0, 0, 1);
		
		
		return costFromSavingAHostage.add(costToKillMutantAgents).add(costToDrop).add(costToReturnToTheTB);
	}
	public  MatrixCost calculateHeuristic2(MatrixSearchNode s) {
		MatrixCost costFromSavingAHostage=getCostToCarryHostages(s);
		MatrixCost costToKillMutantAgents=calculateCostToKillMutantAgents(s);
		MatrixCost costToReturnToTheTB=new MatrixCost(0, 0,  getMinimumDistance(s.getState().getNeoPos(), this.telephoneBooth));
		MatrixCost costToDrop=new MatrixCost(0, 0, 1);
		
		
		return costFromSavingAHostage.add(costToKillMutantAgents).add(costToDrop).add(costToReturnToTheTB);
	}

	public MatrixCost getCostToCarryHostages(MatrixSearchNode  s) {
		MatrixState state=s.getState();
	
		List<Integer> allDamagesSorted=this.getAllDamagesSortedDescendingly(s);
		int countDead=0;
		int totalSteps=0;
		List<Integer> nearstPill=getNearstPill(s);
		for(int i=0;i<allDamagesSorted.size()&&state.getNumberOfCarriedHostages()<maxNumberToCarry;i++) {
			List<Integer> hostagePos=getHostageNearstToMeWithMaxDamage(s, allDamagesSorted.get(i));
			int numberOfStepsToHostage=getMinimumDistance(state.getNeoPos(), hostagePos);
			totalSteps=numberOfStepsToHostage;
			int damage=2*totalSteps;
			//if will die try take the pill
			if(state.getHostages().get(hostagePos)+damage>100) {
				if(nearstPill!=null) {
					int stepsToPill=getMinimumDistance(state.getNeoPos(), nearstPill);
					int stepsFromPillToAHostage=getMinimumDistance(nearstPill, hostagePos);
					totalSteps=(stepsToPill+stepsFromPillToAHostage);
					damage=2*totalSteps;
					if(state.getHostages().get(hostagePos)+damage-20<100) {
						break;
					}
				}
				
			}else {
				break;
			}
			
			countDead++;
			
	
	}
		if(countDead==allDamagesSorted.size()) {
			totalSteps=0;
		}
		MatrixCost cost= new MatrixCost(countDead, 0, totalSteps);
		return cost;
		
	}
	
	
	//relax the problem by considering only one to die
	
	private MatrixCost getCostToSaveNearstHostage(MatrixSearchNode s) {
		MatrixState state=s.getState();
		List<Integer> allDamagesSorted=this.getAllDamagesSortedDescendingly(s);
		int countDead=0;
		int totalSteps=0;
		List<Integer> nearstPill=getNearstPill(s);
		for(int i=0;i<allDamagesSorted.size()&&state.getNumberOfCarriedHostages()<maxNumberToCarry;i++) {
			List<Integer> hostagePos=getHostageNearstToMeWithMaxDamage(s, allDamagesSorted.get(i));
			int numberOfStepsToHostage=getMinimumDistance(state.getNeoPos(), hostagePos);
			int numberOfStepsToTB=getMinimumDistance(hostagePos, telephoneBooth);
			totalSteps=(numberOfStepsToHostage+numberOfStepsToTB);
			int damage=2*totalSteps;
			
			
			//if will die try take the pill
			if(state.getHostages().get(hostagePos)+damage>100) {
				if(nearstPill!=null) {
					int stepsToPill=getMinimumDistance(state.getNeoPos(), nearstPill);
					int stepsFromPillToAHostage=getMinimumDistance(nearstPill, hostagePos);
					totalSteps=(stepsToPill+stepsFromPillToAHostage+numberOfStepsToTB);
					damage=2*totalSteps;
					if(state.getHostages().get(hostagePos)+damage-20<100) {
						break;
					}
				}
				
			}else {
				break;
			}
			
			countDead++;
			
		}
		if(countDead==allDamagesSorted.size()) {
			totalSteps=0;
		}
		MatrixCost cost= new MatrixCost(countDead, 0, totalSteps);
		return cost;
		
	}
	
	public List<Integer> getHostageNearstToMeWithMaxDamage(MatrixSearchNode s,int maxDamage ){

	       //Transfer as List and sort it
		MatrixState state=s.getState();
		List<Integer> neoPos=state.getNeoPos();
		Hashtable<List<Integer>, ?> t=state.getHostages();
	       ArrayList<Map.Entry<?, Integer>> l = new ArrayList(t.entrySet());
	    
	       l.removeIf(x->x.getValue()!=maxDamage );
	       Collections.sort(l, new Comparator<Map.Entry<?, Integer>>(){

		         public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
		        	 List<Integer> host1=(List<Integer>)o1.getKey();
		        	 List<Integer> host2=(List<Integer>)o2.getKey();
		            return compareFunc(host1, host2);
		        }
		         public int compareFunc(List<Integer> host1, List<Integer> host2) {
		        	 int stepsBetweenNeoHost1=getMinimumDistance(neoPos, host1);
		        	 int stepsBetweenNeoHost2=getMinimumDistance(neoPos, host2);
		        	 if(stepsBetweenNeoHost1>stepsBetweenNeoHost2) {
		        		 return 1;
		        	 }
		        	 if(stepsBetweenNeoHost1<stepsBetweenNeoHost2) {
		        		 return -1;
		        	 }
		        	 return 0;
		        	 
		        	 
		         }
	       
	       });
	       
	       List<Integer> hostage=(List<Integer>)l.get(0).getKey();
	       
	       return hostage;
	    }
	
	
	private List<Integer> getNearstPill(MatrixSearchNode s){
		MatrixState state=s.getState();
		List<Integer> nearstPill=null;
		int nearstDistanceToPill=Integer.MAX_VALUE;
		Enumeration<List<Integer>> enumKey = state.getPills().keys();
		
		while(enumKey.hasMoreElements()) {
			List<Integer> pillPos=enumKey.nextElement();
			int distance=getMinimumDistance(state.getNeoPos(), pillPos);
			if(distance<nearstDistanceToPill) {
				nearstDistanceToPill=distance;
				nearstPill=pillPos;
			}
			
		}
		return nearstPill;
		
		
	}

	private List<Integer> getAllDamagesSortedDescendingly(MatrixSearchNode s){
		
		MatrixState state=s.getState();
		Hashtable<List<Integer>, ?> t=state.getHostages();
	       ArrayList<Map.Entry<?, Integer>> l = new ArrayList(t.entrySet());
	    
	       Collections.sort(l, new Comparator<Map.Entry<?, Integer>>(){

	           public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
	              return -1*o1.getValue().compareTo(o2.getValue());
	          }});
	       ArrayList<Integer> damages=new ArrayList<Integer>();
	       for(int i=0;i<l.size();i++) {
	    	   damages.add(l.get(i).getValue());
	       }
	       return MatrixUtil.removeDuplicates(damages);
		
	}
	
	private  MatrixCost calculateCostToKillMutantAgents(MatrixSearchNode s) {
		
		MatrixState state=s.getState();
		List<Integer> neoPos=state.getNeoPos();
		Enumeration<List<Integer>> enumKey = state.getMutatedAgents().keys();
		int totalNumberOfStepsToKillMutantAgents=0;
		while(enumKey.hasMoreElements()) {
			List<Integer> mutatedAgentsPos=enumKey.nextElement();
			int minDistance=this.getMinimumDistance(neoPos, mutatedAgentsPos);
			totalNumberOfStepsToKillMutantAgents+=minDistance;
		}
		int killsCost=state.getMutatedAgents().size();
		MatrixCost cost=new MatrixCost(0, killsCost, totalNumberOfStepsToKillMutantAgents);
		
		
		
		return cost;
	}
	
	private  int getMinimumDistance(List<Integer> neoPos,List<Integer> otherObject) {
		
		Hashtable<List<Integer>,List<Integer>> pads=this.pads;
		int minNumberOfSteps=Integer.MAX_VALUE;
		Enumeration<List<Integer>> enumKey = pads.keys();
		while(enumKey.hasMoreElements()) {
			List<Integer> padsPos=enumKey.nextElement();
			List<Integer> padPortal=pads.get(padsPos);
			int distanceToPad=this.getNumberOfSteps(neoPos, padsPos);
			int distanceFromOtherPadToAgent=this.getNumberOfSteps(padPortal, otherObject);
			int totalDistance=distanceToPad+distanceFromOtherPadToAgent;
			if(totalDistance<minNumberOfSteps) {
				minNumberOfSteps=totalDistance;
			}
		}
		int directNumberOfSteps=this.getNumberOfSteps(neoPos, otherObject);
		if(directNumberOfSteps<minNumberOfSteps) {
			minNumberOfSteps=directNumberOfSteps;
		}
		
		return minNumberOfSteps;
	}
	private int getNumberOfSteps(List<Integer> neoPos,List<Integer> otherObject) {
		int neoX=neoPos.get(0);
		int neoY=neoPos.get(1);
		
		int otherObjectX=otherObject.get(0);
		int otherObjectY=otherObject.get(1);
		
		
		return Math.abs(neoX-otherObjectX)+Math.abs(neoY-otherObjectY);
	}
	public Matrix(String grid) {
		this.initialState=this.getInitialStateFromGrid(grid);
		this.operators=new MatrixOperators();
	
	}
	public boolean goalTest(SearchNode s) {
		MatrixSearchNode node=(MatrixSearchNode)s;
		MatrixState state=node.getState();
		
		if(state.getCarriedHostages().size()==0 && state.getMutatedAgents().size()==0 && state.getHostages().size()==0 && state.getNeoDamage()<100 && telephoneBooth.equals(state.getNeoPos()) ) {
			return true;
		}
		return false;
	}
	
	private  MatrixState getInitialStateFromGrid(String grid) {
		String[] gridAsAnArray=grid.split(";",-1);
		String[] gridSize=MatrixUtil.getGridSize(gridAsAnArray);
		width=Integer.parseInt(gridSize[0]);
		height=Integer.parseInt(gridSize[1]);
		maxNumberToCarry=MatrixUtil.getMaxnumberToCarry(gridAsAnArray);
		List<Integer> neoPos=MatrixUtil.getNeoPos(gridAsAnArray);
		telephoneBooth=MatrixUtil.getTelephoneBoothCoordinates(gridAsAnArray);
		
		Hashtable<List<Integer>,Object> agents=MatrixUtil.getAgents(gridAsAnArray);
		Hashtable<List<Integer>,Object> pills=MatrixUtil.getPills(gridAsAnArray);
		pads=MatrixUtil.getPads(gridAsAnArray); 
		Hashtable<List<Integer>,Integer> hostages=MatrixUtil.getHostages(gridAsAnArray);
		Hashtable<List<Integer>,Object> mutatedAgents=new Hashtable<List<Integer>, Object>();
		Hashtable<List<Integer>,Integer> carriedHostages=new Hashtable<List<Integer>, Integer>();
		MatrixState state=new MatrixState(0, 0, 0, 0, neoPos, hostages, pills, agents,mutatedAgents,carriedHostages);
		
		
		return state;

	}
	
	

	public void visualize(SearchNode goal) {
		MatrixSearchNode goalNode=(MatrixSearchNode)goal;
		ArrayList<MatrixSearchNode> listOfNodes=new ArrayList<MatrixSearchNode>();
		listOfNodes.add(goalNode);
		while(goalNode.parent!=null) {
			goalNode=(MatrixSearchNode)goalNode.parent;
			listOfNodes.add(goalNode);
		}
		for(int i=listOfNodes.size()-1;i>=0;i--) {
			MatrixUtil.visualize(this, listOfNodes.get(i));
		}
		
	}
	
	
	
	public static String solve(String grid,String strategy,boolean visualization) {
		
		Matrix matrix=new Matrix(grid);
		
		String solution="";
		
		
		
		MatrixSearchNode goal=null;
		switch(strategy) {
		case "BF":
			 goal= matrix.solveBF(visualization);
			 
			if(goal==null) {
				return "No Solution";
			}
			else {
				solution=getOperators(goal);
				solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
				
			}
			
			
			break;
		case "DF": 
			 goal= matrix.solveDF(visualization);
			if(goal==null) {
				return "No Solution";
			}
			else {
				solution=getOperators(goal);
				solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
				
			}
			
			break;
		case "ID":  
			
			 goal= matrix.solveID(visualization);
				if(goal==null) {
					return "No Solution";
				}
				else {
					solution=getOperators(goal);
					solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
					
				}
			
			
			
			
			break;
		case "UC":  
			
			 goal= matrix.solveUC(visualization);
				if(goal==null) {
					return "No Solution";
				}
				else {
					solution=getOperators(goal);
					
					
					solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
					
				}
			
			
			
			
			break;
		case "GR1":  
			
			goal= matrix.solveGR1(visualization);
			if(goal==null) {
				return "No Solution";
			}
			else {
				solution=getOperators(goal);
				
				
				solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
				
			}
		
		
			
			
			break;
		case "GR2":  
			
			
		goal= matrix.solveGR2(visualization);
		if(goal==null) {
			return "No Solution";
		}
		else {
			solution=getOperators(goal);
			
			
			solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
			
		}
	break;
		case "AS1":  
			
			 goal= matrix.solveAS1(visualization);
				if(goal==null) {
					return "No Solution";
				}
				else {
					solution=getOperators(goal);
					solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
					
				}
			
			
			
			
			
			
			
			
			break;
		case "AS2": 
			
			 goal= matrix.solveAS2(visualization);
				if(goal==null) {
					return "No Solution";
				}
				else {
					solution=getOperators(goal);
					solution=solution+";"+goal.getState().getNumberOfDeaths()+";"+goal.getState().getNumberOfKills()+";"+matrix.numberOfExpandedNodes;
					
				}
			
			
			
			
			
			
			break;
		}
		
		if(visualization) {
		matrix.visualize(goal);
		}
	 
	
	
		
		
		return solution;
		
	}
	
	
	
	//return the node that passes the goal test, and null if 


	public  MatrixSearchNode solveBF(boolean visualization) {
		
		MatrixSearchNode initialNode=new MatrixSearchNode(0, null, (MatrixState)this.initialState, null,null);
		Queue<MatrixSearchNode> queue=new LinkedList<MatrixSearchNode>();
		String stateId=MatrixUtil.getStateId(initialNode);
		generatedStates.put(stateId, "");
		queue.add(initialNode);
		
		while(!queue.isEmpty()) {
			
			MatrixSearchNode s=queue.remove();
			
			if(this.goalTest(s)) {
				return s;
			}
			List<MatrixSearchNode> expandedNodes=expand(s);
			numberOfExpandedNodes++;
			for(int i=0;i<expandedNodes.size();i++) {
				stateId=MatrixUtil.getStateId(expandedNodes.get(i));
				if(!generatedStates.containsKey(stateId)) {
					generatedStates.put(stateId, "");
					
					queue.add(expandedNodes.get(i));	
				}
				
			
			}
		}
		return null;
		
		
	}
	

	public  MatrixSearchNode solveDF(boolean visualization) {
		
		MatrixSearchNode initialNode=new MatrixSearchNode(0, null, (MatrixState)this.initialState, null,null);
		LinkedList<MatrixSearchNode> queue=new LinkedList<MatrixSearchNode>();
		
		
		String stateId=MatrixUtil.getStateId(initialNode);
		generatedStates.put(stateId, "");
		queue.addFirst(initialNode);
		
		while(!queue.isEmpty()) {
			
			MatrixSearchNode s=queue.removeFirst();
			
			if(this.goalTest(s)) {
				return s;
			}
			List<MatrixSearchNode> expandedNodes=expand(s);
			numberOfExpandedNodes++;
			for(int i=expandedNodes.size()-1;i>=0;i--) {
				stateId=MatrixUtil.getStateId(expandedNodes.get(i));
				if(!generatedStates.containsKey(stateId)) {
					generatedStates.put(stateId, "");
					
					queue.addFirst(expandedNodes.get(i));	
					
				}
				
			
			}
		}
		return null;
		
		
	}
	public MatrixSearchNode solveID(boolean visualization) {
	int depth=0;
	MatrixSearchNode goal;
	
	while(true) {
		goal=depthLimitedSearch(depth);
		depth++;
		if(goal!=null && goal.depth==-100) {
			return null;
		}
		if(goal!=null && goal.depth!=-100) {
			return goal;
		}
		
		
	}
		
	}
	private MatrixSearchNode depthLimitedSearch(int depth) {
		MatrixSearchNode initialNode=new MatrixSearchNode(0, null, (MatrixState)this.initialState, null,null);
		LinkedList<MatrixSearchNode> queue=new LinkedList<MatrixSearchNode>();
		
		
		String stateId=MatrixUtil.getStateId(initialNode);
		generatedStates.clear();
		generatedStates.put(stateId, "");
		
		queue.addFirst(initialNode);
		MatrixSearchNode s;
		int maxReachedDepth=0;
		while(!queue.isEmpty()) {
			
			 s=queue.removeFirst();
			
			
			if(this.goalTest(s)) {
				return s;
			}
			if(s.depth>maxReachedDepth) {
				maxReachedDepth=s.depth;
			}
			if(depth==s.depth) {
				
				continue;
			}
			
			List<MatrixSearchNode> expandedNodes=expand(s);
			numberOfExpandedNodes++;
			for(int i=expandedNodes.size()-1;i>=0;i--) {
				
				stateId=MatrixUtil.getStateId(expandedNodes.get(i));
				if(!generatedStates.containsKey(stateId)) {
					generatedStates.put(stateId, "");
				
					queue.addFirst(expandedNodes.get(i));	
					
				}
				
			
			}

		}
		if(maxReachedDepth<depth) {
			 return new MatrixSearchNode(-100, null, null, null,null);
		}
		
		return null;
	}
	
	

	public  MatrixSearchNode solveUC(boolean visualization) {
		
		MatrixSearchNode initialNode=new MatrixSearchNode(0, null, (MatrixState)this.initialState, null,new MatrixCost(0,0,0));
		 PriorityQueue<MatrixSearchNode> queue = new PriorityQueue<MatrixSearchNode>();
		
		
		String stateId=MatrixUtil.getStateId(initialNode);
		generatedStates.put(stateId, "");
		MatrixCost initialCost=new MatrixCost(0, 0, 0);
		initialNode.cost=initialCost;
		queue.add(initialNode);
		MatrixSearchNode temp;
		while(!queue.isEmpty()) {
			
			MatrixSearchNode s=queue.remove();
			
			if(this.goalTest(s)) {
				return s;
			}
			List<MatrixSearchNode> expandedNodes=expand(s);
			numberOfExpandedNodes++;
			
			for(int i=0;i<expandedNodes.size();i++) {
				temp=expandedNodes.get(i);
				stateId=MatrixUtil.getStateId(temp);
				
				if(!generatedStates.containsKey(stateId)) {
					generatedStates.put(stateId, "");
					MatrixCost newCost=(MatrixCost)this.pathCost( temp);
					temp.cost=newCost;
					queue.add(temp);	
					
				}
				
			
			}
		}
		return null;
		
		
	}

	public  MatrixSearchNode solveAS1(boolean visualization) {
		
		MatrixSearchNode initialNode=new MatrixSearchNode(0, null, (MatrixState)this.initialState, null,new MatrixCost(0,0,0));
		 PriorityQueue<MatrixSearchNode> queue = new PriorityQueue<MatrixSearchNode>();
		
		
		String stateId=MatrixUtil.getStateId(initialNode);
		generatedStates.put(stateId, "");
		queue.add(initialNode);
		MatrixSearchNode temp;
		while(!queue.isEmpty()) {
			
			MatrixSearchNode s=queue.remove();
			
			if(this.goalTest(s)) {
				return s;
			}
			List<MatrixSearchNode> expandedNodes=expand(s);
			numberOfExpandedNodes++;
			
			for(int i=0;i<expandedNodes.size();i++) {
				temp=expandedNodes.get(i);
				stateId=MatrixUtil.getStateId(temp);
				
				if(!generatedStates.containsKey(stateId)) {
					generatedStates.put(stateId, "");
					MatrixCost newCost=(MatrixCost)this.pathCost( temp);
					MatrixCost heuristic=this.calculateHeuristic1(temp);
					//printOverShoot(heuristic, new MatrixCost(0, 0, 8));
					temp.cost=newCost.add(heuristic);
					queue.add(temp);	
					
				}
				
			
			}
		}
		return null;
		
		
	}
	public  MatrixSearchNode solveAS2(boolean visualization) {
		
		MatrixSearchNode initialNode=new MatrixSearchNode(0, null, (MatrixState)this.initialState, null,new MatrixCost(0,0,0));
		 PriorityQueue<MatrixSearchNode> queue = new PriorityQueue<MatrixSearchNode>();
		
		
		String stateId=MatrixUtil.getStateId(initialNode);
		generatedStates.put(stateId, "");
		queue.add(initialNode);
		MatrixSearchNode temp;
		while(!queue.isEmpty()) {
			
			MatrixSearchNode s=queue.remove();
			
			if(this.goalTest(s)) {
				return s;
			}
			List<MatrixSearchNode> expandedNodes=expand(s);
			numberOfExpandedNodes++;
			
			for(int i=0;i<expandedNodes.size();i++) {
				temp=expandedNodes.get(i);
				stateId=MatrixUtil.getStateId(temp);
				
				if(!generatedStates.containsKey(stateId)) {
					generatedStates.put(stateId, "");
					MatrixCost newCost=(MatrixCost)this.pathCost( temp);
					MatrixCost heuristic=this.calculateHeuristic2(temp);
					//printOverShoot(heuristic, new MatrixCost(0, 0, 8));
					temp.cost=newCost.add(heuristic);
					queue.add(temp);	
					
				}
				
			
			}
		}
		return null;
		
		
	}
	public  MatrixSearchNode solveGR1(boolean visualization) {
		
		MatrixSearchNode initialNode=new MatrixSearchNode(0, null, (MatrixState)this.initialState, null,new MatrixCost(0,0,0));
		 PriorityQueue<MatrixSearchNode> queue = new PriorityQueue<MatrixSearchNode>();
		
		
		String stateId=MatrixUtil.getStateId(initialNode);
		generatedStates.put(stateId, "");
		queue.add(initialNode);
		MatrixSearchNode temp;
		while(!queue.isEmpty()) {
			
			MatrixSearchNode s=queue.remove();
			
			if(this.goalTest(s)) {
				return s;
			}
			List<MatrixSearchNode> expandedNodes=expand(s);
			numberOfExpandedNodes++;
			
			for(int i=0;i<expandedNodes.size();i++) {
				temp=expandedNodes.get(i);
				stateId=MatrixUtil.getStateId(temp);
				
				if(!generatedStates.containsKey(stateId)) {
					generatedStates.put(stateId, "");
					
					MatrixCost heuristic=this.calculateHeuristic1(temp);
					//printOverShoot(heuristic, new MatrixCost(0, 0, 8));
					temp.cost=heuristic;
					queue.add(temp);	
					
				}
				
			
			}
		}
		return null;
		
		
	}
	
public  MatrixSearchNode solveGR2(boolean visualization) {
		
		MatrixSearchNode initialNode=new MatrixSearchNode(0, null, (MatrixState)this.initialState, null,new MatrixCost(0,0,0));
		 PriorityQueue<MatrixSearchNode> queue = new PriorityQueue<MatrixSearchNode>();
		
		
		String stateId=MatrixUtil.getStateId(initialNode);
		generatedStates.put(stateId, "");
		queue.add(initialNode);
		MatrixSearchNode temp;
		while(!queue.isEmpty()) {
			
			MatrixSearchNode s=queue.remove();
			
			if(this.goalTest(s)) {
				return s;
			}
			List<MatrixSearchNode> expandedNodes=expand(s);
			numberOfExpandedNodes++;
			
			for(int i=0;i<expandedNodes.size();i++) {
				temp=expandedNodes.get(i);
				stateId=MatrixUtil.getStateId(temp);
				
				if(!generatedStates.containsKey(stateId)) {
					generatedStates.put(stateId, "");
					
					MatrixCost heuristic=this.calculateHeuristic2(temp);
					//printOverShoot(heuristic, new MatrixCost(0, 0, 8));
					temp.cost=heuristic;
					queue.add(temp);	
					
				}
				
			
			}
		}
		return null;
		
		
	}
	
	

	private  List<MatrixSearchNode> expand(MatrixSearchNode s) {
		
		MatrixSearchNode upExpansion=applyMoveUp(s);
		MatrixSearchNode downExpansion=applyMoveDown(s);
		MatrixSearchNode leftExpansion=applyMoveLeft(s);
		MatrixSearchNode rightExpansion=applyMoveRight(s);
		MatrixSearchNode carryExpansion=applyCarry(s);
		MatrixSearchNode dropExpansion=applyDrop(s);
		MatrixSearchNode takePillExpansion=applyTakePill(s);
		MatrixSearchNode killExpansion=applyKill(s);
		MatrixSearchNode flyExpansion=applyFly(s);
		
		List<MatrixSearchNode> newNodes=new ArrayList<MatrixSearchNode>();
		if(upExpansion!=null) {
			newNodes.add(upExpansion);
		}
		if(downExpansion!=null) {
			newNodes.add(downExpansion);
		}
		if(leftExpansion!=null) {
			newNodes.add(leftExpansion);
		}
		if(rightExpansion!=null) {
			newNodes.add(rightExpansion);
		}
		if(carryExpansion!=null) {
			newNodes.add(carryExpansion);
		}
		if(dropExpansion!=null) {
			newNodes.add(dropExpansion);
		}
		if(takePillExpansion!=null) {
			newNodes.add(takePillExpansion);
		}
		if(killExpansion!=null) {
			newNodes.add(killExpansion);
		}
		if(flyExpansion!=null) {
			newNodes.add(flyExpansion);
		}
		
		
		return newNodes;
		
	}
	
	private  MatrixSearchNode applyMoveUp(MatrixSearchNode s) {
	
		MatrixState state=s.getState();
		
		int neoY=state.getNeoY();
		if(neoY-1==-1) {
			return null;
		}
		
		if(state.isThereAnAgentAboveMe()) {
			return null;
		}
		if(state.isThereAHostageAboutToDie(state.getNeoX(),state.getNeoY()-1)) {
			return null;
		}
		List<Integer> newNeoPos=new ArrayList<Integer>();
		newNeoPos.add(state.getNeoX());
		newNeoPos.add(neoY-1);
		Hashtable<List<Integer>, Integer> newHostages=new Hashtable<List<Integer>, Integer>();
		
		Hashtable<List<Integer>,Object> newMutatedAgents=new Hashtable<List<Integer>, Object>();
		
		Hashtable<List<Integer>, Integer> newCarriedHostages=new Hashtable<List<Integer>, Integer>();
		int newDeath=MatrixUtil.increaseDamageBy2(s, newHostages, newMutatedAgents,  newCarriedHostages);
		
	
		
		MatrixState newState=new MatrixState(state.getNumberOfCarriedHostages(),
											state.getNumberOfDeaths()+newDeath,
											state.getNumberOfKills(),
											state.getNeoDamage(),
											
											newNeoPos,
											newHostages,
											state.getPills(),
											state.getAgents(),
											newMutatedAgents,
											newCarriedHostages);
		
		MatrixSearchNode node=new MatrixSearchNode(s.depth+1, s, newState,((MatrixOperators)this.operators).getUp(),null);
		
		
		
		
		return node;
		
		
		
		
	}
	private  MatrixSearchNode applyMoveDown(MatrixSearchNode s) {
		MatrixState state=s.getState();
		
		int neoY=state.getNeoY();
		if(neoY+1==this.height) {
			return null;
		}
		
		if(state.isThereAnAgentBelowMe()) {
			return null;
		}
		if(state.isThereAHostageAboutToDie(state.getNeoX(),state.getNeoY()+1)) {
			return null;
		}
		List<Integer> newNeoPos=new ArrayList<Integer>();
		newNeoPos.add(state.getNeoX());
		newNeoPos.add(neoY+1);
		Hashtable<List<Integer>, Integer> newHostages=new Hashtable<List<Integer>, Integer>();
		
		Hashtable<List<Integer>,Object> newMutatedAgents=new Hashtable<List<Integer>, Object>();
		Hashtable<List<Integer>, Integer> newCarriedHostages=new Hashtable<List<Integer>, Integer>();
		int newDeath=MatrixUtil.increaseDamageBy2(s, newHostages, newMutatedAgents,  newCarriedHostages);
		
		
		
		MatrixState newState=new MatrixState(state.getNumberOfCarriedHostages(),
											state.getNumberOfDeaths()+newDeath,
											state.getNumberOfKills(),
											state.getNeoDamage(),
									
											newNeoPos,
											newHostages,
											state.getPills(),
											state.getAgents(),
											newMutatedAgents,
											newCarriedHostages);
		
		MatrixSearchNode node=new MatrixSearchNode(s.depth+1, s, newState, ((MatrixOperators)this.operators).getDown(),null);
		
		
		
		
		return node;
	}
	private MatrixSearchNode applyMoveRight(MatrixSearchNode s) {
		MatrixState state=s.getState();
		
		int neoX=state.getNeoX();
		if(neoX+1==this.width) {
			return null;
		}
		
		if(state.isThereAnAgentToTheRight()) {
			return null;
		}
		if(state.isThereAHostageAboutToDie(state.getNeoX()+1,state.getNeoY())) {
			return null;
		}
		List<Integer> newNeoPos=new ArrayList<Integer>();
		newNeoPos.add(neoX+1);
		newNeoPos.add(state.getNeoY());
		Hashtable<List<Integer>, Integer> newHostages=new Hashtable<List<Integer>, Integer>();
		
		Hashtable<List<Integer>,Object> newMutatedAgents=new Hashtable<List<Integer>, Object>();
		Hashtable<List<Integer>, Integer> newCarriedHostages=new Hashtable<List<Integer>, Integer>();
		
		int newDeath=MatrixUtil.increaseDamageBy2(s, newHostages, newMutatedAgents,  newCarriedHostages);
		
		MatrixState newState=new MatrixState(state.getNumberOfCarriedHostages(),
											state.getNumberOfDeaths()+newDeath,
											state.getNumberOfKills(),
											state.getNeoDamage(),
										
											newNeoPos,
											newHostages,
											state.getPills(),
											state.getAgents(),
											newMutatedAgents,
											newCarriedHostages);
		
		MatrixSearchNode node=new MatrixSearchNode(s.depth+1, s, newState, ((MatrixOperators)this.operators).getRight(),null);
		
		
		
		
		return node;
	}
	private  MatrixSearchNode applyMoveLeft(MatrixSearchNode s) {
		MatrixState state=s.getState();
		
		int neoX=state.getNeoX();
		if(neoX-1==-1) {
			return null;
		}
		
		if(state.isThereAnAgentToTheLeft()) {
			return null;
		}
		if(state.isThereAHostageAboutToDie(state.getNeoX()-1,state.getNeoY())) {
			return null;
		}
		List<Integer> newNeoPos=new ArrayList<Integer>();
		newNeoPos.add(neoX-1);
		newNeoPos.add(state.getNeoY());
		Hashtable<List<Integer>, Integer> newHostages=new Hashtable<List<Integer>, Integer>();
		
		Hashtable<List<Integer>,Object> newMutatedAgents=new Hashtable<List<Integer>, Object>();
		
		Hashtable<List<Integer>, Integer> newCarriedHostages=new Hashtable<List<Integer>, Integer>();
		int newDeath=MatrixUtil.increaseDamageBy2(s, newHostages, newMutatedAgents,  newCarriedHostages);
		
		MatrixState newState=new MatrixState(state.getNumberOfCarriedHostages(),
											state.getNumberOfDeaths()+newDeath,
											state.getNumberOfKills(),
											state.getNeoDamage(),
											
											newNeoPos,
											newHostages,
											state.getPills(),
											state.getAgents(),
											newMutatedAgents,
											newCarriedHostages);
		
		MatrixSearchNode node=new MatrixSearchNode(s.depth+1, s, newState, ((MatrixOperators)this.operators).getLeft(),null);
		
		
		
		
		return node;
	}
	
	
	
	private MatrixSearchNode applyTakePill(MatrixSearchNode s) {
		MatrixState state=s.getState();
		List<Integer> neoPos=state.getNeoPos();
		if(state.getPills().containsKey(neoPos)) {
			Hashtable<List<Integer>,Object> newPills= new Hashtable<List<Integer>, Object>();
			Enumeration<List<Integer>> enumKey = state.getPills().keys();
			while(enumKey.hasMoreElements()) {
				List<Integer> pillPos=enumKey.nextElement();
				if(!pillPos.equals(neoPos)) {
					newPills.put(pillPos, new Object());
				}
			}
			int oldNeoHealth=state.getNeoDamage();
			int newNeoHealth=oldNeoHealth-20<0?0:oldNeoHealth-20;
			Hashtable<List<Integer>,Integer> newCarriedHostages=new Hashtable<List<Integer>, Integer>();
			Hashtable<List<Integer>,Integer> newHostages=new Hashtable<List<Integer>, Integer>();
			MatrixUtil.reduceDamageBy20(s, newHostages, newCarriedHostages);
			
			MatrixState newState=new MatrixState(
					state.getNumberOfCarriedHostages(),
					state.getNumberOfDeaths(),
					state.getNumberOfKills(),
					newNeoHealth,
					
					state.getNeoPos(),
					newHostages,
					newPills,
					state.getAgents(),
					state.getMutatedAgents(),
					newCarriedHostages);
			
			
			MatrixSearchNode newSearchNode=new MatrixSearchNode(s.depth+1, s, newState, ((MatrixOperators)this.operators).getTakePill(),null);
			return newSearchNode;
			
			
			
		}else {
			return null;
		}
		
	}
	private MatrixSearchNode applyKill(MatrixSearchNode s) {
		MatrixState state=s.getState();
		Hashtable<List<Integer>,Object> killedAgents=new Hashtable<List<Integer>, Object>();
		Hashtable<List<Integer>,Object> newAgents=new Hashtable<List<Integer>, Object>();
		Hashtable<List<Integer>,Object> newMutatedAgents=new Hashtable<List<Integer>, Object>();
		int neoX=state.getNeoX();
		int neoY=state.getNeoY();
		int oldNeoDamage=state.getNeoDamage();
		int newNumberOfKills=0;
		
		if(state.isThereAnAgentAboveMe()||state.isThereAnAgentBelowMe()||state.isThereAnAgentToTheLeft()||state.isThereAnAgentToTheRight()) {
			if(oldNeoDamage+20>=100) {
				return null;
			}
			if(state.isThereAHostageWillDie()) {
				return null;
				
			}
			if(state.isThereAnAgentAboveMe()) {
				newNumberOfKills++;
				List<Integer> agentPos=new ArrayList<Integer>();
				agentPos.add(neoX);
				agentPos.add(neoY-1);
				killedAgents.put(agentPos, "");
			}
			if(state.isThereAnAgentBelowMe()) {
				newNumberOfKills++;
				List<Integer> agentPos=new ArrayList<Integer>();
				agentPos.add(neoX);
				agentPos.add(neoY+1);
				killedAgents.put(agentPos, "");
			}
			if(state.isThereAnAgentToTheLeft()) {
				newNumberOfKills++;
				List<Integer> agentPos=new ArrayList<Integer>();
				agentPos.add(neoX-1);
				agentPos.add(neoY);
				killedAgents.put(agentPos, "");
			}
			if(state.isThereAnAgentToTheRight()) {
				newNumberOfKills++;
				List<Integer> agentPos=new ArrayList<Integer>();
				agentPos.add(neoX+1);
				agentPos.add(neoY);
				killedAgents.put(agentPos, "");
			}
			
			Hashtable<List<Integer>,Integer> newHostages=new Hashtable<List<Integer>, Integer>();
			Hashtable<List<Integer>,Integer> newCarriedHostages=new Hashtable<List<Integer>, Integer>(); 
			
			int newDeath=MatrixUtil.increaseDamageBy2(s, newHostages, newMutatedAgents, newCarriedHostages);
			int newNeoDamage=oldNeoDamage+20;
			MatrixUtil.removeKilledAgents(s, killedAgents, newMutatedAgents, newAgents);
			MatrixState newState=new MatrixState(state.getNumberOfCarriedHostages(),
					state.getNumberOfDeaths()+newDeath,
					state.getNumberOfKills()+newNumberOfKills,
					newNeoDamage,
					
					state.getNeoPos(),
					newHostages,
					state.getPills(),
					newAgents,
					newMutatedAgents,
					newCarriedHostages);
			MatrixSearchNode newSearchNode=new MatrixSearchNode(s.depth+1, s, newState, ((MatrixOperators)this.operators).getKill(),null);
			return newSearchNode;
			
			
			
			
			
		}else {
			return null;
		}
		
	}
	private MatrixSearchNode applyFly(MatrixSearchNode s) {
		MatrixState state=s.getState();
		List<Integer> neoPos=state.getNeoPos();
		if(!this.pads.containsKey(neoPos)) {
			return null;
		}
		List<Integer> newNeoPos=new ArrayList<Integer>();
		List<Integer> otherPadCoordinates=this.pads.get(neoPos);
		newNeoPos.add(otherPadCoordinates.get(0));
		newNeoPos.add(otherPadCoordinates.get(1));
		
		
		Hashtable<List<Integer>,Integer> newHostages=new Hashtable<List<Integer>, Integer>();
		Hashtable<List<Integer>,Integer> newCarriedHostages=new Hashtable<List<Integer>, Integer>(); 
		Hashtable<List<Integer>,Object> newMutatedAgents=new Hashtable<List<Integer>, Object>();
		
		int newDeath=MatrixUtil.increaseDamageBy2(s, newHostages, newMutatedAgents, newCarriedHostages);
		
		MatrixState newState=new MatrixState(state.getNumberOfCarriedHostages(),
				state.getNumberOfDeaths()+newDeath,
				state.getNumberOfKills(),
				state.getNeoDamage(),
				
				newNeoPos,
				newHostages,
				state.getPills(),
				state.getAgents(),
				newMutatedAgents,
				newCarriedHostages);
		MatrixSearchNode newSearchNode= new MatrixSearchNode(s.depth+1, s, newState, ((MatrixOperators)this.operators).getFly(),null);
		return newSearchNode;
		
		
	}
	
	
	private MatrixSearchNode applyCarry(MatrixSearchNode s) {
		MatrixState state = s.getState();

		if (state.getNumberOfCarriedHostages() < this.maxNumberToCarry && state.isThereHostage()) { // is there a hostage & can neo carry more ?
			
			
			Hashtable<List<Integer>,Integer> newHostages=new Hashtable<List<Integer>, Integer>();
			Hashtable<List<Integer>,Integer> newCarriedHostages=new Hashtable<List<Integer>, Integer>(); 
			Hashtable<List<Integer>,Object> newMutatedAgents=new Hashtable<List<Integer>, Object>();
			List<Integer> neoPosDeepCopy=new ArrayList<Integer>();
			neoPosDeepCopy.add(state.getNeoX());
			neoPosDeepCopy.add(state.getNeoY());
			
			
			int newDeath=MatrixUtil.increaseDamageBy2(s, newHostages, newMutatedAgents, newCarriedHostages);
			if(newHostages.containsKey(neoPosDeepCopy)) {
				int hostageDamage=newHostages.remove(neoPosDeepCopy);
				newCarriedHostages.put(neoPosDeepCopy, hostageDamage);
			}
			if(newMutatedAgents.containsKey(neoPosDeepCopy)) {
				int hostageDamage=-1;
				newMutatedAgents.remove(neoPosDeepCopy);
				newCarriedHostages.put(neoPosDeepCopy, hostageDamage);
			}
			int newNumberOfCarriedHostages=state.getNumberOfCarriedHostages()+1;
			MatrixState newState=new MatrixState(newNumberOfCarriedHostages,
					state.getNumberOfDeaths()+newDeath,
					state.getNumberOfKills(),
					state.getNeoDamage(),
					
					state.getNeoPos(),
					newHostages,
					state.getPills(),
					state.getAgents(),
					newMutatedAgents,
					newCarriedHostages);
			MatrixSearchNode newNode=new MatrixSearchNode(s.depth+1, s, newState, ((MatrixOperators)this.operators).getCarry(),null);
			return newNode;
		}else {
			return null;
		}
		
	}
	private MatrixSearchNode applyDrop(MatrixSearchNode s) {
		MatrixState state=s.getState();
		 if (state.getNumberOfCarriedHostages() > 0 && telephoneBooth.get(0) == state.getNeoX() && telephoneBooth.get(1) == state.getNeoY()) { // is he on the booth with hostages ?
			 Hashtable<List<Integer>,Integer> newHostages=new Hashtable<List<Integer>, Integer>();
			 Hashtable<List<Integer>,Integer> newCarriedHostages=new Hashtable<List<Integer>, Integer>(); 
			 Hashtable<List<Integer>,Object> newMutatedAgents=new Hashtable<List<Integer>, Object>();
			 int newDeath=MatrixUtil.increaseDamageBy2(s, newHostages, newMutatedAgents, newCarriedHostages);
			 int numberOfFalseDeaths=MatrixUtil.getNumberOfFalseDeaths(s);
			
			 newDeath=newDeath-numberOfFalseDeaths;
			 newCarriedHostages.clear();
			 MatrixState newState=new MatrixState(0,
					 state.getNumberOfDeaths()+newDeath,
					 state.getNumberOfKills(),
					 state.getNeoDamage(),
					
					 state.getNeoPos(),
					 newHostages,
					 state.getPills(),
					 state.getAgents(),
					 newMutatedAgents,
					 newCarriedHostages);
			 
			 MatrixSearchNode newNode=new MatrixSearchNode(s.depth+1, s, newState, ((MatrixOperators)this.operators).getDrop(),null);
			 return newNode;
			 
			 
		 }else {
			 return null;
		 }
		
	}
	static String[][] map;  // for visualization can be remover
    static String Grid; // The required String
    static ArrayList<String> empty_cells; // save the empty cells . It makes randomization easier
	
	static String genGrid() {
        int M = randomize(5, 15); // M represent the height  of the grid (rows) .
        int N = randomize(5, 15); // N represent the width of the grid (columns) .
        Grid = "";
        //System.out.println("grid generated  " + M + " " + N);
        empty_cells = new ArrayList<String>();
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                empty_cells.add(i + "," + j);
            }
        }

        map = new String[M][N]; //create 2d array of string to visualize the Grid
        Grid = Grid + M + "," + N + ";";

        int C = randomize(1, 4); // the maximum number of members Neo can carry at a time.
        Grid = Grid + C + ";";

        Mapping(1, "neo"); // Map Neo on the map

        Mapping(1, "telephone"); // Map Telephone on the map


        int W = randomize(3, 10); // represent the number of hostages
        //System.out.println("no of hostages " + W);

        int G = randomize(1, W); // represent the number of pills
        //System.out.println("no of pills  " + G);
        int L = randomize(1, (M * N - (1 + 1 + W + G + 1)) / 2); // represent the number of pads However , every pad consists of start and end pad
        // every pad takes 2 cells
        // number of pads is number of cell - neo - telephone - number of hostages - number of pills - 1 at least agent integer divided by 2
        //System.out.println("no of pads " + L);

        int K = randomize(1, M * N - L * 2 - (1 + 1 + W + G)); // Number of agents ranges from 1 to the rest of the empty cells
        //System.out.println("no of agents " + K);
       // System.out.println("total cells " + (M * N) + "total vars " + (K + L + G + W + 1 + 1));

        Mapping(K, "agent"); // put K agent on the map

        Mapping(G, "pill"); // put G pills on the map


        Mapping(L * 2, "pad");// put L pads on the map


        Mapping(W, "hostage"); // put W Hostage on the map

        //System.out.println(Grid);
return Grid;

    }
	
	
    static int randomize(int min, int max) { // min and max are inclusive
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    static void Mapping(int q, String s) { //is a func to generate the grid String and put the objects on the 2d array.

        for (int i = 0; i < q; i++) { // they are q entities of the same object to be handled.
            int rand_n = randomize(0, empty_cells.size() - 1); // get a random index. 
            String Cell = empty_cells.get(rand_n);
            int Y;
            int X;

            if (Cell.charAt(2) == ',') {  // to extract the X and Y from string
                Y = Integer.parseInt((Cell.charAt(0) + "")) * 10 + Integer.parseInt((Cell.charAt(1) + ""));
                if (Cell.length() != 4)
                    X = Integer.parseInt((Cell.charAt(3) + "")) * 10 + Integer.parseInt((Cell.charAt(4) + ""));
                else
                    X = Integer.parseInt((Cell.charAt(3) + ""));

            } else {
                Y = Integer.parseInt((Cell.charAt(0) + ""));
                if (Cell.length() != 3)
                    X = Integer.parseInt((Cell.charAt(2) + "")) * 10 + Integer.parseInt((Cell.charAt(3) + ""));
                else
                    X = Integer.parseInt((Cell.charAt(2) + ""));

            }
            empty_cells.remove(rand_n);





            if (s.equals("hostage")) {
                int damage = randomize(1, 99);
                Grid = Grid + X + "," + Y + "," + damage;
                map[Y][X] = s+i+","+damage;

            } else {
                if (s.equals("pad")) {
                    Grid = Grid + X + "," + Y;
                    if (i % 2 == 0)
                        map[Y][X] = "s_" + s + (i / 2);
                    else
                        map[Y][X] = "e_" + s + (i / 2);


                } else {
                    map[Y][X] = s;
                    Grid = Grid + X + "," + Y;

                }
            }

            if (i == q - 1) {
                if (!s.equals("hostage"))
                    Grid = Grid + ";";
            } else
                Grid = Grid + ",";
        }

    }

	

	
}
