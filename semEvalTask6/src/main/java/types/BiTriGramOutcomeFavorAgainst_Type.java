
/* First created by JCasGen Thu Jan 07 13:52:46 CET 2016 */
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
public class BiTriGramOutcomeFavorAgainst_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (BiTriGramOutcomeFavorAgainst_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = BiTriGramOutcomeFavorAgainst_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new BiTriGramOutcomeFavorAgainst(addr, BiTriGramOutcomeFavorAgainst_Type.this);
  			   BiTriGramOutcomeFavorAgainst_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new BiTriGramOutcomeFavorAgainst(addr, BiTriGramOutcomeFavorAgainst_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = BiTriGramOutcomeFavorAgainst.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("types.BiTriGramOutcomeFavorAgainst");
 
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
      jcas.throwFeatMissing("classificationOutcome", "types.BiTriGramOutcomeFavorAgainst");
    return ll_cas.ll_getStringValue(addr, casFeatCode_classificationOutcome);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setClassificationOutcome(int addr, String v) {
        if (featOkTst && casFeat_classificationOutcome == null)
      jcas.throwFeatMissing("classificationOutcome", "types.BiTriGramOutcomeFavorAgainst");
    ll_cas.ll_setStringValue(addr, casFeatCode_classificationOutcome, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public BiTriGramOutcomeFavorAgainst_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_classificationOutcome = jcas.getRequiredFeatureDE(casType, "classificationOutcome", "uima.cas.String", featOkTst);
    casFeatCode_classificationOutcome  = (null == casFeat_classificationOutcome) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_classificationOutcome).getCode();

  }
}



    