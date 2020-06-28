package com.quittle.svg2androidvector;

import com.android.ide.common.vectordrawable.Svg2Vector;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;
import org.xml.sax.SAXException;

/**
 * Converts an SVG file to an Android vector drawable.
 */
@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public class Svg2AndroidVectorTask extends DefaultTask {
    /**
     * @return The SVG file to convert from
     */
    @InputFile
    public File getSvg() {
        return svg;
    }

    /**
     * @return The Android vector xml file to convert to.
     */
    @OutputFile
    public File getXml() {
        return xml;
    }

    /**
     * @return Whether or not to fail the task if only part of the SVG could be converted.
     */
    @Input
    public boolean getFailOnWarning() {
        return failOnWarning;
    }

    File svg;
    File xml;
    boolean failOnWarning = true;

    @TaskAction
    public void action() {
        try {
            mkdirs(xml.getParentFile());
            try (final OutputStream os = new FileOutputStream(xml)) {
                // parseSvgToXml does not throw on error, it simply returns a log of what it could not convert and
                // writes nothing out if it completely failed.
                final String errorLog;
                try {
                    errorLog = Svg2Vector.parseSvgToXml(svg, os); // NOPMD - DataflowAnomalyAnalysis:DU
                } catch (final Exception e) {
                    throw new TaskExecutionException(this, new Exception("Unable to parse SVG file", e));
                }

                // Handle complete error where the SVG could not be converted at all.
                os.flush();
                if (xml.length() == 0) {
                    throw new TaskExecutionException(this,
                            new RuntimeException(
                                    svg.getAbsolutePath() + " unable to be converted to Android vector: " + errorLog));
                }

                // Handle partial error where not everything could be converted but a drawable was able to be created.
                if (failOnWarning && errorLog.length() != 0) {
                    throw new TaskExecutionException(this, new RuntimeException(errorLog));
                }
            } catch (final IOException e) {
                throw new TaskExecutionException(this, new IOException("Unable to write out Android vector file", e));
            }
        } catch (final TaskExecutionException e) {
            delete(xml);
            throw e;
        }
    }

    private static void mkdirs(final File directory) {
        if (!directory.mkdirs() && !directory.isDirectory()) {
            throw new RuntimeException("Unable to make output directory: " + directory.getAbsolutePath());
        }
    }

    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    private static void delete(final File file) {
        file.delete();
    }
}
