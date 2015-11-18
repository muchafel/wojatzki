

/* First created by JCasGen Wed Nov 04 09:24:00 CET 2015 */
package types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Nov 13 10:04:23 CET 2015
 * XML source: /Users/michael/workspaces/Implicitness/semEvalTask6/src/main/resources/desc/type/ownArgTypes.xml
 * @generated */
public class OriginalResource extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(OriginalResource.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected OriginalResource() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public OriginalResource(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public OriginalResource(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public OriginalResource(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: location

  /** getter for location - gets 
   * @generated
   * @return value of the feature 
   */
  public String getLocation() {
    if (OriginalResource_Type.featOkTst && ((OriginalResource_Type)jcasType).casFeat_location == null)
      jcasType.jcas.throwFeatMissing("location", "types.OriginalResource");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OriginalResource_Type)jcasType).casFeatCode_location);}
    
  /** setter for location - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setLocation(String v) {
    if (OriginalResource_Type.featOkTst && ((OriginalResource_Type)jcasType).casFeat_location == null)
      jcasType.jcas.throwFeatMissing("location", "types.OriginalResource");
    jcasType.ll_cas.ll_setStringValue(addr, ((OriginalResource_Type)jcasType).casFeatCode_location, v);}    
   
    
  //*--------------*
  //* Feature: fileName

  /** getter for fileName - gets 
   * @generated
   * @return value of the feature 
   */
  public String getFileName() {
    if (OriginalResource_Type.featOkTst && ((OriginalResource_Type)jcasType).casFeat_fileName == null)
      jcasType.jcas.throwFeatMissing("fileName", "types.OriginalResource");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OriginalResource_Type)jcasType).casFeatCode_fileName);}
    
  /** setter for fileName - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFileName(String v) {
    if (OriginalResource_Type.featOkTst && ((OriginalResource_Type)jcasType).casFeat_fileName == null)
      jcasType.jcas.throwFeatMissing("fileName", "types.OriginalResource");
    jcasType.ll_cas.ll_setStringValue(addr, ((OriginalResource_Type)jcasType).casFeatCode_fileName, v);}    
  }

    