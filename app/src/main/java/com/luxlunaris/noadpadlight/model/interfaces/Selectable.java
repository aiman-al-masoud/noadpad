package com.luxlunaris.noadpadlight.model.interfaces;

import java.io.Serializable;

/**
 * An object that can be (un/)selected and remember its state.
 */
public interface Selectable extends Serializable {

    /**
     * Is this Page currently selected?
     * @return
     */
    boolean isSelected();

    /**
     * Set this Page as selected.
     * @param select
     */
    void setSelected(boolean select);



}
