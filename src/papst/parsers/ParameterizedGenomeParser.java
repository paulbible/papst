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


public class ParameterizedGenomeParser implements GenomeParserInterface,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, Integer> indexMap;
	private String regex;
	
	public static final String name = "Custom Gene File";
	
	public ParameterizedGenomeParser(HashMap<String, Integer> indexMap,String regex){
		this.indexMap = indexMap;
		this.regex = regex;
	}
	
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
				
				String[] parts = line.split(this.regex);
				
				
				//check gene start and end columns are numeric
				try{
					
					String start_str = parts[indexMap.get("start")];
					Long.parseLong(start_str);
					
					String end_str   = parts[indexMap.get("end")];
					Long.parseLong(end_str);
					
					
					
					
				}catch(Exception e){
					//e.printStackTrace();
					msg.append("Columns "+indexMap.get("start")+
							" and " +indexMap.get("end")+
							" (start and end) are not numeric.");
					scanner.close();
					return false;
				}
				
				try{
					
					if(indexMap.containsKey("exonCount")){
						Integer.parseInt(parts[indexMap.get("exonCount")]);
					}
				}catch(Exception e){
					msg.append("Column "+indexMap.get("exCount")+
							
							" (exCount) is not numeric.");
					scanner.close();
					return false;
				}
				
				String strand = parts[indexMap.get("strand")];
				
				if(!(strand.startsWith("+") || strand.startsWith("-") )){
					msg.append("Column "+indexMap.get("strand")+" (strand) is not + or -.");
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
				
				String[] parts = line.split(this.regex);

				
				//refseq accession
				String accession = parts[indexMap.get("accession")];
				String chrom     = parts[indexMap.get("chrom")];
				
				//strand + - (watson or crick)
				String strand    = parts[indexMap.get("strand")];
				
				
				//get start and end
				String start_str = parts[indexMap.get("start")];
				String end_str   = parts[indexMap.get("end")];
				
				//convert to numeric types
				long start       = Long.parseLong(start_str);
				long end         = Long.parseLong(end_str);
				
				
				int numExons = 0;
				
				//convert number of exons to numeric
				if(indexMap.containsKey("exCount")){
					numExons = Integer.parseInt(parts[5]);
				}
				
				String name = "";
				if(indexMap.containsKey("name")){
					name = parts[8];
				}
				
				Gene gene = new Gene(accession,name,chrom,start,end,0.0f,"refseq",strand);
				
				
				if(indexMap.containsKey("exStarts") && indexMap.containsKey("exEnds")){
					String exStarts  = parts[6];
					String exEnds    = parts[7];
					
					Vector<Region> exonList = getExonRegions(exStarts,exEnds,numExons);
					
					exonChromMap.put(gene.accession, exonList);
				}
				
				
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
		return new ParameterizedGenomeParser(indexMap,regex);
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

}
