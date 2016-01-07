
/* First created by JCasGen Tue Oct 27 17:15:31 CET 2015 */
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
 * Updated by JCasGen Thu Jan 07 14:15:53 CET 2016
 * @generated */
public class StanceAnnotation_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (StanceAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = StanceAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new StanceAnnotation(addr, StanceAnnotation_Type.this);
  			   StanceAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new StanceAnnotation(addr, StanceAnnotation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = StanceAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("types.StanceAnnotation");
 
  /** @generated */
  final Feature casFeat_stance;
  /** @generated */
  final int     casFeatCode_stance;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getStance(int addr) {
        if (featOkTst && casFeat_stance == null)
      jcas.throwFeatMissing("stance", "types.StanceAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_stance);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setStance(int addr, String v) {
        if (featOkTst && casFeat_stance == null)
      jcas.throwFeatMissing("stance", "types.StanceAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_stance, v);}
    
  
 
  /** @generated */
  final Feature casFeat_target;
  /** @generated */
  final int     casFeatCode_target;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTarget(int addr) {
        if (featOkTst && casFeat_target == null)
      jcas.throwFeatMissing("target", "types.StanceAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_target);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTarget(int addr, String v) {
        if (featOkTst && casFeat_target == null)
      jcas.throwFeatMissing("target", "types.StanceAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_target, v);}
    
  
 
  /** @generated */
  final Feature casFeat_originalId;
  /** @generated */
  final int     casFeatCode_originalId;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getOriginalId(int addr) {
        if (featOkTst && casFeat_originalId == null)
      jcas.throwFeatMissing("originalId", "types.StanceAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_originalId);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setOriginalId(int addr, String v) {
        if (featOkTst && casFeat_originalId == null)
      jcas.throwFeatMissing("originalId", "types.StanceAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_originalId, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public StanceAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_stance = jcas.getRequiredFeatureDE(casType, "stance", "uima.cas.String", featOkTst);
    casFeatCode_stance  = (null == casFeat_stance) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_stance).getCode();

 
    casFeat_target = jcas.getRequiredFeatureDE(casType, "target", "uima.cas.String", featOkTst);
    casFeatCode_target  = (null == casFeat_target) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_target).getCode();

 
    casFeat_originalId = jcas.getRequiredFeatureDE(casType, "originalId", "uima.cas.String", featOkTst);
    casFeatCode_originalId  = (null == casFeat_originalId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_originalId).getCode();

  }
}



    