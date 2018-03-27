package arcGISMapTesting4;

public class GeoDBDisplay {	
	
	public GeoDBDisplay() {		
		
	}	
	
	public void createAndShow(String geoDBFileSource) {
		try {
			
			GeoDBMapFrame mapFrame = new GeoDBMapFrame();
			
			Map map = new Map();
			
			GeoDB geoDB = new GeoDB(geoDBFileSource);
			map.addLayer(geoDB.getFeatureLayerFromSource(1));
			
			mapFrame.setMap(map);
			
			mapFrame.finishInitAndShow();
			
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
				
	}
	
	
	
	
}
