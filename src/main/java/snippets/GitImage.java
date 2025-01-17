package snippets;

import java.util.List;
import java.util.Arrays;

public class GitImage {
//@formatter:off
	private final static String ORANGE = "\033[38:2:255:165:0m"; // It's not 3/4 standard, hence not in ANSIColor class.
	private final static List<String> GITLOGO = Arrays.asList(
ORANGE + "      ¸       " + ANSIColor.RESET,
ORANGE + "    ／\\\\＼    " + ANSIColor.RESET,
ORANGE + " ／    ○  ＼  " + ANSIColor.RESET,
ORANGE + "<      | \\  > " + ANSIColor.RESET,
ORANGE + " ＼   ○  ○ ／ " + ANSIColor.RESET,
ORANGE + "    ＼  ／    " + ANSIColor.RESET,
ORANGE + "      ˇ       " + ANSIColor.RESET
);

	private final static List<String> NOREPO =  Arrays.asList(
ANSIColor.RED + "      __    " + ORANGE +"  __    ___  " + ANSIColor.RESET,
ANSIColor.RED + "|\\ | /  \\ " + ORANGE +"   / _` |  |  " + ANSIColor.RESET,
ANSIColor.RED + "| \\| \\__/ " + ORANGE +"   \\__> |  |  " + ANSIColor.RESET,
"",
ANSIColor.BLUE + " __   ___  __   __   " + ANSIColor.YELLOW + "        ___  __   ___  " + ANSIColor.RESET,
ANSIColor.BLUE + "|__) |__  |__) /  \\ " + ANSIColor.YELLOW + "   |__| |__  |__) |__   " + ANSIColor.RESET,
ANSIColor.BLUE + "|  \\ |___ |    \\__/" + ANSIColor.YELLOW + "    |  | |___ |  \\ |___ ." + ANSIColor.RESET);
//@formatter:on
	public static List<String> getLogo() {
		return GITLOGO;
	}

	public static List<String> getNOREPO() {
		return NOREPO;
	}
}
