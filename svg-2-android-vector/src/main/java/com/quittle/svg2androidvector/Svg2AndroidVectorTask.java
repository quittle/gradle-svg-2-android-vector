package com.quittle.svg2androidvector;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.android.ide.common.vectordrawable.Svg2Vector;
import org.gradle.api.tasks.TaskExecutionException;

/**
 * Converts an SVG file to an Android vector drawable.
 */
public class Svg2AndroidVectorTask extends DefaultTask {
    /**
     * The SVG file to convert from
     */
    @InputFile
    public File svg;

    /**
     * The Android vector xml file to convert to.
     */
    @OutputFile
    public File xml;

    /**
     * Whether or not to fail the task if only part of the SVG could be converted.
     */
    @Input
    public boolean failOnWarning = true;

    public Svg2AndroidVectorTask() {
        doLast(task -> {
            xml.getParentFile().mkdirs();
            try (final OutputStream os = new FileOutputStream(xml)) {
                // parseSvgToXml does not throw on error, it simply returns a log of what it could not convert and
                // writes nothing out if it completely failed.
                final String errorLog = Svg2Vector.parseSvgToXml(svg, os);

                // Handle complete error where the SVG could not be converted at all.
                os.flush();
                if (xml.length() == 0) {
                    throw new TaskExecutionException(this,
                            new RuntimeException(svg.getAbsolutePath() + " unable to be converted to Android vector"));
                }

                // Handle partial error where not everything could be converted but a drawable was able to be created.
                if (failOnWarning && errorLog.length() != 0) {
                    throw new TaskExecutionException(this, new RuntimeException(errorLog));
                }
            } catch (final IOException e) {
                throw new TaskExecutionException(this, new IOException("Unable to write out Android vector file", e));
            }
        });
    }
}
