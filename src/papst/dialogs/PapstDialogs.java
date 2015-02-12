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
package papst.dialogs;

import java.awt.Component;
import java.io.File;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JOptionPane;

import papst.parsers.TrackParserInterface;
import papst.utility.NumberAwareStringComparator;


public class PapstDialogs {
	
	private Component view;
	
	public PapstDialogs(Component inView){
		view = inView;
	}
	
	public int showAreYouSure_dropAllResults(){
		StringBuffer msg = new StringBuffer("Are you sure you want to remove all Result Tables?");
		
		return JOptionPane.showConfirmDialog(view, msg.toString(),
										"Confirm Results Removal", JOptionPane.OK_CANCEL_OPTION);
		
	}
	
	public int showAreYouSure_dropManyFeatures(Vector<String> names){
		StringBuffer msg = new StringBuffer("Are you sure you want to remove the followng tracks?\n");
		
		for(String s: names){
			msg.append(s+"\n");
		}
		
		return JOptionPane.showConfirmDialog(view, msg.toString(),
										"Confirm Track Removal", JOptionPane.OK_CANCEL_OPTION);
	}
	
	public int showAreYouSure_dropManyFilters(Vector<String> names){
		
		StringBuffer msg = new StringBuffer("Are you sure you want to remove the following filters?\n");
		
		for(String s: names){
			msg.append(s + "\n");
		}
		
		return JOptionPane.showConfirmDialog(view, msg.toString(),
										"Confirm Filter Removal", JOptionPane.OK_CANCEL_OPTION);
	}
	
	public int showAreYouSure_dropResultTable(String name){
		return JOptionPane.showConfirmDialog(view, "Are you sure you want to delete the result Table '" +
										name+ "'?",
										"Confirm Result Table Delete", JOptionPane.OK_CANCEL_OPTION);
	}
	
	public int showGenomeParserErrorOptionDialog(){
		
		Object[] options = {"Open Gene Parser Wizzard","Cancel"};
		int choice = JOptionPane.showOptionDialog(view,
  			   "This file does not appear to match any of the default parsers.",
  			   "File Parser Error",
  			   JOptionPane.YES_NO_OPTION,
  			   JOptionPane.ERROR_MESSAGE,
  			   null,
  			   options,
  			   options[0]);
		return choice;
	}
	
	public int showParserErrorOptionDialog(File file, TrackParserInterface parser,StringBuffer msg){
		
		Object[] options = {"Choose Another Parser from List","Open Parser Wizard","Cancel"};
		int choice = JOptionPane.showOptionDialog(view,
  			   "This file '" + file.getName() + "' does not appear to be a " +parser.getName() + " file.\n"+msg,
  			   "File Parser Error",
  			   JOptionPane.YES_NO_OPTION,
  			   JOptionPane.ERROR_MESSAGE,
  			   null,
  			   options,
  			   options[0]);
		return choice;
	}
	
	public int showParserErrorSkipOptionDialog(File file,TrackParserInterface parser,StringBuffer msg){
		
		Object[] options = {"Chooser Parser From List","Open Parser Wizard","Skip"};
		int choice = JOptionPane.showOptionDialog(view,
				"This file '" + file.getName() + "' does not appear to be a " +parser.getName() + " file.\n"+msg,
  			   "File Parser Error",
  			   JOptionPane.YES_NO_OPTION,
  			   JOptionPane.ERROR_MESSAGE,
  			   null,
  			   options,
  			   options[0]);
		return choice;
	}
	
	public int showAreYouSure_closePapst(){
		return JOptionPane.showConfirmDialog(view, "Are you sure you want Exit Papst?",
				"Exit Papst?", JOptionPane.OK_CANCEL_OPTION);
	}
	
	public String showTrackParserSectionDialog(Vector<String> keys){
		//Vector<String> keys = model.getTrackParserFactroy().getKeys();
		Collections.sort(keys,NumberAwareStringComparator.INSTANCE);
		
		String selected = (String)JOptionPane.showInputDialog(view,"Choose a Parser",
				"Select Parser",
				JOptionPane.QUESTION_MESSAGE,
				null,
				keys.toArray(),
				keys.elementAt(0));
		
		return selected;
	}
	
	public String showAvailableTrackSectionDialog(Vector<String> keys){
		//Vector<String> keys = model.getFeatureNames();
		Collections.sort(keys,NumberAwareStringComparator.INSTANCE);
		
		String selected = (String)JOptionPane.showInputDialog(view,"Choose a Feature File to set as the base track",
				"Select a Base Track",
				JOptionPane.QUESTION_MESSAGE,
				null,
				keys.toArray(),
				keys.elementAt(0));
		
		return selected;
	}
	
	public int showAssignOptionDialog(){
		
		Object[] options = {"Single Track","All Tracks"};
		int choice = JOptionPane.showOptionDialog(view,
  			   "Would you like to assign a single track or all tracks?\n" +
  			   "'Single Track' will allow you to choose a track and display asignments in the Results Window.\n" +
  			   "'All Tracks' will assign all checked tracks in separate files in a selected folder",
  			   "Gene Assign Options",
  			   JOptionPane.YES_NO_OPTION,
  			   JOptionPane.QUESTION_MESSAGE,
  			   null,
  			   options,
  			   options[0]);
		return choice;
	}
	
	public String showResultColumnOptionDialog(Vector<String> keys){
		String selected = (String)JOptionPane.showInputDialog(view,
				"Which column would you like to use for this track's value?",
	  			"Select a Value Column",
				JOptionPane.QUESTION_MESSAGE,
				null,
				keys.toArray(),
				keys.elementAt(0));
		return selected;
	}
	
	public String showPeakNameInputDialog(){
		String response = JOptionPane.showInputDialog(view,
                "Enter a name for this track set",
                "Set Track Name",
                JOptionPane.QUESTION_MESSAGE);
		return response;
	}
	
	public String showAvailableTracksAssignDialog(Vector<String> keys){
		//Vector<String> keys = model.getFeatureNames();
		Collections.sort(keys,NumberAwareStringComparator.INSTANCE);
		
		String selected = (String)JOptionPane.showInputDialog(view,"Choose a track to assign using the current genome",
				"Select a Track to Assign",
				JOptionPane.QUESTION_MESSAGE,
				null,
				keys.toArray(),
				keys.elementAt(0));
		
		return selected;
	}

}
