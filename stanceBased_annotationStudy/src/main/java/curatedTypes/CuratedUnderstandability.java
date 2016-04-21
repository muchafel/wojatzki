

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
public class CuratedUnderstandability extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CuratedUnderstandability.class);
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
  protected CuratedUnderstandability() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public CuratedUnderstandability(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CuratedUnderstandability(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public CuratedUnderstandability(JCas jcas, int begin, int end) {
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
  //* Feature: understandability

  /** getter for understandability - gets 
   * @generated
   * @return value of the feature 
   */
  public String getUnderstandability() {
    if (CuratedUnderstandability_Type.featOkTst && ((CuratedUnderstandability_Type)jcasType).casFeat_understandability == null)
      jcasType.jcas.throwFeatMissing("understandability", "curatedTypes.CuratedUnderstandability");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CuratedUnderstandability_Type)jcasType).casFeatCode_understandability);}
    
  /** setter for understandability - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setUnderstandability(String v) {
    if (CuratedUnderstandability_Type.featOkTst && ((CuratedUnderstandability_Type)jcasType).casFeat_understandability == null)
      jcasType.jcas.throwFeatMissing("understandability", "curatedTypes.CuratedUnderstandability");
    jcasType.ll_cas.ll_setStringValue(addr, ((CuratedUnderstandability_Type)jcasType).casFeatCode_understandability, v);}    
  }

    