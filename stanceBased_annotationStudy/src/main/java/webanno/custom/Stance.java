

/* First created by JCasGen Thu Mar 24 15:20:38 CET 2016 */
package webanno.custom;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Mar 24 15:20:38 CET 2016
 * XML source: /Users/michael/git/ucsm_git/semEvalTask6/src/main/resources/desc/type/typesystem.xml
 * @generated */
public class Stance extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Stance.class);
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
  protected Stance() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Stance(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Stance(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Stance(JCas jcas, int begin, int end) {
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
  //* Feature: Stance_Polarity

  /** getter for Stance_Polarity - gets 
   * @generated
   * @return value of the feature 
   */
  public String getStance_Polarity() {
    if (Stance_Type.featOkTst && ((Stance_Type)jcasType).casFeat_Stance_Polarity == null)
      jcasType.jcas.throwFeatMissing("Stance_Polarity", "webanno.custom.Stance");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Stance_Type)jcasType).casFeatCode_Stance_Polarity);}
    
  /** setter for Stance_Polarity - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setStance_Polarity(String v) {
    if (Stance_Type.featOkTst && ((Stance_Type)jcasType).casFeat_Stance_Polarity == null)
      jcasType.jcas.throwFeatMissing("Stance_Polarity", "webanno.custom.Stance");
    jcasType.ll_cas.ll_setStringValue(addr, ((Stance_Type)jcasType).casFeatCode_Stance_Polarity, v);}    
   
    
  //*--------------*
  //* Feature: Stance_Target

  /** getter for Stance_Target - gets 
   * @generated
   * @return value of the feature 
   */
  public String getStance_Target() {
    if (Stance_Type.featOkTst && ((Stance_Type)jcasType).casFeat_Stance_Target == null)
      jcasType.jcas.throwFeatMissing("Stance_Target", "webanno.custom.Stance");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Stance_Type)jcasType).casFeatCode_Stance_Target);}
    
  /** setter for Stance_Target - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setStance_Target(String v) {
    if (Stance_Type.featOkTst && ((Stance_Type)jcasType).casFeat_Stance_Target == null)
      jcasType.jcas.throwFeatMissing("Stance_Target", "webanno.custom.Stance");
    jcasType.ll_cas.ll_setStringValue(addr, ((Stance_Type)jcasType).casFeatCode_Stance_Target, v);}    
  }

    