package arcGISMapTesting4;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseArea
{
	@JsonProperty("Agency")
	private String	agency;

	@JsonProperty("AreaName")
	private String	areaName;

	public void setAgency( String agency )
	{
		this.agency = agency;
	}

	public String getAgency()
	{
		return agency;
	}

	public void setAreaName( String areaName )
	{
		this.areaName = areaName;
	}

	public String getAreaName()
	{
		return areaName;
	}
}