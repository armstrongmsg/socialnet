package socialnet.stub;

import java.util.List;
import java.util.Map;

import com.armstrongmsg.socialnet.constants.AuthenticationParameters;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.model.authentication.AuthenticationPlugin;
import com.armstrongmsg.socialnet.model.authentication.UserToken;

public class StubAuthenticationPlugin implements AuthenticationPlugin {
	private List<User> users;
	private Admin admin;
	
	@Override
	public void setUp(Admin admin, List<User> users) {
		this.users = users;
		this.admin = admin;
	}

	@Override
	public UserToken authenticate(Map<String, String> credentials) throws AuthenticationException {
		String username = credentials.get(AuthenticationParameters.USERNAME_KEY);
		String password = credentials.get(AuthenticationParameters.PASSWORD_KEY);
		return authenticate(username, password);
	}
	
	private UserToken authenticate(String username, String password) throws AuthenticationException {
		User user = findUserByUsername(username);
		
		if (user.getPassword().equals(password)) {
			return new UserToken(user.getUserId(), user.getUsername(), user.getProfile().getDescription());
		}
		
		throw new AuthenticationException();
	}
	
	private User findUserByUsername(String username) throws AuthenticationException {
		for (User user : this.users) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		
		if (admin.getUsername().equals(username)) {
			return admin;
		}

		throw new AuthenticationException();
	}
}
