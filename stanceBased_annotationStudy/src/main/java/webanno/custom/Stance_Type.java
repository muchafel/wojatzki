
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
public class Stance_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Stance_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Stance_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Stance(addr, Stance_Type.this);
  			   Stance_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Stance(addr, Stance_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Stance.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("webanno.custom.Stance");
 
  /** @generated */
  final Feature casFeat_Stance_Polarity;
  /** @generated */
  final int     casFeatCode_Stance_Polarity;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getStance_Polarity(int addr) {
        if (featOkTst && casFeat_Stance_Polarity == null)
      jcas.throwFeatMissing("Stance_Polarity", "webanno.custom.Stance");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Stance_Polarity);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setStance_Polarity(int addr, String v) {
        if (featOkTst && casFeat_Stance_Polarity == null)
      jcas.throwFeatMissing("Stance_Polarity", "webanno.custom.Stance");
    ll_cas.ll_setStringValue(addr, casFeatCode_Stance_Polarity, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Stance_Target;
  /** @generated */
  final int     casFeatCode_Stance_Target;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getStance_Target(int addr) {
        if (featOkTst && casFeat_Stance_Target == null)
      jcas.throwFeatMissing("Stance_Target", "webanno.custom.Stance");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Stance_Target);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setStance_Target(int addr, String v) {
        if (featOkTst && casFeat_Stance_Target == null)
      jcas.throwFeatMissing("Stance_Target", "webanno.custom.Stance");
    ll_cas.ll_setStringValue(addr, casFeatCode_Stance_Target, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Stance_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Stance_Polarity = jcas.getRequiredFeatureDE(casType, "Stance_Polarity", "uima.cas.String", featOkTst);
    casFeatCode_Stance_Polarity  = (null == casFeat_Stance_Polarity) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Stance_Polarity).getCode();

 
    casFeat_Stance_Target = jcas.getRequiredFeatureDE(casType, "Stance_Target", "uima.cas.String", featOkTst);
    casFeatCode_Stance_Target  = (null == casFeat_Stance_Target) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Stance_Target).getCode();

  }
}



    