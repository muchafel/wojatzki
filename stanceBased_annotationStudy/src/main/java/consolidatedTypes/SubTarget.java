

/* First created by JCasGen Thu Apr 21 10:23:18 CEST 2016 */
package consolidatedTypes;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Apr 21 10:23:18 CEST 2016
 * XML source: /Users/michael/git/ucsm_git/stanceBased_annotationStudy/src/main/resources/desc/type/consolidatedTypes.xml
 * @generated */
public class SubTarget extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(SubTarget.class);
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
  protected SubTarget() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public SubTarget(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public SubTarget(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public SubTarget(JCas jcas, int begin, int end) {
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
    if (SubTarget_Type.featOkTst && ((SubTarget_Type)jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "consolidatedTypes.SubTarget");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SubTarget_Type)jcasType).casFeatCode_target);}
    
  /** setter for target - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTarget(String v) {
    if (SubTarget_Type.featOkTst && ((SubTarget_Type)jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "consolidatedTypes.SubTarget");
    jcasType.ll_cas.ll_setStringValue(addr, ((SubTarget_Type)jcasType).casFeatCode_target, v);}    
   
    
  //*--------------*
  //* Feature: polarity

  /** getter for polarity - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPolarity() {
    if (SubTarget_Type.featOkTst && ((SubTarget_Type)jcasType).casFeat_polarity == null)
      jcasType.jcas.throwFeatMissing("polarity", "consolidatedTypes.SubTarget");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SubTarget_Type)jcasType).casFeatCode_polarity);}
    
  /** setter for polarity - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPolarity(String v) {
    if (SubTarget_Type.featOkTst && ((SubTarget_Type)jcasType).casFeat_polarity == null)
      jcasType.jcas.throwFeatMissing("polarity", "consolidatedTypes.SubTarget");
    jcasType.ll_cas.ll_setStringValue(addr, ((SubTarget_Type)jcasType).casFeatCode_polarity, v);}    
   
    
  //*--------------*
  //* Feature: annotator

  /** getter for annotator - gets 
   * @generated
   * @return value of the feature 
   */
  public String getAnnotator() {
    if (SubTarget_Type.featOkTst && ((SubTarget_Type)jcasType).casFeat_annotator == null)
      jcasType.jcas.throwFeatMissing("annotator", "consolidatedTypes.SubTarget");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SubTarget_Type)jcasType).casFeatCode_annotator);}
    
  /** setter for annotator - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnnotator(String v) {
    if (SubTarget_Type.featOkTst && ((SubTarget_Type)jcasType).casFeat_annotator == null)
      jcasType.jcas.throwFeatMissing("annotator", "consolidatedTypes.SubTarget");
    jcasType.ll_cas.ll_setStringValue(addr, ((SubTarget_Type)jcasType).casFeatCode_annotator, v);}    
  }

    