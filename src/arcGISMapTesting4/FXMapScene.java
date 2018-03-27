package arcGISMapTesting4;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.postgis.Geometry;
import org.postgis.PGgeometry;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.data.ShapefileInfo;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

//Cannot use any of the com.esri.arcgisruntime classes in here without a licensed version of ArcGIS 
public class FXMapScene extends Application {
	
	private MapView mapView;
	GraphicsOverlay graphicsOverlay;	
	
	SimpleMarkerSymbol redMarker = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000, 10);
	SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF800080, 4);
	
	SimpleLineSymbol outlineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, 0xFF005000, 1);
	SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.DIAGONAL_CROSS, 0xFF005000, outlineSymbol);
	
	
	
	public FXMapScene() {
		
	}
	
	public static void main( String[] args )
	{
		ArcGISRuntimeEnvironment.setInstallDirectory( "C:\\Program Files (x86)\\ArcGIS SDKs\\LocalServer100.1\\64" ); //working path to runtimeDeployemnt: C:\\Program Files (x86)\\ArcGIS SDKs\\java10.2.4

		Application.launch(null);
	}
	
	
	@Override
	public void start(Stage stage) throws Exception {
		
		try {
		
			StackPane stackPane = new StackPane();
		    Scene scene = new Scene(stackPane);	
		    
		    stage.setWidth(800);
		    stage.setHeight(700);
		    stage.setScene(scene);
		    stage.show();
		    
			
			ArcGISMap map = new ArcGISMap(Basemap.createStreetsVector());
			mapView = new MapView();
			mapView.setMap(map);
			mapView.setVisible(true);
			
			
//			File shapefile = new File("C:\\Users\\Interact\\Documents\\PostgreSQL-GISTable\\tabblock2010_25_pophu.shp");
//			
//			
//			ShapefileFeatureTable s = new ShapefileFeatureTable(shapefile.getAbsolutePath());
//			
//			FeatureLayer featureLayer = new FeatureLayer(s);
//			featureLayer.addDoneLoadingListener(() -> {
//		        if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
//		          // zoom to the area containing the layer's features
//		          mapView.setViewpointGeometryAsync(featureLayer.getFullExtent());
//		        } else {
//		          System.out.println("Problem loading FeatureLayer");
//		        }
//		      });
//		
//			map.getOperationalLayers().add(featureLayer);
			
			
			
			
			graphicsOverlay = new GraphicsOverlay();
			mapView.getGraphicsOverlays().add(graphicsOverlay);
			
			getGISDataFromPostgres();
		
			stackPane.getChildren().add(mapView);
			
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	
	
	
	public void getGISDataFromPostgres() {
		try {
			PostgreSQLConnector dbConn = new PostgreSQLConnector();
			ResultSet results = dbConn.initConnection().createStatement()
					.executeQuery("SELECT oid, * FROM public.tabblock2010_25_pophu LIMIT 100");
			
			
			while (results.next()) {
				
				for (int i = 1; i <= results.getMetaData().getColumnCount(); i++)
					System.out.println(results.getMetaData().getColumnTypeName(i) + ": " + results.getObject(i));
				
				testTransform(results.getObject("geom", PGgeometry.class));
			
			}
			
//			for (Record r : results) {
//				System.out.println(r.toString());
//			}
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public void testTransform(PGgeometry g) throws JSONException, SQLException {
		Geometry geom = g.getGeometry();
		
		PointCollection points = new PointCollection(SpatialReferences.getWgs84());
		
		for (int i = 0; i < geom.numPoints(); i++) {
			
			Point p = new Point(geom.getPoint(i).getX(), geom.getPoint(i).getY(), SpatialReferences.getWgs84());
			
			Graphic graphic = new Graphic(p, redMarker);
			graphicsOverlay.getGraphics().add(graphic);
			
			points.add(p);						
		}
		
		Polyline polyline = new Polyline(points);
		Graphic graphicLine = new Graphic(polyline, lineSymbol);
		graphicsOverlay.getGraphics().add(graphicLine);
		
		Polygon polygon = new Polygon(points);
		Graphic graphicFill = new Graphic(polygon, fillSymbol);
		graphicsOverlay.getGraphics().add(graphicFill);
		
	}
	
	
	@Override
	public void stop() {
		if (mapView != null) {
			mapView.dispose();
		}
	}
}
