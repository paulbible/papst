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
package papst.regions;

public abstract class AbstractRegion implements Comparable<Region>,Region, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long   start;
	public long   end;
	
	AbstractRegion(long start,long end){
		this.start = start;
		this.end = end;
	}
	
	AbstractRegion(int start,int end){
		this.start = start;
		this.end = end;
	}
	
	public long getStart(){
		return this.start;
	}
	
	public long getEnd(){
		return this.end;
	}
	
	public long getMidpoint(){
		return this.start + (this.end - this.start)/2;
	}
	
	public boolean contains(Region r){
		if (this.start <= r.getStart() && this.end >= r.getEnd()){
			return true;
		}else{
			return false;
		}
	}//end contains(Peak p)
	
	public boolean contains(long start,long end){
		if (this.start <= start && this.end >= end){
			return true;
		}else{
			return false;
		}
	}//end contains(long start,long end)
	
	public boolean contains(long coord){
		if (coord >= this.start && coord <= this.end){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean overlaps(Region r){
		if(this.start > r.getEnd() ){
			return false;
		}else if(this.end < r.getStart()){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean overlaps(long start,long end){
		if(this.start > end){
			return false;
		}else if(this.end < start){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean overlaps(long coord){
		return contains(coord);
	}
	
	public long overlapSize(Region r){
		if(!this.overlaps(r)){
			return 0;
		}else{
			long e = r.getEnd() < getEnd() ? r.getEnd() : getEnd();
			long s = r.getStart() > getStart() ? r.getStart() : getStart();
			return e - s + 1;
		}
	}
	
	public long overlapSize(long start,long end){
		if(!this.overlaps(start,end)){
			return 0;
		}else{
			long e = end < getEnd() ? end : getEnd();
			long s = start > getStart() ? start : getStart();
			return e - s + 1;
		}
	}

	@Override
	public int compareTo(Region r) {
		if(r.getStart() < this.start){
			return 1;
		}else if(r.getStart() == this.start){
			
			//NCList algorihtm needs longer regions with the same start to come first.
			if( this.end - this.start < r.getEnd() - r.getStart()){
				return 1;
			}else if(this.end == r.getEnd()){
				return 0;
			}else{
				return -1;
			}
				
		}else{
			return -1;
		}
	}//end compareTo(Peak arg0)
	
	@Override
	public double getValue(){
		return 0;
	}
	
	@Override
	public String getLocationString(){
		return getStart()+"-"+getEnd();
	}
	
	public String toString(){
		return getLocationString();
	}
	

}
