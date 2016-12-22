

/* First created by JCasGen Thu Nov 10 11:34:17 CET 2016 */
package preprocessing;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Dec 21 10:36:37 CET 2016
 * XML source: /Users/michael/git/ucsm_git/stance_in_youtube/src/main/resources/desc/type/typesystem_preprocessing.xml
 * @generated */
public class Users extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Users.class);
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
  protected Users() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Users(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Users(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Users(JCas jcas, int begin, int end) {
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
  //* Feature: Author

  /** getter for Author - gets 
   * @generated
   * @return value of the feature 
   */
  public String getAuthor() {
    if (Users_Type.featOkTst && ((Users_Type)jcasType).casFeat_Author == null)
      jcasType.jcas.throwFeatMissing("Author", "preprocessing.Users");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Users_Type)jcasType).casFeatCode_Author);}
    
  /** setter for Author - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAuthor(String v) {
    if (Users_Type.featOkTst && ((Users_Type)jcasType).casFeat_Author == null)
      jcasType.jcas.throwFeatMissing("Author", "preprocessing.Users");
    jcasType.ll_cas.ll_setStringValue(addr, ((Users_Type)jcasType).casFeatCode_Author, v);}    
   
    
  //*--------------*
  //* Feature: Referee

  /** getter for Referee - gets 
   * @generated
   * @return value of the feature 
   */
  public String getReferee() {
    if (Users_Type.featOkTst && ((Users_Type)jcasType).casFeat_Referee == null)
      jcasType.jcas.throwFeatMissing("Referee", "preprocessing.Users");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Users_Type)jcasType).casFeatCode_Referee);}
    
  /** setter for Referee - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setReferee(String v) {
    if (Users_Type.featOkTst && ((Users_Type)jcasType).casFeat_Referee == null)
      jcasType.jcas.throwFeatMissing("Referee", "preprocessing.Users");
    jcasType.ll_cas.ll_setStringValue(addr, ((Users_Type)jcasType).casFeatCode_Referee, v);}    
  }

    