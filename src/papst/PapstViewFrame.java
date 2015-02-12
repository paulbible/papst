/**
 * PERMISSION IS HEREBY GRANTED, FREE OF CHARGE, TO ANY PERSON OBTAINING A COPY 
 * OF THIS SOFTWARE AND ASSOCIATED DOCUMENTATION FILES (THE "SOFTWARE"), TO USE
 * THE SOFTWARE WITHOUT RESTRICTION, INCLUDING WITHOUT LIMITATION THE RIGHTS TO
 * USE, COPY, MODIFY, MERGE, PUBLISH, DISTRIBUTE, AND/OR SELL COPIES OF THE 
 * SOFTWARE, AND TO PERMIT PERSONS TO WHOM THE SOFTWARE IS FURNISHED TO DO SO,
 * SUBJECT TO THE FOLLOWING LIMITATIONS OF LIABILITY AND SUCH LANGUAGE SHALL BE 
 * INCLUDED IN ALL COPIES OR REDISTRIBUTIONS OF ANY COMPLETE OR PORTION OF THIS 
 * SOFTWARE.
 * 
 * SOFTWARE IS BEING DEVELOPED IN PART AT THE NATIONAL INSTITUTE OF ARTHRITIS 
 * AND MUSCULOSKELTAL AND SKIN DISEASES (NIAMS), NATIONAL INSTITUTES OF HEALTH
 * (NIH) BY AN EMPLOYEE OF THE UNITED STATES GOVERNMENT IN THE COURSE OF THEIR 
 * OFFICIAL DUTIES. PURSUANT TO TITLE 17, SECTION 105 OF THE UNITED STATES CODE, 
 * THIS SOFTWARE IS NOT SUBJECT TO COPYRIGHT PROTECTION AND IS IN THE PUBLIC 
 * DOMAIN. EXCEPT AS CONTAINED IN THIS NOTICE, THE NAME OF THE AUTHORS, THE
 * NATIONAL INSTITUTE OF ARTHRITIS AND MUSCULOSKELTAL AND SKIN DISEASES, OR THE
 * NATIONAL INSTITUTES OF HEALTH  MAY NOT  BE USED TO ENDORSE OR PROMOTE PRODUCTS
 * DERIVED FROM THIS SOFTWARE WITHOUT SPECIFIC PRIOR WRITTEN PERMISSION FROM 
 * THE NIAMS OR THE NIH. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF 
 * ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO WARRANTIES OF 
 * MERCHANTABILITY, FITNESS  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR UNITED STATES GOVERNMENT OR ANY AGENCY THEREOF BE 
 * LIABLE FOR ANY CLAIM, DAMAGES OR ANY OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
 * SOFTWARE OR THE USE THEREOF.
 * 
 * Author: Paul W. Bible
 */
package papst;

import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;

import javax.swing.JTabbedPane;
import javax.swing.Icon;

import java.awt.FlowLayout;

import javax.swing.JSeparator;

import java.awt.SystemColor;

import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JToggleButton;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;

import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JComboBox;

import papst.models.*;
import papst.utility.*;

public class PapstViewFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CommandLog log;
	
	private Vector<JTable> resultTables;
	
	private JTextField txtFldUpstreamSetting;
	private JTextField txtFldDownstreamSetting;
	private JPanel statusPanel;
	private JIconButton btnLoadRefseq;
	private JLabel currentStatusLabel;
	private JLabel centerStatusLabel;
	private JLabel rightStatusLabel;
	private JPanel centerPanel;
	private JTabbedPane ribbonTabbedPane;
	private JPanel homePanel;
	private JPanel homeButtonPanel;
	private JSeparator homeSeparator1;
	private JPanel baseTrackHomePanel;
	private JPanel baseTrackBtnHomePanel;
	private JIconButton btnLoadPeakBase;
	private JPanel featureTrackHomePanel;
	private JPanel featureTrackBtnHomePanel;
	private JIconButton btnLoadPeaks;
	private JIconButton btnLoadPeaksFolder;
	private JIconButton btnSaveSession;
	private JIconButton btnLoadSession;
	private JLabel featureTrackSectionLabel;
	private JPanel dataPanel;
	private JPanel dataButtonPanel;
	private JPanel exportDataPanel;
	private JPanel exportDataBtnPanel;
	private JIconButton btnExportTable;
	private JLabel exportDataSectionLabel;
	private JSeparator dataSeparator1;
	private JSeparator dataSeparator2;
	private JPanel settingsPanel;
	private JPanel searchSettingsPanel;
	private JCheckBox chckbxShowIsoforms;
	private JLabel lblUpstreamOfTss;
	private JLabel lblDownstreamOfTss;
	private JToggleButton genomeBaseTglBtn;
	private JToggleButton peakAsBaseTglBtn;
	private JPanel loadGenomePanel;
	private JPanel loadPeakBasePanel;
	private JSplitPane splitPane;
	private JPanel leftDataPanel;
	private JPanel rightDataPanel;
	private JSplitPane leftDataSplitPane;
	private JPanel featureLeftDataPanel;
	private JPanel filterLeftDataPanel;
	private JPanel featureHeaderPanel;
	private JLabel featureHeaderLbl;
	private JPanel filterHeaderPanel;
	private JLabel filterHeaderLbl;
	private JScrollPane scrollPane;
	private JTable featureTable;
	private JPanel featurePlusMinusPanel;
	private JButton addFeatureMiniBtn;
	private JButton dropFeatureMiniBtn;
	private JPanel filterPlusMinusPanel;
	private JButton addFilterMiniBtn;
	private JButton dropFilteMiniBtn;
	private JCheckBox selectAllFeaturesCkBx;
	private JCheckBox selectAllFiltersCkBx;
	private JSeparator homeSeparator2;
	private JScrollPane scrollPane_1;
	private JTable filterTable;
	private JPanel sessionDataPanel;
	private JPanel sessionBtnHomePanel;
	private JLabel sessionSectionLbl;
	private JPanel rightDataHeaderPanel;
	private JButton btnRemoveResultTable;
	private JLabel lblNewLabel_1;
	private JTabbedPane resultsTabbedPane;
	private JSeparator settingsSeparator1;
	private JPanel normalizationSettingsPanel;
	private JLabel lblUtilitySettings;
	private JTextField txtFldNormValue;
	private JLabel lblNormValue;
	private JSeparator settingsSeparator2;
	private JPanel parserSettingsPanel;
	private JLabel lblParserSettings;
	private JLabel lblGenomeDefault;
	private JLabel lblTrackDefault;
	private JComboBox<String> cBoxGenomeParsers;
	private JComboBox<String> cBoxTrackParsers;
	private JSeparator separator_1;
	private JLabel lblNewLabel_2;
	private JPanel panel;
	private JPanel actionsHomePanel;
	private JPanel actionsBtnHomePanel;
	private JLabel actionSectionLabel;
	private JIconButton normalizeRibbonBtn;
	private JIconButton searchRibbonBtn;
	private JIconButton summaryRibbonBtn;
	private JIconButton compareRibbonBtn;
	private JIconButton assignRibbonBtn;
	private JPanel untilityDataPanel;
	private JLabel untilityDataSectionLabel;
	private JPanel utilityDataBtnPanel;
	private JIconButton removeTracksRibbonBtn;
	private JIconButton removeFiltersRibbonBtn;
	private JIconButton removeResultsRibbonBtn;
	private JPanel rightDataHeaderEastPanel;
	private JButton btnResultToPeak;
	private JLabel lblResultTabBtnSpacer;
	private JLabel lblOverlapFactor;
	private JTextField txtFldOverlapFactor;

	/**
	 * Create the application.
	 */
	public PapstViewFrame(CommandLog log) {
		this.log = log;
		this.resultTables = new Vector<JTable>(); 
		initialize();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		this.setTitle("Peak Assignment and Profile Search Tool");
		this.setBounds(100, 100, 1000, 754);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		
		statusPanel = new JPanel();
		statusPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.getContentPane().add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setLayout(new BorderLayout(0, 0));
		
		currentStatusLabel = new JLabel("Welcome to PAPST");
		statusPanel.add(currentStatusLabel, BorderLayout.WEST);
		
		centerStatusLabel = new JLabel("Base Track: None");
		centerStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusPanel.add(centerStatusLabel, BorderLayout.CENTER);
		
		rightStatusLabel = new JLabel("Features Loaded: 0");
		statusPanel.add(rightStatusLabel, BorderLayout.EAST);
		
		centerPanel = new JPanel();
		this.getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		
		splitPane = new JSplitPane();
		centerPanel.add(splitPane, BorderLayout.CENTER);
		splitPane.setOneTouchExpandable(true);
		
		leftDataPanel = new JPanel();
		rightDataPanel = new JPanel();
		
		splitPane.setLeftComponent(leftDataPanel);
		leftDataPanel.setLayout(new BorderLayout(0, 0));
		
		leftDataSplitPane = new JSplitPane();
		leftDataSplitPane.setOneTouchExpandable(true);
		leftDataSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		leftDataPanel.add(leftDataSplitPane, BorderLayout.CENTER);
		
		featureLeftDataPanel = new JPanel();
		featureLeftDataPanel.setPreferredSize(new Dimension(400, 200));
		leftDataSplitPane.setTopComponent(featureLeftDataPanel);
		featureLeftDataPanel.setLayout(new BorderLayout(0, 0));
		
		featureHeaderPanel = new JPanel();
		featureLeftDataPanel.add(featureHeaderPanel, BorderLayout.NORTH);
		featureHeaderPanel.setLayout(new BorderLayout(0, 0));
		
		featureHeaderLbl = new JLabel(" Loaded Peaks and Features");
		featureHeaderPanel.add(featureHeaderLbl, BorderLayout.WEST);
		
		featurePlusMinusPanel = new JPanel();
		featureHeaderPanel.add(featurePlusMinusPanel, BorderLayout.EAST);
		featurePlusMinusPanel.setLayout(new BorderLayout(0, 0));
		
		addFeatureMiniBtn = new JButton("");
		addFeatureMiniBtn.setToolTipText("Add a new peak set");
		addFeatureMiniBtn.setIcon(new ImageIcon(getClass().getResource("/images/plus.PNG")));
		addFeatureMiniBtn.setMargin(new Insets(0, 0, 0, 0));
		featurePlusMinusPanel.add(addFeatureMiniBtn, BorderLayout.WEST);
		
		dropFeatureMiniBtn = new JButton("");
		dropFeatureMiniBtn.setToolTipText("Remove the selected peak sets from the program");
		dropFeatureMiniBtn.setIcon(new ImageIcon(getClass().getResource("/images/minus.PNG")));
		dropFeatureMiniBtn.setMargin(new Insets(0, 0, 0, 0));
		featurePlusMinusPanel.add(dropFeatureMiniBtn, BorderLayout.CENTER);
		
		selectAllFeaturesCkBx = new JCheckBox("");
		selectAllFeaturesCkBx.setToolTipText("Enable or disable all peak sets");
		featurePlusMinusPanel.add(selectAllFeaturesCkBx, BorderLayout.EAST);
		
		featureTable = new JTable();
		featureTable.setAutoCreateRowSorter(true);
		
		scrollPane = new JScrollPane(featureTable);
		featureLeftDataPanel.add(scrollPane, BorderLayout.CENTER);
		
		filterLeftDataPanel = new JPanel();
		leftDataSplitPane.setBottomComponent(filterLeftDataPanel);
		filterLeftDataPanel.setLayout(new BorderLayout(0, 0));
		filterLeftDataPanel.setPreferredSize(new Dimension(315, 50));
		
		filterHeaderPanel = new JPanel();
		filterLeftDataPanel.add(filterHeaderPanel, BorderLayout.NORTH);
		filterHeaderPanel.setLayout(new BorderLayout(0, 0));
		
		filterHeaderLbl = new JLabel(" Filters");
		filterHeaderPanel.add(filterHeaderLbl, BorderLayout.WEST);
		
		filterPlusMinusPanel = new JPanel();
		filterHeaderPanel.add(filterPlusMinusPanel, BorderLayout.EAST);
		filterPlusMinusPanel.setLayout(new BorderLayout(0, 0));
		
		addFilterMiniBtn = new JButton("");
		addFilterMiniBtn.setToolTipText("Add a new filter");
		addFilterMiniBtn.setIcon(new ImageIcon(getClass().getResource("/images/plus.PNG")));
		addFilterMiniBtn.setMargin(new Insets(0, 0, 0, 0));
		filterPlusMinusPanel.add(addFilterMiniBtn, BorderLayout.WEST);
		
		dropFilteMiniBtn = new JButton("");
		dropFilteMiniBtn.setToolTipText("Remove the selected filters from the program");
		dropFilteMiniBtn.setIcon(new ImageIcon(getClass().getResource("/images/minus.PNG")));
		dropFilteMiniBtn.setMargin(new Insets(0, 0, 0, 0));
		filterPlusMinusPanel.add(dropFilteMiniBtn, BorderLayout.CENTER);
		
		selectAllFiltersCkBx = new JCheckBox("");
		selectAllFiltersCkBx.setToolTipText("Enable or disable all filters");
		filterPlusMinusPanel.add(selectAllFiltersCkBx, BorderLayout.EAST);
		
		scrollPane_1 = new JScrollPane();
		filterLeftDataPanel.add(scrollPane_1, BorderLayout.CENTER);
		
		filterTable = new JTable();
		scrollPane_1.setViewportView(filterTable);
		
		
		splitPane.setRightComponent(rightDataPanel);
		rightDataPanel.setLayout(new BorderLayout(0, 0));
		
		rightDataHeaderPanel = new JPanel();
		rightDataPanel.add(rightDataHeaderPanel, BorderLayout.NORTH);
		rightDataHeaderPanel.setLayout(new BorderLayout(0, 0));
		
		lblNewLabel_1 = new JLabel(" Results Window");
		rightDataHeaderPanel.add(lblNewLabel_1, BorderLayout.WEST);
		
		rightDataHeaderEastPanel = new JPanel();
		rightDataHeaderPanel.add(rightDataHeaderEastPanel, BorderLayout.EAST);
		rightDataHeaderEastPanel.setLayout(new BorderLayout(0, 0));
		//TODO
		btnRemoveResultTable = new JButton("");
		rightDataHeaderEastPanel.add(btnRemoveResultTable, BorderLayout.EAST);
		btnRemoveResultTable.setToolTipText("Remove the current results table from the program");
		btnRemoveResultTable.setIcon(new ImageIcon(getClass().getResource("/images/minus.PNG")));
		btnRemoveResultTable.setMargin(new Insets(0, 0, 0, 0));
		
		btnResultToPeak = new JButton("");
		btnResultToPeak.setEnabled(false);
		btnResultToPeak.setToolTipText("Create a new peak track with the current results.");
		rightDataHeaderEastPanel.add(btnResultToPeak, BorderLayout.WEST);
		btnResultToPeak.setIcon(new ImageIcon(getClass().getResource("/images/resultToPeak16.png")));
		btnResultToPeak.setMargin(new Insets(0, 0, 0, 0));
		
		lblResultTabBtnSpacer = new JLabel("  ");
		rightDataHeaderEastPanel.add(lblResultTabBtnSpacer, BorderLayout.CENTER);
		
		resultsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		rightDataPanel.add(resultsTabbedPane, BorderLayout.CENTER);
		

		//TODO ribbon pane
		ribbonTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.getContentPane().add(ribbonTabbedPane, BorderLayout.NORTH);
		
		// home tab
		homePanel = new JPanel();
		ribbonTabbedPane.addTab("Home", (Icon) null, homePanel, "The Home Tab: All of the most commonly used PAPST features");
		homePanel.setLayout(new BorderLayout(0, 0));
		
		homeButtonPanel = new JPanel();
		homePanel.add(homeButtonPanel, BorderLayout.WEST);
		
		homeSeparator1 = new JSeparator();
		homeSeparator1.setForeground(SystemColor.controlShadow);
		homeSeparator1.setOrientation(SwingConstants.VERTICAL);
		Dimension d = homeSeparator1.getPreferredSize();
		d.height = homeButtonPanel.getPreferredSize().height;
		
		baseTrackHomePanel = new JPanel();
		homeButtonPanel.add(baseTrackHomePanel);
		baseTrackHomePanel.setLayout(new BorderLayout(0, 0));
		
		baseTrackBtnHomePanel = new JPanel();
		baseTrackHomePanel.add(baseTrackBtnHomePanel, BorderLayout.CENTER);
		baseTrackBtnHomePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		loadGenomePanel = new JPanel();
		baseTrackBtnHomePanel.add(loadGenomePanel);
		loadGenomePanel.setLayout(new BorderLayout(0, 0));
		
		btnLoadRefseq = new JIconButton("Load RefSeq\nGenome",getClass().getResource("/images/dna64.png"));
		btnLoadRefseq.setToolTipText("Load a gene annotations from a RefGene file");
		loadGenomePanel.add(btnLoadRefseq);
		btnLoadRefseq.setPreferredSize(new Dimension(80,100));
		btnLoadRefseq.setMargin(new Insets(2,0,3,0));
		
		genomeBaseTglBtn = new JToggleButton("Genome Base");
		genomeBaseTglBtn.setToolTipText("Switch the current base to RefGene");
		genomeBaseTglBtn.setMargin(new Insets(0,1,0,1));
		loadGenomePanel.add(genomeBaseTglBtn, BorderLayout.SOUTH);
		
		loadPeakBasePanel = new JPanel();
		baseTrackBtnHomePanel.add(loadPeakBasePanel);
		loadPeakBasePanel.setLayout(new BorderLayout(0, 0));
		
		btnLoadPeakBase = new JIconButton("Load Track as\n Base Track",getClass().getResource("/images/graph64.png"));
		btnLoadPeakBase.setToolTipText("Load a peak regions as the base track (instead of genes)");
		loadPeakBasePanel.add(btnLoadPeakBase, BorderLayout.CENTER);
		btnLoadPeakBase.setPreferredSize(new Dimension(85,100));
		btnLoadPeakBase.setMargin(new Insets(0,0,0,0));
		
		peakAsBaseTglBtn = new JToggleButton("Track as Base");
		peakAsBaseTglBtn.setToolTipText("Switch the current base to a peak set");
		peakAsBaseTglBtn.setMargin(new Insets(0,0,0,0));
		loadPeakBasePanel.add(peakAsBaseTglBtn, BorderLayout.SOUTH);
		
		panel = new JPanel();
		baseTrackHomePanel.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		homeSeparator1.setPreferredSize(new Dimension(2, 125));
		homeButtonPanel.add(homeSeparator1);
		
		featureTrackHomePanel = new JPanel();
		homeButtonPanel.add(featureTrackHomePanel);
		featureTrackHomePanel.setLayout(new BorderLayout(0, 0));
		
		featureTrackBtnHomePanel = new JPanel();
		featureTrackHomePanel.add(featureTrackBtnHomePanel);
		featureTrackBtnHomePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnLoadPeaks = new JIconButton("Load Track\nFile",getClass().getResource("/images/graph64.png"));
		btnLoadPeaks.setToolTipText("Load a single set of peak regions");
		featureTrackBtnHomePanel.add(btnLoadPeaks);
		btnLoadPeaks.setPreferredSize(new Dimension(85,100));
		btnLoadPeaks.setMargin(new Insets(0,0,0,0));
		
		btnLoadPeaksFolder = new JIconButton("Load Track\nFolder",getClass().getResource("/images/folderGraph64.png"));
		btnLoadPeaksFolder.setToolTipText("Load all sets of peak regions in a folder at once");
		featureTrackBtnHomePanel.add(btnLoadPeaksFolder);
		btnLoadPeaksFolder.setPreferredSize(new Dimension(85,100));
		btnLoadPeaksFolder.setMargin(new Insets(8,0,5,0));
		
		featureTrackSectionLabel = new JLabel("Feature Tracks");
		featureTrackSectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		featureTrackHomePanel.add(featureTrackSectionLabel, BorderLayout.SOUTH);
		
		homeSeparator2 = new JSeparator();
		homeButtonPanel.add(homeSeparator2);
		homeSeparator2.setPreferredSize(new Dimension(2, 125));
		homeSeparator2.setOrientation(SwingConstants.VERTICAL);
		homeSeparator2.setForeground(SystemColor.controlShadow);
		
		//TODO actions panel
		actionsHomePanel = new JPanel();
		homeButtonPanel.add(actionsHomePanel);
		actionsHomePanel.setLayout(new BorderLayout(0, 0));
		
		actionsBtnHomePanel = new JPanel();
		actionsHomePanel.add(actionsBtnHomePanel, BorderLayout.CENTER);
		
		normalizeRibbonBtn = new JIconButton("Normalize",getClass().getResource("/images/scales64.png"));
		normalizeRibbonBtn.setToolTipText("Open the normalization dialog");
		actionsBtnHomePanel.add(normalizeRibbonBtn);
		normalizeRibbonBtn.setPreferredSize(new Dimension(85,100));
		normalizeRibbonBtn.setMargin(new Insets(5,0,2,0));
		
		searchRibbonBtn  = new JIconButton("Search", getClass().getResource("/images/search64.png"));
		searchRibbonBtn.setToolTipText("Search for genes matching the filter pattern");
		actionsBtnHomePanel.add(searchRibbonBtn);
		searchRibbonBtn.setPreferredSize(new Dimension(85,100));
		searchRibbonBtn.setMargin(new Insets(5,0,2,0));
		
		summaryRibbonBtn = new JIconButton("Summarize", getClass().getResource("/images/piechart64.png"));
		summaryRibbonBtn.setToolTipText("Summarize the genomic distribution of peak regions");
		actionsBtnHomePanel.add(summaryRibbonBtn);
		summaryRibbonBtn.setPreferredSize(new Dimension(85,100));
		summaryRibbonBtn.setMargin(new Insets(5,0,2,0));
		
		compareRibbonBtn = new JIconButton("Compare", getClass().getResource("/images/grid64.png"));
		compareRibbonBtn.setToolTipText("Compare the overlap between sets of peaks");
		actionsBtnHomePanel.add(compareRibbonBtn);
		compareRibbonBtn.setPreferredSize(new Dimension(85,100));
		compareRibbonBtn.setMargin(new Insets(5,0,2,0));
		
		assignRibbonBtn = new JIconButton("Assign", getClass().getResource("/images/assign64.png"));
		assignRibbonBtn.setToolTipText("Assign each peak to its nearest gene");
		actionsBtnHomePanel.add(assignRibbonBtn);
		assignRibbonBtn.setPreferredSize(new Dimension(85,100));
		assignRibbonBtn.setMargin(new Insets(5,0,2,0));
		
		actionSectionLabel = new JLabel("Actions");
		actionSectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		actionsHomePanel.add(actionSectionLabel, BorderLayout.SOUTH);
		
		
		
		//TODO data tab
		dataPanel = new JPanel();
		ribbonTabbedPane.addTab("Data", null, dataPanel, "The Data Tab: Save, load, and export PAPST data");
		dataPanel.setLayout(new BorderLayout(0, 0));
		
		dataButtonPanel = new JPanel();
		dataPanel.add(dataButtonPanel, BorderLayout.WEST);
		dataButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		sessionDataPanel = new JPanel();
		dataButtonPanel.add(sessionDataPanel);
		sessionDataPanel.setLayout(new BorderLayout(0, 0));
		
		sessionBtnHomePanel = new JPanel();
		sessionDataPanel.add(sessionBtnHomePanel);
		
		btnSaveSession = new JIconButton("Save\nSession",getClass().getResource("/images/sessionSave64.png"));
		btnSaveSession.setToolTipText("Save the current PAPST session for later use");
		sessionBtnHomePanel.add(btnSaveSession);
		btnSaveSession.setPreferredSize(new Dimension(85,100));
		btnSaveSession.setMargin(new Insets(0,0,0,0));
		
		btnLoadSession = new JIconButton("Load\nSession",getClass().getResource("/images/sessionLoad64.png"));
		btnLoadSession.setToolTipText("Load a previous PAPST session");
		sessionBtnHomePanel.add(btnLoadSession);
		btnLoadSession.setPreferredSize(new Dimension(85,100));
		btnLoadSession.setMargin(new Insets(0,0,0,0));
		
		sessionSectionLbl = new JLabel("Sessions");
		sessionSectionLbl.setHorizontalAlignment(SwingConstants.CENTER);
		sessionDataPanel.add(sessionSectionLbl, BorderLayout.SOUTH);
		
		dataSeparator1 = new JSeparator();
		dataSeparator1.setPreferredSize(new Dimension(2, 125));
		dataSeparator1.setOrientation(SwingConstants.VERTICAL);
		dataSeparator1.setForeground(SystemColor.controlShadow);
		dataButtonPanel.add(dataSeparator1);
		
		exportDataPanel = new JPanel();
		dataButtonPanel.add(exportDataPanel);
		exportDataPanel.setLayout(new BorderLayout(0, 0));
		
		exportDataBtnPanel = new JPanel();
		exportDataPanel.add(exportDataBtnPanel, BorderLayout.NORTH);
		
		btnExportTable = new JIconButton("Export\nTable",getClass().getResource("/images/csv64.png"));
		btnExportTable.setToolTipText("Export the current result table to a spreadsheet file");
		exportDataBtnPanel.add(btnExportTable);
		btnExportTable.setPreferredSize(new Dimension(85,100));
		btnExportTable.setMargin(new Insets(0,0,0,0));
		
		exportDataSectionLabel = new JLabel("Export Data");
		exportDataSectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		exportDataPanel.add(exportDataSectionLabel, BorderLayout.SOUTH);
		
		dataSeparator2 = new JSeparator();
		dataSeparator2.setPreferredSize(new Dimension(2, 125));
		dataSeparator2.setOrientation(SwingConstants.VERTICAL);
		dataSeparator2.setForeground(SystemColor.controlShadow);
		dataButtonPanel.add(dataSeparator2);
		
		//TODO add utility buttons
		untilityDataPanel = new JPanel();
		dataButtonPanel.add(untilityDataPanel);
		untilityDataPanel.setLayout(new BorderLayout(0, 0));
		
		untilityDataSectionLabel = new JLabel("Utility");
		untilityDataSectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		untilityDataPanel.add(untilityDataSectionLabel, BorderLayout.SOUTH);
		
		utilityDataBtnPanel = new JPanel();
		untilityDataPanel.add(utilityDataBtnPanel, BorderLayout.CENTER);
		
		//private JIconButton removeTracksRibbonBtn;
		//private JIconButton removeFiltersRibbonBtn;
		//private JIconButton removeresultsRibbonBtn;
		removeTracksRibbonBtn = new JIconButton("Remove All\nTracks",getClass().getResource("/images/removeTracks64.png"));
		removeTracksRibbonBtn.setToolTipText("Remove all peak sets from the program");
		utilityDataBtnPanel.add(removeTracksRibbonBtn);
		removeTracksRibbonBtn.setPreferredSize(new Dimension(85,100));
		removeTracksRibbonBtn.setMargin(new Insets(0,0,0,0));
		
		removeFiltersRibbonBtn = new JIconButton("Remove All\nFilters",getClass().getResource("/images/removeFilters64.png"));
		removeFiltersRibbonBtn.setToolTipText("Remove all filters from the program");
		utilityDataBtnPanel.add(removeFiltersRibbonBtn);
		removeFiltersRibbonBtn.setPreferredSize(new Dimension(85,100));
		removeFiltersRibbonBtn.setMargin(new Insets(0,0,0,0));
		
		removeResultsRibbonBtn = new JIconButton("Remove All\nResults",getClass().getResource("/images/removeResults64.png"));
		removeResultsRibbonBtn.setToolTipText("Remove all tables from the Results Window");
		utilityDataBtnPanel.add(removeResultsRibbonBtn);
		removeResultsRibbonBtn.setPreferredSize(new Dimension(85,100));
		removeResultsRibbonBtn.setMargin(new Insets(0,0,0,0));
		
		
		
		//TODO settings tab
		settingsPanel = new JPanel();
		ribbonTabbedPane.addTab("Settings", null, settingsPanel, "The Settings Tab: Personalize your settings for PAPST");
		settingsPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel leftAlignSettingsPanel = new JPanel();
		settingsPanel.add(leftAlignSettingsPanel, BorderLayout.WEST);
		
		parserSettingsPanel = new JPanel();
		parserSettingsPanel.setPreferredSize(new Dimension(220, 120));
		leftAlignSettingsPanel.add(parserSettingsPanel);
		parserSettingsPanel.setLayout(null);
		
		lblParserSettings = new JLabel("Parser Settings");
		lblParserSettings.setBounds(10, 106, 200, 14);
		lblParserSettings.setHorizontalAlignment(SwingConstants.CENTER);
		parserSettingsPanel.add(lblParserSettings);
		
		lblGenomeDefault = new JLabel("Default Genome Parser");
		lblGenomeDefault.setHorizontalAlignment(SwingConstants.CENTER);
		lblGenomeDefault.setBounds(10, 0, 200, 14);
		parserSettingsPanel.add(lblGenomeDefault);
		
		cBoxGenomeParsers = new JComboBox<String>();
		cBoxGenomeParsers.setToolTipText("Select the default genome parser");
		cBoxGenomeParsers.setBounds(10, 22, 200, 20);
		parserSettingsPanel.add(cBoxGenomeParsers);
		
		lblTrackDefault = new JLabel("Default Track Parser");
		lblTrackDefault.setHorizontalAlignment(SwingConstants.CENTER);
		lblTrackDefault.setBounds(10, 52, 200, 14);
		parserSettingsPanel.add(lblTrackDefault);
		
		cBoxTrackParsers = new JComboBox<String>();
		cBoxTrackParsers.setToolTipText("Select the default peak track parser");
		cBoxTrackParsers.setBounds(10, 73, 200, 20);
		parserSettingsPanel.add(cBoxTrackParsers);
		
		separator_1 = new JSeparator();
		separator_1.setPreferredSize(new Dimension(2, 125));
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setForeground(SystemColor.controlShadow);
		leftAlignSettingsPanel.add(separator_1);
		
		searchSettingsPanel = new JPanel();
		leftAlignSettingsPanel.add(searchSettingsPanel);
		searchSettingsPanel.setLayout(null);
		searchSettingsPanel.setPreferredSize(new Dimension(230, 120));
		
		//TODO settings controls
		chckbxShowIsoforms = new JCheckBox("Show Gene Isoforms?");
		chckbxShowIsoforms.setToolTipText("Toggle whether to show or hide gene isoforms");
		chckbxShowIsoforms.setSelected(true);
		chckbxShowIsoforms.setBounds(34, 7, 168, 23);
		searchSettingsPanel.add(chckbxShowIsoforms);
		
		txtFldUpstreamSetting = new JFormattedTextField(new DecimalFormat("0"));
		txtFldUpstreamSetting.setToolTipText("Set the default upstream promoter distance");
		txtFldUpstreamSetting.setHorizontalAlignment(SwingConstants.RIGHT);
		txtFldUpstreamSetting.setText("2000");
		txtFldUpstreamSetting.setBounds(0, 58, 86, 20);
		searchSettingsPanel.add(txtFldUpstreamSetting);
		txtFldUpstreamSetting.setColumns(10);
		
		lblUpstreamOfTss = new JLabel("Upstream of TSS");
		lblUpstreamOfTss.setHorizontalAlignment(SwingConstants.LEFT);
		lblUpstreamOfTss.setBounds(96, 58, 134, 20);
		searchSettingsPanel.add(lblUpstreamOfTss);
		
		txtFldDownstreamSetting = new JFormattedTextField(new DecimalFormat("0"));
		txtFldDownstreamSetting.setToolTipText("Set the default downstream promoter distance");
		txtFldDownstreamSetting.setHorizontalAlignment(SwingConstants.RIGHT);
		txtFldDownstreamSetting.setText("200");
		txtFldDownstreamSetting.setBounds(0, 79, 86, 20);
		searchSettingsPanel.add(txtFldDownstreamSetting);
		txtFldDownstreamSetting.setColumns(10);
		
		lblDownstreamOfTss = new JLabel("Downstream of TSS");
		lblDownstreamOfTss.setHorizontalAlignment(SwingConstants.LEFT);
		lblDownstreamOfTss.setBounds(96, 79, 134, 20);
		searchSettingsPanel.add(lblDownstreamOfTss);
		
		JLabel lblSearchSettings = new JLabel("Search Settings");
		lblSearchSettings.setHorizontalAlignment(SwingConstants.CENTER);
		lblSearchSettings.setBounds(56, 106, 99, 14);
		searchSettingsPanel.add(lblSearchSettings);
		
		lblNewLabel_2 = new JLabel("Default Promoter ");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(6, 37, 204, 14);
		searchSettingsPanel.add(lblNewLabel_2);
		
		settingsSeparator1 = new JSeparator();
		settingsSeparator1.setPreferredSize(new Dimension(2, 125));
		settingsSeparator1.setOrientation(SwingConstants.VERTICAL);
		settingsSeparator1.setForeground(SystemColor.controlShadow);
		leftAlignSettingsPanel.add(settingsSeparator1);
		
		normalizationSettingsPanel = new JPanel();
		normalizationSettingsPanel.setPreferredSize(new Dimension(220, 120));
		leftAlignSettingsPanel.add(normalizationSettingsPanel);
		normalizationSettingsPanel.setLayout(null);
		
		lblUtilitySettings = new JLabel("Utility Settings");
		lblUtilitySettings.setBounds(10, 106, 199, 14);
		lblUtilitySettings.setHorizontalAlignment(SwingConstants.CENTER);
		normalizationSettingsPanel.add(lblUtilitySettings);
		
		txtFldNormValue = new JFormattedTextField(new DecimalFormat("0"));
		txtFldNormValue.setToolTipText("Set the normalization factor");
		txtFldNormValue.setBounds(10, 28, 199, 20);
		normalizationSettingsPanel.add(txtFldNormValue);
		txtFldNormValue.setHorizontalAlignment(SwingConstants.RIGHT);
		txtFldNormValue.setText("1000000");
		txtFldNormValue.setColumns(10);
		
		lblNormValue = new JLabel("Normalization Factor");
		lblNormValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblNormValue.setBounds(10, 11, 199, 14);
		normalizationSettingsPanel.add(lblNormValue);
		
		lblOverlapFactor = new JLabel("Base Pair Overlap Factor");
		lblOverlapFactor.setHorizontalAlignment(SwingConstants.CENTER);
		lblOverlapFactor.setBounds(10, 59, 199, 14);
		normalizationSettingsPanel.add(lblOverlapFactor);
		
		txtFldOverlapFactor = new JFormattedTextField(new DecimalFormat("0"));
		txtFldOverlapFactor.setHorizontalAlignment(SwingConstants.RIGHT);
		txtFldOverlapFactor.setText("1");
		txtFldOverlapFactor.setBounds(10, 75, 199, 20);
		normalizationSettingsPanel.add(txtFldOverlapFactor);
		txtFldOverlapFactor.setColumns(10);
		
		settingsSeparator2 = new JSeparator();
		settingsSeparator2.setPreferredSize(new Dimension(2, 125));
		settingsSeparator2.setOrientation(SwingConstants.VERTICAL);
		settingsSeparator2.setForeground(SystemColor.controlShadow);
		leftAlignSettingsPanel.add(settingsSeparator2);
		

		
		
	}
	
	/**
	 * 
	 * TODO Utility methods
	 * 
	 */
	
	public void print(String msg){
		System.out.println("View: "+msg);
		log.addCommand("View: "+msg);
	}
	

	
	public void setFeatureStatusLbl(String msg){
		rightStatusLabel.setText(msg);
	}
	
	public void setBaseTrackStatusLbl(String msg){
		centerStatusLabel.setText(msg);
	}
	
	public void setCurrentStatusLbl(String msg){
		currentStatusLabel.setText(msg);
	}
	
	public void setGenomeAsBase(){
		genomeBaseTglBtn.setSelected(true);
		peakAsBaseTglBtn.setSelected(false);
	}
	
	public void setPeakAsBase(){
		genomeBaseTglBtn.setSelected(false);
		peakAsBaseTglBtn.setSelected(true);
	}
	
	
	/**
	 * 
	 * TODO Add listener methods
	 * 
	 */
	
	public void add_btnLoadRefseqActionListener(ActionListener listener){	
		btnLoadRefseq.addActionListener(listener);
	}
	
	public void add_btnLoadPeakBaseActionListener(ActionListener listener){
		btnLoadPeakBase.addActionListener(listener);
	}
	
	public void add_genomeBaseTglBtnActionListener(ActionListener listener){
		genomeBaseTglBtn.addActionListener(listener);
	}
	
	public void add_peakAsBaseTglBtnActionListener(ActionListener listener){
		peakAsBaseTglBtn.addActionListener(listener);
	}
	
	public void add_btnLoadPeaksActionListener(ActionListener listener){
		btnLoadPeaks.addActionListener(listener);
	}
	
	public void add_btnLoadPeaksFolderActionListener(ActionListener listener){
		btnLoadPeaksFolder.addActionListener(listener);
	}
	
	public void add_addFeatureMiniBtnActionListener(ActionListener listener){
		addFeatureMiniBtn.addActionListener(listener);
	}
	
	public void add_addFilterMiniBtnActionListener(ActionListener listener){
		addFilterMiniBtn.addActionListener(listener);
	}
	
	public void add_filterTableMouseListener(MouseListener listener){
		filterTable.addMouseListener(listener);
	}
	
	public void add_featureTableMouseListener(MouseListener listener){
		featureTable.addMouseListener(listener);
	}
	
	public void add_dropFeatureMiniBtnActionListener(ActionListener listener){
		dropFeatureMiniBtn.addActionListener(listener);
	}
	
	public void add_dropFilterMiniBtnActionListener(ActionListener listener){
		dropFilteMiniBtn.addActionListener(listener);
	}
	
	public void add_selectAllFeaturesCkBxItemListener(ItemListener listener){
		selectAllFeaturesCkBx.addItemListener(listener);
	}
	
	public void add_selectAllFiltersCkBxItemListener(ItemListener listener){
		selectAllFiltersCkBx.addItemListener(listener);
	}
	
	public void add_btnSaveSessionActionListener(ActionListener listener){
		btnSaveSession.addActionListener(listener);
	}
	
	public void add_btnLoadSessionActionListener(ActionListener listener){
		btnLoadSession.addActionListener(listener);
	}
	
	public void add_renameTabListener(TabTitleEditListener listener){
		resultsTabbedPane.addChangeListener(listener);
		resultsTabbedPane.addMouseListener(listener);
	}
	
	public void add_removeResultsRibbonBtnListener(ActionListener listener){
		removeResultsRibbonBtn.addActionListener(listener);
	}
	
	public void add_removeFiltersRibbonBtnListener(ActionListener listener){
		removeFiltersRibbonBtn.addActionListener(listener);
	}
	
	public void add_removeTracksRibbonBtnListener(ActionListener listener){
		removeTracksRibbonBtn.addActionListener(listener);
	}
	
	
	/**
	 * 
	 * //TODO Action buttons
	 * 
	 */
	
	public void add_btnSearchActionListener(ActionListener listener){
		searchRibbonBtn.addActionListener(listener);
	}
	
	public void add_btnAssignActionListener(ActionListener listener){
		assignRibbonBtn.addActionListener(listener);
	}
	
	public void add_btnSummarizeActionListener(ActionListener listener){
		summaryRibbonBtn.addActionListener(listener);
	}
	
	
	public void add_btnCompareActionListener(ActionListener listener){
		compareRibbonBtn.addActionListener(listener);
	}
	
	public void add_btnNormalizeActionListener(ActionListener listener){
		normalizeRibbonBtn.addActionListener(listener);
	}
	
	public void addResultTableTab(AbstractResultTableModel tableModel){
		
		//get the panel
		JPanel resultPanel = getResultTablePane(tableModel,tableModel.getResultNote());
		
		resultsTabbedPane.addTab(tableModel.getTabTitle(), resultPanel);
		resultsTabbedPane.setSelectedIndex(resultsTabbedPane.getTabCount()-1);
	}
	
	private JPanel getResultTablePane(TableModel tableModel, String note){
		
		JPanel holdPanel = new JPanel(new BorderLayout());
		JPanel labelPanel = new JPanel(new BorderLayout());
		JLabel activeFilterLbl = new JLabel();
		
		activeFilterLbl.setText(note);
		labelPanel.add(activeFilterLbl, BorderLayout.WEST);
		holdPanel.add(labelPanel,BorderLayout.NORTH);
		
		JScrollPane scrollPane = new JScrollPane();
		JTable resultTable = new JTable(tableModel);
		
		resultTable.setCellSelectionEnabled(true);
		resultTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		//add number aware sort
		resultTable.setRowSorter(new NumberAwareStringRowSorter(tableModel));
		
		DecimalFormatRenderer rightRenderer = new DecimalFormatRenderer(); 
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		rightRenderer.setBackground(Color.white);
		
		//for different table types
		int start = 0;
		if(tableModel instanceof SummaryResultTableModel){
			start = 1;
		}else if(tableModel instanceof SearchResultTableModel){
			start = 2;
		}else if(tableModel instanceof GeneAssignResultTableModel){
			start = 5;
		}
		
		for(int i = start; i < tableModel.getColumnCount(); i++){
			resultTable.getColumnModel().getColumn(i).setCellRenderer( rightRenderer );
		}
		
		resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		scrollPane.setViewportView(resultTable);
		
		holdPanel.add(scrollPane,BorderLayout.CENTER);
		
		//!!! Important !!!!!!
		//add to results table
		resultTables.add(resultTable);
		
		return holdPanel;
	}
	
	public void add_btnRemoveResultTableActionListener(ActionListener listener){
		btnRemoveResultTable.addActionListener(listener);
	}
	
	public JTable getFeatureTable(){
		return featureTable;
	}
	
	public JTable getFilterTable(){
		return filterTable;
	}
	
	public void setFeatureTableModel(TableModel model){
		featureTable.setModel(model);
	}
	
	public void setFilterTableModel(TableModel model){
		filterTable.setModel(model);
	}
	
	public JTabbedPane getResultsTabbedPane(){
		return resultsTabbedPane;
	}
	
	public int getSelectedResultTab(){
		return resultsTabbedPane.getSelectedIndex();
	}
	
	public void dropResultTab(int index){
		resultTables.remove(index);
		resultsTabbedPane.remove(index);
	}
	
	public String getSelectedTabName(int index){
		return resultsTabbedPane.getTitleAt(index);
	}
	
	
	public void add_btnExportTableActionListener(ActionListener listener){
		btnExportTable.addActionListener(listener);
	}
	
	public JTable getResultTable(int index){
		return resultTables.elementAt(index);
	}
	
	
	/**
	 * TODO Settings Tab: getters setters and listeners
	 */
	
	public void setGenomeParserCBox(ComboBoxModel<String> model){
		cBoxGenomeParsers.setModel(model);
	}
	
	public String getDefaultGenomeParser(){
		return (String)cBoxGenomeParsers.getSelectedItem();
	}
	
	public void add_cBoxGenomeParsersActionListener(ActionListener listener){
		cBoxGenomeParsers.addActionListener(listener);
	}
	
	public void setSelectedDefaultGenoemParser(String selection){
		cBoxGenomeParsers.setSelectedItem(selection);
	}
	
	public void setTrackParserCBox(ComboBoxModel<String> model){
		cBoxTrackParsers.setModel(model);
	}
	
	public String getDefaultTrackParser(){
		return (String)cBoxTrackParsers.getSelectedItem();
	}
	
	public void add_cBoxTrackParsersActionListener(ActionListener listener){
		cBoxTrackParsers.addActionListener(listener);
	}
	
	public void setSelectedDefaultTrackParser(String selection){
		cBoxTrackParsers.setSelectedItem(selection);
	}
	
	
	//Default searches
	public void add_chckbxShowIsoformsActionListener(ActionListener listener){
		chckbxShowIsoforms.addActionListener(listener);
	}
	
	public boolean getShowIsoformsIsSelected(){
		return chckbxShowIsoforms.isSelected();
	}
	
	//upstream
	public String getUpstreamSetting(){
		return txtFldUpstreamSetting.getText();
	}
	
	public void setUpstreamSetting(String text){
		txtFldUpstreamSetting.setText(text);
	}
	
	public void add_txtFldUpstreamSettingFocusListener(FocusListener listener){
		txtFldUpstreamSetting.addFocusListener(listener);
	}
	
	
	//downstream
	public String getDownstreamSetting(){
		return txtFldDownstreamSetting.getText();
	}
	
	public void setDownstreamSetting(String text){
		txtFldDownstreamSetting.setText(text);
	}
	
	public void add_txtFldDownstreamSettingFocusListener(FocusListener listener){
		txtFldDownstreamSetting.addFocusListener(listener);
	}
	
	//Normalization
	public String getNormalizationValue(){
		return txtFldNormValue.getText();
	}
	
	public void setNormalizationValue(String text){
		txtFldNormValue.setText(text);
	}
	
	public void add_txtFldNormalizationSettingFocusListener(FocusListener listener){
		txtFldNormValue.addFocusListener(listener);
	}
	
	//Overlap
	public String getOverlapValue(){
		return txtFldOverlapFactor.getText();
	}
	
	public void setOverlapValue(String text){
		txtFldOverlapFactor.setText(text);
	}
	
	public void add_txtFldOverlapFactorFocusListener(FocusListener listener){
		txtFldOverlapFactor.addFocusListener(listener);
	}
	//-----
	
	public void add_resultTabbedPaneChangeListener(ChangeListener listener){
		resultsTabbedPane.addChangeListener(listener);
	}
	
	public void setEnabledResultToPeakBtn(boolean enableStatus){
		btnResultToPeak.setEnabled(enableStatus);
	}
	
	public void add_btnResultToPeakActionListener(ActionListener listener){
		btnResultToPeak.addActionListener(listener);
	}
	
	
	/**
	 * 
	 * Taken from http://helpdesk.objects.com.au/java/how-to-control-decimal-places-displayed-in-jtable-column
	 *
	 */
	public static class DecimalFormatRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private static final DecimalFormat formatter = new DecimalFormat( "#.###" );
		private static final DecimalFormat expFormatter = new DecimalFormat( "0.###E0" );
		
		public DecimalFormatRenderer(){
			super();
			formatter.setMinimumFractionDigits(3);
		}
 
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, 
				int row, int column)
		{

			if(value instanceof Double){
				
				
				if((Double)value == 0.0){
					value = 0.0;
				}else if((Double) value < 0.01){
					value = expFormatter.format((Number)value);
				}else{
					value = formatter.format((Number)value);
				}
			}

			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column );
		}
	}
}//end class, PapstViewFrame

