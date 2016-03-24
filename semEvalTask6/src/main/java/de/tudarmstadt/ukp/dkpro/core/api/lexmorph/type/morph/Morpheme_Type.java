
/* First created by JCasGen Thu Mar 24 15:20:37 CET 2016 */
package de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph;

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
public class Morpheme_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Morpheme_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Morpheme_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Morpheme(addr, Morpheme_Type.this);
  			   Morpheme_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Morpheme(addr, Morpheme_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Morpheme.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.Morpheme");
 
  /** @generated */
  final Feature casFeat_morphTag;
  /** @generated */
  final int     casFeatCode_morphTag;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getMorphTag(int addr) {
        if (featOkTst && casFeat_morphTag == null)
      jcas.throwFeatMissing("morphTag", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.Morpheme");
    return ll_cas.ll_getStringValue(addr, casFeatCode_morphTag);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMorphTag(int addr, String v) {
        if (featOkTst && casFeat_morphTag == null)
      jcas.throwFeatMissing("morphTag", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.Morpheme");
    ll_cas.ll_setStringValue(addr, casFeatCode_morphTag, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Morpheme_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_morphTag = jcas.getRequiredFeatureDE(casType, "morphTag", "uima.cas.String", featOkTst);
    casFeatCode_morphTag  = (null == casFeat_morphTag) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_morphTag).getCode();

  }
}



    