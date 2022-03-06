package code;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
//this class contains helper methods
public class MatrixUtil  {
	
	 public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
	    {
	  
	        // Create a new ArrayList
	        ArrayList<T> newList = new ArrayList<T>();
	  
	        // Traverse through the first list
	        for (T element : list) {
	  
	            // If this element is not present in newList
	            // then add it
	            if (!newList.contains(element)) {
	  
	                newList.add(element);
	            }
	        }
	  
	        // return the new list
	        return newList;
	    }
	
	
	private static String getPos(MatrixSearchNode s) {
		MatrixState state=s.getState();
		String posString="";
		Enumeration<List<Integer>> enumKey = state.getHostages().keys();
		while(enumKey.hasMoreElements()) {
			List<Integer> hostagePos=enumKey.nextElement();
			posString+=hostagePos.get(0)+", "+hostagePos.get(1);
			
		}
		posString+=";";
		enumKey=state.getPills().keys();
		
		while(enumKey.hasMoreElements()) {
			
			List<Integer> pillPos=enumKey.nextElement();
			
			posString+=pillPos.get(0)+", "+pillPos.get(1);
		}
		posString+=";";
		enumKey=state.getAgents().keys();
		
		while(enumKey.hasMoreElements()) {
			
			List<Integer> agentPos=enumKey.nextElement();
			
			posString+=agentPos.get(0)+", "+agentPos.get(1);
			
		}
		posString+=";";
		enumKey=state.getMutatedAgents().keys();
		
		while(enumKey.hasMoreElements()) {
			
			List<Integer> mutatedAgentPos=enumKey.nextElement();
			
			posString+=mutatedAgentPos.get(0)+", "+mutatedAgentPos.get(1);
			
		}
		posString+=";";
		
		enumKey=state.getCarriedHostages().keys();
		
		while(enumKey.hasMoreElements()) {
			
			List<Integer> carriedHostagePos=enumKey.nextElement();
			
			posString+=carriedHostagePos.get(0)+", "+carriedHostagePos.get(1);
			
		}
		return posString;
	}
	
	
	

	
	
	
	
	//return a string as an id for the state of a search node
	public static String getStateId(MatrixSearchNode s) {
		MatrixState state=s.getState();
		String id=""+state.getNumberOfCarriedHostages()+";"+
				  state.getNumberOfDeaths()+";"+
				  state.getNumberOfKills()+";"+
				  state.getNeoX()+", "+state.getNeoY()+";"+
				  state.getNeoDamage()+";"+
				  getPos(s);
		return id;
				  		 
	}
	

	
	public static int getNumberOfFalseDeaths(MatrixSearchNode s) {
		MatrixState state=s.getState();
		int numberOfFalseDeaths=0;
		Enumeration<List<Integer>> enumKey = state.getCarriedHostages().keys();
		while(enumKey.hasMoreElements()) {
			List<Integer> carriedHostagePos=enumKey.nextElement();
			int hostageDamage=state.getCarriedHostages().get(carriedHostagePos);
			if(hostageDamage!=-1) {
			if(hostageDamage+2>=100) {
				numberOfFalseDeaths++;
			}
			}
		}
		return numberOfFalseDeaths;
		
		
		
	}
	
	private static void restoreAgents(MatrixSearchNode s,Hashtable<List<Integer>,Object> newAgents) {
		MatrixState state=s.getState();
		Enumeration<List<Integer>> enumKey = state.getAgents().keys();
		while(enumKey.hasMoreElements()) {
			List<Integer> agentPos=enumKey.nextElement();
			newAgents.put(agentPos, "");
		}
		
		
	}
	public static void removeKilledAgents(MatrixSearchNode s,Hashtable<List<Integer>,Object> killedAgents,Hashtable<List<Integer>,Object> newMutatedAgents,Hashtable<List<Integer>,Object> newAgents) {
		
		restoreAgents(s, newAgents);
		Enumeration<List<Integer>> enumKey = killedAgents.keys();
		while(enumKey.hasMoreElements()) {
			List<Integer> killedAgentPos=enumKey.nextElement();
			newMutatedAgents.remove(killedAgentPos);
			newAgents.remove(killedAgentPos);
		
		}
	}
	
	
	private static void restoreMutatedAgents(MatrixSearchNode s,Hashtable<List<Integer>,Object> newMutatedAgents) {
		MatrixState state=s.getState();
		
		Enumeration<List<Integer>> enumKey = state.getMutatedAgents().keys();
		while(enumKey.hasMoreElements()) {
			List<Integer> agentPos=enumKey.nextElement();
			newMutatedAgents.put(agentPos, new Object());
		
		}
	}
	public static int increaseDamageBy2(MatrixSearchNode s,Hashtable<List<Integer>,Integer> newhostages, Hashtable<List<Integer>,Object> newMutatedAgents,Hashtable<List<Integer>, Integer> newCarriedHostages) {
		restoreMutatedAgents(s,newMutatedAgents);
		MatrixState state=s.getState();
		
		Enumeration<List<Integer>> enumKey = state.getHostages().keys();
		int numberOfDeath=0;
		while(enumKey.hasMoreElements()) {
			List<Integer> hostagePos=enumKey.nextElement();
			Integer damage=state.getHostages().get(hostagePos);
			if(damage+2>=100) {
				numberOfDeath+=1;
				newMutatedAgents.put(hostagePos,new Object());
			}else {
				newhostages.put(hostagePos, new Integer(damage+2));
			}
			
		}
		enumKey=state.getCarriedHostages().keys();
		while(enumKey.hasMoreElements()) {
			List<Integer> carriedPos=enumKey.nextElement();
			Integer damage=state.getCarriedHostages().get(carriedPos);
			if(damage!=-1) {
				
				int newDamage=damage+2;
				if(newDamage>=100) {
					numberOfDeath+=1;
					// flagging dead carried hostages as not increase the number of death more than once
					newDamage=-1;
				}
				newCarriedHostages.put(carriedPos, newDamage);
			}
			
		}
		return numberOfDeath;
	}
	
	public static void reduceDamageBy20 (MatrixSearchNode s,Hashtable<List<Integer>,Integer> newHostages,Hashtable<List<Integer>,Integer> newCarriedHostages){
		MatrixState state=s.getState();
		
		Enumeration<List<Integer>> enumKey = state.getHostages().keys();
		while(enumKey.hasMoreElements()) {
			List<Integer> hostagePos=enumKey.nextElement();
			int oldDamage=state.getHostages().get(hostagePos);
			int newDamage=oldDamage-20<0?0:oldDamage-20;
			newHostages.put(hostagePos, newDamage);
		}
		 enumKey = state.getCarriedHostages().keys();
		 while(enumKey.hasMoreElements()) {
			 List<Integer> hostagePos=enumKey.nextElement();
			 int oldDamage=state.getCarriedHostages().get(hostagePos);
			 int newDamage=oldDamage-20<0?0:oldDamage-20;
			 newCarriedHostages.put(hostagePos, newDamage); 
		 }
		
	
		
		
	}

	
	public static boolean didPassTheGoal(MatrixSearchNode searchNode) {
		
		MatrixState state=(MatrixState)searchNode.getState();
		if(state.getNumberOfCarriedHostages()==0 && state.getNumberOfHostages()==0 && state.getNumberOfMutatedAgents()==0) {
			return true;
		}
		return false;
	}
	
	
	
	public static Hashtable<List<Integer>,Integer> getHostages(String[] s){
		String[] hostages=s[7].split(",",-1);
		Hashtable<List<Integer>,Integer> hostagesTable=new Hashtable<List<Integer>,Integer>();
		for(int i=0;i<hostages.length;i+=3) {
			List<Integer> hostageXY=new ArrayList<Integer>();
			hostageXY.add(Integer.parseInt(hostages[i+1]));
			hostageXY.add(Integer.parseInt(hostages[i]));
			
			Integer damage=Integer.parseInt(hostages[i+2]);
			hostagesTable.put(hostageXY,damage);
			
		}
		return hostagesTable;
		
	}
	
	public static Hashtable<List<Integer>,List<Integer>> getPads(String[] s){
		String[] padsXY=s[6].split(",",-1);
		Hashtable<List<Integer>,List<Integer>> pads=new Hashtable<List<Integer>, List<Integer>>();
		for(int i=0;i<padsXY.length;i+=4) {
			List<Integer> startXY=new ArrayList<Integer>();
			List<Integer> endXY=new ArrayList<Integer>();
			
			startXY.add(Integer.parseInt(padsXY[i+1]));
			startXY.add(Integer.parseInt(padsXY[i]));
			
			
			endXY.add(Integer.parseInt(padsXY[i+3]));
			endXY.add(Integer.parseInt(padsXY[i+2]));
			
			pads.put(startXY,endXY);
			
			
		}
		return pads;
		
	}
	public static Hashtable<List<Integer>,Object> getPills(String[] s){
		String[] pillsXY=s[5].split(",",-1);
		Hashtable<List<Integer>, Object> pills=new Hashtable<List<Integer>,Object>();
		for(int i=0;i<pillsXY.length;i+=2) {
			List<Integer> pill=new ArrayList<Integer>();
			pill.add(Integer.parseInt(pillsXY[i+1]));
			pill.add(Integer.parseInt(pillsXY[i]));
			pills.put(pill,"");
		}
		return pills;
		
	}
	public static Hashtable<List<Integer>, Object> getAgents(String[] s){
		String[] agentsXY=s[4].split(",",-1);
		Hashtable<List<Integer>, Object> agents=new Hashtable<List<Integer>,Object>();
		for(int i=0;i<agentsXY.length;i+=2) {
			List<Integer> agent=new ArrayList<Integer>();
			agent.add(Integer.parseInt(agentsXY[i+1]));
			agent.add(Integer.parseInt(agentsXY[i]));
			agents.put(agent,"");
		}
		return agents;
		
	}
	
	
	public static List<Integer> getTelephoneBoothCoordinates(String[] s) {
		String[] telephoneBoothXandY= s[3].split(",",-1);
		int x=Integer.parseInt(telephoneBoothXandY[1]);
		int y=Integer.parseInt(telephoneBoothXandY[0]);
		List<Integer> tb=new ArrayList<Integer>();
		tb.add(x);
		tb.add(y);
		
		return tb;
				
	}
	public static List<Integer> getNeoPos(String[] s) {
		String[] neoPos=s[2].split(",",-1);
		List<Integer> list=new ArrayList<Integer>();
		list.add(Integer.parseInt(neoPos[1]));
		list.add(Integer.parseInt(neoPos[0]));
		
		return list;
	}
	public static String[] getGridSize(String[] s) {
		return s[0].split(",",-1);
	}
	public static int getMaxnumberToCarry(String[] s) {
		return Integer.parseInt(s[1]);
	}
	public static void visualize(Matrix m,MatrixSearchNode node) {
		MatrixState state=(MatrixState)node.getState();
		
		String[][] grid=new String[m.width][m.height];
		if(grid[state.getNeoX()][state.getNeoY()]==null) {
			grid[state.getNeoX()][state.getNeoY()]="NEO";
		}else {
			grid[state.getNeoX()][state.getNeoY()]+="+"+"NEO";
		}
		if(grid[m.telephoneBooth.get(0)][m.telephoneBooth.get(1)]==null) {
			grid[m.telephoneBooth.get(0)][m.telephoneBooth.get(1)]="TB";
		}else {
			grid[m.telephoneBooth.get(0)][m.telephoneBooth.get(1)]+="+"+"TB";
		}
		
		Enumeration<List<Integer>> enumKey = state.getHostages().keys();
		while(enumKey.hasMoreElements()) {
			List<Integer> hostagePos=enumKey.nextElement();
			if(grid[hostagePos.get(0)][hostagePos.get(1)]==null) {
				grid[hostagePos.get(0)][hostagePos.get(1)]="H("+state.getHostages().get(hostagePos)+")";	
			}
			else {
				grid[hostagePos.get(0)][hostagePos.get(1)]=grid[hostagePos.get(0)][hostagePos.get(1)]+"+"+"H("+state.getHostages().get(hostagePos)+")";
			}
			
			
		}
		
		enumKey=state.getPills().keys();
		
		while(enumKey.hasMoreElements()) {
			
			List<Integer> pillPos=enumKey.nextElement();
			if(grid[pillPos.get(0)][pillPos.get(1)]==null) {
				
				grid[pillPos.get(0)][pillPos.get(1)]="P";
			}
			else {
				grid[pillPos.get(0)][pillPos.get(1)]=grid[pillPos.get(0)][pillPos.get(1)]+"+"+"P";
			}
			
			
			
		
		}
		
		enumKey=state.getAgents().keys();
		
		while(enumKey.hasMoreElements()) {
			
			List<Integer> agentPos=enumKey.nextElement();
			if(grid[agentPos.get(0)][agentPos.get(1)]==null) {
				grid[agentPos.get(0)][agentPos.get(1)]="A";
			}
			else {
				grid[agentPos.get(0)][agentPos.get(1)]=grid[agentPos.get(0)][agentPos.get(1)]+"+"+"A";
			}
			
		
			
		}
		
		enumKey=state.getMutatedAgents().keys();
		
		while(enumKey.hasMoreElements()) {
			
			List<Integer> mutatedAgentPos=enumKey.nextElement();
			if(grid[mutatedAgentPos.get(0)][mutatedAgentPos.get(1)]==null) {
				grid[mutatedAgentPos.get(0)][mutatedAgentPos.get(1)]="MA";
			}
			else {
				grid[mutatedAgentPos.get(0)][mutatedAgentPos.get(1)]+="+"+"MA";
			}
			
		
			
		}
		enumKey=m.pads.keys();
		
		while(enumKey.hasMoreElements()) {
			
			List<Integer> mutatedAgentPos=enumKey.nextElement();
			if(grid[mutatedAgentPos.get(0)][mutatedAgentPos.get(1)]==null) {
				
				grid[mutatedAgentPos.get(0)][mutatedAgentPos.get(1)]="Pad("+m.pads.get(mutatedAgentPos).get(0)+", "+m.pads.get(mutatedAgentPos).get(1)+")";

			}else {
				grid[mutatedAgentPos.get(0)][mutatedAgentPos.get(1)]+="+"+"Pad("+m.pads.get(mutatedAgentPos).get(0)+", "+m.pads.get(mutatedAgentPos).get(1)+")";

			}
					
			
		}
		for(int i=0;i<grid.length;i++) {
			for(int j=0;j<grid[i].length;j++) {
				System.out.print(grid[j][i]+", ");
			}
			System.out.println();
		}
		System.out.println("=====================================================");
		
		
	}

}
