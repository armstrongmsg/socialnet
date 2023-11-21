package socialnet;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.armstrongmsg.socialnet.constants.PropertiesNames;
import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.model.authentication.UserToken;
import com.armstrongmsg.socialnet.storage.StorageManager;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class IntegrationTest {
	private static final String ADMIN_USERNAME = "admin-username";
	private static final String ADMIN_PASSWORD = "admin-password";
	private ApplicationFacade facade;
	private StorageManager storageManager;
	private MockedStatic<PropertiesUtil> propertiesUtilMock;
	
	@Before
	public void setUp() throws FatalErrorException {
		ApplicationFacade.reset();
		
		storageManager = Mockito.mock(StorageManager.class);
		Mockito.when(storageManager.readUsers()).thenReturn(Arrays.asList());
		Mockito.when(storageManager.readGroups()).thenReturn(Arrays.asList());
		Mockito.when(storageManager.readFriendships()).thenReturn(Arrays.asList());
		Mockito.when(storageManager.readFollows()).thenReturn(Arrays.asList());
		
		PropertiesUtil propertiesUtil = Mockito.mock(PropertiesUtil.class);

		Mockito.when(propertiesUtil.getProperty(PropertiesNames.ADMIN_USERNAME)).
			thenReturn(ADMIN_USERNAME);
		Mockito.when(propertiesUtil.getProperty(PropertiesNames.ADMIN_PASSWORD)).
			thenReturn(ADMIN_PASSWORD);
		Mockito.when(propertiesUtil.getProperty("AUTHENTICATION_PLUGIN_CLASS_NAME")).
			thenReturn("com.armstrongmsg.socialnet.model.authentication.LocalPasswordBasedAuthenticationPlugin");
		Mockito.when(propertiesUtil.getProperty("AUTHORIZATION_PLUGIN_CLASS_NAME")).
			thenReturn("com.armstrongmsg.socialnet.model.authorization.AdminAuthorizationPlugin");
		
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
		
		facade = ApplicationFacade.getInstance(storageManager);
	}
	
	@Test
	public void testAdminLogin() throws AuthenticationException {
		Map<String, String> adminCredentials = new HashMap<String, String>();
		// FIXME constant
		adminCredentials.put("USER_ID", ADMIN_USERNAME);
		// FIXME constant
		adminCredentials.put("PASSWORD", ADMIN_PASSWORD);
		
		UserToken adminToken = facade.login(adminCredentials);
		
		assertEquals(ADMIN_USERNAME, adminToken.getUsername());
	}
	
	@Test(expected = AuthenticationException.class)
	public void testAdminLoginWithIncorrectCredentials() throws AuthenticationException {
		Map<String, String> adminIncorrectCredentials = new HashMap<String, String>();
		// FIXME constant
		adminIncorrectCredentials.put("USER_ID", ADMIN_USERNAME);
		// FIXME constant
		adminIncorrectCredentials.put("PASSWORD", "incorrect password");
		
		facade.login(adminIncorrectCredentials);
	}
	
	@After
	public void TearDown() {
		propertiesUtilMock.close();
	}
}
