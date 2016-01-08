

/* First created by JCasGen Thu Jan 07 14:15:53 CET 2016 */
package types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Jan 08 14:00:38 CET 2016
 * XML source: /Users/michael/git/ucsm_git/semEvalTask6/src/main/resources/desc/type/ownArgTypes.xml
 * @generated */
public class ClassifiedConceptOutcome extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ClassifiedConceptOutcome.class);
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
  protected ClassifiedConceptOutcome() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ClassifiedConceptOutcome(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ClassifiedConceptOutcome(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ClassifiedConceptOutcome(JCas jcas, int begin, int end) {
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
  //* Feature: conceptName

  /** getter for conceptName - gets 
   * @generated
   * @return value of the feature 
   */
  public String getConceptName() {
    if (ClassifiedConceptOutcome_Type.featOkTst && ((ClassifiedConceptOutcome_Type)jcasType).casFeat_conceptName == null)
      jcasType.jcas.throwFeatMissing("conceptName", "types.ClassifiedConceptOutcome");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ClassifiedConceptOutcome_Type)jcasType).casFeatCode_conceptName);}
    
  /** setter for conceptName - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setConceptName(String v) {
    if (ClassifiedConceptOutcome_Type.featOkTst && ((ClassifiedConceptOutcome_Type)jcasType).casFeat_conceptName == null)
      jcasType.jcas.throwFeatMissing("conceptName", "types.ClassifiedConceptOutcome");
    jcasType.ll_cas.ll_setStringValue(addr, ((ClassifiedConceptOutcome_Type)jcasType).casFeatCode_conceptName, v);}    
   
    
  //*--------------*
  //* Feature: classificationOutcome

  /** getter for classificationOutcome - gets 
   * @generated
   * @return value of the feature 
   */
  public String getClassificationOutcome() {
    if (ClassifiedConceptOutcome_Type.featOkTst && ((ClassifiedConceptOutcome_Type)jcasType).casFeat_classificationOutcome == null)
      jcasType.jcas.throwFeatMissing("classificationOutcome", "types.ClassifiedConceptOutcome");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ClassifiedConceptOutcome_Type)jcasType).casFeatCode_classificationOutcome);}
    
  /** setter for classificationOutcome - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setClassificationOutcome(String v) {
    if (ClassifiedConceptOutcome_Type.featOkTst && ((ClassifiedConceptOutcome_Type)jcasType).casFeat_classificationOutcome == null)
      jcasType.jcas.throwFeatMissing("classificationOutcome", "types.ClassifiedConceptOutcome");
    jcasType.ll_cas.ll_setStringValue(addr, ((ClassifiedConceptOutcome_Type)jcasType).casFeatCode_classificationOutcome, v);}    
  }

    