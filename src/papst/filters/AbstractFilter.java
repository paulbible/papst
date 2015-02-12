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
package papst.filters;

import java.io.Serializable;

public abstract class AbstractFilter implements FilterInterface,Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final int GREATER_THAN = 0;
	public static final int GREATER_THAN_EQUAL = 1;
	public static final int LESS_THAN = 2;
	public static final int LESS_THAN_EQUAL = 3;
	public static final int EQUAL = 4;
	public static final int NOT_EQUAL = 5;
	
	public static final String STR_GREATER_THAN = ">";
	public static final String STR_GREATER_THAN_EQUAL = ">=";
	public static final String STR_LESS_THAN = "<";
	public static final String STR_LESS_THAN_EQUAL = "<=";
	public static final String STR_EQUAL = "=";
	public static final String STR_NOT_EQUAL = "not =";
	
	public static final String[] RelationStrings = {">",">=","<","<=","=","not ="};
	
	public static final int ADD = 0;
	public static final int SUBTRACT = 1;
	public static final int MULTIPLY = 2;
	public static final int DIVIDE = 3;
	
	public static String[] OperationStrings = {"+ (add)","- (subtract)","* (multiply)","/ (divide)"};
	
	
	public static boolean doesRelationshipHold(double valueA, double valueB, int relationship){
		switch(relationship){
			case  GREATER_THAN:
				return valueA > valueB;
			case  GREATER_THAN_EQUAL:
				return valueA >= valueB;
			case  LESS_THAN:
				return valueA < valueB;
			case  LESS_THAN_EQUAL:
				return valueA <= valueB;
			case  EQUAL:
				return valueA == valueB;
			case  NOT_EQUAL:
				return valueA != valueB;
			default:
				return false;
		}
	}//end doesRelationshipHold
	
	public static double operateValues(double valueA,double valueB,int operation){
	
		switch(operation){
			case ADD:
				return valueA + valueB;
			case SUBTRACT:
				return valueA - valueB;
			case MULTIPLY:
				return valueA * valueB;
			case DIVIDE:
				
				if(valueB == 0){
					
					if(valueA > 0){
						return Double.MAX_VALUE;
					}else if(valueB < 0){
						return Double.MIN_VALUE;
					}else{
						return 0;
					}
				}
				return valueA / valueB;
			default:
				return 0.0;
		}
	} //end method, operateValues
	
	
	
	

}
