package com.quittle.svg2androidvector;

/**
 * Provides configuration for the {@code Svg 2 Android Vector Plugin}.
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
public class Svg2AndroidVectorExtension {
    private boolean failOnWarning = true;

    /**
     * Default constructor.
     */
    public Svg2AndroidVectorExtension() {
    }

    /**
     * Whether or not to fail the task when there are warnings converting the SVG.
     * @return true if the build should fail.
     */
    public boolean getFailOnWarning() {
        return failOnWarning;
    }

    /**
     * Sets the flag when build should fail when there are warnings converting the SVG. If the SVG fails to
     *        convert entirely, then an empty XML file will be generating, causing AAPT to complain.
     * @param failOnWarning If the build should fail.
     */
    public void setFailOnWarning(final boolean failOnWarning) {
        this.failOnWarning = failOnWarning;
    }

    /**
     * Sets the flag when build should fail when there are warnings converting the SVG. If the SVG fails to
     *        convert entirely, then an empty XML file will be generating, causing AAPT to complain.
     * @param failOnWarning If the build should fail.
     */
    public void failOnWarning(final boolean failOnWarning) {
        this.failOnWarning = failOnWarning;
    }
}
