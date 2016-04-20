
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
public class UnUnderstandability_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (UnUnderstandability_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = UnUnderstandability_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new UnUnderstandability(addr, UnUnderstandability_Type.this);
  			   UnUnderstandability_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new UnUnderstandability(addr, UnUnderstandability_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = UnUnderstandability.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("webanno.custom.UnUnderstandability");
 
  /** @generated */
  final Feature casFeat_notUnderstandable;
  /** @generated */
  final int     casFeatCode_notUnderstandable;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getNotUnderstandable(int addr) {
        if (featOkTst && casFeat_notUnderstandable == null)
      jcas.throwFeatMissing("notUnderstandable", "webanno.custom.UnUnderstandability");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_notUnderstandable);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNotUnderstandable(int addr, boolean v) {
        if (featOkTst && casFeat_notUnderstandable == null)
      jcas.throwFeatMissing("notUnderstandable", "webanno.custom.UnUnderstandability");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_notUnderstandable, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public UnUnderstandability_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_notUnderstandable = jcas.getRequiredFeatureDE(casType, "notUnderstandable", "uima.cas.Boolean", featOkTst);
    casFeatCode_notUnderstandable  = (null == casFeat_notUnderstandable) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_notUnderstandable).getCode();

  }
}



    