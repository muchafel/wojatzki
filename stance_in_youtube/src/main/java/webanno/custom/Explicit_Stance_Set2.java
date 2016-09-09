

/* First created by JCasGen Wed Sep 07 09:45:53 CEST 2016 */
package webanno.custom;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Sep 07 09:45:53 CEST 2016
 * XML source: /Users/michael/git/ucsm_git/stance_in_youtube/src/main/resources/desc/type/typesystem.xml
 * @generated */
public class Explicit_Stance_Set2 extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Explicit_Stance_Set2.class);
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
  protected Explicit_Stance_Set2() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Explicit_Stance_Set2(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Explicit_Stance_Set2(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Explicit_Stance_Set2(JCas jcas, int begin, int end) {
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
    if (Explicit_Stance_Set2_Type.featOkTst && ((Explicit_Stance_Set2_Type)jcasType).casFeat_Polarity == null)
      jcasType.jcas.throwFeatMissing("Polarity", "webanno.custom.Explicit_Stance_Set2");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Explicit_Stance_Set2_Type)jcasType).casFeatCode_Polarity);}
    
  /** setter for Polarity - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPolarity(String v) {
    if (Explicit_Stance_Set2_Type.featOkTst && ((Explicit_Stance_Set2_Type)jcasType).casFeat_Polarity == null)
      jcasType.jcas.throwFeatMissing("Polarity", "webanno.custom.Explicit_Stance_Set2");
    jcasType.ll_cas.ll_setStringValue(addr, ((Explicit_Stance_Set2_Type)jcasType).casFeatCode_Polarity, v);}    
   
    
  //*--------------*
  //* Feature: Target

  /** getter for Target - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTarget() {
    if (Explicit_Stance_Set2_Type.featOkTst && ((Explicit_Stance_Set2_Type)jcasType).casFeat_Target == null)
      jcasType.jcas.throwFeatMissing("Target", "webanno.custom.Explicit_Stance_Set2");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Explicit_Stance_Set2_Type)jcasType).casFeatCode_Target);}
    
  /** setter for Target - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTarget(String v) {
    if (Explicit_Stance_Set2_Type.featOkTst && ((Explicit_Stance_Set2_Type)jcasType).casFeat_Target == null)
      jcasType.jcas.throwFeatMissing("Target", "webanno.custom.Explicit_Stance_Set2");
    jcasType.ll_cas.ll_setStringValue(addr, ((Explicit_Stance_Set2_Type)jcasType).casFeatCode_Target, v);}    
  }

    