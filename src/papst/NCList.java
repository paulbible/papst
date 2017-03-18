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

import java.util.Collections;
import java.util.Vector;

import papst.regions.Region;
import papst.regions.SimpleRegion;

public class NCList implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//public functions
	public NCList(Vector<Region> peaks){
		items = new Vector<NCNode>();
		numRegions = peaks.size();
		Collections.sort(peaks);
		
		if( peaks.size() != 0){
			//int addCount = 0;
			Region cRegion = null;
			Vector<Region> tempList = new Vector<Region>();
			for(int i = 0; i < peaks.size(); i ++){
				
				if(cRegion == null){
					cRegion = peaks.get(i);
				}else{
					Region p = peaks.get(i);
					
					if(cRegion.contains(p)){
						//addCount += 1;
						//print(cRegion.toString()+" contains "+p.toString()+"\t\t"+addCount);
						tempList.add(p);
					}else{
						//print(cRegion.toString());
						//print(tempList.toString());
						NCNode n = new NCNode(cRegion,tempList);
						items.add(n);
						cRegion = p;
						tempList = new Vector<Region>();
						//addCount = 0;
					}//else
				}//else
			}//for
			
			//add final one if needed
			if(items.isEmpty()){
				items.add(new NCNode(cRegion,tempList));
			}else if(!items.get(items.size()-1).contains(cRegion)){
				items.add(new NCNode(cRegion,tempList));
			}
			
			
		}//if peaks not empty
		
		size = items.size();
		
	}//end constructor
	
	public int getOverlapIndex(Region p){
		
		int i = 0;
		int k = items.size()-1;
		
		while(i < k){
			int j = (i+k)/2;
			NCNode cNode = items.get(j); 
			if(cNode.contains(p)){
				return j;
			}else{
				if(p.getStart() < cNode.interval.getStart()){
					k = j;
				}else{
					i = j+1;
				}
			}
		}
		return i;
	}
	
	public int getOverlapIndex(long coord){
		int i = 0;
		int k = items.size()-1;
		
		while(i < k){
			int j = (i+k)/2;
			NCNode cNode = items.get(j); 
			if(cNode.contains(coord)){
				return j;
			}else{
				if(coord < cNode.interval.getStart()){
					k = j;
				}else{
					i = j+1;
				}
			}
		}
		return i;
	}
	
	public int getLowerOverlapIndex(long coord){
		int i = getOverlapIndex(coord);
		int j = i;
		for(; j >= 0; --j){
			NCNode cNode = items.get(j);
			if(!cNode.contains(coord)){
				return j;
			}
		}
		if(j >= 0){
			return j;
		}else{
			return 0;
		}
	}
	
	public int getUpperOverlapIndex(long coord){
		int i = getOverlapIndex(coord);
		int j = i;
		for(; j < items.size(); ++j){
			NCNode cNode = items.get(j);
			if(!cNode.contains(coord)){
				return j;
			}
		}
		if(j < items.size()){
			return j;
		}else{
			return items.size() - 1;
		}
	}
	
	public int getLowerOverlapIndex(Region p){
		int i = getOverlapIndex(p);
		int j = i;
		for(; j >= 0; --j){
			NCNode cNode = items.get(j);
			if(!cNode.overlaps(p)){
				return j;
			}
		}
		if(j >= 0){
			return j;
		}else{
			return 0;
		}
	}
	
	public int getUpperOverlapIndex(Region p){
		int i = getOverlapIndex(p);
		int j = i;
		for(; j < items.size(); ++j){
			NCNode cNode = items.get(j);
			if(!cNode.overlaps(p)){
				return j;
			}
		}
		if(j < items.size()){
			return j;
		}else{
			return items.size() - 1;
		}
	}
	
	public Vector<Region> getRegions(){
		Vector<Region> vec = new Vector<Region>();
		
		for(int i = 0; i < items.size(); i ++){
			NCNode cNode = items.get(i);
			vec.add(cNode.interval);
			if(cNode.containments != null && cNode.containments.size() > 0){
				vec.addAll(cNode.containments.getRegions());
			}
		}
		return vec;
	}
	
	public Region getOverlapRegion(Region p){
		int index = getOverlapIndex(p);
		return items.get(index).interval;
	}
	
	public Vector<Region> getRegionsInRange(long start,long end){
		
		Region p = new SimpleRegion(start, end);
		
		return getRegionsInRange(p);
	}
	
	public Vector<Region> getRegionsInRange(Region p){
		long start = p.getStart();
		long end = p.getEnd();
	
		int s = getLowerOverlapIndex(start);
		int e = getUpperOverlapIndex(end);
		
		Vector<Region> vec = new Vector<Region>();
		for(int i = s; i <= e; i ++){
			NCNode cNode = items.get(i);
			if(!cNode.overlaps(start,end)){
				continue;
			}
			if(p.overlaps(cNode.interval)){
				vec.add(cNode.interval);
			}
			if(cNode.containments != null && cNode.containments.size() > 0){
				Vector<Region> contained = cNode.containments.getRegions();
				for(Region r: contained){
					if(p.overlaps(r)){
						vec.add(r);
					}
				}
				//vec.addAll(cNode.containments.getRegions());
			}
		}
		return vec;
	}
	
	public int size(){
		return size;
	}
	
	public int numRegions(){
		return numRegions;
	}

	public Vector<Region> getRegionsAt(int index){
		
		Vector<Region> temp = new Vector<Region>();
		
		temp.add(items.get(index).getRegion());
		if(items.get(index).containments != null){
			temp.addAll(items.get(index).containments.getRegions());
		}
		
		return temp;
	}

	//Private members
	private Vector<NCNode> items;
	private int size;
	private int numRegions;
	
	public class NCNode  implements java.io.Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public NCNode(Region p,Vector<Region> contains){
			interval = p;
			if(contains.size() == 0){
				containments = null;
			}else{
				containments = new NCList(contains);
			}
		}
		
		public String toString(){
			return interval.toString();
		}
		
		public boolean contains(Region p){
			return interval.contains(p);
		}
		
		public boolean contains(long start, long end){
			return interval.contains(start, end);
		}
		
		public boolean contains(long coord){
			return interval.contains(coord);
		}
		
		public boolean overlaps(Region p){
			return interval.overlaps(p);
		}
		
		public boolean overlaps(long start,long end){
			return interval.overlaps(start,end);
		}
		
		public boolean overlaps(long coord){
			return interval.overlaps(coord);
		}
		
		public Region getRegion(){
			return interval;
		}
		
		public Region interval;
		public NCList containments;
		
	}//NCNode
	
	public void print(String s){
		System.out.println(s);
	}

}
