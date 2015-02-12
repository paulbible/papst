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

public class GeneAssignWorker extends SwingWorker<ResultData, Void> {
	
	private PeakDatabase feature;
	private GeneDatabase geneData;
	private int flank;
	
	public GeneAssignWorker(PeakDatabase feature,GeneDatabase geneData){
		this.feature = feature;
		this.geneData = geneData;
		flank = 2;
	}

	@Override
	protected ResultData doInBackground() throws Exception {

		int count = 0;
		
		Vector<Vector<Object> > tableData  = new Vector<Vector<Object> >();
		
		Vector<String> columnLabels = new Vector<String>();
		
		columnLabels.add("Feature_Number");
		columnLabels.add("Location");
		columnLabels.add("Gene_name");
		columnLabels.add("Gene_accession");
		columnLabels.add("Gene_Location");
		columnLabels.add("distance_to_tss");
		
		for(Region r:feature.getRegions()){
			
			Peak peak = (Peak)r;
			String chrom = peak.chrom;
			
			if(geneData.hasKey(chrom)){
				
				int index = geneData.getNCList(chrom).getOverlapIndex(peak);
				
				int s_index = index - flank;
				if(s_index < 0){s_index = 0;}
				
				int e_index = index + flank;
				if(e_index > geneData.getNCList(chrom).size() - 1){e_index = geneData.getNCList(chrom).size() - 1;}
				
				Vector<Region> nearGenes = new Vector<Region>();
				
				for(int i = s_index; i <= e_index; ++i){
					nearGenes.addAll(geneData.getNCList(chrom).getRegionsAt(i));
				}
				
				Gene nearestGene = null;
				long min_dist = Long.MAX_VALUE;
				
				for(Region testPeak :nearGenes){
					
					//print(testPeak.getClass().toString());
					Gene g = (Gene)testPeak;
					
					long dist = getDistToTss(peak,g);

					if(dist < min_dist){
						min_dist = dist;
						nearestGene = (Gene)testPeak;
					}
				}//end for, each close gene.
				
				
				//TODO add vector to table data
				Vector<Object> rowVector = new Vector<Object>();
				
				
				//columnLabels.add("Feature_Number");
				rowVector.add(count);
				//columnLabels.add("Location");
				rowVector.add(peak.getLocationString());
				//columnLabels.add("Gene_name");
				rowVector.add(nearestGene.getName());
				//columnLabels.add("Gene_accession");
				rowVector.add(nearestGene.getAccession());
				//columnLabels.add("Gene_Location");
				rowVector.add(nearestGene.getLocationString());
				//columnLabels.add("distance_to_tss");
				rowVector.add(min_dist);
				
				
				tableData.add(rowVector);
				
			}//end if, has same chromosome key
			
			++count;
		}//end for, each peak region
		
		//TODO
		//GeneAssignResultTableModel assignTable = new GeneAssignResultTableModel(tableData, columnLabels);
		ResultData data = new ResultData(columnLabels,tableData,"","Assign",ResultData.ASSIGN_RESULT);
		
		return data;
	}
	
	public static long getDistToTss(Peak p, Gene g){
		
		if(g.strand.compareTo("+") == 0){
			
			if(p.getMidpoint() < g.start){
				return g.start - p.getMidpoint();
			}else{
				return p.getMidpoint() - g.start;
			}
			
		}else{
			
			if(p.getMidpoint() < g.end){
				return g.end - p.getMidpoint();
			}else{
				return p.getMidpoint() - g.end;
			}
		}
	}

}
