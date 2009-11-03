import java.lang.reflect.*;

abstract class Introspector {
	public String toString() {
		String result = "";
		Field[] fields = getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			result += field.getName() + ": ";
			try {
				result += classBracketsObject(field.getType(), field.get(this));
			}
			catch (IllegalAccessException e) {
				result += classBracketsObject(field.getType(), "HIDDEN");
			}
			if (i < fields.length - 1) result += "; ";
		}
		return result;
	}

	private static String classBracketsObject(Class type, Object object) {
		String result = classToString(type) + "(";
		result += type.isPrimitive() ? "" + object : objectToString(object);
		return result + ")";
	}

	private static String arrayToString(Object object) {
		Class type = unArray(object.getClass());
		String result = "";
		for (int i = 0; i < Array.getLength(object); i++) {
			result += classBracketsObject(type, Array.get(object, i));
			if (i < Array.getLength(object) - 1) result += ", ";
		}
		return result;
	}

	private static String objectToString(Object object) {
		if (object == null) return "null";
		return object.getClass().isArray() ? arrayToString(object) : "" + object;
	}

	private static String classToString(Class type) {
		String string = typeSignatureToString(type.getName());
		String[] split = string.split("\\.");
		if (split.length == 3 && split[0].equals("java") && split[1].equals("lang")) {
			string = split[2];
		}
		return string;
	}

	private static Class typeSignatureToClass(String string) {
		if (string.equals("B")) return byte.class;
		if (string.equals("C")) return char.class;
		if (string.equals("D")) return double.class;
		if (string.equals("F")) return float.class;
		if (string.equals("I")) return int.class;
		if (string.equals("J")) return long.class;
		if (string.equals("S")) return short.class;
		if (string.equals("Z")) return boolean.class;
		if (string.charAt(0) != '[' && string.charAt(string.length() - 1) == ';')
			string = string.substring(1, string.length() - 1);
		try {
			return Class.forName(string);
		}
		catch (ClassNotFoundException e) {
			System.err.println(string);
			return null;
		}
	}

	private static String typeSignatureToString(String string) {
		int arrayDepth = 0;
		while (string.charAt(0) == '[') {
			string = string.substring(1);
			arrayDepth++;
		}
		if (string.equals("B")) return "byte";
		if (string.equals("C")) return "char";
		if (string.equals("D")) return "double";
		if (string.equals("F")) return "float";
		if (string.equals("I")) return "int";
		if (string.equals("J")) return "long";
		if (string.equals("S")) return "short";
		if (string.equals("Z")) return "boolean";
		if (string.charAt(string.length() - 1) == ';')
			string = string.substring(1, string.length() - 1);
		for (; arrayDepth > 0; arrayDepth--) string += "[]";
		return string;
	}

	private static Class unArray(Class type) {
		return typeSignatureToClass(type.getName().substring(1));
	}
}
