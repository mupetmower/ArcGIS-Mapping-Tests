package arcGISMapTesting4;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CrossStreetResponseAreas
{
	@JsonProperty("RelativeDirection")
	private int				relativeDirection;

	@JsonProperty("ResponseArea")
	private ResponseArea	responseArea;

	public void setRelativeDirection( int relativeDirection )
	{
		this.relativeDirection = relativeDirection;
	}

	public int getRelativeDirection()
	{
		return relativeDirection;
	}

	public void setResponseArea( ResponseArea responseArea )
	{
		this.responseArea = responseArea;
	}

	public ResponseArea getResponseArea()
	{
		return responseArea;
	}
}
