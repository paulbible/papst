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
import papst.regions.Gene;

public class GeneDatabase implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private HashMap<String,NCList> geneDB;
	private HashMap<String,NCList> exonDB;
	
	private int numGenes;
	
	
	public GeneDatabase(HashMap<String, Vector<Region> > geneChromMap, HashMap<String, Vector<Region> > exonChromMap,String name){
		this.name = name;
		
		numGenes = 0;
		
		geneDB = new HashMap<String,NCList>();
		exonDB = new HashMap<String,NCList>();
		
		for(String key:geneChromMap.keySet()){
			numGenes += geneChromMap.get(key).size();
			geneDB.put(key, new NCList(geneChromMap.get(key)));
		}
		
		for(String key:exonChromMap.keySet()){
			exonDB.put(key, new NCList(exonChromMap.get(key)));
		}
		
		
	}
	
	public Vector<Region> getGenes(){
		
		Vector<Region> genes = new Vector<Region>();
		
		for(String key: geneDB.keySet()){
			genes.addAll(geneDB.get(key).getRegions());
		}
		
		return genes;
	}
	
	public int getNumGenes(){
		return this.numGenes;
	}
	
	public String toString(){
		return name;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean hasKey(String key){
		return geneDB.containsKey(key);
	}
	
	public NCList getNCList(String key){
		if(geneDB.containsKey(key)){
			return geneDB.get(key);
		}else{
			return null;
		}
	}
	
	public Vector<Region> getExons(Gene gene){
		if(exonDB.containsKey(gene.accession)){
			return exonDB.get(gene.accession).getRegions();
		}else{
			return null;
		}
	}

}
