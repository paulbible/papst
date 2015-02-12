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
package papst.parsers;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

import papst.database.GeneDatabase;
import papst.regions.Region;
import papst.regions.SimpleRegion;
import papst.regions.Gene;

public class RefseqGenomeParser implements GenomeParserInterface,Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String name = "Refseq";
	
	public RefseqGenomeParser(){}
	
	public static void print(String msg){
		System.out.println(msg);
	}// end print
	
	@Override
	public boolean isValid(File file,StringBuffer msg) {
		
		int lineCount = 0;
		int maxLines = 5;
		
		Scanner scanner;
		String line = "";
		try {
			scanner = new Scanner(file);
			
			while(scanner.hasNext() && lineCount < maxLines){
				line = scanner.nextLine();
				
				if(line.startsWith("#") || line.startsWith("!")){continue;}
				
				String[] parts = line.split("\t");
				
				//check correct number of columns
				if(parts.length != 16){
					msg.append("Not enough columns for a refSeq file.\nFile has "+parts.length+" columns.\n16 columns expected.");
					scanner.close();
					return false;
				}
				
				
				//check gene start and end columns are numeric
				try{
					
					String start_str = parts[4];
					Long.parseLong(start_str);
					
					String end_str   = parts[5];
					Long.parseLong(end_str);
					
					Integer.parseInt(parts[8]);
					
				}catch(Exception e){
					msg.append("Columns 5 and 6 (TSS and TES) and 9 (exon number) are not numeric.");
					scanner.close();
					return false;
				}
				
				
				lineCount += 1;
				
			}//end, while
			
			scanner.close();
		}catch (Exception e) {
			msg.append("An unexpected error has occurred on the following line:\n"+line);
			return false;
		}
		
		return true;
	}//end Interface Method, GenomeParserInterface

	@Override
	public GeneDatabase parseGenes(File file) {
		
		HashMap<String, Vector<Region> > geneChromMap = new HashMap<String, Vector<Region> >();
		HashMap<String, Vector<Region> > exonChromMap = new HashMap<String, Vector<Region> >();
		
		
		
		Scanner scanner;
		String line = "";
		try {
			scanner = new Scanner(file);
			
			while(scanner.hasNext()){
				line = scanner.nextLine();
				
				if(line.startsWith("#") || line.startsWith("!")){continue;}
				
				String[] parts = line.split("\t");

				
				//refseq accession
				String accession = parts[1];
				String chrom     = parts[2];
				
				//strand + - (watson or crick)
				String strand    = parts[3];
				
				
				//get start and end
				String start_str = parts[4];
				String end_str   = parts[5];
				
				//convert to numeric types
				long start       = Long.parseLong(start_str);
				long end         = Long.parseLong(end_str);
				
				//convert number of exons to numeric  
				int numExons  = Integer.parseInt(parts[8]);
				
				String exStarts  = parts[9];
				String exEnds    = parts[10];
				
				Vector<Region> exonList = getExonRegions(exStarts,exEnds,numExons);
				
				String name      = parts[12];
				
				Gene gene = new Gene(accession,name,chrom,start,end,0.0f,"refseq",strand);
				
				exonChromMap.put(gene.accession, exonList);
				
				if(!geneChromMap.containsKey(gene.chrom)){
					geneChromMap.put(gene.chrom,new Vector<Region>());
				}
				geneChromMap.get(gene.chrom).add(gene);
				
			}//end, while
			
			
			
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		GeneDatabase genome = new GeneDatabase(geneChromMap,exonChromMap,file.getName());
		
		return genome;
	}//end Interface Method, parseGenes

	@Override
	public GenomeParserInterface clone() {
		return new RefseqGenomeParser();
	}//end Interface Method, GenomeParserInterface
	
	@Override
	public String getName() {
		return name;
	}//end Interface Method, getName
	
	
	private Vector<Region> getExonRegions(String exStarts,String exEnds, int numExons){
		
		String[] starts = exStarts.split(",");
		String[] ends   = exEnds.split(",");
		
		Vector<Region> exons = new Vector<Region>();
		
		for(int i = 0; i < numExons; ++i){
			String temp = starts[i];
			int start = Integer.parseInt(temp);
			temp = ends[i];
			int end   = Integer.parseInt(temp);
			
			exons.add(new SimpleRegion(start,end));
		}
		
		
		
		return exons;
	}//end method,getExonRegions
	

}//end Class
