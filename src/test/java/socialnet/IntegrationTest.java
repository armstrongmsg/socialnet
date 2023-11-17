package socialnet;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.storage.StorageManager;

public class IntegrationTest {
	private ApplicationFacade facade;
	private StorageManager storageManager;
	
	@Before
	public void setUp() {
		ApplicationFacade.reset();
		
		storageManager = Mockito.mock(StorageManager.class);
		Mockito.when(storageManager.readUsers()).thenReturn(Arrays.asList());
		Mockito.when(storageManager.readGroups()).thenReturn(Arrays.asList());
		Mockito.when(storageManager.readFriendships()).thenReturn(Arrays.asList());
		Mockito.when(storageManager.readFollows()).thenReturn(Arrays.asList());
		
		facade = ApplicationFacade.getInstance(storageManager);
	}
	
	@Test
	public void test() {
		Assert.assertTrue(facade.getUsers().isEmpty());
	}
}
