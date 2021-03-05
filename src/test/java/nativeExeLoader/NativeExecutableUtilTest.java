//This code is a derivation of native library loader and has been refactored
//for loading executables. Please see copyright notice for Native Library
/**
* Copyright (C) 2021 ProgrammerPhysicist
*/
package nativeExeLoader;

import nativeExeLoader.PlatformUtil.Architecture;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import static org.mockito.Mockito.*;



import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

// @RunWith attaches a runner with the test class to initialize the test data
//@RunWith(MockitoJUnitRunner.class)
public class NativeExecutableUtilTest {
        
	@Test
	public void ifNoVersionWasFoundExecutableNameIsReturned() throws Exception {
		final String versionedExecutableName =
			NativeExecutableUtil.getVersionedExecutableName(NativeExecutableUtil.class,
				"native-exe-loader");
		assertEquals("native-exe-loader", versionedExecutableName);
	}
        
        @Test
        public void getPlatformExecutablePathTest() {
            PlatformUtil theSystem = mock(PlatformUtil.class);
            NativeExecutableUtil util = new NativeExecutableUtil();
            util.setPlatformUtil(theSystem);
            
            //run case with executable specified.
            when(theSystem.getArchitecture()).thenReturn(Architecture.LINUX_32);
            assertEquals("foo/linux_32/", util.getPlatformExecutablePath("foo"));
        
            //run case with empty string given for executable
            assertEquals(util.getPlatformExecutablePath(""), "linux_32/");
        }
        
        @Test
        public void getPlatformExecutableNameTest() {
            PlatformUtil theSystem = mock(PlatformUtil.class);
            NativeExecutableUtil util = new NativeExecutableUtil();
            util.setPlatformUtil(theSystem);
            
            doReturn(Architecture.AIX_32).when(theSystem).getArchitecture();
            assertEquals("foo", util.getPlatformExecutableName("foo"));
            
            doReturn(Architecture.AIX_64).when(theSystem).getArchitecture();
            assertEquals("foo", util.getPlatformExecutableName("foo"));
            
            doReturn(Architecture.LINUX_32).when(theSystem).getArchitecture();
            assertEquals("foo", util.getPlatformExecutableName("foo"));
            
            doReturn(Architecture.LINUX_64).when(theSystem).getArchitecture();
            assertEquals("foo", util.getPlatformExecutableName("foo"));
            
            doReturn(Architecture.LINUX_ARM).when(theSystem).getArchitecture();
            assertEquals("foo", util.getPlatformExecutableName("foo"));
            
            doReturn(Architecture.LINUX_ARM64).when(theSystem).getArchitecture();
            assertEquals("foo", util.getPlatformExecutableName("foo"));
            
            doReturn(Architecture.OSX_32).when(theSystem).getArchitecture();
            assertEquals("foo", util.getPlatformExecutableName("foo"));
            
            doReturn(Architecture.OSX_64).when(theSystem).getArchitecture();
            assertEquals("foo", util.getPlatformExecutableName("foo"));
            
            doReturn(Architecture.OSX_ARM64).when(theSystem).getArchitecture();
            assertEquals("foo", util.getPlatformExecutableName("foo"));
            
            doReturn(Architecture.WINDOWS_32).when(theSystem).getArchitecture();
            assertEquals("foo.exe", util.getPlatformExecutableName("foo"));
            
            doReturn(Architecture.WINDOWS_64).when(theSystem).getArchitecture();
            assertEquals("foo.exe", util.getPlatformExecutableName("foo"));
            
            doReturn(Architecture.UNKNOWN).when(theSystem).getArchitecture();
            assertEquals(null, util.getPlatformExecutableName("foo"));
            
            doReturn(Architecture.OSX_PPC).when(theSystem).getArchitecture();
            assertEquals(null, util.getPlatformExecutableName("foo"));
        }
}
