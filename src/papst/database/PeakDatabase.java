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
package papst.database;

import java.util.HashMap;
import java.util.Vector;

import papst.NCList;
import papst.regions.Region;
import papst.regions.Peak;

public class PeakDatabase implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private HashMap<String,NCList> peakDB;
	
	private int numRegions;
	
	private int totalTags;
	
	private boolean isNormalized;
	
	public PeakDatabase(HashMap<String, Vector<Region> > peakChromMap,String name){
		this.name = name;
		
		numRegions = 0;
		
		totalTags = 0;
		
		peakDB = new HashMap<String,NCList>();
		
		for(String key:peakChromMap.keySet()){
			numRegions += peakChromMap.get(key).size();
			peakDB.put(key, new NCList(peakChromMap.get(key)));
		}
		
		isNormalized = false;

	}
	
	public Vector<Region> getRegions(){
		
		Vector<Region> regions = new Vector<Region>();
		
		for(String key: peakDB.keySet()){
			regions.addAll(peakDB.get(key).getRegions());
		}
	
		return regions;
	}
	
	public int getNumRegions(){
		return this.numRegions;
	}
	
	public int getTotalTags(){
		return this.totalTags;
	}
	
	public void setTotalTags(int tagCount){
		this.totalTags = tagCount;
	}
	
	public String toString(){
		return name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	
	public boolean containsKey(String chrom){
		return peakDB.containsKey(chrom);
	}
	
	
	public NCList getPeakList(String chrom){
		if(peakDB.containsKey(chrom)){
			return peakDB.get(chrom);
		}else{
			return null;
		}
	}
	
	public void normalize(double normFactor){
		
		for(Region p:getRegions()){
			Peak peak = (Peak)p;
			peak.normalize(normFactor, totalTags);
		}
		isNormalized = true;
	}
	
	public void undoNormalize(){
		for(Region p:getRegions()){
			Peak peak = (Peak)p;
			peak.undoNormalize();
		}
		isNormalized = false;
	}
	
	public boolean isNormalized(){
		return isNormalized;
	}
	
	public NCList getNCList(String key){
		if(peakDB.containsKey(key)){
			return peakDB.get(key);
		}else{
			return null;
		}
	}
	
	public Vector<Region> getOverlappingRegions(Peak peak){
		if(peakDB.containsKey(peak.chrom)){
			Vector<Region> overlaps = peakDB.get(peak.chrom).getRegionsInRange(peak);
			return overlaps;
		}else{
			return new Vector<Region>();
		}
	}
	
	public Vector<Region> getOverlappingRegions(String chrom,long coordinate){
		if(peakDB.containsKey(chrom)){
			Vector<Region> overlaps = peakDB.get(chrom).getRegionsInRange(coordinate,coordinate);
			return overlaps;
		}else{
			return new Vector<Region>();
		}
	}

}
