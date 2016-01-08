
/* First created by JCasGen Thu Nov 05 09:23:12 CET 2015 */
package types;

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
 * Updated by JCasGen Fri Jan 08 14:00:38 CET 2016
 * @generated */
public class TwitterSpecificPOS_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (TwitterSpecificPOS_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TwitterSpecificPOS_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TwitterSpecificPOS(addr, TwitterSpecificPOS_Type.this);
  			   TwitterSpecificPOS_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TwitterSpecificPOS(addr, TwitterSpecificPOS_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TwitterSpecificPOS.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("types.TwitterSpecificPOS");
 
  /** @generated */
  final Feature casFeat_isTokenTwitterSpecific;
  /** @generated */
  final int     casFeatCode_isTokenTwitterSpecific;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getIsTokenTwitterSpecific(int addr) {
        if (featOkTst && casFeat_isTokenTwitterSpecific == null)
      jcas.throwFeatMissing("isTokenTwitterSpecific", "types.TwitterSpecificPOS");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_isTokenTwitterSpecific);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsTokenTwitterSpecific(int addr, boolean v) {
        if (featOkTst && casFeat_isTokenTwitterSpecific == null)
      jcas.throwFeatMissing("isTokenTwitterSpecific", "types.TwitterSpecificPOS");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_isTokenTwitterSpecific, v);}
    
  
 
  /** @generated */
  final Feature casFeat_tag;
  /** @generated */
  final int     casFeatCode_tag;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTag(int addr) {
        if (featOkTst && casFeat_tag == null)
      jcas.throwFeatMissing("tag", "types.TwitterSpecificPOS");
    return ll_cas.ll_getStringValue(addr, casFeatCode_tag);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTag(int addr, String v) {
        if (featOkTst && casFeat_tag == null)
      jcas.throwFeatMissing("tag", "types.TwitterSpecificPOS");
    ll_cas.ll_setStringValue(addr, casFeatCode_tag, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TwitterSpecificPOS_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_isTokenTwitterSpecific = jcas.getRequiredFeatureDE(casType, "isTokenTwitterSpecific", "uima.cas.Boolean", featOkTst);
    casFeatCode_isTokenTwitterSpecific  = (null == casFeat_isTokenTwitterSpecific) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_isTokenTwitterSpecific).getCode();

 
    casFeat_tag = jcas.getRequiredFeatureDE(casType, "tag", "uima.cas.String", featOkTst);
    casFeatCode_tag  = (null == casFeat_tag) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_tag).getCode();

  }
}



    