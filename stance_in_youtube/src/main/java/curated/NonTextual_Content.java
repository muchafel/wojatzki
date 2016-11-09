

/* First created by JCasGen Mon Nov 07 13:23:09 CET 2016 */
package curated;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Nov 07 13:23:09 CET 2016
 * XML source: /Users/michael/git/ucsm_git/stance_in_youtube/src/main/resources/desc/type/typesystem_curated.xml
 * @generated */
public class NonTextual_Content extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(NonTextual_Content.class);
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
  protected NonTextual_Content() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public NonTextual_Content(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public NonTextual_Content(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public NonTextual_Content(JCas jcas, int begin, int end) {
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
  //* Feature: Source

  /** getter for Source - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSource() {
    if (NonTextual_Content_Type.featOkTst && ((NonTextual_Content_Type)jcasType).casFeat_Source == null)
      jcasType.jcas.throwFeatMissing("Source", "curated.NonTextual_Content");
    return jcasType.ll_cas.ll_getStringValue(addr, ((NonTextual_Content_Type)jcasType).casFeatCode_Source);}
    
  /** setter for Source - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSource(String v) {
    if (NonTextual_Content_Type.featOkTst && ((NonTextual_Content_Type)jcasType).casFeat_Source == null)
      jcasType.jcas.throwFeatMissing("Source", "curated.NonTextual_Content");
    jcasType.ll_cas.ll_setStringValue(addr, ((NonTextual_Content_Type)jcasType).casFeatCode_Source, v);}    
  }

    