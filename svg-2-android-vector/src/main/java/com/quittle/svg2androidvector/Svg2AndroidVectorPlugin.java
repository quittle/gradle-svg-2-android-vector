package com.quittle.svg2androidvector;

import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.api.AndroidSourceDirectorySet;
import com.android.build.gradle.api.AndroidSourceSet;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileTree;

import java.io.File;
import java.util.Arrays;

public class Svg2AndroidVectorPlugin implements Plugin<Project> {
    @Override
    public void apply(final Project project) {
        final Svg2AndroidVectorExtension extension = new Svg2AndroidVectorExtension();
        project.getExtensions().add("svg2androidVector", extension);
        project.afterEvaluate(p -> {
            final BaseExtension androidExtension = project.getExtensions().getByType(BaseExtension.class);
            for (final AndroidSourceSet ass : androidExtension.getSourceSets()) {
                final String sourceSetName = ass.getName();
                final AndroidSourceDirectorySet asds = ass.getRes();
                asds.getSourceDirectoryTrees().stream()
                        .map(ConfigurableFileTree::getDir)
                        .filter(File::exists)
                        .map(File::listFiles)
                        .flatMap(Arrays::stream)
                        .filter(folder -> folder.getName().equals("raw"))
                        .map(File::listFiles)
                        .flatMap(Arrays::stream)
                        .filter(file -> file.getName().endsWith(".svg"))
                        .forEach(svgFile -> {
                            project.get
                            final File asdsDir = new File(project.getBuildDir(), "android-vector-resources/" + sourceSetName);
                            final Svg2AndroidVectorTask task = project.getTasks().create("Convert-" + svgFile.getName(), Svg2AndroidVectorTask.class);
                            task.svg = svgFile;
                            task.xml = new File(asdsDir, "drawable/" + svgFile.getName().replace(".svg", ".xml"));
                            task.failOnError = extension.getFailOnError();
                            asds.srcDir(asdsDir);
                            asds.getFilter().exclude(svgFile.getAbsolutePath());
                            project.getTasks().findByName("assemble").dependsOn(task);
                        });
            }
        });
    }

}
