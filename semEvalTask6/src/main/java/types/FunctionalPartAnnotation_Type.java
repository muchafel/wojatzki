
/* First created by JCasGen Fri Nov 13 09:03:27 CET 2015 */
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
 * Updated by JCasGen Thu Jan 07 11:06:34 CET 2016
 * @generated */
public class FunctionalPartAnnotation_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (FunctionalPartAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = FunctionalPartAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new FunctionalPartAnnotation(addr, FunctionalPartAnnotation_Type.this);
  			   FunctionalPartAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new FunctionalPartAnnotation(addr, FunctionalPartAnnotation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = FunctionalPartAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("types.FunctionalPartAnnotation");
 
  /** @generated */
  final Feature casFeat_function;
  /** @generated */
  final int     casFeatCode_function;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getFunction(int addr) {
        if (featOkTst && casFeat_function == null)
      jcas.throwFeatMissing("function", "types.FunctionalPartAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_function);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFunction(int addr, String v) {
        if (featOkTst && casFeat_function == null)
      jcas.throwFeatMissing("function", "types.FunctionalPartAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_function, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public FunctionalPartAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_function = jcas.getRequiredFeatureDE(casType, "function", "uima.cas.String", featOkTst);
    casFeatCode_function  = (null == casFeat_function) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_function).getCode();

  }
}



    