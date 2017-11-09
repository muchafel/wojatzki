

/* First created by JCasGen Thu Nov 09 15:06:29 CET 2017 */
package assertionRegression.annotationTypes;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Nov 09 15:06:29 CET 2017
 * XML source: /Users/michael/git/ucsm_git/interactiveAM/assertionRegression/src/main/resources/desc/type/typesystem_freeTarget.xml
 * @generated */
public class Issue extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Issue.class);
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
  protected Issue() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Issue(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Issue(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Issue(JCas jcas, int begin, int end) {
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
  //* Feature: Issue

  /** getter for Issue - gets 
   * @generated
   * @return value of the feature 
   */
  public String getIssue() {
    if (Issue_Type.featOkTst && ((Issue_Type)jcasType).casFeat_Issue == null)
      jcasType.jcas.throwFeatMissing("Issue", "assertionRegression.annotationTypes.Issue");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Issue_Type)jcasType).casFeatCode_Issue);}
    
  /** setter for Issue - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIssue(String v) {
    if (Issue_Type.featOkTst && ((Issue_Type)jcasType).casFeat_Issue == null)
      jcasType.jcas.throwFeatMissing("Issue", "assertionRegression.annotationTypes.Issue");
    jcasType.ll_cas.ll_setStringValue(addr, ((Issue_Type)jcasType).casFeatCode_Issue, v);}    
  }

    