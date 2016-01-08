

/* First created by JCasGen Fri Jan 08 14:00:31 CET 2016 */
package types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Jan 08 14:00:38 CET 2016
 * XML source: /Users/michael/git/ucsm_git/semEvalTask6/src/main/resources/desc/type/ownArgTypes.xml
 * @generated */
public class TaskBStanceAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TaskBStanceAnnotation.class);
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
  protected TaskBStanceAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TaskBStanceAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TaskBStanceAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TaskBStanceAnnotation(JCas jcas, int begin, int end) {
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
  //* Feature: target

  /** getter for target - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTarget() {
    if (TaskBStanceAnnotation_Type.featOkTst && ((TaskBStanceAnnotation_Type)jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "types.TaskBStanceAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TaskBStanceAnnotation_Type)jcasType).casFeatCode_target);}
    
  /** setter for target - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTarget(String v) {
    if (TaskBStanceAnnotation_Type.featOkTst && ((TaskBStanceAnnotation_Type)jcasType).casFeat_target == null)
      jcasType.jcas.throwFeatMissing("target", "types.TaskBStanceAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TaskBStanceAnnotation_Type)jcasType).casFeatCode_target, v);}    
   
    
  //*--------------*
  //* Feature: stance

  /** getter for stance - gets 
   * @generated
   * @return value of the feature 
   */
  public String getStance() {
    if (TaskBStanceAnnotation_Type.featOkTst && ((TaskBStanceAnnotation_Type)jcasType).casFeat_stance == null)
      jcasType.jcas.throwFeatMissing("stance", "types.TaskBStanceAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TaskBStanceAnnotation_Type)jcasType).casFeatCode_stance);}
    
  /** setter for stance - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setStance(String v) {
    if (TaskBStanceAnnotation_Type.featOkTst && ((TaskBStanceAnnotation_Type)jcasType).casFeat_stance == null)
      jcasType.jcas.throwFeatMissing("stance", "types.TaskBStanceAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((TaskBStanceAnnotation_Type)jcasType).casFeatCode_stance, v);}    
  }

    