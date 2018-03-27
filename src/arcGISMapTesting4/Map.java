package arcGISMapTesting4;

import java.awt.Color;

import javax.swing.border.LineBorder;

import com.esri.map.JMap;
import com.esri.map.Layer;
import com.esri.map.MapOptions;
import com.esri.map.MapOptions.MapType;

public class Map {

	private JMap map;
	private MapOptions mapOptions;
	
	
	public Map() {		
		map = new JMap(defaultMapOptions());
		initMap();
	}
	
	public Map(MapOptions mapOptions) {
		this.mapOptions = mapOptions;
		map = new JMap(mapOptions);		
		initMap();
	}
	
	private void initMap() {
		map.setShowingCopyright( false );
		map.setShowingEsriLogo( false );
		map.setBorder( new LineBorder( new Color( 0, 0, 0 ) ) );
	}
	
	private MapOptions defaultMapOptions() {
		mapOptions = new MapOptions( MapType.TOPO, 39, -85.5, 7 );
		return mapOptions;
	}

	public JMap getMap() {
		return map;
	}

	public void setMap(JMap map) {
		this.map = map;
	}

	public MapOptions getMapOptions() {
		return mapOptions;
	}

	public void setMapOptions(MapOptions mapOptions) {
		this.mapOptions = mapOptions;
	}
	
	
	public void addLayer(Layer layer) {
		map.getLayers().add(layer);
	}
	
}
