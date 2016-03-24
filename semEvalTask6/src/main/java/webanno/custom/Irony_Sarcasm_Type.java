
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
public class Irony_Sarcasm_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Irony_Sarcasm_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Irony_Sarcasm_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Irony_Sarcasm(addr, Irony_Sarcasm_Type.this);
  			   Irony_Sarcasm_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Irony_Sarcasm(addr, Irony_Sarcasm_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Irony_Sarcasm.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("webanno.custom.Irony_Sarcasm");
 
  /** @generated */
  final Feature casFeat_irony;
  /** @generated */
  final int     casFeatCode_irony;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getIrony(int addr) {
        if (featOkTst && casFeat_irony == null)
      jcas.throwFeatMissing("irony", "webanno.custom.Irony_Sarcasm");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_irony);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIrony(int addr, boolean v) {
        if (featOkTst && casFeat_irony == null)
      jcas.throwFeatMissing("irony", "webanno.custom.Irony_Sarcasm");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_irony, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Irony_Sarcasm_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_irony = jcas.getRequiredFeatureDE(casType, "irony", "uima.cas.Boolean", featOkTst);
    casFeatCode_irony  = (null == casFeat_irony) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_irony).getCode();

  }
}



    