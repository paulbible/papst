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
package papst.filters;

import java.util.HashMap;
import java.util.Vector;

import papst.regions.*;
import papst.database.*;


public class TwoFeatureFilter extends AbstractFilter implements FilterInterface,java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	private String name;
	
	private Vector<String> trackList;
	
	private int operation;
	private int relationship;
	
	private double threshold;
	
	private RegionModifier modifier;
	
	
	public TwoFeatureFilter(String name,String trackA, String trackB, int operation,int relationship,double threshold,RegionModifier modifier){
		
		this.name = name;
		
		trackList = new Vector<String>();
		
		trackList.add(trackA);
		trackList.add(trackB);
		
		this.operation = operation;
		this.relationship = relationship;
		
		this.threshold = threshold;
		
		this.modifier = modifier;
		
	}
	
	public int getOperation(){
		return this.operation;
	}
	
	@Override
	public double getThreshold(){
		return this.threshold;
	}
	
	public String getTrackA(){
		return trackList.elementAt(0);
	}
	
	public String getTrackB(){
		return trackList.elementAt(1);
	}

	@Override
	public HashMap<String,Double> evaluateFilter(Region inRegion, HashMap<String,PeakDatabase> featureMap, String dbKey) {
		// TODO region evaluation
		//if the database does not contain the tracks, it fails
		if(!featureMap.containsKey(getTrackA()) || !featureMap.containsKey(getTrackB())){
			return null;
			
		}else if(!featureMap.get(getTrackA()).containsKey(dbKey) || !featureMap.get(getTrackB()).containsKey(dbKey)){
			return null;
		}
		
		
		//get the peak data for this filter
		PeakDatabase peakDB_A = featureMap.get(getTrackA());
		PeakDatabase peakDB_B = featureMap.get(getTrackB());
		
		//Get the augmented regions of the feature
		Vector<Region> regions = modifier.getTestRegions(inRegion);
		
		double valueTotalA = 0;
		double valueTotalB = 0;
		
		//for each region (promoter, body, and downstream)
		for(Region region: regions){
			
			Vector<Region> foundA = peakDB_A.getPeakList(dbKey).getRegionsInRange(region);
			Vector<Region> foundB = peakDB_B.getPeakList(dbKey).getRegionsInRange(region);
			
			//add A values
			if(foundA != null && foundA.size() > 0){
				for(Region r: foundA){
					valueTotalA += r.getValue();
				}
			}//if any overlaps
			
			//add B values
			if(foundB != null && foundB.size() > 0){
				for(Region r: foundB){
					valueTotalB += r.getValue();
				}
			}//if any overlaps
		}//end for, each region
		
		double result = operateValues(valueTotalA, valueTotalB, operation);
		
		if(doesRelationshipHold(result, threshold, relationship)){
			HashMap<String, Double> valueMap = new HashMap<String, Double>();
			valueMap.put(getTrackA(), valueTotalA);
			valueMap.put(getTrackB(), valueTotalB);
			return valueMap;
		}else{
			return null;
		}
	}//end interface method, evaluateFilter -- regions
	

	@Override
	public HashMap<String,Double> evaluateFilter(Gene gene, HashMap<String,PeakDatabase> featureMap, String dbKey) {
		//TODO gene evaluation
		
		//if the database does not contain the tracks, it fails
		if(!featureMap.containsKey(getTrackA()) || !featureMap.containsKey(getTrackB())){
			return null;
			
		}else if(!featureMap.get(getTrackA()).containsKey(dbKey) || !featureMap.get(getTrackB()).containsKey(dbKey)){
			return null;
		}
		
		
		//get the peak data for this filter
		PeakDatabase peakDB_A = featureMap.get(getTrackA());
		PeakDatabase peakDB_B = featureMap.get(getTrackB());
		
		//Get the augmented regions of the feature
		Vector<Region> regions = modifier.getTestRegions(gene);
		
		double valueTotalA = 0;
		double valueTotalB = 0;
		
		//for each region (promoter, body, and downstream)
		for(Region region: regions){
			
			Vector<Region> foundA = peakDB_A.getPeakList(dbKey).getRegionsInRange(region);
			Vector<Region> foundB = peakDB_B.getPeakList(dbKey).getRegionsInRange(region);
			
			//add A values
			if(foundA != null && foundA.size() > 0){
				for(Region r: foundA){
					valueTotalA += r.getValue();
				}
			}//if any overlaps
			
			//add B values
			if(foundB != null && foundB.size() > 0){
				for(Region r: foundB){
					valueTotalB += r.getValue();
				}
			}//if any overlaps
		}//end for, each region
		
		double result = operateValues(valueTotalA, valueTotalB, operation);
		
		if(doesRelationshipHold(result, threshold, relationship)){
			HashMap<String, Double> valueMap = new HashMap<String, Double>();
			valueMap.put(getTrackA(), valueTotalA);
			valueMap.put(getTrackB(), valueTotalB);
			return valueMap;
		}else{
			return null;
		}
		
	}//end interface method, evaluateFilter -- gene

	@Override
	public boolean doesSupportTrack(String queryTrack) {
		
		for(String track: trackList){
			if(queryTrack.compareTo(track) == 0){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isMultiTrack() {
		return true;
	}
	
	public String toString(){
		if(modifier == null){
			return "Filter fake two feature filter.";
		}else{
			return "2 Feature Filter '"+name+"' chooses regions where " +
					getTrackA()+ " " +AbstractFilter.OperationStrings[operation] +
					" "+ getTrackB()+
					" is "+AbstractFilter.RelationStrings[relationship]+" "+threshold+" in "+modifier.toString();
		}
	}
	
	@Override
	public String getName(){
		return name;
	}


	@Override
	public boolean doesSupportTracks(Vector<String> availableTracks) {
		boolean foundA = false;
		boolean foundB = false;
		
		for(String inTrack: availableTracks){
			if(!foundA && inTrack.compareTo(getTrackA()) == 0){
				foundA = true;
			}
			if(!foundB && inTrack.compareTo(getTrackB()) == 0){
				foundB = true;
			}
			if(foundA && foundB){
				return true;
			}
		}
		return false;
	}

	@Override
	public RegionModifier getModifier() {
		return this.modifier;
	}

	@Override
	public int getRelationship() {
		return this.relationship;
	}

	@Override
	public void updateName(String oldName, String newName) {
		
		for(int i = 0; i < trackList.size(); ++i){
			String track = trackList.get(i);
			if(track.compareTo(oldName) == 0){
				trackList.setElementAt(newName, i);
			}
		}
		
	}

}
