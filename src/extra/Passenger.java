package extra;

import java.awt.Point;

public class Passenger
{
	int id;
	public Point coordinate;
	public double utility;
	public int neighbour;
	public int interest;
	public int mtcheVehID;
	
	
	public Passenger(int id, Point coordinate, double utility, int neighbour, int interest, int mtcheVehID) {
		super();
		this.id = id;
		this.coordinate = coordinate;
		this.utility = utility;
		this.neighbour = neighbour;
		this.interest = interest;
		this.mtcheVehID = mtcheVehID;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Point getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(Point coordinate) {
		this.coordinate = coordinate;
	}
	public double getUtility() {
		return utility;
	}
	public void setUtility(double utility) {
		this.utility = utility;
	}

	public int getNeighbour() {
		return neighbour;
	}

	public void setNeighbour(int neighbour) {
		this.neighbour = neighbour;
	}

	public int getInterest() {
		return interest;
	}

	public void setInterest(int interest) {
		this.interest = interest;
	}
	
	public int getMtcheVehID() {
		return mtcheVehID;
	}

	public void setMtcheVehID(int mtcheVehID) {
		this.mtcheVehID = mtcheVehID;
	}

	@Override
	public String toString() {
		return  id + "," + coordinate.x + "," + coordinate.y + "," + utility + "," + neighbour + "," + interest;
	}
			
}