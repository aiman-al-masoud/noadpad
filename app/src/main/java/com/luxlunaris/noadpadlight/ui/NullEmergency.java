package com.luxlunaris.noadpadlight.ui;

/**
 * To be implemented by "top of the stack" gui components,
 * ie: Activities.
 *
 * This informs the Activities that some
 * critical component of the business logic
 * has been wiped out by the garbage collector
 * while the app was in the background.
 *
 * The Activity can then act accordingly.
 *
 * Any gui component or class w/ access to Context
 * can get the instance of the activity that's running
 * (getActivity()) and try casting it to a "NullEmergency".
 *
 * I'm doing this 'cuz I haven't been
 * able to find a way to catch the NullPointerExceptions
 * being thrown by Fragments in my Activities.
 *
 *
 */

public interface NullEmergency {

    /**
     * Called when one or more Pages are null.
     */
    void onNullPage();



}
