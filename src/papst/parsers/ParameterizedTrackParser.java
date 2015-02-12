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

import papst.database.PeakDatabase;
import papst.regions.Region;
import papst.regions.Peak;


public class ParameterizedTrackParser implements TrackParserInterface,Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, Integer> indexMap;
	private String regex;
	private boolean hasHeader;
	private String commentChar;
	
	//TODO
	private int skipNum;
	
	
	public ParameterizedTrackParser(HashMap<String, Integer> indexMap, String regex, String commentChar, boolean hasHeader,int skipNum){
		this.indexMap    = indexMap;
		this.regex       = regex;
		this.commentChar = commentChar;
		this.hasHeader   = hasHeader;
		this.skipNum     = skipNum;
	}

	@Override
	public PeakDatabase parseTracks(File file) {
		HashMap<String, Vector<Region> > peakChromMap = new HashMap<String, Vector<Region> >();
		
		PeakDatabase peakDB = null;
		
		Scanner scanner;
		String line = "";
		try {
			//file input scanner
			scanner = new Scanner(file);
			
			boolean isFirst = true;
			int skipCount = skipNum;
			
			//parser loop
			while(scanner.hasNext()){
				
				//get line
				line = scanner.nextLine();
				
				if(skipCount > 0){
					--skipCount;
					continue;
				}
				
				//skip comments
				if(line.length() == 0 || line.startsWith(commentChar)){continue;}
				
				//if there is a header skip it 
				if(isFirst && hasHeader){
					isFirst = false;
					continue;
				}
				
				//split the line
				String[] parts = line.split(this.regex);
				
				for(int i = 0; i < parts.length;++i){
					parts[i] = parts[i].trim();
				}
				
				//get chromosome
				String chrom = parts[indexMap.get("chrom")];
				
				//get start and end coordinates
				long start = Long.parseLong(parts[indexMap.get("start")]);
				long end   = Long.parseLong(parts[indexMap.get("end")]);
				
				
				double value = Double.parseDouble(parts[indexMap.get("value")]);
				
				String name = file.getName();
				
				if(indexMap.containsKey("name")){
					name = parts[indexMap.get("name")];
				}
				
				
				Peak peak = new Peak(chrom, start, end, value, name);
				
				
				if(!peakChromMap.containsKey(peak.chrom)){
					peakChromMap.put(peak.chrom,new Vector<Region>());
				}
				
				peakChromMap.get(peak.chrom).add(peak);
				
				
				
				
			}//end, while
			
			scanner.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		peakDB = new PeakDatabase(peakChromMap, file.getName());
		
		
		return peakDB;
	}

	@Override
	public boolean isValid(File file, StringBuffer msg) {
		// TODO Auto-generated method stub
		int lineCount = 0;
		int maxLines = 5;
		
		int maxColumns = 0;
		for(String key:indexMap.keySet()){
			
			if(maxColumns < indexMap.get(key)){
				maxColumns = indexMap.get(key);
			}
		}//for each, get highest column;
		
		boolean reachedTest = false;
		
		int skipCount = skipNum;
		
		Scanner scanner;
		String line = "";
		try {
			scanner = new Scanner(file);
			
			boolean isFirst = true;
			
			while(scanner.hasNextLine() && lineCount < maxLines){
				
				reachedTest = true;
				
				line = scanner.nextLine();
				
				if(skipCount > 0){
					--skipCount;
					continue;
				}
				
				if(line.length() == 0 || line.startsWith(commentChar)){continue;}
				
				//if there is a header skip it 
				if(isFirst && hasHeader){
					isFirst = false;
					continue;
				}
				
				
				String[] parts = line.split(this.regex);
				
				for(int i = 0; i < parts.length;++i){
					parts[i] = parts[i].trim();
				}
				
				//check correct number of columns
				if(parts.length < (maxColumns+1) ){
					msg.append("File does not contain less than "+(maxColumns+1)+ " columns.\nFile has "+parts.length+" columns.\n" +
							"At least "+(maxColumns+1)+"columns expected.");
					scanner.close();
					return false;
				}
				
				
				//check gene start and end columns are numeric
				try{
					
					String start_str = parts[indexMap.get("start")];
					Long.parseLong(start_str);
					
					String end_str   = parts[indexMap.get("end")];
					Long.parseLong(end_str);
					
					Double.parseDouble(parts[indexMap.get("value")]);
					
				}catch(Exception e){
					msg.append("Columns " + indexMap.get("start") +
							" and " + indexMap.get("end") +
							" (start and end) and " + indexMap.get("value") +
							" (coverage value) are not numeric.");
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
		
		if(reachedTest){
			return true;
		}else{
			msg.append("The file '"+file.getName()+"' does not appear to be a text file. It may be empyt or a binary file.");
			return false;
		}
	}

	@Override
	public String getName() {
		return "custom formatted";
	}
	
	
	public TrackParserInterface clone(){
		return new ParameterizedTrackParser(indexMap, regex, commentChar, hasHeader,skipNum);
	}

}
