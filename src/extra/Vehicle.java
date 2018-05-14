package extra;

import java.awt.Point;

public class Vehicle
{
	public int id;
	public Point coordinate;
	public double utility;
	int neighbour;
	public int capacity;
	
	
	public Vehicle(int id, Point coordinate, double utility, int neighbour, int capacity) {
		super();
		this.id = id;
		this.coordinate = coordinate;
		this.utility = utility;
		this.neighbour = neighbour;
		this.capacity = capacity;
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
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	@Override
	public String toString() {
		return  id + "," + coordinate.x + "," + coordinate.y + "," + utility + "," + capacity + "," + neighbour;
	}
}