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

public class SingleFeatureFilter extends AbstractFilter implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String track;
	private String name;
	private double threshold;
	private int relationship;
	
	private RegionModifier modifier;
	
	public SingleFeatureFilter(String name,String track,double threshold,int relationship,RegionModifier modifier){
		this.name         = name;
		this.track        = track;
		this.threshold    = threshold;
		this.relationship = relationship;
		
		this.modifier = modifier;
	}//end constructor
	
	public SingleFeatureFilter(){}

	
	@Override
	public HashMap<String,Double> evaluateFilter(Region inRegion, HashMap<String,PeakDatabase> featureMap, String dbKey){
		if(!featureMap.containsKey(track)){
			return null;
		}else if(!featureMap.get(track).containsKey(dbKey)){
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
		
		if(doesRelationshipHold(valueTotal,threshold,relationship)){
			HashMap<String,Double> valueMap = new HashMap<String, Double>();
			valueMap.put(track, valueTotal);
			return valueMap;
		}else{
			return null;
		}
		
	}//end interface method, evaluateFilter



	@Override
	public HashMap<String,Double> evaluateFilter(Gene gene, HashMap<String,PeakDatabase> featureMap, String dbKey) {
		
		if(!featureMap.containsKey(track)){
			return null;
		}else if(!featureMap.get(track).containsKey(dbKey)){
			return null;
		}
		
		//get the peak data for this filter
		PeakDatabase peakDB = featureMap.get(track);
		
		//Get the augmented regions of the feature
		Vector<Region> regions = modifier.getTestRegions(gene);
		
		double valueTotal = 0;
		
		//for each region (promoter, body, and downstream)
		for(Region region: regions){
			//System.out.println("region "+region.getStart() + " " +region.getEnd());
			//System.out.println(gene.toString());
			
			Vector<Region> found = peakDB.getPeakList(dbKey).getRegionsInRange(region);
			
			if(found != null && found.size() > 0){
				
				for(Region r: found){
					//System.out.println(r.toString());
					//TODO add value
					valueTotal += r.getValue();
				}
			}//if any overlaps
			
		}//for each region (promoter, body, downstream)
		
		if(doesRelationshipHold(valueTotal,threshold,relationship)){
			HashMap<String,Double> valueMap = new HashMap<String, Double>();
			valueMap.put(track, valueTotal);
			return valueMap;
		}else{
			return null;
		}
	}//end interface method, 
	
	@Override
	public boolean doesSupportTrack(String queryTrack) {
		if(track != null && track.compareTo(queryTrack) == 0){
			return true;
		}else{
			return false;
		}
	}//end interface method, doesSupportTrack

	@Override
	public boolean isMultiTrack() {
		return false;
	}
	
	public String getTrack(){
		return this.track;
	}
	
	@Override
	public double getThreshold(){
		return this.threshold;
	}
	
	
	
	public String toString(){
		
		if(modifier == null){
			return "Filter fake filter.";
		}else{
			return "Filter '"+name+"' chooses regions where " +track+" is "+AbstractFilter.RelationStrings[relationship]+" "+threshold+" in "+modifier.toString();
		}
	}
	
	@Override
	public String getName(){
		return name;
	}

	@Override
	public boolean doesSupportTracks(Vector<String> availableTracks) {
		for(String inTrack: availableTracks){
			if(inTrack.compareTo(track) == 0){
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
		if(this.track.compareTo(oldName) == 0){
			this.track = newName;
		}
	}

}
