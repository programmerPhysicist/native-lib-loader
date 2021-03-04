/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nativeExeLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 15098
 */
public class PlatformUtil {
    
        public static enum Architecture {
		UNKNOWN, LINUX_32, LINUX_64, LINUX_ARM, LINUX_ARM64, WINDOWS_32, WINDOWS_64, OSX_32,
			OSX_64, OSX_PPC, OSX_ARM64, AIX_32, AIX_64
	}

	private static enum Processor {
		UNKNOWN, INTEL_32, INTEL_64, PPC, PPC_64, ARM, AARCH_64
	}
        
        private static final Logger LOGGER = LoggerFactory.getLogger(
		"nativeExeLoader.PlatformUtil");
        
        private static Architecture architecture = Architecture.UNKNOWN;
    
    	/**
	 * Determines the underlying hardware platform and architecture.
	 *
	 * @return enumerated architecture value
	 */
	public Architecture getArchitecture() {
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
	private Processor getProcessor() {
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
}
