package in.blogspot.freemind_subwaywall.character_encoding;

import java.nio.charset.Charset;
import java.util.SortedMap;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.BreakIterator;
import java.text.Normalizer;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;

class EncodingTest {
    private static String infile;
    private static String outfile;
    private static String incoding;
    private static String outcoding;
    private static String normalization;
    private static boolean debug = false;
    private static final String codepoints = "CODEPOINTS";
    private static final String defaultStr = "DEFAULT";
    private static String defaultencoding;
    private static final int BUFLEN = 512;
    private static boolean inCodepointsFlag = false;
    private static boolean outCodepointsFlag = false;
    private static boolean prettyFlag = false;
    private static boolean normalizeFlag = false;
    private static Normalizer.Form normalizationForm;

    public static void main (String[] args) throws Exception
    {
        Options opt = configureOptions();

        try {
            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse(opt, args);
            if (cmd.hasOption(Opts.HELP.s)) {
                printHelp(opt);
                return;
            }
            if (cmd.hasOption(Opts.DEBUG.s)) {
                debug = true;
            }
            infile = cmd.getOptionValue(Opts.INFILE.s);
            incoding = cmd.getOptionValue(Opts.INCODE.s);
            outfile = cmd.getOptionValue(Opts.OUTFILE.s);
            outcoding = cmd.getOptionValue(Opts.OUTCODE.s);
            defaultencoding = Charset.defaultCharset().name();
            if (incoding.equals(defaultencoding)) {
                incoding = defaultencoding;
            }
            if (incoding.equals(codepoints)) {
                incoding = defaultencoding;
                inCodepointsFlag = true;
            }
            if (outcoding.equals(defaultencoding)) {
                outcoding = defaultencoding;
            }
            if (outcoding.equals(codepoints)) {
                outcoding = defaultencoding;
                outCodepointsFlag = true;
            }
            if (cmd.hasOption(Opts.NORMALIZE.s)) {
                normalization = cmd.getOptionValue(Opts.NORMALIZE.s);
                if (Normalizer.Form.NFC.toString().equals(normalization)) {
                    normalizationForm = Normalizer.Form.NFC;
                } else if (Normalizer.Form.NFD.toString().equals(normalization)) {
                    normalizationForm = Normalizer.Form.NFD;
                } else if (Normalizer.Form.NFKC.toString().equals(normalization)) {
                    normalizationForm = Normalizer.Form.NFKC;
                } else if (Normalizer.Form.NFKD.toString().equals(normalization)) {
                    normalizationForm = Normalizer.Form.NFKD;
                } else {
                    printHelp(opt);
                    System.out.println("Invalid normalization form. choices are " +
                                       Normalizer.Form.NFC.toString() + ", " +
                                       Normalizer.Form.NFD.toString() + ", " +
                                       Normalizer.Form.NFKC.toString() + ", " +
                                       Normalizer.Form.NFKD.toString());
                    return;
                }
                normalizeFlag = true;
            }
            if (cmd.hasOption(Opts.PRETTY.s)) {
                prettyFlag = true;
            }
        } catch (ParseException e) {
            printHelp(opt);
            return;
        }

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                                                   new FileInputStream(infile),
                                                   incoding));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                                                    new FileOutputStream(outfile),
                                                    outcoding));
            int charCount = 0;
            int codeCount = 0;
            int graphemeCount = 0;
            String str = readFromInputFile(in);
            if (debug) {
                System.err.println("INPUT: " + str + "\n");
                System.err.flush();
            }
            //while ((len = readFromInputFile(in, buf)) != -1) {
                if (outCodepointsFlag || prettyFlag) {
                    int idx;
                    for (idx = 0; idx < str.length(); ) {
                        int codepoint = Character.codePointAt(str, idx);
                        int numChars = Character.charCount(codepoint);
                        out.write(Integer.toString(codepoint));
                        if (prettyFlag) {
                            out.write('\t');
                            out.write("0x" + Integer.toHexString(codepoint));
                            out.write('\t');
                            Character.UnicodeBlock block = 
                                Character.UnicodeBlock.of(codepoint);
                            out.write(((block != null) ? block.toString() :
                                                         "NULL"));
                            out.write('\t');
                            if (Character.isLetterOrDigit(codepoint)) {
                                out.write(codepoint);
                            } else if (Character.isJavaIdentifierPart(codepoint)) {
                                out.write(codepoint);
                            } else if (Character.isWhitespace(codepoint)) {
                                out.write("<Whitespace>");
                            } else if (Character.isISOControl(codepoint)) {
                                out.write("<Control>");
                            } else if (isSymbol(codepoint)) {
                                //out.write(str, idx, 1); //sbh
                                out.write(codepoint);
                            } else if (Character.isSpaceChar(codepoint)) {
                                out.write("<SpaceChar>");
                            } else {
                                out.write("<Unknown>");
                            }
                            if (!outCodepointsFlag) {
                                String tmp = str.substring(idx, idx+numChars);
                                byte[] bytes = tmp.getBytes(outcoding);
                                out.write('\t');
                                for (byte b: bytes) {
                                    out.write(String.format("%02x ", b));
                                }
                            }
                        }
                        out.write('\n');
                        idx += numChars;
                    }
                    charCount = str.length();
                    codeCount = Character.codePointCount(str, 0, charCount);
                    BreakIterator graphemeIterator = BreakIterator.
                                                        getCharacterInstance();
                    graphemeIterator.setText(str);
                    while (graphemeIterator.next() != BreakIterator.DONE) {
                        graphemeCount++;
                    }
                } else {
                    out.write(str, 0, str.length());
                }
            //}
            if (prettyFlag) {
                out.write("==============\n");
                out.write("SUMMARY\n");
                out.write("==============\n");
                out.write("Java chars: " + charCount + "\n");
                out.write("Unicode codepoints: " + codeCount + "\n");
                out.write("Graphemes: " + graphemeCount + "\n");
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.toString());
            return;
        } catch (UnsupportedEncodingException e) {
            System.err.println(e.toString());
            System.err.println("Supported encodings are:");
            for (String encname : Charset.availableCharsets().keySet()) {
                System.err.println(encname);
            }
            System.err.println(codepoints);
            System.err.println(defaultStr + " (" + defaultencoding + ")");
            return;
        }
    }

    static boolean isSymbol(int codepoint) {
        int type = Character.getType(codepoint);
        switch (type) {
            case Character.CURRENCY_SYMBOL:
            case Character.DASH_PUNCTUATION :
            case Character.END_PUNCTUATION:
            case Character.FINAL_QUOTE_PUNCTUATION:
            case Character.INITIAL_QUOTE_PUNCTUATION:
            case Character.MATH_SYMBOL:
            case Character.OTHER_PUNCTUATION:
            case Character.OTHER_SYMBOL:
            case Character.START_PUNCTUATION:
            case Character.CONNECTOR_PUNCTUATION:
                return (true);
        }

        return (false);
    }

    static String readFromInputFile (BufferedReader in)
        throws IOException, IndexOutOfBoundsException, IllegalArgumentException
    {
        int idx;
        char buf[] = new char[BUFLEN];
        String str = new String();

        do {
            if (inCodepointsFlag) {
                idx = 0;
                while (idx < (BUFLEN -1)) { //we have room for at least 2 chars
                    String intStr;
                    if ((intStr = in.readLine()) != null) {
                        Integer integerObj = Integer.valueOf(intStr);
                        if (debug) {
                            if (integerObj == null) {
                                System.err.println("null integer object found for "
                                                   + intStr);
                                System.err.flush();
                            }
                        }
                        long unsignedIntVal = integerObj.longValue();
                        idx += Character.toChars((int)unsignedIntVal, buf, idx);
                    } else {
                        break;
                    }
                }
            } else {
                idx = in.read(buf, 0, BUFLEN);
                if (idx < 0) {
                    idx = 0;
                }
            }
            str = str.concat(new String(buf, 0, idx));
        } while (idx > 0);

        if (normalizeFlag) {
            str = Normalizer.normalize(str, normalizationForm);
        }

        return (str);
    }

    static void printHelp(Options opt)
    {
        HelpFormatter someObject = new HelpFormatter();
        someObject.printHelp("java EncodingTest", opt, true);
    }

    private enum Opts {
        INFILE("i", "infile", "input text file", true, true, "input-file"),
        OUTFILE("o", "outfile", "text file where output is written", true, true,
                "output-file"),
        INCODE("j", "incode", "encoding for infile", true, true, "input-encoding"),
        OUTCODE("k", "outcode", "encoding for outfile", true, true,
                "output-encoding"),
        NORMALIZE("z", "normalize", "normalization needed", true, false,
                  "normalization-form"),
        PRETTY("p", "pretty", "pretty print codepoints and code-values",
               false, false),
        DEBUG("d", "debug", "print additonal messages to stderr for debugging",
              false, false),
        HELP("h", "help", "print this message and exit", false, false);

        String s; // short option
        String l; // long option
        String h; // help message
        boolean hasArgs; // if the option has arguments
        boolean mandatory; // if the option is mandatory
        String argName; // a name given to the argument, if the option needs one

        Opts(String s, String l, String h, boolean hasArgs, boolean mandatory)
        {
            this.s = s;
            this.l = l;
            this.h = h;
            this.hasArgs = hasArgs;
            this.mandatory = mandatory;
        }

        Opts(String s, String l, String h, boolean hasArgs, boolean mandatory,
             String argName)
        {
            this(s, l, h, hasArgs, mandatory);
            this.argName = argName;
        }
    }

    private static Options configureOptions()
    {
        Option infileOpt = new Option(Opts.INFILE.s, Opts.INFILE.l,
                                      Opts.INFILE.hasArgs, Opts.INFILE.h);
        infileOpt.setRequired(Opts.INFILE.mandatory);
        infileOpt.setArgName(Opts.INFILE.argName);
        Option outfileOpt = new Option(Opts.OUTFILE.s, Opts.OUTFILE.l,
                                       Opts.OUTFILE.hasArgs, Opts.OUTFILE.h);
        outfileOpt.setRequired(Opts.OUTFILE.mandatory);
        outfileOpt.setArgName(Opts.OUTFILE.argName);
        Option incodingOpt = new Option(Opts.INCODE.s, Opts.INCODE.l,
                                        Opts.INCODE.hasArgs, Opts.INCODE.h);
        incodingOpt.setRequired(Opts.INCODE.mandatory);
        incodingOpt.setArgName(Opts.INCODE.argName);
        Option outcodingOpt = new Option(Opts.OUTCODE.s, Opts.OUTCODE.l,
                                         Opts.OUTCODE.hasArgs, Opts.OUTCODE.h);
        outcodingOpt.setRequired(Opts.OUTCODE.mandatory);
        outcodingOpt.setArgName(Opts.OUTCODE.argName);
        Option normalizeOpt = new Option(Opts.NORMALIZE.s, Opts.NORMALIZE.l,
                                         Opts.NORMALIZE.hasArgs, Opts.NORMALIZE.h);
        normalizeOpt.setArgName(Opts.NORMALIZE.argName);
        Option prettyOpt = new Option(Opts.PRETTY.s, Opts.PRETTY.l,
                                      Opts.PRETTY.hasArgs, Opts.PRETTY.h);
        Option debugOpt = new Option(Opts.DEBUG.s, Opts.DEBUG.l, Opts.DEBUG.hasArgs,
                                     Opts.DEBUG.h);
        Option help = new Option(Opts.HELP.s, Opts.HELP.l, Opts.HELP.hasArgs,
                                 Opts.HELP.h);

        Options opt = new Options();
        opt.addOption(infileOpt);
        opt.addOption(outfileOpt);
        opt.addOption(incodingOpt);
        opt.addOption(outcodingOpt);
        opt.addOption(normalizeOpt);
        opt.addOption(prettyOpt);
        opt.addOption(debugOpt);
        opt.addOption(help);

        return opt;
    }
}
