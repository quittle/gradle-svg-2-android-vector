package com.quittle.svg2androidvector;

import java.io.File;

public class Svg2AndroidVectorExtension {
    private File[] svgs;
    private boolean failOnError = true;

    public File[] getSvgs() {
        return svgs == null ? null : svgs.clone();
    }

    public void setSvgs(File[] svgs) {
        this.svgs = svgs == null ? null : svgs.clone();
    }

    public boolean getFailOnError() {
        return failOnError;
    }

    public void setFailOnError(final boolean failOnError) {
        this.failOnError = failOnError;
    }
}
