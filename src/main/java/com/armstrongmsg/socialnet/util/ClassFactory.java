package com.armstrongmsg.socialnet.util;

import java.lang.reflect.Constructor;

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
			// TODO add message
			throw new FatalErrorException(e.getMessage());
		} catch (NoSuchMethodException e) {
			// TODO add message
			throw new FatalErrorException(e.getMessage());
		} catch (Exception e) {
			// TODO add message
			throw new FatalErrorException(e.getMessage());
		}
	}
}
