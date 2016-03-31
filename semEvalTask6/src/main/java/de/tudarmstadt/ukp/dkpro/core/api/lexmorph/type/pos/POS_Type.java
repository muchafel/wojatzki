
/* First created by JCasGen Thu Mar 24 15:20:37 CET 2016 */
package de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos;

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
 * Updated by JCasGen Thu Mar 24 15:20:37 CET 2016
 * @generated */
public class POS_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (POS_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = POS_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new POS(addr, POS_Type.this);
  			   POS_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new POS(addr, POS_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = POS.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS");
 
  /** @generated */
  final Feature casFeat_PosValue;
  /** @generated */
  final int     casFeatCode_PosValue;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPosValue(int addr) {
        if (featOkTst && casFeat_PosValue == null)
      jcas.throwFeatMissing("PosValue", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS");
    return ll_cas.ll_getStringValue(addr, casFeatCode_PosValue);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPosValue(int addr, String v) {
        if (featOkTst && casFeat_PosValue == null)
      jcas.throwFeatMissing("PosValue", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS");
    ll_cas.ll_setStringValue(addr, casFeatCode_PosValue, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public POS_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_PosValue = jcas.getRequiredFeatureDE(casType, "PosValue", "uima.cas.String", featOkTst);
    casFeatCode_PosValue  = (null == casFeat_PosValue) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_PosValue).getCode();

  }
}



    