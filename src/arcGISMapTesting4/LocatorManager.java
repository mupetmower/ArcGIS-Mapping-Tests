package arcGISMapTesting4;

import java.util.ArrayList;
import java.util.List;

import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;

public class LocatorManager
{

	private Locator		stateAddressLocator;
	private Locator		compositeLocator;
	private Locator		autoLocator;
	private Locator		arcLocator;

	private final int	MAX_SHOWN_LOCATIONS		= 20;
	private final int	MAX_GUESSED_ADDRESSES	= 20;

	List<LocatorGeocodeResult>	resultSet				= new ArrayList<LocatorGeocodeResult>();

	public LocatorManager()
	{

	}

	public void ReverseLookup()
	{

	}

}
