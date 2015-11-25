

/* First created by JCasGen Tue Oct 27 17:15:31 CET 2015 */
package types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Nov 24 15:45:21 CET 2015
 * XML source: /Users/michael/git/ucsm_git/semEvalTask6/src/main/resources/desc/type/ownArgTypes.xml
 * @generated */
public class StanceAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(StanceAnnotation.class);
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
  protected StanceAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public StanceAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public StanceAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public StanceAnnotation(JCas jcas, int begin, int end) {
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
  //* Feature: stance

  /** getter for stance - gets 
   * @generated
   * @return value of the feature 
   */
  public String getStance() {
    if (StanceAnnotation_Type.featOkTst && ((StanceAnnotation_Type)jcasType).casFeat_stance == null)
      jcasType.jcas.throwFeatMissing("stance", "types.StanceAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((StanceAnnotation_Type)jcasType).casFeatCode_stance);}
    
  /** setter for stance - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setStance(String v) {
    if (StanceAnnotation_Type.featOkTst && ((StanceAnnotation_Type)jcasType).casFeat_stance == null)
      jcasType.jcas.throwFeatMissing("stance", "types.StanceAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((StanceAnnotation_Type)jcasType).casFeatCode_stance, v);}    
   
    
  //*--------------*
  //* Feature: target

  /** getter for target - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTarget() {
    if (StanceAnnotation_Type.featOkTst && ((StanceAnnotation_Type)jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "types.StanceAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((StanceAnnotation_Type)jcasType).casFeatCode_target);}
    
  /** setter for target - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTarget(String v) {
    if (StanceAnnotation_Type.featOkTst && ((StanceAnnotation_Type)jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "types.StanceAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((StanceAnnotation_Type)jcasType).casFeatCode_target, v);}    
   
    
  //*--------------*
  //* Feature: originalId

  /** getter for originalId - gets 
   * @generated
   * @return value of the feature 
   */
  public String getOriginalId() {
    if (StanceAnnotation_Type.featOkTst && ((StanceAnnotation_Type)jcasType).casFeat_originalId == null)
      jcasType.jcas.throwFeatMissing("originalId", "types.StanceAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((StanceAnnotation_Type)jcasType).casFeatCode_originalId);}
    
  /** setter for originalId - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setOriginalId(String v) {
    if (StanceAnnotation_Type.featOkTst && ((StanceAnnotation_Type)jcasType).casFeat_originalId == null)
      jcasType.jcas.throwFeatMissing("originalId", "types.StanceAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((StanceAnnotation_Type)jcasType).casFeatCode_originalId, v);}    
  }

    