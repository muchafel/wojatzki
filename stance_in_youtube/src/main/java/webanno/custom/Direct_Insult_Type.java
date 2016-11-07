
/* First created by JCasGen Mon Oct 17 08:10:18 CET 2016 */
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
 * Updated by JCasGen Mon Oct 17 08:10:18 CET 2016
 * @generated */
public class Direct_Insult_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Direct_Insult_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Direct_Insult_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Direct_Insult(addr, Direct_Insult_Type.this);
  			   Direct_Insult_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Direct_Insult(addr, Direct_Insult_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Direct_Insult.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("webanno.custom.Direct_Insult");
 
  /** @generated */
  final Feature casFeat_InsultTarget;
  /** @generated */
  final int     casFeatCode_InsultTarget;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getInsultTarget(int addr) {
        if (featOkTst && casFeat_InsultTarget == null)
      jcas.throwFeatMissing("InsultTarget", "webanno.custom.Direct_Insult");
    return ll_cas.ll_getStringValue(addr, casFeatCode_InsultTarget);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setInsultTarget(int addr, String v) {
        if (featOkTst && casFeat_InsultTarget == null)
      jcas.throwFeatMissing("InsultTarget", "webanno.custom.Direct_Insult");
    ll_cas.ll_setStringValue(addr, casFeatCode_InsultTarget, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Direct_Insult_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_InsultTarget = jcas.getRequiredFeatureDE(casType, "InsultTarget", "uima.cas.String", featOkTst);
    casFeatCode_InsultTarget  = (null == casFeat_InsultTarget) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_InsultTarget).getCode();

  }
}



    