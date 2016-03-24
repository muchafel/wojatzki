

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
public class Undersatndability extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Undersatndability.class);
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
  protected Undersatndability() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Undersatndability(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Undersatndability(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Undersatndability(JCas jcas, int begin, int end) {
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
  //* Feature: Understandability

  /** getter for Understandability - gets 
   * @generated
   * @return value of the feature 
   */
  public String getUnderstandability() {
    if (Undersatndability_Type.featOkTst && ((Undersatndability_Type)jcasType).casFeat_Understandability == null)
      jcasType.jcas.throwFeatMissing("Understandability", "webanno.custom.Undersatndability");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Undersatndability_Type)jcasType).casFeatCode_Understandability);}
    
  /** setter for Understandability - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setUnderstandability(String v) {
    if (Undersatndability_Type.featOkTst && ((Undersatndability_Type)jcasType).casFeat_Understandability == null)
      jcasType.jcas.throwFeatMissing("Understandability", "webanno.custom.Undersatndability");
    jcasType.ll_cas.ll_setStringValue(addr, ((Undersatndability_Type)jcasType).casFeatCode_Understandability, v);}    
  }

    