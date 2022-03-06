package code;

import java.util.*;



public class MatrixState implements State  {
	

	private int numberOfCarriedHostages;
	private int numberOfDeaths;
	private int numberOfKills;
	private int neoHealth;
	
	private List<Integer> neoPos;
	private Hashtable<List<Integer>,Integer> hostages;
	private Hashtable<List<Integer>,Object> pills;
	private Hashtable<List<Integer>,Object> agents;
	private Hashtable<List<Integer>,Object> mutatedAgents;
	
	private Hashtable<List<Integer>,Integer> carriedHostages;
	
	
public MatrixState(int numberOfCarriedHostages,
		int numberOfDeaths,
		int numberOfKills,
		int neoHealth,
		List<Integer> neoPos,
		Hashtable<List<Integer>,Integer> hostages,
		Hashtable<List<Integer>,Object> pills,
		Hashtable<List<Integer>,Object> agents,
		Hashtable<List<Integer>,Object> mutatedAgents,
		Hashtable<List<Integer>,Integer> carriedHostages) {
	
	this.numberOfCarriedHostages=numberOfCarriedHostages;
	this.numberOfDeaths=numberOfDeaths;
	this.numberOfKills=numberOfKills;
	
	this.neoHealth=neoHealth;
	this.neoPos=neoPos;
	this.hostages=hostages;
	this.pills=pills;
	this.agents=agents;
	this.mutatedAgents=mutatedAgents;
	this.carriedHostages=carriedHostages;
	
	
}

public String toString() {
	
	
	String CarriedHost="Number of Carried Hostages: "+numberOfCarriedHostages;
	String numberOfDeath="Number of Death: "+numberOfDeaths;
	String killNumber="Number of Kills: "+numberOfKills;
	String neoDamage="Neo Damage: "+neoHealth;
	String pos="Neo pos: ("+getNeoX()+", "+getNeoY()+")";
	String hostageString="";
	String pillString="";
	String agentString="";
	String mutatedAgentString="";
	String carriedHostageString="";
	Enumeration<List<Integer>> enumKey = hostages.keys();
	while(enumKey.hasMoreElements()) {
	
		List<Integer> hostagePos=enumKey.nextElement();
		Integer damage=getHostages().get(hostagePos);
		hostageString+="hostage at: "+hostagePos.get(0)+", "+hostagePos.get(1)+" has damage of: "+damage+"\n";
		
	}
	enumKey=pills.keys();
	
	while(enumKey.hasMoreElements()) {
		
		List<Integer> pillPos=enumKey.nextElement();
		
		pillString+="pill at: "+pillPos.get(0)+", "+pillPos.get(1)+"\n";
		
	}
	enumKey=agents.keys();
	
	while(enumKey.hasMoreElements()) {
		
		List<Integer> agentPos=enumKey.nextElement();
		
		agentString+="Agent at: "+agentPos.get(0)+", "+agentPos.get(1)+"\n";
		
	}
	
	enumKey=mutatedAgents.keys();
	
	while(enumKey.hasMoreElements()) {
		
		List<Integer> mutatedAgentPos=enumKey.nextElement();
		
		mutatedAgentString+="mutated agent at: "+mutatedAgentPos.get(0)+", "+mutatedAgentPos.get(1)+"\n";
		
	}
	
	enumKey=carriedHostages.keys();
	
	while(enumKey.hasMoreElements()) {
		
		List<Integer> carriedHostagePos=enumKey.nextElement();
		
		carriedHostageString+="carried was at: "+carriedHostagePos.get(0)+", "+carriedHostagePos.get(1)+"\n";
		
	}
	return CarriedHost+"\n"+
			numberOfDeath+"\n"+
			killNumber+"\n"+
			neoDamage+"\n"+
			pos+"\n"+
			"Hostages: "+"\n"+
			hostageString+
			"pills: "+"\n"+
			pillString+
			"agents: "+"\n"+
			agentString+
			"mutated agents: "+"\n"+
			mutatedAgentString+
			"carried hostages: "+"\n"+
			carriedHostageString;
	


}

public Hashtable<List<Integer>,Integer> getCarriedHostages(){
	return this.carriedHostages;
}
public Hashtable<List<Integer>,Integer> getHostages(){
	return this.hostages;
}
public Hashtable<List<Integer>,Object> getPills(){
	return this.pills;
}
public Hashtable<List<Integer>,Object> getAgents(){
	return this.agents;
}
public Hashtable<List<Integer>,Object> getMutatedAgents(){
	return this.mutatedAgents;
}

public int getNumberOfCarriedHostages() {
	return this.numberOfCarriedHostages;
}
public int getNumberOfMutatedAgents() {
	return mutatedAgents.size();
}
public int getNumberOfDeaths() {
	return numberOfDeaths;
}

public int getNumberOfKills() {
	return numberOfKills;
}

public List<Integer> getNeoPos() {
	return neoPos;
}

public int getNumberOfHostages() {
	return this.hostages.size();
}
public int getNeoDamage() {
	return this.neoHealth;
}
public int getNeoX() {
	return this.neoPos.get(0);
}
public int getNeoY() {
	return this.neoPos.get(1);
}
public boolean isThereAnAgentAboveMe() {
	int x=this.getNeoX();
	int y=this.getNeoY();
	List<Integer> pos=new ArrayList<Integer>();
	pos.add(x);
	
	pos.add(y-1);
	return agents.containsKey(pos)||mutatedAgents.containsKey(pos);
}
public boolean isThereAnAgentBelowMe() {
	int x=this.getNeoX();
	int y=this.getNeoY();
	List<Integer> pos=new ArrayList<Integer>();
	pos.add(x);
	
	pos.add(y+1);
	return agents.containsKey(pos)||mutatedAgents.containsKey(pos);
}
public boolean isThereAnAgentToTheRight() {
	int x=this.getNeoX();
	int y=this.getNeoY();
	List<Integer> pos=new ArrayList<Integer>();
	pos.add(x+1);
	pos.add(y);
	return agents.containsKey(pos) ||mutatedAgents.containsKey(pos);
}
public boolean isThereAnAgentToTheLeft() {
	int x=this.getNeoX();
	int y=this.getNeoY();
	List<Integer> pos=new ArrayList<Integer>();
	pos.add(x-1);
	pos.add(y);
	return agents.containsKey(pos) ||mutatedAgents.containsKey(pos);
}

public boolean isThereHostage() {
  
    List<Integer> neoPos =this.getNeoPos();
    return hostages.containsKey(neoPos);
}
public boolean isThereAHostageAboutToDie(int  x, int y){
	List<Integer> pos=new ArrayList<Integer>();
	pos.add(x);
	pos.add(y);
	if(this.hostages.containsKey(pos)) {
		int damage=this.hostages.get(pos);
		if(damage+2>=100) {
			return true;
		}
	}
	return false;
}
public boolean isThereAHostageWillDie(){
	if(hostages.containsKey(this.getNeoPos())) {
		int damage=this.hostages.get(this.getNeoPos());
		if(damage+2>=100) {
			return true;
		}
	}
	return false;
}


}
