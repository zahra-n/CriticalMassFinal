package realWorldScenarios;

//import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
import java.util.Random;

import org.matsim.api.core.v01.Scenario;
//import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;

import extra.ZahraUtility;

public class YarraRangesDynamicFleet {
	
	private static class Passenger
	{
		int id;
		Point2D coordinate;
		double utility;
		int neighbour;
		int interest;
		
		
		public Passenger(int id, Point2D coordinate, double utility, int neighbour, int interest) {
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
		public Point2D getCoordinate() {
			return coordinate;
		}
		public void setCoordinate(Point2D coordinate) {
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
			return  id + "," + coordinate.getX() + "," + coordinate.getY() + "," + utility + "," + neighbour + "," + interest;
		}
				
	}

	private static class Vehicle
	{
		int id;
		Point2D coordinate;
		double utility;
		int neighbour;
		int capacity;
		
		
		public Vehicle(int id, Point2D coordinate, double utility, int neighbour, int capacity) {
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
		public Point2D getCoordinate() {
			return coordinate;
		}
		public void setCoordinate(Point2D coordinate) {
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
			return  id + "," + coordinate.getX() + "," + coordinate.getY() + "," + utility + "," + capacity + "," + neighbour;
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
		
//		int vehicleNumber = 1;
		String planFile = "input\\run0.output_plans.xml.gz";
//		int passengerNumber = 500;
		double xLimit = 38090.047087; //in meter from left to right : 350025.584608 to 388115.631695
		double yLimit = 35053.705297; //in meter from bottom to top : 5800778.020034 to 5835831.725331
		double xStartLimit = 350025.584608;
		double yStartLimit = 5800778.020034;
		double reachMeasure = 300; // in meter
		double potentialUtil = 5.0;
		double vehUtilThres = 2.0;
		double passUtilThres = 2.0;
		int iterations = 4000;
		int vehicleCapacity = 1;
		int vehAddedInIteration = 2000;
		int passInterestThres = 5;
		double passThresLoseInt = 1.0;
		File log = new File("YarraRanges\\" + "aggregated-" + "YarraRanges" + "-P-U" + vehAddedInIteration + "V-" + vehicleCapacity + "C" + ".csv" );
		FileWriter fileWriter = new FileWriter(log, false);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write("Iteration,Passengers' mean utility,Vehicles' mean utility,matched passengers,matched vehicles,Number of passengers,Number of Vehicles\n");
		
		
		//========================================================================================
		
		ArrayList<Passenger> passengers = new ArrayList <Passenger>();
		ArrayList<Vehicle> vehicles = new ArrayList <Vehicle>();
		
		//reading the Yarra Ranges population
		
		Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		new PopulationReader(scenario).readFile(planFile);
//		Population population = (Population) scenario.getPopulation();
		int id = 0 ;
		for(Person person : scenario.getPopulation().getPersons().values())
		{
			Plan plan = person.getSelectedPlan();
			Activity act = (Activity) plan.getPlanElements().get(0);
			Point2D passengerCoord = new Point2D.Double();
			passengerCoord.setLocation(act.getCoord().getX() , act.getCoord().getY());
			Passenger tempPassenger = new Passenger (id++ , passengerCoord, 0.0, 0, vehicleCapacity);
			passengers.add(tempPassenger);
		}
		

		//iterations
		for (int k = 1 ; k <= iterations ; k++)
		{
			System.out.println("Iteration: " + k);
			
			int interestedPassengers = 0;
			double passengerUtilSum = 0.0;
			double vehicleUtilSum = 0.0;
			int matchedPassengers = 0 ;
			int unmatchedVehicles = 0;
			
			//generating the vehicles to add in this iteration with a uniform distribution
			int startingID = -1; // this is necessary to have unique id for vehicles through all iterations
			if (vehicles.size() > 0)
				startingID = vehicles.get(vehicles.size()- 1).id;

			for (int i = startingID + 1 ; i <= startingID + vehAddedInIteration ; i++ )
			{
				Random rnd = new Random();
				Point2D vehicleCoord = new Point2D.Double();
				vehicleCoord.setLocation((rnd.nextDouble() * xLimit) + xStartLimit, (rnd.nextDouble() * yLimit) + yStartLimit);
				Vehicle tempVehicle = new Vehicle (i, vehicleCoord, 0.0, 0, vehicleCapacity);
				vehicles.add(tempVehicle);
			}
			
			
//			System.out.println("passengers:" + passengers.size());
//			System.out.println("vehicles:" + vehicles.size());
			
			
			//Neighbours: counting the number of available vehicles around each passenger
			for (int i = 0 ; i < passengers.size() ; i++ )
			{
				passengers.get(i).setNeighbour(0);
				Point2D checkPoint = passengers.get(i).coordinate;
				for (int j = 0 ; j < vehicles.size() ; j++ )
				{
					double distance = checkPoint.distance( vehicles.get(j).coordinate);
					if (distance < reachMeasure)//) && vehicles.get(j).getCapacity() > 0)
					{
						passengers.get(i).neighbour++;
					}
				}
			}
			
			//counting interested passengers
			for (int i = 0 ; i < passengers.size() ; i++)
			{
				if (passengers.get(i).neighbour > passInterestThres || passengers.get(i).utility >= passUtilThres )
				{
					passengers.get(i).setInterest(1);
					interestedPassengers++;
				}
				else
					passengers.get(i).setInterest(0);
			}
			
			
			//Neighbours: counting the number of interested passenger around each vehicles 
			for (int i = 0 ; i < vehicles.size() ; i ++)
			{
				//first all the vehicles' properties are reset
				vehicles.get(i).setCapacity(vehicleCapacity);
				vehicles.get(i).setNeighbour(0);
				vehicles.get(i).setUtility(0.0);
				
				Point2D checkPoint = vehicles.get(i).coordinate;
				for (int j = 0 ; j < passengers.size() ; j ++)
				{
					double distance = checkPoint.distance( passengers.get(j).coordinate);
					if (passengers.get(j).interest == 1 && distance < reachMeasure)
					{
						vehicles.get(i).neighbour++;
					}
				}
			}
				
		
			///=================================================================================================================
			
			//		calculating utility according to distance
			for (int i = 0 ; i < passengers.size() ; i++ )
			{
				passengers.get(i).setUtility(0.0);
				if (passengers.get(i).getInterest() == 1)
				{
					double finalDist = xLimit;
					int matchedVehID = -1;
					Point2D checkPoint = passengers.get(i).coordinate;
	
					for (int j = 0 ; j < vehicles.size() ; j++ )
					{
						double distance = checkPoint.distance( vehicles.get(j).coordinate);
						if (distance < reachMeasure)
						{
							double vehUtil = (reachMeasure - distance) / reachMeasure * potentialUtil;
							if (distance < finalDist && vehicles.get(j).capacity > 0 )
							{
								vehicles.get(j).setUtility(vehUtil);
								matchedVehID = vehicles.get(j).id;
								finalDist = distance;
							}
							
						}// end of checking distance
						
					}// end of vehicle loop
					
					if (matchedVehID > -1 ) //it means that a vehicles has been assigned to passenger i, so the matchedVehID has been changed from -1 
					{
						double passUtil = (reachMeasure - finalDist) / reachMeasure * potentialUtil;
						passengers.get(i).setUtility(passUtil);
						vehicles.get(matchedVehID).capacity-- ;
							matchedPassengers++;
					}
					
	//				System.out.println("passenger: " + i + " ,matched vehicle: " + matchedVehID);
				}
				
//				//passengers with utility lower than a certain threshold loose interest
				if (passengers.get(i).utility < passThresLoseInt)
					passengers.get(i).setInterest(0);
	
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
			if (k % 100 == 0)
			{
				StringBuilder fileContentP = new StringBuilder();
				StringBuilder fileContentV = new StringBuilder();
				
				fileContentP.append("Passenger_ID,X,Y,Utility,Neighbours,Interest"+ "\n");
				for (int i = 0 ; i < passengers.size(); ++i)
					fileContentP.append(passengers.get(i).toString() + "\n");
				
				fileContentV.append("Vehicle_ID,X,Y,Utility,Capacity,Neighbours" + "\n");
				for (int i = 0 ; i < vehicles.size(); ++i)
					fileContentV.append(vehicles.get(i).toString() + "\n");
				
				ZahraUtility.write2File(fileContentP.toString(),"YarraRanges\\" + k + "-passengers-" + "YarraRanges-" + "P-U" + vehAddedInIteration +
						"V-" + vehicleCapacity + "C" + ".csv");
				ZahraUtility.write2File(fileContentV.toString(),"YarraRanges\\" + k + "-vehicles-" + "YarraRanges-" + "P-U" + vehAddedInIteration + 
						"V-" + vehicleCapacity + "C" + ".csv");
			}
			
						   
			bufferedWriter.write(k + "," + meanPassengersUtil + "," + meanVehUtil + "," + matchedPassPercent + "%," + matchedVehPercent + "%," + 
			    		interestedPassengers + "," +  vehicles.size() + "\n");//fileContentAggregated.toString());
		
			//===============reseting the available numbers================

//				if (meanVehUtil >= vehUtilThres)
//					vehicleNumber *= 1.1;
//				else
//					vehicleNumber *= 0.9;
//				{
//				if (meanPassengersUtil >= passUtilThres )
//					passengerNumber *= 1.1;
//				else
//					passengerNumber *= 0.9;
//				}
			    
			    
			    //vehicles with utility lower than a certain threshold leave
			    for (int i = 0 ; i < vehicles.size(); i ++)
			    {
			    	if (vehicles.get(i).utility < vehUtilThres)
			    	{
			    		vehicles.remove(i);
			    		i--;
			    	}
			    }
			    
			    //resetting veh id
			    for (int i = 0 ; i < vehicles.size(); i ++)
			    {
			    	vehicles.get(i).setId(i);
			    } 
			    
						
		}// end of iterations
		
	    bufferedWriter.close();
	    
	    System.out.println("DONE");
		
	}

}
