package arcGISMapTesting4;

public class LatLon
{

	private double	lat;
	private double	lon;

	public LatLon(float _lat, float _lon)
	{
		setLat( _lat );
		setLon( _lon );
	}

	public LatLon(double _lat, double _lon)
	{
		setLat( (float) _lat );
		setLon( (float) _lon );
	}

	public void setLat( double l )
	{
		lat = l;
	}

	public double getLat()
	{
		return lat;
	}

	public void setLon( double l )
	{
		lon = l;
	}

	public double getLon()
	{
		return lon;
	}

}
