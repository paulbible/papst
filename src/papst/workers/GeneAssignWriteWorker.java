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

import java.io.File;
import java.io.PrintWriter;
import java.util.Vector;
import javax.swing.SwingWorker;

import papst.database.*;
import papst.regions.*;


public class GeneAssignWriteWorker extends SwingWorker<Void, Void> {

	private PeakDatabase feature;
	private GeneDatabase geneData;
	private File file;
	
	public GeneAssignWriteWorker(PeakDatabase feature,GeneDatabase geneData,File file){
		this.feature = feature;
		this.geneData = geneData;
		this.file = file;
	}

	@Override
	protected Void doInBackground() throws Exception {

		int count = 0;
		
		String sep = "\t";
		
		try{
			
			PrintWriter writer = new PrintWriter(file);
		
			writer.write("Feature_Number"+sep);
			writer.write("Location"+sep);
			writer.write("Gene_name"+sep);
			writer.write("Gene_accession"+sep);
			writer.write("Gene_Location"+sep);
			writer.write("distance_to_tss"+"\n");
			
			for(Region r:feature.getRegions()){
				
				
				
				Peak peak = (Peak)r;
				String chrom = peak.chrom;
				
				if(geneData.hasKey(chrom)){
					
					int index = geneData.getNCList(chrom).getOverlapIndex(peak);
					
					Vector<Region> preGenes = new Vector<Region>();
					Vector<Region> nearGenes;
					Vector<Region> postGenes = new Vector<Region>();
					
					if(index > 0){
						preGenes = geneData.getNCList(chrom).getRegionsAt(index - 1);
					}
					
					nearGenes =   geneData.getNCList(chrom).getRegionsAt(index);
					
					if(index + 1 < geneData.getNCList(chrom).size()){
						postGenes = geneData.getNCList(chrom).getRegionsAt(index + 1);
					}
					
					nearGenes.addAll(preGenes);
					nearGenes.addAll(postGenes);
					
					
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
					
					
					writer.write(count+sep);
					writer.write(peak.getLocationString()+sep);
					writer.write(nearestGene.getName()+sep);
					writer.write(nearestGene.getAccession()+sep);
					writer.write(nearestGene.getLocationString()+sep);
					writer.write(min_dist+"\n");
					
					
					
				}//end if, has same chromosome key
				
				++count;
			}//end for, each peak region
		
			writer.close();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
		

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
