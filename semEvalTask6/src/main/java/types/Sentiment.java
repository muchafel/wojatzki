

/* First created by JCasGen Tue Nov 24 15:45:21 CET 2015 */
package types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Jan 07 11:06:34 CET 2016
 * XML source: /Users/michael/git/ucsm_git/semEvalTask6/src/main/resources/desc/type/ownArgTypes.xml
 * @generated */
public class Sentiment extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Sentiment.class);
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
  protected Sentiment() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Sentiment(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Sentiment(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Sentiment(JCas jcas, int begin, int end) {
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
  //* Feature: nrcSentiment

  /** getter for nrcSentiment - gets 
   * @generated
   * @return value of the feature 
   */
  public float getNrcSentiment() {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_nrcSentiment == null)
      jcasType.jcas.throwFeatMissing("nrcSentiment", "types.Sentiment");
    return jcasType.ll_cas.ll_getFloatValue(addr, ((Sentiment_Type)jcasType).casFeatCode_nrcSentiment);}
    
  /** setter for nrcSentiment - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNrcSentiment(float v) {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_nrcSentiment == null)
      jcasType.jcas.throwFeatMissing("nrcSentiment", "types.Sentiment");
    jcasType.ll_cas.ll_setFloatValue(addr, ((Sentiment_Type)jcasType).casFeatCode_nrcSentiment, v);}    
   
    
  //*--------------*
  //* Feature: mpqaSentiment

  /** getter for mpqaSentiment - gets 
   * @generated
   * @return value of the feature 
   */
  public float getMpqaSentiment() {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_mpqaSentiment == null)
      jcasType.jcas.throwFeatMissing("mpqaSentiment", "types.Sentiment");
    return jcasType.ll_cas.ll_getFloatValue(addr, ((Sentiment_Type)jcasType).casFeatCode_mpqaSentiment);}
    
  /** setter for mpqaSentiment - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setMpqaSentiment(float v) {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_mpqaSentiment == null)
      jcasType.jcas.throwFeatMissing("mpqaSentiment", "types.Sentiment");
    jcasType.ll_cas.ll_setFloatValue(addr, ((Sentiment_Type)jcasType).casFeatCode_mpqaSentiment, v);}    
   
    
  //*--------------*
  //* Feature: bingLiuSentiment

  /** getter for bingLiuSentiment - gets 
   * @generated
   * @return value of the feature 
   */
  public float getBingLiuSentiment() {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_bingLiuSentiment == null)
      jcasType.jcas.throwFeatMissing("bingLiuSentiment", "types.Sentiment");
    return jcasType.ll_cas.ll_getFloatValue(addr, ((Sentiment_Type)jcasType).casFeatCode_bingLiuSentiment);}
    
  /** setter for bingLiuSentiment - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBingLiuSentiment(float v) {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_bingLiuSentiment == null)
      jcasType.jcas.throwFeatMissing("bingLiuSentiment", "types.Sentiment");
    jcasType.ll_cas.ll_setFloatValue(addr, ((Sentiment_Type)jcasType).casFeatCode_bingLiuSentiment, v);}    
  }

    