package nz.co.aetheric.maven.plugin.compiler.closure;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Goal closure compile
 */
@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE)
public class ClosureCompilerMojo extends AbstractMojo {

	/** Compilation level. */
	@Parameter(defaultValue = "ADVANCED_OPTIMIZATIONS")
	private String compilation_level;

	/** Recursive file searching. */
	@Parameter(defaultValue = "true")
	private boolean recursive;

	/** Compiled file suffix. */
	@Parameter(defaultValue = ".min.js")
	private String suffix;

	/** Js directory. */
	@Parameter(defaultValue = "${project.basedir}/src/main/javascript")
	private File srcDir;

	/** Js output directory. */
	@Parameter(defaultValue = "${project.build.outputDirectory}/META-INF/resources/script")
	private File outDir;

	/** Version. */
	@Parameter(defaultValue = "${project.version}")
	private String version;

	/** Whether to append the version or not. */
	@Parameter(defaultValue = "false")
	private boolean appendVersion;

	/**
	 * Execute.
	 *
	 * @throws org.apache.maven.plugin.MojoExecutionException The exception.
	 */
	public void execute() throws MojoExecutionException {
		SecurityManager defaultSecurityManager = System.getSecurityManager();
		System.setSecurityManager(new NoExitSecurityManager()); // needed because System.exit is called by the runner.

		if (isValid(srcDir)) {
			getLog().info("Compiling javascript to minified javascript:\n\t" + srcDir + "\n\t" + outDir);
			processDirectory(srcDir, outDir);
		} else {
			getLog().error("The given directories are not valid or are missing. Please check the configuration.");
		}

		System.setSecurityManager(defaultSecurityManager); // set back to original security manager.
	}

	private void processDirectory(File source, File target) {
		if (!isValid(source)) {
			getLog().warn("Tried processing " + source + " as a file.");
			return;
		}

		//noinspection ConstantConditions
		for (File file : source.listFiles()) {
			String relativeFile = file.toString().replace(srcDir.toString(), "");

			if (recursive && file.isDirectory()) {
				getLog().debug("Recursing into " + relativeFile);
				processDirectory(file, new File(target, file.getName()));

			} else if (file.isFile() && file.getName().toLowerCase().endsWith(".js")) {
				getLog().debug("Compiling " + relativeFile);
				compile(file, target);
			}
		}
	}

	/**
	 * Run the compile.
	 *
	 * @param js The js to compile.
	 */
	private void compile(final File js, final File targetDir) {
		final File target = targetPath(targetDir, js.getName());
		final String source = sourcePath(js);

		if (target.exists() && js.lastModified() <= target.lastModified()) {
			getLog().debug("Skipped file because not modified: " + source);
			return;
		}

		if (!targetDir.exists() && !targetDir.mkdirs()) {
			getLog().warn("Unable to create " + targetDir + ". Bad things may happen next.");
		}

		final ClosureCompilerRunner runner = new ClosureCompilerRunner(compilation_level, source, target.toString());
		if (runner.shouldRunCompiler()) {
			try {
				runner.run();
			} catch (SecurityException e) {
				// expected throw when run finishes it calls System.exit.
			}
		}

		getLog().debug("Compiled file: " + source + " to file: " + target);
	}

	/**
	 * Gets the source path.
	 *
	 * @param source The source file.
	 * @return path The source path.
	 */
	private String sourcePath(final File source) {
		return source.getPath();
	}

	/**
	 * Gets the target path.
	 *
	 * @param targetDir The directory to compile the file to.
	 * @param fileName  The filename.
	 * @return target The target.
	 */
	private File targetPath(final File targetDir, final String fileName) {
		String finalName = appendVersion
				? fileName.replace(suffix, "-" + version + suffix)
				: fileName;
		return new File(targetDir.getPath() + File.separator + finalName);
	}

	/**
	 * Indicates if the given directory path is a valid file directory.
	 *
	 * @param file The file.
	 * @return <code>true</code> if the directory exists and is a directory, else <code>false</code>.
	 */
	private boolean isValid(final File file) {
		return file != null && file.exists() && file.isDirectory();
	}

}
