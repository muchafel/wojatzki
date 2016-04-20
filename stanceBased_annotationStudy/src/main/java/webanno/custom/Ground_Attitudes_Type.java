
/* First created by JCasGen Tue Apr 05 14:29:09 CEST 2016 */
package webanno.custom;

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
 * Updated by JCasGen Tue Apr 05 14:29:09 CEST 2016
 * @generated */
public class Ground_Attitudes_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Ground_Attitudes_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Ground_Attitudes_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Ground_Attitudes(addr, Ground_Attitudes_Type.this);
  			   Ground_Attitudes_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Ground_Attitudes(addr, Ground_Attitudes_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Ground_Attitudes.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("webanno.custom.Ground_Attitudes");
 
  /** @generated */
  final Feature casFeat_Attitudes;
  /** @generated */
  final int     casFeatCode_Attitudes;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getAttitudes(int addr) {
        if (featOkTst && casFeat_Attitudes == null)
      jcas.throwFeatMissing("Attitudes", "webanno.custom.Ground_Attitudes");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Attitudes);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAttitudes(int addr, String v) {
        if (featOkTst && casFeat_Attitudes == null)
      jcas.throwFeatMissing("Attitudes", "webanno.custom.Ground_Attitudes");
    ll_cas.ll_setStringValue(addr, casFeatCode_Attitudes, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Polarity;
  /** @generated */
  final int     casFeatCode_Polarity;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPolarity(int addr) {
        if (featOkTst && casFeat_Polarity == null)
      jcas.throwFeatMissing("Polarity", "webanno.custom.Ground_Attitudes");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Polarity);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPolarity(int addr, String v) {
        if (featOkTst && casFeat_Polarity == null)
      jcas.throwFeatMissing("Polarity", "webanno.custom.Ground_Attitudes");
    ll_cas.ll_setStringValue(addr, casFeatCode_Polarity, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Ground_Attitudes_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Attitudes = jcas.getRequiredFeatureDE(casType, "Attitudes", "uima.cas.String", featOkTst);
    casFeatCode_Attitudes  = (null == casFeat_Attitudes) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Attitudes).getCode();

 
    casFeat_Polarity = jcas.getRequiredFeatureDE(casType, "Polarity", "uima.cas.String", featOkTst);
    casFeatCode_Polarity  = (null == casFeat_Polarity) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Polarity).getCode();

  }
}



    