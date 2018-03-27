package arcGISMapTesting4;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdditionalColumnData
{
	@JsonProperty("Key")
	private String	key;
	@JsonProperty("Value")
	private String	value;

	public void setKey( String key )
	{
		this.key = key;
	}

	public String getKey()
	{
		return key;
	}

	public void setValue( String value )
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	//	private AdditionalColumnDataPair additionalColumnDataPair;
	//
	//	public void setAdditionalColumnDataPair( AdditionalColumnDataPair additionalColumnDataPair )
	//	{
	//		this.additionalColumnDataPair = additionalColumnDataPair;
	//	}
	//
	//	public AdditionalColumnDataPair getAdditionalColumnDataPair()
	//	{
	//		return additionalColumnDataPair;
	//	}

}
