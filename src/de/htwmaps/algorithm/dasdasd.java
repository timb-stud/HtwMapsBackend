package de.htwmaps.algorithm;

import java.util.Arrays;

public class dasdasd {
public static void main(String[] args) {
	int[] result = {0,1,2,3,4,5,6,7,8,9};
	int middle = (result.length - 1) / 2;
	for (int i = 0; i <= middle; i++) {
		int tmp2 = result[i];
		result[i] = result[result.length - 1 - i];
		result[result.length - 1 - i] = tmp2;
	}
	System.out.println(Arrays.toString(result));
}
}
