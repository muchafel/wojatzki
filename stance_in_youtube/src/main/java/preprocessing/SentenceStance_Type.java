
/* First created by JCasGen Wed Dec 21 10:36:37 CET 2016 */
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
public class SentenceStance_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (SentenceStance_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = SentenceStance_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new SentenceStance(addr, SentenceStance_Type.this);
  			   SentenceStance_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new SentenceStance(addr, SentenceStance_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = SentenceStance.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("preprocessing.SentenceStance");
 
  /** @generated */
  final Feature casFeat_Target;
  /** @generated */
  final int     casFeatCode_Target;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTarget(int addr) {
        if (featOkTst && casFeat_Target == null)
      jcas.throwFeatMissing("Target", "preprocessing.SentenceStance");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Target);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTarget(int addr, String v) {
        if (featOkTst && casFeat_Target == null)
      jcas.throwFeatMissing("Target", "preprocessing.SentenceStance");
    ll_cas.ll_setStringValue(addr, casFeatCode_Target, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Polarity;
  /** @generated */
  final int     casFeatCode_Polarity;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getPolarity(int addr) {
        if (featOkTst && casFeat_Polarity == null)
      jcas.throwFeatMissing("Polarity", "preprocessing.SentenceStance");
    return ll_cas.ll_getIntValue(addr, casFeatCode_Polarity);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPolarity(int addr, int v) {
        if (featOkTst && casFeat_Polarity == null)
      jcas.throwFeatMissing("Polarity", "preprocessing.SentenceStance");
    ll_cas.ll_setIntValue(addr, casFeatCode_Polarity, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public SentenceStance_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Target = jcas.getRequiredFeatureDE(casType, "Target", "uima.cas.String", featOkTst);
    casFeatCode_Target  = (null == casFeat_Target) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Target).getCode();

 
    casFeat_Polarity = jcas.getRequiredFeatureDE(casType, "Polarity", "uima.cas.Integer", featOkTst);
    casFeatCode_Polarity  = (null == casFeat_Polarity) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Polarity).getCode();

  }
}



    