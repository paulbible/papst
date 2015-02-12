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

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

import papst.database.*;
import papst.dialogs.*;
import papst.filters.*;
import papst.models.*;
import papst.parsers.*;
import papst.regions.*;
import papst.utility.*;
import papst.workers.*;



public class PapstController {
	
	private static PapstViewFrame view;
	private static PapstModel model;
	private static String currentDirectory = ".";
	
	private static String startDirectory = currentDirectory;
	
	private static String defaultTrackParser = "simple_tab";
	private static String defaultGeneParser = "refseq_subset";
	
	private static FeatureTableModel featureTableModel;
	private static FilterTableModel filterTableModel;
	
	private static TabTitleEditListener tabEditListener;
	
	private static CommandLog log;
	
	private static PapstDialogs dialogs;
	
	

	/**
	 * Launch the application.
	 *-//TODO Main
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				log = new CommandLog(startDirectory,false);
				//create veiw and model
				view = new PapstViewFrame(log);
				model = new PapstModel(log);
				
				initInterface();
				initListeners();
				initTabEditListeners();
				
				view.setVisible(true);
			}
		});
	}
	
	/**
	 * //TODO Initialize interface
	 */
	private static void initInterface(){
		
		view.setBaseTrackStatusLbl("Base Track: "+model.getCurrentBaseTrack());
		view.setFeatureStatusLbl("Number of Feature Tracks: " + model.getNumFeatures()+" ");
		
		if(model.getBaseStatus() == PapstModel.REFGENE_BASE){
			view.setGenomeAsBase();
		}else{
			view.setPeakAsBase();
		}
		
		view.getResultsTabbedPane().removeAll();
		
		if(model.getNumResultTables() > 0){
			
			for(ResultData table: model.getResultTables()){
				view.addResultTableTab(table.getTableModel());
			}
		}
		
		dialogs = new PapstDialogs(view);
		
		featureTableModel = new FeatureTableModel(model);
		filterTableModel  = new FilterTableModel(model);
		
		//get the table models
		view.setFeatureTableModel(featureTableModel);
		view.setFilterTableModel(filterTableModel);
		
		
		//set to multiple selection.
		view.getFeatureTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		view.getFilterTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		
		//right align numbers
		DefaultTableCellRenderer rightAlignRenderer = new DefaultTableCellRenderer();
		rightAlignRenderer.setHorizontalAlignment(JLabel.RIGHT);
		rightAlignRenderer.setBackground(Color.white);
		view.getFeatureTable().getColumnModel().getColumn(1).setCellRenderer(rightAlignRenderer);
		view.getFeatureTable().getColumnModel().getColumn(2).setCellRenderer(rightAlignRenderer);
		
		//set cell renderer
		view.getFeatureTable().getColumnModel().getColumn(3).setCellRenderer(new FeatureModelCellRenderer());
		NumberAwareStringRowSorter customStringSorter = new NumberAwareStringRowSorter(featureTableModel);
		view.getFeatureTable().setRowSorter(customStringSorter);
		
		
		// update parsers
		updateGenomeParsers();
		updateTrackParsers();
		
		view.setSelectedDefaultGenoemParser(defaultGeneParser);
		view.setSelectedDefaultTrackParser(defaultTrackParser);
		
	}
	
	/**
	 * //TODO Initialize interface
	 *  
	 *  Add listeners to view 
	 */
	private static void initListeners(){
		
		/**************************************************************
		 * 
		 * 	Genomes / Refseq Controls 
		 * 
		 * ************************************************************
		 */
		
		////////////////////////
		//Load base track refseq
		////////////////////////
		view.add_btnLoadRefseqActionListener(getLoadRefseqActionListener());
		
		
		///////////////////////////////////
		// Select genome base toggle button
		///////////////////////////////////
		view.add_genomeBaseTglBtnActionListener(getGenomeBaseTglBtnActionListener());
		
		
		/**************************************************************
		 * 
		 * 		Features Controls (peaks)
		 * 
		 * ************************************************************
		 */
		
		//////////////////////////
		//Load peak as base button
		//////////////////////////
		view.add_btnLoadPeakBaseActionListener(getBtnLoadPeakBaseActionListener());
		
		///////////////////////////////////
		// Load a single feature file
		///////////////////////////////////
		view.add_btnLoadPeaksActionListener(getAddFeatureActionListener());
		
		//////////////////////////////////////////
		// Load a single feature file, mini button
		//////////////////////////////////////////
		view.add_addFeatureMiniBtnActionListener(getAddFeatureActionListener());
		
		///////////////////////////////////
		//  Load all peak files in a folder
		///////////////////////////////////
		view.add_btnLoadPeaksFolderActionListener(getBtnLoadPeaksFolderActionListener());
		
		/////////////////////////////////////////
		//  Drop the selected peak from the table
		/////////////////////////////////////////
		view.add_dropFeatureMiniBtnActionListener(getDropManyFeaturesActionListener());
		
		/////////////////////////////////////////
		//  select all features check box
		/////////////////////////////////////////
		view.add_selectAllFeaturesCkBxItemListener(getSelectAllFeaturesItemListener());
		
		
		///////////////////////////////////////
		// Select feature as base toggle button
		///////////////////////////////////////
		view.add_peakAsBaseTglBtnActionListener(getPeakAsBaseTglBtnActionListener());
		
		
		/**************************************************************
		 * 
		 *	Filter Controls
		 * 
		 * ************************************************************
		 */
		
		////////////////////////////
		// Add a filter, mini button
		////////////////////////////
		view.add_addFilterMiniBtnActionListener(getAddFilterListener());
		
		////////////////////////////
		// Drop a filter, mini button
		////////////////////////////
		view.add_dropFilterMiniBtnActionListener(getDropManyFiltersListener());
		
		////////////////////////////////////////////////
		// Select / deselect all filters untility button
		////////////////////////////////////////////////
		view.add_selectAllFiltersCkBxItemListener(getSelectAllFiltersItemListener());

		
		
		/**************************************************************
		 * 
		 * 	Session Controls
		 * 
		 * ************************************************************
		 */
		
		view.add_btnLoadSessionActionListener(getLoadSessionListener());
		
		view.add_btnSaveSessionActionListener(getSaveSessionListener());
		
		
		/**************************************************************
		 * 
		 * 	Search / Action Button Controls
		 * 
		 * ************************************************************
		 */
		
		view.add_btnSearchActionListener(getSearchButtonListener());
		
		view.add_btnAssignActionListener(getAssignButtonListener());
		
		view.add_btnSummarizeActionListener(getSummarizeButtonListener());
		
		view.add_btnCompareActionListener(getCompareButtonlistener());
		
		view.add_btnNormalizeActionListener(getNormalizeButtonListener());
		
		
		
		/**************************************************************
		 * 
		 * 	Utility / Remove All button controls
		 * 
		 * ************************************************************
		 */
		view.add_removeTracksRibbonBtnListener(getDropAllTracksListener());
		
		view.add_removeFiltersRibbonBtnListener(getDropAllFiltersListener());
		
		view.add_removeResultsRibbonBtnListener(getDropAllResultsListener());
		
		
		
		/**************************************************************
		 * 
		 * 	Result table controls
		 * 
		 * ************************************************************
		 */
		
		view.add_btnRemoveResultTableActionListener(getDropResultsTableListener());
		view.add_resultTabbedPaneChangeListener(getResultTabbedPaneChangeListener());
		view.add_btnExportTableActionListener(getExportTableListener());
		view.add_btnResultToPeakActionListener(getResultToPeakActionListener());
		
		
		/**************************************************************
		 * 
		 * 	Table Model Controls
		 * 
		 * ************************************************************
		 */
		
		///////////////////////////
		// On table update listener
		///////////////////////////
		featureTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				print("feature table change");
			}
		});
		
		///////////////////////////
		// On table update listener
		///////////////////////////
		filterTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				print("filter table change "+e.getSource().toString());
				
				for(String name:model.getFilterNames()){
					print(name);
				}
			}
		});
		
		

		
		
		/**************************************************************
		 * 
		 * 	Settings Listeners
		 * 
		 * ************************************************************
		 */
		view.add_chckbxShowIsoformsActionListener(getHideIsoformsActionListener());
		
		view.add_txtFldUpstreamSettingFocusListener(getUpstreamSettingFocusListener());
		
		view.add_txtFldDownstreamSettingFocusListener(getDownstreamSettingFocusListener());
		
		view.add_txtFldNormalizationSettingFocusListener(getNormalizationSettingFocusListener());
		
		view.add_txtFldOverlapFactorFocusListener(getOverlapFactorFocusListener());
		
		view.add_cBoxGenomeParsersActionListener(getDefaultGenomeParserCBoxActionListener());
		
		view.add_cBoxTrackParsersActionListener(getDefaultTrackParserCBoxActionListener());
		
		
		/**************************************************************
		 * 
		 * 	View Listeners
		 * 
		 * ************************************************************
		 */
		view.addWindowListener(getOnCloseListener());
		
		view.add_filterTableMouseListener(getFilterTableMouseListener());
		
		view.add_featureTableMouseListener(getFeatureTableMouseListener());

	}
	
	private static void initTabEditListeners(){
		tabEditListener = new TabTitleEditListener(view.getResultsTabbedPane(),model);
		view.add_renameTabListener(tabEditListener);
	}
	
	
	/**
	 *   //TODO Feature Listeners
	 */
	private static ActionListener getAddFeatureActionListener(){
		
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//create a file chooser
	        	JFileChooser chooser = new JFileChooser(currentDirectory);
	        	
	        	int returnVal = chooser.showOpenDialog(view);
	        	
	            if(returnVal == JFileChooser.APPROVE_OPTION) {
	               print("add feature: " + chooser.getSelectedFile().getName());
	            	
	            	boolean isParserValid = false;
	            	boolean isCancelled = false;
	            	
	            	StringBuffer errorMsg = new StringBuffer("");
	            	String trackParserKey = defaultTrackParser;
	            	TrackParserInterface parser =  null;
	            	
	            	//the chosen file
	            	File trackFile = chooser.getSelectedFile();
	            	
	            	//Make sure to prompt for a valid parser
	            	while(!isParserValid && !isCancelled){
	            		print(trackParserKey);
	            		parser = model.getTrackParserFactroy().getParser(trackParserKey);
	            		
            			if(parser.isValid(trackFile,errorMsg)){
            				isParserValid = true;
            			}else{
            				//show error and get user choice
            				int choice = dialogs.showParserErrorOptionDialog(trackFile,parser,errorMsg);
            				//clear message buffer
            				errorMsg = new StringBuffer("");
            				
            				if(choice == JOptionPane.CANCEL_OPTION){
            					isCancelled = true;
            				}else if(choice == JOptionPane.NO_OPTION){
            					print("Launch Custom Parser Wizard");
            					
            					TrackParserWizard wizard = new TrackParserWizard(trackFile,model.getTrackParserFactroy());
            					wizard.setVisible(true);
            					
            					if(wizard.isTrackParserGood()){
            						
            						TrackParserInterface customParser = wizard.getTrackParser();
            						String customKey = wizard.getTrackParserName();
            						
            						model.addTrackParser(customKey, customParser);
            						updateTrackParsers();
            						trackParserKey = customKey;
            					}
            				
            				}else{
            					String selected = dialogs.showTrackParserSectionDialog(model.getTrackParserFactroy().getKeys());
            					
            					if(selected != null){
            						trackParserKey = selected;
            					}
            				}
            			}
	            	}
	            	
	            	
	            	//only parse if valid, otherwise operation was cancelled.
	            	if(isParserValid){
	            		
	            		//Add peaks to the model
	            		SimpleTimer.start();
	            		PeakDatabase peaks = parser.parseTracks(trackFile);
	            		model.addPeakData(peaks);
	            		String timeStr = SimpleTimer.getTimeString();
	            		log.addCommand("Load peak: "+peaks.toString()+"["+trackFile.getAbsolutePath()+"]"+parser.getClass());
	            		
	            		//clean up
	            		featureTableModel.fireTableDataChanged();
	            		currentDirectory = trackFile.getAbsolutePath();
	            		//update track parser
	            		if(trackParserKey.compareTo(defaultTrackParser) != 0){
	            			defaultTrackParser = trackParserKey;
	            			view.setSelectedDefaultTrackParser(defaultTrackParser);
	            		}
	            		//update number of features
	            		//view.setFeatureStatusLbl("Number of Feature Tracks: "+model.getNumFeatures());
	            		view.setFeatureStatusLbl(timeStr+" s");
	            		
	            	}
	            }
			}
		};
	}
	
	private static ActionListener getDropManyFeaturesActionListener(){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(model.getNumFeatures() > 0){
					print("drop feature pressed");
					
					int[] tableRows = view.getFeatureTable().getSelectedRows();
					
					if(tableRows.length > 0){
						
						Vector<String> peaks = new Vector<String>();
						for(int i = 0; i < tableRows.length; ++i){
							int modelRow = view.getFeatureTable().convertRowIndexToModel(tableRows[i]);
							String name = (String)featureTableModel.getValueAt(modelRow, 0);
							peaks.add(name);
							print("peak is "+name);
						}
						
						int choice = dialogs.showAreYouSure_dropManyFeatures(peaks);
						
						if(choice == JOptionPane.OK_OPTION){
							print("choice ok");
							
							StringBuffer dropErrorMsg = new StringBuffer();
							for(String name: peaks){
								boolean isDropSuccess = model.dropTrack(name, dropErrorMsg);
								
								if(!isDropSuccess){
									JOptionPane.showMessageDialog(view,dropErrorMsg,"Drop Error",JOptionPane.ERROR_MESSAGE);
									log.addCommand("Drop peak: "+name);
								}
							}
							
							view.setFeatureStatusLbl("Number of Feature Tracks: "+model.getNumFeatures());
							
						}else if(choice == JOptionPane.CANCEL_OPTION){
							print("choice cancel");
						}
						
					}else{
						print("none selected");
					}
				}else{
					print("drop feature pressed, No tracks");
				}
				
				featureTableModel.fireTableDataChanged();
			}
		};
	}
	
	
	private static ItemListener getSelectAllFeaturesItemListener(){
		return new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					print("selected");
					model.selectAllFeatures();
				}else{
					print("deselected");
					model.deselectAllFeatures();
				}
				
				featureTableModel.fireTableDataChanged();
			}
		};
	}
	
	
	/**
	 *   //TODO filters Listeners
	 */
	
	private static ItemListener getSelectAllFiltersItemListener(){
		return new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					print("selected");
					model.selectAllFilters();
				}else{
					print("deselected");
					model.deselectAllFilters();
				}
				filterTableModel.fireTableDataChanged();
			}
		};
	}
	
	private static ActionListener getAddFilterListener(){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				print("Add Filter button pressed.");
				
				
				if(model.getNumFeatures() > 0){
					
					EditFilterDialog filterDialog = new EditFilterDialog(view,model,log);
					
					filterDialog.setVisible(true);
					
					FilterInterface filter = filterDialog.getFilter();
					
					if(filter != null){
						//do something with filter
						
						model.addFilterData(filter);
						filterTableModel.fireTableDataChanged();
						
					}else{
						print("filter null");
					}
				
				}else{
					//no peaks are loaded
					JOptionPane.showMessageDialog(view, "There are currently no peaks loaded. You can load peaks from the Home tab.","No peaks are loaded.",JOptionPane.PLAIN_MESSAGE);
				}
				
			}
		};
	}
	
	private static ActionListener getDropManyFiltersListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(model.getNumFilters() > 0){
					print("drop filter pressed");
					
					//int tableRow = view.getFilterTable().getSelectedRow();
					int[] tableRows = view.getFilterTable().getSelectedRows();
					
					if(tableRows.length > 0){
						
						Vector<String> names = new Vector<String>();
						for(int i = 0; i < tableRows.length; ++i){
							int modelRow = view.getFilterTable().convertRowIndexToModel(tableRows[i]);
							String name = (String)filterTableModel.getValueAt(modelRow, 0);
							names.add(name);
							print("Filter is "+name);
						}
						
						
						int choice = dialogs.showAreYouSure_dropManyFilters(names);
						
						if(choice == JOptionPane.OK_OPTION){
							print("Drop filter");
							
							StringBuffer dropErrorMsg = new StringBuffer();
							
							for(String name: names){
								boolean isDropSuccess = model.dropFilter(name, dropErrorMsg);
								
								if(!isDropSuccess){
									JOptionPane.showMessageDialog(view,dropErrorMsg,"Drop Error",JOptionPane.ERROR_MESSAGE);
								}
							}
							
						}else if(choice == JOptionPane.CANCEL_OPTION){
							print("drop filter choice cancelled");
						}

					}else{
						print("none selected");
					}
				}else{
					print("drop filter pressed, No filters");
				}
				
				filterTableModel.fireTableDataChanged();
			}
		};
	}
	
	//TODO
	private static ActionListener getDropAllTracksListener(){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				print("remove all tracks pressed");
				
				if(model.getNumFeatures() > 0){
					Vector<String> features = model.getFeatureNames();
					
					int choice = dialogs.showAreYouSure_dropManyFeatures(features);
					
					
					if(choice == JOptionPane.OK_OPTION){
						print("choice ok");
						
						StringBuffer dropErrorMsg = new StringBuffer();
						for(String name: features){
							boolean isDropSuccess = model.dropTrack(name, dropErrorMsg);
							
							if(!isDropSuccess){
								JOptionPane.showMessageDialog(view,dropErrorMsg,"Drop Error",JOptionPane.ERROR_MESSAGE);
								log.addCommand("Drop peak: "+name);
							}
						}
						
						featureTableModel.fireTableDataChanged();
						view.setFeatureStatusLbl("Number of Feature Tracks: "+model.getNumFeatures());
						
					}else if(choice == JOptionPane.CANCEL_OPTION){
						print("choice cancel");
					}
				}
			}
		};
	}
	
	private static ActionListener getDropAllFiltersListener(){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				print("removed all filters pressed");
				if(model.getNumFilters() > 0){
					
					Vector<String> names = model.getFilterNames();

					int choice = dialogs.showAreYouSure_dropManyFilters(names);
					
					if(choice == JOptionPane.OK_OPTION){
						print("Drop filter");
						
						StringBuffer dropErrorMsg = new StringBuffer();
						
						for(String name: names){
							boolean isDropSuccess = model.dropFilter(name, dropErrorMsg);
							
							if(!isDropSuccess){
								JOptionPane.showMessageDialog(view,dropErrorMsg,"Drop Error",JOptionPane.ERROR_MESSAGE);
							}
						}
						
					}else if(choice == JOptionPane.CANCEL_OPTION){
						print("drop filter choice cancelled");
					}

				}else{
					print("none selected");
				}
				
				filterTableModel.fireTableDataChanged();
			}
		};
	}
	
	private static ActionListener getDropAllResultsListener(){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				print("remove all results pressed");
				
				if(model.getNumResultTables() > 0){
					
					print("Are you sure delete?");
					
					int choice = dialogs.showAreYouSure_dropAllResults();
					
					if(choice == JOptionPane.OK_OPTION){
						print("yes, delete");
						
						for(int i = model.getNumResultTables()-1; i >= 0; --i ){
							model.dropResultTable(i);
							view.dropResultTab(i);	
						}
						
					}else{
						print("no, cancel delete");
					}
				}
			}
		};
	}
	
	
	/**
	 *   Save/Load Session Listeners
	 * 
	 */
	private static ActionListener getLoadSessionListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print("load session pressed");
				
				
				JFileChooser chooser = new JFileChooser(currentDirectory);
				
				int returnVal = chooser.showOpenDialog(view);
				
				File loadFile = chooser.getSelectedFile();
				
				PapstModel newModel = null;
				
				if(returnVal != JFileChooser.CANCEL_OPTION){
				
					try{
						
						SessionLoadWorker worker = new SessionLoadWorker(loadFile,view);
						worker.execute();
						newModel = worker.get();
						
						if(newModel != null){
							model = newModel;
							
							initInterface();
							
							tabEditListener.setModel(model);
							
							featureTableModel.fireTableDataChanged();
							filterTableModel.fireTableDataChanged();
						}
						
						print("model loaded, results " + model.getResultTables().size());
						
					}catch (InterruptedException | ExecutionException  ex) {
						JOptionPane.showMessageDialog(view,"This does not appear to be a session file.",
							"Session file format error.",JOptionPane.INFORMATION_MESSAGE);
						ex.printStackTrace();
					}
				
				}// open not cancelled
				
			}
		};
	}
	

	private static ActionListener getSaveSessionListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print("save session pressed");
				
				//get the file chooser
				JFileChooser chooser = new JFileChooser(currentDirectory);
				//set the current file
				chooser.setSelectedFile(new File("session.papst"));
				//open the dialog
				int returnVal = chooser.showSaveDialog(view);
				
				//get the file
				File sessionFile = chooser.getSelectedFile();
				
				//control flag
				boolean writeOk = true;
				
				//check that the file is unique and not in use
				if(returnVal != JFileChooser.CANCEL_OPTION && sessionFile.exists()){
					//check for overwrite of file
					int choice = JOptionPane.showConfirmDialog(view,"The file '"+sessionFile.getName()+"' already exists. Overwrite it?","Filename in Use",JOptionPane.OK_CANCEL_OPTION);
					if(choice != JOptionPane.OK_OPTION){
						writeOk = false;
					}
				}
				
				
				//if ok to write
				if(writeOk && returnVal != JFileChooser.CANCEL_OPTION){
					
					boolean noError = true;
					
					try{
						
						SessionSaveProgressDialog progDialog = new SessionSaveProgressDialog(view,"Saving Model ...",sessionFile,model);
						progDialog.setVisible(true);
						
						
					}catch(Exception ex){
						ex.printStackTrace();
						JOptionPane.showMessageDialog(view, "An unknown error has occurred.","Save Session Error",JOptionPane.ERROR_MESSAGE);
						noError = false;
					}
					
					if(noError){
						JOptionPane.showMessageDialog(view, "Your session has been saved.","Session Saved",JOptionPane.PLAIN_MESSAGE);
					}
				}
			}
		};
	}	
	
	/**
	 *  //TODO Action button listeners
	 * 
	 */
	private static ActionListener getSearchButtonListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print("search button pressed");
				
				//error checking
				if(!model.isBaseTrackLoaded()){
					print("no base loaded");
					JOptionPane.showMessageDialog(view, 
							"No base track is loaded. Please load a base track from the home tab.",
							"No Base Track Loaded.",
							JOptionPane.INFORMATION_MESSAGE);
				}else if(model.getNumFeatures() < 1 || model.getNumEnabledFeatures() < 1){
					print("no features or none enabled");
					JOptionPane.showMessageDialog(view, 
							"No features are selected or none are loaded. Please select a feature track to use during the search.",
							"No Features Are Selected.",
							JOptionPane.INFORMATION_MESSAGE);
				}else if(model.getNumFilters() < 1 || model.getNumEnabledFilters() < 1){
					print("no filters or none enabled");
					JOptionPane.showMessageDialog(view, 
							"No filters are selected or none are loaded. Please select a filter to use during the search.",
							"No Filters Are Selected.",
							JOptionPane.INFORMATION_MESSAGE);
					
				}else{
					print("proceed with search");
					
					SimpleTimer.start();
					ResultData data = model.searchAndGetTable();
					String timeStr = SimpleTimer.getTimeString();
					
					if(data != null){
						
						AbstractResultTableModel tableModel;
						
						tableModel = data.getTableModel();
						
						print("rows "+tableModel.getRowCount());
						print("cols "+tableModel.getColumnCount());
						
						tableModel.setTabTitle("Search");
						
						view.addResultTableTab(tableModel);
						view.setFeatureStatusLbl(timeStr+" s");
					}
				}
			}
		};
	}
	
	
	private static ActionListener getAssignButtonListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print("Assign button pressed");
				
				
				if(model.getNumFeatures() > 0){
					
					if(!model.isGeneDatabaseLoaded()){
						JOptionPane.showMessageDialog(view, 
								"No Genome is loaded. Please load a genome from the home tab.",
								"No Genome Loaded.",
								JOptionPane.INFORMATION_MESSAGE);
					}else{
				
						int choice = dialogs.showAssignOptionDialog();
						
						print("choice "+choice );
						if(choice == JOptionPane.YES_OPTION){
							print("Single track option");
							
							String chosenFeature = dialogs.showAvailableTracksAssignDialog(model.getFeatureNames());
							
							print(chosenFeature);
							
							//AbstractResultTableModel tableModel = model.getAssignResultTable(chosenFeature);
							SimpleTimer.start();
							ResultData data = model.getAssignResultTable(chosenFeature);
							String timeStr = SimpleTimer.getTimeString();
							
							if(data != null){
								
								AbstractResultTableModel tableModel = data.getTableModel();
								
								print("rows "+tableModel.getRowCount());
								print("cols "+tableModel.getColumnCount());
								
								tableModel.setTabTitle("Assign");
								
								view.addResultTableTab(tableModel);
								view.setFeatureStatusLbl(timeStr+" s");
								
							}
							
						}else if(choice == JOptionPane.NO_OPTION){
							print("All tracks option");
							JFileChooser chooser = new JFileChooser(currentDirectory);
							chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							
							int fileChoiceValue = chooser.showOpenDialog(view);
							
							File assignDir = chooser.getSelectedFile();
							if(assignDir != null && assignDir.isDirectory() && fileChoiceValue != JFileChooser.CANCEL_OPTION){
								print("Write to folder "+assignDir.getName());
								
								SimpleTimer.start();
								model.assignAllAvailable(assignDir);
								String timeStr = SimpleTimer.getTimeString();
								
								view.setFeatureStatusLbl(timeStr+" s");
								
								JOptionPane.showMessageDialog(view, 
										"All files have been assigned using the current genome.",
										"Genes Assigned.",
										JOptionPane.INFORMATION_MESSAGE);
							}
						}
					}
				}
			}
		};
	}
	
	private static ActionListener getSummarizeButtonListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print("Summarize button pressed");
				
				if(model.getNumFeatures() > 0){
					
					//genome must be loaded
					if(!model.isGeneDatabaseLoaded()){
						JOptionPane.showMessageDialog(view, 
								"No Genome is loaded. Please load a genome from the home tab.",
								"No Genome Loaded.",
								JOptionPane.INFORMATION_MESSAGE);
					}else{
						
						//AbstractResultTableModel tableModel = model.getSummaryResultTable();
						SimpleTimer.start();
						ResultData data = model.getSummaryResultTable();
						String timerStr = SimpleTimer.getTimeString();
						
						if(data != null){
							
							AbstractResultTableModel tableModel = new SummaryResultTableModel(data.getTableData(), data.getColumnNames());
							
							print("rows "+tableModel.getRowCount());
							print("cols "+tableModel.getColumnCount());
							
							tableModel.setTabTitle("Summary");
							
							view.addResultTableTab(tableModel);
							view.setFeatureStatusLbl(timerStr+" s");
							
						}
					}
				}
			}
		};
	}

	private static ActionListener getCompareButtonlistener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print("Compare button pressed");
				
				if(model.getNumFeatures() > 0 && model.getNumEnabledFeatures() > 1){
						
					//AbstractResultTableModel tableModel = model.getComparisonResultTable();
					SimpleTimer.start();
					ResultData data = model.getComparisonResultTable();
					String timeStr = SimpleTimer.getTimeString();
					
					if(data != null){
						
						AbstractResultTableModel tableModel = data.getTableModel();
						
						
						print("rows "+tableModel.getRowCount());
						print("cols "+tableModel.getColumnCount());
						
						tableModel.setTabTitle("Compare");
						
						view.addResultTableTab(tableModel);
						view.setFeatureStatusLbl(timeStr+" s");
					}
				}
			}
		};
	}
	
	private static ActionListener getNormalizeButtonListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				print("Normalize button pressed");
				
				
				if(model.getNumFeatures() < 1){
					print("No Tracks loaded");
					JOptionPane.showMessageDialog(view, 
							"No tracks have been loaded. You can load new feature tracks from the home tab",
							"No Feature Track Loaded.",
							JOptionPane.INFORMATION_MESSAGE);
					
				}else{
					NormalizationDialog normDialog = new NormalizationDialog(view,model,log,currentDirectory);
					normDialog.setVisible(true);
					print("Normalization dialog closed");
					
					if(!normDialog.isCancelled()){
						if(!normDialog.isUndoNorm()){
							HashMap<String, Integer> tagCountMap = normDialog.getTagCountMap();
							if(tagCountMap != null && tagCountMap.size() > 0){
								print("rows "+tagCountMap.size());
								
								model.normalize(tagCountMap);
							}
						}else{
							print("undo");
							model.undoNormalize(model.getFeatureNames());
						}
						featureTableModel.fireTableDataChanged();
						
					}else{
						print("normalize cancelled.");
					}
				}
			}
		};
	}
	
	
	
	/**
	 * 
	 *  TODO  Result table listeners
	 * 
	 */
	
	private static ActionListener getExportTableListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print("export table button pressed");
				if(model.getNumResultTables() > 0){
					print("tables exist");
					
					//get index
					int index = view.getSelectedResultTab();
					
					//get result name
					String name = view.getSelectedTabName(index);
					
					print("exporting "+name);
					
					//get the file chooser
					JFileChooser chooser = new JFileChooser(currentDirectory);
					//set the current file
					chooser.setSelectedFile(new File("results.csv"));
					//open the dialog
					int returnVal = chooser.showSaveDialog(view);
					
					//get the file
					File exportFile = chooser.getSelectedFile();
					
					//control flag
					boolean writeOk = true;
					
					//check that the file is unique and not in use
					if(returnVal != JFileChooser.CANCEL_OPTION && exportFile.exists()){
						//check for overwrite of file
						int choice = JOptionPane.showConfirmDialog(view,"The file '"+exportFile.getName()+"' already exists. Overwrite it?","Filename in Use",JOptionPane.OK_CANCEL_OPTION);
						if(choice != JOptionPane.OK_OPTION){
							writeOk = false;
						}
					}
					
					
					//if ok to write
					if(writeOk && returnVal != JFileChooser.CANCEL_OPTION){
						
						//TableModel tableModel = model.getResultTable(index);
						ResultData data = model.getResultTable(index);
						JTable table = view.getResultTable(index);
						
						try{
							PrintWriter writer = new PrintWriter(exportFile);
							
							
							for(int i = 0; i < table.getColumnCount(); ++i){
								if(i == 0){
									writer.print(data.getColumnName(table.convertColumnIndexToModel(i)));
								}else{
									writer.print(","+data.getColumnName(table.convertColumnIndexToModel(i)));
								}
							}
							writer.println();
							
							
							//for each row
							for(int i = 0; i < table.getRowCount(); ++i){
								
								int rowIndex = table.convertRowIndexToModel(i);
								
								//for each column
								for(int j = 0; j < table.getColumnCount(); ++j){
									
									int colIndex = table.convertColumnIndexToModel(j);
									
									if(j == 0){
										writer.print(data.getValueAt(rowIndex, colIndex));
									}else{
										writer.print(","+data.getValueAt(rowIndex, colIndex));
									}
								
								}
								//print new line
								writer.println();
								
							}
							//close writer
							writer.close();
						
						}catch(FileNotFoundException exception){
							exception.printStackTrace();
						}
					}
				}
			}
		};
	}
	
	//TODO Drop results table listener
	private static ActionListener getDropResultsTableListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(model.getNumResultTables() > 0){
					print("remove table");
					
					//get index
					int index = view.getSelectedResultTab();
					
					//get result name
					String name = view.getSelectedTabName(index);
					
					print("Are you sure delete?");
					int choice = dialogs.showAreYouSure_dropResultTable(name);
					
					if(choice == JOptionPane.OK_OPTION){
						print("yes, delete");
						
						model.dropResultTable(index);
						view.dropResultTab(index);
						
					}else{
						print("no, cancel delete");
					}
				}
			}
		};
	}
	
	private static ChangeListener getResultTabbedPaneChangeListener(){
		return new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				int resultIndex = view.getSelectedResultTab();
				
				if(resultIndex >= 0 && model.getResultTable(resultIndex).getType() == ResultData.SEARCH_RESULT){
					print("status "+model.getResultTable(resultIndex).getType());
					view.setEnabledResultToPeakBtn(true);
				}else{
					view.setEnabledResultToPeakBtn(false);
				}
				
			}
		};
	}
	
	//TODO result to peak
	private static ActionListener getResultToPeakActionListener(){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				print("resultToPeak pressed");
				
				int index = view.getSelectedResultTab();
				ResultData data = model.getResultTable(index);
				
				Vector<String> choices = new Vector<String>(data.getColumnNames());
				//remove the first 3 madatory fields
				choices.subList(0, 3).clear();
				
				String selected = dialogs.showResultColumnOptionDialog(choices);
				
				boolean nameOk = false;
				
				String trackName = null;
				
				if(selected != null){
					while(!nameOk){
						trackName = dialogs.showPeakNameInputDialog();
						
						//break if cancelled
						if(trackName == null){
							break;
						}
						
						//check if name is unique
						if(model.isFeatureNameUnique(trackName)){
							nameOk = true;
						}else{
							JOptionPane.showMessageDialog(view, 
									"This track name is already in use. Please choose another name.",
									"Track Name is Not Unique",
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
				
				
				if(selected != null && trackName != null){
					print(selected);
					int valueCol = 0;
					for(int i = 0; i < choices.size(); ++i){
						if(selected.compareTo(choices.get(i)) == 0){
							valueCol = i + 3;
							break;
						}
					}
					
					HashMap<String, Vector<Region> > peakChromMap = new HashMap<String, Vector<Region> >();
					PeakDatabase peakDB = null;
					
					
					for(int i = 0; i < data.getTableData().size(); ++i){
						//get location string
						String location = (String)data.getValueAt(i, 2);
						
						int colon = location.indexOf(":");
					    int dash  = location.indexOf("-");
					    
					    String chrom = location.substring(0,colon);
						
						//get start and end coordinates
					    String start_str = location.substring(colon+1,dash);
						String end_str   = location.substring(dash+1,location.length());
						long start = Long.parseLong(start_str);
						long end   = Long.parseLong(end_str);
						
						//get doulbe value
						double value =(Double)data.getValueAt(i,valueCol);
						
						String name0 = (String)data.getValueAt(i, 0);
						String name1 = (String)data.getValueAt(i, 1);
						
						Peak peak = new Peak(chrom, start, end, value, name0+"_"+name1);
						
						//if first chrom, initialize map
						if(!peakChromMap.containsKey(peak.chrom)){
							peakChromMap.put(peak.chrom,new Vector<Region>());
						}
						//add peak to proper chromosome
						peakChromMap.get(peak.chrom).add(peak);
					}
					
					
					peakDB = new PeakDatabase(peakChromMap, trackName);
					
					model.addPeakData(peakDB);
            		String timeStr = SimpleTimer.getTimeString();
            		log.addCommand("Load peak: "+peakDB.toString()+"[resultToPeak]");
            		
            		//clean up
            		featureTableModel.fireTableDataChanged();
            		view.setFeatureStatusLbl(timeStr+" s");
            		
				}else{
					print("Result to Peak cancelled.");
				}
				
				//HashMap<String, Vector<Region> > peakChromMap = new HashMap<String, Vector<Region> >();
				//PeakDatabase peakDB = null;
				
				
				
				
			}
		};
	}

	/**
	 * //TODO Settings Listeners
	 */
	private static ActionListener getHideIsoformsActionListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print("show isoforms clicked");
				
				if(view.getShowIsoformsIsSelected()){
					model.setShowIsoforms(true);
				}else{
					model.setShowIsoforms(false);
				}
			}
		};
	}
	
	private static FocusListener getNormalizationSettingFocusListener(){
		return new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				String text = view.getNormalizationValue();
				print("norm: update setting- "+text);
				
				try{
					int normvalue = Integer.parseInt(text);
					
					if(normvalue < 0){
						normvalue = normvalue * -1;
						view.setNormalizationValue(model.getNormFactor()+"");
					}
					
					model.setNormFactor(normvalue);

				}catch(NumberFormatException ex){
					view.setNormalizationValue(model.getNormFactor()+"");
				}
				
				
			}
			
			//not needed
			@Override
			public void focusGained(FocusEvent arg0) {}
			
		};
	}
	
	private static FocusListener getOverlapFactorFocusListener(){
		return new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				String text = view.getOverlapValue();
				print("overlap: update setting- "+text);
				
				try{
					long overlapValue = Long.parseLong(text);
					
					if(overlapValue < 0){
						overlapValue = 1;
					}
					
					model.setOverlapFactor(overlapValue);
					view.setOverlapValue(model.getOverlapFactor()+"");

				}catch(NumberFormatException ex){
					view.setOverlapValue(model.getNormFactor()+"");
				}
				
				
			}
			
			//not needed
			@Override
			public void focusGained(FocusEvent arg0) {}
			
		};
	}
	
	private static FocusListener getUpstreamSettingFocusListener(){
		return new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				String text = view.getUpstreamSetting();
				print("upstream: update setting- "+text);
				
				try{
					int upvalue = Integer.parseInt(text);
					
					if(upvalue < 0){
						upvalue = upvalue * -1;
						view.setUpstreamSetting(model.getDefaultUpstream()+"");
					}
					
					model.setDefaultUpstream(upvalue);

				}catch(NumberFormatException ex){
					view.setUpstreamSetting(model.getDefaultUpstream()+"");
				}
				
			}
			
			//not needed
			@Override
			public void focusGained(FocusEvent arg0) {}
			
		};
	}
	
	private static FocusListener getDownstreamSettingFocusListener(){
		return new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				String text = view.getDownstreamSetting();
				print("downstream: update setting- "+text);
				
				try{
					int downvalue = Integer.parseInt(text);
					
					if(downvalue < 0){
						downvalue = downvalue * -1;
						view.setDownstreamSetting(model.getDefaultDownstream()+"");
					}
					
					model.setDefaultDownstream(downvalue);

				}catch(NumberFormatException ex){
					view.setDownstreamSetting(model.getDefaultDownstream()+"");
				}
				
			}
			
			//not needed
			@Override
			public void focusGained(FocusEvent arg0) {}
			
		};
	}
	
	private static ActionListener getDefaultGenomeParserCBoxActionListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print("default genome parser selected");
				String newDefault = view.getDefaultGenomeParser();
				defaultGeneParser = newDefault;
			}
		};
	}
	
	private static ActionListener getDefaultTrackParserCBoxActionListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print("default track parser selected");
				String newDefault = view.getDefaultTrackParser();
				defaultTrackParser = newDefault;
			}
		};
	}
	
	private static WindowAdapter getOnCloseListener(){
		return new WindowAdapter() {
	        public void windowClosing(WindowEvent e) 
	        {
	        	
	        	int option = dialogs.showAreYouSure_closePapst();
	        	
	        	if(option == JOptionPane.OK_OPTION){
	        		log.writeLog(startDirectory);
	        		System.exit(0);
	        	}else{
	        		
	        	}
	        }
		};
	}
	
	private static ActionListener getLoadRefseqActionListener(){
		return new ActionListener() {
	        public void actionPerformed(ActionEvent evt) {
	        	//create a file chooser
	        	JFileChooser chooser = new JFileChooser(currentDirectory);
	        	
	        	int returnVal = chooser.showOpenDialog(view);
	        	
	            if(returnVal == JFileChooser.APPROVE_OPTION) {
	            	//print("You chose to open this file: " + chooser.getSelectedFile().getName());
	            	
	            	
	            	boolean isParserValid = false;
	            	boolean isCancelled = false;
	            	
	            	StringBuffer errorMsg = new StringBuffer("");
	            	
	            	String geneParserKey = defaultGeneParser;
	            	GenomeParserInterface parser =  null;
	            	
	            	File geneFile = chooser.getSelectedFile();
	            	
	            	//try default
	            	
	            	
	            	
	            	Vector<String> parsers = model.getGenomeParserFactroy().getKeys();
	            	//try all gene parsers
	            	while(!parsers.isEmpty()){
	            		
	            		geneParserKey = parsers.firstElement();
	            		parsers.remove(geneParserKey);
	            		
	            		parser = model.getGenomeParserFactroy().getParser(geneParserKey);
	            		print("Trying " + parser.getName());
	            		
	            		StringBuffer buffer = new StringBuffer();
	            		if(parser.isValid(geneFile, buffer)){
	            			isParserValid = true;
	            			//reset default
	            			defaultGeneParser = geneParserKey;
	            			view.setSelectedDefaultGenoemParser(defaultGeneParser);
	            			break;
	            		}
	            	}
	            	
	            	//If defaults fail, prompt for wizard
	            	//Make sure to prompt for a valid parser
	            	while(!isParserValid && !isCancelled){
	            		
	            		parser = model.getGenomeParserFactroy().getParser(geneParserKey);
	            		print("parser "+parser.getName());
	            		
            			if(parser.isValid(geneFile,errorMsg)){
            				isParserValid = true;
            			}else{
            				//show error and get user choice
            				// other Genome parsers
            				int choice = dialogs.showGenomeParserErrorOptionDialog();
            				
            				
            				if(choice == JOptionPane.NO_OPTION || choice == JOptionPane.CANCEL_OPTION){
            					isCancelled = true;
            				}else if(choice == JOptionPane.YES_OPTION){
            					print("Launch Custom Parser Wizard");
            					
            					
            					GenomeParserWizzard wizzard = new GenomeParserWizzard(geneFile,model.getGenomeParserFactroy());
            					wizzard.setVisible(true);
            					
            					if(wizzard.isGenomeParserGood()){
            						
            						print("genome parser good");
            						
            						GenomeParserInterface customParser = wizzard.getGenomeParser();
            						String customKey = wizzard.getGenomeParserName();
            						
            						model.addGenomeParser(customKey,customParser);
            						updateGenomeParsers();
            						
            						geneParserKey = customKey;
            						print("custome key "+customKey);
            					}
            				}
            			}
	            	}
	            	
	            	if(isParserValid){
	            		SimpleTimer.start();
		            	//Parser the gene file
		            	GeneDatabase genome = parser.parseGenes(geneFile);
		            	String timeStr = SimpleTimer.getTimeString();
		            	
		            	//add gene to the model
		            	model.setGeneDatabase(genome);
		            	log.addCommand("Load Genome: "+genome.toString()+"["+geneFile.getAbsolutePath()+"]"+parser.getClass());
		            	
		            	//set genome as base track in model
		            	model.setGenomeAsBase();
		            	
		            	//set genome as base track in view, toggle buttons
		            	view.setGenomeAsBase();
		            	//update view feed back
		            	view.setBaseTrackStatusLbl("Base Track: "+model.getCurrentBaseTrack()+ ", "+model.getNumBaseRegions()+" Genes Loaded.");
		        	   
		            	//clean up
		            	currentDirectory = geneFile.getAbsolutePath();
		            	
		            	defaultGeneParser = geneParserKey;
		            	view.setSelectedDefaultGenoemParser(defaultGeneParser);
		            	
		            	view.setCurrentStatusLbl("Base Track set to Genome.");
		            	view.setFeatureStatusLbl(timeStr+" s");
	            	}
	            }
	        }
		};
	}
	
	
	private static ActionListener getGenomeBaseTglBtnActionListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//check if genes are loaded.
				if(!model.isGeneDatabaseLoaded()){
					JOptionPane.showMessageDialog(view,
							"No gene data have been loaded yet. Press Load Refseq Genome from the Home tab.",
							"Gene Datbase Error",
							JOptionPane.ERROR_MESSAGE);
					
					if(model.getBaseStatus() == PapstModel.REFGENE_BASE){
						view.setGenomeAsBase();
					}else if(model.getBaseStatus() == PapstModel.FEATURE_BASE){
						view.setPeakAsBase();
					}
					
				}else{
					model.setGenomeAsBase();
					view.setGenomeAsBase();
					
					view.setBaseTrackStatusLbl("Base Track: "+model.getCurrentBaseTrack()+ ", "+model.getNumBaseRegions()+" Genes Loaded.");
					view.setCurrentStatusLbl("Base Track set to Genome.");
				}
			}
		};
	}
	
	
	private static ActionListener getBtnLoadPeakBaseActionListener(){
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				print("load base peak btn pressed");
				
				//create a file chooser
	        	JFileChooser chooser = new JFileChooser(currentDirectory);
	        	
	        	int returnVal = chooser.showOpenDialog(view);
	        	
	            if(returnVal == JFileChooser.APPROVE_OPTION) {
	               //print("You chose to open this file: " + chooser.getSelectedFile().getName());
	            	
	            	boolean isParserValid = false;
	            	boolean isCancelled = false;
	            	
	            	StringBuffer errorMsg = new StringBuffer("");
	            	
	            	String trackParserKey = defaultTrackParser;
	            	TrackParserInterface parser =  null;
	            	
	            	//the chosen file
	            	File trackFile = chooser.getSelectedFile();
	            	
	            	//Make sure to prompt for a valid parser
	            	while(!isParserValid && !isCancelled){
	            		print(trackParserKey);
	            		parser = model.getTrackParserFactroy().getParser(trackParserKey);
	            		
            			if(parser.isValid(trackFile,errorMsg)){
            				isParserValid = true;
            			}else{
            				//show error and get user choice
            				int choice = dialogs.showParserErrorOptionDialog(trackFile,parser,errorMsg);
            				//clear message buffer
            				errorMsg = new StringBuffer("");
            				
            				print("choice: "+choice);
            				
            				if(choice == JOptionPane.CANCEL_OPTION){
            					isCancelled = true;
            				}else if(choice == JOptionPane.NO_OPTION){
            					print("Launch Custom Parser Wizzard");
            					
            					
            					TrackParserWizard wizzard = new TrackParserWizard(trackFile,model.getTrackParserFactroy());
            					wizzard.setVisible(true);
            					
            					if(wizzard.isTrackParserGood()){
            						
            						TrackParserInterface customParser = wizzard.getTrackParser();
            						String customKey = wizzard.getTrackParserName();
            						
            						model.addTrackParser(customKey, customParser);
            						updateTrackParsers();
            						trackParserKey = customKey;
            					}
            				
            				}else{
            					String selected = dialogs.showTrackParserSectionDialog(model.getTrackParserFactroy().getKeys());
            					if(selected != null){
            						trackParserKey = selected;
            					}
            				}
            			}
	            	}
	            	
	            	
	            	//only parse if valid, otherwise operation was cancelled.
	            	if(isParserValid){
	            		
	            		PeakDatabase peaks = parser.parseTracks(trackFile);
	            		model.addPeakData(peaks);
	            		log.addCommand("Load peak: "+peaks.toString()+"["+trackFile.getAbsolutePath()+"]"+parser.getClass());
	            		model.setPeakAsBase(peaks.toString());
		        	   
	            		view.setBaseTrackStatusLbl("Base Track: "+trackFile.getName()+ ", "+peaks.getNumRegions()+" regions Loaded.");
	            		view.setPeakAsBase();
		        	   
	            		//clean up
	            		currentDirectory = trackFile.getAbsolutePath();
	            		
	            		//update the default track parser
	            		if(trackParserKey.compareTo(defaultTrackParser) != 0){
	            			defaultTrackParser = trackParserKey;
	            			view.setSelectedDefaultTrackParser(defaultTrackParser);
	            		}
	            		
	            		view.setFeatureStatusLbl("Number of Feature Tracks: "+model.getNumFeatures());
	            		
	            		view.setCurrentStatusLbl("Base Track set to Feature.");
	            		featureTableModel.fireTableDataChanged();
	            	}
	            }
			}
		};
	}
	
	private static ActionListener getBtnLoadPeaksFolderActionListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser chooser = new JFileChooser(currentDirectory);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				int fileChoiceValue = chooser.showOpenDialog(view);
				
				File peakDir = chooser.getSelectedFile();
				if(peakDir != null && peakDir.isDirectory() && fileChoiceValue != JFileChooser.CANCEL_OPTION){
					File[] files = peakDir.listFiles();
					
					double totalSeconds = 0.0;
					for(File file:files){
						
						print(file.getName());
						boolean isParserValid = false;
		            	boolean isCancelled = false;
		            	
		            	StringBuffer errorMsg = new StringBuffer("");
		            	
		            	String trackParserKey = defaultTrackParser;
		            	TrackParserInterface parser =  null;
		            	
		            	
		            	//Make sure to prompt for a valid parser
		            	while(!isParserValid && !isCancelled){
		            		
		            		parser = model.getTrackParserFactroy().getParser(trackParserKey);
		            		
	            			if(parser.isValid(file,errorMsg)){
	            				isParserValid = true;
	            			}else{
	            				//show error and get user choice
	            				int choice = dialogs.showParserErrorSkipOptionDialog(file,parser,errorMsg);
	            				//clear message buffer
	            				errorMsg = new StringBuffer("");
	            				
	            				if(choice == JOptionPane.CANCEL_OPTION){
	            					isCancelled = true;
	            				}else if(choice == JOptionPane.NO_OPTION){
	            					print("Launch Custom Parser Wizzard");
	            					
	            					
	            					TrackParserWizard wizzard = new TrackParserWizard(file,model.getTrackParserFactroy());
	            					wizzard.setVisible(true);
	            					
	            					if(wizzard.isTrackParserGood()){
	            						
	            						TrackParserInterface customParser = wizzard.getTrackParser();
	            						String customKey = wizzard.getTrackParserName();
	            						
	            						model.addTrackParser(customKey, customParser);
	            						updateTrackParsers();
	            						trackParserKey = customKey;
	            					}
	            				
	            				}else{
	            					String selected = dialogs.showTrackParserSectionDialog(model.getTrackParserFactroy().getKeys());
	            					if(selected != null){
	            						trackParserKey = selected;
	            					}
	            				}
	            			}
		            	}
		            	
		            	//only parse if valid, otherwise operation was cancelled.
		            	if(isParserValid){
		            		
		            		//Add peaks to the model
		            		SimpleTimer.start();
		            		PeakDatabase peaks = parser.parseTracks(file);
		            		model.addPeakData(peaks);
		            		totalSeconds += SimpleTimer.getElapsedTime();
		            		log.addCommand("Load peak: "+peaks.toString()+"["+file.getAbsolutePath()+"]"+parser.getClass());
		            		
		            		//clean up
		            		featureTableModel.fireTableDataChanged();
		            		currentDirectory = file.getAbsolutePath();
		            		//update track parser
		            		if(trackParserKey.compareTo(defaultTrackParser) != 0){
		            			defaultTrackParser = trackParserKey;
		            			view.setSelectedDefaultTrackParser(defaultTrackParser);
		            		}
		            		//update number of features
		            		view.setFeatureStatusLbl("Number of Feature Tracks: "+model.getNumFeatures());
		            	}
					}
					
					view.setFeatureStatusLbl(String.format("%.5g%n",totalSeconds));
				}
			}
		};
	}
	
	
	private static ActionListener getPeakAsBaseTglBtnActionListener(){
		return new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
			
				//check if genes are loaded.
				if(model.getNumFeatures() == 0){
					JOptionPane.showMessageDialog(view,
						"There are no peak or feature tracks currently loaded.\nPress Load Peak as Base Track to load a feature as the base on the Home Tab.",
						"Feature Datbase Error",
						JOptionPane.ERROR_MESSAGE);
					view.setGenomeAsBase();
				}else{
					
					String selectedPeak = dialogs.showAvailableTrackSectionDialog(model.getFeatureNames());
					
					if(selectedPeak != null){
						model.setPeakAsBase(selectedPeak);
						
						//model.setPeakAsBase();
						view.setPeakAsBase();
						
						view.setBaseTrackStatusLbl("Base Track: "+model.getCurrentBaseTrack()+ ", "+model.getNumBaseRegions()+" Features Loaded.");
						view.setCurrentStatusLbl("Base Track set to Feature.");
					}else{
						view.setGenomeAsBase();
					}
				}
			
			}
		};
		
		
	}
	
	
	private static MouseAdapter getFilterTableMouseListener(){
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				
				if(e.getClickCount() == 2){
					print("filter table double-clicked");
					int tableRow = view.getFilterTable().getSelectedRow();
					int modelRow = view.getFilterTable().convertRowIndexToModel(tableRow);
					
					String name = (String)filterTableModel.getValueAt(modelRow, 0);
					
					FilterInterface filter = model.getFilter(name);
					
					print("'" + filter.getName() + "' selected for editing.");
					
					EditFilterDialog editor = new EditFilterDialog(view,model,log,filter);
					
					editor.setVisible(true);
					
					FilterInterface returnFilter = editor.getFilter();
					
					if(returnFilter != null){
						model.updateFilter(filter, returnFilter);
						filterTableModel.fireTableDataChanged();
					}else{
						print("filter null");
					}
				}
			}
		};
	}
	
	
	private static MouseAdapter getFeatureTableMouseListener(){
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				
				if(e.getClickCount() == 2){
					print("feature table double-clicked");
					int tableRow = view.getFeatureTable().getSelectedRow();
					int modelRow = view.getFeatureTable().convertRowIndexToModel(tableRow);
					String name = (String)featureTableModel.getValueAt(modelRow, 0);
					
					PeakDatabase feature = model.getFeature(name);
					
					EditTrackInfoDialog editDialog = new EditTrackInfoDialog(view,log,model,feature);
					editDialog.setVisible(true);
					
					//update gui for changes
					featureTableModel.fireTableDataChanged();
				}
			}
		};
	}
	
	
	/**
	 *  //TODO Utility
	 */
	
	private static void print(String msg){
		System.out.println("Controller: "+msg);
		log.addCommand("Controller: "+msg);
	}
	
	
	private static void updateGenomeParsers(){
		Vector<String> genomeParsers = model.getGenomeParserFactroy().getKeys();
		view.setGenomeParserCBox(new DefaultComboBoxModel<String>(genomeParsers));
	}
	
	private static void updateTrackParsers(){
		Vector<String> trackParsers = model.getTrackParserFactroy().getKeys();
		view.setTrackParserCBox(new DefaultComboBoxModel<String>(trackParsers));
	}
	
	
	
	private static class SimpleTimer{
		private static long startTime;
		
		static void start(){
			startTime = System.nanoTime();
		}
		
		static double getElapsedTime(){
			return (System.nanoTime() - startTime)/1000000000.0;
		}
		
		static String getTimeString(){
			return String.format("%.5g%n",getElapsedTime());
		}
	}
}

