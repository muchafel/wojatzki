

/* First created by JCasGen Thu Nov 10 11:34:17 CET 2016 */
package preprocessing;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Nov 10 11:34:17 CET 2016
 * XML source: /Users/michael/git/ucsm_git/stance_in_youtube/src/main/resources/desc/type/typesystem_preprocessing.xml
 * @generated */
public class CommentType extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CommentType.class);
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
  protected CommentType() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public CommentType(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CommentType(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public CommentType(JCas jcas, int begin, int end) {
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
  //* Feature: commentNotReply

  /** getter for commentNotReply - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getCommentNotReply() {
    if (CommentType_Type.featOkTst && ((CommentType_Type)jcasType).casFeat_commentNotReply == null)
      jcasType.jcas.throwFeatMissing("commentNotReply", "preprocessing.CommentType");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((CommentType_Type)jcasType).casFeatCode_commentNotReply);}
    
  /** setter for commentNotReply - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCommentNotReply(boolean v) {
    if (CommentType_Type.featOkTst && ((CommentType_Type)jcasType).casFeat_commentNotReply == null)
      jcasType.jcas.throwFeatMissing("commentNotReply", "preprocessing.CommentType");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((CommentType_Type)jcasType).casFeatCode_commentNotReply, v);}    
  }

    