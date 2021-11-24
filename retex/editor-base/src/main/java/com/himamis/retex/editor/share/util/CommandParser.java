package com.himamis.retex.editor.share.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandParser {

	public static List<String> parseCommand(String command) {
		return Arrays.stream(command.split("(\\( <)|(>, <)|(> \\))"))
				.map(String::trim).collect(Collectors.toList());
	}
}
