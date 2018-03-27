package arcGISMapTesting4;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.esri.core.geometry.CoordinateConversion;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.tasks.ags.find.FindParameters;
import com.esri.core.tasks.ags.find.FindResult;
import com.esri.core.tasks.ags.find.FindTask;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorFindParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;
import com.esri.core.tasks.geocode.LocatorReverseGeocodeResult;
import com.esri.core.tasks.geocode.LocatorSuggestionParameters;
import com.esri.core.tasks.geocode.LocatorSuggestionResult;
import com.esri.core.tasks.identify.IdentifyParameters;
import com.esri.core.tasks.identify.IdentifyResult;
import com.esri.core.tasks.identify.IdentifyTask;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;
import com.esri.map.ArcGISDynamicMapServiceLayer;
import com.esri.map.ArcGISFeatureLayer;
import com.esri.map.GraphicsLayer;
import com.esri.map.JMap;
import com.esri.map.LayerInitializeCompleteEvent;
import com.esri.map.LayerInitializeCompleteListener;
import com.esri.map.LayerList;
import com.esri.map.MapOptions;
import com.esri.map.MapOptions.MapType;
import com.esri.runtime.ArcGISRuntime;
import com.esri.toolkit.overlays.InfoPopupOverlay;




/** 
 * @author afrye
 */
public class MapFrame extends JFrame
{
	private final String			REGION4_MAPSERVER_URL					= "http://192.168.53.23:6080/arcgis/rest/services/ISP/ISP_Region4/MapServer";
	private final String			REGION4_FEATUREMAP_URL					= "http://192.168.53.23:6080/arcgis/rest/services/ISP/ISP_Region4_MapService/MapServer";
	private final String			STATE_ADDRESS_LOCATOR_GEOCODESERVER_URL	= "http://192.168.53.23:6080/arcgis/rest/services/ISP/ISP_Region4_StateAddressLocator/GeocodeServer";

	private final String							VALIDATIONSERVICE_URL					= "http://192.168.247.90:8946/Maps/api/rest/ValidationService";

	ArcGISDynamicMapServiceLayer	region4Layer;
	ArcGISDynamicMapServiceLayer	region4FeatureLayer;
	ArcGISFeatureLayer				r4fl;

	MapOptions						mapOptions;
	JMap							map;

	private Locator					stateAddressLocator;
	//private Locator					compositeLocator;
	//private Locator					autoLocator;
	private Locator					arcLocator;

	private PictureMarkerSymbol		symPoint;

	private final int				MAX_SHOWN_LOCATIONS						= 7;
	private int											MAX_GUESSED_ADDRESSES					= 10;
	private final String								SEPARATOR_REGEX							= "%%";

	private GraphicsLayer			addressGraphics;

	private JTextField				txtAddress;
	private String					address;
	JScrollPane											scrollPane;

	JCheckBox						chkFind;
	JCheckBox											chkFeatures;
	JCheckBox										chkValidation;
	javax.swing.JList<String>		addressList;
	DefaultListModel<String>		lstModel								= new DefaultListModel<String>();

	int								listIndex								= -1;

	List<LocatorGeocodeResult>		resultSet								= new ArrayList<LocatorGeocodeResult>();

	List<Map<String, Object>>		attributesList							= new ArrayList<Map<String, Object>>();

	Map<String, Object>				attributes								= new HashMap<>();
	Map<String, Object>				featureAttributes						= new HashMap<>();

	Map<String, Point>				currentFeatureList						= new HashMap<>();

	InfoPopupOverlay				infoPopupOverlay;

	Timer							searchTimer;
	private final int									MSEC_BETWEEN_SEARCH						= 150;
	private boolean									performAutoSearch						= true;

	private final double								REVERSE_LOOKUP_DIST						= 50.0;

	List<Map<String, Object>>		reverseAttList;

	private List<String>			currentAddressList						= new ArrayList<String>();

	private static ArrayList<CompletableFuture<String>>	cfs										= new ArrayList<CompletableFuture<String>>();
	private static ArrayList<CompletableFuture<Void>>	cfs2									= new ArrayList<CompletableFuture<Void>>();

	private ArrayList<Thread>						threadList								= new ArrayList<Thread>();

	private ArrayList<LatLon>							currentValidationResultPoints			= new ArrayList<LatLon>();
	private ArrayList<Map>								currentValidationResultAttributes		= new ArrayList<Map>();
	private Map<String, LatLon>							currentLocList							= new HashMap<String, LatLon>();


	public MapFrame()
	{
		//setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		//ArcGISRuntime.setInstallDirectory( "C:\\Program Files (x86)\\ArcGIS SDKs\\java10.2.4" ); //working path to runtimeDeployemnt: C:\\Program Files (x86)\\ArcGIS SDKs\\java10.2.4

		setSize( 1171, 698 );
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 1165 };
		gridBagLayout.rowHeights = new int[] { 201, 334, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0, 1.0, 0.0 };
		getContentPane().setLayout( gridBagLayout );

		JPanel textPanel = new JPanel();
		textPanel.setBorder( new LineBorder( new Color( 0, 0, 0 ) ) );
		GridBagConstraints gbc_textPanel = new GridBagConstraints();
		gbc_textPanel.fill = GridBagConstraints.BOTH;
		gbc_textPanel.insets = new Insets( 7, 7, 5, 7 );
		gbc_textPanel.gridx = 0;
		gbc_textPanel.gridy = 0;
		getContentPane().add( textPanel, gbc_textPanel );
		GridBagLayout gbl_textPanel = new GridBagLayout();
		gbl_textPanel.columnWidths = new int[] { 11, 30, 30, 73, 139, 166, 0, 133 };
		gbl_textPanel.rowHeights = new int[] { 20, 22, 140, 0 };
		gbl_textPanel.columnWeights = new double[] { 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_textPanel.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		textPanel.setLayout( gbl_textPanel );

		Label label = new Label( "Address:" );
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.fill = GridBagConstraints.BOTH;
		gbc_label.insets = new Insets( 5, 5, 5, 5 );
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		textPanel.add( label, gbc_label );

		txtAddress = new JTextField();
		txtAddress.setFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
		GridBagConstraints gbc_txtAddress = new GridBagConstraints();
		gbc_txtAddress.gridwidth = 2;
		gbc_txtAddress.fill = GridBagConstraints.BOTH;
		gbc_txtAddress.insets = new Insets( 5, 0, 5, 5 );
		gbc_txtAddress.gridx = 1;
		gbc_txtAddress.gridy = 0;
		textPanel.add( txtAddress, gbc_txtAddress );
		txtAddress.setColumns( 10 );

		txtAddress.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e )
			{
				if (!txtAddress.getText().isEmpty() && !txtAddress.getText().startsWith( " " ))
				{
					if (!chkValidation.isSelected())
					{
						FindLocalLocation( txtAddress.getText() );
					}
					else
					{
						ClearAddressList();
						ValidationServiceCall( txtAddress.getText() );
					}
				}
				else
				{
					JOptionPane.showMessageDialog( getContentPane(), "Enter an address" );
				}
			}
		} );

		final JTextComponent tcA = (JTextComponent) txtAddress;

		tcA.getDocument().addDocumentListener( new DocumentListener() {
			public void removeUpdate( DocumentEvent d )
			{
				searchTimer.restart();
				ClearAddressList();
				//AutoSearch();				
				performAutoSearch = true;
			}

			public void insertUpdate( DocumentEvent d )
			{
				searchTimer.restart();
				ClearAddressList();
				//AutoSearch();				
				performAutoSearch = true;
			}

			public void changedUpdate( DocumentEvent d )
			{
				searchTimer.restart();
				ClearAddressList();
				//AutoSearch();				
				performAutoSearch = true;
			}
		} );

		chkFind = new JCheckBox( "Include Find" );
		GridBagConstraints gbc_chkFind = new GridBagConstraints();
		gbc_chkFind.fill = GridBagConstraints.BOTH;
		gbc_chkFind.insets = new Insets( 5, 0, 5, 5 );
		gbc_chkFind.gridx = 3;
		gbc_chkFind.gridy = 0;
		textPanel.add( chkFind, gbc_chkFind );
		chkFind.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged( ItemEvent e )
			{
				ClearAddressList();
				performAutoSearch = true;
				searchTimer.restart();
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					//if (chkFeatures.isSelected())
					//chkFeatures.setSelected( false );
					if (chkValidation.isSelected())
						chkValidation.setSelected( false );
				}
			}
		} );

		chkFeatures = new JCheckBox( "Include Spatial Select" );
		GridBagConstraints gbc_chkFeatures = new GridBagConstraints();
		gbc_chkFeatures.fill = GridBagConstraints.BOTH;
		gbc_chkFeatures.insets = new Insets( 5, 0, 5, 5 );
		gbc_chkFeatures.gridx = 4;
		gbc_chkFeatures.gridy = 0;
		textPanel.add( chkFeatures, gbc_chkFeatures );
		chkFeatures.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged( ItemEvent e )
			{
				ClearAddressList();
				performAutoSearch = true;
				searchTimer.restart();
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					if (chkValidation.isSelected())
						chkValidation.setSelected( false );
				}
			}
		} );

		chkValidation = new JCheckBox( "Validation Service" );
		GridBagConstraints gbc_chkValidation = new GridBagConstraints();
		gbc_chkValidation.fill = GridBagConstraints.BOTH;
		gbc_chkValidation.insets = new Insets( 5, 0, 5, 5 );
		gbc_chkValidation.gridx = 5;
		gbc_chkValidation.gridy = 0;
		textPanel.add( chkValidation, gbc_chkValidation );
		chkValidation.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged( ItemEvent e )
			{
				ClearAddressList();
				performAutoSearch = false;
				//searchTimer.restart();
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					if (chkFind.isSelected())
						chkFind.setSelected( false );
					if (chkFeatures.isSelected())
						chkFeatures.setSelected( false );
				}
			}
		} );

		JButton btnFindAddress = new JButton( "Find" );
		GridBagConstraints gbc_btnFindAddress = new GridBagConstraints();
		gbc_btnFindAddress.fill = GridBagConstraints.BOTH;
		gbc_btnFindAddress.insets = new Insets( 5, 0, 5, 5 );
		gbc_btnFindAddress.gridx = 7;
		gbc_btnFindAddress.gridy = 0;
		textPanel.add( btnFindAddress, gbc_btnFindAddress );
		btnFindAddress.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked( MouseEvent arg0 )
			{
				if (!txtAddress.getText().isEmpty() && !txtAddress.getText().startsWith( " " ))
				{
					if (!chkValidation.isSelected())
					{
						FindLocalLocation( txtAddress.getText() );
					}
					else
					{
						ClearAddressList();
						ValidationServiceCall( txtAddress.getText() );
					}
				}
				else
				{
					JOptionPane.showMessageDialog( getContentPane(), "Enter an address" );
				}
			}
		} );

		JButton btnCenterOnRegion = new JButton( "Center On Region" );
		GridBagConstraints gbc_btnCenterOnRegion = new GridBagConstraints();
		gbc_btnCenterOnRegion.fill = GridBagConstraints.BOTH;
		gbc_btnCenterOnRegion.insets = new Insets( 5, 0, 5, 5 );
		gbc_btnCenterOnRegion.gridx = 8;
		gbc_btnCenterOnRegion.gridy = 0;
		textPanel.add( btnCenterOnRegion, gbc_btnCenterOnRegion );
		btnCenterOnRegion.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked( MouseEvent arg0 )
			{
				map.centerAt( 39, -85.5 );

				//				addressList.repaint();
				//				scrollPane.repaint();
				//				addressList.setModel( lstModel );
				//				update( getGraphics() );
				//				System.err.println( lstModel.toString() );
			}
		} );

		JButton btnClearList = new JButton( "Clear Markers" );
		GridBagConstraints gbc_btnClearList = new GridBagConstraints();
		gbc_btnClearList.fill = GridBagConstraints.BOTH;
		gbc_btnClearList.insets = new Insets( 5, 0, 5, 5 );
		gbc_btnClearList.gridx = 9;
		gbc_btnClearList.gridy = 0;
		textPanel.add( btnClearList, gbc_btnClearList );

		btnClearList.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked( MouseEvent arg0 )
			{
				ClearAddressList();
				currentLocList.clear();
				currentValidationResultPoints.clear();
				resultSet.clear();
				addressGraphics.removeAll();
				attributesList.clear();
			}
		} );

		Label label_1 = new Label( "Addresses Found:" );
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.anchor = GridBagConstraints.NORTHWEST;
		gbc_label_1.insets = new Insets( 0, 5, 5, 5 );
		gbc_label_1.gridwidth = 2;
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 1;
		textPanel.add( label_1, gbc_label_1 );

		JLabel lblNumberOfResults = new JLabel( "Number of Results:" );
		GridBagConstraints gbc_lblNumberOfResults = new GridBagConstraints();
		gbc_lblNumberOfResults.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNumberOfResults.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblNumberOfResults.gridx = 3;
		gbc_lblNumberOfResults.gridy = 1;
		textPanel.add( lblNumberOfResults, gbc_lblNumberOfResults );

		txtNumberOfResults = new JTextField();
		txtNumberOfResults.setText( "10" );
		GridBagConstraints gbc_txtNumberOfResults = new GridBagConstraints();
		gbc_txtNumberOfResults.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNumberOfResults.insets = new Insets( 0, 0, 5, 5 );
		gbc_txtNumberOfResults.gridx = 4;
		gbc_txtNumberOfResults.gridy = 1;
		textPanel.add( txtNumberOfResults, gbc_txtNumberOfResults );
		txtNumberOfResults.setColumns( 10 );
		txtNumberOfResults.setToolTipText( "The number of results (1-100) returned from Find, SpatialSelect, and Validation Service. Default is 10." );
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets( 0, 5, 5, 5 );
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 10;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		textPanel.add( scrollPane, gbc_scrollPane );

		addressList = new javax.swing.JList<String>();
		addressList.setDoubleBuffered( true );

		addressList.addListSelectionListener( new ListSelectionListener() {
			@Override
			public void valueChanged( ListSelectionEvent ev )
			{
				if (ev.getValueIsAdjusting())
				{
					if (addressList.getSelectedIndex() != -1)
					{
						if (!chkValidation.isSelected())
						{
							if (addressList.getSelectedValue().toString().contains( "color=purple" ))
								return;
							if (addressList.getSelectedValue().toString().contains( "color=green" ))
							{
								LatLon loc = currentLocList.get( addressList.getSelectedValue().toString() );
								MarkAndZoom( new Point( loc.getLat(), loc.getLon() ), currentFLAttrList.get( addressList.getSelectedValue().toString() ) );

								//System.out.println( BigDecimal.valueOf( loc.getLat() ) + ", " + BigDecimal.valueOf( loc.getLon() ) );
							}
							else
							{
								FindLocalLocation( currentAddressList.get( addressList.getSelectedIndex() ) );
							}
						}
						else
						{
							
							//MarkAndZoom( currentValidationResultPoints.get( addressList.getSelectedIndex() ), null );

							MarkAndZoom2( new Point( currentValidationResultPoints.get( addressList.getSelectedIndex() ).getLon(),
									currentValidationResultPoints.get( addressList.getSelectedIndex() ).getLat() ), currentValidationResultAttributes.get( addressList.getSelectedIndex() ) );

							/*System.out.println( currentValidationResultPoints.get( addressList.getSelectedIndex() ).getLat() + ", "
									+ currentValidationResultPoints.get( addressList.getSelectedIndex() ).getLon() );*/
						}
					}
					
				}
			}
		} );

		addressList.setFont( new Font( "Arial", Font.PLAIN, 10 ) );

		addressList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

		addressList.setModel( lstModel );
		scrollPane.add( addressList );
		scrollPane.setViewportView( addressList );

		//map options: topographic map, centered at lat-lon 41.9, 12.5 (Rome), zoom level 12
		//39, -85.5
		mapOptions = new MapOptions( MapType.TOPO, 39, -85.5, 8 );


		JPanel mapPanel = new JPanel();
		mapPanel.setBorder( new LineBorder( new Color( 0, 0, 0 ) ) );
		GridBagConstraints gbc_mapPanel = new GridBagConstraints();
		gbc_mapPanel.insets = new Insets( 2, 7, 7, 7 );
		gbc_mapPanel.gridheight = 2;
		gbc_mapPanel.fill = GridBagConstraints.BOTH;
		gbc_mapPanel.gridx = 0;
		gbc_mapPanel.gridy = 1;
		getContentPane().add( mapPanel, gbc_mapPanel );

		map = new JMap( mapOptions );
		map.setToolTipText( "Double-click on map to find nearest address to point clicked." );
		map.setShowingCopyright( false );
		map.setShowingEsriLogo( false );

		map.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked( MouseEvent event )
			{
				if (event.getClickCount() == 2)
				{
					Point mapPoint = map.toMapPoint( event.getX(), event.getY() );
					ReverseLookup( mapPoint );
				}
				if (event.getButton() == 2)
				{
					//Point mapPoint = map.toMapPoint( event.getX(), event.getY() );
					//IdentifyFeatures( mapPoint, attributes );
				}
			}
		} );
		mapPanel.setLayout( new GridLayout( 0, 1, 0, 0 ) );
		map.setBorder( new LineBorder( new Color( 0, 0, 0 ) ) );

		mapPanel.add( map );

		LayerList layers = map.getLayers();

		try
		{

			region4Layer = new ArcGISDynamicMapServiceLayer( REGION4_MAPSERVER_URL );

			region4FeatureLayer = new ArcGISDynamicMapServiceLayer( REGION4_FEATUREMAP_URL );

			map.getLayers().add( region4FeatureLayer );
			map.getLayers().add( region4Layer );

			//map.centerAt( region4Layer.getDefaultSpatialReference()..getCenterX(), region4Layer.getExtent().getCenterY() );

			infoPopupOverlay = new InfoPopupOverlay();
			infoPopupOverlay.setPopupTitle( "Marked Location" );
			//infoPopupOverlay.setItemTitle( "{Match_Addr}" );
			infoPopupOverlay.setInitialPopupSize( new Dimension( 300, 200 ) );

			map.addMapOverlay( infoPopupOverlay );

		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog( getContentPane(), ex.getMessage() );
		}


		//create Locator - default ArcGIS locator: http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer
		arcLocator = Locator.createOnlineLocator( "http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer" );
		stateAddressLocator = Locator.createOnlineLocator( "http://192.168.53.23:6080/arcgis/rest/services/ISP/ISP_Region4_StateAddressLocator/GeocodeServer" );
		//compositeLocator = Locator.createOnlineLocator( "http://192.168.53.23:6080/arcgis/rest/services/ISP/ISP_Region4_CompositeLocator/GeocodeServer" );
		//autoLocator = Locator.createOnlineLocator( "http://192.168.53.23:6080/arcgis/rest/services/ISP/ISP_Region4_StateAddressLocator/GeocodeServer" );

		symPoint = new PictureMarkerSymbol( "http://static.arcgis.com/images/Symbols/Basic/RedShinyPin.png" );
		symPoint.setSize( 40, 40 );
		symPoint.setOffsetY( 20.0f );

		// create a graphics layer to show the locations found
		addressGraphics = new GraphicsLayer();
		addressGraphics.addLayerInitializeCompleteListener( new LayerInitializeCompleteListener() {
			@Override
			public void layerInitializeComplete( LayerInitializeCompleteEvent event )
			{
				infoPopupOverlay.addLayer( event.getLayer() );
			}
		} );

		layers.add( addressGraphics );

			//File dir = new File( File.separator + "ValidationServiceRequestLog" );
			//if (!dir.exists())
			//{
			//dir.mkdir();
			//}

			//String onlineUsersLogName = args[5].toString();
			//String validationLogName = "testOnlineUsersLogFile";

			//File validationLog = new File( dir + File.separator + validationLogName );
			//Creating new File in path above(or overwriting it if there already)

			//if (validationLog.exists())
			//{
			//validationLog.delete();
			//}

			//validationLog.createNewFile();

			//Instantiated needed objects for writing to this file
			//FileWriter fWriter = new FileWriter( validationLog, true );
			//BufferedWriter bWriter = new BufferedWriter( fWriter );
			//pWriter = new PrintWriter( bWriter );

		allOutFields.add( "*" );

		setVisible( true );


		searchTimer = new Timer( MSEC_BETWEEN_SEARCH, autoSearch );
		searchTimer.start();
	}

	private void ClearAddressList()
	{
		lstModel.clear();
		currentAddressList.clear();
	}

	ActionListener autoSearch = new ActionListener() {
		public void actionPerformed( ActionEvent evt )
		{
			if (!performAutoSearch)
				return;

			if (txtAddress.getText().isEmpty())
				return;

			if (txtAddress.getText().startsWith( " " ))
				return;

			if (chkValidation.isSelected())
			{
				performAutoSearch = false;
				return;
			}

			if (!txtNumberOfResults.getText().isEmpty())
			{
				try
				{
					int num;
					num = Integer.parseInt( txtNumberOfResults.getText().trim() );
					MAX_GUESSED_ADDRESSES = Clamp( num, 100, 1 );
				}
				catch (Exception ex)
				{
					MAX_GUESSED_ADDRESSES = 10;
				}
			}
			else
			{
				MAX_GUESSED_ADDRESSES = 10;
			}

			AutoSearch();
		}
	};


	private void AutoSearch()
	{
		if (!performAutoSearch)
			return;

		if (txtAddress.getText().isEmpty())
			return;

		if (txtAddress.getText().startsWith( " " ))
			return;

		if (chkValidation.isSelected())
		{
			performAutoSearch = false;
			return;
		}
		


		ClearAddressList();

		if (!chkFind.isSelected() && !chkFeatures.isSelected() && !chkValidation.isSelected())
		{
			performAutoSearch = false;
			Suggest( txtAddress.getText() );
		}
		else if (chkFind.isSelected() && !chkFeatures.isSelected())
		{
			performAutoSearch = false;
			SuggestAndFind( txtAddress.getText() );
		}
		else if (chkFeatures.isSelected() && !chkFind.isSelected())
		{
			performAutoSearch = false;
			SuggestAndFeatures( txtAddress.getText() );
		}
		else if (chkFeatures.isSelected() && chkFind.isSelected())
		{
			performAutoSearch = false;
			FindAll( txtAddress.getText() );
		}


	}

	public static int Clamp( int value, int max, int min )
	{
		int result = value;
		if (value > max)
			result = max;
		if (value < min)
			result = min;
		return result;
	}

	private void FindLocalLocation( String location )
	{
		LocatorFindParameters params = new LocatorFindParameters( location );
		params.setMaxLocations( 1 );
		params.setOutSR( map.getSpatialReference() );
		params.setOutFields( allOutFields );
		//run the locator find task asynchronously to not freeze the UI
		stateAddressLocator.find( params, new CallbackListener<List<LocatorGeocodeResult>>() {

			@Override
			public void onError( Throwable e )
			{
				FindGlobalLocation( location );
				//throw new Exception( "No locations found. Try another address." );
				//JOptionPane.showMessageDialog( getContentPane(), "No locations found. Try another address." + e.getMessage() );
				//JOptionPane.showMessageDialog( getContentPane(), e.getMessage() );
			}

			@Override
			public void onCallback( List<LocatorGeocodeResult> results )
			{
				if (results != null)
				{
					performAutoSearch = false;

					LocatorGeocodeResult highestScoreResult = results.get( 0 );

					//					Map<String, Object> newAttributes = new HashMap<>();
					//					newAttributes.put( "Located address", highestScoreResult.getAddress() );
					//
					//					newAttributes.put( "Located score", highestScoreResult.getScore() );
					//					newAttributes.put( "Location", highestScoreResult.getLocation() );

					//create a graphic at highest scored location

					IdentifyFeatures( highestScoreResult.getLocation().copy(), highestScoreResult.getAttributes() );


					//Graphic addressGraphic = new Graphic( highestScoreResult.getLocation(), symPoint, attributes );

					//addressGraphics.addGraphic( addressGraphic );

					//Envelope extent = map.getExtent();
					//extent.centerAt( highestScoreResult.getLocation() );
					//map.zoomTo( extent );

					//infoPopupOverlay.setVisible( true );

				}

			}
		} );
	}

	private void FindGlobalLocation( String location )
	{
		LocatorFindParameters params = new LocatorFindParameters( location );
		params.setMaxLocations( 1 );
		params.setOutSR( map.getSpatialReference() );
		params.setOutFields( allOutFields );
		//run the locator find task asynchronously to not freeze the UI
		arcLocator.find( params, new CallbackListener<List<LocatorGeocodeResult>>() {

			@Override
			public void onError( Throwable e )
			{
				//throw new Exception( "No locations found. Try another address." );
				JOptionPane.showMessageDialog( getContentPane(), "No locations found. Try another address.\n" + e.getMessage() );
				//JOptionPane.showMessageDialog( getContentPane(), e.getMessage() );
			}

			@Override
			public void onCallback( List<LocatorGeocodeResult> results )
			{
				if (results != null)
				{
					performAutoSearch = false;

					LocatorGeocodeResult highestScoreResult = results.get( 0 );

					//					Map<String, Object> newAttributes = new HashMap<>();
					//					newAttributes.put( "Located address", highestScoreResult.getAddress() );
					//					newAttributes.put( "Located score", highestScoreResult.getScore() );
					//					newAttributes.put( "Location", highestScoreResult.getLocation() );

					//create a graphic at highest scored location
					MarkAndZoom( highestScoreResult.getLocation(), highestScoreResult.getAttributes() );

					//					addressGraphics.addGraphic( addressGraphic );
					//
					//					Envelope extent = map.getExtent();
					//					extent.centerAt( highestScoreResult.getLocation() );
					//					map.zoomTo( extent );
					//
					//					infoPopupOverlay.setVisible( true );
				}
				

			}
		} );
	}


	static Thread	localLocatorTask;
	static Thread	globalLocatorTask;
	private void SuggestAddresses( String address )
	{


		localLocatorTask = new Thread() {
			public void run()
			{
				try
				{
					LocatorSuggestionParameters params = new LocatorSuggestionParameters( address );

					List<LocatorSuggestionResult> results = stateAddressLocator.suggest( params );

					//LocatorFindParameters p = new LocatorFindParameters( address );
					//p.setMaxLocations( MAX_GUESSED_ADDRESSES );

					//List<LocatorSuggestionResult> results = stateAddressLocator.suggest( params );

					performAutoSearch = false;
					if (results != null)
					{
						try
						{


							for (int i = 0; i < results.size(); i++)
							{

								String addressFound = results.get( i ).getText();

								lstModel.addElement( "<html><font color=red>" + addressFound + "</font></html>" );
								currentAddressList.add( addressFound );

							}

						}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog( getContentPane(), ex.toString() );
						}
					}
					else
					{
						performAutoSearch = true;
					}
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog( getContentPane(), ex.getMessage() );
				}
		    }
		};



		globalLocatorTask = new Thread() {
			public void run()
			{

				try
				{
					LocatorSuggestionParameters params = new LocatorSuggestionParameters( address );
					List<LocatorSuggestionResult> results = arcLocator.suggest( params );

					performAutoSearch = false;
					if (results != null)
					{
						try
						{

							for (int i = 0; i < results.size(); i++)
							{

								String addressFound = results.get( i ).getText();

								lstModel.addElement( "<html><font color=blue>" + addressFound + "</font></html>" );

								currentAddressList.add( addressFound );

							}

						}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog( getContentPane(), ex.toString() );
						}
					}
					else
					{
						performAutoSearch = true;
					}
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog( getContentPane(), ex.getMessage() );
				}
			}
		};





		try
		{
			//lstModel.clear();
			//currentAddressList.clear();

			localLocatorTask.start();
			globalLocatorTask.start();


			localLocatorTask.join();
			globalLocatorTask.join();

		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog( getContentPane(), ex.getMessage() );
		}
	}



	private void FindAddresses2( String address )
	{
		//Thread localLocatorFindTask = new Thread() {
		//public void run()
		//{
				try
				{

					LocatorFindParameters params = new LocatorFindParameters( address );
					params.setMaxLocations( MAX_GUESSED_ADDRESSES );

					List<LocatorGeocodeResult> results = stateAddressLocator.find( params );

					performAutoSearch = false;
					if (results != null)
					{
						try
						{

							for (int i = 0; i < results.size(); i++)
							{

								String addressFound = results.get( i ).getAddress();


								lstModel.addElement( "<html><font color=green>" + addressFound + "</font></html>" );
								currentAddressList.add( addressFound );

							}

						}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog( getContentPane(), ex.toString() );
						}
					}
					else
					{
						performAutoSearch = true;
					}
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog( getContentPane(), ex.getMessage() );
				}
		//}
		//};

		try
		{
			//lstModel.clear();
			//currentAddressList.clear();
			//localLocatorFindTask.start();
			//localLocatorFindTask.join();
			//listClicked = false;
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog( getContentPane(), ex.getMessage() );
		}
	}

	private void SuggestAndFindAddresses2( String address )
	{

		Thread sugestAdd = new Thread() {
			public void run()
			{
				SuggestAddresses( address );
			}
		};

		Thread findAdd = new Thread() {
			public void run()
			{
				FindAddresses2( address );
			}
		};

		try
		{
			lstModel.clear();
			currentAddressList.clear();

			sugestAdd.start();
			findAdd.start();

			sugestAdd.join();
			findAdd.join();
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog( getContentPane(), ex.getMessage() );
		}
	}


	volatile Thread	suggestAdd;
	volatile Thread	findAdd;
	volatile Thread	findFeat;

	private void ReverseLookup( Point mapPoint )
	{
		LocatorReverseGeocodeResult reverseResult;

		try
		{
			// do the reverse geocoding using the locator created
			reverseResult = stateAddressLocator.reverseGeocode( mapPoint, REVERSE_LOOKUP_DIST, region4Layer.getSpatialReference(), map.getSpatialReference() );

			// create and populate attribute map for map tip
			Map<String, Object> reverseAttributes = new HashMap<>();
			for (Entry<String, String> entry : reverseResult.getAddressFields().entrySet())
			{
				reverseAttributes.put( entry.getKey(), entry.getValue() );
			}

			//addressList.add( reverseResult.getAddressFields().values().toString() );
			// create a graphic at this location
			Graphic addressGraphic = new Graphic( reverseResult.getLocation(), symPoint, reverseAttributes );
			addressGraphics.addGraphic( addressGraphic );

			infoPopupOverlay.setVisible( true );
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog( getContentPane(), "Unable to determine an address. Try selecting a location closer to a street.\nMake sure the point is in our Region" );
		}
	}



	private void FindFeatures( String address )
	{
		//lstModel.clear();
		//currentAddressList.clear();

		QueryTask queryTask = new QueryTask( REGION4_FEATUREMAP_URL + "/5" );
		QueryParameters params = new QueryParameters();
		params.setText( address.toUpperCase() );
		params.setMaxFeatures( MAX_GUESSED_ADDRESSES );

		performAutoSearch = false;
		try {
			FeatureResult feat = queryTask.execute( params );
			if (feat.featureCount() > 0)
			{
				int i = 0;
				for (Object record : feat)
				{
					if (i > 7)
						return;

					String featureFound = "";
					Feature feature = (Feature) record;
					
					
					//					for (Entry<String, Object> entry : feature.getAttributes().entrySet())
					//					{
					//						featureFound = entry.getKey() + ": " + entry.getValue() + "\n";
					//
					//					}

					
					featureFound = feature.getAttributes().values().toArray()[0].toString();

					lstModel.addElement( "<html><font color=purple>" + featureFound + "</font></html>" );
					currentAddressList.add( featureFound );

					i++;
				}


			}
			else
			{

			}
		}
		catch (Exception ex)
		{
			performAutoSearch = true;
		}
	}

	private void FindFeatureFromList( String featureName )
	{
		FindTask findTask = new FindTask( REGION4_FEATUREMAP_URL );
		FindParameters params = new FindParameters();
		params.setSearchText( featureName );
		int[] idsList = new int[9];
		int i = 0;
		for (int ids = 0; ids <= 9; ids++)
		{
			if (ids != 0)
			{
				idsList[i] = ids;
				i++;
			}
		}
		params.setLayerIds( idsList );
		params.setOutputSpatialRef( region4FeatureLayer.getSpatialReference() );

		performAutoSearch = false;
		findTask.executeAsync(params, new CallbackListener<List<FindResult>>() {
			
			@Override
			public void onError( Throwable e )
			{
				JOptionPane.showMessageDialog( getContentPane(), "Unable to find Feature" );
			}
						
			@Override
			public void onCallback( List<FindResult> results )
			{
				// check the results
				if ( ( results == null ) || results.isEmpty())
				{
					performAutoSearch = true;
					System.out.println( "There are no records returned" );
					JOptionPane.showMessageDialog( getContentPane(), "There are no records returned" );
					return;
				}

				if (results != null)
				{
					FindResult highest = results.get( 0 );
					//lstModel.addElement( highest.getLayerName() );

					Map<String, Object> newAttributes = new HashMap<>();
					newAttributes.put( "Located address", highest.getValue() );

					Graphic addressGraphic = new Graphic( highest.getGeometry(), symPoint, newAttributes );
					addressGraphics.addGraphic( addressGraphic );

					Envelope extent = map.getExtent();
					//extent.centerAt( (Polyline) highest.getGeometry());
					map.zoomTo( extent );

					infoPopupOverlay.setVisible( true );
				}

				//FindResult highest = results.get( 0 );
				//lstModel.addElement( highest.getLayerName() );

				//				Map<String, Object> newAttributes = new HashMap<>();
				//				newAttributes.put( "Located address", highest.getValue() );
				//
				//				Graphic addressGraphic = new Graphic( highest.getGeometry(), symPoint, newAttributes );
				//				addressGraphics.addGraphic( addressGraphic );
				//
				//				Envelope extent = map.getExtent();
				//				//extent.centerAt( (Polyline) highest.getGeometry());
				//				map.zoomTo( extent );
				//
				//				infoPopupOverlay.setVisible( true );

			}
		} );

	}

	private void IdentifyFeatures( Geometry geo, Map currentAttributes )
	{

		//IdentifyTask identifyTask = new IdentifyTask( REGION4_FEATUREMAP_URL );
		IdentifyParameters identifyparam = new IdentifyParameters();
		//identifyparam.set
		identifyparam.setGeometry( geo );
		identifyparam.setTolerance( 5 );
		identifyparam.setMapExtent( region4FeatureLayer.getExtent() );
		identifyparam.setSpatialReference( region4FeatureLayer.getSpatialReference() );
		identifyparam.setMapHeight( map.getHeight() );
		identifyparam.setMapWidth( map.getWidth() );
		identifyparam.setLayerMode( IdentifyParameters.VISIBLE_LAYERS );
		identifyparam.setDPI( ArcGISRuntime.getDPI() );
		
		//identifyparam.set

		IdentifyTask task = new IdentifyTask( region4FeatureLayer.getUrl() );
	    try {
			IdentifyResult[] results = task.execute( identifyparam );

			if (results.length > 0)
			{

				IdentifyResult highest = results[0];

				//featureAttributes.put( "Feature:", highest.getValue().toString() );

				//				for (Entry<String, Object> entry : highest.getAttributes().entrySet())
				//				{
				//					featureAttributes.put( entry.getKey(), entry.getValue() );
				//
				//				}

				currentAttributes.put( "Feature", highest.getValue().toString() );
				
				Graphic addressGraphic = new Graphic( geo, symPoint, currentAttributes );
				addressGraphics.addGraphic( addressGraphic );

				Envelope extent = map.getExtent();
				extent.centerAt( (Point) geo );
				map.zoomTo( extent );

				infoPopupOverlay.setVisible( true );


				//				for (int i = 0; i < results.length; i++)
				//				{
				//					if (i >= 5)
				//						continue;
				//
				//					IdentifyResult result = results[i];
				//					String resultString = result.getAttributes().get( result.getDisplayFieldName() ) + " (" + result.getLayerName() + ")" + result.getValue();
				//					lstModel.addElement( resultString );
				//				}
			}
			else
			{
				//JOptionPane.showMessageDialog( getContentPane(), "No Features" );
			}
	    } catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog( getContentPane(), e.getMessage() );
	    }

	}

	private void Suggest( String address )
	{
		if (cfs != null)
		{
			if (cfs.size() > 0)
			{
				for (int i = ( cfs.size() - 1 ); i >= 0; i--)
				{
					if (!cfs.get( i ).isDone())
						cfs.get( i ).cancel( true );
				}
				cfs.removeAll( cfs );
			}
		}

		//performAutoSearch = false;
		//ClearAddressList();

		cfs.add( CompletableFuture.supplyAsync( () -> SuggestLocal( address ) ).whenCompleteAsync( ( result, error ) -> {
			List<String> results = Arrays.asList( result.split( SEPARATOR_REGEX ) );
			results.forEach( s -> {
				ListAddress( s, "red", "Geocode" );
			} );
		} ) );
		cfs.add( CompletableFuture.supplyAsync( () -> SuggestGlobal( address ) ).whenCompleteAsync( ( result, error ) -> {
			List<String> results = Arrays.asList( result.split( SEPARATOR_REGEX ) );
			results.forEach( s -> {
				ListAddress( s, "blue", "Online" );
			} );
		} ) );
	}

	private void SuggestAndFind( String address )
	{
		if (cfs.size() > 0)
		{
			for (int i = ( cfs.size() - 1 ); i >= 0; i--)
			{
				if (!cfs.get( i ).isDone())
					cfs.get( i ).cancel( true );

			}
			cfs.removeAll( cfs );
		}

		ExecutorService exec = Executors.newFixedThreadPool( 2 );

		//performAutoSearch = false;
		//ClearAddressList();

		cfs.add( CompletableFuture.supplyAsync( () -> SuggestLocal( address ) ).whenCompleteAsync( ( result, error ) -> {
			List<String> results = Arrays.asList( result.split( SEPARATOR_REGEX ) );
			results.forEach( s -> {
				ListAddress( s, "red", "Geocode" );
			} );
		} ) );
		cfs.add( CompletableFuture.supplyAsync( () -> SuggestGlobal( address ) ).whenCompleteAsync( ( result, error ) -> {
			List<String> results = Arrays.asList( result.split( SEPARATOR_REGEX ) );
			results.forEach( s -> {
				ListAddress( s, "blue", "Online" );
			} );
		} ) );
		cfs.add( CompletableFuture.supplyAsync( () -> FindLocal( address ), exec ).whenCompleteAsync( ( result, error ) -> {
			List<String> results = Arrays.asList( result.split( SEPARATOR_REGEX ) );
			results.forEach( s -> {
				ListAddress( s, "green", "Find" );
			} );
		} ) );
	}

	private void SuggestAndFeatures( String address )
	{
		if (cfs.size() > 0)
		{
			for (int i = ( cfs.size() - 1 ); i >= 0; i--)
			{
				if (!cfs.get( i ).isDone())
					cfs.get( i ).cancel( true );

			}
			cfs.removeAll( cfs );
		}

		ExecutorService exec = Executors.newFixedThreadPool( 2 );

		//performAutoSearch = false;
		//ClearAddressList();

		cfs.add( CompletableFuture.supplyAsync( () -> SuggestLocal( address ) ).whenCompleteAsync( ( result, error ) -> {
			List<String> results = Arrays.asList( result.split( SEPARATOR_REGEX ) );
			results.forEach( s -> {
				ListAddress( s, "red", "Geocode" );
			} );
		} ) );
		cfs.add( CompletableFuture.supplyAsync( () -> SuggestGlobal( address ) ).whenCompleteAsync( ( result, error ) -> {
			List<String> results = Arrays.asList( result.split( SEPARATOR_REGEX ) );
			results.forEach( s -> {
				ListAddress( s, "blue", "Online" );
			} );
		} ) );
		cfs.add( CompletableFuture.supplyAsync( () -> FindFeatures3( address ), exec ).whenCompleteAsync( ( result, error ) -> {
			List<String> results = Arrays.asList( result.split( SEPARATOR_REGEX ) );
			results.forEach( s -> {
				ListAddress( s, "purple", "SpatialSelect" );
			} );
		} ) );
	}

	private void FindAll( String address )
	{
		if (cfs2 != null)
		{
			if (cfs2.size() > 0)
			{
				for (int i = ( cfs2.size() - 1 ); i >= 0; i--)
				{
					if (!cfs2.get( i ).isDone())
						cfs2.get( i ).cancel( true );
				}
				cfs2.removeAll( cfs2 );
			}
		}

		ExecutorService exec = Executors.newFixedThreadPool( 4 );
		//ExecutorService exec2 = Executors.newCachedThreadPool( );

		//performAutoSearch = false;
		//ClearAddressList();

		cfs2.add( CompletableFuture.supplyAsync( () -> SuggestLocal( address ) ).thenAcceptAsync( ( result ) -> {
			List<String> results = Arrays.asList( result.split( SEPARATOR_REGEX ) );
			results.forEach( s -> {
				ListAddress( s, "red", "Geocode" );
			} );
		} ) );
		cfs2.add( CompletableFuture.supplyAsync( () -> SuggestGlobal( address ) ).thenAcceptAsync( ( result ) -> {
			List<String> results = Arrays.asList( result.split( SEPARATOR_REGEX ) );
			results.forEach( s -> {
				ListAddress( s, "blue", "Online" );
			} );
		} ) );
		cfs2.add( CompletableFuture.supplyAsync( () -> FindLocal( address ), exec ).thenAcceptAsync( ( result ) -> {
			List<String> results = Arrays.asList( result.split( SEPARATOR_REGEX ) );
			results.forEach( s -> {
				ListAddress( s, "green", "Find" );
			} );
		} ) );
		cfs2.add( CompletableFuture.supplyAsync( () -> FindFeatures3( address ), exec ).thenAcceptAsync( ( result ) -> {
			List<String> results = Arrays.asList( result.split( SEPARATOR_REGEX ) );
			results.forEach( s -> {
				ListAddress( s, "purple", "SpatialSelect" );
			} );
		} ) );
		

	}

	CompletableFuture<String> a;

	private void FindAllTest( String address )
	{
//		if (a != null)
//		{
//			if (!a.isDone())
//				a.cancel( true );
//		}

		ClearAddressList();

		try {
			a.whenCompleteAsync( ( result, error ) -> {
				List<String> results = Arrays.asList( result.split( SEPARATOR_REGEX ) );
				results.forEach( s -> {
					ListAddress( s, "purple", "SpatialSelect" );
				} );
			} );
		} catch (Exception ex) {
			
		}

	}

	private void SuggestByThread( String address )
	{
		if (threadList != null)
		{
			if (threadList.size() > 0)
			{
				//				threadList.forEach( t -> {
				//					if (t.isAlive())
				//						t.interrupt();
				//				} );
				//				for (int i = ( threadList.size() - 1 ); i >= 0; i--)
				//				{
				//					if (!threadList.get( i ).isAlive())
				//						threadList.get( i ).interrupt();
				//
				//				}
				threadList.removeAll( threadList );
			}
		}

		performAutoSearch = false;
		ClearAddressList();

		threadList.add( new Thread() {
			public void run()
			{
				SuggestLocal( address );
			}
		} );
		threadList.add( new Thread() {
			public void run()
			{
				SuggestGlobal( address );
			}
		} );

		threadList.forEach( t -> t.start() );
	}

	private void SuggestAndFindByThread( String address )
	{
		if (threadList != null)
		{
			if (threadList.size() > 0)
			{
				//				threadList.forEach( t -> {
				//					if (t.isAlive())
				//						t.interrupt();
				//				} );
				//				for (int i = ( threadList.size() - 1 ); i >= 0; i--)
				//				{
				//					if (!threadList.get( i ).isAlive())
				//						threadList.get( i ).interrupt();
				//
				//				}
				threadList.removeAll( threadList );
			}
		}

		performAutoSearch = false;
		ClearAddressList();

		threadList.add( new Thread() {
			public void run()
			{
				SuggestLocal( address );
			}
		} );
		threadList.add( new Thread() {
			public void run()
			{
				SuggestGlobal( address );
			}
		} );
		threadList.add( new Thread() {
			public void run()
			{
				FindLocal( address );
			}
		} );

		threadList.forEach( t -> t.start() );
	}

	private void FindAllByThread( String address )
	{
		if (threadList != null)
		{
			if (threadList.size() > 0)
			{
				//				threadList.forEach( t -> {
				//					if (t.isAlive())
				//						t.interrupt();
				//				} );
				//				for (int i = ( threadList.size() - 1 ); i >= 0; i--)
				//				{
				//					if (!threadList.get( i ).isAlive())
				//						threadList.get( i ).interrupt();
				//
				//				}
				threadList.removeAll( threadList );
			}
		}

		performAutoSearch = false;
		ClearAddressList();

		threadList.add( new Thread() {
			public void run()
			{
				SuggestLocal( address );
			}
		} );
		threadList.add( new Thread() {
			public void run()
			{
				SuggestGlobal( address );
			}
		} );
		threadList.add( new Thread() {
			public void run()
			{
				FindLocal( address );
			}
		} );
		threadList.add( new Thread() {
			public void run()
			{
				FindFeatures3( address );
			}
		} );

		threadList.forEach( t -> t.start() );
	}


	private void ListAddress( String address, String color, String source )
	{
		try
		{
			lstModel.addElement( "<html><font color=" + color + ">" + source + " - " + address + "</font></html>" );
			currentAddressList.add( address );
		}
		catch (Exception ex)
		{

		}
	}


	private String SuggestLocal( String address )
	{
		StringBuilder sbLocal = new StringBuilder();
		try
		{
			LocatorSuggestionParameters params = new LocatorSuggestionParameters( address );


			//performAutoSearch = false;
			List<LocatorSuggestionResult> results = stateAddressLocator.suggest( params );

			if (results != null)
			{
				try
				{
					for (int i = 0; i < results.size(); i++)
					{
						//ListAddress( results.get( i ).getText(), "red", "Geocode" );
						//System.err.println( results.get( i ).getText() );
						if (i == results.size() - 1)
						{
							sbLocal.append( results.get( i ).getText() );
						}
						else
						{
							sbLocal.append( results.get( i ).getText() );
							sbLocal.append( SEPARATOR_REGEX );
						}
					}
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog( getContentPane(), ex.toString() );
				}
			}
			else
			{
				//performAutoSearch = true;
			}
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog( getContentPane(), ex.getMessage() );
		}

		return sbLocal.toString();
	}

	private String SuggestLocal2( String address )
	{
		addresses = "";
		try
		{
			LocatorSuggestionParameters params = new LocatorSuggestionParameters( address );

			performAutoSearch = false;
			stateAddressLocator.suggest( params, new CallbackListener<List<LocatorSuggestionResult>>() {

				@Override
				public void onError( Throwable e )
				{
					JOptionPane.showMessageDialog( getContentPane(), "Unable to find Feature" );
				}

				@Override
				public void onCallback( List<LocatorSuggestionResult> results )
				{
					if (results != null)
					{
						try
						{
							for (int i = 0; i < results.size(); i++)
							{
								ListAddress( results.get( i ).getText(), "red", "Geocode" );
								//								if (i == results.size() - 1)
								//								{
								//									addresses += results.get( i ).getText();
								//								}
								//								else
								//								{
								//									addresses += results.get( i ).getText() + "%%";
								//								}
							}
						}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog( getContentPane(), ex.toString() );
						}
					}
					else
					{
						//performAutoSearch = true;
					}

				}
			} );

		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog( getContentPane(), ex.getMessage() );
		}
		return addresses;

	}

	private String SuggestGlobal( String address )
	{
		StringBuilder sbGlobal = new StringBuilder();
		try
		{
			LocatorSuggestionParameters params = new LocatorSuggestionParameters( address );

			//performAutoSearch = false;
			List<LocatorSuggestionResult> results = arcLocator.suggest( params );

			if (results != null)
			{
				try
				{

					for (int i = 0; i < results.size(); i++)
					{
						//System.err.println( results.get( i ).getText() );
						//ListAddress( results.get( i ).getText(), "blue", "Online" );
						if (i == results.size() - 1)
						{
							sbGlobal.append( results.get( i ).getText() );
						}
						else
						{
							sbGlobal.append( results.get( i ).getText() );
							sbGlobal.append( SEPARATOR_REGEX );
						}

						//String addressFound = results.get( i ).getText();

						//lstModel.addElement( "<html><font color=blue>" + addressFound + "</font></html>" );
						//currentAddressList.add( addressFound );
					}
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog( getContentPane(), ex.toString() );
				}
			}
			else
			{
				//performAutoSearch = true;
			}
		}
		catch (Exception ex)
		{

		}
		return sbGlobal.toString();
	}

	private String SuggestGlobal2( String address )
	{
		addresses = "";
		try
		{
			LocatorSuggestionParameters params = new LocatorSuggestionParameters( address );

			performAutoSearch = false;
			arcLocator.suggest( params, new CallbackListener<List<LocatorSuggestionResult>>() {

				@Override
				public void onError( Throwable e )
				{
					JOptionPane.showMessageDialog( getContentPane(), "Unable to find Feature" );
				}

				@Override
				public void onCallback( List<LocatorSuggestionResult> results )
				{
					if (results != null)
					{
						try
						{

							for (int i = 0; i < results.size(); i++)
							{
								ListAddress( results.get( i ).getText(), "blue", "Online" );
								//								if (i == results.size() - 1)
								//								{
								//									addresses += results.get( i ).getText();
								//								}
								//								else
								//								{
								//									addresses += results.get( i ).getText() + "%%";
								//								}

								//String addressFound = results.get( i ).getText();

								//lstModel.addElement( "<html><font color=blue>" + addressFound + "</font></html>" );
								//currentAddressList.add( addressFound );
							}
						}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog( getContentPane(), ex.toString() );
						}
					}
					else
					{
						//performAutoSearch = true;
					}
				}
			} );

		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog( getContentPane(), ex.getMessage() );
		}
		return addresses;
	}

	Map<String, Map>	currentFLAttrList	= new HashMap<String, Map>();
	List<String> allOutFields = new ArrayList<String>();
	private String FindLocal( String address )
	{
		StringBuilder sbFind = new StringBuilder();

		currentLocList.clear();
		currentFLAttrList.clear();
		try
		{
			LocatorFindParameters params = new LocatorFindParameters( address );
			params.setMaxLocations( MAX_GUESSED_ADDRESSES );
			params.setOutFields( allOutFields );
			//performAutoSearch = false;
			List<LocatorGeocodeResult> results = stateAddressLocator.find( params );

			if (results != null)
			{
				try
				{
					for (int i = 0; i < results.size(); i++)
					{
						//ListAddress( results.get( i ).getAddress(), "green", "Find" );

						LocatorGeocodeResult r = results.get( i );
						int spatialRefIndex = r.toString().indexOf( "spatialReference=" );
						String add = r.toString().substring( 15, spatialRefIndex - 1 );

						currentLocList.put( "<html><font color=green>Find - " + add + "</font></html>", new LatLon( r.getLocation().getX(), r.getLocation().getY() ) );
						currentFLAttrList.put( "<html><font color=green>Find - " + add + "</font></html>", r.getAttributes() );
						if (i == results.size() - 1)
						{
							sbFind.append( add );
						}
						else
						{
							sbFind.append( add );
							sbFind.append( SEPARATOR_REGEX );
						}
					}
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog( getContentPane(), ex.toString() );
				}
			}
			else
			{
				//performAutoSearch = true;
			}
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog( getContentPane(), ex.getMessage() );
		}
		return sbFind.toString();
	}

	private String FindLocal2( String address )
	{
		addresses = "";
		try
		{
			LocatorFindParameters params = new LocatorFindParameters( address );
			params.setMaxLocations( MAX_GUESSED_ADDRESSES );

			performAutoSearch = false;
			stateAddressLocator.find( params, new CallbackListener<List<LocatorGeocodeResult>>() {

				@Override
				public void onError( Throwable e )
				{
					JOptionPane.showMessageDialog( getContentPane(), "Unable to find Feature" );
				}

				@Override
				public void onCallback( List<LocatorGeocodeResult> results )
				{
					if (results != null)
					{
						try
						{
							for (int i = 0; i < results.size(); i++)
							{
								ListAddress( results.get( i ).getAddress(), "green", "Find" );
								//								if (i == results.size() - 1)
								//								{
								//									addresses += results.get( i ).getAddress();
								//								}
								//								else
								//								{
								//									addresses += results.get( i ).getAddress() + "%%";
								//								}

							}
						}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog( getContentPane(), ex.toString() );
						}
					}
					else
					{
						//performAutoSearch = true;
					}
				}

			} );

		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog( getContentPane(), ex.getMessage() );
		}
		return addresses;
	}

	//Only finds features on one layer
	private String FindFeatures2( String address )
	{
		String addresses = "";
		try
		{
			QueryTask queryTask = new QueryTask( REGION4_FEATUREMAP_URL + "/5" );
			QueryParameters params = new QueryParameters();
			params.setText( address.toUpperCase() );
			params.setMaxFeatures( MAX_GUESSED_ADDRESSES );

			performAutoSearch = false;

			FeatureResult feat = queryTask.execute( params );

			if (feat.featureCount() > 0 && feat != null)
			{
				int i = 0;
				for (Object record : feat)
				{
					if (i > MAX_GUESSED_ADDRESSES)
						continue;
					else
					{
						Feature feature = (Feature) record;

						//ListAddress( feature.getAttributes().values().toArray()[0].toString(), "purple" );

						//						if (i == MAX_GUESSED_ADDRESSES - 1)
						//						{
						//							addresses += feature.getAttributes().values().toArray()[0].toString();
						//						}
						//						else
						//						{
						//							addresses += feature.getAttributes().values().toArray()[0].toString() + "%%";
						//						}
						i++;
					}
				}
			}
			else
			{

			}
		}
		catch (Exception ex)
		{
			//performAutoSearch = true;
		}

		return addresses;
	}

	private String FindFeatures3( String address )
	{
		StringBuilder sbFeature = new StringBuilder();

		try
		{
			FindTask findTask = new FindTask( region4FeatureLayer.getUrl() );
			FindParameters params = new FindParameters();
			params.setSearchText( address );
			//			int[] idsList = new int[7];
			//			int i = 0;
			//			for (int ids = 0; ids < 9; ids++)
			//			{
			//				if (ids != 0 && ids != 1)
			//				{
			//					idsList[i] = ids;
			//					i++;
			//				}
			//			}
			params.setLayerIds( new int[] { 4, 5, 6, 7, 8, 9 } );

			//params.setOutputSpatialRef( region4FeatureLayer.getSpatialReference() );

			//performAutoSearch = false;

			List<FindResult> results = findTask.execute( params );

			if (results != null && results.size() > 0)
			{
				for (int j = 0; j < MAX_GUESSED_ADDRESSES; j++)
				{
					//ListAddress( results.get( j ).getValue(), "purple", "SpatialSelect" );
					if (j == results.size() - 1)
					{
						sbFeature.append( results.get( j ).getValue() );
					}
					else
					{
						sbFeature.append( results.get( j ).getValue() );
						sbFeature.append( SEPARATOR_REGEX );
					}
				}
			}

		}
		catch (Exception ex)
		{

		}
		return sbFeature.toString();
	}

	String addresses;
	private JTextField	txtNumberOfResults;
	private String FindFeatures4( String address )
	{
		addresses = "";
		try
		{
			FindTask findTask = new FindTask( REGION4_FEATUREMAP_URL );
			FindParameters params = new FindParameters();
			params.setSearchText( address );
			//			int[] idsList = new int[7];
			//			int i = 0;
			//			for (int ids = 0; ids < 9; ids++)
			//			{
			//				if (ids != 0 && ids != 1)
			//				{
			//					idsList[i] = ids;
			//					i++;
			//				}
			//			}
			params.setLayerIds( new int[] { 3, 4, 5, 6, 7, 8, 9 } );

			params.setOutputSpatialRef( region4FeatureLayer.getSpatialReference() );

			performAutoSearch = false;

			findTask.executeAsync( params, new CallbackListener<List<FindResult>>() {

				@Override
				public void onError( Throwable e )
				{
					JOptionPane.showMessageDialog( getContentPane(), "Unable to find Feature" );
				}

				@Override
				public void onCallback( List<FindResult> results )
				{
					if (results != null && results.size() > 0)
					{
						for (int j = 0; j < MAX_GUESSED_ADDRESSES; j++)
						{
							ListAddress( results.get( j ).getValue(), "purple", "SpatialSelect" );
							//							if (j == results.size() - 1)
							//							{
							//								addresses += results.get( j ).getValue();
							//							}
							//							else
							//							{
							//								addresses += results.get( j ).getValue() + "%%";
							//							}
						}
					}
				}
			} );

		}
		catch (Exception ex)
		{

		}
		return addresses;
	}

	private void ValidationServiceCall( String address )
	{

		performAutoSearch = false;

		DefaultHttpClient httpclient = new DefaultHttpClient();
		try
		{
			StringBuilder sb = new StringBuilder();

			String search = address.trim().replaceAll( " ", "%20" );

			sb.append( "http://192.168.247.90:8946/Maps/api/rest/ValidationService/Validate?request=" );
			sb.append( search );
			sb.append( "&resultsPerPage=" );
			sb.append( MAX_GUESSED_ADDRESSES );
			sb.append( "&pageNumber=1" );

			HttpPost getRequest = new HttpPost( sb.toString() );

			HttpResponse httpResponse = httpclient.execute( getRequest );

			//System.out.println( "Executing request to " + getRequest.getURI() );


			try
			{

				//System.out.println( EntityUtils.toString( httpResponse.getEntity() ) );
				currentValidationResultPoints.clear();
				currentValidationResultAttributes.clear();

				JSONObjectMapper objMapper = new JSONObjectMapper();

				objMapper.convertJSONToPOJO( httpResponse.getEntity() );


				objMapper.getAllResults().forEach( str -> {
					sb.setLength( 0 );
					sb.append( "ValServ - " );
					sb.append( str );
					lstModel.addElement( sb.toString() );
				} );
				
				for (int i = 0; i < objMapper.getValidationServiceObject().getValidateInput().length; i++)
				{
					currentValidationResultAttributes.add( objMapper.getAttributes( i ) );
				}

				currentValidationResultPoints = objMapper.getResultPoints();

			}
			catch (Exception ex)
			{
				System.out.println( "Couldnt create Object with Object Mapper.\n" + ex.getMessage() );
			}


			//System.out.println( "Finished" );

		}
		catch (Exception ex)
		{
			System.out.println( "Problem: " + ex.getMessage() );
		}
		finally
		{
			httpclient.getConnectionManager().shutdown();
		}


	}

	private void MarkAndZoom( Point loc, Map attr )
	{
		Graphic addressGraphic = new Graphic( loc, symPoint, attr );
		addressGraphics.addGraphic( addressGraphic );
		Envelope extent = map.getExtent();
		extent.centerAt( loc );
		map.zoomTo( extent );

		infoPopupOverlay.setVisible( true );
	}

	private void MarkAndZoom2( Point loc, Map<String, Object> attr )
	{
		Point p = CoordinateConversion.decimalDegreesToPoint( loc.getY() + ", " + loc.getX(), region4FeatureLayer.getSpatialReference() );
		Graphic addressGraphic2 = new Graphic( p, symPoint, attr );
		addressGraphics.addGraphic( addressGraphic2 );
		Envelope extent = map.getExtent();
		extent.centerAt( p );
		map.zoomTo( extent );

		infoPopupOverlay.setVisible( true );
	}

	private boolean SLTest( String address )
	{
		try
		{
			LocatorSuggestionParameters params = new LocatorSuggestionParameters( address );
			//performAutoSearch = false;
			List<LocatorSuggestionResult> results = stateAddressLocator.suggest( params );
			if (results != null)
			{
				try
				{
					for (int i = 0; i < results.size(); i++)
					{
						ListAddress( results.get( i ).getText(), "red", "Geocode" );
					}
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog( getContentPane(), ex.toString() );
				}
			}
			else
			{
				//performAutoSearch = true;
			}
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog( getContentPane(), ex.getMessage() );
		}

		return true;

	}

	private boolean GLTest( String address )
	{
		try
		{
			LocatorSuggestionParameters params = new LocatorSuggestionParameters( address );
			//performAutoSearch = false;
			List<LocatorSuggestionResult> results = arcLocator.suggest( params );
			if (results != null)
			{
				try
				{

					for (int i = 0; i < results.size(); i++)
					{
						ListAddress( results.get( i ).getText(), "blue", "Online" );
					}
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog( getContentPane(), ex.toString() );
				}
			}
		}
		catch (Exception ex)
		{

		}
		return true;
	}

	private boolean FLTest( String address )
	{
		try
		{
			LocatorFindParameters params = new LocatorFindParameters( address );
			params.setMaxLocations( MAX_GUESSED_ADDRESSES );
			//performAutoSearch = false;
			List<LocatorGeocodeResult> results = stateAddressLocator.find( params );
			if (results != null)
			{
				try
				{
					for (int i = 0; i < results.size(); i++)
					{
						ListAddress( results.get( i ).getAddress(), "green", "Find" );
					}
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog( getContentPane(), ex.toString() );
				}
			}
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog( getContentPane(), ex.getMessage() );
		}
		return true;
	}

	private boolean FFTest( String address )
	{
		try
		{
			FindTask findTask = new FindTask( region4FeatureLayer.getUrl() );
			FindParameters params = new FindParameters();
			params.setSearchText( address );
			params.setLayerIds( new int[] { 3, 4, 5, 6, 7, 8, 9 } );
			params.setOutputSpatialRef( region4FeatureLayer.getSpatialReference() );
			List<FindResult> results = findTask.execute( params );

			if (results != null && results.size() > 0)
			{
				for (int j = 0; j < MAX_GUESSED_ADDRESSES; j++)
				{
					ListAddress( results.get( j ).getValue(), "purple", "SpatialSelect" );
				}
			}

		}
		catch (Exception ex)
		{

		}
		return true;
	}

}


