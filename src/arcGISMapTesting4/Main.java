package arcGISMapTesting4;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.WindowConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.postgis.PGgeometry;
import org.postgresql.geometric.PGpoint;
import org.postgresql.geometric.PGpolygon;
import org.postgresql.util.PGbytea;
import org.postgresql.util.PGobject;

import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.layers.TableQuerySublayerSource;
import com.esri.core.geometry.Geometry.Type;
import com.esri.core.geometry.GeometryEngine;



public class Main
{
	static String tableName = "tabblock2010_25_pophu";

	public static void main( String[] args )
	{
		System.setProperty( "java.util.concurrent.ForkJoinPool.common.parallelism", "10" );
		
		//ShapefileWorkspace s = new ShapefileWorkspace("203", "C:\\Users\\Interact\\Documents\\PostgreSQL-GISTable");
		//createGeoDBMapFrame();
		//createGeoDBMapFrame();
		
		//getGISDataFromPostgres();
		
		createMapFrame();
		
	}
	
	
	public static void getGISDataFromPostgres() {
		try {
			PostgreSQLConnector dbConn = new PostgreSQLConnector();
			ResultSet results = dbConn.initConnection().createStatement()
					.executeQuery("SELECT oid, * FROM public.tabblock2010_25_pophu LIMIT 1");
			
			results.next();
			
			System.out.println(results.getMetaData().getColumnTypeName(1));
			results.getMetaData();
			
			for (int i = 1; i <= results.getMetaData().getColumnCount(); i++)
				System.out.println(results.getMetaData().getColumnTypeName(i) + ": " + results.getObject(i));
			
			testTransform(results.getObject("geom", PGgeometry.class));
			
			
			
//			for (Record r : results) {
//				System.out.println(r.toString());
//			}
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void testTransform(PGgeometry o) throws JSONException, SQLException {
		
		System.out.println(o.getGeometry().getFirstPoint().x);
		
		
	}
	
	
	public static void createMapFrame() {
		try {
			MapFrame mapFrame = new MapFrame();
			mapFrame.setMinimumSize( new Dimension( 1100, 600 ) );
			mapFrame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
			mapFrame.addWindowListener( new WindowListener() {
				public void windowIconified( WindowEvent e ) {}
				public void windowDeiconified( WindowEvent e ) {}
				public void windowDeactivated( WindowEvent e ) {}
				public void windowClosed( WindowEvent e ) {	System.exit( 0 ); }
				public void windowClosing( WindowEvent e ) { System.exit( 0 ); }
				public void windowOpened( WindowEvent e ) {}
				public void windowActivated( WindowEvent e ) {}
			});
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	
	public static void createGeoDBMapFrame() {
		try {						
			GeoDBDisplay geoDB = new GeoDBDisplay();
			geoDB.createAndShow("C:\\Users\\Interact\\Documents\\PostgreSQL-GISTable\\usa.geodatabase");			
			
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	
	

}
