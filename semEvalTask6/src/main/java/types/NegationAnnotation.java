

/* First created by JCasGen Tue Nov 10 10:05:22 CET 2015 */
package types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Jan 07 14:15:53 CET 2016
 * XML source: /Users/michael/git/ucsm_git/semEvalTask6/src/main/resources/desc/type/ownArgTypes.xml
 * @generated */
public class NegationAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(NegationAnnotation.class);
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
  protected NegationAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public NegationAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public NegationAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public NegationAnnotation(JCas jcas, int begin, int end) {
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
  //* Feature: isNegation

  /** getter for isNegation - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getIsNegation() {
    if (NegationAnnotation_Type.featOkTst && ((NegationAnnotation_Type)jcasType).casFeat_isNegation == null)
      jcasType.jcas.throwFeatMissing("isNegation", "types.NegationAnnotation");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((NegationAnnotation_Type)jcasType).casFeatCode_isNegation);}
    
  /** setter for isNegation - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsNegation(boolean v) {
    if (NegationAnnotation_Type.featOkTst && ((NegationAnnotation_Type)jcasType).casFeat_isNegation == null)
      jcasType.jcas.throwFeatMissing("isNegation", "types.NegationAnnotation");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((NegationAnnotation_Type)jcasType).casFeatCode_isNegation, v);}    
  }

    