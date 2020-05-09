package me.rey.clans.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class NMSUtil {

	private static HashMap<String, Method> methodCache = new HashMap<String, Method>();
	private static HashMap<String, Class<?>> classCache = new HashMap<String, Class<?>>();
	private static HashMap<String, Object> enumCache = new HashMap<String, Object>();
	private static long savedTime = 0;

	//my bad method that works
	public static boolean is1_8() {
		String[] s = Bukkit.getServer().getVersion().split(":");
		String q = s[1].replace(" ", "");
		q = q.replace(")", "");
		return q.contains("1.8");
	}

	/**
	 * Clears the caches in this class.
	 * Run this when you disable the plugin.
	 * srry it had to work this way xd - lib
	 */
	public static void clearCaches() {
		classCache.clear();
		methodCache.clear();
		enumCache.clear();
		savedTime = 0;
	}

	static {

		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			classCache.put("net.minecraft.server." + version + "." + "ChatSerializer",
					Class.forName("net.minecraft.server." + version + "." + "ChatSerializer"));
		} catch(ClassNotFoundException e) {

		}
	}



	/**
	 * This method gets a method from a certain class and invokes it.
	 * It will return the output whether it be a value or just null.
	 *
	 * @typeParam typeParameter - The expected output

	 * @return Returns the output of the method requested. May be null.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAndInvokeMethod(Class<?> clazz, String methodName, Class<?>[] parametersAsClass, Object instance, Object... parametersReal) {
		Method m = getMethod(clazz, methodName, parametersAsClass);
		return (T) invokeMethod(m, instance, parametersReal);
	}

	public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramaters) {
		if (methodCache.containsKey(methodName)) {
			return getFromCache(methodName, methodCache);

		}
		try {
			return clazz.getDeclaredMethod(methodName, paramaters);
		} catch (NoSuchMethodException ex) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + ex.getMessage());
			return null;
		}
	}



	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> getConstructor(Class<?> clazz, Class<?>... paramaterTypes) {
		try {
			return (Constructor<T>) clazz.getConstructor(paramaterTypes);
		} catch (NoSuchMethodException ex) {
			return null;
		} catch (ClassCastException e1) {
			throw new ClassCastException("Type Parameter was not correct");
		}
	}

	public static <T> T callConstructor(Constructor<T> constructor, Object... paramaters) {
		if (constructor == null) {
			throw new NullPointerException("Constructor was null.");
		}
		constructor.setAccessible(true);
		try {
			return (T) constructor.newInstance(paramaters);
		} catch (InvocationTargetException ex) {
			throw new RuntimeException(ex.getCause());
		} catch (ClassCastException e1) {
			throw new ClassCastException("Type Parameter was not correct");
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void getAndSetField(Class<?> clazz, String name, Object instance, Object value) {
		Field field = getField(clazz, name);
		setFieldValue(field, instance, value);
	}

	public static Field getField(Class<?> clazz, String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (NoSuchFieldException ex) {
			return null;
		}
	}

	public static <T> T getFieldAndValue(Class<?> clazz, String fieldName, Object instance) {
		Field field = getField(clazz, fieldName);
		return getFieldValue(field, instance);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Field field, Object instance) {
		if (field == null) {
			throw new NullPointerException("Field was null");
		}
		field.setAccessible(true);
		try {
			return (T) field.get(instance);
		} catch (ClassCastException | IllegalArgumentException | IllegalAccessException e1) {
			throw new ClassCastException("Type Parameter was not correct");
		}
	}

	public static void setFieldValue(Field field, Object instance, Object value) {
		if (field == null) {
			throw new NullPointerException("Field was null");
		}
		field.setAccessible(true);
		try {
			field.set(instance, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		field.setAccessible(!field.isAccessible());
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Method method, Object instance, Object... paramaters) {
		if (method == null) {
			throw new NullPointerException("Method was null");
		}
		try {
			T output = (T) method.invoke(instance, paramaters);
			addToCache(method.getName(), method, methodCache);
			return output;
		} catch (ClassCastException e1) {
			throw new ClassCastException("Type Parameter was not correct");
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}



	private static Object getConnection(Player player) {
		Method getHandle;
		Object con = null;
		try {
			getHandle = player.getClass().getMethod("getHandle");
			Object nmsPlayer = getHandle.invoke(player);
			Field conField = nmsPlayer.getClass().getField("playerConnection");
			con = conField.get(nmsPlayer);
		} catch (NoSuchMethodException | SecurityException | NoSuchFieldException |
				IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return con;
	}

	public static void sendParticles(Player player, Object particle, boolean distEx, float x, float y, float z,
			float xOffset, float yOffset, float zOffset, float speed, int amount, int[] moreData)  {
		try {
			Class<?> packetClass = getNmsClass("PacketPlayOutWorldParticles");
			Constructor<?> packetConstructor = packetClass.getConstructor(particle.getClass(), boolean.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class, int[].class);
			Object packet = packetConstructor.newInstance(particle, distEx, x, y, z, xOffset, yOffset, zOffset, speed, amount, moreData);
			Method sendPacket = getNmsClass("PlayerConnection").getMethod("sendPacket", getNmsClass("Packet"));
			sendPacket.invoke(getConnection(player), packet);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | NoSuchMethodException |
				InstantiationException e) {
			e.printStackTrace();
		}

	}


	public static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNmsClass("Packet")).invoke(playerConnection, packet);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getEnumConstant(Class<T> clazz, String constant) {
		if (enumCache.containsKey(clazz.getName() + "." + constant)) {
			return (T) getFromCache(clazz.getName() + "." + constant, enumCache);
		}
		if (clazz.getEnumConstants() == null) {
			return null;
		}
		for (Object constt : clazz.getEnumConstants()) {
			if (constt.toString().equalsIgnoreCase(constant)) {
				addToCache(clazz.getName() + "." + constant, constt, enumCache);
				return (T) constt;
			}
		}
		return null;
	}

	public static String getVersion() {
		String packageName = Bukkit.getServer().getClass().getPackage().getName();
		return packageName.substring(packageName.lastIndexOf('.') + 1);
	}

	public static Object getHandle(Object craftClass) {
		Method getHandle = getMethod(craftClass.getClass(), "getHandle");
		return invokeMethod(getHandle, craftClass);
	}

	/**
	 * Gets an object from a cache. Cache and key specified in params.
	 * Clears cache if savedTime is exceeded.
	 *
	 * @param key
	 * @param hashMap
	 * @return
	 */
	private static <T> T getFromCache(String key, HashMap<String, T> hashMap) {
		if (key == null) {
			throw new NullPointerException("Key was null");
		}
		T output = null;
		if (hashMap.containsKey(key)) {
			output = (T) hashMap.get(key);
		} else {
			return null;
		}

		if (savedTime == 0) {
			savedTime = System.currentTimeMillis();
		} else if ((System.currentTimeMillis()-savedTime)/100000 >= 5) {
			savedTime = System.currentTimeMillis();
			hashMap.clear();
		}
		return output;

	}

	private static <T> void addToCache(String key, T value, HashMap<String, T> hashMap) {
		if (key == null) {
			throw new NullPointerException("Key was null");
		}
		if (value == null) {
			throw new NullPointerException("Method was null");
		}
		hashMap.put(key, value);
		if (savedTime == 0) {
			savedTime = System.currentTimeMillis();
		} else if ((System.currentTimeMillis()-savedTime)/100000 >= 5) {
			savedTime = System.currentTimeMillis();
			hashMap.clear();
		}
	}

	/**
	 * Gets a class from the package "org.bukkit.craftbukkit."
	 * You must put extensions such as "entity."
	 * @param name
	 * @return
	 */
	public static Class<?> getCraftBukkitClass(String name) {
		String className = "org.bukkit.craftbukkit." + getVersion() + "." + name;
		if (classCache.containsKey(className)) {
			return getFromCache(className, classCache);
		}
		try {
			Class<?> clazz = Class.forName(className);
			addToCache(className, clazz, classCache);
			return clazz;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getUtilClass(String name) {
		String className = "net.minecraft.util." + name;
		if (classCache.containsKey(className)) {
			return getFromCache(className, classCache);
		}
		try {
			Class<?> clazz = Class.forName(className);
			addToCache(className, clazz, classCache);
			return clazz;
		} catch (ClassNotFoundException ex2) {
			return null;
		}
	}

	public static Class<?> getNmsClass(String name) {
		String className = "net.minecraft.server." + getVersion() + "." + name;
		if (classCache.containsKey(className)) {
			return getFromCache(className, classCache);
		}
		try {
			Class<?> clazz = Class.forName(className);
			addToCache(className, clazz, classCache);
			return clazz;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
