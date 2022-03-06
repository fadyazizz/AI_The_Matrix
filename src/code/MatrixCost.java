package code;

public class MatrixCost extends Cost {

	int deathNumber;
	int killedNumber;
	int actualCost;
	
	public MatrixCost( int deathNumber,int killedNumber,int actualCost) {
		
		this.deathNumber=deathNumber;
		this.killedNumber=killedNumber;
		this.actualCost=actualCost;
	}
	public int compareTo(Cost o) {
		MatrixCost cost=(MatrixCost)o;
		
		int currentCost=400*this.deathNumber+200*this.killedNumber+this.actualCost;
		int otherCost=400*cost.deathNumber+200*cost.killedNumber+cost.actualCost;
		if(currentCost>otherCost) {
			return 1;
		}
		if(currentCost<otherCost) {
			return -1;
		}
		return 0;
	}
	public MatrixCost add(MatrixCost c) {
		MatrixCost total= new MatrixCost(this.deathNumber+c.deathNumber, this.killedNumber+c.killedNumber, this.actualCost+c.actualCost);
		return total;
	}
	public int getDeathNumber() {
		return this.deathNumber;
	}
	public int getKilledNumber() {
		return this.killedNumber;
	}
	public int getActualCost() {
		return this.actualCost;
	}
	public String toString() {
		return ""+this.deathNumber+", "+this.killedNumber+", "+this.actualCost;
	}

}
