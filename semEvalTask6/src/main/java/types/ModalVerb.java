

/* First created by JCasGen Wed Oct 28 15:44:08 CET 2015 */
package types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Jan 14 11:10:51 CET 2016
 * XML source: /Users/michael/git/ucsm_git/semEvalTask6/src/main/resources/desc/type/ownArgTypes.xml
 * @generated */
public class ModalVerb extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ModalVerb.class);
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
  protected ModalVerb() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ModalVerb(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ModalVerb(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ModalVerb(JCas jcas, int begin, int end) {
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
  //* Feature: isModalVerb

  /** getter for isModalVerb - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getIsModalVerb() {
    if (ModalVerb_Type.featOkTst && ((ModalVerb_Type)jcasType).casFeat_isModalVerb == null)
      jcasType.jcas.throwFeatMissing("isModalVerb", "types.ModalVerb");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((ModalVerb_Type)jcasType).casFeatCode_isModalVerb);}
    
  /** setter for isModalVerb - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsModalVerb(boolean v) {
    if (ModalVerb_Type.featOkTst && ((ModalVerb_Type)jcasType).casFeat_isModalVerb == null)
      jcasType.jcas.throwFeatMissing("isModalVerb", "types.ModalVerb");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((ModalVerb_Type)jcasType).casFeatCode_isModalVerb, v);}    
  }

    