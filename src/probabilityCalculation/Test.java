package probabilityCalculation;

import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import firstExample.ZahraUtility;

public class Test {
	
	public static class Passenger
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
		
		
		for (int p = 10 ; p < 101 ; p+=10 )
		{
			for (int v = 25 ; v < 151 ; v+=25)
			{
				
				int probability = 0; //counting successful instances
				int allIterations = 0; // counting all instances
				double xLimit = 3000; //in meter
				double yLimit = 3000; //in meter
				int area = (int) (xLimit*yLimit/Math.pow(10, 6));
				int passengerNumber = p * area;
				int maxPassengers = 100 * area; 
				int vehAddedInIteration = v ;
				String passDist = "3N";
				String vehDist = "N";
				int interestedPassengers = 0;

				
				for (int probIteration = 0 ; probIteration < 100 ; probIteration++)
				{
					
					String dir = "final\\test\\" + area + "sqkm\\" + passDist + vehDist + "\\" + "P" + passDist + passengerNumber + "\\V" + vehDist + v + "\\" + probIteration + "\\" ;
					double reachMeasure = 100; // in meter
					double potentialUtil = 5.0;
					double vehUtilThres = 2.0;
					double passUtilThres = 2.0;
					int iterations = 100;
					int vehicleCapacity = 1;
					int passInterestThres = 5;
					int iterationWrite = 100;
					
					Files.createDirectories(Paths.get(dir));
					File log = new File(dir + "aggregated-P" + passengerNumber + passDist + "-V" + v + vehDist + "-" + vehicleCapacity + "C" + ".csv" );
					FileWriter fileWriter = new FileWriter(log, false);
					BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
					bufferedWriter.write("Iteration,Passengers' mean utility,Vehicles' mean utility,"
							+ "% Interested passengers,% Matched passengers,% Matched Vehicles,"
							+ "Interested passengers,Matched passengers,Matched Vehicles,Total Vehicles\n");
					
					
					//========================================================================================
					
					ArrayList<Passenger> passengers = new ArrayList <Passenger>();
					ArrayList<Vehicle> vehicles = new ArrayList <Vehicle>();
					
					//generating the population with a normal distribution
					for (int i = 0 ; i < passengerNumber; i++ )
					{
						Random rnd = new Random();
						Point passengerCoord = new Point();
						passengerCoord.setLocation((rnd.nextGaussian()*0.125 + 0.5) * xLimit, (rnd.nextGaussian()*0.125 + 0.5) * yLimit);
						Passenger tempPassenger = new Passenger (i, passengerCoord, 0.0, 0, 0, -1);
						passengers.add(tempPassenger);
					}
					
					vehAddedInIteration = v;
					double vehicleUtilSum = 0.0;
			
					//iterations
					for (int k = 1 ; k <= iterations ; k++)
					{
//						System.out.println( "vehAddedInIteration: " + vehAddedInIteration);
						interestedPassengers = 0;

						double passengerUtilSum = 0.0;
						vehicleUtilSum = 0.0;
						int matchedPassengers = 0 ;
						int unmatchedVehicles = 0;
						
						//generating the vehicles to add in this iteration with a uniform distribution
						int startingID = -1; // this is necessary to have unique id for vehicles through all iterations
						if (vehicles.size() > 0)
							startingID = vehicles.get(vehicles.size()- 1).id;
			
						for (int i = startingID + 1 ; i <= startingID + vehAddedInIteration ; i++ )
						{
							Random rnd = new Random();
							Point vehicleCoord = new Point();
							vehicleCoord.setLocation((rnd.nextGaussian()*0.125 + 0.5) * xLimit, (rnd.nextGaussian()*0.125 + 0.5) * yLimit);
							Vehicle tempVehicle = new Vehicle (i, vehicleCoord, 0.0, 0, vehicleCapacity);
							vehicles.add(tempVehicle);
						}
						
						for (int i = 0 ; i < passengers.size() ; i++ )
						{
							
							passengers.get(i).setMtcheVehID(-1);
							passengers.get(i).setNeighbour(0);
							
							Point passengerCoord = passengers.get(i).coordinate;
							double finalDist = xLimit;
							
							for (int j = 0 ; j < vehicles.size() ; j++ )
							{
								
								Point vehicleCoord = vehicles.get(j).coordinate;
								
								if (Math.abs(passengerCoord.getX() - vehicleCoord.getX()) <= reachMeasure && Math.abs(passengerCoord.getY() - vehicleCoord.getY()) <= reachMeasure )
								{
									double distance = passengerCoord.distance( vehicleCoord );
									
									if (distance < reachMeasure)
									{
										passengers.get(i).neighbour++;
										
										if (distance < finalDist && vehicles.get(j).capacity > 0)
										{
											finalDist = distance;
											passengers.get(i).setMtcheVehID(j);	
										}
									}
								}
							}// end of vehicle loop
							
//							System.out.println("Pass" + passengers.get(i).id + ": " + passengers.get(i).neighbour);
							if (passengers.get(i).neighbour > passInterestThres || passengers.get(i).utility > passUtilThres )
							{
								passengers.get(i).setInterest(1);
								interestedPassengers++;
							}
							else
								passengers.get(i).setInterest(0);
							
							 
							
							
							if (passengers.get(i).getInterest() == 1 && passengers.get(i).mtcheVehID > -1 )
							{					
								double util = (reachMeasure - finalDist) / reachMeasure * potentialUtil;
								passengers.get(i).setUtility(util);
								vehicles.get(passengers.get(i).mtcheVehID).setUtility(util);
								vehicles.get(passengers.get(i).mtcheVehID).capacity-- ;
								matchedPassengers++;
							}
							else
								passengers.get(i).setUtility(0.0);
							
							passengerUtilSum += passengers.get(i).utility;							
							
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

						double meanPassengersUtil = passengerUtilSum / passengers.size();
						
						for (int i = 0 ; i < vehicles.size() ; i++)
						{
							vehicleUtilSum += vehicles.get(i).utility;
						}
						double meanVehUtil = vehicleUtilSum / (vehicles.size() );
						
						double matchedPassPercent = (double) matchedPassengers/passengers.size() * 100;
						double matchedVehPercent = (double) (vehicles.size() - unmatchedVehicles)/vehicles.size() * 100;
						
						if (k % iterationWrite == 0 || k == 1)
						{
							StringBuilder fileContentP = new StringBuilder();
							StringBuilder fileContentV = new StringBuilder();
							
							fileContentP.append("Iteration,Passenger_ID,X,Y,Utility,Neighbours,Interest"+ "\n");
							for (int i = 0 ; i < passengers.size(); ++i)
								fileContentP.append(k + "," + passengers.get(i).toString() + "\n");
							
							fileContentV.append("Iteration,Vehicle_ID,X,Y,Utility,Capacity,Neighbours" + "\n");
							for (int i = 0 ; i < vehicles.size(); ++i)
								fileContentV.append(k + "," +vehicles.get(i).toString() + "\n");
							
							ZahraUtility.write2File(fileContentP.toString(), dir + k + "-passengers-P" + passengerNumber + passDist 
									+ "-V" + v + vehDist + "-" + vehicleCapacity + "C" + ".csv");
							ZahraUtility.write2File(fileContentV.toString(), dir + k + "-vehicles-P" + passengerNumber + passDist + "-V" 
									+ v + vehDist + "-" + vehicleCapacity + "C" + ".csv");
						}
						
						bufferedWriter.write(k + "," + meanPassengersUtil + "," + meanVehUtil + "," 
								+ (double)interestedPassengers/passengers.size() * 100 + "%," 
								+  matchedPassPercent + "%," + matchedVehPercent + "%," + interestedPassengers 
								+ "," + matchedPassengers + "," + (vehicles.size() - unmatchedVehicles) + "," 
								+ vehicles.size() + "\n");//fileContentAggregated.toString());
						    
						    
						    for (int i = 0 ; i < vehicles.size(); i ++)
							{
								if (vehicles.get(i).utility < vehUtilThres)
								{
									vehicles.remove(i);
									i--;
								}
								else
								{
									vehicles.get(i).setUtility(0.0);
									vehicles.get(i).setCapacity(vehicleCapacity);
									vehicles.get(i).setId(i);
								}
							}
						    
							if(meanVehUtil <= vehUtilThres)
								vehAddedInIteration *= 0.9;
						    
						    if (vehicles.size() >= maxPassengers || vehAddedInIteration == 0)
						    	break;
						 	
					}// end of iterations			
					
					
					
					if (interestedPassengers >= 0.1 * passengers.size())
				    {
				    	probability++;
		//		    	System.out.println("p:"+ probability);
				    }
					allIterations++;
		//			System.out.println("total:" + allIterations);
		//		    bufferedWriter.write(probability);
		//			System.out.println(probIteration);
					
					bufferedWriter.close();
				    
				    
				}//end of probability iteration
				double successProb = (double) probability/allIterations * 100; 
				System.out.println(p + " passengers and " + v + " vehicles: " + successProb + "%");
				
				File plog = new File("final\\test\\" + area + "sqkm\\" + passDist + vehDist + "\\" + area + "sqkm_probabilities.csv" );
				FileWriter pFileWriter = new FileWriter(plog, true);
				BufferedWriter pBufferedWriter = new BufferedWriter(pFileWriter);
//				pBufferedWriter.write("Area_sqKm,Population,Population_distribution,Vehicles_added_each_iteration,Vehicles_distribution,Success_probability\n");
				pBufferedWriter.write(area + "," + passengerNumber + "," + passDist + "," + v + "," + vehDist + "," + successProb + "\n");
				pBufferedWriter.close();
				
				}
			}
		
		System.out.println("DONE");
		Toolkit.getDefaultToolkit().beep();
	}//end of main

}//end of class
