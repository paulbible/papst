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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.JCheckBox;

import papst.PapstModel;
import papst.filters.*;
import papst.utility.CommandLog;
import papst.regions.*;
import papst.utility.NumberAwareStringComparator;


public class EditFilterDialog extends JDialog {
	
	private PapstModel model;
	private FilterInterface filter;
	private CommandLog log;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JPanel buttonPane;
	private JButton okButton;
	private JButton cancelButton;
	private JPanel singleFeatureFilterPanel;
	private JPanel doubleFeatureFilterPanel;
	private JPanel mainDataPanel;
	private JPanel filterNamePanel;
	private JLabel filterNameLbl;
	private JTextField filterNameTxtFld;
	private JPanel radioSinglePanel;
	private JPanel radioDoublePanel;
	private JRadioButton rdbtnSinglePeakFilter;
	private JRadioButton rdbtnTwoFeatureCombined;
	private JPanel singleFeatureDataPanel;
	private JPanel doubleFeatureDataPanel;
	private JLabel singleHeaderLbl;
	private JLabel doubleHeaderLbl;
	private JLabel singleFeatureSetLbl;
	private JLabel doubleFeature1Lbl;
	private JLabel singleHasValueLbl;
	private JLabel doubleFeature2Lbl;
	private JLabel doubleIsLbl;
	private JComboBox<String> singleFeatureCBox;
	private JComboBox<String> singleRelationCBox;
	private JTextField singleValueThresholdTxtFld;
	private JLabel singleInLbl;
	private ButtonGroup selectGroup;
	private JCheckBox singleUsePromoterChkBx;
	private JTextField singlePromoterUpTxtFld;
	private JTextField singlePromoterDownTxtFld;
	private JLabel singleStartMinusLbl;
	private JLabel singleStartPlusLbl;
	private JCheckBox singleUseBodyChkBx;
	private JCheckBox singleUseDownstreamChkBx;
	private JTextField singleDownstreamDistTxtFld;
	private JLabel singleEndPlusLbl;
	private JLabel doubleInLbl;
	private JCheckBox doubleUsePromoterChkBx;
	private JLabel doubleStartMinusLbl;
	private JTextField doublePromoterUpTxtFld;
	private JTextField doublePromoterDownTxtFld;
	private JLabel doubleStartPlusLbl;
	private JCheckBox doubleUseBodyChkBx;
	private JCheckBox doubleUseDownstreamChkBx;
	private JLabel doubleEndPlusLbl;
	private JTextField doubleDownstreamDistTxtFld;
	private JComboBox<String> doubleFeature_1_CBox;
	private JComboBox<String> doubleFeature_2_CBox;
	private JComboBox<String> doubleRelationCBox;
	private JTextField doubleValueThresholdTxtFld;
	private JComboBox<String> doubleOperationCBox;
	private boolean isEditMode;


	/**
	 * Create the dialog.
	 * @wbp.parser.constructor
	 */
	public EditFilterDialog(JFrame locationComponent,PapstModel model,CommandLog log) {
		this.model = model;
		this.log = log;
		this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
		
		
		this.isEditMode = false;
		
		selectGroup = new ButtonGroup();
		init();
		
		selectGroup.add(rdbtnSinglePeakFilter);
		selectGroup.add(rdbtnTwoFeatureCombined);
		
		rdbtnSinglePeakFilter.addActionListener(getRadioButtonListener());
		rdbtnTwoFeatureCombined.addActionListener(getRadioButtonListenerDouble());
		
		this.setTitle("Create a new feature filter");
		
		//selectGroup.add
		setDefaultValues();
		enableSingle();
		
		filter = null;
		
		//set default promoter distances
		singlePromoterUpTxtFld.setText(model.getDefaultUpstream()+"");
		singlePromoterDownTxtFld.setText(model.getDefaultDownstream()+"");
		
		doublePromoterUpTxtFld.setText(model.getDefaultUpstream()+"");
		doublePromoterDownTxtFld.setText(model.getDefaultDownstream()+"");
		
		this.setLocationRelativeTo(locationComponent);
	}//constructor
	
	
	
	/**
	 * Create the dialog.
	 */
	public EditFilterDialog(JFrame locationComponent, PapstModel model,CommandLog log,FilterInterface inFilter) {
		this.model = model;
		this.log = log;
		this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
		
		this.isEditMode = true;
		
		selectGroup = new ButtonGroup();
		init();
		
		selectGroup.add(rdbtnSinglePeakFilter);
		selectGroup.add(rdbtnTwoFeatureCombined);
		
		rdbtnSinglePeakFilter.addActionListener(getRadioButtonListener());
		rdbtnTwoFeatureCombined.addActionListener(getRadioButtonListenerDouble());
		
		this.setTitle("Edit exisiting feature filter");
		
		//selectGroup.add
		setDefaultValues();
		
		initFilter(inFilter);
		
		filter = null;
		
		this.setLocationRelativeTo(locationComponent);
	}//constructor
	
	private void initFilter(FilterInterface inFilter){

		//TODO initialize dialog with filter
		if(inFilter instanceof SingleFeatureFilter){
			print("Single feature filter edit");
			initSingleFeatureFilter((SingleFeatureFilter)inFilter);
			
		}else if(inFilter instanceof AllFeaturesFilter){
			print("All feature filter edit");
			initAllFeaturesFilter((AllFeaturesFilter)inFilter);
			
		}else if(inFilter instanceof TwoFeatureFilter){
			print("Double feature filter edit");
			initTwoFeatureFilter((TwoFeatureFilter)inFilter);
		}
		
		
	}//end method intiFilter
	
	private void initSingleFeatureFilter(SingleFeatureFilter filter){
		
		//set filter name
		filterNameTxtFld.setText(filter.getName());
		
		//enable single side
		enableSingle();
		
		//set combo box
		singleFeatureCBox.setSelectedItem(filter.getTrack());
		
		//set relathionship
		singleRelationCBox.setSelectedIndex(filter.getRelationship());
		
		//set threshold
		singleValueThresholdTxtFld.setText(""+filter.getThreshold());
		
		//set region
		RegionModifier modifier = filter.getModifier();
		
		if(modifier.usePromoter()){
			singleUsePromoterChkBx.setSelected(true);
			singlePromoterUpTxtFld.setText(""+modifier.getPromoterUpDistance());
			singlePromoterDownTxtFld.setText(""+modifier.getPromoterDownDistance());
		}else{
			singleUsePromoterChkBx.setSelected(false);
		}
		
		if(modifier.useBody()){
			singleUseBodyChkBx.setSelected(true);
		}else{
			singleUseBodyChkBx.setSelected(false);
		}
		
		if(modifier.useDownstream()){
			singleUseDownstreamChkBx.setSelected(true);
			singleDownstreamDistTxtFld.setText(""+modifier.getDownstreamDistance());
		}else{
			singleUseDownstreamChkBx.setSelected(false);
		}
		
	}//end method,initSingleFeatureFilter
	
	private void initAllFeaturesFilter(AllFeaturesFilter filter){
		
		//set filter name
		filterNameTxtFld.setText(filter.getName());
		
		//enable single side
		enableSingle();
		
		//set combo box
		singleFeatureCBox.setSelectedItem("all");
		
		//set relathionship
		singleRelationCBox.setSelectedIndex(filter.getRelationship());
		
		//set threshold
		singleValueThresholdTxtFld.setText(""+filter.getThreshold());
		
		//set region
		RegionModifier modifier = filter.getModifier();
		
		if(modifier.usePromoter()){
			singleUsePromoterChkBx.setSelected(true);
			singlePromoterUpTxtFld.setText(""+modifier.getPromoterUpDistance());
			singlePromoterDownTxtFld.setText(""+modifier.getPromoterDownDistance());
		}else{
			singleUsePromoterChkBx.setSelected(false);
		}
		
		if(modifier.useBody()){
			singleUseBodyChkBx.setSelected(true);
		}else{
			singleUseBodyChkBx.setSelected(false);
		}
		
		if(modifier.useDownstream()){
			singleUseDownstreamChkBx.setSelected(true);
			singleDownstreamDistTxtFld.setText(""+modifier.getDownstreamDistance());
		}else{
			singleUseDownstreamChkBx.setSelected(false);
		}
		
	}//end method, initAllFeaturesFilter
	
	
	private void initTwoFeatureFilter(TwoFeatureFilter filter){
		
		//set filter name
		filterNameTxtFld.setText(filter.getName());
		
		//enable double side
		enableDouble();
		
		//set combo boxes
		doubleFeature_1_CBox.setSelectedItem(filter.getTrackA());
		doubleFeature_2_CBox.setSelectedItem(filter.getTrackB());
		
		//set operation
		doubleOperationCBox.setSelectedIndex(filter.getOperation());
		
		//set relathionship
		doubleRelationCBox.setSelectedIndex(filter.getRelationship());
		
		//set threshold
		doubleValueThresholdTxtFld.setText(""+filter.getThreshold());
		
		//set region
		RegionModifier modifier = filter.getModifier();
		
		if(modifier.usePromoter()){
			doubleUsePromoterChkBx.setSelected(true);
			doublePromoterUpTxtFld.setText(""+modifier.getPromoterUpDistance());
			doublePromoterDownTxtFld.setText(""+modifier.getPromoterDownDistance());
		}else{
			doubleUsePromoterChkBx.setSelected(false);
		}
		
		if(modifier.useBody()){
			doubleUseBodyChkBx.setSelected(true);
		}else{
			doubleUseBodyChkBx.setSelected(false);
		}
		
		if(modifier.useDownstream()){
			doubleUseDownstreamChkBx.setSelected(true);
			doubleDownstreamDistTxtFld.setText(""+modifier.getDownstreamDistance());
		}else{
			doubleUseDownstreamChkBx.setSelected(false);
		}
		
	}//end method, initAllFeaturesFilter


	private void init() {
		setBounds(100, 100, 625, 435);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			mainDataPanel = new JPanel();
			contentPanel.add(mainDataPanel);
			mainDataPanel.setLayout(new BoxLayout(mainDataPanel, BoxLayout.X_AXIS));
			{
				singleFeatureFilterPanel = new JPanel();
				singleFeatureFilterPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
				mainDataPanel.add(singleFeatureFilterPanel);
				singleFeatureFilterPanel.setLayout(new BorderLayout(0, 0));
				{
					radioSinglePanel = new JPanel();
					singleFeatureFilterPanel.add(radioSinglePanel, BorderLayout.NORTH);
					{
						rdbtnSinglePeakFilter = new JRadioButton("Single Feature Filter");
						rdbtnSinglePeakFilter.setToolTipText("Create a filter for a single peak set");
						rdbtnSinglePeakFilter.setSelected(true);
						radioSinglePanel.add(rdbtnSinglePeakFilter);
					}
				}
				{
					singleFeatureDataPanel = new JPanel();
					singleFeatureFilterPanel.add(singleFeatureDataPanel, BorderLayout.CENTER);
					singleFeatureDataPanel.setLayout(null);
					
					singleHeaderLbl = new JLabel("Find features where ...");
					singleHeaderLbl.setBounds(10, 11, 219, 14);
					singleFeatureDataPanel.add(singleHeaderLbl);
					
					singleFeatureSetLbl = new JLabel("feature set");
					singleFeatureSetLbl.setBounds(10, 57, 67, 14);
					singleFeatureDataPanel.add(singleFeatureSetLbl);
					
					singleHasValueLbl = new JLabel("has value");
					singleHasValueLbl.setBounds(10, 112, 67, 14);
					singleFeatureDataPanel.add(singleHasValueLbl);
					
					singleFeatureCBox = new JComboBox<String>();
					singleFeatureCBox.setToolTipText("Select a peak set of feature for filtering");
					singleFeatureCBox.setBounds(87, 54, 195, 20);
					singleFeatureDataPanel.add(singleFeatureCBox);
					
					singleRelationCBox = new JComboBox<String>();
					singleRelationCBox.setToolTipText("Select a relationship to apply to the threshold");
					singleRelationCBox.setBounds(87, 109, 82, 20);
					singleFeatureDataPanel.add(singleRelationCBox);
					
					singleValueThresholdTxtFld = new JTextField();
					singleValueThresholdTxtFld.setToolTipText("Input a threshold");
					singleValueThresholdTxtFld.setHorizontalAlignment(SwingConstants.RIGHT);
					singleValueThresholdTxtFld.setText("0.0");
					singleValueThresholdTxtFld.setBounds(179, 109, 103, 20);
					singleFeatureDataPanel.add(singleValueThresholdTxtFld);
					singleValueThresholdTxtFld.setColumns(10);
					
					singleInLbl = new JLabel("in ...");
					singleInLbl.setBounds(10, 155, 44, 14);
					singleFeatureDataPanel.add(singleInLbl);
					
					singleUsePromoterChkBx = new JCheckBox("Promoter");
					singleUsePromoterChkBx.setToolTipText("Turn the promoter region on or off");
					singleUsePromoterChkBx.setSelected(true);
					singleUsePromoterChkBx.setBounds(10, 176, 122, 23);
					singleFeatureDataPanel.add(singleUsePromoterChkBx);
					
					singlePromoterUpTxtFld = new JFormattedTextField(new DecimalFormat("0"));
					singlePromoterUpTxtFld.setToolTipText("Distance upstream of the TSS");
					singlePromoterUpTxtFld.setHorizontalAlignment(SwingConstants.RIGHT);
					singlePromoterUpTxtFld.setText("2000");
					singlePromoterUpTxtFld.setBounds(138, 177, 67, 20);
					singleFeatureDataPanel.add(singlePromoterUpTxtFld);
					singlePromoterUpTxtFld.setColumns(10);
					
					singlePromoterDownTxtFld = new JFormattedTextField(new DecimalFormat("0"));
					singlePromoterDownTxtFld.setToolTipText("Distance downstream of the TSS");
					singlePromoterDownTxtFld.setHorizontalAlignment(SwingConstants.RIGHT);
					singlePromoterDownTxtFld.setText("200");
					singlePromoterDownTxtFld.setBounds(215, 177, 67, 20);
					singleFeatureDataPanel.add(singlePromoterDownTxtFld);
					singlePromoterDownTxtFld.setColumns(10);
					
					singleStartMinusLbl = new JLabel("- Start");
					singleStartMinusLbl.setBounds(138, 165, 55, 14);
					singleFeatureDataPanel.add(singleStartMinusLbl);
					
					singleStartPlusLbl = new JLabel("+ Start");
					singleStartPlusLbl.setBounds(215, 165, 46, 14);
					singleFeatureDataPanel.add(singleStartPlusLbl);
					
					singleUseBodyChkBx = new JCheckBox("Body");
					singleUseBodyChkBx.setToolTipText("Turn the gene body on or off");
					singleUseBodyChkBx.setBounds(10, 212, 103, 23);
					singleFeatureDataPanel.add(singleUseBodyChkBx);
					
					singleUseDownstreamChkBx = new JCheckBox("Downstream");
					singleUseDownstreamChkBx.setToolTipText("Turn the downstream region on or off");
					singleUseDownstreamChkBx.setBounds(10, 246, 128, 23);
					singleFeatureDataPanel.add(singleUseDownstreamChkBx);
					
					singleDownstreamDistTxtFld = new JFormattedTextField(new DecimalFormat("0"));
					singleDownstreamDistTxtFld.setToolTipText("Distance downstream of the TES");
					singleDownstreamDistTxtFld.setHorizontalAlignment(SwingConstants.RIGHT);
					singleDownstreamDistTxtFld.setText("500");
					singleDownstreamDistTxtFld.setBounds(138, 249, 67, 20);
					singleFeatureDataPanel.add(singleDownstreamDistTxtFld);
					singleDownstreamDistTxtFld.setColumns(10);
					
					singleEndPlusLbl = new JLabel("+ End");
					singleEndPlusLbl.setBounds(138, 236, 46, 14);
					singleFeatureDataPanel.add(singleEndPlusLbl);
				}
			}
			{
				doubleFeatureFilterPanel = new JPanel();
				doubleFeatureFilterPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
				mainDataPanel.add(doubleFeatureFilterPanel);
				doubleFeatureFilterPanel.setLayout(new BorderLayout(0, 0));
				{
					radioDoublePanel = new JPanel();
					doubleFeatureFilterPanel.add(radioDoublePanel, BorderLayout.NORTH);
					{
						rdbtnTwoFeatureCombined = new JRadioButton("Double Feature Filter");
						rdbtnTwoFeatureCombined.setToolTipText("Create a filter for two related peak sets");
						radioDoublePanel.add(rdbtnTwoFeatureCombined);
					}
				}
				{
					doubleFeatureDataPanel = new JPanel();
					doubleFeatureFilterPanel.add(doubleFeatureDataPanel, BorderLayout.CENTER);
					doubleFeatureDataPanel.setLayout(null);
					
					doubleHeaderLbl = new JLabel("Find features where ...");
					doubleHeaderLbl.setBounds(10, 11, 232, 14);
					doubleFeatureDataPanel.add(doubleHeaderLbl);
					
					doubleFeature1Lbl = new JLabel("feature set");
					doubleFeature1Lbl.setBounds(10, 36, 82, 14);
					doubleFeatureDataPanel.add(doubleFeature1Lbl);
					
					doubleFeature2Lbl = new JLabel("feature set");
					doubleFeature2Lbl.setBounds(10, 92, 82, 14);
					doubleFeatureDataPanel.add(doubleFeature2Lbl);
					
					doubleIsLbl = new JLabel("is");
					doubleIsLbl.setBounds(10, 120, 46, 14);
					doubleFeatureDataPanel.add(doubleIsLbl);
					
					doubleInLbl = new JLabel("in ...");
					doubleInLbl.setBounds(10, 155, 44, 14);
					doubleFeatureDataPanel.add(doubleInLbl);
					
					doubleUsePromoterChkBx = new JCheckBox("Promoter");
					doubleUsePromoterChkBx.setToolTipText("Turn the promoter region on or off");
					doubleUsePromoterChkBx.setSelected(true);
					doubleUsePromoterChkBx.setBounds(10, 176, 128, 23);
					doubleFeatureDataPanel.add(doubleUsePromoterChkBx);
					
					doubleStartMinusLbl = new JLabel("- Start");
					doubleStartMinusLbl.setBounds(144, 165, 55, 14);
					doubleFeatureDataPanel.add(doubleStartMinusLbl);
					
					doublePromoterUpTxtFld = new JFormattedTextField(new DecimalFormat("0"));
					doublePromoterUpTxtFld.setToolTipText("Distance upstream of the TSS");
					doublePromoterUpTxtFld.setText("2000");
					doublePromoterUpTxtFld.setHorizontalAlignment(SwingConstants.RIGHT);
					doublePromoterUpTxtFld.setColumns(10);
					doublePromoterUpTxtFld.setBounds(144, 177, 67, 20);
					doubleFeatureDataPanel.add(doublePromoterUpTxtFld);
					
					doublePromoterDownTxtFld = new JTextField();
					doublePromoterDownTxtFld.setToolTipText("Distance downstream of the TSS");
					doublePromoterDownTxtFld.setText("200");
					doublePromoterDownTxtFld.setHorizontalAlignment(SwingConstants.RIGHT);
					doublePromoterDownTxtFld.setColumns(10);
					doublePromoterDownTxtFld.setBounds(221, 177, 67, 20);
					doubleFeatureDataPanel.add(doublePromoterDownTxtFld);
					
					doubleStartPlusLbl = new JLabel("+ Start");
					doubleStartPlusLbl.setBounds(221, 165, 46, 14);
					doubleFeatureDataPanel.add(doubleStartPlusLbl);
					
					doubleUseBodyChkBx = new JCheckBox("Body");
					doubleUseBodyChkBx.setToolTipText("Turn the gene body on or off");
					doubleUseBodyChkBx.setBounds(10, 212, 106, 23);
					doubleFeatureDataPanel.add(doubleUseBodyChkBx);
					
					doubleUseDownstreamChkBx = new JCheckBox("Downstream");
					doubleUseDownstreamChkBx.setToolTipText("Turn the downstream region on or off");
					doubleUseDownstreamChkBx.setBounds(10, 246, 128, 23);
					doubleFeatureDataPanel.add(doubleUseDownstreamChkBx);
					
					doubleEndPlusLbl = new JLabel("+ End");
					doubleEndPlusLbl.setBounds(144, 236, 46, 14);
					doubleFeatureDataPanel.add(doubleEndPlusLbl);
					
					doubleDownstreamDistTxtFld = new JFormattedTextField(new DecimalFormat("0"));
					doubleDownstreamDistTxtFld.setToolTipText("Distance downstream of the TES");
					doubleDownstreamDistTxtFld.setText("500");
					doubleDownstreamDistTxtFld.setHorizontalAlignment(SwingConstants.RIGHT);
					doubleDownstreamDistTxtFld.setColumns(10);
					doubleDownstreamDistTxtFld.setBounds(144, 249, 67, 20);
					doubleFeatureDataPanel.add(doubleDownstreamDistTxtFld);
					
					doubleFeature_1_CBox = new JComboBox<String>();
					doubleFeature_1_CBox.setToolTipText("Select a peak set of feature for filtering");
					doubleFeature_1_CBox.setBounds(97, 33, 191, 20);
					doubleFeatureDataPanel.add(doubleFeature_1_CBox);
					
					doubleFeature_2_CBox = new JComboBox<String>();
					doubleFeature_2_CBox.setToolTipText("Select a peak set of feature for filtering");
					doubleFeature_2_CBox.setBounds(97, 89, 191, 20);
					doubleFeatureDataPanel.add(doubleFeature_2_CBox);
					
					doubleRelationCBox = new JComboBox<String>();
					doubleRelationCBox.setToolTipText("Select a relationship to apply to the threshold");
					doubleRelationCBox.setBounds(97, 117, 75, 20);
					doubleFeatureDataPanel.add(doubleRelationCBox);
					
					doubleValueThresholdTxtFld = new JTextField();
					doubleValueThresholdTxtFld.setToolTipText("Input a threshold");
					doubleValueThresholdTxtFld.setHorizontalAlignment(SwingConstants.RIGHT);
					doubleValueThresholdTxtFld.setText("0.0");
					doubleValueThresholdTxtFld.setBounds(182, 117, 106, 20);
					doubleFeatureDataPanel.add(doubleValueThresholdTxtFld);
					doubleValueThresholdTxtFld.setColumns(10);
					
					doubleOperationCBox = new JComboBox<String>();
					doubleOperationCBox.setToolTipText("Select an operation to apply to both peak sets");
					doubleOperationCBox.setBounds(97, 61, 191, 20);
					doubleFeatureDataPanel.add(doubleOperationCBox);
				}
			}
		}
		{
			filterNamePanel = new JPanel();
			contentPanel.add(filterNamePanel, BorderLayout.NORTH);
			filterNamePanel.setLayout(new BorderLayout(0, 0));
			{
				filterNameLbl = new JLabel("Filter Name ");
				filterNamePanel.add(filterNameLbl, BorderLayout.WEST);
			}
			{
				filterNameTxtFld = new JTextField();
				filterNameTxtFld.setText("default_filter_name");
				filterNamePanel.add(filterNameTxtFld);
				filterNameTxtFld.setColumns(10);
			}
		}
		{
			buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setToolTipText("Create the current filter");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(getOkListener());
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setToolTipText("Close this dialog and take no action");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(getCancelListener());
			}
		}
	}//end method, init
	
	
	private void setDefaultValues(){
		
		//populate check boxes
		singleFeatureCBox.addItem("all");
		Vector<String> names = model.getFeatureNames();
		Collections.sort(names,NumberAwareStringComparator.INSTANCE);
		
		for(String featureName: names){
			singleFeatureCBox.addItem(featureName);
			doubleFeature_1_CBox.addItem(featureName);
			doubleFeature_2_CBox.addItem(featureName);
		}
		
		for(String relation:AbstractFilter.RelationStrings){
			singleRelationCBox.addItem(relation);
			doubleRelationCBox.addItem(relation);
		}
		
		for(String operation:AbstractFilter.OperationStrings){
			doubleOperationCBox.addItem(operation);
		}
		
	}//end method, setDefaultValues
	
	
	private void switchFilterType(){
		
		//single selected
		if(rdbtnSinglePeakFilter.isSelected()){
			enableSingle();
		}//double selected
		else{ 
			enableDouble();
		}
		
	}
	
	private void enableSingle(){
		rdbtnSinglePeakFilter.setSelected(true);
		setEnableSingleSide(true);
		setEnableDoubleSide(false);
	}
	
	private void enableDouble(){
		rdbtnTwoFeatureCombined.setSelected(true);
		setEnableSingleSide(false);
		setEnableDoubleSide(true);
	}
	
	
	private void setEnableSingleSide(boolean boolFlag){
		//Lables
		singleHeaderLbl.setEnabled(boolFlag);
		singleFeatureSetLbl.setEnabled(boolFlag);
		singleHasValueLbl.setEnabled(boolFlag);
		singleInLbl.setEnabled(boolFlag);
		singleStartMinusLbl.setEnabled(boolFlag);
		singleStartPlusLbl.setEnabled(boolFlag);
		singleEndPlusLbl.setEnabled(boolFlag);
		
		//Combo boxes
		singleFeatureCBox.setEnabled(boolFlag);
		singleRelationCBox.setEnabled(boolFlag);
		
		//Text Fields
		singleValueThresholdTxtFld.setEnabled(boolFlag);
		singlePromoterUpTxtFld.setEnabled(boolFlag);
		singlePromoterDownTxtFld.setEnabled(boolFlag);
		singleDownstreamDistTxtFld.setEnabled(boolFlag);
		
		//check boxes
		singleUsePromoterChkBx.setEnabled(boolFlag);
		singleUseBodyChkBx.setEnabled(boolFlag);
		singleUseDownstreamChkBx.setEnabled(boolFlag);
	}//end method, setEnableSingleSide
	
	private void setEnableDoubleSide(boolean boolFlag){
		//Lables
		doubleHeaderLbl.setEnabled(boolFlag);
		doubleFeature1Lbl.setEnabled(boolFlag);
		doubleFeature2Lbl.setEnabled(boolFlag);
		doubleIsLbl.setEnabled(boolFlag);
		doubleInLbl.setEnabled(boolFlag);
		doubleStartMinusLbl.setEnabled(boolFlag);
		doubleStartPlusLbl.setEnabled(boolFlag);
		doubleEndPlusLbl.setEnabled(boolFlag);
		
		//Combo boxes
		doubleFeature_1_CBox.setEnabled(boolFlag);
		doubleFeature_2_CBox.setEnabled(boolFlag);
		doubleRelationCBox.setEnabled(boolFlag);
		doubleOperationCBox.setEnabled(boolFlag);
		
		//Text Fields
		doubleValueThresholdTxtFld.setEnabled(boolFlag);
		doublePromoterUpTxtFld.setEnabled(boolFlag);
		doublePromoterDownTxtFld.setEnabled(boolFlag);
		doubleDownstreamDistTxtFld.setEnabled(boolFlag);
		
		//check boxes
		doubleUsePromoterChkBx.setEnabled(boolFlag);
		doubleUseBodyChkBx.setEnabled(boolFlag);
		doubleUseDownstreamChkBx.setEnabled(boolFlag);
	}//end method, setEnableDoubleSide
	
	
	private ActionListener getCancelListener(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				print("cancel button pressed");
				
				dispose();
			}
		};
		
	}//end method, getCancelListener
	
	private ActionListener getOkListener(){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//TODO OK button pressed
				print("ok button pressed");
				
				String filterName = filterNameTxtFld.getText();
				print("filterName "+filterName);
				
				if(model.isFilterNameUnique(filterName) || isEditMode){
					
					filter = getFilterFromDialog();
					
					if(filter != null){
						print("filter is good");
						print(filter.toString());
						dispose();
					}else{
						print("filter null");
					}
					
				}else{
					//filter name already in use
					JOptionPane.showMessageDialog(contentPanel,
							"The filer name '"+filterName+"' is already in use.\nPlease choose another name.",
							"Filter Name Already in Use.",
							JOptionPane.PLAIN_MESSAGE);
				}
				
			}//end interface method, actionPerformed
		};
		
	}//end method, getOkListener
	
	
	//Logging method for printing information
	public void print(String msg){
		System.out.println("EditFilterDialog: "+msg);
		log.addCommand("EditFilterDialog: "+msg);
	}//end method, print
	
	
	private ActionListener getRadioButtonListener(){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//print("radio button pressed");
				switchFilterType();
				
			}
		};
	}// end method, getRadioButtonListener
	
	private ActionListener getRadioButtonListenerDouble(){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//print("radio button pressed");
				if(model.getNumFeatures() < 2){
					JOptionPane.showMessageDialog(contentPanel,
							"There must be at least 2 features loaded for a double feature filter.",
							"Must have 2 features loaded",
							JOptionPane.PLAIN_MESSAGE);
					setEnableSingleSide(true);
					setEnableDoubleSide(false);
					rdbtnSinglePeakFilter.setSelected(true);
				}else{
					switchFilterType();
				}
				
			}
		};
	}// end method, getRadioButtonListener
	
	public FilterInterface getFilter(){
		return filter;
	}//end method, getFilter
	
	
	private FilterInterface getFilterFromDialog(){
		
		if(rdbtnSinglePeakFilter.isSelected()){
			
			String track = (String)singleFeatureCBox.getSelectedItem();
			
			if(track.equalsIgnoreCase("all")){
				return getAllFilter();
			}else{
				return getSingleFilter();
			}
		}else{
			return getDoubleFilter();
		}
		
	}//end method, getFilterFromDialog
	
	private AllFeaturesFilter getAllFilter(){
		if(areInputsGood()){
			String name = filterNameTxtFld.getText();
			String track = (String)singleFeatureCBox.getSelectedItem();
			
			double value = Double.parseDouble(singleValueThresholdTxtFld.getText());
			
			int relationship = singleRelationCBox.getSelectedIndex();
			
			int promoterUp = Integer.parseInt(singlePromoterUpTxtFld.getText());
			int promoterDown = Integer.parseInt(singlePromoterDownTxtFld.getText());
			int downstream = Integer.parseInt(singleDownstreamDistTxtFld.getText());
			
			RegionModifier modifier = new RegionModifier(singleUsePromoterChkBx.isSelected(),
														 singleUseBodyChkBx.isSelected(),
														 singleUseDownstreamChkBx.isSelected(),
														 promoterUp, promoterDown, downstream);
			
			return new AllFeaturesFilter(name,track,value,relationship,modifier,model);
		}else{
			return null;
		}
		
		
	}
	
	private SingleFeatureFilter getSingleFilter(){
		if(areInputsGood()){
			String name = filterNameTxtFld.getText();
			String track = (String)singleFeatureCBox.getSelectedItem();
			
			double value = Double.parseDouble(singleValueThresholdTxtFld.getText());
			
			int relationship = singleRelationCBox.getSelectedIndex();
			
			int promoterUp = Integer.parseInt(singlePromoterUpTxtFld.getText());
			int promoterDown = Integer.parseInt(singlePromoterDownTxtFld.getText());
			int downstream = Integer.parseInt(singleDownstreamDistTxtFld.getText());
			
			RegionModifier modifier = new RegionModifier(singleUsePromoterChkBx.isSelected(),
														 singleUseBodyChkBx.isSelected(),
														 singleUseDownstreamChkBx.isSelected(),
														 promoterUp, promoterDown, downstream);
			
			return new SingleFeatureFilter(name,track,value,relationship,modifier);
			
		}else{
			return null;
		}
	}//end method, getSingleFilter
	
	private TwoFeatureFilter getDoubleFilter(){
		
		if(areInputsGood()){
			
			String name = filterNameTxtFld.getText();
			String track1 = (String)doubleFeature_1_CBox.getSelectedItem();
			String track2 = (String)doubleFeature_2_CBox.getSelectedItem();
			
			double value = Double.parseDouble(doubleValueThresholdTxtFld.getText());
			
			int operation    = doubleOperationCBox.getSelectedIndex();
			
			int relationship = doubleRelationCBox.getSelectedIndex();
			
			
			int promoterUp   = 0;
			int promoterDown = 0; 
			int downstream   = 0;
			
			if(doubleUsePromoterChkBx.isSelected()){
				promoterUp = Integer.parseInt(doublePromoterUpTxtFld.getText());
				promoterDown = Integer.parseInt(doublePromoterDownTxtFld.getText());
			}
			
			if(doubleUseDownstreamChkBx.isSelected()){
				downstream = Integer.parseInt(doubleDownstreamDistTxtFld.getText());
			}
			
			RegionModifier modifier = new RegionModifier(doubleUsePromoterChkBx.isSelected(),
														 doubleUseBodyChkBx.isSelected(),
														 doubleUseDownstreamChkBx.isSelected(),
														 promoterUp, promoterDown, downstream);
			
			//TODO Get double track filter object
			return new TwoFeatureFilter(name,track1,track2,operation,relationship,value,modifier);
		}else{
			return null;
		}
	}//end method, getDoubleFilter
	
	public void clearHighlights(){
		
		//Single
		clearHighlightTextField(singleValueThresholdTxtFld);
		clearHighlightTextField(singlePromoterUpTxtFld);
		clearHighlightTextField(singlePromoterDownTxtFld);
		clearHighlightTextField(singleDownstreamDistTxtFld);
		
		
		clearHighlightCheckBox(singleUsePromoterChkBx);
		clearHighlightCheckBox(singleUseBodyChkBx);
		clearHighlightCheckBox(singleUseDownstreamChkBx);
		
		
		//Double
		clearHighlightTextField(doubleValueThresholdTxtFld);
		clearHighlightTextField(doublePromoterUpTxtFld);
		clearHighlightTextField(doublePromoterDownTxtFld);
		clearHighlightTextField(doubleDownstreamDistTxtFld);
		
		clearHighlightComboBox(doubleFeature_1_CBox);
		clearHighlightComboBox(doubleFeature_2_CBox);
		
		
		clearHighlightCheckBox(doubleUsePromoterChkBx);
		clearHighlightCheckBox(doubleUseBodyChkBx);
		clearHighlightCheckBox(doubleUseDownstreamChkBx);
		
	}
	
	public static void highlightComboBox(JComboBox<String> cBox){
		cBox.setBorder(BorderFactory.createLineBorder(Color.red,2));
	}
	
	public static void clearHighlightComboBox(JComboBox<String> cBox){
		cBox.setBorder(UIManager.getBorder("ComboBox.border"));
	}
	
	public static void highlightTextField(JTextField field){
		field.setBorder(BorderFactory.createLineBorder(Color.red,2));
	}
	
	public static void clearHighlightTextField(JTextField field){
		field.setBorder(UIManager.getBorder("TextField.border"));
	}
	
	public static void highlightCheckBox(JCheckBox chkBox){
		chkBox.setBorder(BorderFactory.createLineBorder(Color.red,2));
		chkBox.setBorderPainted(true);
	}
	
	public static void clearHighlightCheckBox(JCheckBox chkBox){
		chkBox.setBorder(UIManager.getBorder("CheckBox.border"));
		chkBox.setBorderPainted(false);
	}
	
	
	public boolean areInputsGood(){
		clearHighlights();
		if(rdbtnSinglePeakFilter.isSelected()){
			return areSingleInputsGood();
		}else{
			return areDoubleInputsGood();
		}
	}//end  method, areInputsGood
	
	
	public boolean areSingleInputsGood(){
		
		StringBuffer errorMsg = new StringBuffer();
		int errorCount = 0;
		
		//threshold value
		try{
			Double.parseDouble(singleValueThresholdTxtFld.getText().trim());
		}catch(Exception e){
			errorMsg.append("Threshold must be a numeric value.\n");
			highlightTextField(singleValueThresholdTxtFld);
			++errorCount;
		}
		
		//promoter checkbox
		if(singleUsePromoterChkBx.isSelected()){
			try{
				Integer.parseInt(singlePromoterUpTxtFld.getText().trim());
			}catch(Exception e){
				errorMsg.append("Promoter upstream distance must be an integer value.\n");
				highlightTextField(singlePromoterUpTxtFld);
				++errorCount;
			}
			
			try{
				Integer.parseInt(singlePromoterDownTxtFld.getText().trim());
			}catch(Exception e){
				errorMsg.append("Promoter downstream distance must be an integer value.\n");
				highlightTextField(singlePromoterDownTxtFld);
				++errorCount;
			}
		}
		
		if(singleUseDownstreamChkBx.isSelected()){
			try{
				Integer.parseInt(singleDownstreamDistTxtFld.getText().trim());
			}catch(Exception e){
				errorMsg.append("Downstream distance must be an integer value.\n");
				highlightTextField(singleDownstreamDistTxtFld);
				++errorCount;
			}
		}
		
		if(!singleUsePromoterChkBx.isSelected() && !singleUseBodyChkBx.isSelected() && !singleUseDownstreamChkBx.isSelected()){
			errorMsg.append("At least one of the 3 regions must be selected.");
			
			highlightCheckBox(singleUsePromoterChkBx);
			highlightCheckBox(singleUseBodyChkBx);
			highlightCheckBox(singleUseDownstreamChkBx);
			++errorCount;
		}
		
		if(errorCount > 0){
			
			JOptionPane.showMessageDialog(contentPanel,
					"There " + (errorCount == 1 ? "is ":"are ") +errorCount+" error"+(errorCount == 1 ? "":"s")+" with this filter.\n\n"+errorMsg.toString(),
					"Filter Error",
					JOptionPane.PLAIN_MESSAGE);
			
			return false;
		}else{
			return true;
		}
	}//end method, areSingleInputsGood
	
	
	public boolean areDoubleInputsGood(){
		
		StringBuffer errorMsg = new StringBuffer();
		int errorCount = 0;
		
		int track1 = doubleFeature_1_CBox.getSelectedIndex();
		int track2 = doubleFeature_2_CBox.getSelectedIndex();
		if(track1 == track2){
			errorMsg.append("Both tracks should be different from one another.\n");
			highlightComboBox(doubleFeature_1_CBox);
			highlightComboBox(doubleFeature_2_CBox);
			++errorCount;
		}
		
		//threshold value
		try{
			Double.parseDouble(doubleValueThresholdTxtFld.getText().trim());
		}catch(Exception e){
			errorMsg.append("Threshold must be a numeric value.\n");
			highlightTextField(doubleValueThresholdTxtFld);
			++errorCount;
		}
		
		//promoter checkbox
		if(doubleUsePromoterChkBx.isSelected()){
			try{
				Integer.parseInt(doublePromoterUpTxtFld.getText().trim());
			}catch(Exception e){
				errorMsg.append("Promoter upstream distance must be an integer value.\n");
				highlightTextField(doublePromoterUpTxtFld);
				++errorCount;
			}
			
			try{
				Integer.parseInt(doublePromoterDownTxtFld.getText().trim());
			}catch(Exception e){
				errorMsg.append("Promoter downstream distance must be an integer value.\n");
				highlightTextField(doublePromoterDownTxtFld);
				++errorCount;
			}
		}
		
		if(doubleUseDownstreamChkBx.isSelected()){
			try{
				Integer.parseInt(doubleDownstreamDistTxtFld.getText().trim());
			}catch(Exception e){
				errorMsg.append("Downstream distance must be an integer value.\n");
				highlightTextField(doubleDownstreamDistTxtFld);
				++errorCount;
			}
		}
		
		if(!doubleUsePromoterChkBx.isSelected() && !doubleUseBodyChkBx.isSelected() && !doubleUseDownstreamChkBx.isSelected()){
			errorMsg.append("At least one of the 3 regions must be selected.");
			
			highlightCheckBox(doubleUsePromoterChkBx);
			highlightCheckBox(doubleUseBodyChkBx);
			highlightCheckBox(doubleUseDownstreamChkBx);
			++errorCount;
		}
		
		if(errorCount > 0){
			
			JOptionPane.showMessageDialog(contentPanel,
					"There " + (errorCount == 1 ? "is ":"are ") +errorCount+" error"+(errorCount == 1 ? "":"s")+" with this filter.\n\n"+errorMsg.toString(),
					"Filter Error",
					JOptionPane.PLAIN_MESSAGE);
			
			return false;
		}else{
			return true;
		}
	}//end method, areDoubleInputsGood
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}//end class, EditFilterDialog

