

/* First created by JCasGen Wed Dec 21 10:36:37 CET 2016 */
package preprocessing;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Dec 21 10:36:37 CET 2016
 * XML source: /Users/michael/git/ucsm_git/stance_in_youtube/src/main/resources/desc/type/typesystem_preprocessing.xml
 * @generated */
public class SentenceStance extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(SentenceStance.class);
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
  protected SentenceStance() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public SentenceStance(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public SentenceStance(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public SentenceStance(JCas jcas, int begin, int end) {
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
  //* Feature: Target

  /** getter for Target - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTarget() {
    if (SentenceStance_Type.featOkTst && ((SentenceStance_Type)jcasType).casFeat_Target == null)
      jcasType.jcas.throwFeatMissing("Target", "preprocessing.SentenceStance");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SentenceStance_Type)jcasType).casFeatCode_Target);}
    
  /** setter for Target - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTarget(String v) {
    if (SentenceStance_Type.featOkTst && ((SentenceStance_Type)jcasType).casFeat_Target == null)
      jcasType.jcas.throwFeatMissing("Target", "preprocessing.SentenceStance");
    jcasType.ll_cas.ll_setStringValue(addr, ((SentenceStance_Type)jcasType).casFeatCode_Target, v);}    
   
    
  //*--------------*
  //* Feature: Polarity

  /** getter for Polarity - gets 
   * @generated
   * @return value of the feature 
   */
  public int getPolarity() {
    if (SentenceStance_Type.featOkTst && ((SentenceStance_Type)jcasType).casFeat_Polarity == null)
      jcasType.jcas.throwFeatMissing("Polarity", "preprocessing.SentenceStance");
    return jcasType.ll_cas.ll_getIntValue(addr, ((SentenceStance_Type)jcasType).casFeatCode_Polarity);}
    
  /** setter for Polarity - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPolarity(int v) {
    if (SentenceStance_Type.featOkTst && ((SentenceStance_Type)jcasType).casFeat_Polarity == null)
      jcasType.jcas.throwFeatMissing("Polarity", "preprocessing.SentenceStance");
    jcasType.ll_cas.ll_setIntValue(addr, ((SentenceStance_Type)jcasType).casFeatCode_Polarity, v);}    
  }

    