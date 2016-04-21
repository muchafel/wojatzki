

/* First created by JCasGen Thu Apr 21 12:49:34 CEST 2016 */
package curatedTypes;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Apr 21 12:49:34 CEST 2016
 * XML source: /Users/michael/git/ucsm_git/stanceBased_annotationStudy/src/main/resources/desc/type/curatedTypes.xml
 * @generated */
public class CuratedSubTarget extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CuratedSubTarget.class);
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
  protected CuratedSubTarget() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public CuratedSubTarget(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CuratedSubTarget(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public CuratedSubTarget(JCas jcas, int begin, int end) {
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
  //* Feature: target

  /** getter for target - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTarget() {
    if (CuratedSubTarget_Type.featOkTst && ((CuratedSubTarget_Type)jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "curatedTypes.CuratedSubTarget");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CuratedSubTarget_Type)jcasType).casFeatCode_target);}
    
  /** setter for target - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTarget(String v) {
    if (CuratedSubTarget_Type.featOkTst && ((CuratedSubTarget_Type)jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "curatedTypes.CuratedSubTarget");
    jcasType.ll_cas.ll_setStringValue(addr, ((CuratedSubTarget_Type)jcasType).casFeatCode_target, v);}    
   
    
  //*--------------*
  //* Feature: polarity

  /** getter for polarity - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPolarity() {
    if (CuratedSubTarget_Type.featOkTst && ((CuratedSubTarget_Type)jcasType).casFeat_polarity == null)
      jcasType.jcas.throwFeatMissing("polarity", "curatedTypes.CuratedSubTarget");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CuratedSubTarget_Type)jcasType).casFeatCode_polarity);}
    
  /** setter for polarity - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPolarity(String v) {
    if (CuratedSubTarget_Type.featOkTst && ((CuratedSubTarget_Type)jcasType).casFeat_polarity == null)
      jcasType.jcas.throwFeatMissing("polarity", "curatedTypes.CuratedSubTarget");
    jcasType.ll_cas.ll_setStringValue(addr, ((CuratedSubTarget_Type)jcasType).casFeatCode_polarity, v);}    
  }

    