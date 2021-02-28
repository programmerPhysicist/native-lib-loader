

package nativeExeLoader;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NativeExecutableUtilTest {

	@Test
	public void ifNoVersionWasFoundExecutableNameIsReturned() throws Exception {
		final String versionedExecutableName =
			NativeExecutableUtil.getVersionedExecutableName(NativeExecutableUtil.class,
				"native-exe-loader");
		assertEquals("native-exe-loader", versionedExecutableName);
	}
}
