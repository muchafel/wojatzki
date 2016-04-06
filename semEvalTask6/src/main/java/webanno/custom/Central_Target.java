

/* First created by JCasGen Mon Apr 04 11:51:05 CEST 2016 */
package webanno.custom;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Apr 04 11:51:05 CEST 2016
 * XML source: /Users/michael/git/ucsm_git/semEvalTask6/src/main/resources/desc/type/typesystem_freeTarget.xml
 * @generated */
public class Central_Target extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Central_Target.class);
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
  protected Central_Target() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Central_Target(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Central_Target(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Central_Target(JCas jcas, int begin, int end) {
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
  //* Feature: Polarity

  /** getter for Polarity - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPolarity() {
    if (Central_Target_Type.featOkTst && ((Central_Target_Type)jcasType).casFeat_Polarity == null)
      jcasType.jcas.throwFeatMissing("Polarity", "webanno.custom.Central_Target");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Central_Target_Type)jcasType).casFeatCode_Polarity);}
    
  /** setter for Polarity - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPolarity(String v) {
    if (Central_Target_Type.featOkTst && ((Central_Target_Type)jcasType).casFeat_Polarity == null)
      jcasType.jcas.throwFeatMissing("Polarity", "webanno.custom.Central_Target");
    jcasType.ll_cas.ll_setStringValue(addr, ((Central_Target_Type)jcasType).casFeatCode_Polarity, v);}    
   
    
  //*--------------*
  //* Feature: Target

  /** getter for Target - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTarget() {
    if (Central_Target_Type.featOkTst && ((Central_Target_Type)jcasType).casFeat_Target == null)
      jcasType.jcas.throwFeatMissing("Target", "webanno.custom.Central_Target");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Central_Target_Type)jcasType).casFeatCode_Target);}
    
  /** setter for Target - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTarget(String v) {
    if (Central_Target_Type.featOkTst && ((Central_Target_Type)jcasType).casFeat_Target == null)
      jcasType.jcas.throwFeatMissing("Target", "webanno.custom.Central_Target");
    jcasType.ll_cas.ll_setStringValue(addr, ((Central_Target_Type)jcasType).casFeatCode_Target, v);}    
  }

    