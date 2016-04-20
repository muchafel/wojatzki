

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
public class Irony_Sarcasm extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Irony_Sarcasm.class);
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
  protected Irony_Sarcasm() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Irony_Sarcasm(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Irony_Sarcasm(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Irony_Sarcasm(JCas jcas, int begin, int end) {
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
  //* Feature: irony

  /** getter for irony - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getIrony() {
    if (Irony_Sarcasm_Type.featOkTst && ((Irony_Sarcasm_Type)jcasType).casFeat_irony == null)
      jcasType.jcas.throwFeatMissing("irony", "webanno.custom.Irony_Sarcasm");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((Irony_Sarcasm_Type)jcasType).casFeatCode_irony);}
    
  /** setter for irony - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIrony(boolean v) {
    if (Irony_Sarcasm_Type.featOkTst && ((Irony_Sarcasm_Type)jcasType).casFeat_irony == null)
      jcasType.jcas.throwFeatMissing("irony", "webanno.custom.Irony_Sarcasm");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((Irony_Sarcasm_Type)jcasType).casFeatCode_irony, v);}    
  }

    