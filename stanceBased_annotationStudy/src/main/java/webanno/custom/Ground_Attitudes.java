

/* First created by JCasGen Tue Apr 05 14:29:09 CEST 2016 */
package webanno.custom;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Apr 05 14:29:09 CEST 2016
 * XML source: /Users/michael/git/ucsm_git/semEvalTask6/src/main/resources/desc/type/typesystem_groundTruth.xml
 * @generated */
public class Ground_Attitudes extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Ground_Attitudes.class);
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
  protected Ground_Attitudes() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Ground_Attitudes(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Ground_Attitudes(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Ground_Attitudes(JCas jcas, int begin, int end) {
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
  //* Feature: Attitudes

  /** getter for Attitudes - gets 
   * @generated
   * @return value of the feature 
   */
  public String getAttitudes() {
    if (Ground_Attitudes_Type.featOkTst && ((Ground_Attitudes_Type)jcasType).casFeat_Attitudes == null)
      jcasType.jcas.throwFeatMissing("Attitudes", "webanno.custom.Ground_Attitudes");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Ground_Attitudes_Type)jcasType).casFeatCode_Attitudes);}
    
  /** setter for Attitudes - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAttitudes(String v) {
    if (Ground_Attitudes_Type.featOkTst && ((Ground_Attitudes_Type)jcasType).casFeat_Attitudes == null)
      jcasType.jcas.throwFeatMissing("Attitudes", "webanno.custom.Ground_Attitudes");
    jcasType.ll_cas.ll_setStringValue(addr, ((Ground_Attitudes_Type)jcasType).casFeatCode_Attitudes, v);}    
   
    
  //*--------------*
  //* Feature: Polarity

  /** getter for Polarity - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPolarity() {
    if (Ground_Attitudes_Type.featOkTst && ((Ground_Attitudes_Type)jcasType).casFeat_Polarity == null)
      jcasType.jcas.throwFeatMissing("Polarity", "webanno.custom.Ground_Attitudes");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Ground_Attitudes_Type)jcasType).casFeatCode_Polarity);}
    
  /** setter for Polarity - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPolarity(String v) {
    if (Ground_Attitudes_Type.featOkTst && ((Ground_Attitudes_Type)jcasType).casFeat_Polarity == null)
      jcasType.jcas.throwFeatMissing("Polarity", "webanno.custom.Ground_Attitudes");
    jcasType.ll_cas.ll_setStringValue(addr, ((Ground_Attitudes_Type)jcasType).casFeatCode_Polarity, v);}    
  }

    