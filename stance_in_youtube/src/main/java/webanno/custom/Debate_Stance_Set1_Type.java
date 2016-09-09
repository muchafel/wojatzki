
/* First created by JCasGen Wed Sep 07 09:45:53 CEST 2016 */
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
 * Updated by JCasGen Wed Sep 07 09:45:53 CEST 2016
 * @generated */
public class Debate_Stance_Set1_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Debate_Stance_Set1_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Debate_Stance_Set1_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Debate_Stance_Set1(addr, Debate_Stance_Set1_Type.this);
  			   Debate_Stance_Set1_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Debate_Stance_Set1(addr, Debate_Stance_Set1_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Debate_Stance_Set1.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("webanno.custom.Debate_Stance_Set1");
 
  /** @generated */
  final Feature casFeat_Polarity;
  /** @generated */
  final int     casFeatCode_Polarity;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPolarity(int addr) {
        if (featOkTst && casFeat_Polarity == null)
      jcas.throwFeatMissing("Polarity", "webanno.custom.Debate_Stance_Set1");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Polarity);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPolarity(int addr, String v) {
        if (featOkTst && casFeat_Polarity == null)
      jcas.throwFeatMissing("Polarity", "webanno.custom.Debate_Stance_Set1");
    ll_cas.ll_setStringValue(addr, casFeatCode_Polarity, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Debate_Stance_Set1_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Polarity = jcas.getRequiredFeatureDE(casType, "Polarity", "uima.cas.String", featOkTst);
    casFeatCode_Polarity  = (null == casFeat_Polarity) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Polarity).getCode();

  }
}



    