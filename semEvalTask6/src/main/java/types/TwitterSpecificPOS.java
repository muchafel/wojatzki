

/* First created by JCasGen Thu Nov 05 09:23:12 CET 2015 */
package types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Jan 07 11:06:34 CET 2016
 * XML source: /Users/michael/git/ucsm_git/semEvalTask6/src/main/resources/desc/type/ownArgTypes.xml
 * @generated */
public class TwitterSpecificPOS extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TwitterSpecificPOS.class);
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
  protected TwitterSpecificPOS() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TwitterSpecificPOS(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TwitterSpecificPOS(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TwitterSpecificPOS(JCas jcas, int begin, int end) {
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
  //* Feature: isTokenTwitterSpecific

  /** getter for isTokenTwitterSpecific - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getIsTokenTwitterSpecific() {
    if (TwitterSpecificPOS_Type.featOkTst && ((TwitterSpecificPOS_Type)jcasType).casFeat_isTokenTwitterSpecific == null)
      jcasType.jcas.throwFeatMissing("isTokenTwitterSpecific", "types.TwitterSpecificPOS");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((TwitterSpecificPOS_Type)jcasType).casFeatCode_isTokenTwitterSpecific);}
    
  /** setter for isTokenTwitterSpecific - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsTokenTwitterSpecific(boolean v) {
    if (TwitterSpecificPOS_Type.featOkTst && ((TwitterSpecificPOS_Type)jcasType).casFeat_isTokenTwitterSpecific == null)
      jcasType.jcas.throwFeatMissing("isTokenTwitterSpecific", "types.TwitterSpecificPOS");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((TwitterSpecificPOS_Type)jcasType).casFeatCode_isTokenTwitterSpecific, v);}    
   
    
  //*--------------*
  //* Feature: tag

  /** getter for tag - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTag() {
    if (TwitterSpecificPOS_Type.featOkTst && ((TwitterSpecificPOS_Type)jcasType).casFeat_tag == null)
      jcasType.jcas.throwFeatMissing("tag", "types.TwitterSpecificPOS");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TwitterSpecificPOS_Type)jcasType).casFeatCode_tag);}
    
  /** setter for tag - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTag(String v) {
    if (TwitterSpecificPOS_Type.featOkTst && ((TwitterSpecificPOS_Type)jcasType).casFeat_tag == null)
      jcasType.jcas.throwFeatMissing("tag", "types.TwitterSpecificPOS");
    jcasType.ll_cas.ll_setStringValue(addr, ((TwitterSpecificPOS_Type)jcasType).casFeatCode_tag, v);}    
  }

    