
/* First created by JCasGen Tue Nov 10 11:11:35 CET 2015 */
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
 * Updated by JCasGen Tue Nov 24 15:45:21 CET 2015
 * @generated */
public class Aspect_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Aspect_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Aspect_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Aspect(addr, Aspect_Type.this);
  			   Aspect_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Aspect(addr, Aspect_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Aspect.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("types.Aspect");
 
  /** @generated */
  final Feature casFeat_grounding;
  /** @generated */
  final int     casFeatCode_grounding;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getGrounding(int addr) {
        if (featOkTst && casFeat_grounding == null)
      jcas.throwFeatMissing("grounding", "types.Aspect");
    return ll_cas.ll_getStringValue(addr, casFeatCode_grounding);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setGrounding(int addr, String v) {
        if (featOkTst && casFeat_grounding == null)
      jcas.throwFeatMissing("grounding", "types.Aspect");
    ll_cas.ll_setStringValue(addr, casFeatCode_grounding, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Aspect_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_grounding = jcas.getRequiredFeatureDE(casType, "grounding", "uima.cas.String", featOkTst);
    casFeatCode_grounding  = (null == casFeat_grounding) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_grounding).getCode();

  }
}



    