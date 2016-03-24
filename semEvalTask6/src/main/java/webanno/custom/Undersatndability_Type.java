
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
public class Undersatndability_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Undersatndability_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Undersatndability_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Undersatndability(addr, Undersatndability_Type.this);
  			   Undersatndability_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Undersatndability(addr, Undersatndability_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Undersatndability.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("webanno.custom.Undersatndability");
 
  /** @generated */
  final Feature casFeat_Understandability;
  /** @generated */
  final int     casFeatCode_Understandability;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getUnderstandability(int addr) {
        if (featOkTst && casFeat_Understandability == null)
      jcas.throwFeatMissing("Understandability", "webanno.custom.Undersatndability");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Understandability);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setUnderstandability(int addr, String v) {
        if (featOkTst && casFeat_Understandability == null)
      jcas.throwFeatMissing("Understandability", "webanno.custom.Undersatndability");
    ll_cas.ll_setStringValue(addr, casFeatCode_Understandability, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Undersatndability_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Understandability = jcas.getRequiredFeatureDE(casType, "Understandability", "uima.cas.String", featOkTst);
    casFeatCode_Understandability  = (null == casFeat_Understandability) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Understandability).getCode();

  }
}



    