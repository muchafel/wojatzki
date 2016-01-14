

/* First created by JCasGen Thu Jan 14 11:10:41 CET 2016 */
package types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Jan 14 11:10:51 CET 2016
 * XML source: /Users/michael/git/ucsm_git/semEvalTask6/src/main/resources/desc/type/ownArgTypes.xml
 * @generated */
public class TransferClassificationOutcome extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TransferClassificationOutcome.class);
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
  protected TransferClassificationOutcome() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TransferClassificationOutcome(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TransferClassificationOutcome(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TransferClassificationOutcome(JCas jcas, int begin, int end) {
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
  //* Feature: model

  /** getter for model - gets 
   * @generated
   * @return value of the feature 
   */
  public String getModel() {
    if (TransferClassificationOutcome_Type.featOkTst && ((TransferClassificationOutcome_Type)jcasType).casFeat_model == null)
      jcasType.jcas.throwFeatMissing("model", "types.TransferClassificationOutcome");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TransferClassificationOutcome_Type)jcasType).casFeatCode_model);}
    
  /** setter for model - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setModel(String v) {
    if (TransferClassificationOutcome_Type.featOkTst && ((TransferClassificationOutcome_Type)jcasType).casFeat_model == null)
      jcasType.jcas.throwFeatMissing("model", "types.TransferClassificationOutcome");
    jcasType.ll_cas.ll_setStringValue(addr, ((TransferClassificationOutcome_Type)jcasType).casFeatCode_model, v);}    
   
    
  //*--------------*
  //* Feature: outcome

  /** getter for outcome - gets 
   * @generated
   * @return value of the feature 
   */
  public String getOutcome() {
    if (TransferClassificationOutcome_Type.featOkTst && ((TransferClassificationOutcome_Type)jcasType).casFeat_outcome == null)
      jcasType.jcas.throwFeatMissing("outcome", "types.TransferClassificationOutcome");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TransferClassificationOutcome_Type)jcasType).casFeatCode_outcome);}
    
  /** setter for outcome - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setOutcome(String v) {
    if (TransferClassificationOutcome_Type.featOkTst && ((TransferClassificationOutcome_Type)jcasType).casFeat_outcome == null)
      jcasType.jcas.throwFeatMissing("outcome", "types.TransferClassificationOutcome");
    jcasType.ll_cas.ll_setStringValue(addr, ((TransferClassificationOutcome_Type)jcasType).casFeatCode_outcome, v);}    
  }

    