/*
 * This class is exactly the same as NormalDistDynamicFleetProb, only with different
 * distributions. The reason for its creation is to simultaneously run various
 * simulations. For comments please refer to NormalDistDynamicFleetProb.
 */
package artificialEnvironment;

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

import extra.Passenger;
import extra.Vehicle;
import extra.ZahraUtility;

public class NUDistDynamicFleetProb {
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		double xLimit = 3000; //in meter
		double yLimit = 3000; //in meter
		int area = (int) (xLimit*yLimit/Math.pow(10, 6));
		String passDist = "N";
		String vehDist = "U";
		
		File plog = new File("final\\" + area + "sqkm\\" + passDist + vehDist + "\\" + area + "sqkm_probabilities.csv" );
		FileWriter pFileWriter = new FileWriter(plog, true);
		BufferedWriter pBufferedWriter = new BufferedWriter(pFileWriter);
//		pBufferedWriter.write("Area_sqKm,Population,Population_distribution,Vehicles_added_each_iteration,Vehicles_distribution,Success_probability,SP5Per,SP10Per,SP15Per,SP20Per\n");
		
		for (int p = 10 ; p < 11 ; p+=10 )
		{
			for (int v = 700; v < 901 ; v+=100)
			{
				
				int probability = 0; //counting successful instances
				int prob5Per = 0;
				int prob10Per = 0;
				int prob15Per = 0;
				int prob20Per = 0;
				int allIterations = 0; // counting all instances
				
				int passengerNumber = p * area;
				int maxPassengers = 100 * area; 
				int vehAddedInIteration = v ;
				
				int interestedPassengers = 0;

				
				for (int probIteration = 0 ; probIteration < 100 ; probIteration++)
				{
					
					String dir = "final\\" + area + "sqkm\\" + passDist + vehDist + "\\" + "P" + passDist + passengerNumber + "\\V" + vehDist + v + "\\" ;//+ probIteration + "\\" ;
					double reachMeasure = 100; // in meter
					double potentialUtil = 5.0;
					double vehUtilThres = 3.5;
					double passUtilThres = 3.5;
					int iterations = 100;
					int vehicleCapacity = 1;
					int passInterestThres = 5;
					int iterationWrite = 100;
					
					Files.createDirectories(Paths.get(dir));
					File log = new File(dir + probIteration + ".aggregated-P" + passengerNumber + passDist + "-V" + v + vehDist + "-" + vehicleCapacity + "C" + ".csv" );
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
							vehicleCoord.setLocation(rnd.nextDouble() * xLimit, rnd.nextDouble() * yLimit);
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
						
//						if (k % iterationWrite == 0 || k == 1)
//						{
//							StringBuilder fileContentP = new StringBuilder();
//							StringBuilder fileContentV = new StringBuilder();
//							
//							fileContentP.append("Iteration,Passenger_ID,X,Y,Utility,Neighbours,Interest"+ "\n");
//							for (int i = 0 ; i < passengers.size(); ++i)
//								fileContentP.append(k + "," + passengers.get(i).toString() + "\n");
//							
//							fileContentV.append("Iteration,Vehicle_ID,X,Y,Utility,Capacity,Neighbours" + "\n");
//							for (int i = 0 ; i < vehicles.size(); ++i)
//								fileContentV.append(k + "," +vehicles.get(i).toString() + "\n");
//							
//							ZahraUtility.write2File(fileContentP.toString(), dir + k + "-passengers-P" + passengerNumber + passDist 
//									+ "-V" + v + vehDist + "-" + vehicleCapacity + "C" + ".csv");
//							ZahraUtility.write2File(fileContentV.toString(), dir + k + "-vehicles-P" + passengerNumber + passDist + "-V" 
//									+ v + vehDist + "-" + vehicleCapacity + "C" + ".csv");
//						}
						
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
						    

						 	
					}// end of iterations			
					
					
					
					if (interestedPassengers > 0) 
				    	probability++ ;
				    if (interestedPassengers >= 0.05 * passengers.size())
				    	prob5Per++ ;
				    if (interestedPassengers >= 0.1 * passengers.size())
				    	prob10Per++ ;
				    if (interestedPassengers >= 0.15 * passengers.size())
				    	prob15Per++ ;
				    if (interestedPassengers >= 0.2 * passengers.size())
				    	prob20Per++ ;
				    
					allIterations++;
		//			System.out.println("total:" + allIterations);
		//		    bufferedWriter.write(probability);
		//			System.out.println(probIteration);
					
					bufferedWriter.close();
				    
				    
				}//end of probability iteration
				
				double successProb = (double) probability/allIterations * 100;
				double successProb5 = (double) prob5Per/allIterations * 100;
				double successProb10 = (double) prob10Per/allIterations * 100;
				double successProb15 = (double) prob15Per/allIterations * 100;
				double successProb20 = (double) prob20Per/allIterations * 100;
				System.out.println(p + " passengers and " + v + " vehicles: " + successProb + "%");
				

				pBufferedWriter.write(area + "," + passengerNumber + "," + passDist + "," + v + "," + vehDist + "," +
										successProb + "," + successProb5 + "," + successProb10 + "," + successProb15 +
										"," + successProb20 + "\n");
				
				}
			}
		pBufferedWriter.close();
		System.out.println("DONE");
		Toolkit.getDefaultToolkit().beep();
	}//end of main

}//end of class
