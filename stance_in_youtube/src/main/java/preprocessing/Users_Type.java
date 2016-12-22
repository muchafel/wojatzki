
/* First created by JCasGen Thu Nov 10 11:34:17 CET 2016 */
package preprocessing;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Wed Dec 21 10:36:37 CET 2016
 * @generated */
public class Users_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Users_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Users_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Users(addr, Users_Type.this);
  			   Users_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Users(addr, Users_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Users.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("preprocessing.Users");
 
  /** @generated */
  final Feature casFeat_Author;
  /** @generated */
  final int     casFeatCode_Author;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getAuthor(int addr) {
        if (featOkTst && casFeat_Author == null)
      jcas.throwFeatMissing("Author", "preprocessing.Users");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Author);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAuthor(int addr, String v) {
        if (featOkTst && casFeat_Author == null)
      jcas.throwFeatMissing("Author", "preprocessing.Users");
    ll_cas.ll_setStringValue(addr, casFeatCode_Author, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Referee;
  /** @generated */
  final int     casFeatCode_Referee;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getReferee(int addr) {
        if (featOkTst && casFeat_Referee == null)
      jcas.throwFeatMissing("Referee", "preprocessing.Users");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Referee);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setReferee(int addr, String v) {
        if (featOkTst && casFeat_Referee == null)
      jcas.throwFeatMissing("Referee", "preprocessing.Users");
    ll_cas.ll_setStringValue(addr, casFeatCode_Referee, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Users_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Author = jcas.getRequiredFeatureDE(casType, "Author", "uima.cas.String", featOkTst);
    casFeatCode_Author  = (null == casFeat_Author) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Author).getCode();

 
    casFeat_Referee = jcas.getRequiredFeatureDE(casType, "Referee", "uima.cas.String", featOkTst);
    casFeatCode_Referee  = (null == casFeat_Referee) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Referee).getCode();

  }
}



    