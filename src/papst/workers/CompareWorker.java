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
package papst.workers;

import java.util.Vector;
import javax.swing.SwingWorker;

import papst.models.*;
import papst.database.*;
import papst.regions.*;


public class CompareWorker extends SwingWorker<CompareResultTableModel, Void> {

	private Vector<PeakDatabase> features;
	
	public CompareWorker(Vector<PeakDatabase> features){
		this.features = features;
	}
	
	@Override
	protected CompareResultTableModel doInBackground() throws Exception {
		
		Vector<Vector<Object>> tableData = new Vector<Vector<Object>>();
		Vector<String> columnLabels = new Vector<String>();
		
		columnLabels.add("Feature");
		
		for(int i = 0; i < features.size(); ++i){
			
			
			
			Vector<Region> rowPeaks  = features.elementAt(i).getRegions();
			Vector<Object> rowValues = new Vector<Object>();
			
			//add column and row label
			columnLabels.add(features.elementAt(i).getName());
			rowValues.add(features.elementAt(i).getName());
			
			
			
			for(int j = 0; j < features.size(); ++ j){
				//add column label
				int numOverlaps = 0;
				PeakDatabase columnPeakDB = features.elementAt(j); 
				
				
				if(j == i){
					rowValues.add(columnPeakDB.getNumRegions());
				}else{
				
					for(Region region: rowPeaks){
						
						Peak peak = (Peak)region;
						
						Vector<Region> overlaps = columnPeakDB.getOverlappingRegions(peak.chrom, peak.getMidpoint());
						
						if(overlaps.size() > 0){
							++numOverlaps;
						}
						
					}//for each region
					
					rowValues.add(numOverlaps);
				}
				
			}//end column for
			
			//add table model
			tableData.add(rowValues);
		}//end row for
		
		return new CompareResultTableModel(tableData, columnLabels);
	}
	

}

