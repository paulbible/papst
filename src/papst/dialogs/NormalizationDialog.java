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

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import papst.utility.CommandLog;
import papst.PapstModel;


public class NormalizationDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPanel;
	private JPanel topPanel;
	private JLabel lblInputTagCounts;
	private JPanel buttonPanel;
	private JPanel rightAlignPanel;
	private JButton btnCancel;
	private JButton btnNornalize;
	private JPanel leftPanel;
	
	private Vector<String> colNames;
	
	private CommandLog log;
	private JPanel panel;
	private JButton btnImport;
	private JScrollPane scrollPane;
	private JTable table;
	private String currentDir;
	
	public boolean isCancelled;
	public boolean isUndoAll;
	
	private HashMap<String, Integer> normMap;
	private HashMap<String, Integer> withTagsMap;
	private JLabel lblNewLabel;
	private JPanel labelPanel;
	private JPanel centerPanel;
	private JButton normWithCountsButton;
	private JButton unAllNormsButton;
	
	public NormalizationDialog(JFrame locationComponent,PapstModel model,CommandLog log,String currentDir){
		this.log = log;
		
		this.currentDir = currentDir;
		
		isCancelled = false;
		isUndoAll   = false;
		normMap = null;
		
		
		setBounds(100, 100, 700, 300);
		setModalityType(ModalityType.APPLICATION_MODAL);
		
		setTitle("Normalization");
		
		contentPanel = new JPanel();
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		
		colNames = new Vector<String>();
		colNames.add("Track Name");
		colNames.add("Tag Count");
		
		Vector<Vector<String> > emptyCells = new Vector<Vector<String> >();
		for(int i  = 0; i < model.getNumFeatures(); ++i){
			Vector<String> rowVec = new Vector<String>();
			rowVec.add("");
			rowVec.add("");
			emptyCells.add(rowVec);
		}
		
		//get a map with all the current tags, to avoid having to load them all in
		withTagsMap = new  HashMap<String, Integer>();
		Vector<String> featureNames = model.getFeatureNames();
		for(String s: featureNames){
			if(model.getFeature(s).getTotalTags() > 0){
				withTagsMap.put(s, model.getFeature(s).getTotalTags());
			}
		}
		
		
		DefaultTableModel tableModel = new DefaultTableModel(emptyCells,colNames);
		
		table = new JTable(tableModel);
		table.setEnabled(false);
		scrollPane.setViewportView(table);
		
		table.addMouseListener(getTableRightClickListener());
		
		topPanel = new JPanel();
		getContentPane().add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		topPanel.add(panel, BorderLayout.EAST);
		
		btnImport = new JButton("Import Counts ...");
		btnImport.setToolTipText("Import tag counts uisng an external tag count file");
		panel.add(btnImport);
		
		labelPanel = new JPanel();
		topPanel.add(labelPanel, BorderLayout.WEST);
		labelPanel.setLayout(new BorderLayout(0, 0));
		
		lblInputTagCounts = new JLabel("  Paste tag counts with Right-Click or Ctrl + Click on Mac");
		labelPanel.add(lblInputTagCounts, BorderLayout.NORTH);
		
		lblNewLabel = new JLabel("   or Import from a file.");
		labelPanel.add(lblNewLabel, BorderLayout.SOUTH);
		
		buttonPanel = new JPanel();
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new BorderLayout(0, 0));
		
		rightAlignPanel = new JPanel();
		buttonPanel.add(rightAlignPanel, BorderLayout.EAST);
		
		btnCancel = new JButton("Cancel");
		btnCancel.setToolTipText("Close this dialog and take no action");
		rightAlignPanel.add(btnCancel);
		
		leftPanel = new JPanel();
		buttonPanel.add(leftPanel, BorderLayout.WEST);
		
		btnNornalize = new JButton("Normalize");
		btnNornalize.setToolTipText("Normalize the peak sets in the above table");
		leftPanel.add(btnNornalize);
		
		centerPanel = new JPanel();
		buttonPanel.add(centerPanel, BorderLayout.CENTER);
		
		normWithCountsButton = new JButton("Normalize all with tag counts");
		normWithCountsButton.setToolTipText("Normalize all peak sets that already have tag counts");
		centerPanel.add(normWithCountsButton);
		
		unAllNormsButton = new JButton("Undo all normalization");
		unAllNormsButton.setToolTipText("Undo all peak sets that are currently normalized");
		centerPanel.add(unAllNormsButton);
		
		
		btnCancel.addActionListener(getCancelListener());
		btnNornalize.addActionListener(getNormalizeListener());
		normWithCountsButton.addActionListener(getNormalizeAllActionListener());
		unAllNormsButton.addActionListener(getUndoActionListener());
		
		btnImport.addActionListener(getImportListener());
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		this.setLocationRelativeTo(locationComponent);
		
	}
	
	private ActionListener getImportListener(){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				print("Import button pressed");
				
				JFileChooser chooser = new JFileChooser(currentDir);
				
				int returnVal = chooser.showOpenDialog(getContentPane());
				
				File loadFile = chooser.getSelectedFile();
				
				
				
				if(returnVal != JFileChooser.CANCEL_OPTION){
				
					boolean noErrors = true;
					Scanner scanner;
					Vector<Vector<String>> tableVals = new Vector<Vector<String>>();
					
					
					try{
						//TODO Import from file
						scanner = new Scanner(loadFile);
						
						while(scanner.hasNext()){
							
							String line = scanner.nextLine();
							
							String[] parts = line.split("\t");
							
							try{
								Double.parseDouble(parts[1]);
							}catch(Exception ex){
								noErrors = false;
							}
							
							if(noErrors){
								Vector<String> row = new Vector<String>();
								for(String s: parts){
									row.add(s);
								}
								
								tableVals.add(row);
							}
							
							noErrors = true;
							
						}
						
						
						scanner.close();
					}catch (Exception ex) {
						JOptionPane.showMessageDialog(getContentPane(),"This does not appear to be a simple tab delimited list.",
							"Format error.",JOptionPane.INFORMATION_MESSAGE);
						//ex.printStackTrace();
						noErrors = false;
					}
					
					if(noErrors){
						table.setModel(new DefaultTableModel(tableVals,colNames));
					}
					
					
				
				}// open not cancelled
				
				
				
			}
		};
	}
	
	
	private ActionListener getNormalizeListener(){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				print("Normalize button pressed");
				
				normMap = new HashMap<String, Integer>();
				
				for(int i = 0; i < table.getRowCount();++i){
					
					String key = (String)table.getValueAt(i, 0);
					
					String valStr = (String)table.getValueAt(i, 1);
					
					try{
						
						Integer value = Integer.parseInt(valStr.trim());
						normMap.put(key, value);
						
					}catch(Exception ex){
						print("double parser error, bad row data");
					}
					
				}//end for, each row
				
				dispose();
			}//end interface method
		};//end annonymous class
	}//end method getNormalizeListener
	
	private ActionListener getCancelListener(){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				print("Cancel button pressed");
				isCancelled = true;
				dispose();
				
			}
		};
	}//end method, getCancelListener
	
	private ActionListener getUndoActionListener(){
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				print("Undo all normalization pressed.");
				isUndoAll = true;
				dispose();
			}
		};
	}
	
	private ActionListener getNormalizeAllActionListener(){
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				print("Normalize all with tag counts pressed.");
				
				normMap = withTagsMap;
				dispose();
			}
		};
	}
	
	
	private void print(String msg){
		System.out.println("NormalizationDialog: "+msg);
		log.addCommand("NormalizationDialog: "+msg);
	}
	
	
	private MouseListener getTableRightClickListener(){
		return new MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				
				if(SwingUtilities.isRightMouseButton(e) || e.isControlDown()){
					print("mouse right click");
					
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					Transferable contents = clipboard.getContents(null);
					
					boolean hasTransferableText = (contents != null) && 
							contents.isDataFlavorSupported(DataFlavor.stringFlavor);
					
					if(hasTransferableText){
						try{
							String result = (String)contents.getTransferData(DataFlavor.stringFlavor);
							print("result - "+result);
							
							Vector<Vector<String>> tableVals = parseTableString(result);
							
							if(tableVals != null){
								print("data good");
								
								table.setModel(new DefaultTableModel(tableVals,colNames));
								
								
							}else{
								print("data bad");
							}
							
						}catch(Exception ex){
							ex.printStackTrace();
						}
						
					}//text ok
					
					
				}//end if, is right click

			}//end mouseClicked method
		};
	}//end method, getTableRightClickListener
	
	private Vector<Vector<String>> parseTableString(String inString){
		
		Vector<Vector<String>> tableData = new Vector<Vector<String>>();
		
		String[] strings = inString.split("\n");
		for(String rowStr: strings){
			
			Vector<String> row = new Vector<String>();
			String[] parts = rowStr.split("\t");
			for(String p: parts){
				row.add(p.trim());
			}
			
			try{
				Integer.parseInt(row.elementAt(1));
			}catch(Exception e){
				print("bad data parse table string");
				continue;
			}
			
			
			tableData.add(row);
		}
		
		if(tableData.size() == 0 || tableData.elementAt(0).size() != 2){
			return null;
		}else{
			return tableData;
		}
	}
	
	public boolean isCancelled(){
		return isCancelled;
	}
	
	public boolean isUndoNorm(){
		return isUndoAll;
	}
	
	public HashMap<String, Integer> getTagCountMap(){
		return normMap;
	}
	
	

}
