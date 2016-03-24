

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
public class UnUnderstandability extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(UnUnderstandability.class);
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
  protected UnUnderstandability() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public UnUnderstandability(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public UnUnderstandability(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public UnUnderstandability(JCas jcas, int begin, int end) {
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
  //* Feature: notUnderstandable

  /** getter for notUnderstandable - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getNotUnderstandable() {
    if (UnUnderstandability_Type.featOkTst && ((UnUnderstandability_Type)jcasType).casFeat_notUnderstandable == null)
      jcasType.jcas.throwFeatMissing("notUnderstandable", "webanno.custom.UnUnderstandability");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((UnUnderstandability_Type)jcasType).casFeatCode_notUnderstandable);}
    
  /** setter for notUnderstandable - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNotUnderstandable(boolean v) {
    if (UnUnderstandability_Type.featOkTst && ((UnUnderstandability_Type)jcasType).casFeat_notUnderstandable == null)
      jcasType.jcas.throwFeatMissing("notUnderstandable", "webanno.custom.UnUnderstandability");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((UnUnderstandability_Type)jcasType).casFeatCode_notUnderstandable, v);}    
  }

    