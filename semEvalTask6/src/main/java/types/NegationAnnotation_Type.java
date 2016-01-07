
/* First created by JCasGen Tue Nov 10 10:05:22 CET 2015 */
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
public class NegationAnnotation_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (NegationAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = NegationAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new NegationAnnotation(addr, NegationAnnotation_Type.this);
  			   NegationAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new NegationAnnotation(addr, NegationAnnotation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = NegationAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("types.NegationAnnotation");
 
  /** @generated */
  final Feature casFeat_isNegation;
  /** @generated */
  final int     casFeatCode_isNegation;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getIsNegation(int addr) {
        if (featOkTst && casFeat_isNegation == null)
      jcas.throwFeatMissing("isNegation", "types.NegationAnnotation");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_isNegation);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsNegation(int addr, boolean v) {
        if (featOkTst && casFeat_isNegation == null)
      jcas.throwFeatMissing("isNegation", "types.NegationAnnotation");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_isNegation, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public NegationAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_isNegation = jcas.getRequiredFeatureDE(casType, "isNegation", "uima.cas.Boolean", featOkTst);
    casFeatCode_isNegation  = (null == casFeat_isNegation) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_isNegation).getCode();

  }
}



    