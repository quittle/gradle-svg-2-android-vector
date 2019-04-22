package com.quittle.svg2androidvector;

/**
 * Provides configuration for the {@code Svg 2 Android Vector Plugin}.
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
public class Svg2AndroidVectorExtension {
    private boolean failOnWarning = true;

    /**
     * @return true if the build should fail if there are errors converting the SVG.
     */
    public boolean getFailOnWarning() {
        return failOnWarning;
    }

    /**
     * @param failOnWarning If the build should fail when there are warnings converting the SVG. If the SVG fails to
     *                      convert entirely, then an empty XML file will be generating, causing AAPT to complain.
     */
    public void setFailOnWarning(final boolean failOnWarning) {
        this.failOnWarning = failOnWarning;
    }

    /**
     * @param failOnWarning If the build should fail when there are warnings converting the SVG. If the SVG fails to
     *                      convert entirely, then an empty XML file will be generating, causing AAPT to complain.
     */
    public void failOnWarning(final boolean failOnWarning) {
        this.failOnWarning = failOnWarning;
    }
}
