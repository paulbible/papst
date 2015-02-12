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

import papst.database.*;
import papst.regions.*;

public class SummaryWorker  extends SwingWorker<Vector<Object>, Void> {
	
	private PeakDatabase feature;
	private GeneDatabase geneData;
	private int promoterUp;
	
	public SummaryWorker(PeakDatabase feature, GeneDatabase geneData, int promoterUp){
		this.feature = feature;
		this.geneData = geneData;
		this.promoterUp = promoterUp;
	}

	@Override
	protected Vector<Object> doInBackground() throws Exception {
		
		
		int totalPeaks = feature.getNumRegions();
		int numPromoterPeaks = 0;
		int numGenePeaks     = 0;
		int numExon			 = 0;
		int numIntron	     = 0;
		int numIntergenic    = 0;
		
		
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
				
				if(min_dist < promoterUp && !nearestGene.contains(peak.getMidpoint())){
					if(nearestGene.strand.compareTo("+") == 0 && peak.getMidpoint() < nearestGene.start){
						++numPromoterPeaks;
					}else if(nearestGene.strand.compareTo("-") == 0 && peak.getMidpoint() > nearestGene.end){
						++numPromoterPeaks;
					}
				}else if(peak.overlaps(nearestGene)){
					
					//overlapping a gene, add to gene count
					++numGenePeaks;
					
					//determine if overlapping exon
					Vector<Region> exons = geneData.getExons(nearestGene);
					boolean inExon = false;
					for(Region exon:exons){
						if(exon.contains(peak.getMidpoint())){
							inExon = true;
							++numExon;
							break;
						}
					}
					if(!inExon){
						++numIntron;
					}
				}else{
					++numIntergenic;
				}
				

			}//end if, has same chromosome key
			
		}//end for, each peak region
		
		
		Vector<Object> row = new Vector<Object>();
		
		row.add(feature.getName());
			
		row.add(numPromoterPeaks);
		double percent = 100*(double)numPromoterPeaks/totalPeaks;
		row.add(percent);
		
		row.add(numGenePeaks);
		percent = 100*(double)numGenePeaks/totalPeaks;
		row.add(percent);
		
		row.add(numExon);
		percent = 100*(double)numExon/totalPeaks;
		row.add(percent);
		
		row.add(numIntron);
		percent = 100*(double)numIntron/totalPeaks;
		row.add(percent);
		
		row.add(numIntergenic);
		percent = 100*(double)numIntergenic/totalPeaks;
		row.add(percent);
		
		return row;
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
