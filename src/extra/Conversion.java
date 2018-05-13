package extra;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

public class Conversion {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation("EPSG:28355", "WGS84");
		Coord origin = new Coord(320178.2123,5812628.318);
		Coord destination = new Coord (345737.0862,5801959.063);
		System.out.println(ct.transform(origin) + "," + ct.transform(destination));

	}

}
