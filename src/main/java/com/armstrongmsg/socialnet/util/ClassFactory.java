package com.armstrongmsg.socialnet.util;

import java.lang.reflect.Constructor;

import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;

public class ClassFactory {
	public Object createInstance(String className, Object... constructorParams) throws FatalErrorException {
		try {
			Class<?> classpath = Class.forName(className);
			Constructor<?> constructor;
			
			if (constructorParams.length > 0) {
				Class<?>[] constructorArgTypes = new Class[constructorParams.length];
				
                for (int i = 0; i < constructorParams.length; i++) {
                    constructorArgTypes[i] = constructorParams[i].getClass();
                }
                
				constructor = classpath.getConstructor(constructorArgTypes);
			} else {
				constructor = classpath.getConstructor();
			}
			
			return constructor.newInstance(constructorParams);
		} catch (ClassNotFoundException e) {
			throw new FatalErrorException(String.format(Messages.Exception.CLASS_NOT_FOUND_ON_INSTANTIATION, 
					className, e.getMessage()));
		} catch (NoSuchMethodException e) {
			throw new FatalErrorException(String.format(Messages.Exception.CONSTRUCTOR_NOT_FOUND_ON_INSTANTIATION, 
					className, e.getMessage()));
		} catch (Exception e) {
			// TODO add exception class to the message
			throw new FatalErrorException(String.format(Messages.Exception.ERROR_ON_INSTANTIATION, 
					className, e.getMessage()));
		}
	}
}
