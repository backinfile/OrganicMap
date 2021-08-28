package com.backinfile.support;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.backinfile.map.Log;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class ReflectionUtils {

	// 这个函数需要需改class文件，所以执行这个函数前尽量少加载类
	public static void initTimingMethod(String packageName) {
		ClassPool pool = ClassPool.getDefault();
		CtClass timeLoggerCtClass;
		try {
			timeLoggerCtClass = pool.get(TimeLogger.class.getName());
		} catch (NotFoundException e1) {
			Log.reflection.error("error in reflection oper", e1);
			return;
		}
		for (String targetClassName : getClasseNames(packageName)) {
			try {
				boolean needRewrite = false;
				CtClass ctClass = pool.get(targetClassName);
				for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
					Timing timing = (Timing) ctMethod.getAnnotation(Timing.class);
					if (timing == null) {
						continue;
					}
					String loggerName = Utils.isNullOrEmpty(timing.value()) ? ctMethod.getName() : timing.value();
					ctMethod.addLocalVariable("$timeLogger", timeLoggerCtClass);
					ctMethod.insertBefore(
							"$timeLogger = new " + TimeLogger.class.getName() + "(\"" + loggerName + "\");");
					ctMethod.insertAfter("$timeLogger.log();");
					needRewrite = true;
				}
				if (!needRewrite) {
					continue;
				}
				ctClass.toClass();
				ctClass.writeFile();
				Log.reflection.info("rewrite class {}", targetClassName);
			} catch (Exception e) {
				Log.reflection.error("error in rewrite class {}", targetClassName);
			}
		}
	}

	public static boolean isAbstract(Class<?> clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}

//	public static String PackageName = "com.backinfile";

	public static Set<Class<?>> getClassesExtendsClass(String packageName, Class<?> clazz) {
		Set<Class<?>> set = new HashSet<>();
		for (Class<?> type : getClasses(packageName)) {
			if (isAbstract(type))
				continue;

			if (clazz.isAssignableFrom(type)) {
				set.add(type);
			}
		}
		return set;
	}

	public static Set<Class<?>> getClassesWithAnnotation(String packageName, Class<? extends Annotation> annotation) {
		Set<Class<?>> set = new HashSet<>();
		for (Class<?> type : getClasses(packageName)) {
			Annotation ann = type.getAnnotation(annotation);
			if (ann != null) {
				set.add(type);
			}
		}
		return set;
	}

	public static Set<Class<?>> getClassesExtendsClassAndWithAnnotation(String packageName, Class<?> clazz,
			Class<? extends Annotation> annotation) {
		Set<Class<?>> set = new HashSet<>();
		for (Class<?> type : getClasses(packageName)) {
			if (clazz.isAssignableFrom(type)) {
				Annotation ann = type.getAnnotation(annotation);
				if (ann != null) {
					set.add(type);
				}
			}
		}
		return set;
	}

	public static Set<Class<?>> getClasses(String pack) {
		Set<Class<?>> classes = new HashSet<>();
		for (String name : getClasseNames(pack)) {
			try {
				classes.add(Class.forName(name));
			} catch (ClassNotFoundException e) {
				Log.reflection.error("", e);
			}
		}
		return classes;
	}

	public static Set<String> getClasseNames(String pack) {

		// 第一个class类的集合
		Set<String> classes = new LinkedHashSet<>();
		// 是否循环迭代
		boolean recursive = true;
		// 获取包的名字 并进行替换
		String packageDirName = pack.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), Utils.UTF8);
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					findClassesInPackageByFile(pack, filePath, recursive, classes);
				} else if ("jar".equals(protocol)) {
					// 如果是jar包文件
					// 定义一个JarFile
					System.out.println("jar类型的扫描");
					JarFile jar;
					try {
						// 获取jar
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						// 从此jar包 得到一个枚举类
						Enumeration<JarEntry> entries = jar.entries();
						findClassesInPackageByJar(pack, entries, packageDirName, true, classes);
					} catch (IOException e) {
						// log.error("在扫描用户定义视图时从jar包获取文件出错");
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classes;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 */
	private static void findClassesInPackageByFile(String packageName, String packagePath, final boolean recursive,
			Set<String> classes) {
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			// log.warn("用户定义包名 " + PackageName + " 下没有任何文件");
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
		File[] dirfiles = dir
				.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
						classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				classes.add(packageName + '.' + className);
				// Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' +
				// className)
			}
		}
	}

	/**
	 * 以jar的形式来获取包下的所有Class
	 */
	private static void findClassesInPackageByJar(String packageName, Enumeration<JarEntry> entries,
			String packageDirName, final boolean recursive, Set<String> classes) {
		// 同样的进行循环迭代
		while (entries.hasMoreElements()) {
			// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			// 如果是以/开头的
			if (name.charAt(0) == '/') {
				// 获取后面的字符串
				name = name.substring(1);
			}
			// 如果前半部分和定义的包名相同
			if (name.startsWith(packageDirName)) {
				int idx = name.lastIndexOf('/');
				// 如果以"/"结尾 是一个包
				if (idx != -1) {
					// 获取包名 把"/"替换成"."
					packageName = name.substring(0, idx).replace('/', '.');
				}
				// 如果可以迭代下去 并且是一个包
				if ((idx != -1) || recursive) {
					// 如果是一个.class文件 而且不是目录
					if (name.endsWith(".class") && !entry.isDirectory()) {
						// 去掉后面的".class" 获取真正的类名
						String className = name.substring(packageName.length() + 1, name.length() - 6);
						// 添加到classes
						classes.add(packageName + '.' + className);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> wrap(Class<T> c) {
		return c.isPrimitive() ? (Class<T>) PRIMITIVES_TO_WRAPPERS.get(c) : c;
	}

	private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new HashMap<Class<?>, Class<?>>();
	static {
		PRIMITIVES_TO_WRAPPERS.put(boolean.class, Boolean.class);
		PRIMITIVES_TO_WRAPPERS.put(byte.class, Byte.class);
		PRIMITIVES_TO_WRAPPERS.put(char.class, Character.class);
		PRIMITIVES_TO_WRAPPERS.put(double.class, Double.class);
		PRIMITIVES_TO_WRAPPERS.put(float.class, Float.class);
		PRIMITIVES_TO_WRAPPERS.put(int.class, Integer.class);
		PRIMITIVES_TO_WRAPPERS.put(long.class, Long.class);
		PRIMITIVES_TO_WRAPPERS.put(short.class, Short.class);
		PRIMITIVES_TO_WRAPPERS.put(void.class, Void.class);
	}
}