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

import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;


import papst.utility.CommandLog;
import papst.PapstModel;
import papst.database.PeakDatabase;


public class EditTrackInfoDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JPanel buttonPanel;
	private JPanel leftAlignBtnPanel;
	private JPanel rightAlignBtnPanel;
	private JPanel headerPanel;
	private JLabel lblTrackName;
	private JTextField txtFldTrackName;
	private JPanel txtFldPanel;
	private JButton btnCancel;
	
	private CommandLog log;
	private JPanel centerBtnPanel;
	private JButton btnApply;
	private JButton btnNormalize;
	private JButton btnUndoNormalize;
	private JPanel dataPanel;
	private JLabel lblNumberOfRegions;
	private JLabel lblRegionCount;
	private JLabel lblNumTags;
	private JTextField txtFldTagCount;
	private JLabel lblNormalized;
	private JLabel lblIsNormalizedValue;
	
	private PapstModel model;
	private PeakDatabase feature;
	
	private int originalTagCount;
	private boolean wasNormalized;
	private String currName;
	
	
	public EditTrackInfoDialog(JFrame locationComponent,CommandLog log,PapstModel model,PeakDatabase feature){
		setTitle("Edit Track Information");
		this.log = log;
		this.model = model;
		this.feature = feature;
		
		currName = feature.getName();
		
		originalTagCount = feature.getTotalTags();
		wasNormalized = feature.isNormalized();
		
		this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
		
		setBounds(100, 100, 533, 161);
		setModalityType(ModalityType.APPLICATION_MODAL);
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(locationComponent);
		
		buttonPanel = new JPanel();
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new BorderLayout(0, 0));
		
		leftAlignBtnPanel = new JPanel();
		buttonPanel.add(leftAlignBtnPanel, BorderLayout.WEST);
		
		btnApply = new JButton("Apply");
		btnApply.setToolTipText("Apply all changes to this peak set.");
		leftAlignBtnPanel.add(btnApply);
		btnApply.addActionListener(getApplyActionListener());
		
		rightAlignBtnPanel = new JPanel();
		buttonPanel.add(rightAlignBtnPanel, BorderLayout.EAST);
		
		btnCancel = new JButton("Cancel");
		btnCancel.setToolTipText("Close this dialog and take no action.");
		rightAlignBtnPanel.add(btnCancel);
		btnCancel.addActionListener(getCancelActionListener());
		
		centerBtnPanel = new JPanel();
		buttonPanel.add(centerBtnPanel, BorderLayout.CENTER);
		
		btnNormalize = new JButton("Normalize");
		btnNormalize.setToolTipText("Normalize this peak set using the above tag count.");
		centerBtnPanel.add(btnNormalize);
		btnNormalize.addActionListener(getNormalizeActionListener());
		
		btnUndoNormalize = new JButton("Undo Normalize");
		btnUndoNormalize.setToolTipText("Undo any normalization applied to this peak set.");
		centerBtnPanel.add(btnUndoNormalize);
		btnUndoNormalize.addActionListener(getUndoNormalizeActionListener());
		
		headerPanel = new JPanel();
		getContentPane().add(headerPanel, BorderLayout.NORTH);
		headerPanel.setLayout(new BorderLayout(0, 0));
		
		txtFldPanel = new JPanel();
		headerPanel.add(txtFldPanel);
		txtFldPanel.setLayout(new BorderLayout(0, 0));
		
		txtFldTrackName = new JTextField();
		txtFldPanel.add(txtFldTrackName);
		txtFldTrackName.setColumns(10);
		
		
		
		lblTrackName = new JLabel(" Track name: ");
		headerPanel.add(lblTrackName, BorderLayout.WEST);
		
		dataPanel = new JPanel();
		getContentPane().add(dataPanel, BorderLayout.CENTER);
		dataPanel.setLayout(null);
		
		lblNumberOfRegions = new JLabel("Number of Regions");
		lblNumberOfRegions.setBounds(10, 11, 119, 14);
		dataPanel.add(lblNumberOfRegions);
		
		lblRegionCount = new JLabel("");
		lblRegionCount.setHorizontalAlignment(SwingConstants.CENTER);
		lblRegionCount.setBounds(10, 36, 103, 14);
		dataPanel.add(lblRegionCount);
		lblRegionCount.setText(""+feature.getNumRegions());
		
		lblNumTags = new JLabel("Number of Tags");
		lblNumTags.setBounds(181, 11, 119, 14);
		dataPanel.add(lblNumTags);
		
		txtFldTagCount = new JFormattedTextField(new DecimalFormat("0"));
		txtFldTagCount.setHorizontalAlignment(SwingConstants.RIGHT);
		txtFldTagCount.setBounds(181, 33, 119, 20);
		dataPanel.add(txtFldTagCount);
		txtFldTagCount.setColumns(10);
		
		
		lblNormalized = new JLabel("Is This Track Normalized?");
		lblNormalized.setBounds(335, 11, 172, 14);
		dataPanel.add(lblNormalized);
		
		lblIsNormalizedValue = new JLabel("");
		lblIsNormalizedValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblIsNormalizedValue.setBounds(335, 36, 102, 14);
		dataPanel.add(lblIsNormalizedValue);
		
		
		initForFeature();
		
		
		setLocationRelativeTo(locationComponent);
	}
	
	private void initForFeature(){
		
		//name
		txtFldTrackName.setText(currName);
		
		//regions
		lblRegionCount.setText(""+feature.getNumRegions());
		
		//total tags
		txtFldTagCount.setText(""+feature.getTotalTags());
		
		//is normalized
		if(feature.isNormalized()){
			lblIsNormalizedValue.setText("Yes");
			btnNormalize.setEnabled(false);
			btnUndoNormalize.setEnabled(true);
		}else{
			lblIsNormalizedValue.setText("No");
			btnUndoNormalize.setEnabled(false);
			btnNormalize.setEnabled(true);
		}
		
	}//end method, initForFeature
	
	private void print(String msg){
		System.out.println("EditTrackInfoDialog: "+msg);
		log.addCommand("EditTrackInfoDialog: "+msg);
	}//end method, print
	
	
	private ActionListener getApplyActionListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				print("apply pressed");
				
				String oldName = feature.getName();
				String newName = txtFldTrackName.getText();
				
				boolean hasErrors = false;
				boolean changeNames = false;
				StringBuffer errorMsg = new StringBuffer();
				
				if(oldName.compareTo(newName) != 0){
					print("different names");
					
					if(!model.isFeatureNameUnique(newName)){
						hasErrors = true;
						errorMsg.append("The name '"+newName+"' is used by another feature track. Choose a unique name.\n");
					}else{
						changeNames = true;
					}
					
				}
				
				int tagCount = 0;
				
				try{
					tagCount = Integer.parseInt(txtFldTagCount.getText());
				}catch(Exception ex){
					print("not a valid integer");
					errorMsg.append("Invalid tag count. The tag count must be a non-zero integer.\n");
					hasErrors = true;
				}
				
				
				if(!hasErrors){
					feature.setTotalTags(tagCount);
					if(changeNames){
						model.renameFeature(oldName,newName);
					}
					dispose();
					
				}else{
					JOptionPane.showMessageDialog(getContentPane(),
							errorMsg.toString(),
							"Track Information Error",
							JOptionPane.PLAIN_MESSAGE);
				}
				
				
				
				
			}
		};
	}//end method, getApplyActionListener

	private ActionListener getNormalizeActionListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				print("normalize pressed");
				
				currName = txtFldTrackName.getText();
				
				boolean hasErrors = false;
				
				int tagCount = 0;
				
				try{
					tagCount = Integer.parseInt(txtFldTagCount.getText());
				}catch(Exception ex){
					print("not a valid integer");
					hasErrors = true;
				}
				
				
				if(!hasErrors && tagCount != 0){
					HashMap<String, Integer> normMap = new HashMap<String, Integer>();
					normMap.put(feature.getName(), tagCount);
					model.normalize(normMap);
					initForFeature();
				}else{
					JOptionPane.showMessageDialog(getContentPane(),
							"Invalid tag count. The tag count must be a non-zero integer.\n",
							"Invalid Tag Count Error",
							JOptionPane.PLAIN_MESSAGE);
				}
				
			}
		};
	}//end method, getNormalizeActionListener
	
	private ActionListener getUndoNormalizeActionListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				print("undo normalize pressed");
				currName = txtFldTrackName.getText();
				
				if(feature.isNormalized()){
					Vector<String> peakToUndoNormalize = new Vector<String>();
					peakToUndoNormalize.add(feature.getName());
					model.undoNormalize(peakToUndoNormalize);
					initForFeature();
				}
				
			}
		};
	}//end method, getUndoNormalizeActionListener
	
	private ActionListener getCancelActionListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				print("cancel pressed");
				
				//undo any normalization
				if(feature.isNormalized() && !wasNormalized){
					Vector<String> peakToUndoNormalize = new Vector<String>();
					peakToUndoNormalize.add(feature.getName());
					model.undoNormalize(peakToUndoNormalize);
				}
				
				
				//redo any normalzation
				if(!feature.isNormalized() && wasNormalized){
					
					String name = feature.getName();
					HashMap<String, Integer> normMap = new HashMap<String, Integer>();
					normMap.put(name, originalTagCount);
					model.normalize(normMap);
				}
				
				dispose();
			}

		};
	}//end method, getCancelActionListener
}
