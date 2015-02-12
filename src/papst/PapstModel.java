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

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import papst.database.*;
import papst.filters.*;
import papst.models.*;
import papst.parsers.*;
import papst.regions.*;
import papst.utility.*;
import papst.workers.*;

public class PapstModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final int REFGENE_BASE = 0;
	public static final int FEATURE_BASE = 1;
	
	//public CommandLog log;
	
	private String baseFeatureName;
	private int baseStatus;
	
	private GeneDatabase geneDB;
	
	private HashMap<String,PeakDatabase> featureMap;
	private HashMap<String,FilterInterface> filterMap;
	
	private HashMap<String,Boolean> featureEnabledMap;
	private HashMap<String,Boolean> filterEnabledMap;
	
	//private FeatureTableModel featureTableModel;
	//private FilterTableModel filterTableModel;
	
	private Vector<ResultData> resultTableData;
	
	private double currNormalizationFactor = 1000000;
	
	private GenomeParserFactory genomeParserFactory;
	private String defaultGenomeParser;
	
	private TrackParserFactory trackParserFactory;
	private String defaultTrackParser;
	
	private boolean showIsoforms;
	
	private int defaultUpstream;
	private int defaultDownstream;
	private long overlapFactor;
	
	
	
	/**
	 * Default constructor
	 */
	public PapstModel(CommandLog log){
		//this.log = log;
		baseStatus = REFGENE_BASE;
		geneDB = null;
		
		defaultUpstream = 2000;
		defaultDownstream = 200;
		
		overlapFactor = 1;
		
		showIsoforms = true;
		
		featureMap = new HashMap<String,PeakDatabase>();
		filterMap  = new HashMap<String, FilterInterface>();
		
		featureEnabledMap = new HashMap<String,Boolean>();
		filterEnabledMap = new HashMap<String,Boolean>();
		
		resultTableData = new Vector<ResultData>();
		
		defaultTrackParser  = "simple_tab";
		defaultGenomeParser = "refseq_subset";
		
		initGenomeParsers();
		initTrackParsers();
		
	}
	
	/**
	 * initGenomeParsers()
	 *  initializes genome parsers
	 */
	public void initGenomeParsers(){
		genomeParserFactory = new GenomeParserFactory();
		//populate with parsers
		genomeParserFactory.addParser("refseq", new RefseqGenomeParser());
		genomeParserFactory.addParser("refseq_subset", new RefseqSubsetGenomeParser());
	}
	
	public boolean addGenomeParser(String name,GenomeParserInterface genomeParser){
		if(genomeParserFactory.hasParser(name)){
			return false;
		}else{
			genomeParserFactory.addParser(name, genomeParser);
			return true;
		}
	}
	
	public String getDefaultGenomeParser(){
		return defaultGenomeParser;
	}
	
	public void setDefaultGenomeParser(String parser){
		if(genomeParserFactory.hasParser(parser)){
			defaultGenomeParser = parser;
		}
	}
	
	public String getDefaultTrackParser(){
		return defaultTrackParser;
	}
	
	public void setDefaultTrackParser(String parser){
		if(trackParserFactory.hasParser(parser)){
			defaultTrackParser = parser;
		}
	}
	
	
	
	/**
	 * initTrackParsers()
	 *  Initialize track parsers
	 */
	public void initTrackParsers(){
		trackParserFactory = new TrackParserFactory();
		//populate with parsers
		trackParserFactory.addParser("simple_tab", new SimpleBedPeakTrackParser());
		trackParserFactory.addParser("simple_space", new SimpleBedSpaceTrackParser());
		trackParserFactory.addParser("macs_1.4_xls",new Macs14Parser());
		trackParserFactory.addParser("PAPST_alt-base_csv",new PapstResultCSVTrackParser());
		//trackParserFactory.addParser("macs_2_bed_foldchange", new MacsNarrowPeakTrackParser(MacsNarrowPeakTrackParser.FOLD_CHANGE));
		//trackParserFactory.addParser("macs_2_bed_p_value", new MacsNarrowPeakTrackParser(MacsNarrowPeakTrackParser.P_VALUE_IC));
		//trackParserFactory.addParser("macs_2_bed_q_value", new MacsNarrowPeakTrackParser(MacsNarrowPeakTrackParser.Q_VALUE_IC));
	}
	
	public boolean addTrackParser(String name,TrackParserInterface genomeParser){
		if(trackParserFactory.hasParser(name)){
			return false;
		}else{
			trackParserFactory.addParser(name, genomeParser);
			return true;
		}
	}
	
	/**
	 * getGenomeParserFactroy()
	 */
	public GenomeParserFactory getGenomeParserFactroy(){
		return genomeParserFactory;
	}//end method, getGenomeParserFactroy
	
	/**
	 * getTrackParserFactroy()
	 */
	public TrackParserFactory getTrackParserFactroy(){
		return trackParserFactory;
	}//end method, getTrackParserFactroy
	
	
	
	//Settings data
	public boolean isShowIsoforms(){
		return this.showIsoforms;
	}
	
	public void setShowIsoforms(boolean isShow){
		
		this.showIsoforms = isShow;
		print("show iso? "+this.showIsoforms);
	}
	
	
	public void setDefaultUpstream(int upstream){
		this.defaultUpstream = upstream;
	}
	
	public void setDefaultDownstream(int downstream){
		this.defaultDownstream = downstream;
	}
	
	public int getDefaultUpstream(){
		return this.defaultUpstream;
	}
	
	public int getDefaultDownstream(){
		return this.defaultDownstream;
	}
	
	
	
	/**
	 * getNumResultTables()
	 */
 	public int getNumResultTables(){
 		return resultTableData.size();
 		//return resultTableModels.size();
 	}//end method, getTrackParserFactroy
 	
 	/**
 	 * dropResultTable
 	 * @param index
 	 */
 	public void dropResultTable(int index){
 		if(index >= 0 && index < resultTableData.size()){
 			resultTableData.remove(index);
 		}
 	}
 	
 	/**
 	 * getResultTableModels
 	 * @return
 	 */
 	public Vector<ResultData> getResultTables(){
 		return resultTableData;
 	}
 	
 	public ResultData getResultTable(int index){
 		return resultTableData.elementAt(index);
 	}
	
	/**
	 * getBaseStatus
	 * 
	 * Returns the int status of baseStatus
	 *  One of the static finaly constats defined in Model.
	 */
	public int getBaseStatus(){
		return baseStatus;
	}
	
	
	/**
	 * isGeneDatabaseLoaded
	 *   Returns true if a gene database is loaded.
	 */
	public boolean isGeneDatabaseLoaded(){
		return geneDB != null;
	}
	
	/**
	 * getCurrentBaseTrack
	 *  Returns the string name of the current base track
	 *  Tests baseStatus.
	 */
	public String getCurrentBaseTrack(){
		if(baseStatus == REFGENE_BASE && isGeneDatabaseLoaded()){
			return geneDB.toString();
		}else if(baseStatus == REFGENE_BASE && !isGeneDatabaseLoaded()){
			return "None";
		}else if(baseStatus == FEATURE_BASE){
			return baseFeatureName;
		}else{
			return "None";
		}
	}
	
	
	/**
	 * Check if a base track has been loaded or not
	 */
	public boolean isBaseTrackLoaded(){
		if(getCurrentBaseTrack().compareTo("None") == 0){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * Get the number of features loaded
	 */
	public int getNumFeatures(){
		return featureMap.size();
	}
	
	/**
	 * Get the number of filters
	 */
	public int getNumFilters(){
		return filterMap.size();
	}
	
	
	/**
	 * getNumBaseRegions
	 *  Gets the number of regions in the current base track.
	 */
	public int getNumBaseRegions(){
		if(baseStatus == REFGENE_BASE){
			return geneDB.getNumGenes();
		}else{
			return featureMap.get(baseFeatureName).getNumRegions();
		}
	}
	
	/**
	 * setGeneDatabase(GeneDatabase)
	 *  Sets the current gene database to the passed argument.
	 */
	public void setGeneDatabase(GeneDatabase geneDB){
		if(geneDB != null){
			this.geneDB = geneDB;
		}
	}
	
	
	/**
	 * print(String), a logging/testing tool. Outputs a message with
	 *  the prefix 'model' to know where the message originated.
	 */
	public void print(String msg){
		System.out.println("Model: "+msg);
		//log.addCommand("Model: "+msg);
	}
	
	/**
	 * setGenomeAsBase()
	 * Updates base status provided the gene database is not null.
	 */
	public void setGenomeAsBase(){
		if(geneDB != null){
			baseStatus = REFGENE_BASE;
		}
	}
	
	/**
	 * setPeakAsBase(String), sets the current base track
	 *  to be the track represented by the given string key.
	 *  Updates baseStatus
	 */
	public void setPeakAsBase(String key){
		if(featureMap.containsKey(key)){
			baseFeatureName = key;
			baseStatus = FEATURE_BASE;
		}
	}
	
	public void enableFeature(String key){
		featureEnabledMap.put(key, new Boolean(true));
	}
	
	public void enableFilter(String key){
		filterEnabledMap.put(key, new Boolean(true));
	}
	
	/**
	 * addPeakdData, adds a PeakDatabase object to the model
	 *  putting the key value pair into the featureMap HashMap
	 */
	public void addPeakData(PeakDatabase peakDB){
		featureMap.put(peakDB.toString(), peakDB);
		enableFeature(peakDB.toString());
	}
	
	
	/**
	 * addPeakdFilter, adds a Filter object to the model
	 *  putting the key value pair into the featureMap HashMap
	 */
	public void addFilterData(FilterInterface inFilter){
		filterMap.put(inFilter.getName(),inFilter);
		enableFilter(inFilter.getName());
	}
	
	public void updateFilter(FilterInterface originalFilter,FilterInterface newFilter){
		if(originalFilter.getName().compareTo(newFilter.getName()) == 0){
			//overwrite the old filter, names are the same
			filterMap.put(newFilter.getName(),newFilter);
			enableFilter(newFilter.getName());
		}else{
			
			//remove old filter
			filterMap.remove(originalFilter.getName());
			filterEnabledMap.remove(originalFilter.getName());
			
			//add new filter
			filterMap.put(newFilter.getName(),newFilter);
			enableFilter(newFilter.getName());
		}
	}//end method, updateFilter
	
	/**
	 *  getFeatureNames() returns an array of strings
	 *  representing the names of peaks loaded into the model
	 * 
	 */
	public Vector<String> getFeatureNames(){
		
		Vector<String> names = new Vector<String>();
		for(String str:featureMap.keySet()){
			names.add(str);
		}
		
		return names;
	}
	
	public Vector<String> getFilterNames(){
		
		Vector<String> names = new Vector<String>();
		for(String str:filterMap.keySet()){
			names.add(str);
		}
		
		return names;
	}
	
	public Vector<FilterInterface> getAvailableFilterObjects(){
		Vector<FilterInterface> filters = new Vector<FilterInterface>();
		for(String key: getAvailableFilters()){
			filters.add(filterMap.get(key));
		}
		return filters;
	}
	
	

	/*
	public void updateFilterTable(){
		filterTableModel.fireTableDataChanged();
	}*/
	
	public boolean dropTrack(String name, StringBuffer msg){
		
		if(featureMap.containsKey(name)){
			
			if(name.compareTo(getCurrentBaseTrack()) == 0){
				msg.append("The track '"+name+"' cannot be deleted. It is the current base track.");
				return false;
			}else{
				msg.append("The track '"+name+"' has been removed.");
				//remove form the database
				featureMap.remove(name);
				
				//remove from enabled map
				if(featureEnabledMap.containsKey(name)){
					featureEnabledMap.remove(name);
				}
				
				return true;
			}
		}else{
			msg.append("The track '"+name+"' does not exist");
			return false;
		}
	}
	
	public boolean dropFilter(String name, StringBuffer msg){
		
		if(filterMap.containsKey(name)){
			
			
			msg.append("The Filter '"+name+"' has been removed.");
			//remove form the database
			filterMap.remove(name);
			
			//remove from enabled map
			if(filterEnabledMap.containsKey(name)){
				filterEnabledMap.remove(name);
			}
			
			
			//return true
			return true;
				
		}else{
			msg.append("The filter '"+name+"' does not exist");
			return false;
		}
	}
	
	/**
	 * Select all features
	 */
	public void selectAllFeatures(){
		for(String key: featureEnabledMap.keySet()){
			featureEnabledMap.put(key, new Boolean(true));
		}
	}
	
	/**
	 * Deselect all features
	 */
	public void deselectAllFeatures(){
		for(String key: featureEnabledMap.keySet()){
			featureEnabledMap.put(key, new Boolean(false));
		}
	}
	
	public void selectAllFilters(){
		for(String key: filterEnabledMap.keySet()){
			filterEnabledMap.put(key, new Boolean(true));
		}
	}
	
	public void deselectAllFilters(){
		for(String key: filterEnabledMap.keySet()){
			filterEnabledMap.put(key, new Boolean(false));
		}
	}
	
	/**
	 * Get the keys for each enabled feature
	 */
	public Vector<String> getAvailableFeatures(){
		Vector<String> availableFeatures = new Vector<String>();
		
		for(String s: featureEnabledMap.keySet()){
			if((Boolean)featureEnabledMap.get(s)){
				availableFeatures.add(s);
			}
		}
		
		return availableFeatures;
	}
	
	
	
	
	public Vector<String> getAvailableFilters(){
		Vector<String> availableFilters = new Vector<String>();
		
		for(String s: filterEnabledMap.keySet()){
			if((Boolean)filterEnabledMap.get(s)){
				availableFilters.add(s);
			}
		}
		return availableFilters;
	}
	
	public boolean isFilterNameUnique(String name){
		return !filterEnabledMap.containsKey(name);
	}
	
	public boolean isFeatureNameUnique(String name){
		return !featureEnabledMap.containsKey(name);
	}
	
	public int getNumEnabledFeatures(){
		return getAvailableFeatures().size();
	}
	
	public int getNumEnabledFilters(){
		return getAvailableFilters().size();
	}
	
	
	
	/**
	 * 
	 * TODO  Search functionality
	 * 
	 * */
	public ResultData searchAndGetTable(){
		print("Search started");
		
		if(baseStatus == REFGENE_BASE){
			return getResultTableGenomeBase();
		}else{
			return getResultTableFeatureBase();
		}
	}
	
	/**
	 * 
	 *   Search functionality (Genome based)
	 * 
	 * */
	public ResultData getResultTableGenomeBase(){
		print("Genome base search");
		Vector<String> features = getAvailableFeatures();
		Collections.sort(features,NumberAwareStringComparator.INSTANCE);
		Vector<String> filters = getAvailableFilters();
		
		print(filterMap.get(filters.elementAt(0)).toString());
		
		//GENES
		Vector<Region> genes = geneDB.getGenes();
		
		//table data table of objects
		Vector<Vector<Object> > tableData  = new Vector<Vector<Object> >();
		
		Vector<String> columnLabels = new Vector<String>();
		
		columnLabels.add("Gene Name");
		columnLabels.add("Accession");
		columnLabels.add("Location");
		
		//add column headers
		for(String feature: features){
			columnLabels.add(feature);
		}
		
		HashMap<String,Boolean> uniqueHash = null;
		
		////////////////////////////
		//  MAIN LOOP: Each Gene
		////////////////////////////
		for(Region inRegion: genes){
			
			Gene gene = (Gene)inRegion;
			
			if(!showIsoforms){
				
				if(uniqueHash == null){
					uniqueHash = new HashMap<String, Boolean>();
				}
				
				if(!uniqueHash.containsKey(gene.getName())){
					uniqueHash.put(gene.getName(), new Boolean(true));
				}else{
					continue;
				}
			}

			
			boolean doesPass = true;
			
			HashMap<String,Double> trackValueMap = new HashMap<String, Double>();
			for(String feature: features){
				trackValueMap.put(feature, new Double(0.0));
			}
			
			
			////////////////////////////
			// FILTER CONSTRAINTS
			////////////////////////////
			for(String filterKey: filters){
				
				FilterInterface filter = filterMap.get(filterKey);
				
				//print(filter.toString());
			
				if(filter.doesSupportTracks(features)){
					
					HashMap<String,Double> filterTrackValue = filter.evaluateFilter(gene, featureMap, gene.chrom);
					
					
					if(filterTrackValue != null){
						
						for(String key:filterTrackValue.keySet()){
							//print(gene.toString());
							//print(key+" "+filterTrackValue.get(key));
							if(filterTrackValue.get(key) > trackValueMap.get(key)){
								trackValueMap.put(key, filterTrackValue.get(key));
							}
						}
					}else{
						doesPass = false;
					}
					
				}else{
					doesPass = false;
				}
				
			}///// FILTERS ////////////
			
			
			if(doesPass){
				Vector<Object> rowVector = new Vector<Object>();
				
				rowVector.add(gene.name);
				rowVector.add(gene.accession);
				rowVector.add(gene.getLocationString());
				
				for(String feature:features){
					rowVector.add(trackValueMap.get(feature));
				}
				
				tableData.add(rowVector);
			}
				
			
		}///////// GENES ////////////
	
		
		
		StringBuffer sb = new StringBuffer();
		sb.append(tableData.size()+" elements returned: ");
		for(String s: filters){
			sb.append("("+s+") ");
		}
		String filtersUsed = sb.toString();
		
		ResultData data = new ResultData(columnLabels,tableData,filtersUsed,"Search",ResultData.SEARCH_RESULT);
		resultTableData.add(data);
		
		//TODO
		//SearchResultTableModel currTable = new SearchResultTableModel(tableData,columnLabels);
		//currTable.setResultNote(filtersUsed);
		//resultTableModels.add(currTable);
		
		
		return resultTableData.lastElement();
	}
	
	
	/**
	 * 
	 * Search functionality (Region based)
	 * 
	 * */
	public ResultData getResultTableFeatureBase(){
		print("Feature base search");
		Vector<String> features = getAvailableFeatures();
		Collections.sort(features,NumberAwareStringComparator.INSTANCE);
		Vector<String> filters = getAvailableFilters();
		
		//Regions
		Vector<Region> regions = featureMap.get(getCurrentBaseTrack()).getRegions();
		
		//table data table of objects
		Vector<Vector<Object> > tableData  = new Vector<Vector<Object> >();
		
		Vector<String> columnLabels = new Vector<String>();
		
		columnLabels.add("Number");
		columnLabels.add("Region Name");
		columnLabels.add("Location");
		
		//add column headers
		for(String feature: features){
			columnLabels.add(feature);
		}
		
		////////////////////////////
		//  MAIN LOOP: Each Region
		////////////////////////////
		int regionNum = 0;
		for(Region inRegion: regions){

			boolean doesPass = true;
			
			Peak region = (Peak)inRegion;
			
			HashMap<String,Double> trackValueMap = new HashMap<String, Double>();
			for(String feature: features){
				trackValueMap.put(feature, new Double(0.0));
			}
			
			
			////////////////////////////
			// FILTER CONSTRAINTS
			////////////////////////////
			for(String filterKey: filters){
				
				FilterInterface filter = filterMap.get(filterKey);
				
				//print(filter.toString());
			
				if(filter.doesSupportTracks(features)){
					
					HashMap<String,Double> filterTrackValue = filter.evaluateFilter(region, featureMap, region.chrom);
					
					if(filterTrackValue != null){
						for(String key:filterTrackValue.keySet()){
							//print(region.toString());
							//print(key+" "+filterTrackValue.get(key));
							if(filterTrackValue.get(key) > trackValueMap.get(key)){
								trackValueMap.put(key, filterTrackValue.get(key));
							}
						}
					}else{
						doesPass = false;
					}
					
				}else{
					doesPass = false;
				}
				
			}///// FILTERS ////////////
			
			
			if(doesPass){
				Vector<Object> rowVector = new Vector<Object>();
				
				Integer safeInt = new Integer(regionNum);
				
				rowVector.add(safeInt.toString());
				rowVector.add(getCurrentBaseTrack());
				rowVector.add(inRegion.getLocationString());
				
				for(String feature:features){
					rowVector.add(trackValueMap.get(feature));
				}
				
				tableData.add(rowVector);
			}
				
			++regionNum;
		}///////// Region ////////////
		
		//TODO result table, feature base
		StringBuffer sb = new StringBuffer();
		sb.append(tableData.size()+" elements returned: ");
		for(String s: filters){
			sb.append("("+s+") ");
		}
		String filtersUsed = sb.toString();
		
		ResultData data = new ResultData(columnLabels,tableData,filtersUsed,"Search",ResultData.SEARCH_RESULT);
		resultTableData.add(data);
		
		//TODO
		//SearchResultTableModel currTable = new SearchResultTableModel(tableData, columnLabels);
		//currTable.setResultNote(filtersUsed);
		//resultTableModels.add(currTable);
		
		return resultTableData.lastElement();
	}
	
	public void normalize(HashMap<String, Integer> tagCountMap){
		
		for(String key: tagCountMap.keySet()){
			
			if(featureMap.containsKey(key)){
				PeakDatabase peaks = featureMap.get(key);
				print("normalizing "+peaks.toString());
				
				peaks.setTotalTags(tagCountMap.get(key));
				peaks.normalize(currNormalizationFactor);
			}
			
		}
	}
	
	public void undoNormalize(Vector<String> names){
		
		for(String key:names){
			PeakDatabase peaks = featureMap.get(key);
			print("undoing normalization for "+peaks.toString());
			
			peaks.undoNormalize();
		}
	}
	
	public void setNormFactor(double normFactor){
		currNormalizationFactor = normFactor;
	}
	
	public double getNormFactor(){
		return currNormalizationFactor;
	}
	
	public void setOverlapFactor(long value){
		overlapFactor = value;
	}
	
	public long getOverlapFactor(){
		return overlapFactor;
	}
	
	public void removeEnabledKey(String name){
		if(featureEnabledMap.containsKey(name)){
			featureEnabledMap.remove(name);
		}
	}
	
	public void setFeatureEnabled(String name,Boolean value){
		featureEnabledMap.put(name, value);
	}
	
	public void setFilterEnabled(String name,Boolean value){
		filterEnabledMap.put(name, value);
	}
	
	public Boolean isFeatureEnabled(String name){
		return featureEnabledMap.get(name);
	}
	
	public Boolean isFilterEnabled(String name){
		return filterEnabledMap.get(name);
	}
	
	public void renameFeature(String oldName,String newName){
		
		PeakDatabase feature = featureMap.get(oldName);
		feature.setName(newName);
		featureMap.remove(oldName);
		featureMap.put(newName, feature);
		
		removeEnabledKey(oldName);
		enableFeature(newName);
		
		for(String filter: getFilterNames()){
			filterMap.get(filter).updateName(oldName, newName);
		}
	}
	
	
	public PeakDatabase getFeature(String name){
		if(featureMap.containsKey(name)){
			return featureMap.get(name);
		}else{
			return null;
		}
	}
	
	public FilterInterface getFilter(String name){
		if(filterMap.containsKey(name)){
			return filterMap.get(name);
		}else{
			return null;
		}
	}
	
	
	public ResultData getAssignResultTable(String feature){
		print("Start assing single");
		
		GeneAssignWorker worker = new GeneAssignWorker(featureMap.get(feature), geneDB);
		worker.execute();
		try {
			//AbstractResultTableModel table =  worker.get();
			//table.setResultNote("Assign -- Track: "+feature+" to Genome: "+geneDB.getName());
			ResultData data = worker.get();
			data.setNote("Assign -- Track: "+feature+" to Genome: "+geneDB.getName());
			
			//resultTableModels.add(table);
			resultTableData.add(data);
			
			return resultTableData.lastElement();
		} catch (InterruptedException e) {
			return null;
		} catch (ExecutionException e) {
			return null;
		}
	}
	
	//TODO remove jframe reference
	public void assignAllAvailable(File file){
		print("start assign all");
		Vector<String> availableFeatures = getAvailableFeatures();
		
		//Start workers to extract data
		for(String key: availableFeatures){
			PeakDatabase peakSet = featureMap.get(key);
			GeneAssignWriteWorker worker = new GeneAssignWriteWorker(peakSet,geneDB,new File(file,key+".assign"));
			worker.execute();
		}
	}
	
	
	public ResultData getSummaryResultTable(){
		print("Start summary");
		
		Vector<String> features = getAvailableFeatures();
		Collections.sort(features,NumberAwareStringComparator.INSTANCE);
		
		Vector<SummaryWorker> workers = new Vector<SummaryWorker>();
		
		for(String feature:features){
			SummaryWorker worker = new SummaryWorker(featureMap.get(feature),geneDB,getDefaultUpstream());
			worker.execute();
			workers.add(worker);
		}
		
		Vector<Vector<Object>> tableData = new Vector<Vector<Object>>();
		Vector<String> columnLabels = new Vector<String>();
		
		columnLabels.add("Feature");
		columnLabels.add("Promoter");
		columnLabels.add("Promoter_%");
		columnLabels.add("Gene_body");
		columnLabels.add("Gene_body_%");
		columnLabels.add("Exon");
		columnLabels.add("Exon_%");
		columnLabels.add("Intron");
		columnLabels.add("Intron_%");
		columnLabels.add("Intergenic");
		columnLabels.add("Intergenic_%");
		
		for(SummaryWorker worker:workers){
			try {
				
				tableData.add(worker.get());
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		ResultData data = new ResultData(columnLabels,tableData,
				"Summary with "+getDefaultUpstream()+"bp upstream promoter","Summary",ResultData.SUMMARY_RESULT);
		resultTableData.add(data);
		
		//TODO
		//AbstractResultTableModel tableModel = new SummaryResultTableModel(tableData, columnLabels);
		//tableModel.setResultNote("Summary with "+getDefaultUpstream()+"bp upstream promoter");
		//resultTableModels.add(tableModel);
		
		return resultTableData.lastElement();
	}
	
	
	public ResultData getComparisonResultTable(){
		print("Start comparison");
		
		Vector<String> features = getAvailableFeatures();
		Collections.sort(features,NumberAwareStringComparator.INSTANCE);
		
		Vector<PeakDatabase> peakSets = new Vector<PeakDatabase>();
		for(String feature:features){
			peakSets.add(featureMap.get(feature));
		}
		
		CompareSymmetricWorker worker = new CompareSymmetricWorker(peakSets,getOverlapFactor());
		//CompareWorker worker = new CompareWorker(peakSets);
		worker.execute();
		
		
		try{
			
			//AbstractResultTableModel tableModel = worker.get();
			//resultTableModels.add(tableModel);
			ResultData data = worker.get();
			resultTableData.add(data);
			
			return resultTableData.lastElement();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	

	
	
	/********************************************************
	 * //TODO end of methods 
	 ********************************************************
	 */

}//end class


