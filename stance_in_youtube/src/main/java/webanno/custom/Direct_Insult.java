

/* First created by JCasGen Thu Sep 15 11:43:47 CEST 2016 */
package webanno.custom;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Sep 15 11:43:47 CEST 2016
 * XML source: /Users/michael/git/ucsm_git/stance_in_youtube/src/main/resources/desc/type/typesystem.xml
 * @generated */
public class Direct_Insult extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Direct_Insult.class);
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
  protected Direct_Insult() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Direct_Insult(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Direct_Insult(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Direct_Insult(JCas jcas, int begin, int end) {
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
  //* Feature: InsultTarget

  /** getter for InsultTarget - gets 
   * @generated
   * @return value of the feature 
   */
  public String getInsultTarget() {
    if (Direct_Insult_Type.featOkTst && ((Direct_Insult_Type)jcasType).casFeat_InsultTarget == null)
      jcasType.jcas.throwFeatMissing("InsultTarget", "webanno.custom.Direct_Insult");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Direct_Insult_Type)jcasType).casFeatCode_InsultTarget);}
    
  /** setter for InsultTarget - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setInsultTarget(String v) {
    if (Direct_Insult_Type.featOkTst && ((Direct_Insult_Type)jcasType).casFeat_InsultTarget == null)
      jcasType.jcas.throwFeatMissing("InsultTarget", "webanno.custom.Direct_Insult");
    jcasType.ll_cas.ll_setStringValue(addr, ((Direct_Insult_Type)jcasType).casFeatCode_InsultTarget, v);}    
  }

    