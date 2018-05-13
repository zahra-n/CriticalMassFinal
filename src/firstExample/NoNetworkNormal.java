package firstExample;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public class NoNetworkNormal {
	
	private static class Passenger
	{
		int id;
		Point coordinate;
		double utility;
		int neighbour;
		
		
		public Passenger(int id, Point coordinate, double utility, int neighbour) {
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

	private static class Vehicle
	{
		int id;
		Point coordinate;
		double utility;
		int neighbour;
		int capacity;
		
		
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

//		public double compareTo(Vehicle compareVeh) {
//			// TODO Auto-generated method stub
//			
//			double compareUtil = ((Vehicle) compareVeh) .getUtility();
//			return (this.utility-compareUtil);
//		}
				
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int vehicleNumber = 100;
		int passengerNumber = 500;
		double xLimit = 1000; //in meter
		double yLimit = 1000; //in meter
		double reachMeasure = 100; // in meter
		double potentialUtil = 5.0;
		double passengerUtilSum = 0.0;
		double vehicleUtilSum = 0.0;
		int matchedPassengers = 0 ;
		int unmatchedVehicles = 0;
		int vehicleCapacity = 1;
		
		
		ArrayList<Vehicle> vehicles = new ArrayList <Vehicle>();
		ArrayList<Passenger> passengers = new ArrayList <Passenger>();
		
		for (int i = 0 ; i < passengerNumber; i++ )
		{
			Random rnd = new Random();
			Point passengerCoord = new Point();
			passengerCoord.setLocation((rnd.nextGaussian()*0.125 + 0.5) * xLimit, (rnd.nextGaussian()*0.125 + 0.5) * yLimit);
			Passenger tempPassenger = new Passenger (i,passengerCoord,0.0,0);
			passengers.add(tempPassenger);
		}
		
		for (int i = 0 ; i < vehicleNumber; i++ )
		{
			Random rnd = new Random();
			Point vehicleCoord = new Point();
			vehicleCoord.setLocation((rnd.nextGaussian()*0.125 + 0.5) * xLimit, (rnd.nextGaussian()*0.125 + 0.5) * yLimit);
			Vehicle tempVehicle = new Vehicle (i, vehicleCoord, 0.0, 0, vehicleCapacity);
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
			double finalDist = xLimit;
			int matchedVehID = -1;
			Point checkPoint = passengers.get(i).coordinate;

			for (int j = 0 ; j < vehicles.size() ; j++ )
			{
				double distance = checkPoint.distance( vehicles.get(j).coordinate);
				if (distance < reachMeasure)
				{
					double vehUtil = (reachMeasure - distance) / reachMeasure * potentialUtil;
					if (distance < finalDist && vehicles.get(j).capacity > 0 ) //vehUtil > vehicles.get(j).utility
					{
						vehicles.get(j).setUtility(vehUtil);
						matchedVehID = vehicles.get(j).id;
						finalDist = distance;
					}
					
				}// end of checking distance
				
			}// end of vehicle loop
			
			if (matchedVehID > -1 )
			{
				double passUtil = (reachMeasure - finalDist) / reachMeasure * potentialUtil;
				passengers.get(i).setUtility(passUtil);
				vehicles.get(matchedVehID).setCapacity(vehicles.get(matchedVehID).capacity - 1);
				if (passengers.get(i).utility > 0.0)
					matchedPassengers++;
			}
			
//			System.out.println("passenger: " + i + " ,matched vehicle: " + matchedVehID);

		}// end of passenger loop
		
		for (int i = 0 ; i < vehicles.size() ; i++)
		{
			if (vehicles.get(i).capacity == vehicleCapacity)
			{
				vehicles.get(i).utility = 0.0;
				unmatchedVehicles++;
			}
		}
		
		
		//====================calculating the mean utility
		
				for (int i = 0 ; i < passengers.size() ; i++)
				{
					passengerUtilSum += passengers.get(i).utility;
				}
				double meanPassengersUtil = passengerUtilSum / passengers.size();
				
				for (int i = 0 ; i < vehicles.size() ; i++)
				{
					vehicleUtilSum += vehicles.get(i).utility;
				}
				double meanVehUtil = vehicleUtilSum / (vehicles.size() );	
			
				System.out.println("Passengers' mean utility = " + meanPassengersUtil);
				System.out.println("matched passengers = " + (double) matchedPassengers/passengers.size() * 100 + "%" );
				System.out.println("Vehicles' mean utility = " + meanVehUtil);
				System.out.println("matched vehicles = " + (double) (vehicles.size() - unmatchedVehicles)/vehicles.size() * 100 + "%");
				
				StringBuilder fileContentP = new StringBuilder();
				StringBuilder fileContentV = new StringBuilder();
				StringBuilder fileContentAggregated = new StringBuilder();
				
				fileContentP.append("Passenger_ID,X,Y,Utility,Neighbours"+ "\n");
				for (int i = 0 ; i < passengers.size(); ++i)
					fileContentP.append(passengers.get(i).toString() + "\n");
				
				fileContentV.append("Vehicle_ID,X,Y,Utility,Capacity,Neighbours" + "\n");
				for (int i = 0 ; i < vehicles.size(); ++i)
					fileContentV.append(vehicles.get(i).toString() + "\n");
				
				fileContentAggregated.append("Passengers' mean utility = " + meanPassengersUtil + "\t" +
						"Vehicles' mean utility = " + meanVehUtil + "\t" +
						"matched passengers = " + (double) matchedPassengers/passengers.size() * 100 + "%\t" +
						"matched vehicles = " + (double) (vehicles.size() - unmatchedVehicles)/vehicles.size() * 100 + "%\t");
				
				ZahraUtility.write2File(fileContentP.toString(),"test\\passengers-N" + passengerNumber + "P-N" + vehicleNumber + "V-" + vehicleCapacity + "C" + ".csv");
				ZahraUtility.write2File(fileContentV.toString(),"test\\vehicles-N" + passengerNumber + "P-N" + vehicleNumber + "V-" + vehicleCapacity + "C" + ".csv");
				ZahraUtility.write2File(fileContentAggregated.toString(),"test\\aggregation-N" + passengerNumber + "P-N" + vehicleNumber + "V-" + vehicleCapacity + "C" + ".txt");
				
			}

}
