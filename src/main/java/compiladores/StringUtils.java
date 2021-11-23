package compiladores;

import java.util.Stack;

public class StringUtils {
  public static int countMatches(String str, char ch) {
    int count = 0;
    for(int i = 0; i < str.length(); i++) {
      if(str.charAt(i) == ch)
        count++;
    }
    return count;
  }

  public static boolean isStringInArray(String str, String[] array) {
    for (String elem : array) {
      if(str.contains(elem)) //verificamos si lo contiene
        return true;
    }
    return false;
  }
}