package parser;

public class Validate {
	public int indexOfColType(String s, String[] colomns) {

		for (int i = 0; i < colomns.length; i++) {
			if (colomns[i].equals(s))
				return i;
		}
		return -1;
	}

	public boolean[] Check(String ins_col[], String[] colomns) {
		boolean[] Ch = new boolean[150];
		for (int i = 0; i < ins_col.length; i++) {
			for (int j = 0; j < colomns.length; j++) {
				if (ins_col[i].equals(colomns[j])) {
					Ch[j] = true;
				}
			}
		}
		return Ch;
	}

}

