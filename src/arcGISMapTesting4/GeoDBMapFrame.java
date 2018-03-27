package arcGISMapTesting4;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;

import com.esri.map.JMap;

public class GeoDBMapFrame extends JFrame {
	
	private JPanel mapHolder;
	
	
	public GeoDBMapFrame() {
		initFrame();
		initMapHolder();
		showFrame();
	}
	
	private void initFrame() {
		getContentPane().setLayout(new GridLayout());
		this.setSize(new Dimension(1100, 600));
		this.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		this.addWindowListener( new WindowListener() {
			public void windowIconified( WindowEvent e ) {}
			public void windowDeiconified( WindowEvent e ) {}
			public void windowDeactivated( WindowEvent e ) {}
			public void windowClosed( WindowEvent e ) {	System.exit( 0 ); }
			public void windowClosing( WindowEvent e ) { System.exit( 0 ); }
			public void windowOpened( WindowEvent e ) {}
			public void windowActivated( WindowEvent e ) {}
		});
	}
	
	public void initMapHolder() {
		mapHolder = new JPanel();
		mapHolder.setLayout( new GridLayout() );
		mapHolder.setBorder( new LineBorder( new Color( 0, 0, 0 ) ) );
		this.getContentPane().add(mapHolder);
	}
	
	public void showFrame() {
		getContentPane().repaint();
		setVisible(true);
	}

	public void finishInitAndShow() {
		getContentPane().repaint();
		setVisible(true);
	}

	public JPanel getMapHolder() {
		return mapHolder;
	}

	public void setMapHolder(JPanel mapHolder) {
		this.mapHolder = mapHolder;
	}	
	
	public void setMap(Map map) {
		mapHolder.add(map.getMap());
	}
}
