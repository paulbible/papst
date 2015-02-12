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

import java.io.Serializable;
import java.util.Vector;


public class ResultData implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int SEARCH_RESULT  = 0;
	public static final int SUMMARY_RESULT = 1;
	public static final int ASSIGN_RESULT  = 2;
	public static final int COMPARE_RESULT = 3;
	
	
	private Vector<String> columnNames;
	private Vector< Vector<Object> > tableData;
	private String note;
	private String name;
	private int type;
	
	public ResultData(Vector<String> columnNames, Vector< Vector<Object> > tableData, String note, String name, int type){
		this.columnNames = columnNames;
		this.tableData   = tableData;
		this.note        = note;
		
		this.name        = name;
		this.type        = type;
		
	}
	
	public ResultData(Object[] aColumnNames, Object[][] aTableData, String note,String name, int type){
		this.columnNames = new Vector<String>();
		this.tableData   = new Vector<Vector<Object>>();
		
		
		for(int i = 0; i < aColumnNames.length; ++i){
			this.columnNames.add((String)aColumnNames[i]);
		}
		
		for(int i = 0; i < aTableData.length; ++i){
			
			Vector<Object> aRow = new Vector<Object>();
			for(int j = 0; j < aTableData[i].length; ++j){
				aRow.add(aTableData[i][j]);
			}
			this.tableData.add(aRow);
		}
		
		this.note        = note;
		this.name        = name;
		this.type        = type;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public int getType(){
		return this.type;
	}
	
	public Vector<String> getColumnNames(){
		return columnNames;
	}
	
	public Vector< Vector<Object> > getTableData(){
		return tableData;
	}
	
	public String getNote(){
		return this.note;
	}
	
	public void setNote(String note){
		this.note = note;
	}
	
	public int getColumnCount(){
		return tableData.elementAt(0).size();
	}
	
	public String getColumnName(int index){
		return columnNames.elementAt(index);
	}
	
	public Object getValueAt(int row, int column){
		return tableData.elementAt(row).elementAt(column);
	}
	
	public AbstractResultTableModel getTableModel(){
		
		AbstractResultTableModel tableModel;
		
		switch(this.type){
			case SEARCH_RESULT:
				tableModel = new SearchResultTableModel(tableData, columnNames);
				break;
			case SUMMARY_RESULT:
				tableModel = new SummaryResultTableModel(tableData, columnNames);
				break;
			case ASSIGN_RESULT:
				tableModel = new GeneAssignResultTableModel(tableData, columnNames);
				break;
			case COMPARE_RESULT:
				tableModel = new CompareResultTableModel(tableData, columnNames);
				break;
			default:
				tableModel = new SearchResultTableModel(tableData, columnNames);
		}
		
		tableModel.setTabTitle(name);
		tableModel.setResultNote(note);
		return tableModel;
	} 
}
