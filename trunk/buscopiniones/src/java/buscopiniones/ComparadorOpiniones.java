/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package buscopiniones;

import java.util.Comparator;

/**
 *
 * @author Rodrigo
 */
public class ComparadorOpiniones implements Comparator<Opinion> {
    @Override
    public int compare(Opinion o1, Opinion o2) {
        return o1.getDate().compareTo(o2.getDate());
    }
}
