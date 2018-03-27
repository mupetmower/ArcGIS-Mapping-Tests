package arcGISMapTesting4;

import com.fasterxml.jackson.annotation.JsonProperty;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationServiceObject
{
	@JsonProperty("AuthorizationAllowed")
	private boolean			authorizationAllowed;

	@JsonProperty("AuthorizationMessage")
	private String			authorizationMessage;

	@JsonProperty("InformationCard")
	private int				informationCard;

	@JsonProperty("TotalHits")
	private int				totalHits;

	@JsonProperty("ValidateInput")
	private ValidateInput[]	validateInput;

	public boolean getauthorizationAllowed()
	{
		return authorizationAllowed;
	}

	public void setAuthorizationAllowed( boolean authorizationAllowed )
	{
		this.authorizationAllowed = authorizationAllowed;
	}

	public void setAuthorizationMessage( String authorizationMessage )
	{
		this.authorizationMessage = authorizationMessage;
	}

	public String getAuthorizationMessage()
	{
		return authorizationMessage;
	}

	public void setInformationCard( int informationCard )
	{
		this.informationCard = informationCard;
	}

	public int getInformationCard()
	{
		return informationCard;
	}

	public void setTotalHits( int totalHits )
	{
		this.totalHits = totalHits;
	}

	public int getTotalHits()
	{
		return totalHits;
	}

	public void setValidateInput( ValidateInput[] validateInput )
	{
		this.validateInput = validateInput;
	}

	public ValidateInput[] getValidateInput()
	{
		return validateInput;
	}
}