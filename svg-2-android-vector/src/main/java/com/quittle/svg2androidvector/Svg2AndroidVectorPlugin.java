package com.quittle.svg2androidvector;

import com.android.build.api.variant.AndroidComponentsExtension;
import com.android.build.api.variant.SourceDirectories;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

/**
 * Entry point for the project. This plugin will replace {@code res/raw&#42;/*.svg}
 * files with {@code res/drawable&#42;/*.xml}
 * files in the Android Vector format. This allows you check-in the SVG sources
 * for Android vector drawables.
 */
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class Svg2AndroidVectorPlugin implements Plugin<Project> {
    /**
     * The string used for the extension visible in consumers' {@code build.gradle}.
     */
    public static final String EXTENSION_NAME = "svg2androidVector";

    /**
     * The name of the root parent task for all the conversion tasks to be
     * dependencies of.
     */
    public static final String CONVERSION_PARENT_TASK_NAME = "ConvertSvgToXml";

    private static final String ANDROID_RESOURCES_DIR_NAME_RAW = "raw";
    private static final String ANDROID_RESOURCES_DIR_NAME_DRAWABLE = "drawable";
    private static final String SVG_FILE_EXTENSION = ".svg";
    private static final String XML_FILE_EXTENSION = ".xml";
    private static final String BUILD_DIR_RESOURCE_NAME = "android-vector-resources";
    private static final String EARLY_ANDROID_TASK_NAME = "preBuild";
    // TODO: See if the drawable folder may be used instead of raw.
    private static final String SVG_FILTER_PATTERN = String.format("%s*/*%s", ANDROID_RESOURCES_DIR_NAME_RAW,
            SVG_FILE_EXTENSION);
    private static final String CONVERSION_TASK_NAME_FORMAT = "ConvertSvgToXml-%s-%s";

    /**
     * Default constructor.
     */
    public Svg2AndroidVectorPlugin() {
    }

    @Override
    public void apply(final Project project) {
        final Svg2AndroidVectorExtension extension = new Svg2AndroidVectorExtension();
        project.getExtensions().add(EXTENSION_NAME, extension);

        project.getPlugins().withId("com.android.application", plugin ->
            registerSvgFromVariantResources(project, extension)
        );
        project.getPlugins().withId("com.android.library", plugin ->
            registerSvgFromVariantResources(project, extension)
        );
    }

    private static String buildTaskName(final String sourceSetName, final File svgFile) {
        return String.format(CONVERSION_TASK_NAME_FORMAT, sourceSetName, svgFile.getName());
    }

    /**
     * Parses the file's parent directory and returns the qualifier suffix for raw directory.
     * For example: for ".../res/raw-night/logo.svg" will return "-night"
     *              for ".../res/raw/logo.svg" will return ""
     */
    private static String getQualifierSuffix(File svgFile) {
        String name = svgFile.getParentFile().getName();

        // Check if a folder contains a hyphen (a sign of qualifiers such as language/region)
        if (name.startsWith(ANDROID_RESOURCES_DIR_NAME_RAW + "-")) {
            // Strip off the "raw" prefix and return the remaining part (e.g. "-en")
            return name.substring(name.indexOf("-"));
        }
        // If there is no hyphen (just the "raw" folder), there is no qualifier.
        return "";
    }

    private static void registerSvgFromVariantResources(final Project project, final Svg2AndroidVectorExtension extension) {
        AndroidComponentsExtension<?, ?, ?> androidComponents =
                project.getExtensions().getByType(AndroidComponentsExtension.class);
        final TaskContainer taskContainer = project.getTasks();
        final TaskProvider<Task> parentTaskProvider = taskContainer.register(CONVERSION_PARENT_TASK_NAME);
        // An early Android task all the conversion tasks should be a dependency of
        final TaskProvider<Task> preBuildTaskProvider = taskContainer.named(EARLY_ANDROID_TASK_NAME);

        preBuildTaskProvider.configure( preBuildTask ->
            preBuildTask.dependsOn(parentTaskProvider)
        );

        androidComponents.onVariants(androidComponents.selector().all(), variant -> {
            final SourceDirectories.Layered variantRes = variant.getSources().getRes();

            if (variantRes == null)
                return;

            final Provider<List<Collection<Directory>>> resProvider = variantRes.getAll();
            final List<Collection<Directory>> resourceCollections = resProvider.get();
            final String variantName = variant.getName();

            for (Collection<Directory> setOfDirs : resourceCollections) {
                for (Directory directory : setOfDirs) {
                    File resDir = directory.getAsFile();

                    if (resDir.exists()) {
                        ConfigurableFileTree svgTree = project.fileTree(resDir);
                        svgTree.include(SVG_FILTER_PATTERN);

                        svgTree.forEach(svgFile -> {
                            final String suffix = getQualifierSuffix(svgFile);
                            final String taskName = buildTaskName(variantName + suffix, svgFile);

                            if (taskContainer.findByName(taskName) == null) {
                                // create relative path to new generated resource directory
                                final String relativeOutPath = Paths.get(
                                        BUILD_DIR_RESOURCE_NAME,
                                        variantName).toString();
                                // create relative path to drawable directory with localization suffix
                                final String drawableRelativeOutPath = Paths.get(
                                        relativeOutPath,
                                        ANDROID_RESOURCES_DIR_NAME_DRAWABLE + suffix).toString();

                                final TaskProvider<Svg2AndroidVectorTask> taskProvider = taskContainer.register(taskName,
                                        Svg2AndroidVectorTask.class, task -> {
                                            task.svg = svgFile;
                                            task.failOnWarning = extension.getFailOnWarning();
                                            // Absolute path must be created inside the task.
                                            // Ouside getBuildDirectory returns path to working directory
                                            // of the currently running process - that is the Gradle daemon.
                                            task.getOutputDirectory().set(project.getLayout().getBuildDirectory().dir(relativeOutPath));
                                            task.xml = new File(project.getLayout().getBuildDirectory().dir(drawableRelativeOutPath).get().toString(),
                                                svgFile.getName().replace(SVG_FILE_EXTENSION, XML_FILE_EXTENSION));
                                        }
                                );

                                parentTaskProvider.configure( parentTask ->
                                    parentTask.dependsOn(taskProvider)
                                );

                                variantRes.addGeneratedSourceDirectory(
                                        taskProvider,
                                        Svg2AndroidVectorTask::getOutputDirectory
                                );
                            }
                        });
                    }
                }
            }
        });
    }
}
