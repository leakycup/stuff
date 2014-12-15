package in.blogspot.freemind_subwaywall.everything_else;

import java.io.BufferedReader;
import java.io.InputStreamReader;

interface DetectRepeatedString {
	void process(String input);
}

// Queue Automata == deterministic finite automata + a queue that can grow
// infinitely (i.e. infinite memory with FIFO access)
class QAutomata implements DetectRepeatedString {
	public void process (String input) {
		System.out.println(input + ": " + input.length());
		//System.out.println(input.length());

		if (input.length() == 0) {
			System.out.println("Not a Duplicated String (empty)");
			return;
		}
		buffer = new StringQueue();
		QAutomataState.state = QAutomataState.INSERT;
		if (findDup(input, 0, buffer)) {
			System.out.println("Duplicated String");
		} else {
			System.out.println("Not a Duplicated String");
		}
	}
	private static class QAutomataState {
		static char INSERT = 1;
		static char REMOVE = 2;
		static int state;
	}
	char midpoint = '$';
	private class StringQueue {
		private StringBuilder strBuf;
		StringQueue () {
			strBuf = new StringBuilder();
		}
		void insertChar (char ch) {
			strBuf.append(ch);
		}
		char removeChar () {
			char ch = strBuf.charAt(0);
			strBuf.deleteCharAt(0);
			return (ch);
		}
		char getHead () {
			return (strBuf.charAt(0));
		}
		boolean isEmpty () {
			return (strBuf.length() == 0 ? true : false);
		}
		// for debugging only
		void dump() {
			int idx;
			for (idx = 0; idx < strBuf.length(); idx++) {
				System.out.println(idx + ": strBuf[idx]: " + strBuf.charAt(idx));
			}
		}
	}
	private StringQueue buffer;
	private boolean findDup (String str, int idx, StringQueue q) {
		if (idx > (str.length() - 1)) { //end of input
			if (q.isEmpty()) {
				//System.out.println(idx + ": q is empty");
				return true;
			} else {
				//System.out.println(idx + ": q is not empty");
				//buffer.dump();
				return false;
			}
		}
		
		if ((QAutomataState.state == QAutomataState.REMOVE)) {
			if (q.isEmpty()) {
				return false;
			}
			if (str.charAt(idx) != q.getHead()) {
				return false;
			}
			q.removeChar();
			return (findDup(str, idx+1, q));
		}

		if ((QAutomataState.state == QAutomataState.INSERT)) {
			if (str.charAt(idx) == midpoint) {
				QAutomataState.state = QAutomataState.REMOVE;
			} else {
				q.insertChar(str.charAt(idx));
			}
			return (findDup(str, idx+1, q));
		}

		return false;
	}
}

class RepeatedStringDriver {
	public static void main (String[] args) {
		BufferedReader in_stream = new BufferedReader(new InputStreamReader(System.in));
		try {
			String input = in_stream.readLine();
			QAutomata q = new QAutomata();
			q.process(input);
			System.out.println("before exception");
			testExceptionThrower(input);
			throw new DerivedTestException();
			//unreachable code
		} catch (java.io.IOException e) {
			System.err.println("IO Error");
		} catch (DerivedTestException t) {
			System.err.println("Derived Test Exception");
			System.out.println("after derived test exception");
		} catch (TestException t) { //shud be handled after DerivedTestException
			System.err.println("Test Exception");
			System.out.println("after test exception");
		}
		System.out.println("end of program " + RepeatedStringDriver.class.getName());
        // experiment
        /*
        String foo = "foo";
        String bad = null;
        if (!foo.equals(bad)) {
            System.out.println("foo is " + foo + " and bad is " + bad);
        }
        */
	}

	static boolean testExceptionThrower (String str) throws TestException {
		if (str.length() < 5) {
			throw new TestException();
			//unreachable
		}
		System.out.println("end of testExceptionThrower");
		return true;
	}
}

class TestException extends Exception {
}

class DerivedTestException extends TestException {
}
