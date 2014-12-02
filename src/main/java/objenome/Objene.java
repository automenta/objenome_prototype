/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objenome;

/**
 * Gene of an Objenome; a particular solution to an objenomic problem
 */
public interface Objene {
   
    /**
     * the DI target instance key that this affects.
     * will consist of alternating items between:
     *  --Class (input target class)
     *  --ClassBuilder (solved dependent Class)
     *  --DependencyKey (which will contain a String key and possible Parameter reference)
     * 
     */


    /** apply the consequences of this gene to an Phenotainer */
    public void apply(Phenotainer c);




    public String key();

    public void mutate();
    
    
    
}
