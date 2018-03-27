package arcGISMapTesting4;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.esri.core.geometry.Point;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONObjectMapper
{
	private HttpEntity		entity;

	ObjectMapper	objectMapper	= new ObjectMapper();

	ValidationServiceObject	validationObj;

	StringBuilder			sb				= new StringBuilder();

	public JSONObjectMapper()
	{
		//setEntity( ent );
	}

	public ValidationServiceObject convertJSONToPOJO( HttpEntity ent ) throws IOException, JsonParseException, JsonMappingException
	{
		setValidationServiceObject( objectMapper.readValue( EntityUtils.toString( ent ), ValidationServiceObject.class ) );
		return validationObj;
	}

	public ArrayList<String> printAllFields()
	{
		ArrayList<String> results = new ArrayList<String>();

		results.add( "AuthorizationAllowed: " + Boolean.toString( getValidationServiceObject().getauthorizationAllowed() ) );
		results.add( "AuthorizationMessage: " + getValidationServiceObject().getAuthorizationMessage() );
		results.add( "InformationCard: " + getValidationServiceObject().getInformationCard() );
		results.add( "TotalHits: " + getValidationServiceObject().getTotalHits() );
		results.add( "---------------------------------------------------------------------" );

		ValidateInput[] validateInput = getValidationServiceObject().getValidateInput();

		for (int i = 0; i < validateInput.length; i++)
		{
			//results.add( getValidationServiceObject().getValidateInput()[i].getAddressRange() );
			results.add( getNextEntry( i ) );
		}

		return results;
	}

	public ArrayList<String> getAllResults()
	{
		ArrayList<String> results = new ArrayList<String>();

		//ValidateInput[] validateInput = getValidationServiceObject().getValidateInput();

		for (int i = 0; i < getValidationServiceObject().getValidateInput().length; i++)
		{
			results.add( getNextEntry( i ) );
		}

		return results;
	}

	public ArrayList<LatLon> getResultPoints()
	{
		ArrayList<LatLon> results = new ArrayList<LatLon>();

		ValidateInput[] validateInput = getValidationServiceObject().getValidateInput();

		for (int i = 0; i < validateInput.length; i++)
		{
			results.add( new LatLon( ( validateInput[i].getPointOnShape().getLat() ), ( validateInput[i].getPointOnShape().getLon() ) ) );
		}

		return results;
	}

	public Map getAttributes( int i )
	{
		Map attributes = new HashMap();
		ValidateInput vi = getValidationServiceObject().getValidateInput()[i];
		

		attributes.put( "AddressRange", vi.getAddressRange() );
		attributes.put( "AliasSubstitution", vi.getAliasSubstitution() );
		attributes.put( "Community", vi.getCommunity() );
		attributes.put( "Confidence", vi.getConfidence() );
		attributes.put( "CrossStreetName", vi.getCrossStreetName() );
		attributes.put( "CrossStreetPostDirectional", vi.getCrossStreetPostDirectional() );
		attributes.put( "CrossStreetPreDirectional", vi.getCrossStreetPreDirectional() );

		//String crossStreetRespAreas = "";

		sb.setLength( 0 );

		CrossStreetResponseAreas[] crossStrRespAreas = vi.getCrossStreetResponseAreas();
		if (crossStrRespAreas != null && crossStrRespAreas.length > 0)
		{
			for (int j = 0; j < crossStrRespAreas.length; j++)
			{
				if (j > 0)
					sb.append( ", " );

				sb.append( crossStrRespAreas[j].toString() );
			}
		}
		attributes.put( "CrossStreetResponseAreas", sb.toString() );

		attributes.put( "CrossStreetType", vi.getCrossStreetType() );

		sb.setLength( 0 );

		String[] crossFrom = vi.getCrossStreetsFrom();
		if (crossFrom != null && crossFrom.length > 0)
		{
			for (int j = 0; j < crossFrom.length; j++)
			{
				if (j > 0)
					sb.append( ", " );

				sb.append( crossFrom[j] );
			}
		}
		attributes.put( "CrossStreetsFrom", sb.toString() );

		sb.setLength( 0 );
		
		String[] crossTo = vi.getCrossStreetsTo();
		if (crossTo != null && crossTo.length > 0)
		{
			for (int j = 0; j < crossTo.length; j++)
			{
				if (j > 0)
					sb.append( ", " );

				sb.append(crossTo[j]);
			}
		}
		attributes.put( "CrossStreetsTo", sb.toString() );

		attributes.put( "IsResponseValid", vi.getIsResponseValid() );
		attributes.put( "MatchObjectId", vi.getMatchObjectId() );
		attributes.put( "MatchSource", vi.getMatchSource() );
		attributes.put( "MatchedAlternate", vi.getMatchedAlternate() );
		attributes.put( "Municipality", vi.getMunicipality() );
		attributes.put( "OriginalInput", vi.getOriginalInput() );
		attributes.put( "OriginalRequestId", vi.getOriginalRequestId() );
		attributes.put( "PointOnOffsetVectorStr", vi.getPointOnOffsetVectorStr() );
		attributes.put( "PointOnShapeStr", vi.getPointOnShapeStr() );
		attributes.put( "PostDirectional", vi.getPostDirectional() );
		attributes.put( "PreDirectional", vi.getPreDirectional() );
		attributes.put( "PrimaryName", vi.getPrimaryName() );



		PrimaryResponseAreas[] primRespAreas = vi.getPrimaryResponseAreas();
		if (primRespAreas != null && primRespAreas.length > 0)
		{
			for (int j = 0; j < primRespAreas.length; j++)
			{
				sb.setLength( 0 );
				sb.append( "RelativeDir=" );
				sb.append( primRespAreas[j].getRelativeDirection() );
				sb.append( ", Agency=" );
				sb.append( primRespAreas[j].getResponseArea().getAgency() );
				sb.append( ", AreaName=" );
				sb.append( primRespAreas[j].getResponseArea().getAreaName() );

				//String value = "RelativeDir=" + primRespAreas[j].getRelativeDirection() + ", Agency=" + primRespAreas[j].getResponseArea().getAgency() + ", AreaName=" + primRespAreas[j].getResponseArea().getAreaName();

				attributes.put( "PrimaryResponseArea" + ( j + 1 ), sb.toString() );
			}
		}
		else
		{
			attributes.put( "PrimaryResponseArea", "" );
		}

		attributes.put( "Region", vi.getRegion() );
		attributes.put( "StreetType", vi.getStreetType() );
		attributes.put( "TieBreakOrdinal", vi.getTieBreakOrdinal() );
		attributes.put( "UniqueKey", vi.getUniqueKey() );
		attributes.put( "ValidationType", vi.getValidationType() );
		for (int j = 0; j < vi.getAdditionalColumnData().length; j++)
		{
			AdditionalColumnData data = vi.getAdditionalColumnData()[j];
			attributes.put( data.getKey(), data.getValue() );
		}

		return attributes;
	}

	public double getResultLat( int i )
	{
		return getValidationServiceObject().getValidateInput()[i].getPointOnShape().getLat();
	}

	public double getResultLon( int i )
	{
		return getValidationServiceObject().getValidateInput()[i].getPointOnShape().getLon();
	}

	public Point getSinglePoint( int i )
	{
		return new Point( getValidationServiceObject().getValidateInput()[i].getPointOnShape().getLat(), getValidationServiceObject().getValidateInput()[i].getPointOnShape().getLon() );
	}

	public String getNextEntry( int i )
	{
		//String entry;
		ValidateInput vi = getValidationServiceObject().getValidateInput()[i];

		sb.setLength( 0 );

		sb.append( "Score=" );

		double score = vi.getConfidence();
		NumberFormat percentFormatter;
		percentFormatter = NumberFormat.getPercentInstance( Locale.US );

		sb.append( percentFormatter.format( score ) );

		sb.append( " | Addr=" );
		sb.append( vi.getAddressRange() );
		sb.append( " | PreDir=" );
		sb.append( vi.getPreDirectional() );
		sb.append( " | Name=" );
		sb.append( vi.getPrimaryName() );
		sb.append( " | StrType=" );
		sb.append( vi.getStreetType() );
		sb.append( " | PostDir=" );
		sb.append( vi.getPostDirectional() );
		sb.append( " | Com=" );
		sb.append( vi.getCommunity() );
		sb.append( " | Reg=" );
		sb.append( vi.getRegion() );
		sb.append( " | Muni=" );
		sb.append( vi.getMunicipality() );
		sb.append( " | Alt=" );
		sb.append( vi.getMatchedAlternate() );
		sb.append( " | OrgInput=" );
		sb.append( vi.getOriginalInput() );
		sb.append( " | OrgReqID=" );
		sb.append( vi.getOriginalRequestId() );
		sb.append( " | Alt=" );
		sb.append( vi.getMatchedAlternate() );
		sb.append( " | MatchObjID=" );
		sb.append( vi.getMatchObjectId() );
		sb.append( " | AliasSub=" );
		sb.append( vi.getAliasSubstitution() );


		/*
		entry = "Score=" + scorePercent + " | Addr=" + vi.getAddressRange() + " | PreDir=" + vi.getPreDirectional() + " | Name=" + vi.getPrimaryName() + " | StrType="
				+ vi.getStreetType() + " | PostDir=" + vi.getPostDirectional() + " | Com=" + vi.getCommunity() + " | Reg=" + vi.getRegion() + " | Muni=" + vi.getMunicipality() + " | Alt="
				+ vi.getMatchedAlternate() + " | OrgInput=" + vi.getOriginalInput() + " | OrgReqID=" + vi.getOriginalRequestId() + " | Alt=" + vi.getMatchedAlternate() + " | MatchObjID="
				+ vi.getMatchObjectId() + " | AliasSub=" + vi.getAliasSubstitution();
		 */

		//entry += " | PrimRespAreas=[ ";

		sb.append( " | PrimRespAreas=[ " );

		PrimaryResponseAreas[] primRespAreas = vi.getPrimaryResponseAreas();
		if (primRespAreas != null && primRespAreas.length > 0)
		{
			for (int j = 0; j < primRespAreas.length; j++)
			{
				PrimaryResponseAreas pr = primRespAreas[j];
				if (j > 0)
					sb.append( ", " );

				sb.append( "(RelativeDir=" );
				sb.append( pr.getRelativeDirection() );
				sb.append( ", Agency=" );
				sb.append( pr.getResponseArea().getAgency() );
				sb.append( " AreaName=" );
				sb.append( pr.getResponseArea().getAreaName() );
				sb.append( ")" );

				//entry += "(RelativeDir=" + pr.getRelativeDirection() + ", Agency=" + pr.getResponseArea().getAgency() + " AreaName=" + pr.getResponseArea().getAreaName() + ")";
			}
		}

		sb.append( " ]" );

		//entry += " ]";
		
		sb.append(" | CrsStrPreDir=");
		sb.append(vi.getCrossStreetPreDirectional());
		sb.append(" | CrsStrName=");
		sb.append(vi.getCrossStreetName());
		sb.append(" | CrsStrPostDir=");
		sb.append(vi.getCrossStreetPostDirectional());
		sb.append(" | CrsStrType=");
		sb.append(vi.getCrossStreetType());

		//entry += " | CrsStrPreDir=" + vi.getCrossStreetPreDirectional() + " | CrsStrName=" + vi.getCrossStreetName() + " | CrsStrPostDir=" + vi.getCrossStreetPostDirectional() + " | CrsStrType=" + vi.getCrossStreetType();

		//entry += " | CrossStrTo=[ ";

		sb.append( " | CrossStrTo=[ " );

		String[] crossTo = vi.getCrossStreetsTo();
		if (crossTo != null && crossTo.length > 0)
		{
			for (int j = 0; j < crossTo.length; j++)
			{
				if (j > 0)
					sb.append( ", " );

				sb.append( crossTo[j] );
			}
		}

		sb.append( " ] | CrossStrFrom=[ " );

		String[] crossFrom = vi.getCrossStreetsTo();
		if (crossFrom != null && crossFrom.length > 0)
		{
			for (int j = 0; j < crossFrom.length; j++)
			{
				if (j > 0)
					sb.append( ", " );

				sb.append( crossFrom[j] );
			}
		}

		sb.append( " ] | CrossStrRespAreas=[ " );

		CrossStreetResponseAreas[] crossStrRespAreas = vi.getCrossStreetResponseAreas();
		if (crossStrRespAreas != null && crossStrRespAreas.length > 0)
		{
			for (int j = 0; j < crossStrRespAreas.length; j++)
			{
				if (j > 0)
					sb.append( ", " );

				sb.append( crossStrRespAreas[j].toString() );
			}
		}

		sb.append( " ] | RespValid=" );

		sb.append( vi.getIsResponseValid() );
		sb.append( " | TieBrkOrd=" );
		sb.append( vi.getTieBreakOrdinal() );
		sb.append( " | ValidType=" );
		sb.append( vi.getValidationType() );
		sb.append( " | UniqueKey=" );
		sb.append( vi.getUniqueKey() );
		sb.append( " | MatchSrc=" );
		sb.append( vi.getMatchSource() );

		//entry += " | RespValid=" + vi.getIsResponseValid() + " | TieBrkOrd=" + vi.getTieBreakOrdinal() + " | ValidType=" + vi.getValidationType() + " | UniqueKey=" + vi.getUniqueKey() + " | MatchSrc=" + vi.getMatchSource();

		sb.append( " | AddColData=[ " );

		AdditionalColumnData[] addColData = vi.getAdditionalColumnData();
		if (addColData != null && addColData.length > 0)
		{
			for (int j = 0; j < addColData.length; j++)
			{
				AdditionalColumnData data = addColData[j];
				if (j > 0)
					sb.append( ", " );

				sb.append( data.getKey() );
				sb.append( "=\"" );
				sb.append( data.getValue() );
				sb.append( "\"" );

				//entry += addColData[j].getKey() + "=\"" + addColData[j].getValue() + "\"";
			}
		}

		sb.append( " ] | PntOnShapeLoc=" );

		PointOnShape ps = vi.getPointOnShape();
		PointOnOffsetVector pv = vi.getPointOnOffsetVector();

		sb.append( vi.getPointOnShapeStr() );
		sb.append( " | PntOnShape={ IsValid=" );
		sb.append( ps.getIsValid() );
		sb.append( ", Lat=" );
		sb.append( ps.getLat() );
		sb.append( ", Lon=" );
		sb.append( ps.getLon() );
		sb.append( ", Zel=" );
		sb.append( ps.getZel() );
		sb.append( " } | PntOnOffsetVectLoc=" );
		sb.append( vi.getPointOnOffsetVectorStr() );
		sb.append( " | PntOnOffsetVect={ IsValid=" );
		sb.append( ", Lat=" );
		sb.append( pv.getLat() );
		sb.append( ", Lon=" );
		sb.append( pv.getLon() );
		sb.append( ", Zel=" );
		sb.append( pv.getZel() );
		sb.append( " }" );

		/*
		entry += " | PntOnShapeLoc=" + vi.getPointOnShapeStr() + " | PntOnShape={ IsValid=" + ps.getIsValid() + ", Lat=" + ps.getLat() + ", Lon=" + ps.getLon() + ", Zel=" + ps.getZel() + " }"
				+ " | PntOnOffsetVectLoc=" + vi.getPointOnOffsetVectorStr() + " | PntOnOffsetVect={ IsValid=" + pv.getIsValid() + ", Lat=" + pv.getLat() + ", Lon=" + pv.getLon() + ", Zel="
				+ pv.getZel() + " }";
		 */

		return sb.toString();
	}

	public void setEntity( HttpEntity ent )
	{
		entity = ent;
	}

	public HttpEntity getEntity()
	{
		return entity;
	}

	public void setObjectMapper( ObjectMapper objMapper )
	{
		objectMapper = objMapper;
	}

	public ObjectMapper getObjectMapper()
	{
		return objectMapper;
	}

	public void setValidationServiceObject( ValidationServiceObject validationServiceObject )
	{
		validationObj = validationServiceObject;
	}

	public ValidationServiceObject getValidationServiceObject()
	{
		return validationObj;
	}
}
