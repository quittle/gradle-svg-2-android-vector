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

public class Svg2AndroidVectorTask extends DefaultTask {
    @InputFile
    File svg;

    @OutputFile
    File xml;

    @Input
    boolean failOnError = true;

    public Svg2AndroidVectorTask() {
        doLast(task -> {
            xml.getParentFile().mkdirs();
            try (final OutputStream os = new FileOutputStream(xml)) {
                final String errorLog = Svg2Vector.parseSvgToXml(svg, os);
                if (failOnError && errorLog.length() != 0) {
                    throw new TaskExecutionException(this, new RuntimeException(errorLog));
                }
            } catch (final IOException e) {
                throw new TaskExecutionException(this, new IOException("Unable to write out android vector file", e));
            }
        });
    }
}
