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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import papst.PapstModel;
import papst.filters.FilterInterface;
import papst.filters.FilterSaveWorker;


public class FilterSaveProgressDialog extends JDialog implements PropertyChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPanel;
	private JPanel buttoPpanel;
	private JButton okButton;
	private JPanel dataPanel;
	private JLabel lblNewLabel;
	private JProgressBar progressBar;

	
	/**
	 * Create the dialog.
	 */
	public FilterSaveProgressDialog(JFrame locationComponent,String message,File file,PapstModel model) {
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 469, 119);
		
		contentPanel = new JPanel();
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		buttoPpanel = new JPanel();
		contentPanel.add(buttoPpanel, BorderLayout.SOUTH);
		
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		okButton.setEnabled(false);
		buttoPpanel.add(okButton);
		
		dataPanel = new JPanel();
		contentPanel.add(dataPanel, BorderLayout.CENTER);
		dataPanel.setLayout(new BorderLayout(0, 0));
		
		lblNewLabel = new JLabel(message);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		dataPanel.add(lblNewLabel);
		
		progressBar = new JProgressBar();
		dataPanel.add(progressBar, BorderLayout.SOUTH);
		
		Vector<FilterInterface> filters = model.getAvailableFilterObjects();
		
		try{
			FilterSaveWorker worker = new FilterSaveWorker(file,filters);
			worker.addPropertyChangeListener(this);
			worker.execute();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(locationComponent);

	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if("progress".equals(evt.getPropertyName())){
			int progress = (Integer)evt.getNewValue();
			progressBar.setValue(progress);
			
			if(progress == 100){
				okButton.setEnabled(true);
			}
		}
	}

}
