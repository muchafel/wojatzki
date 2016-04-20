
/* First created by JCasGen Thu Mar 24 15:20:38 CET 2016 */
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
 * Updated by JCasGen Thu Mar 24 15:20:38 CET 2016
 * @generated */
public class Alternative_concepts_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Alternative_concepts_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Alternative_concepts_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Alternative_concepts(addr, Alternative_concepts_Type.this);
  			   Alternative_concepts_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Alternative_concepts(addr, Alternative_concepts_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Alternative_concepts.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("webanno.custom.Alternative_concepts");
 
  /** @generated */
  final Feature casFeat_alternative_concepts;
  /** @generated */
  final int     casFeatCode_alternative_concepts;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getAlternative_concepts(int addr) {
        if (featOkTst && casFeat_alternative_concepts == null)
      jcas.throwFeatMissing("alternative_concepts", "webanno.custom.Alternative_concepts");
    return ll_cas.ll_getStringValue(addr, casFeatCode_alternative_concepts);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAlternative_concepts(int addr, String v) {
        if (featOkTst && casFeat_alternative_concepts == null)
      jcas.throwFeatMissing("alternative_concepts", "webanno.custom.Alternative_concepts");
    ll_cas.ll_setStringValue(addr, casFeatCode_alternative_concepts, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Alternative_concepts_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_alternative_concepts = jcas.getRequiredFeatureDE(casType, "alternative_concepts", "uima.cas.String", featOkTst);
    casFeatCode_alternative_concepts  = (null == casFeat_alternative_concepts) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_alternative_concepts).getCode();

  }
}



    