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
/**
 * Adapted from:
 * http://java-swing-tips.blogspot.com/2008/09/double-click-on-each-tab-and-change-its.html
 * 
 * */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import papst.PapstModel;

public class TabTitleEditListener extends MouseAdapter implements ChangeListener {
    private final JTextField editor = new JTextField();
    private final JTabbedPane tabbedPane;
    private int editing_idx = -1;
    private int len = -1;
    private Dimension dim;
    private Component tabComponent = null;
    private PapstModel model;

    public TabTitleEditListener(final JTabbedPane tabbedPane,PapstModel model) {
        super();
        this.tabbedPane = tabbedPane;
        this.model = model;
        editor.setBorder(BorderFactory.createEmptyBorder());
        editor.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                renameTabTitle();
            }
        });
        
        editor.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ENTER) {
                    renameTabTitle();
                }else if(e.getKeyCode()==KeyEvent.VK_ESCAPE) {
                    cancelEditing();
                }else{
                    editor.setPreferredSize(editor.getText().length()>len ? null : dim);
                    tabbedPane.revalidate();
                }
            }
        });
        
        /*
        tabbedPane.getInputMap(JComponent.WHEN_FOCUSED).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "start-editing");
        tabbedPane.getActionMap().put("start-editing", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                startEditing();
            }
        });*/
    }
    
    @Override public void stateChanged(ChangeEvent e) {
        renameTabTitle();
    }
    
    @Override public void mouseClicked(MouseEvent me) {
    	
    	if(tabbedPane.getSelectedIndex() >= 0){
	        Rectangle rect = tabbedPane.getUI().getTabBounds(tabbedPane, tabbedPane.getSelectedIndex());
	        if(rect!=null && rect.contains(me.getPoint()) && me.getClickCount()==2) {
	            startEditing();
	        }else{
	            renameTabTitle();
	        }
    	}
        
    }
    
    private void startEditing() {
        editing_idx = tabbedPane.getSelectedIndex();
        tabComponent = tabbedPane.getTabComponentAt(editing_idx);
        tabbedPane.setTabComponentAt(editing_idx, editor);
        editor.setVisible(true);
        editor.setText(tabbedPane.getTitleAt(editing_idx));
        editor.selectAll();
        editor.requestFocusInWindow();
        len = editor.getText().length();
        dim = editor.getPreferredSize();
        editor.setMinimumSize(dim);
    }
    
    private void cancelEditing() {
        if(editing_idx>=0) {
            tabbedPane.setTabComponentAt(editing_idx, tabComponent);
            editor.setVisible(false);
            editing_idx = -1;
            len = -1;
            tabComponent = null;
            editor.setPreferredSize(null);
            tabbedPane.requestFocusInWindow();
        }
    }
    
    private void renameTabTitle() {
        String title = editor.getText().trim();
        if(editing_idx>=0 && !title.isEmpty()) {
            tabbedPane.setTitleAt(editing_idx, title);
            model.getResultTable(editing_idx).setName(title);
        }
        cancelEditing();
    }
    
    public void setModel(PapstModel model){
    	this.model = model;
    }
    
}
