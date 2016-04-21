
/* First created by JCasGen Thu Apr 21 12:49:34 CEST 2016 */
package curatedTypes;

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
 * Updated by JCasGen Thu Apr 21 12:49:34 CEST 2016
 * @generated */
public class CuratedIrony_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (CuratedIrony_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = CuratedIrony_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new CuratedIrony(addr, CuratedIrony_Type.this);
  			   CuratedIrony_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new CuratedIrony(addr, CuratedIrony_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = CuratedIrony.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("curatedTypes.CuratedIrony");
 
  /** @generated */
  final Feature casFeat_Irony;
  /** @generated */
  final int     casFeatCode_Irony;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getIrony(int addr) {
        if (featOkTst && casFeat_Irony == null)
      jcas.throwFeatMissing("Irony", "curatedTypes.CuratedIrony");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Irony);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIrony(int addr, String v) {
        if (featOkTst && casFeat_Irony == null)
      jcas.throwFeatMissing("Irony", "curatedTypes.CuratedIrony");
    ll_cas.ll_setStringValue(addr, casFeatCode_Irony, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public CuratedIrony_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Irony = jcas.getRequiredFeatureDE(casType, "Irony", "uima.cas.String", featOkTst);
    casFeatCode_Irony  = (null == casFeat_Irony) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Irony).getCode();

  }
}



    