
/* First created by JCasGen Wed Oct 28 15:44:08 CET 2015 */
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
public class ModalVerb_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ModalVerb_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ModalVerb_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ModalVerb(addr, ModalVerb_Type.this);
  			   ModalVerb_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ModalVerb(addr, ModalVerb_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ModalVerb.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("types.ModalVerb");
 
  /** @generated */
  final Feature casFeat_isModalVerb;
  /** @generated */
  final int     casFeatCode_isModalVerb;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getIsModalVerb(int addr) {
        if (featOkTst && casFeat_isModalVerb == null)
      jcas.throwFeatMissing("isModalVerb", "types.ModalVerb");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_isModalVerb);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsModalVerb(int addr, boolean v) {
        if (featOkTst && casFeat_isModalVerb == null)
      jcas.throwFeatMissing("isModalVerb", "types.ModalVerb");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_isModalVerb, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ModalVerb_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_isModalVerb = jcas.getRequiredFeatureDE(casType, "isModalVerb", "uima.cas.Boolean", featOkTst);
    casFeatCode_isModalVerb  = (null == casFeat_isModalVerb) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_isModalVerb).getCode();

  }
}



    