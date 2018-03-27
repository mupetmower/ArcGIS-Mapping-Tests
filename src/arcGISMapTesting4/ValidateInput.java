package arcGISMapTesting4;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidateInput
{

	@JsonProperty("AdditionalColumnData")
	private AdditionalColumnData[]		additionalColumnData;

	@JsonProperty("AddressRange")
	private String						addressRange;

	@JsonProperty("AliasSubstitution")
	private String						aliasSubstitution;

	@JsonProperty("Community")
	private String						community;

	@JsonProperty("Confidence")
	private double						confidence;

	@JsonProperty("CrossStreetName")
	private String						crossStreetName;

	@JsonProperty("CrossStreetPostDirectional")
	private String						crossStreetPostDirectional;

	@JsonProperty("CrossStreetPreDirectional")
	private String						crossStreetPreDirectional;

	@JsonProperty("CrossStreetResponseAreas")
	private CrossStreetResponseAreas[]	crossStreetResponseAreas;

	@JsonProperty("CrossStreetType")
	private String						crossStreetType;

	@JsonProperty("CrossStreetsFrom")
	private String[]					crossStreetsFrom;

	@JsonProperty("CrossStreetsTo")
	private String[]					crossStreetsTo;

	@JsonProperty("IsResponseValid")
	private boolean						isResponseValid;

	@JsonProperty("MatchObjectId")
	private int							matchObjectId;

	@JsonProperty("MatchSource")
	private String						matchSource;

	@JsonProperty("MatchedAlternate")
	private String						matchedAlternate;

	@JsonProperty("Municipality")
	private String						municipality;

	@JsonProperty("OriginalInput")
	private String						originalInput;

	@JsonProperty("OriginalRequestId")
	private String						originalRequestId;

	@JsonProperty("PointOnOffsetVector")
	private PointOnOffsetVector			pointOnOffsetVector;

	@JsonProperty("PointOnOffsetVectorStr")
	private String						pointOnOffsetVectorStr;

	@JsonProperty("PointOnShape")
	private PointOnShape				pointOnShape;

	@JsonProperty("PointOnShapeStr")
	private String						pointOnShapeStr;

	@JsonProperty("PostDirectional")
	private String						postDirectional;

	@JsonProperty("PreDirectional")
	private String						preDirectional;

	@JsonProperty("PrimaryName")
	private String						primaryName;

	@JsonProperty("PrimaryResponseAreas")
	private PrimaryResponseAreas[]		primaryResponseAreas;

	@JsonProperty("Region")
	private String						region;

	@JsonProperty("StreetType")
	private String						streetType;

	@JsonProperty("TieBreakOrdinal")
	private int							tieBreakOrdinal;

	@JsonProperty("UniqueKey")
	private String						uniqueKey;

	@JsonProperty("ValidationType")
	private int							validationType;

	public void setAdditionalColumnData( AdditionalColumnData[] additionalColumnData )
	{
		this.additionalColumnData = additionalColumnData;
	}

	public AdditionalColumnData[] getAdditionalColumnData()
	{
		return additionalColumnData;
	}

	public void setAddressRange( String addressRange )
	{
		this.addressRange = addressRange;
	}

	public String getAddressRange()
	{
		return addressRange;
	}

	public void setAliasSubstitution( String aliasSubstitution )
	{
		this.aliasSubstitution = aliasSubstitution;
	}

	public String getAliasSubstitution()
	{
		return aliasSubstitution;
	}

	public void setCommunity( String community )
	{
		this.community = community;
	}

	public String getCommunity()
	{
		return community;
	}

	public void setConfidence( double confidence )
	{
		this.confidence = confidence;
	}

	public double getConfidence()
	{
		return confidence;
	}

	public void setCrossStreetName( String crossStreetName )
	{
		this.crossStreetName = crossStreetName;
	}

	public String getCrossStreetName()
	{
		return crossStreetName;
	}

	public void setCrossStreetPostDirectional( String crossStreetPostDirectional )
	{
		this.crossStreetPostDirectional = crossStreetPostDirectional;
	}

	public String getCrossStreetPostDirectional()
	{
		return crossStreetPostDirectional;
	}

	public void setCrossStreetPreDirectional( String crossStreetPreDirectional )
	{
		this.crossStreetPreDirectional = crossStreetPreDirectional;
	}

	public String getCrossStreetPreDirectional()
	{
		return crossStreetPreDirectional;
	}

	public void setCrossStreetResponseAreas( CrossStreetResponseAreas[] crossStreetResponseAreas )
	{
		this.crossStreetResponseAreas = crossStreetResponseAreas;
	}

	public CrossStreetResponseAreas[] getCrossStreetResponseAreas()
	{
		return crossStreetResponseAreas;
	}

	public void setCrossStreetType( String crossStreetType )
	{
		this.crossStreetType = crossStreetType;
	}

	public String getCrossStreetType()
	{
		return crossStreetType;
	}

	public void setCrossStreetsFrom( String[] crossStreetsFrom )
	{
		this.crossStreetsFrom = crossStreetsFrom;
	}

	public String[] getCrossStreetsFrom()
	{
		return crossStreetsFrom;
	}

	public void setCrossStreetsTo( String[] crossStreetsTo )
	{
		this.crossStreetsTo = crossStreetsTo;
	}

	public String[] getCrossStreetsTo()
	{
		return crossStreetsTo;
	}

	public void setIsResponseValid( boolean isResponseValid )
	{
		this.isResponseValid = isResponseValid;
	}

	public boolean getIsResponseValid()
	{
		return isResponseValid;
	}

	public void setMatchObjectId( int matchObjectId )
	{
		this.matchObjectId = matchObjectId;
	}

	public int getMatchObjectId()
	{
		return matchObjectId;
	}

	public void setMatchSource( String matchSource )
	{
		this.matchSource = matchSource;
	}

	public String getMatchSource()
	{
		return matchSource;
	}

	public void setMatchedAlternate( String matchedAlternate )
	{
		this.matchedAlternate = matchedAlternate;
	}

	public String getMatchedAlternate()
	{
		return matchedAlternate;
	}

	public void setMunicipality( String municipality )
	{
		this.municipality = municipality;
	}

	public String getMunicipality()
	{
		return municipality;
	}

	public void setOriginalInput( String originalInput )
	{
		this.originalInput = originalInput;
	}

	public String getOriginalInput()
	{
		return originalInput;
	}

	public void setOriginalRequestId( String originalRequestId )
	{
		this.originalRequestId = originalRequestId;
	}

	public String getOriginalRequestId()
	{
		return originalRequestId;
	}

	public void setpointOnOffsetVector( PointOnOffsetVector pointOnOffsetVector )
	{
		this.pointOnOffsetVector = pointOnOffsetVector;
	}

	public PointOnOffsetVector getPointOnOffsetVector()
	{
		return pointOnOffsetVector;
	}

	public void setpointOnOffsetVectorStr( String pointOnOffsetVectorStr )
	{
		this.pointOnOffsetVectorStr = pointOnOffsetVectorStr;
	}

	public String getPointOnOffsetVectorStr()
	{
		return pointOnOffsetVectorStr;
	}

	public void setpointOnShape( PointOnShape pointOnShape )
	{
		this.pointOnShape = pointOnShape;
	}

	public PointOnShape getPointOnShape()
	{
		return pointOnShape;
	}

	public void setpointOnShapeStr( String pointOnShapeStr )
	{
		this.pointOnShapeStr = pointOnShapeStr;
	}

	public String getPointOnShapeStr()
	{
		return pointOnShapeStr;
	}

	public void setPostDirectional( String postDirectional )
	{
		this.postDirectional = postDirectional;
	}

	public String getPostDirectional()
	{
		return postDirectional;
	}

	public void setPreDirectional( String preDirectional )
	{
		this.preDirectional = preDirectional;
	}

	public String getPreDirectional()
	{
		return preDirectional;
	}

	public void setPrimaryName( String primaryName )
	{
		this.primaryName = primaryName;
	}

	public String getPrimaryName()
	{
		return primaryName;
	}

	public void setPrimaryResponseAreas( PrimaryResponseAreas[] primaryResponseAreas )
	{
		this.primaryResponseAreas = primaryResponseAreas;
	}

	public PrimaryResponseAreas[] getPrimaryResponseAreas()
	{
		return primaryResponseAreas;
	}

	public void setRegion( String region )
	{
		this.region = region;
	}

	public String getRegion()
	{
		return region;
	}

	public void setStreetType( String streetType )
	{
		this.streetType = streetType;
	}

	public String getStreetType()
	{
		return streetType;
	}

	public void setTieBreakOrdinal( int tieBreakOrdinal )
	{
		this.tieBreakOrdinal = tieBreakOrdinal;
	}

	public int getTieBreakOrdinal()
	{
		return tieBreakOrdinal;
	}

	public void setUniqueKey( String uniqueKey )
	{
		this.uniqueKey = uniqueKey;
	}

	public String getUniqueKey()
	{
		return uniqueKey;
	}

	public void setValidationType( int validationType )
	{
		this.validationType = validationType;
	}

	public int getValidationType()
	{
		return validationType;
	}
}