package dynamic;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import firstExample.ZahraUtility;

public class NormalDistDyna {
	
	private static class Passenger
	{
		int id;
		Point coordinate;
		double utility;
		int neighbour;
		int interest;
		
		
		public Passenger(int id, Point coordinate, double utility, int neighbour, int interest) {
			super();
			this.id = id;
			this.coordinate = coordinate;
			this.utility = utility;
			this.neighbour = neighbour;
			this.interest = interest;
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

		@Override
		public String toString() {
			return  id + "," + coordinate.x + "," + coordinate.y + "," + utility + "," + neighbour + "," + interest;
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
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		int vehicleNumber = 500;
		int passengerNumber = 10;
		double xLimit = 1000; //in meter
		double yLimit = 1000; //in meter
		double reachMeasure = 100; // in meter
		double potentialUtil = 5.0;
		double vehUtilThres = 2.0;
		double passUtilThres = 2.0;
		int iterations = 100;
		int vehicleCapacity = 1;
		File log = new File("test\\" + "aggregated-N" + passengerNumber + "P-N" + vehicleNumber + "V-" + vehicleCapacity + "C" + ".csv");
		FileWriter fileWriter = new FileWriter(log, true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write("Iteration,Passengers' mean utility,Vehicles' mean utility,matched passengers,matched vehicles,Number of passengers,Number of Vehicles\n");
		
		
		//========================================================================================
		
		
		//iterations
		for (int k = 0 ; k < iterations ; k++)
		{
			double passengerUtilSum = 0.0;
			double vehicleUtilSum = 0.0;
			int matchedPassengers = 0 ;
			int unmatchedVehicles = 0;
			
			//==============generating the original population==============
			ArrayList<Vehicle> vehicles = new ArrayList <Vehicle>();
			ArrayList<Passenger> passengers = new ArrayList <Passenger>();
			
			for (int i = 0 ; i < passengerNumber; i++ )
			{
				Random rnd = new Random();
				Point passengerCoord = new Point();
				passengerCoord.setLocation((rnd.nextGaussian()*0.125 + 0.5) * xLimit, (rnd.nextGaussian()*0.125 + 0.5) * yLimit);
				Passenger tempPassenger = new Passenger (i, passengerCoord, 0.0, 0, 1);
				passengers.add(tempPassenger);
			}
			
			for (int i = 0 ; i < vehicleNumber; i++ )
			{
				Random rnd = new Random();
				Point vehicleCoord = new Point();
				vehicleCoord.setLocation((rnd.nextGaussian()*0.125 + 0.5) * xLimit, (rnd.nextGaussian()*0.125 + 0.5) * yLimit);//rnd.nextDouble() * xLimit, rnd.nextDouble() * yLimit
				Vehicle tempVehicle = new Vehicle (i, vehicleCoord, 0.0, 0, vehicleCapacity);
				vehicles.add(tempVehicle);
			}
			
			System.out.println(k);
//			System.out.println("passengers:" + passengers.size());
//			System.out.println("vehicles:" + vehicles.size());
			
			//counting the number of available vehicles around each passenger and visa versa 
			for (int i = 0 ; i < passengers.size() ; i++ )
			{
				if (passengers.get(i).getInterest() == 1)
				{
					Point checkPoint = passengers.get(i).coordinate;
					for (int j = 0 ; j < vehicles.size() ; j++ )
					{
						double distance = checkPoint.distance( vehicles.get(j).coordinate);
						if (distance < reachMeasure && vehicles.get(j).getCapacity() > 0)
						{
							passengers.get(i).neighbour++;
							vehicles.get(j).neighbour++;
						}
					}
				}
				
			}
			///=================================================================================================================
			
			//		calculating utility according to distance
			for (int i = 0 ; i < passengers.size() ; i++ )
			{
				if (passengers.get(i).getInterest() == 1)
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
					
	//				System.out.println("passenger: " + i + " ,matched vehicle: " + matchedVehID);
				}
				
	
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
			
			double matchedPassPercent = (double) matchedPassengers/passengers.size() * 100;
			double matchedVehPercent = (double) (vehicles.size() - unmatchedVehicles)/vehicles.size() * 100;
		
			
//			System.out.println("Passengers' mean utility = " + meanPassengersUtil);
//			System.out.println("matched passengers = " + matchedPassPercent + "%" );
//			System.out.println("Vehicles' mean utility = " + meanVehUtil);
//			System.out.println("matched vehicles = " + matchedVehPercent + "%");
			
			StringBuilder fileContentP = new StringBuilder();
			StringBuilder fileContentV = new StringBuilder();
			
			fileContentP.append("Passenger_ID,X,Y,Utility,Neighbours,Interest"+ "\n");
			for (int i = 0 ; i < passengers.size(); ++i)
				fileContentP.append(passengers.get(i).toString() + "\n");
			
			fileContentV.append("Vehicle_ID,X,Y,Utility,Capacity,Neighbours" + "\n");
			for (int i = 0 ; i < vehicles.size(); ++i)
				fileContentV.append(vehicles.get(i).toString() + "\n");
			
			ZahraUtility.write2File(fileContentP.toString(),"test\\" + k + "-passengers-N" + passengerNumber + "P-N" + vehicleNumber +
					"V-" + vehicleCapacity + "C" + ".csv");
			ZahraUtility.write2File(fileContentV.toString(),"test\\" + k + "-vehicles-N" + passengerNumber + "P-N" + vehicleNumber + 
					"V-" + vehicleCapacity + "C" + ".csv");
						   
			    bufferedWriter.write(k + "," + meanPassengersUtil + "," + meanVehUtil + "," + matchedPassPercent + "%," + matchedVehPercent + "%," + 
			    		passengers.size() + "," +  vehicles.size() + "\n");//fileContentAggregated.toString());
		
			//===============reseting the available numbers================

				if (meanVehUtil >= vehUtilThres)
					vehicleNumber *= 1.1;
				else
					vehicleNumber *= 0.9;
				{
				if (meanPassengersUtil >= passUtilThres )
					passengerNumber *= 1.1;
				else
					passengerNumber *= 0.9;
				}
						
		}// end of iterations
		
	    bufferedWriter.close();
	    
	    System.out.println("DONE");
		
	}

}
