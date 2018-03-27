package arcGISMapTesting4;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.LineBorder;

import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.map.ArcGISDynamicMapServiceLayer;
import com.esri.map.GraphicsLayer;
import com.esri.map.JMap;
import com.esri.map.MapOptions;
import com.esri.map.MapOptions.MapType;
import com.esri.toolkit.overlays.InfoPopupOverlay;

public class MapManager
{
	private MapOptions		mapOptions;
	private JMap			map;

	private final String			REGION4_MAPSERVER_URL	= "http://192.168.53.23:6080/arcgis/rest/services/ISP/ISP_Region4/MapServer";
	ArcGISDynamicMapServiceLayer	region4Layer;

	private GraphicsLayer			addressGraphics;
	InfoPopupOverlay				infoPopupOverlay;
	private PictureMarkerSymbol		symPoint;

	public MapManager()
	{
		InitMap();
		InitRegion4Layer();
	}

	public void InitMap()
	{
		//map options: topographic map, centered at lat-lon 41.9, 12.5 (Rome), zoom level 12
		mapOptions = new MapOptions( MapType.TOPO, 39, -85.5, 7 );

		map = new JMap( mapOptions );
		map.setToolTipText( "Double-click on map to find nearest address to point clicked." );
		map.setShowingCopyright( false );
		map.setShowingEsriLogo( false );

		map.setLocation( 0, 0 );

		map.setSize( 674, 542 );
		map.setBorder( new LineBorder( new Color( 0, 0, 0 ) ) );
	}

	public void InitRegion4Layer()
	{
		region4Layer = new ArcGISDynamicMapServiceLayer( REGION4_MAPSERVER_URL );
		map.getLayers().add( region4Layer );
	}

	public void InitPopupOverlay()
	{
		infoPopupOverlay = new InfoPopupOverlay();
		infoPopupOverlay.setPopupTitle( "Address" );
		//infoPopupOverlay.setItemTitle( "{Match_Addr}" );
		infoPopupOverlay.setInitialPopupSize( new Dimension( 300, 200 ) );
		map.addMapOverlay( infoPopupOverlay );
	}

	public void setMap( JMap m )
	{
		map = m;
	}

	public JMap getMap()
	{
		return map;
	}

}
