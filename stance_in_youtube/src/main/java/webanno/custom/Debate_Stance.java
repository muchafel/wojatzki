

/* First created by JCasGen Thu Sep 15 11:43:47 CEST 2016 */
package webanno.custom;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Sep 15 11:43:47 CEST 2016
 * XML source: /Users/michael/git/ucsm_git/stance_in_youtube/src/main/resources/desc/type/typesystem.xml
 * @generated */
public class Debate_Stance extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Debate_Stance.class);
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
  protected Debate_Stance() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Debate_Stance(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Debate_Stance(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Debate_Stance(JCas jcas, int begin, int end) {
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
  //* Feature: Polarity

  /** getter for Polarity - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPolarity() {
    if (Debate_Stance_Type.featOkTst && ((Debate_Stance_Type)jcasType).casFeat_Polarity == null)
      jcasType.jcas.throwFeatMissing("Polarity", "webanno.custom.Debate_Stance");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Debate_Stance_Type)jcasType).casFeatCode_Polarity);}
    
  /** setter for Polarity - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPolarity(String v) {
    if (Debate_Stance_Type.featOkTst && ((Debate_Stance_Type)jcasType).casFeat_Polarity == null)
      jcasType.jcas.throwFeatMissing("Polarity", "webanno.custom.Debate_Stance");
    jcasType.ll_cas.ll_setStringValue(addr, ((Debate_Stance_Type)jcasType).casFeatCode_Polarity, v);}    
  }

    