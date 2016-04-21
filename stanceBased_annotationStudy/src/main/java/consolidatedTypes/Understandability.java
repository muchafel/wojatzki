

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
public class Understandability extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Understandability.class);
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
  protected Understandability() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Understandability(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Understandability(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Understandability(JCas jcas, int begin, int end) {
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
  //* Feature: annotator

  /** getter for annotator - gets 
   * @generated
   * @return value of the feature 
   */
  public String getAnnotator() {
    if (Understandability_Type.featOkTst && ((Understandability_Type)jcasType).casFeat_annotator == null)
      jcasType.jcas.throwFeatMissing("annotator", "consolidatedTypes.Understandability");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Understandability_Type)jcasType).casFeatCode_annotator);}
    
  /** setter for annotator - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnnotator(String v) {
    if (Understandability_Type.featOkTst && ((Understandability_Type)jcasType).casFeat_annotator == null)
      jcasType.jcas.throwFeatMissing("annotator", "consolidatedTypes.Understandability");
    jcasType.ll_cas.ll_setStringValue(addr, ((Understandability_Type)jcasType).casFeatCode_annotator, v);}    
  }

    