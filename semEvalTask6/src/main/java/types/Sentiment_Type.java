
/* First created by JCasGen Tue Nov 24 15:45:21 CET 2015 */
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
 * Updated by JCasGen Thu Jan 14 11:10:51 CET 2016
 * @generated */
public class Sentiment_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Sentiment_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Sentiment_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Sentiment(addr, Sentiment_Type.this);
  			   Sentiment_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Sentiment(addr, Sentiment_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Sentiment.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("types.Sentiment");
 
  /** @generated */
  final Feature casFeat_nrcSentiment;
  /** @generated */
  final int     casFeatCode_nrcSentiment;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public float getNrcSentiment(int addr) {
        if (featOkTst && casFeat_nrcSentiment == null)
      jcas.throwFeatMissing("nrcSentiment", "types.Sentiment");
    return ll_cas.ll_getFloatValue(addr, casFeatCode_nrcSentiment);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNrcSentiment(int addr, float v) {
        if (featOkTst && casFeat_nrcSentiment == null)
      jcas.throwFeatMissing("nrcSentiment", "types.Sentiment");
    ll_cas.ll_setFloatValue(addr, casFeatCode_nrcSentiment, v);}
    
  
 
  /** @generated */
  final Feature casFeat_mpqaSentiment;
  /** @generated */
  final int     casFeatCode_mpqaSentiment;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public float getMpqaSentiment(int addr) {
        if (featOkTst && casFeat_mpqaSentiment == null)
      jcas.throwFeatMissing("mpqaSentiment", "types.Sentiment");
    return ll_cas.ll_getFloatValue(addr, casFeatCode_mpqaSentiment);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMpqaSentiment(int addr, float v) {
        if (featOkTst && casFeat_mpqaSentiment == null)
      jcas.throwFeatMissing("mpqaSentiment", "types.Sentiment");
    ll_cas.ll_setFloatValue(addr, casFeatCode_mpqaSentiment, v);}
    
  
 
  /** @generated */
  final Feature casFeat_bingLiuSentiment;
  /** @generated */
  final int     casFeatCode_bingLiuSentiment;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public float getBingLiuSentiment(int addr) {
        if (featOkTst && casFeat_bingLiuSentiment == null)
      jcas.throwFeatMissing("bingLiuSentiment", "types.Sentiment");
    return ll_cas.ll_getFloatValue(addr, casFeatCode_bingLiuSentiment);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setBingLiuSentiment(int addr, float v) {
        if (featOkTst && casFeat_bingLiuSentiment == null)
      jcas.throwFeatMissing("bingLiuSentiment", "types.Sentiment");
    ll_cas.ll_setFloatValue(addr, casFeatCode_bingLiuSentiment, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Sentiment_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_nrcSentiment = jcas.getRequiredFeatureDE(casType, "nrcSentiment", "uima.cas.Float", featOkTst);
    casFeatCode_nrcSentiment  = (null == casFeat_nrcSentiment) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_nrcSentiment).getCode();

 
    casFeat_mpqaSentiment = jcas.getRequiredFeatureDE(casType, "mpqaSentiment", "uima.cas.Float", featOkTst);
    casFeatCode_mpqaSentiment  = (null == casFeat_mpqaSentiment) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_mpqaSentiment).getCode();

 
    casFeat_bingLiuSentiment = jcas.getRequiredFeatureDE(casType, "bingLiuSentiment", "uima.cas.Float", featOkTst);
    casFeatCode_bingLiuSentiment  = (null == casFeat_bingLiuSentiment) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_bingLiuSentiment).getCode();

  }
}



    