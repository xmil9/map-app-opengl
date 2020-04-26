//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

package types;

import java.util.List;

public class ConversionUtil {

    public static float[] toFloatArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

    public static int[] toIntArray(List<Integer> list) {
    	return list.stream().mapToInt(i -> i).toArray();
    }
}