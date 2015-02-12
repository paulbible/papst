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

import java.util.Vector;


public class RegionModifier implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean usePromoter;
	private boolean useBody;
	private boolean useDownstream;
	
	private int promoterDistUp;
	private int promoterDistDown;
	
	private int downstreamDist;
	
	public RegionModifier(boolean usePromoter,boolean useBody,boolean useDownstream,
			int promoterDistUp,int promoterDistDown,int downstreamDist){
		
		this.usePromoter   = usePromoter;
		this.useBody       = useBody;
		this.useDownstream = useDownstream;
		
		this.promoterDistUp   = promoterDistUp;
		this.promoterDistDown = promoterDistDown;
		
		this.downstreamDist = downstreamDist;
	}//end constructor
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		
		boolean isFirst = true;
		
		if(usePromoter){
			buff.append("promoter ("+promoterDistUp+","+promoterDistDown+")");
			if(isFirst){
				isFirst = false;
			}
		}
		
		if(useBody){
			
			if(!isFirst){
				buff.append(" ");
			}else{
				isFirst = false;
			}
			
			buff.append("body");
		}
		
		if(useDownstream){
			if(!isFirst){
				buff.append(" ");
			}else{
				isFirst = false;
			}
			buff.append("downstream ("+downstreamDist+")");
		}
		return buff.toString();
	}
	
	public boolean usePromoter(){
		return usePromoter;
	}
	
	public int getPromoterUpDistance(){
		return promoterDistUp;
	}
	
	public int getPromoterDownDistance(){
		return promoterDistDown;
	}
	
	public int getDownstreamDistance(){
		return downstreamDist;
	}
	
	public boolean useBody(){
		return useBody;
	}
	
	public boolean useDownstream(){
		return useDownstream;
	}
	
	public Vector<Region> getTestRegions(Gene baseRegion){
		
		Vector<Region> testRegions = new Vector<Region>();
		
		int p = 1;
		int b = 2;
		int d = 4;
		
		int comboCode = 0;
		
		long p_start = baseRegion.start;
		long p_end = baseRegion.end;
		
		long b_start = baseRegion.start;
		long b_end = baseRegion.end;
		
		long d_start = baseRegion.start;
		long d_end = baseRegion.end;
		
		boolean strandIsPlus = baseRegion.strand.compareTo("+") == 0; 
		
		if(this.usePromoter()){
			if(strandIsPlus){
				p_start = baseRegion.getStart() - this.getPromoterUpDistance();
				p_end   = baseRegion.getStart() + this.getPromoterDownDistance();
			}else{
				p_start = baseRegion.getEnd() - this.getPromoterDownDistance();
				p_end   = baseRegion.getEnd() + this.getPromoterUpDistance();
			}
			comboCode += p;
		}
		
		if(this.useBody()){
			b_start = baseRegion.getStart();
			b_end   = baseRegion.getEnd();
			comboCode += b;
		}
		
		if(this.useDownstream()){
			if(strandIsPlus){
				d_start = baseRegion.getEnd();
				d_end   = baseRegion.getEnd() + this.getDownstreamDistance();
			}else{
				d_start = baseRegion.getStart() - this.getDownstreamDistance();
				d_end   = baseRegion.getStart();
			}
			comboCode += d;
		}
		
		 
		if(comboCode == 1){
			//Promtoer only
			testRegions.add(new SimpleRegion(p_start, p_end));
		}else if(comboCode == 2){
			//body only
			testRegions.add(new SimpleRegion(b_start, b_end));
		}else if(comboCode == 3){
			//promoter and body
			testRegions.add(new SimpleRegion(Math.min(p_start,b_start), Math.max(p_end,b_end)));
		}else if(comboCode == 4){
			//downstream only
			testRegions.add(new SimpleRegion(d_start, d_end));
		}else if(comboCode == 5){
			//promoter and downstream
			if(strandIsPlus){
				if(d_start < p_end){
					testRegions.add(new SimpleRegion(Math.min(p_start,d_start),Math.max(p_end,d_end)));
				}else{
					testRegions.add(new SimpleRegion(p_start, p_end));
					testRegions.add(new SimpleRegion(d_start, d_end));
				}
			}else{
				if(p_start < d_end){
					testRegions.add(new SimpleRegion(Math.min(p_start,d_start),Math.max(p_end,d_end)));
				}else{
					testRegions.add(new SimpleRegion(p_start, p_end));
					testRegions.add(new SimpleRegion(d_start, d_end));
				}
			}
			
		}else if(comboCode == 6){
			//downstream and body
			testRegions.add(new SimpleRegion(Math.min(b_start,d_start), Math.max(b_end,d_end)));
		}else if(comboCode == 7){
			//promoter, body, and downstream
			testRegions.add(new SimpleRegion(Math.min(Math.min(p_start,b_start),d_start),Math.max(Math.max(p_end,b_end),d_end)));
		}
		
		
		return testRegions;
	}
	
	public Vector<Region> getTestRegions(Region baseRegion){
		
		Vector<Region> testRegions = new Vector<Region>();
		
		int p = 1;
		int b = 2;
		int d = 4;
		
		int comboCode = 0;
		
		long p_start = baseRegion.getStart();
		long p_end = baseRegion.getEnd();
		
		long b_start = baseRegion.getStart();
		long b_end =  baseRegion.getEnd();
		
		long d_start = baseRegion.getStart();
		long d_end =  baseRegion.getEnd();
		
		if(this.usePromoter()){
			p_start = baseRegion.getStart() - this.getPromoterUpDistance();
			p_end   = baseRegion.getStart() + this.getPromoterDownDistance();
			comboCode += p;
		}
		
		if(this.useBody()){
			b_start = baseRegion.getStart();
			b_end   = baseRegion.getEnd();
			comboCode += b;
		}
		
		if(this.useDownstream()){
			d_start = baseRegion.getEnd();
			d_end   = baseRegion.getEnd() + this.getDownstreamDistance();
			comboCode += d;
		}
		
		if(comboCode == 1){
			//Promtoer only
			testRegions.add(new SimpleRegion(p_start, p_end));
		}else if(comboCode == 2){
			//body only
			testRegions.add(new SimpleRegion(b_start, b_end));
		}else if(comboCode == 3){
			//promoter and body
			testRegions.add(new SimpleRegion(Math.min(p_start,b_start), Math.max(p_end,b_end)));
		}else if(comboCode == 4){
			//downstream only
			testRegions.add(new SimpleRegion(d_start, d_end));
		}else if(comboCode == 5){
			//promoter and downstream
			if(d_start < p_end){
				testRegions.add(new SimpleRegion(Math.min(p_start,d_start),Math.max(p_end,d_end)));
			}else{
				testRegions.add(new SimpleRegion(p_start, p_end));
				testRegions.add(new SimpleRegion(d_start, d_end));
			}
		}else if(comboCode == 6){
			//downstream and body
			testRegions.add(new SimpleRegion(Math.min(b_start,d_start), Math.max(b_end,d_end)));
		}else if(comboCode == 7){
			//promoter, body, and downstream
			testRegions.add(new SimpleRegion(Math.min(Math.min(p_start,b_start),d_start),Math.max(Math.max(p_end,b_end),d_end)));
		}
		
		return testRegions;
	}
	

}
