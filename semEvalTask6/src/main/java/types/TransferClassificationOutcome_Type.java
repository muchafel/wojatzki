
/* First created by JCasGen Thu Jan 14 11:10:41 CET 2016 */
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
public class TransferClassificationOutcome_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (TransferClassificationOutcome_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TransferClassificationOutcome_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TransferClassificationOutcome(addr, TransferClassificationOutcome_Type.this);
  			   TransferClassificationOutcome_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TransferClassificationOutcome(addr, TransferClassificationOutcome_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = TransferClassificationOutcome.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("types.TransferClassificationOutcome");
 
  /** @generated */
  final Feature casFeat_model;
  /** @generated */
  final int     casFeatCode_model;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getModel(int addr) {
        if (featOkTst && casFeat_model == null)
      jcas.throwFeatMissing("model", "types.TransferClassificationOutcome");
    return ll_cas.ll_getStringValue(addr, casFeatCode_model);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setModel(int addr, String v) {
        if (featOkTst && casFeat_model == null)
      jcas.throwFeatMissing("model", "types.TransferClassificationOutcome");
    ll_cas.ll_setStringValue(addr, casFeatCode_model, v);}
    
  
 
  /** @generated */
  final Feature casFeat_outcome;
  /** @generated */
  final int     casFeatCode_outcome;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getOutcome(int addr) {
        if (featOkTst && casFeat_outcome == null)
      jcas.throwFeatMissing("outcome", "types.TransferClassificationOutcome");
    return ll_cas.ll_getStringValue(addr, casFeatCode_outcome);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setOutcome(int addr, String v) {
        if (featOkTst && casFeat_outcome == null)
      jcas.throwFeatMissing("outcome", "types.TransferClassificationOutcome");
    ll_cas.ll_setStringValue(addr, casFeatCode_outcome, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TransferClassificationOutcome_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_model = jcas.getRequiredFeatureDE(casType, "model", "uima.cas.String", featOkTst);
    casFeatCode_model  = (null == casFeat_model) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_model).getCode();

 
    casFeat_outcome = jcas.getRequiredFeatureDE(casType, "outcome", "uima.cas.String", featOkTst);
    casFeatCode_outcome  = (null == casFeat_outcome) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_outcome).getCode();

  }
}



    