package firstExample;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NoNetworkUniform {
	
	public static class Individual
	{
		int id;
		Point coordinate;
		double utility;
		int neighbour;
		
		
		public Individual(int id, Point coordinate, double utility, int neighbour) {
			super();
			this.id = id;
			this.coordinate = coordinate;
			this.utility = utility;
			this.neighbour = neighbour;
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

		@Override
		public String toString() {
			return  id + "," + coordinate.x + "," + coordinate.y + "," + utility + "," + neighbour;
		}
				
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int vehicleNumber = 500;
		int passengerNumber = 500;
		double xLimit = 1000; //in meter
		double yLimit = 1000; //in meter
		double reachMeasure = 100; // in meter
		double potentialUtil = 5.0;
		double passengerSum = 0.0;
		double vehicleSum = 0.0;
		int unMatchedPassengers = 0;
		int matchedPassengers = 0 ;
		
		
		ArrayList<Individual> vehicles = new ArrayList <Individual>();
		ArrayList<Individual> matchedVehicles = new ArrayList <Individual>();
		ArrayList<Individual> passengers = new ArrayList <Individual>();
		
		for (int i = 0 ; i < passengerNumber; i++ )
		{
			Random rnd = new Random();
			Point passengerCoord = new Point();
			passengerCoord.setLocation(rnd.nextDouble()*xLimit, rnd.nextDouble()*yLimit);
			Individual tempPassenger = new Individual (i,passengerCoord,0.0,0);
			passengers.add(tempPassenger);
		}
		
		for (int i = 0 ; i < vehicleNumber; i++ )
		{
			Random rnd = new Random();
			Point vehicleCoord = new Point();
			vehicleCoord.setLocation(rnd.nextDouble()*xLimit, rnd.nextDouble()*yLimit);
			Individual tempVehicle = new Individual (i,vehicleCoord,0.0,0);
			vehicles.add(tempVehicle);
		}
		
		System.out.println("passengers:" + passengers.size());
		System.out.println("vehicles:" + vehicles.size());
		
		//counting the number of vehicles around each passenger and visa versa 
		for (int i = 0 ; i < passengers.size() ; i++ )
		{
			Point checkPoint = passengers.get(i).coordinate;
			for (int j = 0 ; j < vehicles.size() ; j++ )
			{
				double distance = checkPoint.distance( vehicles.get(j).coordinate);
				if (distance < reachMeasure)
				{
					passengers.get(i).neighbour++;
					vehicles.get(j).neighbour++;
				}
			}
		}
		///=============
		
		//		calculating utility according to distance
		for (int i = 0 ; i < passengers.size() ; i++ )
		{
			Point checkPoint = passengers.get(i).coordinate;
			for (int j = 0 ; j < vehicles.size() ; j++ )
			{
				double distance = checkPoint.distance( vehicles.get(j).coordinate);
				if (distance < reachMeasure)
				{
					double PassUtil = (reachMeasure - distance) / reachMeasure * potentialUtil;
					double VehUtil = (reachMeasure - distance) / reachMeasure * potentialUtil;
					passengers.get(i).setUtility(PassUtil);
					vehicles.get(j).setUtility(VehUtil);
					matchedVehicles.add(vehicles.get(j));
					vehicles.remove(j);
					matchedPassengers++;
					break;
				}
				else
					unMatchedPassengers++;
			}
		}
		
		//calculating the mean utility
		
		for (int i = 0 ; i < passengers.size() ; i++)
		{
			passengerSum += passengers.get(i).utility;
		}
		double meanPassengersUtil = passengerSum / passengers.size();
		
		for (int i = 0 ; i < matchedVehicles.size() ; i++)
		{
			vehicleSum += matchedVehicles.get(i).utility;
		}
		double meanVehUtil = vehicleSum / (vehicles.size() + matchedVehicles.size());		
	
		System.out.println("Passengers' mean utility = " + meanPassengersUtil);
		System.out.println("matched passengers = " + matchedPassengers);
		System.out.println("Vehicles' mean utility = " + meanVehUtil);
		System.out.println("matched vehicles = " + matchedVehicles.size());
		
		StringBuilder fileContentP = new StringBuilder();
		StringBuilder fileContentV = new StringBuilder();
		
		fileContentP.append("Passenger ID, X, Y, Utility, Neighbours"+ "\n");
		for (int i = 0 ; i < passengers.size(); ++i)
			fileContentP.append(passengers.get(i).toString() + "\n");
		
			fileContentV.append("Vehicle ID, X, Y, Utility, Neighbours" + "\n");
		for (int i = 0 ; i < matchedVehicles.size(); ++i)
			fileContentV.append(matchedVehicles.get(i).toString() + "\n");
		for (int i = 0 ; i < vehicles.size(); ++i)
			fileContentV.append(vehicles.get(i).toString() + "\n");
		
		ZahraUtility.write2File(fileContentP.toString(),"C:\\Users\\znavidikasha\\eclipse-workspace\\CriticalMass\\test\\passengersU-" + passengerNumber + "P-" + vehicleNumber + "V" + ".csv");
		ZahraUtility.write2File(fileContentV.toString(),"C:\\Users\\znavidikasha\\eclipse-workspace\\CriticalMass\\test\\vehiclesU-" + passengerNumber + "P-" + vehicleNumber + "V" + ".csv");
		
	}

}
