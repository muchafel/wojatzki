

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
public class Alternative_concepts extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Alternative_concepts.class);
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
  protected Alternative_concepts() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Alternative_concepts(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Alternative_concepts(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Alternative_concepts(JCas jcas, int begin, int end) {
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
  //* Feature: alternative_concepts

  /** getter for alternative_concepts - gets 
   * @generated
   * @return value of the feature 
   */
  public String getAlternative_concepts() {
    if (Alternative_concepts_Type.featOkTst && ((Alternative_concepts_Type)jcasType).casFeat_alternative_concepts == null)
      jcasType.jcas.throwFeatMissing("alternative_concepts", "webanno.custom.Alternative_concepts");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Alternative_concepts_Type)jcasType).casFeatCode_alternative_concepts);}
    
  /** setter for alternative_concepts - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAlternative_concepts(String v) {
    if (Alternative_concepts_Type.featOkTst && ((Alternative_concepts_Type)jcasType).casFeat_alternative_concepts == null)
      jcasType.jcas.throwFeatMissing("alternative_concepts", "webanno.custom.Alternative_concepts");
    jcasType.ll_cas.ll_setStringValue(addr, ((Alternative_concepts_Type)jcasType).casFeatCode_alternative_concepts, v);}    
  }

    