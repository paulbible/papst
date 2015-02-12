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


public class MacsNarrowPeakTrackParser implements TrackParserInterface,Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int valueColumn;
	
	public static final int FOLD_CHANGE = 6;
	public static final int P_VALUE_IC = 7;
	public static final int Q_VALUE_IC = 8;

	@Override
	public PeakDatabase parseTracks(File file) {
		HashMap<String, Vector<Region> > peakChromMap = new HashMap<String, Vector<Region> >();
		
		PeakDatabase peakDB = null;
		
		Scanner scanner;
		String line = "";
		try {
			//file input scanner
			scanner = new Scanner(file);
			
			//parser loop
			while(scanner.hasNext()){
				
				//get line
				line = scanner.nextLine();
				
				//skip comments
				if(line.startsWith("#") || line.startsWith("!")){continue;}
				
				//split the line
				String[] parts = line.split("\t");
				
				for(int i = 0; i < parts.length;++i){
					parts[i] = parts[i].trim();
				}
				
				//get chromosome
				String chrom = parts[0];
				
				//get start and end coordinates
				long start = Long.parseLong(parts[1]);
				long end   = Long.parseLong(parts[2]);
				
				
				double value = Double.parseDouble(parts[valueColumn]);
				
				Peak peak = new Peak(chrom, start, end, value, file.getName());
				
				
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
		int lineCount = 0;
		int maxLines = 5;
		
		boolean reachedTest = false;
		
		Scanner scanner;
		String line = "";
		try {
			scanner = new Scanner(file);
			
			while(scanner.hasNext() && lineCount < maxLines){
				reachedTest = true;
				line = scanner.nextLine();
				
				if(line.startsWith("#") || line.startsWith("!")){continue;}
				
				String[] parts = line.split("\t");
				
				for(int i = 0; i < parts.length;++i){
					parts[i] = parts[i].trim();
				}
				
				
				
				//check correct number of columns
				if(parts.length != 10){
					msg.append("File does not contain 10 columns.\nFile has "+parts.length+" columns.\n10 columns expected.");
					scanner.close();
					return false;
				}
				
				
				//check gene start and end columns are numeric
				try{
					
					String start_str = parts[1];
					Long.parseLong(start_str);
					
					String end_str   = parts[2];
					Long.parseLong(end_str);
					
					Double.parseDouble(parts[valueColumn]);
					
				}catch(Exception e){
					msg.append("Columns 2 and 3 (TSS and TES) and " +(valueColumn+1) + " (coverage/score) are not numeric.");
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
			msg.append("The file '"+file.getName()+"' does not appear to be a text file. It may be empty or a binary file.");
			return false;
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "MACS NarrowPeak";
	}
	
	@Override
	public TrackParserInterface clone(){
		return new MacsNarrowPeakTrackParser(valueColumn);
	}
	
	public MacsNarrowPeakTrackParser(){
		this.valueColumn = P_VALUE_IC;
	}
	
	public MacsNarrowPeakTrackParser(int valueColumn){
		switch(valueColumn){
			case FOLD_CHANGE:
				this.valueColumn = FOLD_CHANGE;
				break;
			case Q_VALUE_IC:
				this.valueColumn = Q_VALUE_IC;
				break;
			default:
				this.valueColumn = P_VALUE_IC;
		}
	}
	

}
