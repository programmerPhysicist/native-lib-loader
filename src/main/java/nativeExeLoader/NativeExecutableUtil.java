//This code is a derivation of native library loader and has been refactored
//for loading executables. Please see copyright notice for Native Library
/**
* Copyright (C) 2021 ProgrammerPhysicist
*/

package nativeExeLoader;

import org.scijava.nativelib.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a utility for loading native executables.
 * <p>
 * Native executables should be packaged into a single jar file, with the
 * following directory and file structure:
 *
 * <pre>
 * natives
 *   linux_32
 *     xxx[-vvv]
 *   linux_64
 *     xxx[-vvv]
 *   linux_arm
 *     xxx[-vvv]
 *   linux_arm64
 *     xxx[-vvv]
 *   osx_32
 *     xxx[-vvv]
 *   osx_64
 *     xxx[-vvv]
 *   windows_32
 *     xxx[-vvv].exe
 *   windows_64
 *     xxx[-vvv].exe
 *   aix_32
 *     xxx[-vvv]
 *   aix_64
 *     xxx[-vvv]
 * </pre>
 * <p>
 * Here "xxx" is the name of the native executable and "-vvv" is an optional
 * version number.
 * <p>
 * Current approach is to unpack the native executable into a temporary file and
 * load from there.
 *
 * @author programmerPhysicist
 */
public class NativeExecutableUtil {

	public static enum Architecture {
		UNKNOWN, LINUX_32, LINUX_64, LINUX_ARM, LINUX_ARM64, WINDOWS_32, WINDOWS_64, OSX_32,
			OSX_64, OSX_PPC, OSX_ARM64, AIX_32, AIX_64
	}

	private static enum Processor {
		UNKNOWN, INTEL_32, INTEL_64, PPC, PPC_64, ARM, AARCH_64
	}

	public static final String DELIM = "/";
	public static final String DEFAULT_SEARCH_PATH = "natives" + DELIM;

	private static Architecture architecture = Architecture.UNKNOWN;
	private static String archStr = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(
		"nativeExeLoader.NativeExecutableUtil");

	/**
	 * Determines the underlying hardware platform and architecture.
	 *
	 * @return enumerated architecture value
	 */
	public static Architecture getArchitecture() {
		if (Architecture.UNKNOWN == architecture) {
			final Processor processor = getProcessor();
			if (Processor.UNKNOWN != processor) {
				final String name = System.getProperty("os.name").toLowerCase();
				if (name.contains("nix") || name.contains("nux")) {
					if (Processor.INTEL_32 == processor) {
						architecture = Architecture.LINUX_32;
					}
					else if (Processor.INTEL_64 == processor) {
						architecture = Architecture.LINUX_64;
					}
					else if (Processor.ARM == processor) {
						architecture = Architecture.LINUX_ARM;
					}
					else if (Processor.AARCH_64 == processor) {
						architecture = Architecture.LINUX_ARM64;
					}
				}
				else if (name.contains("aix")) {
					if (Processor.PPC == processor) {
						architecture = Architecture.AIX_32;
					}
					else if (Processor.PPC_64 == processor) {
						architecture = Architecture.AIX_64;
					}
				}
				else if (name.contains("win")) {
					if (Processor.INTEL_32 == processor) {
						architecture = Architecture.WINDOWS_32;
					}
					else if (Processor.INTEL_64 == processor) {
						architecture = Architecture.WINDOWS_64;
					}
				}
				else if (name.contains("mac")) {
					if (Processor.INTEL_32 == processor) {
						architecture = Architecture.OSX_32;
					}
					else if (Processor.INTEL_64 == processor) {
						architecture = Architecture.OSX_64;
					}
					else if (Processor.AARCH_64 == processor) {
						architecture = Architecture.OSX_ARM64;
					}
					else if (Processor.PPC == processor) {
						architecture = Architecture.OSX_PPC;
					}
				}
			}
		}
		LOGGER.debug("architecture is " + architecture + " os.name is " +
			System.getProperty("os.name").toLowerCase());
		return architecture;
	}

	/**
	 * Determines what processor is in use.
	 *
	 * @return The processor in use.
	 */
	private static Processor getProcessor() {
		Processor processor = Processor.UNKNOWN;
		int bits;

		// Note that this is actually the architecture of the installed JVM.
		final String arch = System.getProperty("os.arch").toLowerCase();

		if (arch.contains("arm")) {
			processor = Processor.ARM;
		}
		else if (arch.contains("aarch64")) {
			processor = Processor.AARCH_64;
		}
		else if (arch.contains("ppc")) {
			bits = 32;
			if (arch.contains("64")) {
				bits = 64;
			}
			processor = (32 == bits) ? Processor.PPC : Processor.PPC_64;
		}
		else if (arch.contains("86") || arch.contains("amd")) {
			bits = 32;
			if (arch.contains("64")) {
				bits = 64;
			}
			processor = (32 == bits) ? Processor.INTEL_32 : Processor.INTEL_64;
		}
		LOGGER.debug("processor is " + processor + " os.arch is " +
			System.getProperty("os.arch").toLowerCase());
		return processor;
	}

	/**
	 * Returns the path to the native executable.
	 * 
	 * @param searchPath the path to search for &lt;platform&gt; directory.
	 * 			Pass in <code>null</code> to get default path
	 * 			(natives/&lt;platform&gt;).
	 *
	 * @return path
	 */
	public static String getPlatformExecutablePath(String searchPath) {
		if (archStr == null)
			archStr = getArchitecture().name().toLowerCase();

		// foolproof
		String fullSearchPath = (searchPath.equals("") || searchPath.endsWith(DELIM) ?
				searchPath : searchPath + DELIM) + archStr + DELIM;
		LOGGER.debug("platform specific path is " + fullSearchPath);
		return fullSearchPath;
	}

	/**
	 * Returns the full file name (without path) of the native executable.
	 *
	 * @param exeName name of executable
	 * @return file name
	 */
	public static String getPlatformExecutableName(final String exeName) {
		String name = null;
		switch (getArchitecture()) {
			case AIX_32:
			case AIX_64:
			case LINUX_32:
			case LINUX_64:
			case LINUX_ARM:
			case LINUX_ARM64:
			case OSX_32:
			case OSX_64:
			case OSX_ARM64:
				name = exeName;
				break;
                        case WINDOWS_32:
                        case WINDOWS_64:
                                name = exeName + ".exe";
                                break;
			default:
				break;
		}
		LOGGER.debug("native executable name " + name);
		return name;
	}

	/**
	 * Returns the Maven-versioned file name of the native executable. In order for
	 * this to work Maven needs to save its version number in the jar manifest.
	 * The version of the executable-containing jar and the version encoded in the
	 * native executable names should agree.
	 *
	 * <pre>
	 * {@code
	 * <build>
	 *   <plugins>
	 *     <plugin>
	 *       <artifactId>maven-jar-plugin</artifactId>
	 *         <inherited>true</inherited> *
	 *         <configuration>
	 *            <archive>
	 *              <manifest>
	 *                <packageName>com.example.package</packageName>
	 *                <addDefaultImplementationEntries>true</addDefaultImplementationEntries> *
	 *              </manifest>
	 *           </archive>
	 *         </configuration>
	 *     </plugin>
	 *   </plugins>
	 * </build>
	 *
	 * * = necessary to save version information in manifest
	 * }
	 * </pre>
	 *
	 * @param nativeExeJarClass any class within the executable-containing jar
	 * @param exeName name of executable
	 * @return The Maven-versioned file name of the native executable.
	 */
	public static String getVersionedExecutableName(final Class<?> nativeExeJarClass,
		String exeName)
	{
		final String version =
			nativeExeJarClass.getPackage().getImplementationVersion();
		if (null != version && version.length() > 0) {
			exeName += "-" + version;
		}
		return exeName;
	}

	/**
	 * Loads the native executable. Picks up the version number to specify from the
	 * executable-containing jar.
	 *
	 * @param nativeExeJarClass any class within the executable-containing jar
	 * @param exeName name of executable
	 * @return whether or not successful
	 */
	public static boolean loadVersionedNativeExecutable(
		final Class<?> nativeExeJarClass, String exeName)
	{
		// append version information to native executable name
		exeName = getVersionedExecutableName(nativeExeJarClass, exeName);

		return loadNativeExecutable(nativeExeJarClass, exeName);
	}

	/**
	 * Loads the native executable.
	 *
	 * @param jniExtractor the extractor to use
	 * @param exeName name of executable
	 * @param searchPaths a list of additional paths to search for the executable
	 * @return whether or not successful
	 */
	public static boolean loadNativeExecutable(final JniExtractor jniExtractor,
		final String exeName, final String... searchPaths)
	{
		if (Architecture.UNKNOWN == getArchitecture()) {
			LOGGER.warn("No native executable available for this platform.");
		}
		else {
			try {
				final List<String> exePaths = searchPaths == null ?
						new LinkedList<String>() :
						new LinkedList<String>(Arrays.asList(searchPaths));
				exePaths.add(0, NativeExecutableUtil.DEFAULT_SEARCH_PATH);
				// for backward compatibility
				exePaths.add(1, "");
				exePaths.add(2, "META-INF" + NativeExecutableUtil.DELIM + "lib");
				// NB: Although the documented behavior of this method is to load
				// native executable from META-INF/lib/, what it actually does is
				// to load from the root dir. See: https://github.com/scijava/
				// native-lib-loader/blob/6c303443cf81bf913b1732d42c74544f61aef5d1/
				// src/main/java/org/scijava/nativelib/NativeLoader.java#L126

				// search in each path in {natives/, /, META-INF/lib/, ...}
				for (String exePath : exePaths) {
					File extracted = jniExtractor.extractJni(NativeExecutableUtil.getPlatformExecutablePath(exePath),
							exeName);
					if (extracted != null) {
						System.load(extracted.getAbsolutePath());
						return true;
					}
				}
			}
			catch (final UnsatisfiedLinkError e) {
				LOGGER.debug("Problem with executable", e);
			} catch (IOException e) {
				LOGGER.debug("Problem with extracting the executable", e);
			}
		}
		return false;
	}

	/**
	 * Loads the native executable.
	 *
	 * @param nativeExeJarClass any class within the executable-containing jar
	 * @param exeName name of executable
	 * @return whether or not successful
	 */
	public static boolean loadNativeExecutable(final Class<?> nativeExeJarClass,
		final String exeName)
	{
		try {
			return NativeExecutableUtil.loadNativeExecutable(new DefaultJniExtractor(nativeExeJarClass), exeName);
		}
		catch (final IOException e) {
			LOGGER.debug("IOException creating DefaultJniExtractor", e);
		}
		return false;
	}
}
