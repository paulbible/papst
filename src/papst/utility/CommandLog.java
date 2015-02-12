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
package papst.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;


public class CommandLog  implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<String> logStack;
	
	private File logFile;
	private PrintWriter writer;
	private int count;
	private boolean isLoggingOn;
	
	private static SimpleDateFormat fineFormat = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
	private static SimpleDateFormat coarseFormat = new SimpleDateFormat("yyyy.MM.dd_HH.mm");
	
	public CommandLog(String directory, boolean isOn){
		logStack = new Vector<String>();
		isLoggingOn = isOn;
		
		String dir = directory+"/papst_logs";
		
		if(isLoggingOn){
			new File(dir).mkdir();
			logFile = new File(dir+"/"+coarseFormat.format(new Date())+".papst_log");
			try {
				writer = new PrintWriter(logFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("Could not create log directory.");
				System.exit(1);
			}
		}
		count = 0;
	}
	
	public void addCommand(String command){
		if(isLoggingOn){
			logStack.add(fineFormat.format(new Date())+"-"+command);
			writer.println("log: "+count+"\t"+command);
			writer.flush();
			++count;
		}
	}
	
	public void printStack(){
		int temp = 0;
		
		if(logStack.size() > 0){
			for(String command: logStack){
				System.out.println("command log:"+temp+"\t"+command);
				++temp;
			}
		}
	}
	
	public void writeLog(String directory){
		if(isLoggingOn){
			writer.close();
		}
	}

}
