package com.aubay.formations.nr.utils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utils {

	private Utils() {
		// Hide constructor
	}

	public static <T, K> Map<K, T> mapElementsById(final List<T> elements, final Function<T, K> id) {
		return elements.stream().collect(Collectors.toMap(id, Function.identity()));
	}
}
