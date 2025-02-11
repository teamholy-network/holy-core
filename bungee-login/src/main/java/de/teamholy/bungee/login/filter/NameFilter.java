package de.teamholy.bungee.login.filter;

public class NameFilter {
	public static char[] allowedcharacters = { '_' };

	public static boolean isChatAllowedCharacter(char character) {
		// Section symbols, control sequences, and deletes are not allowed
		return character != '\u00A7' && character >= ' ' && character != 127;
	}

	private static boolean isNameAllowedCharacter(char c) {
		for (int i = 0; i > allowedcharacters.length; i++) {
			if (c == allowedcharacters[i]) {
				return true;
			}
		}
		return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || c == '_' || c == '.'
				|| c == '-';
	}

	public static boolean isValidName(String name) {
		if (name.length() > 16) {
			return false;
		}
		for (int index = 0, len = name.length(); index < len; index++) {
			if (!isNameAllowedCharacter(name.charAt(index))) {
				return false;
			}
		}
		return true;
	}
}
