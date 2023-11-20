package socialnet;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.storage.StorageManager;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class IntegrationTest {
	private ApplicationFacade facade;
	private StorageManager storageManager;
	
	@Before
	public void setUp() throws FatalErrorException {
		ApplicationFacade.reset();
		
		storageManager = Mockito.mock(StorageManager.class);
		Mockito.when(storageManager.readUsers()).thenReturn(Arrays.asList());
		Mockito.when(storageManager.readGroups()).thenReturn(Arrays.asList());
		Mockito.when(storageManager.readFriendships()).thenReturn(Arrays.asList());
		Mockito.when(storageManager.readFollows()).thenReturn(Arrays.asList());
		
		PropertiesUtil propertiesUtil = Mockito.mock(PropertiesUtil.class);

		Mockito.when(propertiesUtil.getProperty("AUTHENTICATION_PLUGIN_CLASS_NAME")).
			thenReturn("com.armstrongmsg.socialnet.model.authentication.LocalPasswordBasedAuthenticationPlugin");
		Mockito.when(propertiesUtil.getProperty("AUTHORIZATION_PLUGIN_CLASS_NAME")).
			thenReturn("com.armstrongmsg.socialnet.model.authorization.AdminAuthorizationPlugin");
		
		Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
		
		facade = ApplicationFacade.getInstance(storageManager);
	}
	
	@Test
	public void test() {
		Assert.assertTrue(facade.getUsers().isEmpty());
	}
}
