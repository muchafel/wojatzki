

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
public class Irony extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Irony.class);
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
  protected Irony() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Irony(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Irony(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Irony(JCas jcas, int begin, int end) {
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
  public String getIrony() {
    if (Irony_Type.featOkTst && ((Irony_Type)jcasType).casFeat_irony == null)
      jcasType.jcas.throwFeatMissing("irony", "webanno.custom.Irony");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Irony_Type)jcasType).casFeatCode_irony);}
    
  /** setter for irony - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIrony(String v) {
    if (Irony_Type.featOkTst && ((Irony_Type)jcasType).casFeat_irony == null)
      jcasType.jcas.throwFeatMissing("irony", "webanno.custom.Irony");
    jcasType.ll_cas.ll_setStringValue(addr, ((Irony_Type)jcasType).casFeatCode_irony, v);}    
  }

    