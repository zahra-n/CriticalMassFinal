package realWorldScenarios;
/*
 * in this class the passenger loop happens only once and everything is calculate there
 */
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.opengis.feature.simple.SimpleFeature;

import extra.ZahraUtility;
import population.RandomPopulation;

public class YarraRangesPercentPop {
	
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
				
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		long startTime = System.currentTimeMillis();
		
		int probability = 0; //counting successful instances
		int prob5Per = 0;
		int prob10Per = 0;
		int prob15Per = 0;
		int prob20Per = 0;
		int allIterations = 0; // counting all instances
		
		double reachMeasure = 100; // in meter
		String vehDist = "U";
		String mainFolder = "YarraRanges";
		

		//=================================== required inputs for vehicle generation =======================================
		
//		Random rndOD = new Random();
		CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84,"EPSG:28355");
		String sa3Name = "Yarra Ranges";
		String zonesFile = "G:\\My Drive\\1-PhDProject\\CriticalMass\\GM-MB\\LivableYarraRanger_SA2_Disso.shp";
		File dataFile = new File(zonesFile);
        dataFile.setReadOnly();
		FileDataStore store = FileDataStoreFinder.getDataStore(dataFile);;
		SimpleFeatureSource source = store.getFeatureSource();
		Map<String,SimpleFeature> featureMap = new LinkedHashMap<>();
		{
			//Iterator to iterate over the features from the shape file
			try ( SimpleFeatureIterator it = source.getFeatures().features() )
			{
				while (it.hasNext())
				{
					// get feature
					SimpleFeature ft = it.next(); //A feature contains a geometry (in this case a polygon) and an arbitrary number
					featureMap.put((String) ft.getAttribute("SA3_NAME16") , ft ) ;
				}
				it.close();
				store.dispose();
			}
			catch ( Exception ee )
			{
				throw new RuntimeException(ee) ;
			}
		}
		//============================================= Passengers Generation ============================================================
		
		//As the passengers are fixed through all iterations we create them here
		//generating the population and reading their locations from the created file in RandomPopulation
		String [][] population = ZahraUtility.Data(22035, 3 , "G:\\My Drive\\1-PhDProject\\CriticalMass\\GM-MB\\YarraRangesPop10%.csv");//33515
		ArrayList<Passenger> passengers = new ArrayList <Passenger>();
		
		for (int i = 1 ; i < population.length ; i++)
		{
			Point passengerCoord = new Point();
			passengerCoord.setLocation(Double.parseDouble(population[i][1]), Double.parseDouble(population[i][2]));
			Passenger tempPassenger = new Passenger (i, passengerCoord, 0.0, 0, 0, -1);
			passengers.add(tempPassenger);
		}
		
		//===================================================================================================================
		int v = 20000;
		for (int probIteration = 1 ; probIteration <= 10 ; probIteration++)
		{
			int vehAddedInIteration = 22500; //CHANGE THE v TOO
			
			long ProbStartTime = System.currentTimeMillis();
			String dir = mainFolder + "\\V" + vehDist + v + "\\" + probIteration + "\\" ;
			double potentialUtil = 5.0;
			double vehUtilThres = 2.0;
			double passUtilThres = 2.0;
			int iterations = 100;
			int iterationWrite = 100;
			int vehicleCapacity = 1;
			int passInterestThres = 5;
			int interestedPassengers = 0;
			Files.createDirectories(Paths.get(dir));
			File log = new File(dir + "aggregated-YarraRanges" + "-V" + v + vehDist + "-" + vehicleCapacity + "C" + ".csv" );
			FileWriter fileWriter = new FileWriter(log, false);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("Iteration,Passengers' mean utility,Vehicles' mean utility,"
					+ "% Interested passengers,% Matched passengers,% Matched Vehicles,"
					+ "Interested passengers,Matched passengers,Matched Vehicles,Total Vehicles\n");
			
			
//			//========================================================================================
//			/*
//			 * here we randomly add interest to a percentage of the passengers 
//			 */
//			
//			for (int intrst = 0 ; intrst < passengers.size() * 0.1 ; intrst++ )
//			{
//				Random rnd = new Random();
//				int randomID = rnd.nextInt(passengers.size());
//				passengers.get(randomID).setInterest(1);
//			}
//			System.out.println(passengers.size() * 0.1);
//			
//			File pasLog = new File(dir + "passengers-YarraRanges" + "-V"
//					+ vehAddedInIteration + vehDist + "-" + vehicleCapacity + "C" + ".csv");
//			FileWriter pasFileWriter = new  FileWriter(pasLog, false);
//			BufferedWriter pasBufferedWriter = new BufferedWriter(pasFileWriter);
//			pasBufferedWriter.write("Passenger_ID,X,Y,Utility,Neighbours,Interest"+ "\n");
//			for (int i = 0 ; i < passengers.size(); ++i)
//				pasBufferedWriter.write(passengers.get(i).toString() + "\n");
//			pasBufferedWriter.close();
			
			//========================================================================================
			
			if (probIteration == 10)
			{
				iterations = 1000;
				iterationWrite = 200;
 			}
			
			ArrayList<Vehicle> vehicles = new ArrayList <Vehicle>();

			//iterations
			for (int k = 1 ; k <= iterations ; k++)
			{
				
				interestedPassengers = 0;
				double passengerUtilSum = 0.0;
				double vehicleUtilSum = 0.0;
				int matchedPassengers = 0 ;
//				int unmatchedVehicles = 0;
				
				
				//generating the vehicles to add in this iteration with a uniform distribution
				int startingID = -1; // this is necessary to have unique id for vehicles through all iterations
				if (vehicles.size() > 0)
					startingID = vehicles.get(vehicles.size()- 1).id;
				
				for (int i = startingID + 1 ; i <= startingID + vehAddedInIteration ; i++ )
				{
					Random rndOD = new Random();
					Point vehicleCoord = new Point();
					Coord originCoord = RandomPopulation.createRandomCoordinateInCcdZone(rndOD,featureMap ,sa3Name, ct);
					vehicleCoord.setLocation(originCoord.getX(), originCoord.getY());
					Vehicle tempVehicle = new Vehicle (i, vehicleCoord, 0.0, 0, vehicleCapacity);
					vehicles.add(tempVehicle);
				}
				
				//========================================================================================
				//
				for (int i = 0 ; i < passengers.size() ; i++ )
				{
					
					passengers.get(i).setMtcheVehID(-1);
					passengers.get(i).setNeighbour(0);
//					if (k > 1 )
					passengers.get(i).setInterest(0);
					
					
					Point passengerCoord = passengers.get(i).coordinate;
					double finalDist = reachMeasure + 10;
					
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
					
					if (passengers.get(i).neighbour > passInterestThres || passengers.get(i).utility > passUtilThres )
					{
						passengers.get(i).setInterest(1);
						interestedPassengers++;
					}
					
					//here the utility is reset to 0 because the interest based on the previous example is already calculated
					passengers.get(i).setUtility(0.0);
					
					if ( passengers.get(i).getInterest() == 1 && passengers.get(i).mtcheVehID > -1)//
					{					
						double util = (reachMeasure - finalDist) / reachMeasure * potentialUtil;
						passengers.get(i).setUtility(util);
						vehicles.get(passengers.get(i).mtcheVehID).setUtility(util);
						vehicles.get(passengers.get(i).mtcheVehID).capacity-- ;
						matchedPassengers++;
					}
				
					passengerUtilSum += passengers.get(i).utility;
	
					
				}// end of passenger loop
				
				
				for (int i = 0 ; i < vehicles.size() ; i++)
				{
					vehicleUtilSum += vehicles.get(i).utility;
				}
				
				//========================================================================================
				
//				if (k % iterationWrite == 0 || k == 1 )
//				{
//					File passLog = new File(dir + k + "-passengers-YarraRanges" + "-V"
//							+ vehAddedInIteration + vehDist + "-" + vehicleCapacity + "C" + ".csv");
//					FileWriter passFileWriter = new  FileWriter(passLog, false);
//					BufferedWriter passBufferedWriter = new BufferedWriter(passFileWriter);
//					passBufferedWriter.write("Iteration,Passenger_ID,X,Y,Utility,Neighbours,Interest"+ "\n");
//					for (int i = 0 ; i < passengers.size(); ++i)
//						passBufferedWriter.write(k + "," + passengers.get(i).toString() + "\n");
//					passBufferedWriter.close();
//					
//					File vehLog = new File(dir + k + "-vehicles-YarraRanges" + "-V" 
//							+ vehAddedInIteration + vehDist + "-" + vehicleCapacity + "C" + ".csv");
//					FileWriter vehFileWriter = new FileWriter(vehLog, false);
//					BufferedWriter vehBufferedWriter = new BufferedWriter(vehFileWriter);
//					vehBufferedWriter.write("Iteration,Vehicle_ID,X,Y,Utility,Capacity,Neighbours" + "\n");
//					for (int i = 0 ; i < vehicles.size(); ++i)
//						vehBufferedWriter.write(k + "," +vehicles.get(i).toString() + "\n");
//					vehBufferedWriter.close();
//				}
				
				
				//==================== calculating the mean utility ====================

				if (k % iterationWrite == 0 || k == 1 )
				{

					StringBuilder fileContentP = new StringBuilder();
					StringBuilder fileContentV = new StringBuilder();
					
					fileContentP.append("Iteration,Passenger_ID,X,Y,Utility,Neighbours,Interest"+ "\n");
					for (int i = 0 ; i < passengers.size(); ++i)
						fileContentP.append(k + "," + passengers.get(i).toString() + "\n");
					
					fileContentV.append("Iteration,Vehicle_ID,X,Y,Utility,Capacity,Neighbours" + "\n");
					for (int i = 0 ; i < vehicles.size(); ++i)
						fileContentV.append(k + "," +vehicles.get(i).toString() + "\n");
					
					ZahraUtility.write2File(fileContentP.toString(), dir + k + "-passengers-YarraRanges" 
							+ "-V" + v + vehDist + "-" + vehicleCapacity + "C" + ".csv");
					ZahraUtility.write2File(fileContentV.toString(), dir + k + "-vehicles-YarraRanges" + "-V" 
							+ v + vehDist + "-" + vehicleCapacity + "C" + ".csv");
				}
				
				double meanPassengersUtil = passengerUtilSum / passengers.size();
				double meanVehUtil = vehicleUtilSum / (vehicles.size() );
				
				double matchedPassPercent = (double) matchedPassengers/passengers.size() * 100;
				double matchedVehPercent = (double) matchedPassengers /vehicles.size() * 100; 
				
				bufferedWriter.write(k + "," + meanPassengersUtil + "," + meanVehUtil + "," 
						+ (double)interestedPassengers/passengers.size() * 100 + "%," 
						+  matchedPassPercent + "%," + matchedVehPercent + "%," + interestedPassengers 
						+ "," + matchedPassengers + "," + matchedPassengers + "," 
						+ vehicles.size() + "\n");
				    
				    
				//=============== vehicles with utility lower than a certain threshold leave ===============
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
			    
			    System.out.println("It." + k);				
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

			System.out.println(probIteration);
			
			bufferedWriter.close();
		    
			long ProbEndTime = System.currentTimeMillis();
			System.out.println(probIteration + " Probability iteration execution time: " + (ProbEndTime - ProbStartTime)/ 60000 + " mins");
		}//end of probability iteration
		
		double successProb = (double) probability/allIterations * 100;
		double successProb5 = (double) prob5Per/allIterations * 100;
		double successProb10 = (double) prob10Per/allIterations * 100;
		double successProb15 = (double) prob15Per/allIterations * 100;
		double successProb20 = (double) prob20Per/allIterations * 100;
		
		System.out.println(successProb + "%");
		
		File plog = new File(mainFolder + "\\YarraRanges_probabilities.csv" ); //outputFile
		FileWriter pFileWriter = new FileWriter(plog, true);
		BufferedWriter pBufferedWriter = new BufferedWriter(pFileWriter);
//		pBufferedWriter.write("Area_sqKm,Population,Population_distribution,Vehicles_added_each_iteration,Vehicles_distribution,Success_probability,Acceptance Threshold%\n");
		pBufferedWriter.write("1040," + passengers.size() + "," + "unknown"  + "," + v + "," + vehDist 
				+ "," + successProb + "," + successProb5 + "," + successProb10 + "," + successProb15 +
				"," + successProb20 + "," + reachMeasure + "\n");
		pBufferedWriter.close();
		
		
		long endTime = System.currentTimeMillis();
		System.out.println("Total execution time: " + (endTime - startTime) / 60000 + " mins");
		System.out.println("DONE");
		Toolkit.getDefaultToolkit().beep();
	}
		

}
