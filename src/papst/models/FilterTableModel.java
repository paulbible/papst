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
package papst.models;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import papst.PapstModel;

/********************************************************
 *  Class FilterTableModel
 * ******************************************************
 */
public class FilterTableModel extends AbstractTableModel  implements java.io.Serializable{
	
	private PapstModel model;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public FilterTableModel(PapstModel inModel){
		model = inModel;
	}
	
	

	@Override
	public int getColumnCount() {
		return 2;
	}
	
	public String getColumnName(int col) {
        switch(col){
        	case 0:
        		return "Name";
        	default:
        		return "Selected";
        }
    }
	
	@Override
	public Class<?> getColumnClass(int column){
		if(column == 1){
			return Boolean.class;
		}else{
			return Object.class;
		}
	}
	

	@Override
	public int getRowCount() {
		//TODO
		return model.getNumFilters();
	}

	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
	
		Vector<String> names = model.getFilterNames();
		String name = names.elementAt(rowIndex);
		switch(columnIndex){
			case 0:
				return name;
			default:
				return model.isFilterEnabled(name);
		}
	}
	
	
	@Override
	public void setValueAt(Object value,int rowIndex, int columnIndex) {
		Vector<String> names = model.getFilterNames();
		String name = names.elementAt(rowIndex);
		if(columnIndex == 1){
			model.setFilterEnabled(name, (Boolean)value);
		}
	}
	
	
	@Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex == 1){
        	return true;
        }else{
        	return false;
        }
    }
	
	
	
	/*
	public void enableFilter(String name){
		filterEnabledMap.put(name, new Boolean(true));
	}
	
	public void disableFilter(String name){
		filterEnabledMap.put(name, new Boolean(false));
	}
	
	public void dropFilter(String name){
		if(filterEnabledMap.containsKey(name)){
			filterEnabledMap.remove(name);
		}
	}
	
	public void selectAll(){
		for(String key: filterEnabledMap.keySet()){
			filterEnabledMap.put(key, new Boolean(true));
		}
		//TODO
		//updateFilterTable();
	}
	
	public void deselectAll(){
		for(String key: filterEnabledMap.keySet()){
			filterEnabledMap.put(key, new Boolean(false));
		}
		//TODO
		//updateFilterTable();
	}
	
	public Vector<String> getEnabledFilters(){
		
		Vector<String> enabledFilters = new Vector<String>();
		
		for(String key: filterEnabledMap.keySet()){
			if(filterEnabledMap.get(key)){
				enabledFilters.add(key);
			}
		}
		
		return enabledFilters;
	}
	
	public boolean isFilterNameUnique(String name){
		return !filterEnabledMap.containsKey(name);
	}
	
	*/
	
	
	
	
}//END class, FilterTableModel