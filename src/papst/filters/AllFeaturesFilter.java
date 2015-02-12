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

import papst.PapstModel;
import papst.regions.*;
import papst.database.*;

public class AllFeaturesFilter extends AbstractFilter implements FilterInterface {
	
	
	private static final long serialVersionUID = 1L;
	
	private String track;
	private String name;
	private double threshold;
	private int relationship;
	
	private PapstModel model;
	
	private RegionModifier modifier;
	
	
	public AllFeaturesFilter(String name,String track,double threshold,int relationship,RegionModifier modifier,PapstModel model){
		this.name         = name;
		this.track        = track;
		this.threshold    = threshold;
		this.relationship = relationship;
		
		this.model = model;
		
		this.modifier = modifier;
	}//end constructor
	

	@Override
	public HashMap<String, Double> evaluateFilter(Region inRegion,
			HashMap<String, PeakDatabase> featureMap, String dbKey) {
		
		boolean passAll = false;
		
		if(getRelationship() == AbstractFilter.GREATER_THAN_EQUAL && getThreshold() == 0){
			passAll = true;
		}
		
		
		boolean isRegionGood = true;
		HashMap<String,Double> valueMap = new HashMap<String, Double>();
		
		Vector<String> tracks = model.getAvailableFeatures();
		
		for(String track:tracks){
			
			//if any feature set is missing the chromosome,
			// the regions fails to pass the filter 
			if(!featureMap.get(track).containsKey(dbKey)){
				//early exit
				isRegionGood = false;
				
				if(passAll){
					valueMap.put(track, 0.0);
					return valueMap;
				}
				return null;
			}
			
			//get the peak data for this filter
			PeakDatabase peakDB = featureMap.get(track);
			
			//Get the augmented regions of the feature
			Vector<Region> regions = modifier.getTestRegions(inRegion);
			
			double valueTotal = 0;
			
			//for each region (promoter, body, and downstream)
			for(Region region: regions){
				
				Vector<Region> found = peakDB.getPeakList(dbKey).getRegionsInRange(region);
				
				if(found != null && found.size() > 0){
					
					for(Region r: found){
						valueTotal += r.getValue();
					}
				}//if any overlaps
				
			}//for each region (promoter, body, downstream)
			
			if(!doesRelationshipHold(valueTotal,threshold,relationship)){
				isRegionGood = false;
				
				if(passAll){
					valueMap.put(track, 0.0);
					return valueMap;
				}
				return null;
			}else{
				valueMap.put(track, valueTotal);
			}
			
		
		}//end for, each track
		
		if(isRegionGood){
			return valueMap;
		}else{
			
			if(passAll){
				valueMap.put(track, 0.0);
				return valueMap;
			}
			return null;
		}
	}//end method, evaluateFilter -- Region

	@Override
	public HashMap<String,Double> evaluateFilter(Gene gene, HashMap<String,PeakDatabase> featureMap, String dbKey) {
		
		boolean passAll = false;
		
		if(getRelationship() == AbstractFilter.GREATER_THAN_EQUAL && getThreshold() == 0){
			passAll = true;
		}
		
		boolean isRegionGood = true;
		HashMap<String,Double> valueMap = new HashMap<String, Double>();
		
		Vector<String> tracks = model.getAvailableFeatures();
		
		for(String track:tracks){
			
			//if any feature set is missing the chromosome,
			// the regions fails to pass the filter 
			if(!featureMap.get(track).containsKey(dbKey)){
				//early exit
				isRegionGood = false;
				
				if(passAll){
					valueMap.put(track, 0.0);
					return valueMap;
				}
				return null;
			}
			
			//get the peak data for this filter
			PeakDatabase peakDB = featureMap.get(track);
			
			//Get the augmented regions of the feature
			Vector<Region> regions = modifier.getTestRegions(gene);
			
			double valueTotal = 0;
			
			//for each region (promoter, body, and downstream)
			for(Region region: regions){
				
				Vector<Region> found = peakDB.getPeakList(dbKey).getRegionsInRange(region);
				
				if(found != null && found.size() > 0){
					
					for(Region r: found){
						valueTotal += r.getValue();
					}
				}//if any overlaps
				
			}//for each region (promoter, body, downstream)
			
			if(!doesRelationshipHold(valueTotal,threshold,relationship)){
				isRegionGood = false;
				
				if(passAll){
					valueMap.put(track, 0.0);
					return valueMap;
				}
				
				return null;
			}else{
				valueMap.put(track, valueTotal);
			}
			
		
		}//end for, each track
		
		if(isRegionGood){
			return valueMap;
		}else{
			if(passAll){
				valueMap.put(track, 0.0);
				return valueMap;
			}
			return null;
		}
		
	}//end interface method, evaluateFilter -- gene

	
	//The All filter supports all tracks
	@Override
	public boolean doesSupportTrack(String queryTrack) {
		return true;
	}

	//The All filter supports all tracks
	@Override
	public boolean doesSupportTracks(Vector<String> availableTracks) {
		return true;
	}

	@Override
	public boolean isMultiTrack() {
		return true;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public double getThreshold() {
		return this.threshold;
	}

	@Override
	public RegionModifier getModifier() {
		return modifier;
	}

	@Override
	public int getRelationship() {
		return this.relationship;
	}
	
	public String toString(){
		
		if(modifier == null){
			return "Filter fake filter.";
		}else{
			return "All Filter '"+name+"' chooses regions where "+track+" tracks are "+AbstractFilter.RelationStrings[relationship]+" "+threshold+" in "+modifier.toString();
		}
	}
	
	//NO action for all filter
	@Override
	public void updateName(String oldName,String newName){}

}
