

/* First created by JCasGen Fri Nov 13 09:03:27 CET 2015 */
package types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Jan 07 14:15:53 CET 2016
 * XML source: /Users/michael/git/ucsm_git/semEvalTask6/src/main/resources/desc/type/ownArgTypes.xml
 * @generated */
public class FunctionalPartAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(FunctionalPartAnnotation.class);
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
  protected FunctionalPartAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public FunctionalPartAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public FunctionalPartAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public FunctionalPartAnnotation(JCas jcas, int begin, int end) {
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
  //* Feature: function

  /** getter for function - gets 
   * @generated
   * @return value of the feature 
   */
  public String getFunction() {
    if (FunctionalPartAnnotation_Type.featOkTst && ((FunctionalPartAnnotation_Type)jcasType).casFeat_function == null)
      jcasType.jcas.throwFeatMissing("function", "types.FunctionalPartAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((FunctionalPartAnnotation_Type)jcasType).casFeatCode_function);}
    
  /** setter for function - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFunction(String v) {
    if (FunctionalPartAnnotation_Type.featOkTst && ((FunctionalPartAnnotation_Type)jcasType).casFeat_function == null)
      jcasType.jcas.throwFeatMissing("function", "types.FunctionalPartAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((FunctionalPartAnnotation_Type)jcasType).casFeatCode_function, v);}    
  }

    