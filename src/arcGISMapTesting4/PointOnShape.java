package arcGISMapTesting4;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PointOnShape
{
	@JsonProperty("IsValid")
	private boolean	isValid;

	@JsonProperty("Lat")
	private double	lat;

	@JsonProperty("Lon")
	private double	lon;

	@JsonProperty("Zel")
	private double	zel;

	public void setIsValid( boolean isValid )
	{
		this.isValid = isValid;
	}

	public boolean getIsValid()
	{
		return isValid;
	}

	public void setLat( double lat )
	{
		this.lat = lat;
	}

	public double getLat()
	{
		return lat;
	}

	public void setLon( double lon )
	{
		this.lon = lon;
	}

	public double getLon()
	{
		return lon;
	}

	public void setZel( double zel )
	{
		this.zel = zel;
	}

	public double getZel()
	{
		return zel;
	}

}
