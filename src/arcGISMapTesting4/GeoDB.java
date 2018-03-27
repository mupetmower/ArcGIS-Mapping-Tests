package arcGISMapTesting4;

import java.io.FileNotFoundException;

import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.esri.map.FeatureLayer;

public class GeoDB {

	private Geodatabase geoDB;
	private String geoDbUrlString;
	
	public GeoDB(String source) throws FileNotFoundException {
		createGeoDB(source);
	}
	
	public void createGeoDB(String source) throws FileNotFoundException {
		geoDbUrlString = source;
		geoDB = new Geodatabase(geoDbUrlString);
	}
	
	public FeatureLayer getFeatureLayerFromSource(int layerId) {
		return convertFeatureTableToLayer(extractFeatureTable(layerId));
	}
	
	public GeodatabaseFeatureTable extractFeatureTable(int layerId) {
		GeodatabaseFeatureTable geodatabaseFeatureTable = geoDB.getGeodatabaseFeatureTableByLayerId(layerId);
		return geodatabaseFeatureTable;
	}
	
	public FeatureLayer convertFeatureTableToLayer(GeodatabaseFeatureTable geodatabaseFeatureTable) {
		FeatureLayer featureLayer = new FeatureLayer(geodatabaseFeatureTable);
		return featureLayer;
	}

	public Geodatabase getGeoDB() {
		return geoDB;
	}

	public void setGeoDB(Geodatabase geoDB) {
		this.geoDB = geoDB;
	}
	
	public String getGeoDbUrlString() {
		return geoDbUrlString;
	}

	public void setGeoDbUrlString(String geoDbUrlString) {
		this.geoDbUrlString = geoDbUrlString;
	}
		
}
