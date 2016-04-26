
/* First created by JCasGen Mon Apr 25 15:05:07 CEST 2016 */
package predictedTypes;

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
 * Updated by JCasGen Mon Apr 25 15:05:07 CEST 2016
 * @generated */
public class NgramClassification_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (NgramClassification_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = NgramClassification_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new NgramClassification(addr, NgramClassification_Type.this);
  			   NgramClassification_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new NgramClassification(addr, NgramClassification_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = NgramClassification.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("predictedTypes.NgramClassification");
 
  /** @generated */
  final Feature casFeat_classificationOutcome;
  /** @generated */
  final int     casFeatCode_classificationOutcome;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getClassificationOutcome(int addr) {
        if (featOkTst && casFeat_classificationOutcome == null)
      jcas.throwFeatMissing("classificationOutcome", "predictedTypes.NgramClassification");
    return ll_cas.ll_getStringValue(addr, casFeatCode_classificationOutcome);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setClassificationOutcome(int addr, String v) {
        if (featOkTst && casFeat_classificationOutcome == null)
      jcas.throwFeatMissing("classificationOutcome", "predictedTypes.NgramClassification");
    ll_cas.ll_setStringValue(addr, casFeatCode_classificationOutcome, v);}
    
  
 
  /** @generated */
  final Feature casFeat_variant;
  /** @generated */
  final int     casFeatCode_variant;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getVariant(int addr) {
        if (featOkTst && casFeat_variant == null)
      jcas.throwFeatMissing("variant", "predictedTypes.NgramClassification");
    return ll_cas.ll_getStringValue(addr, casFeatCode_variant);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setVariant(int addr, String v) {
        if (featOkTst && casFeat_variant == null)
      jcas.throwFeatMissing("variant", "predictedTypes.NgramClassification");
    ll_cas.ll_setStringValue(addr, casFeatCode_variant, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public NgramClassification_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_classificationOutcome = jcas.getRequiredFeatureDE(casType, "classificationOutcome", "uima.cas.String", featOkTst);
    casFeatCode_classificationOutcome  = (null == casFeat_classificationOutcome) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_classificationOutcome).getCode();

 
    casFeat_variant = jcas.getRequiredFeatureDE(casType, "variant", "uima.cas.String", featOkTst);
    casFeatCode_variant  = (null == casFeat_variant) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_variant).getCode();

  }
}



    