

/* First created by JCasGen Mon Apr 25 15:05:07 CEST 2016 */
package predictedTypes;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Apr 25 15:05:07 CEST 2016
 * XML source: /Users/michael/git/ucsm_git/stanceBased_annotationStudy/src/main/resources/desc/type/stackingTypes.xml
 * @generated */
public class NgramClassification extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(NgramClassification.class);
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
  protected NgramClassification() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public NgramClassification(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public NgramClassification(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public NgramClassification(JCas jcas, int begin, int end) {
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
    if (NgramClassification_Type.featOkTst && ((NgramClassification_Type)jcasType).casFeat_classificationOutcome == null)
      jcasType.jcas.throwFeatMissing("classificationOutcome", "predictedTypes.NgramClassification");
    return jcasType.ll_cas.ll_getStringValue(addr, ((NgramClassification_Type)jcasType).casFeatCode_classificationOutcome);}
    
  /** setter for classificationOutcome - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setClassificationOutcome(String v) {
    if (NgramClassification_Type.featOkTst && ((NgramClassification_Type)jcasType).casFeat_classificationOutcome == null)
      jcasType.jcas.throwFeatMissing("classificationOutcome", "predictedTypes.NgramClassification");
    jcasType.ll_cas.ll_setStringValue(addr, ((NgramClassification_Type)jcasType).casFeatCode_classificationOutcome, v);}    
   
    
  //*--------------*
  //* Feature: variant

  /** getter for variant - gets 
   * @generated
   * @return value of the feature 
   */
  public String getVariant() {
    if (NgramClassification_Type.featOkTst && ((NgramClassification_Type)jcasType).casFeat_variant == null)
      jcasType.jcas.throwFeatMissing("variant", "predictedTypes.NgramClassification");
    return jcasType.ll_cas.ll_getStringValue(addr, ((NgramClassification_Type)jcasType).casFeatCode_variant);}
    
  /** setter for variant - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setVariant(String v) {
    if (NgramClassification_Type.featOkTst && ((NgramClassification_Type)jcasType).casFeat_variant == null)
      jcasType.jcas.throwFeatMissing("variant", "predictedTypes.NgramClassification");
    jcasType.ll_cas.ll_setStringValue(addr, ((NgramClassification_Type)jcasType).casFeatCode_variant, v);}    
  }

    