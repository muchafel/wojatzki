

/* First created by JCasGen Fri Apr 22 16:04:08 CEST 2016 */
package predictedTypes;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Apr 22 16:04:08 CEST 2016
 * XML source: /Users/michael/git/ucsm_git/stanceBased_annotationStudy/src/main/resources/desc/type/stackingTypes.xml
 * @generated */
public class ClassifiedSubTarget extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ClassifiedSubTarget.class);
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
  protected ClassifiedSubTarget() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ClassifiedSubTarget(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ClassifiedSubTarget(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ClassifiedSubTarget(JCas jcas, int begin, int end) {
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
  //* Feature: classificationOutcome

  /** getter for classificationOutcome - gets 
   * @generated
   * @return value of the feature 
   */
  public String getClassificationOutcome() {
    if (ClassifiedSubTarget_Type.featOkTst && ((ClassifiedSubTarget_Type)jcasType).casFeat_classificationOutcome == null)
      jcasType.jcas.throwFeatMissing("classificationOutcome", "predictedTypes.ClassifiedSubTarget");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ClassifiedSubTarget_Type)jcasType).casFeatCode_classificationOutcome);}
    
  /** setter for classificationOutcome - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setClassificationOutcome(String v) {
    if (ClassifiedSubTarget_Type.featOkTst && ((ClassifiedSubTarget_Type)jcasType).casFeat_classificationOutcome == null)
      jcasType.jcas.throwFeatMissing("classificationOutcome", "predictedTypes.ClassifiedSubTarget");
    jcasType.ll_cas.ll_setStringValue(addr, ((ClassifiedSubTarget_Type)jcasType).casFeatCode_classificationOutcome, v);}    
   
    
  //*--------------*
  //* Feature: subTarget

  /** getter for subTarget - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSubTarget() {
    if (ClassifiedSubTarget_Type.featOkTst && ((ClassifiedSubTarget_Type)jcasType).casFeat_subTarget == null)
      jcasType.jcas.throwFeatMissing("subTarget", "predictedTypes.ClassifiedSubTarget");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ClassifiedSubTarget_Type)jcasType).casFeatCode_subTarget);}
    
  /** setter for subTarget - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSubTarget(String v) {
    if (ClassifiedSubTarget_Type.featOkTst && ((ClassifiedSubTarget_Type)jcasType).casFeat_subTarget == null)
      jcasType.jcas.throwFeatMissing("subTarget", "predictedTypes.ClassifiedSubTarget");
    jcasType.ll_cas.ll_setStringValue(addr, ((ClassifiedSubTarget_Type)jcasType).casFeatCode_subTarget, v);}    
  }

    